import { useState, useEffect } from 'react';
import type { ReactNode } from 'react';
import { AuthContext, type User } from './AuthContextTypes';
import * as authApi from '../services/auth-api';
import type { LoginRequest } from '../types/auth';

// Provider do contexto de autenticação
export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [loading, setLoading] = useState(true);

  // Verificar se há usuário logado no localStorage ao carregar
  const checkAuthStatus = () => {
    try {
      const token = localStorage.getItem('access_token');
      const storedUser = localStorage.getItem('usuarioLogado');

      if (token && storedUser) {
        // Verificar se token está expirado
        if (!authApi.isTokenExpired(token)) {
          const userData = JSON.parse(storedUser);
          setUser(userData);
          setIsLoggedIn(true);
        } else {
          // Token expirado, limpar dados
          localStorage.removeItem('access_token');
          localStorage.removeItem('usuarioLogado');
        }
      }
    } catch (error) {
      console.error('Erro ao verificar status de autenticação:', error);
      // Limpar dados corrompidos
      localStorage.removeItem('access_token');
      localStorage.removeItem('usuarioLogado');
    } finally {
      setLoading(false);
    }
  };

  // Função para fazer login
  const login = async (credentials: LoginRequest) => {
    try {
      const { loginData, user: userData } = await authApi.login(credentials);

      // Armazenar token e dados do usuário
      localStorage.setItem('access_token', loginData.token);
      localStorage.setItem(
        'usuarioLogado',
        JSON.stringify({ ...userData, loggedIn: true }),
      );

      setUser({ ...userData, loggedIn: true });
      setIsLoggedIn(true);
    } catch (error) {
      console.error('Erro ao fazer login:', error);
      throw error;
    }
  };

  // Função para fazer logout
  const logout = async () => {
    try {
      await authApi.logout();
    } catch (error) {
      console.error('Erro ao fazer logout no servidor:', error);
    } finally {
      // Limpar dados locais independentemente do resultado
      setUser(null);
      setIsLoggedIn(false);
      localStorage.removeItem('access_token');
      localStorage.removeItem('usuarioLogado');
    }
  };

  // Verificar status de autenticação ao montar o componente
  useEffect(() => {
    checkAuthStatus();
  }, []);

  const value = {
    user,
    isLoggedIn,
    loading,
    login,
    logout,
    checkAuthStatus,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
