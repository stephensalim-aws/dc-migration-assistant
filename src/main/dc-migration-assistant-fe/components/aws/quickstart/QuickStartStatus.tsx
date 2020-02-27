import React, { FunctionComponent, ReactElement, useEffect, useState } from 'react';
import styled from 'styled-components';
import SuccessIcon from '@atlaskit/icon/glyph/check-circle';
import ErrorIcon from '@atlaskit/icon/glyph/error';
import Spinner from '@atlaskit/spinner';
import Flag from '@atlaskit/flag';
import { callAppRest } from '../../../utils/api';
import { awsStackStatusRestPath } from '../../../utils/RoutePaths';

const QuickStartStatusContainer = styled.div`
    display: flex;
`;

const stageStatusFlag = (currentProvisioningStatus: string): ReactElement => {
    return (
        <Flag
            actions={[
                {
                    content: 'CloudFormation Console',
                    onClick: () => {},
                },
            ]}
            icon={
                currentProvisioningStatus === 'CREATE_COMPLETE' ? (
                    <SuccessIcon primaryColor="#36B37E" label="Success" />
                ) : currentProvisioningStatus === 'CREATE_IN_PROGRESS' ? (
                    <SuccessIcon primaryColor="#FFC400" label="InProgress" />
                ) : (
                    <ErrorIcon primaryColor="#FF5630" label="Failure" />
                )
            }
            description="All good things take time. Like your next Uber Eats delivery!"
            id="1"
            key="1"
            title="Provisioning Status"
        />
    );
};

export const QuickStartStatus: FunctionComponent = (props: Record<any, any>): ReactElement => {
    const [inProgress, setInProgress]: [boolean, Function] = useState(true);
    const [provisioningStatus, setProvisioningStatus]: [string, Function] = useState('UNKNOWN');

    const getStackStatusFromApi = (stackIdentifier: string): Promise<any> => {
        return callAppRest('GET', awsStackStatusRestPath.replace(':stackId:', stackIdentifier))
            .then(response => {
                if (response.status !== 200) {
                    throw Error('FAILED');
                }
                return response;
            })
            .then(r => r.text())
            .then(status => {
                if (status === 'CREATE_COMPLETE') {
                    setInProgress(false);
                    setProvisioningStatus(status);
                } else {
                    setProvisioningStatus(status);
                    setInProgress(true);
                }
            })
            .catch(err => {
                setProvisioningStatus(err.toString());
                setInProgress(false);
            });
    };

    useEffect(() => {
        const { match } = props;
        const stackIdentifier = match.params.stackId;
        getStackStatusFromApi(stackIdentifier).then(() => {
            console.log(
                'Retrieved status from API once. TODO: periodically poll this API endpoint'
            );
        });
    }, []);

    return (
        <QuickStartStatusContainer>
            {inProgress ? <Spinner /> : stageStatusFlag(provisioningStatus)}
        </QuickStartStatusContainer>
    );
};
