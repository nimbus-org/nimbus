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
package jp.ossc.nimbus.service.scheduler2;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import jp.ossc.nimbus.core.Utility;
import jp.ossc.nimbus.io.CSVReader;

/**
 * コマンドスケジュール実行。<p>
 * 実行を依頼されたタスクをコマンドとして実行する。<br>
 * スケジュールの入力のフォーマットは、以下。<br>
 * <pre>
 * commands
 * environments
 * 
 * workDir
 * timeout
 * logFile
 * waitPattern
 * </pre>
 * commandsは、コマンド及び引数をCSV形式で指定する。CSV形式の最後の要素に、"&"を指定すると、プロセスの終了を待機しない。また、コマンドは、スケジュールのタスク名が指定されている場合は、それを実行するコマンドとみなす。<br>
 * environmentsは、コマンド実行時に適用する環境変数を変数名=値で指定する。複数指定する場合は、改行して指定する。終了は、空行を指定する。<br>
 * workDirは、コマンドの作業ディレクトリを指定する。<br>
 * timeoutは、コマンドの終了待ちタイムアウトを指定する。指定しない場合は、終了待ちしない。<br>
 * logFileは、コマンドの終了待ちを、ログファイルの出力で行う場合の、ログファイルパスを指定する。また、ファイルの文字コードを指定する場合は、カンマ区切りで指定する。ログファイルを指定しない場合は、プロセスの終了待ちを行う。<br>
 * waitPatternは、コマンドの終了待ちを、ログファイルの出力内容で行う場合の、出力内容の正規表現を指定する。この正規表現に一致する出力が見られた場合に、終了待ちを終える。指定しない場合は、ログファイルの作成待ちを行う。<br>
 * また、スケジュールの入力をJSON形式にする場合のフォーマットは、以下。<br>
 * <pre>
 * {
 *     "commands":["コマンドまたは引数","コマンドまたは引数",...],
 *     "environments":{"変数名":"値","変数名":"値",...},
 *     "workDir":"コマンドの作業ディレクトリ",
 *     "waitTime":1000,
 *     "logFile":{"file":"ログファイルパス", "encoding":"ファイルの文字コード"},
 *     "waitPattern":"出力内容の正規表現"
 * }
 * </pre>
 *
 * @author M.Takata
 */
public class CommandScheduleExecutorService
 extends AbstractScheduleExecutorService
 implements CommandScheduleExecutorServiceMBean{
    
    private static final long serialVersionUID = -3986953305707553467L;
    
    protected String workDirectoryPath;
    protected String[] environmentVariables;
    protected long checkInterval = 1000l;
    protected Map threadMap;
    
    {
        type = DEFAULT_EXECUTOR_TYPE;
    }
    
    public void setWorkDirectory(String path){
        workDirectoryPath = path;
    }
    public String getWorkDirectory(){
        return workDirectoryPath;
    }
    
    public void setEnvironmentVariables(String[] env){
        environmentVariables = env;
    }
    public String[] getEnvironmentVariables(){
        return environmentVariables;
    }
    
    public void setCheckInterval(long interval){
        checkInterval = interval;
    }
    public long getCheckInterval(){
        return checkInterval;
    }
    
    public void createService() throws Exception{
        threadMap = Collections.synchronizedMap(new HashMap());
    }
    
    public void destroyService() throws Exception{
        threadMap = null;
    }
    
    protected void checkPreExecute(Schedule schedule) throws Exception{
        Object input = schedule.getInput();
        if(input == null){
            throw new IllegalArgumentException("Command is null.");
        }else if(input instanceof String){
            if(((String)input).length() == 0){
                throw new IllegalArgumentException("Command is empty.");
            }
        }else if(input instanceof Map){
            if(((Map)input).size() == 0){
                throw new IllegalArgumentException("Command is empty.");
            }
        }else{
            throw new IllegalArgumentException("Input is not String. type=" + input.getClass().getName());
        }
    }
    
    protected Schedule executeInternal(Schedule schedule) throws Throwable{
        threadMap.put(schedule.getId(), Thread.currentThread());
        try{
            Object input = schedule.getInput();
            
            BufferedReader br = null;
            String line = null;
            
            String[] commands = null;
            boolean isWait = true;
            if(input instanceof String){
                br = new BufferedReader(new StringReader((String)input));
                line = br.readLine();
                commands = CSVReader.toArray(
                    replaceProperty(line),
                    ',',
                    '\\',
                    '"',
                    "",
                    null,
                    true,
                    false,
                    true,
                    false
                );
            }else{
                List commandList = (List)((Map)input).get("commands");
                commands = commandList == null ? new String[0] : (String[])commandList.toArray(new String[commandList.size()]);
                for(int i = 0; i < commands.length; i++){
                    commands[i] = replaceProperty(commands[i]);
                }
            }
            if(commands.length > 1 && "&".equals(commands[commands.length - 1])){
                isWait = false;
                String[] tmpCommands = new String[commands.length - 1];
                System.arraycopy(commands, 0, tmpCommands, 0, tmpCommands.length);
                commands = tmpCommands;
            }
            if(schedule.getTaskName() != null && schedule.getTaskName().length() != 0){
                String[] tmpCommands = new String[commands.length + 1];
                tmpCommands[0] = schedule.getTaskName();
                System.arraycopy(commands, 0, tmpCommands, 1, commands.length);
                commands = tmpCommands;
            }
            
            //環境変数
            Map tmpEnv = new LinkedHashMap();
            tmpEnv.putAll(System.getenv());
            if(environmentVariables != null){
                for(int i = 0; i < environmentVariables.length; i++){
                    int index = environmentVariables[i].indexOf('=');
                    tmpEnv.put(environmentVariables[i].substring(0, index), environmentVariables[i].substring(index + 1));
                }
            }
            if(input instanceof String){
                while((line = br.readLine()) != null && line.length() != 0){
                    int index = line.indexOf('=');
                    if(index == -1){
                        throw new IllegalArgumentException("Illegal format : " + line);
                    }
                    tmpEnv.put(line.substring(0, index), replaceProperty(line.substring(index + 1)));
                }
            }else{
                Map environmentsMap = (Map)((Map)input).get("environments");
                if(environmentsMap != null){
                    Iterator entries = environmentsMap.entrySet().iterator();
                    while(entries.hasNext()){
                        Map.Entry entry = (Map.Entry)entries.next();
                        tmpEnv.put(entry.getKey(), replaceProperty((String)entry.getValue()));
                    }
                }
            }
            String[] envp = null;
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
            File workDir = workDirectoryPath == null ? null : new File(workDirectoryPath);
            String workDirStr = null;
            if(input instanceof String){
                if((line = br.readLine()) != null && line.length() != 0){
                    workDirStr = line;
                }
            }else{
                workDirStr = (String)((Map)input).get("workDir");
            }
            if(workDirStr != null){
                workDir = new File(replaceProperty(workDirStr));
            }
            
            //タイムアウト
            long waitTime = -1;
            if(input instanceof String){
                if((line = br.readLine()) != null && line.length() != 0){
                    waitTime = Long.parseLong(line);
                }
            }else{
                Number waitTimeNum = (Number)((Map)input).get("waitTime");
                if(waitTimeNum != null){
                    waitTime = waitTimeNum.longValue();
                }
            }
            
            //ログファイル
            String fileEncoding = null;
            File logFile = null;
            if(input instanceof String){
                if((line = br.readLine()) != null && line.length() != 0){
                    String logFilePath = replaceProperty(line);
                    final int index = logFilePath.indexOf(',');
                    if(index != -1){
                        fileEncoding = logFilePath.substring(index + 1);
                        logFilePath = logFilePath.substring(0, index);
                    }
                    logFile = new File(logFilePath);
                }
            }else{
                Map logFileMap = (Map)((Map)input).get("logFile");
                if(logFileMap != null){
                    fileEncoding = (String)logFileMap.get("encoding");
                    if(fileEncoding != null){
                        fileEncoding = replaceProperty(fileEncoding);
                    }
                    logFile = new File(replaceProperty((String)logFileMap.get("file")));
                }
            }
            
            //終了待ち正規表現
            String waitPatternStr = null;
            Pattern waitPattern = null;
            if(input instanceof String){
                if((line = br.readLine()) != null && line.length() != 0){
                    waitPatternStr = line;
                }
            }else{
                waitPatternStr = (String)((Map)input).get("waitPattern");
            }
            if(waitPatternStr != null){
                waitPattern = Pattern.compile(waitPatternStr);
            }
            
            final Runtime r = Runtime.getRuntime();
            final Process process = r.exec(commands, envp, workDir);
            int pid = getUnixPid(process);
            int exitCode = 0;
            boolean isTimeout = false;;
            if(waitTime > 0){
                ProcessStreamReadRunnable stdReadRunnable = new ProcessStreamReadRunnable(process.getInputStream());
                ProcessStreamReadRunnable errReadRunnable = new ProcessStreamReadRunnable(process.getErrorStream());
                Thread stdReadThread = new Thread(stdReadRunnable);
                Thread errReadThread = new Thread(errReadRunnable);
                stdReadThread.start();
                errReadThread.start();
                try{
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
                            exitCode = waitRunnable.exitCode.intValue(); //戻り値
                        }
                        stdReadThread = null;
                        errReadThread = null;
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
                    }
                }catch(InterruptedException e){
                    schedule.setState(Schedule.STATE_FAILED);
                    schedule.setOutput("Waiting for a response of command, it is interrupted. pid=" + (pid == -1 ? "unknown" : Integer.toString(pid)));
                    return schedule;
                }finally{
                    if(stdReadThread != null){
                        stdReadThread.interrupt();
                    }
                    if(errReadThread != null){
                        errReadThread.interrupt();
                    }
                }
                StringBuilder buf = new StringBuilder();
                if(isTimeout){
                    schedule.setState(Schedule.STATE_FAILED);
                    buf.append("Waiting for a response of command, it is timeout. pid=" + (pid == -1 ? "unknown" : Integer.toString(pid)));
                }else{
                    if(exitCode != 0){
                        schedule.setState(Schedule.STATE_FAILED);
                    }
                    buf.append("ExitCode=").append(exitCode);
                }
                buf.append(", err=").append(errReadRunnable.getResult() == null ? errReadRunnable.getCurrentResult() : errReadRunnable.getResult());
                buf.append(", std=").append(stdReadRunnable.getResult() == null ? stdReadRunnable.getCurrentResult() : stdReadRunnable.getResult());
                schedule.setOutput(buf.toString());
            }
        }finally{
            threadMap.remove(schedule.getId());
        }
        return schedule;
    }
    
    public boolean controlState(String id, int cntrolState)
     throws ScheduleStateControlException{
        if(cntrolState == Schedule.CONTROL_STATE_ABORT){
            Thread thread = (Thread)threadMap.get(id);
            if(thread != null){
                thread.interrupt();
                return true;
            }
        }
        return false;
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
    
    private int getUnixPid(Process process){
        int pid = -1;
        if(process.getClass().getName().equals("java.lang.UNIXProcess")){
            try{
                java.lang.reflect.Field f = process.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                pid = f.getInt(process);
            }catch(Throwable e){
            }
        }
        return pid;
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