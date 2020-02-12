import React, { FunctionComponent, ReactElement, useState, useEffect } from 'react';
import yaml from 'yaml';
import Form from '@atlaskit/form';
import Button from '@atlaskit/button';

import {
    createQuickstartFormField,
    QuickStartParameterYamlNode,
    QuickstartParameter,
    // eslint-disable-next-line import/extensions
} from '../utils/quickstartToAtlaskit';

const QuickstartForm = ({
    quickstartParams,
}: Record<string, Array<QuickstartParameter>>): ReactElement => (
    <Form onSubmit={(data: FormData): void => console.log('form data', data)}>
        {({ formProps }: any): ReactElement => (
            <form {...formProps}>
                {quickstartParams.map(({ key, paramProperties }: QuickstartParameter) => {
                    return createQuickstartFormField({ key, paramProperties });
                })}
                <Button type="submit" appearance="primary">
                    Submit
                </Button>
            </form>
        )}
    </Form>
);

const quickstartUrl =
    'https://dcd-slinghost-templates.s3.amazonaws.com/quickstart-jira-dc-with-vpc.template.parameters.yaml';

export const QuickStartDeploy: FunctionComponent = (): ReactElement => {
    const [params, setParams] = useState({});
    const [hasUpdatedTemplate, setHasUpdatedTemplate] = useState(false);

    useEffect(() => {
        if (!hasUpdatedTemplate) {
            fetch(quickstartUrl, {
                method: 'GET',
            })
                .then(resp => resp.text())
                .then(text => {
                    const paramDoc = yaml.parse(text);
                    setParams(paramDoc.Parameters);
                    setHasUpdatedTemplate(true);
                });
        }
    });

    return (
        <div>
            <QuickstartForm
                quickstartParams={Object.entries(params).map(entry => {
                    const [key, value] = entry;
                    return {
                        key,
                        paramProperties: value as QuickStartParameterYamlNode,
                    };
                })}
            />
        </div>
    );
};
