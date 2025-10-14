import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { Home } from './pages/Home/Home';
import { Denuncia } from './pages/Denuncia/Denuncia';
import { DenunciaSucesso } from './pages/DenunciaSucesso/DenunciaSucesso';
import { DenunciasList } from './pages/DenunciaList/DenunciaList';
import { Login } from './pages/Login/Login';
import { ResetPassword } from './pages/ResetPassword/ResetPassword';
import { MainLayout } from './components/MainLayout';
import { AuthProvider } from './contexts/AuthContext';
import { Acompanhamento } from './pages/Acompanhamento/Acompanhamento';

export function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Rotas de autenticação - sem o MainLayout */}
          <Route path='/login' element={<Login />} />
          <Route path='/reset-password' element={<ResetPassword />} />

          {/* Rota "pai" que renderiza o Layout Principal */}
          <Route element={<MainLayout />}>
            {/* Rotas "filhas" que serão renderizadas dentro do <Outlet> do Layout */}
            <Route path='/' element={<Home />} />
            <Route path='/denuncia' element={<Denuncia />} />
            <Route path='/denuncia/sucesso' element={<DenunciaSucesso />} />
            <Route path='/dashboard-denuncias' element={<DenunciasList />} />
            <Route path='/acompanhamento' element={<Acompanhamento />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
