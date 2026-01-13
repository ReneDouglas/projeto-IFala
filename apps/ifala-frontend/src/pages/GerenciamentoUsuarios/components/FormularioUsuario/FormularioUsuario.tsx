import { useState, useEffect } from 'react';
import {
  Box,
  TextField,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
  CircularProgress,
  InputAdornment,
  IconButton,
} from '@mui/material';
import {
  Visibility,
  VisibilityOff,
  Save,
  Cancel,
  PersonAdd,
} from '@mui/icons-material';
//import type {  UsuarioFormData } from '../../types/usuario';
import './FormularioUsuario.css';
import {
  registrarUsuario,
  atualizarUsuario,
} from '../../../../services/admin-usuarios-api';
import type {
  Usuario,
  AtualizarUsuarioRequest,
  RegistroUsuarioRequest,
} from '../../../../types/usuario';

// Tipos locais para o formulário
interface UsuarioFormData {
  nome: string;
  username: string;
  email: string;
  senha?: string;
  confirmarSenha?: string;
  roles: string[];
  mustChangePassword: boolean;
}

interface FormularioUsuarioProps {
  usuarioEditando: Usuario | null;
  onUsuarioSalvo: () => void;
  onCancelarEdicao: () => void;
}

export function FormularioUsuario({
  usuarioEditando,
  onUsuarioSalvo,
  onCancelarEdicao,
}: FormularioUsuarioProps) {
  const [formData, setFormData] = useState<UsuarioFormData>({
    nome: '',
    username: '',
    email: '',
    senha: '',
    confirmarSenha: '',
    roles: ['ADMIN'],
    mustChangePassword: true,
  });

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});

  // Preenche o formulário quando está editando ou limpa para um novo usuário
  useEffect(() => {
    if (usuarioEditando) {
      setFormData({
        nome: usuarioEditando.nome,
        username: usuarioEditando.username,
        email: usuarioEditando.email,
        senha: '',
        confirmarSenha: '',
        roles: usuarioEditando.roles,
        mustChangePassword: usuarioEditando.mustChangePassword,
      });
      setSuccess(false);
      setError(null);
      setErrors({});
    } else {
      resetForm();
    }
  }, [usuarioEditando]);

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.nome.trim()) {
      newErrors.nome = 'Nome é obrigatório';
    }

    if (!formData.username.trim()) {
      newErrors.username = 'Nome de usuário é obrigatório';
    } else if (formData.username.length < 3) {
      newErrors.username = 'Nome de usuário deve ter no mínimo 3 caracteres';
    }

    if (!formData.email.trim()) {
      newErrors.email = 'E-mail é obrigatório';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = 'E-mail inválido';
    }

    // Validação de senha apenas para novos usuários ou se a senha foi preenchida
    if (!usuarioEditando || formData.senha) {
      if (!formData.senha) {
        newErrors.senha = 'Senha temporária é obrigatória';
      } else if (formData.senha.length < 6) {
        newErrors.senha = 'Senha deve ter no mínimo 6 caracteres';
      }

      if (formData.senha !== formData.confirmarSenha) {
        newErrors.confirmarSenha = 'As senhas não coincidem';
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setLoading(true);
    setError(null);
    setSuccess(false);

    try {
      let response: Usuario;

      if (usuarioEditando) {
        const dadosAtualizados: AtualizarUsuarioRequest = {
          nome: formData.nome,
          email: formData.email,
          username: formData.username,
          roles: formData.roles,
          mustChangePassword: formData.mustChangePassword,
        };
        response = await atualizarUsuario(usuarioEditando.id, dadosAtualizados);
      } else {
        const dadosRegistro: RegistroUsuarioRequest = {
          nome: formData.nome,
          email: formData.email,
          username: formData.username,
          senha: formData.senha!,
          roles: formData.roles,
        };
        response = await registrarUsuario(dadosRegistro);
      }
      console.log('Usuário salvo com sucesso:', response);

      setSuccess(true);
      setTimeout(() => {
        resetForm();
        onUsuarioSalvo();
      }, 1500);
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : 'Erro ao salvar usuário. Tente novamente.',
      );
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    setFormData({
      nome: '',
      username: '',
      email: '',
      senha: '',
      confirmarSenha: '',
      roles: ['ADMIN'],
      mustChangePassword: true,
    });
    setErrors({});
    setError(null);
    setSuccess(false);
  };

  const handleCancel = () => {
    resetForm();
    onCancelarEdicao();
  };

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    // Limpa o erro do campo quando o usuário começa a digitar
    if (errors[name]) {
      setErrors((prev) => {
        const newErrors = { ...prev };
        delete newErrors[name];
        return newErrors;
      });
    }
  };

  const fieldStyles = {
    '& .MuiOutlinedInput-root': {
      '&:hover fieldset': {
        borderColor: 'var(--verde-esperanca)',
      },
      '&.Mui-focused fieldset': {
        borderColor: 'var(--verde-esperanca)',
      },
    },
    '& .MuiInputLabel-root.Mui-focused': {
      color: 'var(--verde-esperanca)',
    },
  };

  return (
    <Box
      component='form'
      onSubmit={handleSubmit}
      className='formulario-usuario'
    >
      {error && (
        <Alert severity='error' sx={{ mb: 3 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {success && (
        <Alert severity='success' sx={{ mb: 3 }}>
          Usuário {usuarioEditando ? 'atualizado' : 'cadastrado'} com sucesso!
        </Alert>
      )}

      <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
        {/* Nome Completo e Nome de Usuário */}
        <Box
          sx={{
            display: 'flex',
            gap: 3,
            flexDirection: { xs: 'column', md: 'row' },
          }}
        >
          <Box sx={{ flex: 1 }}>
            <TextField
              fullWidth
              label='Nome Completo *'
              name='nome'
              value={formData.nome}
              onChange={handleChange}
              error={!!errors.nome}
              helperText={errors.nome}
              disabled={loading}
              sx={fieldStyles}
            />
          </Box>
          <Box sx={{ flex: 1 }}>
            <TextField
              fullWidth
              label='Nome de Usuário *'
              name='username'
              value={formData.username}
              onChange={handleChange}
              error={!!errors.username}
              helperText={
                errors.username || 'Usado para fazer login no sistema'
              }
              disabled={loading}
              sx={fieldStyles}
            />
          </Box>
        </Box>

        {/* E-mail */}
        <Box>
          <TextField
            fullWidth
            type='email'
            label='E-mail *'
            name='email'
            value={formData.email}
            onChange={handleChange}
            error={!!errors.email}
            helperText={errors.email}
            disabled={loading}
            sx={fieldStyles}
          />
        </Box>

        {/* Perfil */}
        <Box
          sx={{
            display: 'flex',
            gap: 3,
            flexDirection: { xs: 'column', md: 'row' },
          }}
        >
          <Box sx={{ flex: 1 }}>
            <FormControl fullWidth sx={fieldStyles}>
              <InputLabel
                sx={{
                  '&.Mui-focused': { color: 'var(--verde-esperanca)' },
                }}
              >
                Perfil de Acesso *
              </InputLabel>
              <Select
                name='perfil'
                value='ADMIN'
                label='Perfil de Acesso *'
                onChange={(e) =>
                  setFormData((prev) => ({
                    ...prev,
                    roles: [e.target.value as 'ADMIN'],
                  }))
                }
                disabled={loading}
                sx={{
                  '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
                    borderColor: 'var(--verde-esperanca)',
                  },
                }}
              >
                <MenuItem value='ADMIN'>Administrador</MenuItem>
              </Select>
            </FormControl>
          </Box>
          <Box sx={{ flex: 1 }}>
            <TextField
              fullWidth
              type={showPassword ? 'text' : 'password'}
              label={
                usuarioEditando
                  ? 'Nova Senha (deixe em branco para manter)'
                  : 'Senha Temporária *'
              }
              name='senha'
              value={formData.senha}
              onChange={handleChange}
              error={!!errors.senha}
              helperText={
                errors.senha ||
                (usuarioEditando
                  ? 'Preencha apenas se desejar alterar a senha'
                  : 'Usuário deverá alterar no primeiro acesso')
              }
              disabled={loading}
              sx={fieldStyles}
              InputProps={{
                endAdornment: (
                  <InputAdornment position='end'>
                    <IconButton
                      onClick={() => setShowPassword(!showPassword)}
                      edge='end'
                    >
                      {showPassword ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
            />
          </Box>
        </Box>

        {/* Confirmar Senha */}
        {(formData.senha || !usuarioEditando) && (
          <Box
            sx={{
              display: 'flex',
              gap: 3,
              flexDirection: { xs: 'column', md: 'row' },
            }}
          >
            <Box sx={{ flex: 1 }}>
              <TextField
                fullWidth
                type={showConfirmPassword ? 'text' : 'password'}
                label='Confirmar Senha *'
                name='confirmarSenha'
                value={formData.confirmarSenha}
                onChange={handleChange}
                error={!!errors.confirmarSenha}
                helperText={errors.confirmarSenha}
                disabled={loading}
                sx={fieldStyles}
                InputProps={{
                  endAdornment: (
                    <InputAdornment position='end'>
                      <IconButton
                        onClick={() =>
                          setShowConfirmPassword(!showConfirmPassword)
                        }
                        edge='end'
                      >
                        {showConfirmPassword ? (
                          <VisibilityOff />
                        ) : (
                          <Visibility />
                        )}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />
            </Box>
            <Box sx={{ flex: 1 }}></Box>
          </Box>
        )}
      </Box>

      {/* Botões de Ação */}
      <Box sx={{ display: 'flex', gap: 2, mt: 4, justifyContent: 'flex-end' }}>
        {usuarioEditando && (
          <Button
            variant='outlined'
            color='inherit'
            startIcon={<Cancel />}
            onClick={handleCancel}
            disabled={loading}
            sx={{
              borderColor: 'var(--cinza-medio)',
              color: 'var(--cinza-escuro)',
              '&:hover': {
                borderColor: 'var(--cinza-escuro)',
                backgroundColor: 'var(--cinza-claro)',
              },
            }}
          >
            Cancelar
          </Button>
        )}

        <Button
          type='submit'
          variant='contained'
          startIcon={
            loading ? (
              <CircularProgress size={20} color='inherit' />
            ) : usuarioEditando ? (
              <Save />
            ) : (
              <PersonAdd />
            )
          }
          disabled={loading}
          sx={{
            backgroundColor: 'var(--verde-esperanca)',
            color: 'var(--branco)',
            '&:hover': {
              backgroundColor: '#257247',
            },
          }}
        >
          {loading
            ? 'Salvando...'
            : usuarioEditando
              ? 'Atualizar Usuário'
              : 'Cadastrar Usuário'}
        </Button>
      </Box>
    </Box>
  );
}
