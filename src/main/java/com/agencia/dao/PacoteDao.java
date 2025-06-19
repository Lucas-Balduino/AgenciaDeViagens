package com.agencia.dao;

import com.agencia.model.Pacote;
import com.agencia.model.Servico;
import com.agencia.util.DataBaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PacoteDao {
    public void inserir(Pacote pacote) throws SQLException {
        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();
            String sql = "INSERT INTO pacotes (nome, destino, duracao, preco) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, pacote.getNome());
            stmt.setString(2, pacote.getDestino());
            stmt.setInt(3, pacote.getDuracao());
            stmt.setDouble(4, pacote.getPreco());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                pacote.setId(rs.getLong(1));
            }
            rs.close();
        } finally {
            DataBaseConnection.desconectar(conn);
        }
    }

    public List<Pacote> buscarTodos() throws SQLException {
        List<Pacote> pacotes = new ArrayList<>();
        String sql = "SELECT * FROM pacotes";
        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
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

    public Pacote buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM pacotes WHERE id = ?";
        Connection conn = null;
        Pacote pacote = null;
        try {
            conn = DataBaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                pacote = new Pacote();
                pacote.setId(rs.getLong("id"));
                pacote.setNome(rs.getString("nome"));
                pacote.setDestino(rs.getString("destino"));
                pacote.setDuracao(rs.getInt("duracao"));
                pacote.setPreco(rs.getDouble("preco"));
            }
            rs.close();
        } finally {
            DataBaseConnection.desconectar(conn);
        }
        return pacote;
    }

    public void atualizar(Pacote pacote) throws SQLException {
        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();
            String sql = "UPDATE pacotes SET nome = ?, destino = ?, duracao = ?, preco = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, pacote.getNome());
            stmt.setString(2, pacote.getDestino());
            stmt.setInt(3, pacote.getDuracao());
            stmt.setDouble(4, pacote.getPreco());
            stmt.setLong(5, pacote.getId());
            stmt.executeUpdate();
        } finally {
            DataBaseConnection.desconectar(conn);
        }
    }

    public void deletar(Long id) throws SQLException {
        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();
            String sql = "DELETE FROM pacotes WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } finally {
            DataBaseConnection.desconectar(conn);
        }
    }

    public void adicionarServicoAoPacote(Long pacoteId, Long servicoId) throws SQLException {
        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();
            String sql = "INSERT INTO pacote_servico (pacote_id, servico_id) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, pacoteId);
            stmt.setLong(2, servicoId);
            stmt.executeUpdate();
        } finally {
            DataBaseConnection.desconectar(conn);
        }
    }

    public void removerServicoDoPacote(Long pacoteId, Long servicoId) throws SQLException {
        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();
            String sql = "DELETE FROM pacote_servico WHERE pacote_id = ? AND servico_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, pacoteId);
            stmt.setLong(2, servicoId);
            stmt.executeUpdate();
        } finally {
            DataBaseConnection.desconectar(conn);
        }
    }

    public List<Servico> buscarServicosDoPacote(Long pacoteId) throws SQLException {
        List<Servico> servicos = new ArrayList<>();
        String sql = "SELECT s.* FROM servicos s " +
                "INNER JOIN pacote_servico ps ON s.id = ps.servico_id " +
                "WHERE ps.pacote_id = ?";
        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, pacoteId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Servico servico = new Servico();
                servico.setId(rs.getLong("id"));
                servico.setNome(rs.getString("nome"));
                servico.setDuracao(rs.getInt("duracao"));
                servico.setDescricao(rs.getString("descricao"));
                servicos.add(servico);
            }
            rs.close();
        } finally {
            DataBaseConnection.desconectar(conn);
        }
        return servicos;
    }
    
    public boolean pacoteEstaContratado(Long pacoteId) throws SQLException {
        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();

            // Verificar em nacional_pacotes
            String sqlNacional = "SELECT * FROM nacional_pacotes WHERE pacote_id = ?";
            PreparedStatement stmtNacional = conn.prepareStatement(sqlNacional);
            stmtNacional.setLong(1, pacoteId);
            ResultSet rsNacional = stmtNacional.executeQuery();
            if (rsNacional.next()) {
                return true;
            }

            // Verificar em estrangeiro_pacotes
            String sqlEstrangeiro = "SELECT * FROM estrangeiro_pacotes WHERE pacote_id = ?";
            PreparedStatement stmtEstrangeiro = conn.prepareStatement(sqlEstrangeiro);
            stmtEstrangeiro.setLong(1, pacoteId);
            ResultSet rsEstrangeiro = stmtEstrangeiro.executeQuery();
            if (rsEstrangeiro.next()) {
                return true;
            }

            return false;

        } finally {
            if (conn != null) {
                DataBaseConnection.desconectar(conn);
            }
        }
    }

}