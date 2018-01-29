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
        this.setModal(true);
        
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
                for(Throwable ee = th.getCause(); ee != null; ee = ee.getCause()){
                    stringBuilder.append(lineSp).append("Caused by: ")
                        .append(ee).append(lineSp);
                    final StackTraceElement[] elems = ee.getStackTrace();
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
    

    public StatusDialogView(JFrame ownerFrame, String title, Exception throwObject) {
        super(ownerFrame);
        this.setModal(true);
        
        setTitle(title);
        setBounds(ownerFrame.getX()+100, ownerFrame.getY()+100, 700, 400);
        
        JTextArea area1 = new JTextArea();
        JScrollPane scrollpane1 = new JScrollPane(area1);
        
        Container contentPane = getContentPane();
        contentPane.add(scrollpane1, BorderLayout.CENTER);
        
        // Statusオブジェクトを表示
        StringBuilder stringBuilder = new StringBuilder();
        
        String lineSp = System.getProperty("line.separator");
        
        if(throwObject != null){
            
            stringBuilder.append(throwObject.toString() + lineSp);
            stringBuilder.append(lineSp);
            
            StackTraceElement[] stackTraceElementArray = throwObject.getStackTrace();
            
            for (int i=0; i<stackTraceElementArray.length; i++) {
                StackTraceElement stackTraceElement = stackTraceElementArray[i];
                stringBuilder.append(stackTraceElement.toString() + lineSp);
            }
            area1.setText(stringBuilder.toString());
        }
        area1.setCaretPosition(0);
    }

}
