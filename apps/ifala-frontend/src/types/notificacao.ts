export interface Notificacao {
  id: number;
  conteudo: string;
  tipo: 'NOVA_DENUNCIA' | 'NOVA_MENSAGEM' | string;
  denunciaId?: number | null;
  lida?: boolean;
  lidaPor?: string | null;
  dataEnvio?: string | null;
}
