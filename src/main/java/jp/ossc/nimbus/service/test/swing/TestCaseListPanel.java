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

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import jp.ossc.nimbus.service.test.TestCase;
import jp.ossc.nimbus.service.test.TestCase.Status;
import jp.ossc.nimbus.service.test.TestController;
import jp.ossc.nimbus.service.test.TestStatusException;

public class TestCaseListPanel extends JPanel /*implements ComponentListener*/ {
    
    private final int MARGIN = 8;       // 周り、行間の余白
    private final int HEIGHT = 30;      // 行の高さ
    
    private final int NO_WIDTH = 50;
    private int ID_WIDTH = 150; //可変
    private int TITLE_WIDTH = 150; //可変
    private final int START_DATE_WIDTH = 150;
    private final int END_DATE_WIDTH = 150;
    private final int STATE_WIDTH = 65;
    private final int START_BUTTON_WIDTH = 70;
    private final int END_BUTTON_WIDTH = 70;
    private final int DOWNLOAD_BUTTON_WIDTH = 90;
    private final int EVI_BUTTON_WIDTH = 90;
    
    private String userId = null;
    private TestController testController = null;
    private String scenarioGroupId = null;
    private String scenarioId = null;
    private List testCaseList = new ArrayList();

    private File cashDlDir = null;
    
    private List testCaseControlListenerList = null;
    
    private JFrame ownerFrame = null;
    
    private List lineList = null;
    
    private boolean isServerCalling = false;
    
    public TestCaseListPanel(JFrame ownerFrame) throws Exception {
        this.ownerFrame = ownerFrame;
        initialize();
    }
    
    public void initialize() throws Exception {
        testCaseList = null;
        setupTestCaseCompornents();
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public void setTestController(TestController testController) {
        this.testController = testController;
    }
    
    public void setScenarioGroupId(String scenarioGroupId) {
        this.scenarioGroupId = scenarioGroupId;
    }
    
    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
    }
    
    public void setTestCaseList(List testCaseList) throws Exception {
        this.testCaseList = testCaseList;
        setupTestCaseCompornents();
        testCaseControlListenerList = new ArrayList();
    }
    
    public void resetup() throws Exception{
        setupTestCaseCompornents();
    }
    
    public void addTestCaseControlListener(TestCaseControlListener testCaseControlListener){
        testCaseControlListenerList.add(testCaseControlListener);
    }
    
    
    
    /**
     * テストケースコンポーネントのセットアップ
     * @throws Exception 
     */
    private void setupTestCaseCompornents() throws Exception {

        int ROW_SPACE = 10;   // 列間の余白
        Font font = new Font("ＭＳ ゴシック", Font.BOLD, 16);
        
        int tmpX = 0;
        int tmpY = 0;
        
        String DATE_PATTERN = "yyyy/MM/dd HH:mm";
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        
        // 初期化
        removeAll();
        setLayout(null);
        
        int winWidth = getWidth();
        
        ROW_SPACE = (winWidth - (NO_WIDTH + START_DATE_WIDTH + END_DATE_WIDTH + STATE_WIDTH + STATE_WIDTH + START_BUTTON_WIDTH + END_BUTTON_WIDTH + DOWNLOAD_BUTTON_WIDTH + EVI_BUTTON_WIDTH + MARGIN + MARGIN)) / 2;
        ID_WIDTH = ROW_SPACE;
        TITLE_WIDTH = ROW_SPACE;
        
        // タイトル行のセットアップ
        JLabel label1 = new JLabel("No");
        JLabel label2 = new JLabel("テストケースID");
        JLabel label3 = new JLabel("テストケース名");
        JLabel label4 = new JLabel("開始日時");
        JLabel label5 = new JLabel("終了日時");
        JLabel label6 = new JLabel("ｴﾗｰ状態");
        JLabel label7 = new JLabel("状態");
        JLabel label8 = new JLabel("　");
        JLabel label9 = new JLabel("　");
        JLabel label10 = new JLabel("　");
        JLabel label11 = new JLabel("　");
        
        label1.setFont(font);
        label2.setFont(font);
        label3.setFont(font);
        label4.setFont(font);
        label5.setFont(font);
        label6.setFont(font);
        label7.setFont(font);
        label8.setFont(font);
        label9.setFont(font);
        label10.setFont(font);
        label11.setFont(font);
        
        tmpX += MARGIN;
        tmpY += MARGIN;
        
        label1.setBounds(tmpX, tmpY, NO_WIDTH, HEIGHT);
        tmpX += NO_WIDTH;
        label2.setBounds(tmpX, tmpY, ID_WIDTH, HEIGHT);
        tmpX += ID_WIDTH;
        label3.setBounds(tmpX, tmpY, TITLE_WIDTH, HEIGHT);
        tmpX += TITLE_WIDTH;
        label4.setBounds(tmpX, tmpY, START_DATE_WIDTH, HEIGHT);
        tmpX += START_DATE_WIDTH;
        label5.setBounds(tmpX, tmpY, END_DATE_WIDTH, HEIGHT);
        tmpX += END_DATE_WIDTH;
        label6.setBounds(tmpX, tmpY, STATE_WIDTH, HEIGHT);
        tmpX += STATE_WIDTH;
        label7.setBounds(tmpX, tmpY, STATE_WIDTH, HEIGHT);
        tmpX += STATE_WIDTH;
        label8.setBounds(tmpX, tmpY, START_BUTTON_WIDTH, HEIGHT);
        tmpX += START_BUTTON_WIDTH;
        label9.setBounds(tmpX, tmpY, END_BUTTON_WIDTH, HEIGHT);
        tmpX += END_BUTTON_WIDTH;
        label10.setBounds(tmpX, tmpY, DOWNLOAD_BUTTON_WIDTH, HEIGHT);
        tmpX += DOWNLOAD_BUTTON_WIDTH;
        label11.setBounds(tmpX, tmpY, EVI_BUTTON_WIDTH, HEIGHT);
        
        add(label1);
        add(label2);
        add(label3);
        add(label4);
        add(label5);
        add(label6);
        add(label7);
        add(label8);
        add(label9);
        add(label10);
        add(label11);
        
        if (testCaseList != null) {
            
            boolean startTestCaseFlg = false;
            
            lineList = new ArrayList();
            
            for (int i = 0; i < testCaseList.size(); i++) {
                TestCase testCase = (TestCase) testCaseList.get(i);
                Status status = testCase.getStatus();
                if (status != null && (status.getState() == TestCase.Status.STARTED || status.getState() == TestCase.Status.ERROR)) {
                    startTestCaseFlg = true;
                }
            }
            
            for (int i = 0; i < testCaseList.size(); i++) {
                
                tmpX = MARGIN;
                tmpY += HEIGHT + MARGIN;
                
                TestCase testCase = (TestCase) testCaseList.get(i);
                
                label1 = new JLabel((new Integer(i + 1)).toString());
                label2 = new JLabel(testCase.getTestCaseId());
                label2.setToolTipText(testCase.getTestCaseId());
                label3 = new JLabel(testCase.getTestCaseResource().getTitle());
                label3.setToolTipText(testCase.getTestCaseResource().getTitle());
                label4 = new JLabel("");
                label5 = new JLabel("");
                label6 = new JLabel("");
                
                TestErrorStatusDispButton tmpErrorStatusButton = new TestErrorStatusDispButton(ownerFrame);
                
                Status status = null;
                try {
                    status = testCase.getStatus();
                } catch (Exception e) {
                }
                
                label1.setFont(font);
                label2.setFont(font);
                label3.setFont(font);
                label4.setFont(font);
                label5.setFont(font);
                label6.setFont(font);

                Point xLinePoint = new Point(tmpX, tmpY - ((int)(MARGIN / 2)));
                
                label1.setBounds(tmpX, tmpY, NO_WIDTH, HEIGHT);
                tmpX += NO_WIDTH;
                label2.setBounds(tmpX, tmpY, ID_WIDTH, HEIGHT);
                tmpX += ID_WIDTH;
                label3.setBounds(tmpX, tmpY, TITLE_WIDTH, HEIGHT);
                tmpX += TITLE_WIDTH;
                label4.setBounds(tmpX, tmpY, START_DATE_WIDTH, HEIGHT);
                tmpX += START_DATE_WIDTH;
                label5.setBounds(tmpX, tmpY, END_DATE_WIDTH, HEIGHT);
                tmpX += END_DATE_WIDTH;
                tmpErrorStatusButton.setBounds(tmpX, tmpY, STATE_WIDTH, HEIGHT);
                tmpX += STATE_WIDTH;
                label6.setBounds(tmpX, tmpY, STATE_WIDTH, HEIGHT);
                tmpX += STATE_WIDTH;
                
                Point yLinePoint = new Point(tmpX, tmpY - ((int)(MARGIN / 2)));
                lineList.add(new Line(xLinePoint, yLinePoint));
                
                // 開始時間、終了時間、状態 を表示
                if (status != null){
                    if(status.getStartTime() != null)
                        label4.setText(dateFormat.format(status.getStartTime()));
                    if(status.getEndTime() != null)
                        label5.setText(dateFormat.format(status.getEndTime()));
                    
                    label6.setText(status.getStateString());
                }
                tmpErrorStatusButton.change(status);
                
                JButton tmpButton1 = new JButton("開始");
                tmpButton1.addActionListener(new StartButtonActionListener(tmpButton1, testCase));
                
                JButton tmpButton2 = new JButton("終了");
                tmpButton2.addActionListener(new EndButtonActionListener(tmpButton1, testCase));
                
                JButton tmpButton3 = new JButton("結果DL");
                tmpButton3.addActionListener(new DownloadButtonActionListener(testCase));
                
                JButton tmpButton4 = new JButton("確認OK");
                tmpButton4.setToolTipText("比較対象データファイルをエビデンスファイルに変換します。");
                tmpButton4.addActionListener(new EvidenceButtonActionListener(testCase));
                
                if(!startTestCaseFlg){
                    tmpButton1.setEnabled(true);
                }else{
                    tmpButton1.setEnabled(false);
                }
                if(status == null) {
                    tmpButton2.setEnabled(false);
                    tmpButton3.setEnabled(false);
                    tmpButton4.setEnabled(false);
                } else {
                    int state = status.getState();
                    switch(state) {
                        case TestCase.Status.INITIAL:
                            tmpButton2.setEnabled(false);
                            tmpButton3.setEnabled(false);
                            break;
                        case TestCase.Status.STARTED:
                            tmpButton2.setEnabled(true);
                            tmpButton3.setEnabled(false);
                            break;
                        case TestCase.Status.END:
                            tmpButton2.setEnabled(false);
                            tmpButton3.setEnabled(true);
                            break;
                        case TestCase.Status.ERROR:
                            tmpButton2.setEnabled(true);
                            tmpButton3.setEnabled(true);
                            break;
                        case TestCase.Status.CANCELED:
                            tmpButton2.setEnabled(true);
                            tmpButton3.setEnabled(false);
                            break;
                    }
                    if(status.getResult()) {
                        tmpButton4.setEnabled(false);
                    } else {
                        tmpButton4.setEnabled(true);
                    }
                }
                
                tmpButton1.setFont(font);
                tmpButton2.setFont(font);
                tmpButton3.setFont(font);
                tmpButton4.setFont(font);
                
                tmpButton1.setBounds(tmpX, tmpY, START_BUTTON_WIDTH, HEIGHT);
                tmpX += START_BUTTON_WIDTH;
                tmpButton2.setBounds(tmpX, tmpY, END_BUTTON_WIDTH, HEIGHT);
                tmpX += END_BUTTON_WIDTH;
                tmpButton3.setBounds(tmpX, tmpY, DOWNLOAD_BUTTON_WIDTH, HEIGHT);
                tmpX += DOWNLOAD_BUTTON_WIDTH;
                tmpButton4.setBounds(tmpX, tmpY, EVI_BUTTON_WIDTH, HEIGHT);

                add(label1);
                add(label2);
                add(label3);
                add(label4);
                add(label5);
                add(tmpErrorStatusButton);
                add(label6);
                add(tmpButton1);
                add(tmpButton2);
                add(tmpButton3);
                add(tmpButton4);
            }
        }

        int tmpW = winWidth;
        int tmpH = tmpY + HEIGHT + MARGIN;

        setPreferredSize(new Dimension(tmpW, tmpH));
        repaint();
    }
    
    /**
     * テストケース開始ボタンのアクションリスナー
     * @author j-higuchi
     */
    private class StartButtonActionListener implements ActionListener {
        
        private TestCase testCase = null;
        private JButton button = null;
        
        public StartButtonActionListener(JButton button, TestCase testCase) {
            this.testCase = testCase;
            this.button = button;
        }

        public void actionPerformed(ActionEvent e) {
            SwingWorker testControllerWorker = new TestControllerOperationWorker(ownerFrame, button, testCase, TestControllerOperationWorker.MODE_TEST_START);
            testControllerWorker.execute();
            if(((ScenarioTestView)ownerFrame).isStatusDialogDisplay()) {
                SwingWorker statusWorker = new StatusCheckWorker(ownerFrame, true);
                statusWorker.execute();
            }
        }
    }
    
    private void startTestCase(TestCase testCase) throws Exception {
        try {
            testController.startTestCase(userId, scenarioId, testCase.getTestCaseId());
            // リスナー呼び出し
            for(int i=0; i < testCaseControlListenerList.size(); i++) {
                ((TestCaseControlListener)testCaseControlListenerList.get(i)).startTestCase(testCase);
            }
        } finally {
            setupTestCaseCompornents();
        }
    }

    /**
     * テストケース終了ボタンのアクションリスナー
     * @author j-higuchi
     */
    private class EndButtonActionListener implements ActionListener {

        private TestCase testCase = null;
        private JButton button = null;

        public EndButtonActionListener(JButton button, TestCase testCase) {
            this.testCase = testCase;
            this.button = button;
        }

        public void actionPerformed(ActionEvent e) {
            SwingWorker testControllerWorker = new TestControllerOperationWorker(ownerFrame, button, testCase, TestControllerOperationWorker.MODE_TEST_END);
            testControllerWorker.execute();
            if(((ScenarioTestView)ownerFrame).isStatusDialogDisplay()) {
                SwingWorker statusWorker = new StatusCheckWorker(ownerFrame, true);
                statusWorker.execute();
            }
        }
    }
    
    private void endTestCase(TestCase testCase) throws Exception {
        try {
            testController.endTestCase(scenarioId, testCase.getTestCaseId());
        } finally {
            Exception ex = null;
            try {
                // リスナー呼び出し
                for(int i=0; i < testCaseControlListenerList.size(); i++) {
                    ((TestCaseControlListener)testCaseControlListenerList.get(i)).startTestCase(testCase);
                }
            } catch(Exception e) {
                if(ex == null) {
                    ex = e;
                }
            }
            setupTestCaseCompornents();
            if(ex != null) {
                throw ex;
            }
        }
    }
    
    /**
     * テストケースの実行結果ダウンロードボタンのアクションリスナー
     * @author j-higuchi
     */
    private class DownloadButtonActionListener implements ActionListener {
        
        private TestCase testCase = null;
        
        public DownloadButtonActionListener(TestCase testCase) {
            this.testCase = testCase;
        }
        
        public void actionPerformed(ActionEvent e) {
            // テストケースの結果ダウンロード
            try {
                File dlDir = showDownloadFileSaveDialog(cashDlDir);
                if(dlDir != null){
                    testController.downloadTestCaseResult(dlDir, scenarioGroupId, scenarioId, testCase.getTestCaseId(), TestController.RESPONSE_FILE_TYPE_ZIP);
                    
                    showMessageDialog("ディレクトリ「" + dlDir + "」に\r\n正常にダウンロードが完了しました。");
                }
                cashDlDir = dlDir;
                
                // コンポーネントの再配置
                setupTestCaseCompornents();
            } catch (Exception e1) {
                JDialog dialog = new StatusDialogView(ownerFrame, "Exception", e1);
                dialog.setModal(true);
                dialog.setVisible(true);
            }
        }
    }
    
    /**
     * テストケースのエビデンス変換ボタンのアクションリスナー
     */
    private class EvidenceButtonActionListener implements ActionListener {
        
        private TestCase testCase = null;
        
        public EvidenceButtonActionListener(TestCase testCase) {
            this.testCase = testCase;
        }
        
        public void actionPerformed(ActionEvent e) {
            int result = JOptionPane.showConfirmDialog(ownerFrame, "結果ファイルをエビデンスファイルに変換しますか？", "確認",
                    JOptionPane.YES_NO_OPTION);
            if (JOptionPane.YES_OPTION == result) {
                try {
                    testController.generateTestCaseEvidenceFile(testCase.getScenarioGroupId(), testCase.getScenarioId(), testCase.getTestCaseId());
                } catch (Exception e1) {
                    JDialog dialog = new StatusDialogView(ownerFrame, "Exception", e1);
                    dialog.setModal(true);
                    dialog.setVisible(true);
                }
            }
        }
    }
    
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
    
    private void showMessageDialog(String message){
        JOptionPane.showMessageDialog(this, message);
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        float dash[] = {1.0f, 1.0f};
        BasicStroke dsahStroke = new BasicStroke(1.0f, 
                BasicStroke.CAP_BUTT, 
                BasicStroke.JOIN_MITER, 
                1.0f,
                dash,
                0.0f);
        ((Graphics2D)g).setStroke(dsahStroke);
        
        if(lineList != null){
            for(int i = 0; i < lineList.size(); i++){
                Line tmpLine = (Line)lineList.get(i);
                if(tmpLine != null && tmpLine.getStartPoint() != null && tmpLine.getEndPoint() != null){
                    
                    g.drawLine(
                            (int)tmpLine.getStartPoint().getX(),
                            (int)tmpLine.getStartPoint().getY(),
                            (int)tmpLine.getEndPoint().getX(),
                            (int)tmpLine.getEndPoint().getY());
                }
            }
        }
    }
    
    public void showTestCaseStatusDialog() {
        SwingWorker statusWorker = new StatusCheckWorker(ownerFrame, false);
        statusWorker.execute();
    }
    
    private class Line{
        
        private Point startPoint = null;
        private Point endPoint = null;
        
        public Line(Point startPoint, Point endPoint){
            this.startPoint = startPoint;
            this.endPoint = endPoint;
        }

        public Point getStartPoint() {
            return startPoint;
        }
        public void setStartPoint(Point startPoint) {
            this.startPoint = startPoint;
        }

        public Point getEndPoint() {
            return endPoint;
        }
        public void setEndPoint(Point endPoint) {
            this.endPoint = endPoint;
        }
    }

    private class TestControllerOperationWorker extends SwingWorker<Object, Object> {
        
        private int mode;
        private JFrame frame;
        private JButton button;
        private TestCase testCase;
        
        public static final int MODE_TEST_START = 1;
        public static final int MODE_TEST_END = 2;
        
        public TestControllerOperationWorker(JFrame frame, JButton button, TestCase testCase, int mode) {
            this.frame = frame;
            this.button = button;
            this.testCase = testCase;
            this.mode = mode;
        }

        @Override
        protected Object doInBackground() throws Exception {
            button.setEnabled(false);
            try {
                isServerCalling = true;
                switch(mode) {
                case MODE_TEST_START:
                    startTestCase(testCase);
                    break;
                case MODE_TEST_END:
                    endTestCase(testCase);
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
        
        private JFrame frame;
        private TextAreaDialogView dialog;
        private boolean isAutoDisplay;
        
        public StatusCheckWorker(JFrame frame, boolean isAutoDisplay) {
            this.frame = frame;
            this.isAutoDisplay = isAutoDisplay;
        }
        
        @Override
        protected Object doInBackground() throws Exception {
            dialog = new TextAreaDialogView(frame, "Testcase Action実行状況");
            dialog.setVisible(true);
            while((isServerCalling || !isAutoDisplay) && dialog != null && dialog.isVisible()) {
                try {
                    StringBuilder sb = new StringBuilder();
                    TestCase testCase = testController.getCurrentTestCase();
                    if(testCase == null) {
                        sb.append("TestCase is not started.");
                    } else {
                        sb.append("TestCase [" + testCase.getTestCaseId() + "] Started User [" + testCase.getStatus().getUserId() + "] Status...\r\n");
                        Map endMap = testCase.getStatus().getActionEndMap();
                        if(endMap.isEmpty()) {
                            sb.append("\t Action is empty.");
                        } else {
                            Iterator itr = endMap.entrySet().iterator();
                            while(itr.hasNext()) {
                                Entry entry = (Entry)itr.next();
                                boolean isEnd = (Boolean)entry.getValue();
                                sb.append("\t Action [" + entry.getKey() + "] is ");
                                if(isEnd) {
                                    boolean result = testCase.getStatus().getActionResult((String)entry.getKey());
                                    sb.append("end. result is " + result + "\r\n");
                                } else {
                                    sb.append("excuting...\r\n");
                                }
                            }
                        }
                    }
                    publish(sb.toString());
                    Thread.sleep(2000l);
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
