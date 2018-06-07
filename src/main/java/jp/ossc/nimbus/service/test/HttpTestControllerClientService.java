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
package jp.ossc.nimbus.service.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.io.RecurciveSearchFile;
import jp.ossc.nimbus.service.http.HttpClient;
import jp.ossc.nimbus.service.http.HttpClientFactory;
import jp.ossc.nimbus.service.http.HttpException;
import jp.ossc.nimbus.service.http.HttpRequest;
import jp.ossc.nimbus.service.http.HttpResponse;
import jp.ossc.nimbus.service.test.TestCase.TestCaseResource;
import jp.ossc.nimbus.service.test.TestScenario.TestScenarioResource;
import jp.ossc.nimbus.service.test.TestScenarioGroup.TestScenarioGroupResource;

/**
 * HTTPテストコントローラクライアント。
 * <p>
 *
 * @author M.Takata
 */
public class HttpTestControllerClientService extends ServiceBase implements TestController, HttpTestControllerClientServiceMBean {
    
    private ServiceName httpClientFactoryServiceName;
    private HttpClientFactory httpClientFactory;
    private String templateAction = "template";
    private String uploadAction = "upload";
    private String urlEncodeCharacterEncoding = "UTF-8";
    
    protected ServiceName testResourceManagerServiceName;
    protected TestResourceManager testResourceManager;
    
    protected File temporaryDirectory;
    protected File testResourceFileBaseDirectory;
    
    protected boolean isUploadEvidenceServer = false;
    
    public void setHttpClientFactoryServiceName(ServiceName name) {
        httpClientFactoryServiceName = name;
    }
    
    public ServiceName getHttpClientFactoryServiceName() {
        return httpClientFactoryServiceName;
    }
    
    public void setTemplateAction(String action) {
        templateAction = action;
    }
    
    public String getTemplateAction() {
        return templateAction;
    }
    
    public String getUploadAction() {
        return uploadAction;
    }
    
    public void setUploadAction(String action) {
        uploadAction = action;
    }
    
    public void setURLEncodeCharacterEncoding(String encoding) {
        urlEncodeCharacterEncoding = encoding;
    }
    
    public String getURLEncodeCharacterEncoding() {
        return urlEncodeCharacterEncoding;
    }
    
    public ServiceName getTestResourceManagerServiceName() {
        return testResourceManagerServiceName;
    }
    
    public void setTestResourceManagerServiceName(ServiceName serviceName) {
        testResourceManagerServiceName = serviceName;
    }
    
    public TestResourceManager getTestResourceManager() {
        return testResourceManager;
    }
    
    public void setTestResourceManager(TestResourceManager manager) {
        testResourceManager = manager;
    }
    
    public File getTemporaryDirectory() {
        return temporaryDirectory;
    }
    
    public void setTemporaryDirectory(File path) {
        temporaryDirectory = path;
    }
    
    public File getTestResourceFileBaseDirectory() {
        return testResourceFileBaseDirectory;
    }
    
    public void setTestResourceFileBaseDirectory(File dir) {
        testResourceFileBaseDirectory = dir;
    }
    
    public boolean isUploadEvidenceServer() {
        return isUploadEvidenceServer;
    }
    
    public void setUploadEvidenceServer(boolean isUpload) {
        isUploadEvidenceServer = isUpload;
    }
    
    public void startService() throws Exception {
        if (httpClientFactoryServiceName != null) {
            httpClientFactory = (HttpClientFactory) ServiceManagerFactory.getServiceObject(httpClientFactoryServiceName);
        }
        if (httpClientFactory == null) {
            throw new IllegalArgumentException("HttpClientFactory is null.");
        }
        httpClientFactory.createRequest(templateAction);
        if (testResourceManagerServiceName != null) {
            testResourceManager = (TestResourceManager) ServiceManagerFactory.getServiceObject(testResourceManagerServiceName);
        }
        if (testResourceManager != null) {
            if (temporaryDirectory == null) {
                temporaryDirectory = new File(System.getProperty("java.io.tmpdir"));
            }
            if (!temporaryDirectory.exists()) {
                temporaryDirectory.mkdirs();
            }
            httpClientFactory.createRequest(uploadAction);
            if (testResourceFileBaseDirectory == null) {
                throw new IllegalArgumentException("TestResourceFileBaseDirectory is null.");
            }
            setupDir(testResourceFileBaseDirectory, false);
        }
    }
    
    private Object upload(String action, Map params, File file) throws HttpException, Exception {
        return request(action, params, file);
    }
    
    private Object request(String action, Map params) throws HttpException, Exception {
        return request(action, params, null);
    }
    
    private Object request(String action, Map params, File file) throws HttpException, Exception {
        HttpClient client = httpClientFactory.createHttpClient();
        HttpRequest request = file == null ? httpClientFactory.createRequest(templateAction) : httpClientFactory.createRequest(uploadAction);
        String accept = request.getHeader("Accept");
        if (accept == null) {
            request.setHeader("Accept", "application/octet-stream");
        }
        String url = request.getURL();
        request.setURL(url + (url.endsWith("/") ? "" : "/") + action);
        if (params != null) {
            Iterator entries = params.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                Object value = entry.getValue();
                if (value != null && value.getClass().isArray()) {
                    String[] values = (String[]) value;
                    for (int i = 0; i < values.length; i++) {
                        values[i] = URLEncoder.encode(values[i], urlEncodeCharacterEncoding);
                    }
                    request.setParameters((String) entry.getKey(), values);
                } else {
                    request.setParameter((String) entry.getKey(), URLEncoder.encode((String) entry.getValue(), urlEncodeCharacterEncoding));
                }
            }
        }
        if (file != null) {
            request.setFileParameter("zipfile", file, file.getName(), null);
        }
        HttpResponse response = client.executeRequest(request);
        if (response.getStatusCode() != 200) {
            throw new HttpException("Illegal http status : " + response.getStatusCode());
        }
        String contentLengthStr = response.getHeader("Content-Length");
        int contentLength = 0;
        if (contentLengthStr != null) {
            contentLength = Integer.parseInt(contentLengthStr);
        }
        if (contentLength == 0) {
            return null;
        } else {
            String contentTypeStr = response.getHeader("Content-Type");
            if (contentTypeStr == null) {
                throw new HttpException("Content-Type is null.");
            }
            final MediaType mediaType = new MediaType(contentTypeStr);
            InputStream is = response.getInputStream();
            if ("application/octet-stream".equals(mediaType.getMediaType())) {
                ObjectInputStream ois = new ObjectInputStream(is);
                Object responseObj = ois.readObject();
                if (responseObj instanceof Exception) {
                    throw (Exception) responseObj;
                } else {
                    return responseObj;
                }
            } else if ("application/zip".equals(mediaType.getMediaType())) {
                return new Object[] { mediaType.getParameter("name"), is };
            } else {
                throw new HttpException("Unsupported Content-Type : " + mediaType.getMediaType());
            }
        }
    }
    
    public void setTestPhase(String phase) {
        Map params = new HashMap();
        params.put("phase", phase);
        try {
            request("setTestPhase", params);
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new UndeclaredThrowableException(e);
            }
        }
    }
    
    public String getTestPhase() {
        try {
            return (String) request("getTestPhase", null);
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new UndeclaredThrowableException(e);
            }
        }
    }
    
    public void startScenarioGroup(String userId, String scenarioGroupId) throws Exception {
        Map params = new HashMap();
        params.put("userId", userId);
        params.put("scenarioGroupId", scenarioGroupId);
        if (testResourceManager != null) {
            File targetDir = new File(testResourceFileBaseDirectory, scenarioGroupId);
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            } else {
                RecurciveSearchFile.deleteAllTree(targetDir, false);
            }
            testResourceManager.downloadScenarioGroupResource(targetDir, scenarioGroupId);
            File zipFile = new File(temporaryDirectory, scenarioGroupId + ".zip");
            createZipFile(targetDir, zipFile, false);
            upload("uploadScenarioGroupResource", params, zipFile);
            zipFile.delete();
            RecurciveSearchFile.deleteAllTree(temporaryDirectory, false);
        }
        request("startScenarioGroup", params);
    }
    
    public void endScenarioGroup() throws Exception {
        String scenarioGroupId = getCurrentScenarioGroup().getScenarioGroupId();
        request("endScenarioGroup", null);
        if (testResourceManager != null) {
            downloadScenarioGroupResult(testResourceFileBaseDirectory, scenarioGroupId, RESPONSE_FILE_TYPE_DEFAULT);
        }
    }
    
    public void startScenario(String userId, String scenarioId) throws Exception {
        Map params = new HashMap();
        params.put("userId", userId);
        params.put("scenarioId", scenarioId);
        if (testResourceManager != null) {
            String scenarioGroupId = getCurrentScenarioGroup().getScenarioGroupId();
            params.put("scenarioGroupId", scenarioGroupId);
            File targetDir = new File(testResourceFileBaseDirectory, scenarioGroupId + File.separator + scenarioId);
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            } else {
                RecurciveSearchFile.deleteAllTree(targetDir, false);
            }
            testResourceManager.downloadScenarioResource(targetDir, scenarioGroupId, scenarioId);
            File zipFile = new File(temporaryDirectory, scenarioId + ".zip");
            createZipFile(targetDir, zipFile, true);
            upload("uploadScenarioResource", params, zipFile);
            zipFile.delete();
            RecurciveSearchFile.deleteAllTree(temporaryDirectory, false);
        }
        request("startScenario", params);
    }
    
    public void cancelScenario(String scenarioId) throws Exception {
        Map params = new HashMap();
        params.put("scenarioId", scenarioId);
        request("cancelScenario", params);
    }
    
    public void endScenario(String scenarioId) throws Exception {
        String scenarioGroupId = getCurrentScenarioGroup().getScenarioGroupId();
        Map params = new HashMap();
        params.put("scenarioId", scenarioId);
        request("endScenario", params);
        if (testResourceManager != null) {
            File targetDir = new File(testResourceFileBaseDirectory, scenarioGroupId);
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            downloadScenarioResult(targetDir, scenarioGroupId, scenarioId, RESPONSE_FILE_TYPE_DEFAULT);
        }
    }
    
    public void startTestCase(String userId, String scenarioId, String testcaseId) throws Exception {
        Map params = new HashMap();
        params.put("userId", userId);
        params.put("scenarioId", scenarioId);
        params.put("testcaseId", testcaseId);
        request("startTestCase", params);
    }
    
    public void cancelTestCase(String scenarioId, String testcaseId) throws Exception {
        Map params = new HashMap();
        params.put("scenarioId", scenarioId);
        params.put("testcaseId", testcaseId);
        request("cancelTestCase", params);
    }
    
    public void endTestCase(String scenarioId, String testcaseId) throws Exception {
        String scenarioGroupId = getCurrentScenarioGroup().getScenarioGroupId();
        Map params = new HashMap();
        params.put("scenarioId", scenarioId);
        params.put("testcaseId", testcaseId);
        request("endTestCase", params);
        if (testResourceManager != null) {
            File targetDir = new File(testResourceFileBaseDirectory, scenarioGroupId + File.separator + scenarioId);
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            downloadTestCaseResult(targetDir, scenarioGroupId, scenarioId, testcaseId, RESPONSE_FILE_TYPE_DEFAULT);
        }
    }
    
    public TestScenarioGroup[] getScenarioGroups() throws Exception {
        TestScenarioGroup[] groups = (TestScenarioGroup[]) request("getScenarioGroups", null);
        if (testResourceManager != null) {
            String[] scenarioGroupIds = testResourceManager.getScenarioGroupIds();
            if (scenarioGroupIds == null || scenarioGroupIds.length == 0) {
                return null;
            }
            List scenarioGroupIdList = Arrays.asList(scenarioGroupIds);
            List list = new ArrayList();
            for (int i = 0; i < groups.length; i++) {
                if (scenarioGroupIdList.contains(groups[i].getScenarioGroupId())) {
                    list.add(groups[i]);
                }
            }
            groups = (TestScenarioGroup[]) list.toArray(new TestScenarioGroup[] {});
        }
        if (groups != null) {
            for (int i = 0; i < groups.length; i++) {
                ((TestScenarioGroupImpl) groups[i]).setController(this);
            }
        }
        return groups;
    }
    
    public String[] getScenarioGroupIds() throws Exception {
        String[] scenarioGroupIds = null;
        if (testResourceManager == null) {
            scenarioGroupIds = (String[]) request("getScenarioGroupIds", null);
        } else {
            scenarioGroupIds = testResourceManager.getScenarioGroupIds();
        }
        return scenarioGroupIds;
    }
    
    public TestScenarioGroup getScenarioGroup(String scenarioGroupId) throws Exception {
        Map params = new HashMap();
        params.put("scenarioGroupId", scenarioGroupId);
        TestScenarioGroup group = (TestScenarioGroup) request("getScenarioGroup", params);
        if (group != null) {
            ((TestScenarioGroupImpl) group).setController(this);
        }
        return group;
    }
    
    public TestScenarioGroup getCurrentScenarioGroup() throws Exception {
        TestScenarioGroup group = (TestScenarioGroup) request("getCurrentScenarioGroup", null);
        if (group != null) {
            ((TestScenarioGroupImpl) group).setController(this);
        }
        return group;
    }
    
    public TestScenarioGroup.TestScenarioGroupResource getTestScenarioGroupResource(String scenarioGroupId) throws Exception {
        Map params = new HashMap();
        params.put("scenarioGroupId", scenarioGroupId);
        return (TestScenarioGroup.TestScenarioGroupResource) request("getTestScenarioGroupResource", params);
    }
    
    public TestScenarioGroup.Status getTestScenarioGroupStatus(String scenarioGroupId) {
        Map params = new HashMap();
        params.put("scenarioGroupId", scenarioGroupId);
        try {
            return (TestScenarioGroup.Status) request("getTestScenarioGroupStatus", params);
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new UndeclaredThrowableException(e);
            }
        }
    }
    
    public TestScenario[] getScenarios(String scenarioGroupId) throws Exception {
        Map params = new HashMap();
        params.put("scenarioGroupId", scenarioGroupId);
        TestScenario[] scenarios = (TestScenario[]) request("getScenarios", params);
        if (testResourceManager != null) {
            String[] scenarioIds = testResourceManager.getScenarioIds(scenarioGroupId);
            if (scenarioIds == null || scenarioIds.length == 0) {
                return null;
            }
            List scenarioIdList = Arrays.asList(scenarioIds);
            List list = new ArrayList();
            for (int i = 0; i < scenarios.length; i++) {
                if (scenarioIdList.contains(scenarios[i].getScenarioId())) {
                    list.add(scenarios[i]);
                }
            }
            scenarios = (TestScenario[]) list.toArray(new TestScenario[] {});
        }
        if (scenarios != null) {
            for (int i = 0; i < scenarios.length; i++) {
                ((TestScenarioImpl) scenarios[i]).setController(this);
            }
        }
        return scenarios;
    }
    
    public String[] getScenarioIds(String scenarioGroupId) throws Exception {
        String[] scenarioIds = null;
        if (testResourceManager == null) {
            Map params = new HashMap();
            params.put("scenarioGroupId", scenarioGroupId);
            scenarioIds = (String[]) request("getScenarioIds", params);
        } else {
            scenarioIds = testResourceManager.getScenarioIds(scenarioGroupId);
        }
        return scenarioIds;
    }
    
    public TestScenario getScenario(String scenarioGroupId, String scenarioId) throws Exception {
        Map params = new HashMap();
        params.put("scenarioGroupId", scenarioGroupId);
        params.put("scenarioId", scenarioId);
        TestScenario scenario = (TestScenario) request("getScenario", params);
        if (scenario != null) {
            ((TestScenarioImpl) scenario).setController(this);
        }
        return scenario;
    }
    
    public TestScenario getCurrentScenario() throws Exception {
        TestScenario scenario = (TestScenario) request("getCurrentScenario", null);
        if (scenario != null) {
            ((TestScenarioImpl) scenario).setController(this);
        }
        return scenario;
    }
    
    public TestScenario.TestScenarioResource getTestScenarioResource(String scenarioGroupId, String scenarioId) throws Exception {
        Map params = new HashMap();
        params.put("scenarioGroupId", scenarioGroupId);
        params.put("scenarioId", scenarioId);
        return (TestScenario.TestScenarioResource) request("getTestScenarioResource", params);
    }
    
    public TestScenario.Status getTestScenarioStatus(String scenarioGroupId, String scenarioId) {
        Map params = new HashMap();
        params.put("scenarioGroupId", scenarioGroupId);
        params.put("scenarioId", scenarioId);
        try {
            return (TestScenario.Status) request("getTestScenarioStatus", params);
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new UndeclaredThrowableException(e);
            }
        }
    }
    
    public TestCase[] getTestCases(String scenarioGroupId, String scenarioId) throws Exception {
        Map params = new HashMap();
        params.put("scenarioGroupId", scenarioGroupId);
        params.put("scenarioId", scenarioId);
        TestCase[] testcases = (TestCase[]) request("getTestCases", params);
        if (testResourceManager != null) {
            String[] testcaseIds = testResourceManager.getTestCaseIds(scenarioGroupId, scenarioId);
            if (testcaseIds == null || testcaseIds.length == 0) {
                return null;
            }
            List testcaseIdList = Arrays.asList(testcaseIds);
            List list = new ArrayList();
            for (int i = 0; i < testcases.length; i++) {
                if (testcaseIdList.contains(testcases[i].getTestCaseId())) {
                    list.add(testcases[i]);
                }
            }
            testcases = (TestCase[]) list.toArray(new TestCase[] {});
        }
        if (testcases != null) {
            for (int i = 0; i < testcases.length; i++) {
                ((TestCaseImpl) testcases[i]).setController(this);
            }
        }
        return testcases;
    }
    
    public String[] getTestCaseIds(String scenarioGroupId, String scenarioId) throws Exception {
        String[] testcaseIds = null;
        if (testResourceManager == null) {
            Map params = new HashMap();
            params.put("scenarioGroupId", scenarioGroupId);
            params.put("scenarioId", scenarioId);
            testcaseIds = (String[]) request("getTestCaseIds", params);
        } else {
            testcaseIds = testResourceManager.getTestCaseIds(scenarioGroupId, scenarioId);
        }
        return testcaseIds;
    }
    
    public TestCase getTestCase(String scenarioGroupId, String scenarioId, String testcaseId) throws Exception {
        Map params = new HashMap();
        params.put("scenarioGroupId", scenarioGroupId);
        params.put("scenarioId", scenarioId);
        params.put("testcaseId", testcaseId);
        TestCase testcase = (TestCase) request("getTestCase", params);
        if (testcase != null) {
            ((TestCaseImpl) testcase).setController(this);
        }
        return testcase;
    }
    
    public TestCase getCurrentTestCase() throws Exception {
        TestCase testCase = (TestCase) request("getCurrentTestCase", null);
        if (testCase != null) {
            ((TestCaseImpl) testCase).setController(this);
        }
        return testCase;
    }
    
    public TestCase.TestCaseResource getTestCaseResource(String scenarioGroupId, String scenarioId, String testcaseId) throws Exception {
        Map params = new HashMap();
        params.put("scenarioGroupId", scenarioGroupId);
        params.put("scenarioId", scenarioId);
        params.put("testcaseId", testcaseId);
        return (TestCase.TestCaseResource) request("getTestCaseResource", params);
    }
    
    public TestCase.Status getTestCaseStatus(String scenarioGroupId, String scenarioId, String testcaseId) {
        Map params = new HashMap();
        params.put("scenarioGroupId", scenarioGroupId);
        params.put("scenarioId", scenarioId);
        params.put("testcaseId", testcaseId);
        try {
            return (TestCase.Status) request("getTestCaseStatus", params);
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new UndeclaredThrowableException(e);
            }
        }
    }
    
    public File downloadScenarioGroupResult(File dir, String scenarioGroupId, int responseFileType) throws Exception {
        Map params = new HashMap();
        params.put("scenarioGroupId", scenarioGroupId);
        Object[] ret = (Object[]) request("downloadScenarioGroupResult", params);
        String fileName = (String) ret[0];
        InputStream is = (InputStream) ret[1];
        return downloadResult(dir, responseFileType, fileName, is);
    }
    
    public File downloadScenarioResult(File dir, String scenarioGroupId, String scenarioId, int responseFileType) throws Exception {
        Map params = new HashMap();
        params.put("scenarioGroupId", scenarioGroupId);
        params.put("scenarioId", scenarioId);
        Object[] ret = (Object[]) request("downloadScenarioResult", params);
        String fileName = (String) ret[0];
        InputStream is = (InputStream) ret[1];
        return downloadResult(dir, responseFileType, fileName, is);
    }
    
    private File downloadResult(File dir, int responseFileType, String fileName, InputStream is) throws Exception {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File result = new File(dir, fileName);
        FileOutputStream fos = null;
        switch (responseFileType) {
        case RESPONSE_FILE_TYPE_ZIP:
            fos = new FileOutputStream(result);
            try {
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) > 0) {
                    fos.write(bytes, 0, len);
                }
                fos.flush();
            } finally {
                fos.close();
            }
            break;
        case RESPONSE_FILE_TYPE_DEFAULT:
        default:
            ZipInputStream zis = new ZipInputStream(is);
            ZipEntry entry = null;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                File entryFile = new File(dir, entry.getName());
                if (!entryFile.getParentFile().exists()) {
                    entryFile.getParentFile().mkdirs();
                }
                fos = new FileOutputStream(entryFile);
                try {
                    byte[] bytes = new byte[1024];
                    int len = 0;
                    while ((len = zis.read(bytes)) > 0) {
                        fos.write(bytes, 0, len);
                    }
                    fos.flush();
                } finally {
                    fos.close();
                }
                zis.closeEntry();
            }
        }
        return result;
    }
    
    public File downloadTestCaseResult(File dir, String scenarioGroupId, String scenarioId, String testcaseId, int responseFileType)
            throws Exception {
        Map params = new HashMap();
        params.put("scenarioGroupId", scenarioGroupId);
        params.put("scenarioId", scenarioId);
        params.put("testcaseId", testcaseId);
        Object[] ret = (Object[]) request("downloadTestCaseResult", params);
        String fileName = (String) ret[0];
        InputStream is = (InputStream) ret[1];
        return downloadResult(dir, responseFileType, fileName, is);
    }
    
    public void downloadTestScenarioGroupResource(String scenarioGroupId) throws Exception {
        Map params = new HashMap();
        params.put("scenarioGroupId", scenarioGroupId);
        request("downloadTestScenarioGroupResource", params);
    }
    
    public void downloadTestScenarioResource(String scenarioGroupId, String scenarioId) throws Exception {
        Map params = new HashMap();
        params.put("scenarioGroupId", scenarioGroupId);
        params.put("scenarioId", scenarioId);
        request("downloadTestScenarioResource", params);
    }
    
    public void reset() throws Exception {
        request("reset", null);
    }
    
    public void generateTestScenarioGroupEvidenceFile(String scenarioGroupId) throws Exception {
        File targetDir = new File(testResourceFileBaseDirectory, scenarioGroupId);
        if (!targetDir.exists()) {
            return;
        }
        if (isUploadEvidenceServer) {
            Map params = new HashMap();
            params.put("scenarioGroupId", scenarioGroupId);
            request("generateTestScenarioGroupEvidenceFile", params);
        }
        if (testResourceManager != null && testResourceManager instanceof UploadableTestResourceManager) {
            TestScenarioGroupResource testScenarioGroupResource = getTestScenarioGroupResource(scenarioGroupId);
            if (testScenarioGroupResource != null) {
                Map actionEvidenceFileNameMap = testScenarioGroupResource.getActionEvidenceFileNameMap();
                if (actionEvidenceFileNameMap != null && !actionEvidenceFileNameMap.isEmpty()) {
                    try {
                        File tmpDir = new File(temporaryDirectory, scenarioGroupId);
                        setupDir(tmpDir, false);
                        Iterator itr = actionEvidenceFileNameMap.entrySet().iterator();
                        while (itr.hasNext()) {
                            Entry entry = (Entry) itr.next();
                            String[] names = (String[]) entry.getValue();
                            File fromFile = new File(targetDir, names[0]);
                            if (fromFile.exists()) {
                                File toFile = new File(tmpDir, names[1]);
                                RecurciveSearchFile.dataCopy(fromFile, toFile);
                            }
                        }
                        ((UploadableTestResourceManager) testResourceManager).uploadScenarioGroupResource(tmpDir, scenarioGroupId, false);
                    } finally {
                        RecurciveSearchFile.deleteAllTree(temporaryDirectory, false);
                    }
                }
            }
        }
    }
    
    public void generateTestScenarioEvidenceFile(String scenarioGroupId, String scenarioId) throws Exception {
        File targetDir = new File(testResourceFileBaseDirectory, scenarioGroupId + File.separator + scenarioId);
        if (!targetDir.exists()) {
            return;
        }
        if (isUploadEvidenceServer) {
            Map params = new HashMap();
            params.put("scenarioGroupId", scenarioGroupId);
            params.put("scenarioId", scenarioId);
            request("generateTestScenarioEvidenceFile", params);
        }
        if (testResourceManager != null && testResourceManager instanceof UploadableTestResourceManager) {
            TestScenarioResource testScenarioResource = getTestScenarioResource(scenarioGroupId, scenarioId);
            if (testScenarioResource != null) {
                Map actionEvidenceFileNameMap = testScenarioResource.getActionEvidenceFileNameMap();
                if (actionEvidenceFileNameMap != null && !actionEvidenceFileNameMap.isEmpty()) {
                    try {
                        File tmpDir = new File(temporaryDirectory, scenarioId);
                        setupDir(tmpDir, false);
                        Iterator itr = actionEvidenceFileNameMap.entrySet().iterator();
                        while (itr.hasNext()) {
                            Entry entry = (Entry) itr.next();
                            String[] names = (String[]) entry.getValue();
                            File fromFile = new File(targetDir, names[0]);
                            if (fromFile.exists()) {
                                File toFile = new File(tmpDir, names[1]);
                                RecurciveSearchFile.dataCopy(fromFile, toFile);
                            }
                        }
                        ((UploadableTestResourceManager) testResourceManager).uploadScenarioResource(tmpDir, scenarioGroupId, scenarioId, false);
                    } finally {
                        RecurciveSearchFile.deleteAllTree(temporaryDirectory, false);
                    }
                }
            }
        }
    }
    
    public void generateTestCaseEvidenceFile(String scenarioGroupId, String scenarioId, String testcaseId) throws Exception {
        File targetDir = new File(testResourceFileBaseDirectory, scenarioGroupId + File.separator + scenarioId + File.separator + testcaseId);
        if (!targetDir.exists()) {
            return;
        }
        if (isUploadEvidenceServer) {
            Map params = new HashMap();
            params.put("scenarioGroupId", scenarioGroupId);
            params.put("scenarioId", scenarioId);
            params.put("testcaseId", testcaseId);
            request("generateTestCaseEvidenceFile", params);
        }
        if (testResourceManager != null && testResourceManager instanceof UploadableTestResourceManager) {
            TestCaseResource testCaseResource = getTestCaseResource(scenarioGroupId, scenarioId, testcaseId);
            if (testCaseResource != null) {
                Map actionEvidenceFileNameMap = testCaseResource.getActionEvidenceFileNameMap();
                if (actionEvidenceFileNameMap != null && !actionEvidenceFileNameMap.isEmpty()) {
                    try {
                        File tmpDir = new File(temporaryDirectory, testcaseId);
                        setupDir(tmpDir, false);
                        Iterator itr = actionEvidenceFileNameMap.entrySet().iterator();
                        while (itr.hasNext()) {
                            Entry entry = (Entry) itr.next();
                            String[] names = (String[]) entry.getValue();
                            File fromFile = new File(targetDir, names[0]);
                            if (fromFile.exists()) {
                                File toFile = new File(tmpDir, names[1]);
                                RecurciveSearchFile.dataCopy(fromFile, toFile);
                            }
                        }
                        ((UploadableTestResourceManager) testResourceManager).uploadTestCaseResource(tmpDir, scenarioGroupId, scenarioId, testcaseId,
                                false);
                    } finally {
                        RecurciveSearchFile.deleteAllTree(temporaryDirectory, false);
                    }
                }
            }
        }
    }
    
    private void createZipFile(File targetDir, File zipFile, boolean isZipTree) throws Exception {
        File[] files = { targetDir };
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zipFile));
            doZip(zos, files, targetDir.getParent(), isZipTree);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (Exception e) {
                }
            }
        }
    }
    
    private void doZip(ZipOutputStream zos, File[] files, String baseDir, boolean isZipTree) throws Exception {
        byte[] buf = new byte[1024];
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isDirectory()) {
                if (isZipTree || baseDir.equals(f.getParent())) {
                    doZip(zos, f.listFiles(), baseDir, isZipTree);
                }
            } else {
                String path = f.getPath().substring(f.getPath().indexOf(baseDir) + baseDir.length() + 1);
                ZipEntry entry = new ZipEntry(path.replace('\\', '/'));
                zos.putNextEntry(entry);
                InputStream is = null;
                try {
                    is = new BufferedInputStream(new FileInputStream(f));
                    int len = 0;
                    while ((len = is.read(buf)) != -1) {
                        zos.write(buf, 0, len);
                    }
                } finally {
                    is.close();
                }
            }
        }
    }
    
    private void setupDir(File dir, boolean isContains) throws IOException {
        if (dir.exists()) {
            RecurciveSearchFile.deleteAllTree(dir, isContains);
        } else if (!dir.mkdirs()) {
            throw new IOException("Directory can not make. path=" + dir);
        }
    }
    
    protected static class HeaderValue {
        protected String value;
        protected Map parameters;
        protected int hashCode;
        
        public HeaderValue() {
        }
        
        public HeaderValue(String header) {
            String[] types = header.split(";");
            value = types[0].trim();
            hashCode = value.hashCode();
            if (types.length > 1) {
                parameters = new HashMap();
                for (int i = 1; i < types.length; i++) {
                    String parameter = types[i].trim();
                    final int index = parameter.indexOf('=');
                    if (index != -1) {
                        parameters.put(parameter.substring(0, index).toLowerCase(), parameter.substring(index + 1).toLowerCase());
                    } else {
                        parameters.put(parameter.toLowerCase(), null);
                    }
                }
                hashCode += parameters.hashCode();
            }
        }
        
        public String getValue() {
            return value;
        }
        
        public void setValue(String val) {
            value = val;
        }
        
        public String getParameter(String name) {
            return parameters == null ? null : (String) parameters.get(name);
        }
        
        public void setParameter(String name, String value) {
            if (parameters == null) {
                parameters = new HashMap();
            }
            parameters.put(name, value);
        }
        
        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append(value);
            if (parameters != null) {
                Iterator entries = parameters.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry entry = (Map.Entry) entries.next();
                    buf.append(';').append(entry.getKey()).append('=').append(entry.getValue());
                }
            }
            return buf.toString();
        }
        
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof HeaderValue)) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            HeaderValue cmp = (HeaderValue) obj;
            if (!value.equals(cmp.value)) {
                return false;
            }
            if ((parameters == null && cmp.parameters != null) || (parameters != null && cmp.parameters == null)
                    || (parameters != null && !parameters.equals(cmp.parameters))) {
                return false;
            }
            return true;
        }
        
        public int hashCode() {
            return hashCode;
        }
    }
    
    protected static class MediaType extends HeaderValue {
        public MediaType() {
        }
        
        public MediaType(String header) {
            super(header);
        }
        
        public String getMediaType() {
            return getValue();
        }
        
        public void setMediaType(String type) {
            setValue(type);
        }
    }
}