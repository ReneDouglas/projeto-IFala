import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Typography,
  Box,
  TextField,
  Stack,
  Paper,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Button,
  Chip,
} from '@mui/material';

const simulado_dados = {
  token: 'GUARD-XXXX-0002',
  creationDate: '06/10/2025',
  category: 'bullying e assédio',
  initialStatus: 'Resolvido',
  status: '',
  motivo_rejeicao: '',
};

const simulado_mensagens = [
  { autor: 'admin', texto: 'Olá, tudo bem?', data: '10:00' },
  { autor: 'user', texto: 'Sim, obrigado!', data: '10:01' },
  { autor: 'admin', texto: 'Você pode fornecer mais detalhes?', data: '10:02' },
  {
    autor: 'user',
    texto: 'Claro, o incidente ocorreu na escola.',
    data: '10:05',
  },
  {
    autor: 'admin',
    texto: 'Entendido, obrigado pela informação.',
    data: '10:06',
  },
  { autor: 'user', texto: 'De nada!', data: '10:07' },
];

const statusColorMap = (status: string) => {
  switch (status) {
    case 'Em Análise':
      return 'warning';
    case 'Recebido':
      return 'success';
    case 'Finalizado':
      return 'secondary';
    default:
      return 'default';
  }
};

export function Acompanhamento() {
  const navigate = useNavigate();

  const [currentStatus, setCurrentStatus] = useState(
    simulado_dados.initialStatus,
  );
  const [newMessage, setNewMessage] = useState('');
  const [details, setDetails] = useState(simulado_dados);
  const [mensagens, setMensagens] = useState(simulado_mensagens);

  const mensagensBoxRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (mensagensBoxRef.current) {
      mensagensBoxRef.current.scrollTop = mensagensBoxRef.current.scrollHeight;
    }
  }, [mensagens]);

  useEffect(() => {
    const token = details.token;
    fetch(`/api/v1/public/denuncias/${token}/acompanhamento`)
      .then((res) => res.json())
      .then((data) => {});

    fetch(`/api/v1/public/denuncias/${token}/acompanhamento/mensagens`)
      .then((res) => res.json())
      .then((data) => {});
  }, [details.token]);

  const handleStatusChange = (event: any) => {
    setCurrentStatus(event.target.value as string);
  };

  const handleUpdateStatus = () => {
    alert(
      `Status atualizado localmente para: ${currentStatus}. ação da api simulada`,
    );
  };

  const handleSendMessage = () => {
    if (newMessage.trim() === '') return;
    const now = new Date();
    const timeString = `${String(now.getHours()).padStart(2, '0')}:${String(
      now.getMinutes(),
    ).padStart(2, '0')}`;
    setMensagens([
      ...mensagens,
      { autor: 'user', texto: newMessage.trim(), data: timeString },
    ]);
    setNewMessage('');
  };

  if (details.status === 'Rejeitado' || details.status === 'Resolvido') {
    return (
      <Container maxWidth='sm' sx={{ py: 8 }}>
        <Paper
          elevation={3}
          sx={{ p: 5, borderRadius: 3, textAlign: 'center' }}
        >
          <Typography variant='h5' sx={{ fontWeight: 700 }}>
            {details.status}
          </Typography>
          {details.status === 'Rejeitado' && (
            <Typography variant='body1' sx={{ mt: 2 }}>
              Motivo: {details.motivo_rejeicao}
            </Typography>
          )}
        </Paper>
      </Container>
    );
  }

  return (
    <Box sx={{ backgroundColor: 'background.default', minHeight: '100vh' }}>
      <Container maxWidth='lg' sx={{ py: 4 }}>
        <Box
          sx={{
            display: 'grid',
            gridTemplateColumns: { xs: '1fr', md: '1fr 2fr' },
            gap: 4,
          }}
        >
          <Paper
            elevation={3}
            sx={{ p: 5, borderRadius: 3, height: 'fit-content' }}
          >
            <Box
              sx={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                mb: 2,
              }}
            >
              <Typography variant='h6' sx={{ fontWeight: 600 }}>
                Detalhes da Denúncia
              </Typography>
              <Chip
                label={currentStatus}
                color={statusColorMap(currentStatus)}
                icon={
                  <span className='material-symbols-outlined'>schedule</span>
                }
                sx={{ fontWeight: 'bold' }}
              />
            </Box>
            <Typography
              variant='subtitle2'
              sx={{ color: 'text.secondary', mb: 2 }}
            >
              Visualize e atualize as informações principais
            </Typography>
            <Stack spacing={2}>
              <Box>
                <Typography variant='caption' sx={{ color: 'text.secondary' }}>
                  Token
                </Typography>
                <Typography
                  variant='body1'
                  sx={{ fontFamily: 'monospace', fontWeight: 500 }}
                >
                  {details.token}
                </Typography>
              </Box>
              <Box>
                <Typography variant='caption' sx={{ color: 'text.secondary' }}>
                  Data de Criação
                </Typography>
                <Typography variant='body1' sx={{ fontWeight: 500 }}>
                  {details.creationDate}
                </Typography>
              </Box>
              <Box>
                <Typography variant='caption' sx={{ color: 'text.secondary' }}>
                  Categoria
                </Typography>
                <Typography
                  variant='body1'
                  sx={{ fontWeight: 700, color: 'primary.main' }}
                >
                  {details.category}
                </Typography>
              </Box>
              <FormControl fullWidth size='small'>
                <InputLabel id='status-select-label'>
                  Status da Denúncia
                </InputLabel>
                <Select
                  labelId='status-select-label'
                  id='status-select'
                  value={currentStatus}
                  label='Status da Denúncia'
                  onChange={handleStatusChange}
                  sx={{ borderRadius: 2 }}
                >
                  <MenuItem value='Em Análise'>Em Análise</MenuItem>
                  <MenuItem value='Recebido'>Recebido</MenuItem>
                  <MenuItem value='Resolvido'>Resolvido</MenuItem>
                  <MenuItem value='Aguardando'>Aguardando informações</MenuItem>
                  <MenuItem value='Rejeitado'>Rejeitado</MenuItem>
                </Select>
              </FormControl>
              <Button
                variant='contained'
                color='success'
                onClick={handleUpdateStatus}
                sx={{ mt: 2, fontWeight: 700 }}
              >
                Atualizar Status
              </Button>
            </Stack>
          </Paper>

          <Paper
            elevation={3}
            sx={{
              p: 0,
              borderRadius: 3,
              display: 'flex',
              flexDirection: 'column',
              minHeight: '70vh',
            }}
          >
            <Box
              sx={{
                p: 4,
                borderBottom: '1px solid #c0bebeff',
                background: 'background.paper',
                borderTopLeftRadius: 12,
                borderTopRightRadius: 12,
              }}
            >
              <Typography variant='h6' sx={{ fontWeight: 600 }}>
                Chat
              </Typography>
              <Typography
                variant='subtitle2'
                sx={{ color: 'text.secondary', mt: 1 }}
              >
                Converse com o denunciante para obter mais informações
              </Typography>
            </Box>
            <Box
              ref={mensagensBoxRef}
              sx={{
                flexGrow: 1,
                p: 4,
                overflowY: 'auto',
                display: 'flex',
                flexDirection: 'column',
                height: '400px',
              }}
            >
              {mensagens.map((msg, idx) => (
                <Box
                  key={idx}
                  sx={{
                    mb: 2,
                    p: 2,
                    borderRadius: 2,
                    background: msg.autor === 'admin' ? '#e3f6ea' : '#f5f5f5',
                    alignSelf:
                      msg.autor === 'admin' ? 'flex-end' : 'flex-start',
                  }}
                >
                  <Typography variant='body2'>{msg.texto}</Typography>
                  <Typography
                    variant='caption'
                    sx={{ color: 'gray' }}
                  >{`${msg.data}`}</Typography>
                </Box>
              ))}
            </Box>
            <Box
              sx={{
                p: 3,
                borderTop: '1px solid #eee',
                display: 'flex',
                alignItems: 'flex-end',
                background: 'background.paper',
                borderBottomLeftRadius: 12,
                borderBottomRightRadius: 12,
              }}
            >
              <TextField
                fullWidth
                multiline
                rows={1}
                maxRows={4}
                placeholder='Escreva uma mensagem para solicitar mais informações...'
                variant='outlined'
                value={newMessage}
                onChange={(e) => setNewMessage(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    handleSendMessage();
                  }
                }}
                sx={{
                  mr: 2,
                  background: 'background.default',
                  borderRadius: 2,
                }}
                InputProps={{
                  style: { borderRadius: '12px', padding: '10px' },
                }}
              />
              <Button
                variant='contained'
                color='success'
                onClick={handleSendMessage}
                disabled={!newMessage.trim()}
                sx={{ py: 1.5, px: 3, borderRadius: 2, fontWeight: 700 }}
                endIcon={
                  <span className='material-symbols-outlined'>send</span>
                }
              >
                Enviar
              </Button>
            </Box>
          </Paper>
        </Box>
      </Container>
    </Box>
  );
}
