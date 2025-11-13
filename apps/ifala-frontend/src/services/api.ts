import axiosClient from './axios-client';
import type {
  CriarDenunciaRequest,
  DenunciaResponse,
  EnumOption,
} from '../types/denuncia';

// ENDPOINTS DO UTILS CONTROLLER

export async function getCategorias(): Promise<EnumOption[]> {
  const response = await axiosClient.get('/utils/categorias');
  return response.data;
}

export async function getGraus(): Promise<EnumOption[]> {
  const response = await axiosClient.get('/utils/graus');
  return response.data;
}

export async function getCursos(): Promise<EnumOption[]> {
  const response = await axiosClient.get('/utils/cursos');
  return response.data;
}

export async function getTurmas(): Promise<EnumOption[]> {
  const response = await axiosClient.get('/utils/turmas');
  return response.data;
}

export async function getStatus(): Promise<EnumOption[]> {
  const response = await axiosClient.get('/utils/status');
  return response.data;
}

// ENDPOINT DE DENÚNCIA PÚBLICA

export async function criarDenuncia(
  dados: CriarDenunciaRequest,
): Promise<DenunciaResponse> {
  const response = await axiosClient.post('/public/denuncias', dados);
  return response.data;
}
