import type { Notificacao } from '../types/notificacao';

/**
 * Constrói os cabeçalhos de autenticação com o token Bearer.
 */
function authHeaders() {
  const token = localStorage.getItem('access_token');
  const headers: HeadersInit = {
    'Content-Type': 'application/json', // Adiciona o Content-Type para requisições com corpo
  };
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  return headers;
}

/**
 * Lista as notificações do servidor.
 * @param unreadOnly Se true, busca apenas as notificações não lidas.
 */
export async function listarNotificacoes(
  unreadOnly?: boolean,
): Promise<Notificacao[]> {
  const query = unreadOnly ? '?unreadOnly=true' : '';
  const url = `/api/notificacoes${query}`;

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
  const data: Notificacao[] = await response.json();
  return data;
}

/**
 * Marca uma notificação específica como lida.
 * @param id O ID da notificação a ser marcada.
 */
export async function marcarComoLida(id: number): Promise<Notificacao> {
  const url = `/api/notificacoes/${id}/ler`;

  const response = await fetch(url, {
    // Usamos 'PUT' para atualizar o estado da notificação
    method: 'PUT',
    // O corpo é 'null' pois não estamos enviando dados, apenas acionando a URL de ação
    body: null,
    headers: authHeaders(),
  });

  //Tratamento de Erro
  if (!response.ok) {
    throw new Error(
      `Erro ao marcar notificação ${id} como lida: ${response.status} ${response.statusText}`,
    );
  }

  //Parse do JSON
  const data: Notificacao = await response.json();
  return data;
}

export default { listarNotificacoes, marcarComoLida };
