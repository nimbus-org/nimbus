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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.io.RecurciveSearchFile;
import jp.ossc.nimbus.service.test.StubResourceManager;

/**
 * ローカルディスクを経由してスタブリソースを管理する{@link StubResourceManager}インタフェースの実装サービス。<p>
 * 
 * @author M.Aono
 */
public class LocalStubResourceManagerService extends ServiceBase implements StubResourceManager, LocalStubResourceManagerServiceMBean{
    
    private static final long serialVersionUID = -2154792864362378027L;
    
    private File temporaryDirectory;
    private File internalTemporaryDirectory;
    
    public File getTemporaryDirectory() {
        return temporaryDirectory;
    }
    
    public void setTemporaryDirectory(File path) {
        this.temporaryDirectory = path;
    }
    
    public void startService() throws Exception{
        if(temporaryDirectory == null){
            temporaryDirectory = new File(System.getProperty("java.io.tmpdir"));
        }
        internalTemporaryDirectory = new File(temporaryDirectory, getClass().getName());
        if(!internalTemporaryDirectory.exists()){
            if(!internalTemporaryDirectory.mkdirs()){
                throw new IOException("TemporaryDirectory can not make. path=" + internalTemporaryDirectory);
            }
        }
        internalTemporaryDirectory.deleteOnExit();
        String tmpDirName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        long count = 0;
        File tmpDir = null;
        do{
            tmpDirName += count++;
            tmpDir = new File(internalTemporaryDirectory, tmpDirName);
        }while(tmpDir.exists());
        if(!tmpDir.mkdir()){
            throw new IOException("TemporaryDirectory can not make. path=" + tmpDir);
        }
        tmpDir.deleteOnExit();
        internalTemporaryDirectory = tmpDir;
    }
    
    public void uploadScenarioResource(File dir, String scenarioGroupId, String scenarioId, String stubId) throws Exception{
        uploadResource(dir, new String[]{scenarioGroupId, scenarioId, stubId});
    }
    
    public void uploadTestCaseResource(File dir, String scenarioGroupId, String scenarioId, String testcaseId, String stubId) throws Exception {
        uploadResource(dir, new String[]{scenarioGroupId, scenarioId, testcaseId, stubId});
    }
    
    private void uploadResource(File dir, String[] dirs) throws Exception{
        if(!dir.exists()){
            throw new Exception("UploadDirectory is not existed.");
        }
        RecurciveSearchFile subDir = new RecurciveSearchFile(internalTemporaryDirectory);
        for(int i = 0, imax = dirs.length; i < imax; i++){
            subDir = new RecurciveSearchFile(subDir, dirs[i]);
            if(i == imax - 1 && subDir.exists()){
                subDir.deleteAllTree();
            }
            if(subDir.mkdir()){
                subDir.deleteOnExit();
            }
        }
        RecurciveSearchFile uploadDir = new RecurciveSearchFile(dir.getAbsolutePath());
        uploadDir.copyAllTree(subDir);
        subDir.deleteOnExitAllTree();
    }
    
    public void downloadScenarioResource(File dir, String scenarioGroupId, String scenarioId, String stubId) throws Exception{
        downloadResource(dir, new String[]{scenarioGroupId, scenarioId, stubId});
    }
    
    public void downloadTestCaseResource(File dir, String scenarioGroupId, String scenarioId, String testcaseId, String stubId) throws Exception {
        downloadResource(dir, new String[]{scenarioGroupId, scenarioId, testcaseId, stubId});
    }
    
    private void downloadResource(File dir, String[] dirs) throws Exception{
        if(!dir.exists()){
            dir.mkdirs();
        }
        RecurciveSearchFile subDir = new RecurciveSearchFile(internalTemporaryDirectory);
        for(int i = 0, imax = dirs.length; i < imax; i++){
            subDir = new RecurciveSearchFile(subDir, dirs[i]);
        }
        if(!subDir.exists()){
            throw new Exception("ScenarioResource not found." + subDir);
        }
        subDir.copyAllTree(dir);
        subDir.deleteAllTree();
    }
}
