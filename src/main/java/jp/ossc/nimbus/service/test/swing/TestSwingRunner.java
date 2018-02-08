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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.*;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.test.TestController;
import jp.ossc.nimbus.service.test.TestReporter;
import jp.ossc.nimbus.beans.ServiceNameEditor;

/**
 * テスト実行。<p>
 * テストフレームワークを定義したサービス定義を読み込みサービスを起動し、テスト実行定義ファイルを読み込み、その内容に従って{@link TestController}に、シナリオグループ、シナリオ、テストケースの開始、終了を依頼する。また、テスト終了後に、{@link TestReporter}に依頼してレポートを出力する。<br>
 * 
 * @author M.Takata
 * @see <a href="TestRunnerUsage.txt">テスト実行コマンド使用方法</a>
 * @see <a href="testrunner_1_0.dtd">テスト実行定義ファイルDTD</a>
 */
public class TestSwingRunner{
    
    private static final String USAGE_RESOURCE
         = "jp/ossc/nimbus/service/test/TestSwingRunnerUsage.txt";
    
    
    private static void usage(){
        try{
            System.out.println(
                getResourceString(USAGE_RESOURCE)
            );
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    /**
     * リソースを文字列として読み込む。<p>
     *
     * @param name リソース名
     * @exception IOException リソースが存在しない場合
     */
    private static String getResourceString(String name) throws IOException{
        
        // リソースの入力ストリームを取得
        InputStream is = ServiceManagerFactory.class.getClassLoader()
            .getResourceAsStream(name);
        
        // メッセージの読み込み
        StringBuilder buf = new StringBuilder();
        BufferedReader reader = null;
        final String separator = System.getProperty("line.separator");
        try{
            reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while((line = reader.readLine()) != null){
                buf.append(line).append(separator);
            }
        }finally{
            if(reader != null){
                try{
                    reader.close();
                }catch(IOException e){
                }
            }
        }
        return unicodeConvert(buf.toString());
    }
    
    private static String unicodeConvert(String str){
        char c;
        int len = str.length();
        StringBuilder buf = new StringBuilder(len);
        
        for(int i = 0; i < len; ){
            c = str.charAt(i++);
            if(c == '\\' && i < len){
                c = str.charAt(i++);
                if(c == 'u'){
                    int startIndex = i;
                    int value = 0;
                    boolean isUnicode = true;
                    for(int j = 0; j < 4; j++){
                        if(i >= len){
                            isUnicode = false;
                            break;
                        }
                        c = str.charAt(i++);
                        switch(c){
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            value = (value << 4) + (c - '0');
                            break;
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                            value = (value << 4) + 10 + (c - 'a');
                            break;
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                            value = (value << 4) + 10 + (c - 'A');
                            break;
                        default:
                            isUnicode = false;
                            break;
                        }
                    }
                    if(isUnicode){
                        buf.append((char)value);
                    }else{
                        buf.append('\\').append('u');
                        i = startIndex;
                    }
                }else{
                    buf.append('\\').append(c);
                }
            }else{
                buf.append(c);
            }
        }
        return buf.toString();
    }
    
    
    
    public static void main(String[] args) throws Exception{
        
        if(args.length != 0 && args[0].equals("-help")){
            usage();
            return;
        }
        
        final List servicePaths = new ArrayList();
        boolean validate = false;
        
        for(int i = 0; i < args.length; i++){
            servicePaths.add(args[i]);
        }
        
        if(servicePaths.size() == 0){
            usage();
            return;
        }
        
        for(int i = 0, max = servicePaths.size(); i < max; i++){
            if(!ServiceManagerFactory.loadManager((String)servicePaths.get(i), false, validate)){
                System.exit(-1);
            }
        }
        if(!ServiceManagerFactory.checkLoadManagerCompleted()){
            System.exit(-1);
        }
        
        TestController testController = null;
        try{
            String controllerServiceNameStr = null;
            
            // コントローラーのサービス名は、とりあえずテストで固定値
            if(controllerServiceNameStr == null)
                controllerServiceNameStr = "Nimbus#TestController";
                
            final ServiceNameEditor editor = new ServiceNameEditor();
            editor.setAsText(controllerServiceNameStr);
            final ServiceName controllerServiceName = (ServiceName)editor.getValue();
            testController = (TestController)ServiceManagerFactory.getServiceObject(controllerServiceName);
            
        } catch(Exception e){
            ServiceManagerFactory.getLogger().write("TR___00004", e);
            System.exit(-1);
        }
        
        // GUI を起動
        final UserIdInputView view = new UserIdInputView(servicePaths);
        view.setTestController(testController);
        view.setVisible(true);
        view.addWindowListener(
            new WindowAdapter(){
                public void windowClosing(WindowEvent e){
                    view.setWindowClosed(true);
                }
            }
        );
        while(!view.isWindowClosed()){
            synchronized(view){
                view.wait(1000);
            }
        }
    }
}
