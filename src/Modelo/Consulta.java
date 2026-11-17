package Modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Consulta {
    private int id;
    private Pet pet;
    private Tutor tutor;
    private Veterinario veterinario;
    private LocalDateTime dataHora;
    private String motivo;
    private String diagnostico;
    private String prescricao;
    private BigDecimal valor;
    private String status; // AGENDADA, CONCLUIDA, CANCELADA

    public Consulta() {
        this.status = "AGENDADA";
        this.valor = new BigDecimal("150.00");
    }
    public Consulta(Pet pet,Tutor tutor, Veterinario veterinario, LocalDateTime dataHora, String motivo, BigDecimal valor) {
        this();
        this.pet = pet;
        this.veterinario = veterinario;
        this.dataHora = dataHora;
        this.motivo = motivo;
        this.valor = valor;
        this.tutor = tutor;
    }
    public Consulta(Pet pet, Tutor tutor, Veterinario veterinario, LocalDateTime dataHora, String motivo) {
    this(pet, tutor, veterinario, dataHora, motivo, new BigDecimal("150.00"));
}
    public void concluirConsulta(String diagnostico, String prescricao) {
        this.diagnostico = diagnostico;
        this.prescricao = prescricao;
        this.status = "CONCLUIDA";
    }

    public void cancelarConsulta() {
        this.status = "CANCELADA";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Veterinario getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(Veterinario veterinario) {
        this.veterinario = veterinario;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getPrescricao() {
        return prescricao;
    }

    public void setPrescricao(String prescricao) {
        this.prescricao = prescricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }
    
}