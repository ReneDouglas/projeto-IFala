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
  onHomeClick: () => void;
}

// ================================
// COMPONENTE HEADER
// ================================
function Header({
  sidebarOpen,
  setSidebarOpen,
  onHomeClick,
}: HeaderProps) {
  // Alterna estado do menu lateral (abre/fecha)
  const toggleSidebar = () => {
    setSidebarOpen(!sidebarOpen);
  };

  return (
    <header className='header'>
      <div className='header-container'>
        {/* Logo e título - área clicável) */}
        <div
          className='header-brand'
          onClick={onHomeClick}
          role='button'
          tabIndex={0}
        >
          <img src={ifalaLogo} alt='Logo IFala' className='header-logo' />
          <div className='header-title'>
            <h1 className='app-title'></h1>
            <p className='app-subtitle'>Campus Corrente</p>
          </div>
        </div>

        {/* Botão do menu hamburger) */}
        <button
          className='hamburger-menu'
          onClick={toggleSidebar}
          aria-label='Abrir menu de navegação'
        >
          <span className='material-symbols-outlined'>menu</span>
        </button>
      </div>
    </header>
  );
}

export default Header;
