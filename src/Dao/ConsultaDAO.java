package Dao;

import Modelo.Consulta;
import Modelo.Pet;
import Modelo.Tutor;
import Modelo.Veterinario;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConsultaDAO {
    /**
     * Valida a RN02: impede agendamento simultâneo para o mesmo veterinário.
     */
    public boolean existeConflitoHorario(int veterinarioId, LocalDateTime dataHora) throws SQLException {
        String sql = "SELECT COUNT(*) FROM consultas " + "WHERE veterinario_id = ? AND data_hora = ? AND status != 'CANCELADA'";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, veterinarioId);
            stmt.setTimestamp(2, Timestamp.valueOf(dataHora));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    /**
     * Executa o agendamento no banco respeitando as regras de integridade.
     */
    public boolean agendar(Consulta consulta) throws SQLException {
        if (existeConflitoHorario(consulta.getVeterinario().getId(), consulta.getDataHora())) {
            return false;
        }

        String sql = "INSERT INTO consultas (pet_id, veterinario_id, data_hora, motivo, valor, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, consulta.getPet().getId());
            stmt.setInt(2, consulta.getVeterinario().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(consulta.getDataHora()));
            stmt.setString(4, consulta.getMotivo());
            stmt.setBigDecimal(5, consulta.getValor());
            stmt.setString(6, consulta.getStatus());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        consulta.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    public List<Consulta> listarTodas() throws SQLException {
        List<Consulta> lista = new ArrayList<>();
        String sql = "SELECT c.id, c.data_hora, c.motivo, c.diagnostico, c.prescricao, c.valor, c.status, "
                   + "       p.id AS pet_id, p.nome AS pet_nome, p.especie AS pet_especie, p.raca AS pet_raca, p.peso AS pet_peso, "
                   + "       t.id AS tutor_id, t.nome AS tutor_nome, t.cpf AS tutor_cpf, t.telefone AS tutor_tel, t.email AS tutor_email, "
                   + "       v.id AS vet_id, v.nome AS vet_nome, v.crmv AS vet_crmv, v.especialidade AS vet_esp, v.telefone AS vet_tel "
                   + "FROM consultas c "
                   + "INNER JOIN pets p ON c.pet_id = p.id "
                   + "INNER JOIN tutores t ON p.tutor_id = t.id "
                   + "INNER JOIN veterinarios v ON c.veterinario_id = v.id "
                   + "ORDER BY c.data_hora ASC";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Tutor tutor = new Tutor(
                    rs.getInt("tutor_id"),
                    rs.getString("tutor_nome"),
                    rs.getString("tutor_cpf"),
                    rs.getString("tutor_tel"),
                    rs.getString("tutor_email")
                );

                Pet pet = new Pet(
                    rs.getInt("pet_id"),
                    tutor.getId(),
                    rs.getString("pet_nome"),
                    rs.getString("pet_especie"),
                    rs.getString("pet_raca"),
                    null,
                    rs.getDouble("pet_peso")
                );

                Veterinario vet = new Veterinario(
                    rs.getInt("vet_id"),
                    rs.getString("vet_nome"),
                    rs.getString("vet_crmv"),
                    rs.getString("vet_esp"),
                    rs.getString("vet_tel")
                );

                Consulta consulta = new Consulta();
                consulta.setPet(pet);
                consulta.setTutor(tutor);
                consulta.setVeterinario(vet);
                consulta.setDataHora(rs.getTimestamp("Data_Hora").toLocalDateTime());
                consulta.setMotivo(rs.getString("Motivo"));
                consulta.setId(rs.getInt("id"));
                consulta.setDiagnostico(rs.getString("diagnostico"));
                consulta.setPrescricao(rs.getString("prescricao"));
                consulta.setValor(rs.getBigDecimal("valor"));
                consulta.setStatus(rs.getString("status"));

                lista.add(consulta);
            }
        }
        return lista;
    }

    public void atualizarProntuario(Consulta consulta) throws SQLException {
        String sql = "UPDATE consultas SET diagnostico = ?, prescricao = ?, status = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, consulta.getDiagnostico());
            stmt.setString(2, consulta.getPrescricao());
            stmt.setString(3, consulta.getStatus());
            stmt.setInt(4, consulta.getId());
            stmt.executeUpdate();
        }
    }
}
