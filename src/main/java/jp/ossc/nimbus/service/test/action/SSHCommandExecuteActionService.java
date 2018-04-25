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
package jp.ossc.nimbus.service.test.action;

import java.io.*;
import java.util.*;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.service.test.TestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;
import jp.ossc.nimbus.service.test.TestContext;

/**
 * SSHコマンドを実行するテストアクション。<p>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 * 
 * @author M.Takata
 */
public class SSHCommandExecuteActionService extends ServiceBase implements TestAction, TestActionEstimation, SSHCommandExecuteActionServiceMBean{
    
    private static final long serialVersionUID = -1763457363374423240L;
    
    protected String ptyType;
    protected boolean isPty = true;
    protected boolean isXForwarding;
    
    protected String[] environments;
    
    protected double expectedCost = Double.NaN;
    protected long checkInterval = 1000l;
    
    protected int sessionTimeout = -1;
    protected int serverAliveInterval = -1;
    protected int serverAliveCountMax = -1;
    
    protected String hostName;
    protected int port = -1;
    
    protected String userName;
    protected String password;
    protected File pemFile;
    
    protected Properties configProperties;
    protected String encoding;
    
    public void setPtyType(String type){
        ptyType = type;
    }
    public String getPtyType(){
        return ptyType;
    }
    
    public void setPty(boolean pty){
        isPty = pty;
    }
    public boolean isPty(){
        return isPty;
    }
    
    public void setXForwarding(boolean forwarding){
        isXForwarding = forwarding;
    }
    public boolean isXForwarding(){
        return isXForwarding;
    }
    
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
    
    public void setSessionTimeout(int timeout){
        this.sessionTimeout = timeout;
    }
    public int getSessionTimeout(){
        return sessionTimeout;
    }
    
    public void setServerAliveInterval(int interval){
        serverAliveInterval = interval;
    }
    public int getServerAliveInterval(){
        return serverAliveInterval;
    }
    
    public void setServerAliveCountMax(int count){
        serverAliveCountMax = count;
    }
    public int getServerAliveCountMax(){
        return serverAliveCountMax;
    }
    
    public void setHostName(String addr){
        hostName = addr;
    }
    public String getHostName(){
        return hostName;
    }
    
    public void setPort(int port){
        this.port = port;
    }
    public int getPort(){
        return port;
    }
    
    public void setUserName(String name){
        userName = name;
    }
    public String getUserName(){
        return userName;
    }
    
    public void setPassword(String password){
        this.password = password;
    }
    public String getPassword(){
        return password;
    }
    
    public void setPemFile(File path){
        this.pemFile = path;
    }
    public File getPemFile(){
        return pemFile;
    }
    
    public void setConfig(Properties conf){
        configProperties = conf;
    }
    public Properties getConfig(){
        return configProperties;
    }
    
    public void setCheckInterval(long interval){
        checkInterval = interval;
    }
    public long getCheckInterval(){
        return checkInterval;
    }
    
    public void setEncoding(String encoding){
        this.encoding = encoding;
    }
    public String getEncoding(){
        return encoding;
    }
    
    public void setExpectedCost(double cost){
        expectedCost = cost;
    }
    public double getExpectedCost(){
        return expectedCost;
    }
    
    /**
     * コマンドを実行する。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * command
     * 
     * 
     * environments
     * 
     * timeout
     * </pre>
     * commandは、実行するコマンドを指定する。終了は、空行を2行挟む。<br>
     * environmentsは、コマンド実行時に適用する環境変数を変数名=値で指定する。複数指定する場合は、改行して指定する。終了は、空行を指定する。<br>
     * timeoutは、コマンドの終了待ちタイムアウトを指定する。指定しない場合は、終了待ちしない。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return コマンドの終了待ちをした場合は、exitコード。
     */
    public Object execute(TestContext context, String actionId, Reader resource) throws Exception{
        BufferedReader br = new BufferedReader(resource);
        StringWriter command = new StringWriter();
        PrintWriter pw = new PrintWriter(command);
        Map envs = new LinkedHashMap();
        long timeout = 0;
        try{
            
            // コマンド
            String line = null;
            String preLine = null;
            while((line = br.readLine()) != null
                && (line.length() != 0 || preLine == null || preLine.length() != 0)
            ){
                pw.println(line);
                preLine = line;
            }
            pw.flush();
            
            //環境変数
            if(environments != null){
                for(int i = 0; i < environments.length; i++){
                    int index = environments[i].indexOf('=');
                    envs.put(environments[i].substring(0, index), environments[i].substring(index + 1));
                }
            }
            while((line = br.readLine()) != null && line.length() != 0){
                int index = line.indexOf('=');
                if(index == -1){
                    break;
                }
                envs.put(line.substring(0, index), line.substring(index + 1));
            }
            
            // タイムアウト
            if(line != null && line.length() == 0){
                line = br.readLine();
            }
            if(line != null && line.length() != 0){
                timeout = Long.parseLong(line.trim());
            }
        }finally{
            try{
                br.close();
            }catch(IOException e){}
            
            pw.close();
            pw = null;
        }
        
        JSch jsch = new JSch();
        if(pemFile != null){
            if(!pemFile.exists()){
                throw new Exception("PemFile don't exists. pemFile=" + pemFile);
            }
            jsch.addIdentity(pemFile.getPath(), password);
        }
        
        Session session = null;
        ChannelShell channel = null;
        try{
            session = jsch.getSession(userName, hostName, port);
            if(configProperties != null){
                session.setConfig(configProperties);
            }
            if(sessionTimeout >= 0){
                session.setTimeout(sessionTimeout);
            }
            if(serverAliveInterval >= 0){
                session.setServerAliveInterval(serverAliveInterval);
            }
            if(serverAliveCountMax >= 0){
                session.setServerAliveCountMax(serverAliveCountMax);
            }
            if(pemFile == null && password != null){
                session.setPassword(password);
            }
            session.connect();
            channel = (ChannelShell)session.openChannel("shell");
            if(envs.size() != 0){
                Iterator entries = envs.entrySet().iterator();
                while(entries.hasNext()){
                    Map.Entry entry = (Map.Entry)entries.next();
                    channel.setEnv((String)entry.getKey(), (String)entry.getValue());
                }
            }
            if(ptyType != null){
                channel.setPtyType(ptyType);
            }
            
            channel.setPty(isPty);
            channel.setXForwarding(isXForwarding);
            channel.connect();
            
            final long startTime = System.currentTimeMillis();
            PrintStream ps = new PrintStream(channel.getOutputStream());
            ps.println(command.toString());
            ps.println("exit");
            ps.flush();
            
            int exitStatus = -1;
            if(timeout > 0){
                File outFile = new File(context.getCurrentDirectory(), actionId + ".txt");
                pw = new PrintWriter(
                    encoding == null ? new FileWriter(outFile) : new OutputStreamWriter(new FileOutputStream(outFile), encoding)
                );
                InputStream is = channel.getInputStream();
                byte[] bytes = new byte[1024];
                long remainingTime = timeout;
                try{
                    while(remainingTime > 0){
                        int length = 0;
                        String output = null;
                        while(is.available() > 0 && (length = is.read(bytes, 0, bytes.length)) != -1){
                            output = encoding == null ? new String(bytes, 0, length) : new String(bytes, 0, length, encoding);
                            pw.print(output);
                            pw.flush();
                        }
                        
                        if(channel.isClosed() || (output != null && output.contains("logout"))){
                            break;
                        }
                        Thread.sleep(remainingTime > checkInterval ? checkInterval : remainingTime);
                        remainingTime = timeout - (System.currentTimeMillis() - startTime);
                    }
                    if(remainingTime <= 0){
                        throw new Exception("Comannd execute timeout : elapsed=" + (System.currentTimeMillis() - startTime));
                    }
                    exitStatus = channel.getExitStatus();
                    pw.println();
                    pw.println("Exit code：");
                    pw.println(exitStatus);
                }finally{
                    pw.flush();
                }
            }
            
            return timeout > 0 ? new Integer(exitStatus) : null;
        }finally{
            if(pw != null){
                pw.close();
            }
            if(channel != null){
                channel.disconnect();
                channel = null;
            }
            if(session != null){
                session.disconnect();
                session = null;
            }
        }
    }
}