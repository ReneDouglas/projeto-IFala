import type { Denuncia } from '../../types/denunciaTypes';
import { StatusBadge } from '../StatusBadge/StatusBadge';
import './DenunciaCard.css';

interface DenunciaCardProps {
  denuncia: Denuncia;
  onViewDetails: (token: string) => void;
  onViewMessages: (token: string) => void;
}

export const DenunciaCard = ({
  denuncia,
  onViewDetails,
  onViewMessages,
}: DenunciaCardProps) => {
  const formatDate = (dateString: string) =>
    new Date(dateString).toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });

  const getCategoriaIcon = (categoria: string) => {
    const icons: { [key: string]: string } = {
      ASSEDIO: 'warning',
      VIOLENCIA: 'security',
      DISCRIMINACAO: 'diversity_3',
      OUTROS: 'help',
    };
    return icons[categoria] || 'help';
  };

  const getPriorityColor = (status: string) => {
    const priorities: { [key: string]: string } = {
      EM_ANALISE: 'high-priority',
      AGUARDANDO_INFORMACOES: 'medium-priority',
      RECEPTADO: 'low-priority',
      RESOLVIDO: 'resolved',
      REJEITADO: 'rejected',
    };
    return priorities[status] || 'default';
  };

  return (
    <div
      className={`denuncia-card bg-white ${getPriorityColor(denuncia.status)}`}
    >
      {/* Header com token e status */}
      <div className='denuncia-header'>
        <div className='header-left'>
          <div className='denuncia-token'>
            <span className='material-symbols-outlined token-icon'>
              fingerprint
            </span>
            <span className='token-value'>{denuncia.token}</span>
          </div>
        </div>
        <StatusBadge status={denuncia.status} />
      </div>

      {/* Conteúdo principal */}
      <div className='denuncia-content'>
        <h4 className='denuncia-titulo'>{denuncia.titulo}</h4>

        <p className='denuncia-descricao'>
          {denuncia.descricao.length > 120
            ? `${denuncia.descricao.substring(0, 120)}...`
            : denuncia.descricao}
        </p>

        {/* Metadados */}
        <div className='denuncia-metadata'>
          <div className='metadata-item categoria'>
            <span
              className={`material-symbols-outlined icon ${getCategoriaIcon(denuncia.categoria)}`}
            >
              {getCategoriaIcon(denuncia.categoria)}
            </span>
            <span className='metadata-text'>{denuncia.categoria}</span>
          </div>

          <div className='metadata-item data'>
            <span className='material-symbols-outlined icon'>
              calendar_today
            </span>
            <span className='metadata-text'>
              Criado em {formatDate(denuncia.dataCriacao)}
            </span>
          </div>
        </div>

        {/* Notificação de mensagens não lidas */}
        {denuncia.hasUnreadMessages && (
          <div className='unread-messages-alert'>
            <button
              className='unread-messages-btn'
              onClick={() => onViewMessages(denuncia.token)}
            >
              <span className='alert-icon material-symbols-outlined'>
                mark_email_unread
              </span>
              <span className='alert-text'>Novas mensagens não lidas</span>
              <span className='alert-badge'>!</span>
            </button>
          </div>
        )}
      </div>

      {/* Ações */}
      <div className='denuncia-actions'>
        <button
          className='btn-details'
          onClick={() => onViewDetails(denuncia.token)}
        >
          <span className='material-symbols-outlined'>visibility</span>
          Ver Detalhes
        </button>

        <button
          className='btn-messages'
          onClick={() => onViewMessages(denuncia.token)}
        >
          <span className='material-symbols-outlined'>chat</span>
          Mensagens
        </button>
      </div>

      {/* Footer */}
      <div className='denuncia-footer'>
        <div className='footer-content'>
          <span className='material-symbols-outlined footer-icon'>update</span>
          <span className='ultima-atualizacao'>
            Atualizado em {formatDate(denuncia.ultimaAtualizacao)}
          </span>
        </div>
      </div>

      {/* Efeito de hover */}
      <div className='card-hover-effect'></div>
    </div>
  );
};
