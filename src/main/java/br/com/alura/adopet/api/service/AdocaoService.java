package br.com.alura.adopet.api.service;

import br.com.alura.adopet.api.dto.AprovacaoAdocaoDTO;
import br.com.alura.adopet.api.dto.ReprovacaoAdocaoDTO;
import br.com.alura.adopet.api.dto.SolicitacaoAdocaoDTO;
import br.com.alura.adopet.api.exception.ValidacaoException;
import br.com.alura.adopet.api.model.Adocao;
import br.com.alura.adopet.api.model.Pet;
import br.com.alura.adopet.api.model.StatusAdocao;
import br.com.alura.adopet.api.model.Tutor;
import br.com.alura.adopet.api.repository.AdocaoRepository;
import br.com.alura.adopet.api.repository.PetRepository;
import br.com.alura.adopet.api.repository.TutorRepository;
import br.com.alura.adopet.api.validacoes.ValidacaoSolicitacaoAdocao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AdocaoService {

    @Autowired
    private AdocaoRepository repository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private List<ValidacaoSolicitacaoAdocao> validacoes;


    public void solicitar(SolicitacaoAdocaoDTO dto){

        Pet pet = petRepository.getReferenceById(dto.idPet());
        Tutor tutor = tutorRepository.getReferenceById(dto.idTutor());

        validacoes.forEach(v -> v.validar(dto));

        Adocao adocao = new Adocao(tutor, pet, dto.motivo());
        repository.save(adocao);
        emailService.enviarEmail(adocao.getPet().getAbrigo().getEmail(), "Solicitação de adoção ", "Olá " +adocao.getPet().getAbrigo().getNome() +"!\n\nUma solicitação de adoção foi registrada hoje para o pet: " +adocao.getPet().getNome() +". \nFavor avaliar para aprovação ou reprovação.");

    }

    public void aprovar(AprovacaoAdocaoDTO dto){
        Adocao adocao = repository.getReferenceById(dto.idAdocao());
        adocao.marcarComoAprovada();
        repository.save(adocao);
        emailService.enviarEmail(adocao.getPet().getAbrigo().getEmail(), "Adoção aprovada ", "!\n\nSua adoção do pet " +adocao.getPet().getNome() +", solicitada em " +adocao.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) +", foi aprovada.\nFavor entrar em contato com o abrigo " +adocao.getPet().getAbrigo().getNome() +" para agendar a busca do seu pet.");
    }

    public void reprovar(ReprovacaoAdocaoDTO dto){
        Adocao adocao = repository.getReferenceById(dto.idAdocao());
        adocao.marcarComoPeprovada(dto.justificativa());
        adocao.setJustificativaStatus(dto.justificativa());
        repository.save(adocao);
        emailService.enviarEmail(adocao.getPet().getAbrigo().getEmail(), "Adoção reprovada ", "!\\n\\nInfelizmente sua adoção do pet \" +adocao.getPet().getNome() +\", solicitada em \" +adocao.getData().format(DateTimeFormatter.ofPattern(\"dd/MM/yyyy HH:mm:ss\")) +\", foi reprovada pelo abrigo \" +adocao.getPet().getAbrigo().getNome() +\" com a seguinte justificativa: \" +adocao.getJustificativaStatus()");
    }
}
