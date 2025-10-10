import { createContext } from 'react';

// Tipos para o contexto de autenticação
export interface User {
  usuario: string;
  nome: string;
  perfil: string;
  loggedIn: boolean;
}

export interface AuthContextType {
  user: User | null;
  isLoggedIn: boolean;
  login: (userData: User) => void;
  logout: () => void;
  checkAuthStatus: () => void;
}

// Criar o contexto
export const AuthContext = createContext<AuthContextType | undefined>(
  undefined,
);
