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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.test.TestController;

public class UserIdInputView extends JFrame implements ActionListener, KeyListener {
    
    private JTextField textBox = null;
    private JButton okButton = null;
    private boolean isWindowClosed;
    private List servicePaths;
    
    private TestController testController = null;
    

    public void setTestController(TestController testController){
        this.testController = testController;
    }
    
    public UserIdInputView(List servicePaths) throws Exception {
        this.servicePaths = servicePaths;
        this.initialize();
    }
    public boolean isWindowClosed(){
        return isWindowClosed;
    }
    public synchronized void setWindowClosed(boolean isClosed){
        isWindowClosed = isClosed;
        if(isWindowClosed){
            if(servicePaths != null){
                for(int i = servicePaths.size(); --i >= 0;){
                    ServiceManagerFactory.unloadManager((String)servicePaths.get(i));
                }
            }
            UserIdInputView.this.notifyAll();
        }
    }
    
    private void initialize() throws Exception {
        
        Font font = new Font("ＭＳ ゴシック", Font.BOLD, 16);
        
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout());
        
        this.textBox = new JTextField(15);
        this.textBox.addKeyListener(this);
        String user = System.getProperty("user.name");
        if(user != null){
            this.textBox.setText(user);
        }
        
        this.okButton = new JButton("OK");
        this.okButton.setFont(font);
        this.okButton.addActionListener(this);
        
        this.addWindowListener(
            new WindowAdapter(){
                public void windowClosing(WindowEvent e){
                    setWindowClosed(true);
                }
            }
        );
        
        JLabel label = new JLabel("ユーザID：");
        label.setFont(font);
        
        p.add(label);
        p.add(this.textBox);
        p.add(this.okButton);
        
        this.setTitle("ユーザIDの入力画面");
        this.setBounds(100, 100, 400, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        getContentPane().add(Box.createVerticalStrut(50), BorderLayout.NORTH); 
        getContentPane().add(p, BorderLayout.CENTER);
    }
    
    public static void main(String[] args) throws Exception{
        UserIdInputView view = new UserIdInputView(null);
        view.setVisible(true);
    }
    
    private void mainViewStartup(){

        this.setVisible(false);
        
        try {
            ScenarioTestView view = new ScenarioTestView(testController, this.textBox.getText());
            view.addWindowListener(
                new WindowAdapter(){
                    public void windowClosing(WindowEvent e){
                        setWindowClosed(true);
                    }
                }
            );
            view.setVisible(true);
            
        } catch (Exception e1) {
            JDialog dialog = new StatusDialogView(this, "Exception", e1);
            dialog.setModal(true);
            dialog.setVisible(true);
        }
    }

    /**
     * 「OK」ボタンが押下された時のイベント
     */
    public void actionPerformed(ActionEvent e) {
        mainViewStartup();
    }

    public void keyTyped(KeyEvent e) {
        // 処理なし
    }

    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            mainViewStartup();
        }
    }

    public void keyReleased(KeyEvent e) {
        // 処理なし
    }
}
