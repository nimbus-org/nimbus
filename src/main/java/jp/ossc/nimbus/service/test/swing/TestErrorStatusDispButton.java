package jp.ossc.nimbus.service.test.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingConstants;

import jp.ossc.nimbus.service.test.StatusActionMnager;

public class TestErrorStatusDispButton extends JButton implements ActionListener {

    private static final Color NON_COLOR = Color.LIGHT_GRAY;
    private static final Color NORMAL_COLOR = new Color(100, 150, 255);
    private static final Color NG_COLOR = new Color(255, 75, 75);

    private StatusActionMnager status = null;

    private JFrame ownerFrame = null;
    
    
    public TestErrorStatusDispButton(JFrame ownerFrame) {
        this.ownerFrame = ownerFrame;
        
        Font font = new Font("ÇlÇr ÉSÉVÉbÉN", Font.BOLD, 16);
        Dimension dim = new Dimension(50, 20);
        this.setEnabled(false);

        this.setPreferredSize(dim);
        this.setFont(font);
        this.setFocusPainted(false);
        this.setMargin(new Insets(0,0,0,0));

        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.setVerticalAlignment(SwingConstants.CENTER);
        this.setHorizontalTextPosition(SwingConstants.CENTER);
        this.setVerticalTextPosition(SwingConstants.CENTER);
        this.change(null);
    }

    public void change(StatusActionMnager status) {
        
        this.status = status;
        
        if (status != null && status.getResult()) {
            this.setText("OK");
            this.setBackground(NORMAL_COLOR);
            this.setEnabled(true);
        } else if (status != null && !status.getResult()) {
            this.setText("NG");
            this.setBackground(NG_COLOR);
            this.setEnabled(true);
            
            this.removeActionListener(this);
            this.addActionListener(this);
        }else{
            this.setText("-");
            this.setBackground(NON_COLOR);
            this.setEnabled(false);
        }
    }

    public void actionPerformed(ActionEvent e) {
        JDialog dialog = new StatusDialogView(this.ownerFrame, "èÛë‘", this.status);
        dialog.setVisible(true);
    }

}
