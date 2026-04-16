/**
 * 表单验证规则
 */

// 表单验证规则
export const FORM_RULES = {
  APP_NAME: {
    MIN_LENGTH: 1,
    MAX_LENGTH: 50,
  },
  PROMPT: {
    MAX_LENGTH: 1000,
  },
  USER_NAME: {
    MIN_LENGTH: 1,
    MAX_LENGTH: 20,
  },
  USER_ACCOUNT: {
    MIN_LENGTH: 4,
    MAX_LENGTH: 16,
  },
  PASSWORD: {
    MIN_LENGTH: 8,
    MAX_LENGTH: 20,
  },
} as const
