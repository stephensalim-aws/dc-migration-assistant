import React, { FunctionComponent } from 'react';
import { Route, Switch } from 'react-router-dom';

import { quickstartPath, awsAuthPath, quickstartStatusPath } from '../../utils/RoutePaths';
import { QuickStartDeploy } from './quickstart/QuickStartDeploy';
import { QuickStartStatus } from './quickstart/QuickStartStatus';
import { AuthenticateAWS, QueryRegionFun } from './auth/AuthenticateAWS';
import { callAppRest } from '../../utils/api';

const getRegions: QueryRegionFun = () => {
    return callAppRest('GET', 'aws/global-infrastructure/regions').then(response =>
        response.json()
    );
};

export const AWSRoutes: FunctionComponent = () => (
    <Switch>
        <Route exact path={quickstartPath}>
            <QuickStartDeploy />
        </Route>
        <Route exact path={quickstartStatusPath}>
            <QuickStartStatus />
        </Route>
        <Route exact path={awsAuthPath}>
            <AuthenticateAWS
                getRegions={getRegions}
                onSubmitCreds={(creds): Promise<string> => {
                    // This should be replaced with API call that stores the credentials
                    // eslint-disable-next-line no-console
                    console.log('received AWS credentials!');
                    return Promise.resolve('');
                }}
            />
        </Route>
    </Switch>
);
