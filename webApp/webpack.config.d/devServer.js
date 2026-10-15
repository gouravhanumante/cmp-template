// The app calls its own origin's /api (see HttpClientFactory.wasmJs.kt); the dev server forwards it.
const apiTarget = process.env.APP_API_TARGET || 'http://localhost:8080';

if (config.devServer) {
    // Set APP_DEV_HOST=0.0.0.0 to open the dev server to a phone on the same Wi-Fi.
    config.devServer.host = process.env.APP_DEV_HOST || 'localhost';
    // Not 8080: that is the local backend's port.
    config.devServer.port = Number(process.env.APP_DEV_PORT || 8081);
    config.devServer.allowedHosts = 'all';
    config.devServer.open = false;
    config.devServer.proxy = [
        {
            context: ['/api'],
            target: apiTarget,
            changeOrigin: true,
        },
    ];
}
