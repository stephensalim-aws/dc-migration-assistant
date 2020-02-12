import React, { FunctionComponent, ReactElement, ReactNode } from 'react';
import Select from '@atlaskit/select';
import Toggle from '@atlaskit/toggle';
import TextField from '@atlaskit/textfield';
import { Field } from '@atlaskit/form';

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

type SelectValue = {
    label: string;
    value: string | number | boolean;
};
type InputElementAndDefault = [FunctionComponent, string | number | boolean | SelectValue];
type FormElementGenerator = (param: QuickstartParameter) => InputElementAndDefault;
type InputProps = Record<string, boolean | number | string>;

const createAZSelection: FormElementGenerator = param => {
    const input = (): ReactElement => <div />;
    return [input, ''];
};

const createNumberInputFromQuickstartParam: FormElementGenerator = param => {
    const {
        paramProperties: { Default, MaxValue, MinValue },
    } = param;

    let overrideFieldProps: InputProps = {
        type: 'number',
    };

    if (MaxValue) {
        overrideFieldProps = {
            max: MaxValue,
            ...overrideFieldProps,
        };
    }

    if (MinValue) {
        overrideFieldProps = {
            min: MinValue,
            ...overrideFieldProps,
        };
    }

    const input = (props: InputProps): ReactElement => (
        <TextField {...props} {...overrideFieldProps} />
    );
    return [input, Default];
};

const createStringInputFromQuickstartParam: FormElementGenerator = param => {
    const {
        paramProperties: { AllowedPattern, Default, MaxLength, NoEcho },
    } = param;
    let overrideFieldProps: Record<string, string | number | boolean> = {
        type: NoEcho ? 'password' : 'text',
    };

    if (MaxLength) {
        overrideFieldProps = {
            maxLength: MaxLength,
            ...overrideFieldProps,
        };
    }

    if (AllowedPattern) {
        overrideFieldProps = {
            pattern: AllowedPattern,
            ...overrideFieldProps,
        };
    }

    const input = (props: InputProps): ReactElement => (
        <TextField {...props} {...overrideFieldProps} />
    );
    return [input, Default];
};

const createInputFromQuickstartParam: FormElementGenerator = param => {
    const {
        key,
        paramProperties: { Type },
    } = param;
    if (Type === 'Number') {
        return createNumberInputFromQuickstartParam(param);
    }
    if (Type === 'String') {
        return createStringInputFromQuickstartParam(param);
    }

    const input = (): ReactElement => <div key={key}>UNRECOGNISED PARAM TYPE</div>;
    return [input, ''];
};

const createSelectFromQuickstartParam: FormElementGenerator = param => {
    const { paramProperties } = param;
    const { AllowedValues, Default } = paramProperties;
    if (AllowedValues.length === 2 && typeof AllowedValues[0] === 'boolean') {
        const input = (props: InputProps): ReactElement => (
            <Toggle {...props} size="large" isDefaultChecked={Default as boolean} />
        );
        return [input, Default];
    }

    const options = AllowedValues.map(val => ({ label: val as string, value: val }));
    const defaultOption = options.find(option => option.value === Default);

    const overrideFieldProps = {
        options,
        deafault: defaultOption,
    };

    const input = (props: InputProps): ReactElement => {
        return <Select {...props} {...overrideFieldProps} />;
    };
    return [input, defaultOption];
};

const quickstartParamToAtlaskitFormElement: FormElementGenerator = param => {
    const { paramProperties } = param;
    if (paramProperties.AllowedValues) {
        return createSelectFromQuickstartParam(param);
    }
    if (paramProperties.Type === 'List<AWS::EC2::AvailabilityZone::Name>') {
        return createAZSelection(param);
    }
    return createInputFromQuickstartParam(param);
};

export const createQuickstartFormField = (param: QuickstartParameter): ReactElement => {
    const [FormInput, defaultValue] = quickstartParamToAtlaskitFormElement(param);

    const defaultFieldProps = {
        key: param.key,
        label: param.key,
        name: param.key,
        defaultValue: defaultValue || '',
    };

    const parentFieldProps = {
        ...defaultFieldProps,
    };

    return (
        <Field {...parentFieldProps}>
            {({ fieldProps }: any): ReactElement => {
                return <FormInput {...fieldProps} />;
            }}
        </Field>
    );
};
