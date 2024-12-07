import type { AuthProvider } from './AuthProvider';

export default interface InfoDto {
  fullName: string;
  email: string;
  authProvider: AuthProvider;
}
