import { createContext } from 'react';
import type { LoginRequest } from '../types/auth';

// Tipos para o contexto de autenticação
export interface User {
  id: string;
  nome: string;
  email: string;
  username: string;
  roles: string[];
  loggedIn: boolean;
}

export interface AuthContextType {
  user: User | null;
  isLoggedIn: boolean;
  loading: boolean;
  login: (credentials: LoginRequest) => Promise<void>;
  logout: () => Promise<void>;
  checkAuthStatus: () => void;
}

// Criar o contexto
export const AuthContext = createContext<AuthContextType | undefined>(
  undefined,
);
