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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.http.proxy.HttpProcessServiceBase;
import jp.ossc.nimbus.service.http.proxy.HttpRequest;
import jp.ossc.nimbus.service.http.proxy.HttpResponse;
import jp.ossc.nimbus.util.converter.BeanJSONConverter;

/**
 * HTTPテストコントローラサーバ。
 * <p>
 *
 * @author M.Takata
 */
public class HttpTestControllerServerService extends HttpProcessServiceBase implements HttpTestControllerServerServiceMBean {

    private ServiceName testControllerServiceName;
    private TestController testController;
    private ServiceName beanJSONConverterServiceName;
    private BeanJSONConverter beanJSONConverter;
    private String urlEncodeCharacterEncoding = "UTF-8";
    private String defaultResponseCharacterEncoding = "UTF-8";

    public void setTestControllerServiceName(ServiceName name) {
        testControllerServiceName = name;
    }

    public ServiceName getTestControllerServiceName() {
        return testControllerServiceName;
    }

    public void setBeanJSONConverterServiceName(ServiceName name) {
        beanJSONConverterServiceName = name;
    }

    public ServiceName getBeanJSONConverterServiceName() {
        return beanJSONConverterServiceName;
    }

    public void setURLEncodeCharacterEncoding(String encoding) {
        urlEncodeCharacterEncoding = encoding;
    }

    public String getURLEncodeCharacterEncoding() {
        return urlEncodeCharacterEncoding;
    }

    public void setDefaultResponseCharacterEncoding(String encoding) {
        defaultResponseCharacterEncoding = encoding;
    }

    public String getDefaultResponseCharacterEncoding() {
        return defaultResponseCharacterEncoding;
    }

    public void setTestController(TestController controller) {
        testController = controller;
    }

    public void setBeanJSONConverter(BeanJSONConverter converter) {
        beanJSONConverter = converter;
    }

    public void startService() throws Exception {
        if (testControllerServiceName != null) {
            testController = (TestController) ServiceManagerFactory.getServiceObject(testControllerServiceName);
        }
        if (testController == null) {
            throw new IllegalArgumentException("TestController is null.");
        }
        if (beanJSONConverterServiceName != null) {
            beanJSONConverter = (BeanJSONConverter) ServiceManagerFactory.getServiceObject(beanJSONConverterServiceName);
        }
        if (beanJSONConverter == null) {
            beanJSONConverter = new BeanJSONConverter();
        }
    }

    public void doProcess(HttpRequest request, HttpResponse response) throws Exception {
        String acceptStr = request.getHeader().getHeader("Accept");
        boolean isJSON = true;
        if (acceptStr != null) {
            Accept accept = new Accept(acceptStr);
            for (int i = 0; i < accept.mediaRanges.size(); i++) {
                MediaRange mr = (MediaRange) accept.mediaRanges.get(i);
                if ("application/json".equals(mr.getMediaType())) {
                    isJSON = true;
                    break;
                } else if ("application/octet-stream".equals(mr.getMediaType())) {
                    isJSON = false;
                    break;
                }
            }
        }
        String body = null;
        if (request.getBody() != null) {
            request.getBody().read();
            body = request.getBody().toString();
        }

        if (request.getHeader().getURLMatcher(".*setTestPhase").matches()) {
            Map params = parseQuery(request.getHeader().getQuery());
            if (body != null) {
                params.putAll(parseQuery(body));
            }
            testController.setTestPhase(getParameter(params, "phase"));
        } else if (request.getHeader().getURLMatcher(".*getTestPhase").matches()) {
            if (isJSON) {
                Map jsonMap = new HashMap();
                jsonMap.put("phase", testController.getTestPhase());
                responseJSON(request, response, jsonMap);
            } else {
                responseBinary(response, testController.getTestPhase());
            }
        } else if (request.getHeader().getURLMatcher(".*startScenarioGroup").matches()) {
            Map params = parseQuery(request.getHeader().getQuery());
            if (body != null) {
                params.putAll(parseQuery(body));
            }
            try {
                testController.startScenarioGroup(getParameter(params, "userId"), getParameter(params, "scenarioGroupId"));
            } catch (Exception e) {
                if (isJSON) {
                    Map jsonMap = new HashMap();
                    jsonMap.put("exception", e);
                    responseJSON(request, response, jsonMap);
                } else {
                    responseBinary(response, e);
                }
            }
        } else if (request.getHeader().getURLMatcher(".*endScenarioGroup").matches()) {
            try {
                testController.endScenarioGroup();
            } catch (Exception e) {
                if (isJSON) {
                    Map jsonMap = new HashMap();
                    jsonMap.put("exception", e);
                    responseJSON(request, response, jsonMap);
                } else {
                    responseBinary(response, e);
                }
            }
        } else if (request.getHeader().getURLMatcher(".*startScenario").matches()) {
            Map params = parseQuery(request.getHeader().getQuery());
            if (body != null) {
                params.putAll(parseQuery(body));
            }
            try {
                testController.startScenario(getParameter(params, "userId"), getParameter(params, "scenarioId"));
            } catch (Exception e) {
                if (isJSON) {
                    Map jsonMap = new HashMap();
                    jsonMap.put("exception", e);
                    responseJSON(request, response, jsonMap);
                } else {
                    responseBinary(response, e);
                }
            }
        } else if (request.getHeader().getURLMatcher(".*cancelScenario").matches()) {
            Map params = parseQuery(request.getHeader().getQuery());
            if (body != null) {
                params.putAll(parseQuery(body));
            }
            try {
                testController.cancelScenario(getParameter(params, "scenarioId"));
            } catch (Exception e) {
                if (isJSON) {
                    Map jsonMap = new HashMap();
                    jsonMap.put("exception", e);
                    responseJSON(request, response, jsonMap);
                } else {
                    responseBinary(response, e);
                }
            }
        } else if (request.getHeader().getURLMatcher(".*endScenario").matches()) {
            Map params = parseQuery(request.getHeader().getQuery());
            if (body != null) {
                params.putAll(parseQuery(body));
            }
            try {
                testController.endScenario(getParameter(params, "scenarioId"));
            } catch (Exception e) {
                if (isJSON) {
                    Map jsonMap = new HashMap();
                    jsonMap.put("exception", e);
                    responseJSON(request, response, jsonMap);
                } else {
                    responseBinary(response, e);
                }
            }
        } else if (request.getHeader().getURLMatcher(".*startTestCase").matches()) {
            Map params = parseQuery(request.getHeader().getQuery());
            if (body != null) {
                params.putAll(parseQuery(body));
            }
            try {
                testController.startTestCase(getParameter(params, "userId"), getParameter(params, "scenarioId"), getParameter(params, "testcaseId"));
            } catch (Exception e) {
                if (isJSON) {
                    Map jsonMap = new HashMap();
                    jsonMap.put("exception", e);
                    responseJSON(request, response, jsonMap);
                } else {
                    responseBinary(response, e);
                }
            }
        } else if (request.getHeader().getURLMatcher(".*cancelTestCase").matches()) {
            Map params = parseQuery(request.getHeader().getQuery());
            if (body != null) {
                params.putAll(parseQuery(body));
            }
            try {
                testController.cancelTestCase(getParameter(params, "scenarioId"), getParameter(params, "testcaseId"));
            } catch (Exception e) {
                if (isJSON) {
                    Map jsonMap = new HashMap();
                    jsonMap.put("exception", e);
                    responseJSON(request, response, jsonMap);
                } else {
                    responseBinary(response, e);
                }
            }
        } else if (request.getHeader().getURLMatcher(".*endTestCase").matches()) {
            Map params = parseQuery(request.getHeader().getQuery());
            if (body != null) {
                params.putAll(parseQuery(body));
            }
            try {
                testController.endTestCase(getParameter(params, "scenarioId"), getParameter(params, "testcaseId"));
            } catch (Exception e) {
                if (isJSON) {
                    Map jsonMap = new HashMap();
                    jsonMap.put("exception", e);
                    responseJSON(request, response, jsonMap);
                } else {
                    responseBinary(response, e);
                }
            }
        } else if (request.getHeader().getURLMatcher(".*getScenarioGroups").matches()) {
            Map jsonMap = isJSON ? new HashMap() : null;
            Object result = null;
            try {
                if (isJSON) {
                    jsonMap.put("ScenarioGroups", testController.getScenarioGroups());
                } else {
                    result = testController.getScenarioGroups();
                }
            } catch (Exception e) {
                if (isJSON) {
                    jsonMap.put("exception", e);
                } else {
                    result = e;
                }
            }
            if (isJSON) {
                responseJSON(request, response, jsonMap);
            } else {
                responseBinary(response, result);
            }
        } else if (request.getHeader().getURLMatcher(".*getScenarioGroupIds").matches()) {
            Map jsonMap = isJSON ? new HashMap() : null;
            Object result = null;
            try {
                if (isJSON) {
                    jsonMap.put("ScenarioGroupIds", testController.getScenarioGroupIds());
                } else {
                    result = testController.getScenarioGroupIds();
                }
            } catch (Exception e) {
                if (isJSON) {
                    jsonMap.put("exception", e);
                } else {
                    result = e;
                }
            }
            if (isJSON) {
                responseJSON(request, response, jsonMap);
            } else {
                responseBinary(response, result);
            }
        } else if (request.getHeader().getURLMatcher(".*getScenarioGroup").matches()) {
            Map params = parseQuery(request.getHeader().getQuery());
            if (body != null) {
                params.putAll(parseQuery(body));
            }
            Map jsonMap = isJSON ? new HashMap() : null;
            Object result = null;
            try {
                if (isJSON) {
                    jsonMap.put("ScenarioGroup", testController.getScenarioGroup(getParameter(params, "scenarioGroupId")));
                } else {
                    result = testController.getScenarioGroup(getParameter(params, "scenarioGroupId"));
                }
            } catch (Exception e) {
                if (isJSON) {
                    jsonMap.put("exception", e);
                } else {
                    result = e;
                }
            }
            if (isJSON) {
                responseJSON(request, response, jsonMap);
            } else {
                responseBinary(response, result);
            }
        } else if (request.getHeader().getURLMatcher(".*getCurrentScenarioGroup").matches()) {
            Map jsonMap = isJSON ? new HashMap() : null;
            Object result = null;
            try {
                if (isJSON) {
                    jsonMap.put("CurrentScenarioGroup", testController.getCurrentScenarioGroup());
                } else {
                    result = testController.getCurrentScenarioGroup();
                }
            } catch (Exception e) {
                if (isJSON) {
                    jsonMap.put("exception", e);
                } else {
                    result = e;
                }
            }
            if (isJSON) {
                responseJSON(request, response, jsonMap);
            } else {
                responseBinary(response, result);
            }
        } else if (request.getHeader().getURLMatcher(".*getTestScenarioGroupResource").matches()) {
            Map params = parseQuery(request.getHeader().getQuery());
            if (body != null) {
                params.putAll(parseQuery(body));
            }
            Map jsonMap = isJSON ? new HashMap() : null;
            Object result = null;
            try {
                if (isJSON) {
                    jsonMap.put("TestScenarioGroupResource", testController.getTestScenarioGroupResource(getParameter(params, "scenarioGroupId")));
                } else {
                    result = testController.getTestScenarioGroupResource(getParameter(params, "scenarioGroupId"));
                }
            } catch (Exception e) {
                if (isJSON) {
                    jsonMap.put("exception", e);
                } else {
                    result = e;
                }
            }
            if (isJSON) {
                responseJSON(request, response, jsonMap);
            } else {
                responseBinary(response, result);
            }
        } else if (request.getHeader().getURLMatcher(".*getTestScenarioGroupStatus").matches()) {
            Map params = parseQuery(request.getHeader().getQuery());
            if (body != null) {
                params.putAll(parseQuery(body));
            }
            Map jsonMap = isJSON ? new HashMap() : null;
            Object result = null;
            try {
                if (isJSON) {
                    jsonMap.put("TestScenarioGroupStatus", testController.getTestScenarioGroupStatus(getParameter(params, "scenarioGroupId")));
                } else {
                    result = testController.getTestScenarioGroupStatus(getParameter(params, "scenarioGroupId"));
                }
            } catch (Exception e) {
                if (isJSON) {
                    jsonMap.put("exception", e);
                } else {
                    result = e;
                }
            }
            if (isJSON) {
                responseJSON(request, response, jsonMap);
            } else {
                responseBinary(response, result);
            }
        } else if (request.getHeader().getURLMatcher(".*getScenarios").matches()) {
            Map params = parseQuery(request.getHeader().getQuery());
            if (body != null) {
                params.putAll(parseQuery(body));
            }
            Map jsonMap = isJSON ? new HashMap() : null;
            Object result = null;
            try {
                if (isJSON) {
                    jsonMap.put("Scenarios", testController.getScenarios(getParameter(params, "scenarioGroupId")));
                } else {
                    result = testController.getScenarios(getParameter(params, "scenarioGroupId"));
                }
            } catch (Exception e) {
                if (isJSON) {
                    jsonMap.put("exception", e);
                } else {
                    result = e;
                }
            }
            if (isJSON) {
                responseJSON(request, response, jsonMap);
            } else {
                responseBinary(response, result);
            }
        } else if (request.getHeader().getURLMatcher(".*getScenarioIds").matches()) {
            Map params = parseQuery(request.getHeader().getQuery());
            if (body != null) {
                params.putAll(parseQuery(body));
            }
            Map jsonMap = isJSON ? new HashMap() : null;
            Object result = null;
            try {
                if (isJSON) {
                    jsonMap.put("ScenarioIds", testController.getScenarioIds(getParameter(params, "scenarioGroupId")));
                } else {
                    result = testController.getScenarioIds(getParameter(params, "scenarioGroupId"));
                }
            } catch (Exception e) {
                if (isJSON) {
                    jsonMap.put("exception", e);
                } else {
                    result = e;
                }
            }
            if (isJSON) {
                responseJSON(request, response, jsonMap);
            } else {
                responseBinary(response, result);
            }
        } else if (request.getHeader().getURLMatcher(".*getScenario").matches()) {
            Map params = parseQuery(request.getHeader().getQuery());
            if (body != null) {
                params.putAll(parseQuery(body));
            }
            Map jsonMap = isJSON ? new HashMap() : null;
            Object result = null;
            try {
                if (isJSON) {
                    jsonMap.put("Scenario", testController.getScenario(getParameter(params, "scenarioGroupId"), getParameter(params, "scenarioId")));
                } else {
                    result = testController.getScenario(getParameter(params, "scenarioGroupId"), getParameter(params, "scenarioId"));
                }
            } catch (Exception e) {
                if (isJSON) {
                    jsonMap.put("exception", e);
                } else {
                    result = e;
                }
            }
            if (isJSON) {
                responseJSON(request, response, jsonMap);
            } else {
                responseBinary(response, result);
            }
        } else if (request.getHeader().getURLMatcher(".*getCurrentScenario").matches()) {
            Map jsonMap = isJSON ? new HashMap() : null;
            Object result = null;
            try {
                if (isJSON) {
                    jsonMap.put("CurrentScenario", testController.getCurrentScenario());
                } else {
                    result = testController.getCurrentScenario();
                }
            } catch (Exception e) {
                if (isJSON) {
                    jsonMap.put("exception", e);
                } else {
                    result = e;
                }
            }
            if (isJSON) {
                responseJSON(request, response, jsonMap);
            } else {
                responseBinary(response, result);
            }
        } else if (request.getHeader().getURLMatcher(".*getTestScenarioResource").matches()) {
            Map params = parseQuery(request.getHeader().getQuery());
            if (body != null) {
                params.putAll(parseQuery(body));
            }
            Map jsonMap = isJSON ? new HashMap() : null;
            Object result = null;
            try {
                if (isJSON) {
                    jsonMap.put("TestScenarioResource",
                            testController.getTestScenarioResource(getParameter(params, "scenarioGroupId"), getParameter(params, "scenarioId")));
                } else {
                    result = testController.getTestScenarioResource(getParameter(params, "scenarioGroupId"), getParameter(params, "scenarioId"));
                }
            } catch (Exception e) {
                if (isJSON) {
                    jsonMap.put("exception", e);
                } else {
                    result = e;
                }
            }
            if (isJSON) {
                responseJSON(request, response, jsonMap);
            } else {
                responseBinary(response, result);
            }
        } else if (request.getHeader().getURLMatcher(".*getTestScenarioStatus").matches()) {
            Map params = parseQuery(request.getHeader().getQuery());
            if (body != null) {
                params.putAll(parseQuery(body));
            }
            Map jsonMap = isJSON ? new HashMap() : null;
            Object result = null;
            try {
                if (isJSON) {
                    jsonMap.put("TestScenarioStatus",
                            testController.getTestScenarioStatus(getParameter(params, "scenarioGroupId"), getParameter(params, "scenarioId")));
                } else {
                    result = testController.getTestScenarioStatus(getParameter(params, "scenarioGroupId"), getParameter(params, "scenarioId"));
                }
            } catch (Exception e) {
                if (isJSON) {
                    jsonMap.put("exception", e);
                } else {
                    result = e;
                }
            }
            if (isJSON) {
                responseJSON(request, response, jsonMap);
            } else {
                responseBinary(response, result);
            }
        } else if (request.getHeader().getURLMatcher(".*getTestCases").matches()) {
            Map params = parseQuery(request.getHeader().getQuery());
            if (body != null) {
                params.putAll(parseQuery(body));
            }
            Map jsonMap = isJSON ? new HashMap() : null;
            Object result = null;
            try {
                if (isJSON) {
                    jsonMap.put("TestCases", testController.getTestCases(getParameter(params, "scenarioGroupId"), getParameter(params, "scenarioId")));
                } else {
                    result = testController.getTestCases(getParameter(params, "scenarioGroupId"), getParameter(params, "scenarioId"));
                }
            } catch (Exception e) {
                if (isJSON) {
                    jsonMap.put("exception", e);
                } else {
                    result = e;
                }
            }
            if (isJSON) {
                responseJSON(request, response, jsonMap);
            } else {
                responseBinary(response, result);
            }
        } else if (request.getHeader().getURLMatcher(".*getTestCaseIds").matches()) {
            Map params = parseQuery(request.getHeader().getQuery());
            if (body != null) {
                params.putAll(parseQuery(body));
            }
            Map jsonMap = isJSON ? new HashMap() : null;
            Object result = null;
            try {
                if (isJSON) {
                    jsonMap.put("TestCaseIds",
                            testController.getTestCaseIds(getParameter(params, "scenarioGroupId"), getParameter(params, "scenarioId")));
                } else {
                    result = testController.getTestCaseIds(getParameter(params, "scenarioGroupId"), getParameter(params, "scenarioId"));
                }
            } catch (Exception e) {
                if (isJSON) {
                    jsonMap.put("exception", e);
                } else {
                    result = e;
                }
            }
            if (isJSON) {
                responseJSON(request, response, jsonMap);
            } else {
                responseBinary(response, result);
            }
        } else if (request.getHeader().getURLMatcher(".*getTestCase").matches()) {
            Map params = parseQuery(request.getHeader().getQuery());
            if (body != null) {
                params.putAll(parseQuery(body));
            }
            Map jsonMap = isJSON ? new HashMap() : null;
            Object result = null;
            try {
                if (isJSON) {
                    jsonMap.put(
                            "TestCase",
                            testController.getTestCase(getParameter(params, "scenarioGroupId"), getParameter(params, "scenarioId"),
                                    getParameter(params, "testcaseId")));
                } else {
                    result = testController.getTestCase(getParameter(params, "scenarioGroupId"), getParameter(params, "scenarioId"),
                            getParameter(params, "testcaseId"));
                }
            } catch (Exception e) {
                if (isJSON) {
                    jsonMap.put("exception", e);
                } else {
                    result = e;
                }
            }
            if (isJSON) {
                responseJSON(request, response, jsonMap);
            } else {
                responseBinary(response, result);
            }
        } else if (request.getHeader().getURLMatcher(".*getCurrentTestCase").matches()) {
            Map jsonMap = isJSON ? new HashMap() : null;
            Object result = null;
            try {
                if (isJSON) {
                    jsonMap.put("CurrentTestCase", testController.getCurrentTestCase());
                } else {
                    result = testController.getCurrentTestCase();
                }
            } catch (Exception e) {
                if (isJSON) {
                    jsonMap.put("exception", e);
                } else {
                    result = e;
                }
            }
            if (isJSON) {
                responseJSON(request, response, jsonMap);
            } else {
                responseBinary(response, result);
            }
        } else if (request.getHeader().getURLMatcher(".*getTestCaseResource").matches()) {
            Map params = parseQuery(request.getHeader().getQuery());
            if (body != null) {
                params.putAll(parseQuery(body));
            }
            Map jsonMap = isJSON ? new HashMap() : null;
            Object result = null;
            try {
                if (isJSON) {
                    jsonMap.put("TestCaseResource", testController.getTestCaseResource(getParameter(params, "scenarioGroupId"),
                            getParameter(params, "scenarioId"), getParameter(params, "testcaseId")));
                } else {
                    result = testController.getTestCaseResource(getParameter(params, "scenarioGroupId"), getParameter(params, "scenarioId"),
                            getParameter(params, "testcaseId"));
                }
            } catch (Exception e) {
                if (isJSON) {
                    jsonMap.put("exception", e);
                } else {
                    result = e;
                }
            }
            if (isJSON) {
                responseJSON(request, response, jsonMap);
            } else {
                responseBinary(response, result);
            }
        } else if (request.getHeader().getURLMatcher(".*getTestCaseStatus").matches()) {
            Map params = parseQuery(request.getHeader().getQuery());
            if (body != null) {
                params.putAll(parseQuery(body));
            }
            Map jsonMap = isJSON ? new HashMap() : null;
            Object result = null;
            try {
                if (isJSON) {
                    jsonMap.put("TestCaseStatus", testController.getTestCaseStatus(getParameter(params, "scenarioGroupId"),
                            getParameter(params, "scenarioId"), getParameter(params, "testcaseId")));
                } else {
                    result = testController.getTestCaseStatus(getParameter(params, "scenarioGroupId"), getParameter(params, "scenarioId"),
                            getParameter(params, "testcaseId"));
                }
            } catch (Exception e) {
                if (isJSON) {
                    jsonMap.put("exception", e);
                } else {
                    result = e;
                }
            }
            if (isJSON) {
                responseJSON(request, response, jsonMap);
            } else {
                responseBinary(response, result);
            }
        } else if (request.getHeader().getURLMatcher(".*downloadTestScenarioGroupResource").matches()) {
            Map params = parseQuery(request.getHeader().getQuery());
            if (body != null) {
                params.putAll(parseQuery(body));
            }
            try {
                testController.downloadTestScenarioGroupResource(getParameter(params, "scenarioGroupId"));
            } catch (Exception e) {
                if (isJSON) {
                    Map jsonMap = new HashMap();
                    jsonMap.put("exception", e);
                    responseJSON(request, response, jsonMap);
                } else {
                    responseBinary(response, e);
                }
            }
        } else if (request.getHeader().getURLMatcher(".*downloadTestScenarioResource").matches()) {
            Map params = parseQuery(request.getHeader().getQuery());
            if (body != null) {
                params.putAll(parseQuery(body));
            }
            try {
                testController.downloadTestScenarioResource(getParameter(params, "scenarioGroupId"), getParameter(params, "scenarioId"));
            } catch (Exception e) {
                if (isJSON) {
                    Map jsonMap = new HashMap();
                    jsonMap.put("exception", e);
                    responseJSON(request, response, jsonMap);
                } else {
                    responseBinary(response, e);
                }
            }
        } else if (request.getHeader().getURLMatcher(".*downloadScenarioResult").matches()) {
            Map params = parseQuery(request.getHeader().getQuery());
            if (body != null) {
                params.putAll(parseQuery(body));
            }
            File zipFile = null;
            try {
                zipFile = testController.downloadScenarioResult(new File(System.getProperty("java.io.tmpdir")),
                        getParameter(params, "scenarioGroupId"), getParameter(params, "scenarioId"), TestController.RESPONSE_FILE_TYPE_ZIP);
                responseZIPFile(response, zipFile);
            } catch (Exception e) {
                if (isJSON) {
                    Map jsonMap = new HashMap();
                    jsonMap.put("exception", e);
                    responseJSON(request, response, jsonMap);
                } else {
                    responseBinary(response, e);
                }
            } finally {
                if (zipFile != null) {
                    zipFile.delete();
                }
            }
        } else if (request.getHeader().getURLMatcher(".*downloadTestCaseResult").matches()) {
            Map params = parseQuery(request.getHeader().getQuery());
            if (body != null) {
                params.putAll(parseQuery(body));
            }
            File zipFile = null;
            try {
                zipFile = testController.downloadTestCaseResult(new File(System.getProperty("java.io.tmpdir")),
                        getParameter(params, "scenarioGroupId"), getParameter(params, "scenarioId"), getParameter(params, "testcaseId"), TestController.RESPONSE_FILE_TYPE_ZIP);
                responseZIPFile(response, zipFile);
            } catch (Exception e) {
                if (isJSON) {
                    Map jsonMap = new HashMap();
                    jsonMap.put("exception", e);
                    responseJSON(request, response, jsonMap);
                } else {
                    responseBinary(response, e);
                }
            } finally {
                if (zipFile != null) {
                    zipFile.delete();
                }
            }
        } else if (request.getHeader().getURLMatcher(".*reset").matches()) {
            Map jsonMap = isJSON ? new HashMap() : null;
            Object result = null;
            try {
                testController.reset();
            } catch (Exception e) {
                if (isJSON) {
                    jsonMap.put("exception", e);
                } else {
                    result = e;
                }
            }
            if (isJSON) {
                responseJSON(request, response, jsonMap);
            } else {
                responseBinary(response, result);
            }
        } else {
            response.setStatusCode(404);
        }
    }

    private Map parseQuery(String query) throws UnsupportedEncodingException {
        Map paramMap = new HashMap();
        if (query == null) {
            return paramMap;
        }
        String[] nameAndValues = query.split("&");
        for (int i = 0; i < nameAndValues.length; i++) {
            int index = nameAndValues[i].indexOf("=");
            String name = null;
            String value = null;
            if (index == -1) {
                name = URLDecoder.decode(nameAndValues[i], urlEncodeCharacterEncoding);
            } else {
                name = URLDecoder.decode(nameAndValues[i].substring(0, index), urlEncodeCharacterEncoding);
                value = URLDecoder.decode(nameAndValues[i].substring(index + 1), urlEncodeCharacterEncoding);
            }
            List values = (List) paramMap.get(name);
            if (values == null) {
                values = new ArrayList();
                paramMap.put(name, values);
            }
            values.add(value);
        }
        return paramMap;
    }

    private String getParameter(Map paramMap, String name) {
        List values = (List) paramMap.get(name);
        return values == null ? null : (String) values.get(0);
    }

    private void responseJSON(HttpRequest request, HttpResponse response, Map jsonMap) throws Exception {
        String charset = defaultResponseCharacterEncoding;
        String acceptCharsetStr = request.getHeader().getHeader("Accept-Charset");
        if (acceptCharsetStr != null) {
            AcceptCharset acceptCharset = null;
            try {
                acceptCharset = new AcceptCharset(acceptCharsetStr);
            } catch (IllegalArgumentException e) {
                response.setStatusCode(406);
                return;
            }
            boolean isSupported = false;
            for (int i = 0; i < acceptCharset.charsetRanges.size(); i++) {
                CharsetRange cr = (CharsetRange) acceptCharset.charsetRanges.get(i);
                if (Charset.isSupported(cr.getCharset())) {
                    isSupported = true;
                    charset = Charset.forName(cr.getCharset()).name();
                    break;
                }
            }
            if (!isSupported) {
                response.setStatusCode(406);
                return;
            }
        }
        response.setHeader("Content-Type", "application/json; charset=" + charset);
        InputStream is = beanJSONConverter.cloneCharacterEncodingToStream(charset).convertToStream(jsonMap);
        OutputStream os = response.getOutputStream();
        byte[] bytes = new byte[1024];
        int len = 0;
        while ((len = is.read(bytes)) > 0) {
            os.write(bytes, 0, len);
        }
    }

    private void responseBinary(HttpResponse response, Object responseObj) throws Exception {
        response.setHeader("Content-Type", "application/octet-stream");
        ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream());
        oos.writeObject(responseObj);
        oos.flush();
    }

    private void responseZIPFile(HttpResponse response, File zipFile) throws Exception {
        response.setHeader("Content-Type", "application/zip; name=" + zipFile.getName());
        InputStream is = new FileInputStream(zipFile);
        try {
            OutputStream os = response.getOutputStream();
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = is.read(bytes)) > 0) {
                os.write(bytes, 0, len);
            }
            os.flush();
        } finally {
            is.close();
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
            StringBuffer buf = new StringBuffer();
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

    protected static class CharsetRange extends HeaderValue {
        protected float q = 1.0f;

        public CharsetRange(String header) throws IllegalArgumentException {
            super(header);
            String qvalue = getParameter("q");
            if (qvalue != null) {
                try {
                    q = Float.parseFloat(qvalue);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("qvalue is illegal. q=" + qvalue);
                }
            }
        }

        public String getCharset() {
            return getValue();
        }

        public void setCharset(String charset) {
            setValue(charset);
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

    protected static class MediaRange extends MediaType {
        protected float q = 1.0f;

        public MediaRange(String header) throws IllegalArgumentException {
            super(header);
            String qvalue = getParameter("q");
            if (qvalue != null) {
                try {
                    q = Float.parseFloat(qvalue);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("qvalue is illegal. q=" + qvalue);
                }
            }
        }
    }

    protected static class Accept {
        protected final List mediaRanges;

        public Accept(String header) throws IllegalArgumentException {
            String[] mediaRangeArray = header.split(",");
            mediaRanges = new ArrayList(mediaRangeArray.length);
            for (int i = 0; i < mediaRangeArray.length; i++) {
                mediaRanges.add(new MediaRange(mediaRangeArray[i]));
            }
            Collections.sort(mediaRanges, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ((MediaRange) o1).q == ((MediaRange) o2).q ? 0 : ((MediaRange) o1).q > ((MediaRange) o2).q ? -1 : 1;
                }
            });
        }
    }

    protected static class AcceptCharset {
        protected final List charsetRanges;

        public AcceptCharset(String header) throws IllegalArgumentException {
            String[] charsetRangeArray = header.split(",");
            charsetRanges = new ArrayList(charsetRangeArray.length);
            for (int i = 0; i < charsetRangeArray.length; i++) {
                charsetRanges.add(new CharsetRange(charsetRangeArray[i]));
            }
            Collections.sort(charsetRanges, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ((CharsetRange) o1).q == ((CharsetRange) o2).q ? 0 : ((CharsetRange) o1).q > ((CharsetRange) o2).q ? -1 : 1;
                }
            });
        }
    }
}