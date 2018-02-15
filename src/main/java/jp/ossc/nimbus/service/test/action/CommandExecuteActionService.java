package jp.ossc.nimbus.service.test.action;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.Utility;
import jp.ossc.nimbus.io.CSVReader;
import jp.ossc.nimbus.service.test.TestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;
import jp.ossc.nimbus.service.test.TestContext;

/**
 * コマンドを実行するテストアクション。<p>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 * 
 * @author T.Takakura
 */
public class CommandExecuteActionService extends ServiceBase implements TestAction,TestActionEstimation, CommandExecuteActionServiceMBean {
    
    private static final long serialVersionUID = -5250176082027977563L;
    
    protected String[] environments;
    protected double expectedCost = 0d;
    protected long checkInterval = 1000l;
    
    public String[] getEnvironments() {
        return environments;
    }
    public void setEnvironments(String[] envs) {
        for(int i = 0; i < envs.length; i++){
            int index = envs[i].indexOf('=');
            if(index == -1){
                throw new IllegalArgumentException("Illegal format : " + envs[i]);
            }
        }
        this.environments = envs;
    }
    
    public void setCheckInterval(long interval){
        checkInterval = interval;
    }
    public long getCheckInterval(){
        return checkInterval;
    }
    
    public void setExpectedCost(double cost) {
        expectedCost = cost;
    }
    public double getExpectedCost() {
        return expectedCost;
    }
    
    /**
     * コマンドを実行する。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * commands
     * environments
     * 
     * workDir
     * timeout
     * logFile
     * waitPattern
     * </pre>
     * commandsは、コマンド及び引数をスペース区切りで指定する。<br>
     * environmentsは、コマンド実行時に適用する環境変数を変数名=値で指定する。複数指定する場合は、改行して指定する。終了は、空行を指定する。<br>
     * workDirは、コマンドの作業ディレクトリを指定する。<br>
     * timeoutは、コマンドの終了待ちタイムアウトを指定する。指定しない場合は、終了待ちしない。<br>
     * logFileは、コマンドの終了待ちを、ログファイルの出力で行う場合の、ログファイルパスを指定する。また、ファイルの文字コードを指定する場合は、カンマ区切りで指定する。ログファイルを指定しない場合は、プロセスの終了待ちを行う。<br>
     * waitPatternは、コマンドの終了待ちを、ログファイルの出力内容で行う場合の、出力内容の正規表現を指定する。この正規表現に一致する出力が見られた場合に、終了待ちを終える。指定しない場合は、ログファイルの作成待ちを行う。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return コマンドの終了待ちをした場合は、exitコード。
     */
    public Object execute(TestContext context, String actionId, Reader resource) throws Exception{
        BufferedReader br = new BufferedReader(resource);
        File dir = null;
        String command = null;
        String[] cmdarray = null;
        String[] envp = null;
        long waitTime = 0;
        File logFile = null;
        String fileEncoding = null;
        Pattern waitPattern = null;
        try{
            String str;
            //コマンド名と引数
            if ((str = br.readLine()) != null && str.length() != 0) {
                command = str;
                cmdarray = CSVReader.toArray(replaceProperty(str), ' ', '\\', '"', null, null, false, false, true, true);
            } else {
                throw new Exception("ファイルフォーマットが想定外です。");
            }
            Map tmpEnv = new LinkedHashMap();

            tmpEnv.putAll(System.getenv());

            if(environments != null){
                for(int i = 0; i < environments.length; i++){
                    int index = environments[i].indexOf('=');
                    tmpEnv.put(environments[i].substring(0, index), environments[i].substring(index + 1));
                }
                
            }
            //環境変数
            while((str = br.readLine()) != null && str.length() != 0){
                int index = str.indexOf('=');
                if(index == -1){
                    throw new IllegalArgumentException("Illegal format : " + str);
                }
                tmpEnv.put(str.substring(0, index), str.substring(index + 1));
            }
            if(tmpEnv.size() != 0){
                envp = new String[tmpEnv.size()];
                Iterator entries = tmpEnv.entrySet().iterator();
                int index = 0;
                while(entries.hasNext()){
                    Map.Entry entry = (Map.Entry)entries.next();
                    envp[index++] = (String)entry.getKey() + '=' + (String)entry.getValue();
                }
            }
            //作業ディレクトリ
            if ((str = br.readLine()) != null && str.length() != 0) {
                String workDirStr = replaceProperty(str);
                if(".".equals(workDirStr)){
                    dir = context.getCurrentDirectory();
                }else{
                    dir = new File(workDirStr);
                }
            }
            //タイムアウト
            if ((str = br.readLine()) != null && str.length() != 0) {
                waitTime = Long.parseLong(str);
            }
            //ログファイル
            if ((str = br.readLine()) != null && str.length() != 0) {
                String logFilePath = replaceProperty(str);
                final int index = logFilePath.indexOf(',');
                if(index != -1){
                    fileEncoding = logFilePath.substring(index + 1);
                    logFilePath = logFilePath.substring(0, index);
                }
                logFile = new File(logFilePath);
            }
            //終了待ち正規表現
            if ((str = br.readLine()) != null && str.length() != 0) {
                waitPattern = Pattern.compile(str);
            }
        } catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                br.close();
            }catch(IOException e){}
        }
        final Runtime r = Runtime.getRuntime();
        final Process process = r.exec(cmdarray, envp, dir);
        Integer ret = null;
        boolean isTimeout = false;;
        if (waitTime > 0) {
            ProcessStreamReadRunnable stdReadRunnable = new ProcessStreamReadRunnable(process.getInputStream());
            ProcessStreamReadRunnable errReadRunnable = new ProcessStreamReadRunnable(process.getErrorStream());
            Thread stdReadThread = new Thread(stdReadRunnable);
            Thread errReadThread = new Thread(errReadRunnable);
            stdReadThread.start();
            errReadThread.start();
            if(logFile == null){
                ProcessWaitRunnable waitRunnable = new ProcessWaitRunnable(process);
                Thread processWaitThread = new Thread(waitRunnable);
                processWaitThread.setDaemon(true);
                processWaitThread.start();
                processWaitThread.join(waitTime);
                if(waitRunnable.exitCode == null){
                    processWaitThread.interrupt();
                    stdReadThread.interrupt();
                    errReadThread.interrupt();
                    isTimeout = true;
                }else{
                    stdReadThread.join();
                    errReadThread.join();
                    ret = waitRunnable.exitCode; //戻り値
                }
            }else{
                isTimeout = true;
                do{
                    long sleepTime = Math.min(checkInterval, waitTime);
                    Thread.sleep(sleepTime);
                    waitTime -= sleepTime;
                    if(logFile.exists()){
                        if(waitPattern == null){
                            isTimeout = false;
                            break;
                        }else{
                            StringWriter sw = new StringWriter();
                            InputStreamReader isr = fileEncoding == null ? new InputStreamReader(new FileInputStream(logFile)) : new InputStreamReader(new FileInputStream(logFile), fileEncoding);
                            char[] buf = new char[1024];
                            int len = 0;
                            String fileContent = null;
                            try{
                                while((len = isr.read(buf, 0 , buf.length)) > 0){
                                    sw.write(buf, 0, len);
                                }
                                fileContent = sw.toString();
                                sw.close();
                                sw = null;
                            }finally{
                                isr.close();
                                isr = null;
                            }
                            if(waitPattern.matcher(fileContent).find()){
                                isTimeout = false;
                                break;
                            }
                        }
                    }
                }while(waitTime > 0);
                stdReadThread.interrupt();
                errReadThread.interrupt();
            }
            
            BufferedReader brOut = null;
            BufferedReader brErrer = null;
            PrintWriter pw = null;
            try {
                File outFile = new File(context.getCurrentDirectory(), actionId + ".txt");
                FileWriter filewriter = new FileWriter(outFile);
                BufferedWriter bw = new BufferedWriter(filewriter);
                pw = new PrintWriter(bw);
                
                pw.println("標準出力：");
                pw.println(stdReadRunnable.getResult() == null ? stdReadRunnable.getCurrentResult() : stdReadRunnable.getResult());
                pw.println();
                pw.println("エラー出力：");
                pw.println(errReadRunnable.getResult() == null ? errReadRunnable.getCurrentResult() : errReadRunnable.getResult());
                pw.println();
                pw.println("戻り値：");
                pw.println(ret);
                pw.flush();
            } finally {
                if (brOut != null)
                    brOut.close();
                if (brErrer != null)
                    brErrer.close();
                if (pw != null)
                    pw.close();
            }
            if(isTimeout){
                throw new Exception("Comannd execute timeout : command=" + command);
            }
        }
        return ret;
    }
    
    protected String replaceProperty(String textValue){
        
        // システムプロパティの置換
        textValue = Utility.replaceSystemProperty(textValue);
        
        // サービスローダ構成プロパティの置換
        if(getServiceLoader() != null){
            textValue = Utility.replaceServiceLoderConfig(
                textValue,
                getServiceLoader().getConfig()
            );
        }
        
        // マネージャプロパティの置換
        if(getServiceManager() != null){
            textValue = Utility.replaceManagerProperty(
                getServiceManager(),
                textValue
            );
        }
        
        // サーバプロパティの置換
        textValue = Utility.replaceServerProperty(textValue);
        
        return textValue;
    }
    
    protected class ProcessWaitRunnable implements Runnable{
        private final Process process;
        public Integer exitCode = null;
        public ProcessWaitRunnable(Process process){
            this.process = process;
        }
        public void run(){
            try {
                exitCode = new Integer(process.waitFor());
            } catch (InterruptedException e) {
            }
        }
    }
    
    protected class ProcessStreamReadRunnable implements Runnable{
        
        private BufferedReader br;
        private String result;
        private StringWriter sw = new StringWriter();
        private PrintWriter pw = new PrintWriter(sw);
        
        public ProcessStreamReadRunnable(InputStream is){
            br = new BufferedReader(new InputStreamReader(is));
        }
        
        public void run(){
            try{
                String line = null;
                while((line = br.readLine()) != null){
                    pw.println(line);
                }
            }catch(IOException e){
            }finally{
                pw.flush();
                result = sw.toString();
                try{
                    br.close();
                    br = null;
                }catch(IOException e){}
            }
        }
        
        public String getCurrentResult(){
            pw.flush();
            return sw.toString();
        }
        
        public String getResult(){
            return result;
        }
    }
}
