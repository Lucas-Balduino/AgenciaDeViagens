package com.agencia.dao;

import com.agencia.model.Servico;
import com.agencia.util.DataBaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServicoDao {
    public void inserir(Servico servico) throws SQLException {
        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();
            String sql = "INSERT INTO servicos (nome, duracao, descricao) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, servico.getNome());
            stmt.setInt(2, servico.getDuracao());
            stmt.setString(3, servico.getDescricao());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                servico.setId(rs.getLong(1));
            }
            rs.close();
        } finally {
            DataBaseConnection.desconectar(conn);
        }
    }

    public List<Servico> buscarTodos() throws SQLException {
        List<Servico> servicos = new ArrayList<>();
        String sql = "SELECT * FROM servicos";
        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
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

    public Servico buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM servicos WHERE id = ?";
        Connection conn = null;
        Servico servico = null;
        try {
            conn = DataBaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                servico = new Servico();
                servico.setId(rs.getLong("id"));
                servico.setNome(rs.getString("nome"));
                servico.setDuracao(rs.getInt("duracao"));
                servico.setDescricao(rs.getString("descricao"));
            }
            rs.close();
        } finally {
            DataBaseConnection.desconectar(conn);
        }
        return servico;
    }

    public void atualizar(Servico servico) throws SQLException {
        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();
            String sql = "UPDATE servicos SET nome = ?, duracao = ?, descricao = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, servico.getNome());
            stmt.setInt(2, servico.getDuracao());
            stmt.setString(3, servico.getDescricao());
            stmt.setLong(4, servico.getId());
            stmt.executeUpdate();
        } finally {
            DataBaseConnection.desconectar(conn);
        }
    }

    public void deletar(Long id) throws SQLException {
        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();
            String sql = "DELETE FROM servicos WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } finally {
            DataBaseConnection.desconectar(conn);
        }
    }

    public List<Servico> buscarPorNome(String nome) throws SQLException {
        List<Servico> servicos = new ArrayList<>();
        String sql = "SELECT * FROM servicos WHERE nome LIKE ?";
        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + nome + "%");
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
}