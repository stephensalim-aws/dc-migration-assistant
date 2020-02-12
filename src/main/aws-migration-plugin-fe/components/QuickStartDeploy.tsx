import React, { FunctionComponent, ReactElement, useState, useEffect } from 'react';
import yaml from 'yaml';
import Form, { FormHeader, FormSection } from '@atlaskit/form';
import Button from '@atlaskit/button';
import Spinner from '@atlaskit/spinner';
import { I18n } from '@atlassian/wrm-react-i18n';

import { createQuickstartFormField } from '../utils/quickstartToAtlaskit.tsx';

type QuickStartParameterYamlNode = {
    Type: string;
    Default: string | number | boolean;
    Description: string;
    AllowedValues?: Array<string | boolean>;
    ConstraintDescription?: string;
    AllowedPattern?: string;
    MaxLength?: number;
    MinLength?: number;
    MaxValue?: number;
    MinValue?: number;
    NoEcho?: boolean;
};

type QuickstartParamLabelYamlNode = {
    default: string;
};

type QuickstartParamGroupYamlNode = {
    Label: QuickstartParamLabelYamlNode;
    Parameters: Array<string>;
};

type QuickstartParameterGroup = {
    groupLabel: string;
    parameters: Array<QuickstartParameter>;
};

export type QuickstartParameterProperties = QuickStartParameterYamlNode;

export type Test = {
    hello: boolean;
};

export type QuickstartParameter = {
    paramKey: string;
    paramLabel: string;
    paramProperties: QuickstartParameterProperties;
};

const QuickstartForm = ({
    quickstartParamGroups,
}: Record<string, Array<QuickstartParameterGroup>>): ReactElement => (
    /* eslint-disable @typescript-eslint/no-explicit-any */
    <Form onSubmit={(data: FormData): void => console.log('form data', data)}>
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

const quickstartUrl =
    'https://dcd-slinghost-templates.s3.amazonaws.com/quickstart-jira-dc-with-vpc.template.parameters.yaml';

export const QuickStartDeploy: FunctionComponent = (): ReactElement => {
    const [params, setParams]: [Array<QuickstartParameterGroup>, Function] = useState([]);
    const [hasUpdatedTemplate, setHasUpdatedTemplate] = useState(false);

    useEffect(() => {
        if (!hasUpdatedTemplate) {
            fetch(quickstartUrl, {
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
