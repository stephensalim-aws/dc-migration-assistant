import React, { FunctionComponent } from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import { I18n } from '@atlassian/wrm-react-i18n';

import { overviewPath } from '../utils/RoutePaths';
import { MigrationOverview } from './MigrationOverview';
import { Home } from './Home';
import { AWSRoutes } from './aws/AwsRoutes';

export const App: FunctionComponent = () => (
    <Router>
        <Switch>
            <AWSRoutes />
            <Route path={overviewPath}>
                <MigrationOverview />
            </Route>
            <Route>
                <Home
                    title={I18n.getText('atlassian.migration.datacenter.home.title')}
                    synopsis={I18n.getText('atlassian.migration.datacenter.home.synopsis')}
                    exploreMigrationButtonText={I18n.getText(
                        'atlassian.migration.datacenter.home.explore.migration'
                    )}
                />
            </Route>
        </Switch>
    </Router>
);
