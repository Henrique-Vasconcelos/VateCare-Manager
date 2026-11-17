package Dao;

import Modelo.Veterinario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VeterinarioDAO {
    public List<Veterinario> listarTodos() throws SQLException {
        List<Veterinario> lista = new ArrayList<>();
        String sql = "SELECT * FROM veterinarios ORDER BY nome ASC";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Veterinario(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("crmv"),
                    rs.getString("especialidade"),
                    rs.getString("telefone")
                ));
            }
        }
        return lista;
    }
}
