export interface Denuncia {
  id: number;
  tokenAcompanhamento: string;
  categoria: string;
  status: string;
  criadoEm: string;
  alteradoEm?: string;
  descricao?: string;
  titulo?: string;
  hasUnreadMessages?: boolean;
}


export interface SearchParams {
  search: string;
  categoria: string;
  status: string;
  ordenacao: string;
}

/*export interface DenunciasResponse {
  content: Denuncia[];
  totalPages: number;
  totalElements: number;
  size: number;
  first: boolean;
  last: boolean;
}*/

export interface DenunciasResponse {
  content: Denuncia[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;        // página atual
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
