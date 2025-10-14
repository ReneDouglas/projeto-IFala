package br.edu.ifpi.ifala.denuncia;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.edu.ifpi.ifala.acompanhamento.acompanhamentoDTO.AcompanhamentoDto;
import br.edu.ifpi.ifala.denuncia.denunciaDTO.CriarDenunciaDto;
import br.edu.ifpi.ifala.denuncia.denunciaDTO.DenunciaResponseDto;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Controller responsável pelos endpoints PÚBLICOS de denúncias.
 * Permite a criação e consulta de denúncias por token.
 *
 * @author Renê Morais
 * @author Jhonatas G Ribeiro
 */
@RestController
@RequestMapping("/api/v1/public/denuncias")
public class DenunciaPublicController {

  private final DenunciaService denunciaService;

  public DenunciaPublicController(DenunciaService denunciaService) {
    this.denunciaService = denunciaService;
  }

  @PostMapping
  public ResponseEntity<DenunciaResponseDto> criarDenuncia(
      @Valid @RequestBody CriarDenunciaDto novaDenunciaDto,
      UriComponentsBuilder uriBuilder) {

    DenunciaResponseDto denunciaSalvaDto = denunciaService.criarDenuncia(novaDenunciaDto);

    URI uri = uriBuilder.path("/api/v1/public/denuncias/{token}")
        .buildAndExpand(denunciaSalvaDto.getTokenAcompanhamento()).toUri();

    return ResponseEntity.created(uri).body(denunciaSalvaDto);
  }

  @GetMapping("/{tokenAcompanhamento}")
  public ResponseEntity<DenunciaResponseDto> consultarPorToken(@PathVariable UUID tokenAcompanhamento) {
    return denunciaService.consultarPorTokenAcompanhamento(tokenAcompanhamento)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/{tokenAcompanhamento}/acompanhamentos")
  public ResponseEntity<List<AcompanhamentoDto>> listarAcompanhamentos(@PathVariable UUID tokenAcompanhamento) {
    List<AcompanhamentoDto> acompanhamentos = denunciaService.listarAcompanhamentosPorToken(tokenAcompanhamento);
    return ResponseEntity.ok(acompanhamentos);
  }

  @PostMapping("/{tokenAcompanhamento}/acompanhamentos")
  public ResponseEntity<AcompanhamentoDto> adicionarAcompanhamento(
      @PathVariable UUID tokenAcompanhamento,
      @Valid @RequestBody AcompanhamentoDto novoAcompanhamento) {
    AcompanhamentoDto acompanhamentoSalvo = denunciaService.adicionarAcompanhamentoDenunciante(tokenAcompanhamento,
        novoAcompanhamento);
    return ResponseEntity.status(HttpStatus.CREATED).body(acompanhamentoSalvo);
  }

}