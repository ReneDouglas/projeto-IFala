import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { Home } from './pages/Home/Home';
import { Denuncia } from './pages/Denuncia/Denuncia';
import { DenunciaSucesso } from './pages/DenunciaSucesso/DenunciaSucesso';
import { Acompanhamento } from './pages/Acompanhamento/Acompanhamento';
import { MainLayout } from './components/MainLayout'; // Importa o layout principal

export function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Rota "pai" que renderiza o Layout Principal */}
        <Route element={<MainLayout />}>
          {/* Rotas "filhas" que serão renderizadas dentro do <Outlet> do Layout */}
          <Route path='/' element={<Home />} />
          <Route path='/denuncia' element={<Denuncia />} />
          <Route path='/denuncia/sucesso' element={<DenunciaSucesso />} />
          <Route path='/acompanhamento' element={<Acompanhamento />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
