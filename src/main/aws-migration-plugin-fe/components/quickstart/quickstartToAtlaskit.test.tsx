import { render, fireEvent } from '@testing-library/react';

// eslint-disable-next-line import/extensions
import { QuickstartParameter } from './QuickStartTypes';
import { createQuickstartFormField } from './quickstartToAtlaskit.tsx';

const TEST_PARAM_KEY = 'TestParam';
const TEST_PARAM_LABEL = 'Test Param';
const TEST_PARAM_DESCRIPTION = 'a test parameter';
const TEST_STRING_DEFAULT = 'test default';
const TEST_CONSTRAINT_DESCRIPTION = 'test constraint description';

const STRING_INPUT: QuickstartParameter = {
    paramKey: TEST_PARAM_KEY,
    paramLabel: TEST_PARAM_LABEL,
    paramProperties: {
        Type: 'String',
        Default: TEST_STRING_DEFAULT,
        Description: TEST_PARAM_DESCRIPTION,
    },
};

const NO_ECHO_INPUT: QuickstartParameter = {
    ...STRING_INPUT,
    paramProperties: {
        ...STRING_INPUT.paramProperties,
        NoEcho: true,
    },
};

const NUMBER_INPUT: QuickstartParameter = {
    paramKey: TEST_PARAM_KEY,
    paramLabel: TEST_PARAM_LABEL,
    paramProperties: {
        Type: 'Number',
        Default: 12,
        Description: TEST_PARAM_DESCRIPTION,
    },
};

const TOGGLE_INPUT: QuickstartParameter = {
    paramKey: TEST_PARAM_KEY,
    paramLabel: TEST_PARAM_LABEL,
    paramProperties: {
        Type: 'String',
        Default: true,
        Description: TEST_PARAM_DESCRIPTION,
        AllowedValues: [true, false],
    },
};

const SELECT_INPUT: QuickstartParameter = {
    paramKey: TEST_PARAM_KEY,
    paramLabel: TEST_PARAM_LABEL,
    paramProperties: {
        Type: 'String',
        Default: 'Option 1',
        Description: TEST_PARAM_DESCRIPTION,
        AllowedValues: ['Option 1', 'Option 2'],
    },
};

const AZ_INPUT: QuickstartParameter = {
    paramKey: TEST_PARAM_KEY,
    paramLabel: TEST_PARAM_LABEL,
    paramProperties: {
        Type: 'List<AWS::EC2::AvailabilityZone::Name>',
        Description: TEST_PARAM_DESCRIPTION,
    },
};

describe('Quick Start to Atlaskit Input Converter', () => {
    it('Should create a label and description for any parameter', () => {
        const { getByText } = render(createQuickstartFormField(STRING_INPUT));

        const label = getByText(TEST_PARAM_LABEL);
        expect(label).toBeTruthy();

        const description = getByText(TEST_PARAM_DESCRIPTION);
        expect(description).toBeTruthy();
    });

    it('Should create a number input', () => {
        const { getByLabelText } = render(createQuickstartFormField(NUMBER_INPUT));

        expect(getByLabelText(TEST_PARAM_LABEL)).toMatchSnapshot();
    });

    it('Should render String inputs', () => {
        const { getByLabelText } = render(createQuickstartFormField(STRING_INPUT));

        const stringInput = getByLabelText(TEST_PARAM_LABEL);
        expect(stringInput).toMatchSnapshot();
    });

    it('Should create a toggle input when there are two allowed values that are boolean', () => {
        const { getByRole } = render(createQuickstartFormField(TOGGLE_INPUT));

        // Atlaskit toggle does not use the label attributes properly so we must look for a checkbox
        expect(getByRole('checkbox')).toMatchSnapshot();
    });

    it('Should create a select input when there are multiple allowed values that are not boolean', () => {
        const { getAllByLabelText } = render(createQuickstartFormField(SELECT_INPUT));

        const selectComponents = getAllByLabelText(TEST_PARAM_LABEL);
        const select = selectComponents.find(component => component.id === 'react-select-2-input');

        expect(select).toMatchSnapshot();
    });

    it('Should create multi select input for AZ parameter', () => {
        const { getByLabelText } = render(createQuickstartFormField(AZ_INPUT));

        expect(getByLabelText(TEST_PARAM_LABEL)).toMatchSnapshot();
    });

    it('Should create a password input for NoEcho parameters', () => {
        const { getByLabelText } = render(createQuickstartFormField(NO_ECHO_INPUT));

        expect(getByLabelText(TEST_PARAM_LABEL)).toMatchSnapshot();
    });
});
