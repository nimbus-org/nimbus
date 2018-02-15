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
package jp.ossc.nimbus.service.ftp.ftpclient;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import jp.ossc.nimbus.service.ftp.*;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileListParser;

/**
 * FTPクライアント。<p>
* <a href="http://commons.apache.org/net/">Jakarta Commons Net</a>のFTPライブラリを使用した{@link FTPClient}実装クラスである。<br>
  *
 * @author M.Takata
 */
public class FTPClientImpl implements FTPClient{
    
    private org.apache.commons.net.ftp.FTPClient client;
    private FTPFileListParser ftpFileListParser;
    private int transferType = FTP.ASCII_FILE_TYPE;
    private File homeDir;
    private boolean isJavaRegexEnabled = false;
    private boolean isPassive = false;
    private int port = 21;
    private int connectMaxRetryCount = 0;
    private int soTimeout = -1;
    private int soLinger = -1;
    private Boolean isTcpNoDelay;
    
    public FTPClientImpl(org.apache.commons.net.ftp.FTPClient client){
        this.client = client;
    }
    
    public void setFTPFileListParser(FTPFileListParser parser){
        ftpFileListParser = parser;
    }
    public FTPFileListParser getFTPFileListParser(){
        return ftpFileListParser;
    }
    
    public void setHomeDirectory(File dir){
        homeDir = dir;
    }
    public File getHomeDirectory(){
        return homeDir;
    }
    
    public void setJavaRegexEnabled(boolean isEnabled){
        isJavaRegexEnabled = isEnabled;
    }
    public boolean isJavaRegexEnabled(){
        return isJavaRegexEnabled;
    }
    
    public void setConnectMaxRetryCount(int count){
        connectMaxRetryCount = count;
    }
    public int getConnectMaxRetryCount(){
        return connectMaxRetryCount;
    }
    
    public void setSoTimeout(int timeout){
        soTimeout = timeout;
    }
    public int getSoTimeout(){
        return soTimeout;
    }
    
    public void setSoLinger(int time){
        soLinger = time;
    }
    public int getSoLinger(){
        return soLinger;
    }
    
    public void setTcpNoDelay(boolean noDelay){
        isTcpNoDelay = noDelay ? Boolean.TRUE : Boolean.FALSE;
    }
    public boolean isTcpNoDelay(){
        return isTcpNoDelay == null ? false : isTcpNoDelay.booleanValue();
    }
    
    public void connect(String host) throws FTPException{
        int retryCount = 0;
        while(true){
            try{
                client.connect(host);
                this.port = 21;
                break;
            }catch(SocketException e){
                if(retryCount < connectMaxRetryCount){
                    retryCount++;
                    continue;
                }
                throw new FTPException(e);
            }catch(UnknownHostException e){
                throw new FTPException(e);
            }catch(IOException e){
                throw new FTPException(e);
            }
        }
        try{
            if(soTimeout >= 0){
                client.setSoTimeout(soTimeout);
            }
            if(soLinger >= 0){
                client.setSoLinger(true, soLinger);
            }
            if(isTcpNoDelay != null){
                client.setTcpNoDelay(isTcpNoDelay.booleanValue());
            }
        }catch(SocketException e){
            throw new FTPException(e);
        }
    }
    
    public void connect(String host, int port) throws FTPException{
        int retryCount = 0;
        while(true){
            try{
                client.connect(host, port);
                this.port = port;
                break;
            }catch(SocketException e){
                if(retryCount < connectMaxRetryCount){
                    retryCount++;
                    continue;
                }
                throw new FTPException(e);
            }catch(UnknownHostException e){
                throw new FTPException(e);
            }catch(IOException e){
                throw new FTPException(e);
            }
        }
        try{
            if(soTimeout >= 0){
                client.setSoTimeout(soTimeout);
            }
            if(soLinger >= 0){
                client.setSoLinger(true, soLinger);
            }
            if(isTcpNoDelay != null){
                client.setTcpNoDelay(isTcpNoDelay.booleanValue());
            }
        }catch(SocketException e){
            throw new FTPException(e);
        }
    }
    
    public void connect(
        String host,
        int port,
        String localAddr,
        int localPort
    ) throws FTPException{
        int retryCount = 0;
        while(true){
            try{
                client.connect(host, port, InetAddress.getByName(localAddr), localPort);
                this.port = port;
                break;
            }catch(SocketException e){
                if(retryCount < connectMaxRetryCount){
                    retryCount++;
                    continue;
                }
                throw new FTPException(e);
            }catch(UnknownHostException e){
                throw new FTPException(e);
            }catch(IOException e){
                throw new FTPException(e);
            }
        }
        try{
            if(soTimeout >= 0){
                client.setSoTimeout(soTimeout);
            }
            if(soLinger >= 0){
                client.setSoLinger(true, soLinger);
            }
            if(isTcpNoDelay != null){
                client.setTcpNoDelay(isTcpNoDelay.booleanValue());
            }
        }catch(SocketException e){
            throw new FTPException(e);
        }
    }
    
    public void login(String user, String password) throws FTPException{
        try{
            if(!client.login(user, password)){
                throw new FTPErrorResponseException(
                    client.getReplyCode(),
                    "login failed. cause=" + client.getReplyString()
                );
            }
        }catch(IOException e){
            throw new FTPException(e);
        }
    }
    
    public void logout() throws FTPException{
        try{
            if(!client.logout()){
                throw new FTPErrorResponseException(
                    client.getReplyCode(),
                    "logout failed. cause=" + client.getReplyString()
                );
            }
        }catch(IOException e){
            throw new FTPException(e);
        }
    }
    
    public String[] ls() throws FTPException{
        try{
            if(ftpFileListParser == null){
                return client.listNames();
            }else{
                FTPFile[] files = client.listFiles(ftpFileListParser);
                if(files == null || files.length == 0){
                    return new String[0];
                }
                String[] paths = new String[files.length];
                for(int i = 0; i < files.length; i++){
                    paths[i] = files[i].getName();
                }
                return paths;
            }
        }catch(IOException e){
            throw new FTPException(e);
        }
    }
    
    public FTPFile[] lsFTPFile() throws FTPException{
        try{
            return ftpFileListParser == null
                ? client.listFiles() : client.listFiles(ftpFileListParser);
        }catch(IOException e){
            throw new FTPException(e);
        }
    }
    
    public String[] ls(String path) throws FTPException{
        try{
            if(ftpFileListParser == null){
                return client.listNames(path);
            }else{
                FTPFile[] files = client.listFiles(ftpFileListParser, path);
                if(files == null || files.length == 0){
                    return new String[0];
                }
                String[] paths = new String[files.length];
                for(int i = 0; i < files.length; i++){
                    paths[i] = files[i].getName();
                }
                return paths;
            }
        }catch(IOException e){
            throw new FTPException(e);
        }
    }
    
    public FTPFile[] lsFTPFile(String path) throws FTPException{
        try{
            return ftpFileListParser == null
                ? client.listFiles(path) : client.listFiles(ftpFileListParser, path);
        }catch(IOException e){
            throw new FTPException(e);
        }
    }
    
    public String pwd() throws FTPException{
        try{
            return client.printWorkingDirectory();
        }catch(IOException e){
            throw new FTPException(e);
        }
    }
    
    public File lpwd() throws FTPException{
        if(homeDir == null){
            return new File(".");
        }else{
            return homeDir;
        }
    }
    
    public void cd(String path) throws FTPException{
        try{
            if(!client.changeWorkingDirectory(path)){
                throw new FTPErrorResponseException(
                    client.getReplyCode(),
                    "Can't change directory! from=" + client.printWorkingDirectory() + ", to=" + path + ", cause=" + client.getReplyString()
                );
            }
        }catch(IOException e){
            throw new FTPException(e);
        }
    }
    
    public void lcd(String path) throws FTPException{
        File file = new File(path);
        if(!file.isAbsolute() && homeDir != null){
            file = new File(homeDir, path);
        }
        if(!file.exists()){
            throw new FTPException("Can't change directory, because it is not exists! path=" + path);
        }
        if(!file.isDirectory()){
            throw new FTPException("Can't change directory, because it is not directory! path=" + path);
        }
        homeDir = file;
    }
    
    public void mkdir(String dir) throws FTPException{
        try{
            if(!client.makeDirectory(dir)){
                throw new FTPErrorResponseException(
                    client.getReplyCode(),
                    "Can't make directory! dir=" + dir + ", cause=" + client.getReplyString()
                );
            }
        }catch(IOException e){
            throw new FTPException(e);
        }
    }
    
    public void rename(String from, String to) throws FTPException{
        try{
            if(!client.rename(from, to)){
                throw new FTPErrorResponseException(
                    client.getReplyCode(),
                    "Can't rename file! from=" + from + ", to=" + to + ", cause=" + client.getReplyString()
                );
            }
        }catch(IOException e){
            throw new FTPException(e);
        }
    }
    
    public File get(String path) throws FTPException{
        FileOutputStream fos = null;
        try{
            File file = new File(path);
            String name = file.getName();
            File localFile = homeDir == null ? new File(name) : new File(homeDir, name);
            fos = new FileOutputStream(localFile);
            if(!client.retrieveFile(path, fos)){
                throw new FTPErrorResponseException(
                    client.getReplyCode(),
                    "Can't get file! remote=" + path + ", local=" + localFile + ", cause=" + client.getReplyString()
                );
            }
            return localFile;
        }catch(IOException e){
            throw new FTPException(e);
        }finally{
            if(fos != null){
                try{
                    fos.close();
                }catch(IOException e){
                }
            }
        }
    }
    
    public File get(String remote, String local) throws FTPException{
        FileOutputStream fos = null;
        try{
            File localFile = new File(local);
            if(homeDir != null && !localFile.isAbsolute()){
                localFile = new File(homeDir, local);
            }
            fos = new FileOutputStream(localFile);
            if(!client.retrieveFile(remote, fos)){
                throw new FTPErrorResponseException(
                    client.getReplyCode(),
                    "Can't get file! remote=" + remote + ", local=" + localFile + ", cause=" + client.getReplyString()
                );
            }
            return localFile;
        }catch(IOException e){
            throw new FTPException(e);
        }finally{
            if(fos != null){
                try{
                    fos.close();
                }catch(IOException e){
                }
            }
        }
    }
    
    public File[] mget(String path) throws FTPException{
        try{
            File file = new File(path);
            File dir = file.getParentFile();
            FTPFile[] files = null;
            if(dir == null){
                if(ftpFileListParser == null){
                    files = client.listFiles();
                }else{
                    files = client.listFiles(ftpFileListParser);
                }
            }else{
                if(ftpFileListParser == null){
                    files = client.listFiles(dir.getPath());
                }else{
                    files = client.listFiles(ftpFileListParser, dir.getPath());
                }
            }
            if(files == null || files.length == 0){
                return new File[0];
            }
            String fileName = file.getName();
            if(!isJavaRegexEnabled){
                fileName = fileName.replaceAll("\\.", "\\\\.");
                fileName = fileName.replaceAll("\\*", ".*");
            }
            Pattern p = Pattern.compile(fileName);
            List result = new ArrayList();
            for(int i = 0; i < files.length; i++){
                if(files[i].isDirectory()){
                    continue;
                }
                
                Matcher m = p.matcher(files[i].getName());
                if(!m.matches()){
                    continue;
                }
                
                result.add(
                    get(dir == null
                        ? files[i].getName()
                            : new File(dir, files[i].getName()).getPath())
                );
            }
            return (File[])result.toArray(new File[result.size()]);
        }catch(IOException e){
            throw new FTPException(e);
        }
    }
    
    public void put(String path) throws FTPException{
        FileInputStream fis = null;
        try{
            File localFile = new File(path);
            String remote = localFile.getName();
            if(homeDir != null && !localFile.isAbsolute()){
                localFile = new File(homeDir, path);
            }
            fis = new FileInputStream(localFile);
            if(!client.storeFile(remote, fis)){
                throw new FTPErrorResponseException(
                    client.getReplyCode(),
                    "Can't put file! local=" + path + ", remote=" + remote + ", cause=" + client.getReplyString()
                );
            }
        }catch(IOException e){
            throw new FTPException(e);
        }finally{
            if(fis != null){
                try{
                    fis.close();
                }catch(IOException e){
                }
            }
        }
    }
    
    public void put(String local, String remote) throws FTPException{
        FileInputStream fis = null;
        try{
            File localFile = new File(local);
            if(homeDir != null && !localFile.isAbsolute()){
                localFile = new File(homeDir, local);
            }
            fis = new FileInputStream(localFile);
            if(!client.storeFile(remote, fis)){
                throw new FTPErrorResponseException(
                    client.getReplyCode(),
                    "Can't put file! local=" + local + ", remote=" + remote + ", cause=" + client.getReplyString()
                );
            }
        }catch(IOException e){
            throw new FTPException(e);
        }finally{
            if(fis != null){
                try{
                    fis.close();
                }catch(IOException e){
                }
            }
        }
    }
    
    public void mput(String path) throws FTPException{
        File file = new File(path);
        if(homeDir != null && !file.isAbsolute()){
            file = new File(homeDir, path);
        }
        File dir = file.getParentFile();
        String fileName = file.getName();
        if(!isJavaRegexEnabled){
            fileName = fileName.replaceAll("\\.", "\\\\.");
            fileName = fileName.replaceAll("\\*", ".*");
        }
        File[] files = dir.listFiles();
        Pattern p = Pattern.compile(fileName);
        for(int i = 0; i < files.length; i++){
            if(files[i].isDirectory()){
                continue;
            }
            
            Matcher m = p.matcher(files[i].getName());
            if(!m.matches()){
                continue;
            }
            put(files[i].getPath());
        }
    }
    
    public void delete(String path) throws FTPException{
        try{
            if(!client.deleteFile(path)){
                throw new FTPErrorResponseException(
                    client.getReplyCode(),
                    "Can't delete file! path=" + path + ", cause=" + client.getReplyString()
                );
            }
        }catch(IOException e){
            throw new FTPException(e);
        }
    }
    
    public void mdelete(String path) throws FTPException{
        try{
            File file = new File(path);
            File dir = file.getParentFile();
            FTPFile[] files = null;
            if(dir == null){
                if(ftpFileListParser == null){
                    files = client.listFiles();
                }else{
                    files = client.listFiles(ftpFileListParser);
                }
            }else{
                if(ftpFileListParser == null){
                    files = client.listFiles(dir.getPath());
                }else{
                    files = client.listFiles(ftpFileListParser, dir.getPath());
                }
            }
            if(files == null || files.length == 0){
                return;
            }
            String fileName = file.getName();
            if(!isJavaRegexEnabled){
                fileName = fileName.replaceAll("\\.", "\\\\.");
                fileName = fileName.replaceAll("\\*", ".*");
            }
            Pattern p = Pattern.compile(fileName);
            for(int i = 0; i < files.length; i++){
                if(files[i].isDirectory()){
                    continue;
                }
                
                Matcher m = p.matcher(files[i].getName());
                if(!m.matches()){
                    continue;
                }
                
                delete(
                    dir == null ? files[i].getName()
                        : new File(dir, files[i].getName()).getPath()
                );
            }
        }catch(IOException e){
            throw new FTPException(e);
        }
    }
    
    public void ascii() throws FTPException{
        setTransferType(FTP.ASCII_FILE_TYPE);
    }
    
    public void binary() throws FTPException{
        setTransferType(FTP.BINARY_FILE_TYPE);
    }
    
    public void active() throws FTPException{
        if(isPassive){
//            try{
                client.enterLocalActiveMode();
// 呼ぶとエラーになる。とりあえず、コメントアウト。
//                if(!client.enterRemoteActiveMode(client.getRemoteAddress(), port)){
//                    throw new FTPErrorResponseException(
//                        client.getReplyCode(),
//                        "Can't change active mode! cause=" + client.getReplyString()
//                    );
//                }
                isPassive = false;
//            }catch(IOException e){
//                throw new FTPException(e);
//            }
        }
    }
    
    public void passive() throws FTPException{
        if(!isPassive){
            try{
                if(!client.enterRemotePassiveMode()){
                    throw new FTPErrorResponseException(
                        client.getReplyCode(),
                        "Can't change passive mode! cause=" + client.getReplyString()
                    );
                }
                client.enterLocalPassiveMode();
                isPassive = true;
            }catch(IOException e){
                throw new FTPException(e);
            }
        }
    }
    
    public void setTransferType(int type) throws FTPException{
        try{
            client.setFileType(type);
            transferType = type;
        }catch(IOException e){
            throw new FTPException(e);
        }
    }
    
    public int getTransferType() throws FTPException{
        return transferType;
    }
    
    public void close() throws FTPException{
        try{
            client.disconnect();
        }catch(IOException e){
            throw new FTPException(e);
        }
    }
}