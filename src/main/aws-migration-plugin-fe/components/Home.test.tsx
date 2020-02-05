import React from 'react';
import { render } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

import { Home } from './Home.tsx';

describe('Home', () => {
    it('Should render', () => {
        const { queryByText } = render(
            <MemoryRouter>
                <Home
                    title="Test Title"
                    synopsis="Test synopsis"
                    exploreMigrationButtonText="Test button"
                />
            </MemoryRouter>
        );

        expect(queryByText('Test Title')).toBeTruthy();
        expect(queryByText('Test synopsis')).toBeTruthy();
        expect(queryByText('Test button')).toBeTruthy();
    });
});
