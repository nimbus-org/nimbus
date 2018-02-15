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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.http.HttpClient;
import jp.ossc.nimbus.service.http.HttpClientFactory;
import jp.ossc.nimbus.service.http.HttpException;
import jp.ossc.nimbus.service.http.HttpRequest;
import jp.ossc.nimbus.service.http.HttpResponse;

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
    private String urlEncodeCharacterEncoding = "UTF-8";

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

    public void setURLEncodeCharacterEncoding(String encoding) {
        urlEncodeCharacterEncoding = encoding;
    }

    public String getURLEncodeCharacterEncoding() {
        return urlEncodeCharacterEncoding;
    }

    public void startService() throws Exception {
        if (httpClientFactoryServiceName != null) {
            httpClientFactory = (HttpClientFactory) ServiceManagerFactory.getServiceObject(httpClientFactoryServiceName);
        }
        if (httpClientFactory == null) {
            throw new IllegalArgumentException("HttpClientFactory is null.");
        }
        httpClientFactory.createRequest(templateAction);
    }

    private Object request(String action, Map params) throws HttpException, Exception {
        HttpClient client = httpClientFactory.createHttpClient();
        HttpRequest request = httpClientFactory.createRequest(templateAction);
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
        request("startScenarioGroup", params);
    }

    public void endScenarioGroup() throws Exception {
        request("endScenarioGroup", null);
    }

    public void startScenario(String userId, String scenarioId) throws Exception {
        Map params = new HashMap();
        params.put("userId", userId);
        params.put("scenarioId", scenarioId);
        request("startScenario", params);
    }

    public void cancelScenario(String scenarioId) throws Exception {
        Map params = new HashMap();
        params.put("scenarioId", scenarioId);
        request("cancelScenario", params);
    }

    public void endScenario(String scenarioId) throws Exception {
        Map params = new HashMap();
        params.put("scenarioId", scenarioId);
        request("endScenario", params);
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
        Map params = new HashMap();
        params.put("scenarioId", scenarioId);
        params.put("testcaseId", testcaseId);
        request("endTestCase", params);
    }

    public TestScenarioGroup[] getScenarioGroups() throws Exception {
        TestScenarioGroup[] groups = (TestScenarioGroup[]) request("getScenarioGroups", null);
        if (groups != null) {
            for (int i = 0; i < groups.length; i++) {
                ((TestScenarioGroupImpl) groups[i]).setController(this);
            }
        }
        return groups;
    }

    public String[] getScenarioGroupIds() throws Exception {
        return (String[]) request("getScenarioGroupIds", null);
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
        if (scenarios != null) {
            for (int i = 0; i < scenarios.length; i++) {
                ((TestScenarioImpl) scenarios[i]).setController(this);
            }
        }
        return scenarios;
    }

    public String[] getScenarioIds(String scenarioGroupId) throws Exception {
        Map params = new HashMap();
        params.put("scenarioGroupId", scenarioGroupId);
        return (String[]) request("getScenarioIds", params);
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
        if (testcases != null) {
            for (int i = 0; i < testcases.length; i++) {
                ((TestCaseImpl) testcases[i]).setController(this);
            }
        }
        return testcases;
    }

    public String[] getTestCaseIds(String scenarioGroupId, String scenarioId) throws Exception {
        Map params = new HashMap();
        params.put("scenarioGroupId", scenarioGroupId);
        params.put("scenarioId", scenarioId);
        return (String[]) request("getTestCaseIds", params);
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

    public File downloadTestCaseResult(File dir, String scenarioGroupId, String scenarioId, String testcaseId, int responseFileType) throws Exception {
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