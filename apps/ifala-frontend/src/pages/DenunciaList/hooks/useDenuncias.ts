import { useState, useEffect, useCallback } from 'react';
import type { Denuncia, DenunciasResponse, SearchParams } from '../types/denunciaTypes';

/**
 * Simula uma chamada de API para obter denúncias.
 * @param currentPage Número da página atual (0-indexed)
 * @param searchParams Parâmetros de busca e filtros
 * @returns Promise resolvendo um objeto DenunciasResponse
 */
const simulateApiCall = (currentPage: number, searchParams: SearchParams): Promise<DenunciasResponse> => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      try {
        // Simula falha aleatória de conexão (10% de chance)
        if (Math.random() < 0.1) {
          throw new Error('Erro de conexão com o servidor');
        }

        // Gera dados dinâmicos com base nos filtros
        const denuncias = generateDynamicDenuncias(currentPage, searchParams);
        const totalElements = calculateTotalElements(searchParams);
        const totalPages = Math.ceil(totalElements / 9);

        resolve({
          content: denuncias,
          totalPages,
          totalElements,
          size: 9,
          first: currentPage === 0,
          last: currentPage === totalPages - 1
        });
      } catch (error) {
        reject(error);
      }
    }, 800); // Simula atraso de rede de 800ms
  });
};

/**
 * Gera uma lista de denúncias simuladas dinamicamente
 * @param currentPage Página atual
 * @param searchParams Filtros aplicados
 * @returns Array de denúncias (Denuncia[])
 */
const generateDynamicDenuncias = (currentPage: number, searchParams: SearchParams): Denuncia[] => {
  const denuncias: Denuncia[] = [];
  const startId = currentPage * 9 + 1;

  for (let i = 0; i < 9; i++) {
    const id = startId + i;
    const hasUnreadMessages = Math.random() > 0.7; // 30% chance de mensagens não lidas
    const daysAgo = Math.floor(Math.random() * 30); // Dias desde a criação
    const hoursAgo = Math.floor(Math.random() * 24); // Horas desde a última atualização

    // Aplicar filtros
    const categoria = getFilteredCategoria(searchParams.categoria);
    const status = getFilteredStatus(searchParams.status);
    const titulo = getFilteredTitulo(searchParams.search, id);
    const descricao = getFilteredDescricao(searchParams.search);

    denuncias.push({
      id,
      token: `IFALA-${String(id).padStart(6, '0')}`,
      titulo,
      descricao,
      categoria,
      status,
      dataCriacao: new Date(Date.now() - daysAgo * 24 * 60 * 60 * 1000).toISOString(),
      hasUnreadMessages,
      ultimaAtualizacao: new Date(Date.now() - hoursAgo * 60 * 60 * 1000).toISOString()
    });
  }

  return denuncias;
};

/**
 * Funções auxiliares para aplicar filtros ou gerar valores aleatórios
 */
const getFilteredCategoria = (filtroCategoria: string): string => {
  const categorias = ['ASSEDIO', 'VIOLENCIA', 'DISCRIMINACAO', 'OUTROS'];
  if (filtroCategoria && categorias.includes(filtroCategoria)) {
    return filtroCategoria;
  }
  return categorias[Math.floor(Math.random() * categorias.length)];
};

const getFilteredStatus = (filtroStatus: string): string => {
  const statusList = ['RECEPTADO', 'EM_ANALISE', 'AGUARDANDO_INFORMACOES', 'RESOLVIDO', 'REJEITADO'];
  if (filtroStatus && statusList.includes(filtroStatus)) {
    return filtroStatus;
  }
  return statusList[Math.floor(Math.random() * statusList.length)];
};

const getFilteredTitulo = (search: string, id: number): string => {
  const titulos = [
    'Bullying no Pátio',
    'Assédio em Sala de Aula',
    'Discriminação Racial',
    'Vandalismo no Laboratório',
    'Violência Verbal',
    'Ameaças entre Alunos',
    'Assédio Moral - Professor',
    'Danos ao Patrimônio',
    'Conflito entre Turmas',
    'Uso de Substâncias Ilícitas'
  ];

  let titulo = titulos[Math.floor(Math.random() * titulos.length)];

  if (search) {
    titulo = `${search} - ${titulo}`; // Inserir termo de busca no título
  }

  return `Denúncia ${id}: ${titulo}`;
};

const getFilteredDescricao = (search: string): string => {
  const descricoes = [
    'Relato de situação ocorrida durante o intervalo das aulas envolvendo estudantes.',
    'Denúncia grave que requer atenção imediata da coordenação.',
    'Situação recorrente que vem acontecendo há algumas semanas.',
    'Caso isolado, mas que merece investigação para prevenir futuras ocorrências.',
    'Relato detalhado com testemunhas e evidências que comprovam os fatos.',
    'Situação complexa envolvendo múltiplas partes e requiring análise cuidadosa.',
    'Denúncia anônima com informações sobre violação do código de conduta.'
  ];

  let descricao = descricoes[Math.floor(Math.random() * descricoes.length)];

  if (search) {
    descricao = `${search.toUpperCase()}: ${descricao}`;
  }

  return descricao;
};

/**
 * Calcula o total de elementos simulados com base nos filtros
 */
const calculateTotalElements = (searchParams: SearchParams): number => {
  let baseTotal = 27; // 3 páginas de 9 itens

  if (searchParams.search || searchParams.categoria || searchParams.status) {
    baseTotal = 9 + Math.floor(Math.random() * 18); // Entre 9 e 27 itens se houver filtros
  }

  return baseTotal;
};

/**
 * Hook personalizado para carregar, gerenciar e filtrar denúncias.
 * Inclui estado de carregamento, erro, paginação e função de refetch.
 */
export const useDenuncias = (currentPage: number, searchParams: SearchParams) => {
  const [denuncias, setDenuncias] = useState<Denuncia[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  /**
   * Função para carregar denúncias do “servidor”.
   * Utiliza useCallback para prevenir recriação desnecessária e evitar warnings do ESLint.
   */
  const loadDenuncias = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      console.log('Carregando denúncias:', { currentPage, searchParams });

      const response = await simulateApiCall(currentPage, searchParams);

      setDenuncias(response.content);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);

      console.log('Denúncias carregadas:', response.content.length, 'itens');
    } catch (error) {
      console.error('Erro ao carregar denúncias:', error);
      setError(error instanceof Error ? error.message : 'Erro ao carregar denúncias. Tente novamente.');
      setDenuncias([]);
      setTotalPages(0);
      setTotalElements(0);
    } finally {
      setLoading(false);
    }
  }, [currentPage, searchParams]);

  /**
   * useEffect para disparar o carregamento inicial e quando currentPage ou searchParams mudarem.
   * loadDenuncias está como dependência para obedecer à regra do React Hooks.
   */
  useEffect(() => {
    loadDenuncias();
  }, [loadDenuncias]);

  return {
    denuncias,
    loading,
    error,
    totalPages,
    totalElements,
    refetch: loadDenuncias // Permite recarregar manualmente de fora do hook
  };
};
