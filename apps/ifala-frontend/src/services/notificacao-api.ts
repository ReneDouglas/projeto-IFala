import type { Notificacao, NotificacaoPaginada } from '../types/notificacao';

/**
 * Constrói os cabeçalhos de autenticação com o token Bearer.
 */
function authHeaders() {
  const token = localStorage.getItem('access_token');
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
  };
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  return headers;
}

/**
 * Lista as notificações não lidas do servidor com paginação.
 * @param page Número da página (começa em 0)
 * @param size Quantidade de itens por página
 */
export async function listarNotificacoes(
  page = 0,
  size = 5,
): Promise<NotificacaoPaginada> {
  const url = `/api/v1/notificacoes?page=${page}&size=${size}`;

  const response = await fetch(url, {
    method: 'GET',
    headers: authHeaders(),
  });

  //Fetch API: O tratamento de erro deve ser explícito para status 4xx/5xx
  if (!response.ok) {
    // Lança um erro se o status HTTP for 400 ou superior
    throw new Error(
      `Erro ao buscar notificações: ${response.status} ${response.statusText}`,
    );
  }

  //Fetch API: A resposta precisa ser parseada manualmente como JSON
  const data: NotificacaoPaginada = await response.json();
  return data;
}

/**
 * Marca uma notificação específica como lida.
 * @param id O ID da notificação a ser marcada.
 */
export async function marcarComoLida(id: number): Promise<Notificacao> {
  const url = `/api/v1/notificacoes/${id}/ler`;

  const response = await fetch(url, {
    method: 'PUT',
    body: null,
    headers: authHeaders(),
  });

  //Tratamento de Erro
  if (!response.ok) {
    throw new Error(
      `Erro ao marcar notificação ${id} como lida: ${response.status} ${response.statusText}`,
    );
  }

  const data: Notificacao = await response.json();
  return data;
}

/**
 * Marca todas as notificações relacionadas a uma denúncia como lidas.
 * @param denunciaId O ID da denúncia
 */
export async function marcarComoLidaPorDenuncia(
  denunciaId: number,
): Promise<void> {
  const url = `/api/v1/notificacoes/denuncia/${denunciaId}/ler`;

  const response = await fetch(url, {
    method: 'PUT',
    headers: authHeaders(),
  });

  if (!response.ok) {
    throw new Error(
      `Erro ao marcar notificações da denúncia ${denunciaId} como lidas: ${response.status} ${response.statusText}`,
    );
  }
}

export default {
  listarNotificacoes,
  marcarComoLida,
  marcarComoLidaPorDenuncia,
};
