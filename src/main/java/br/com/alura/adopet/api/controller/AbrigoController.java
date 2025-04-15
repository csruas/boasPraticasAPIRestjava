package br.com.alura.adopet.api.controller;

import br.com.alura.adopet.api.dto.AbrigoDTO;
import br.com.alura.adopet.api.dto.CadastroAbrigoDto;
import br.com.alura.adopet.api.dto.CadastroPetDTO;
import br.com.alura.adopet.api.dto.PetDTO;
import br.com.alura.adopet.api.exception.ValidacaoException;
import br.com.alura.adopet.api.model.Abrigo;
import br.com.alura.adopet.api.model.Pet;
import br.com.alura.adopet.api.repository.AbrigoRepository;
import br.com.alura.adopet.api.service.AbrigoService;
import br.com.alura.adopet.api.service.PetService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/abrigos")
public class AbrigoController {

    @Autowired
    private AbrigoService abrigoService;

    @Autowired
    private PetService petService;

    @GetMapping
    public ResponseEntity<List<AbrigoDTO>> listar() {
        List<AbrigoDTO> abrigos = abrigoService.listar();
        return ResponseEntity.ok(abrigos);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<String> cadastrar(@RequestBody @Valid CadastroAbrigoDto dto) {
        try {
            abrigoService.cadatrar(dto);
            return ResponseEntity.ok().build();
        } catch (ValidacaoException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @GetMapping("/{idOuNome}/pets")
    public ResponseEntity<List<PetDTO>> listarPets(@PathVariable String idOuNome) {
        try {
            List<PetDTO> petsDoAbrigo = abrigoService.listarPetsDoAbrigo(idOuNome);
            return ResponseEntity.ok(petsDoAbrigo);
        } catch (ValidacaoException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{idOuNome}/pets")
    @Transactional
    public ResponseEntity<String> cadastrarPet(@PathVariable String idOuNome, @RequestBody @Valid CadastroPetDTO dto) {
        try {
            Abrigo abrigo = abrigoService.carregarAbrigo(idOuNome);
            petService.cadastrarPet(abrigo, dto);
            return ResponseEntity.ok().build();
        } catch (ValidacaoException exception) {
            return ResponseEntity.notFound().build();
        }
    }


}
