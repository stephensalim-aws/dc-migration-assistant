import React from 'react';
import ReactDOM from 'react-dom';
import whenDomReady from 'when-dom-ready';
import { hot } from 'react-hot-loader';
// eslint-disable-next-line import/no-unresolved
import * as formatter from '@atlassian/wrm-react-i18n';

import Home from './components/Home.tsx';

whenDomReady().then(function example() {
    const container = document.getElementById('dc-on-aws-migration-plugin');
    // eslint-disable-next-line no-undef
    const AwsMigrationPlugin = hot(module)(() => (
        <Home
            title={formatter.I18n.getText('aws.migration.home.title')}
            synopsis={formatter.I18n.getText('aws.migration.home.synopsis')}
            exploreMigrationButtonText={formatter.I18n.getText(
                'aws.migration.home.explore.migration'
            )}
        />
    ));
    ReactDOM.render(<AwsMigrationPlugin />, container);
});
