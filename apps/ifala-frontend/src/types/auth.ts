/**
 * Tipos TypeScript para autenticação
 */

export interface LoginRequest {
  email?: string;
  username?: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  issuedAt: string;
  expirationTime: string;
  passwordChangeRequired: boolean;
  redirect?: string;
  message?: string;
}

export interface RedefinirSenhaRequest {
  email: string;
  newPassword: string;
  token?: string;
  currentPassword?: string | null;
}

export interface AuthUser {
  id: string;
  nome: string;
  email: string;
  username: string;
  roles: string[];
}

export interface JwtPayload {
  sub: string;
  nome: string;
  email: string;
  username: string;
  roles: string[];
  iat: number;
  exp: number;
}
