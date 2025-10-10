import { useState } from 'react';
import {
  Container,
  Paper,
  Typography,
  Box,
  TextField,
  Button,
  Stack,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';

export function ResetPassword() {
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    setError('');

    // --- VALIDAÇÕES ESPECIFICADAS ---
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
    console.log('Simulando redefinição de senha com a nova senha:', password);
    alert('Senha redefinida com sucesso!');
    navigate('/'); // Simula o retorno para a página inicial
  };

  return (
    <Container component='main' maxWidth='sm' sx={{ mt: 8 }}>
      <Paper elevation={3} sx={{ p: 4, borderRadius: 3, textAlign: 'center' }}>
        <Typography
          component='h1'
          variant='h4'
          sx={{ fontWeight: 'bold', mb: 1 }}
        >
          Redefinir Senha
        </Typography>
        <Typography color='text.secondary' sx={{ mb: 3 }}>
          Digite sua nova senha abaixo.
        </Typography>

        <Box component='form' onSubmit={handleSubmit} noValidate>
          <Stack spacing={2}>
            <TextField
              required
              fullWidth
              name='password'
              label='Nova Senha'
              type='password'
              id='password'
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            <TextField
              required
              fullWidth
              name='confirmPassword'
              label='Confirme a Nova Senha'
              type='password'
              id='confirmPassword'
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
            />
            {error && (
              <Typography color='error' variant='body2' sx={{ mt: 1 }}>
                {error}
              </Typography>
            )}
            <Button
              type='submit'
              fullWidth
              variant='contained'
              sx={{ mt: 3, mb: 2, py: 1.5, fontWeight: 'bold' }}
            >
              Redefinir Senha
            </Button>
          </Stack>
        </Box>
      </Paper>
    </Container>
  );
}
