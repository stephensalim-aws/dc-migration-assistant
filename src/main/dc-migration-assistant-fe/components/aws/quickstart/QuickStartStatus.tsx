import React, { FunctionComponent, ReactElement, useEffect, useState } from 'react';
import styled from 'styled-components';
import Button, { ButtonAppearances } from '@atlaskit/button';
import Spinner from '@atlaskit/spinner';
import { BrowserRouterProps } from 'react-router-dom';
import { callAppRest } from '../../../utils/api';
import { awsStackStatusRestPath } from '../../../utils/RoutePaths';

const QuickStartStatusContainer = styled.div`
    margin-top: 250px;
    align-self: flex-start;
`;

const proceedToFileSystemMigrateButton = (isStackProvisioned: boolean): ReactElement => {
    return (
        <Button appearance={isStackProvisioned ? 'primary' : 'warning'}>
            Proceed to Filesystem migration
        </Button>
    );
};

export const QuickStartStatus: FunctionComponent = (props: Record<any, any>): ReactElement => {
    const [inProgress, setInProgress]: [boolean, Function] = useState(true);
    const [provisioningFailed, setProvisioningFailed]: [boolean, Function] = useState(false);

    function getStackStatusFromApi(stackIdentifier: string): Promise<any> {
        return callAppRest('GET', awsStackStatusRestPath.replace(':stackId:', stackIdentifier))
            .then(response => {
                if (response.status !== 200) {
                    throw Error('Stack not found');
                }
                return response;
            })
            .then(r => r.text())
            .then(status => {
                if (status === 'CREATE_COMPLETE') {
                    setInProgress(false);
                    setProvisioningFailed(false);
                }
            })
            .catch(err => {
                setProvisioningFailed(true);
                setInProgress(false);
            });
    }

    useEffect(() => {
        const { match } = props;
        const stackIdentifier = match.params.stackId;
        getStackStatusFromApi(stackIdentifier).then(() => {
            console.log(
                'Retrieved status from API once. TODO: periodically poll this API endpoint'
            );
        });
    });

    return (
        <QuickStartStatusContainer>
            {inProgress ? <Spinner /> : proceedToFileSystemMigrateButton(provisioningFailed)}
        </QuickStartStatusContainer>
    );
};
