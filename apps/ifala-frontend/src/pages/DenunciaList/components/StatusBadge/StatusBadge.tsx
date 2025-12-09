import type { StatusConfig } from '../../types/denunciaTypes';
import './StatusBadge.css';

interface StatusBadgeProps {
  status: string;
}

export const StatusBadge = ({ status }: StatusBadgeProps) => {
  const statusConfig: StatusConfig = {
    RESOLVIDO: { label: 'Resolvido', className: 'status-resolvido' },
    REJEITADO: { label: 'Rejeitado', className: 'status-rejeitado' },
    AGUARDANDO: {
      label: 'Aguardando\nInformações',
      className: 'status-aguardando',
    },
    EM_ANALISE: { label: 'Em Análise', className: 'status-analise' },
    RECEBIDO: { label: 'Recebido', className: 'status-recebido' },
  };

  const cfg = statusConfig[status] || {
    label: status,
    className: 'status-default',
  };

  return <span className={`status-badge ${cfg.className}`}>{cfg.label}</span>;
};
