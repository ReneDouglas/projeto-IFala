export interface EnumOption {
  value: string;
  label: string;
}

export interface DadosDeIdentificacao {
  nomeCompleto: string;
  email: string;
  grau: string;
  curso: string;
  turma: string;
}

export interface CriarDenunciaRequest {
  desejaSeIdentificar: boolean;
  dadosDeIdentificacao?: DadosDeIdentificacao;
  descricaoDetalhada: string;
  categoriaDaDenuncia: string;
  'g-recaptcha-response'?: string;
}

export interface DenunciaResponse {
  tokenAcompanhamento: string;
  status: string;
  categoria: string;
  criadoEm: string;
}

export interface ApiError {
  message?: string;
  errors?: Record<string, string>;
}
