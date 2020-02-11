import React, { FunctionComponent, ReactElement } from 'react';
import Select from '@atlaskit/select';
import Toggle from '@atlaskit/toggle';
import TextField from '@atlaskit/textfield';
import { Field } from '@atlaskit/form';
import { string } from 'prop-types';

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
        <Field
            name={key}
            label={key}
            key={key}
            options={param.AllowedValues.map(val => ({ label: val, value: val }))}
            value={{ label: param.Default, value: param.Default }}
        >
            {({ fieldProps }: any): ReactElement => <Select {...fieldProps} />}
        </Field>
    );
};

const createAZSelection = (key: string, param: QuickStartParameterYamlNode): ReactElement => {
    return <div key={key} />;
};

const createNumberInputFromQuickstartParam = (
    key: string,
    param: QuickStartParameterYamlNode
): ReactElement => {
    let textFieldProps: Record<string, string | number> = {
        name: key,
        label: key,
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

    return (
        <Field {...textFieldProps}>
            {({ fieldProps }: any): ReactElement => <TextField {...fieldProps} />}
        </Field>
    );
};

const createStringInputFromQuickstartParam = (
    key: string,
    param: QuickStartParameterYamlNode
): ReactElement => {
    let textFieldProps: any = {
        key,
        name: key,
        label: key,
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

    return (
        <Field {...textFieldProps}>
            {({ fieldProps }: any): ReactElement => <TextField {...fieldProps} />}
        </Field>
    );
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
    if (param.AllowedValues) {
        return createSelectFromQuickstartParam(key, param);
    }
    if (param.Type === 'List<AWS::EC2::AvailabilityZone::Name>') {
        return createAZSelection(key, param);
    }
    return createInputFromQuickstartParam(key, param);
};
