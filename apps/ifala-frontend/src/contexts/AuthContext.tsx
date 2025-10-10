import { createContext, useContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';

// Tipos para o contexto de autenticação
interface User {
  usuario: string;
  nome: string;
  perfil: string;
  loggedIn: boolean;
}

interface AuthContextType {
  user: User | null;
  isLoggedIn: boolean;
  login: (userData: User) => void;
  logout: () => void;
  checkAuthStatus: () => void;
}

// Criar o contexto
const AuthContext = createContext<AuthContextType | undefined>(undefined);

// Provider do contexto de autenticação
export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  // Verificar se há usuário logado no localStorage ao carregar
  const checkAuthStatus = () => {
    try {
      const storedUser = localStorage.getItem('usuarioLogado');
      if (storedUser) {
        const userData = JSON.parse(storedUser);
        if (userData && userData.loggedIn) {
          setUser(userData);
          setIsLoggedIn(true);
        }
      }
    } catch (error) {
      console.error('Erro ao verificar status de autenticação:', error);
      // Limpar dados corrompidos
      localStorage.removeItem('usuarioLogado');
    }
  };

  // Função para fazer login
  const login = (userData: User) => {
    setUser(userData);
    setIsLoggedIn(true);
    localStorage.setItem('usuarioLogado', JSON.stringify(userData));
  };

  // Função para fazer logout
  const logout = () => {
    setUser(null);
    setIsLoggedIn(false);
    localStorage.removeItem('usuarioLogado');
  };

  // Verificar status de autenticação ao montar o componente
  useEffect(() => {
    checkAuthStatus();
  }, []);

  const value = {
    user,
    isLoggedIn,
    login,
    logout,
    checkAuthStatus,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}

// Hook para usar o contexto de autenticação
export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth deve ser usado dentro de um AuthProvider');
  }
  return context;
}