const TerserPlugin = require('terser-webpack-plugin');
const merge = require('webpack-merge');
const webpack = require('webpack');
const path = require('path');
const dotenv = require('dotenv');

const {
    DEV_SERVER_HOST,
    DEV_SERVER_PORT,
    ENTRY_POINT,
    FRONTEND_OUTPUT_DIR,
    FRONTEND_SRC_DIR,
} = require('./webpack.constants');

const { plugins } = require('./webpack.plugins');
const { loaders } = require('./webpack.loaders');

const watchConfig = {
    devServer: {
        host: DEV_SERVER_HOST,
        port: DEV_SERVER_PORT,
        historyApiFallback: true,
        contentBase: path.join(__dirname, '../public'),
        overlay: true,
        hot: true,
        headers: {
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Headers': '*',
        },
    },
    plugins: [new webpack.HotModuleReplacementPlugin()],
    devtool: 'inline-source-map',
};

const devConfig = env => {
    return merge([
        {
            optimization: {
                minimize: false,
                runtimeChunk: false,
                splitChunks: false,
            },
            output: {
                publicPath: `http://${DEV_SERVER_HOST}:${DEV_SERVER_PORT}/`,
                filename: '[name].js',
                chunkFilename: '[name].chunk.js',
            },
        },
        env === 'dev-server' && watchConfig,
    ]);
};

const prodConfig = {
    optimization: {
        minimizer: [
            new TerserPlugin({
                terserOptions: {
                    mangle: {
                        reserved: ['I18n', 'getText'],
                    },
                },
            }),
        ],
    },
    output: {
        filename: 'bundled.main.js',
    },
};

module.exports = (env, argv = {}) => {
    function isProductionEnv(mode = argv.mode) {
        return mode !== 'development';
    }

    function loadDotEnvVariables(mode) {
        let dotEnvFilePath = path.join(__dirname, '..', '.env');
        if (!isProductionEnv(mode)) {
            dotEnvFilePath = dotEnvFilePath + '.' + mode;
        }
        const dotEnvOverrides = dotenv.config({ path: dotEnvFilePath }).parsed;

        return Object.keys(dotEnvOverrides).reduce((acc, current) => {
            acc[`process.env.${current}`] = JSON.stringify(dotEnvOverrides[current]);
            return acc;
        }, {});
    }

    const isProduction = isProductionEnv();
    const modeConfig = isProduction ? prodConfig : devConfig(env);
    return merge([
        {
            mode: argv.mode,
            entry: ENTRY_POINT,
            resolve: {
                extensions: ['*', '.ts', '.tsx', '.js', '.jsx'],
            },
            stats: {
                logging: 'info',
            },
            context: FRONTEND_SRC_DIR,
            plugins: [
                ...plugins(!isProduction),
                new webpack.DefinePlugin(loadDotEnvVariables(argv.mode)),
            ],
            module: {
                rules: loaders(isProduction),
            },
            output: {
                path: FRONTEND_OUTPUT_DIR,
            },
        },
        modeConfig,
    ]);
};
