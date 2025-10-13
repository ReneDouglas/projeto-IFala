export interface Denuncia {
  id: number;
  token: string;
  titulo: string;
  descricao: string;
  categoria: string;
  status: string;
  dataCriacao: string;
  hasUnreadMessages: boolean;
  ultimaAtualizacao: string;
}

export interface SearchParams {
  search: string;
  categoria: string;
  status: string;
  ordenacao: string;
}

export interface DenunciasResponse {
  content: Denuncia[];
  totalPages: number;
  totalElements: number;
  size: number;
  first: boolean;
  last: boolean;
}

export interface FieldErrors {
  search: string;
  categoria: string;
  status: string;
  ordenacao: string;
}

export interface StatusConfig {
  [key: string]: {
    label: string;
    className: string;
  };
}
