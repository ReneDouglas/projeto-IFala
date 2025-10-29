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

// ENDPOINT PARA CONSULTAR DENÚNCIA POR TOKEN

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
