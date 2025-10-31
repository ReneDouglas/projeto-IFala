import { useLocation, useNavigate } from 'react-router-dom';
import {
  Container,
  Typography,
  Box,
  Alert,
  Button,
  Paper,
  Snackbar,
  Stack,
} from '@mui/material';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import { useEffect, useState, useRef } from 'react';

export function DenunciaSucesso() {
  const location = useLocation();
  const navigate = useNavigate();
  const token = location.state?.token as string | undefined;
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const tokenRef = useRef<HTMLDivElement | null>(null);

  // Segurança: apos recarregar, voltar para a página inicial
  useEffect(() => {
    if (!token) {
      navigate('/');
    }
  }, [token, navigate]);

  const copyToken = async () => {
    if (!token) return;
    try {
      await navigator.clipboard.writeText(token);
      setSnackbarOpen(true);
    } catch {
      if (tokenRef.current) {
        const range = document.createRange();
        range.selectNodeContents(tokenRef.current);
        const sel = window.getSelection();
        sel?.removeAllRanges();
        sel?.addRange(range);
        document.execCommand('copy');
        sel?.removeAllRanges();
        setSnackbarOpen(true);
      }
    }
  };

  return (
    <Container maxWidth='md' sx={{ mt: 6, mb: 6 }}>
      <Paper elevation={3} sx={{ p: 4, position: 'relative' }}>
        <Box sx={{ display: 'flex', justifyContent: 'center', mb: 2 }}>
          <Box
            sx={{
              width: 72,
              height: 72,
              borderRadius: '50%',
              bgcolor: 'success.light',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}
          >
            <CheckCircleOutlineIcon
              sx={{ fontSize: 40, color: 'success.main' }}
            />
          </Box>
        </Box>

        <Typography
          variant='h5'
          component='h1'
          align='center'
          color='success.main'
          gutterBottom
        >
          Denúncia Recebida com Sucesso
        </Typography>
        <Typography
          variant='body1'
          align='center'
          color='text.secondary'
          sx={{ mb: 3 }}
        >
          Sua denúncia foi registrada e será analisada em breve
        </Typography>

        {/* Token box */}
        {token && (
          <Box
            sx={{ bgcolor: 'success.lighter', p: 3, borderRadius: 2, mb: 2 }}
          >
            <Typography
              variant='subtitle1'
              align='center'
              sx={{ fontWeight: 600 }}
            >
              Token de Acompanhamento
            </Typography>

            <Box
              onClick={copyToken}
              sx={{
                mt: 2,
                p: 2,
                border: '2px dashed rgba(0,0,0,0.12)',
                borderRadius: 1,
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: 1,
                userSelect: 'none',
                transition: 'all 180ms ease',
                '&:hover': {
                  boxShadow: '0 6px 18px rgba(0,0,0,0.08)',
                  borderColor: 'rgba(0,0,0,0.18)',
                  transform: 'translateY(-2px)',
                },
              }}
              aria-label='Token de acompanhamento (clique para copiar)'
            >
              <Typography
                ref={tokenRef}
                variant='h6'
                sx={{ letterSpacing: 2 }}
                data-testid='token-text'
              >
                {token}
              </Typography>
            </Box>

            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
              <Button
                variant='outlined'
                startIcon={<ContentCopyIcon />}
                onClick={copyToken}
                sx={{
                  borderColor: 'success.main',
                  color: 'success.main',
                  width: '60%',
                  '&:hover': {
                    bgcolor: 'success.main',
                    color: '#fff',
                    transform: 'translateY(-2px)',
                  },
                  transition: 'all 180ms ease',
                }}
              >
                Copiar Token
              </Button>
            </Box>
          </Box>
        )}

        <Alert
          severity='error'
          sx={{ bgcolor: '#fdecea', color: '#7a1919', mb: 3 }}
        >
          <strong>Importante - Guarde este Token!</strong>
          <ul style={{ marginTop: 8, marginBottom: 0 }}>
            <li>Este é o único código para acompanhar sua denúncia</li>
            <li>Anote-o em um lugar seguro</li>
            <li>Não compartilhe com outras pessoas</li>
            <li>Não será possível recuperá-lo se for perdido</li>
          </ul>
        </Alert>

        {/* Actions */}
        <Stack
          direction={{ xs: 'column', sm: 'row' }}
          spacing={2}
          justifyContent='center'
          alignItems={{ xs: 'stretch', sm: 'center' }}
          sx={{ mt: 1 }}
        >
          <Button
            variant='contained'
            color='success'
            onClick={() => navigate('/acompanhamento', { state: { token } })}
            sx={{
              width: { xs: '100%', sm: 'auto' },
              minWidth: { xs: '100%', sm: 220 },
            }}
          >
            Acompanhar Agora
          </Button>

          <Button
            variant='outlined'
            onClick={() => navigate('/')}
            sx={{
              width: { xs: '100%', sm: 'auto' },
              minWidth: { xs: '100%', sm: 220 },
              borderColor: 'success.main',
              color: 'success.main',
              '&:hover': {
                bgcolor: 'success.main',
                color: '#fff',
                transform: 'translateY(-2px)',
              },
              transition: 'all 180ms ease',
            }}
          >
            Voltar ao Início
          </Button>
        </Stack>
      </Paper>

      <Snackbar
        open={snackbarOpen}
        autoHideDuration={2500}
        onClose={() => setSnackbarOpen(false)}
        message='Token copiado para a área de transferência'
      />
    </Container>
  );
}
