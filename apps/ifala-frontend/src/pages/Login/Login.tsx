import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Paper,
  TextField,
  Button,
  Typography,
  Box,
  FormControlLabel,
  Checkbox,
  Link,
  Alert,
  CircularProgress,
  InputAdornment,
  IconButton,
} from '@mui/material';
import {
  Visibility,
  VisibilityOff,
  Login as LoginIcon,
  School,
  Lock,
} from '@mui/icons-material';
import ifalaLogo from '../../assets/IFala-logo.png';
import '../../App.css';

declare global {
  interface Window {
    grecaptcha: {
      ready: (callback: () => void) => void;
      execute: (
        siteKey: string,
        options: { action: string },
      ) => Promise<string>;
    };
  }
}

export function Login() {
  const navigate = useNavigate();

  // Estados do formulário
  const [formData, setFormData] = useState({
    matricula: '',
    senha: '',
  });

  // Estados de UI
  const [showPassword, setShowPassword] = useState(false);
  const [rememberMe, setRememberMe] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Estados de validação
  const [errors, setErrors] = useState({
    matricula: '',
    senha: '',
  });

  // Função para validar campos
  const validateField = (name: string, value: string) => {
    switch (name) {
      case 'matricula': {
        if (!value.trim()) {
          return 'Usuário ou email é obrigatório';
        }
        // Validação flexível: aceita usuário (texto), matrícula (números) ou email
        const matriculaRegex = /^\d{10,}$/; // Matrícula: pelo menos 10 dígitos
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/; // Email válido
        const usuarioRegex = /^[a-zA-Z0-9_]{3,}$/; // Usuário: letras, números, underscore, min 3 chars

        if (
          !matriculaRegex.test(value) &&
          !emailRegex.test(value) &&
          !usuarioRegex.test(value)
        ) {
          return 'Digite um usuário válido (letras/números), matrícula (números) ou email válido';
        }
        return '';
      }

      case 'senha':
        if (!value) {
          return 'Senha é obrigatória';
        }
        if (value.length < 6) {
          return 'Senha deve ter pelo menos 6 caracteres';
        }
        return '';

      default:
        return '';
    }
  };

  // Manipulador de mudança nos campos
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;

    // Atualiza o valor
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));

    // Valida o campo em tempo real
    const fieldError = validateField(name, value);
    setErrors((prev) => ({
      ...prev,
      [name]: fieldError,
    }));

    // Remove erro geral se existir
    if (error) {
      setError('');
    }
  };

  // Manipulador do toggle de mostrar/ocultar senha
  const handleTogglePassword = () => {
    setShowPassword(!showPassword);
  };

  // Validação completa do formulário
  const validateForm = () => {
    const newErrors = {
      matricula: validateField('matricula', formData.matricula),
      senha: validateField('senha', formData.senha),
    };

    setErrors(newErrors);

    return !Object.values(newErrors).some((error) => error !== '');
  };

  // Manipulador do submit do formulário
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setLoading(true);
    setError('');

    if (!window.grecaptcha) {
      setError('Erro ao carregar o reCAPTCHA. Por favor, recarregue a página.');
      setLoading(false);
      return;
    }

    window.grecaptcha.ready(() => {
      window.grecaptcha
        .execute(import.meta.env.VITE_RECAPTCHA_SITE_KEY, { action: 'login' })
        .then(async (token) => {
          if (!token) {
            setError('Falha ao obter o token do reCAPTCHA. Tente novamente.');
            setLoading(false);
            return;
          }

          const loginData = {
            ...formData,
            recaptchaToken: token,
          };

          try {
            await new Promise((resolve) => setTimeout(resolve, 2000));

            console.log('Dados que seriam enviados para a API:', loginData);

            setError(
              'Credenciais inválidas. Integração com API de autenticação pendente.',
            );
          } catch {
            setError(
              'Erro ao fazer login. Verifique suas credenciais e tente novamente.',
            );
          } finally {
            setLoading(false);
          }
        });
    });
  };

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
          {/* Formulário de login */}
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
              onClick={() => navigate('/')}
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
              aria-label='Voltar para a página inicial'
            >
              <span
                className='material-symbols-outlined'
                style={{ fontSize: '35px', fontWeight: 'bold' }}
              >
                arrow_back
              </span>
            </IconButton>

            <Box
              component='form'
              onSubmit={handleSubmit}
              sx={{ width: '100%' }}
            >
              {/* Logo e título dentro do formulário */}
              <Box
                sx={{
                  textAlign: 'center',
                  mb: 4,
                }}
              >
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
                  Acesso ao Sistema
                </Typography>
                <Typography
                  variant='subtitle1'
                  sx={{
                    color: 'var(--cinza-medio)',
                    mb: 2,
                  }}
                >
                  Entre com seu usuário e senha
                </Typography>
              </Box>

              {/* Mensagem de erro geral */}
              {error && (
                <Alert severity='error' sx={{ mb: 3, borderRadius: '12px' }}>
                  {error}
                </Alert>
              )}

              {/* Campo de Usuário/Email */}
              <TextField
                fullWidth
                name='matricula'
                label='Usuário ou Email Institucional'
                placeholder='Digite su usuário ou email institucional'
                value={formData.matricula}
                onChange={handleInputChange}
                error={!!errors.matricula}
                helperText={errors.matricula}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position='start'>
                      <School color='primary' />
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

              {/* Campo de Senha */}
              <TextField
                fullWidth
                name='senha'
                label='Senha'
                type={showPassword ? 'text' : 'password'}
                placeholder='Digite sua senha'
                value={formData.senha}
                onChange={handleInputChange}
                error={!!errors.senha}
                helperText={errors.senha}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position='start'>
                      <Lock color='primary' />
                    </InputAdornment>
                  ),
                  endAdornment: (
                    <InputAdornment position='end'>
                      <IconButton
                        onClick={handleTogglePassword}
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

              {/* Checkbox "Lembrar de mim" */}
              <FormControlLabel
                control={
                  <Checkbox
                    checked={rememberMe}
                    onChange={(e) => setRememberMe(e.target.checked)}
                    sx={{
                      color: 'var(--verde-esperanca)',
                      '&.Mui-checked': {
                        color: 'var(--verde-esperanca)',
                      },
                    }}
                  />
                }
                label='Lembrar de mim'
                sx={{ mb: 4, alignSelf: 'flex-start' }}
              />

              {/* Botão de login */}
              <Button
                type='submit'
                fullWidth
                variant='contained'
                disabled={loading}
                startIcon={
                  loading ? (
                    <CircularProgress size={20} color='inherit' />
                  ) : (
                    <LoginIcon />
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
                  mb: 4,
                }}
              >
                {loading ? 'Entrando...' : 'Entrar'}
              </Button>

              {/* Links auxiliares */}
              <Box
                sx={{
                  textAlign: 'center',
                  display: 'flex',
                  flexDirection: 'column',
                  gap: 2,
                  mt: 2,
                }}
              >
                <Link
                  component='button'
                  type='button'
                  underline='hover'
                  onClick={() => navigate('/reset-password')}
                  sx={{
                    color: 'var(--azul-confianca)',
                    fontWeight: 500,
                    fontSize: '1rem',
                    background: 'none',
                    border: 'none',
                    cursor: 'pointer',
                    boxShadow: 'none',
                    outline: 'none',
                    padding: 0,
                    margin: 0,
                    textDecoration: 'none',
                    '&:hover': {
                      color: 'var(--verde-esperanca)',
                    },
                    '&:focus': {
                      outline: '2px solid var(--azul-confianca)',
                      outlineOffset: '2px',
                      borderRadius: '4px',
                    },
                    '&:active': {
                      transform: 'none',
                      boxShadow: 'none',
                    },
                  }}
                >
                  Esqueci minha senha
                </Link>
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
              Suas credenciais são protegidas por criptografia.
            </Typography>
          </Paper>
        </Box>
      </Container>
    </div>
  );
}
