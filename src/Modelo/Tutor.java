package Modelo;

import java.util.ArrayList;
import java.util.List;

public class Tutor {
    private int id;
    private String nome;
    private String cpf;
    private String telefone;
    private String email;
    private List<Pet> pets;
    
    public Tutor(){
        this.pets = new ArrayList<>();
    }
    
    public Tutor(int id, String nome, String cpf, String telefone, String email) {
        this();
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.email = email;
    }
    
    public void adicionarPet(Pet pet){
        this.pets.add(pet);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Pet> getPets() {
        return pets;
    }
    
    @Override
    public String toString(){
        return nome;
    }
}
