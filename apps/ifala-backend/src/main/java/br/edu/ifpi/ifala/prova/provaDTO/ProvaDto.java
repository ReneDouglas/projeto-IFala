package br.edu.ifpi.ifala.prova.provaDTO;

/**
 * DTO para retornar informações de uma prova.
 *
 * @author Guilherme Alves
 */
public record ProvaDto(Long id, String nomeArquivo, String caminhoArquivo, Long tamanhoBytes,
    String tipoMime, String criadoEm) {
}
