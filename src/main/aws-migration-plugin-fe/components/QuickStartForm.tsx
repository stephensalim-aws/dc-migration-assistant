import React, { FunctionComponent, ReactElement, ReactNode } from 'react';
import yaml from 'yaml';
import { string } from 'prop-types';

const quickstartUrl =
    'https://dcd-slinghost-templates.s3.amazonaws.com/quickstart-jira-dc-with-vpc.template.parameters.yaml';

type QuickStartParameterYamlNode = {
    Type: string;
    Default: string | number;
    Description: string;
    AllowedValues?: Array<string>;
    ConstraintDescription?: string;
    AllowedPattern?: string;
    MaxLength?: number;
    MinLength?: number;
    MaxValue?: number;
    MinValue?: number;
};

/** Quickstart param to atlaskit form component mapping
 *
 * Type: String - Input
 * Type: String, AllowedValues - Select (if values are true or false use Toggle)
 * Type: Number - Input (number)
 * Type: Number, Max/MinValue - Input with min/max
 * Type: String, AllowedPattern/MaxLength/MinLength - Input with constraints
 * Type: List<AWS::EC2::AvailabilityZone::Name> - select with AZ's for region
 */

const createSelectFromQuickstartParam = (
    key: string,
    param: QuickStartParameterYamlNode
): ReactNode => {
    return <div />;
};

const createAZSelection = (key: string, param: QuickStartParameterYamlNode): ReactNode => {
    return <div />;
};

const createInputFromQuickstartParam = (
    key: string,
    param: QuickStartParameterYamlNode
): ReactNode => {
    return <div />;
};

const quickstartParamToAtlaskitFormElement = (
    key: string,
    param: QuickStartParameterYamlNode
): ReactNode => {
    if (param.AllowedValues) {
        return createSelectFromQuickstartParam(key, param);
    }
    if (param.Type === 'List<AWS::EC2::AvailabilityZone::Name>') {
        return createAZSelection(key, param);
    }
    return createInputFromQuickstartParam(key, param);
};

export const QuickstartForm: FunctionComponent = (): ReactElement => {
    fetch(quickstartUrl, {
        method: 'GET',
    })
        .then(resp => resp.text())
        .then(text => {
            const paramDoc = yaml.parse(text);
            const params = paramDoc.Parameters;
            console.log(params);

            Object.entries(params).forEach((entry: [string, QuickStartParameterYamlNode]) => {
                const [key, value] = entry;
                console.log(key);
                console.log(value);
            });
        });

    return <h1>Hello</h1>;
};
