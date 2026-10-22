// The app calls its own origin's /api (see HttpClientFactory.wasmJs.kt); the dev server forwards it.
const apiTarget = process.env.APP_API_TARGET || 'http://localhost:8080';

if (config.devServer) {
    // Set APP_DEV_HOST=0.0.0.0 to open the dev server to a phone on the same Wi-Fi.
    config.devServer.host = process.env.APP_DEV_HOST || 'localhost';
    // Not 8080: that is the local backend's port.
    config.devServer.port = Number(process.env.APP_DEV_PORT || 8081);
    config.devServer.allowedHosts = 'all';
    config.devServer.open = false;
    // Cross-origin isolation: the web database keeps its file in OPFS only with these (core/database/worker).
    // Production hosts must send the same two headers; see webApp/src/wasmJsMain/resources/_headers.
    config.devServer.headers = {
        'Cross-Origin-Opener-Policy': 'same-origin',
        'Cross-Origin-Embedder-Policy': 'require-corp',
    };
    config.devServer.proxy = [
        {
            context: ['/api'],
            target: apiTarget,
            changeOrigin: true,
        },
    ];
}
