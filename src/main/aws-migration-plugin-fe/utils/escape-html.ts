const special: Record<string, string> = {
    '<': '&lt;',
    '>': '&gt;',
    '&': '&amp;',
    '"': '&quot;',
    "'": '&#39;',
    '`': '&#96;',
};

const expr = new RegExp(`[${Object.keys(special).join('')}]`, 'g');

function escapeHtml(str: string): string {
    return str.replace(expr, substr => special[substr]);
}

export default escapeHtml;
