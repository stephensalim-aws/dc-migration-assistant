import React, { FunctionComponent, ReactElement, useState, useEffect } from 'react';
import yaml from 'yaml';
import Form, { FormHeader, FormSection } from '@atlaskit/form';
import Button from '@atlaskit/button';
import Spinner from '@atlaskit/spinner';
import { I18n } from '@atlassian/wrm-react-i18n';

import { createQuickstartFormField } from './quickstartToAtlaskit.tsx';
import {
    QuickstartParameterGroup,
    QuickStartParameterYamlNode,
    QuickstartParamGroupYamlNode,
    QuickstartParamLabelYamlNode,
    // eslint-disable-next-line import/extensions
} from './QuickStartTypes';

const QUICKSTART_PARAMS_URL =
    'https://dcd-slinghost-templates.s3.amazonaws.com/mothra/quickstart-jira-dc-with-vpc.template.parameters.yaml';

const QuickstartForm = ({
    quickstartParamGroups,
}: Record<string, Array<QuickstartParameterGroup>>): ReactElement => (
    /* eslint-disable @typescript-eslint/no-explicit-any */
    <Form
        onSubmit={(data: Record<string, any>): void => {
            // console.log(data);
            const transformedCfnParams = data;
            Object.entries(data).forEach(entry => {
                // Hoist value from Select inputs to root of form value
                const [key, value] = entry;
                if (value.label) {
                    transformedCfnParams[key] = value.value;
                }
                return true;
            });
            console.log(transformedCfnParams);
        }}
    >
        {({ formProps }: any): ReactElement => (
            /* eslint-enable @typescript-eslint/no-explicit-any */
            <form {...formProps}>
                <FormHeader title={I18n.getText('aws.migration.provision.aws.form.title')} />

                {quickstartParamGroups.map(group => {
                    return (
                        <FormSection key={group.groupLabel} title={group.groupLabel}>
                            {group.parameters.map(parameter => {
                                return createQuickstartFormField(parameter);
                            })}
                        </FormSection>
                    );
                })}
                <Button type="submit" appearance="primary">
                    Submit
                </Button>
            </form>
        )}
    </Form>
);

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const buildQuickstartParams = (quickstartParamDoc: any): Array<QuickstartParameterGroup> => {
    const params: Record<string, QuickStartParameterYamlNode> = quickstartParamDoc.Parameters;
    const paramLabels: Record<string, QuickstartParamLabelYamlNode> =
        quickstartParamDoc.ParameterLabels;
    const paramGroups: Array<QuickstartParamGroupYamlNode> = quickstartParamDoc.ParameterGroups;

    return paramGroups.map(group => {
        const { Label, Parameters } = group;
        const paramGroupLabel = Label;
        return {
            groupLabel: paramGroupLabel.default,
            parameters: Parameters.map(parameter => {
                return {
                    paramKey: parameter,
                    paramLabel: paramLabels[parameter].default,
                    paramProperties: params[parameter],
                };
            }),
        };
    });
};

export const QuickStartDeploy: FunctionComponent = (): ReactElement => {
    const [params, setParams]: [Array<QuickstartParameterGroup>, Function] = useState([]);
    const [hasUpdatedTemplate, setHasUpdatedTemplate] = useState(false);

    useEffect(() => {
        if (!hasUpdatedTemplate) {
            fetch(QUICKSTART_PARAMS_URL, {
                method: 'GET',
            })
                .then(resp => resp.text())
                .then(text => {
                    const paramDoc = yaml.parse(text);

                    const groupedParameters = buildQuickstartParams(paramDoc);

                    setParams(groupedParameters);
                    setHasUpdatedTemplate(true);
                });
        }
    });

    return (
        <div>
            {hasUpdatedTemplate ? <QuickstartForm quickstartParamGroups={params} /> : <Spinner />}
        </div>
    );
};
