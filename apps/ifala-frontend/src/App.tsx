import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { Home } from './pages/Home/Home';
import { Denuncia } from './pages/Denuncia/Denuncia';
import { DenunciaSucesso } from './pages/DenunciaSucesso/DenunciaSucesso';

export function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path='/' element={<Home />} />

        {/* Rotas da funcionalidade de Denúncia */}
        <Route path='/denuncia' element={<Denuncia />} />
        <Route path='/denuncia/sucesso' element={<DenunciaSucesso />} />
      </Routes>
    </BrowserRouter>
  );
}
