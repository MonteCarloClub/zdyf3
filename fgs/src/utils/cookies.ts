/**
 * @Reference：https://javascript.info/cookie
 */

interface CookieOptions {
    [key: string] : any;
    path?: string;
    expires?: string | Date;
}

/**
 * returns the cookie with the given name, or undefined if not found
 * @param name 属性名
 * @returns 属性值
 */
export function get(name: string) {
    let matches = document.cookie.match(new RegExp(
        "(?:^|; )" + name + "=([^;]*)"
    ));
    // Please note that a cookie value is encoded, 
    // so we use a built-in decodeURIComponent function to decode it.
    return matches ? decodeURIComponent(matches[1]) : undefined;
}

/**
 * 设置一条 Cookie，比如 user=John; path=/; secure; max-age=3600
 * @param {String} name    键
 * @param {String} value   值
 * @param {Object} options 属性
 */
export function set(name:string, value:string, options = {} as CookieOptions) {

    options = {
        path: '/',
        // add other defaults here if necessary
        ...options
    };

    if (options.expires instanceof Date) {
        options.expires = options.expires.toUTCString();
    }

    let updatedCookie = encodeURIComponent(name) + "=" + encodeURIComponent(value);
    
    let k: keyof typeof options;
    for (k in options) {
        updatedCookie += "; " + k;
        let optionValue = options[k];
        if (optionValue !== true) {
            updatedCookie += "=" + optionValue;
        }
    }

    document.cookie = updatedCookie;
}

export function remove(name: string) {
    set(name, "", {
        'max-age': -1
    })
}