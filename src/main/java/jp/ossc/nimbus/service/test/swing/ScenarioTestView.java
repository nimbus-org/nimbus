package jp.ossc.nimbus.service.test.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import jp.ossc.nimbus.service.test.TestCase;
import jp.ossc.nimbus.service.test.TestCase.Status;
import jp.ossc.nimbus.service.test.TestController;
import jp.ossc.nimbus.service.test.TestScenario;
import jp.ossc.nimbus.service.test.TestScenarioGroup;

public class ScenarioTestView extends JFrame implements ActionListener, ComponentListener{

    /**
     * �E�B���h�E�̍ŏ��T�C�Y
     */
    private Dimension MINIMUM_SIZE = new Dimension(1150, 700);



    // �㕔�ɕ\�����郆�[�UID
    private String userId = null;

    // �R���g���[���X�V�{�^��
    private JButton updateButton = null;

    // �R���g���[�����Z�b�g�{�^��
    private JButton resetButton = null;

    // �V�i���I�O���[�v�R���{�{�b�N�X
    private JComboBox scenarioGroupCombobox = null;

    // �V�i���I�O���[�v�J�n�{�^��
    private JButton scenarioGroupStartButton = null;

    // �V�i���I�O���[�v�I���{�^��
    private JButton scenarioGroupEndButton = null;

    // ��ԃ��x��
    private JLabel statusLabel = null;

    // ��ԃ��x��
    private JLabel statusLabel2 = null;

    // �V�i���I�R���{�{�b�N�X
    private JComboBox scenarioCombobox = null;

    // �V�i���I�J�n�{�^��
    private JButton scenarioStartButton = null;

    // �V�i���I�I���{�^��
    private JButton scenarioEndButton = null;

    // �V�i���I�L�����Z���{�^��
    private JButton scenarioCancelButton = null;

    // �V�i���I�_�E�����[�h�{�^��
    private JButton scenarioDownloadButton = null;

    // �e�X�g�P�[�X�p�l��
    private TestCaseListPanel testCasePanel = null;
    private JScrollPane testCaseScrollPanel = null;

    // �e�X�g�R���g���[���[
    private TestController testController = null;

    // ��xDL�����f�B���N�g��
    private File cashDlDir = null;



    // �V�i���I�O���[�v�̏�Ԃ�\������{�^��
    private TestErrorStatusDispButton scenarioGroupStatusButton = null;

    // �V�i���I�̏�Ԃ�\������{�^��
    private TestErrorStatusDispButton scenarioStatusButton = null;





    public ScenarioTestView(TestController testController, String userId) throws Exception {
        this.testController = testController;
        this.userId = userId;
        this.initialize();
    }

    private void initialize() throws Exception {

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(100, 100, MINIMUM_SIZE.width, MINIMUM_SIZE.height);

        Font font = new Font("�l�r �S�V�b�N", Font.BOLD, 16);

        JPanel p = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        p.setLayout(layout);


        this.getContentPane().add(p, BorderLayout.CENTER);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.insets = new Insets(5, 5, 5, 5);

        // �u���[�UID�v�\�����x��
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        JLabel label1 = new JLabel("���[�UID �F " + userId);
        label1.setFont(font);
        label1.setHorizontalTextPosition(JLabel.LEFT);
        label1.setVerticalTextPosition(JLabel.TOP);
        layout.setConstraints(label1, constraints);
        p.add(label1);

        // �u�X�V�v�{�^��
        constraints.gridx = 5;
        constraints.gridy = 0;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        this.updateButton = new JButton("�X�e�[�^�X�X�V");
        this.updateButton.setFont(font);
        this.updateButton.addActionListener(this);
        this.updateButton.setSize(150, 25);
        layout.setConstraints(this.updateButton, constraints);
        p.add(this.updateButton);

        // �u���Z�b�g�v�{�^��
        constraints.gridx = 6;
        constraints.gridy = 0;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        this.resetButton = new JButton("���\�[�X�ŐV��");
        this.resetButton.setFont(font);
        this.resetButton.addActionListener(this);
        this.resetButton.setSize(150, 25);
        layout.setConstraints(this.resetButton, constraints);
        p.add(this.resetButton);

        // ��ԃ��x��
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(0, 5, 5, 5);
        this.statusLabel = new JLabel("���s���");
        this.statusLabel.setFont(font);
        layout.setConstraints(this.statusLabel, constraints);
        p.add(this.statusLabel);

        // ��ԃ��x��
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 5;
        constraints.gridheight = 1;
        constraints.insets = new Insets(0, 5, 5, 5);
        this.statusLabel2 = new JLabel();
        this.statusLabel2.setFont(font);
        layout.setConstraints(this.statusLabel2, constraints);
        p.add(this.statusLabel2);


        // �u�V�i���I�O���[�v�v�\�����x��
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        JLabel label2 = new JLabel("�V�i���I�O���[�v");
        label2.setFont(font);
        label2.setHorizontalTextPosition(JLabel.LEFT);
        label2.setVerticalTextPosition(JLabel.TOP);
        layout.setConstraints(label2, constraints);
        p.add(label2);

        // �u�V�i���I�O���[�v�v�R���{�{�b�N�X
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        this.scenarioGroupCombobox = new JComboBox();
        this.scenarioGroupCombobox.setFont(font);
        this.scenarioGroupCombobox.addActionListener(this);
        layout.setConstraints(this.scenarioGroupCombobox, constraints);
        p.add(this.scenarioGroupCombobox);

        // �u�V�i���I�O���[�v�v��ԃ��x��
        constraints.gridx = 2;
        constraints.gridy = 2;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(0, 5, 5, 5);
        this.scenarioGroupStatusButton = new TestErrorStatusDispButton(this);
        layout.setConstraints(this.scenarioGroupStatusButton, constraints);
        p.add(this.scenarioGroupStatusButton);

        // �u�V�i���I�O���[�v�v�J�n�{�^��
        constraints.gridx = 3;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        this.scenarioGroupStartButton = new JButton("�J�n");
        this.scenarioGroupStartButton.setFont(font);
        this.scenarioGroupStartButton.addActionListener(this);
        this.scenarioGroupStartButton.setSize(150, 25);
        layout.setConstraints(this.scenarioGroupStartButton, constraints);
        p.add(this.scenarioGroupStartButton);

        // �u�V�i���I�O���[�v�v�J�n�{�^��
        constraints.gridx = 4;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        this.scenarioGroupEndButton = new JButton("�I��");
        this.scenarioGroupEndButton.setFont(font);
        this.scenarioGroupEndButton.addActionListener(this);
        this.scenarioGroupEndButton.setSize(150, 25);
        layout.setConstraints(this.scenarioGroupEndButton, constraints);
        p.add(this.scenarioGroupEndButton);

        // �u�V�i���I�v�\�����x��
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        JLabel label3 = new JLabel("�V�i���I");
        label3.setFont(font);
        label3.setHorizontalTextPosition(JLabel.LEFT);
        label3.setVerticalTextPosition(JLabel.TOP);
        layout.setConstraints(label3, constraints);
        p.add(label3);

        // �u�V�i���I�v�R���{�{�b�N�X
        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        this.scenarioCombobox = new JComboBox();
        this.scenarioCombobox.setFont(font);
        this.scenarioCombobox.addActionListener(this);
        layout.setConstraints(this.scenarioCombobox, constraints);
        p.add(this.scenarioCombobox);

        // �u�V�i���I�v��ԃ��x��
        constraints.gridx = 2;
        constraints.gridy = 3;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(0, 5, 5, 5);
        this.scenarioStatusButton = new TestErrorStatusDispButton(this);
        layout.setConstraints(this.scenarioStatusButton, constraints);
        p.add(this.scenarioStatusButton);

        // �u�V�i���I�v�J�n�{�^��
        constraints.gridx = 3;
        constraints.gridy = 3;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        this.scenarioStartButton = new JButton("�J�n");
        this.scenarioStartButton.setFont(font);
        this.scenarioStartButton.addActionListener(this);
        layout.setConstraints(this.scenarioStartButton, constraints);
        p.add(this.scenarioStartButton);

        // �u�V�i���I�v�I���{�^��
        constraints.gridx = 4;
        constraints.gridy = 3;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        this.scenarioEndButton = new JButton("�I��");
        this.scenarioEndButton.setFont(font);
        this.scenarioEndButton.addActionListener(this);
        layout.setConstraints(this.scenarioEndButton, constraints);
        p.add(this.scenarioEndButton);

        // �u�V�i���I�v�L�����Z���{�^��
        constraints.gridx = 5;
        constraints.gridy = 3;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        this.scenarioCancelButton = new JButton("��ݾ�");
        this.scenarioCancelButton.setFont(font);
        this.scenarioCancelButton.addActionListener(this);
        layout.setConstraints(this.scenarioCancelButton, constraints);
        p.add(this.scenarioCancelButton);

        // �u�V�i���I�v�_�E�����[�h�{�^��
        constraints.gridx = 6;
        constraints.gridy = 3;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        this.scenarioDownloadButton = new JButton("����DL");
        this.scenarioDownloadButton.setFont(font);
        this.scenarioDownloadButton.addActionListener(this);
        layout.setConstraints(this.scenarioDownloadButton, constraints);
        p.add(this.scenarioDownloadButton);

        // �u�e�X�g�P�[�X�v�\�����x��
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        JLabel label4 = new JLabel("�e�X�g�P�[�X");
        label4.setFont(font);
        label4.setHorizontalTextPosition(JLabel.LEFT);
        label4.setVerticalTextPosition(JLabel.TOP);
        label4.setHorizontalAlignment(JLabel.LEFT);
        label4.setVerticalAlignment(JLabel.TOP);
        layout.setConstraints(label4, constraints);
        p.add(label4);

        // �u�e�X�g�P�[�X�v�̃e�X�g�P�[�X���{�p�l��
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 5;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridwidth = 6;
        constraints.gridheight = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 5, 5);

        this.testCasePanel = new TestCaseListPanel(this);
        this.testCasePanel.setTestController(this.testController);

        JScrollPane scrollpane = new JScrollPane(this.testCasePanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        layout.setConstraints(scrollpane, constraints);
        p.add(scrollpane);

        testCaseScrollPanel = scrollpane;

        setupStatusLabel();

        updatehState();

        // �T�C�Y�ύX�C�x���g
        this.addComponentListener(this);
    }

    // Contoroller����ŐV�̏����擾����ʂɕ\������
    private void updatehState() throws Exception{
        // ���s���̃V�i���I�O���[�v������ꍇ�擾���Ă���
        TestScenarioGroup currentScenarioGroup = this.testController.getCurrentScenarioGroup();

        // �e�X�g�V�i���I�O���[�v�̃R���{�{�b�N�X��ݒ�
        this.setupScenarioGroupCombobox(null);

        // �R���{�{�b�N�X��ҏW�\�ɐݒ�
        this.scenarioGroupCombobox.setEditable(true);
        this.scenarioCombobox.setEditable(true);

        // ���s���̃V�i���I�O���[�v������ꍇ�̂�
        if(currentScenarioGroup != null){
            this.scenarioGroupCombobox.setSelectedItem(currentScenarioGroup.getScenarioGroupId());

            this.scenarioCombobox.setEnabled(true);
            this.scenarioStartButton.setEnabled(true);
            this.scenarioEndButton.setEnabled(false);
            this.scenarioCancelButton.setEnabled(false);
            this.scenarioDownloadButton.setEnabled(false);

            // �V�i���I�R���{�{�b�N�X�̐ݒ�
            this.setupScenarioCombobox(null);
        }

        // �V�i���I�n�R���|�[�l���g�̐ݒ�i�������j
        this.setupScenarioComponents();
    }

    // �{�^���������̃C�x���g
    public void actionPerformed(ActionEvent e) {

        try {
            if (e.getSource() == this.scenarioGroupStartButton) {
                // �V�i���I�O���[�v�J�n�{�^��
                this.scenarioGroupStartAction();

            } else if (e.getSource() == this.scenarioGroupEndButton) {

                // �V�i���I�O���[�v�I���{�^��
                this.scenarioGroupEndAction();

            } else if (e.getSource() == this.scenarioStartButton) {

                try{
                    // �V�i���I�J�n�{�^��
                    this.scenarioStartAction();
                }catch(Exception eStart){
                    // �V�i���I�L�����Z���{�^��
                    try{
                        this.scenarioCancelAction();
                    }catch(Exception e2){
                        e2.printStackTrace();
                    }
                    throw eStart;
                }

            } else if (e.getSource() == this.scenarioEndButton) {

                try{
                    // �V�i���I�I���{�^��
                    this.scenarioEndAction();
                }catch(Exception eStart){
                    // �V�i���I�L�����Z���{�^��
                    try{
                        this.scenarioCancelAction();
                    }catch(Exception e2){
                        e2.printStackTrace();
                    }
                    throw eStart;
                }

            } else if (e.getSource() == this.scenarioCancelButton) {
                // �V�i���I�L�����Z���{�^��
                this.scenarioCancelAction();

            } else if (e.getSource() == this.scenarioDownloadButton) {
                // �V�i���I�_�E�����[�h�{�^��
                this.scenarioDownloadAction();

            } else if (e.getSource() == this.scenarioGroupCombobox) {
                // �V�i���I�O���[�v �I���R���{�{�b�N�X
                if("comboBoxEdited".equals(e.getActionCommand())){
                    // �R���{�{�b�N�X�̕����񂪕ҏW���ꂽ�ꍇ
                    this.scenarioGroupComboboxAction();
                }else if("comboBoxChanged".equals(e.getActionCommand())){
                    // �R���{�{�b�N�X�̑I��l���ύX���ꂽ�ꍇ

                }
            } else if (e.getSource() == this.scenarioCombobox) {
                // �V�i���I �I���R���{�{�b�N�X
                if("comboBoxEdited".equals(e.getActionCommand())){
                    // �R���{�{�b�N�X�̕����񂪕ҏW���ꂽ�ꍇ
                    this.scenarioComboboxEditedAction();
                }else if("comboBoxChanged".equals(e.getActionCommand())){
                    // �R���{�{�b�N�X�̑I��l���ύX���ꂽ�ꍇ
                    this.scenarioComboboxChangeAction();
                }
            } else if (e.getSource() == this.resetButton) {
                // �R���g���[�����Z�b�g
                this.testController.reset();
            } else if (e.getSource() == this.updateButton) {
                // �X�V
                this.setupStatusLabel();
                this.updatehState();
            }

        } catch (Exception e1) {
            JDialog dialog = new StatusDialogView(this, "��O", e1);
            dialog.setModal(true);
            dialog.setVisible(true);
        }
    }

    /**
     * �V�i���I�O���[�v�J�n�{�^���̉����A�N�V����
     * @throws Exception
     */
    private void scenarioGroupStartAction() throws Exception {

        String selectScenarioGroupId = this.scenarioGroupCombobox.getSelectedItem().toString();
        TestScenarioGroup obj = testController.getCurrentScenarioGroup();

        if(obj != null && obj.getStatus() != null && obj.getStatus().getUserId() != null){

            int result = JOptionPane.showConfirmDialog(this, "�V�i���I�O���[�v�͊��ɊJ�n����Ă��܂����A���Z�b�g���čēx�J�n���܂����H", "�m�F", JOptionPane.OK_CANCEL_OPTION);
            if(JOptionPane.OK_OPTION != result){
                return;
            }
        }

        // �V�i���I�O���[�v�̊J�n
        this.testController.startScenarioGroup(this.userId, selectScenarioGroupId);

        // �V�i���I�̌����A�V�i���I�ꗗ���R���{�{�b�N�X�ɐݒ�
        this.scenarioCombobox.setEnabled(true);
        // �V�i���I�R���{�{�b�N�X��ݒ�
        this.setupScenarioCombobox(null);

        // �V�i���I�n�R���|�[�l���g�̐ݒ�i�������j
        this.setupScenarioComponents();

        // �e�X�g�P�[�X�\���p�l���̏�����
        this.testCasePanel.initialize();
    }

    /**
     * �V�i���I�O���[�v�I���{�^���̉����A�N�V����
     * @throws Exception
     */
    private void scenarioGroupEndAction() throws Exception {

        // �V�i���I�O���[�v�̏I��
        this.testController.endScenarioGroup();

        // �R���{�{�b�N�X��񊈐���
        this.scenarioCombobox.setEnabled(false);
        // �V�i���I�R���{�{�b�N�X��ݒ�
        this.setupScenarioCombobox(null);

        // �V�i���I�n�R���|�[�l���g�̐ݒ�i�������j
        this.setupScenarioComponents();
    }

    /**
     * �V�i���I�J�n�{�^���̉����A�N�V����
     * @throws Exception
     */
    private void scenarioStartAction() throws Exception {

        String selectScenarioGroupId = this.scenarioGroupCombobox.getSelectedItem().toString();
        String selectScenarioId = this.scenarioCombobox.getSelectedItem().toString();

        // �V�i���I�̊J�n
        this.testController.startScenario(this.userId, selectScenarioId);

        // �V�i���I�̌����A�V�i���I�ꗗ���R���{�{�b�N�X�ɐݒ�
        TestCase[] testCaseArray = this.testController.getTestCases(selectScenarioGroupId, selectScenarioId);
        List testCaseList = new ArrayList();
        for (int i = 0; i < testCaseArray.length; i++){
            testCaseList.add(testCaseArray[i]);
        }
        // �R���|�[�l���g�A�A�N�V�������X�i�[�̐ݒ�
        this.testCasePanel.initialize();
        this.testCasePanel.setScenarioGroupId(selectScenarioGroupId);
        this.testCasePanel.setScenarioId(selectScenarioId);
        this.testCasePanel.setTestCaseList(testCaseList);
        this.testCasePanel.addTestCaseControlListener(new TestCaseControlListenerImpl());
        this.testCasePanel.setUserId(userId);

        // �V�i���I�n�R���|�[�l���g�̐ݒ�i�������j
        this.setupScenarioComponents();
    }

    /**
     * �V�i���I�I���{�^���̉����A�N�V����
     * @throws Exception
     */
    private void scenarioEndAction() throws Exception {
        // �V�i���I�̏I��
        String selectScenarioId = this.scenarioCombobox.getSelectedItem().toString();
        this.testController.endScenario(selectScenarioId);
        this.testCasePanel.initialize();

        // �V�i���I�n�R���|�[�l���g�̐ݒ�
        this.setupScenarioComponents();
    }

    /**
     * �V�i���I�L�����Z���{�^���̉����A�N�V����
     * @throws Exception
     */
    private void scenarioCancelAction() throws Exception {
        // �V�i���I�̃L�����Z��
        String selectScenarioId = this.scenarioCombobox.getSelectedItem().toString();
        this.testController.cancelScenario(selectScenarioId);
        this.testCasePanel.initialize();

        // �V�i���I�n�R���|�[�l���g�̐ݒ�
        this.setupScenarioComponents();
    }

    /**
     * �V�i���I�_�E�����[�h�{�^���̉����A�N�V����
     * @throws Exception
     */
    private void scenarioDownloadAction() throws Exception {
        // �V�i���I�̌��ʃ_�E�����[�h
        File dlDir = showDownloadFileSaveDialog(this.cashDlDir);
        if(dlDir != null){
            String scenarioGroup = testController.getCurrentScenarioGroup().getScenarioGroupId();
            String selectScenarioId = this.scenarioCombobox.getSelectedItem().toString();
            testController.downloadScenarioResult(dlDir, scenarioGroup, selectScenarioId, TestController.RESPONSE_FILE_TYPE_ZIP);

            JOptionPane.showMessageDialog(this, "�f�B���N�g���u" + dlDir + "�v��\r\n����Ƀ_�E�����[�h���������܂����B");
        }
        this.cashDlDir = dlDir;
        this.testCasePanel.initialize();

        // �V�i���I�n�R���|�[�l���g�̐ݒ�
        this.setupScenarioComponents();
    }

    /**
     * �_�E�����[�h��f�B���N�g���̑I��
     * @return
     */
    private File showDownloadFileSaveDialog(File cashDlDir){
        JFileChooser filechooser = new JFileChooser(cashDlDir);
        filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int selected = filechooser.showSaveDialog(this);
        if (selected == JFileChooser.APPROVE_OPTION){
            return filechooser.getSelectedFile();
        }else{
            return null;
        }
    }

    /**
     * �V�i���I�O���[�v�R���{�{�b�N�X�̒l�ύX���̃A�N�V����
     * @throws Exception
     */
    private void scenarioGroupComboboxAction() throws Exception {
        Object selectObject = this.scenarioGroupCombobox.getSelectedItem();
        if(selectObject != null){
            String tmpEditText = selectObject.toString();
            this.setupScenarioGroupCombobox(tmpEditText);
        }
    }

    /**
     * �V�i���I�R���{�{�b�N�X�̒l�ҏW���̃A�N�V����
     * @throws Exception
     */
    private void scenarioComboboxEditedAction() throws Exception {
        Object selectObject = this.scenarioCombobox.getSelectedItem();
        if(selectObject != null){
            String tmpEditText = selectObject.toString();
            this.setupScenarioCombobox(tmpEditText);
        }
        this.setupScenarioComponents();
    }

    /**
     * �V�i���I�R���{�{�b�N�X�̒l�ύX���̃A�N�V����
     * @throws Exception
     */
    private void scenarioComboboxChangeAction() throws Exception {
        this.setupScenarioComponents();
    }

    /**
     * �V�i���I�O���[�v�̃R���{�{�b�N�X��ݒ�
     *
     * @param keyword ���ꂪ�w�肳��Ă���ꍇ�́A���̕������܂ނ��̂̂݃R���{�{�b�N�X�ɐݒ�
     * @throws Exception
     */
    private void setupScenarioGroupCombobox(String keyword) throws Exception{

        this.scenarioGroupCombobox.removeAllItems();
        // �e�X�g�V�i���I�O���[�v�̃R���{�{�b�N�X��ݒ�
        TestScenarioGroup[] testScenarioGroupArray = this.testController.getScenarioGroups();
        if(testScenarioGroupArray != null){
            for (int i = 0; i < testScenarioGroupArray.length; i++) {
                String scenarioGroupId = testScenarioGroupArray[i].getScenarioGroupId();
                if(keyword == null || keyword.length() == 0 || scenarioGroupId.indexOf(keyword) >= 0){
                    this.scenarioGroupCombobox.addItem(scenarioGroupId);
                }
            }
        }
    }

    private void setupScenarioCombobox(String keyword) throws Exception{

        this.scenarioCombobox.removeAllItems();
        TestScenarioGroup currentScenarioGroup = this.testController.getCurrentScenarioGroup();

        if(currentScenarioGroup == null){
            this.scenarioCombobox.setEnabled(false);
            return;
        }
        this.scenarioCombobox.setEnabled(true);

        // �e�X�g�V�i���I�̃R���{�{�b�N�X��ݒ�
        TestScenario[] testScenarioArray = this.testController.getScenarios(currentScenarioGroup.getScenarioGroupId());
        if(testScenarioArray != null){
            for (int i = 0; i < testScenarioArray.length; i++) {
                String scenarioId = testScenarioArray[i].getScenarioId();
                if(keyword == null || keyword.length() == 0 || scenarioId.indexOf(keyword) >= 0){
                    this.scenarioCombobox.addItem(scenarioId);
                }

                // �J�n��Ԃ̏ꍇ
                if(testScenarioArray[i] != null &&
                        testScenarioArray[i].getStatus() != null &&
                        testScenarioArray[i].getStatus().getState() == Status.STARTED){

                    String selectScenarioGroupId = currentScenarioGroup.getScenarioGroupId();
                    String selectScenarioId = testScenarioArray[i].getScenarioId();

                    TestCase[] testCaseArray = this.testController.getTestCases(selectScenarioGroupId, selectScenarioId);
                    List testCaseList = new ArrayList();
                    for (int t = 0; t < testCaseArray.length; t++){
                        testCaseList.add(testCaseArray[t]);
                    }

                    this.testCasePanel.initialize();
                    this.testCasePanel.setScenarioGroupId(selectScenarioGroupId);
                    this.testCasePanel.setScenarioId(selectScenarioId);
                    this.testCasePanel.setTestCaseList(testCaseList);
                    this.testCasePanel.addTestCaseControlListener(new TestCaseControlListenerImpl());
                    this.testCasePanel.setUserId(userId);
                }
            }
        }
    }

    /**
     * ���s���̃V�i���I�O���[�v�A�V�i���I�A�e�X�g�P�[�X�����x���ɐݒ肷��B
     * @throws Exception
     */
    private void setupStatusLabel() throws Exception {

        TestScenarioGroup group = testController.getCurrentScenarioGroup();
        TestScenario scenario = testController.getCurrentScenario();
        TestCase testcase = testController.getCurrentTestCase();
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
        if(group != null){
            sb.append("ScenarioGroup=" + group.getScenarioGroupId());
            if(group.getStatus() != null){
                sb.append("(" + group.getStatus().getUserId());
                if(group.getStatus().getStartTime() != null){
                    sb.append(" " + sdf.format(group.getStatus().getStartTime()));
                }
                sb.append(")");
            }
            if(scenario != null){
                sb.append(", Scenario=" + scenario.getScenarioId());
                if(scenario.getStatus() != null){
                    sb.append("(" + scenario.getStatus().getUserId());
                    if(scenario.getStatus().getStartTime() != null){
                        sb.append(" " + sdf.format(scenario.getStatus().getStartTime()));
                    }
                    sb.append(")");
                }
                if(testcase != null){
                    sb.append(", Testcase=" + testcase.getTestCaseId());
                    if(testcase.getStatus() != null){
                        sb.append("(" + testcase.getStatus().getUserId());
                        if(testcase.getStatus().getStartTime() != null){
                            sb.append(" " + sdf.format(testcase.getStatus().getStartTime()));
                        }
                        sb.append(")");
                    }
                }
            }
        } else {
            sb.append("����");
        }
        this.statusLabel2.setText(sb.toString());
        this.statusLabel2.setToolTipText(sb.toString());
    }

    /**
     * �V�i���I�n�R���|�[�l���g�̐ݒ�
     * @throws Exception
     */
    private void setupScenarioComponents() throws Exception {

        TestScenarioGroup currentScenarioGroup = testController.getCurrentScenarioGroup();
        if(currentScenarioGroup == null){
            this.scenarioGroupStartButton.setEnabled(true);
            this.scenarioGroupEndButton.setEnabled(false);
            this.scenarioCombobox.setEnabled(false);
            this.scenarioStartButton.setEnabled(false);
            this.scenarioEndButton.setEnabled(false);
            this.scenarioCancelButton.setEnabled(false);
            this.scenarioDownloadButton.setEnabled(false);
            return;
        }else{
            this.scenarioGroupStartButton.setEnabled(false);
            this.scenarioGroupEndButton.setEnabled(true);
        }

        String currentScenarioGroupId = currentScenarioGroup.getScenarioGroupId();

        if(this.scenarioCombobox.getSelectedItem() == null)
            return;

        String selectScenarioId = this.scenarioCombobox.getSelectedItem().toString();

        TestScenario selectSenario = null;

        if(currentScenarioGroupId != null && selectScenarioId != null){
            selectSenario = testController.getScenario(currentScenarioGroupId, selectScenarioId);
        }
        if(selectSenario == null){
            return;
        }
        final TestScenario.Status status = selectSenario.getStatus();

        this.scenarioStatusButton.change(status);
        //--

        if (status != null && status.getState() == TestScenario.Status.STARTED) {
            this.scenarioCombobox.setEnabled(true);
            this.scenarioStartButton.setEnabled(false);
            this.scenarioEndButton.setEnabled(true);
            this.scenarioCancelButton.setEnabled(true);
            this.scenarioDownloadButton.setEnabled(false);

        } else if (status != null && status.getState() == TestScenario.Status.END) {
            this.scenarioCombobox.setEnabled(true);
            this.scenarioStartButton.setEnabled(true);
            this.scenarioEndButton.setEnabled(false);
            this.scenarioCancelButton.setEnabled(false);
            this.scenarioDownloadButton.setEnabled(true);

        } else if (status == null ||
                (status != null &&
                    (status.getState() == TestScenario.Status.INITIAL ||
                     status.getState() == TestScenario.Status.CANCELED ||
                     status.getState() == TestScenario.Status.ERROR))) {
            this.scenarioCombobox.setEnabled(true);
            this.scenarioStartButton.setEnabled(true);
            this.scenarioEndButton.setEnabled(false);
            this.scenarioCancelButton.setEnabled(false);
            this.scenarioDownloadButton.setEnabled(false);

        } else {

            if(this.scenarioCombobox.getItemCount() == 0){
                this.scenarioCombobox.setEnabled(false);
            }else{
                this.scenarioCombobox.setEnabled(true);
            }
            this.scenarioStartButton.setEnabled(false);
            this.scenarioEndButton.setEnabled(false);
            this.scenarioCancelButton.setEnabled(false);
            this.scenarioDownloadButton.setEnabled(false);
        }
    }


    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentResized(ComponentEvent e) {

        if(MINIMUM_SIZE.width > this.getWidth() || MINIMUM_SIZE.height > this.getHeight()){
            this.setSize(MINIMUM_SIZE);
            return;
        }

        if(this.testCasePanel.getWidth() > this.testCaseScrollPanel.getViewport().getWidth()){
            int width = this.testCaseScrollPanel.getViewport().getWidth();
            int height = this.testCasePanel.getHeight();
            this.testCasePanel.setSize(width, height);
        }

        try {
            this.testCasePanel.resetup();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public void componentShown(ComponentEvent e) {
    }

    /**
     * �e�X�g�P�[�X���X�^�[�g�A�G���h�Ȃǂ��ꂽ�ۂɒʒm���Ă���郊�X�i�[�����N���X
     * @author j-higuchi
     */
    private class TestCaseControlListenerImpl implements TestCaseControlListener{

        public void startTestCase(TestCase testcase) throws Exception {
            setupScenarioComponents();
        }

        public void endTestCase(TestCase testcase) throws Exception {
            setupScenarioComponents();
        }
    }
}
