package br.com.alura.adopet.api.dto;

import br.com.alura.adopet.api.model.Abrigo;

public record AbrigoDTO(Long id, String nome) {

    public AbrigoDTO(Abrigo abrigo) {
        this(abrigo.getId(), abrigo.getNome());
    }
}
