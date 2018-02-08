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

import jp.ossc.nimbus.io.RecurciveSearchFile;
import jp.ossc.nimbus.service.test.TestResourceManager;

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
import org.apache.maven.scm.provider.cvslib.cvsjava.CvsJavaScmProvider;
import org.apache.maven.scm.repository.ScmRepository;
import org.apache.maven.scm.repository.ScmRepositoryException;

/**
 * CVSサーバで管理されたテストリソースを使用する{@link TestResourceManager}インタフェースの実装サービス。
 * <p>
 *
 * scmUrlは以下の仕様となる。<br>
 *
 * <pre>
 * scm:cvs&lt;delimiter&gt;local&lt;delimiter&gt;path_to_repository&lt;delimiter&gt;module_name
 * </pre>
 *
 * <pre>
 * scm:cvs&lt;delimiter&gt;lserver&lt;delimiter&gt;[username@]servername[&lt;delimiter&gt;port]&lt;delimiter&gt;path_to_repository&lt;delimiter&gt;module_name
 * </pre>
 *
 * <pre>
 * scm:cvs&lt;delimiter&gt;pserver&lt;delimiter&gt;[username[&lt;delimiter&gt;password]@]servername[&lt;delimiter&gt;port]&lt;delimiter&gt;path_to_repository&lt;delimiter&gt;module_name
 * </pre>
 *
 * <pre>
 * scm:cvs&lt;delimiter&gt;ext&lt;delimiter&gt;[username@]servername[&lt;delimiter&gt;port]&lt;delimiter&gt;path_to_repository&lt;delimiter&gt;module_name
 * </pre>
 *
 * <pre>
 * scm:cvs&lt;delimiter&gt;sspi&lt;delimiter&gt;[username@]host&lt;delimiter&gt;path&lt;delimiter&gt;module
 * </pre>
 *
 * @author M.Ishida
 *
 */
public class CVSTestResourceManagerService extends LocalTestResourceManagerService implements TestResourceManager, CVSTestResourceManagerServiceMBean {

    private static final long serialVersionUID = -1903951590151650735L;

    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    protected String method;
    protected String userName;
    protected String password;
    protected String serverName;
    protected int port = -1;
    protected String repositoryPath;
    protected String modulePath;
    protected File cvsCheckOutDirectory;
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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
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

    public File getCvsCheckOutDirectory() {
        return cvsCheckOutDirectory;
    }

    public void setCvsCheckOutDirectory(File directory) {
        cvsCheckOutDirectory = directory;
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

    public void createService() throws Exception {
        manager = new CustomScmManager();
        provider = new CvsJavaScmProvider();
        manager.setScmProvider("cvs", provider);
    }

    public void startService() throws Exception {
        super.startService();
        if (repositoryPath == null) {
            throw new IllegalArgumentException("RepositoryPath must be specified.");
        }
        if (modulePath == null) {
            throw new IllegalArgumentException("ModulePath must be specified.");
        }
        if (!METHOD_EXT.equals(method) && !METHOD_LSERVER.equals(method) && !METHOD_LOCAL.equals(method) && !METHOD_PSERVER.equals(method)
                && !METHOD_SSPI.equals(method)) {
            throw new IllegalArgumentException("Method is illegal value. method=" + method);
        }
        if (targetBranch != null && targetTag != null) {
            throw new IllegalArgumentException("TargetBranch and TargetTag can not be specified at the same time.");

        }
        if (cvsCheckOutDirectory == null) {
            throw new IllegalArgumentException("CvsCheckOutDirectory must be specified.");
        }
        if (targetBranch != null) {
            branch = new ScmBranch(targetBranch);
        }
        if (targetTag != null) {
            tag = new ScmTag(targetTag);
        }
    }

    public void checkOut() throws Exception {
        if (cvsCheckOutDirectory.exists()) {
            RecurciveSearchFile.deleteAllTree(cvsCheckOutDirectory, false);
        } else {
            cvsCheckOutDirectory.mkdirs();
        }
        checkOutInternal(getRepository(null, null, null), cvsCheckOutDirectory, true);
    }

    public String[] getScenarioGroupIds() throws Exception {
        update(getRepository(null, null, null), testResourceDirectory);
        return super.getScenarioGroupIds();
    }

    public String[] getScenarioIds(String groupId) throws Exception {
        update(getRepository(groupId, null, null), new File(testResourceDirectory, groupId));
        return super.getScenarioIds(groupId);

    }

    public String[] getTestCaseIds(String groupId, String scenarioId) throws Exception {
        update(getRepository(groupId, scenarioId, null), new File(testResourceDirectory, groupId + FILE_SEPARATOR + scenarioId));
        return super.getTestCaseIds(groupId, scenarioId);
    }

    public String[] getStubIds(String groupId, String scenarioId, String testcaseId) throws Exception {
        update(getRepository(groupId, scenarioId, testcaseId), new File(testResourceDirectory, groupId + FILE_SEPARATOR + scenarioId + FILE_SEPARATOR
                + testcaseId));
        return super.getStubIds(groupId, scenarioId, testcaseId);

    }

    public void downloadScenarioGroupResource(File dir, String groupId) throws Exception {
        update(getRepository(groupId, null, null), new File(testResourceDirectory, groupId));
        super.downloadScenarioGroupResource(dir, groupId);
    }

    public void downloadScenarioResource(File dir, String groupId, String scenarioId) throws Exception {
        update(getRepository(groupId, scenarioId, null), new File(testResourceDirectory, groupId + FILE_SEPARATOR + scenarioId));
        super.downloadScenarioResource(dir, groupId, scenarioId);
    }

    protected ScmRepository getRepository(String scenarioGroupId, String scenarioId, String testcaseId) throws ScmRepositoryException,
            NoSuchScmProviderException {
        StringBuilder scmUrlBuff = new StringBuilder();
        scmUrlBuff.append("scm:cvs:");
        if (METHOD_EXT.equals(method) || METHOD_LSERVER.equals(method)) {
            if (serverName == null) {
                throw new IllegalArgumentException("ServerName must be specified.");
            }
            scmUrlBuff.append(method + ":");
            if (userName != null) {
                scmUrlBuff.append(userName + "@");
            }
            scmUrlBuff.append(serverName);
            if (port != -1) {
                scmUrlBuff.append(":" + port);
            }
            scmUrlBuff.append(":" + repositoryPath + ":" + modulePath);
        } else if (METHOD_LOCAL.equals(method)) {
            scmUrlBuff.append(method + ":" + repositoryPath + ":" + modulePath);
        } else if (METHOD_PSERVER.equals(method)) {
            if (serverName == null) {
                throw new IllegalArgumentException("ServerName must be specified.");
            }
            scmUrlBuff.append(method + ":");
            if (userName != null) {
                scmUrlBuff.append(userName);
                if (password != null) {
                    scmUrlBuff.append(":" + userName);
                }
                scmUrlBuff.append("@");
            }
            scmUrlBuff.append(serverName);
            if (port != -1) {
                scmUrlBuff.append(":" + port);
            }
            scmUrlBuff.append(":" + repositoryPath + ":" + modulePath);
        } else if (METHOD_SSPI.equals(method)) {
            if (serverName == null) {
                throw new IllegalArgumentException("ServerName must be specified.");
            }
            scmUrlBuff.append(method + ":");
            if (userName != null) {
                scmUrlBuff.append(userName + "@");
            }
            scmUrlBuff.append(serverName + ":" + repositoryPath + ":" + modulePath);
        }
        if (scenarioGroupId != null) {
            scmUrlBuff.append("/" + scenarioGroupId);
            if (scenarioId != null) {
                scmUrlBuff.append("/" + scenarioId);
                if (testcaseId != null) {
                    scmUrlBuff.append("/" + testcaseId);
                }
            }
        }
        return manager.makeScmRepository(scmUrlBuff.toString());
    }

    protected void update(ScmRepository repository, File updateDir) throws Exception {
        UpdateScmResult result = null;
        if (branch != null) {
            result = manager.update(repository, new ScmFileSet(updateDir), branch);
        } else if (tag != null) {
            result = manager.update(repository, new ScmFileSet(updateDir), tag);
        } else {
            result = manager.update(repository, new ScmFileSet(updateDir));
        }
        if (result == null) {
            throw new Exception("CVS UpdateScmResult is null.");
        }
        if (!result.isSuccess()) {
            throw new Exception("CVS UpdateScmResult is failed. CommandOutput=" + result.getCommandOutput() + " ProviderMessage="
                    + result.getProviderMessage());
        }
    }

    protected void checkOutInternal(ScmRepository repository, File checkOutDir, boolean isRecursive) throws Exception {
        CheckOutScmResult result = null;
        if (branch != null) {
            result = manager.checkOut(repository, new ScmFileSet(checkOutDir), branch, isRecursive);
        } else if (tag != null) {
            result = manager.checkOut(repository, new ScmFileSet(checkOutDir), tag, isRecursive);
        } else {
            result = manager.checkOut(repository, new ScmFileSet(checkOutDir), isRecursive);
        }
        if (result == null) {
            throw new Exception("CVS CheckOutScmResult is null.");
        }
        if (!result.isSuccess()) {
            throw new Exception("CVS CheckOutScmResult is failed. CommandOutput=" + result.getCommandOutput() + " ProviderMessage="
                    + result.getProviderMessage());
        }
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
