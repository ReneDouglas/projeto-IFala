import { useEffect, useMemo, useRef, useState } from 'react';
import {
  Alert,
  Box,
  Dialog,
  DialogContent,
  IconButton,
  Tooltip,
  Typography,
  useMediaQuery,
  useTheme,
} from '@mui/material';
import { getProvaUrl } from '../services/api';
import './ProvasModal.css';

export type ProvaItem = {
  id: number;
  nomeArquivo: string;
  tipoMime: string;
};

type ProvasModalProps = {
  open: boolean;
  provas: ProvaItem[];
  indexAtual: number;
  onClose: () => void;
  onChangeIndex: (nextIndex: number) => void;
  showOpenInNewTab?: boolean;
};

const clamp = (v: number, min: number, max: number) =>
  Math.max(min, Math.min(max, v));

export function ProvasModal({
  open,
  provas,
  indexAtual,
  onClose,
  onChangeIndex,
  showOpenInNewTab = true,
}: ProvasModalProps) {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const [zoom, setZoom] = useState(1);
  const [imgLoading, setImgLoading] = useState(true);
  const [imgError, setImgError] = useState(false);

  const thumbsRef = useRef<HTMLDivElement>(null);

  const provaSelecionada = useMemo(
    () => provas[indexAtual] ?? null,
    [provas, indexAtual],
  );

  useEffect(() => {
    if (!open) return;
    setZoom(1);
    setImgLoading(true);
    setImgError(false);
  }, [open, indexAtual]);

  // Se estiver aberto e a lista esvaziar, fecha por segurança
  useEffect(() => {
    if (open && provas.length === 0) onClose();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open, provas.length]);

  // Garantir que a miniatura ativa fique visível
  useEffect(() => {
    if (!open) return;
    const container = thumbsRef.current;
    if (!container) return;

    const active = container.querySelector<HTMLButtonElement>(
      `[data-thumb-index="${indexAtual}"]`,
    );
    if (!active) return;

    active.scrollIntoView({
      behavior: 'smooth',
      inline: 'center',
      block: 'nearest',
    });
  }, [open, indexAtual]);

  const handleProxima = () => {
    if (provas.length <= 1) return;
    onChangeIndex((indexAtual + 1) % provas.length);
  };

  const handleAnterior = () => {
    if (provas.length <= 1) return;
    onChangeIndex((indexAtual - 1 + provas.length) % provas.length);
  };

  const handleZoomIn = () =>
    setZoom((z) => clamp(+(z + 0.25).toFixed(2), 0.5, 3));
  const handleZoomOut = () =>
    setZoom((z) => clamp(+(z - 0.25).toFixed(2), 0.5, 3));
  const handleResetZoom = () => setZoom(1);

  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth='md'
      fullWidth
      fullScreen={isMobile}
      PaperProps={{ className: 'provasModal-paper' }}
      onKeyDown={(e) => {
        if (e.key === 'ArrowRight') handleProxima();
        if (e.key === 'ArrowLeft') handleAnterior();
      }}
    >
      {/* Header compacto */}
      <Box className='provasModal-header'>
        <Box className='provasModal-titleWrap'>
          <Typography className='provasModal-title' variant='subtitle1'>
            {provaSelecionada?.nomeArquivo ?? 'Prova/Evidência'}
          </Typography>

          <Typography className='provasModal-subtitle' variant='caption'>
            {provas.length > 0 ? `${indexAtual + 1} de ${provas.length}` : ''}
            {provaSelecionada?.tipoMime
              ? ` • ${provaSelecionada.tipoMime}`
              : ''}
          </Typography>
        </Box>

        <Box className='provasModal-actions'>
          {/* Navegação (sempre visível) */}
          <Tooltip title='Anterior (←)'>
            <span>
              <IconButton
                size='small'
                onClick={handleAnterior}
                disabled={provas.length <= 1}
              >
                <span className='material-symbols-outlined'>chevron_left</span>
              </IconButton>
            </span>
          </Tooltip>

          <Tooltip title='Próxima (→)'>
            <span>
              <IconButton
                size='small'
                onClick={handleProxima}
                disabled={provas.length <= 1}
              >
                <span className='material-symbols-outlined'>chevron_right</span>
              </IconButton>
            </span>
          </Tooltip>

          {/* Zoom (somente desktop) */}
          {!isMobile && (
            <>
              <Tooltip title='Diminuir zoom'>
                <span>
                  <IconButton
                    size='small'
                    onClick={handleZoomOut}
                    disabled={zoom <= 0.5}
                  >
                    <span className='material-symbols-outlined'>zoom_out</span>
                  </IconButton>
                </span>
              </Tooltip>

              <Tooltip title='Resetar zoom'>
                <IconButton size='small' onClick={handleResetZoom}>
                  <span className='material-symbols-outlined'>restart_alt</span>
                </IconButton>
              </Tooltip>

              <Tooltip title='Aumentar zoom'>
                <span>
                  <IconButton
                    size='small'
                    onClick={handleZoomIn}
                    disabled={zoom >= 3}
                  >
                    <span className='material-symbols-outlined'>zoom_in</span>
                  </IconButton>
                </span>
              </Tooltip>
            </>
          )}

          {/* Abrir em nova aba (sempre visível se habilitado) */}
          {showOpenInNewTab && provaSelecionada && (
            <Tooltip title='Abrir em nova aba'>
              <IconButton
                size='small'
                component='a'
                href={getProvaUrl(provaSelecionada.id)}
                target='_blank'
                rel='noopener noreferrer'
              >
                <span className='material-symbols-outlined'>open_in_new</span>
              </IconButton>
            </Tooltip>
          )}

          {/* Fechar (sempre visível) */}
          <Tooltip title='Fechar (Esc)'>
            <IconButton size='small' onClick={onClose}>
              <span className='material-symbols-outlined'>close</span>
            </IconButton>
          </Tooltip>
        </Box>
      </Box>

      <DialogContent className='provasModal-content'>
        {provaSelecionada ? (
          <Box
            className='provasModal-viewer'
            style={{ height: isMobile ? 'calc(100vh - 210px)' : '72vh' }}
          >
            {/* Overlay inferior (zoom %) */}
            <Box className='provasModal-overlay'>
              <Typography className='provasModal-overlayText' variant='caption'>
                {Math.round(zoom * 100)}%
              </Typography>
            </Box>

            {imgError ? (
              <Box className='provasModal-messageWrap'>
                <Alert severity='error'>
                  Não foi possível carregar a imagem. Use “Abrir em nova aba”.
                </Alert>
              </Box>
            ) : (
              <>
                {imgLoading && (
                  <Box className='provasModal-loadingPill'>
                    <Typography
                      variant='caption'
                      className='provasModal-loadingText'
                    >
                      Carregando...
                    </Typography>
                  </Box>
                )}

                <img
                  key={provaSelecionada.id} // ✅ garante que troca realmente a imagem
                  className='provasModal-image'
                  src={getProvaUrl(provaSelecionada.id)}
                  alt={provaSelecionada.nomeArquivo}
                  onLoad={() => setImgLoading(false)}
                  onError={() => {
                    setImgLoading(false);
                    setImgError(true);
                  }}
                  style={{
                    transform: `scale(${zoom})`,
                  }}
                />
              </>
            )}
          </Box>
        ) : null}

        {/* Filmstrip de miniaturas */}
        {provas.length > 1 && (
          <Box className='provasModal-filmstrip' ref={thumbsRef}>
            {provas.map((p, i) => {
              const ativo = i === indexAtual;
              return (
                <button
                  key={p.id}
                  type='button'
                  data-thumb-index={i}
                  className={`provasModal-thumbBtn ${ativo ? 'is-active' : ''}`}
                  onClick={() => onChangeIndex(i)}
                  aria-label={`Abrir ${p.nomeArquivo}`}
                >
                  <img
                    className='provasModal-thumbImg'
                    src={getProvaUrl(p.id)}
                    alt={p.nomeArquivo}
                    loading='lazy'
                  />
                </button>
              );
            })}
          </Box>
        )}
      </DialogContent>
    </Dialog>
  );
}
