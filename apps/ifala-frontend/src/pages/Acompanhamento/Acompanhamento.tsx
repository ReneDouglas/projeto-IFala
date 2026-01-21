import { useState, useEffect, useRef, useMemo, memo } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ProvasModal } from '../../components/ProvasModal';
import './AcompanhamentoProvas.css';
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
  MenuItem,
  Menu,
  Dialog,
  DialogTitle,
  DialogContent,
  IconButton,
} from '@mui/material';
import {
  consultarDenunciaPorToken,
  consultarDenunciaPorId,
  listarMensagens,
  listarAcompanhamentosPorId,
  enviarMensagem,
  enviarMensagemAdmin,
  alterarStatusDenuncia,
} from '../../services/acompanhamento-api';
import { listarProvasDenuncia, getProvaUrl } from '../../services/api';
import type {
  AcompanhamentoDetalhes,
  MensagemAcompanhamento,
} from '../../types/acompanhamento';
import { useAuth } from '../../hooks/useAuth';
import { PDFDownloadLink } from '@react-pdf/renderer';
import { RelatorioDenunciaPDF } from '../../components/RelatorioDenuncia';
import PrintIcon from '@mui/icons-material/Print';
type DetalhesComRelato = AcompanhamentoDetalhes & {
  descricaoDetalhada?: string;
  descricao?: string;
};

const statusColorMap = (status: string) => {
  switch (status.toUpperCase()) {
    case 'EM_ANALISE':
      return 'default';
    case 'RECEBIDO':
      return 'success';
    case 'RESOLVIDO':
      return 'success';
    case 'REJEITADO':
      return 'error';
    case 'AGUARDANDO':
      return 'warning';
    default:
      return 'default';
  }
};

const formatarStatus = (status: string): string => {
  const statusMap: Record<string, string> = {
    RECEBIDO: 'Recebido',
    EM_ANALISE: 'Em Análise',
    RESOLVIDO: 'Resolvido',
    REJEITADO: 'Rejeitado',
    AGUARDANDO: 'Aguardando Informações',
  };
  return statusMap[status.toUpperCase()] || status;
};

const formatarCategoria = (categoria: string): string => {
  const categoriaMap: Record<string, string> = {
    BULLYING: 'Bullying e Assédio',
    DROGAS: 'Uso ou Porte de Substâncias Ilícitas',
    VIOLENCIA: 'Violência Física ou Verbal',
    VANDALISMO: 'Vandalismo e Danos ao Patrimônio',
    ACADEMICO: 'Questões Acadêmicas (Fraude, Plágio)',
    DISPOSITIVO_ELETRONICO: 'Uso ou Porte de Dispositivo Eletrônico',
    OUTROS: 'Outros',
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

const formatarGrau = (grau: string): string => {
  const grauMap: Record<string, string> = {
    MEDIO: 'Médio',
    SUPERIOR: 'Superior',
  };
  return grauMap[grau.toUpperCase()] || grau;
};

const formatarCurso = (curso: string): string => {
  const cursoMap: Record<string, string> = {
    ADMINISTRACAO: 'Administração',
    AGROPECUARIA: 'Agropecuária',
    INFORMATICA: 'Informática',
    MEIO_AMBIENTE: 'Meio Ambiente',
    ANALISE_DESENVOLVIMENTO_SISTEMAS: 'Análise e Desenvolvimento de Sistemas',
    LICENCIATURA_MATEMATICA: 'Licenciatura em Matemática',
    LICENCIATURA_FISICA: 'Licenciatura em Física',
    GESTAO_AMBIENTAL: 'Gestão Ambiental',
  };
  return cursoMap[curso.toUpperCase()] || curso;
};

const formatarTurma = (turma: string): string => {
  const turmaMap: Record<string, string> = {
    UNICA: 'Única',
    A: 'A',
    B: 'B',
    MODULO_I: 'Módulo I',
    MODULO_II: 'Módulo II',
    MODULO_III: 'Módulo III',
    MODULO_IV: 'Módulo IV',
    MODULO_V: 'Módulo V',
    MODULO_VI: 'Módulo VI',
    MODULO_VII: 'Módulo VII',
    MODULO_VIII: 'Módulo VIII',
  };
  return turmaMap[turma.toUpperCase()] || turma;
};

const formatarAno = (ano: string | null): string => {
  if (!ano) return '';
  const anoMap: Record<string, string> = {
    PRIMEIRO_ANO: '1º Ano',
    SEGUNDO_ANO: '2º Ano',
    TERCEIRO_ANO: '3º Ano',
  };
  return anoMap[ano.toUpperCase()] || ano;
};

// --- COMPONENTE ISOLADO (Evita piscar ao digitar) ---

const BotaoExportarPDF = memo(
  ({
    dados,
  }: {
    dados: {
      protocolo: string;
      data: string;
      categoria: string;
      status: string;
      relato: string;
      temAnexos: boolean;
      historico: Array<{
        autor: string;
        mensagem: string;
        data: string;
      }>;
    };
  }) => {
    if (!dados) return null;

    return (
      <PDFDownloadLink
        document={<RelatorioDenunciaPDF dados={dados} />}
        fileName={`relatorio_${dados.protocolo}.pdf`}
        style={{ textDecoration: 'none' }}
      >
        {({ loading }) => (
          <Button
            variant='outlined'
            fullWidth
            startIcon={loading ? <CircularProgress size={20} /> : <PrintIcon />}
            disabled={loading}
            sx={{
              color: '#555',
              borderColor: '#999',
              '&:hover': {
                backgroundColor: '#f5f5f5',
                borderColor: '#333',
              },
            }}
          >
            {loading ? 'Gerando PDF...' : 'Exportar Relatório / Imprimir'}
          </Button>
        )}
      </PDFDownloadLink>
    );
  },
);

export function Acompanhamento() {
  const { token, denunciaId: denunciaIdParam } = useParams<{
    token?: string;
    denunciaId?: string;
  }>();
  const navigate = useNavigate();
  const { user, isLoggedIn } = useAuth();

  // Converter denunciaId para número se existir
  const denunciaId = denunciaIdParam ? parseInt(denunciaIdParam, 10) : null;

  const [modalProvasOpen, setModalProvasOpen] = useState(false);
  const [provaIndexSelecionada, setProvaIndexSelecionada] = useState(0);

  const abrirModalProvas = (index: number) => {
    setProvaIndexSelecionada(index);
    setModalProvasOpen(true);
  };

  const fecharModalProvas = () => setModalProvasOpen(false);

  const [detalhes, setDetalhes] = useState<AcompanhamentoDetalhes | null>(null);
  const [mensagens, setMensagens] = useState<MensagemAcompanhamento[]>([]);
  const [provas, setProvas] = useState<
    Array<{ id: number; nomeArquivo: string; tipoMime: string }>
  >([]);
  const [newMessage, setNewMessage] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [sending, setSending] = useState(false);
  const [alterandoStatus, setAlterandoStatus] = useState(false);
  const [avisoFlood, setAvisoFlood] = useState(false);
  const [anchorElStatus, setAnchorElStatus] = useState<null | HTMLElement>(
    null,
  );
  const [modalDenuncianteAberto, setModalDenuncianteAberto] = useState(false);

  const mensagensBoxRef = useRef<HTMLDivElement>(null);

  const isAdmin = isLoggedIn && user?.roles?.includes('ADMIN');
  const statusMenuAberto = Boolean(anchorElStatus);

  // Determinar modo de acesso (token ou ID)
  const modoToken = !!token && !denunciaId;
  const modoAdmin = !!denunciaId && isAdmin;

  const handleAbrirMenuStatus = (event: React.MouseEvent<HTMLElement>) => {
    if (isAdmin && modoAdmin) {
      setAnchorElStatus(event.currentTarget);
    }
  };

  const handleFecharMenuStatus = () => {
    setAnchorElStatus(null);
  };

  const handleAlterarStatus = async (novoStatus: string) => {
    if (!detalhes || !isAdmin || !modoAdmin) return;

    handleFecharMenuStatus();
    setAlterandoStatus(true);
    try {
      await alterarStatusDenuncia(detalhes.id, novoStatus);

      // Atualizar o status localmente
      setDetalhes({ ...detalhes, status: novoStatus });

      await carregarMensagens();

      setError(null);
    } catch (err) {
      console.error('Erro ao alterar status:', err);
      setError('Erro ao alterar o status da denúncia.');
    } finally {
      setAlterandoStatus(false);
    }
  };

  // Função para carregar mensagens de acordo com o modo
  const carregarMensagens = async () => {
    try {
      if (modoToken && token) {
        const mensagensData = await listarMensagens(token);
        setMensagens(mensagensData);
      } else if (modoAdmin && denunciaId) {
        const acompanhamentosData =
          await listarAcompanhamentosPorId(denunciaId);
        // Adaptar a resposta da API para o formato esperado
        setMensagens(acompanhamentosData);
      }
    } catch (err) {
      console.error('Erro ao carregar mensagens:', err);
    }
  };

  // Verifica se a mensagem pertence ao usuário atual
  const ehMinhaMensagem = (mensagemId: number, autor: string): boolean => {
    // Modo token: usuário anônimo
    if (modoToken) {
      return autor === 'Usuário Anônimo';
    }

    // Modo admin: admin autenticado
    if (modoAdmin && isAdmin) {
      return autor === 'Admin' || autor === user?.nome || autor === user?.email;
    }

    return false;
  };

  // Função para carregar detalhes da denúncia
  const carregarDetalhesDenuncia = async () => {
    try {
      setLoading(true);
      setError(null);

      let denunciaData: AcompanhamentoDetalhes;

      if (modoToken && token) {
        denunciaData = await consultarDenunciaPorToken(token);
      } else if (modoAdmin && denunciaId) {
        denunciaData = await consultarDenunciaPorId(denunciaId);
      } else {
        throw new Error('Modo de acesso inválido');
      }
      setDetalhes(denunciaData);
    } catch (err) {
      console.error('Erro ao carregar detalhes:', err);
      setError(
        (err as { message?: string }).message ||
          'Erro ao carregar dados. Token ou ID podem ser inválidos.',
      );
      throw err;
    }
  };

  // Função para carregar provas da denúncia
  const carregarProvas = async (denunciaIdNumerico: number) => {
    try {
      const provasData = await listarProvasDenuncia(denunciaIdNumerico);
      setProvas(provasData);
    } catch (err) {
      console.error('Erro ao carregar provas:', err);
      // Não bloquear o carregamento se as provas falharem
    }
  };

  useEffect(() => {
    if (mensagensBoxRef.current) {
      mensagensBoxRef.current.scrollTop = mensagensBoxRef.current.scrollHeight;
    }
  }, [mensagens]);

  // Carregar dados da denúncia e mensagens
  useEffect(() => {
    if (!token && !denunciaId) {
      setError('Token ou ID da denúncia não fornecidos');
      setLoading(false);
      return;
    }

    // Verificar se é admin quando acessando por ID
    if (denunciaId && !isAdmin) {
      setError('Acesso não autorizado. É necessário ser administrador.');
      setLoading(false);
      return;
    }

    const carregarDados = async () => {
      try {
        setLoading(true);
        setError(null);

        // Carregar detalhes da denúncia e mensagens em paralelo
        await Promise.all([carregarDetalhesDenuncia(), carregarMensagens()]);

        // Carregar provas se tivermos o ID da denúncia
        if (detalhes?.id) {
          await carregarProvas(detalhes.id);
        }
      } catch (err) {
        console.error('Erro ao carregar dados:', err);
        // O erro já foi definido nas funções individuais
      } finally {
        setLoading(false);
      }
    };

    carregarDados();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token, denunciaId, isAdmin]);

  // Carregar provas quando detalhes estiverem disponíveis
  useEffect(() => {
    if (detalhes?.id && provas.length === 0) {
      carregarProvas(detalhes.id);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [detalhes]);

  useEffect(() => {
    if (modalProvasOpen && provas.length === 0) {
      setModalProvasOpen(false);
    }
  }, [modalProvasOpen, provas.length]);

  const handleSendMessage = async () => {
    if (!newMessage.trim()) return;

    // Validar modo de acesso
    if (modoToken && !token) return;
    if (modoAdmin && !denunciaId) return;

    try {
      setSending(true);
      setAvisoFlood(false);

      let novaMensagem: MensagemAcompanhamento;

      if (modoAdmin && denunciaId) {
        // Admin enviando mensagem via ID
        novaMensagem = await enviarMensagemAdmin(denunciaId, newMessage.trim());
      } else if (modoToken && token) {
        // Usuário anônimo ou admin enviando via token
        if (isAdmin) {
          // Admin também pode enviar via token se estiver nesse modo
          novaMensagem = await enviarMensagemAdmin(
            detalhes!.id,
            newMessage.trim(),
          );
        } else {
          novaMensagem = await enviarMensagem(token, newMessage.trim());
        }
      } else {
        throw new Error('Modo de envio inválido');
      }

      setMensagens([...mensagens, novaMensagem]);
      setNewMessage('');
      setError(null);
    } catch (err: unknown) {
      console.error('Erro ao enviar mensagem:', err);

      const error = err as { message?: string; status?: number };
      const errorMessage = error?.message || '';

      if (
        (errorMessage.includes('aguardar') ||
          errorMessage.includes('administrador') ||
          error?.status === 403) &&
        modoToken
      ) {
        setAvisoFlood(true);
        setTimeout(() => setAvisoFlood(false), 5000);
      } else {
        // Outros erros mostram como erro real
        setError(errorMessage || 'Erro ao enviar mensagem. Tente novamente.');
      }
    } finally {
      setSending(false);
    }
  };

  const podeEnviarMensagem = (): boolean => {
    // Admin sempre pode enviar mensagens no modo admin
    if (modoAdmin && isAdmin) return true;

    // No modo token, admin também pode enviar
    if (modoToken && isAdmin) return true;

    // Verificar se a denúncia está finalizada (Resolvida ou Rejeitada)
    if (detalhes) {
      const statusFinal = detalhes.status.toUpperCase();
      if (statusFinal === 'RESOLVIDO' || statusFinal === 'REJEITADO') {
        // Usuário comum não pode enviar mensagens em denúncias finalizadas
        return false;
      }
    }

    // Se não há mensagens ainda, usuário comum pode enviar a primeira
    if (mensagens.length === 0 && modoToken) return true;

    if (mensagens.length === 0) return false;

    const ultimaMensagem = mensagens[mensagens.length - 1];
    // Denunciante pode enviar se a última mensagem NÃO for dele
    return !ehMinhaMensagem(ultimaMensagem.id, ultimaMensagem.autor);
  };

  const usuarioPodeEnviar = podeEnviarMensagem();

  // Verificar se denúncia está finalizada (para exibir aviso)
  const denunciaFinalizada =
    detalhes &&
    (detalhes.status.toUpperCase() === 'RESOLVIDO' ||
      detalhes.status.toUpperCase() === 'REJEITADO');

  // --- MEMOIZAÇÃO DOS DADOS PARA O PDF ---
  // --- MEMOIZAÇÃO COMPLETA DOS DADOS PARA O PDF ---
  const dadosParaRelatorio = useMemo(() => {
    // 1. Definimos o relato primeiro para poder comparar depois
    const textoRelato =
      (detalhes as DetalhesComRelato)?.descricaoDetalhada ||
      (detalhes as DetalhesComRelato)?.descricao ||
      mensagens?.[0]?.mensagem ||
      'Sem descrição disponível.';

    return detalhes
      ? {
          protocolo: detalhes.tokenAcompanhamento || detalhes.id.toString(),
          data: formatarData(detalhes.criadoEm),
          categoria: formatarCategoria(detalhes.categoria),
          status: formatarStatus(detalhes.status),
          temAnexos: provas && provas.length > 0,

          // LÓGICA DO HISTÓRICO (CHAT) COM FILTRO DE DUPLICIDADE
          historico: mensagens
            ? mensagens
                // FILTRO: Remove a mensagem se ela for idêntica ao texto da Seção 2
                .filter((item) => item.mensagem.trim() !== textoRelato.trim())
                .map((item) => {
                  const msg = item as unknown as {
                    mensagem: string;
                    dataEnvio: string;
                    respondidoPor?: unknown;
                    admin?: unknown;
                    autor?: string;
                  };

                  // Verifica quem mandou a mensagem
                  const ehAdmin =
                    msg.respondidoPor ||
                    msg.admin ||
                    (msg.autor && msg.autor !== 'Usuário Anônimo');

                  return {
                    mensagem: msg.mensagem,
                    // MUDANÇA DE NOME AQUI 👇
                    autor: ehAdmin ? 'Coordenação/Admin' : 'Usuário',
                    data: formatarData(msg.dataEnvio),
                  };
                })
            : [],

          // RELATO ORIGINAL (SEÇÃO 2)
          relato: textoRelato,
        }
      : null;
  }, [detalhes, mensagens, provas]); // <--- A LISTA DE DEPENDÊNCIAS É O SEGREDO

  const indexSeguro =
    provas.length > 0 ? Math.min(provaIndexSelecionada, provas.length - 1) : 0;

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
            Verifique se o {modoToken ? 'token' : 'ID'} está correto ou se a
            denúncia ainda existe no sistema.
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
            sx={{
              p: 5,
              borderRadius: 3,
              height: 'fit-content',
            }}
          >
            <Box sx={{ mb: 3 }}>
              <Typography variant='h6' sx={{ fontWeight: 600, mb: 2 }}>
                Detalhes da Denúncia
              </Typography>

              {isAdmin && modoAdmin && !denunciaFinalizada && (
                <Typography
                  variant='caption'
                  sx={{ color: 'text.secondary', display: 'block', mb: 1 }}
                >
                  Clique para alterar o status
                </Typography>
              )}

              {/* Aviso de denúncia finalizada (apenas admin) */}
              {isAdmin && modoAdmin && denunciaFinalizada && (
                <Alert severity='info' sx={{ mb: 2 }}>
                  <Typography variant='body2' sx={{ fontWeight: 600 }}>
                    Status Final
                  </Typography>
                  <Typography variant='body2' sx={{ mt: 0.5 }}>
                    Esta denúncia foi{' '}
                    {detalhes.status.toUpperCase() === 'RESOLVIDO'
                      ? 'resolvida'
                      : 'rejeitada'}{' '}
                    e seu status não pode mais ser alterado.
                  </Typography>
                </Alert>
              )}

              {/* Badge de Status - Interativo para admin (se não finalizada), estático para usuário */}
              <Chip
                label={formatarStatus(detalhes.status)}
                color={statusColorMap(detalhes.status)}
                icon={
                  <span className='material-symbols-outlined'>schedule</span>
                }
                onClick={
                  isAdmin && modoAdmin && !denunciaFinalizada
                    ? handleAbrirMenuStatus
                    : undefined
                }
                sx={{
                  fontWeight: 'bold',
                  mb: 2,
                  cursor:
                    isAdmin && modoAdmin && !denunciaFinalizada
                      ? 'pointer'
                      : 'default',
                  opacity: denunciaFinalizada ? 0.8 : 1,
                  ...(detalhes.status.toUpperCase() === 'EM_ANALISE'
                    ? {
                        backgroundColor: '#eae304',
                        color: '#fefdfd',
                        border: '1px solid #eae304',
                        '& .MuiChip-icon': {
                          color: '#fefdfd',
                        },
                      }
                    : {}),
                  '&:hover':
                    isAdmin && modoAdmin && !denunciaFinalizada
                      ? {
                          opacity: 0.8,
                          transform: 'scale(1.02)',
                          transition: 'all 0.2s',
                        }
                      : {},
                }}
              />

              {/* Menu dropdown de status (apenas admin no modo admin) */}
              {isAdmin && modoAdmin && (
                <Menu
                  anchorEl={anchorElStatus}
                  open={statusMenuAberto}
                  onClose={handleFecharMenuStatus}
                  anchorOrigin={{
                    vertical: 'bottom',
                    horizontal: 'left',
                  }}
                  transformOrigin={{
                    vertical: 'top',
                    horizontal: 'left',
                  }}
                >
                  <MenuItem
                    onClick={() => handleAlterarStatus('RECEBIDO')}
                    disabled={detalhes.status === 'RECEBIDO'}
                  >
                    <Chip
                      label='Recebido'
                      color='success'
                      size='small'
                      sx={{ mr: 1 }}
                    />
                  </MenuItem>
                  <MenuItem
                    onClick={() => handleAlterarStatus('EM_ANALISE')}
                    disabled={detalhes.status === 'EM_ANALISE'}
                  >
                    <Chip
                      label='Em Análise'
                      size='small'
                      sx={{
                        mr: 1,
                        backgroundColor: '#eae304',
                        color: '#fefdfd',
                        border: '1px solid #eae304',
                      }}
                    />
                  </MenuItem>
                  <MenuItem
                    onClick={() => handleAlterarStatus('AGUARDANDO')}
                    disabled={detalhes.status === 'AGUARDANDO'}
                  >
                    <Chip
                      label='Aguardando Informações'
                      color='warning'
                      size='small'
                      sx={{ mr: 1 }}
                    />
                  </MenuItem>
                  <MenuItem
                    onClick={() => handleAlterarStatus('RESOLVIDO')}
                    disabled={detalhes.status === 'RESOLVIDO'}
                  >
                    <Chip
                      label='Resolvido'
                      color='success'
                      size='small'
                      sx={{ mr: 1 }}
                    />
                  </MenuItem>
                  <MenuItem
                    onClick={() => handleAlterarStatus('REJEITADO')}
                    disabled={detalhes.status === 'REJEITADO'}
                  >
                    <Chip
                      label='Rejeitado'
                      color='error'
                      size='small'
                      sx={{ mr: 1 }}
                    />
                  </MenuItem>
                </Menu>
              )}

              {/* Indicador de carregamento durante alteração */}
              {alterandoStatus && (
                <Typography
                  variant='caption'
                  sx={{ color: 'primary.main', display: 'block', mb: 2 }}
                >
                  Alterando status...
                </Typography>
              )}
            </Box>

            <Typography
              variant='subtitle2'
              sx={{ color: 'text.secondary', mb: 2 }}
            >
              Informações da denúncia
            </Typography>

            <Stack spacing={2}>
              <Box>
                <Typography variant='caption' sx={{ color: 'text.secondary' }}>
                  Token de Acompanhamento
                </Typography>
                <Typography
                  variant='body1'
                  sx={{ fontFamily: 'monospace', fontWeight: 600 }}
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

            {/* Seção de Provas/Evidências */}
            {provas.length > 0 && (
              <Box sx={{ mt: 3 }}>
                <Typography
                  variant='subtitle2'
                  sx={{ color: 'text.secondary', mb: 1 }}
                >
                  Provas/Evidências Anexadas
                </Typography>
                <Box
                  sx={{
                    display: 'grid',
                    gridTemplateColumns:
                      'repeat(auto-fill, minmax(120px, 1fr))',
                    gap: 1,
                  }}
                >
                  {provas.map((prova, index) => (
                    <Box
                      key={prova.id}
                      role='button'
                      tabIndex={0}
                      onClick={() => abrirModalProvas(index)}
                      onKeyDown={(e) => {
                        if (e.key === 'Enter' || e.key === ' ') {
                          e.preventDefault();
                          abrirModalProvas(index);
                        }
                      }}
                      className='prova-thumb'
                    >
                      <img
                        src={getProvaUrl(prova.id)}
                        alt={prova.nomeArquivo}
                        className='prova-thumb__img'
                        loading='lazy'
                      />

                      <div className='prova-thumb__overlay'>
                        <div className='prova-thumb__overlayContent'>
                          <span className='material-symbols-outlined'>
                            zoom_in
                          </span>
                          <span className='prova-thumb__overlayText'>
                            Clique para ampliar
                          </span>
                        </div>
                      </div>
                    </Box>
                  ))}
                </Box>
              </Box>
            )}
            {isAdmin && detalhes && dadosParaRelatorio && (
              <Box sx={{ mt: 3, pt: 2, borderTop: '1px solid #eee' }}>
                <BotaoExportarPDF dados={dadosParaRelatorio} />
              </Box>
            )}
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
                {modoToken
                  ? 'Converse com os administradores sobre sua denúncia'
                  : 'Acompanhamento da denúncia'}
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
                  // Determinar se é minha mensagem
                  const minhaMsg = ehMinhaMensagem(msg.id, msg.autor);

                  // Determinar o nome a ser exibido
                  let nomeExibido = msg.autor;
                  if (minhaMsg) {
                    nomeExibido = 'Você';
                  } else if (
                    msg.autor === 'Usuário Anônimo' &&
                    detalhes.denunciante
                  ) {
                    nomeExibido = detalhes.denunciante.nomeCompleto;
                  }

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
                        sx={{
                          fontWeight: 600,
                          color:
                            msg.autor === 'Usuário Anônimo' &&
                            detalhes.denunciante
                              ? 'primary.main'
                              : 'text.secondary',
                          cursor:
                            msg.autor === 'Usuário Anônimo' &&
                            detalhes.denunciante
                              ? 'pointer'
                              : 'default',
                          '&:hover':
                            msg.autor === 'Usuário Anônimo' &&
                            detalhes.denunciante
                              ? { textDecoration: 'underline' }
                              : {},
                        }}
                        onClick={() => {
                          if (
                            msg.autor === 'Usuário Anônimo' &&
                            detalhes.denunciante
                          ) {
                            setModalDenuncianteAberto(true);
                          }
                        }}
                      >
                        {nomeExibido}
                      </Typography>
                      <Typography
                        variant='body2'
                        sx={{
                          mt: 0.5,
                          wordBreak: 'break-word',
                          overflowWrap: 'break-word',
                        }}
                      >
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
                background: 'background.paper',
                borderBottomLeftRadius: 12,
                borderBottomRightRadius: 12,
              }}
            >
              {avisoFlood && (
                <Alert
                  severity='warning'
                  sx={{ mb: 2 }}
                  onClose={() => setAvisoFlood(false)}
                >
                  Você precisa aguardar a resposta do administrador antes de
                  enviar outra mensagem.
                </Alert>
              )}

              {/* Aviso de denúncia finalizada (apenas para usuário comum no modo token) */}
              {denunciaFinalizada && modoToken && !isAdmin && (
                <Alert severity='info' sx={{ mb: 2 }}>
                  <Typography variant='body2' sx={{ fontWeight: 600 }}>
                    Esta denúncia foi{' '}
                    {detalhes.status.toUpperCase() === 'RESOLVIDO'
                      ? 'resolvida'
                      : 'rejeitada'}
                    .
                  </Typography>
                  <Typography variant='body2' sx={{ mt: 0.5 }}>
                    Você pode visualizar o histórico de mensagens, mas não pode
                    mais enviar novas mensagens.
                  </Typography>
                </Alert>
              )}

              {/* Aviso de aguardar resposta do admin (regra de flood) */}
              {!usuarioPodeEnviar &&
                modoToken &&
                !isAdmin &&
                !denunciaFinalizada && (
                  <Alert severity='info' sx={{ mb: 2 }}>
                    Aguarde a resposta do administrador para enviar outra
                    mensagem.
                  </Alert>
                )}

              <Box sx={{ display: 'flex', alignItems: 'flex-end' }}>
                <TextField
                  fullWidth
                  multiline
                  rows={1}
                  maxRows={4}
                  placeholder={
                    usuarioPodeEnviar
                      ? 'Escreva uma mensagem...'
                      : modoToken && !isAdmin
                        ? 'Aguardando resposta do administrador...'
                        : 'Digite uma mensagem...'
                  }
                  variant='outlined'
                  value={newMessage}
                  onChange={(e) => setNewMessage(e.target.value)}
                  onKeyDown={(e) => {
                    if (e.key === 'Enter' && !e.shiftKey && usuarioPodeEnviar) {
                      e.preventDefault();
                      handleSendMessage();
                    }
                  }}
                  disabled={sending || !usuarioPodeEnviar}
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
                  disabled={!newMessage.trim() || sending || !usuarioPodeEnviar}
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
            </Box>
          </Paper>
        </Box>
        <ProvasModal
          open={modalProvasOpen}
          provas={provas}
          indexAtual={indexSeguro}
          onClose={fecharModalProvas}
          onChangeIndex={setProvaIndexSelecionada}
        />
      </Container>

      {/* Modal de Informações do Denunciante */}
      <Dialog
        open={modalDenuncianteAberto}
        onClose={() => setModalDenuncianteAberto(false)}
        maxWidth='sm'
        fullWidth
      >
        <DialogTitle
          sx={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            pb: 1,
          }}
        >
          <Typography variant='h6' sx={{ fontWeight: 600 }}>
            Informações do Denunciante
          </Typography>
          <IconButton
            onClick={() => setModalDenuncianteAberto(false)}
            size='small'
          >
            <span className='material-symbols-outlined'>close</span>
          </IconButton>
        </DialogTitle>
        <DialogContent>
          {detalhes?.denunciante && (
            <Stack spacing={3} sx={{ mt: 1 }}>
              <Box>
                <Typography
                  variant='caption'
                  sx={{ color: 'text.secondary', fontWeight: 600 }}
                >
                  Nome Completo
                </Typography>
                <Typography variant='body1' sx={{ fontWeight: 500, mt: 0.5 }}>
                  {detalhes.denunciante.nomeCompleto}
                </Typography>
              </Box>
              <Box>
                <Typography
                  variant='caption'
                  sx={{ color: 'text.secondary', fontWeight: 600 }}
                >
                  Grau
                </Typography>
                <Typography variant='body1' sx={{ fontWeight: 500, mt: 0.5 }}>
                  {formatarGrau(detalhes.denunciante.grau)}
                </Typography>
              </Box>
              <Box>
                <Typography
                  variant='caption'
                  sx={{ color: 'text.secondary', fontWeight: 600 }}
                >
                  Curso
                </Typography>
                <Typography variant='body1' sx={{ fontWeight: 500, mt: 0.5 }}>
                  {formatarCurso(detalhes.denunciante.curso)}
                </Typography>
              </Box>
              {detalhes.denunciante.ano && (
                <Box>
                  <Typography
                    variant='caption'
                    sx={{ color: 'text.secondary', fontWeight: 600 }}
                  >
                    Ano
                  </Typography>
                  <Typography variant='body1' sx={{ fontWeight: 500, mt: 0.5 }}>
                    {formatarAno(detalhes.denunciante.ano)}
                  </Typography>
                </Box>
              )}
              <Box>
                <Typography
                  variant='caption'
                  sx={{ color: 'text.secondary', fontWeight: 600 }}
                >
                  Turma
                </Typography>
                <Typography variant='body1' sx={{ fontWeight: 500, mt: 0.5 }}>
                  {formatarTurma(detalhes.denunciante.turma)}
                </Typography>
              </Box>
            </Stack>
          )}
        </DialogContent>
      </Dialog>
    </Box>
  );
}
