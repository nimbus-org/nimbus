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
package jp.ossc.nimbus.service.scp.jsch;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;

import jp.ossc.nimbus.io.RecurciveSearchFile;
import jp.ossc.nimbus.service.scp.SCPException;

/**
 * SCPクライアント。<p>
 * <a href="http://www.jcraft.com/jsch/">JSch - Java Secure Channel</a>のSCPライブラリを使用した{@link jp.ossc.nimbus.service.scp.SCPClient SCPClient}実装クラスである。<br>
 *
 * @author M.Takata
 */
public class SCPClientImpl implements jp.ossc.nimbus.service.scp.SCPClient{
    
    private int timeout = -1;
    private int serverAliveInterval = -1;
    private int serverAliveCountMax = -1;
    
    private Properties configProperties;
    
    private File homeDir;
    
    private JSch jsch;
    private Session session;
    
    public void setTimeout(int timeout){
        this.timeout = timeout;
    }
    public int getTimeout(){
        return timeout;
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
    
    public void setConfig(Properties conf){
        configProperties = conf;
    }
    public Properties getConfig(){
        return configProperties;
    }
    
    public void setHomeDirectory(File dir){
        homeDir = dir;
    }
    public File getHomeDirectory(){
        return homeDir;
    }
    
    public void connect(String user, String host, String password) throws SCPException{
        connect(user, host, 22, password);
    }
    
    public void connect(String user, String host, int port, String password) throws SCPException{
        if(jsch != null){
            throw new SCPException("It is already connected!");
        }
        jsch = new JSch();
        try{
            session = jsch.getSession(user, host, port);
            if(configProperties != null){
                session.setConfig(configProperties);
            }
            if(timeout >= 0){
                session.setTimeout(timeout);
            }
            if(serverAliveInterval >= 0){
                session.setServerAliveInterval(serverAliveInterval);
            }
            if(serverAliveCountMax >= 0){
                session.setServerAliveCountMax(serverAliveCountMax);
            }
            if(password != null){
                session.setPassword(password);
            }
            session.connect();
        }catch(JSchException e){
            session = null;
            jsch = null;
            throw new SCPException("It failed to authenticate!", e);
        }
    }
    
    public void connect(String user, String host, File pemFile, String passphrase) throws SCPException{
        connect(user, host, 22, pemFile, passphrase);
    }
    
    public void connect(String user, String host, int port, File pemFile, String passphrase) throws SCPException{
        if(jsch != null){
            throw new SCPException("It is already connected!");
        }
        jsch = new JSch();
        if(!pemFile.exists()){
            throw new SCPException("The pemFile not exists! path=" + pemFile);
        }
        try{
            jsch.addIdentity(pemFile.getPath(), passphrase);
            session = jsch.getSession(user, host, port);
            if(configProperties != null){
                session.setConfig(configProperties);
            }
            if(timeout >= 0){
                session.setTimeout(timeout);
            }
            if(serverAliveInterval >= 0){
                session.setServerAliveInterval(serverAliveInterval);
            }
            if(serverAliveCountMax >= 0){
                session.setServerAliveCountMax(serverAliveCountMax);
            }
            session.connect();
        }catch(JSchException e){
            session = null;
            jsch = null;
            throw new SCPException("It failed to authenticate!", e);
        }
    }
    
    private int checkAck(InputStream in) throws IOException{
        final int b = in.read();
        if(b == 0){
            return b;
        }
        if(b == -1){
            return b;
        }else if(b == 1 || b == 2){
            StringBuilder sb = new StringBuilder();
            int c;
            do{
                c = in.read();
                sb.append((char)c);
            }while(c != '\n');
            throw new IOException("Remote SCP error: " + sb.toString());
        }
        return b;
    }
    
    public File get(String remote) throws SCPException{
        return get(remote, null);
    }
    
    public File get(String remote, String local) throws SCPException{
        if(jsch == null){
            throw new SCPException("Connection is not established!");
        }
        if(session == null){
            throw new SCPException("It is not authenticated!");
        }
        if(remote == null || remote.length() == 0){
            throw new SCPException("Path is null.");
        }
        Channel channel = null;
        final String cmd = "scp -f " + remote;
        File localFile = null;
        try{
            channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand(cmd);
            
            OutputStream os = channel.getOutputStream();
            InputStream is = channel.getInputStream();
            channel.connect();
            
            byte[] buf = new byte[1024];
            buf[0]=0;
            os.write(buf, 0, 1);
            os.flush();
            
            int c = checkAck(is);
            if(c == -1){
                throw new IOException("Remote SCP terminated unexpectedly.");
            }
            is.read(buf, 0, 5);
            
            long fileSize = 0L;
            while(true){
                if(is.read(buf, 0, 1) < 0){
                    throw new SCPException("Unexpected EOF.");
                }
                if(buf[0]==' '){
                    break;
                }
                fileSize = fileSize * 10L + (long)(buf[0] - '0');
            }
            
            String fileName = null;
            for(int i = 0; ; i++){
                is.read(buf, i, 1);
                if(buf[i] == (byte)0x0a){
                    fileName = new String(buf, 0, i);
                    break;
                }
            }
            buf[0] = 0;
            os.write(buf, 0, 1);
            os.flush();
            
            if(local == null){
                localFile = homeDir == null ? new File(fileName) : new File(homeDir, fileName);
            }else{
                localFile = new File(local);
                if(homeDir != null && !localFile.isAbsolute()){
                    localFile = new File(homeDir, local);
                }
            }
            FileOutputStream fos = new FileOutputStream(localFile);
            try{
                int readLen = 0;
                while(true){
                    if(buf.length < fileSize){
                        readLen = buf.length;
                    }else{
                        readLen=(int)fileSize;
                    }
                    readLen = is.read(buf, 0, readLen);
                    if(readLen < 0){
                        throw new SCPException("Unexpected EOF.");
                    }
                    fos.write(buf, 0, readLen);
                    fileSize -= readLen;
                    if(fileSize == 0L){
                        break;
                    }
                }
            }finally{
                fos.close();
                fos = null;
            }
            checkAck(is);
            buf[0] = 0;
            os.write(buf, 0, 1);
            os.flush();
        }catch(IOException e){
            throw new SCPException("It failed to mget! from=" + remote + ", to=" + localFile, e);
        }catch(JSchException e){
            throw new SCPException("It failed to mget! from=" + remote + ", to=" + localFile, e);
        }finally{
            if(channel != null){
                channel.disconnect();
            }
        }
        return localFile;
    }
    
    public File[] mget(String remote) throws SCPException{
        return mget(remote, null);
    }
    
    public File[] mget(String remote, String localDir) throws SCPException{
        if(jsch == null){
            throw new SCPException("Connection is not established!");
        }
        if(session == null){
            throw new SCPException("It is not authenticated!");
        }
        if(remote == null || remote.length() == 0){
            throw new SCPException("Path is null.");
        }
        if(localDir == null){
            localDir = ".";
        }
        File localDirFile = new File(localDir);
        if(homeDir != null && !localDirFile.isAbsolute()){
            localDirFile = new File(homeDir, localDir);
        }
        List<File> localFiles = new ArrayList<File>();
        Channel channel = null;
        final String cmd = "scp -f " + remote;
        File localFile = null;
        try{
            channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand(cmd);
            
            OutputStream os = channel.getOutputStream();
            InputStream is = channel.getInputStream();
            channel.connect();
            
            byte[] buf = new byte[1024];
            os.write(0);
            os.flush();
            
            while(true){
                int c = checkAck(is);
                if(c == -1){
                    if(localFiles.size() == 0){
                        throw new IOException("Remote SCP terminated unexpectedly.");
                    }
                    break;
                }
                if(c != 'C'){
                    break;
                }
                is.read(buf, 0, 5);
                
                long fileSize = 0L;
                while(true){
                    if(is.read(buf, 0, 1) < 0){
                        throw new SCPException("Unexpected EOF.");
                    }
                    if(buf[0]==' '){
                        break;
                    }
                    fileSize = fileSize * 10L + (long)(buf[0] - '0');
                }
                
                String fileName = null;
                for(int i = 0; ;i++){
                    is.read(buf, i, 1);
                    if(buf[i] == (byte)0x0a){
                        fileName = new String(buf, 0, i);
                        break;
                    }
                }
                buf[0] = 0;
                os.write(buf, 0, 1);
                os.flush();
                
                localFile = new File(localDirFile, fileName);
                localFiles.add(localFile);
                FileOutputStream fos = new FileOutputStream(localFile);
                try{
                    int readLen = 0;
                    while(true){
                        if(buf.length < fileSize){
                            readLen = buf.length;
                        }else{
                            readLen=(int)fileSize;
                        }
                        readLen = is.read(buf, 0, readLen);
                        if(readLen < 0){
                            throw new SCPException("Unexpected EOF.");
                        }
                        fos.write(buf, 0, readLen);
                        fileSize -= readLen;
                        if(fileSize == 0L){
                            break;
                        }
                    }
                }finally{
                    fos.close();
                    fos = null;
                }
                checkAck(is);
                localFile = null;
                buf[0] = 0;
                os.write(buf, 0, 1);
                os.flush();
            }
        }catch(IOException e){
            throw new SCPException("It failed to mget! from=" + remote + ", to=" + localFile, e);
        }catch(JSchException e){
            throw new SCPException("It failed to mget! from=" + remote + ", to=" + localFile, e);
        }finally{
            if(channel != null){
                channel.disconnect();
            }
        }
        return localFiles.toArray(new File[localFiles.size()]);
    }
    
    public void put(String local) throws SCPException{
        put(local, null);
    }
    
    public void put(String local, String remote) throws SCPException{
        put(local, remote, null);
    }
    
    public void put(String local, String remote, String mode) throws SCPException{
        if(jsch == null){
            throw new SCPException("Connection is not established!");
        }
        if(session == null){
            throw new SCPException("It is not authenticated!");
        }
        File localFile = null;
        FileInputStream fis = null;
        OutputStream os = null;
        Channel channel = null;
        try{
            localFile = new File(local);
            if(homeDir != null && !localFile.isAbsolute()){
                localFile = new File(homeDir, local);
            }
            if(!localFile.exists()){
                throw new SCPException("File not exists! path=" + local);
            }
            
            StringBuilder command = new StringBuilder("scp -p -t ").append(remote);
            channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command.toString());
            
            os = channel.getOutputStream();
            InputStream is = channel.getInputStream();
            
            channel.connect();
            
            if(checkAck(is) != 0){
                throw new IOException("Remote SCP terminated unexpectedly.");
            }
            
            command.setLength(0);
            command.append("T ").append(localFile.lastModified() / 1000).append(" 0 ");
            command.append(localFile.lastModified() / 1000).append(" 0\n");
            os.write(command.toString().getBytes());
            os.flush();
            
            if(checkAck(is) != 0){
                throw new IOException("Remote SCP terminated unexpectedly.");
            }
            
            command.setLength(0);
            command.append("C");
            if(mode == null){
                mode = "0644";
            }
            command.append(" ").append(localFile.length()).append(" ");
            if(localFile.getParentFile() != null){
                command.append(localFile.getName());
            }
            command.append("\n");
            os.write(command.toString().getBytes());
            os.flush();
            if(checkAck(is) != 0){
                throw new IOException("Remote SCP terminated unexpectedly.");
            }
            
            fis = new FileInputStream(localFile);
            int length = 0;
            byte[] buf = new byte[1024];
            while((length = fis.read(buf)) != -1){
                os.write(buf, 0, length);
            }
            buf[0] = 0;
            os.write(buf, 0, 1);
            os.flush();
            if(checkAck(is) != 0){
                throw new IOException("Remote SCP terminated unexpectedly.");
            }
        }catch(IOException e){
            throw new SCPException("It failed to put! file=" + localFile, e);
        }catch(JSchException e){
            throw new SCPException("It failed to put! file=" + localFile, e);
        }finally{
            if(fis != null){
                try{
                    fis.close();
                }catch(IOException e){
                }
            }
            if(os != null){
                try{
                    os.close();
                }catch(IOException e){
                }
            }
            if(channel != null){
                channel.disconnect();
            }
        }
    }
    
    public void mput(String local) throws SCPException{
        mput(local, ".");
    }
    
    public void mput(String local, String remoteDir) throws SCPException{
        mput(local, remoteDir, null);
    }
    
    public void mput(String local, String remoteDir, String mode) throws SCPException{
        if(jsch == null){
            throw new SCPException("Connection is not established!");
        }
        if(session == null){
            throw new SCPException("It is not authenticated!");
        }
        if(remoteDir == null){
            remoteDir = "./";
        }else if(!remoteDir.endsWith("/")){
            remoteDir += "/";
        }
        RecurciveSearchFile rsf = new RecurciveSearchFile(".");
        File[] localFiles = rsf.listAllTreeFiles(local);
        for(int i = 0; i < localFiles.length; i++){
            if(mode == null){
                put(localFiles[i].getAbsolutePath(), remoteDir + localFiles[i].getName());
            }else{
                put(localFiles[i].getAbsolutePath(), remoteDir + localFiles[i].getName(), mode);
            }
        }
    }
    
    public void close() throws SCPException{
        if(session != null){
            session.disconnect();
            session = null;
        }
        jsch = null;
    }
}