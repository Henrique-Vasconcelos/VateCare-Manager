package Dao;

import Modelo.Pet;
import Modelo.Tutor;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TutorDAO {
    public List<Tutor> listarTodos() throws SQLException {
        List<Tutor> lista = new ArrayList<>();
        String sql = "SELECT * FROM tutores ORDER BY id DESC";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Tutor tutor = new Tutor(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getString("telefone"),
                    rs.getString("email")
                );
                carregarPets(conn, tutor);
                lista.add(tutor);
            }
        }
        return lista;
    }

    private void carregarPets(Connection conn, Tutor tutor) throws SQLException {
        String sql = "SELECT * FROM pets WHERE tutor_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tutor.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Date dt = rs.getDate("data_nascimento");
                    LocalDate dataNasc = (dt != null) ? dt.toLocalDate() : LocalDate.now();
                    Pet pet = new Pet(
                        rs.getInt("id"),
                        tutor.getId(),
                        rs.getString("nome"),
                        rs.getString("especie"),
                        rs.getString("raca"),
                        dataNasc,
                        rs.getDouble("peso")
                    );
                    tutor.adicionarPet(pet);
                }
            }
        }
    }

    public void salvar(Tutor tutor) throws SQLException {
        if (tutor.getId() == 0) {
            String sql = "INSERT INTO tutores (nome, cpf, telefone, email) VALUES (?, ?, ?, ?)";
            try (Connection conn = Conexao.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, tutor.getNome());
                stmt.setString(2, tutor.getCpf());
                stmt.setString(3, tutor.getTelefone());
                stmt.setString(4, tutor.getEmail());
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        tutor.setId(rs.getInt(1));
                    }
                }
            }
        } else {
            String sql = "UPDATE tutores SET nome = ?, telefone = ?, email = ? WHERE id = ?";
            try (Connection conn = Conexao.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, tutor.getNome());
                stmt.setString(2, tutor.getTelefone());
                stmt.setString(3, tutor.getEmail());
                stmt.setInt(4, tutor.getId());
                stmt.executeUpdate();
            }
        }
    }

    public void adicionarPetAoTutor(Tutor tutor, Pet pet) throws SQLException {
        String sql = "INSERT INTO pets (tutor_id, nome, especie, raca, data_nascimento, peso) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, tutor.getId());
            stmt.setString(2, pet.getNome());
            stmt.setString(3, pet.getEspecie());
            stmt.setString(4, pet.getRaca());
            stmt.setDate(5, Date.valueOf(pet.getDataNascimento()));
            stmt.setDouble(6, pet.getPeso());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    pet.setId(rs.getInt(1));
                }
            }
        }
        pet.setTutorId(tutor.getId());
        tutor.adicionarPet(pet);
    }
}
