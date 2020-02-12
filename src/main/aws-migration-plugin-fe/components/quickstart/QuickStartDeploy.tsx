import React, { FunctionComponent, ReactElement, useState, useEffect } from 'react';
import yaml from 'yaml';
import Form, { FormHeader, FormSection } from '@atlaskit/form';
import Button from '@atlaskit/button';
import Spinner from '@atlaskit/spinner';
import { OptionType } from '@atlaskit/select';
import { I18n } from '@atlassian/wrm-react-i18n';
import styled from 'styled-components';

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

const isOptionType = (obj: any): obj is OptionType => {
    return obj.label && obj.value;
};

const isArrayOfOptionType = (obj: any): obj is Array<OptionType> => {
    return obj.length > 0 && isOptionType(obj[0]);
};

const QuickstartFormContainer = styled.form`
    width: 60%;
`;

const QuickstartSubmitButton = styled(Button)`
    margin-top: 10px;
`;

const QuickstartForm = ({
    quickstartParamGroups,
}: Record<string, Array<QuickstartParameterGroup>>): ReactElement => (
    /* eslint-disable @typescript-eslint/no-explicit-any */
    <Form
        onSubmit={(data: Record<string, any>): void => {
            const transformedCfnParams = data;
            Object.entries(data).forEach(entry => {
                // Hoist value from Select/Multiselect inputs to root of form value
                const [key, value] = entry;
                if (isOptionType(value)) {
                    transformedCfnParams[key] = value.value;
                } else if (isArrayOfOptionType(value)) {
                    transformedCfnParams[key] = value.map(option => option.value);
                }
            });

            // TODO: Send this to the API
            // eslint-disable-next-line no-console
            console.log(transformedCfnParams);
        }}
    >
        {({ formProps }: any): ReactElement => (
            /* eslint-enable @typescript-eslint/no-explicit-any */
            <QuickstartFormContainer {...formProps}>
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
                <QuickstartSubmitButton type="submit" appearance="primary">
                    Submit
                </QuickstartSubmitButton>
            </QuickstartFormContainer>
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

const QuickStartDeployContainer = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-items: center;
`;

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
        <QuickStartDeployContainer>
            {hasUpdatedTemplate ? <QuickstartForm quickstartParamGroups={params} /> : <Spinner />}
        </QuickStartDeployContainer>
    );
};
