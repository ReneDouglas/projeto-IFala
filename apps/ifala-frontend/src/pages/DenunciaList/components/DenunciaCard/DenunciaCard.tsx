import type { Denuncia } from '../../types/denunciaTypes';
import { StatusBadge } from '../StatusBadge/StatusBadge';
import './DenunciaCard.css';

interface DenunciaCardProps {
  denuncia: Denuncia;
  contador: number;
  onViewDetails: (denunciaId: number) => void;
}

export const DenunciaCard = ({
  denuncia,
  contador,
  onViewDetails,
}: DenunciaCardProps) => {
  const formatDate = (dateString: string) => {
    if (!dateString) return '---';

    const date = new Date(dateString);
    if (isNaN(date.getTime())) return '---';

    return date.toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const formatToken = (token: string) => {
    if (!token) return '---';
    if (token.length <= 4) return token;
    return `*****${token.slice(-4)}`;
  };

  const getCategoriaIcon = (categoria: string) => {
    const icons: Record<string, string> = {
      VIOLENCIA: 'security',
      VANDALISMO: 'build',
      BULLYING: 'sentiment_very_dissatisfied',
      DROGAS: 'no_drinks',
      ACADEMICO: 'menu_book',
      OUTROS: 'help',
    };
    return icons[categoria] || 'help';
  };

  const getPriorityColor = (status: string) => {
    const priorities: Record<string, string> = {
      EM_ANALISE: 'high-priority',
      AGUARDANDO: 'medium-priority',
      RECEBIDO: 'low-priority',
      RESOLVIDO: 'resolved',
      REJEITADO: 'rejected',
    };
    return priorities[status] || 'default';
  };

  return (
    <div className={`denuncia-card ${getPriorityColor(denuncia.status)}`}>
      <div className='denuncia-header'>
        <div className='header-left'>
          <div className='denuncia-token'>
            <span className='material-symbols-outlined token-icon'>
              fingerprint
            </span>
            <span className='token-value'>
              Denúncia {contador}:{' '}
              {formatToken(String(denuncia.tokenAcompanhamento))}
            </span>
          </div>
        </div>

        <div className='header-right'>
          <StatusBadge status={denuncia.status} />
        </div>
      </div>

      <div className='denuncia-content'>
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
              Criado em: {formatDate(denuncia.criadoEm)}
            </span>
          </div>

          <div className='metadata-item data'>
            <span className='material-symbols-outlined icon'>update</span>
            <span className='metadata-text'>
              Atualizado em:{' '}
              {denuncia.alteradoEm ? formatDate(denuncia.alteradoEm) : '---'}
            </span>
          </div>

          {denuncia.temMensagemNaoLida && (
            <div className='metadata-item nova-mensagem-wrapper'>
              <span className='badge nova-mensagem'>Nova mensagem</span>
            </div>
          )}
        </div>
      </div>

      <div className='denuncia-actions'>
        <button
          className='btn-details'
          title='Ver Detalhes da Denúncia'
          onClick={() => onViewDetails(denuncia.id)} // passar id
        >
          <span className='material-symbols-outlined'>visibility</span>
          <span>Ver Detalhes</span>
        </button>
      </div>

      <div className='card-hover-effect'></div>
    </div>
  );
};
