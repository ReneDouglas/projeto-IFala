import { useState, useEffect } from 'react';
import { useDenuncias } from './hooks/useDenuncias';
import type { SearchParams } from './types/denunciaTypes';
import { Filters } from './components/Filters/Filters';
import { DenunciaCard } from './components/DenunciaCard/DenunciaCard';
import { Pagination } from './components/Pagination/Pagination';
import './DenunciaList.css';

export function DenunciasList() {
  const [currentPage, setCurrentPage] = useState(0);
  const [searchParams, setSearchParams] = useState<SearchParams>({
    search: '',
    categoria: '',
    status: '',
    ordenacao: 'dataCriacao,desc'
  });

  const { 
    denuncias, 
    loading, 
    error, 
    totalPages, 
     
    refetch 
  } = useDenuncias(currentPage, searchParams);

  const [showWelcome, setShowWelcome] = useState(true);

  

  const handleFilterChange = (field: keyof SearchParams, value: string) => {
    setSearchParams(prev => ({ ...prev, [field]: value }));
    setCurrentPage(0);
  };

  const handleClearFilters = () => {
    setSearchParams({ 
      search: '', 
      categoria: '', 
      status: '', 
      ordenacao: 'dataCriacao,desc' 
    });
    setCurrentPage(0);
  };

  const handleViewDetails = (token: string) => {
    // Simulação de navegação para detalhes
    console.log(`Navegando para detalhes da denúncia: ${token}`);
    alert(`🔍 Visualizando detalhes da denúncia: ${token}\n\nEsta funcionalidade irá navegar para a página de detalhes.`);
  };
  
  const handleViewMessages = (token: string) => {
    // Simulação de navegação para mensagens
    console.log(`Navegando para mensagens da denúncia: ${token}`);
    alert(`💬 Abrindo mensagens da denúncia: ${token}\n\nEsta funcionalidade irá abrir o painel de mensagens.`);
  };

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
    // Scroll suave para o topo
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  // Esconder welcome message após 5 segundos
  useEffect(() => {
    const timer = setTimeout(() => {
      setShowWelcome(false);
    }, 5000);

    return () => clearTimeout(timer);
  }, []);

  return (
    <div className="denuncias-page">
      {/* Hero Section Premium */}
      <section className="hero-section">
        <div className="hero-background">
          <div className="hero-gradient"></div>
          <div className="hero-pattern"></div>
        </div>
        <div className="container">
          <div className="hero-content">
            <h1 className="hero-title">
              IFala Corrente
            </h1>
            <p className="hero-subtitle">
              Gerencie e acompanhe todas as denúncias do sistema de forma segura e organizada.
            </p>
          </div>
        </div>
      </section>

      <main className="main-content">
        <div className="container">
          {/* Welcome Message */}
          {showWelcome && (
            <div className="welcome-message">
              <div className="welcome-content">
                <span className="material-symbols-outlined welcome-icon">waving_hand</span>
                <div className="welcome-text">
                  <h3>Bem-vindo ao Painel de Denúncias!</h3>
                  <p>Use os filtros abaixo para encontrar denúncias específicas.</p>
                </div>
                <button 
                  className="welcome-close"
                  onClick={() => setShowWelcome(false)}
                >
                  <span className="material-symbols-outlined">close</span>
                </button>
              </div>
            </div>
          )}

          {/* Filtros Avançados */}
          <Filters
            searchParams={searchParams}
            loading={loading}
            fieldErrors={{
              search: '',
              categoria: '',
              status: '',
              ordenacao: ''
            }}
            onFilterChange={handleFilterChange}
            onClearFilters={handleClearFilters}
            onRefresh={refetch}
          />

          {/* Lista de Denúncias */}
          <section className="denuncias-section">
            {/* Estado de Erro */}
            {error && (
              <div className="error-state">
                <div className="error-icon">
                  <span className="material-symbols-outlined">error</span>
                </div>
                <div className="error-content">
                  <h3>Ocorreu um erro</h3>
                  <p>{error}</p>
                  <button onClick={refetch} className="btn-retry">
                    <span className="material-symbols-outlined">refresh</span>
                    Tentar Novamente
                  </button>
                </div>
              </div>
            )}

            {/* Estado de Carregamento */}
            {loading && (
              <div className="loading-state">
                <div className="loading-spinner-large">
                  <div className="spinner-circle"></div>
                  <div className="spinner-circle"></div>
                  <div className="spinner-circle"></div>
                </div>
                <div className="loading-content">
                  <h3>Carregando denúncias</h3>
                  <p>Estamos buscando as informações mais recentes...</p>
                  <div className="loading-progress">
                    <div className="progress-bar">
                      <div className="progress-fill"></div>
                    </div>
                  </div>
                </div>
              </div>
            )}

            {/* Conteúdo Principal */}
            {!loading && !error && (
              <>
                {/* CORREÇÃO: Header com informações de paginação */}
                <div className="denuncias-header">
                  <div className="header-content">
                    <h2 className="section-title">
                      Denúncias
                    </h2>
                    
          
                    
                    {/* Filtros Ativos */}
                    {(searchParams.search || searchParams.categoria || searchParams.status) && (
                      <div className="active-filters-preview">
                        <span className="preview-label">Filtros aplicados:</span>
                        <div className="preview-chips">
                          {searchParams.search && (
                            <span className="preview-chip">
                              <span className="material-symbols-outlined">search</span>
                              "{searchParams.search}"
                            </span>
                          )}
                          {searchParams.categoria && (
                            <span className="preview-chip">
                              <span className="material-symbols-outlined">category</span>
                              {searchParams.categoria}
                            </span>
                          )}
                          {searchParams.status && (
                            <span className="preview-chip">
                              <span className="material-symbols-outlined">status</span>
                              {searchParams.status}
                            </span>
                          )}
                        </div>
                      </div>
                    )}
                  </div>

                  {/* Ordenação Rápida */}
                  <div className="quick-sort">
                    <label>Ordenar por:</label>
                    <select 
                      value={searchParams.ordenacao}
                      onChange={(e) => handleFilterChange('ordenacao', e.target.value)}
                      className="sort-select"
                    >
                      <option value="dataCriacao,desc">Mais Recentes</option>
                      <option value="dataCriacao,asc">Mais Antigas</option>
                      <option value="titulo,asc">Título A-Z</option>
                      <option value="titulo,desc">Título Z-A</option>
                    </select>
                  </div>
                </div>

                {/* CORREÇÃO: Grid de Denúncias com paginação de 9 itens */}
                <div className="denuncias-grid">
                  {denuncias.length > 0 ? (
                    denuncias.map((denuncia) => (
                      <DenunciaCard
                        key={denuncia.id}
                        denuncia={denuncia}
                        onViewDetails={handleViewDetails}
                        onViewMessages={handleViewMessages}
                      />
                    ))
                  ) : (
                    <div className="no-results">
                      <div className="no-results-icon">
                        <span className="material-symbols-outlined">search_off</span>
                      </div>
                      <div className="no-results-content">
                        <h3>Nenhuma denúncia encontrada</h3>
                        <p>
                          Não encontramos denúncias que correspondam aos seus critérios de busca. 
                          Tente ajustar os filtros ou limpar todas as filtragens para ver todos os resultados.
                        </p>
                        <div className="no-results-actions">
                          <button onClick={handleClearFilters} className="btn-clear-all">
                            <span className="material-symbols-outlined">clear_all</span>
                            Limpar Todos os Filtros
                          </button>
                          <button onClick={refetch} className="btn-refresh">
                            <span className="material-symbols-outlined">refresh</span>
                            Recarregar Dados
                          </button>
                        </div>
                      </div>
                    </div>
                  )}
                </div>

                {/* CORREÇÃO: Paginação só aparece se tiver mais de 9 itens */}
                {totalPages > 1 && (
                  <Pagination
                    currentPage={currentPage}
                    totalPages={totalPages}
                    onPageChange={handlePageChange}
                  />
                )}
              </>
            )}
          </section>

          
        </div>
      </main>

      {/* Loading Overlay para operações */}
      {loading && <div className="global-loading-overlay"></div>}
    </div>
  );
}