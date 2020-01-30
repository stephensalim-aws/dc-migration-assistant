const path = require('path');

const paths = {
    mocksRoot: path.resolve('./src/main/rate-limit/atlassian/mocks'),
    testsRoot: path.resolve('./src/main/rate-limit/components'),
};

module.exports = {
    clearMocks: true,
    verbose: true,
    roots: [paths.testsRoot],
    moduleFileExtensions: ['ts', 'tsx', 'js', 'jsx', 'json'],
    transformIgnorePatterns: ['node_modules/(?!(@atlaskit)/)'],
    moduleNameMapper: {
        '^wrm/context-path$': path.join(paths.mocksRoot, './wrm/context-path.js'),
        '^@atlassian/wrm-react-i18n$': path.join(paths.mocksRoot, './@atlassian/wrm-react-i18n.js'),
        '\\.(css|less)$': 'identity-obj-proxy',
    },
    testMatch: ['**/__tests__/**/*.+(ts|tsx|js)', '**/?(*.)+(spec|test).+(ts|tsx|js)'],
    transform: {
        '^.+\\.(ts|tsx)$': 'ts-jest',
    },
};
