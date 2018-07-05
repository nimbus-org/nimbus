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
package jp.ossc.nimbus.service.sftp.jsch;

import java.io.File;
import java.util.Properties;
import java.util.List;
import java.util.Date;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.Proxy;

import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.io.RecurciveSearchFile;
import jp.ossc.nimbus.service.sftp.SFTPException;
import jp.ossc.nimbus.service.semaphore.Semaphore;

/**
 * SFTPクライアント。<p>
 * <a href="http://www.jcraft.com/jsch/">JSch - Java Secure Channel</a>のSFTPライブラリを使用した{@link jp.ossc.nimbus.service.sftp.SFTPClient SFTPClient}実装クラスである。<br>
 *
 * @author M.Takata
 */
public class SFTPClientImpl implements jp.ossc.nimbus.service.sftp.SFTPClient{

    private static final int ERROR_ID_NO_SUCH_FILE = 2;

    private int timeout = -1;
    private int serverAliveInterval = -1;
    private int serverAliveCountMax = -1;

    private Properties configProperties;

    private File homeDir;
    private String fileNameEncoding;

    private JSch jsch;
    private Session session;
    private ChannelSftp channel;
    private Proxy proxy;
    private Semaphore semaphore;
    
    private ServiceName sftpClientFactoryServiceName;
    
    public void setSemaphore(Semaphore semaphore){
        this.semaphore = semaphore;
    }
    
    public void setTimeout(int timeout){
        this.timeout = timeout;
    }
    public int getTimeout(){
        return timeout;
    }

    public void setServerAliveInterval(int interval){
        serverAliveInterval = timeout;
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

    public void setProxy(Proxy proxy){
        this.proxy = proxy;
    }

    public void setHomeDirectory(File dir){
        homeDir = dir;
    }
    public File getHomeDirectory(){
        return homeDir;
    }

    public void setFileNameEncoding(String encoding){
        fileNameEncoding = encoding;
    }
    public String getFileNameEncoding(){
        return fileNameEncoding;
    }
    
    public ServiceName getSftpClientFactoryServiceName() {
        return sftpClientFactoryServiceName;
    }
    
    public void setSftpClientFactoryServiceName(ServiceName name) {
        sftpClientFactoryServiceName = name;
    }

    public void connect(String user, String host, String password) throws SFTPException{
        connect(user, host, 22, password);
    }

    public void connect(String user, String host, int port, String password) throws SFTPException{
        if(jsch != null){
            throw new SFTPException(sftpClientFactoryServiceName, "It is already connected!");
        }
        jsch = new JSch();
        try{
            session = jsch.getSession(user, host, port);
            if(configProperties != null){
                session.setConfig(configProperties);
            }
            if(proxy != null){
                session.setProxy(proxy);
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
            channel = (ChannelSftp)session.openChannel("sftp");
            if(fileNameEncoding != null){
                channel.setFilenameEncoding(fileNameEncoding);
            }
            channel.connect();
            if(homeDir != null){
                channel.lcd(homeDir.getPath());
            }
        }catch(JSchException e){
            if(channel != null){
                channel.disconnect();
                channel = null;
            }
            if(session != null){
                session.disconnect();
                session = null;
            }
            jsch = null;
            throw new SFTPException(sftpClientFactoryServiceName, "It failed to connect!", e);
        }catch(SftpException e){
            if(channel != null){
                channel.disconnect();
                channel = null;
            }
            if(session != null){
                session.disconnect();
                session = null;
            }
            jsch = null;
            throw new SFTPException(sftpClientFactoryServiceName, "It failed to connect!", e);
        }
    }

    public void connect(String user, String host, File pemFile, String passphrase) throws SFTPException{
        connect(user, host, 22, pemFile, passphrase);
    }

    public void connect(String user, String host, int port, File pemFile, String passphrase) throws SFTPException{
        if(jsch != null){
            throw new SFTPException(sftpClientFactoryServiceName, "It is already connected!");
        }
        jsch = new JSch();
        try{
            jsch.addIdentity(pemFile.getAbsolutePath(), passphrase);
            session = jsch.getSession(user, host, port);
            if(configProperties != null){
                session.setConfig(configProperties);
            }
            if(proxy != null){
                session.setProxy(proxy);
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
            channel = (ChannelSftp)session.openChannel("sftp");
            if(fileNameEncoding != null){
                channel.setFilenameEncoding(fileNameEncoding);
            }
            channel.connect();
            if(homeDir != null){
                channel.lcd(homeDir.getPath());
            }
        }catch(JSchException e){
            if(channel != null){
                channel.disconnect();
                channel = null;
            }
            if(session != null){
                session.disconnect();
                session = null;
            }
            jsch = null;
            throw new SFTPException(sftpClientFactoryServiceName, "It failed to connect!", e);
        }catch(SftpException e){
            if(channel != null){
                channel.disconnect();
                channel = null;
            }
            if(session != null){
                session.disconnect();
                session = null;
            }
            jsch = null;
            throw new SFTPException(sftpClientFactoryServiceName, "It failed to connect!", e);
        }
    }

    public String[] ls() throws SFTPException{
        return ls(".");
    }

    public String[] ls(String path) throws NoSuchFileSFTPException, SFTPException{
        if(channel == null){
            throw new SFTPException(sftpClientFactoryServiceName, "Connection is not established!");
        }
        try{
            List entries = channel.ls(path);
            String[] fileNames = new String[entries.size()];
            for(int i = 0; i < entries.size(); i++){
                ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry)entries.get(i);
                fileNames[i] = entry.getFilename();
            }
            return fileNames;
        }catch(SftpException e){
            if(e.id == ERROR_ID_NO_SUCH_FILE){
                throw new NoSuchFileSFTPException(sftpClientFactoryServiceName, "It failed to ls! path=" + path, e);
            }
            throw new SFTPException(sftpClientFactoryServiceName, "It failed to ls! path=" + path, e);
        }
    }

    public SFTPFile[] lsFile() throws SFTPException{
        return lsFile(".");
    }

    public SFTPFile[] lsFile(String path) throws SFTPException{
        if(channel == null){
            throw new SFTPException(sftpClientFactoryServiceName, "Connection is not established!");
        }
        try{
            List entries = channel.ls(path);
            SFTPFile[] files = new SFTPFile[entries.size()];
            for(int i = 0; i < entries.size(); i++){
                ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry)entries.get(i);
                files[i] = new SFTPFileImpl(entry);
            }
            return files;
        }catch(SftpException e){
            if(e.id == ERROR_ID_NO_SUCH_FILE){
                throw new NoSuchFileSFTPException(sftpClientFactoryServiceName, "It failed to ls! path=" + path, e);
            }
            throw new SFTPException(sftpClientFactoryServiceName, "It failed to ls! path=" + path, e);
        }
    }

    public String pwd() throws SFTPException{
        if(channel == null){
            throw new SFTPException(sftpClientFactoryServiceName, "Connection is not established!");
        }
        try{
            return channel.pwd();
        }catch(SftpException e){
            throw new SFTPException(sftpClientFactoryServiceName, "It failed to pwd!", e);
        }
    }

    public File lpwd() throws SFTPException{
        if(channel == null){
            throw new SFTPException(sftpClientFactoryServiceName, "Connection is not established!");
        }
        final String path = channel.lpwd();
        return new File(path);
    }

    public void cd(String path) throws NoSuchFileSFTPException, SFTPException{
        if(channel == null){
            throw new SFTPException(sftpClientFactoryServiceName, "Connection is not established!");
        }
        try{
            channel.cd(path);
        }catch(SftpException e){
            if(e.id == ERROR_ID_NO_SUCH_FILE){
                throw new NoSuchFileSFTPException(sftpClientFactoryServiceName, "It failed to cd! to=" + path, e);
            }
            throw new SFTPException(sftpClientFactoryServiceName, "It failed to cd! to=" + path, e);
        }
    }

    public void lcd(String path) throws SFTPException{
        if(channel == null){
            throw new SFTPException(sftpClientFactoryServiceName, "Connection is not established!");
        }
        try{
            channel.lcd(path);
        }catch(SftpException e){
            throw new SFTPException(sftpClientFactoryServiceName, "It failed to lcd! to=" + path, e);
        }
    }

    public void mkdir(String dir) throws SFTPException{
        if(channel == null){
            throw new SFTPException(sftpClientFactoryServiceName, "Connection is not established!");
        }
        try{
            channel.mkdir(dir);
        }catch(SftpException e){
            throw new SFTPException(sftpClientFactoryServiceName, "It failed to mkdir! dir=" + dir, e);
        }
    }

    public void rename(String from, String to) throws NoSuchFileSFTPException, SFTPException{
        if(channel == null){
            throw new SFTPException(sftpClientFactoryServiceName, "Connection is not established!");
        }
        try{
            channel.rename(from, to);
        }catch(SftpException e){
            if(e.id == ERROR_ID_NO_SUCH_FILE){
                throw new NoSuchFileSFTPException(sftpClientFactoryServiceName, "It failed to rename! from=" + from + ", to=" + to, e);
            }
            throw new SFTPException(sftpClientFactoryServiceName, "It failed to rename! from=" + from + ", to=" + to, e);
        }
    }

    public File get(String remote) throws NoSuchFileSFTPException, SFTPException{
        return get(remote, null);
    }

    public File get(String remote, String local) throws NoSuchFileSFTPException, SFTPException{
        if(channel == null){
            throw new SFTPException(sftpClientFactoryServiceName, "Connection is not established!");
        }
        try{
            if(local == null){
                local = lpwd().getPath();
            }
            channel.get(remote, local);
            File remoteFile = new File(remote);
            File localFile = new File(local);
            return localFile.isDirectory() ? new File(localFile, remoteFile.getName()) : localFile;
        }catch(SftpException e){
            if(e.id == ERROR_ID_NO_SUCH_FILE){
                throw new NoSuchFileSFTPException(sftpClientFactoryServiceName, "It failed to get! remote=" + remote + ", local=" + local, e);
            }
            throw new SFTPException(sftpClientFactoryServiceName, "It failed to get! remote=" + remote + ", local=" + local, e);
        }
    }

    public File[] mget(String remote) throws NoSuchFileSFTPException, SFTPException{
        return mget(remote, null);
    }

    public File[] mget(String remote, String localDir) throws NoSuchFileSFTPException, SFTPException{
        if(channel == null){
            throw new SFTPException(sftpClientFactoryServiceName, "Connection is not established!");
        }
        try{
            String[] fileNames = ls(remote);
            if(fileNames.length == 0){
                return new File[0];
            }
            if(localDir == null){
                localDir = lpwd().getPath();
            }
            channel.get(remote, localDir);
            File[] files = new File[fileNames.length];
            for(int i = 0; i < fileNames.length; i++){
                files[i] = new File(localDir, fileNames[i]);
            }
            return files;
        }catch(SftpException e){
            if(e.id == ERROR_ID_NO_SUCH_FILE){
                throw new NoSuchFileSFTPException(sftpClientFactoryServiceName, "It failed to mget! remote=" + remote + ", localDir=" + localDir, e);
            }
            throw new SFTPException(sftpClientFactoryServiceName, "It failed to mget! remote=" + remote + ", localDir=" + localDir, e);
        }
    }

    public void put(String local) throws SFTPException{
        put(local, null);
    }

    public void put(String local, String remote) throws SFTPException{
        put(local, remote, null);
    }

    public void put(String local, String remote, String mode) throws SFTPException{
        if(channel == null){
            throw new SFTPException(sftpClientFactoryServiceName, "Connection is not established!");
        }
        try{
            channel.put(local, remote == null ? "." : remote, mode == null ? 644 : Integer.parseInt(mode));
        }catch(NumberFormatException e){
            throw new SFTPException(sftpClientFactoryServiceName, "Mode must be number! mode=" + mode, e);
        }catch(SftpException e){
            throw new SFTPException(sftpClientFactoryServiceName, "It failed to put! local=" + local + ", remote=" + remote, e);
        }
    }

    public void mput(String local) throws SFTPException{
        mput(local, ".");
    }

    public void mput(String local, String remoteDir) throws SFTPException{
        mput(local, remoteDir, null);
    }

    public void mput(String local, String remoteDir, String mode) throws SFTPException{
        if(channel == null){
            throw new SFTPException(sftpClientFactoryServiceName, "Connection is not established!");
        }
        if(remoteDir == null){
            remoteDir = "./";
        }else if(!remoteDir.endsWith("/")){
            remoteDir += "/";
        }
        RecurciveSearchFile rsf = new RecurciveSearchFile(lpwd().getPath());
        File[] localFiles = rsf.listAllTreeFiles(local);
        for(int i = 0; i < localFiles.length; i++){
            if(mode == null){
                put(localFiles[i].getAbsolutePath(), remoteDir + localFiles[i].getName());
            }else{
                put(localFiles[i].getAbsolutePath(), remoteDir + localFiles[i].getName(), mode);
            }
        }
    }

    public boolean rm(String path) throws SFTPException{
        if(channel == null){
            throw new SFTPException(sftpClientFactoryServiceName, "Connection is not established!");
        }
        try{
            channel.rm(path);
        }catch(SftpException e){
            if(e.id == ERROR_ID_NO_SUCH_FILE){
                return false;
            }
            throw new SFTPException(sftpClientFactoryServiceName, "It failed to rmove! path=" + path, e);
        }
        return true;
    }

    public boolean rmdir(String path) throws SFTPException{
        if(channel == null){
            throw new SFTPException(sftpClientFactoryServiceName, "Connection is not established!");
        }
        try{
            channel.rmdir(path);
        }catch(SftpException e){
            if(e.id == ERROR_ID_NO_SUCH_FILE){
                return false;
            }
            throw new SFTPException(sftpClientFactoryServiceName, "It failed to rmove! path=" + path, e);
        }
        return true;
    }

    public void chmod(String mode, String path) throws NoSuchFileSFTPException, SFTPException{
        if(channel == null){
            throw new SFTPException(sftpClientFactoryServiceName, "Connection is not established!");
        }
        try{
            channel.chmod(Integer.parseInt(mode), path);
        }catch(NumberFormatException e){
            throw new SFTPException(sftpClientFactoryServiceName, "Mode must be number! mode=" + mode, e);
        }catch(SftpException e){
            if(e.id == ERROR_ID_NO_SUCH_FILE){
                throw new NoSuchFileSFTPException(sftpClientFactoryServiceName, "It failed to chmod! mode=" + mode + ", path=" + path, e);
            }
            throw new SFTPException(sftpClientFactoryServiceName, "It failed to chmod! mode=" + mode + ", path=" + path, e);
        }
    }

    public void chown(String uid, String path) throws NoSuchFileSFTPException, SFTPException{
        if(channel == null){
            throw new SFTPException(sftpClientFactoryServiceName, "Connection is not established!");
        }
        try{
            channel.chown(Integer.parseInt(uid), path);
        }catch(NumberFormatException e){
            throw new SFTPException(sftpClientFactoryServiceName, "uid must be number! uid=" + uid, e);
        }catch(SftpException e){
            if(e.id == ERROR_ID_NO_SUCH_FILE){
                throw new NoSuchFileSFTPException(sftpClientFactoryServiceName, "It failed to chown! uid=" + uid + ", path=" + path, e);
            }
            throw new SFTPException(sftpClientFactoryServiceName, "It failed to chown! uid=" + uid + ", path=" + path, e);
        }
    }

    public void chgrp(String gid, String path) throws NoSuchFileSFTPException, SFTPException{
        if(channel == null){
            throw new SFTPException(sftpClientFactoryServiceName, "Connection is not established!");
        }
        try{
            channel.chgrp(Integer.parseInt(gid), path);
        }catch(NumberFormatException e){
            throw new SFTPException(sftpClientFactoryServiceName, "gid must be number! gid=" + gid, e);
        }catch(SftpException e){
            if(e.id == ERROR_ID_NO_SUCH_FILE){
                throw new NoSuchFileSFTPException(sftpClientFactoryServiceName, "It failed to chgrp! gid=" + gid + ", path=" + path, e);
            }
            throw new SFTPException(sftpClientFactoryServiceName, "It failed to chgrp! gid=" + gid + ", path=" + path, e);
        }
    }

    public void symlink(String path, String link) throws NoSuchFileSFTPException, SFTPException{
        if(channel == null){
            throw new SFTPException(sftpClientFactoryServiceName, "Connection is not established!");
        }
        try{
            channel.symlink(path, link);
        }catch(SftpException e){
            if(e.id == ERROR_ID_NO_SUCH_FILE){
                throw new NoSuchFileSFTPException(sftpClientFactoryServiceName, "It failed to symlink! path=" + path + ", link=" + link, e);
            }
            throw new SFTPException(sftpClientFactoryServiceName, "It failed to symlink! path=" + path + ", link=" + link, e);
        }
    }
    
    public void ln(String path, String link) throws NoSuchFileSFTPException, SFTPException{
        if(channel == null){
            throw new SFTPException(sftpClientFactoryServiceName, "Connection is not established!");
        }
        try{
            channel.hardlink(path, link);
        }catch(SftpException e){
            if(e.id == ERROR_ID_NO_SUCH_FILE){
                throw new NoSuchFileSFTPException(sftpClientFactoryServiceName, "It failed to ln! path=" + path + ", link=" + link, e);
            }
            throw new SFTPException(sftpClientFactoryServiceName, "It failed to ln! path=" + path + ", link=" + link, e);
        }
    }

    public void close() throws SFTPException{
        if(semaphore != null){
            semaphore.freeResource();
        }
        if(channel != null){
            channel.disconnect();
            channel = null;
        }
        if(session != null){
            session.disconnect();
            session = null;
        }
        jsch = null;
    }

    protected static class SFTPFileImpl implements SFTPFile{

        protected ChannelSftp.LsEntry entry;

        public SFTPFileImpl(ChannelSftp.LsEntry entry){
            this.entry = entry;
        }

        public String getName(){
            return entry.getFilename();
        }

        public int getUserId(){
            return entry.getAttrs().getUId();
        }

        public int getGroupId(){
            return entry.getAttrs().getGId();
        }

        public int getPermissions(){
            return entry.getAttrs().getPermissions();
        }

        public Date getLastAccessTime(){
            return new Date(entry.getAttrs().getATime() * 1000l);
        }

        public Date getLastModificationTime(){
            return new Date(entry.getAttrs().getMTime() * 1000l);
        }

        public boolean isDirectory(){
            return entry.getAttrs().isDir();
        }

        public boolean isLink(){
            return entry.getAttrs().isLink();
        }

        public long size(){
            return entry.getAttrs().getSize();
        }

        public boolean equals(Object obj){
            if(obj == null || !(obj instanceof SFTPFile)){
                return false;
            }
            if(obj == this){
                return true;
            }
            SFTPFile file = (SFTPFile)obj;
            return getName().equals(file.getName());
        }

        public int hashCode(){
            return getName().hashCode();
        }
    }
}