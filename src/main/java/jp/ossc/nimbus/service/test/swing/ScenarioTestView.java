/*
 * This software is distributed under following license based on modified BSD
 * style license.
 * ----------------------------------------------------------------------
 *
 * Copyright 2003 The Nimbus Project. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE NIMBUS PROJECT ``AS IS'' AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 * NO EVENT SHALL THE NIMBUS PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of the Nimbus Project.
 */
package jp.ossc.nimbus.service.test.swing;

import java.awt.BorderLayout;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;

import jp.ossc.nimbus.service.test.TestCase;
import jp.ossc.nimbus.service.test.TestCase.Status;
import jp.ossc.nimbus.service.test.TestController;
import jp.ossc.nimbus.service.test.TestScenario;
import jp.ossc.nimbus.service.test.TestScenarioGroup;
import jp.ossc.nimbus.service.test.TestStatusException;

public class ScenarioTestView extends JFrame implements ActionListener, ComponentListener{

    /**
     * ウィンドウの最小サイズ
     */
    private Dimension MINIMUM_SIZE = new Dimension(1280, 720);

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
    
    // Actionステータスダイアログ表示ボタン
    private JButton statusDialogDisplayButton = null;
    
    // Actionステータス自動表示用チェックボックス
    private JCheckBox statusDialogDisplayCheckBox = null;

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

    private boolean isServerCalling = false;

    private final static long MAX_WAIT_TIME_MILLIS = 10 * 60 * 1000; 
    
    public ScenarioTestView(TestController testController, String userId) throws Exception {
        this.testController = testController;
        this.userId = userId;
        initialize();
    }

    private void initialize() throws Exception {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, MINIMUM_SIZE.width, MINIMUM_SIZE.height);

        Font font = new Font("ＭＳ ゴシック", Font.BOLD, 16);

        JPanel p = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        p.setLayout(layout);


        getContentPane().add(p, BorderLayout.CENTER);

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
        updateButton = new JButton("実行状態更新");
        updateButton.setFont(font);
        updateButton.addActionListener(this);
        updateButton.setSize(150, 25);
        layout.setConstraints(updateButton, constraints);
        p.add(updateButton);

        // 「リセット」ボタン
        constraints.gridx = 6;
        constraints.gridy = 0;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        resetButton = new JButton("リソース最新化");
        resetButton.setFont(font);
        resetButton.addActionListener(this);
        resetButton.setSize(150, 25);
        layout.setConstraints(resetButton, constraints);
        p.add(resetButton);

        // 状態ラベル
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(0, 5, 5, 5);
        statusLabel = new JLabel("実行状態");
        statusLabel.setFont(font);
        layout.setConstraints(statusLabel, constraints);
        p.add(statusLabel);

        // 状態ラベル
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 6;
        constraints.gridheight = 1;
        constraints.insets = new Insets(0, 5, 5, 5);
        statusLabel2 = new JLabel();
        statusLabel2.setFont(font);
        layout.setConstraints(statusLabel2, constraints);
        p.add(statusLabel2);


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
        scenarioGroupCombobox = new JComboBox();
        scenarioGroupCombobox.setFont(font);
        scenarioGroupCombobox.addActionListener(this);
        layout.setConstraints(scenarioGroupCombobox, constraints);
        p.add(scenarioGroupCombobox);

        // 「シナリオグループ」状態ラベル
        constraints.gridx = 2;
        constraints.gridy = 2;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(0, 5, 5, 5);
        scenarioGroupStatusButton = new TestErrorStatusDispButton(this);
        layout.setConstraints(scenarioGroupStatusButton, constraints);
        p.add(scenarioGroupStatusButton);

        // 「シナリオグループ」開始ボタン
        constraints.gridx = 3;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        scenarioGroupStartButton = new JButton("開始");
        scenarioGroupStartButton.setFont(font);
        scenarioGroupStartButton.addActionListener(this);
        scenarioGroupStartButton.setSize(150, 25);
        layout.setConstraints(scenarioGroupStartButton, constraints);
        p.add(scenarioGroupStartButton);

        // 「シナリオグループ」終了ボタン
        constraints.gridx = 4;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        scenarioGroupEndButton = new JButton("終了");
        scenarioGroupEndButton.setFont(font);
        scenarioGroupEndButton.addActionListener(this);
        scenarioGroupEndButton.setSize(150, 25);
        layout.setConstraints(scenarioGroupEndButton, constraints);
        p.add(scenarioGroupEndButton);
        
        // Actionステータス自動表示用チェックボックス
        constraints.gridx = 5;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        statusDialogDisplayCheckBox = new JCheckBox("Actionステータス表示");
        statusDialogDisplayCheckBox.setSelected(true);
        statusDialogDisplayCheckBox.setFont(font);
        layout.setConstraints(statusDialogDisplayCheckBox, constraints);
        p.add(statusDialogDisplayCheckBox);

        // Actionステータスダイアログ表示ボタン
        constraints.gridx = 6;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        statusDialogDisplayButton = new JButton("Actionステータス");
        statusDialogDisplayButton.setFont(font);
        statusDialogDisplayButton.addActionListener(this);
        statusDialogDisplayButton.setSize(150, 25);
        layout.setConstraints(statusDialogDisplayButton, constraints);
        p.add(statusDialogDisplayButton);
        
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
        scenarioCombobox = new JComboBox();
        scenarioCombobox.setFont(font);
        scenarioCombobox.addActionListener(this);
        layout.setConstraints(scenarioCombobox, constraints);
        p.add(scenarioCombobox);

        // 「シナリオ」状態ラベル
        constraints.gridx = 2;
        constraints.gridy = 3;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(0, 5, 5, 5);
        scenarioStatusButton = new TestErrorStatusDispButton(this);
        layout.setConstraints(scenarioStatusButton, constraints);
        p.add(scenarioStatusButton);

        // 「シナリオ」開始ボタン
        constraints.gridx = 3;
        constraints.gridy = 3;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        scenarioStartButton = new JButton("開始");
        scenarioStartButton.setFont(font);
        scenarioStartButton.addActionListener(this);
        layout.setConstraints(scenarioStartButton, constraints);
        p.add(scenarioStartButton);

        // 「シナリオ」終了ボタン
        constraints.gridx = 4;
        constraints.gridy = 3;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        scenarioEndButton = new JButton("終了");
        scenarioEndButton.setFont(font);
        scenarioEndButton.addActionListener(this);
        layout.setConstraints(scenarioEndButton, constraints);
        p.add(scenarioEndButton);

        // 「シナリオ」キャンセルボタン
        constraints.gridx = 5;
        constraints.gridy = 3;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        scenarioCancelButton = new JButton("ｷｬﾝｾﾙ");
        scenarioCancelButton.setFont(font);
        scenarioCancelButton.addActionListener(this);
        layout.setConstraints(scenarioCancelButton, constraints);
        p.add(scenarioCancelButton);

        // 「シナリオ」ダウンロードボタン
        constraints.gridx = 6;
        constraints.gridy = 3;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        scenarioDownloadButton = new JButton("結果DL");
        scenarioDownloadButton.setFont(font);
        scenarioDownloadButton.addActionListener(this);
        layout.setConstraints(scenarioDownloadButton, constraints);
        p.add(scenarioDownloadButton);

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

        testCasePanel = new TestCaseListPanel(this);
        testCasePanel.setTestController(testController);

        JScrollPane scrollpane = new JScrollPane(testCasePanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        layout.setConstraints(scrollpane, constraints);
        p.add(scrollpane);

        testCaseScrollPanel = scrollpane;

        setupStatusLabel();

        updateState();

        // サイズ変更イベント
        addComponentListener(this);
    }

    // Contorollerから最新の情報を取得し画面に表示する
    private void updateState() throws Exception{
        // 実行中のシナリオグループがある場合取得しておく
        TestScenarioGroup currentScenarioGroup = testController.getCurrentScenarioGroup();

        // テストシナリオグループのコンボボックスを設定
        setupScenarioGroupCombobox(null);

        // コンボボックスを編集可能に設定
        scenarioGroupCombobox.setEditable(true);
        scenarioCombobox.setEditable(true);

        // 実行中のシナリオグループがある場合のみ
        if(currentScenarioGroup != null){
            scenarioGroupCombobox.setSelectedItem(currentScenarioGroup.getScenarioGroupId());

            scenarioCombobox.setEnabled(true);
            scenarioStartButton.setEnabled(true);
            scenarioEndButton.setEnabled(false);
            scenarioCancelButton.setEnabled(false);
            scenarioDownloadButton.setEnabled(false);

            // シナリオコンボボックスの設定
            setupScenarioCombobox(null);
        }

        // シナリオ系コンポーネントの設定（初期化）
        setupScenarioComponents();
    }

    // ボタン押下時のイベント
    public void actionPerformed(ActionEvent e) {

        try {
            if (e.getSource() == scenarioGroupStartButton) {
                SwingWorker testControllerWorker = new TestControllerOperationWorker(this, scenarioGroupStartButton, TestControllerOperationWorker.MODE_SCENARIO_GROUP_START);
                testControllerWorker.execute();
                if(statusDialogDisplayCheckBox.isSelected()) {
                    SwingWorker statusWorker = new StatusCheckWorker(this, StatusCheckWorker.MODE_SCENARIO_GROUP, true);
                    statusWorker.execute();
                }
            } else if (e.getSource() == scenarioGroupEndButton) {
                SwingWorker testControllerWorker = new TestControllerOperationWorker(this, scenarioGroupEndButton, TestControllerOperationWorker.MODE_SCENARIO_GROUP_END);
                testControllerWorker.execute();
                if(statusDialogDisplayCheckBox.isSelected()) {
                    SwingWorker statusWorker = new StatusCheckWorker(this, StatusCheckWorker.MODE_SCENARIO_GROUP, true);
                    statusWorker.execute();
                }
            } else if (e.getSource() == scenarioStartButton) {
                SwingWorker testControllerWorker = new TestControllerOperationWorker(this, scenarioStartButton, TestControllerOperationWorker.MODE_SCENARIO_START);
                testControllerWorker.execute();
                if(statusDialogDisplayCheckBox.isSelected()) {
                    SwingWorker statusWorker = new StatusCheckWorker(this, StatusCheckWorker.MODE_SCENARIO, true);
                    statusWorker.execute();
                }
            } else if (e.getSource() == scenarioEndButton) {
                SwingWorker testControllerWorker = new TestControllerOperationWorker(this, scenarioEndButton, TestControllerOperationWorker.MODE_SCENARIO_END);
                testControllerWorker.execute();
                if(statusDialogDisplayCheckBox.isSelected()) {
                    SwingWorker statusWorker = new StatusCheckWorker(this, StatusCheckWorker.MODE_SCENARIO, true);
                    statusWorker.execute();
                }
            } else if (e.getSource() == scenarioCancelButton) {
                SwingWorker testControllerWorker = new TestControllerOperationWorker(this, scenarioCancelButton, TestControllerOperationWorker.MODE_SCENARIO_CANCEL);
                testControllerWorker.execute();
                if(statusDialogDisplayCheckBox.isSelected()) {
                    SwingWorker statusWorker = new StatusCheckWorker(this, StatusCheckWorker.MODE_SCENARIO, true);
                    statusWorker.execute();
                }
            } else if (e.getSource() == scenarioDownloadButton) {
                // シナリオダウンロードボタン
                scenarioDownloadAction();
            } else if (e.getSource() == scenarioGroupCombobox) {
                // シナリオグループ 選択コンボボックス
                if("comboBoxEdited".equals(e.getActionCommand())){
                    // コンボボックスの文字列が編集された場合
                    scenarioGroupComboboxAction();
                }else if("comboBoxChanged".equals(e.getActionCommand())){
                    // コンボボックスの選択値が変更された場合
                }
            } else if (e.getSource() == scenarioCombobox) {
                // シナリオ 選択コンボボックス
                if("comboBoxEdited".equals(e.getActionCommand())){
                    // コンボボックスの文字列が編集された場合
                    scenarioComboboxEditedAction();
                }else if("comboBoxChanged".equals(e.getActionCommand())){
                    // コンボボックスの選択値が変更された場合
                    scenarioComboboxChangeAction();
                }
            } else if (e.getSource() == resetButton) {
                // コントローラリセット
                testController.reset();
            } else if (e.getSource() == updateButton) {
                // 更新
                setupStatusLabel();
                updateState();
            } else if(e.getSource() == statusDialogDisplayButton) {
                if(testController.getCurrentTestCase() != null) {
                    testCasePanel.showTestCaseStatusDialog(); 
                } else if(testController.getCurrentScenario() != null) {
                    SwingWorker statusWorker = new StatusCheckWorker(this, StatusCheckWorker.MODE_SCENARIO, false);
                    statusWorker.execute();
                } else if(testController.getCurrentScenarioGroup() != null) {
                    SwingWorker statusWorker = new StatusCheckWorker(this, StatusCheckWorker.MODE_SCENARIO_GROUP, false);
                    statusWorker.execute();
                }
            }
        } catch (Exception e1) {
            JDialog dialog = new StatusDialogView(this, "例外", e1);
            dialog.setModal(true);
            dialog.setVisible(true);
        } finally {
            
        }
    }

    /**
     * シナリオグループ開始ボタンの押下アクション
     * @throws Exception
     */
    private void scenarioGroupStartAction(boolean isWait) throws Exception {

        String selectScenarioGroupId = scenarioGroupCombobox.getSelectedItem().toString();
        
        try {
            // シナリオグループの開始
            testController.startScenarioGroup(userId, selectScenarioGroupId);
            // シナリオの検索、シナリオ一覧をコンボボックスに設定
            scenarioCombobox.setEnabled(true);
            setupStatusLabel();
        }catch(TestStatusException e){
            if(isWait) {
                int result = JOptionPane.showConfirmDialog(this, e.getMessage() + "\r\n\r\n実行されているシナリオグループの終了を待ち、終了次第自動で開始しますか？" , "確認", JOptionPane.YES_NO_OPTION);
                if(JOptionPane.YES_OPTION == result){
                    long startTime = System.currentTimeMillis();
                    while(true) {
                        TestScenarioGroup group = testController.getCurrentScenarioGroup();
                        if(group == null) {
                            try {
                                scenarioGroupStartAction(false);
                            } catch(TestStatusException e2) {
                                continue;
                            }
                            return;
                        }
                        if(System.currentTimeMillis() - startTime > MAX_WAIT_TIME_MILLIS) {
                            JOptionPane.showMessageDialog(this, "実行されているシナリオグループが待ち時間[" + MAX_WAIT_TIME_MILLIS + "]ms以内に終了しませんでした。\r\n時間をおいて再度実行してください。");
                            return;
                        }
                        try {
                            Thread.sleep(5000);
                        } catch(InterruptedException e2) {}
                    }
                }
            } else {
                throw e;
            }
        } finally {
            if(isWait) {
                // シナリオコンボボックスを設定
                setupScenarioCombobox(null);
        
                // シナリオ系コンポーネントの設定（初期化）
                setupScenarioComponents();
        
                // テストケース表示パネルの初期化
                testCasePanel.initialize();
            }
        }
    }

    /**
     * シナリオグループ終了ボタンの押下アクション
     * @throws Exception
     */
    private void scenarioGroupEndAction() throws Exception {

        try {
            // シナリオグループの終了
            testController.endScenarioGroup();
            setupStatusLabel();
        } finally {
            // コンボボックスを非活性に
            scenarioCombobox.setEnabled(false);
            // シナリオコンボボックスを設定
            setupScenarioCombobox(null);
            // シナリオ系コンポーネントの設定（初期化）
            setupScenarioComponents();
        }
    }

    /**
     * シナリオ開始ボタンの押下アクション
     * @throws Exception
     */
    private void scenarioStartAction(boolean isWait) throws Exception {

        String selectScenarioGroupId = scenarioGroupCombobox.getSelectedItem().toString();
        String selectScenarioId = scenarioCombobox.getSelectedItem().toString();
        try {
            // シナリオの開始
            testController.startScenario(userId, selectScenarioId);
    
            // シナリオの検索、シナリオ一覧をコンボボックスに設定
            TestCase[] testCaseArray = testController.getTestCases(selectScenarioGroupId, selectScenarioId);
            List testCaseList = new ArrayList();
            for (int i = 0; i < testCaseArray.length; i++){
                testCaseList.add(testCaseArray[i]);
            }
            // コンポーネント、アクションリスナーの設定
            testCasePanel.initialize();
            testCasePanel.setScenarioGroupId(selectScenarioGroupId);
            testCasePanel.setScenarioId(selectScenarioId);
            testCasePanel.setTestCaseList(testCaseList);
            testCasePanel.addTestCaseControlListener(new TestCaseControlListenerImpl());
            testCasePanel.setUserId(userId);
            setupStatusLabel();
        }catch(TestStatusException e){
            if(isWait) {
                int result = JOptionPane.showConfirmDialog(this, e.getMessage() + "\r\n\r\n実行されているシナリオの終了を待ち、終了次第自動で開始しますか？" , "確認", JOptionPane.YES_NO_OPTION);
                if(JOptionPane.YES_OPTION == result){
                    long startTime = System.currentTimeMillis();
                    while(true) {
                        TestScenario scenario = testController.getCurrentScenario();
                        if(scenario == null) {
                            try {
                                scenarioStartAction(false);
                            } catch(TestStatusException e2) {
                                continue;
                            }
                            return;
                        }
                        if(System.currentTimeMillis() - startTime > MAX_WAIT_TIME_MILLIS) {
                            JOptionPane.showMessageDialog(this, "実行されているシナリオが待ち時間[" + MAX_WAIT_TIME_MILLIS + "]ms以内に終了しませんでした。\r\n時間をおいて再度実行してください。");
                            return;
                        }
                        try {
                            Thread.sleep(5000);
                        } catch(InterruptedException e2) {}
                    }
                }
            } else {
                throw e;
            }
        } finally {
            if(isWait) {
                // シナリオ系コンポーネントの設定（初期化）
                setupScenarioComponents();
            }
        }
    }

    /**
     * シナリオ終了ボタンの押下アクション
     * @throws Exception
     */
    private void scenarioEndAction() throws Exception {
        // シナリオの終了
        String selectScenarioId = scenarioCombobox.getSelectedItem().toString();
        try {
            testController.endScenario(selectScenarioId);
            setupStatusLabel();
        } finally {
            testCasePanel.initialize();
            // シナリオ系コンポーネントの設定
            setupScenarioComponents();
        }
    }

    /**
     * シナリオキャンセルボタンの押下アクション
     * @throws Exception
     */
    private void scenarioCancelAction() throws Exception {
        // シナリオのキャンセル
        String selectScenarioId = scenarioCombobox.getSelectedItem().toString();
        try {
            testController.cancelScenario(selectScenarioId);
            setupStatusLabel();
        } finally {
            testCasePanel.initialize();
            // シナリオ系コンポーネントの設定
            setupScenarioComponents();
        }
    }

    /**
     * シナリオダウンロードボタンの押下アクション
     * @throws Exception
     */
    private void scenarioDownloadAction() throws Exception {
        // シナリオの結果ダウンロード
        File dlDir = showDownloadFileSaveDialog(cashDlDir);
        if(dlDir != null){
            String scenarioGroup = testController.getCurrentScenarioGroup().getScenarioGroupId();
            String selectScenarioId = scenarioCombobox.getSelectedItem().toString();
            testController.downloadScenarioResult(dlDir, scenarioGroup, selectScenarioId, TestController.RESPONSE_FILE_TYPE_ZIP);

            JOptionPane.showMessageDialog(this, "ディレクトリ「" + dlDir + "」に\r\n正常にダウンロードが完了しました。");
        }
        cashDlDir = dlDir;
        testCasePanel.initialize();

        // シナリオ系コンポーネントの設定
        setupScenarioComponents();
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
        Object selectObject = scenarioGroupCombobox.getSelectedItem();
        if(selectObject != null){
            String tmpEditText = selectObject.toString();
            setupScenarioGroupCombobox(tmpEditText);
        }
    }

    /**
     * シナリオコンボボックスの値編集時のアクション
     * @throws Exception
     */
    private void scenarioComboboxEditedAction() throws Exception {
        Object selectObject = scenarioCombobox.getSelectedItem();
        if(selectObject != null){
            String tmpEditText = selectObject.toString();
            setupScenarioCombobox(tmpEditText);
        }
        setupScenarioComponents();
    }

    /**
     * シナリオコンボボックスの値変更時のアクション
     * @throws Exception
     */
    private void scenarioComboboxChangeAction() throws Exception {
        setupScenarioComponents();
    }

    /**
     * シナリオグループのコンボボックスを設定
     *
     * @param keyword これが指定されている場合は、この文字を含むもののみコンボボックスに設定
     * @throws Exception
     */
    private void setupScenarioGroupCombobox(String keyword) throws Exception{

        scenarioGroupCombobox.removeAllItems();
        // テストシナリオグループのコンボボックスを設定
        TestScenarioGroup[] testScenarioGroupArray = testController.getScenarioGroups();
        if(testScenarioGroupArray != null){
            for (int i = 0; i < testScenarioGroupArray.length; i++) {
                String scenarioGroupId = testScenarioGroupArray[i].getScenarioGroupId();
                if(keyword == null || keyword.length() == 0 || scenarioGroupId.indexOf(keyword) >= 0){
                    scenarioGroupCombobox.addItem(scenarioGroupId);
                }
            }
        }
    }

    private void setupScenarioCombobox(String keyword) throws Exception{

        scenarioCombobox.removeAllItems();
        TestScenarioGroup currentScenarioGroup = testController.getCurrentScenarioGroup();

        if(currentScenarioGroup == null || TestScenarioGroup.Status.STARTED != currentScenarioGroup.getStatus().getState() ){
            scenarioCombobox.removeAllItems();
            scenarioCombobox.setEnabled(false);
            return;
        }
        scenarioCombobox.setEnabled(true);

        // テストシナリオのコンボボックスを設定
        TestScenario[] testScenarioArray = testController.getScenarios(currentScenarioGroup.getScenarioGroupId());
        if(testScenarioArray != null){
            for (int i = 0; i < testScenarioArray.length; i++) {
                String scenarioId = testScenarioArray[i].getScenarioId();
                if(keyword == null || keyword.length() == 0 || scenarioId.indexOf(keyword) >= 0){
                    scenarioCombobox.addItem(scenarioId);
                }

                // 開始状態の場合
                if(testScenarioArray[i] != null &&
                        testScenarioArray[i].getStatus() != null &&
                        testScenarioArray[i].getStatus().getState() == Status.STARTED){

                    String selectScenarioGroupId = currentScenarioGroup.getScenarioGroupId();
                    String selectScenarioId = testScenarioArray[i].getScenarioId();

                    TestCase[] testCaseArray = testController.getTestCases(selectScenarioGroupId, selectScenarioId);
                    List testCaseList = new ArrayList();
                    for (int t = 0; t < testCaseArray.length; t++){
                        testCaseList.add(testCaseArray[t]);
                    }

                    testCasePanel.initialize();
                    testCasePanel.setScenarioGroupId(selectScenarioGroupId);
                    testCasePanel.setScenarioId(selectScenarioId);
                    testCasePanel.setTestCaseList(testCaseList);
                    testCasePanel.addTestCaseControlListener(new TestCaseControlListenerImpl());
                    testCasePanel.setUserId(userId);
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
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        if(group != null){
            sb.append("ScenarioGroup=" + group.getScenarioGroupId());
            if(group.getStatus() != null){
                sb.append("(" + group.getStatus().getUserId());
                int state = group.getStatus().getState();
                switch (state) {
                    case Status.INITIAL:
                        sb.append(" INITIAL");
                        break;
                    case Status.STARTED:
                        sb.append(" STARTED");
                        break;
                    case Status.END:
                        sb.append(" END");
                        break;
                    case Status.ERROR:
                        sb.append(" ERROR");
                        break;
                    case Status.CANCELED:
                        sb.append(" CANCELED");
                        break;
                    default:
                }
                if(group.getStatus().getStartTime() != null){
                    sb.append(" " + sdf.format(group.getStatus().getStartTime()) + " - ");
                }
                if(group.getStatus().getEndTime() != null){
                    sb.append(sdf.format(group.getStatus().getEndTime()));
                }
                sb.append(")");
            }
            if(scenario != null){
                sb.append(", Scenario=" + scenario.getScenarioId());
                if(scenario.getStatus() != null){
                    sb.append("(" + scenario.getStatus().getUserId());
                    int state = scenario.getStatus().getState();
                    switch (state) {
                        case Status.INITIAL:
                            sb.append(" INITIAL");
                            break;
                        case Status.STARTED:
                            sb.append(" STARTED");
                            break;
                        case Status.END:
                            sb.append(" END");
                            break;
                        case Status.ERROR:
                            sb.append(" ERROR");
                            break;
                        case Status.CANCELED:
                            sb.append(" CANCELED");
                            break;
                        default:
                    }
                    if(scenario.getStatus().getStartTime() != null){
                        sb.append(" " + sdf.format(scenario.getStatus().getStartTime()) + " - ");
                    }
                    if(scenario.getStatus().getEndTime() != null){
                        sb.append(sdf.format(scenario.getStatus().getEndTime()));
                    }
                    sb.append(")");
                }
                if(testcase != null){
                    sb.append(", Testcase=" + testcase.getTestCaseId());
                    if(testcase.getStatus() != null){
                        sb.append("(" + testcase.getStatus().getUserId());
                        int state = testcase.getStatus().getState();
                        switch (state) {
                            case Status.INITIAL:
                                sb.append(" INITIAL");
                                break;
                            case Status.STARTED:
                                sb.append(" STARTED");
                                break;
                            case Status.END:
                                sb.append(" END");
                                break;
                            case Status.ERROR:
                                sb.append(" ERROR");
                                break;
                            case Status.CANCELED:
                                sb.append(" CANCELED");
                                break;
                            default:
                        }
                        if(testcase.getStatus().getStartTime() != null){
                            sb.append(" " + sdf.format(testcase.getStatus().getStartTime()) + " - ");
                        }
                        if(testcase.getStatus().getEndTime() != null){
                            sb.append(sdf.format(testcase.getStatus().getEndTime()));
                        }
                        sb.append(")");
                    }
                }
            }
        } else {
            sb.append("無し");
        }
        statusLabel2.setText(sb.toString());
        statusLabel2.setToolTipText(sb.toString());
    }

    /**
     * シナリオ系コンポーネントの設定
     * @throws Exception
     */
    private void setupScenarioComponents() throws Exception {

        TestScenarioGroup.Status scenarioGroupStstus = null;
        if(scenarioGroupCombobox.getSelectedItem() != null) {
            String selectedScenarioGroupId = scenarioGroupCombobox.getSelectedItem().toString();
            TestScenarioGroup scenarioGroup = testController.getScenarioGroup(selectedScenarioGroupId);
            if(scenarioGroup != null) {
                scenarioGroupStstus = scenarioGroup.getStatus();
            }
        }
        TestScenarioGroup currentScenarioGroup = testController.getCurrentScenarioGroup();
        if(currentScenarioGroup == null){
            scenarioGroupStatusButton.change(scenarioGroupStstus);
            scenarioGroupStartButton.setEnabled(true);
            scenarioGroupEndButton.setEnabled(false);
            scenarioCombobox.setEnabled(false);
            scenarioStartButton.setEnabled(false);
            scenarioEndButton.setEnabled(false);
            scenarioCancelButton.setEnabled(false);
            scenarioDownloadButton.setEnabled(false);
            scenarioStatusButton.change(null);
            return;
        } else if(TestScenarioGroup.Status.STARTED != currentScenarioGroup.getStatus().getState()) {
            scenarioGroupStatusButton.change(currentScenarioGroup.getStatus());
            scenarioGroupStartButton.setEnabled(false);
            scenarioGroupEndButton.setEnabled(true);
            scenarioCombobox.setEnabled(false);
            scenarioStartButton.setEnabled(false);
            scenarioEndButton.setEnabled(false);
            scenarioCancelButton.setEnabled(false);
            scenarioDownloadButton.setEnabled(false);
            scenarioStatusButton.change(null);
            return;
        }else{
            scenarioGroupStatusButton.change(currentScenarioGroup.getStatus());
            scenarioGroupStartButton.setEnabled(false);
            scenarioGroupEndButton.setEnabled(true);
        }

        String currentScenarioGroupId = currentScenarioGroup.getScenarioGroupId();

        if(scenarioCombobox.getSelectedItem() == null)
            return;

        String selectScenarioId = scenarioCombobox.getSelectedItem().toString();

        TestScenario selectSenario = null;

        if(currentScenarioGroupId != null && selectScenarioId != null){
            selectSenario = testController.getScenario(currentScenarioGroupId, selectScenarioId);
        }
        if(selectSenario == null){
            return;
        }
        final TestScenario.Status status = selectSenario.getStatus();

        scenarioStatusButton.change(status);
        //--

        if (status != null && status.getState() == TestScenario.Status.STARTED) {
            scenarioCombobox.setEnabled(true);
            scenarioStartButton.setEnabled(false);
            scenarioEndButton.setEnabled(true);
            scenarioCancelButton.setEnabled(true);
            scenarioDownloadButton.setEnabled(false);

        } else if (status != null && status.getState() == TestScenario.Status.END) {
            scenarioCombobox.setEnabled(true);
            scenarioStartButton.setEnabled(true);
            scenarioEndButton.setEnabled(false);
            scenarioCancelButton.setEnabled(false);
            scenarioDownloadButton.setEnabled(true);

        } else if (status == null ||
                (status != null &&
                    (status.getState() == TestScenario.Status.INITIAL ||
                     status.getState() == TestScenario.Status.CANCELED ||
                     status.getState() == TestScenario.Status.ERROR))) {
            scenarioCombobox.setEnabled(true);
            scenarioStartButton.setEnabled(true);
            scenarioEndButton.setEnabled(false);
            scenarioCancelButton.setEnabled(false);
            scenarioDownloadButton.setEnabled(false);

        } else {

            if(scenarioCombobox.getItemCount() == 0){
                scenarioCombobox.setEnabled(false);
            }else{
                scenarioCombobox.setEnabled(true);
            }
            scenarioStartButton.setEnabled(false);
            scenarioEndButton.setEnabled(false);
            scenarioCancelButton.setEnabled(false);
            scenarioDownloadButton.setEnabled(false);
        }
    }


    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentResized(ComponentEvent e) {

        if(MINIMUM_SIZE.width > getWidth() || MINIMUM_SIZE.height > getHeight()){
            setSize(MINIMUM_SIZE);
            return;
        }

        if(testCasePanel.getWidth() > testCaseScrollPanel.getViewport().getWidth()){
            int width = testCaseScrollPanel.getViewport().getWidth();
            int height = testCasePanel.getHeight();
            testCasePanel.setSize(width, height);
        }

        try {
            testCasePanel.resetup();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public void componentShown(ComponentEvent e) {
    }

    public boolean isStatusDialogDisplay() {
        return statusDialogDisplayCheckBox.isSelected();
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
    
    private class TestControllerOperationWorker extends SwingWorker<Object, Object> {
        
        private int mode;
        private JFrame frame;
        private JButton button;
        
        public static final int MODE_SCENARIO_GROUP_START = 1;
        public static final int MODE_SCENARIO_GROUP_END = 2;
        public static final int MODE_SCENARIO_START = 3;
        public static final int MODE_SCENARIO_END = 4;
        public static final int MODE_SCENARIO_CANCEL = 5;
        
        public TestControllerOperationWorker(JFrame frame, JButton button, int mode) {
            this.frame = frame;
            this.button = button;
            this.mode = mode;
        }

        @Override
        protected Object doInBackground() throws Exception {
            button.setEnabled(false);
            try {
                isServerCalling = true;
                switch(mode) {
                case MODE_SCENARIO_GROUP_START:
                    scenarioGroupStartAction(true);
                    break;
                case MODE_SCENARIO_GROUP_END:
                    scenarioGroupEndAction();
                    break;
                case MODE_SCENARIO_START:
                    scenarioStartAction(true);
                    break;
                case MODE_SCENARIO_END:
                    scenarioEndAction();
                    break;
                case MODE_SCENARIO_CANCEL:
                    scenarioCancelAction();
                    break;
                default:
                }
            }catch(TestStatusException e){
                JDialog dialog = new StatusDialogView(frame, "警告", e);
                dialog.setModal(true);
                dialog.setVisible(true);
            } catch (Exception e) {
                JDialog dialog = new StatusDialogView(frame, "例外", e);
                dialog.setModal(true);
                dialog.setVisible(true);
            } finally {
                isServerCalling = false;                
            }
            return null;
        }
    }
    
    private class StatusCheckWorker extends SwingWorker<Object, Object> {
        
        private int mode;
        private JFrame frame;
        private TextAreaDialogView dialog;
        private boolean isAutoDisplay;
        
        public static final int MODE_SCENARIO_GROUP = 1;
        public static final int MODE_SCENARIO = 2;
        
        public StatusCheckWorker(JFrame frame, int mode, boolean isAutoDisplay) {
            this.frame = frame;
            this.mode = mode;
            this.isAutoDisplay = isAutoDisplay;
        }
        
        @Override
        protected Object doInBackground() throws Exception {
            switch(mode) {
            case MODE_SCENARIO_GROUP:
                dialog = new TextAreaDialogView(frame, "ScenarioGroup Action実行状況");
                break;
            case MODE_SCENARIO:
                dialog = new TextAreaDialogView(frame, "Scenario Action実行状況");
                break;
            }
            dialog.setVisible(true);
            while((isServerCalling || !isAutoDisplay) && dialog != null && dialog.isVisible()) {
                try {
                    StringBuilder sb = new StringBuilder();
                    switch(mode) {
                    case MODE_SCENARIO_GROUP:
                        TestScenarioGroup group = testController.getCurrentScenarioGroup();
                        if(group == null) {
                            sb.append("ScenarioGroup is not started.");
                        } else {
                            sb.append("ScenarioGroup [" + group.getScenarioGroupId() + "] Started User [" + group.getStatus().getUserId() + "] Status...\r\n");
                            Map endMap = group.getStatus().getActionEndMap();
                            Iterator itr = endMap.entrySet().iterator();
                            if(endMap.isEmpty()) {
                                sb.append("\t Action is empty.");
                            } else {
                                while(itr.hasNext()) {
                                    Entry entry = (Entry)itr.next();
                                    boolean isEnd = (Boolean)entry.getValue();
                                    sb.append("\t Action [" + entry.getKey() + "] is ");
                                    if(isEnd) {
                                        boolean result = group.getStatus().getActionResult((String)entry.getKey());
                                        sb.append("end. result is " + result + "\r\n");
                                    } else {
                                        sb.append("excuting...\r\n");
                                    }
                                }
                            }
                        }
                        break;
                    case MODE_SCENARIO:
                        TestScenario scenario = testController.getCurrentScenario();
                        if(scenario == null) {
                            sb.append("Scenario is not started.");
                        } else {
                            sb.append("Scenario [" + scenario.getScenarioId() + " Started User [" + scenario.getStatus().getUserId() + "] Status...\r\n");
                            Map endMap = scenario.getStatus().getActionEndMap();
                            if(endMap.isEmpty()) {
                                sb.append("\t Action is empty.");
                            } else {
                                Iterator itr = endMap.entrySet().iterator();
                                while(itr.hasNext()) {
                                    Entry entry = (Entry)itr.next();
                                    boolean isEnd = (Boolean)entry.getValue();
                                    sb.append("\t Action [" + entry.getKey() + "] is ");
                                    if(isEnd) {
                                        boolean result = scenario.getStatus().getActionResult((String)entry.getKey());
                                        sb.append("end. result is " + result + "\r\n");
                                    } else {
                                        sb.append("excuting...\r\n");
                                    }
                                }
                            }
                        }
                        break;
                    default :
                        break;
                    }
                    publish(sb.toString());
                    Thread.sleep(3000l);
                }catch (InterruptedException e) {
                }catch (Exception e) {
                    throw e;
                }
            }
            return null;
        }
        
        @Override
        protected void done() {
            dialog.setVisible(false);
            dialog = null;
        }
        
        @Override
        protected void process(List<Object> chunks) {
            if(chunks != null && chunks.size() > 0) {
                dialog.setText((String)chunks.get(chunks.size() - 1));
            }
        }
    }

}
