package com.agencia.dao;

import com.agencia.model.Pacote;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.agencia.model.Cliente;
import com.agencia.model.Estrangeiro;
import com.agencia.model.Nacional;
import com.agencia.util.DataBaseConnection;

public class ClienteDao {

    public void inserir(Cliente cliente) throws SQLException {
        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();

            String sql;
            PreparedStatement stmt;

            if (cliente instanceof Nacional) {
                sql = "INSERT INTO nacional (nome, telefone, email, cpf) VALUES (?, ?, ?, ?)";
                stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, cliente.getNome());
                stmt.setString(2, cliente.getTelefone());
                stmt.setString(3, cliente.getEmail());
                stmt.setString(4, ((Nacional) cliente).getCpf());

            } else if (cliente instanceof Estrangeiro) {
                sql = "INSERT INTO estrangeiro (nome, telefone, email, passaporte) VALUES (?, ?, ?, ?)";
                stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, cliente.getNome());
                stmt.setString(2, cliente.getTelefone());
                stmt.setString(3, cliente.getEmail());
                stmt.setString(4, ((Estrangeiro) cliente).getPassaporte());

            } else {
                throw new IllegalArgumentException("Tipo de cliente desconhecido");
            }

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setId(rs.getLong(1));
                }
            }

        } finally {
            if (conn != null)
                DataBaseConnection.desconectar(conn);
        }
    }
    
    public Nacional buscarNacionalPorCpf(String cpf) throws SQLException {
        String sql = "SELECT * FROM nacional WHERE cpf = ?";
        Connection conn = null;

        try {
            conn = DataBaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, cpf);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Nacional nacional = new Nacional();
                nacional.setId(rs.getLong("id"));
                nacional.setNome(rs.getString("nome"));
                nacional.setTelefone(rs.getString("telefone"));
                nacional.setEmail(rs.getString("email"));
                nacional.setCpf(rs.getString("cpf"));
                return nacional;
            } else {
                return null;
            }
        } finally {
            if (conn != null)
                DataBaseConnection.desconectar(conn);
        }
    }
    
    public Estrangeiro buscarEstrangeiroPorPassaporte(String passaporte) throws SQLException {
        String sql = "SELECT * FROM estrangeiro WHERE passaporte = ?";
        Connection conn = null;

        try {
            conn = DataBaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, passaporte);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Estrangeiro estrangeiro = new Estrangeiro();
                estrangeiro.setId(rs.getLong("id"));
                estrangeiro.setNome(rs.getString("nome"));
                estrangeiro.setTelefone(rs.getString("telefone"));
                estrangeiro.setEmail(rs.getString("email"));
                estrangeiro.setPassaporte(rs.getString("passaporte"));
                return estrangeiro;
            } else {
                return null;
            }
        } finally {
            if (conn != null)
                DataBaseConnection.desconectar(conn);
        }
    }
    
    public void listarTodosClientesSimples() throws SQLException {
        Connection conn = null;

        try {
            conn = DataBaseConnection.getConnection();

            System.out.println("\n📋 Lista de Clientes Nacionais:");
            String sqlNacional = "SELECT id, nome, cpf FROM nacional";
            PreparedStatement stmtNacional = conn.prepareStatement(sqlNacional);
            ResultSet rsNacional = stmtNacional.executeQuery();

            while (rsNacional.next()) {
                Long id = rsNacional.getLong("id");
                String nome = rsNacional.getString("nome");
                String cpf = rsNacional.getString("cpf");
                System.out.println("[" + id + "] " + nome + " - CPF: " + cpf);
            }

            System.out.println("\n📋 Lista de Clientes Estrangeiros:");
            String sqlEstrangeiro = "SELECT id, nome, passaporte FROM estrangeiro";
            PreparedStatement stmtEstrangeiro = conn.prepareStatement(sqlEstrangeiro);
            ResultSet rsEstrangeiro = stmtEstrangeiro.executeQuery();

            while (rsEstrangeiro.next()) {
                Long id = rsEstrangeiro.getLong("id");
                String nome = rsEstrangeiro.getString("nome");
                String passaporte = rsEstrangeiro.getString("passaporte");
                System.out.println("[" + id + "] " + nome + " - Passaporte: " + passaporte);
            }

        } finally {
            if (conn != null)
                DataBaseConnection.desconectar(conn);
        }
    }
    
    public void removerClientePorDocumento(String documento, String tipo) throws SQLException {
        Connection conn = null;

        try {
            conn = DataBaseConnection.getConnection();

            String sql;
            if (tipo.equalsIgnoreCase("n")) {
                sql = "DELETE FROM nacional WHERE cpf = ?";
            } else if (tipo.equalsIgnoreCase("e")) {
                sql = "DELETE FROM estrangeiro WHERE passaporte = ?";
            } else {
                throw new IllegalArgumentException("Tipo de cliente inválido para remoção");
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, documento);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("✅ Cliente removido com sucesso.");
            } else {
                System.out.println("❌ Cliente não encontrado.");
            }

        } finally {
            if (conn != null)
                DataBaseConnection.desconectar(conn);
        }
    }

    public void contratarPacoteNacional(Long nacionalId, Long pacoteId) throws SQLException {
        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();
            String sql = "INSERT INTO nacional_pacotes (nacional_id, pacote_id) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, nacionalId);
            stmt.setLong(2, pacoteId);
            stmt.executeUpdate();
        } finally {
            DataBaseConnection.desconectar(conn);
        }
    }

    public void contratarPacoteEstrangeiro(Long estrangeiroId, Long pacoteId) throws SQLException {
        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();
            String sql = "INSERT INTO estrangeiro_pacotes (estrangeiro_id, pacote_id) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, estrangeiroId);
            stmt.setLong(2, pacoteId);
            stmt.executeUpdate();
        } finally {
            DataBaseConnection.desconectar(conn);
        }
    }

    public List<Pacote> buscarPacotesContratadosNacional(Long nacionalId) throws SQLException {
        List<Pacote> pacotes = new ArrayList<>();
        String sql = "SELECT p.* FROM pacotes p " +
                     "INNER JOIN nacional_pacotes np ON p.id = np.pacote_id " +
                     "WHERE np.nacional_id = ?";
        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, nacionalId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Pacote pacote = new Pacote();
                pacote.setId(rs.getLong("id"));
                pacote.setNome(rs.getString("nome"));
                pacote.setDestino(rs.getString("destino"));
                pacote.setDuracao(rs.getInt("duracao"));
                pacote.setPreco(rs.getDouble("preco"));
                pacotes.add(pacote);
            }
            rs.close();
        } finally {
            DataBaseConnection.desconectar(conn);
        }
        return pacotes;
    }

    public List<Pacote> buscarPacotesContratadosEstrangeiro(Long estrangeiroId) throws SQLException {
        List<Pacote> pacotes = new ArrayList<>();
        String sql = "SELECT p.* FROM pacotes p " +
                     "INNER JOIN estrangeiro_pacotes ep ON p.id = ep.pacote_id " +
                     "WHERE ep.estrangeiro_id = ?";
        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, estrangeiroId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Pacote pacote = new Pacote();
                pacote.setId(rs.getLong("id"));
                pacote.setNome(rs.getString("nome"));
                pacote.setDestino(rs.getString("destino"));
                pacote.setDuracao(rs.getInt("duracao"));
                pacote.setPreco(rs.getDouble("preco"));
                pacotes.add(pacote);
            }
            rs.close();
        } finally {
            DataBaseConnection.desconectar(conn);
        }
        return pacotes;
    }

    public List<Nacional> buscarNacionaisPorPacote(Long pacoteId) throws SQLException {
        List<Nacional> nacionais = new ArrayList<>();
        String sql = "SELECT n.* FROM nacional n " +
                     "INNER JOIN nacional_pacotes np ON n.id = np.nacional_id " +
                     "WHERE np.pacote_id = ?";
        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, pacoteId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Nacional nacional = new Nacional();
                nacional.setId(rs.getLong("id"));
                nacional.setNome(rs.getString("nome"));
                nacional.setTelefone(rs.getString("telefone"));
                nacional.setEmail(rs.getString("email"));
                nacional.setCpf(rs.getString("cpf"));
                nacionais.add(nacional);
            }
            rs.close();
        } finally {
            if (conn != null) DataBaseConnection.desconectar(conn);
        }
        return nacionais;
    }

    public List<Estrangeiro> buscarEstrangeirosPorPacote(Long pacoteId) throws SQLException {
        List<Estrangeiro> estrangeiros = new ArrayList<>();
        String sql = "SELECT e.* FROM estrangeiro e " +
                     "INNER JOIN estrangeiro_pacotes ep ON e.id = ep.estrangeiro_id " +
                     "WHERE ep.pacote_id = ?";
        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, pacoteId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Estrangeiro estrangeiro = new Estrangeiro();
                estrangeiro.setId(rs.getLong("id"));
                estrangeiro.setNome(rs.getString("nome"));
                estrangeiro.setTelefone(rs.getString("telefone"));
                estrangeiro.setEmail(rs.getString("email"));
                estrangeiro.setPassaporte(rs.getString("passaporte"));
                estrangeiros.add(estrangeiro);
            }
            rs.close();
        } finally {
            if (conn != null) DataBaseConnection.desconectar(conn);
        }
        return estrangeiros;
    }


}
