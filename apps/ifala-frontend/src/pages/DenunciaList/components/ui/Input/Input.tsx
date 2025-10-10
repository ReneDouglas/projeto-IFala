import './Input.css';

interface InputProps {
  label: string;
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  error?: string;
  icon?: string;
  type?: 'text' | 'email' | 'password';
  disabled?: boolean;
  required?: boolean;
}

export const Input = ({
  label,
  value,
  onChange,
  placeholder,
  error,
  icon,
  type = 'text',
  disabled = false,
  required = false
}: InputProps) => {
  return (
    <div className="input-group">
      <label className="input-label">
        {label}
        {required && <span className="required-asterisk">*</span>}
      </label>
      
      <div className="input-container">
        {icon && (
          <span className="input-icon material-symbols-outlined">
            {icon}
          </span>
        )}
        
        <input
          type={type}
          value={value}
          onChange={(e) => onChange(e.target.value)}
          placeholder={placeholder}
          disabled={disabled}
          className={`custom-input ${icon ? 'with-icon' : ''} ${error ? 'error' : ''} ${
            disabled ? 'disabled' : ''
          }`}
        />
        
        {value && !error && !disabled && (
          <span className="input-success material-symbols-outlined">
            check_circle
          </span>
        )}
      </div>
      
      {error && <span className="error-message">{error}</span>}
      
      {!error && value && (
        <span className="success-message">Campo válido</span>
      )}
    </div>
  );
};