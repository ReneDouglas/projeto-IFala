// regex geral para validação básica de e-mail
const EMAIL_REGEX =
  /^[a-zA-Z0-9]([a-zA-Z0-9._+-]*[a-zA-Z0-9])?@[a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])?(\.[a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])?)*\.[a-zA-Z]{2,}$/;

// formato: campus.2024121curso0000@aluno.ifpi.edu.br
// info: a quantidade de caracteres no localPart pode variar,
// mas deve seguir a estrutura: campus + ponto + ano(4) + período(variavel) + curso(variável) + matrícula(variável)
const IFPI_ALUNO_REGEX =
  /^[a-z]+\.20\d{4}\d*[a-zA-Z]*\d+@aluno\.ifpi\.edu\.br$/i;

const DOMINIOS_COMUNS = [
  'ifpi.edu.br',
  'aluno.ifpi.edu.br',
  'gmail.com',
  'googlemail.com',
  'hotmail.com',
  'outlook.com',
  'live.com',
  'msn.com',
  'yahoo.com',
  'yahoo.com.br',
  'icloud.com',
  'me.com',
  'mac.com',
  'aol.com',
  'uol.com.br',
  'bol.com.br',
  'terra.com.br',
  'ig.com.br',
  'r7.com',
  'globo.com',
  'globomail.com',
  'oi.com.br',
  'zipmail.com.br',
  'proton.me',
  'protonmail.com',
  'tutanota.com',
  'tutamail.com',
  'mailfence.com',
  'zoho.com',
  'mail.com',
  'gmx.com',
  'gmx.net',
  'yandex.com',
];

const TLD_VALIDAS = [
  'com',
  'org',
  'net',
  'edu',
  'gov',
  'mil',
  'int',
  'info',
  'biz',
  'br',
  'com.br',
  'edu.br',
  'gov.br',
  'org.br',
  'net.br',
  'mil.br',
  'io',
  'me',
  'tech',
  'dev',
  'app',
  'online',
  'site',
  'website',
  'uk',
  'us',
  'de',
  'fr',
  'it',
  'es',
  'pt',
  'ar',
  'mx',
  'cl',
  'co.uk',
];

export const validateEmail = (email: string): string => {
  const trimmedEmail = email.trim();

  if (trimmedEmail === '') {
    return 'O e-mail é obrigatório.';
  }

  if (!trimmedEmail.includes('@')) {
    return 'O e-mail deve conter o símbolo @';
  }

  const atCount = (trimmedEmail.match(/@/g) || []).length;
  if (atCount > 1) {
    return 'O e-mail não pode conter mais de um símbolo @';
  }

  if (trimmedEmail.startsWith('@') || trimmedEmail.endsWith('@')) {
    return 'O símbolo @ não pode estar no início ou fim do e-mail.';
  }

  if (trimmedEmail.length < 5) {
    return 'O e-mail é muito curto. Deve ter pelo menos 5 caracteres.';
  }

  if (/\s/.test(trimmedEmail)) {
    return 'O e-mail não pode conter espaços em branco.';
  }

  if (trimmedEmail.includes('..')) {
    return 'O e-mail não pode conter pontos consecutivos (..)';
  }

  const parts = trimmedEmail.split('@');
  if (parts.length !== 2) {
    return 'Formato de e-mail inválido.';
  }

  const [localPart, domain] = parts;

  if (!localPart || localPart.length === 0) {
    return 'A parte antes do @ não pode estar vazia.';
  }

  if (localPart.startsWith('.') || localPart.endsWith('.')) {
    return 'O e-mail não pode começar ou terminar com ponto antes do @';
  }

  if (
    localPart.startsWith('-') ||
    localPart.endsWith('-') ||
    localPart.startsWith('_') ||
    localPart.endsWith('_')
  ) {
    return 'O e-mail não pode começar ou terminar com hífen ou underscore antes do @';
  }

  if (!/^[a-zA-Z0-9._+-]+$/.test(localPart)) {
    return 'A parte antes do @ só pode conter letras, números, pontos, hífens, underscores e sinais de mais.';
  }

  if (localPart.length > 64) {
    return 'A parte antes do @ não pode ter mais de 64 caracteres.';
  }

  if (!domain || domain.length === 0) {
    return 'O e-mail deve conter um domínio após o @';
  }

  if (domain.startsWith('.') || domain.endsWith('.')) {
    return 'O domínio não pode começar ou terminar com ponto.';
  }

  if (domain.startsWith('-') || domain.endsWith('-')) {
    return 'O domínio não pode começar ou terminar com hífen.';
  }

  if (!domain.includes('.')) {
    return 'O domínio deve conter pelo menos um ponto (ex: dominio.com)';
  }

  if (!/^[a-zA-Z0-9.-]+$/.test(domain)) {
    return 'O domínio só pode conter letras, números, pontos e hífens.';
  }

  const domainParts = domain.split('.');
  for (const part of domainParts) {
    if (part.length === 0) {
      return 'O domínio contém pontos consecutivos inválidos.';
    }
    if (part.startsWith('-') || part.endsWith('-')) {
      return 'As partes do domínio não podem começar ou terminar com hífen.';
    }
  }

  const tld = domainParts[domainParts.length - 1].toLowerCase();
  const tldWith2Parts =
    domainParts.length >= 2
      ? `${domainParts[domainParts.length - 2]}.${tld}`.toLowerCase()
      : '';

  if (tld.length < 2) {
    return 'A extensão do domínio deve ter pelo menos 2 caracteres (ex: .com, .br)';
  }

  if (!/^[a-zA-Z]+$/.test(tld)) {
    return 'A extensão do domínio deve conter apenas letras.';
  }

  if (domain.length > 255) {
    return 'O domínio não pode ter mais de 255 caracteres.';
  }

  if (!EMAIL_REGEX.test(trimmedEmail)) {
    return 'Formato de e-mail inválido. Use o formato: usuario@dominio.com';
  }

  const lowerEmail = trimmedEmail.toLowerCase();

  // validações específicas para e-mails IFPI
  if (
    lowerEmail.includes('@ifpi.edu.br') ||
    lowerEmail.includes('@aluno.ifpi.edu.br')
  ) {
    if (lowerEmail.endsWith('@aluno.ifpi.edu.br')) {
      if (!IFPI_ALUNO_REGEX.test(trimmedEmail)) {
        return 'E-mail institucional de aluno inválido. Formato esperado: campus.202412curso0003@aluno.ifpi.edu.br (campus + ano + período + curso + matrícula)';
      }

      const localPartLower = localPart.toLowerCase();
      const parts = localPartLower.split('.');

      if (parts.length !== 2) {
        return 'E-mail de aluno deve ter formato: campus.202412curso0003@aluno.ifpi.edu.br';
      }

      const [campus, resto] = parts;

      if (campus.length < 3 || !/^[a-z]+$/.test(campus)) {
        return 'Código do campus inválido (mínimo 3 letras, sem números).';
      }

      // validar ano (4 dígitos começando com 202 ou 201, mas rejeitando 201X)
      if (!/^202\d/.test(resto)) {
        return 'Ano inválido no formato. Deve começar com 202X (ex: 2024 ou 2025).';
      }

      // validar que há período + curso + matrícula após o ano
      const restoSemAno = resto.substring(4);
      if (restoSemAno.length < 8) {
        return 'Formato incompleto. Deve conter: ano(4) + período(variável) + curso(variável) + matrícula(variável).';
      }

      // verificar que tem letras (curso) e números finais (matrícula)
      if (!/[a-zA-Z]/.test(restoSemAno) || !/\d+$/.test(restoSemAno)) {
        return 'Formato deve incluir identificação do curso (letras) e número de matrícula.';
      }
    } else if (lowerEmail.endsWith('@ifpi.edu.br')) {
      const localPartLower = localPart.toLowerCase();

      // validar formato com ponto
      if (localPartLower.includes('.')) {
        const partsWithoutNumbers = localPartLower.replace(/\d+$/, '');
        const parts = partsWithoutNumbers.split('.');

        if (parts.length < 2) {
          return 'E-mail de servidor deve conter nome e sobrenome separados por ponto.';
        }

        for (const part of parts) {
          if (part.length === 0) {
            return 'Cada parte do nome deve ter pelo menos 2 letras.';
          }
          if (!/^[a-z]+$/.test(part) || part.length < 2) {
            return 'Cada parte do nome deve ter pelo menos 2 letras e conter apenas letras.';
          }
        }
      }
      // validar formato sem ponto
      else {
        const nameWithoutNumbers = localPartLower.replace(/\d+$/, '');
        if (nameWithoutNumbers.length < 4) {
          return 'O nome completo deve ter pelo menos 4 caracteres.';
        }
        if (!/^[a-z]+$/.test(nameWithoutNumbers)) {
          return 'O nome deve conter apenas letras (sem caracteres especiais).';
        }
      }
    } else if (
      lowerEmail.includes('ifpi') &&
      !lowerEmail.endsWith('@ifpi.edu.br') &&
      !lowerEmail.endsWith('@aluno.ifpi.edu.br')
    ) {
      return 'Domínio IFPI incorreto. Use @ifpi.edu.br (servidores) ou @aluno.ifpi.edu.br (alunos)';
    }
  }

  const dominioCompleto = domain.toLowerCase();
  const isDominioComum = DOMINIOS_COMUNS.some(
    (d) => dominioCompleto === d || dominioCompleto.endsWith('.' + d),
  );

  const isTLDValida =
    TLD_VALIDAS.includes(tld) || TLD_VALIDAS.includes(tldWith2Parts);

  if (!isDominioComum && !isTLDValida) {
    return `Domínio "${domain}" não é reconhecido. Verifique se digitou corretamente. Para e-mail institucional, use @ifpi.edu.br ou @aluno.ifpi.edu.br`;
  }

  return '';
};

export const getEmailSuggestion = (email: string): string => {
  const trimmedEmail = email.trim().toLowerCase();

  if (trimmedEmail.length < 5) {
    return '';
  }

  if (trimmedEmail.includes('ifpi')) {
    // Verificar se é um email IFPI válido antes de retornar sugestões
    const validationResult = validateEmail(email);
    if (validationResult === '') {
      return ''; // Email válido, não retorna sugestão
    }

    if (/\d{6,}/.test(trimmedEmail)) {
      if (!trimmedEmail.includes('@aluno.ifpi.edu.br')) {
        return 'Sugestão: Alunos devem usar @aluno.ifpi.edu.br';
      }
    } else if (/^[a-z.]+@/.test(trimmedEmail)) {
      if (
        !trimmedEmail.includes('@ifpi.edu.br') &&
        !trimmedEmail.includes('@aluno.ifpi.edu.br')
      ) {
        return 'Sugestão: Servidores/professores devem usar @ifpi.edu.br';
      }
    }

    if (
      trimmedEmail.includes('@ifpi.br') ||
      trimmedEmail.includes('@ifpi.com') ||
      trimmedEmail.includes('@ifpi.edu')
    ) {
      return 'Sugestão: O domínio correto é @ifpi.edu.br';
    }
  }

  const typoCorrections: { [key: string]: string } = {
    gmial: 'gmail',
    gmai: 'gmail',
    gmaill: 'gmail',
    gmil: 'gmail',
    gmal: 'gmail',
    gnail: 'gmail',
    hotmial: 'hotmail',
    hotmal: 'hotmail',
    homail: 'hotmail',
    hotmil: 'hotmail',
    htmail: 'hotmail',
    outlok: 'outlook',
    outloo: 'outlook',
    outllook: 'outlook',
    yahooo: 'yahoo',
    yaho: 'yahoo',
    yhoo: 'yahoo',
    yaboo: 'yahoo',
    ifp: 'ifpi',
    ipfi: 'ifpi',
    ifip: 'ifpi',
    'edu.com': 'edu.br',
    'com.com': 'com.br',
  };

  for (const [typo, correct] of Object.entries(typoCorrections)) {
    if (trimmedEmail.includes(typo)) {
      return `Você quis dizer "${correct}"?`;
    }
  }

  if (
    !trimmedEmail.includes('.br') &&
    (trimmedEmail.includes('@uol.com') ||
      trimmedEmail.includes('@bol.com') ||
      trimmedEmail.includes('@terra.com') ||
      trimmedEmail.includes('@ig.com'))
  ) {
    return 'Sugestão: Domínios brasileiros geralmente usam .com.br';
  }

  return '';
};
