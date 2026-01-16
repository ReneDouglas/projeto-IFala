import {
  Document,
  Page,
  Text,
  View,
  StyleSheet,
  Image,
} from '@react-pdf/renderer';

// --- CONFIGURAÇÃO DAS IMAGENS ---
const logoCampusUrl = '/Logo-IFPI-Corrente-Vertical.png';
const logoIfalaUrl = '/IFala-logo.png';
const watermarkUrl = '/Logo-IFPI-Vertical.png';
// --------------------------------

const styles = StyleSheet.create({
  page: {
    padding: 40,
    fontFamily: 'Helvetica',
    fontSize: 10,
    paddingBottom: 60,
    position: 'relative',
  },
  watermark: {
    position: 'absolute',
    top: 300,
    left: 0,
    width: '100%',
    height: 'auto',
    opacity: 0.12,
    zIndex: -1,
  },
  headerContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 20,
    borderBottom: '1px solid #000',
    paddingBottom: 10,
    height: 120,
  },
  logoEsquerda: { width: 90, height: 100, objectFit: 'contain' },
  logoDireita: { width: 90, height: 90, objectFit: 'contain' },
  headerTextContainer: { flex: 1, alignItems: 'center', marginHorizontal: 10 },
  headerLine1: {
    fontSize: 9,
    fontWeight: 'bold',
    marginBottom: 3,
    textAlign: 'center',
  },
  headerLine2: {
    fontSize: 9,
    fontWeight: 'bold',
    marginBottom: 3,
    textAlign: 'center',
    textTransform: 'uppercase',
  },
  headerLine3: { fontSize: 8, color: '#333', textAlign: 'center' },

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
  label: { width: 130, fontWeight: 'bold', fontSize: 10 },
  value: { flex: 1, fontSize: 10 },

  textBox: {
    border: '1px solid #ccc',
    padding: 10,
    minHeight: 60,
    textAlign: 'justify',
    lineHeight: 1.5,
    fontSize: 10,
    marginTop: 5,
  },

  // --- ESTILOS DO CHAT ---
  chatContainer: {
    marginTop: 5,
    // Mudei para #ccc para ficar igual às outras caixas
    border: '1px solid #ccc',
  },
  chatItem: {
    padding: 8,
    // Mantive a borda de baixo para separar as mensagens, mas sem fundo
    borderBottom: '1px solid #eee',
    flexDirection: 'column',
  },
  chatHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 4,
  },
  chatAuthor: { fontSize: 9, fontWeight: 'bold', color: '#333' },
  chatDate: { fontSize: 8, color: '#666' },
  chatMessage: { fontSize: 10, color: '#444' },
  // -----------------------

  signaturesContainer: {
    marginTop: 40,
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  signatureBlock: { width: '45%', alignItems: 'center' },
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
    temAnexos: boolean;
    historico: Array<{
      autor: string;
      mensagem: string;
      data: string;
    }>;
  };
}

export const RelatorioDenunciaPDF = ({ dados }: RelatorioProps) => (
  <Document>
    <Page size='A4' style={styles.page}>
      <Image src={watermarkUrl} style={styles.watermark} fixed />

      <View style={styles.headerContainer}>
        <Image src={logoCampusUrl} style={styles.logoEsquerda} />
        <View style={styles.headerTextContainer}>
          <Text style={styles.headerLine1}>
            INSTITUTO FEDERAL DE CIÊNCIA E TECNOLOGIA DO PIAUÍ
          </Text>
          <Text style={styles.headerLine2}>CAMPUS CORRENTE</Text>
          <Text style={styles.headerLine3}>SISTEMA IFALA</Text>
        </View>
        <Image src={logoIfalaUrl} style={styles.logoDireita} />
      </View>

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
      <View style={styles.row}>
        <Text style={styles.label}>Possui Anexos:</Text>
        <Text style={styles.value}>{dados.temAnexos ? 'Sim' : 'Não'}</Text>
      </View>

      <Text style={styles.sectionTitle}>2. DESCRIÇÃO DOS FATOS</Text>
      <View style={styles.textBox}>
        <Text>{dados.relato}</Text>
      </View>

      {/* --- SEÇÃO DO HISTÓRICO (CHAT) --- */}
      <Text style={styles.sectionTitle}>3. HISTÓRICO DE TRAMITAÇÃO (CHAT)</Text>
      {dados.historico && dados.historico.length > 0 ? (
        <View style={styles.chatContainer}>
          {dados.historico.map((msg, index) => (
            <View
              key={index}
              style={styles.chatItem} // Removi o backgroundColor aqui!
            >
              <View style={styles.chatHeader}>
                <Text style={styles.chatAuthor}>{msg.autor}</Text>
                <Text style={styles.chatDate}>{msg.data}</Text>
              </View>
              <Text style={styles.chatMessage}>{msg.mensagem}</Text>
            </View>
          ))}
        </View>
      ) : (
        <View style={styles.textBox}>
          <Text style={{ color: '#666', fontStyle: 'italic' }}>
            Nenhuma tramitação registrada além do relato inicial.
          </Text>
        </View>
      )}

      {/* --- SEÇÃO DE OBSERVAÇÕES --- */}
      <Text style={styles.sectionTitle}>4. OBSERVAÇÕES</Text>
      <View style={{ ...styles.textBox, minHeight: 60 }}>
        <Text>
          Este relatório deve ser analisado pela coordenação competente para as
          devidas providências.
        </Text>
      </View>

      <View style={styles.signaturesContainer}>
        <View style={styles.signatureBlock}>
          <View style={styles.signatureLine} />
          <Text style={styles.signatureText}>COORDENAÇÃO</Text>
          <Text style={styles.signatureSubText}>Assinatura ou Carimbo</Text>
        </View>
        <View style={styles.signatureBlock}>
          <View style={styles.signatureLine} />
          <Text style={styles.signatureText}>ALUNO / RESPONSÁVEL</Text>
          <Text style={styles.signatureSubText}>Assinatura (Opcional)</Text>
        </View>
      </View>

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
