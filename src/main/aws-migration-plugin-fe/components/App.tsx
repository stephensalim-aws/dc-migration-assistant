import React, { ReactElement } from 'react';

interface AppProps {
    title: string;
}

export function App(props: AppProps): ReactElement {
    const { title } = props;
    return (
        <>
            <h1>{title}</h1>
            <p>It&apos;s time to get you into the cloud, AWS Style!!</p>
        </>
    );
}
