import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Typography,
  Box,
  TextField,
  FormControlLabel,
  Radio,
  RadioGroup,
  Stack,
  Paper,
  FormHelperText,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
  Button,
  type SelectChangeEvent,
} from '@mui/material';
import ReCAPTCHA from 'react-google-recaptcha';
import '../../styles/theme.css';

const cursosPorNivel = {
  medio: ['Administração', 'Agropecuária', 'Informática', 'Meio Ambiente'],
  superior: [
    'Análise e Desenvolvimento de Sistemas',
    'Licenciatura em Matemática',
    'Licenciatura em Física',
    'Administração',
    'Gestão Ambiental',
  ],
};

const turmasPorNivel = {
  medio: [
    '1º ano A',
    '1º ano B',
    '2º ano A',
    '2º ano B',
    '3º ano A',
    '3º ano B',
  ],
  superior: [
    'Módulo I',
    'Módulo II',
    'Módulo III',
    'Módulo IV',
    'Módulo V',
    'Módulo VI',
    'Módulo VII',
    'Módulo VIII',
  ],
};

export function Denuncia() {
  const [formData, setFormData] = useState({
    nome: '',
    email: '',
    grau: '',
    curso: '',
    turma: '',
    categoria: '',
    relato: '',
  });
  const [tipoDenuncia, setTipoDenuncia] = useState('anonima');
  const [recaptchaToken, setRecaptchaToken] = useState<string | null>(null);
  const [errors, setErrors] = useState({
    nome: false,
    email: false,
    grau: false,
    curso: false,
    turma: false,
    categoria: false,
    relato: false,
    recaptcha: false,
  });

  const navigate = useNavigate();

  const handleChange = (
    event:
      | React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
      | SelectChangeEvent,
  ) => {
    const { name, value } = event.target;

    if (name === 'grau') {
      setFormData((prev) => ({ ...prev, grau: value, curso: '', turma: '' }));
    } else {
      setFormData((prev) => ({ ...prev, [name]: value }));
    }
  };

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    const newErrors = {
      nome: tipoDenuncia === 'identificada' && formData.nome.trim() === '',
      email:
        tipoDenuncia === 'identificada' && !emailRegex.test(formData.email),
      grau: tipoDenuncia === 'identificada' && formData.grau.trim() === '',
      curso:
        tipoDenuncia === 'identificada' &&
        formData.grau.trim() !== '' &&
        formData.curso.trim() === '',
      turma:
        tipoDenuncia === 'identificada' &&
        formData.grau.trim() !== '' &&
        formData.turma.trim() === '',
      categoria: formData.categoria.trim() === '',
      relato: formData.relato.trim().length < 50,
      recaptcha: !recaptchaToken,
    };

    setErrors(newErrors);
    const hasErrors = Object.values(newErrors).some((error) => error === true);

    if (hasErrors) {
      let errorMessage = 'Por favor, preencha todos os campos obrigatórios.';
      if (newErrors.relato) {
        errorMessage =
          'Por favor, preencha todos os campos obrigatórios e garanta que a descrição tenha no mínimo 50 caracteres.';
      }
      if (newErrors.recaptcha) {
        errorMessage = 'Por favor, complete o desafio "Não sou um robô".';
      }
      alert(errorMessage);
      return;
    }

    const fakeTrackingToken = `IFALA-${Math.random()
      .toString(36)
      .substring(2, 11)
      .toUpperCase()}`;
    navigate('/denuncia/sucesso', { state: { token: fakeTrackingToken } });
  };

  const handleRecaptchaChange = (token: string | null) => {
    setRecaptchaToken(token);
    if (token) {
      setErrors((prevErrors) => ({ ...prevErrors, recaptcha: false }));
    }
  };

  const fieldStyles = {
    '& .MuiOutlinedInput-root': {
      '&:hover fieldset': {
        borderColor: 'var(--verde-esperanca)',
      },
      '&.Mui-focused fieldset': {
        borderColor: 'var(--verde-esperanca)',
      },
    },
    '& .MuiInputLabel-root.Mui-focused': {
      color: 'var(--verde-esperanca)',
    },
  };

  const menuItemStyles = {
    '&:hover': {
      backgroundColor: 'var(--verde-esperanca)',
      color: 'var(--branco)',
    },
    '&.Mui-selected': {
      backgroundColor: 'var(--verde-esperanca-20)',
      color: 'var(--verde-esperanca)',
      '&:hover': {
        backgroundColor: 'var(--verde-esperanca)',
        color: 'var(--branco)',
      },
    },
  };

  return (
    <Box sx={{ backgroundColor: 'background.default', minHeight: '100vh' }}>
      <Container maxWidth='md' sx={{ py: 4 }}>
        <Paper
          component='form'
          onSubmit={handleSubmit}
          variant='outlined'
          sx={{ p: { xs: 2, sm: 4 }, borderRadius: 3 }}
        >
          <Box sx={{ mb: 4 }}>
            <Typography
              variant='h5'
              component='h1'
              gutterBottom
              sx={{ fontWeight: 'bold' }}
            >
              Nova Denúncia
            </Typography>
            <Typography variant='body1' color='text.secondary'>
              Relate ocorrências que acontecem dentro da instituição de forma
              anônima e segura através do IFala.
            </Typography>
          </Box>

          <Stack spacing={3}>
            <FormControl>
              <Typography
                component='label'
                sx={{ fontWeight: 'medium', mb: 1 }}
              >
                Deseja se identificar? *
              </Typography>
              <RadioGroup
                value={tipoDenuncia}
                onChange={(e) => setTipoDenuncia(e.target.value)}
              >
                <FormControlLabel
                  value='anonima'
                  control={
                    <Radio
                      sx={{
                        '&.Mui-checked': { color: 'var(--verde-esperanca)' },
                      }}
                    />
                  }
                  label='Não, prefiro permanecer anônimo'
                />
                <FormControlLabel
                  value='identificada'
                  control={
                    <Radio
                      sx={{
                        '&.Mui-checked': { color: 'var(--verde-esperanca)' },
                      }}
                    />
                  }
                  label='Sim, desejo me identificar'
                />
              </RadioGroup>
            </FormControl>

            {tipoDenuncia === 'identificada' && (
              <Stack
                spacing={3}
                sx={{
                  borderLeft: '3px solid',
                  borderColor: 'var(--verde-esperanca)',
                  pl: 2,
                  ml: 1,
                  py: 2,
                }}
              >
                <Typography variant='h6' sx={{ fontWeight: 'medium' }}>
                  Dados de Identificação
                </Typography>
                <Alert
                  severity='success'
                  icon={false}
                  sx={{
                    backgroundColor: 'var(--verde-esperanca-10)',
                    color: 'var(--cinza-escuro)',
                  }}
                >
                  <strong>Confidencialidade Garantida:</strong> Mesmo se
                  identificando, seus dados serão mantidos em absoluto sigilo.
                  Apenas a administração autorizada terá acesso às informações,
                  e você não será exposto em momento algum durante o processo de
                  investigação.
                </Alert>
                <TextField
                  name='nome'
                  label='Nome Completo *'
                  variant='outlined'
                  fullWidth
                  value={formData.nome}
                  onChange={handleChange}
                  error={errors.nome}
                  helperText={errors.nome ? 'O nome é obrigatório.' : ''}
                  sx={fieldStyles}
                />
                <TextField
                  name='email'
                  label='Email *'
                  variant='outlined'
                  fullWidth
                  value={formData.email}
                  onChange={handleChange}
                  error={errors.email}
                  helperText={
                    errors.email ? 'Por favor, insira um email válido.' : ''
                  }
                  sx={fieldStyles}
                />

                <FormControl fullWidth required error={errors.grau}>
                  <InputLabel
                    sx={{
                      '&.Mui-focused': { color: 'var(--verde-esperanca)' },
                    }}
                  >
                    Grau
                  </InputLabel>
                  <Select
                    name='grau'
                    value={formData.grau}
                    label='Grau *'
                    onChange={handleChange}
                    sx={{
                      '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
                        borderColor: 'var(--verde-esperanca)',
                      },
                      '&:hover .MuiOutlinedInput-notchedOutline': {
                        borderColor: 'var(--verde-esperanca)',
                      },
                    }}
                  >
                    <MenuItem value='medio' sx={menuItemStyles}>
                      Médio
                    </MenuItem>
                    <MenuItem value='superior' sx={menuItemStyles}>
                      Superior
                    </MenuItem>
                  </Select>
                  {errors.grau && (
                    <FormHelperText>O grau é obrigatório.</FormHelperText>
                  )}
                </FormControl>

                {formData.grau && (
                  <>
                    <FormControl fullWidth required error={errors.curso}>
                      <InputLabel
                        sx={{
                          '&.Mui-focused': {
                            color: 'var(--verde-esperanca)',
                          },
                        }}
                      >
                        Curso
                      </InputLabel>
                      <Select
                        name='curso'
                        value={formData.curso}
                        label='Curso *'
                        onChange={handleChange}
                        sx={{
                          '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
                            borderColor: 'var(--verde-esperanca)',
                          },
                          '&:hover .MuiOutlinedInput-notchedOutline': {
                            borderColor: 'var(--verde-esperanca)',
                          },
                        }}
                      >
                        {(
                          cursosPorNivel[
                            formData.grau as 'medio' | 'superior'
                          ] || []
                        ).map((curso) => (
                          <MenuItem
                            key={curso}
                            value={curso}
                            sx={menuItemStyles}
                          >
                            {curso}
                          </MenuItem>
                        ))}
                      </Select>
                      {errors.curso && (
                        <FormHelperText>O curso é obrigatório.</FormHelperText>
                      )}
                    </FormControl>

                    <FormControl fullWidth required error={errors.turma}>
                      <InputLabel
                        sx={{
                          '&.Mui-focused': {
                            color: 'var(--verde-esperanca)',
                          },
                        }}
                      >
                        Turma
                      </InputLabel>
                      <Select
                        name='turma'
                        value={formData.turma}
                        label='Turma *'
                        onChange={handleChange}
                        sx={{
                          '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
                            borderColor: 'var(--verde-esperanca)',
                          },
                          '&:hover .MuiOutlinedInput-notchedOutline': {
                            borderColor: 'var(--verde-esperanca)',
                          },
                        }}
                      >
                        {(
                          turmasPorNivel[
                            formData.grau as 'medio' | 'superior'
                          ] || []
                        ).map((turma) => (
                          <MenuItem
                            key={turma}
                            value={turma}
                            sx={menuItemStyles}
                          >
                            {turma}
                          </MenuItem>
                        ))}
                      </Select>
                      {errors.turma && (
                        <FormHelperText>A turma é obrigatória.</FormHelperText>
                      )}
                    </FormControl>
                  </>
                )}
              </Stack>
            )}

            <FormControl fullWidth required error={errors.categoria}>
              <InputLabel
                sx={{ '&.Mui-focused': { color: 'var(--verde-esperanca)' } }}
              >
                Categoria da Denúncia
              </InputLabel>
              <Select
                name='categoria'
                value={formData.categoria}
                label='Categoria da Denúncia *'
                onChange={handleChange}
                sx={{
                  '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
                    borderColor: 'var(--verde-esperanca)',
                  },
                  '&:hover .MuiOutlinedInput-notchedOutline': {
                    borderColor: 'var(--verde-esperanca)',
                  },
                }}
              >
                <MenuItem value='bullying_assedio' sx={menuItemStyles}>
                  Bullying e Assédio
                </MenuItem>
                <MenuItem value='uso_substancias' sx={menuItemStyles}>
                  Uso ou Porte de Substâncias Ilícitas
                </MenuItem>
                <MenuItem value='violencia' sx={menuItemStyles}>
                  Violência Física ou Verbal
                </MenuItem>
                <MenuItem value='vandalismo' sx={menuItemStyles}>
                  Vandalismo e Danos ao Patrimônio
                </MenuItem>
                <MenuItem value='fraude_academica' sx={menuItemStyles}>
                  Questões Acadêmicas (Fraude, Plágio)
                </MenuItem>
                <MenuItem value='outros' sx={menuItemStyles}>
                  Outros
                </MenuItem>
              </Select>
              {errors.categoria && (
                <FormHelperText>A categoria é obrigatória.</FormHelperText>
              )}
            </FormControl>

            <TextField
              name='relato'
              label='Descrição Detalhada '
              placeholder={`Descreva sua denúncia com o máximo de detalhes possível:
                            - O que aconteceu?
                            - Quem são os envolvidos?
                            - Onde e quando ocorreu?
                            - Existem testemunhas?
                            - Qualquer informação adicional é valiosa.`}
              multiline
              rows={8}
              fullWidth
              required
              value={formData.relato}
              onChange={handleChange}
              error={errors.relato}
              helperText={
                errors.relato
                  ? 'A descrição é obrigatória e deve ter no mínimo 50 caracteres.'
                  : `Mínimo: 50 caracteres | Máximo: 500 caracteres (atual: ${formData.relato.length}/500)`
              }
              slotProps={{
                htmlInput: {
                  maxLength: 500,
                },
              }}
              sx={fieldStyles}
            />

            <Alert
              severity='info'
              icon={false}
              sx={{
                backgroundColor: 'var(--verde-esperanca-10)',
                color: 'var(--cinza-escuro)',
                borderLeft: '4px solid var(--verde-esperanca)',
              }}
            >
              <strong>Sua Segurança é Nossa Prioridade:</strong> Esta denúncia
              será totalmente anônima. Não coletamos endereços IP, dados
              pessoais ou qualquer informação que possa identificá-lo.
            </Alert>

            <Box
              sx={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                my: 1,
              }}
            >
              <ReCAPTCHA
                sitekey='6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI'
                onChange={handleRecaptchaChange}
              />
              {errors.recaptcha && (
                <FormHelperText error sx={{ mt: 1 }}>
                  Por favor, complete o desafio.
                </FormHelperText>
              )}
            </Box>

            <Button
              variant='contained'
              size='large'
              type='submit'
              fullWidth
              sx={{
                py: 1.5,
                fontWeight: 'bold',
                textTransform: 'none',
                fontSize: '1rem',
                borderRadius: 2,
                backgroundColor: 'var(--verde-esperanca)',
                '&:hover': {
                  backgroundColor: '#257247',
                },
              }}
            >
              {tipoDenuncia === 'anonima'
                ? 'Enviar Denúncia de Forma Anônima'
                : 'Enviar Denúncia Identificada'}
            </Button>
          </Stack>
        </Paper>
      </Container>
    </Box>
  );
}
