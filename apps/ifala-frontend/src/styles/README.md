# Sistema de Design - IFala

## Visão Geral

Este diretório contém o sistema de design completo do projeto IFala, incluindo paleta de cores institucional, gradientes, assets e guias de uso.

## Estrutura de Arquivos

```
src/styles/
├── theme.css          # Tema principal com cores, gradientes e assets
└── README.md         # Este arquivo
```

## Paleta de Cores

### Cores Primárias
- `--verde-esperanca`: #2E8B57 - Verde principal (esperança e crescimento)
- `--azul-confianca`: #1E3A8A - Azul escuro (confiança e segurança)
- `--azul-claro`: #3B82F6 - Azul médio (modernidade)
- `--azul-suave`: #60A5FA - Azul claro (suavidade)

### Cores Neutras
- `--cinza-escuro`: #374151 - Textos principais
- `--cinza-medio`: #6B7280 - Textos secundários
- `--cinza-claro`: #F3F4F6 - Fundos suaves
- `--cinza-extra-claro`: #F9FAFB - Fundos neutros
- `--branco`: #FFFFFF - Fundos principais

### Cores de Estado
- `--vermelho-alerta`: #DC2626 - Alertas e erros
- `--amarelo-atencao`: #F59E0B - Avisos
- `--verde-sucesso`: #10B981 - Sucessos

## Gradientes Disponíveis

### Classes CSS
- `.gradient-primary` - Verde para Azul (gradiente principal)
- `.gradient-secondary` - Azul claro para Azul escuro
- `.gradient-success` - Verde claro para Verde escuro
- `.gradient-hero` - Gradiente especial para hero sections
- `.gradient-subtle` - Gradiente sutil para backgrounds

### Exemplo de Uso
```css
.meu-elemento {
  /* Usando gradiente principal */
  @apply gradient-primary;
}

.hero-section {
  /* Usando gradiente do hero */
  @apply gradient-hero;
}
```

## Assets e Logos

### Logos Institucionais
- **IFala (Principal)**: `/src/assets/IFala-logo.png`
  - Tamanho recomendado: 60px altura
  - Uso: Header, footer, seções principais
  
- **IFPI (Institucional)**: `/src/assets/Logo-IFPI-Horizontal.png`
  - Tamanho recomendado: 60px altura
  - Uso: Rodapé, seções institucionais

### Classes CSS para Logos
```css
.logo-ifala {
  height: 60px;
  width: auto;
  filter: drop-shadow(var(--sombra-suave));
}

.logo-ifpi {
  height: 60px;
  width: auto;
  filter: drop-shadow(var(--sombra-suave));
}
```

## 🛠️ Classes Utilitárias

### Backgrounds
- `.bg-primary`, `.bg-secondary`, `.bg-light`, `.bg-white`
- `.bg-success`, `.bg-warning`, `.bg-danger`

### Textos
- `.text-primary`, `.text-secondary`, `.text-dark`, `.text-muted`
- `.text-white`, `.text-success`, `.text-warning`, `.text-danger`

### Bordas
- `.border-primary`, `.border-secondary`, `.border-light`
- `.border-success`, `.border-warning`, `.border-danger`

### Sombras
- `.shadow-sm` - Sombra suave
- `.shadow-md` - Sombra média
- `.shadow-lg` - Sombra forte

## Como Usar

### 1. Importação Automática
O tema é importado automaticamente no `index.css`, então todas as variáveis estão disponíveis globalmente.

### 2. Usando Variáveis CSS
```css
.meu-componente {
  background-color: var(--verde-esperanca);
  color: var(--branco);
  box-shadow: var(--sombra-media);
}
```

### 3. Usando Classes Utilitárias
```html
<div class="bg-primary text-white shadow-md">
  Conteúdo com tema aplicado
</div>
```

### 4. Usando Gradientes
```html
<section class="gradient-hero">
  <h1>Hero Section com Gradiente</h1>
</section>
```

## Acessibilidade

- Todas as cores atendem ao contraste mínimo WCAG 2.1 AA
- Usar textos escuros sobre backgrounds claros
- Usar textos brancos sobre backgrounds escuros
- Nunca usar apenas cor para transmitir informação

##  Responsividade

- **Logos**: Reduzir para 40px em telas pequenas
- **Gradientes**: Manter em todas as resoluções
- **Sombras**: Reduzir intensidade em dispositivos móveis

##  Versionamento

Qualquer alteração neste sistema de design deve ser:
1. Documentada neste README
2. Testada em todos os componentes
3. Validada para acessibilidade

##  Para Desenvolvedores

### Boas Práticas
- Sempre use as variáveis CSS em vez de valores hardcoded
- Prefira classes utilitárias para casos simples
- Mantenha consistência visual usando o sistema
- Teste em diferentes dispositivos e resoluções

### Exemplo Completo
```css
.card-component {
  background: var(--branco);
  border: 1px solid var(--cinza-claro);
  border-radius: 8px;
  box-shadow: var(--sombra-suave);
  padding: 1.5rem;
}

.card-component:hover {
  box-shadow: var(--sombra-media);
  transform: translateY(-2px);
}

.card-title {
  color: var(--azul-confianca);
  font-size: 1.25rem;
  font-weight: 600;
}

.card-text {
  color: var(--cinza-escuro);
  line-height: 1.6;
}
```