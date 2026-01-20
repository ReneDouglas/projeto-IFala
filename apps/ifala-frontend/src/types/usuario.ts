export type Perfis = 'ADMIN' | 'ANONIMO';

/**
 * Representa os detalhes de um usuário, como recebido do endpoint de listagem.
 * Corresponde ao UsuarioDetalheResponseDTO.java
 */
export interface Usuario {
  id: number;
  nome: string;
  username: string;
  email: string;
  roles: Perfis[];
  mustChangePassword: boolean;
  receberNotificacoes: boolean;
}

export interface UsuarioFilters {
  search: string;
  role: string;
  mustChangePassword: string;
}

export interface Page<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      sorted: boolean;
      unsorted: boolean;
      empty: boolean;
    };
    offset: number;
    paged: boolean;
    unpaged: boolean;
  };
  last: boolean;
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  sort: {
    sorted: boolean;
    unsorted: boolean;
    empty: boolean;
  };
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}

export type UsuariosResponse = Page<Usuario>;

export interface RegistroUsuarioRequest {
  nome: string;
  email: string;
  username: string;
  senha: string;
  roles: string[];
  mustChangePassword?: boolean;
}

export interface AtualizarUsuarioRequest {
  nome: string;
  email: string;
  username: string;
  roles: string[];
  mustChangePassword: boolean;
  receberNotificacoes?: boolean;
}
