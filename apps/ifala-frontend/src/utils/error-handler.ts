import { AxiosError } from 'axios';

/**
 * Interface para representar a resposta de erro do backend
 */
interface ErrorResponse {
  error: string;
  message?: string;
}

/**
 * Extrai e formata a mensagem de erro de uma resposta Axios
 * @param error O erro do Axios ou qualquer outro erro
 * @returns Uma mensagem de erro formatada e amigável ao usuário
 */
export function extractErrorMessage(error: unknown): string {
  // Verifica se é um erro do Axios
  if (error instanceof AxiosError) {
    const axiosError = error as AxiosError<ErrorResponse>;

    // Se houver uma resposta do servidor
    if (axiosError.response) {
      const status = axiosError.response.status;
      const data = axiosError.response.data;

      // Tratamento específico por código de status
      switch (status) {
        case 409: // Conflito - email ou username já existe
          if (data?.error) {
            return data.error;
          }
          return 'Este e-mail ou nome de usuário já está cadastrado no sistema.';

        case 400: // Bad Request - dados inválidos
          if (data?.error) {
            return data.error;
          }
          if (data?.message) {
            return data.message;
          }
          return 'Os dados fornecidos são inválidos. Por favor, verifique e tente novamente.';

        case 401: // Não autorizado
          return 'Você não tem permissão para realizar esta ação. Faça login novamente.';

        case 403: // Proibido
          return 'Acesso negado. Você não tem permissão para realizar esta ação.';

        case 404: // Não encontrado
          return 'O recurso solicitado não foi encontrado.';

        case 422: // Unprocessable Entity - validação falhou
          if (data?.error) {
            return data.error;
          }
          return 'Não foi possível processar os dados. Verifique as informações fornecidas.';

        case 500: // Erro interno do servidor
          return 'Ocorreu um erro interno no servidor. Por favor, tente novamente mais tarde.';

        case 503: // Serviço indisponível
          return 'O serviço está temporariamente indisponível. Por favor, tente novamente mais tarde.';

        default:
          // Para outros códigos, tenta extrair a mensagem do backend
          if (data?.error) {
            return data.error;
          }
          if (data?.message) {
            return data.message;
          }
          return `Erro ao processar a requisição (código ${status}).`;
      }
    }

    // Se não houver resposta (erro de rede, timeout, etc.)
    if (axiosError.request) {
      if (axiosError.code === 'ECONNABORTED') {
        return 'A requisição demorou muito tempo. Verifique sua conexão e tente novamente.';
      }
      if (axiosError.code === 'ERR_NETWORK') {
        return 'Erro de rede. Verifique sua conexão com a internet.';
      }
      return 'Não foi possível conectar ao servidor. Verifique sua conexão.';
    }

    // Erro na configuração da requisição
    return axiosError.message || 'Erro ao configurar a requisição.';
  }

  // Se for um erro padrão do JavaScript
  if (error instanceof Error) {
    return error.message;
  }

  // Fallback para erros desconhecidos
  return 'Ocorreu um erro inesperado. Por favor, tente novamente.';
}

/**
 * Verifica se o erro é um conflito (409) específico de email
 * @param error O erro do Axios
 * @returns true se for um erro 409 relacionado a email
 */
export function isEmailConflictError(error: unknown): boolean {
  if (error instanceof AxiosError) {
    const axiosError = error as AxiosError<ErrorResponse>;
    if (axiosError.response?.status === 409) {
      const message = axiosError.response.data?.error?.toLowerCase() || '';
      return message.includes('e-mail') || message.includes('email');
    }
  }
  return false;
}

/**
 * Verifica se o erro é um conflito (409) específico de username
 * @param error O erro do Axios
 * @returns true se for um erro 409 relacionado a username
 */
export function isUsernameConflictError(error: unknown): boolean {
  if (error instanceof AxiosError) {
    const axiosError = error as AxiosError<ErrorResponse>;
    if (axiosError.response?.status === 409) {
      const message = axiosError.response.data?.error?.toLowerCase() || '';
      return message.includes('username') || message.includes('usuário');
    }
  }
  return false;
}

/**
 * Verifica se o erro é relacionado a validação de dados
 * @param error O erro do Axios
 * @returns true se for um erro de validação (400 ou 422)
 */
export function isValidationError(error: unknown): boolean {
  if (error instanceof AxiosError) {
    const status = error.response?.status;
    return status === 400 || status === 422;
  }
  return false;
}
