import { useLocation } from 'react-router-dom';
import { Container, Typography, Box, Alert } from '@mui/material';

export function DenunciaSucesso() {
  const location = useLocation();
  // Placeholder para simular pagina de sucesso
  const token = location.state?.token;

  return (
    <Container maxWidth='md' sx={{ mt: 4, mb: 4 }}>
      <Box
        sx={{
          p: 3,
          border: '2px dashed #ccc',
          borderRadius: 2,
          textAlign: 'center',
        }}
      >
        <Typography variant='h5' component='h1' gutterBottom>
          Futura Página de Sucesso de Cadastro da Denúncia
        </Typography>
        <Typography variant='body1' color='text.secondary'>
          (Esta tela será implementada pela task #17)
        </Typography>
        {token && (
          <Alert severity='info' sx={{ mt: 3, textAlign: 'left' }}>
            <strong>Token recebido para teste:</strong> {token}
          </Alert>
        )}
      </Box>
    </Container>
  );
}
