import { isDevMode } from "@/utils/env";

/** UI 上的常量*/
export const UNKNOWN_ERROR_MSG = '未知错误，请重试';

/** 设置 */
export const REQUEST_BASE_URL = isDevMode() ? '/dev/': '/';

export const BASE_ROUTE = import.meta.env.BASE_URL;

export const IGNORE_AUTH = true;

/** 缓存的 KEY */
export const TOKEN_KEY = 'TOKEN__';

export const USER_INFO_KEY = 'USER__INFO__';