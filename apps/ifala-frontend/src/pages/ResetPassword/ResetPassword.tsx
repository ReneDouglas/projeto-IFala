import { useState, useEffect } from 'react';
import {
  Container,
  Paper,
  Typography,
  Box,
  TextField,
  Button,
  Stack,
  IconButton,
  Alert,
  CircularProgress,
} from '@mui/material';
import { useNavigate, useParams } from 'react-router-dom';
import * as authApi from '../../services/auth-api';
import '../../App.css';
import ifalaLogo from '../../assets/IFala-logo.png';

export function RedefinirSenha() {
  const { token } = useParams<{ token: string }>();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [validatingToken, setValidatingToken] = useState(true);
  const navigate = useNavigate();

  // Validar token ao carregar a página e buscar email
  useEffect(() => {
    const validarToken = async () => {
      // Se não houver token na URL, redirecionar para página 404
      if (!token) {
        navigate('/404', { replace: true });
        return;
      }

      try {
        // Buscar email associado ao token
        const emailDoToken = await authApi.validarTokenRedefinicao(token);
        setEmail(emailDoToken);
      } catch {
        // Token inválido ou expirado
        setError(
          'Token inválido ou expirado. Solicite um novo link de redefinição.',
        );
        setTimeout(() => {
          navigate('/login', { replace: true });
        }, 3000);
      } finally {
        setValidatingToken(false);
      }
    };

    validarToken();
  }, [token, navigate]);

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setError('');

    // Validação 1: Senhas não coincidem
    if (password !== confirmPassword) {
      setError('As senhas não coincidem.');
      return;
    }
    // Validação 2: Mínimo de 8 caracteres
    if (password.length < 8) {
      setError('A senha deve ter no mínimo 8 caracteres.');
      return;
    }
    // Validação 3: Deve conter letras e números (alfanumérica)
    const hasLetter = /[a-zA-Z]/.test(password);
    const hasNumber = /\d/.test(password);
    if (!hasLetter || !hasNumber) {
      setError('A senha deve conter pelo menos uma letra e um número.');
      return;
    }

    setLoading(true);

    try {
      await authApi.redefinirSenha({
        email,
        newPassword: password,
        token: token || undefined,
        currentPassword: null,
      });

      // Redirecionar para login após redefinir senha com sucesso
      alert('Senha redefinida com sucesso! Faça login com sua nova senha.');
      navigate('/login');
    } catch (err: unknown) {
      if (err && typeof err === 'object' && 'response' in err) {
        const axiosError = err as {
          response?: { data?: { message?: string }; status?: number };
        };

        // Tratar erros específicos
        if (axiosError.response?.status === 400) {
          setError(
            'Token inválido ou expirado. Solicite um novo link de redefinição.',
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

  // Exibir loader enquanto valida o token
  if (validatingToken) {
    return (
      <div
        className='hero'
        style={{
          minHeight: '100vh',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        }}
      >
        <Box
          sx={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            gap: 2,
          }}
        >
          <CircularProgress size={60} />
          <Typography variant='h6' color='white'>
            Validando token...
          </Typography>
        </Box>
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
          {/* Formulário de reset password */}
          <Paper
            elevation={10}
            sx={{
              p: 6,
              py: 8,
              width: '100%',
              borderRadius: '20px',
              backdropFilter: 'blur(10px)',
              backgroundColor: 'rgba(255, 255, 255, 0.98)',
              minHeight: '500px',
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'center',
              position: 'relative',
            }}
          >
            {/* Botão de voltar dentro do formulário */}
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
              aria-label='Voltar para a página de login'
            >
              <span
                className='material-symbols-outlined'
                style={{ fontSize: '35px', fontWeight: 'bold' }}
              >
                arrow_back
              </span>
            </IconButton>

            <Box sx={{ textAlign: 'center', mb: 4 }}>
              {/* Logo IFala */}
              <Box sx={{ mb: 3 }}>
                <img
                  src={ifalaLogo}
                  alt='IFala Logo'
                  style={{
                    width: '140px',
                    height: 'auto',
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
                  sx={{
                    color: 'var(--cinza-medio)',
                    mb: 2,
                  }}
                >
                  Defina sua nova senha
                </Typography>
              </Box>

              {/* Mensagem de erro */}
              {error && (
                <Box sx={{ mb: 3 }}>
                  <Alert severity='error' sx={{ borderRadius: '8px' }}>
                    {error}
                  </Alert>
                </Box>
              )}

              {/* Formulário */}
              <Box
                component='form'
                onSubmit={handleSubmit}
                sx={{ textAlign: 'left' }}
              >
                <Stack spacing={3}>
                  {/* Campo Email (desabilitado) */}
                  <TextField
                    fullWidth
                    name='email'
                    label='Email'
                    type='email'
                    value={email}
                    disabled
                    sx={{
                      '& .MuiOutlinedInput-root': {
                        borderRadius: '12px',
                        backgroundColor: 'rgba(0, 0, 0, 0.05)',
                        '& fieldset': {
                          borderColor: 'rgba(0, 0, 0, 0.15)',
                        },
                      },
                      '& .MuiInputLabel-root': {
                        color: 'rgba(0, 0, 0, 0.6)',
                      },
                    }}
                  />

                  {/* Campo Nova Senha */}
                  <TextField
                    fullWidth
                    name='password'
                    label='Nova Senha'
                    type='password'
                    placeholder='Digite sua nova senha'
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                    sx={{
                      '& .MuiOutlinedInput-root': {
                        borderRadius: '12px',
                        backgroundColor: 'rgba(255, 255, 255, 0.8)',
                        '& fieldset': {
                          borderColor: 'rgba(0, 0, 0, 0.2)',
                        },
                        '&:hover fieldset': {
                          borderColor: 'var(--verde-esperanca)',
                        },
                        '&.Mui-focused fieldset': {
                          borderColor: 'var(--azul-confianca)',
                        },
                      },
                      '& .MuiInputLabel-root.Mui-focused': {
                        color: 'var(--azul-confianca)',
                      },
                    }}
                  />

                  {/* Campo Confirmar Senha */}
                  <TextField
                    fullWidth
                    name='confirmPassword'
                    label='Confirme a Nova Senha'
                    type='password'
                    placeholder='Confirme sua nova senha'
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    required
                    sx={{
                      '& .MuiOutlinedInput-root': {
                        borderRadius: '12px',
                        backgroundColor: 'rgba(255, 255, 255, 0.8)',
                        '& fieldset': {
                          borderColor: 'rgba(0, 0, 0, 0.2)',
                        },
                        '&:hover fieldset': {
                          borderColor: 'var(--verde-esperanca)',
                        },
                        '&.Mui-focused fieldset': {
                          borderColor: 'var(--azul-confianca)',
                        },
                      },
                      '& .MuiInputLabel-root.Mui-focused': {
                        color: 'var(--azul-confianca)',
                      },
                    }}
                  />

                  {/* Botão Redefinir */}
                  <Button
                    type='submit'
                    fullWidth
                    variant='contained'
                    size='large'
                    disabled={loading}
                    sx={{
                      borderRadius: '12px',
                      py: 2,
                      fontSize: '1.1rem',
                      fontWeight: 600,
                      textTransform: 'none',
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
                      mt: 4,
                    }}
                  >
                    {loading ? 'Redefinindo...' : 'Redefinir Senha'}
                  </Button>
                </Stack>
              </Box>
            </Box>
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
              Suas informações estão protegidas e criptografadas.
            </Typography>
          </Paper>
        </Box>
      </Container>
    </div>
  );
}
