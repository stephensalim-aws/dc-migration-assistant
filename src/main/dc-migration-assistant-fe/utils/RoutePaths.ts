// eslint-disable-next-line import/no-unresolved
import contextPath from 'wrm/context-path';

const routePrefix = ((): string => {
    const awsMigrationServletPath = '/plugins/servlet/dc-migration-assistant';
    const pathname = contextPath(); // eslint-disable-line no-undef

    if (pathname.includes(awsMigrationServletPath)) {
        return pathname;
    }

    return `${pathname}${awsMigrationServletPath}`;
})();

export const homePath = routePrefix;
export const overviewPath = `${routePrefix}/overview`;

export const awsBasePath = `${routePrefix}/aws`;
export const quickstartPath = `${awsBasePath}/provision`;
export const awsAuthPath = `${awsBasePath}/auth`;
