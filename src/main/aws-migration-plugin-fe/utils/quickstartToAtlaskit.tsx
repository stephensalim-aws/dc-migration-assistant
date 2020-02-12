import React, { ReactElement } from 'react';
import Select from '@atlaskit/select';
import Toggle from '@atlaskit/toggle';
import TextField from '@atlaskit/textfield';
import { ErrorMessage, Field, HelperMessage } from '@atlaskit/form';

// eslint-disable-next-line import/extensions
import { QuickstartParameter } from '../components/QuickStartDeploy';

/* eslint-disable @typescript-eslint/no-explicit-any */
type FormElementGenerator = (
    defaultProps: Record<string, string>,
    param: QuickstartParameter
) => ReactElement;
type InputProps = Record<string, boolean | number | string>;

const createAZSelection: FormElementGenerator = (defaultFieldProps, param) => {
    const {
        paramProperties: { Description },
    } = param;
    return (
        <Field {...defaultFieldProps}>
            {({ fieldProps }: any): ReactElement => (
                <>
                    <HelperMessage>{Description}</HelperMessage>
                    <div {...fieldProps} />
                </>
            )}
        </Field>
    );
};

const createNumberInputFromQuickstartParam: FormElementGenerator = (defaultFieldProps, param) => {
    const {
        paramProperties: { Default, Description, MaxValue, MinValue },
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
                return (
                    <>
                        <HelperMessage>{Description}</HelperMessage>
                        <TextField width="medium" {...fieldProps} {...overrideInputProps} />
                    </>
                );
            }}
        </Field>
    );
};

const createStringInputFromQuickstartParam: FormElementGenerator = (defaultFieldProps, param) => {
    const {
        paramProperties: {
            AllowedPattern,
            ConstraintDescription,
            Description,
            Default,
            MaxLength,
            NoEcho,
        },
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
            validate: (value: string): string => {
                const regex = new RegExp(AllowedPattern);
                const testSuccess = regex.test(value);
                if (testSuccess) {
                    return undefined;
                }
                return ConstraintDescription || `${param.paramKey} must match ${AllowedPattern}`;
            },
        };
    }

    return (
        <Field {...defaultFieldProps} {...overrideFieldProps}>
            {({ fieldProps, error }: any): ReactElement => (
                <>
                    <HelperMessage>{Description}</HelperMessage>
                    <TextField width="xlarge" {...fieldProps} {...overrideInputProps} />
                    {error && <ErrorMessage>{error}</ErrorMessage>}
                </>
            )}
        </Field>
    );
};

const createInputFromQuickstartParam: FormElementGenerator = (defaultFieldProps, param) => {
    const {
        paramKey,
        paramProperties: { Type },
    } = param;
    if (Type === 'Number') {
        return createNumberInputFromQuickstartParam(defaultFieldProps, param);
    }
    if (Type === 'String') {
        return createStringInputFromQuickstartParam(defaultFieldProps, param);
    }

    return <div key={paramKey}>UNRECOGNISED PARAM TYPE</div>;
};

const createSelectFromQuickstartParam: FormElementGenerator = (defaultFieldProps, param) => {
    const { paramProperties } = param;
    const { AllowedValues, Default, Description } = paramProperties;
    if (AllowedValues.length === 2 && typeof AllowedValues[0] === 'boolean') {
        return (
            <Field {...defaultFieldProps}>
                {({ fieldProps }: any): ReactElement => (
                    <>
                        <HelperMessage>{Description}</HelperMessage>
                        <Toggle
                            {...fieldProps}
                            size="large"
                            isDefaultChecked={Default as boolean}
                        />
                    </>
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
                <>
                    <HelperMessage>{Description}</HelperMessage>
                    <Select {...fieldProps} {...overrideFieldProps} />
                </>
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
    const { paramKey, paramLabel } = param;

    const defaultFieldProps = {
        key: paramKey,
        label: paramLabel,
        name: paramKey,
        defaultValue: '',
    };

    return quickstartParamToAtlaskitFormElement(defaultFieldProps, param);
};
