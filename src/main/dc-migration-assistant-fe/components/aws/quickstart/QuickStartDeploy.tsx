import React, {FunctionComponent, ReactElement, useEffect, useState} from 'react';
import yaml from 'yaml';
import Form, {FormHeader, FormSection} from '@atlaskit/form';
import Button from '@atlaskit/button';
import Spinner from '@atlaskit/spinner';
import {OptionType} from '@atlaskit/select';
import {I18n} from '@atlassian/wrm-react-i18n';
import styled from 'styled-components';

import {createQuickstartFormField} from './quickstartToAtlaskit';
import {
    QuickstartParameterGroup,
    QuickStartParameterYamlNode,
    QuickstartParamGroupYamlNode,
    QuickstartParamLabelYamlNode,
} from './QuickStartTypes';

import {callAppRest} from "../../../utils/api";

const QUICKSTART_PARAMS_URL =
    'https://dcd-slinghost-templates.s3.amazonaws.com/mothra/test-create-s3-bucket.parameters.yaml';

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

const STACK_NAME_FIELD_NAME = "QSStackName";
const QuickstartForm = ({
    quickstartParamGroups,
}: Record<string, Array<QuickstartParameterGroup>>): ReactElement => (
    <Form
        onSubmit={(data: Record<string, any>): void => {
            const transformedCfnParams = data;
            let stackName = "";
            Object.entries(data).forEach(entry => {
                // Hoist value from Select/Multiselect inputs to root of form value
                const [key, value] = entry;
                if (key == STACK_NAME_FIELD_NAME){
                    stackName = value;
                    delete transformedCfnParams[key];
                }
                else if (isOptionType(value)) {
                    transformedCfnParams[key] = value.value;
                } else if (isArrayOfOptionType(value)) {
                    transformedCfnParams[key] = value.map(option => option.value);
                }
            });

            callAppRest('POST', 'aws/cloudformation/create', {
                templateUrl: 'https://dcd-slinghost-templates.s3-ap-southeast-2.amazonaws.com/mothra/test-create-s3-bucket.yaml',
                stackName: stackName,
                params: transformedCfnParams,
            }).then(x => x.text())
                // eslint-disable-next-line no-console
                .then(console.log);
        }}
    >
        {({ formProps }: any): ReactElement => (
            <QuickstartFormContainer {...formProps}>
                <FormHeader
                    title={I18n.getText('atlassian.migration.datacenter.provision.aws.form.title')}
                />

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
                    {I18n.getText('atlassian.migration.datacenter.generic.submit')}
                </QuickstartSubmitButton>
            </QuickstartFormContainer>
        )}
    </Form>
);

const buildAdditionalParameters = () : Array<QuickstartParameterGroup> => {
  return [{
          groupLabel: 'default',
          parameters: [{
              paramKey: STACK_NAME_FIELD_NAME,
              paramLabel: 'Name of the Quickstart Stack',
              paramProperties: {
                  Type: 'String',
                  Default: '',
                  Description: 'Name of the stack',
                  ConstraintDescription: '^[a-zA-Z.-]+$'
              }
          }]
      }];
};

const buildQuickstartParams = (quickstartParamDoc: any): Array<QuickstartParameterGroup> => {
    const params: Record<string, QuickStartParameterYamlNode> = quickstartParamDoc.Parameters;
    const paramLabels: Record<string, QuickstartParamLabelYamlNode> =
        quickstartParamDoc.ParameterLabels;
    const paramGroups: Array<QuickstartParamGroupYamlNode> = quickstartParamDoc.ParameterGroups;

    const formParameters = paramGroups.map(group => {
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
    return [...buildAdditionalParameters(), ...formParameters];
};

const QuickStartDeployContainer = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-items: center;
`;

export const QuickStartDeploy: FunctionComponent = (): ReactElement => {
    const [params, setParams]: [Array<QuickstartParameterGroup>, Function] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        setLoading(true);
        fetch(QUICKSTART_PARAMS_URL, {
            method: 'GET',
        })
            .then(resp => resp.text())
            .then(text => {
                const paramDoc = yaml.parse(text);

                const groupedParameters = buildQuickstartParams(paramDoc);

                setParams(groupedParameters);
                setLoading(false);
            });
    }, []);

    return (
        <QuickStartDeployContainer>
            {loading ? <Spinner /> : <QuickstartForm quickstartParamGroups={params} />}
        </QuickStartDeployContainer>
    );
};
