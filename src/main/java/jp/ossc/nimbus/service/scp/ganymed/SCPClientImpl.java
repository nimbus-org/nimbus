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
package jp.ossc.nimbus.service.scp.ganymed;

import java.io.File;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.SCPClient;

import jp.ossc.nimbus.io.RecurciveSearchFile;
import jp.ossc.nimbus.service.scp.SCPException;

/**
 * SCPクライアント。<p>
 * <a href="http://www.ganymed.ethz.ch/ssh2/">Ganymed SSH-2 for Java</a>のSCPライブラリを使用した{@link SCPClient}実装クラスである。<br>
  *
 * @author M.Takata
 */
public class SCPClientImpl implements jp.ossc.nimbus.service.scp.SCPClient{
    
    private int connectionTimeout;
    private int keyExchangeTimeout;
    private Boolean isTcpNoDelay;
    
    private String[] serverHostKeyAlgorithms;
    
    private File homeDir;
    
    private Connection connection;
    private SCPClient scpClient;
    
    public void setConnectionTimeout(int timeout){
        connectionTimeout = timeout;
    }
    public int getConnectionTimeout(){
        return connectionTimeout;
    }
    
    public void setKeyExchangeTimeout(int timeout){
        keyExchangeTimeout = timeout;
    }
    public int getKeyExchangeTimeout(){
        return keyExchangeTimeout;
    }
    
    public void setTcpNoDelay(boolean noDelay){
        isTcpNoDelay = noDelay ? Boolean.TRUE : Boolean.FALSE;
    }
    public boolean isTcpNoDelay(){
        return isTcpNoDelay == null ? false : isTcpNoDelay.booleanValue();
    }
    
    public void setServerHostKeyAlgorithms(String[] algos){
        serverHostKeyAlgorithms = algos;
    }
    public String[] getServerHostKeyAlgorithms(){
        return serverHostKeyAlgorithms;
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
        if(connection != null){
            throw new SCPException("It is already connected!");
        }
        connection = new Connection(host, port);
        try{
            if(isTcpNoDelay != null){
                connection.setTCPNoDelay(isTcpNoDelay.booleanValue());
            }
            if(serverHostKeyAlgorithms != null){
                connection.setServerHostKeyAlgorithms(serverHostKeyAlgorithms);
            }
            connection.connect(null, connectionTimeout, keyExchangeTimeout);
            if(!connection.authenticateWithPassword(user, password)){
                throw new SCPException("It failed to authenticate!");
            }
            scpClient = connection.createSCPClient();
        }catch(IOException e){
            scpClient = null;
            connection.close();
            connection = null;
            throw new SCPException("It failed to connect!", e);
        }
    }
    
    public void connect(String user, String host, File pemFile, String passphrase) throws SCPException{
        connect(user, host, 22, pemFile, passphrase);
    }
    
    public void connect(String user, String host, int port, File pemFile, String passphrase) throws SCPException{
        if(connection != null){
            throw new SCPException("It is already connected!");
        }
        if(!pemFile.exists()){
            throw new SCPException("The pemFile not exists! path=" + pemFile);
        }
        FileInputStream fis = null;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String pem = null;
        try{
            fis = new FileInputStream(pemFile);
            int length = 0;
            byte[] buf = new byte[1024];
            while((length = fis.read(buf)) != -1){
                baos.write(buf, 0, length);
            }
            pem = new String(baos.toByteArray());
        }catch(IOException e){
            throw new SCPException("It failed to read pemFile! path=" + pemFile, e);
        }finally{
            if(fis != null){
                try{
                    fis.close();
                }catch(IOException e){}
            }
        }
        connection = new Connection(host, port);
        try{
            if(isTcpNoDelay != null){
                connection.setTCPNoDelay(isTcpNoDelay.booleanValue());
            }
            if(serverHostKeyAlgorithms != null){
                connection.setServerHostKeyAlgorithms(serverHostKeyAlgorithms);
            }
            connection.connect(null, connectionTimeout, keyExchangeTimeout);
            if(!connection.authenticateWithPublicKey(user, pem.toCharArray(), passphrase)){
                throw new SCPException("It failed to authenticate!");
            }
            scpClient = connection.createSCPClient();
        }catch(IOException e){
            scpClient = null;
            connection.close();
            connection = null;
            throw new SCPException("It failed to authenticate!", e);
        }
    }
    
    public File get(String remote) throws SCPException{
        if(connection == null){
            throw new SCPException("Connection is not established!");
        }
        if(scpClient == null){
            throw new SCPException("It is not authenticated!");
        }
        File localFile = null;
        try{
            File file = new File(remote);
            String name = file.getName();
            localFile = homeDir == null ? new File(name) : new File(homeDir, name);
            scpClient.get(remote, homeDir == null ? "." : homeDir.getPath());
        }catch(IOException e){
            throw new SCPException("It failed to get! file=" + remote, e);
        }
        return localFile;
    }
    
    public File get(String remote, String local) throws SCPException{
        if(connection == null){
            throw new SCPException("Connection is not established!");
        }
        if(scpClient == null){
            throw new SCPException("It is not authenticated!");
        }
        File localFile = null;
        try{
            localFile = new File(local);
            if(homeDir != null && !localFile.isAbsolute()){
                localFile = new File(homeDir, local);
            }
            final String targetDir = localFile.getParentFile() == null ? "." : localFile.getParentFile().getPath();
            scpClient.get(remote, targetDir);
            File remoteFile = new File(remote);
            final String name = remoteFile.getName();
            File getFile = new File(targetDir, name);
            if(!getFile.equals(localFile)){
                if(!getFile.renameTo(localFile)){
                    getFile.delete();
                    throw new SCPException("Can not write to directory! dir=" + targetDir);
                }
            }
        }catch(IOException e){
            throw new SCPException("It failed to get! file=" + remote, e);
        }
        return localFile;
    }
    
    public File[] mget(String remote) throws SCPException{
        return mget(remote, null);
    }
    
    public File[] mget(String remote, String localDir) throws SCPException{
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
        List localFiles = new ArrayList();
        Session session = null;
        final String cmd = "scp -f " + remote;
        File localFile = null;
        try{
            session = connection.openSession();
            session.execCommand(cmd);
            
            byte[] buf = new byte[1024];
            OutputStream os = new BufferedOutputStream(session.getStdin(), 512);
            InputStream is = new BufferedInputStream(session.getStdout(), 40000);
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
            throw new SCPException("It failed to mget! from=" + remote + ", to=" + (localFile == null ? localDirFile : localFile), e);
        }finally{
            if(session != null){
                session.close();
            }
        }
        return (File[])localFiles.toArray(new File[localFiles.size()]);
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
    
    public void put(String local) throws SCPException{
        if(connection == null){
            throw new SCPException("Connection is not established!");
        }
        if(scpClient == null){
            throw new SCPException("It is not authenticated!");
        }
        File localFile = null;
        try{
            localFile = new File(local);
            if(homeDir != null && !localFile.isAbsolute()){
                localFile = new File(homeDir, local);
            }
            if(!localFile.exists()){
                throw new SCPException("File not exists! path=" + local);
            }
            scpClient.put(localFile.getPath(), ".");
        }catch(IOException e){
            throw new SCPException("It failed to put! file=" + localFile, e);
        }
    }
    
    public void put(String local, String remote) throws SCPException{
        put(local, remote, null);
    }
    
    public void put(String local, String remote, String mode) throws SCPException{
        if(connection == null){
            throw new SCPException("Connection is not established!");
        }
        if(scpClient == null){
            throw new SCPException("It is not authenticated!");
        }
        File localFile = null;
        try{
            localFile = new File(local);
            if(homeDir != null && !localFile.isAbsolute()){
                localFile = new File(homeDir, local);
            }
            if(!localFile.exists()){
                throw new SCPException("File not exists! path=" + local);
            }
            File remoteFile = new File(remote);
            if(localFile.getName().equals(remoteFile.getName())){
                if(mode == null){
                    scpClient.put(localFile.getPath(), remoteFile.getParentFile() == null ? "." : remoteFile.getParentFile().getPath());
                }else{
                    scpClient.put(localFile.getPath(), remoteFile.getParentFile() == null ? "." : remoteFile.getParentFile().getPath(), mode);
                }
            }else{
                FileInputStream fis = null;
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try{
                    fis = new FileInputStream(localFile);
                    int length = 0;
                    byte[] buf = new byte[1024];
                    while((length = fis.read(buf)) != -1){
                        baos.write(buf, 0, length);
                    }
                }catch(IOException e){
                    throw new SCPException("It failed to read path=" + localFile, e);
                }finally{
                    if(fis != null){
                        try{
                            fis.close();
                        }catch(IOException e){}
                    }
                }
                if(mode == null){
                    scpClient.put(baos.toByteArray(), remoteFile.getName(), remoteFile.getParentFile() == null ? "." : remoteFile.getParentFile().getPath());
                }else{
                    scpClient.put(baos.toByteArray(), remoteFile.getName(), remoteFile.getParentFile() == null ? "." : remoteFile.getParentFile().getPath(), mode);
                }
            }
        }catch(IOException e){
            throw new SCPException("It failed to put! file=" + localFile, e);
        }
    }
    
    public void mput(String local) throws SCPException{
        mput(local, ".");
    }
    
    public void mput(String local, String remoteDir) throws SCPException{
        mput(local, remoteDir, null);
    }
    
    public void mput(String local, String remoteDir, String mode) throws SCPException{
        if(connection == null){
            throw new SCPException("Connection is not established!");
        }
        if(scpClient == null){
            throw new SCPException("It is not authenticated!");
        }
        RecurciveSearchFile rsf = new RecurciveSearchFile(".");
        File[] localFiles = rsf.listAllTreeFiles(local);
        try{
            for(int i = 0; i < localFiles.length; i++){
                if(mode == null){
                    scpClient.put(localFiles[i].getPath(), remoteDir);
                }else{
                    scpClient.put(localFiles[i].getPath(), remoteDir, mode);
                }
            }
        }catch(IOException e){
            throw new SCPException("It failed to mput! local=" + local, e);
        }
    }
    
    public void close() throws SCPException{
        if(connection != null){
            connection.close();
            connection = null;
        }
        scpClient = null;
    }
}