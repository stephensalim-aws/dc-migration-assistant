const routePrefix = (() => {
    const awsMigrationServletPath = '/plugins/servlet/aws-migration-plugin';
    const pathname = WRM.contextPath(); // eslint-disable-line no-undef

    if (pathname.includes(awsMigrationServletPath)) {
        return pathname;
    }

    return `${pathname}${awsMigrationServletPath}`;
})();

export const homePath = routePrefix;
export const overviewPath = `${routePrefix}/overview`;
