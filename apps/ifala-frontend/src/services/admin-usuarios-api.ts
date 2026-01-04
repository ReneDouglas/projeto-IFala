import axiosClient from './axios-client';
import type {
  AtualizarUsuarioRequest,
  RegistroUsuarioRequest,
  Usuario,
  UsuarioFilters,
  UsuariosResponse,
} from '../types/usuario';

/**
 * Busca uma lista paginada de usuários do sistema.
 * @param page O número da página a ser buscada (começando em 0).
 * @param size O número de itens por página.
 * @returns Uma promessa que resolve para um objeto de resposta paginada de usuários.
 */
export async function listarUsuarios(
  page: number,
  size = 10,
  filters: Partial<UsuarioFilters>,
): Promise<UsuariosResponse> {
  const response = await axiosClient.get('/admin/usuarios', {
    params: {
      page,
      size,
      sort: 'nome,asc',
      search: filters.search || '',
      role: filters.role || '',
      mustChangePassword: filters.mustChangePassword || '',
    },
  });
  return response.data;
}

/**
 * Busca um usuário específico pelo seu ID.
 * @param id O ID do usuário a ser buscado.
 * @returns Uma promessa que resolve para os detalhes do usuário.
 */
export async function buscarUsuarioPorId(id: number): Promise<Usuario> {
  const response = await axiosClient.get(`/admin/usuarios/${id}`);
  return response.data;
}

/**
 * Registra um novo usuário administrador no sistema.
 * @param dadosUsuario Os dados do usuário a ser registrado.
 * @returns Uma promessa que resolve para os detalhes do usuário criado.
 */
export async function registrarUsuario(
  dadosUsuario: RegistroUsuarioRequest,
): Promise<Usuario> {
  const response = await axiosClient.post(
    '/admin/registrar-usuario',
    dadosUsuario,
  );
  return response.data;
}

/**
 * Atualiza os dados de um usuário existente.
 * @param id O ID do usuário a ser atualizado.
 * @param dadosUsuario Os novos dados para o usuário.
 * @returns Uma promessa que resolve para os detalhes do usuário atualizado.
 */
export async function atualizarUsuario(
  id: number,
  dadosUsuario: AtualizarUsuarioRequest,
): Promise<Usuario> {
  const response = await axiosClient.put(`/admin/usuarios/${id}`, dadosUsuario);
  return response.data;
}
