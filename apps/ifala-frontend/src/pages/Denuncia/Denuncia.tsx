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
  // Estado para os dados do formulário
  const [identificado, setIdentificado] = useState(false);
  const [nome, setNome] = useState('');
  const [email, setEmail] = useState('');
  const [relato, setRelato] = useState('');

  // Estado para o reCAPTCHA
  const [recaptchaToken, setRecaptchaToken] = useState<string | null>(null);

  // Estado para controlar os erros de validação
  const [errors, setErrors] = useState({
    nome: false,
    email: false,
    relato: false,
    recaptcha: false,
  });

  const navigate = useNavigate();

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();

    // ----- LÓGICA DE VALIDAÇÃO -----
    const newErrors = {
      // Valida o nome SOMENTE SE o usuário escolheu se identificar
      nome: identificado && nome.trim() === '',
      // Valida o email SOMENTE SE o usuário escolheu se identificar
      email: identificado && email.trim() === '',
      // Valida o relato sempre
      relato: relato.trim() === '',
      // Valida o reCAPTCHA sempre
      recaptcha: !recaptchaToken,
    };

    setErrors(newErrors);

    // Verifica se há algum erro no objeto 'newErrors'
    const hasErrors = Object.values(newErrors).some((error) => error === true);

    if (hasErrors) {
      console.log('Formulário com erros, envio bloqueado.');
      return; // Para a execução se houver erros
    }
    // ----- FIM DA VALIDAÇÃO -----

    // Casi passe por todas as validações, simula o envio
    const fakeTrackingToken = `IFALA-${Math.random().toString(36).substring(2, 11).toUpperCase()}`;
    navigate('/denuncia/sucesso', { state: { token: fakeTrackingToken } });
  };

  const handleRecaptchaChange = (token: string | null) => {
    setRecaptchaToken(token);
    if (token) {
      setErrors((prevErrors) => ({ ...prevErrors, recaptcha: false }));
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
              Este é um espaço seguro e confidencial.
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
                  required
                  value={nome}
                  onChange={(e) => setNome(e.target.value)}
                  error={errors.nome}
                  helperText={errors.nome ? 'O nome é obrigatório.' : ''}
                />
                <TextField
                  name='email'
                  label='Seu e-mail para contato'
                  variant='outlined'
                  fullWidth
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  error={errors.email}
                  helperText={errors.email ? 'O e-mail é obrigatório.' : ''}
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
              value={relato}
              onChange={(e) => setRelato(e.target.value)}
              error={errors.relato}
              helperText={
                errors.relato ? 'O relato da denúncia é obrigatório.' : ''
              }
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
              {errors.recaptcha && (
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
