import {
  Document,
  Page,
  Text,
  View,
  StyleSheet,
  Image,
} from '@react-pdf/renderer';

// --- CONFIGURAÇÃO DAS IMAGENS ---
const logoVerticalUrl = '/Logo-IFPI-Vertical.png';
const logoIfalaUrl = '/IFala-logo.png';
// --------------------------------

const styles = StyleSheet.create({
  page: {
    padding: 40,
    fontFamily: 'Helvetica',
    fontSize: 10,
    paddingBottom: 60,
    position: 'relative',
  },

  // Marca D'água (VOLTOU PARA A POSIÇÃO ORIGINAL)
  watermark: {
    position: 'absolute',
    top: 299,
    left: 0,
    width: '100%',
    height: 'auto',
    opacity: 0.12,
    zIndex: -1,
  },

  // --- CABEÇALHO (LOGOS GRANDES) ---
  headerContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 20,
    borderBottom: '1px solid #000',
    paddingBottom: 10,
    height: 130,
  },

  // Logo IFPI (Esquerda)
  logoIfpiVertical: {
    width: 100,
    height: 120,
    objectFit: 'contain',
  },

  // Logo IFala (Direita)
  logoIfala: {
    width: 110,
    height: 110,
    objectFit: 'contain',
  },
  // -----------------------------

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

  // Títulos e Seções
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

  // CAIXA DE TEXTO (MENOR)
  textBox: {
    border: '1px solid #ccc',
    padding: 10,
    minHeight: 80,
    textAlign: 'justify',
    lineHeight: 1.5,
    fontSize: 10,
    marginTop: 5,
  },

  // Área de Assinaturas
  signaturesContainer: {
    marginTop: 50,
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  signatureBlock: {
    width: '45%',
    alignItems: 'center',
  },
  signatureLine: {
    borderTop: '1px solid #999',
    width: '100%',
    marginBottom: 5,
  },
  signatureText: {
    fontSize: 9,
    textAlign: 'center',
    fontWeight: 'bold',
    color: '#333',
  },
  signatureSubText: {
    fontSize: 8,
    textAlign: 'center',
    color: '#666',
    marginTop: 2,
  },

  // Rodapé Fixo
  footerContainer: {
    position: 'absolute',
    bottom: 30,
    left: 40,
    right: 40,
    textAlign: 'center',
    borderTop: '1px solid #ccc',
    paddingTop: 10,
  },
  footerText: { fontSize: 8, color: '#999' },
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
      {/* Marca D'água */}
      <Image src={logoVerticalUrl} style={styles.watermark} fixed />

      {/* Cabeçalho */}
      <View style={styles.headerContainer}>
        {/* Logo IFPI Vertical Grande */}
        <Image src={logoVerticalUrl} style={styles.logoIfpiVertical} />

        <View style={styles.headerTextContainer}>
          <Text style={styles.headerTitle}>INSTITUTO FEDERAL DO PIAUÍ</Text>
          <Text style={styles.headerSub}>
            SISTEMA IFALA - Canal de Denúncias
          </Text>
        </View>

        {/* Logo IFala Grande */}
        <Image src={logoIfalaUrl} style={styles.logoIfala} />
      </View>

      {/* Conteúdo */}
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

      <Text style={styles.sectionTitle}>3. OBSERVAÇÕES </Text>
      {}
      <View style={{ ...styles.textBox, minHeight: 60 }}>
        <Text>
          Este relatório deve ser analisado pela coordenação competente para as
          devidas providências.
        </Text>
      </View>

      {/* Assinaturas */}
      <View style={styles.signaturesContainer}>
        <View style={styles.signatureBlock}>
          <View style={styles.signatureLine} />
          <Text style={styles.signatureText}>COORDENAÇÃO</Text>
          <Text style={styles.signatureSubText}>Assinatura ou Carimbo</Text>
        </View>

        <View style={styles.signatureBlock}>
          <View style={styles.signatureLine} />
          <Text style={styles.signatureText}>ALUNO / RESPONSÁVEL</Text>
          <Text style={styles.signatureSubText}>Assinatura</Text>
        </View>
      </View>

      {/* Rodapé */}
      <View style={styles.footerContainer} fixed>
        <Text style={styles.footerText}>
          Documento emitido pelo Sistema IFala em{' '}
          {new Date().toLocaleDateString()}. Autenticidade verificável junto à
          instituição.
        </Text>
      </View>
    </Page>
  </Document>
);
