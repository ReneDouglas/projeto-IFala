import { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Container,
  Typography,
  Box,
  TextField,
  Stack,
  Paper,
  Button,
  Chip,
  CircularProgress,
  Alert,
} from '@mui/material';
import {
  consultarDenunciaPorToken,
  listarMensagens,
  enviarMensagem,
} from '../../services/acompanhamento-api';
import type {
  AcompanhamentoDetalhes,
  MensagemAcompanhamento,
} from '../../types/acompanhamento';

const statusColorMap = (status: string) => {
  switch (status.toUpperCase()) {
    case 'EM_ANALISE':
      return 'warning';
    case 'RECEBIDO':
      return 'success';
    case 'RESOLVIDO':
    case 'FINALIZADO':
      return 'secondary';
    case 'REJEITADO':
      return 'error';
    default:
      return 'default';
  }
};

const formatarStatus = (status: string): string => {
  const statusMap: Record<string, string> = {
    RECEBIDO: 'Recebido',
    EM_ANALISE: 'Em Análise',
    RESOLVIDO: 'Resolvido',
    FINALIZADO: 'Finalizado',
    REJEITADO: 'Rejeitado',
  };
  return statusMap[status.toUpperCase()] || status;
};

const formatarCategoria = (categoria: string): string => {
  const categoriaMap: Record<string, string> = {
    BULLYING_ASSEDIO: 'Bullying e Assédio',
    DISCRIMINACAO: 'Discriminação',
    VIOLENCIA_FISICA: 'Violência Física',
    ABUSO_AUTORIDADE: 'Abuso de Autoridade',
    VANDALISMO: 'Vandalismo',
    OUTRO: 'Outro',
  };
  return categoriaMap[categoria.toUpperCase()] || categoria;
};

const formatarData = (dataISO: string): string => {
  const data = new Date(dataISO);
  return data.toLocaleDateString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  });
};

const formatarHora = (dataISO: string): string => {
  const data = new Date(dataISO);
  return data.toLocaleTimeString('pt-BR', {
    hour: '2-digit',
    minute: '2-digit',
  });
};

export function Acompanhamento() {
  const { token } = useParams<{ token: string }>();
  const navigate = useNavigate();

  const [detalhes, setDetalhes] = useState<AcompanhamentoDetalhes | null>(null);
  const [mensagens, setMensagens] = useState<MensagemAcompanhamento[]>([]);
  const [newMessage, setNewMessage] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [sending, setSending] = useState(false);

  const mensagensBoxRef = useRef<HTMLDivElement>(null);

  // Funções para gerenciar IDs de mensagens enviadas pelo usuário no localStorage
  const getMinhasMensagensIds = (token: string): Set<number> => {
    const stored = localStorage.getItem(`minhas-mensagens-${token}`);
    return stored ? new Set(JSON.parse(stored)) : new Set();
  };

  const salvarMinhaMensagemId = (token: string, id: number) => {
    const ids = getMinhasMensagensIds(token);
    ids.add(id);
    localStorage.setItem(`minhas-mensagens-${token}`, JSON.stringify([...ids]));
  };

  const ehMinhaMensagem = (mensagemId: number): boolean => {
    if (!token) return false;
    const ids = getMinhasMensagensIds(token);
    return ids.has(mensagemId);
  };

  // Auto-scroll para última mensagem
  useEffect(() => {
    if (mensagensBoxRef.current) {
      mensagensBoxRef.current.scrollTop = mensagensBoxRef.current.scrollHeight;
    }
  }, [mensagens]);

  // Carregar dados da denúncia e mensagens
  useEffect(() => {
    if (!token) {
      setError('Token não fornecido');
      setLoading(false);
      return;
    }

    const carregarDados = async () => {
      try {
        setLoading(true);
        setError(null);

        // Carregar detalhes da denúncia e mensagens em paralelo
        const [denunciaData, mensagensData] = await Promise.all([
          consultarDenunciaPorToken(token),
          listarMensagens(token),
        ]);

        setDetalhes(denunciaData);
        setMensagens(mensagensData);
      } catch (err) {
        console.error('Erro ao carregar dados:', err);
        setError(
          (err as { message?: string }).message ||
            'Erro ao carregar dados. Token pode ser inválido.',
        );
      } finally {
        setLoading(false);
      }
    };

    carregarDados();
  }, [token]);

  const handleSendMessage = async () => {
    if (!newMessage.trim() || !token) return;

    try {
      setSending(true);
      const novaMensagem = await enviarMensagem(token, newMessage.trim());

      // Salvar o ID da mensagem enviada no localStorage
      if (novaMensagem.id) {
        salvarMinhaMensagemId(token, novaMensagem.id);
      }

      setMensagens([...mensagens, novaMensagem]);
      setNewMessage('');
    } catch (err) {
      console.error('Erro ao enviar mensagem:', err);
      alert(
        (err as { message?: string }).message ||
          'Erro ao enviar mensagem. Tente novamente.',
      );
    } finally {
      setSending(false);
    }
  };

  // Estado de carregamento
  if (loading) {
    return (
      <Container maxWidth='sm' sx={{ py: 8, textAlign: 'center' }}>
        <CircularProgress size={60} />
        <Typography variant='h6' sx={{ mt: 3, color: 'text.secondary' }}>
          Carregando informações...
        </Typography>
      </Container>
    );
  }

  // Estado de erro
  if (error || !detalhes) {
    return (
      <Container maxWidth='sm' sx={{ py: 8 }}>
        <Paper
          elevation={3}
          sx={{ p: 5, borderRadius: 3, textAlign: 'center' }}
        >
          <Alert severity='error' sx={{ mb: 3 }}>
            {error || 'Denúncia não encontrada'}
          </Alert>
          <Typography variant='body1' sx={{ mb: 3 }}>
            Verifique se o token está correto ou se a denúncia ainda existe no
            sistema.
          </Typography>
          <Button
            variant='contained'
            color='primary'
            onClick={() => navigate('/')}
          >
            Voltar ao Início
          </Button>
        </Paper>
      </Container>
    );
  }

  // Verificar se denúncia está finalizada (Resolvido ou Rejeitado)
  const denunciaFinalizada =
    detalhes.status.toUpperCase() === 'RESOLVIDO' ||
    detalhes.status.toUpperCase() === 'REJEITADO';

  if (denunciaFinalizada) {
    return (
      <Container maxWidth='sm' sx={{ py: 8 }}>
        <Paper
          elevation={3}
          sx={{ p: 5, borderRadius: 3, textAlign: 'center' }}
        >
          <Chip
            label={formatarStatus(detalhes.status)}
            color={statusColorMap(detalhes.status)}
            sx={{ fontSize: '1.1rem', fontWeight: 700, px: 2, py: 3, mb: 2 }}
          />
          <Typography variant='h5' sx={{ fontWeight: 700, mt: 2 }}>
            Denúncia {formatarStatus(detalhes.status)}
          </Typography>
          <Typography variant='body1' sx={{ mt: 2, color: 'text.secondary' }}>
            Esta denúncia foi finalizada e não aceita mais mensagens.
          </Typography>
          <Box sx={{ mt: 4 }}>
            <Typography variant='caption' sx={{ color: 'text.secondary' }}>
              Token de Acompanhamento
            </Typography>
            <Typography
              variant='body1'
              sx={{ fontFamily: 'monospace', fontWeight: 500 }}
            >
              {detalhes.tokenAcompanhamento}
            </Typography>
          </Box>
          <Button
            variant='contained'
            color='primary'
            onClick={() => navigate('/')}
            sx={{ mt: 3 }}
          >
            Voltar ao Início
          </Button>
        </Paper>
      </Container>
    );
  }

  // Interface principal de acompanhamento
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
          {/* Painel de Detalhes da Denúncia */}
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
                label={formatarStatus(detalhes.status)}
                color={statusColorMap(detalhes.status)}
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
              Informações da sua denúncia
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
                  {detalhes.tokenAcompanhamento}
                </Typography>
              </Box>
              <Box>
                <Typography variant='caption' sx={{ color: 'text.secondary' }}>
                  Data de Criação
                </Typography>
                <Typography variant='body1' sx={{ fontWeight: 500 }}>
                  {formatarData(detalhes.criadoEm)}
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
                  {formatarCategoria(detalhes.categoria)}
                </Typography>
              </Box>
            </Stack>
          </Paper>

          {/* Painel de Chat */}
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
                Converse com os administradores sobre sua denúncia
              </Typography>
            </Box>

            {/* Área de Mensagens */}
            <Box
              ref={mensagensBoxRef}
              sx={{
                flexGrow: 1,
                p: 4,
                overflowY: 'auto',
                display: 'flex',
                flexDirection: 'column',
                height: '400px',
                backgroundColor: '#fafafa',
              }}
            >
              {mensagens.length === 0 ? (
                <Box
                  sx={{
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                    height: '100%',
                  }}
                >
                  <Typography variant='body2' color='text.secondary'>
                    Nenhuma mensagem ainda. Envie a primeira!
                  </Typography>
                </Box>
              ) : (
                mensagens.map((msg) => {
                  // Verifica se a mensagem foi enviada pelo usuário atual
                  const minhaMsg = ehMinhaMensagem(msg.id);

                  return (
                    <Box
                      key={msg.id}
                      sx={{
                        mb: 2,
                        p: 2,
                        borderRadius: 2,
                        background: minhaMsg ? '#e3f6ea' : '#f5f5f5',
                        alignSelf: minhaMsg ? 'flex-end' : 'flex-start',
                        maxWidth: '75%',
                      }}
                    >
                      <Typography
                        variant='caption'
                        sx={{ fontWeight: 600, color: 'text.secondary' }}
                      >
                        {minhaMsg ? 'Você' : msg.autor}
                      </Typography>
                      <Typography variant='body2' sx={{ mt: 0.5 }}>
                        {msg.mensagem}
                      </Typography>
                      <Typography
                        variant='caption'
                        sx={{ color: 'gray', mt: 1 }}
                      >
                        {formatarHora(msg.dataEnvio)}
                      </Typography>
                    </Box>
                  );
                })
              )}
            </Box>

            {/* Área de Envio de Mensagem */}
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
                placeholder='Escreva uma mensagem...'
                variant='outlined'
                value={newMessage}
                onChange={(e) => setNewMessage(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    handleSendMessage();
                  }
                }}
                disabled={sending}
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
                disabled={!newMessage.trim() || sending}
                sx={{ py: 1.5, px: 3, borderRadius: 2, fontWeight: 700 }}
                endIcon={
                  sending ? (
                    <CircularProgress size={20} color='inherit' />
                  ) : (
                    <span className='material-symbols-outlined'>send</span>
                  )
                }
              >
                {sending ? 'Enviando...' : 'Enviar'}
              </Button>
            </Box>
          </Paper>
        </Box>
      </Container>
    </Box>
  );
}
