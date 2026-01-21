import './Pagination.css';

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

export const Pagination = ({
  currentPage,
  totalPages,
  onPageChange,
}: PaginationProps) => {
  if (totalPages <= 1) return null;

  const getPageNumbers = () => {
    const pages: (number | string)[] = [];
    const maxVisible = 5;

    if (totalPages <= maxVisible + 2) {
      return Array.from({ length: totalPages }, (_, i) => i);
    }

    pages.push(0);

    if (currentPage <= 2) {
      for (let i = 1; i < maxVisible; i++) pages.push(i);
      pages.push('...');
    } else if (currentPage >= totalPages - 3) {
      pages.push('...');
      for (let i = totalPages - maxVisible + 1; i < totalPages - 1; i++)
        pages.push(i);
    } else {
      pages.push('...');
      pages.push(currentPage - 1);
      pages.push(currentPage);
      pages.push(currentPage + 1);
      pages.push('...');
    }

    pages.push(totalPages - 1);
    return pages;
  };

  return (
    <div className='pagination'>
      <button
        className='pagination-btn border-primary text-primary'
        disabled={currentPage === 0}
        onClick={() => onPageChange(currentPage - 1)}
      >
        <span className='material-symbols-outlined'>chevron_left</span>
        Anterior
      </button>

      <div className='pagination-pages'>
        {getPageNumbers().map((page, idx) =>
          typeof page === 'string' ? (
            <span key={`ellipsis-${idx}`} className='pagination-ellipsis'>
              {page}
            </span>
          ) : (
            <button
              key={page}
              className={`pagination-page border-primary ${
                currentPage === page ? 'active' : 'text-primary'
              }`}
              onClick={() => onPageChange(page)}
            >
              {page + 1}
            </button>
          ),
        )}
      </div>

      <button
        className='pagination-btn border-primary text-primary'
        disabled={currentPage === totalPages - 1}
        onClick={() => onPageChange(currentPage + 1)}
      >
        Próxima
        <span className='material-symbols-outlined'>chevron_right</span>
      </button>
    </div>
  );
};
