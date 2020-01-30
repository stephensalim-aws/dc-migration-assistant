const path = require('path');

const rootDir = path.join(__dirname, '..');
const srcDir = path.join(rootDir, 'src', 'main');

const I18N_SRC_DIR = path.join(srcDir, 'resources');
const FRONTEND_SRC_DIR = path.join(srcDir, 'aws-migration-plugin');
const FRONTEND_OUTPUT_DIR = path.join(rootDir, 'target', 'classes');
const PLUGIN_KEY = 'com.atlassian.aws-migration-plugin';
const ENTRY_POINT = { 'aws-migration-plugin': path.join(FRONTEND_SRC_DIR, 'index.tsx') };

const MY_I18N_FILES = ['aws-migration-plugin.properties'].map(file => path.join(I18N_SRC_DIR, 'i18n', file));
const WRM_OUTPUT = path.resolve(
    './',
    'target',
    FRONTEND_OUTPUT_DIR,
    'META-INF',
    'plugin-descriptors',
    'wr-webpack-bundles.xml'
);

const JQUERY_EXTERNAL = {
    jquery: {
        commonjs: 'jquery',
        commonjs2: 'jquery',
        amd: 'jquery',
        root: 'jQuery',
        global: 'jQuery',
        var: 'jQuery',
    },
};

const DEV_SERVER_PORT = 3333;
const DEV_SERVER_HOST = 'localhost';

module.exports = {
    srcDir,
    I18N_SRC_DIR,
    FRONTEND_SRC_DIR,
    FRONTEND_OUTPUT_DIR,
    MY_I18N_FILES,
    WRM_OUTPUT,
    JQUERY_EXTERNAL,
    PLUGIN_KEY,
    ENTRY_POINT,
    DEV_SERVER_PORT,
    DEV_SERVER_HOST,
};
