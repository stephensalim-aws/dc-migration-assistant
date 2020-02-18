import React, { FunctionComponent } from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import { I18n } from '@atlassian/wrm-react-i18n';

import { awsAuthPath, overviewPath, quickstartPath } from '../utils/RoutePaths';
import { MigrationOverview } from './MigrationOverview';
import { QuickStartDeploy } from './aws/quickstart/QuickStartDeploy';
import { AuthenticateAWS } from './aws/auth/AuthenticateAWS';
import { Home } from './Home';

export const App: FunctionComponent = () => {
    const regions = [
        'us-east-1',
        'us-east-2',
        'us-west-1',
        'us-west-2',
        'we really need to populate this via API',
    ];

    return (
        <Router>
            <Switch>
                <Route path={overviewPath}>
                    <MigrationOverview />
                </Route>
                <Route path={quickstartPath}>
                    <QuickStartDeploy />
                </Route>
                <Route path={awsAuthPath}>
                    <AuthenticateAWS
                        getRegions={(): Promise<Array<string>> => {
                            // This should be replaced with API call which gets regions
                            return Promise.resolve(regions);
                        }}
                        onSubmitCreds={(creds): Promise<string> => {
                            // This should be replaced with API call that stores the credentials
                            // eslint-disable-next-line no-console
                            console.log(creds);
                            return Promise.resolve('');
                        }}
                    />
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
};
