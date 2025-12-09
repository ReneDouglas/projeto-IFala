package br.edu.ifpi.ifala.prova;

import br.edu.ifpi.ifala.denuncia.Denuncia;
import br.edu.ifpi.ifala.prova.provaDTO.ProvaDto;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço gerencia o armazenamento de provas. Implementa as regras de negócio para upload,
 * validação e organização de arquivos.
 *
 * @author Guilherme Alves
 */
@Service
@Transactional
public class ProvaService {

  private static final Logger log = LoggerFactory.getLogger(ProvaService.class);

  // Formatos permitidos
  private static final List<String> TIPOS_MIME_PERMITIDOS =
      Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp", "image/bmp");

  // Limite máximo de 20MB
  private static final long TAMANHO_MAXIMO_TOTAL = 20 * 1024 * 1024; // 20MB

  // Formato de timestamp: ddMMaaaaHHmmsssss
  private static final DateTimeFormatter TIMESTAMP_FORMATTER =
      DateTimeFormatter.ofPattern("ddMMyyyyHHmmssSSS");

  @Value("${app.upload.provas.base-path:/app/uploads/provas}")
  private String baseUploadPath;

  private final ProvaRepository provaRepository;

  public ProvaService(ProvaRepository provaRepository) {
    this.provaRepository = provaRepository;
  }

  /**
   * Inicialização do diretório base de uploads ao iniciar o serviço.
   */
  @PostConstruct
  public void init() {
    try {
      Files.createDirectories(Paths.get(baseUploadPath));
      log.info("Diretório de uploads de provas criado/verificado: {}", baseUploadPath);
    } catch (IOException e) {
      log.error("Erro ao criar diretório de uploads: {}", e.getMessage(), e);
      throw new RuntimeException("Não foi possível criar diretório de uploads", e);
    }
  }

  /**
   * Salva multiplas provas associadas a uma denúncia.
   *
   * @param denuncia Denúncia à qual as provas serão associadas
   * @param arquivos Lista de arquivos enviados
   * @return Lista de DTOs das provas salvas
   */
  public List<ProvaDto> salvarProvas(Denuncia denuncia, List<MultipartFile> arquivos) {
    if (arquivos == null || arquivos.isEmpty()) {
      return List.of();
    }

    validarArquivos(arquivos);

    // Criar diretório específico para esta denúncia
    String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
    String nomeDiretorio = String.format("denuncia-%d-%s", denuncia.getId(), timestamp);
    Path diretorioDenuncia = Paths.get(baseUploadPath, nomeDiretorio);

    try {
      Files.createDirectories(diretorioDenuncia);
      log.info("Diretório criado para denúncia {}: {}", denuncia.getId(), diretorioDenuncia);
    } catch (IOException e) {
      log.error("Erro ao criar diretório para denúncia {}: {}", denuncia.getId(), e.getMessage());
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Erro ao criar diretório de armazenamento");
    }

    // Processar cada arquivo
    return arquivos.stream().map(arquivo -> salvarArquivo(denuncia, arquivo, diretorioDenuncia))
        .collect(Collectors.toList());
  }

  /**
   * Valida a lista de arquivos enviados.
   */
  private void validarArquivos(List<MultipartFile> arquivos) {
    // Validar tamanho total
    long tamanhoTotal = arquivos.stream().mapToLong(MultipartFile::getSize).sum();

    if (tamanhoTotal > TAMANHO_MAXIMO_TOTAL) {
      log.warn("Tentativa de upload excedendo limite: {} bytes", tamanhoTotal);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "O tamanho total dos arquivos excede o limite de 20MB");
    }

    // Validação individual
    for (MultipartFile arquivo : arquivos) {
      if (arquivo.isEmpty()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Arquivo vazio não é permitido");
      }

      String tipoMime = arquivo.getContentType();
      if (tipoMime == null || !TIPOS_MIME_PERMITIDOS.contains(tipoMime.toLowerCase())) {
        log.warn("Tipo de arquivo não permitido: {}", tipoMime);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Tipo de arquivo não permitido. Apenas imagens são aceitas (JPEG, PNG, GIF, WebP, BMP)");
      }
    }
  }

  /**
   * Salva um arquivo individual no sistema de arquivos e no banco de dados.
   */
  private ProvaDto salvarArquivo(Denuncia denuncia, MultipartFile arquivo, Path diretorioDenuncia) {
    try {
      // nome único para o arquivo
      String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
      String extensao = obterExtensao(arquivo.getOriginalFilename());
      String nomeArquivo = String.format("prova-%d-%s%s", System.nanoTime(), timestamp, extensao);

      Path caminhoCompleto = diretorioDenuncia.resolve(nomeArquivo).normalize();

      // Validar que o caminho final está dentro do diretório esperado (prevenção contra path
      // traversal)
      if (!caminhoCompleto.startsWith(diretorioDenuncia.normalize())) {
        log.error("Tentativa de path traversal detectada: {}", nomeArquivo);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome de arquivo inválido");
      }

      // Salvar arquivo no disco
      Files.copy(arquivo.getInputStream(), caminhoCompleto);
      log.info("Arquivo salvo: {}", caminhoCompleto);

      // Criar entidade Prova
      Prova prova = new Prova();
      prova.setDenuncia(denuncia);
      prova.setNomeArquivo(nomeArquivo);
      prova.setCaminhoArquivo(caminhoCompleto.toString());
      prova.setTamanhoBytes(arquivo.getSize());
      prova.setTipoMime(arquivo.getContentType());

      // Salvar no banco
      Prova provaSalva = provaRepository.save(prova);
      log.info("Prova registrada no banco com ID: {}", provaSalva.getId());

      return mapToDto(provaSalva);

    } catch (IOException e) {
      log.error("Erro ao salvar arquivo: {}", e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao salvar arquivo");
    }
  }


  private String obterExtensao(String nomeOriginal) {
    if (nomeOriginal == null || nomeOriginal.isEmpty()) {
      return "";
    }

    // Sanitizar nome do arquivo: remover path separators e caracteres perigosos
    String nomeSanitizado = nomeOriginal.replaceAll("[/\\\\]", "");

    int lastDot = nomeSanitizado.lastIndexOf('.');
    if (lastDot > 0 && lastDot < nomeSanitizado.length() - 1) {
      String extensao = nomeSanitizado.substring(lastDot).toLowerCase();
      if (extensao.matches("^\\.[a-z0-9]+$")) {
        return extensao;
      }
    }

    return "";
  }

  /**
   * Lista todas as provas de uma denúncia.
   */
  @Transactional(readOnly = true)
  public List<ProvaDto> listarProvasPorDenuncia(Long denunciaId) {
    return provaRepository.findByDenunciaId(denunciaId).stream().map(this::mapToDto)
        .collect(Collectors.toList());
  }

  /**
   * Busca um arquivo de prova pelo ID.
   */
  @Transactional(readOnly = true)
  public Prova buscarPorId(Long id) {
    return provaRepository.findById(id).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prova não encontrada"));
  }

  /**
   * Mapeia entidade Prova para DTO.
   */
  private ProvaDto mapToDto(Prova prova) {
    return new ProvaDto(prova.getId(), prova.getNomeArquivo(), prova.getCaminhoArquivo(),
        prova.getTamanhoBytes(), prova.getTipoMime(), prova.getCriadoEm().toString());
  }
}
