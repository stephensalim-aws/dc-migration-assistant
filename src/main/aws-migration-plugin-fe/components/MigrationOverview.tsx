import React, { FunctionComponent } from 'react';
import { I18n } from '@atlassian/wrm-react-i18n';

export const MigrationOverview: FunctionComponent = () => {
    return <h1>{I18n.getText('aws.migration.overview.title')}</h1>;
};
