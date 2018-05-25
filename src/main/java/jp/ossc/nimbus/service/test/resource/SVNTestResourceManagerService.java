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
package jp.ossc.nimbus.service.test.resource;

import java.io.File;

import org.apache.maven.scm.ScmBranch;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmTag;
import org.apache.maven.scm.ScmVersion;
import org.apache.maven.scm.command.checkout.CheckOutScmResult;
import org.apache.maven.scm.command.update.UpdateScmResult;
import org.apache.maven.scm.log.DefaultLog;
import org.apache.maven.scm.log.ScmLogger;
import org.apache.maven.scm.manager.BasicScmManager;
import org.apache.maven.scm.manager.NoSuchScmProviderException;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.provider.ScmProvider;
import org.apache.maven.scm.provider.svn.svnexe.SvnExeScmProvider;
import org.apache.maven.scm.repository.ScmRepository;
import org.apache.maven.scm.repository.ScmRepositoryException;

import jp.ossc.nimbus.io.RecurciveSearchFile;
import jp.ossc.nimbus.service.test.TestResourceManager;

/**
 * SVNサーバで管理されたテストリソースを使用する{@link TestResourceManager}インタフェースの実装サービス。
 * <p>
 *
 * scmUrlは以下の仕様となる。<br>
 *
 * <pre>
 * scm:svn:svn://[username[:password]@]server_name[:port]/path_to_repository
 * </pre>
 *
 * <pre>
 * scm:svn:svn+ssh://[username@]server_name[:port]/path_to_repository
 * </pre>
 *
 * <pre>
 * scm:svn:file://[hostname]/path_to_repository
 * </pre>
 *
 * <pre>
 * scm:svn:http://[username[:password]@]server_name[:port]/path_to_repository
 * </pre>
 *
 * <pre>
 * scm:svn:https://[username[:password]@]server_name[:port]/path_to_repository
 * </pre>
 *
 * @author M.Ishida
 *
 */
public class SVNTestResourceManagerService extends LocalTestResourceManagerService implements TestResourceManager, SVNTestResourceManagerServiceMBean {

    private static final long serialVersionUID = -7739613842364386865L;
    
    protected String protocol;
    protected String userName;
    protected String password;
    protected String serverName;
    protected int port = -1;
    protected String repositoryPath;
    protected String modulePath;
    protected File svnCheckOutDirectory;
    protected File temporaryDirectory;
    protected String targetBranch;
    protected String targetTag;

    protected ScmManager manager;
    protected ScmProvider provider;
    protected ScmRepository repository;
    protected ScmVersion branch;
    protected ScmVersion tag;

    protected boolean isDebugEnabled = false;
    protected boolean isInfoEnabled = true;
    protected boolean isWarnEnabled = true;
    protected boolean isErrorEnabled = true;

    public String getProtocol() {
        return protocol;
    }
    
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String user) {
        userName = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String str) {
        password = str;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String server) {
        serverName = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getRepositoryPath() {
        return repositoryPath;
    }

    public void setRepositoryPath(String path) {
        repositoryPath = path;
    }

    public String getModulePath() {
        return modulePath;
    }

    public void setModulePath(String module) {
        modulePath = module;
    }

    public File getSvnCheckOutDirectory() {
        return svnCheckOutDirectory;
    }

    public void setSvnCheckOutDirectory(File directory) {
        svnCheckOutDirectory = directory;
    }

    public String getTargetBranch() {
        return targetBranch;
    }

    public void setTargetBranch(String branch) {
        targetBranch = branch;
    }

    public String getTargetTag() {
        return targetTag;
    }

    public void setTargetTag(String tag) {
        targetTag = tag;
    }

    public boolean isDebugEnabled() {
        return isDebugEnabled;
    }

    public void setDebugEnabled(boolean enabled) {
        isDebugEnabled = enabled;
    }

    public boolean isInfoEnabled() {
        return isInfoEnabled;
    }

    public void setInfoEnabled(boolean enabled) {
        isInfoEnabled = enabled;
    }

    public boolean isWarnEnabled() {
        return isWarnEnabled;
    }

    public void setWarnEnabled(boolean enabled) {
        isWarnEnabled = enabled;
    }

    public boolean isErrorEnabled() {
        return isErrorEnabled;
    }

    public void setErrorEnabled(boolean enabled) {
        isErrorEnabled = enabled;
    }

    public File getTemporaryDirectory() {
        return temporaryDirectory;
    }
    
    public void setTemporaryDirectory(File path) {
        temporaryDirectory = path;
    }
    
    public void createService() throws Exception {
        manager = new CustomScmManager();
        provider = new SvnExeScmProvider();
        manager.setScmProvider("svn", provider);
    }

    public void startService() throws Exception {
        super.startService();
        if (repositoryPath == null) {
            throw new IllegalArgumentException("RepositoryPath must be specified.");
        }
        if (!PROTOCOL_SVN.equals(protocol) && !PROTOCOL_HTTP.equals(protocol) && !PROTOCOL_HTTPS.equals(protocol) && !PROTOCOL_SVN_SSH.equals(protocol)
                && !PROTOCOL_FILE.equals(protocol)) {
            throw new IllegalArgumentException("Protocol is illegal value. protocol=" + protocol);
        }
        if (!PROTOCOL_FILE.equals(protocol) && serverName == null) {
            throw new IllegalArgumentException("ServerName must be specified.");
        }
        if (targetBranch != null && targetTag != null) {
            throw new IllegalArgumentException("TargetBranch and TargetTag can not be specified at the same time.");
            
        }
        if (svnCheckOutDirectory == null) {
            throw new IllegalArgumentException("SvnCheckOutDirectory must be specified.");
        }
        if (targetBranch != null) {
            branch = new ScmBranch(targetBranch);
        }
        if (targetTag != null) {
            tag = new ScmTag(targetTag);
        }
        if (temporaryDirectory == null) {
            temporaryDirectory = new File(System.getProperty("java.io.tmpdir") + "/gitresource/");
        }
        if (!temporaryDirectory.exists()) {
            temporaryDirectory.mkdirs();
        } else {
            RecurciveSearchFile.deleteAllTree(temporaryDirectory, false);
        }
        repository = getRepository();
    }

    public void checkOut() throws Exception {
        if (svnCheckOutDirectory.exists()) {
            RecurciveSearchFile.deleteAllTree(svnCheckOutDirectory, false);
        } else {
            svnCheckOutDirectory.mkdirs();
        }
        checkOutInternal();
    }

    public String[] getScenarioGroupIds() throws Exception {
        update();
        return super.getScenarioGroupIds();
    }

    public String[] getScenarioIds(String groupId) throws Exception {
        update();
        return super.getScenarioIds(groupId);

    }

    public String[] getTestCaseIds(String groupId, String scenarioId) throws Exception {
        update();
        return super.getTestCaseIds(groupId, scenarioId);
    }

    public String[] getStubIds(String groupId, String scenarioId, String testcaseId) throws Exception {
        update();
        return super.getStubIds(groupId, scenarioId, testcaseId);

    }

    public void downloadScenarioGroupResource(File dir, String groupId) throws Exception {
        update();
        super.downloadScenarioGroupResource(dir, groupId);
    }
    
    public void uploadScenarioGroupResource(File dir, String scenarioGroupId) throws Exception {
        throw new UnsupportedOperationException("This method is not supported");
    }

    public void downloadScenarioResource(File dir, String groupId, String scenarioId) throws Exception {
        update();
        super.downloadScenarioResource(dir, groupId, scenarioId);
    }

    public void uploadScenarioResource(File dir, String scenarioGroupId, String scenarioId) throws Exception {
        throw new UnsupportedOperationException("This method is not supported");
    }
    
    protected ScmRepository getRepository() throws ScmRepositoryException,
            NoSuchScmProviderException {
        StringBuilder scmUrlBuff = new StringBuilder();
        scmUrlBuff.append("scm:svn:" + protocol + "://");
        if (userName != null) {
            scmUrlBuff.append(userName);
            if (password != null) {
                scmUrlBuff.append(":" + password);
            }
            scmUrlBuff.append("@");
        }
        if (serverName != null) {
            scmUrlBuff.append(serverName);
        }
        if (!PROTOCOL_FILE.equals(protocol) && port != -1) {
            scmUrlBuff.append(":" + port);
        }
        scmUrlBuff.append("/" + repositoryPath);
        return manager.makeScmRepository(scmUrlBuff.toString());
    }

    protected void update() throws Exception {
        UpdateScmResult result = null;
        if (branch != null) {
            result = manager.update(repository, new ScmFileSet(temporaryDirectory), branch);
        } else if (tag != null) {
            result = manager.update(repository, new ScmFileSet(temporaryDirectory), tag);
        } else {
            result = manager.update(repository, new ScmFileSet(temporaryDirectory));
        }
        if (result == null) {
            throw new Exception("SVN UpdateScmResult is null.");
        }
        if (!result.isSuccess()) {
            throw new Exception("SVN UpdateScmResult is failed. CommandOutput=" + result.getCommandOutput() + " ProviderMessage="
                    + result.getProviderMessage());
        }
        RecurciveSearchFile.copyAllTree(new File(temporaryDirectory, modulePath), svnCheckOutDirectory);
    }

    protected void checkOutInternal() throws Exception {
        CheckOutScmResult result = null;
        RecurciveSearchFile.deleteAllTree(temporaryDirectory, false);
        if (branch != null) {
            result = manager.checkOut(repository, new ScmFileSet(temporaryDirectory), branch, true);
        } else if (tag != null) {
            result = manager.checkOut(repository, new ScmFileSet(temporaryDirectory), tag, true);
        } else {
            result = manager.checkOut(repository, new ScmFileSet(temporaryDirectory), true);
        }
        if (result == null) {
            throw new Exception("SVN CheckOutScmResult is null.");
        }
        if (!result.isSuccess()) {
            throw new Exception("SVN CheckOutScmResult is failed. CommandOutput=" + result.getCommandOutput() + " ProviderMessage="
                    + result.getProviderMessage());
        }
        RecurciveSearchFile.copyAllTree(new File(temporaryDirectory, modulePath), svnCheckOutDirectory);
    }

    public class CustomScmManager extends BasicScmManager {
        protected ScmLogger getScmLogger() {
            return new CustomLog();
        }
    }

    public class CustomLog extends DefaultLog {

        public boolean isDebugEnabled() {
            return isDebugEnabled;
        }

        public boolean isInfoEnabled() {
            return isInfoEnabled;
        }

        public boolean isWarnEnabled() {
            return isWarnEnabled;
        }

        public boolean isErrorEnabled() {
            return isErrorEnabled;
        }
    }

}
