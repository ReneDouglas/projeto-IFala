import axiosClient from "./axios-client";
import type { DenunciasResponse } from "../pages/DenunciaList/types/denunciaTypes";

export async function listarDenunciasAdmin(
  page: number,
  searchParams: any
): Promise<DenunciasResponse> {
  const params: any = {
    pageNumber: page,   // ← CORRETO PARA SEU BACKEND
    size: 10,
  };

  if (searchParams.search) params.search = searchParams.search;
  if (searchParams.status) params.status = searchParams.status;
  if (searchParams.categoria) params.categoria = searchParams.categoria;

  if (searchParams.ordenacao) {
    params.sort = searchParams.ordenacao;
  }

  console.debug("[API] Params enviados:", params);

  const response = await axiosClient.get("/admin/denuncias", { params });

  console.debug("[API] Pageable:", response.data.pageable);

  return response.data;
}
