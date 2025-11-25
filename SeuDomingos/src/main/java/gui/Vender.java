/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gui;

import db.OperacoesDb;
import db.ProdutoDTO;
import java.time.LocalDate;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author tim
 */
public class Vender extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Vender.class.getName());
    private JComboBox<ProdutoDTO> comboProdutos;
    private JTextField campoCPF;
    private JTextField campoNomeCliente;
    private JTextField campoQuantidade;
    private JTextField campoValorTotal;
    private JButton botaoVender;

    /**
     * Creates new form Vender
     */
    public Vender() {
        initMyComponents();
        carregarProdutos();
        configurarEventos();
    }
    
    private void carregarProdutos() {
        List<ProdutoDTO> produtos = OperacoesDb.listarProdutos();
        for (ProdutoDTO p : produtos) {
            comboProdutos.addItem(p);
        }
    }
    
     private void concluirVenda() {

        try {
            String cpf = campoCPF.getText().trim();
            String nome = campoNomeCliente.getText().trim();
            ProdutoDTO p = (ProdutoDTO) comboProdutos.getSelectedItem();
            int quantidade = Integer.parseInt(campoQuantidade.getText());
            double valor = Double.parseDouble(campoValorTotal.getText());
            
            if (quantidade > p.getQuantidade()) {
            JOptionPane.showMessageDialog(
                this,
                "Quantidade insuficiente em estoque!\n" +
                "Estoque disponível: " + p.getQuantidade(),
                "Erro de estoque",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

            // 1. Buscar cliente
            Integer idCliente = OperacoesDb.buscarClientePorCPF(cpf);

            // 2. Se cliente não existe, criar
            if (idCliente == null) {
                if (nome.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Cliente não encontrado. Informe o nome para cadastrar.");
                    return;
                }
                idCliente = OperacoesDb.criarCliente(cpf, nome);
            }

            // 3. Registrar venda
            boolean vendaOK = OperacoesDb.registrarVenda(
                    valor,
                    LocalDate.now(),
                    idCliente,
                    p.getId(),
                    quantidade
            );

            if (!vendaOK) {
                JOptionPane.showMessageDialog(this, "Erro ao registrar venda.");
                return;
            }

            // 4. Atualizar estoque
            boolean estoqueOK = OperacoesDb.atualizarEstoque(p.getId(), quantidade);

            if (!estoqueOK) {
                JOptionPane.showMessageDialog(this, "Venda registrada, mas não foi possível atualizar o estoque.");
            } else {
                JOptionPane.showMessageDialog(this, "Venda concluída com sucesso!");
            }

            this.dispose();

            this.setVisible(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }
    
    private void configurarEventos() {
        comboProdutos.addActionListener(e -> atualizarValorTotal());
        campoQuantidade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                atualizarValorTotal();
            }
        });

        botaoVender.addActionListener(e -> concluirVenda());
    }

    private void atualizarValorTotal() {
        ProdutoDTO p = (ProdutoDTO) comboProdutos.getSelectedItem();
        if (p == null) return;

        try {
            int q = Integer.parseInt(campoQuantidade.getText());
            campoValorTotal.setText(String.valueOf(p.getPreco() / 100 * q));
        } catch (Exception e) {
            campoValorTotal.setText("");
        }
    }
    
    private void initMyComponents() {
        campoCPF = new JTextField();
        campoNomeCliente = new JTextField();
        comboProdutos = new JComboBox<>();
        campoQuantidade = new JTextField();
        campoValorTotal = new JTextField();
        campoValorTotal.setEditable(false);
        botaoVender = new JButton("Concluir Venda");

        JLabel labelCPF = new JLabel("CPF do cliente:");
        JLabel labelNome = new JLabel("Nome do cliente (se novo):");
        JLabel labelProduto = new JLabel("Produto:");
        JLabel labelQuantidade = new JLabel("Quantidade:");
        JLabel labelValor = new JLabel("Valor total:");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Realizar Venda");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(30)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(labelCPF)
                        .addComponent(campoCPF, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(labelNome)
                        .addComponent(campoNomeCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(labelProduto)
                        .addComponent(comboProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(labelQuantidade)
                        .addComponent(campoQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(labelValor)
                        .addComponent(campoValorTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(botaoVender, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    )
                    .addContainerGap(500, Short.MAX_VALUE)
                )
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(20)
                    .addComponent(labelCPF)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(campoCPF, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)

                    .addGap(15)
                    .addComponent(labelNome)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(campoNomeCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)

                    .addGap(15)
                    .addComponent(labelProduto)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(comboProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)

                    .addGap(15)
                    .addComponent(labelQuantidade)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(campoQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)

                    .addGap(15)
                    .addComponent(labelValor)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(campoValorTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)

                    .addGap(20)
                    .addComponent(botaoVender, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)

                    .addContainerGap(50, Short.MAX_VALUE)
                )
        );

        pack();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Vender().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
