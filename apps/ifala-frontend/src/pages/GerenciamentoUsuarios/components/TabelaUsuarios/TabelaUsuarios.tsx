import { useState, useEffect } from 'react';
import {
  Box,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TablePagination,
  Paper,
  IconButton,
  Chip,
  TextField,
  InputAdornment,
  CircularProgress,
  Alert,
  Tooltip,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
} from '@mui/material';
import {
  Edit,
  Search,
  Refresh,
  FilterList,
  Check,
  Close,
} from '@mui/icons-material';
import type { Usuario, UsuarioFilters } from '../../types/usuario';
import './TabelaUsuarios.css';

interface TabelaUsuariosProps {
  refetchTrigger: number;
  onEditarUsuario: (usuario: Usuario) => void;
}

export function TabelaUsuarios({
  refetchTrigger,
  onEditarUsuario,
}: TabelaUsuariosProps) {
  const [usuarios, setUsuarios] = useState<Usuario[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalElements, setTotalElements] = useState(0);
  const [filters, setFilters] = useState<UsuarioFilters>({
    search: '',
    perfil: '',
    senhaTemporaria: '',
  });
  const [showFilters, setShowFilters] = useState(false);

  // Buscar usuários quando a página ou filtros mudarem
  useEffect(() => {
    fetchUsuarios();
  }, [page, rowsPerPage, refetchTrigger]);

  const fetchUsuarios = async () => {
    setLoading(true);
    setError(null);

    try {
      // Simula chamada API com dados mockados
      await new Promise((resolve) => setTimeout(resolve, 800));

      // TODO: Substituir por chamada real à API
      // const response = await buscarUsuarios(page, rowsPerPage, filters);

      // Dados mockados para demonstração
      const mockUsuarios: Usuario[] = [
        {
          id: 1,
          nome: 'João Silva Santos',
          username: 'joao.silva',
          email: 'joao.silva@ifpi.edu.br',
          senhaTemporaria: true,
          perfil: 'ADMIN',
          criadoEm: '2025-01-10T10:30:00',
        },
        {
          id: 2,
          nome: 'Maria Oliveira Costa',
          username: 'maria.oliveira',
          email: 'maria.oliveira@ifpi.edu.br',
          senhaTemporaria: false,
          perfil: 'USER',
          criadoEm: '2025-01-11T14:20:00',
        },
        {
          id: 3,
          nome: 'Pedro Henrique Almeida',
          username: 'pedro.almeida',
          email: 'pedro.almeida@ifpi.edu.br',
          senhaTemporaria: true,
          perfil: 'USER',
          criadoEm: '2025-01-12T09:15:00',
        },
        {
          id: 4,
          nome: 'Ana Paula Rodrigues',
          username: 'ana.rodrigues',
          email: 'ana.rodrigues@ifpi.edu.br',
          senhaTemporaria: false,
          perfil: 'ADMIN',
          criadoEm: '2025-01-13T16:45:00',
        },
        {
          id: 5,
          nome: 'Carlos Eduardo Souza',
          username: 'carlos.souza',
          email: 'carlos.souza@ifpi.edu.br',
          senhaTemporaria: true,
          perfil: 'USER',
          criadoEm: '2025-01-14T11:00:00',
        },
      ];

      setUsuarios(mockUsuarios);
      setTotalElements(mockUsuarios.length);
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : 'Erro ao carregar usuários. Tente novamente.'
      );
    } finally {
      setLoading(false);
    }
  };

  const handleChangePage = (event: unknown, newPage: number) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleFilterChange = (field: keyof UsuarioFilters, value: string) => {
    setFilters((prev) => ({ ...prev, [field]: value }));
  };

  const handleApplyFilters = () => {
    setPage(0);
    fetchUsuarios();
  };

  const handleClearFilters = () => {
    setFilters({
      search: '',
      perfil: '',
      senhaTemporaria: '',
    });
    setPage(0);
    fetchUsuarios();
  };

  const fieldStyles = {
    '& .MuiOutlinedInput-root': {
      '&:hover fieldset': {
        borderColor: 'var(--azul-confianca)',
      },
      '&.Mui-focused fieldset': {
        borderColor: 'var(--azul-confianca)',
      },
    },
    '& .MuiInputLabel-root.Mui-focused': {
      color: 'var(--azul-confianca)',
    },
  };

  return (
    <Box className='tabela-usuarios-container'>
      {/* Barra de Ferramentas */}
      <Box className='toolbar-usuarios'>
        <TextField
          placeholder='Buscar por nome, email ou username...'
          value={filters.search}
          onChange={(e) => handleFilterChange('search', e.target.value)}
          onKeyPress={(e) => e.key === 'Enter' && handleApplyFilters()}
          InputProps={{
            startAdornment: (
              <InputAdornment position='start'>
                <Search />
              </InputAdornment>
            ),
          }}
          sx={{ ...fieldStyles, minWidth: { xs: '100%', sm: 300 }, flex: 1 }}
          size='small'
        />

        <Box sx={{ display: 'flex', gap: 1 }}>
          <Tooltip title='Filtros avançados'>
            <IconButton
              onClick={() => setShowFilters(!showFilters)}
              color={showFilters ? 'primary' : 'default'}
              sx={{
                color: showFilters ? 'var(--azul-confianca)' : 'inherit',
              }}
            >
              <FilterList />
            </IconButton>
          </Tooltip>

          <Tooltip title='Atualizar lista'>
            <IconButton onClick={fetchUsuarios} disabled={loading}>
              <Refresh />
            </IconButton>
          </Tooltip>
        </Box>
      </Box>

      {/* Filtros Avançados */}
      {showFilters && (
        <Box className='filtros-avancados'>
          <FormControl size='small' sx={{ ...fieldStyles, minWidth: { xs: '100%', sm: 200 }, flex: { xs: 1, sm: 'initial' } }}>
            <InputLabel>Perfil</InputLabel>
            <Select
              value={filters.perfil}
              label='Perfil'
              onChange={(e) => handleFilterChange('perfil', e.target.value)}
            >
              <MenuItem value=''>Todos</MenuItem>
              <MenuItem value='ADMIN'>Administrador</MenuItem>
              <MenuItem value='USER'>Usuário</MenuItem>
            </Select>
          </FormControl>

          <FormControl size='small' sx={{ ...fieldStyles, minWidth: { xs: '100%', sm: 200 }, flex: { xs: 1, sm: 'initial' } }}>
            <InputLabel>Senha Temporária</InputLabel>
            <Select
              value={filters.senhaTemporaria}
              label='Senha Temporária'
              onChange={(e) =>
                handleFilterChange('senhaTemporaria', e.target.value)
              }
            >
              <MenuItem value=''>Todos</MenuItem>
              <MenuItem value='true'>Sim</MenuItem>
              <MenuItem value='false'>Não</MenuItem>
            </Select>
          </FormControl>

          <Box sx={{ display: 'flex', gap: 1 }}>
            <Tooltip title='Aplicar filtros'>
              <IconButton
                onClick={handleApplyFilters}
                sx={{
                  backgroundColor: 'var(--verde-esperanca)',
                  color: 'var(--branco)',
                  '&:hover': {
                    backgroundColor: '#257247',
                  },
                }}
              >
                <Search />
              </IconButton>
            </Tooltip>

            <Tooltip title='Limpar filtros'>
              <IconButton
                onClick={handleClearFilters}
                sx={{
                  backgroundColor: 'var(--cinza-claro)',
                  '&:hover': {
                    backgroundColor: 'var(--cinza-medio)',
                  },
                }}
              >
                <Close />
              </IconButton>
            </Tooltip>
          </Box>
        </Box>
      )}

      {/* Mensagem de Erro */}
      {error && (
        <Alert severity='error' sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {/* Tabela */}
      <TableContainer component={Paper} className='table-container' sx={{ overflowX: 'auto' }}>
        {loading ? (
          <Box className='loading-state'>
            <CircularProgress />
            <p>Carregando usuários...</p>
          </Box>
        ) : usuarios.length === 0 ? (
          <Box className='empty-state'>
            <span className='material-symbols-outlined'>person_off</span>
            <h3>Nenhum usuário encontrado</h3>
            <p>
              Não há usuários cadastrados ou os filtros aplicados não retornaram
              resultados.
            </p>
          </Box>
        ) : (
          <Table>
            <TableHead>
              <TableRow
                sx={{
                  backgroundColor: 'var(--azul-confianca)',
                  '& th': {
                    color: 'var(--branco)',
                    fontWeight: 600,
                  },
                }}
              >
                <TableCell>N.</TableCell>
                <TableCell>Nome</TableCell>
                <TableCell>Username</TableCell>
                <TableCell>Email</TableCell>
                <TableCell align='center'>Senha Temporária</TableCell>
                <TableCell align='center'>Perfil</TableCell>
                <TableCell align='center'>Ações</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {usuarios.map((usuario, index) => (
                <TableRow
                  key={usuario.id}
                  sx={{
                    '&:hover': {
                      backgroundColor: 'var(--cinza-claro)',
                    },
                  }}
                >
                  <TableCell>{page * rowsPerPage + index + 1}</TableCell>
                  <TableCell>
                    <strong>{usuario.nome}</strong>
                  </TableCell>
                  <TableCell>
                    <code className='username-code'>{usuario.username}</code>
                  </TableCell>
                  <TableCell>{usuario.email}</TableCell>
                  <TableCell align='center'>
                    {usuario.senhaTemporaria ? (
                      <Chip
                        icon={<Check />}
                        label='Sim'
                        size='small'
                        sx={{
                          backgroundColor: 'var(--amarelo-atencao)',
                          color: 'var(--branco)',
                        }}
                      />
                    ) : (
                      <Chip
                        icon={<Close />}
                        label='Não'
                        size='small'
                        sx={{
                          backgroundColor: 'var(--verde-sucesso)',
                          color: 'var(--branco)',
                        }}
                      />
                    )}
                  </TableCell>
                  <TableCell align='center'>
                    <Chip
                      label={usuario.perfil === 'ADMIN' ? 'Admin' : 'Usuário'}
                      size='small'
                      sx={{
                        backgroundColor:
                          usuario.perfil === 'ADMIN'
                            ? 'var(--azul-confianca)'
                            : 'var(--cinza-medio)',
                        color: 'var(--branco)',
                      }}
                    />
                  </TableCell>
                  <TableCell align='center'>
                    <Tooltip title='Editar usuário'>
                      <IconButton
                        onClick={() => onEditarUsuario(usuario)}
                        sx={{
                          color: 'var(--verde-esperanca)',
                          '&:hover': {
                            backgroundColor: 'var(--verde-esperanca-10)',
                          },
                        }}
                      >
                        <Edit />
                      </IconButton>
                    </Tooltip>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        )}
      </TableContainer>

      {/* Paginação */}
      {!loading && usuarios.length > 0 && (
        <TablePagination
          component='div'
          count={totalElements}
          page={page}
          onPageChange={handleChangePage}
          rowsPerPage={rowsPerPage}
          onRowsPerPageChange={handleChangeRowsPerPage}
          rowsPerPageOptions={[5, 10, 25, 50]}
          labelRowsPerPage='Registros por página:'
          labelDisplayedRows={({ from, to, count }) =>
            `${from}-${to} de ${count !== -1 ? count : `mais de ${to}`}`
          }
          sx={{
            borderTop: '1px solid var(--cinza-claro)',
            '& .MuiTablePagination-select': {
              color: 'var(--azul-confianca)',
            },
            '& .MuiTablePagination-selectIcon': {
              color: 'var(--azul-confianca)',
            },
          }}
        />
      )}
    </Box>
  );
}
