import React from 'react';
import { render } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import { AuthenticateAWS, AuthenticateAWSProps, AWSCreds } from './AuthenticateAWS';

const NO_OP_AUTHENTICATION_PAGE_PROPS: AuthenticateAWSProps = {
    onSubmitCreds: () => Promise.resolve('submitted'),
    getRegions: () => Promise.resolve(['my-fake-region-1', 'my-fake-region-2']),
};

describe('AWS Authentication page', () => {
    it('should render', () => {
        const { getByText } = render(<AuthenticateAWS {...NO_OP_AUTHENTICATION_PAGE_PROPS} />);

        expect(getByText('aws.migration.authenticate.aws.accessKeyId.label')).toBeTruthy();
        expect(getByText('aws.migration.authenticate.aws.secretAccessKey.label')).toBeTruthy();
        expect(getByText('aws.migration.authenticate.aws.region.label')).toBeTruthy();
    });

    it('should submit the credentials to the onSubmitCreds function', async () => {
        //TODO: use this method to mock atlaskit select https://www.polvara.me/posts/testing-a-custom-select-with-react-testing-library/

        const testAccessKey = 'my access key';
        const testSecretKey = 'my secret key';
        const testRegion = 'my-fake-region-1';

        const verifyCreds = (creds: AWSCreds): Promise<string> => {
            expect(creds.accessKeyID).toEqual(testAccessKey);
            expect(creds.secretAccessKey).toEqual(testSecretKey);
            expect(creds.region).toEqual(testRegion);

            return Promise.resolve('');
        };

        const { getByText, getByLabelText, getAllByLabelText, getByRole } = render(
            <AuthenticateAWS {...NO_OP_AUTHENTICATION_PAGE_PROPS} onSubmitCreds={verifyCreds} />
        );

        const accessKeyInput = getByLabelText('aws.migration.authenticate.aws.accessKeyId.label');
        await userEvent.type(accessKeyInput, testAccessKey);

        const secretKeyInput = getByLabelText(
            'aws.migration.authenticate.aws.secretAccessKey.label'
        );
        await userEvent.type(secretKeyInput, testAccessKey);

        const regionPicker = getAllByLabelText('aws.migration.authenticate.aws.region.label');
        console.log(regionPicker[1]);
        await userEvent.click(regionPicker[1]);

        const fakeRegionOneOption = getByText(testRegion);
        await userEvent.click(fakeRegionOneOption);

        const submitButton = getByRole('button');
        await userEvent.click(submitButton);

        expect(verifyCreds).toBeCalled();
    });
});
