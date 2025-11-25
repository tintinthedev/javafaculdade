/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tim
 */
public class OperacoesDb {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    
    public static List<ProdutoDTO> listarProdutos() {
        List<ProdutoDTO> lista = new ArrayList<>();

        String sql = "SELECT id, nome, preco, quantidade FROM produtos";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ProdutoDTO p = new ProdutoDTO(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getDouble("preco"),
                    rs.getInt("quantidade")
                );
                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
    
    public static void atualizarProduto(int id, String nome, int preco, int quantidade) throws Exception {
        String sql = "UPDATE produtos SET nome=?, preco=?, quantidade=? WHERE id=?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setInt(2, preco);
            stmt.setInt(3, quantidade);
            stmt.setInt(4, id);

            stmt.executeUpdate();
        }
    }
    
    public static boolean atualizarEstoque(int produtoId, int quantidadeVendida) {
    String sql = "UPDATE produtos SET quantidade = quantidade - ? WHERE id = ?";

    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, quantidadeVendida);
        stmt.setInt(2, produtoId);

        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
    
    public static boolean registrarVenda(
        double valorVenda,
            LocalDate dataVenda,
        int idCliente,
        int idProduto,
        int quantidadeVendida
    ) {

    String sql = "INSERT INTO venda (valor_venda, data_venda, id_cliente, id_produto, quantidade_vendida) VALUES (?, ?, ?, ?, ?)";

    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setDouble(1, valorVenda);
        stmt.setDate(2, java.sql.Date.valueOf(dataVenda));
        stmt.setInt(3, idCliente);
        stmt.setInt(4, idProduto);
        stmt.setInt(5, quantidadeVendida);

        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


    
    public static int criarCliente(String cpf, String nome) {
    String sql = "INSERT INTO cliente (cpf, nome) VALUES (?, ?) RETURNING id";

    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, cpf);
        stmt.setString(2, nome);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("id");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return -1;
}

    
    public static Integer buscarClientePorCPF(String cpf) {
    String sql = "SELECT id FROM cliente WHERE cpf = ?";
    
    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, cpf);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getInt("id");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}
    
    public static void inserirProduto(String nomeStr, int preco, String quantidadeStr) {
        String nome = nomeStr;           
        int quantidade = Integer.parseInt(quantidadeStr);

        String sql = "INSERT INTO produtos (nome, preco, quantidade) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setInt(2, preco);
            stmt.setInt(3, quantidade);

            stmt.executeUpdate();
            System.out.println("Produto inserido com sucesso!");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Erro: preço ou quantidade não são números válidos.");
        }
    }
}
