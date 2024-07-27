export type AuthProvider = 'DAILY' | 'GOOGLE';

export function isDailyAuthProvider(authProvider: AuthProvider): boolean {
  return authProvider === 'DAILY';
}

export function isOauth2AuthProvider(authProvider: AuthProvider): boolean {
  return !isDailyAuthProvider(authProvider);
}
