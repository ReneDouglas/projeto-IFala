import axiosClient from './axios-client';
import type {
  AcompanhamentoDetalhes,
  MensagemAcompanhamento,
  EnviarMensagemRequest,
} from '../types/acompanhamento';

// ENDPOINT PARA CONSULTAR DENNCIA POR TOKEN

export async function consultarDenunciaPorToken(
  token: string,
): Promise<AcompanhamentoDetalhes> {
  const response = await axiosClient.get(`/public/denuncias/${token}`);
  return response.data;
}

// ENDPOINT PARA LISTAR MENSAGENS DE ACOMPANHAMENTO

export async function listarMensagens(
  token: string,
): Promise<MensagemAcompanhamento[]> {
  const response = await axiosClient.get(
    `/public/denuncias/${token}/acompanhamentos`,
  );
  return response.data;
}

// ENDPOINT PARA ENVIAR NOVA MENSAGEM

export async function enviarMensagem(
  token: string,
  mensagem: string,
): Promise<MensagemAcompanhamento> {
  const dados: EnviarMensagemRequest = { mensagem };

  const response = await axiosClient.post(
    `/public/denuncias/${token}/acompanhamentos`,
    dados,
  );

  return response.data;
}

// ENDPOINT ADMIN PARA ENVIAR MENSAGEM (sem restrição de flood)

export async function enviarMensagemAdmin(
  denunciaId: number,
  mensagem: string,
): Promise<MensagemAcompanhamento> {
  const response = await axiosClient.post(
    `/admin/denuncias/${denunciaId}/acompanhamentos`,
    { mensagem },
  );

  return response.data;
}

// ENDPOINT ADMIN PARA ALTERAR STATUS DA DENÚNCIA

export async function alterarStatusDenuncia(
  denunciaId: number,
  novoStatus: string,
): Promise<void> {
  await axiosClient.post(`/admin/denuncias/${denunciaId}/status`, {
    status: novoStatus,
  });
}

// ENDPOINT ADMIN PARA CONSULTAR DENÚNCIA POR ID
export async function consultarDenunciaPorId(denunciaId: number): Promise<{
  id: number;
  categoria: string;
  status: string;
  descricao: string;
  criadoEm: string;
  atualizadoEm?: string;
  denunciante?: {
    nome: string;
    email: string;
    grau?: string;
    curso?: string;
    turma?: string;
  };
}> {
  const response = await axiosClient.get(`/admin/denuncias/${denunciaId}`);
  return response.data;
}

// ENDPOINT ADMIN PARA LISTAR ACOMPANHAMENTOS POR ID
export async function listarAcompanhamentosPorId(
  denunciaId: number,
): Promise<Array<{
  id: number;
  mensagem: string;
  autor: string;
  dataEnvio: string;
}>> {
  const response = await axiosClient.get(
    `/admin/denuncias/${denunciaId}/acompanhamentos`,
  );
  return response.data;
}
