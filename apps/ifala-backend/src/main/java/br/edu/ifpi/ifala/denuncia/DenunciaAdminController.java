package br.edu.ifpi.ifala.denuncia;

import br.edu.ifpi.ifala.acompanhamento.acompanhamentoDTO.AcompanhamentoDto;
import br.edu.ifpi.ifala.denuncia.denunciaDTO.AtualizarDenunciaDto;
import br.edu.ifpi.ifala.denuncia.denunciaDTO.DenunciaResponseDto;
import br.edu.ifpi.ifala.shared.enums.Categorias;
import br.edu.ifpi.ifala.shared.enums.Status;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsável pelos endpoints de ADMINISTRAÇÃO de denúncias.
 * Requer autenticação e autorização.
 *
 * @author Renê Morais
 * @author Jhonatas G Ribeiro
 */
@RestController
@RequestMapping("/api/v1/admin/denuncias")
public class DenunciaAdminController {

  private final DenunciaService denunciaService;

  public DenunciaAdminController(DenunciaService denunciaService) {
    this.denunciaService = denunciaService;
  }

  @GetMapping
  public ResponseEntity<Page<DenunciaResponseDto>> listarTodas(
      @RequestParam(required = false) Status status,
      @RequestParam(required = false) Categorias categoria,
      Pageable pageable) {

    Page<DenunciaResponseDto> denunciasPage = denunciaService.listarTodas(status, categoria, pageable);
    return ResponseEntity.ok(denunciasPage);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<DenunciaResponseDto> atualizarDenuncia(
      @PathVariable Long id,
      @Valid @RequestBody AtualizarDenunciaDto dto,
      Authentication authentication) {

    String adminName = authentication.getName();

    return denunciaService.atualizarDenuncia(id, dto, adminName)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletarDenuncia(@PathVariable Long id) {
    boolean deletado = denunciaService.deletarDenuncia(id);
    if (deletado) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/{id}/acompanhamentos")
  public ResponseEntity<List<AcompanhamentoDto>> listarAcompanhamentos(@PathVariable Long id) {
    List<AcompanhamentoDto> acompanhamentos = denunciaService.listarAcompanhamentosPorId(id);
    return ResponseEntity.ok(acompanhamentos);
  }

  @PostMapping("/{id}/acompanhamentos")
  public ResponseEntity<AcompanhamentoDto> adicionarAcompanhamento(
      @PathVariable Long id,
      @Valid @RequestBody AcompanhamentoDto novoAcompanhamento,
      Authentication authentication) { // para pegar o nome do admin logado

    String nomeAdmin = authentication.getName(); // pega o nome do usuário autenticado
    AcompanhamentoDto acompanhamentoSalvo = denunciaService.adicionarAcompanhamentoAdmin(id, novoAcompanhamento,
        nomeAdmin);
    return ResponseEntity.status(HttpStatus.CREATED).body(acompanhamentoSalvo);
  }
}