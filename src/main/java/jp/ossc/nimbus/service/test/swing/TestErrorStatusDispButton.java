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
        
        Font font = new Font("ＭＳ ゴシック", Font.BOLD, 16);
        Dimension dim = new Dimension(50, 20);
        setEnabled(false);

        setPreferredSize(dim);
        setFont(font);
        setFocusPainted(false);
        setMargin(new Insets(0,0,0,0));

        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        setHorizontalTextPosition(SwingConstants.CENTER);
        setVerticalTextPosition(SwingConstants.CENTER);
        change(null);
    }

    public void change(StatusActionMnager status) {
        
        this.status = status;
        
        if (status != null && status.getResult()) {
            setText("OK");
            setBackground(NORMAL_COLOR);
            setEnabled(true);
            
            removeActionListener(this);
            addActionListener(this);
        } else if (status != null && !status.getResult()) {
            setText("NG");
            setBackground(NG_COLOR);
            setEnabled(true);
            
            removeActionListener(this);
            addActionListener(this);
        }else{
            setText("-");
            setBackground(NON_COLOR);
            setEnabled(false);
        }
    }

    public void actionPerformed(ActionEvent e) {
        JDialog dialog = new StatusDialogView(ownerFrame, "状態", status);
        dialog.setVisible(true);
    }

}
