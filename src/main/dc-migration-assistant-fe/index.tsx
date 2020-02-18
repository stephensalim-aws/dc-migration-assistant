import React from 'react';
import ReactDOM from 'react-dom';
import whenDomReady from 'when-dom-ready';
import { hot } from 'react-hot-loader';

import { App } from './components/App';

whenDomReady().then(function example() {
    const container = document.getElementById('dc-migration-assistant-root');
    // eslint-disable-next-line no-undef
    const AwsMigrationPlugin = hot(module)(() => <App />);
    ReactDOM.render(<AwsMigrationPlugin />, container);
});
