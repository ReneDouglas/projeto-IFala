import { useState, useEffect } from 'react';
import type { Denuncia, DenunciasResponse, SearchParams } from '../types/denunciaTypes';

// Simulação de API local (sem dados fixos)
const simulateApiCall = (currentPage: number, searchParams: SearchParams): Promise<DenunciasResponse> => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      try {
        // Simular erro aleatório 
        if (Math.random() < 0.1) {
          throw new Error('Erro de conexão com o servidor');
        }

        // Gerar dados dinâmicos baseados nos filtros
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
    }, 800); // Simular delay de rede
  });
};

const generateDynamicDenuncias = (currentPage: number, searchParams: SearchParams): Denuncia[] => {
  const denuncias: Denuncia[] = [];
  const startId = currentPage * 9 + 1;

  for (let i = 0; i < 9; i++) {
    const id = startId + i;
    const hasUnreadMessages = Math.random() > 0.7;
    const daysAgo = Math.floor(Math.random() * 30);
    const hoursAgo = Math.floor(Math.random() * 24);
    
    // Aplicar filtros na geração
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
  
  // Se há busca, incluir termo no título
  if (search) {
    titulo = `${search} - ${titulo}`;
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
  
  // Se há busca, incluir termo na descrição
  if (search) {
    descricao = `${search.toUpperCase()}: ${descricao}`;
  }
  
  return descricao;
};

const calculateTotalElements = (searchParams: SearchParams): number => {
  // Simular diferentes totais baseados nos filtros
  let baseTotal = 27; // 3 páginas de 9 itens
  
  // Se há filtros ativos, reduzir o total para simular filtragem
  if (searchParams.search || searchParams.categoria || searchParams.status) {
    baseTotal = 9 + Math.floor(Math.random() * 18); // Entre 9 e 27
  }
  
  return baseTotal;
};

export const useDenuncias = (currentPage: number, searchParams: SearchParams) => {
  const [denuncias, setDenuncias] = useState<Denuncia[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const loadDenuncias = async () => {
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
  };

  useEffect(() => {
    loadDenuncias();
  }, [currentPage, searchParams]);

  return {
    denuncias,
    loading,
    error,
    totalPages,
    totalElements,
    refetch: loadDenuncias
  };
};