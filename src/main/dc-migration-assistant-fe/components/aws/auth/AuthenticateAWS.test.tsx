import React from 'react';
import { render } from '@testing-library/react';

import { AuthenticateAWS, AuthenticateAWSProps } from './AuthenticateAWS';

const NO_OP_AUTHENTICATION_PAGE_PROPS: AuthenticateAWSProps = {
    onSubmitCreds: () => Promise.resolve('submitted'),
    getRegions: () => Promise.resolve(['my-fake-region-1', 'my-fake-region-2']),
};

describe('AWS Authentication page', () => {
    it('should render', () => {
        const { getByText } = render(<AuthenticateAWS {...NO_OP_AUTHENTICATION_PAGE_PROPS} />);

        expect(
            getByText('atlassian.migration.datacenter.authenticate.aws.accessKeyId.label')
        ).toBeTruthy();
        expect(
            getByText('atlassian.migration.datacenter.authenticate.aws.secretAccessKey.label')
        ).toBeTruthy();
        expect(
            getByText('atlassian.migration.datacenter.authenticate.aws.region.label')
        ).toBeTruthy();
    });

    it('should use the getRegions function to query AWS regions', () => {
        let regionFunCalled = false;
        const testGetRegions = (): Promise<Array<string>> => {
            regionFunCalled = true;
            return Promise.resolve([]);
        };

        render(
            <AuthenticateAWS {...NO_OP_AUTHENTICATION_PAGE_PROPS} getRegions={testGetRegions} />
        );

        expect(regionFunCalled).toBeTruthy();
    });
});
