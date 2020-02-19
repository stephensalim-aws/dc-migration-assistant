import React, { FunctionComponent } from 'react';
import { Route } from 'react-router-dom';

import { quickstartPath, awsAuthPath } from '../../utils/RoutePaths';
import { QuickStartDeploy } from './quickstart/QuickStartDeploy';
import { AuthenticateAWS, QueryRegionFun } from './auth/AuthenticateAWS';
import { callAppRest } from '../../utils/api';

const getRegions: QueryRegionFun = () => {
    return callAppRest('GET', 'aws/global-infrastructure/regions').then(response =>
        response.json()
    );
};

export const AWSRoutes: FunctionComponent = () => (
    <>
        <Route path={quickstartPath}>
            <QuickStartDeploy />
        </Route>
        <Route path={awsAuthPath}>
            <AuthenticateAWS
                getRegions={getRegions}
                onSubmitCreds={(creds): Promise<string> => {
                    // This should be replaced with API call that stores the credentials
                    // eslint-disable-next-line no-console
                    console.log(creds);
                    return Promise.resolve('');
                }}
            />
        </Route>
    </>
);
