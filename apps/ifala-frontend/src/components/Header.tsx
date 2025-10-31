import './Header.css';
import { useNavigate } from 'react-router-dom';
import ifalaLogo from '../assets/IFala-logo.png';

interface HeaderProps {
  setSidebarOpen: (open: boolean) => void;
  variant?: 'home' | 'page';
}

function Header({ setSidebarOpen, variant = 'home' }: HeaderProps) {
  const navigate = useNavigate();

  const handleHomeClick = () => {
    navigate('/');
  };

  return (
    <header className='header'>
      <div className='header-container'>
        {/* --- LADO ESQUERDO DO HEADER --- */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          {/* Mostra a seta de voltar APENAS se for uma página interna */}
          {variant === 'page' && (
            <button
              className='hamburger-menu' // Reutiliza o estilo do botão para o ícone
              onClick={() => navigate(-1)}
              aria-label='Voltar para a página anterior'
            >
              <span className='material-symbols-outlined'>arrow_back</span>
            </button>
          )}

          {/* A logo e o título agora ficam sempre no grupo da esquerda */}
          <div
            className='header-brand'
            onClick={handleHomeClick}
            role='button'
            tabIndex={0}
          >
            <img src={ifalaLogo} alt='Logo IFala' className='header-logo' />
            <div className='header-title'>
              <p className='app-subtitle'>Campus Corrente</p>
            </div>
          </div>
        </div>

        {/* --- LADO DIREITO DO HEADER --- */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          {/* Sempre mostra o menu hamburger para navegação */}
          <button
            className='hamburger-menu'
            onClick={() => setSidebarOpen(true)}
            aria-label='Abrir menu de navegação'
          >
            <span className='material-symbols-outlined'>menu</span>
          </button>
        </div>
      </div>
    </header>
  );
}

export default Header;
