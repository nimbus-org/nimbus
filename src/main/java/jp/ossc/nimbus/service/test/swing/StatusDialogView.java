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

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import jp.ossc.nimbus.service.test.StatusActionMnager;

import javax.swing.JScrollPane;
import java.awt.Container;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.awt.BorderLayout;

public class StatusDialogView extends JDialog {
    
    public StatusDialogView(JFrame ownerFrame, String title, StatusActionMnager statusObject) {
        super(ownerFrame);
        setModal(true);
        
        setTitle(title);
        setBounds(ownerFrame.getX()+100, ownerFrame.getY()+100, 700, 400);
        
        JTextArea area1 = new JTextArea();
        JScrollPane scrollpane1 = new JScrollPane(area1);
        
        Container contentPane = getContentPane();
        contentPane.add(scrollpane1, BorderLayout.CENTER);
        
        // Statusオブジェクトを表示
        StringBuilder stringBuilder = new StringBuilder();
        
        String lineSp = System.getProperty("line.separator");
        
        if(statusObject != null){
            stringBuilder.append("実行中Action：" + statusObject.getCurrentActionId() + lineSp);
            stringBuilder.append("全Actionの結果：" + lineSp);
            
            Map resultMap = statusObject.getActionResultMap();
            Set keys = resultMap.keySet();
            Iterator it = keys.iterator();
            while(it.hasNext()){
                String key = it.next().toString();
                String result = resultMap.get(key).toString();
                
                stringBuilder.append("  " + key + ":" + result + lineSp);
                
            }
            stringBuilder.append(lineSp);
            
            Throwable th = statusObject.getThrowable();
            
            if(th != null){
                stringBuilder.append("例外： ").append(th).append(lineSp);
                final StackTraceElement[] elemss = th.getStackTrace();
                if(elemss != null){
                    for(int i = 0, max = elemss.length; i < max; i++){
                        stringBuilder.append('\t');
                        stringBuilder.append(elemss[i]);
                        if(i != max - 1){
                            stringBuilder.append(lineSp);
                        }
                    }
                }
                for(Throwable cause = th.getCause(); cause != null; cause = cause.getCause()){
                    stringBuilder.append(lineSp).append("Caused by: ")
                        .append(cause).append(lineSp);
                    final StackTraceElement[] elems = cause.getStackTrace();
                    if(elems != null){
                        for(int i = 0, max = elems.length; i < max; i++){
                            stringBuilder.append('\t');
                            stringBuilder.append(elems[i]);
                            if(i != max - 1){
                                stringBuilder.append(lineSp);
                            }
                        }
                    }
                }
            }else{
                stringBuilder.append("例外：なし");
            }
            area1.setText(stringBuilder.toString());
        }
        
        area1.setCaretPosition(0);
        
        //scrollpane1.getHorizontalScrollBar().setValue(0);
        //scrollpane1.getVerticalScrollBar().setValue(0);
    }
    

    public StatusDialogView(JFrame ownerFrame, String title, Throwable throwObject) {
        super(ownerFrame);
        setModal(true);
        
        setTitle(title);
        setBounds(ownerFrame.getX()+100, ownerFrame.getY()+100, 700, 400);
        
        JTextArea area1 = new JTextArea();
        JScrollPane scrollpane1 = new JScrollPane(area1);
        
        Container contentPane = getContentPane();
        contentPane.add(scrollpane1, BorderLayout.CENTER);
        
        if(throwObject != null){
            StringBuilder buf = new StringBuilder();
            final String lineSeparator = System.getProperty("line.separator");
            buf.append("例外： ").append(throwObject.toString()).append(lineSeparator);
            final StackTraceElement[] elemss = throwObject.getStackTrace();
            if(elemss != null){
                for(int i = 0; i < elemss.length; i++){
                    buf.append('\t');
                    if(elemss[i] != null){
                        buf.append(elemss[i].toString()).append(lineSeparator);
                    }else{
                        buf.append("null").append(lineSeparator);
                    }
                }
            }
            for(Throwable cause = throwObject.getCause(); cause != null; cause = cause.getCause()){
                buf.append("Caused by:").append(cause.toString()).append(lineSeparator);
                final StackTraceElement[] elems = cause.getStackTrace();
                if(elems != null){
                    for(int i = 0; i < elems.length; i++){
                        buf.append('\t');
                        if(elems[i] != null){
                            buf.append(elems[i].toString()).append(lineSeparator);
                        }else{
                            buf.append("null").append(lineSeparator);
                        }
                    }
                }
            }
            area1.setText(buf.toString());
        }
        // Statusオブジェクトを表示
        area1.setCaretPosition(0);
    }

}
