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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceLoader;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceMetaData;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.io.ExtentionFileFilter;
import jp.ossc.nimbus.io.RecurciveSearchFile;
import jp.ossc.nimbus.service.test.TemplateEngine;
import jp.ossc.nimbus.service.test.TestResourceManager;
import jp.ossc.nimbus.service.test.UploadableTestResourceManager;

/**
 * ローカルディスクのテストリソースを管理する{@link TestResourceManager}インタフェースの実装サービス。
 * <p>
 *
 * @author M.Aono
 */
public class LocalTestResourceManagerService extends ServiceBase implements UploadableTestResourceManager, LocalTestResourceManagerServiceMBean {

    private static final long serialVersionUID = 2415695763380446884L;

    protected File testResourceDirectory;
    protected String[] excludeFilterRegexs;
    protected ServiceName templateEngineServiceName;
    protected TemplateEngine templateEngine;
    protected String templateLinkFileExtention = DEFAULT_TEMPLATE_LINK_FILE_EXTENTION;
    protected String templateLinkFileEncoding;

    public File getTestResourceDirectory() {
        return testResourceDirectory;
    }

    public void setTestResourceDirectory(File path) {
        testResourceDirectory = path;
    }

    public String[] getExcludeFilterRegexs() {
        return excludeFilterRegexs;
    }

    public void setExcludeFilterRegexs(String[] excludeFilterRegexs) {
        this.excludeFilterRegexs = excludeFilterRegexs;
    }

    public void setTemplateEngineServiceName(ServiceName name) {
        templateEngineServiceName = name;
    }

    public ServiceName getTemplateEngineServiceName() {
        return templateEngineServiceName;
    }

    public void setTemplateLinkFileExtention(String ext) {
        if (ext.charAt(0) != '.') {
            ext = '.' + ext;
        }
        templateLinkFileExtention = ext;
    }

    public String getTemplateLinkFileExtention() {
        return templateLinkFileExtention;
    }

    public void setTemplateLinkFileEncoding(String encoding) {
        templateLinkFileEncoding = encoding;
    }

    public String getTemplateLinkFileEncoding() {
        return templateLinkFileEncoding;
    }

    public void setTemplateEngine(TemplateEngine engine) {
        templateEngine = engine;
    }

    public void startService() throws Exception {
        File serviceDefDir = null;
        if (getServiceNameObject() != null) {
            ServiceMetaData metaData = ServiceManagerFactory.getServiceMetaData(getServiceNameObject());
            if (metaData != null) {
                ServiceLoader loader = metaData.getServiceLoader();
                if (loader != null) {
                    String filePath = loader.getServiceURL().getFile();
                    if (filePath != null) {
                        serviceDefDir = new File(filePath).getParentFile();
                    }
                }
            }
        }
        if (testResourceDirectory == null) {
            testResourceDirectory = serviceDefDir == null ? new File(".") : serviceDefDir;
        } else if (!testResourceDirectory.isAbsolute() && !testResourceDirectory.exists() && serviceDefDir != null) {
            testResourceDirectory = new File(serviceDefDir, testResourceDirectory.getPath());
        }
        if (!testResourceDirectory.exists()) {
            testResourceDirectory.mkdirs();
        }

        if (templateEngineServiceName != null) {
            templateEngine = (TemplateEngine) ServiceManagerFactory.getServiceObject(templateEngineServiceName);
        }
    }

    public void checkOut() throws Exception {
    }

    public String[] getScenarioGroupIds() throws Exception {

        File[] files = testResourceDirectory.listFiles(new FileFilter());
        List scenarioGroupList = new ArrayList();
        if(files == null) {
            return new String[] {};
        } else {
            for (int index = 0; index < files.length; index++) {
                File file = files[index];
                if (file.isDirectory()) {
                    scenarioGroupList.add(file.getName());
                }
            }
            String[] scenarioGroups = new String[scenarioGroupList.size()];
            for (int index = 0; index < scenarioGroupList.size(); index++) {
                scenarioGroups[index] = (String) scenarioGroupList.get(index);
            }
            if (scenarioGroups != null) {
                Arrays.sort(scenarioGroups);
            }
            return scenarioGroups;
        }
    }

    public String[] getScenarioIds(String scenarioGroupId) throws Exception {

        File testResourceFiles = new File(testResourceDirectory, scenarioGroupId);
        File[] files = testResourceFiles.listFiles(new FileFilter());
        if (files == null) {
            return null;
        }
        List scenarioList = new ArrayList();
        for (int index = 0; index < files.length; index++) {
            File file = files[index];
            if (file.isDirectory()) {
                scenarioList.add(file.getName());
            }
        }

        String[] scenarios = new String[scenarioList.size()];
        for (int index = 0; index < scenarioList.size(); index++) {
            scenarios[index] = (String) scenarioList.get(index);
        }

        if (scenarios != null) {
            Arrays.sort(scenarios);
        }

        return scenarios;
    }

    public String[] getTestCaseIds(String scenarioGroupId, String scenarioId) throws Exception {

        File testResourceFiles = new File(testResourceDirectory, scenarioGroupId + "/" + scenarioId);
        File[] files = testResourceFiles.listFiles(new FileFilter());
        if (files == null) {
            return null;
        }
        List testCaseList = new ArrayList();
        for (int index = 0; index < files.length; index++) {
            File file = files[index];
            if (file.isDirectory()) {
                testCaseList.add(file.getName());
            }
        }

        String[] testCases = new String[testCaseList.size()];
        for (int index = 0; index < testCaseList.size(); index++) {
            testCases[index] = (String) testCaseList.get(index);
        }

        if (testCases != null) {
            Arrays.sort(testCases);
        }

        return testCases;

    }

    public String[] getStubIds(String scenarioGroupId, String scenarioId, String testcaseId) throws Exception {

        File testResourceFiles = new File(testResourceDirectory, "/" + scenarioGroupId + "/" + scenarioId + "/" + testcaseId);
        File[] files = testResourceFiles.listFiles(new FileFilter());
        if (files == null) {
            return null;
        }
        List stubList = new ArrayList();
        for (int index = 0; index < files.length; index++) {
            File file = files[index];
            if (file.isDirectory()) {
                stubList.add(file.getName());
            }
        }

        String[] stubIds = (String[]) stubList.toArray();

        if (stubIds != null) {
            Arrays.sort(stubIds);
        }

        return stubIds;
    }

    public void downloadScenarioGroupResource(File dir, String scenarioGroupId) throws Exception {

        File resourceFiles = new File(testResourceDirectory, "/" + scenarioGroupId);
        File[] files = resourceFiles.listFiles(new FileFilter());
        if (files == null) {
            return;
        }
        for (int index = 0; index < files.length; index++) {
            File file = files[index];
            if (file.isFile()) {
                File copyFile = new File(dir, file.getName());
                FileInputStream fis = new FileInputStream(file);
                FileOutputStream fos = new FileOutputStream(copyFile);
                try {
                    byte[] buf = new byte[1024];
                    int i = 0;
                    while ((i = fis.read(buf)) != -1) {
                        fos.write(buf, 0, i);
                    }
                } catch (Exception e) {
                    throw e;
                } finally {
                    if (fis != null)
                        fis.close();
                    if (fos != null)
                        fos.close();
                }
            }
        }
        linkTemplate(dir, false);
    }

    public void uploadScenarioGroupResource(File dir, String scenarioGroupId, boolean isClear) throws Exception {
        File targetDir = new File(testResourceDirectory, "/" + scenarioGroupId);
        if(targetDir.exists()) {
            if(isClear){
                File[] fileList = targetDir.listFiles();
                if(fileList != null && fileList.length > 0) {
                    for(int i = 0; i < fileList.length; i++) {
                        if(fileList[i].isFile()) {
                            fileList[i].delete(); 
                        }
                    }
                }
            }
        } else {
            targetDir.mkdirs(); 
        }
        RecurciveSearchFile.copyAllTree(dir, targetDir, new FileFilter());
    }
    
    protected void linkTemplate(File dir, boolean isRecurcive) throws Exception {
        File[] linkFiles = null;
        if (isRecurcive) {
            RecurciveSearchFile targetDir = new RecurciveSearchFile(dir);
            linkFiles = targetDir.listAllTreeFiles(new ExtentionFileFilter(templateLinkFileExtention));
        } else {
            linkFiles = dir.listFiles(new ExtentionFileFilter(templateLinkFileExtention));
        }
        if (linkFiles != null) {
            Set deleteDataFiles = new HashSet();
            for (int i = 0; i < linkFiles.length; i++) {
                if (linkFiles[i].isDirectory()) {
                    continue;
                }
                createTemplateFile(linkFiles[i], deleteDataFiles);
            }
            Iterator dataFiles = deleteDataFiles.iterator();
            while (dataFiles.hasNext()) {
                ((File) dataFiles.next()).delete();
            }
        }
    }


    protected void createTemplateFile(File linkFile, Set deleteDataFiles) throws Exception {
        if(!linkFile.exists()){
            return;
        }
        final String outputFilePath = linkFile.getPath();
        final File outputFile = new File(outputFilePath.substring(0, outputFilePath.length() - templateLinkFileExtention.length()));
        BufferedReader br = new BufferedReader(templateLinkFileEncoding == null ? new FileReader(linkFile) : new InputStreamReader(
                new FileInputStream(linkFile), templateLinkFileEncoding));
        File tmplateFile = null;
        File dataFile = null;
        try {
            final String tmplateFilePath = br.readLine();
            if (tmplateFilePath == null) {
                throw new Exception("Unexpected EOF on tmplateFilePath");
            }
            tmplateFile = new File(tmplateFilePath);
            final String dataFilePath = br.readLine();
            if (dataFilePath != null && dataFilePath.length() != 0) {
                dataFile = new File(dataFilePath);
                if (!dataFile.exists() && linkFile.getParentFile() != null) {
                    dataFile = new File(linkFile.getParentFile(), dataFilePath);
                }
                if (!dataFile.exists()) {
                    File linkDataFile = new File(dataFilePath + templateLinkFileExtention);
                    if(linkDataFile.exists()){
                        createTemplateFile(linkDataFile, deleteDataFiles);
                    }
                }
                if (!dataFile.exists() && linkFile.getParentFile() != null) {
                    File linkDataFile = new File(linkFile.getParentFile(), dataFilePath + templateLinkFileExtention);
                    if(linkDataFile.exists()){
                        createTemplateFile(linkDataFile, deleteDataFiles);
                    }
                }
                if (!dataFile.exists()) {
                    return;
                }
            }
        } finally {
            br.close();
            br = null;
        }
        if (templateEngine == null) {
            throw new UnsupportedOperationException("TmplateEngine is null.");
        }
        try{
            templateEngine.transform(tmplateFile, dataFile, outputFile, templateLinkFileEncoding);
        }catch(Exception e){
            throw new Exception("Template trasform error. tmplateFile=" + tmplateFile + ", dataFile=" + dataFile, e);
        }
        linkFile.delete();
        if (dataFile != null) {
            deleteDataFiles.add(dataFile);
        }
    }

    public void downloadScenarioResource(File dir, String scenarioGroupId, String scenarioId) throws Exception {
        RecurciveSearchFile testResourceFiles = new RecurciveSearchFile(testResourceDirectory, scenarioGroupId + "/" + scenarioId);
        testResourceFiles.copyAllTree(dir, new FileFilter());
        linkTemplate(dir, true);
    }

    public void uploadScenarioResource(File dir, String scenarioGroupId, String scenarioId, boolean isClear) throws Exception {
        RecurciveSearchFile targetDir = new RecurciveSearchFile(testResourceDirectory, scenarioGroupId + "/" + scenarioId);
        if(targetDir.exists()) {
            if(isClear){
                targetDir.deleteAllTree(false);
            }
        } else {
            targetDir.mkdirs(); 
        }
        RecurciveSearchFile.copyAllTree(dir, targetDir, new FileFilter());
    }
    
    public void uploadTestCaseResource(File dir, String scenarioGroupId, String scenarioId, String testcaseId, boolean isClear) throws Exception {
        RecurciveSearchFile targetDir = new RecurciveSearchFile(testResourceDirectory, scenarioGroupId + File.separator + scenarioId + File.separator + testcaseId);
        if(targetDir.exists()) {
            if(isClear){
                targetDir.deleteAllTree(false);
            }
        } else {
            targetDir.mkdirs(); 
        }
        RecurciveSearchFile.copyAllTree(dir, targetDir, new FileFilter());
    }
    
    protected class FileFilter implements FilenameFilter {

        public boolean accept(File dir, String name) {

            if (excludeFilterRegexs == null) {
                return true;
            }

            for (int index = 0; index < excludeFilterRegexs.length; index++) {
                if (name.matches(excludeFilterRegexs[index])) {
                    return false;
                }
            }

            return true;
        }
    }

}
