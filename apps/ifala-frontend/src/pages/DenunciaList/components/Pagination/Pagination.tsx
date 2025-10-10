import './Pagination.css';

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

export const Pagination = ({ currentPage, totalPages, onPageChange }: PaginationProps) => {
  if (totalPages <= 1) return null;

  return (
    <div className="pagination">
      <button
        className="pagination-btn border-primary text-primary"
        disabled={currentPage === 0}
        onClick={() => onPageChange(currentPage - 1)}
      >
        <span className="material-symbols-outlined">chevron_left</span>
        Anterior
      </button>

      <div className="pagination-pages">
        {Array.from({ length: totalPages }, (_, i) => (
          <button
            key={i}
            className={`pagination-page border-primary ${
              currentPage === i 
                ? 'bg-primary text-white' 
                : 'text-primary'
            }`}
            onClick={() => onPageChange(i)}
          >
            {i + 1}
          </button>
        ))}
      </div>

      <button
        className="pagination-btn border-primary text-primary"
        disabled={currentPage === totalPages - 1}
        onClick={() => onPageChange(currentPage + 1)}
      >
        Próxima
        <span className="material-symbols-outlined">chevron_right</span>
      </button>
    </div>
  );
};