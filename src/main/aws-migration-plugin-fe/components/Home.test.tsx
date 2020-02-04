import React from 'react';
import { render } from '@testing-library/react';
import Home from './Home.tsx';

describe('Home', () => {
    it('Should render', () => {
        const { queryByText } = render(
            <Home
                title="Test Title"
                synopsis="Test synopsis"
                exploreMigrationButtonText="Test button"
            />
        );

        expect(queryByText('Test Title')).toBeTruthy();
        expect(queryByText('Test synopsis')).toBeTruthy();
        expect(queryByText('Test button')).toBeTruthy();
    });
});
