export interface DenuncianteInfo {
  nomeCompleto: string;
  grau: string;
  curso: string;
  ano: string | null;
  turma: string;
  email?: string;
}

export interface AcompanhamentoDetalhes {
  id: number;
  tokenAcompanhamento?: string;
  status: string;
  categoria: string;
  criadoEm: string;
  updatedAt?: string;
  denunciante?: DenuncianteInfo | null;
  descricao?: string;
  atualizadoEm?: string;
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
