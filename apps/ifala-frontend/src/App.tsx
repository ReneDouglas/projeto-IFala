// ================================
// IMPORTAÇÕES E DEPENDÊNCIAS
// ================================
import { useState } from 'react'; // Hook para gerenciar estados
import './App.css'; // Estilos do componente
import ifalaLogo from './assets/IFala-logo.png'; // Logo do IFala
import ifpiLogo from './assets/Logo-IFPI-Horizontal.png'; // Logo do IFPI

// ================================
// COMPONENTE PRINCIPAL DA APLICAÇÃO
// ================================
function App() {
  // ================================
  // ESTADOS DO COMPONENTE
  // ================================

  // Controla se o campo de token está visível
  const [showTokenInput, setShowTokenInput] = useState(false);

  // Armazena o token digitado pelo usuário
  const [token, setToken] = useState('');

  // Controla se o menu lateral está aberto
  const [sidebarOpen, setSidebarOpen] = useState(false);

  // Controla se o usuário está logado (para futura implementação)
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  // ================================
  // FUNÇÕES DE MANIPULAÇÃO DE EVENTOS
  // ================================

  // Manipula clique no botão "Fazer Denúncia"
  const handleDenunciaClick = () => {
    // TODO: Implementar navegação para página de denúncia
    console.log('Redirecionando para página de denúncia');
  };

  // Manipula clique no botão "Acompanhar Denúncia"
  const handleAcompanharClick = () => {
    // Alterna visibilidade do campo de token
    setShowTokenInput(!showTokenInput);
  };

  // Manipula envio do token de acompanhamento
  const handleTokenSubmit = () => {
    if (token.trim()) {
      // TODO: Implementar validação e navegação
      console.log('Token fornecido, verificando status da denúncia...');
      // Fazer requisição para API sem expor o token
    } else {
      alert('Por favor, insira um token válido.');
    }
  };

  // ================================
  // FUNÇÕES DO MENU LATERAL
  // ================================

  // Alterna estado do menu lateral (abre/fecha)
  const toggleSidebar = () => {
    setSidebarOpen(!sidebarOpen);
  };

  // Fecha o menu lateral
  const closeSidebar = () => {
    setSidebarOpen(false);
  };

  // Manipula clique no botão home (logo + título)
  const handleHomeClick = () => {
    // Rola suavemente para o topo da página
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  // ================================
  // FUNÇÕES DE AUTENTICAÇÃO E NAVEGAÇÃO
  // ================================

  // Manipula processo de login
  const handleLogin = () => {
    // TODO: Implementar navegação para página de login
    console.log('Redirecionando para login');
    closeSidebar(); // Fecha menu após ação
  };

  // Manipula processo de logout
  const handleLogout = () => {
    setIsLoggedIn(false); // Atualiza estado de autenticação
    console.log('Logout realizado com sucesso');
    closeSidebar(); // Fecha menu após ação
  };

  // Navega para dashboard do usuário
  const handleDashboard = () => {
    // TODO: Implementar navegação para dashboard
    console.log('Redirecionando para dashboard');
    closeSidebar();
  };

  // Navega para página de alterar senha
  const handleChangePassword = () => {
    // TODO: Implementar navegação para alterar senha
    console.log('Redirecionando para alterar senha');
    closeSidebar();
  };

  // Navega para criar nova denúncia
  const handleNewDenuncia = () => {
    // TODO: Implementar navegação para nova denúncia
    console.log('Redirecionando para nova denúncia');
    closeSidebar();
  };

  // ================================
  // RENDERIZAÇÃO DO COMPONENTE
  // ================================
  return (
    <div className='app'>
      {/* ================================
          CABEÇALHO DA APLICAÇÃO
          Contém logo, título e botão do menu
          ================================ */}
      <header className='header'>
        <div className='container'>
          <div className='header-content'>
            {/* Botão Home (Logo + Título) */}
            <button className='home-button' onClick={handleHomeClick}>
              <img src={ifalaLogo} alt='IFala Logo' className='home-logo' />
              <h1 className='header-title'>Corrente Piaui</h1>
            </button>

            {/* Botão Menu Hamburger */}
            <button
              className={`menu-toggle ${sidebarOpen ? 'active' : ''}`}
              onClick={toggleSidebar}
              aria-label='Menu'
            >
              {/* Linhas do ícone hamburger */}
              <div className='hamburger-line'></div>
              <div className='hamburger-line'></div>
              <div className='hamburger-line'></div>
            </button>
          </div>
        </div>
      </header>

      {/* Sidebar Overlay */}
      <div
        className={`sidebar-overlay ${sidebarOpen ? 'show' : ''}`}
        onClick={closeSidebar}
      ></div>

      {/* Sidebar */}
      <aside className={`sidebar ${sidebarOpen ? 'open' : ''}`}>
        <div className='sidebar-header'>
          <h2 className='sidebar-title'>Menu</h2>
          <button className='close-button' onClick={closeSidebar}>
            ×
          </button>
        </div>

        <div className='sidebar-content'>
          {!isLoggedIn ? (
            <button className='menu-item' onClick={handleLogin}>
              Login
            </button>
          ) : (
            <>
              <button className='menu-item' onClick={handleDashboard}>
                <span className='menu-item-icon'>📊</span>
                Dashboard
              </button>
              <button className='menu-item' onClick={handleChangePassword}>
                <span className='menu-item-icon'>🔑</span>
                Alterar Senha
              </button>
              <button className='menu-item' onClick={handleNewDenuncia}>
                <span className='menu-item-icon'>📝</span>
                Nova Denúncia
              </button>
              <button className='menu-item' onClick={handleLogout}>
                <span className='menu-item-icon'>🚪</span>
                Sair
              </button>
            </>
          )}
        </div>
      </aside>

      {/* ================================
          SEÇÃO HERO (BANNER PRINCIPAL)
          Primeira impressão da aplicação com título e subtítulo
          ================================ */}
      <section className='hero'>
        <div className='container'>
          <div className='hero-content'>
            {/* Título principal da aplicação */}
            <h1 className='hero-title'>Sua voz importa</h1>
            {/* Subtítulo explicativo */}
            <p className='hero-subtitle'>
              Sua identidade está protegida. Relate ocorrências com total
              privacidade e acompanhamento seguro.
            </p>
          </div>
        </div>
      </section>

      {/* ================================
          CONTEÚDO PRINCIPAL
          Área principal com ações e informações
          ================================ */}
      <main className='main-content'>
        <div className='container'>
          {/* ================================
              SEÇÃO DE AÇÕES PRINCIPAIS
              Botões para fazer e acompanhar denúncias
              ================================ */}
          <section className='action-section'>
            <h2 className='action-title'>Como podemos ajudar?</h2>
            <p>Escolha uma das opções abaixo para prosseguir</p>

            <div className='action-buttons'>
              <div className='action-card'>
                <div className='action-card-icon'>
                  <span className='material-symbols-outlined'>shield</span>
                </div>
                <h3>Fazer uma Denúncia</h3>
                <p>
                  Relate ocorrências de forma completamente anônima e segura
                </p>
                <button className='btn-primary' onClick={handleDenunciaClick}>
                  Fazer Denúncia
                </button>
              </div>

              <div className='action-card'>
                <div className='action-card-icon'>
                  <span className='material-symbols-outlined'>search</span>
                </div>
                <h3>Acompanhar Denúncia</h3>
                <p>Use seu token para verificar o status da sua denúncia</p>
                <button
                  className='btn-secondary'
                  onClick={handleAcompanharClick}
                >
                  Acompanhar Denúncia
                </button>
                <div className={`token-input ${showTokenInput ? 'show' : ''}`}>
                  <input
                    type='text'
                    placeholder='Digite seu token de acompanhamento...'
                    value={token}
                    onChange={(e) => setToken(e.target.value)}
                  />
                  <button className='btn-outline' onClick={handleTokenSubmit}>
                    Verificar Token
                  </button>
                </div>
              </div>
            </div>
          </section>

          {/* ================================
              SEÇÃO DE DESCRIÇÃO
              Informações sobre o sistema e exclusividade
              ================================ */}
          <section className='description-section'>
            <div className='container'>
              {/* Logos institucionais */}
              <div className='institutional-logos'>
                <img
                  src={ifalaLogo}
                  alt='Logo IFala'
                  className='institutional-logo'
                />
                <img
                  src={ifpiLogo}
                  alt='Logo IFPI'
                  className='institutional-logo'
                />
              </div>
              <h2 className='description-title'>
                Sistema Oficial do IFPI - Campus Corrente
              </h2>
              <p className='description-text'>
                O IFala é o sistema oficial do Instituto Federal do Piauí -
                Campus Corrente para dar voz aos estudantes de forma anônima e
                segura. Relate ocorrências que acontecem dentro da instituição
                com total privacidade e acompanhamento protegido.
              </p>
              <div className='exclusive-notice'>
                <p>
                  <strong>Exclusivo para estudantes:</strong> Este canal é
                  destinado apenas aos estudantes regularmente matriculados no
                  Instituto Federal do Piauí - Campus Corrente.
                </p>
              </div>
            </div>
          </section>
        </div>
      </main>

      {/* ================================
          SEÇÃO DE GARANTIAS DE SEGURANÇA
          Demonstra as medidas de proteção e anonimato
          ================================ */}
      <section className='security-section'>
        <div className='container'>
          <h2 className='security-title'>Garantias de Segurança</h2>
          <div className='security-features'>
            <div className='security-feature'>
              <div className='security-feature-icon'>
                <span className='material-symbols-outlined'>lock</span>
              </div>
              <h3>Anonimato Total</h3>
              <p>Sua identidade nunca será revelada. Sistema 100% anônimo.</p>
            </div>
            <div className='security-feature'>
              <div className='security-feature-icon'>
                <span className='material-symbols-outlined'>block</span>
              </div>
              <h3>Nenhum Dado Pessoal</h3>
              <p>Não coletamos nem armazenamos informações pessoais.</p>
            </div>
            <div className='security-feature'>
              <div className='security-feature-icon'>
                <span className='material-symbols-outlined'>encrypted</span>
              </div>
              <h3>Comunicação Criptografada</h3>
              <p>
                Todas as informações são protegidas por criptografia avançada.
              </p>
            </div>
            <div className='security-feature'>
              <div className='security-feature-icon'>
                <span className='material-symbols-outlined'>smartphone</span>
              </div>
              <h3>Acompanhamento Seguro</h3>
              <p>
                Use seu token para acompanhar o progresso de forma protegida.
              </p>
            </div>
          </div>

          {/* ================================
              SEÇÃO DE AVISO IMPORTANTE
              Alertas legais sobre uso adequado do sistema
              ================================ */}
          <div className='warning-section'>
            <div className='warning-card'>
              <h3 className='warning-title'>
                <span className='material-symbols-outlined'>warning</span> Aviso
                Importante
              </h3>
              <p className='warning-text'>
                Este canal é destinado a denúncias sérias e legítimas. O uso
                inadequado, incluindo trotes ou falsas denúncias, pode
                constituir crime conforme a legislação brasileira (Art. 340 do
                Código Penal - Comunicação falsa de crime).
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* ================================
          RODAPÉ
          Informações institucionais
          ================================ */}
      <footer className='footer'>
        <div className='container'>
          <p>
            Sistema desenvolvido pelo Curso de Análise e Desenvolvimento de
            Sistemas
          </p>
          <p>
            <strong>Instituto Federal do Piauí - Campus Corrente</strong>
          </p>
        </div>
      </footer>
    </div>
  );
}

export default App;
