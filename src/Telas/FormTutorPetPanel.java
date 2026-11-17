package Telas;

import Dao.TutorDAO;
import Modelo.Tutor;
import Modelo.Pet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;


public class FormTutorPetPanel extends JPanel {
    private final TutorDAO tutorDAO;
    private final MainFrame mainFrame;

    private JTextField txtNomeTutor, txtCpf, txtTelefone, txtEmail;
    private JTextField txtNomePet, txtRaca, txtPeso;
    private JComboBox<String> cbEspecie;
    private JLabel lblTutorSelecionado, lblPetsVinculados;
    private JTable tabelaTutores;
    private DefaultTableModel tableModel;

    private Tutor tutorSelecionado;
    private List<Tutor> cacheTutores;

    public FormTutorPetPanel(MainFrame mainFrame, TutorDAO tutorDAO) {
        this.mainFrame = mainFrame;
        this.tutorDAO = tutorDAO;

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 25, 20, 25));
        setBackground(new Color(248, 250, 252));

        add(criarCabecalho(), BorderLayout.NORTH);
        add(criarCentro(), BorderLayout.CENTER);
        add(criarTabelaPainel(), BorderLayout.SOUTH);

        carregarDados();
    }

    private JPanel criarCabecalho() {
        JPanel p = new JPanel(new GridLayout(2, 1));
        p.setOpaque(false);
        JLabel title = new JLabel("Gestão de Tutores e Pacientes");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(15, 23, 42));

        JLabel sub = new JLabel("Cada cuidado começa com um vínculo. Dados persistidos diretamente no MySQL.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(100, 116, 139));

        p.add(title);
        p.add(sub);
        return p;
    }

    private JPanel criarCentro() {
        JPanel centro = new JPanel(new GridLayout(1, 2, 20, 0));
        centro.setOpaque(false);

        // Formulário Tutor
        JPanel pTutor = new JPanel(new GridBagLayout());
        pTutor.setBackground(Color.WHITE);
        pTutor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(15, 15, 15, 15)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblSecao = new JLabel("Dados do Tutor");
        lblSecao.setFont(new Font("Segoe UI", Font.BOLD, 15));
        pTutor.add(lblSecao, gbc);

        gbc.gridy = 1; pTutor.add(new JLabel("Nome Completo:"), gbc);
        gbc.gridy = 2; txtNomeTutor = new JTextField(); pTutor.add(txtNomeTutor, gbc);

        gbc.gridy = 3; pTutor.add(new JLabel("CPF (000.000.000-00):"), gbc);
        gbc.gridy = 4; txtCpf = new JTextField(); pTutor.add(txtCpf, gbc);

        gbc.gridy = 5; pTutor.add(new JLabel("Telefone:"), gbc);
        gbc.gridy = 6; txtTelefone = new JTextField(); pTutor.add(txtTelefone, gbc);

        gbc.gridy = 7; pTutor.add(new JLabel("E-mail:"), gbc);
        gbc.gridy = 8; txtEmail = new JTextField(); pTutor.add(txtEmail, gbc);

        gbc.gridy = 9; gbc.gridwidth = 1;
        JButton btnSalvar = new JButton("✓ Salvar Tutor");
        btnSalvar.setBackground(new Color(13, 148, 136));
        btnSalvar.setForeground(new Color(15, 23, 42));
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSalvar.addActionListener(e -> salvarTutor());
        pTutor.add(btnSalvar, gbc);

        gbc.gridx = 1;
        JButton btnLimpar = new JButton("Limpar");
        btnLimpar.addActionListener(e -> limparFormTutor());
        pTutor.add(btnLimpar, gbc);

        centro.add(pTutor);

        // Formulário Pets
        JPanel pPet = new JPanel(new GridBagLayout());
        pPet.setBackground(Color.WHITE);
        pPet.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(15, 15, 15, 15)));

        GridBagConstraints gbcP = new GridBagConstraints();
        gbcP.fill = GridBagConstraints.HORIZONTAL;
        gbcP.insets = new Insets(5, 5, 5, 5);
        gbcP.weightx = 1.0;

        gbcP.gridx = 0; gbcP.gridy = 0; gbcP.gridwidth = 2;
        lblTutorSelecionado = new JLabel("Pets do Tutor: Selecione um tutor abaixo");
        lblTutorSelecionado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTutorSelecionado.setForeground(new Color(13, 148, 136));
        pPet.add(lblTutorSelecionado, gbcP);

        gbcP.gridy = 1;
        lblPetsVinculados = new JLabel("Nenhum pet carregado.");
        pPet.add(lblPetsVinculados, gbcP);

        gbcP.gridy = 2; pPet.add(new JLabel("Adicionar Novo Pet:"), gbcP);

        gbcP.gridy = 3; gbcP.gridwidth = 1; pPet.add(new JLabel("Nome do Pet:"), gbcP);
        gbcP.gridx = 1; pPet.add(new JLabel("Espécie:"), gbcP);

        gbcP.gridx = 0; gbcP.gridy = 4; txtNomePet = new JTextField(); pPet.add(txtNomePet, gbcP);
        gbcP.gridx = 1; cbEspecie = new JComboBox<>(new String[]{"Cão", "Gato", "Ave", "Outro"}); pPet.add(cbEspecie, gbcP);

        gbcP.gridx = 0; gbcP.gridy = 5; pPet.add(new JLabel("Raça:"), gbcP);
        gbcP.gridx = 1; pPet.add(new JLabel("Peso (kg):"), gbcP);

        gbcP.gridx = 0; gbcP.gridy = 6; txtRaca = new JTextField(); pPet.add(txtRaca, gbcP);
        gbcP.gridx = 1; txtPeso = new JTextField("0.0"); pPet.add(txtPeso, gbcP);

        gbcP.gridx = 0; gbcP.gridy = 7; gbcP.gridwidth = 2;
        JButton btnAddPet = new JButton("+ Adicionar Pet ao Tutor");
        btnAddPet.setBackground(new Color(30, 41, 59));
        btnAddPet.setForeground(new Color(15, 23, 42));
        btnAddPet.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAddPet.addActionListener(e -> adicionarPet());
        pPet.add(btnAddPet, gbcP);

        centro.add(pPet);
        return centro;
    }

    private JPanel criarTabelaPainel() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder("Tutores no MySQL (Clique para selecionar)"));
        p.setPreferredSize(new Dimension(600, 180));

        String[] cols = {"ID", "Nome", "CPF", "Telefone", "E-mail", "Qtd. Pets"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tabelaTutores = new JTable(tableModel);
        tabelaTutores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaTutores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabelaTutores.getSelectedRow() != -1) {
                selecionarTutorPorLinha(tabelaTutores.getSelectedRow());
            }
        });

        p.add(new JScrollPane(tabelaTutores), BorderLayout.CENTER);
        return p;
    }

    public void carregarDados() {
        tableModel.setRowCount(0);
        try {
            cacheTutores = tutorDAO.listarTodos();
            for (Tutor t : cacheTutores) {
                tableModel.addRow(new Object[]{
                    t.getId(), t.getNome(), t.getCpf(), t.getTelefone(), t.getEmail(), t.getPets().size() + " pet(s)"
                });
            }
            if (!cacheTutores.isEmpty()) {
                tabelaTutores.setRowSelectionInterval(0, 0);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar com MySQL: " + e.getMessage(), "Erro de Banco", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selecionarTutorPorLinha(int row) {
        int id = (int) tableModel.getValueAt(row, 0);
        for (Tutor t : cacheTutores) {
            if (t.getId() == id) {
                tutorSelecionado = t;
                txtNomeTutor.setText(t.getNome());
                txtCpf.setText(t.getCpf());
                txtTelefone.setText(t.getTelefone());
                txtEmail.setText(t.getEmail());

                lblTutorSelecionado.setText("Pets de: " + t.getNome());
                StringBuilder sb = new StringBuilder("<html>");
                for (Pet p : t.getPets()) {
                    sb.append("• <b>").append(p.getNome()).append("</b> (").append(p.getRaca()).append(" - ").append(p.getPeso()).append("kg)<br>");
                }
                sb.append("</html>");
                lblPetsVinculados.setText(t.getPets().isEmpty() ? "Nenhum pet cadastrado." : sb.toString());
                break;
            }
        }
    }

    private void salvarTutor() {
        if (txtNomeTutor.getText().trim().isEmpty() || txtCpf.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e CPF são campos obrigatórios!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (tutorSelecionado != null && tutorSelecionado.getCpf().equals(txtCpf.getText().trim())) {
                tutorSelecionado.setNome(txtNomeTutor.getText().trim());
                tutorSelecionado.setTelefone(txtTelefone.getText().trim());
                tutorSelecionado.setEmail(txtEmail.getText().trim());
                tutorDAO.salvar(tutorSelecionado);
                JOptionPane.showMessageDialog(this, "Tutor atualizado no MySQL!");
            } else {
                Tutor novo = new Tutor(0, txtNomeTutor.getText().trim(), txtCpf.getText().trim(),
                        txtTelefone.getText().trim(), txtEmail.getText().trim());
                tutorDAO.salvar(novo);
                JOptionPane.showMessageDialog(this, "Tutor inserido no MySQL!");
            }
            carregarDados();
            mainFrame.atualizarListasGlobais();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar tutor no MySQL (verifique se o CPF já existe): " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void adicionarPet() {
        if (tutorSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um tutor na tabela primeiro!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (txtNomePet.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome do pet!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double peso = 0.0;
        try {
            peso = Double.parseDouble(txtPeso.getText().replace(",", "."));
        } catch (NumberFormatException ignored) {}

        Pet p = new Pet(0, tutorSelecionado.getId(), txtNomePet.getText().trim(),
                (String) cbEspecie.getSelectedItem(), txtRaca.getText().trim(), LocalDate.now(), peso);

        try {
            tutorDAO.adicionarPetAoTutor(tutorSelecionado, p);
            JOptionPane.showMessageDialog(this, "Pet " + p.getNome() + " persistido no MySQL!");

            txtNomePet.setText("");
            txtRaca.setText("");
            txtPeso.setText("0.0");
            carregarDados();
            mainFrame.atualizarListasGlobais();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao gravar pet: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparFormTutor() {
        tutorSelecionado = null;
        txtNomeTutor.setText("");
        txtCpf.setText("");
        txtTelefone.setText("");
        txtEmail.setText("");
        lblTutorSelecionado.setText("Novo Tutor em Cadastro");
        lblPetsVinculados.setText("Salve o tutor antes de vincular animais.");
        tabelaTutores.clearSelection();
    }
}
