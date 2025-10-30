export interface AcompanhamentoDetalhes {
  tokenAcompanhamento: string;
  status: string;
  categoria: string;
  criadoEm: string;
}

export interface MensagemAcompanhamento {
  id: number;
  mensagem: string;
  autor: string;
  dataEnvio: string;
}

export interface EnviarMensagemRequest {
  mensagem: string;
}

export interface AcompanhamentoError {
  message?: string;
  errors?: Record<string, string>;
}
