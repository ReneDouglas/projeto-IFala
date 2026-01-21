// ================================
// IMPORTAÇÕES E DEPENDÊNCIAS
// ================================
import { useState, useEffect } from 'react';
import './Sobre.css';
import ifalaLogo from '../../assets/IFala-logo.png';
import ifpiLogo from '../../assets/Logo-IFPI-Horizontal.png';

// ================================
// TIPOS E INTERFACES
// ================================
interface Contributor {
  login: string;
  avatar_url: string;
  html_url: string;
}

interface Professor {
  login: string;
  avatar_url: string;
  html_url: string;
  name: string;
}

// ================================
// DADOS ESTÁTICOS DOS DESENVOLVEDORES
// ================================
const DESENVOLVEDORES: Contributor[] = [
  {
    login: 'GuilhermeAlves25',
    avatar_url: 'https://github.com/GuilhermeAlves25.png',
    html_url: 'https://github.com/GuilhermeAlves25',
  },
  {
    login: 'phaolapaixao',
    avatar_url: 'https://github.com/phaolapaixao.png',
    html_url: 'https://github.com/phaolapaixao',
  },
  {
    login: 'jhonatasjgr',
    avatar_url: 'https://github.com/jhonatasjgr.png',
    html_url: 'https://github.com/jhonatasjgr',
  },
  {
    login: 'jonielmendes',
    avatar_url: 'https://github.com/jonielmendes.png',
    html_url: 'https://github.com/jonielmendes',
  },
  {
    login: 'LuisTheDevMagician',
    avatar_url: 'https://github.com/LuisTheDevMagician.png',
    html_url: 'https://github.com/LuisTheDevMagician',
  },
  {
    login: 'MrRafha',
    avatar_url: 'https://github.com/MrRafha.png',
    html_url: 'https://github.com/MrRafha',
  },
  {
    login: 'keyllaneguedes1',
    avatar_url: 'https://github.com/keyllaneguedes1.png',
    html_url: 'https://github.com/keyllaneguedes1',
  },
  {
    login: 'JoaoAndreBSantana',
    avatar_url: 'https://github.com/JoaoAndreBSantana.png',
    html_url: 'https://github.com/JoaoAndreBSantana',
  },
];

// ================================
// COMPONENTE DA PÁGINA SOBRE
// ================================
export function Sobre() {
  // ================================
  // ESTADOS DO COMPONENTE
  // ================================
  const [professor, setProfessor] = useState<Professor | null>(null);

  // ================================
  // EFEITOS
  // ================================
  useEffect(() => {
    // Buscar dados do professor orientador
    const fetchProfessor = async () => {
      try {
        const response = await fetch(
          'https://api.github.com/users/ReneDouglas',
        );
        if (response.ok) {
          const data = await response.json();
          setProfessor({
            login: data.login,
            avatar_url: data.avatar_url,
            html_url: data.html_url,
            name: data.name || 'Renê Morais',
          });
        }
      } catch (error) {
        console.error('Erro ao buscar dados do professor:', error);
      }
    };

    fetchProfessor();
  }, []);
  // ================================
  // RENDERIZAÇÃO DO COMPONENTE
  // ================================
  return (
    <>
      <section className='sobre-hero'>
        <div className='container'>
          <div className='sobre-hero-content'>
            <h1 className='sobre-hero-title'>Sobre o IFala</h1>
            <p className='sobre-hero-subtitle'>
              Conheça mais sobre o projeto, a equipe e como contribuir
            </p>
          </div>
        </div>
      </section>

      <main className='sobre-main'>
        <div className='container'>
          {/* SOBRE O PROJETO */}
          <section className='sobre-section'>
            <div className='sobre-section-header'>
              <span
                className='material-symbols-outlined notranslate'
                translate='no'
              >
                info
              </span>
              <h2>Sobre o Projeto</h2>
            </div>
            <div className='sobre-section-content'>
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
              <p>
                O <strong>IFala</strong> é um sistema de denúncias anônimas
                desenvolvido para proporcionar um canal seguro e confidencial
                onde estudantes do Instituto Federal do Piauí - Campus Corrente
                possam relatar ocorrências que acontecem dentro da instituição.
              </p>
              <p>
                O projeto foi desenvolvido como parte da disciplina de{' '}
                <strong>Programação para Internet II</strong> do curso de
                Análise e Desenvolvimento de Sistemas do IFPI, com o objetivo de
                criar uma solução real que atenda às necessidades da comunidade
                acadêmica.
              </p>
              <p>
                Nosso sistema garante <strong>anonimato total</strong>, não
                coletando ou armazenando dados pessoais dos usuários. Toda
                comunicação é protegida por criptografia avançada, e o
                acompanhamento das denúncias é feito através de tokens únicos e
                seguros.
              </p>
              <div className='repo-link'>
                <a
                  href='https://github.com/ReneDouglas/projeto-IFala'
                  target='_blank'
                  rel='noopener noreferrer'
                  className='btn-github'
                >
                  <span
                    className='material-symbols-outlined notranslate'
                    translate='no'
                  >
                    code
                  </span>
                  Repositório no GitHub
                </a>
              </div>
            </div>
          </section>

          {/* COMO CONTRIBUIR */}
          <section className='sobre-section'>
            <div className='sobre-section-header'>
              <span
                className='material-symbols-outlined notranslate'
                translate='no'
              >
                volunteer_activism
              </span>
              <h2>Como Contribuir</h2>
            </div>
            <div className='sobre-section-content'>
              <p>
                O IFala é um projeto de código aberto e estamos abertos a
                contribuições da comunidade. Se você deseja contribuir com o
                projeto, aqui estão algumas formas de fazer isso:
              </p>
              <div className='contribution-cards'>
                <div className='contribution-card'>
                  <span
                    className='material-symbols-outlined notranslate'
                    translate='no'
                  >
                    bug_report
                  </span>
                  <h3>Reportar Bugs</h3>
                  <p>
                    Encontrou um problema? Abra uma{' '}
                    <a
                      href='https://github.com/ReneDouglas/projeto-IFala/issues'
                      target='_blank'
                      rel='noopener noreferrer'
                    >
                      issue no GitHub
                    </a>{' '}
                    descrevendo o bug encontrado.
                  </p>
                </div>
                <div className='contribution-card'>
                  <span
                    className='material-symbols-outlined notranslate'
                    translate='no'
                  >
                    lightbulb
                  </span>
                  <h3>Sugerir Melhorias</h3>
                  <p>
                    Tem uma ideia para melhorar o sistema? Compartilhe suas
                    sugestões através das issues do projeto.
                  </p>
                </div>
                <div className='contribution-card'>
                  <span
                    className='material-symbols-outlined notranslate'
                    translate='no'
                  >
                    code
                  </span>
                  <h3>Contribuir com Código</h3>
                  <p>
                    Faça um fork do{' '}
                    <a
                      href='https://github.com/ReneDouglas/projeto-IFala'
                      target='_blank'
                      rel='noopener noreferrer'
                    >
                      repositório
                    </a>
                    , implemente suas melhorias e envie um pull request.
                  </p>
                </div>
              </div>
            </div>
          </section>

          {/* CRÉDITOS E EQUIPE */}
          <section className='sobre-section'>
            <div className='sobre-section-header'>
              <span
                className='material-symbols-outlined notranslate'
                translate='no'
              >
                groups
              </span>
              <h2>Créditos e Equipe</h2>
            </div>
            <div className='sobre-section-content'>
              <div className='team-section'>
                <h3>Professor Orientador</h3>
                {professor ? (
                  <div className='professor-card'>
                    <img
                      src={professor.avatar_url}
                      alt={`Foto de ${professor.name}`}
                      className='professor-avatar'
                    />
                    <div className='professor-info'>
                      <h4>Prof. {professor.name}</h4>
                      <a
                        href={professor.html_url}
                        target='_blank'
                        rel='noopener noreferrer'
                        className='professor-github-link'
                      >
                        <span
                          className='material-symbols-outlined notranslate'
                          translate='no'
                        >
                          code
                        </span>
                        GitHub: @{professor.login}
                      </a>
                    </div>
                  </div>
                ) : (
                  <div className='team-member'>
                    <span
                      className='material-symbols-outlined notranslate'
                      translate='no'
                    >
                      school
                    </span>
                    <p>Prof. Renê Morais</p>
                  </div>
                )}
              </div>

              <div className='team-section'>
                <h3>Desenvolvedores</h3>
                <div className='developers-grid'>
                  {DESENVOLVEDORES.map((developer) => (
                    <div key={developer.login} className='developer-card'>
                      <img
                        src={developer.avatar_url}
                        alt={`Foto de ${developer.login}`}
                        className='developer-avatar'
                      />
                      <h4>{developer.login}</h4>
                      <p className='developer-role'>Estudante</p>
                      <div className='developer-links'>
                        <a
                          href={developer.html_url}
                          target='_blank'
                          rel='noopener noreferrer'
                          title='GitHub'
                        >
                          <span
                            className='material-symbols-outlined notranslate'
                            translate='no'
                          >
                            code
                          </span>
                        </a>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </section>

          {/* AGRADECIMENTOS ESPECIAIS */}
          <section className='sobre-section agradecimentos-section'>
            <div className='sobre-section-header'>
              <span
                className='material-symbols-outlined notranslate'
                translate='no'
              >
                favorite
              </span>
              <h2>Agradecimentos Especiais</h2>
            </div>
            <div className='sobre-section-content'>
              <div className='agradecimentos-card'>
                <p>
                  Agradecemos especialmente aos servidores do setor de TI do
                  Campus Corrente:
                </p>
                <div className='agradecimentos-names'>
                  <div className='agradecimento-item'>
                    <span
                      className='material-symbols-outlined notranslate'
                      translate='no'
                    >
                      computer
                    </span>
                    <strong>Danilo</strong>
                  </div>
                  <div className='agradecimento-item'>
                    <span
                      className='material-symbols-outlined notranslate'
                      translate='no'
                    >
                      computer
                    </span>
                    <strong>Wanderson</strong>
                  </div>
                </div>
                <p>
                  Pela colaboração, suporte técnico e infraestrutura
                  disponibilizada para o desenvolvimento e hospedagem deste
                  projeto.
                </p>
              </div>
            </div>
          </section>
        </div>
      </main>

      <footer className='footer'>
        <div className='container'>
          <p>
            Sistema desenvolvido por alunos do Curso de Análise e
            Desenvolvimento de Sistemas
          </p>
          <p>
            <strong>Instituto Federal do Piauí - Campus Corrente</strong>
          </p>
        </div>
      </footer>
    </>
  );
}
