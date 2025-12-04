import axiosClient from "./axios-client";
import type { DenunciasResponse } from "../pages/DenunciaList/types/denunciaTypes";

export async function listarDenunciasAdmin(
  page: number,
  searchParams: any
): Promise<DenunciasResponse> {
  
  const params: any = {
    pageNumber: page,
    size: 10,
    search: searchParams.search || "",
    categoria: searchParams.categoria || "",
    status: searchParams.status || "",
    sortProperty: searchParams.sortProperty || "id",
    sortDirection: searchParams.sortDirection || "DESC",
  };

  console.debug("[API] Params enviados:", params);

  const response = await axiosClient.get("/admin/denuncias", { params });

  console.debug("[API] Pageable:", response.data.pageable);

  return response.data;
}
