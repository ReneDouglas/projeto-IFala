import { useState } from 'react';
import {
  Box,
  Container,
  Typography,
  Accordion,
  AccordionSummary,
  AccordionDetails,
} from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import PeopleIcon from '@mui/icons-material/People';
import { FormularioUsuario } from './components/FormularioUsuario/FormularioUsuario';
import { TabelaUsuarios } from './components/TabelaUsuarios/TabelaUsuarios';
import type { Usuario } from './types/usuario';
import './GerenciamentoUsuarios.css';

export function GerenciamentoUsuarios() {
  const [expandedPanel, setExpandedPanel] = useState<string | false>('panel1'); // Começa com formulário aberto
  const [usuarioEditando, setUsuarioEditando] = useState<Usuario | null>(null);
  const [refetchTrigger, setRefetchTrigger] = useState(0);

  const handlePanelChange =
    (panel: string) => (event: React.SyntheticEvent, isExpanded: boolean) => {
      setExpandedPanel(isExpanded ? panel : false);
    };

  const handleUsuarioSalvo = () => {
    setRefetchTrigger((prev) => prev + 1);
    setUsuarioEditando(null);
    setExpandedPanel('panel2'); // Muda para o painel da tabela após salvar
  };

  const handleEditarUsuario = (usuario: Usuario) => {
    setUsuarioEditando(usuario);
    setExpandedPanel('panel1'); // Abre o painel do formulário
  };

  const handleCancelarEdicao = () => {
    setUsuarioEditando(null);
  };

  return (
    <div className='gerenciamento-usuarios-page'>
      {/* Hero Section */}
      <section className='hero-section-usuarios'>
        <div className='hero-background-usuarios'>
          <div className='hero-gradient-usuarios'></div>
          <div className='hero-pattern-usuarios'></div>
        </div>
        <Container>
          <div className='hero-content-usuarios'>
            <div className='hero-icon-usuarios'>
              <span className='material-symbols-outlined'>admin_panel_settings</span>
            </div>
            <h1 className='hero-title-usuarios'>Gerenciamento de Usuários</h1>
            <p className='hero-subtitle-usuarios'>
              Cadastre novos usuários administradores e gerencie permissões de
              acesso ao sistema IFala.
            </p>
          </div>
        </Container>
      </section>

      {/* Main Content */}
      <Container maxWidth='lg' sx={{ py: 4 }}>
        <Box className='gerenciamento-usuarios-container'>
          {/* Accordion 1: Formulário de Cadastro/Edição */}
          <Accordion
            expanded={expandedPanel === 'panel1'}
            onChange={handlePanelChange('panel1')}
            className='usuario-accordion'
            sx={{
              mb: 2,
              borderRadius: '12px !important',
              boxShadow: 'var(--sombra-media)',
              '&:before': { display: 'none' },
              '&.Mui-expanded': {
                margin: '0 0 16px 0 !important',
              },
            }}
          >
            <AccordionSummary
              expandIcon={<ExpandMoreIcon />}
              aria-controls='panel1-content'
              id='panel1-header'
              sx={{
                backgroundColor: 'var(--verde-esperanca)',
                color: 'var(--branco)',
                borderRadius: '12px 12px 0 0',
                minHeight: '64px',
                '&.Mui-expanded': {
                  minHeight: '64px',
                  borderRadius: '12px 12px 0 0',
                },
                '& .MuiAccordionSummary-content': {
                  margin: '16px 0',
                },
              }}
            >
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <PersonAddIcon />
                <Typography variant='h6' component='h2' sx={{ fontWeight: 600 }}>
                  {usuarioEditando
                    ? 'Editar Usuário Existente'
                    : 'Cadastrar Novo Usuário'}
                </Typography>
              </Box>
            </AccordionSummary>
            <AccordionDetails sx={{ p: 3 }}>
              <FormularioUsuario
                usuarioEditando={usuarioEditando}
                onUsuarioSalvo={handleUsuarioSalvo}
                onCancelarEdicao={handleCancelarEdicao}
              />
            </AccordionDetails>
          </Accordion>

          {/* Accordion 2: Tabela de Usuários */}
          <Accordion
            expanded={expandedPanel === 'panel2'}
            onChange={handlePanelChange('panel2')}
            className='usuario-accordion'
            sx={{
              borderRadius: '12px !important',
              boxShadow: 'var(--sombra-media)',
              '&:before': { display: 'none' },
              '&.Mui-expanded': {
                margin: '0 !important',
              },
            }}
          >
            <AccordionSummary
              expandIcon={<ExpandMoreIcon />}
              aria-controls='panel2-content'
              id='panel2-header'
              sx={{
                backgroundColor: 'var(--azul-confianca)',
                color: 'var(--branco)',
                borderRadius: '12px 12px 0 0',
                minHeight: '64px',
                '&.Mui-expanded': {
                  minHeight: '64px',
                  borderRadius: '12px 12px 0 0',
                },
                '& .MuiAccordionSummary-content': {
                  margin: '16px 0',
                },
              }}
            >
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <PeopleIcon />
                <Typography variant='h6' component='h2' sx={{ fontWeight: 600 }}>
                  Usuários Cadastrados
                </Typography>
              </Box>
            </AccordionSummary>
            <AccordionDetails sx={{ p: 3 }}>
              <TabelaUsuarios
                refetchTrigger={refetchTrigger}
                onEditarUsuario={handleEditarUsuario}
              />
            </AccordionDetails>
          </Accordion>
        </Box>
      </Container>
    </div>
  );
}
