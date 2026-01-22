import type { Denuncia } from '../../types/denunciaTypes';
import { StatusBadge } from '../StatusBadge/StatusBadge';
import { useAuth } from '../../../../hooks/useAuth';
import './DenunciaCard.css';

interface DenunciaCardProps {
  denuncia: Denuncia;
  contador: number;
  onViewDetails: (denunciaId: number) => void;
  onAcompanhar?: (denunciaId: number) => void;
  onDesacompanhar?: (denunciaId: number) => void;
}

export const DenunciaCard = ({
  denuncia,
  contador,
  onViewDetails,
  onAcompanhar,
  onDesacompanhar,
}: DenunciaCardProps) => {
  const { user } = useAuth();
  const currentUserEmail = user?.email;

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
      VIOLENCIA: 'sports_mma',
      VANDALISMO: 'bomb',
      BULLYING: 'sentiment_very_dissatisfied',
      DROGAS: 'cannabis',
      ACADEMICO: 'school',
      DISPOSITIVO_ELETRONICO: 'smartphone',
      OUTROS: 'help',
    };
    return icons[categoria] || 'help';
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
            <span className='metadata-text'>
              {formatarCategoria(denuncia.categoria)}
            </span>
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

          {/* Tag do admin acompanhando */}
          <div className='metadata-item admin-acompanhando'>
            <span className='material-symbols-outlined icon'>person</span>
            {denuncia.adminAcompanhandoEmail ? (
              currentUserEmail === denuncia.adminAcompanhandoEmail ? (
                // O próprio admin está acompanhando - mostrar botão de sair
                <button
                  className='btn-sair-denuncia'
                  onClick={(e) => {
                    e.stopPropagation();
                    onDesacompanhar?.(denuncia.id);
                  }}
                  title='Clique para deixar de acompanhar esta denúncia'
                >
                  <span className='material-symbols-outlined'>logout</span>
                  Sair da Denúncia
                </button>
              ) : (
                // Outro admin está acompanhando - mostrar tag com nome
                <span
                  className='admin-tag'
                  title={`${denuncia.adminAcompanhandoNome} está acompanhando esta denúncia`}
                >
                  {denuncia.adminAcompanhandoNome || 'Admin'}
                </span>
              )
            ) : (
              // Nenhum admin acompanhando - mostrar botão de acompanhar
              <button
                className='btn-acompanhar-denuncia'
                onClick={(e) => {
                  e.stopPropagation();
                  onAcompanhar?.(denuncia.id);
                }}
                title='Clique para acompanhar esta denúncia'
              >
                <span className='material-symbols-outlined'>person_add</span>
                Acompanhar Denúncia
              </button>
            )}
          </div>

          {denuncia.temMensagemNaoLida && (
            <div className='metadata-item nova-mensagem-wrapper'>
              <span className='badge nova-mensagem'>Nova Mensagem</span>
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
