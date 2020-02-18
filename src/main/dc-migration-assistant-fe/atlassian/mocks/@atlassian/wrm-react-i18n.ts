import format from '../wrm/format.ts';
import { getPhrase } from '../i18n-mock.ts';

export const I18n = {
    getText(phraseId: string, ...params: Array<string>) {
        const phrase = getPhrase(phraseId);

        return format(phrase, ...params);
    },
};

export { format };
