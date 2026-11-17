package Telas;

import Dao.ConsultaDAO;
import Dao.TutorDAO;
import Dao.VeterinarioDAO;
import Modelo.Consulta;
import Modelo.Pet;
import Modelo.Tutor;
import Modelo.Veterinario;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AgendamentoPanel extends JPanel {
    private final MainFrame mainFrame;
    private final TutorDAO tutorDAO;
    private final ConsultaDAO consultaDAO;
    private final VeterinarioDAO veterinarioDAO;

    private JComboBox<Tutor> cbTutores;
    private JComboBox<Pet> cbPets;
    private JComboBox<Veterinario> cbVeterinarios;
    private JComboBox<String> cbHorarios;
    private JTextField txtMotivo;
    private JTable tabelaGrade;
    private DefaultTableModel modelGrade;

    private final DateTimeFormatter horaFmt = DateTimeFormatter.ofPattern("HH:mm");

    public AgendamentoPanel(MainFrame mainFrame, TutorDAO tutorDAO, ConsultaDAO consultaDAO) {
        this.mainFrame = mainFrame;
        this.tutorDAO = tutorDAO;
        this.consultaDAO = consultaDAO;
        this.veterinarioDAO = new VeterinarioDAO();

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 25, 20, 25));
        setBackground(new Color(248, 250, 252));

        add(criarTopo(), BorderLayout.NORTH);
        add(criarCorpo(), BorderLayout.CENTER);
    }

    private JPanel criarTopo() {
        JPanel topo = new JPanel(new GridLayout(2, 1));
        topo.setOpaque(false);

        JLabel lblTitulo = new JLabel("Agenda de Consultas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Grade de atendimentos vinculada diretamente ao banco MySQL.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));

        topo.add(lblTitulo);
        topo.add(lblSub);
        return topo;
    }

    private JPanel criarCorpo() {
        JPanel corpo = new JPanel(new BorderLayout(20, 0));
        corpo.setOpaque(false);

        // Formulário Esquerda
        JPanel pForm = new JPanel(new GridBagLayout());
        pForm.setBackground(Color.WHITE);
        pForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(15, 15, 15, 15)));
        pForm.setPreferredSize(new Dimension(380, 400));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblNova = new JLabel("Nova Consulta");
        lblNova.setFont(new Font("Segoe UI", Font.BOLD, 15));
        pForm.add(lblNova, gbc);

        gbc.gridy = 1; pForm.add(new JLabel("Tutor:"), gbc);
        gbc.gridy = 2;
        cbTutores = new JComboBox<>();
        cbTutores.addActionListener(e -> atualizarComboPets());
        pForm.add(cbTutores, gbc);

        gbc.gridy = 3; pForm.add(new JLabel("Pet:"), gbc);
        gbc.gridy = 4;
        cbPets = new JComboBox<>();
        pForm.add(cbPets, gbc);

        gbc.gridy = 5; pForm.add(new JLabel("Veterinário:"), gbc);
        gbc.gridy = 6;
        cbVeterinarios = new JComboBox<>();
        pForm.add(cbVeterinarios, gbc);

        gbc.gridy = 7; pForm.add(new JLabel("Horário Disponível:"), gbc);
        gbc.gridy = 8;
        cbHorarios = new JComboBox<>(new String[]{"08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "14:00", "14:30", "15:00", "15:30", "16:00"});
        pForm.add(cbHorarios, gbc);

        gbc.gridy = 9; pForm.add(new JLabel("Motivo / Observações:"), gbc);
        gbc.gridy = 10;
        txtMotivo = new JTextField("Consulta de rotina");
        pForm.add(txtMotivo, gbc);

        gbc.gridy = 11;
        JButton btnAgendar = new JButton("+ Agendar Consulta");
        btnAgendar.setBackground(new Color(13, 148, 136));
        btnAgendar.setForeground(new Color(15, 23, 42));
        btnAgendar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAgendar.addActionListener(e -> realizarAgendamento());
        pForm.add(btnAgendar, gbc);

        corpo.add(pForm, BorderLayout.WEST);

        // Grade Direita
        JPanel pGrade = new JPanel(new BorderLayout(10, 10));
        pGrade.setBackground(Color.WHITE);
        pGrade.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(15, 15, 15, 15)));

        JLabel lblHoje = new JLabel("Grade de Atendimentos Cadastrados no Banco");
        lblHoje.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pGrade.add(lblHoje, BorderLayout.NORTH);

        String[] cols = {"ID", "Hora", "Pet", "Tutor", "Veterinário", "Motivo", "Status"};
        modelGrade = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tabelaGrade = new JTable(modelGrade);
        tabelaGrade.setRowHeight(26);
        pGrade.add(new JScrollPane(tabelaGrade), BorderLayout.CENTER);

        JPanel pBotoesAcao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pBotoesAcao.setOpaque(false);
        JButton btnIrProntuario = new JButton("Abrir Atendimento no Prontuário →");
        btnIrProntuario.setBackground(new Color(30, 41, 59));
        btnIrProntuario.setForeground(new Color(15, 23, 42));
        btnIrProntuario.addActionListener(e -> abrirConsultaSelecionada());
        pBotoesAcao.add(btnIrProntuario);
        pGrade.add(pBotoesAcao, BorderLayout.SOUTH);

        corpo.add(pGrade, BorderLayout.CENTER);
        return corpo;
    }

    public void recarregarCombos() {
        try {
            cbTutores.removeAllItems();
            for (Tutor t : tutorDAO.listarTodos()) {
                cbTutores.addItem(t);
            }

            cbVeterinarios.removeAllItems();
            for (Veterinario v : veterinarioDAO.listarTodos()) {
                cbVeterinarios.addItem(v);
            }

            atualizarComboPets();
            atualizarTabelaConsultas();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao recarregar dados do banco: " + e.getMessage());
        }
    }

    private void atualizarComboPets() {
        cbPets.removeAllItems();
        Tutor selecionado = (Tutor) cbTutores.getSelectedItem();
        if (selecionado != null) {
            for (Pet p : selecionado.getPets()) {
                cbPets.addItem(p);
            }
        }
    }

    private void realizarAgendamento() {
        Tutor tutor = (Tutor) cbTutores.getSelectedItem();
        Pet pet = (Pet) cbPets.getSelectedItem();
        Veterinario vet = (Veterinario) cbVeterinarios.getSelectedItem();

        if (tutor == null || pet == null || vet == null) {
            JOptionPane.showMessageDialog(this, "Selecione o tutor, o pet e o veterinário!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String horaStr = (String) cbHorarios.getSelectedItem();
        LocalTime lt = LocalTime.parse(horaStr);
        LocalDateTime ldt = LocalDateTime.of(LocalDate.of(2026, 11, 20), lt);

        Consulta consulta = new Consulta(pet, tutor, vet, ldt, txtMotivo.getText().trim());

        try {
            boolean sucesso = consultaDAO.agendar(consulta);
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Consulta gravada com sucesso no MySQL!");
                atualizarTabelaConsultas();
            } else {
                JOptionPane.showMessageDialog(this, "Conflito de Horário! " + vet.getNome() + " já possui consulta marcada neste horário.", "Aviso", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao agendar no MySQL: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void atualizarTabelaConsultas() {
        modelGrade.setRowCount(0);
        try {
            for (Consulta c : consultaDAO.listarTodas()) {
                modelGrade.addRow(new Object[]{
                    c.getId(),
                    c.getDataHora().format(horaFmt),
                    c.getPet().getNome(),
                    c.getTutor().getNome(),
                    c.getVeterinario().getNome(),
                    c.getMotivo(),
                    c.getStatus()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao listar consultas do banco: " + e.getMessage());
        }
    }

    private void abrirConsultaSelecionada() {
        int row = tabelaGrade.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma consulta na grade primeiro!");
            return;
        }
        int consultaId = (int) modelGrade.getValueAt(row, 0);
        try {
            for (Consulta c : consultaDAO.listarTodas()) {
                if (c.getId() == consultaId) {
                    mainFrame.carregarNoProntuario(c);
                    mainFrame.mostrarTela("PRONTUARIO");
                    break;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar consulta: " + e.getMessage());
        }
    }
}
