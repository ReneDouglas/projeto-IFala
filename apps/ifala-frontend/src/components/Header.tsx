// ================================
// COMPONENTE HEADER - CABEÇALHO DA APLICAÇÃO
// Componente reutilizável para o cabeçalho principal
// ================================
import './Header.css';
import ifalaLogo from '../assets/IFala-logo.png';

// Propriedades do componente Header
interface HeaderProps {
  sidebarOpen: boolean;
  setSidebarOpen: (open: boolean) => void;
  isLoggedIn: boolean;
  onHomeClick: () => void;
}

// ================================
// COMPONENTE HEADER
// ================================
function Header({ sidebarOpen, setSidebarOpen, isLoggedIn, onHomeClick }: HeaderProps) {
  // Alterna estado do menu lateral (abre/fecha)
  const toggleSidebar = () => {
    setSidebarOpen(!sidebarOpen);
  };

  return (
    <header className="header">
      <div className="header-container">
        {/* Botão do menu hamburger - visível apenas quando logado */}
        {isLoggedIn && (
          <button
            className="hamburger-menu"
            onClick={toggleSidebar}
            aria-label="Abrir menu de navegação"
          >
            <span className="material-symbols-outlined">menu</span>
          </button>
        )}

        {/* Logo e título - área clicável */}
        <div className="header-brand" onClick={onHomeClick} role="button" tabIndex={0}>
          <img src={ifalaLogo} alt="Logo IFala" className="header-logo" />
          <div className="header-title">
            <h1 className="app-title">IFala</h1>
            <p className="app-subtitle">Sistema de Denúncias Anônimas</p>
          </div>
        </div>

        {/* Espaço reservado para futuros botões de ação */}
        <div className="header-actions">
          {/* Aqui podem ser adicionados botões como notificações, perfil, etc. */}
        </div>
      </div>
    </header>
  );
}

export default Header;