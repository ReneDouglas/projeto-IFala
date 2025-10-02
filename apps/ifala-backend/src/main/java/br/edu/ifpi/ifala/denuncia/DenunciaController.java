package br.edu.ifpi.ifala.denuncia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import br.edu.ifpi.ifala.shared.enums.Categorias;
import br.edu.ifpi.ifala.shared.enums.Status;

/**
 * Classe Controller responsável por manipular operações relacionadas a
 * denúncias.
 *
 * @author Renê Morais
 * @author Jhonatas G Ribeiro
 */

@RestController()
@RequestMapping("/api/v1/denuncias")
public class DenunciaController {

  private final DenunciaService denunciaService;

  public DenunciaController(DenunciaService denunciaService) {
    this.denunciaService = denunciaService;
  }

  @PostMapping
  public ResponseEntity<Denuncia> criarDenuncia(
      @RequestBody Denuncia novaDenuncia,
      UriComponentsBuilder uriBuilder) {

    Denuncia denunciaSalva = denunciaService.criarDenuncia(novaDenuncia);

    // cria a URI para o recurso recém-criado
    URI uri = uriBuilder.path("/denuncias/acompanhar/{token}")
        .buildAndExpand(denunciaSalva.getTokenAcompanhamento()).toUri();

    return ResponseEntity.created(uri).body(denunciaSalva); // retorna 201 Created com a URI no header
  }

  @GetMapping("/acompanhar/{tokenAcompanhamento}")
  public ResponseEntity<Denuncia> consultarPorToken(@PathVariable UUID tokenAcompanhamento) {
    return denunciaService.consultarPorTokenAcompanhamento(tokenAcompanhamento)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping
  public ResponseEntity<Page<Denuncia>> listarTodas(
      @RequestParam(required = false) Status status,
      @RequestParam(required = false) Categorias categoria,
      Pageable pageable) {

    Page<Denuncia> denunciasPage = denunciaService.listarTodas(status, categoria, pageable);
    return ResponseEntity.ok(denunciasPage);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Denuncia> atualizarDenuncia(
      @PathVariable Long id,
      @RequestBody Denuncia dadosParaAtualizar) {

    return denunciaService.atualizarDenuncia(id, dadosParaAtualizar)
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

}
