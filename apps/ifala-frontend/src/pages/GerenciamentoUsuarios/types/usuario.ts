/**
 * Tipos e interfaces para gerenciamento de usuários
 */

export interface Usuario {
  id: number;
  nome: string;
  username: string;
  email: string;
  senhaTemporaria: boolean;
  perfil?: string;
  criadoEm?: string;
  alteradoEm?: string;
}

export interface UsuarioFormData {
  nome: string;
  username: string;
  email: string;
  senha: string;
  confirmarSenha: string;
  perfil: 'ADMIN' | 'USER';
}

export interface PaginatedUsuarios {
  content: Usuario[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface UsuarioFilters {
  search: string;
  perfil: string;
  senhaTemporaria: string;
}
