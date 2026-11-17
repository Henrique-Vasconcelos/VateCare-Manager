package Telas;

import Dao.ConsultaDAO;
import Dao.TutorDAO;
import Modelo.Consulta;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel painelConteudo;

    private final FormTutorPetPanel formTutorPetPanel;
    private final AgendamentoPanel agendamentoPanel;
    private final ProntuarioPanel prontuarioPanel;

    public MainFrame() {
        super("VetCare - Gestão Veterinária [Clínica Bem Animal]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);

        TutorDAO tutorDAO = new TutorDAO();
        ConsultaDAO consultaDAO = new ConsultaDAO();

        // 1. Sidebar de Navegação
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(15, 23, 42));
        sidebar.setPreferredSize(new Dimension(240, 800));
        sidebar.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel logo = new JLabel("VetCare");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setForeground(Color.WHITE);
        sidebar.add(logo);

        JLabel subLogo = new JLabel("GESTÃO VETERINÁRIA");
        subLogo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        subLogo.setForeground(new Color(148, 163, 184));
        sidebar.add(subLogo);

        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        JLabel lblEspaco = new JLabel("ESPAÇO DE TRABALHO");
        lblEspaco.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblEspaco.setForeground(new Color(100, 116, 139));
        sidebar.add(lblEspaco);

        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton btnTutores = criarBotaoSidebar("Tutores & Pets");
        JButton btnAgenda = criarBotaoSidebar("Agendamento");
        JButton btnProntuario = criarBotaoSidebar("Prontuário/Atendimento");

        sidebar.add(btnTutores);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnAgenda);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnProntuario);

        sidebar.add(Box.createVerticalGlue());

        JLabel lblUser = new JLabel("Julia Costa (Recepção)");
        lblUser.setForeground(new Color(203, 213, 225));
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sidebar.add(lblUser);

        // 2. Painel Central com CardLayout
        cardLayout = new CardLayout();
        painelConteudo = new JPanel(cardLayout);

        formTutorPetPanel = new FormTutorPetPanel(this, tutorDAO);
        agendamentoPanel = new AgendamentoPanel(this, tutorDAO, consultaDAO);
        prontuarioPanel = new ProntuarioPanel(this, consultaDAO);

        painelConteudo.add(formTutorPetPanel, "TUTORES");
        painelConteudo.add(agendamentoPanel, "AGENDAMENTO");
        painelConteudo.add(prontuarioPanel, "PRONTUARIO");

        btnTutores.addActionListener(e -> mostrarTela("TUTORES"));
        btnAgenda.addActionListener(e -> {
            agendamentoPanel.recarregarCombos();
            mostrarTela("AGENDAMENTO");
        });
        btnProntuario.addActionListener(e -> mostrarTela("PRONTUARIO"));

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(painelConteudo, BorderLayout.CENTER);

        mostrarTela("TUTORES");
    }

    private JButton criarBotaoSidebar(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(new Color(15, 23, 42));
        btn.setBackground(new Color(30, 41, 59));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 15, 10, 15));
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void mostrarTela(String nomeCard) {
        cardLayout.show(painelConteudo, nomeCard);
    }

    public void carregarNoProntuario(Consulta c) {
        prontuarioPanel.setConsulta(c);
    }

    public void atualizarListasGlobais() {
        agendamentoPanel.recarregarCombos();
        agendamentoPanel.atualizarTabelaConsultas();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
