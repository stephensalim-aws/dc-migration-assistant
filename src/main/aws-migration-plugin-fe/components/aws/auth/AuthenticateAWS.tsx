import React, { FunctionComponent, ReactElement } from 'react';
import Form, { Field } from '@atlaskit/form';
import Button from '@atlaskit/button';
import styled from 'styled-components';
import TextField from '@atlaskit/textfield';
import { I18n } from '@atlassian/wrm-react-i18n';
import { AsyncSelect, OptionType } from '@atlaskit/select';

type AWSCreds = {
    accessKeyID: string;
    secretAccessKey: string;
    region: string;
};

/*
Function to submit the AWS credentials. Should return a promise which resolves when the
credentials have been submitted. Should reject with an error message if there was an error submitting the credentials.
*/
type CredSubmitFun = (creds: AWSCreds) => Promise<string>;

/*
Function to get all AWS regions. Should return a promise which resolves
with the AWS regions
*/
type QueryRegionFun = () => Promise<Array<string>>;

type AuthenticateAWSProps = {
    onSubmitCreds: CredSubmitFun;
    getRegions: QueryRegionFun;
};

const RegionSelect: FunctionComponent<{ getRegions: QueryRegionFun }> = (props): ReactElement => {
    const { getRegions } = props;

    // This will be replaced by an API call
    const promiseOptions = (): Promise<Array<OptionType>> => {
        return getRegions().then(regions => {
            return regions.map(region => ({ label: region, value: region, key: region }));
        });
    };

    const LargeAsyncSelect = styled(AsyncSelect)`
        width: 480.4px;
    `;

    return (
        <LargeAsyncSelect
            {...props}
            cacheOptions
            defaultOptions
            isMulti
            isSearchable={false}
            loadOptions={promiseOptions}
        />
    );
};

export const AuthenticateAWS: FunctionComponent<{
    onSubmitCreds: CredSubmitFun;
    getRegions: QueryRegionFun;
}> = ({ onSubmitCreds, getRegions }): ReactElement => {
    return (
        <>
            <h1>{I18n.getText('aws.migration.authenticate.aws.title')}</h1>
            <Form>
                {({ formProps }: any): ReactElement => (
                    <form {...formProps}>
                        <Field
                            isRequired
                            label={I18n.getText('aws.migration.authenticate.aws.accessKeyId.label')}
                            name="acccessKeyId"
                            defaultValue=""
                        >
                            {({ fieldProps }: any): ReactElement => (
                                <TextField width="xlarge" {...fieldProps} />
                            )}
                        </Field>
                        <Field
                            isRequired
                            label={I18n.getText(
                                'aws.migration.authenticate.aws.secretAccessKey.label'
                            )}
                            name="secretAccessKey"
                            defaultValue=""
                        >
                            {({ fieldProps }: any): ReactElement => (
                                <TextField width="xlarge" {...fieldProps} />
                            )}
                        </Field>
                        <Field
                            isRequired
                            label={I18n.getText('aws.migration.authenticate.aws.region.label')}
                            name="region"
                            defaultValue=""
                        >
                            {({ fieldProps }: any): ReactElement => (
                                <RegionSelect getRegions={getRegions} {...fieldProps} />
                            )}
                        </Field>
                    </form>
                )}
            </Form>
        </>
    );
};
