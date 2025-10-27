import type { Denuncia } from '../../types/denunciaTypes';
import { StatusBadge } from '../StatusBadge/StatusBadge';
import './DenunciaCard.css';

interface DenunciaCardProps {
  denuncia: Denuncia;
  contador: number;
  onViewDetails: (token: string) => void;
  onViewMessages?: (token: string) => void;
}

export const DenunciaCard = ({
  denuncia,
  contador,
  onViewDetails,
}: DenunciaCardProps) => {
  const formatDate = (dateString: string) =>
    new Date(dateString).toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });

  // Função para formatar o token (últimos 4 dígitos com asteriscos)
  const formatToken = (token: string) => {
    if (token.length <= 4) return token;
    return `*******${token.slice(-4)}`;
  };

  // Função para obter o ícone da categoria
  const getCategoriaIcon = (categoria: string) => {
    const icons: { [key: string]: string } = {
      ASSEDIO: 'warning',
      VIOLENCIA: 'security',
      DISCRIMINACAO: 'diversity_3',
      OUTROS: 'help',
    };
    return icons[categoria] || 'help';
  };

  // Lógica básica para verificar se há mensagens não lidas (para testes)
  const hasUnreadMessages = denuncia.hasUnreadMessages || Math.random() > 0.5;

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
    <div className={`denuncia-card ${getPriorityColor(denuncia.status)}`}>
      {/* Header com contador e token */}
      <div className='denuncia-header'>
        <div className='header-left'>
          <div className='denuncia-token'>
            <span className='material-symbols-outlined token-icon'>
              fingerprint
            </span>
            <span className='token-value'>
              Denúncia {contador}: {formatToken(denuncia.token)}
            </span>
          </div>
        </div>
        <StatusBadge status={denuncia.status} />
      </div>

      {/* Conteúdo principal */}
      <div className='denuncia-content'>
        {/* Categoria da denúncia com ícone */}
        <div className='denuncia-metadata'>
          <div className='metadata-item categoria'>
            <span
              className={`material-symbols-outlined icon ${getCategoriaIcon(denuncia.categoria)}`}
            >
              {getCategoriaIcon(denuncia.categoria)}
            </span>
            <span className='metadata-text'>{denuncia.categoria}</span>
          </div>

          {/* Datas */}
          <div className='metadata-item data'>
            <span className='material-symbols-outlined icon'>
              calendar_today
            </span>
            <span className='metadata-text'>
              Criado em: {formatDate(denuncia.dataCriacao)}
            </span>
          </div>
          <div className='metadata-item data'>
            <span className='material-symbols-outlined icon'>update</span>
            <span className='metadata-text'>
              Atualizado em: {formatDate(denuncia.ultimaAtualizacao)}
            </span>
          </div>
        </div>

        {/* Nova Mensagem (se houver mensagens não lidas) */}
        {hasUnreadMessages && (
          <div className='unread-messages-alert'>
            <div className='unread-messages-btn'>
              <span className='alert-icon material-symbols-outlined'>
                mark_email_unread
              </span>
              <span className='alert-text'>Nova Mensagem</span>
            </div>
          </div>
        )}
      </div>

      {/* Ação - Ver Detalhes */}
      <div className='denuncia-actions'>
        <button
          className='btn-details'
          title='Ver Detalhes da Denúncia'
          onClick={() => onViewDetails(denuncia.token)}
        >
          <span className='material-symbols-outlined'>visibility</span>
          <span>Ver Detalhes</span>
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
