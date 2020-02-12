import React, { FunctionComponent, ReactElement, ReactNode } from 'react';
import Select from '@atlaskit/select';
import Toggle from '@atlaskit/toggle';
import TextField from '@atlaskit/textfield';
import { ErrorMessage, Field } from '@atlaskit/form';

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
    NoEcho?: boolean;
};

export type QuickstartParameter = {
    key: string;
    paramProperties: QuickStartParameterYamlNode;
};

type FormElementGenerator = (
    defaultProps: Record<string, string>,
    param: QuickstartParameter
) => ReactElement;
type InputProps = Record<string, boolean | number | string>;

const createAZSelection: FormElementGenerator = (defaultFieldProps, param) => {
    return <Field {...defaultFieldProps}>{({ fieldProps }: any): ReactElement => <div />}</Field>;
};

const createNumberInputFromQuickstartParam: FormElementGenerator = (defaultFieldProps, param) => {
    const {
        paramProperties: { Default, MaxValue, MinValue },
    } = param;

    let overrideInputProps: InputProps = {
        type: 'number',
    };

    if (MaxValue) {
        overrideInputProps = {
            max: MaxValue,
            ...overrideInputProps,
        };
    }

    if (MinValue) {
        overrideInputProps = {
            min: MinValue,
            ...overrideInputProps,
        };
    }
    return (
        <Field {...defaultFieldProps} defaultValue={Default as number}>
            {({ fieldProps }: any): ReactElement => {
                // console.log(fieldProps);
                return <TextField {...fieldProps} {...overrideInputProps} />;
            }}
        </Field>
    );
};

const createStringInputFromQuickstartParam: FormElementGenerator = (defaultFieldProps, param) => {
    const {
        paramProperties: { AllowedPattern, ConstraintDescription, Default, MaxLength, NoEcho },
    } = param;

    let overrideInputProps: Record<string, string | number | boolean | Function> = {
        type: NoEcho ? 'password' : 'text',
    };

    let overrideFieldProps: Record<string, string | number | boolean | Function> = {
        defaultValue: Default as string,
    };

    if (MaxLength) {
        overrideInputProps = {
            maxLength: MaxLength,
            ...overrideInputProps,
        };
    }

    if (AllowedPattern) {
        overrideInputProps = {
            pattern: AllowedPattern,
            ...overrideInputProps,
        };
        overrideFieldProps = {
            ...overrideFieldProps,
            validate: (value: string): boolean => new RegExp(AllowedPattern).test(value),
        };
    }

    return (
        <Field {...defaultFieldProps} {...overrideFieldProps}>
            {({ fieldProps, error }: any): ReactElement => (
                <>
                    <TextField {...fieldProps} {...overrideInputProps} />
                    {error && <ErrorMessage>{ConstraintDescription}</ErrorMessage>}
                </>
            )}
        </Field>
    );
};

const createInputFromQuickstartParam: FormElementGenerator = (defaultFieldProps, param) => {
    const {
        key,
        paramProperties: { Type },
    } = param;
    if (Type === 'Number') {
        return createNumberInputFromQuickstartParam(defaultFieldProps, param);
    }
    if (Type === 'String') {
        return createStringInputFromQuickstartParam(defaultFieldProps, param);
    }

    return <div key={key}>UNRECOGNISED PARAM TYPE</div>;
};

const createSelectFromQuickstartParam: FormElementGenerator = (defaultFieldProps, param) => {
    const { paramProperties } = param;
    const { AllowedValues, Default } = paramProperties;
    if (AllowedValues.length === 2 && typeof AllowedValues[0] === 'boolean') {
        return (
            <Field {...defaultFieldProps}>
                {({ fieldProps }: any): ReactElement => (
                    <Toggle {...fieldProps} size="large" isDefaultChecked={Default as boolean} />
                )}
            </Field>
        );
    }

    const options = AllowedValues.map(val => ({ label: val as string, value: val }));
    const defaultOption = options.find(option => option.value === Default);

    const overrideFieldProps = {
        options,
    };

    return (
        <Field {...defaultFieldProps} defaultValue={defaultOption}>
            {({ fieldProps }: any): ReactElement => (
                <Select {...fieldProps} {...overrideFieldProps} />
            )}
        </Field>
    );
};

const quickstartParamToAtlaskitFormElement: FormElementGenerator = (defaultFieldProps, param) => {
    const { paramProperties } = param;
    if (paramProperties.AllowedValues) {
        return createSelectFromQuickstartParam(defaultFieldProps, param);
    }
    if (paramProperties.Type === 'List<AWS::EC2::AvailabilityZone::Name>') {
        return createAZSelection(defaultFieldProps, param);
    }
    return createInputFromQuickstartParam(defaultFieldProps, param);
};

export const createQuickstartFormField = (param: QuickstartParameter): ReactElement => {
    const { key } = param;

    const defaultFieldProps = {
        key,
        label: key,
        name: key,
        defaultValue: '',
    };

    return quickstartParamToAtlaskitFormElement(defaultFieldProps, param);
};
