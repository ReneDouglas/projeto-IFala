import { useState } from 'react';
import {
  Container,
  Paper,
  Typography,
  Box,
  TextField,
  Button,
  Stack,
  IconButton,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import '../../App.css';
import ifalaLogo from '../../assets/IFala-logo.png';

export function ResetPassword() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    setError('');

    // --- VALIDAÇÕES ESPECIFICADAS ---
    // Validação 0: Email deve ser válido
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!email || !emailRegex.test(email)) {
      setError('Por favor, insira um email válido.');
      return;
    }
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

    // caso todas as validações passem
    console.log(
      'Simulando redefinição de senha para o email:',
      email,
      'com nova senha:',
      password,
    );
    alert('Senha redefinida com sucesso!');
    navigate('/'); // Simula o retorno para a página inicial
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
                  Digite seu email e defina uma nova senha
                </Typography>
              </Box>

              {/* Mensagem de erro */}
              {error && (
                <Box sx={{ mb: 3 }}>
                  <Typography
                    color='error'
                    variant='body2'
                    sx={{
                      backgroundColor: 'rgba(211, 47, 47, 0.1)',
                      border: '1px solid rgba(211, 47, 47, 0.3)',
                      borderRadius: '8px',
                      p: 2,
                      fontSize: '0.9rem',
                    }}
                  >
                    {error}
                  </Typography>
                </Box>
              )}

              {/* Formulário */}
              <Box
                component='form'
                onSubmit={handleSubmit}
                sx={{ textAlign: 'left' }}
              >
                <Stack spacing={3}>
                  {/* Campo Email */}
                  <TextField
                    fullWidth
                    name='email'
                    label='Email'
                    type='email'
                    placeholder='Digite seu email'
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
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
                      transition: 'all 0.3s ease',
                      mt: 4,
                    }}
                  >
                    Redefinir Senha
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
