import React, { FunctionComponent } from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import { I18n } from '@atlassian/wrm-react-i18n';

import { overviewPath, quickstartPath } from '../utils/RoutePaths.ts';
import { MigrationOverview } from './MigrationOverview.tsx';
import { QuickStartDeploy } from './QuickStartDeploy.tsx';
import { Home } from './Home.tsx';

export const App: FunctionComponent = () => {
    return (
        <Router>
            <Switch>
                <Route path={overviewPath}>
                    <MigrationOverview />
                </Route>
                <Route path={quickstartPath}>
                    <QuickStartDeploy />
                </Route>
                <Route>
                    <Home
                        title={I18n.getText('aws.migration.home.title')}
                        synopsis={I18n.getText('aws.migration.home.synopsis')}
                        exploreMigrationButtonText={I18n.getText(
                            'aws.migration.home.explore.migration'
                        )}
                    />
                </Route>
            </Switch>
        </Router>
    );
};
