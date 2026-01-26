import { useState, useEffect, useMemo } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useDenuncias } from './hooks/useDenuncias';
import type { SearchParams } from './types/denunciaTypes';
import { Filters } from './components/Filters/Filters';
import { DenunciaCard } from './components/DenunciaCard/DenunciaCard';
import { Pagination } from './components/Pagination/Pagination';
import { marcarComoLidaPorDenuncia } from '../../services/notificacao-api';
import {
  fixarDenuncia,
  desfixarDenuncia,
  acompanharDenuncia,
  desacompanharDenuncia,
} from '../../services/admin-denuncias-api';
import './DenunciaList.css';

export function DenunciasList() {
  const navigate = useNavigate();
  const [urlSearchParams, setUrlSearchParams] = useSearchParams();

  // Ler parâmetros da URL ou usar valores padrão
  // URL usa base-1 (página 1, 2, 3...) mas internamente usamos base-0 (0, 1, 2...)
  const [currentPage, setCurrentPage] = useState(() => {
    const pageFromUrl = parseInt(urlSearchParams.get('page') || '1', 10);
    return Math.max(0, pageFromUrl - 1); // Converte de base-1 para base-0
  });
  const [searchParams, setSearchParams] = useState<SearchParams>({
    search: urlSearchParams.get('search') || '',
    categoria: urlSearchParams.get('categoria') || '',
    status: urlSearchParams.get('status') || '',
    sortProperty: urlSearchParams.get('sortProperty') || '',
    sortDirection:
      (urlSearchParams.get('sortDirection') as 'ASC' | 'DESC') || '',
    adminEmail: urlSearchParams.get('adminEmail') || '',
  });

  const normalizedParams: SearchParams = useMemo(
    () => ({
      ...searchParams,

      sortProperty: searchParams.sortProperty || 'id',
      sortDirection:
        searchParams.sortDirection === 'ASC' ||
        searchParams.sortDirection === 'DESC'
          ? searchParams.sortDirection
          : 'DESC',
    }),
    [searchParams],
  );

  const { denuncias, loading, error, totalPages, refetch } = useDenuncias(
    currentPage,
    normalizedParams,
  );
  const [showWelcome, setShowWelcome] = useState(true);

  const handleFilterChange = (field: keyof SearchParams, value: string) => {
    setSearchParams((prev) => ({ ...prev, [field]: value }));
    setCurrentPage(0);
  };

  const handleClearFilters = () => {
    setSearchParams({
      search: '',
      categoria: '',
      status: '',
      sortProperty: '',
      sortDirection: '',
      adminEmail: '',
    });
    setCurrentPage(0);
  };

  // Handler para acompanhar denúncia
  const handleAcompanhar = async (denunciaId: number) => {
    try {
      await acompanharDenuncia(denunciaId);
      refetch();
    } catch (error) {
      console.error('Erro ao acompanhar denúncia:', error);
    }
  };

  // Handler para desacompanhar denúncia
  const handleDesacompanhar = async (denunciaId: number) => {
    try {
      await desacompanharDenuncia(denunciaId);
      refetch();
    } catch (error) {
      console.error('Erro ao sair da denúncia:', error);
    }
  };

  // Sincronizar estado com URL sempre que mudar
  useEffect(() => {
    const params: Record<string, string> = {
      page: (currentPage + 1).toString(), // Converte de base-0 para base-1 na URL
    };

    // Adicionar apenas filtros não vazios
    if (searchParams.search) params.search = searchParams.search;
    if (searchParams.categoria) params.categoria = searchParams.categoria;
    if (searchParams.status) params.status = searchParams.status;
    if (searchParams.sortProperty)
      params.sortProperty = searchParams.sortProperty;
    if (searchParams.sortDirection)
      params.sortDirection = searchParams.sortDirection;
    if (searchParams.adminEmail) params.adminEmail = searchParams.adminEmail;

    setUrlSearchParams(params, { replace: true });
  }, [currentPage, searchParams, setUrlSearchParams]);

  // navega para acompanhamento do admin pelo id
  const handleViewDetails = async (denunciaId: number) => {
    try {
      // Marca todas as notificações relacionadas a esta denúncia como lidas
      await marcarComoLidaPorDenuncia(denunciaId);
    } catch (error) {
      console.error('Erro ao marcar notificações como lidas:', error);
      // Continua a navegação mesmo se houver erro
    }
    navigate(`/admin/denuncias/${denunciaId}/acompanhamento`);
  };

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleToggleFixar = async (denunciaId: number, fixar: boolean) => {
    try {
      if (fixar) {
        await fixarDenuncia(denunciaId);
      } else {
        await desfixarDenuncia(denunciaId);
      }
      // Recarregar a lista para atualizar o estado
      refetch();
    } catch (error) {
      console.error('Erro ao fixar/desfixar denúncia:', error);
      // Você pode adicionar uma notificação de erro aqui se tiver um sistema de toast/snackbar
    }
  };

  useEffect(() => {
    const timer = setTimeout(() => setShowWelcome(false), 5000);
    return () => clearTimeout(timer);
  }, []);

  return (
    <div className='denuncias-page'>
      <section className='hero-section'>
        <div className='hero-background'>
          <div className='hero-gradient'></div>
          <div className='hero-pattern'></div>
        </div>
        <div className='container'>
          <div className='hero-content'>
            <h1 className='hero-title'>IFala Corrente</h1>
            <p className='hero-subtitle'>
              Gerencie e acompanhe todas as denúncias do sistema de forma segura
              e organizada.
            </p>
          </div>
        </div>
      </section>

      <main className='main-content'>
        <div className='container'>
          {showWelcome && (
            <div className='welcome-message'>
              <div className='welcome-content'>
                <span className='material-symbols-outlined welcome-icon'>
                  waving_hand
                </span>
                <div className='welcome-text'>
                  <h3>Bem-vindo ao Painel de Denúncias!</h3>
                  <p>
                    Use os filtros abaixo para encontrar denúncias específicas.
                  </p>
                </div>
                <button
                  className='welcome-close'
                  onClick={() => setShowWelcome(false)}
                >
                  <span className='material-symbols-outlined'>close</span>
                </button>
              </div>
            </div>
          )}

          <Filters
            searchParams={searchParams}
            loading={loading}
            fieldErrors={{
              search: '',
              categoria: '',
              status: '',
              sortProperty: '',
              sortDirection: '',
              adminEmail: '',
            }}
            onFilterChange={handleFilterChange}
            onClearFilters={handleClearFilters}
            onRefresh={refetch}
          />

          <section className='denuncias-section'>
            {error && (
              <div className='error-state'>
                <div className='error-icon'>
                  <span className='material-symbols-outlined'>error</span>
                </div>
                <div className='error-content'>
                  <h3>Ocorreu um erro</h3>
                  <p>{error}</p>
                  <button onClick={refetch} className='btn-retry'>
                    <span className='material-symbols-outlined'>refresh</span>
                    Tentar Novamente
                  </button>
                </div>
              </div>
            )}

            {loading && (
              <div className='loading-state'>
                <div className='loading-spinner-large'>
                  <div className='spinner-circle'></div>
                  <div className='spinner-circle'></div>
                  <div className='spinner-circle'></div>
                </div>
                <div className='loading-content'>
                  <h3>Carregando denúncias</h3>
                  <p>Estamos buscando as informações mais recentes...</p>
                </div>
              </div>
            )}

            {!loading && !error && (
              <>
                <div className='denuncias-header'>
                  <h2 className='section-title'>Denúncias</h2>
                </div>

                <div className='denuncias-grid'>
                  {denuncias.length > 0 ? (
                    denuncias.map((denuncia, index) => (
                      <DenunciaCard
                        key={denuncia.id}
                        denuncia={denuncia}
                        contador={currentPage * 10 + (index + 1)}
                        onViewDetails={handleViewDetails}
                        onToggleFixar={handleToggleFixar}
                        onAcompanhar={handleAcompanhar}
                        onDesacompanhar={handleDesacompanhar}
                      />
                    ))
                  ) : (
                    <div className='no-results'>
                      <span className='material-symbols-outlined'>
                        search_off
                      </span>
                      <h3>Nenhuma denúncia encontrada</h3>
                      <p>
                        Tente ajustar os filtros ou limpar todas as filtragens.
                      </p>
                      <button
                        onClick={handleClearFilters}
                        className='btn-clear-all'
                      >
                        <span className='material-symbols-outlined'>
                          clear_all
                        </span>
                        Limpar Todos os Filtros
                      </button>
                    </div>
                  )}
                </div>

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
    </div>
  );
}
