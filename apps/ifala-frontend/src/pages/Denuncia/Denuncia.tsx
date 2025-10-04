import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Typography,
  Box,
  TextField,
  FormControlLabel,
  Checkbox,
  Button,
  Stack,
  Paper,
  FormHelperText,
} from '@mui/material';
import ReCAPTCHA from 'react-google-recaptcha';

export function Denuncia() {
  const [identificado, setIdentificado] = useState(false);
  const [recaptchaToken, setRecaptchaToken] = useState<string | null>(null);
  const [recaptchaError, setRecaptchaError] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();

    if (!recaptchaToken) {
      setRecaptchaError(true);
      return;
    }

    setRecaptchaError(false);
    const fakeTrackingToken = `IFALA-${Math.random().toString(36).substring(2, 11).toUpperCase()}`;
    navigate('/denuncia/sucesso', { state: { token: fakeTrackingToken } });
  };

  const handleRecaptchaChange = (token: string | null) => {
    setRecaptchaToken(token);
    if (token) {
      setRecaptchaError(false);
    }
  };

  return (
    <Box sx={{ backgroundColor: '#f4f6f8', minHeight: '100vh', py: 5 }}>
      <Container maxWidth='md'>
        <Paper
          component='form'
          noValidate
          autoComplete='off'
          onSubmit={handleSubmit}
          elevation={3}
          sx={{ p: 4, borderRadius: 3 }}
        >
          <Box sx={{ textAlign: 'center', mb: 3 }}>
            <Typography
              variant='h4'
              component='h1'
              gutterBottom
              sx={{ fontWeight: 'bold' }}
            >
              Canal de Denúncias
            </Typography>
            <Typography variant='body1' color='text.secondary'>
              Este é um espaço seguro e confidencial para relatar quaisquer
              irregularidades. Sua identidade, caso fornecida, será mantida em
              sigilo.
            </Typography>
          </Box>

          <Stack spacing={3}>
            <FormControlLabel
              control={
                <Checkbox
                  checked={identificado}
                  onChange={(e) => setIdentificado(e.target.checked)}
                />
              }
              label='Desejo me identificar (Opcional)'
            />
            {identificado && (
              <>
                <TextField
                  name='nome'
                  label='Seu nome completo'
                  variant='outlined'
                  fullWidth
                />
                <TextField
                  name='email'
                  label='Seu e-mail para contato'
                  variant='outlined'
                  fullWidth
                />
              </>
            )}
            <TextField
              name='relato'
              label='Relato da Denúncia'
              placeholder='Descreva detalhadamente o ocorrido...'
              variant='outlined'
              fullWidth
              multiline
              rows={10}
              required
            />
            <Box
              sx={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                my: 1,
              }}
            >
              <ReCAPTCHA
                sitekey='6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI'
                onChange={handleRecaptchaChange}
              />
              {recaptchaError && (
                <FormHelperText error sx={{ mt: 1 }}>
                  Por favor, complete o desafio "Não sou um robô".
                </FormHelperText>
              )}
            </Box>

            <Stack direction='row' spacing={2}>
              <Button
                variant='outlined'
                color='primary'
                size='large'
                fullWidth
                sx={{ py: 1.5 }}
                onClick={() => navigate(-1)}
              >
                Voltar
              </Button>
              <Button
                variant='contained'
                color='primary'
                size='large'
                type='submit'
                fullWidth
                sx={{ py: 1.5, fontWeight: 'bold' }}
              >
                Enviar Denúncia
              </Button>
            </Stack>
          </Stack>
        </Paper>
      </Container>
    </Box>
  );
}
