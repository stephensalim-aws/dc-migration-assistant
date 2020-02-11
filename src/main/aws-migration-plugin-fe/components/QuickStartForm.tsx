import React, { FunctionComponent, ReactElement } from 'react';

const quickstartUrl =
    'https://dcd-slinghost-templates.s3.amazonaws.com/quickstart-jira-dc-with-vpc.template.yaml';

export const QuickstartForm: FunctionComponent = (): ReactElement => {
    fetch(quickstartUrl, {
        method: 'GET',
    }).then(resp => {
        console.log(resp.body);
    });

    return <h1>Hello</h1>;
};
