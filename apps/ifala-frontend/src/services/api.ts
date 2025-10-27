import type {
  CriarDenunciaRequest,
  DenunciaResponse,
  EnumOption,
} from '../types/denuncia';

const API_BASE_URL = '/api/v1';

// Função auxiliar para lidar com erros da API
async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const error = await response.json().catch(() => ({
      message: 'Erro ao processar requisição',
    }));
    throw error;
  }
  return response.json();
}

// ENDPOINTS DO UTILS CONTROLLER

export async function getCategorias(): Promise<EnumOption[]> {
  const response = await fetch(`${API_BASE_URL}/utils/categorias`);
  return handleResponse<EnumOption[]>(response);
}

export async function getGraus(): Promise<EnumOption[]> {
  const response = await fetch(`${API_BASE_URL}/utils/graus`);
  return handleResponse<EnumOption[]>(response);
}

export async function getCursos(): Promise<EnumOption[]> {
  const response = await fetch(`${API_BASE_URL}/utils/cursos`);
  return handleResponse<EnumOption[]>(response);
}

export async function getTurmas(): Promise<EnumOption[]> {
  const response = await fetch(`${API_BASE_URL}/utils/turmas`);
  return handleResponse<EnumOption[]>(response);
}

// ENDPOINT DE DENÚNCIA PÚBLICA

export async function criarDenuncia(
  dados: CriarDenunciaRequest,
): Promise<DenunciaResponse> {
  const response = await fetch(`${API_BASE_URL}/public/denuncias`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(dados),
  });

  return handleResponse<DenunciaResponse>(response);
}
