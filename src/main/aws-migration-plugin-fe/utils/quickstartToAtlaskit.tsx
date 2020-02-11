import React, { FunctionComponent, ReactElement } from 'react';
import Select from '@atlaskit/select';
import Toggle from '@atlaskit/toggle';
import TextField from '@atlaskit/textfield';

export type QuickStartParameterYamlNode = {
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

const createSelectFromQuickstartParam = (
    key: string,
    param: QuickStartParameterYamlNode
): ReactElement => {
    if (param.AllowedValues.length === 2 && typeof param.AllowedValues[0] === 'boolean') {
        return <Toggle key={key} size="large" isDefaultChecked={param.Default as boolean} />;
    }
    return (
        <Select
            options={param.AllowedValues.map(val => ({ label: val, value: val }))}
            value={{ label: param.Default, value: param.Default }}
        />
    );
};

const createAZSelection = (key: string, param: QuickStartParameterYamlNode): ReactElement => {
    return <div />;
};

const createNumberInputFromQuickstartParam = (
    key: string,
    param: QuickStartParameterYamlNode
): ReactElement => {
    let textFieldProps: Record<string, string | number> = {
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
    let textFieldProps: Record<string, string | number | boolean> = {
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

export const quickstartParamToAtlaskitFormElement = (
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
