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
import com.jcraft.jsch.ChannelExec;

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
    
    private static final long serialVersionUID = -1L;
    
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
     * command
     * 
     * 
     * timeout
     * </pre>
     * commandは、実行するコマンドを指定する。複数指定する場合は、空行を1行挟む。終了は、空行を2行挟む。<br>
     * timeoutは、コマンドの終了待ちタイムアウトを指定する。指定しない場合は、終了待ちしない。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return コマンドの終了待ちをした場合は、exitコード。
     */
    public Object execute(TestContext context, String actionId, Reader resource) throws Exception{
        BufferedReader br = new BufferedReader(resource);
        List commands = new ArrayList();
        long timeout = 0;
        try{
            String str = null;
            
            // コマンド
            StringBuilder buf = new StringBuilder();
            while((str = br.readLine()) != null){
                if(str.length () == 0){
                    if(buf.length() != 0){
                        commands.add(buf.toString());
                        buf.setLength(0);
                    }else{
                        break;
                    }
                }else{
                    buf.append(str);
                }
            }
            if(buf.length() != 0){
                commands.add(buf.toString());
            }
            buf = null;
            
            // タイムアウト
            if(str != null){
                str = br.readLine();
                if(str != null && str.length() != 0){
                    timeout = Long.parseLong(str.trim());
                }
            }
        }finally{
            try{
                br.close();
            }catch(IOException e){}
        }
        
        JSch jsch = new JSch();
        if(pemFile != null){
            if(!pemFile.exists()){
                throw new Exception("PemFile don't exists. pemFile=" + pemFile);
            }
            jsch.addIdentity(pemFile.getPath(), password);
        }
        
        Session session = null;
        ChannelExec channel = null;
        PrintWriter pw = null;
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
            
            File outFile = new File(context.getCurrentDirectory(), actionId + ".txt");
            FileWriter filewriter = new FileWriter(outFile);
            BufferedWriter bw = new BufferedWriter(filewriter);
            pw = new PrintWriter(bw);
            
            ByteArrayOutputStream stdos = new ByteArrayOutputStream();
            ByteArrayOutputStream erros = new ByteArrayOutputStream();
            long remainingTime = timeout;
            final long startTime = System.currentTimeMillis();
            int exitStatus = -1;
            for(int i = 0, imax = commands.size(); i < imax; i++){
                pw.println("Command：");
                pw.println(commands.get(i));
                pw.println();
                try{
                    stdos.reset();
                    erros.reset();
                    exitStatus = -1;
                    exitStatus = executeCommnad(
                        session,
                        (String)commands.get(i),
                        stdos,
                        erros,
                        remainingTime
                    );
                }finally{
                    if(timeout > 0){
                        pw.println("Standard output：");
                        String output = encoding == null ? new String(stdos.toByteArray()) : new String(stdos.toByteArray(), encoding);
                        if(output.length() != 0){
                            pw.print(output);
                        }
                        pw.println();
                        pw.println("Error output：");
                        output = encoding == null ? new String(erros.toByteArray()) : new String(erros.toByteArray(), encoding);
                        if(output.length() != 0){
                            pw.print(output);
                        }
                        pw.println();
                        pw.println("Exit code：");
                        pw.println(exitStatus);
                        if(i != imax - 1){
                            pw.println();
                            pw.println();
                        }
                    }
                }
                if(timeout > 0){
                    if(exitStatus != 0){
                        throw new Exception("Comannd execute error exit : command=" + commands.get(i) + ", exitCode=" + exitStatus);
                    }
                    remainingTime = timeout - (System.currentTimeMillis() - startTime);
                    if(remainingTime <= 0 && i != imax - 1){
                        throw new Exception("Comannd execute timeout : remainingCommands=" + commands.subList(i + 1, commands.size()));
                    }
                }
            }
            
            pw.flush();
            
            return timeout > 0 ? new Integer(exitStatus) : null;
        }finally{
            if(pw != null){
                pw.close();
            }
            if(session != null){
                session.disconnect();
                session = null;
            }
        }
    }
    
    protected int executeCommnad(Session session, String command, OutputStream stdos, OutputStream erros, long timeout) throws Exception{
        ChannelExec channel = null;
        InputStream is = null;
        try{
            channel = (ChannelExec)session.openChannel("exec");
            is = channel.getInputStream();
            channel.setErrStream(erros);
            channel.setCommand(command);
            
            final long startTime = System.currentTimeMillis();
            channel.connect();
            
            byte[] bytes = new byte[1024];
            int exitStatus = -1;
            long remainingTime = timeout;
            boolean isTimeout = false;
            while(timeout > 0 && remainingTime > 0){
                int length = 0;
                while((length = is.read(bytes, 0, 1024)) != -1){
                    stdos.write(bytes, 0, length);
                }
                
                exitStatus = channel.getExitStatus();
                if(exitStatus != -1 || channel.isClosed()){
                    break;
                }
                if(exitStatus == -1 && timeout > 0){
                    Thread.sleep(remainingTime > checkInterval ? checkInterval : remainingTime);
                    remainingTime = timeout - (System.currentTimeMillis() - startTime);
                    if(remainingTime <= 0){
                        isTimeout = true;
                    }
                }
            }
            if(isTimeout){
                throw new Exception("Comannd execute timeout : command=" + command + ", elapsed=" + (System.currentTimeMillis() - startTime));
            }
            return exitStatus;
        }finally{
            if(is != null){
                is.close();
                is = null;
            }
            if(channel != null){
                channel.disconnect();
                channel = null;
            }
        }
    }
}
