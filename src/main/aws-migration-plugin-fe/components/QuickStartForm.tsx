import React, { FunctionComponent, ReactElement, ReactNode, useState, useEffect } from 'react';
import yaml from 'yaml';
import Select from '@atlaskit/select';
import Toggle from '@atlaskit/toggle';
import TextField from '@atlaskit/textfield';

const quickstartUrl =
    'https://dcd-slinghost-templates.s3.amazonaws.com/quickstart-jira-dc-with-vpc.template.parameters.yaml';

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
};

/** Quickstart param to atlaskit form component mapping
 *
 * Type: String - Input
 * Type: String, AllowedPattern/MaxLength/MinLength - Input with constraints
 * Type: Number - Input (number)
 * Type: Number, Max/MinValue - Input (number) with min/max
 * Type: String, AllowedValues - Select (if values are true or false use Toggle)
 * Type: List<AWS::EC2::AvailabilityZone::Name> - select with AZ's for region
 */

const createSelectFromQuickstartParam = (
    key: string,
    param: QuickStartParameterYamlNode
): ReactElement => {
    if (param.AllowedValues.length === 2 && typeof param.AllowedValues[0] === 'boolean') {
        return <Toggle key={key} size="large" isDefaultChecked={param.Default as boolean} />;
    }
    return (
        <Select
            key={key}
            options={param.AllowedValues.map(val => ({ label: val, value: val }))}
            value={{ label: param.Default, value: param.Default }}
        />
    );
};

const createAZSelection = (key: string, param: QuickStartParameterYamlNode): ReactElement => {
    return <div key={key} />;
};

const createNumberInputFromQuickstartParam = (
    key: string,
    param: QuickStartParameterYamlNode
): ReactElement => {
    let textFieldProps: Record<string, any> = {
        key,
        type: 'number',
        defaultValue: param.Default as number,
    };

    if (param.MaxValue) {
        textFieldProps = {
            max: param.MaxValue,
            ...textFieldProps,
        };
    }

    if (param.MinValue) {
        textFieldProps = {
            min: param.MinValue,
            ...textFieldProps,
        };
    }

    return <TextField {...textFieldProps} />;
};

const createStringInputFromQuickstartParam = (
    key: string,
    param: QuickStartParameterYamlNode
): ReactElement => {
    let textFieldProps: Record<string, any> = {
        key,
        type: 'text',
        defaultValue: param.Default as string,
    };

    if (param.MaxLength) {
        textFieldProps = {
            maxLength: param.MaxLength,
            ...textFieldProps,
        };
    }

    if (param.AllowedPattern) {
        textFieldProps = {
            pattern: param.AllowedPattern,
            isRequired: true,
            ...textFieldProps,
        };
    }

    return <TextField {...textFieldProps} />;
};

const createInputFromQuickstartParam = (
    key: string,
    param: QuickStartParameterYamlNode
): ReactElement => {
    if (param.Type === 'Number') {
        return createNumberInputFromQuickstartParam(key, param);
    }
    if (param.Type === 'String') {
        return createStringInputFromQuickstartParam(key, param);
    }

    return <div key={key}>UNRECOGNISED PARAM TYPE</div>;
};

const quickstartParamToAtlaskitFormElement = (
    key: string,
    param: QuickStartParameterYamlNode
): ReactElement => {
    let InputElement: FunctionComponent;
    if (param.AllowedValues) {
        InputElement = (): ReactElement => {
            return createSelectFromQuickstartParam(key, param);
        };
    }
    if (param.Type === 'List<AWS::EC2::AvailabilityZone::Name>') {
        InputElement = (): ReactElement => {
            return createAZSelection(key, param);
        };
    }
    InputElement = (): ReactElement => {
        return createInputFromQuickstartParam(key, param);
    };

    return (
        <div key={key}>
            <span>
                {key}
                <InputElement />
            </span>
        </div>
    );
};

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
