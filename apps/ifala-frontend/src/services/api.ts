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

export async function criarDenunciaComProvas(
  dados: CriarDenunciaRequest,
  provas: File[],
): Promise<DenunciaResponse> {
  const formData = new FormData();

  // Adicionar dados da denúncia como JSON string
  formData.append('denuncia', JSON.stringify(dados));

  // Adicionar arquivos de prova
  provas.forEach((prova) => {
    formData.append('provas', prova);
  });

  const response = await axiosClient.post(
    '/public/denuncias/com-provas',
    formData,
    {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    },
  );

  return response.data;
}

// ENDPOINT DE PROVAS

export async function listarProvasDenuncia(denunciaId: number): Promise<any[]> {
  const response = await axiosClient.get(
    `/public/provas/denuncia/${denunciaId}`,
  );
  return response.data;
}

export function getProvaUrl(provaId: number): string {
  return `${axiosClient.defaults.baseURL}/public/provas/${provaId}`;
}
