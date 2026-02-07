import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import * as authApi from '../../services/auth-api';
import {
  Container,
  Paper,
  TextField,
  Button,
  Typography,
  Box,
  Alert,
  CircularProgress,
  InputAdornment,
  IconButton,
} from '@mui/material';
import {
  Visibility,
  VisibilityOff,
  Lock,
  LockReset,
} from '@mui/icons-material';
import ifalaLogo from '../../assets/IFala-logo.png';
import '../../App.css';

export function RedefinirSenha() {
  const { token } = useParams<{ token: string }>();
  const navigate = useNavigate();

  // Estados
  const [email, setEmail] = useState('');
  const [novaSenha, setNovaSenha] = useState('');
  const [confirmarSenha, setConfirmarSenha] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [validating, setValidating] = useState(true);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [tokenInvalido, setTokenInvalido] = useState(false);

  // Erros de validação
  const [errors, setErrors] = useState({
    novaSenha: '',
    confirmarSenha: '',
  });

  // Validar token ao carregar a página
  useEffect(() => {
    const validarToken = async () => {
      if (!token) {
        setTokenInvalido(true);
        setError('Token de redefinição não encontrado.');
        setValidating(false);
        return;
      }

      try {
        const emailUsuario = await authApi.validarTokenRedefinicao(token);
        setEmail(emailUsuario);
        setValidating(false);
      } catch (err: unknown) {
        setTokenInvalido(true);
        setValidating(false);

        if (err && typeof err === 'object' && 'response' in err) {
          const axiosError = err as {
            response?: { data?: { message?: string }; status?: number };
          };
          if (axiosError.response?.status === 401) {
            setError(
              'Token inválido ou expirado. Solicite uma nova redefinição de senha.',
            );
          } else {
            setError(
              axiosError.response?.data?.message ||
                'Erro ao validar token. Tente solicitar uma nova redefinição.',
            );
          }
        } else {
          setError(
            'Erro ao validar token. Tente solicitar uma nova redefinição.',
          );
        }
      }
    };

    validarToken();
  }, [token]);

  // Validação dos campos
  const validateField = (name: string, value: string) => {
    switch (name) {
      case 'novaSenha':
        if (!value) return 'Nova senha é obrigatória';
        if (value.length < 6) return 'Senha deve ter pelo menos 6 caracteres';
        return '';
      case 'confirmarSenha':
        if (!value) return 'Confirmação de senha é obrigatória';
        if (value !== novaSenha) return 'As senhas não coincidem';
        return '';
      default:
        return '';
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;

    if (name === 'novaSenha') {
      setNovaSenha(value);
      setErrors((prev) => ({
        ...prev,
        novaSenha: validateField('novaSenha', value),
        // Revalidar confirmação se já tiver valor
        ...(confirmarSenha
          ? {
              confirmarSenha:
                value !== confirmarSenha ? 'As senhas não coincidem' : '',
            }
          : {}),
      }));
    } else if (name === 'confirmarSenha') {
      setConfirmarSenha(value);
      setErrors((prev) => ({
        ...prev,
        confirmarSenha: validateField('confirmarSenha', value),
      }));
    }

    if (error) setError('');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Validar todos os campos
    const newErrors = {
      novaSenha: validateField('novaSenha', novaSenha),
      confirmarSenha: validateField('confirmarSenha', confirmarSenha),
    };
    setErrors(newErrors);

    if (Object.values(newErrors).some((err) => err !== '')) return;

    setLoading(true);
    setError('');

    try {
      await authApi.redefinirSenha({
        email,
        newPassword: novaSenha,
        token: token || '',
      });

      setSuccessMessage(
        'Senha redefinida com sucesso! Você será redirecionado para o login.',
      );

      // Redirecionar para login após 3 segundos
      setTimeout(() => {
        navigate('/login', { replace: true });
      }, 3000);
    } catch (err: unknown) {
      if (err && typeof err === 'object' && 'response' in err) {
        const axiosError = err as {
          response?: { data?: { message?: string }; status?: number };
        };
        if (axiosError.response?.status === 401) {
          setError(
            'Token inválido ou expirado. Solicite uma nova redefinição de senha.',
          );
        } else {
          setError(
            axiosError.response?.data?.message ||
              'Erro ao redefinir senha. Tente novamente.',
          );
        }
      } else {
        setError('Erro ao redefinir senha. Tente novamente.');
      }
    } finally {
      setLoading(false);
    }
  };

  // Tela de carregamento enquanto valida o token
  if (validating) {
    return (
      <div
        className='hero'
        style={{ minHeight: '100vh', display: 'flex', alignItems: 'center' }}
      >
        <Container maxWidth='sm'>
          <Box
            sx={{
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              gap: 3,
            }}
          >
            <Paper
              elevation={10}
              sx={{
                p: 6,
                width: '100%',
                borderRadius: '20px',
                backdropFilter: 'blur(10px)',
                backgroundColor: 'rgba(255, 255, 255, 0.98)',
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                minHeight: '300px',
              }}
            >
              <CircularProgress
                size={48}
                sx={{ color: 'var(--verde-esperanca)', mb: 3 }}
              />
              <Typography variant='h6' sx={{ color: 'var(--cinza-escuro)' }}>
                Validando token de redefinição...
              </Typography>
            </Paper>
          </Box>
        </Container>
      </div>
    );
  }

  return (
    <div
      className='hero'
      style={{ minHeight: '100vh', display: 'flex', alignItems: 'center' }}
    >
      <Container maxWidth='sm'>
        <Box
          sx={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            gap: 3,
          }}
        >
          <Paper
            elevation={10}
            sx={{
              p: 6,
              py: 8,
              width: '100%',
              borderRadius: '20px',
              backdropFilter: 'blur(10px)',
              backgroundColor: 'rgba(255, 255, 255, 0.98)',
              minHeight: '400px',
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'center',
              position: 'relative',
            }}
          >
            {/* Botão de voltar */}
            <IconButton
              onClick={() => navigate('/login')}
              sx={{
                position: 'absolute',
                top: 26,
                left: 16,
                backgroundColor: 'rgba(0, 0, 0, 0.05)',
                color: 'var(--azul-confianca)',
                width: '55px',
                height: '55px',
                '&:hover': {
                  backgroundColor: 'rgba(0, 0, 0, 0.1)',
                  transform: 'translateY(-2px)',
                  boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)',
                },
                transition: 'all 0.3s ease',
                zIndex: 1,
              }}
              aria-label='Voltar para login'
            >
              <span
                className='material-symbols-outlined'
                style={{ fontSize: '35px', fontWeight: 'bold' }}
              >
                arrow_back
              </span>
            </IconButton>

            {/* Logo e título */}
            <Box sx={{ textAlign: 'center', mb: 4 }}>
              <img
                src={ifalaLogo}
                alt='Logo IFala'
                style={{
                  height: '80px',
                  marginBottom: '16px',
                  filter: 'drop-shadow(0 2px 8px rgba(0, 0, 0, 0.1))',
                }}
              />
              <Typography
                variant='h4'
                component='h1'
                sx={{
                  color: 'var(--azul-confianca)',
                  fontWeight: 800,
                  mb: 1,
                }}
              >
                Redefinir Senha
              </Typography>
              <Typography
                variant='subtitle1'
                sx={{ color: 'var(--cinza-medio)', mb: 1 }}
              >
                {tokenInvalido
                  ? 'Não foi possível validar seu token'
                  : `Digite sua nova senha para ${email}`}
              </Typography>
            </Box>

            {/* Mensagem de erro */}
            {error && (
              <Alert severity='error' sx={{ mb: 3, borderRadius: '12px' }}>
                {error}
              </Alert>
            )}

            {/* Mensagem de sucesso */}
            {successMessage && (
              <Alert severity='success' sx={{ mb: 3, borderRadius: '12px' }}>
                {successMessage}
              </Alert>
            )}

            {/* Token inválido - mostrar botão para voltar ao login */}
            {tokenInvalido && !successMessage && (
              <Box sx={{ textAlign: 'center', mt: 2 }}>
                <Button
                  variant='contained'
                  onClick={() => navigate('/login')}
                  sx={{
                    py: 1.5,
                    px: 4,
                    fontSize: '1rem',
                    fontWeight: 600,
                    borderRadius: '12px',
                    background:
                      'linear-gradient(135deg, var(--verde-esperanca) 0%, var(--azul-confianca) 100%)',
                    '&:hover': {
                      background:
                        'linear-gradient(135deg, var(--azul-confianca) 0%, var(--verde-esperanca) 100%)',
                      transform: 'translateY(-2px)',
                      boxShadow: '0 8px 25px rgba(0, 0, 0, 0.2)',
                    },
                    transition: 'all 0.3s ease',
                  }}
                >
                  Voltar ao Login
                </Button>
              </Box>
            )}

            {/* Formulário de redefinição */}
            {!tokenInvalido && !successMessage && (
              <Box
                component='form'
                onSubmit={handleSubmit}
                sx={{ width: '100%' }}
              >
                {/* Campo Nova Senha */}
                <TextField
                  fullWidth
                  name='novaSenha'
                  label='Nova Senha'
                  type={showPassword ? 'text' : 'password'}
                  placeholder='Digite sua nova senha'
                  value={novaSenha}
                  onChange={handleInputChange}
                  error={!!errors.novaSenha}
                  helperText={errors.novaSenha}
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position='start'>
                        <Lock color='primary' />
                      </InputAdornment>
                    ),
                    endAdornment: (
                      <InputAdornment position='end'>
                        <IconButton
                          onClick={() => setShowPassword(!showPassword)}
                          edge='end'
                          aria-label='toggle password visibility'
                        >
                          {showPassword ? <VisibilityOff /> : <Visibility />}
                        </IconButton>
                      </InputAdornment>
                    ),
                  }}
                  sx={{
                    mb: 4,
                    '& .MuiOutlinedInput-root': {
                      borderRadius: '12px',
                      height: '56px',
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
                  }}
                />

                {/* Campo Confirmar Senha */}
                <TextField
                  fullWidth
                  name='confirmarSenha'
                  label='Confirmar Nova Senha'
                  type={showConfirmPassword ? 'text' : 'password'}
                  placeholder='Confirme sua nova senha'
                  value={confirmarSenha}
                  onChange={handleInputChange}
                  error={!!errors.confirmarSenha}
                  helperText={errors.confirmarSenha}
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position='start'>
                        <Lock color='primary' />
                      </InputAdornment>
                    ),
                    endAdornment: (
                      <InputAdornment position='end'>
                        <IconButton
                          onClick={() =>
                            setShowConfirmPassword(!showConfirmPassword)
                          }
                          edge='end'
                          aria-label='toggle confirm password visibility'
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
                  sx={{
                    mb: 4,
                    '& .MuiOutlinedInput-root': {
                      borderRadius: '12px',
                      height: '56px',
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
                  }}
                />

                {/* Botão de redefinir */}
                <Button
                  type='submit'
                  fullWidth
                  variant='contained'
                  disabled={loading}
                  startIcon={
                    loading ? (
                      <CircularProgress size={20} color='inherit' />
                    ) : (
                      <LockReset />
                    )
                  }
                  sx={{
                    py: 2,
                    fontSize: '1.1rem',
                    fontWeight: 600,
                    borderRadius: '12px',
                    height: '56px',
                    background:
                      'linear-gradient(135deg, var(--verde-esperanca) 0%, var(--azul-confianca) 100%)',
                    '&:hover': {
                      background:
                        'linear-gradient(135deg, var(--azul-confianca) 0%, var(--verde-esperanca) 100%)',
                      transform: 'translateY(-2px)',
                      boxShadow: '0 8px 25px rgba(0, 0, 0, 0.2)',
                    },
                    '&:disabled': {
                      background: 'rgba(0, 0, 0, 0.12)',
                    },
                    transition: 'all 0.3s ease',
                  }}
                >
                  {loading ? 'Redefinindo...' : 'Redefinir Senha'}
                </Button>
              </Box>
            )}
          </Paper>

          {/* Informações de segurança */}
          <Paper
            sx={{
              p: 2,
              width: '100%',
              borderRadius: '12px',
              backgroundColor: 'rgba(255, 255, 255, 0.1)',
              backdropFilter: 'blur(10px)',
              border: '1px solid rgba(255, 255, 255, 0.2)',
            }}
          >
            <Typography
              variant='caption'
              sx={{
                color: 'rgba(255, 255, 255, 0.9)',
                textAlign: 'center',
                display: 'block',
                lineHeight: 1.4,
              }}
            >
              Esta área utiliza conexão segura HTTPS.
              <br />
              Suas credenciais são protegidas por criptografia.
            </Typography>
          </Paper>
        </Box>
      </Container>
    </div>
  );
}
