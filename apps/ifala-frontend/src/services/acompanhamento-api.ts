import type {
  AcompanhamentoDetalhes,
  MensagemAcompanhamento,
  EnviarMensagemRequest,
} from '../types/acompanhamento';

const API_BASE_URL = '/api/v1/public/denuncias';

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

// ENDPOINT PARA CONSULTAR DENNCIA POR TOKEN

export async function consultarDenunciaPorToken(
  token: string,
): Promise<AcompanhamentoDetalhes> {
  const response = await fetch(`${API_BASE_URL}/${token}`);
  return handleResponse<AcompanhamentoDetalhes>(response);
}

// ENDPOINT PARA LISTAR MENSAGENS DE ACOMPANHAMENTO

export async function listarMensagens(
  token: string,
): Promise<MensagemAcompanhamento[]> {
  const response = await fetch(`${API_BASE_URL}/${token}/acompanhamentos`);
  return handleResponse<MensagemAcompanhamento[]>(response);
}

// ENDPOINT PARA ENVIAR NOVA MENSAGEM

export async function enviarMensagem(
  token: string,
  mensagem: string,
): Promise<MensagemAcompanhamento> {
  const dados: EnviarMensagemRequest = { mensagem };

  const response = await fetch(`${API_BASE_URL}/${token}/acompanhamentos`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(dados),
  });

  return handleResponse<MensagemAcompanhamento>(response);
}

// ENDPOINT ADMIN PARA ENVIAR MENSAGEM (sem restrição de flood)

export async function enviarMensagemAdmin(
  denunciaId: number,
  mensagem: string,
): Promise<MensagemAcompanhamento> {
  const token = localStorage.getItem('access_token');

  const response = await fetch(
    `/api/v1/admin/denuncias/${denunciaId}/acompanhamentos`,
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ mensagem }),
    },
  );

  return handleResponse<MensagemAcompanhamento>(response);
}

// ENDPOINT ADMIN PARA ALTERAR STATUS DA DENÚNCIA

export async function alterarStatusDenuncia(
  denunciaId: number,
  novoStatus: string,
): Promise<void> {
  const token = localStorage.getItem('access_token');

  const response = await fetch(`/api/v1/admin/denuncias/${denunciaId}/status`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify({ status: novoStatus }),
  });

  if (!response.ok) {
    const error = await response.json().catch(() => ({
      message: 'Erro ao alterar status',
    }));
    throw error;
  }
}
