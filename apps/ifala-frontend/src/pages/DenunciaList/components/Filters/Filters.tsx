import type { SearchParams, FieldErrors } from '../../types/denunciaTypes';
import { Input } from '../ui/Input/Input';
import { Select } from '../ui/Select/Select';
import './Filters.css';

interface FiltersProps {
  searchParams: SearchParams;
  loading: boolean;
  fieldErrors: FieldErrors;
  onFilterChange: (field: keyof SearchParams, value: string) => void;
  onClearFilters: () => void;
  onRefresh: () => void;
}

export const Filters = ({
  searchParams,
  loading,
  fieldErrors,
  onFilterChange,
  onClearFilters,
  onRefresh,
}: FiltersProps) => {
  const categoriaOptions = [
    { value: '', label: 'Todas as categorias' },
    { value: 'VANDALISMO', label: 'Vandalismo' },
    { value: 'VIOLENCIA', label: 'Violência' },
    { value: 'BULLYING', label: 'Bullying' },
    { value: 'DROGAS', label: 'Drogas' },
    { value: 'ACADEMICO', label: 'Academico' },
    { value: 'OUTROS', label: 'Outros' },
  ];

  const statusOptions = [
    { value: '', label: 'Todos os status' },
    { value: 'RECEBIDO', label: 'Recebido' },
    { value: 'EM_ANALISE', label: 'Em Análise' },
    { value: 'AGUARDANDO', label: 'Aguardando Informações' },
    { value: 'RESOLVIDO', label: 'Resolvido' },
    { value: 'REJEITADO', label: 'Rejeitado' },
  ];

  // Mapeamento visual para algo como "campo,direção"
  const ordenacaoOptions = [
    { value: 'criadoEm,desc', label: 'Mais recentes' },
    { value: 'criadoEm,asc', label: 'Mais antigas' },
    { value: 'categoria,asc', label: 'Categoria (A-Z)' },
    { value: 'categoria,desc', label: 'Categoria (Z-A)' },
  ];

  return (
    <section className='filters-section'>
      <div className='filters-card bg-white shadow-md'>
        <div className='filters-header'>
          <h3 className='filters-title text-secondary'>
            <span className='material-symbols-outlined filter-icon'>
              filter_list
            </span>
            Filtros e Busca Avançada
          </h3>
          <div className='filters-indicator'>
            {loading && (
              <div className='loading-indicator'>
                <div className='loading-spinner'></div>
                <span>Buscando...</span>
              </div>
            )}
          </div>
        </div>

        <div className='filters-form'>
          {/* BUSCA */}
          <Input
            label='Buscar por Token'
            value={searchParams.search}
            onChange={(value) => onFilterChange('search', value)}
            placeholder='Insira o token'
            error={fieldErrors.search}
            icon='search'
          />

          <div className='filters-row'>
            {/* CATEGORIA */}
            <Select
              label='Categoria'
              value={searchParams.categoria}
              onChange={(value) => onFilterChange('categoria', value)}
              options={categoriaOptions}
              error={fieldErrors.categoria}
              icon='category'
            />

            {/* STATUS */}
            <Select
              label='Status'
              value={searchParams.status}
              onChange={(value) => onFilterChange('status', value)}
              options={statusOptions}
              error={fieldErrors.status}
              icon='pending'
            />

            {/* ORDENAR POR — CORRIGIDO */}
            <Select
              label='Ordenar por'
              // Pegamos sortProperty + sortDirection e montamos o value visual
              value={`${searchParams.sortProperty},${searchParams.sortDirection}`}
              onChange={(value) => {
                const [property, direction] = value.split(',');
                onFilterChange('sortProperty', property);
                onFilterChange('sortDirection', direction.toUpperCase());
              }}
              options={ordenacaoOptions}
              // remove fieldErrors.ordenacao (não existe mais no tipo)
              icon='sort'
            />
          </div>

          {/* BOTÕES */}
          <div className='filters-actions'>
            <button
              onClick={onClearFilters}
              className='btn-clear-filters'
              disabled={loading}
            >
              <span className='material-symbols-outlined'>clear_all</span>
              {loading ? 'Limpando...' : 'Limpar Filtros'}
            </button>

            <button
              onClick={onRefresh}
              className='btn-apply-filters'
              disabled={loading}
            >
              {loading ? (
                <>
                  <div className='button-spinner'></div>
                  Carregando...
                </>
              ) : (
                <>
                  <span className='material-symbols-outlined'>refresh</span>
                  Atualizar Dados
                </>
              )}
            </button>
          </div>

          {/* RESUMO DOS ATIVOS */}
          {(searchParams.search ||
            searchParams.categoria ||
            searchParams.status) && (
            <div className='active-filters-summary'>
              <div className='summary-header'>
                <span className='material-symbols-outlined'>tune</span>
                Filtros Ativos:
              </div>

              <div className='filter-chips'>
                {searchParams.search && (
                  <span className='filter-chip search-chip'>
                    Busca: "{searchParams.search}"
                    <button
                      onClick={() => onFilterChange('search', '')}
                      className='chip-close'
                    >
                      <span className='material-symbols-outlined'>close</span>
                    </button>
                  </span>
                )}

                {searchParams.categoria && (
                  <span className='filter-chip category-chip'>
                    Categoria:{' '}
                    {
                      categoriaOptions.find(
                        (opt) => opt.value === searchParams.categoria,
                      )?.label
                    }
                    <button
                      onClick={() => onFilterChange('categoria', '')}
                      className='chip-close'
                    >
                      <span className='material-symbols-outlined'>close</span>
                    </button>
                  </span>
                )}

                {searchParams.status && (
                  <span className='filter-chip status-chip'>
                    Status:{' '}
                    {
                      statusOptions.find(
                        (opt) => opt.value === searchParams.status,
                      )?.label
                    }
                    <button
                      onClick={() => onFilterChange('status', '')}
                      className='chip-close'
                    >
                      <span className='material-symbols-outlined'>close</span>
                    </button>
                  </span>
                )}
              </div>
            </div>
          )}
        </div>
      </div>
    </section>
  );
};
