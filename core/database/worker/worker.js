// Runs SQLite's own Wasm build for Room's WebWorkerSQLiteDriver. The message protocol is
// documented on androidx.sqlite.driver.web.WebWorkerSQLiteDriver; based on the example worker in
// androidx (sqlite/sqlite-web-worker-test), Apache-2.0.
//
// Storage: the "opfs" VFS (Origin Private File System) needs SharedArrayBuffer, which the browser only
// gives a cross-origin-isolated page, i.e. one served with
//   Cross-Origin-Opener-Policy: same-origin
//   Cross-Origin-Embedder-Policy: require-corp
// Without those headers the database lives in memory for this page only; the app says so
// (DatabaseStorage.MEMORY_ONLY). With them, a failure to open the file is an error, never a
// silent switch to memory that would lose what the person saves.
import sqlite3InitModule from '@sqlite.org/sqlite-wasm';

let sqlite3 = null;
const databases = new Map();
const statements = new Map();
let nextDatabaseId = 0;
let nextStatementId = 0;

const canUseFileStorage = () => self.crossOriginIsolated === true && typeof sqlite3.oo1.OpfsDb === 'function';

function reply(id, data) {
    postMessage({ id, data });
}

function fail(id, error) {
    postMessage({ id, error: String((error && error.message) || error) });
}

function open(id, { fileName }) {
    const database = canUseFileStorage() ? new sqlite3.oo1.OpfsDb(fileName, 'c') : new sqlite3.oo1.DB(':memory:', 'c');
    const databaseId = nextDatabaseId++;
    databases.set(databaseId, database);
    reply(id, { databaseId });
}

function prepare(id, { databaseId, sql }) {
    const database = databases.get(databaseId);
    if (!database) throw new Error('Unknown database ' + databaseId);
    const statement = database.prepare(sql);
    const statementId = nextStatementId++;
    statements.set(statementId, statement);
    const columnNames = [];
    for (let i = 0; i < statement.columnCount; i++) {
        columnNames.push(sqlite3.capi.sqlite3_column_name(statement, i));
    }
    reply(id, {
        statementId,
        parameterCount: sqlite3.capi.sqlite3_bind_parameter_count(statement),
        columnNames,
    });
}

function step(id, { statementId, bindings }) {
    const statement = statements.get(statementId);
    if (!statement) throw new Error('Unknown statement ' + statementId);
    const rows = [];
    const columnTypes = [];
    statement.reset();
    statement.clearBindings();
    bindings.forEach((value, index) => statement.bind(index + 1, value));
    while (statement.step()) {
        if (!columnTypes.length) {
            for (let i = 0; i < statement.columnCount; i++) {
                columnTypes.push(sqlite3.capi.sqlite3_column_type(statement, i));
            }
        }
        rows.push(statement.get([]));
    }
    reply(id, { rows, columnTypes });
}

// One-way: the driver waits for no answer, and an unexpected reply makes it throw.
function close({ statementId, databaseId }) {
    try {
        if (statementId !== undefined && statementId !== null && statements.has(statementId)) {
            statements.get(statementId).finalize();
            statements.delete(statementId);
        }
        if (databaseId !== undefined && databaseId !== null && databases.has(databaseId)) {
            databases.get(databaseId).close();
            databases.delete(databaseId);
        }
    } catch (error) {
        console.warn('SQLite worker: close failed', error);
    }
}

const commands = { open, prepare, step };

function handle({ data: message }) {
    const { id, data } = message || {};
    if (!data || !data.cmd) {
        fail(id, 'Invalid request');
        return;
    }
    if (data.cmd === 'close') {
        close(data);
        return;
    }
    const command = commands[data.cmd];
    if (!command) {
        fail(id, 'Unknown command ' + data.cmd);
        return;
    }
    try {
        command(id, data);
    } catch (error) {
        fail(id, error);
    }
}

const queued = [];
onmessage = (event) => (sqlite3 ? handle(event) : queued.push(event));

sqlite3InitModule().then((instance) => {
    sqlite3 = instance;
    queued.splice(0).forEach(handle);
});
