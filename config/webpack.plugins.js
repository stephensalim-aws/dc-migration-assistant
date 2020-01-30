const path = require('path');
const WrmPlugin = require('atlassian-webresource-webpack-plugin');
const DuplicatePackageCheckerPlugin = require('duplicate-package-checker-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const WriteFilePlugin = require('write-file-webpack-plugin')
const { PLUGIN_KEY, WRM_OUTPUT, JQUERY_EXTERNAL } = require('./webpack.constants');

const plugins = shouldWatch => [
    new WrmPlugin({
        pluginKey: PLUGIN_KEY,
        xmlDescriptors: WRM_OUTPUT,
        providedDependencies: {
            jquery: {
                dependency: 'com.atlassian.plugins.jquery:jquery',
                import: JQUERY_EXTERNAL.jquery,
            },
            'wrm/context-path': {
                dependency:
                    'com.atlassian.plugins.atlassian-plugins-webresource-plugin:context-path',
                import: {
                    var: "require('wrm/context-path')",
                    amd: 'wrm/context-path',
                },
            },
            'wrm/format': {
                dependency: 'com.atlassian.plugins.atlassian-plugins-webresource-plugin:format',
                import: {
                    var: 'require("wrm/format")',
                    amd: 'wrm/format',
                },
            },
        },
        singleRuntimeWebResourceKey: 'rate-limiting-plugin-runtime',
        watch: shouldWatch,
        watchPrepare: shouldWatch,
    }),
    new DuplicatePackageCheckerPlugin(),
    new HtmlWebpackPlugin({
        inject: true,
        template: path.join(__dirname, '../public/index.html'),
    }),
    new WriteFilePlugin(),
];

module.exports = {
    plugins,
};
