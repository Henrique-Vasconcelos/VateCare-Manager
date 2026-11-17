package Telas;

import Dao.ConsultaDAO;
import Modelo.Consulta;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class ProntuarioPanel extends JPanel {
    private final MainFrame mainFrame;
    private final ConsultaDAO consultaDAO;
    private Consulta consultaAtiva;

    private JLabel lblPaciente, lblTutor, lblStatus;
    private JTextField txtPeso, txtTemp, txtFC, txtFR;
    private JTextArea txtAnamnese, txtExameFisico, txtDiagnostico, txtConduta;

    public ProntuarioPanel(MainFrame mainFrame, ConsultaDAO consultaDAO) {
        this.mainFrame = mainFrame;
        this.consultaDAO = consultaDAO;

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 25, 20, 25));
        setBackground(new Color(248, 250, 252));

        add(criarCabecalho(), BorderLayout.NORTH);
        add(criarCorpoClinico(), BorderLayout.CENTER);
        add(criarRodape(), BorderLayout.SOUTH);
    }

    private JPanel criarCabecalho() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Color.WHITE);
        topo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(12, 16, 12, 16)));

        JPanel pInfo = new JPanel(new GridLayout(2, 1));
        pInfo.setOpaque(false);
        lblPaciente = new JLabel("Paciente: Nenhum atendimento aberto no momento");
        lblPaciente.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPaciente.setForeground(new Color(15, 23, 42));

        lblTutor = new JLabel("Selecione uma consulta na aba de Agendamento para iniciar");
        lblTutor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTutor.setForeground(new Color(100, 116, 139));

        pInfo.add(lblPaciente);
        pInfo.add(lblTutor);

        lblStatus = new JLabel("AGUARDANDO");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStatus.setOpaque(true);
        lblStatus.setBackground(new Color(254, 240, 138));
        lblStatus.setBorder(new EmptyBorder(4, 12, 4, 12));

        topo.add(pInfo, BorderLayout.CENTER);
        topo.add(lblStatus, BorderLayout.EAST);
        return topo;
    }

    private JPanel criarCorpoClinico() {
        JPanel corpo = new JPanel(new BorderLayout(15, 10));
        corpo.setOpaque(false);

        // Barra de Sinais Vitais
        JPanel pSinais = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        pSinais.setBackground(Color.WHITE);
        pSinais.setBorder(BorderFactory.createTitledBorder("Sinais Vitais e Avaliação Inicial"));

        pSinais.add(new JLabel("Peso (kg):"));
        txtPeso = new JTextField("28.5", 5);
        pSinais.add(txtPeso);

        pSinais.add(new JLabel("Temperatura (°C):"));
        txtTemp = new JTextField("38.6", 5);
        pSinais.add(txtTemp);

        pSinais.add(new JLabel("FC (bpm):"));
        txtFC = new JTextField("92", 5);
        pSinais.add(txtFC);

        pSinais.add(new JLabel("FR (irpm):"));
        txtFR = new JTextField("24", 5);
        pSinais.add(txtFR);

        corpo.add(pSinais, BorderLayout.NORTH);

        // Áreas de Texto Clínico
        JPanel pCampos = new JPanel(new GridLayout(2, 2, 15, 15));
        pCampos.setOpaque(false);

        txtAnamnese = new JTextArea(4, 20);
        txtAnamnese.setLineWrap(true);
        txtExameFisico = new JTextArea(4, 20);
        txtExameFisico.setLineWrap(true);
        txtDiagnostico = new JTextArea(4, 20);
        txtDiagnostico.setLineWrap(true);
        txtConduta = new JTextArea(4, 20);
        txtConduta.setLineWrap(true);

        pCampos.add(criarCardTexto("Anamnese e Queixa Principal", txtAnamnese));
        pCampos.add(criarCardTexto("Exame Físico / Achados", txtExameFisico));
        pCampos.add(criarCardTexto("Diagnóstico / Hipótese Diagnóstica", txtDiagnostico));
        pCampos.add(criarCardTexto("Conduta / Prescrição Medicamentosa", txtConduta));

        corpo.add(pCampos, BorderLayout.CENTER);
        return corpo;
    }

    private JPanel criarCardTexto(String titulo, JTextArea area) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(titulo));
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        return p;
    }

    private JPanel criarRodape() {
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        rodape.setBackground(Color.WHITE);

        JButton btnSalvar = new JButton("Salvar Rascunho");
        btnSalvar.addActionListener(e -> salvarDadosClinicos(false));

        JButton btnFinalizar = new JButton("✓ Concluir Atendimento");
        btnFinalizar.setBackground(new Color(13, 148, 136));
        btnFinalizar.setForeground(new Color(15, 23, 42));
        btnFinalizar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnFinalizar.addActionListener(e -> salvarDadosClinicos(true));

        rodape.add(btnSalvar);
        rodape.add(btnFinalizar);
        return rodape;
    }

    public void setConsulta(Consulta c) {
        this.consultaAtiva = c;
        if (c != null) {
            lblPaciente.setText("Paciente: " + c.getPet().getNome() + " (" + (c.getPet().getRaca() != null ? c.getPet().getRaca() : c.getPet().getEspecie()) + ")");
            lblTutor.setText("Tutor: " + c.getTutor().getNome() + " | Tel: " + c.getTutor().getTelefone() + " | Vet: " + c.getVeterinario().getNome());
            lblStatus.setText(c.getStatus());
            txtPeso.setText(String.valueOf(c.getPet().getPeso()));
            txtDiagnostico.setText(c.getDiagnostico() != null ? c.getDiagnostico() : "");
            txtConduta.setText(c.getPrescricao() != null ? c.getPrescricao() : "");
            txtAnamnese.setText("Queixa: " + c.getMotivo());
            txtExameFisico.setText("Paciente responsivo e hidratado.");
        }
    }

    private void salvarDadosClinicos(boolean finalizar) {
        if (consultaAtiva == null) {
            JOptionPane.showMessageDialog(this, "Nenhum atendimento carregado!");
            return;
        }

        consultaAtiva.setDiagnostico(txtDiagnostico.getText());
        consultaAtiva.setPrescricao(txtConduta.getText());

        if (finalizar) {
            consultaAtiva.setStatus("CONCLUIDA");
            lblStatus.setText("CONCLUIDA");
            lblStatus.setBackground(new Color(187, 247, 208));
        }

        try {
            consultaDAO.atualizarProntuario(consultaAtiva);
            JOptionPane.showMessageDialog(this, finalizar ? "Atendimento finalizado no MySQL!" : "Rascunho atualizado no MySQL!");
            mainFrame.atualizarListasGlobais();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar prontuário: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
