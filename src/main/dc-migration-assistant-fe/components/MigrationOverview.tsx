import React, { FunctionComponent } from 'react';
import { I18n } from '@atlassian/wrm-react-i18n';
import styled from 'styled-components';
import { Button } from '@atlaskit/button/dist/cjs/components/Button';
import { Link } from 'react-router-dom';

import { homePath } from '../utils/RoutePaths';

const OverviewContainer = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
`;

const ButtonContainer = styled.div`
    margin-top: 250px;
    align-self: flex-start;
`;
export const MigrationOverview: FunctionComponent = () => {
    return (
        <OverviewContainer>
            <h1>{I18n.getText('atlassian.migration.datacenter.overview.title')}</h1>
            <ButtonContainer>
                <Link to={homePath}>
                    <Button appearance="danger">Abort</Button>
                </Link>
            </ButtonContainer>
        </OverviewContainer>
    );
};
