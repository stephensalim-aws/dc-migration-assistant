import React, { FunctionComponent, ReactElement, useState, useEffect } from 'react';
import yaml from 'yaml';
import Form, { Field } from '@atlaskit/form';
import Button from '@atlaskit/button';

import {
    quickstartParamToAtlaskitFormElement,
    QuickStartParameterYamlNode,
    // eslint-disable-next-line import/extensions
} from '../utils/quickstartToAtlaskit';

const QuickstartForm = () => (
    <Form onSubmit={(data: any) => console.log('form data', data)}>
        {({ formProps }: any) => (
            <form {...formProps}>
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
            {Object.entries(params).map((entry: [string, QuickStartParameterYamlNode]) => {
                const [key, value] = entry;
                return quickstartParamToAtlaskitFormElement(key, value);
            })}
        </div>
    );
};
