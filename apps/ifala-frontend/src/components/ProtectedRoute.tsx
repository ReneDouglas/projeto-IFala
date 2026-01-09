import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { CircularProgress, Box } from '@mui/material';

/**
 * Componente para proteger rotas que requerem autenticação
 */
export function ProtectedRoute() {
  const { isLoggedIn, loading } = useAuth();
  const location = useLocation();

  //  loader é exibido enquanto verifica autenticação
  if (loading) {
    return (
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          minHeight: '100vh',
        }}
      >
        <CircularProgress />
      </Box>
    );
  }

  // Se nao estiver logado, redirecionar para login salvando a URL de destino
  if (!isLoggedIn) {
    return <Navigate to='/login' state={{ from: location }} replace />;
  }

  return <Outlet />;
}
