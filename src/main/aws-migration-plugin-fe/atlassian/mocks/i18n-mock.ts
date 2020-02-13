const phrases = new Map();

export const getPhrase = (phraseId: string) => {
    return phrases.has(phraseId) ? phrases.get(phraseId) : phraseId;
};
