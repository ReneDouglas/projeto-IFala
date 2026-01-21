import axios, { type AxiosError, type InternalAxiosRequestConfig } from 'axios';
import type { LoginResponse } from '../types/auth';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api/v1';

// instacia do Axios
const axiosClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});

// evitar multiplas tentativas de refresh
let isRefreshing = false;
let failedQueue: Array<{
  resolve: (value?: unknown) => void;
  reject: (reason?: unknown) => void;
}> = [];

const processQueue = (error: Error | null, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });

  failedQueue = [];
};

// Interceptor de REQUEST
axiosClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('access_token');

    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

// Interceptor de RESPONSE
axiosClient.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & {
      _retry?: boolean;
    };

    // Se erro for 401 e naa for uma tentativa de retry
    if (error.response?.status === 401 && !originalRequest._retry) {
      // Evitar refresh em rotas de autenticacao
      if (
        originalRequest.url?.includes('/auth/login') ||
        originalRequest.url?.includes('/auth/refresh') ||
        originalRequest.url?.includes('/auth/redefinir-senha')
      ) {
        return Promise.reject(error);
      }

      if (isRefreshing) {
        // caso for refresh, adicionar à fila
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then((token) => {
            if (originalRequest.headers) {
              originalRequest.headers.Authorization = `Bearer ${token}`;
            }
            return axiosClient(originalRequest);
          })
          .catch((err) => {
            return Promise.reject(err);
          });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        //  fazendo refresh do token
        const response = await axios.post<LoginResponse>(
          `${API_BASE_URL}/auth/refresh`,
          {},
          {
            withCredentials: true, // cookie de refresh token
          },
        );

        const { token } = response.data;

        // Atualizacao do token no localStorage
        localStorage.setItem('access_token', token);

        // Processar fila de requisições pendentes
        processQueue(null, token);

        // Atualizar header da requisicao original
        if (originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${token}`;
        }

        // Retentar requisição original
        return axiosClient(originalRequest);
      } catch (refreshError) {
        // caso refresh falhar, fazer logout
        console.error('[AUTH] Refresh falhou, fazendo logout...', refreshError);

        processQueue(refreshError as Error, null);
        localStorage.removeItem('access_token');
        localStorage.removeItem('usuarioLogado');

        // redireciona para login
        window.location.href = '/login';

        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  },
);

export default axiosClient;
