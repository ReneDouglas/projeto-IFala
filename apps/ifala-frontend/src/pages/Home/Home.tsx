// ================================
// IMPORTAÇÕES E DEPENDÊNCIAS
// ================================
import { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // <-- IMPORTADO PARA NAVEGAÇÃO
import '../../App.css'; // Estilos do componente (caminho ajustado)
import ifalaLogo from '../../assets/IFala-logo.png'; // Logo do IFala (caminho ajustado)
import ifpiLogo from '../../assets/Logo-IFPI-Horizontal.png'; // Logo do IFPI (caminho ajustado)

// ================================
// COMPONENTE DA PÁGINA PRINCIPAL
// ================================
export function Home() {
  // ================================
  // ESTADOS DO COMPONENTE
  // ================================
  const [showTokenInput, setShowTokenInput] = useState(false);
  const [token, setToken] = useState('');

  // Hook do React Router para controlar a navegação
  const navigate = useNavigate();

  // ================================
  // FUNÇÕES DE MANIPULAÇÃO DE EVENTOS
  // ================================

  // MUDANÇA AQUI: Agora usa o 'navigate' para ir para a página de denúncia
  const handleDenunciaClick = () => {
    navigate('/denuncia');
  };

  const handleAcompanharClick = () => {
    setShowTokenInput(!showTokenInput);
  };

  const handleTokenSubmit = () => {
    const tokenTrimmed = token.trim();
    if (tokenTrimmed) {
      // Navega para a página de acompanhamento com o token
      navigate(`/acompanhamento/${tokenTrimmed}`);
    } else {
      alert('Por favor, insira um token válido.');
    }
  };

  // ================================
  // RENDERIZAÇÃO DO COMPONENTE
  // ================================
  return (
    <>
      <section className='hero'>
        <div className='container'>
          <div className='hero-content'>
            <h1 className='hero-title'>Sua voz importa</h1>
            <p className='hero-subtitle'>
              Sua identidade está protegida. Relate ocorrências com total
              privacidade e acompanhamento seguro.
            </p>
          </div>
        </div>
      </section>

      <main className='main-content'>
        <div className='container'>
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
                    placeholder='Digite seu token de acompanhamento'
                    value={token}
                    onChange={(e) => setToken(e.target.value)}
                    onKeyDown={(e) => {
                      if (e.key === 'Enter') {
                        handleTokenSubmit();
                      }
                    }}
                  />
                  <button className='btn-outline' onClick={handleTokenSubmit}>
                    Verificar Token
                  </button>
                </div>
              </div>
            </div>
          </section>

          <section className='description-section'>
            <div className='container'>
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
    </>
  );
}
