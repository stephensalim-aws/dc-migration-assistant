import React, { FunctionComponent, ReactElement } from 'react';
import Form, { Field } from '@atlaskit/form';
import Button from '@atlaskit/button';
import TextField from '@atlaskit/textfield';
import { I18n } from '@atlassian/wrm-react-i18n';

type AWSCreds = {
    accessKeyID: string;
    secretAccessKey: string;
    region: string;
};

/*
Function to submit the AWS credentials. Should return a promise which resolves when the
credentials have been submitted. Should reject if there was an error submitting the credentials.
*/
type CredSubmitFun = (creds: AWSCreds) => Promise<void>;

type AuthenticateAWSProps = {
    onSubmitCreds: CredSubmitFun;
};

export const AuthenticateAWS: FunctionComponent<AuthenticateAWSProps> = ({
    onSubmitCreds,
}): ReactElement => {
    return (
        <>
            <h1>{I18n.getText('aws.migration.authenticate.aws.title')}</h1>
            <Form<AWSCreds>>
                <Field<string>
                    isRequired
                    label={I18n.getText('aws.migration.authenticate.aws.accessKeyId.label')}
                    name="acccessKeyId"
                    defaultValue=""
                >
                    {({ fieldProps }: any): ReactElement => <TextField {...fieldProps} />}
                </Field>
            </Form>
        </>
    );
};
