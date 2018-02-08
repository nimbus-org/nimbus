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
     * ウィンドウの最小サイズ
     */
    private Dimension MINIMUM_SIZE = new Dimension(1150, 700);



    // 上部に表示するユーザID
    private String userId = null;

    // コントローラ更新ボタン
    private JButton updateButton = null;

    // コントローラリセットボタン
    private JButton resetButton = null;

    // シナリオグループコンボボックス
    private JComboBox scenarioGroupCombobox = null;

    // シナリオグループ開始ボタン
    private JButton scenarioGroupStartButton = null;

    // シナリオグループ終了ボタン
    private JButton scenarioGroupEndButton = null;

    // 状態ラベル
    private JLabel statusLabel = null;

    // 状態ラベル
    private JLabel statusLabel2 = null;

    // シナリオコンボボックス
    private JComboBox scenarioCombobox = null;

    // シナリオ開始ボタン
    private JButton scenarioStartButton = null;

    // シナリオ終了ボタン
    private JButton scenarioEndButton = null;

    // シナリオキャンセルボタン
    private JButton scenarioCancelButton = null;

    // シナリオダウンロードボタン
    private JButton scenarioDownloadButton = null;

    // テストケースパネル
    private TestCaseListPanel testCasePanel = null;
    private JScrollPane testCaseScrollPanel = null;

    // テストコントローラー
    private TestController testController = null;

    // 一度DLしたディレクトリ
    private File cashDlDir = null;



    // シナリオグループの状態を表示するボタン
    private TestErrorStatusDispButton scenarioGroupStatusButton = null;

    // シナリオの状態を表示するボタン
    private TestErrorStatusDispButton scenarioStatusButton = null;





    public ScenarioTestView(TestController testController, String userId) throws Exception {
        this.testController = testController;
        this.userId = userId;
        this.initialize();
    }

    private void initialize() throws Exception {

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(100, 100, MINIMUM_SIZE.width, MINIMUM_SIZE.height);

        Font font = new Font("ＭＳ ゴシック", Font.BOLD, 16);

        JPanel p = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        p.setLayout(layout);


        this.getContentPane().add(p, BorderLayout.CENTER);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.insets = new Insets(5, 5, 5, 5);

        // 「ユーザID」表示ラベル
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        JLabel label1 = new JLabel("ユーザID ： " + userId);
        label1.setFont(font);
        label1.setHorizontalTextPosition(JLabel.LEFT);
        label1.setVerticalTextPosition(JLabel.TOP);
        layout.setConstraints(label1, constraints);
        p.add(label1);

        // 「更新」ボタン
        constraints.gridx = 5;
        constraints.gridy = 0;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        this.updateButton = new JButton("ステータス更新");
        this.updateButton.setFont(font);
        this.updateButton.addActionListener(this);
        this.updateButton.setSize(150, 25);
        layout.setConstraints(this.updateButton, constraints);
        p.add(this.updateButton);

        // 「リセット」ボタン
        constraints.gridx = 6;
        constraints.gridy = 0;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        this.resetButton = new JButton("リソース最新化");
        this.resetButton.setFont(font);
        this.resetButton.addActionListener(this);
        this.resetButton.setSize(150, 25);
        layout.setConstraints(this.resetButton, constraints);
        p.add(this.resetButton);

        // 状態ラベル
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(0, 5, 5, 5);
        this.statusLabel = new JLabel("実行状態");
        this.statusLabel.setFont(font);
        layout.setConstraints(this.statusLabel, constraints);
        p.add(this.statusLabel);

        // 状態ラベル
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


        // 「シナリオグループ」表示ラベル
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        JLabel label2 = new JLabel("シナリオグループ");
        label2.setFont(font);
        label2.setHorizontalTextPosition(JLabel.LEFT);
        label2.setVerticalTextPosition(JLabel.TOP);
        layout.setConstraints(label2, constraints);
        p.add(label2);

        // 「シナリオグループ」コンボボックス
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

        // 「シナリオグループ」状態ラベル
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

        // 「シナリオグループ」開始ボタン
        constraints.gridx = 3;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        this.scenarioGroupStartButton = new JButton("開始");
        this.scenarioGroupStartButton.setFont(font);
        this.scenarioGroupStartButton.addActionListener(this);
        this.scenarioGroupStartButton.setSize(150, 25);
        layout.setConstraints(this.scenarioGroupStartButton, constraints);
        p.add(this.scenarioGroupStartButton);

        // 「シナリオグループ」開始ボタン
        constraints.gridx = 4;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        this.scenarioGroupEndButton = new JButton("終了");
        this.scenarioGroupEndButton.setFont(font);
        this.scenarioGroupEndButton.addActionListener(this);
        this.scenarioGroupEndButton.setSize(150, 25);
        layout.setConstraints(this.scenarioGroupEndButton, constraints);
        p.add(this.scenarioGroupEndButton);

        // 「シナリオ」表示ラベル
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        JLabel label3 = new JLabel("シナリオ");
        label3.setFont(font);
        label3.setHorizontalTextPosition(JLabel.LEFT);
        label3.setVerticalTextPosition(JLabel.TOP);
        layout.setConstraints(label3, constraints);
        p.add(label3);

        // 「シナリオ」コンボボックス
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

        // 「シナリオ」状態ラベル
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

        // 「シナリオ」開始ボタン
        constraints.gridx = 3;
        constraints.gridy = 3;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        this.scenarioStartButton = new JButton("開始");
        this.scenarioStartButton.setFont(font);
        this.scenarioStartButton.addActionListener(this);
        layout.setConstraints(this.scenarioStartButton, constraints);
        p.add(this.scenarioStartButton);

        // 「シナリオ」終了ボタン
        constraints.gridx = 4;
        constraints.gridy = 3;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        this.scenarioEndButton = new JButton("終了");
        this.scenarioEndButton.setFont(font);
        this.scenarioEndButton.addActionListener(this);
        layout.setConstraints(this.scenarioEndButton, constraints);
        p.add(this.scenarioEndButton);

        // 「シナリオ」キャンセルボタン
        constraints.gridx = 5;
        constraints.gridy = 3;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        this.scenarioCancelButton = new JButton("ｷｬﾝｾﾙ");
        this.scenarioCancelButton.setFont(font);
        this.scenarioCancelButton.addActionListener(this);
        layout.setConstraints(this.scenarioCancelButton, constraints);
        p.add(this.scenarioCancelButton);

        // 「シナリオ」ダウンロードボタン
        constraints.gridx = 6;
        constraints.gridy = 3;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        this.scenarioDownloadButton = new JButton("結果DL");
        this.scenarioDownloadButton.setFont(font);
        this.scenarioDownloadButton.addActionListener(this);
        layout.setConstraints(this.scenarioDownloadButton, constraints);
        p.add(this.scenarioDownloadButton);

        // 「テストケース」表示ラベル
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        JLabel label4 = new JLabel("テストケース");
        label4.setFont(font);
        label4.setHorizontalTextPosition(JLabel.LEFT);
        label4.setVerticalTextPosition(JLabel.TOP);
        label4.setHorizontalAlignment(JLabel.LEFT);
        label4.setVerticalAlignment(JLabel.TOP);
        layout.setConstraints(label4, constraints);
        p.add(label4);

        // 「テストケース」のテストケース実施パネル
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

        // サイズ変更イベント
        this.addComponentListener(this);
    }

    // Contorollerから最新の情報を取得し画面に表示する
    private void updatehState() throws Exception{
        // 実行中のシナリオグループがある場合取得しておく
        TestScenarioGroup currentScenarioGroup = this.testController.getCurrentScenarioGroup();

        // テストシナリオグループのコンボボックスを設定
        this.setupScenarioGroupCombobox(null);

        // コンボボックスを編集可能に設定
        this.scenarioGroupCombobox.setEditable(true);
        this.scenarioCombobox.setEditable(true);

        // 実行中のシナリオグループがある場合のみ
        if(currentScenarioGroup != null){
            this.scenarioGroupCombobox.setSelectedItem(currentScenarioGroup.getScenarioGroupId());

            this.scenarioCombobox.setEnabled(true);
            this.scenarioStartButton.setEnabled(true);
            this.scenarioEndButton.setEnabled(false);
            this.scenarioCancelButton.setEnabled(false);
            this.scenarioDownloadButton.setEnabled(false);

            // シナリオコンボボックスの設定
            this.setupScenarioCombobox(null);
        }

        // シナリオ系コンポーネントの設定（初期化）
        this.setupScenarioComponents();
    }

    // ボタン押下時のイベント
    public void actionPerformed(ActionEvent e) {

        try {
            if (e.getSource() == this.scenarioGroupStartButton) {
                // シナリオグループ開始ボタン
                this.scenarioGroupStartAction();

            } else if (e.getSource() == this.scenarioGroupEndButton) {

                // シナリオグループ終了ボタン
                this.scenarioGroupEndAction();

            } else if (e.getSource() == this.scenarioStartButton) {

                try{
                    // シナリオ開始ボタン
                    this.scenarioStartAction();
                }catch(Exception eStart){
                    // シナリオキャンセルボタン
                    try{
                        this.scenarioCancelAction();
                    }catch(Exception e2){
                        e2.printStackTrace();
                    }
                    throw eStart;
                }

            } else if (e.getSource() == this.scenarioEndButton) {

                try{
                    // シナリオ終了ボタン
                    this.scenarioEndAction();
                }catch(Exception eStart){
                    // シナリオキャンセルボタン
                    try{
                        this.scenarioCancelAction();
                    }catch(Exception e2){
                        e2.printStackTrace();
                    }
                    throw eStart;
                }

            } else if (e.getSource() == this.scenarioCancelButton) {
                // シナリオキャンセルボタン
                this.scenarioCancelAction();

            } else if (e.getSource() == this.scenarioDownloadButton) {
                // シナリオダウンロードボタン
                this.scenarioDownloadAction();

            } else if (e.getSource() == this.scenarioGroupCombobox) {
                // シナリオグループ 選択コンボボックス
                if("comboBoxEdited".equals(e.getActionCommand())){
                    // コンボボックスの文字列が編集された場合
                    this.scenarioGroupComboboxAction();
                }else if("comboBoxChanged".equals(e.getActionCommand())){
                    // コンボボックスの選択値が変更された場合

                }
            } else if (e.getSource() == this.scenarioCombobox) {
                // シナリオ 選択コンボボックス
                if("comboBoxEdited".equals(e.getActionCommand())){
                    // コンボボックスの文字列が編集された場合
                    this.scenarioComboboxEditedAction();
                }else if("comboBoxChanged".equals(e.getActionCommand())){
                    // コンボボックスの選択値が変更された場合
                    this.scenarioComboboxChangeAction();
                }
            } else if (e.getSource() == this.resetButton) {
                // コントローラリセット
                this.testController.reset();
            } else if (e.getSource() == this.updateButton) {
                // 更新
                this.setupStatusLabel();
                this.updatehState();
            }

        } catch (Exception e1) {
            JDialog dialog = new StatusDialogView(this, "例外", e1);
            dialog.setModal(true);
            dialog.setVisible(true);
        }
    }

    /**
     * シナリオグループ開始ボタンの押下アクション
     * @throws Exception
     */
    private void scenarioGroupStartAction() throws Exception {

        String selectScenarioGroupId = this.scenarioGroupCombobox.getSelectedItem().toString();
        TestScenarioGroup obj = testController.getCurrentScenarioGroup();

        if(obj != null && obj.getStatus() != null && obj.getStatus().getUserId() != null){

            int result = JOptionPane.showConfirmDialog(this, "シナリオグループは既に開始されていますが、リセットして再度開始しますか？", "確認", JOptionPane.OK_CANCEL_OPTION);
            if(JOptionPane.OK_OPTION != result){
                return;
            }
        }

        // シナリオグループの開始
        this.testController.startScenarioGroup(this.userId, selectScenarioGroupId);

        // シナリオの検索、シナリオ一覧をコンボボックスに設定
        this.scenarioCombobox.setEnabled(true);
        // シナリオコンボボックスを設定
        this.setupScenarioCombobox(null);

        // シナリオ系コンポーネントの設定（初期化）
        this.setupScenarioComponents();

        // テストケース表示パネルの初期化
        this.testCasePanel.initialize();
    }

    /**
     * シナリオグループ終了ボタンの押下アクション
     * @throws Exception
     */
    private void scenarioGroupEndAction() throws Exception {

        // シナリオグループの終了
        this.testController.endScenarioGroup();

        // コンボボックスを非活性に
        this.scenarioCombobox.setEnabled(false);
        // シナリオコンボボックスを設定
        this.setupScenarioCombobox(null);

        // シナリオ系コンポーネントの設定（初期化）
        this.setupScenarioComponents();
    }

    /**
     * シナリオ開始ボタンの押下アクション
     * @throws Exception
     */
    private void scenarioStartAction() throws Exception {

        String selectScenarioGroupId = this.scenarioGroupCombobox.getSelectedItem().toString();
        String selectScenarioId = this.scenarioCombobox.getSelectedItem().toString();

        // シナリオの開始
        this.testController.startScenario(this.userId, selectScenarioId);

        // シナリオの検索、シナリオ一覧をコンボボックスに設定
        TestCase[] testCaseArray = this.testController.getTestCases(selectScenarioGroupId, selectScenarioId);
        List testCaseList = new ArrayList();
        for (int i = 0; i < testCaseArray.length; i++){
            testCaseList.add(testCaseArray[i]);
        }
        // コンポーネント、アクションリスナーの設定
        this.testCasePanel.initialize();
        this.testCasePanel.setScenarioGroupId(selectScenarioGroupId);
        this.testCasePanel.setScenarioId(selectScenarioId);
        this.testCasePanel.setTestCaseList(testCaseList);
        this.testCasePanel.addTestCaseControlListener(new TestCaseControlListenerImpl());
        this.testCasePanel.setUserId(userId);

        // シナリオ系コンポーネントの設定（初期化）
        this.setupScenarioComponents();
    }

    /**
     * シナリオ終了ボタンの押下アクション
     * @throws Exception
     */
    private void scenarioEndAction() throws Exception {
        // シナリオの終了
        String selectScenarioId = this.scenarioCombobox.getSelectedItem().toString();
        this.testController.endScenario(selectScenarioId);
        this.testCasePanel.initialize();

        // シナリオ系コンポーネントの設定
        this.setupScenarioComponents();
    }

    /**
     * シナリオキャンセルボタンの押下アクション
     * @throws Exception
     */
    private void scenarioCancelAction() throws Exception {
        // シナリオのキャンセル
        String selectScenarioId = this.scenarioCombobox.getSelectedItem().toString();
        this.testController.cancelScenario(selectScenarioId);
        this.testCasePanel.initialize();

        // シナリオ系コンポーネントの設定
        this.setupScenarioComponents();
    }

    /**
     * シナリオダウンロードボタンの押下アクション
     * @throws Exception
     */
    private void scenarioDownloadAction() throws Exception {
        // シナリオの結果ダウンロード
        File dlDir = showDownloadFileSaveDialog(this.cashDlDir);
        if(dlDir != null){
            String scenarioGroup = testController.getCurrentScenarioGroup().getScenarioGroupId();
            String selectScenarioId = this.scenarioCombobox.getSelectedItem().toString();
            testController.downloadScenarioResult(dlDir, scenarioGroup, selectScenarioId, TestController.RESPONSE_FILE_TYPE_ZIP);

            JOptionPane.showMessageDialog(this, "ディレクトリ「" + dlDir + "」に\r\n正常にダウンロードが完了しました。");
        }
        this.cashDlDir = dlDir;
        this.testCasePanel.initialize();

        // シナリオ系コンポーネントの設定
        this.setupScenarioComponents();
    }

    /**
     * ダウンロード先ディレクトリの選択
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
     * シナリオグループコンボボックスの値変更時のアクション
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
     * シナリオコンボボックスの値編集時のアクション
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
     * シナリオコンボボックスの値変更時のアクション
     * @throws Exception
     */
    private void scenarioComboboxChangeAction() throws Exception {
        this.setupScenarioComponents();
    }

    /**
     * シナリオグループのコンボボックスを設定
     *
     * @param keyword これが指定されている場合は、この文字を含むもののみコンボボックスに設定
     * @throws Exception
     */
    private void setupScenarioGroupCombobox(String keyword) throws Exception{

        this.scenarioGroupCombobox.removeAllItems();
        // テストシナリオグループのコンボボックスを設定
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

        // テストシナリオのコンボボックスを設定
        TestScenario[] testScenarioArray = this.testController.getScenarios(currentScenarioGroup.getScenarioGroupId());
        if(testScenarioArray != null){
            for (int i = 0; i < testScenarioArray.length; i++) {
                String scenarioId = testScenarioArray[i].getScenarioId();
                if(keyword == null || keyword.length() == 0 || scenarioId.indexOf(keyword) >= 0){
                    this.scenarioCombobox.addItem(scenarioId);
                }

                // 開始状態の場合
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
     * 実行中のシナリオグループ、シナリオ、テストケースをラベルに設定する。
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
            sb.append("無し");
        }
        this.statusLabel2.setText(sb.toString());
        this.statusLabel2.setToolTipText(sb.toString());
    }

    /**
     * シナリオ系コンポーネントの設定
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
     * テストケースがスタート、エンドなどされた際に通知してくれるリスナー実装クラス
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
