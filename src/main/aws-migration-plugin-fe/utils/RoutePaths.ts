// eslint-disable-next-line import/no-unresolved
import contextPath from 'wrm/context-path';

const routePrefix = ((): string => {
    const awsMigrationServletPath = '/plugins/servlet/aws-migration-plugin';
    const pathname = contextPath(); // eslint-disable-line no-undef

    if (pathname.includes(awsMigrationServletPath)) {
        return pathname;
    }

    return `${pathname}${awsMigrationServletPath}`;
})();

export const homePath = routePrefix;
export const overviewPath = `${routePrefix}/overview`;
export const quickstartPath = `${routePrefix}/provision-aws`;
export const awsAuthPath = `${routePrefix}/auth-aws`;
