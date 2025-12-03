import React, { useEffect, useState, useRef } from 'react';
import {
  listarNotificacoes,
  marcarComoLida,
} from '../../services/notificacao-api';
import type { Notificacao } from '../../types/notificacao';

const NotificacaoBell: React.FC = () => {
  const [notificacoes, setNotificacoes] = useState<Notificacao[]>([]);
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const mounted = useRef(true);

  useEffect(() => {
    mounted.current = true;
    fetchNotificacoes();
    const id = window.setInterval(fetchNotificacoes, 30000);

    return () => {
      mounted.current = false;
      clearInterval(id as unknown as number);
    };
  }, []);

  async function fetchNotificacoes() {
    setLoading(true);
    try {
      // buscar apenas não lidas (backend já retorna filtrado e limitado a 10)
      const data = await listarNotificacoes();

      if (!mounted.current) return;

      // Merge para manter notificações já carregadas (inclui as que foram marcadas como lidas)
      // Backend retorna apenas as 10 não lidas mais antigas, então fazemos merge para UX
      setNotificacoes((prev) => {
        const map = new Map<number, Notificacao>();
        prev.forEach((p) => map.set(p.id, p));
        data.forEach((d) => map.set(d.id, d));

        // Ordenar da mais recente para a mais antiga para exibição no dropdown
        return Array.from(map.values()).sort((a, b) => {
          const ta = a.dataEnvio ? new Date(a.dataEnvio).getTime() : 0;
          const tb = b.dataEnvio ? new Date(b.dataEnvio).getTime() : 0;
          return tb - ta;
        });
      });
    } catch (err) {
      console.error('Erro ao buscar notificações', err);
    } finally {
      if (mounted.current) setLoading(false);
    }
  }

  const unreadCount = notificacoes.filter((n) => !n.lida).length;

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
        setNotificacoes((prev) =>
          prev.map((p) => (p.id === n.id ? { ...p, lida: true } : p)),
        );
      }
      window.location.href = '/painel-denuncias';
    } catch (err) {
      console.error('Erro ao marcar notificação como lida', err);
    }
  }

  return (
    <div style={{ position: 'relative', display: 'inline-block' }}>
      <button
        aria-label='Notificações'
        onClick={() => setOpen((s) => !s)}
        style={{
          background: 'transparent',
          border: 'none',
          cursor: 'pointer',
          padding: 6,
        }}
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
            style={{
              position: 'absolute',
              top: -4,
              right: -4,
              minWidth: 18,
              height: 18,
              borderRadius: 18,
              background: '#d6336c',
              color: '#fff',
              display: 'inline-flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontSize: 11,
              padding: '0 6px',
              fontWeight: 600,
              boxSizing: 'border-box',
            }}
          >
            {unreadCount > 99 ? '99+' : unreadCount}
          </span>
        )}
      </button>

      {open && (
        <div
          role='dialog'
          aria-label='Lista de notificações'
          style={{
            position: 'absolute',
            right: 0,
            marginTop: 8,
            width: 360,
            maxHeight: 420,
            overflow: 'auto',
            background: '#fff',
            boxShadow: '0 8px 24px rgba(16,24,40,.12)',
            borderRadius: 8,
            zIndex: 1200,
          }}
        >
          <div
            style={{
              padding: 12,
              borderBottom: '1px solid #eef1f5',
              fontWeight: 600,
            }}
          >
            Notificações{' '}
            {loading && (
              <span style={{ fontWeight: 400, marginLeft: 8 }}>
                (carregando...)
              </span>
            )}
          </div>
          {notificacoes.length === 0 && (
            <div style={{ padding: 14, color: '#666' }}>
              Nenhuma notificação.
            </div>
          )}
          <ul style={{ listStyle: 'none', margin: 0, padding: 0 }}>
            {notificacoes.map((n) => (
              <li key={n.id} style={{ borderBottom: '1px solid #f2f4f6' }}>
                <button
                  onClick={() => handleClickNotificacao(n)}
                  style={{
                    width: '100%',
                    textAlign: 'left',
                    padding: '12px 14px',
                    background: 'transparent',
                    border: 'none',
                    cursor: 'pointer',
                    display: 'block',
                  }}
                >
                  <div
                    style={{
                      display: 'flex',
                      justifyContent: 'space-between',
                      gap: 12,
                    }}
                  >
                    <div style={{ flex: 1 }}>
                      <div style={{ fontSize: 14, fontWeight: 600 }}>
                        {buildTitle(n)}
                      </div>
                      <div
                        style={{ marginTop: 6, color: '#666', fontSize: 13 }}
                      >
                        {n.conteudo && n.conteudo.length > 120
                          ? n.conteudo.slice(0, 120) + '...'
                          : n.conteudo}
                      </div>
                    </div>
                    {!n.lida && (
                      <div style={{ alignSelf: 'center' }}>
                        <span
                          style={{
                            display: 'inline-block',
                            width: 10,
                            height: 10,
                            borderRadius: '50%',
                            background: '#d6336c',
                          }}
                        />
                      </div>
                    )}
                  </div>
                </button>
              </li>
            ))}
          </ul>
          <div style={{ padding: 10, textAlign: 'center' }}>
            <button
              onClick={() => {
                fetchNotificacoes();
              }}
              style={{
                padding: '8px 12px',
                borderRadius: 8,
                border: '1px solid #e6eef9',
                background: '#fff',
                cursor: 'pointer',
              }}
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
