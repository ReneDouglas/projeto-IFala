import './Select.css';

interface SelectOption {
  value: string;
  label: string;
}

interface SelectProps {
  label: string;
  value: string;
  onChange: (value: string) => void;
  options: SelectOption[];
  error?: string;
  placeholder?: string;
  disabled?: boolean;
  required?: boolean;
  icon?: string;
}

export const Select = ({
  label,
  value,
  onChange,
  options,
  error,
  placeholder = 'Selecione uma opção',
  disabled = false,
  required = false,
  icon,
}: SelectProps) => {
  return (
    <div className='select-group'>
      <label className='select-label'>
        {label}
        {required && <span className='required-asterisk'>*</span>}
      </label>

      <div className='select-container'>
        {icon && (
          <span className='select-icon material-symbols-outlined'>{icon}</span>
        )}

        <select
          value={value}
          onChange={(e) => onChange(e.target.value)}
          disabled={disabled}
          className={`custom-select ${icon ? 'with-icon' : ''} ${error ? 'error' : ''} ${
            !value ? 'placeholder' : ''
          }`}
        >
          <option value=''>{placeholder}</option>
          {options.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>

        <span className='select-arrow material-symbols-outlined'>
          expand_more
        </span>

        {value && !error && !disabled && (
          <span className='select-success material-symbols-outlined'>
            check_circle
          </span>
        )}
      </div>

      {error && <span className='error-message'>{error}</span>}
    </div>
  );
};
