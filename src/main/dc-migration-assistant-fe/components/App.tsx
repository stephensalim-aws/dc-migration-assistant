import React, { FunctionComponent } from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import { I18n } from '@atlassian/wrm-react-i18n';

import { overviewPath, homePath, awsBasePath } from '../utils/RoutePaths';
import { MigrationOverview } from './MigrationOverview';
import { Home } from './Home';
import { AWSRoutes } from './aws/AwsRoutes';

export const App: FunctionComponent = () => (
    <Router>
        <Switch>
            <Route exact path={overviewPath}>
                <MigrationOverview />
            </Route>
            <Route path={awsBasePath}>
                <AWSRoutes />
            </Route>
            <Route exact path={homePath}>
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
