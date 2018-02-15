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
