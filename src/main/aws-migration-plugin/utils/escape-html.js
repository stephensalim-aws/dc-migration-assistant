const special = {
    '<': '&lt;',
    '>': '&gt;',
    '&': '&amp;',
    '"': '&quot;',
    "'": '&#39;',
    '`': '&#96;',
};

const expr = new RegExp(`[${Object.keys(special).join('')}]`, 'g');

function escapeHtml(str) {
    return str.replace(expr, str => special[str]);
}

export default escapeHtml;
