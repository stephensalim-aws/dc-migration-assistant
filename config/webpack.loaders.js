const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const PostCssIcssValuesPlugin = require('postcss-icss-values');

const { MY_I18N_FILES } = require('./webpack.constants');

function getLoaders({ isProductionEnv = false }) {
    return [
        {
            test: /\.jsx?$/,
            exclude: /node_modules/,
            use: [
                {
                    loader: '@atlassian/i18n-properties-loader',
                    options: {
                        i18nFiles: MY_I18N_FILES,
                        disabled: isProductionEnv,
                    },
                },
                {
                    loader: 'babel-loader',
                    options: {
                        cacheDirectory: true,
                    },
                },
            ],
        },
        {
            test: /\.less$/,
            use: [
                {
                    loader: isProductionEnv ? MiniCssExtractPlugin.loader : 'style-loader',
                    options: {
                        sourceMap: true,
                    },
                },
                {
                    loader: 'css-loader',
                    options: {
                        sourceMap: true,
                    },
                },
                {
                    loader: 'postcss-loader',
                    options: {
                        sourceMap: true,
                        plugins: [new PostCssIcssValuesPlugin()],
                    },
                },
                {
                    loader: 'less-loader',
                    options: {
                        sourceMap: true,
                    },
                },
            ],
        },
        {
            test: /\.(png|jpg|gif|svg)$/,
            use: [
                {
                    loader: 'file-loader',
                    options: {},
                },
            ],
        },
        {
            test: /\.soy$/,
            use: [
                ...((!isProductionEnv && [
                    {
                        loader: '@atlassian/i18n-properties-loader',
                        options: {
                            MY_I18N_FILES,
                        },
                    },
                ]) ||
                    []),
                {
                    loader: '@atlassian/soy-loader',
                    options: {
                        dontExpose: true,
                    },
                },
            ],
        },
        { test: /\.tsx?$/, loader: 'awesome-typescript-loader' },
    ];
}

module.exports.loaders = isProduction => getLoaders(isProduction);
