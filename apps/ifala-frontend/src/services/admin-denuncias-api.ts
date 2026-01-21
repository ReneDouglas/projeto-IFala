import axiosClient from './axios-client';
import type {
  DenunciasResponse,
  SearchParams,
} from '../pages/DenunciaList/types/denunciaTypes';

// Regex para validar formato UUID
const UUID_REGEX = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

/**
 * Verifica se o termo de busca é válido para enviar à API.
 * Válido se: for um UUID válido OU tiver pelo menos 3 caracteres.
 */
function isValidSearchTerm(search: string): boolean {
  if (!search || search.trim() === '') return false;
  const trimmed = search.trim();
  // Se for UUID válido, aceita independente do tamanho
  if (UUID_REGEX.test(trimmed)) return true;
  // Caso contrário, precisa ter pelo menos 3 caracteres
  return trimmed.length >= 3;
}

export async function listarDenunciasAdmin(
  page: number,
  searchParams: SearchParams,
): Promise<DenunciasResponse> {
  const params: Record<string, string | number> = {
    pageNumber: page,
    size: 10,
    categoria: searchParams.categoria || '',
    status: searchParams.status || '',
    sortProperty: searchParams.sortProperty || 'id',
    sortDirection: searchParams.sortDirection || 'DESC',
  };

  // Só envia o parâmetro search se for válido (UUID ou >= 3 caracteres)
  if (isValidSearchTerm(searchParams.search)) {
    params.search = searchParams.search;
  }

  const response = await axiosClient.get('/admin/denuncias', { params });

  return response.data;
}
