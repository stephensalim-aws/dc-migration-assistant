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

export type QuickstartParamLabelYamlNode = {
    default: string;
};

export type QuickstartParamGroupYamlNode = {
    Label: QuickstartParamLabelYamlNode;
    Parameters: Array<string>;
};

export type QuickstartParameterGroup = {
    groupLabel: string;
    parameters: Array<QuickstartParameter>;
};

export type QuickstartParameterProperties = QuickStartParameterYamlNode;

export type QuickstartParameter = {
    paramKey: string;
    paramLabel: string;
    paramProperties: QuickstartParameterProperties;
};
