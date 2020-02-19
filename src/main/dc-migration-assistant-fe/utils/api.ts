// eslint-disable-next-line import/no-unresolved
import contextPath from 'wrm/context-path';

type HTTPMethod = 'GET' | 'POST' | 'PUT' | 'DELETE';

type FetchHeader = Record<string, string>;
type FetchOption = string | FetchHeader;
type FetchOptions = Record<string, FetchOption>;

const addToOptionsIfexists = (
    newOption: FetchOption,
    currentOptions: FetchOptions
): FetchOptions => {
    if (newOption) {
        return {
            ...currentOptions,
            newOption,
        };
    }
    return currentOptions;
};

export const callAppRest = (
    method: HTTPMethod,
    path: string,
    body?: Record<string, any>,
    headers?: Record<string, string>,
    queryParams?: string
): Promise<Response> => {
    let options: FetchOptions = {
        method,
        headers: { 'Content-Type': 'application/json', ...headers },
    };

    options = addToOptionsIfexists(body, options);
    options = addToOptionsIfexists(headers, options);

    return fetch(`${contextPath()}/rest/dc-migration/1.0/${path}?${queryParams}`, options);
};
