import { useState } from 'react';
import { Outlet, useLocation, useNavigate } from 'react-router-dom';
import Header from './Header';
import Sidebar from './Sidebar';
import { useAuth } from '../contexts/AuthContext';

export function MainLayout() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();
  const { isLoggedIn, logout } = useAuth();

  // Define qual variante do header usar com base na URL atual
  const headerVariant = location.pathname === '/' ? 'home' : 'page';

  // Funções de navegação para o sidebar
  const handleLogin = () => {
    navigate('/login');
  };

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const handleDashboard = () => {
    // TODO: Implementar navegação para dashboard
  };

  const handleChangePassword = () => {
    // TODO: Implementar navegação para alterar senha
  };

  const handleNewDenuncia = () => {
    navigate('/denuncia');
  };

  return (
    <div className='app'>
      <Header
        setSidebarOpen={setSidebarOpen}
        variant={headerVariant} // Passa a variante correta para o Header
      />
      <Sidebar
        sidebarOpen={sidebarOpen}
        setSidebarOpen={setSidebarOpen}
        isLoggedIn={isLoggedIn}
        onLogin={handleLogin}
        onLogout={handleLogout}
        onDashboard={handleDashboard}
        onChangePassword={handleChangePassword}
        onNewDenuncia={handleNewDenuncia}
      />
      <main>
        {/* O <Outlet /> é o espaço onde o conteúdo de cada página será renderizado */}
        <Outlet />
      </main>
    </div>
  );
}
