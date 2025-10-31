// ================================
// COMPONENTE SIDEBAR - MENU LATERAL
// Componente reutilizável para o menu de navegação lateral
// ================================
import './Sidebar.css';

// Propriedades do componente Sidebar
interface SidebarProps {
  sidebarOpen: boolean;
  setSidebarOpen: (open: boolean) => void;
  isLoggedIn: boolean;
  onLogin: () => void;
  onLogout: () => void;
  onDashboard: () => void;
  onChangePassword: () => void;
  onNewDenuncia: () => void;
  onAcompanhamento: () => void;
}

// ================================
// COMPONENTE SIDEBAR
// ================================
function Sidebar({
  sidebarOpen,
  setSidebarOpen,
  isLoggedIn,
  onLogin,
  onLogout,
  onDashboard,
  onChangePassword,
  onNewDenuncia,
  onAcompanhamento,
}: SidebarProps) {
  // Fecha o menu lateral
  const closeSidebar = () => {
    setSidebarOpen(false);
  };

  return (
    <>
      {/* Overlay escuro quando sidebar está aberto */}
      {sidebarOpen && (
        <div className='sidebar-overlay' onClick={closeSidebar}></div>
      )}

      {/* Menu lateral */}
      <nav className={`sidebar ${sidebarOpen ? 'open' : ''}`}>
        {/* Cabeçalho do menu */}
        <div className='sidebar-header'>
          <h2 className='sidebar-title'></h2>
          <button
            className='sidebar-close'
            onClick={closeSidebar}
            aria-label='Fechar menu'
          >
            <span className='material-symbols-outlined'>close</span>
          </button>
        </div>

        {/* Conteúdo do menu */}
        <div className='sidebar-content'>
          {/* Seção principal - sempre visível */}
          <div className='menu-section'>
            <h3 className='menu-section-title'>Navegação</h3>
            <button
              className='menu-item'
              onClick={() => {
                window.location.href = '/';
                closeSidebar();
              }}
            >
              <span className='material-symbols-outlined'>home</span>
              Página Inicial
            </button>
            <button
              className='menu-item'
              onClick={() => {
                onNewDenuncia();
                closeSidebar();
              }}
            >
              <span className='material-symbols-outlined'>shield</span>
              Fazer Denúncia
            </button>
            <button
              className='menu-item'
              onClick={() => {
                onAcompanhamento();
                closeSidebar();
              }}
            >
              <span className='material-symbols-outlined'>search</span>
              Acompanhar Denúncia
            </button>
          </div>

          {isLoggedIn ? (
            // Menu para usuário logado (admin)
            <div className='menu-section'>
              <h3 className='menu-section-title'>Painel Administrativo</h3>
              <button
                className='menu-item'
                onClick={() => {
                  onDashboard();
                  closeSidebar();
                }}
              >
                <span className='material-symbols-outlined'>dashboard</span>
                Dashboard de Denúncias
              </button>
              <button
                className='menu-item'
                onClick={() => {
                  onChangePassword();
                  closeSidebar();
                }}
              >
                <span className='material-symbols-outlined'>lock</span>
                Alterar Senha
              </button>
              <button
                className='menu-item logout'
                onClick={() => {
                  onLogout();
                  closeSidebar();
                }}
              >
                <span className='material-symbols-outlined'>logout</span>
                Sair
              </button>
            </div>
          ) : (
            // Menu para usuário não logado
            <div className='menu-section'>
              <h3 className='menu-section-title'>Acesso Administrativo</h3>
              <button
                className='menu-item'
                onClick={() => {
                  onLogin();
                  closeSidebar();
                }}
              >
                <span className='material-symbols-outlined'>login</span>
                Login
              </button>
            </div>
          )}
        </div>
      </nav>
    </>
  );
}

export default Sidebar;
