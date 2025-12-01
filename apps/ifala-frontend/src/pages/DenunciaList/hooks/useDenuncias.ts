import { useState, useEffect, useCallback } from "react";
import type { Denuncia, DenunciasResponse, SearchParams } from "../types/denunciaTypes";
import { listarDenunciasAdmin } from "../../../services/admin-denuncias-api";

export const useDenuncias = (
  currentPage: number,
  searchParams: SearchParams
) => {
  const [denuncias, setDenuncias] = useState<Denuncia[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [totalPages, setTotalPages] = useState(0);

  const load = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      const response: DenunciasResponse = await listarDenunciasAdmin(
        currentPage,
        searchParams
      );

      setDenuncias(response.content || []);
      setTotalPages(response.totalPages || 0);

    } catch (err: any) {
      setError(err?.response?.data?.message || "Erro ao carregar denúncias.");
      setDenuncias([]);
      setTotalPages(0);
    } finally {
      setLoading(false);
    }
  }, [currentPage, searchParams]);

  useEffect(() => {
    load();
  }, [load]);

  return {
    denuncias,
    loading,
    error,
    totalPages,
    refetch: load,
  };
};
