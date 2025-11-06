import axiosClient from './axios-client';
import type {
  LoginRequest,
  LoginResponse,
  RedefinirSenhaRequest,
  AuthUser,
} from '../types/auth';
import { jwtDecode } from 'jwt-decode';
import type { JwtPayload } from '../types/auth';

/**
 * Realiza login no sistema
 */
export async function login(
  credentials: LoginRequest,
): Promise<{ loginData: LoginResponse; user: AuthUser }> {
  const response = await axiosClient.post<LoginResponse>(
    '/auth/login',
    credentials,
  );

  const loginData = response.data;

  // Decodificar JWT para obter dados do usuário
  const decoded = jwtDecode<JwtPayload>(loginData.token);

  const user: AuthUser = {
    id: decoded.sub,
    nome: decoded.nome,
    email: decoded.email,
    username: decoded.username,
    roles: decoded.roles || [],
  };

  return { loginData, user };
}

/**
 * Solicitar redefinicão de senha
 */
export async function solicitarRedefinicaoSenha(email: string): Promise<void> {
  await axiosClient.post('/auth/redefinir-senha', {
    email,
    newPassword: null,
    token: null,
    currentPassword: null,
  });
}

/**
 * Redefinir senha (com token recebido por email)
 */
export async function redefinirSenha(
  data: RedefinirSenhaRequest,
): Promise<void> {
  await axiosClient.post('/auth/redefinir-senha', data);
}

/**
 * Fazer refresh do token
 */
export async function refreshToken(): Promise<LoginResponse> {
  const response = await axiosClient.post<LoginResponse>('/auth/refresh');
  return response.data;
}

/**
 * Fazer logout
 */
export async function logout(): Promise<void> {
  try {
    await axiosClient.post('/auth/sair');
  } catch (error) {
    console.error('Erro ao fazer logout:', error);
  }
}

/**
 * Decodificar token JWT para obter dados do usuário
 */
export function decodeToken(token: string): AuthUser {
  const decoded = jwtDecode<JwtPayload>(token);

  return {
    id: decoded.sub,
    nome: decoded.nome,
    email: decoded.email,
    username: decoded.username,
    roles: decoded.roles || [],
  };
}

/**
 * Verificar se token está expirado
 */
export function isTokenExpired(token: string): boolean {
  try {
    const decoded = jwtDecode<JwtPayload>(token);
    const currentTime = Date.now() / 1000;
    return decoded.exp < currentTime;
  } catch {
    return true;
  }
}

/**
 * Validar token de redefinição e retornar o email do usuário
 */
export async function validarTokenRedefinicao(token: string): Promise<string> {
  const response = await axiosClient.get<{ email: string }>(
    `/auth/validar-token-redefinicao`,
    {
      params: { token },
    },
  );
  return response.data.email;
}
