import React, { FunctionComponent, ReactElement, useState, useEffect } from 'react';
import yaml from 'yaml';

import {
    quickstartParamToAtlaskitFormElement,
    QuickStartParameterYamlNode,
    // eslint-disable-next-line import/extensions
} from '../utils/quickstartToAtlaskit';

const quickstartUrl =
    'https://dcd-slinghost-templates.s3.amazonaws.com/quickstart-jira-dc-with-vpc.template.parameters.yaml';

export const QuickstartForm: FunctionComponent = (): ReactElement => {
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
