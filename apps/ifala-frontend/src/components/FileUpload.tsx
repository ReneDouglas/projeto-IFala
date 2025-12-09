import { useState, useRef } from 'react';
import {
  Box,
  Typography,
  IconButton,
  Alert,
  Paper,
  LinearProgress,
  Stack,
} from '@mui/material';
import {
  CloudUpload as CloudUploadIcon,
  Delete as DeleteIcon,
} from '@mui/icons-material';

interface FileUploadProps {
  onFilesChange: (files: File[]) => void;
  maxSizeMB?: number;
  acceptedFormats?: string[];
  maxFiles?: number;
  error?: boolean;
  helperText?: string;
}

interface FileWithPreview {
  file: File;
  preview: string;
  id: string;
}

export function FileUpload({
  onFilesChange,
  maxSizeMB = 20,
  acceptedFormats = [
    'image/jpeg',
    'image/jpg',
    'image/png',
    'image/gif',
    'image/webp',
    'image/bmp',
  ],
  maxFiles = 10,
  error = false,
  helperText,
}: FileUploadProps) {
  const [files, setFiles] = useState<FileWithPreview[]>([]);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [isDragging, setIsDragging] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const maxSizeBytes = maxSizeMB * 1024 * 1024;

  const formatFileSize = (bytes: number): string => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i];
  };

  const getTotalSize = (filesList: FileWithPreview[]): number => {
    return filesList.reduce((total, f) => total + f.file.size, 0);
  };

  const validateFiles = (newFiles: FileList | null): string | null => {
    if (!newFiles || newFiles.length === 0) return null;

    const fileArray = Array.from(newFiles);

    // Validar número máximo de arquivos
    if (files.length + fileArray.length > maxFiles) {
      return `Você pode enviar no máximo ${maxFiles} arquivos`;
    }

    // Validar formato dos arquivos
    for (const file of fileArray) {
      if (!acceptedFormats.includes(file.type.toLowerCase())) {
        return `Formato não permitido: ${file.name}. Apenas imagens são aceitas (JPEG, PNG, GIF, WebP, BMP)`;
      }
    }

    // Validar tamanho total
    const currentSize = getTotalSize(files);
    const newSize = fileArray.reduce((total, f) => total + f.size, 0);
    if (currentSize + newSize > maxSizeBytes) {
      return `O tamanho total dos arquivos excede ${maxSizeMB}MB`;
    }

    return null;
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFiles = event.target.files;
    addFiles(selectedFiles);
  };

  const addFiles = (selectedFiles: FileList | null) => {
    const validationError = validateFiles(selectedFiles);
    if (validationError) {
      setErrorMessage(validationError);
      return;
    }

    if (!selectedFiles) return;

    const newFilesWithPreview: FileWithPreview[] = Array.from(
      selectedFiles,
    ).map((file) => ({
      file,
      preview: URL.createObjectURL(file),
      id: `${file.name}-${Date.now()}-${Math.random()}`,
    }));

    const updatedFiles = [...files, ...newFilesWithPreview];
    setFiles(updatedFiles);
    setErrorMessage(null);

    // Notificar componente pai
    onFilesChange(updatedFiles.map((f) => f.file));

    // Limpar input
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  const handleRemoveFile = (id: string) => {
    const fileToRemove = files.find((f) => f.id === id);
    if (fileToRemove) {
      URL.revokeObjectURL(fileToRemove.preview);
    }

    const updatedFiles = files.filter((f) => f.id !== id);
    setFiles(updatedFiles);
    onFilesChange(updatedFiles.map((f) => f.file));
    setErrorMessage(null);
  };

  const handleDragEnter = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragging(true);
  };

  const handleDragLeave = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragging(false);
  };

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragging(false);

    const droppedFiles = e.dataTransfer.files;
    addFiles(droppedFiles);
  };

  const totalSize = getTotalSize(files);
  const sizePercentage = (totalSize / maxSizeBytes) * 100;

  return (
    <Box>
      <Typography component='label' sx={{ fontWeight: 'medium', mb: 1 }}>
        Provas/Evidências (Opcional)
      </Typography>

      {/* Área de upload */}
      <Paper
        variant='outlined'
        onDragEnter={handleDragEnter}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
        sx={{
          p: 3,
          textAlign: 'center',
          cursor: 'pointer',
          borderStyle: 'dashed',
          borderWidth: 2,
          borderColor: error
            ? 'error.main'
            : isDragging
              ? 'var(--verde-esperanca)'
              : 'divider',
          backgroundColor: isDragging
            ? 'var(--verde-esperanca-10)'
            : 'background.paper',
          transition: 'all 0.2s',
          '&:hover': {
            borderColor: 'var(--verde-esperanca)',
            backgroundColor: 'var(--verde-esperanca-10)',
          },
        }}
        onClick={() => fileInputRef.current?.click()}
      >
        <input
          ref={fileInputRef}
          type='file'
          multiple
          accept={acceptedFormats.join(',')}
          onChange={handleFileChange}
          style={{ display: 'none' }}
        />
        <CloudUploadIcon
          sx={{ fontSize: 48, color: 'var(--verde-esperanca)', mb: 1 }}
        />
        <Typography variant='body1' sx={{ mb: 1 }}>
          <strong>Clique para selecionar</strong> ou arraste os arquivos aqui
        </Typography>
        <Typography variant='caption' color='text.secondary'>
          Formatos aceitos: JPEG, PNG, GIF, WebP, BMP
        </Typography>
        <Typography variant='caption' color='text.secondary' display='block'>
          Tamanho máximo total: {maxSizeMB}MB
        </Typography>
      </Paper>

      {/* Mensagens de erro */}
      {(errorMessage || helperText) && (
        <Alert
          severity={error || errorMessage ? 'error' : 'info'}
          sx={{ mt: 1 }}
        >
          {errorMessage || helperText}
        </Alert>
      )}

      {/* Barra de progresso de tamanho */}
      {files.length > 0 && (
        <Box sx={{ mt: 2 }}>
          <Box
            sx={{
              display: 'flex',
              justifyContent: 'space-between',
              mb: 0.5,
            }}
          >
            <Typography variant='caption' color='text.secondary'>
              Tamanho total: {formatFileSize(totalSize)} / {maxSizeMB}MB
            </Typography>
            <Typography variant='caption' color='text.secondary'>
              {files.length} / {maxFiles} arquivos
            </Typography>
          </Box>
          <LinearProgress
            variant='determinate'
            value={Math.min(sizePercentage, 100)}
            sx={{
              height: 8,
              borderRadius: 4,
              backgroundColor: 'grey.300',
              '& .MuiLinearProgress-bar': {
                borderRadius: 4,
                backgroundColor:
                  sizePercentage > 100
                    ? 'error.main'
                    : 'var(--verde-esperanca)',
              },
            }}
          />
        </Box>
      )}

      {/* Lista de arquivos selecionados */}
      {files.length > 0 && (
        <Stack spacing={1} sx={{ mt: 2 }}>
          {files.map((fileWithPreview) => (
            <Paper
              key={fileWithPreview.id}
              variant='outlined'
              sx={{
                p: 1.5,
                display: 'flex',
                alignItems: 'center',
                gap: 2,
              }}
            >
              {/* Preview da imagem */}
              <Box
                component='img'
                src={fileWithPreview.preview}
                alt={fileWithPreview.file.name}
                sx={{
                  width: 60,
                  height: 60,
                  objectFit: 'cover',
                  borderRadius: 1,
                  border: '1px solid',
                  borderColor: 'divider',
                }}
              />

              {/* Informações do arquivo */}
              <Box sx={{ flex: 1, minWidth: 0 }}>
                <Typography
                  variant='body2'
                  noWrap
                  sx={{ fontWeight: 'medium' }}
                >
                  {fileWithPreview.file.name}
                </Typography>
                <Typography variant='caption' color='text.secondary'>
                  {formatFileSize(fileWithPreview.file.size)}
                </Typography>
              </Box>

              {/* Botão de remover */}
              <IconButton
                size='small'
                color='error'
                onClick={(e) => {
                  e.stopPropagation();
                  handleRemoveFile(fileWithPreview.id);
                }}
                aria-label={`Remover ${fileWithPreview.file.name}`}
              >
                <DeleteIcon />
              </IconButton>
            </Paper>
          ))}
        </Stack>
      )}
    </Box>
  );
}
