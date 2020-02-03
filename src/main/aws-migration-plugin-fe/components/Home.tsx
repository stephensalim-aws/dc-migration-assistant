import React, { ReactElement } from 'react';
import Button from '@atlaskit/button';

type HomeProps = {
    title: string;
    synopsis: string;
    exploreMigrationButtonText: string;
};

const Home = ({ title, synopsis, exploreMigrationButtonText }: HomeProps): ReactElement => {
    return (
        <>
            <h2>{title}</h2>
            <p>{synopsis}</p>
            <Button appearance="primary">{exploreMigrationButtonText}</Button>
        </>
    );
};

export default Home;
