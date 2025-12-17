import React, { useEffect, useState, useRef, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  listarNotificacoes,
  marcarComoLida,
} from '../../services/notificacao-api';
import type { Notificacao } from '../../types/notificacao';
import './notificacao.css';

const NotificacaoBell: React.FC = () => {
  const navigate = useNavigate();
  const [notificacoes, setNotificacoes] = useState<Notificacao[]>([]);
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [totalItems, setTotalItems] = useState(0);
  const [pageSize, setPageSize] = useState(5);
  const mounted = useRef(true);
  const containerRef = useRef<HTMLDivElement>(null);

  const fetchNotificacoes = useCallback(
    async (page: number) => {
      setLoading(true);
      try {
        // Backend retorna página de notificações (page começa em 0 no backend, mas em 1 no front)
        const data = await listarNotificacoes(page - 1, pageSize);

        if (!mounted.current) return;

        setNotificacoes(data.items);
        setTotalPages(data.totalPages);
        setTotalItems(data.totalItems);
        // Atualiza pageSize com o valor retornado pelo backend
        if (data.pageSize) {
          setPageSize(data.pageSize);
        }
      } catch (err) {
        console.error('Erro ao buscar notificações', err);
      } finally {
        if (mounted.current) setLoading(false);
      }
    },
    [pageSize],
  );

  // Busca notificações ao montar e quando a página atual mudar
  useEffect(() => {
    mounted.current = true;
    fetchNotificacoes(currentPage);

    return () => {
      mounted.current = false;
    };
  }, [currentPage, fetchNotificacoes]);

  // Polling automático a cada 30s
  useEffect(() => {
    const id = window.setInterval(() => {
      fetchNotificacoes(currentPage);
    }, 30000);

    return () => {
      clearInterval(id as unknown as number);
    };
  }, [currentPage, fetchNotificacoes]);

  // Fecha o dropdown ao clicar fora
  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (
        containerRef.current &&
        !containerRef.current.contains(event.target as Node)
      ) {
        setOpen(false);
      }
    }

    if (open) {
      document.addEventListener('mousedown', handleClickOutside);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [open]);

  // Conta apenas as notificações não lidas do total
  const unreadCount = totalItems;

  // Reseta página ao abrir o modal
  useEffect(() => {
    if (open) {
      setCurrentPage(1);
    }
  }, [open]);

  function buildTitle(n: Notificacao) {
    if (n.tipo === 'NOVA_DENUNCIA') return '[IFala] Nova Denúncia Cadastrada';
    if (n.tipo === 'NOVA_MENSAGEM') {
      const id = n.denunciaId ? String(n.denunciaId) : '';
      const short = id.length >= 3 ? id.slice(-3) : id;
      return `[IFala] Nova Mensagem Recebida (Denúncia *******${short})`;
    }
    return n.conteudo ?? 'Notificação';
  }

  async function handleClickNotificacao(n: Notificacao) {
    try {
      if (!n.lida) {
        await marcarComoLida(n.id);
        // Atualiza a notificação local para mostrar como lida
        setNotificacoes((prev) =>
          prev.map((p) => (p.id === n.id ? { ...p, lida: true } : p)),
        );
        // Atualiza o contador
        setTotalItems((prev) => Math.max(0, prev - 1));
      }

      // Fecha o dropdown de notificações
      setOpen(false);

      // Se a notificação tem denunciaId, navega para o acompanhamento dessa denúncia
      if (n.denunciaId) {
        navigate(`/admin/denuncias/${n.denunciaId}/acompanhamento`);
        return;
      }

      // Fallback: vai para o painel de denúncias
      navigate('/painel-denuncias');
    } catch (err) {
      console.error('Erro ao marcar notificação como lida', err);
    }
  }

  return (
    <div ref={containerRef} className='notification-bell-container'>
      <button
        aria-label='Notificações'
        onClick={() => setOpen((s) => !s)}
        className='notification-bell-button'
      >
        {/* sino SVG */}
        <svg
          width='22'
          height='22'
          viewBox='0 0 24 24'
          fill='none'
          xmlns='http://www.w3.org/2000/svg'
        >
          <path d='M12 22c1.1 0 2-.9 2-2H10c0 1.1.9 2 2 2z' fill='#333' />
          <path
            d='M18 16v-5c0-3.07-1.63-5.64-4.5-6.32V4a1.5 1.5 0 0 0-3 0v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z'
            fill='#333'
          />
        </svg>
        {unreadCount > 0 && (
          <span
            aria-label={`${unreadCount} notificações não lidas`}
            className='notification-badge'
          >
            {unreadCount > 99 ? '99+' : unreadCount}
          </span>
        )}
      </button>

      {open && (
        <div
          role='dialog'
          aria-label='Lista de notificações'
          className='notification-modal'
        >
          <div className='notification-header'>
            Notificações{' '}
            {loading && (
              <span className='notification-loading'>(carregando...)</span>
            )}
          </div>
          {notificacoes.length === 0 && (
            <div className='notification-empty'>Nenhuma notificação.</div>
          )}
          <ul className='notification-list'>
            {notificacoes.map((n) => (
              <li key={n.id} className='notification-item'>
                <button
                  onClick={() => handleClickNotificacao(n)}
                  className='notification-item-button'
                >
                  <div className='notification-item-content'>
                    <div className='notification-item-text'>
                      <div className='notification-title'>{buildTitle(n)}</div>
                    </div>
                    {!n.lida && (
                      <div className='notification-unread-indicator'>
                        <span className='notification-unread-dot' />
                      </div>
                    )}
                  </div>
                </button>
              </li>
            ))}
          </ul>
          {notificacoes.length > 0 && (
            <div className='notification-pagination'>
              <button
                onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
                disabled={currentPage === 1}
                className='pagination-button'
                aria-label='Página anterior'
              >
                <span className='material-symbols-outlined'>chevron_left</span>
              </button>
              <span className='pagination-info'>
                {currentPage} / {totalPages}
              </span>
              <button
                onClick={() =>
                  setCurrentPage((p) => Math.min(totalPages, p + 1))
                }
                disabled={currentPage === totalPages}
                className='pagination-button'
                aria-label='Próxima página'
              >
                <span className='material-symbols-outlined'>chevron_right</span>
              </button>
            </div>
          )}
          <div className='notification-footer'>
            <button
              onClick={() => {
                fetchNotificacoes(currentPage);
              }}
              className='notification-refresh-button'
            >
              Atualizar
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
export default NotificacaoBell;
