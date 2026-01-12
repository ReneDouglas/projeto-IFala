import {
  Document,
  Page,
  Text,
  View,
  StyleSheet,
  Image,
} from '@react-pdf/renderer';

// --- CONFIGURAÇÃO DAS IMAGENS ---
// Caminhos exatos conforme estão na sua pasta public
const logoIfpiUrl = '/logo-IFPI-Horizontal.png';
const logoIfalaUrl = '/IFala-logo.png';
const watermarkUrl = '/Logo-IFPI-Vertical.png'; // <--- AQUI ESTÁ A MÁGICA
// --------------------------------

const styles = StyleSheet.create({
  page: {
    padding: 40,
    fontFamily: 'Helvetica',
    fontSize: 10,
    paddingBottom: 60,
    position: 'relative', // Necessário para o fundo funcionar
  },

  // --- ESTILO DA MARCA D'ÁGUA (FUNDO) ---
  watermark: {
    position: 'absolute',
    top: 180, // Empurra para o meio da página
    left: '25%', // Centraliza horizontalmente
    width: '50%', // Tamanho da imagem (ajuste se ficar grande demais)
    opacity: 0.1, // Bem clarinho (10%) para não atrapalhar o texto
    zIndex: -1, // Garante que fica atrás do texto
  },
  // ---------------------------------------

  // Cabeçalho
  headerContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 20,
    borderBottom: '1px solid #000',
    paddingBottom: 10,
    height: 70,
  },
  logoIfpi: { width: 100, height: 45, objectFit: 'contain' },
  logoIfala: { width: 50, height: 50, objectFit: 'contain' },
  headerTextContainer: { flex: 1, alignItems: 'center', marginHorizontal: 10 },
  headerTitle: {
    fontSize: 11,
    fontWeight: 'bold',
    marginBottom: 4,
    textAlign: 'center',
  },
  headerSub: {
    fontSize: 9,
    color: '#333',
    textAlign: 'center',
    marginBottom: 2,
  },

  // Título e Seções
  title: {
    fontSize: 14,
    textAlign: 'center',
    marginTop: 10,
    marginBottom: 20,
    fontWeight: 'bold',
    textTransform: 'uppercase',
  },
  sectionTitle: {
    backgroundColor: '#f0f0f0',
    padding: 5,
    marginTop: 15,
    marginBottom: 8,
    fontSize: 10,
    fontWeight: 'bold',
    borderBottom: '1px solid #ccc',
  },
  row: { flexDirection: 'row', marginBottom: 6, paddingLeft: 5 },
  label: { width: 120, fontWeight: 'bold', fontSize: 10 },
  value: { flex: 1, fontSize: 10 },
  textBox: {
    border: '1px solid #ccc',
    padding: 10,
    minHeight: 120,
    textAlign: 'justify',
    lineHeight: 1.5,
    fontSize: 10,
    marginTop: 5,
  },

  // Rodapé
  footerContainer: {
    position: 'absolute',
    bottom: 30,
    left: 40,
    right: 40,
    textAlign: 'center',
    borderTop: '1px solid #ccc',
    paddingTop: 10,
  },
  footerText: { fontSize: 8, color: '#666' },
});

interface RelatorioProps {
  dados: {
    protocolo: string;
    data: string;
    categoria: string;
    status: string;
    relato: string;
  };
}

export const RelatorioDenunciaPDF = ({ dados }: RelatorioProps) => (
  <Document>
    <Page size='A4' style={styles.page}>
      {/* 1. MARCA D'ÁGUA (Fica em primeiro para ser o fundo) */}
      <Image src={watermarkUrl} style={styles.watermark} fixed />

      {/* 2. CABEÇALHO */}
      <View style={styles.headerContainer}>
        <Image src={logoIfpiUrl} style={styles.logoIfpi} />

        <View style={styles.headerTextContainer}>
          <Text style={styles.headerTitle}>INSTITUTO FEDERAL DO PIAUÍ</Text>
          <Text style={styles.headerSub}>
            SISTEMA IFALA - Gestão de Ouvidoria
          </Text>
        </View>

        <Image src={logoIfalaUrl} style={styles.logoIfala} />
      </View>

      {/* 3. CONTEÚDO */}
      <Text style={styles.title}>RELATÓRIO DE OCORRÊNCIA</Text>

      <Text style={styles.sectionTitle}>1. DADOS DO REGISTRO</Text>
      <View style={styles.row}>
        <Text style={styles.label}>Protocolo:</Text>
        <Text style={styles.value}>{dados.protocolo}</Text>
      </View>
      <View style={styles.row}>
        <Text style={styles.label}>Data de Abertura:</Text>
        <Text style={styles.value}>{dados.data}</Text>
      </View>
      <View style={styles.row}>
        <Text style={styles.label}>Categoria:</Text>
        <Text style={styles.value}>{dados.categoria}</Text>
      </View>
      <View style={styles.row}>
        <Text style={styles.label}>Situação Atual:</Text>
        <Text style={styles.value}>{dados.status}</Text>
      </View>

      <Text style={styles.sectionTitle}>2. DESCRIÇÃO DOS FATOS</Text>
      <View style={styles.textBox}>
        <Text>{dados.relato}</Text>
      </View>

      <Text style={styles.sectionTitle}>3. ENCAMINHAMENTO / OBSERVAÇÕES</Text>
      <View style={{ ...styles.textBox, minHeight: 60 }}>
        <Text>
          Este relatório deve ser analisado pela coordenação competente para as
          devidas providências, conforme regulamento institucional.
        </Text>
      </View>

      {/* 4. RODAPÉ FIXO */}
      <View style={styles.footerContainer} fixed>
        <Text style={styles.footerText}>
          Este documento foi emitido eletronicamente pelo Sistema IFala. A sua
          autenticidade pode ser verificada junto à instituição.
        </Text>
      </View>
    </Page>
  </Document>
);
