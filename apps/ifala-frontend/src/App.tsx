import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { Home } from './pages/Home/Home';
import { Denuncia } from './pages/Denuncia/Denuncia';
import { DenunciaSucesso } from './pages/DenunciaSucesso/DenunciaSucesso';
import { DenunciasList } from './pages/DenunciaList/DenunciaList';
import { Login } from './pages/Login/Login';
import { RedefinirSenha } from './pages/ResetPassword/ResetPassword';
import { MainLayout } from './components/MainLayout';
import { ProtectedRoute } from './components/ProtectedRoute';
import { AuthProvider } from './contexts/AuthContext';
import { Acompanhamento } from './pages/Acompanhamento/Acompanhamento';

export function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Rotas sem layout */}
          <Route path='/login' element={<Login />} />
          <Route path='/redefinir-senha/:token' element={<RedefinirSenha />} />

          {/* Layout principal */}
          <Route element={<MainLayout />}>
            <Route path='/' element={<Home />} />
            <Route path='/denuncia' element={<Denuncia />} />
            <Route path='/denuncia/sucesso' element={<DenunciaSucesso />} />

            {/* Acompanhamento público */}
            <Route path='/acompanhamento/:token' element={<Acompanhamento />} />

            {/* Rotas protegidas */}
            <Route element={<ProtectedRoute />}>
              <Route path='/painel-denuncias' element={<DenunciasList />} />
              {/* Acompanhamento administrativo usando id */}
              <Route
                path='/admin/denuncias/:denunciaId/acompanhamento'
                element={<Acompanhamento />}
              />
            </Route>
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
