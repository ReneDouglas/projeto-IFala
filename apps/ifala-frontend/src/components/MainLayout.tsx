// Salve em: src/components/MainLayout.tsx

import { useState } from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import Header from './Header';
import Sidebar from './Sidebar';

export function MainLayout() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const location = useLocation();

  // Define qual variante do header usar com base na URL atual
  const headerVariant = location.pathname === '/' ? 'home' : 'page';

  return (
    <div className='app'>
      <Header
        setSidebarOpen={setSidebarOpen}
        variant={headerVariant} // Passa a variante correta para o Header
      />
      <Sidebar
        sidebarOpen={sidebarOpen}
        setSidebarOpen={setSidebarOpen}
        // As props do sidebar que vêm do App.tsx antigo precisariam ser passadas aqui
        isLoggedIn={false} // Exemplo
        onLogin={() => {}} // Exemplo
        onLogout={() => {}} // Exemplo
        onDashboard={() => {}} // Exemplo
        onChangePassword={() => {}} // Exemplo
        onNewDenuncia={() => {}} // Exemplo
      />
      <main>
        {/* O <Outlet /> é o espaço onde o conteúdo de cada página será renderizado */}
        <Outlet />
      </main>
    </div>
  );
}
