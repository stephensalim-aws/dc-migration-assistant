import React from 'react';
import { render } from '@testing-library/react';
import { App } from './App';

describe('App', () => {
    it('Should render', () => {
        const { queryByText } = render(<App title="hello" />);

        expect(queryByText('hello')).toBeTruthy();
    });
});
