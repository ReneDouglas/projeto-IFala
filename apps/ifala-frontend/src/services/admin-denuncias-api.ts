import axiosClient from './axios-client';
import type {
  DenunciasResponse,
  SearchParams,
} from '../pages/DenunciaList/types/denunciaTypes';

export async function listarDenunciasAdmin(
  page: number,
  searchParams: SearchParams,
): Promise<DenunciasResponse> {
  const params: Record<string, string | number> = {
    pageNumber: page,
    size: 10,
    search: searchParams.search || '',
    categoria: searchParams.categoria || '',
    status: searchParams.status || '',
    sortProperty: searchParams.sortProperty || 'id',
    sortDirection: searchParams.sortDirection || 'DESC',
  };

  const response = await axiosClient.get('/admin/denuncias', { params });

  return response.data;
}
