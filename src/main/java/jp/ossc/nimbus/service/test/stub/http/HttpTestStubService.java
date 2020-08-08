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
package jp.ossc.nimbus.service.test.stub.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import jp.ossc.nimbus.core.ServiceLoader;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceMetaData;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.io.CSVReader;
import jp.ossc.nimbus.io.RecurciveSearchFile;
import jp.ossc.nimbus.service.http.proxy.HttpProcessServiceBase;
import jp.ossc.nimbus.service.http.proxy.HttpRequest;
import jp.ossc.nimbus.service.http.proxy.HttpResponse;
import jp.ossc.nimbus.service.interpreter.Interpreter;
import jp.ossc.nimbus.service.test.StubResourceManager;
import jp.ossc.nimbus.service.test.TestStub;

/**
 * HTTPテストスタブ。<p>
 * {@link jp.ossc.nimbus.service.http.proxy.ProxyServerService ProxyServerService}に、{@link jp.ossc.nimbus.service.http.proxy.Process Process}サービスとして設定する事で使用する。<br>
 * シナリオ開始の通知を受けると、{@link StubResourceManager}から、シナリオ単位でスタブのリソースをダウンロードし、配置する。<br>
 * テストケース開始の通知を受けると、テストケースIDを保持し、HTTPリクエストに備える。<br>
 * HTTPリクエストを受けると、そのテストケース内のリソースファイルから、リクエストに合致するファイルを特定して、その内容に従ってHTTPレスポンスを応答する。また、同時にHTTPリクエストの内容をリクエストファイルとして出力する。<br>
 * リソースファイルのフォーマットは、以下。<br>
 * <pre>
 * urlPattern
 * queryPattern|bodyPattern
 * status,message
 * headerName:headerValue
 *
 * sleep millsec
 * interpreter:start
 * script
 * interpreter:end
 * bodyType
 * body
 * </pre>
 * urlPatternは、リクエストされたURLとマッチングするURLの正規表現を指定する。<br>
 * queryPatternは、リクエストされたクエリの内容とマッチングする正規表現を指定する。クエリでマッチングさせる必要がない場合は、空文字を指定する。<br>
 * bodyPatternは、リクエストされたボディの内容とマッチングする正規表現を指定する。ボディでマッチングさせる必要がない場合は、空文字を指定する。<br>
 * urlPatternは、リクエストされたURLとマッチングするURLの正規表現を指定する。<br>
 * statusは、応答するHTTPステータスを指定する。それに続いてカンマで区切り、応答するメッセージをmessageで指定する。この行に、空文字を指定すると、200応答する。また、messageが不要な場合は、statusのみ指定する。<br>
 * headerNameは、HTTPヘッダ名を指定する。それに続いて":"を挟んで、ヘッダ値をheaderValueとして指定する。この行は、複数指定が可能なため、終わりを示すために、空行を１行入れる。ヘッダが不要な場合は、指定する必要はない。<br>
 * sleepは、応答時間を調整するために、指定されたミリ秒の間スリープする場合に、指定する。必要がない場合は、この行は必要ない。<br>
 * interpreter:startとinterpreter:endの行で挟んで、応答するボディを編集するスクリプトをscriptに指定できる。scriptは、{@link Interpreter#evaluate(String,Map)}で評価され、引数の変数マップには、"request"で{@link HttpRequest}が、"response"でボディ文字列が渡される。スクリプトを指定する必要がない場合は、この行は必要ない。<br>
 * bodyTypeは、"text"、"binary"のいずれかを指定する。HTTPボディが必要ない場合は、この行以下は必要ない。<br>
 * bodyは、bodyTypeによって、記述方法が異なる。<br>
 * <ul>
 * <li>bodyTypeが"text"の場合<br>任意の文字列で指定する。</li>
 * <li>bodyTypeが"binary"の場合<br>バイナリファイルのファイル名を指定する。</li>
 * </ul>
 * <p>
 * 受信したHTTPリクエストを出力するファイルは、ヘッダファイルとボディファイルの２つである。<br>
 * ヘッダファイルは、リソースファイルのファイル名に拡張子".h.req"を付加したファイル名になる。<br>
 * ヘッダファイルのフォーマットは、以下。<br>
 * <pre>
 * method url version
 * headerName:headerValue
 * </pre>
 * <p>
 * ボディファイルは、リソースファイルのファイル名に拡張子".b.req"を付加したファイル名になる。<br>
 * ボディファイルは、HTTPボディをそのまま出力する。<br>
 *
 * @author M.Takata
 */
public class HttpTestStubService extends HttpProcessServiceBase implements TestStub, HttpTestStubServiceMBean{

    private static final long serialVersionUID = 356502420591126756L;

    protected static final RequestKey NOT_FOUND = new RequestKey();

    protected String id;
    protected ServiceName stubResourceManagerServiceName;
    protected StubResourceManager stubResourceManager;
    protected ServiceName interpreterServiceName;
    protected Interpreter interpreter;
    protected File resourceDirectory;
    protected String fileEncoding;
    protected String httpVersion;
    protected Map httpHeaders;
    protected String[] binaryFileExtensions;
    protected boolean isAllowRepeatRequest;
    protected boolean isSafeMultithread = true;
    protected boolean isSaveRequestFile = true;
    protected boolean isCacheResponse;

    protected Map responseMap;
    protected Map binaryMap;
    protected Map evidenceMap;
    protected Set binaryFileExtensionSet;
    protected String userId;
    protected String scenarioGroupId;
    protected String scenarioId;
    protected String testcaseId;
    protected Object lock = new String("lock");
    protected Map responseCacheMap;


    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }

    public void setFileEncoding(String encoding) {
        fileEncoding = encoding;
    }
    public String getFileEncoding() {
        return fileEncoding;
    }

    public void setStubResourceManagerServiceName(ServiceName name) {
        stubResourceManagerServiceName = name;
    }
    public ServiceName getStubResourceManagerServiceName() {
        return stubResourceManagerServiceName;
    }

    public void setInterpreterServiceName(ServiceName name) {
        interpreterServiceName = name;
    }
    public ServiceName getInterpreterServiceName() {
        return interpreterServiceName;
    }

    public void setResourceDirectory(File dir) {
        resourceDirectory = dir;
    }
    public File getResourceDirectory() {
        return resourceDirectory;
    }

    public void setHttpVersion(String version) {
        httpVersion = version;
    }
    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpHeaders(String name, String[] values){
        httpHeaders.put(name, values);
    }
    public String[] getHttpHeaders(String name){
        return httpHeaders == null ? null : (String[])httpHeaders.get(name);
    }
    public String getHttpHeader(){
        if(httpHeaders == null || httpHeaders.size() == 0){
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Iterator headers = httpHeaders.entrySet().iterator();
        while(headers.hasNext()){
            Map.Entry header = (Map.Entry)headers.next();
            pw.print(header.getKey());
            pw.print(": ");
            String[] values = (String[])header.getValue();
            for(int i = 0, imax = values.length; i < imax; i++){
                pw.print(values[i]);
                if(i != imax - 1){
                    pw.print("; ");
                }
            }
            pw.println();
        }
        return sw.toString();
    }

    public void setBinaryFileExtensions(String[] exts){
        binaryFileExtensions = exts;
    }
    public String[] getBinaryFileExtensions(){
        return binaryFileExtensions;
    }

    public boolean isAllowRepeatRequest(){
        return isAllowRepeatRequest;
    }
    public void setAllowRepeatRequest(boolean isAllow){
        isAllowRepeatRequest = isAllow;
    }

    public boolean isSafeMultithread(){
        return isSafeMultithread;
    }
    public void setSafeMultithread(boolean isSafe){
        isSafeMultithread = isSafe;
    }

    public boolean isSaveRequestFile(){
        return isSaveRequestFile;
    }
    public void setSaveRequestFile(boolean isSave){
        isSaveRequestFile = isSave;
    }
    
    public boolean isCacheResponse(){
        return isCacheResponse;
    }
    public void setCacheResponse(boolean isCache){
        isCacheResponse = isCache;
    }

    public void setStubResourceManager(StubResourceManager manager) {
        stubResourceManager = manager;
    }

    public void setInterpreter(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public void createService() throws Exception {
        responseMap = Collections.synchronizedMap(new HashMap());
        binaryMap = Collections.synchronizedMap(new HashMap());
        evidenceMap = Collections.synchronizedMap(new HashMap());
        httpHeaders = new HashMap();
        binaryFileExtensionSet = new HashSet();
        responseCacheMap = Collections.synchronizedMap(new HashMap());
    }

    public void startService() throws Exception {
        if(id == null){
            throw new IllegalArgumentException("Id is null.");
        }

        if(stubResourceManagerServiceName != null){
            stubResourceManager = (StubResourceManager)ServiceManagerFactory.getServiceObject(stubResourceManagerServiceName);
        }
        if(stubResourceManager == null){
            throw new IllegalArgumentException("StubResourceManager is null.");
        }
        if(interpreterServiceName != null){
            interpreter = (Interpreter)ServiceManagerFactory.getServiceObject(interpreterServiceName);
        }

        File serviceDefDir = null;
        if(getServiceNameObject() != null){
            ServiceMetaData metaData = ServiceManagerFactory.getServiceMetaData(getServiceNameObject());
            if(metaData != null){
                ServiceLoader loader = metaData.getServiceLoader();
                if(loader != null){
                    String filePath = loader.getServiceURL().getFile();
                    if(filePath != null){
                        serviceDefDir = new File(filePath).getParentFile();
                    }
                }
            }
        }
        if(resourceDirectory == null){
            resourceDirectory = serviceDefDir == null ? new File(id) : new File(serviceDefDir, id);
        }else{
            if(!resourceDirectory.isAbsolute() && !resourceDirectory.exists() && serviceDefDir != null){
                resourceDirectory = new File(serviceDefDir, resourceDirectory.getPath());
            }
            resourceDirectory = new File(resourceDirectory, id);
        }
        if(!resourceDirectory.exists()){
            resourceDirectory.mkdirs();
        }

        if(binaryFileExtensions != null){
            for(int i = 0; i < binaryFileExtensions.length; i++){
                String ext = binaryFileExtensions[i];
                if(ext == null || ext.length() == 0){
                    continue;
                }
                if(ext.charAt(0) != '.'){
                    ext = '.' + ext;
                }
                binaryFileExtensionSet.add(ext);
            }
        }
    }

    public void stopService() throws Exception {
        cancelScenario();
        if(binaryFileExtensionSet != null){
            binaryFileExtensionSet.clear();
        }
    }

    public void doProcess(HttpRequest request, HttpResponse response) throws Exception{

        if(request.getBody() != null){
            request.getBody().read();
        }

        if(httpVersion != null){
            response.setVersion(httpVersion);
        }

        if(httpHeaders != null && httpHeaders.size() != 0){
            Iterator entries = httpHeaders.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                response.setHeaders((String)entry.getKey(), (String[])entry.getValue());
            }
        }
        if(isSafeMultithread){
            synchronized(lock){
                doProcessInternal(request, response);
            }
        }else{
            doProcessInternal(request, response);
        }
    }

    protected  void doProcessInternal(HttpRequest request, HttpResponse response) throws Exception{
        if(scenarioGroupId == null || scenarioId == null || testcaseId == null){
            response.setStatusCode(503);
            response.setStatusMessage("Testcase not started. scenarioGroupId=" + scenarioGroupId + ", scenarioId=" + scenarioId + ", testcaseId=" + testcaseId);
            return;
        }
        Map evidenceMapByTestCase = (Map)evidenceMap.get(testcaseId);
        if(evidenceMapByTestCase == null){
            synchronized(evidenceMap){
                evidenceMapByTestCase = (Map)evidenceMap.get(testcaseId);
                if(evidenceMapByTestCase == null){
                    evidenceMapByTestCase = Collections.synchronizedMap(new HashMap());
                    evidenceMap.put(testcaseId, evidenceMapByTestCase);
                    evidenceMapByTestCase.put(NOT_FOUND, Collections.synchronizedList(new ArrayList()));
                }
            }
        }
        if(responseMap.size() == 0){
            responseNotFound(
                request,
                response,
                scenarioGroupId,
                scenarioId,
                testcaseId,
                evidenceMapByTestCase,
                "Scenario not found. scenarioGroupId=" + scenarioGroupId + ", scenarioId=" + scenarioId
            );
            return;
        }
        Map map = (Map)responseMap.get(testcaseId);
        Map binMap = (Map)binaryMap.get(testcaseId);
        if(map == null){
            responseNotFound(
                request,
                response,
                scenarioGroupId,
                scenarioId,
                testcaseId,
                evidenceMapByTestCase,
                "Testcase not found. scenarioGroupId=" + scenarioGroupId + ", scenarioId=" + scenarioId + ", testcaseId=" + testcaseId
            );
            return;
        }
        Iterator entries = map.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry)entries.next();
            RequestKey key = (RequestKey)entry.getKey();
            Matcher urlMatcher = request.getHeader().getURLMatcher(key.urlPattern);
            Matcher bodyMatcher = null;
            if("POST".equals(request.getHeader().getMethod()) || "PUT".equals(request.getHeader().getMethod())){
                if(key.bodyPattern != null && request.getBody() != null){
                    bodyMatcher = request.getBody().getMatcher(key.bodyPattern);
                }
            }else{
                if(key.bodyPattern != null && request.getHeader().getQuery() != null){
                    bodyMatcher = request.getHeader().getQueryMatcher(key.bodyPattern);
                }
            }

            if(urlMatcher.matches() && (bodyMatcher == null || bodyMatcher.matches())){
                List evidenceList = (List)evidenceMapByTestCase.get(key);
                if(evidenceList == null){
                    synchronized(evidenceMapByTestCase){
                        evidenceList = (List)evidenceMapByTestCase.get(key);
                        if(evidenceList == null){
                            evidenceList = Collections.synchronizedList(new ArrayList());
                            evidenceMapByTestCase.put(key, evidenceList);
                        }
                    }
                }
                ResponseList list = (ResponseList)entry.getValue();
                File file = null;
                file = list.get();
                if(file == null){
                    responseNotFound(
                        request,
                        response,
                        scenarioGroupId,
                        scenarioId,
                        testcaseId,
                        evidenceMapByTestCase,
                        "The number of calls is too much. scenarioGroupId=" + scenarioGroupId + ", scenarioId=" + scenarioId + ", testcaseId=" + testcaseId + ", url=" + request.getHeader().getURL()
                    );
                    return;
                }

                if(isSaveRequestFile){
                    File requestFile = saveRequestFile(request, file);
                    evidenceList.add(requestFile);
                }
                evidenceList.add(file);
                ResponseData data = null;
                if(isCacheResponse && responseCacheMap.containsKey(file)){
                    data = (ResponseData)responseCacheMap.get(file);
                }else{
                    data = new ResponseData();
                    data.read(file);
                    if(isCacheResponse){
                        responseCacheMap.put(file, data);
                    }
                }
                OutputStreamWriter osw = null;
                try {
                    if(data.statusAndMessage == null){
                        response.setStatusCode(500);
                        response.setStatusMessage("Status not found in file. file=" + file);
                        return;
                    }else{
                        String[] statusAndMessageArray = CSVReader.toArray(
                            data.statusAndMessage,
                            ',',
                            '\\',
                            null,
                            null,
                            true,
                            false,
                            true,
                            true
                        );
                        if(statusAndMessageArray == null || statusAndMessageArray.length == 0){
                            response.setStatusCode(200);
                        }else if(statusAndMessageArray.length == 1){
                            response.setStatusCode(Integer.parseInt(statusAndMessageArray[0]));
                        }else if(statusAndMessageArray.length == 2){
                            response.setStatusCode(Integer.parseInt(statusAndMessageArray[0]));
                            response.setStatusMessage(statusAndMessageArray[1]);
                        }else{
                            response.setStatusCode(500);
                            response.setStatusMessage("Status is illegal in file. file=" + file + ", status=" + data.statusAndMessage);
                            return;
                        }
                    }
                    if(data.headerMap != null){
                        Iterator headers = data.headerMap.entrySet().iterator();
                        while(headers.hasNext()){
                            Map.Entry header = (Map.Entry)headers.next();
                            response.setHeaders((String)header.getKey(), (String[])header.getValue());
                        }
                    }
                    if(data.sleep > 0){
                        Thread.sleep(data.sleep);
                    }
                    if(data.responseType != null){
                        if("text".equals(data.responseType)){
                            String responseStr = data.response;
                            if(interpreter != null && data.interpretScript != null){
                                Map variables = new HashMap();
                                variables.put("request", request);
                                variables.put("response", responseStr);
                                responseStr = (String)interpreter.evaluate(data.interpretScript, variables);
                            }
                            if(responseStr != null){
                                String encoding = response.getCharacterEncoding();
                                osw = encoding == null ? new OutputStreamWriter(response.getOutputStream()) : new OutputStreamWriter(response.getOutputStream(), encoding);
                                osw.write(responseStr, 0, responseStr.length());
                                osw.flush();
                            }
                        }else if("binary".equals(data.responseType)){
                            String binstr = data.response;
                            File binaryFile = binMap == null ? null : (File)binMap.get(binstr);
                            if(binaryFile != null){
                                FileInputStream bfis = new FileInputStream(binaryFile);
                                try{
                                    byte[] binary = new byte[1024];
                                    int readLength = 0;
                                    while((readLength = bfis.read(binary, 0, binary.length)) != -1){
                                        response.getOutputStream().write(binary, 0, readLength);
                                    }
                                }finally{
                                    bfis.close();
                                }
                            }
                        }
                    }
                }finally{
                    if(osw != null){
                        osw.close();
                    }
                }
                return;
            }
        }
        responseNotFound(
            request,
            response,
            scenarioGroupId,
            scenarioId,
            testcaseId,
            evidenceMapByTestCase,
            "TestCase not found. scenarioGroupId=" + scenarioGroupId + ", scenarioId=" + scenarioId + ", testcaseId=" + testcaseId + ", url=" + request.getHeader().getURL()
        );
    }

    protected File saveRequestFile(HttpRequest request, File file) throws IOException{
        final File requestFile = new File(file.getParentFile(), file.getName() + ".h.req");
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(requestFile)));
        try{
            pw.println(request.getHeader());
            pw.flush();
        }finally {
            pw.close();
            pw = null;
        }
        if(request.getBody() == null){
            return requestFile;
        }
        FileOutputStream fos = new FileOutputStream(new File(file.getParentFile(), file.getName() + ".b.req"));
        try{
            byte[] bytes = request.getBody().toByteArray();
            fos.write(bytes, 0, bytes.length);
        }finally{
            fos.close();
            fos = null;
        }
        return requestFile;
    }

    protected void responseNotFound(HttpRequest request, HttpResponse response, String scenarioGroupId, String scenarioId, String testcaseId, Map evidenceMapByTestCase, String message) throws IOException {
        List notFoundList = (List)evidenceMapByTestCase.get(NOT_FOUND);
        File file = null;
        synchronized(notFoundList){
            file = new File(new File(new File(new File(resourceDirectory, scenarioGroupId), scenarioId), testcaseId), "NOT_FOUND_" + (notFoundList.size() + 1) + ".txt");
        }
        if(isSaveRequestFile){
            File requestFile = saveRequestFile(request, file);
            notFoundList.add(requestFile);
        }
        response.setStatusCode(404);
        response.setStatusMessage(message);
    }

    public void startScenario(String userId, String scenarioGroupId, String scenarioId) throws Exception{
        synchronized(lock){
            if(this.scenarioGroupId != null && this.scenarioId != null){
                throw new Exception("Scenario started. scenarioGroupId=" + this.scenarioGroupId + ", scenarioId=" + this.scenarioId + ", userId=" + this.userId);
            }
            if(scenarioGroupId.equals(this.scenarioGroupId) && scenarioId.equals(this.scenarioId)){
                return;
            }
            RecurciveSearchFile scenarioDir = new RecurciveSearchFile(new File(resourceDirectory, scenarioGroupId), scenarioId);
            if(!scenarioDir.deleteAllTree()){
                throw new Exception("Resource directory can not delete. path=" + scenarioDir);
            }
            if(!scenarioDir.mkdirs()) {
                throw new Exception("Resource directory can not make. path=" + scenarioDir);
            }
            stubResourceManager.downloadScenarioResource(scenarioDir, scenarioGroupId, scenarioId, id);
            responseMap.clear();
            binaryMap.clear();
            evidenceMap.clear();
            File[] testcaseDirs = scenarioDir.listFiles();
            if(testcaseDirs == null || testcaseDirs.length == 0){
                this.userId = userId;
                this.scenarioGroupId = scenarioGroupId;
                this.scenarioId = scenarioId;
                this.testcaseId = null;
                return;
            }
            for(int i = 0; i < testcaseDirs.length; i++){
                if(!testcaseDirs[i].isDirectory()){
                    continue;
                }
                String testcaseId = testcaseDirs[i].getName();
                File[] files = testcaseDirs[i].listFiles();
                if(files == null){
                    continue;
                }
                sort(files);
                for(int j = 0; j < files.length; j++){
                    File file = files[j];
                    if(file.isDirectory()){
                        continue;
                    }
                    int extIndex = file.getName().lastIndexOf('.');
                    String ext = null;
                    if(extIndex != -1){
                        ext = file.getName().substring(extIndex);
                    }
                    if(ext != null && binaryFileExtensionSet.contains(ext)){
                        Map binMap = (Map)binaryMap.get(testcaseId);
                        if(binMap == null){
                            binMap = new LinkedHashMap();
                            binaryMap.put(testcaseId, binMap);
                        }
                        binMap.put(file.getName(), file);
                    }else{
                        Map map = (Map)responseMap.get(testcaseId);
                        if(map == null){
                            map = new LinkedHashMap();
                            responseMap.put(testcaseId, map);
                        }
                        FileInputStream fis = new FileInputStream(file);
                        InputStreamReader isr = fileEncoding == null ? new InputStreamReader(fis) : new InputStreamReader(fis, fileEncoding);
                        RequestKey reqKey = new RequestKey();
                        try{
                            BufferedReader br = new BufferedReader(isr);
                            reqKey.urlPattern = br.readLine();
                            if(reqKey.urlPattern != null && reqKey.urlPattern.length() == 0){
                                reqKey.urlPattern = null;
                            }
                            reqKey.bodyPattern = br.readLine();
                            if(reqKey.bodyPattern != null && reqKey.bodyPattern.length() == 0){
                                reqKey.bodyPattern = null;
                            }
                        }finally{
                            fis.close();
                            isr.close();
                        }
                        if(reqKey.urlPattern == null){
                            throw new Exception("URL not contains in Response file. path=" + file);
                        }
                        ResponseList list = (ResponseList)map.get(reqKey);
                        if(list == null){
                            list = new ResponseList();
                            map.put(reqKey, list);
                        }
                        list.add(file);
                        list.sort();
                    }
                }
            }
            this.userId = userId;
            this.scenarioGroupId = scenarioGroupId;
            this.scenarioId = scenarioId;
            this.testcaseId = null;
        }
    }

    public void cancelScenario() throws Exception{
        synchronized(lock){
            if(scenarioGroupId == null && scenarioId == null){
                return;
            }
            responseMap.clear();
            binaryMap.clear();
            evidenceMap.clear();
            responseCacheMap.clear();
            userId = null;
            RecurciveSearchFile scenarioDir = new RecurciveSearchFile(new File(resourceDirectory, scenarioGroupId), scenarioId);
            scenarioDir.deleteAllTree();
            this.scenarioGroupId = null;
            this.scenarioId = null;
            this.testcaseId = null;
        }
    }

    public void endScenario() throws Exception{
        synchronized(lock){
            if(scenarioGroupId == null && scenarioId == null){
                return;
            }
            RecurciveSearchFile scenarioDir = new RecurciveSearchFile(new File(resourceDirectory, scenarioGroupId), scenarioId);
            responseMap.clear();
            binaryMap.clear();
            evidenceMap.clear();
            responseCacheMap.clear();
            userId = null;
            this.scenarioGroupId = null;
            this.scenarioId = null;
            this.testcaseId = null;
            scenarioDir.deleteAllTree();
        }
    }

    public void startTestCase(String testcaseId) throws Exception{
        synchronized(lock){
            if(this.scenarioGroupId != null && this.scenarioId != null && this.testcaseId != null){
                throw new Exception("Testcase started. scenarioGroupId=" + this.scenarioGroupId + ", scenarioId=" + this.scenarioId + ", testcaseId=" + this.testcaseId + ", userId=" + this.userId);
            }
            if(this.scenarioGroupId == null || this.scenarioId == null){
                throw new Exception("Scenario not started. scenarioGroupId=" + this.scenarioGroupId + ", scenarioId=" + this.scenarioId + ", testcaseId=" + this.testcaseId);
            }
            if(!responseMap.containsKey(testcaseId)){
                throw new Exception("Testcase not found. scenarioGroupId=" + this.scenarioGroupId + ", scenarioId=" + this.scenarioId + ", testcaseId=" + testcaseId);
            }
            this.testcaseId = testcaseId;
            Map responseMapByTestCase = (Map)responseMap.get(testcaseId);
            Set responseFiles = new HashSet();
            if(responseMapByTestCase != null){
                Iterator itr = responseMapByTestCase.values().iterator();
                while(itr.hasNext()){
                    ResponseList list = (ResponseList)itr.next();
                    list.reset();
                    responseFiles.addAll(list.files());
                }
            }
            Map evidenceMapByTestCase = (Map)evidenceMap.get(testcaseId);
            if(evidenceMapByTestCase != null){
                Iterator itr = evidenceMapByTestCase.values().iterator();
                while(itr.hasNext()){
                    List list = (List)itr.next();
                    Iterator files = list.iterator();
                    while(files.hasNext()){
                        File file = (File)files.next();
                        if(!responseFiles.contains(file)){
                            file.delete();
                        }
                    }
                }
            }
        }
    }

    public void endTestCase() throws Exception{
        synchronized(lock){
            if(scenarioGroupId == null || scenarioId == null || testcaseId == null){
                return;
            }
            File testcaseDir = new File(new File(new File(resourceDirectory, scenarioGroupId), scenarioId), testcaseId);
            if(testcaseDir.exists()){
                stubResourceManager.uploadTestCaseResource(testcaseDir, scenarioGroupId, scenarioId, testcaseId, id);
            }
            this.testcaseId = null;
        }
    }

    protected static class RequestKey{
        public String urlPattern;
        public String bodyPattern;

        public boolean equals(Object obj){
            if(obj == this){
                return true;
            }
            if(obj == null || !(obj instanceof RequestKey)){
                return false;
            }
            RequestKey cmp = (RequestKey)obj;
            if((urlPattern == null && cmp.urlPattern != null)
                || (urlPattern != null && (cmp.urlPattern == null || !urlPattern.equals(cmp.urlPattern)))){
                return false;
            }
            if((bodyPattern == null && cmp.bodyPattern != null)
                || (bodyPattern != null && (cmp.bodyPattern == null || !bodyPattern.equals(cmp.bodyPattern)))){
                return false;
            }
            return true;
        }

        public int hashCode(){
            int hashCode = 0;
            if(urlPattern != null){
                hashCode += urlPattern.hashCode();
            }
            if(bodyPattern != null){
                hashCode += bodyPattern.hashCode();
            }
            return hashCode;
        }
    }

    protected class ResponseList{
        private List list = new ArrayList();
        private int requestCount = 0;
        public void add(File file){
            list.add(file);
        }
        public void sort(){
            Collections.sort(list);
        }
        public synchronized File get(){
            if(list.size() > requestCount){
                return (File)list.get(requestCount++);
            }else{
                if(isAllowRepeatRequest){
                    requestCount = 0;
                    return get();
                }else{
                    return null;
                }
            }
        }
        
        public List files(){
            return list;
        }
        
        public void reset(){
            requestCount = 0;
        }
    }
    
    protected class ResponseData{
        public String statusAndMessage;
        public Map headerMap;
        public long sleep;
        public String interpretScript;
        public String responseType;
        public String response;
        
        public void read(File file) throws Exception{
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = fileEncoding == null ? new InputStreamReader(fis) : new InputStreamReader(fis, fileEncoding);
            try{
                BufferedReader br = new BufferedReader(isr);
                String urlPattern = br.readLine();
                String bodyPattern = br.readLine();
                statusAndMessage = br.readLine();
                if(statusAndMessage == null){
                    return;
                }else{
                    String[] statusAndMessageArray = CSVReader.toArray(
                        statusAndMessage,
                        ',',
                        '\\',
                        null,
                        null,
                        true,
                        false,
                        true,
                        true
                    );
                    if(statusAndMessageArray != null
                        && statusAndMessageArray.length > 2){
                        return;
                    }
                }
                String headerLine = null;
                while((headerLine = br.readLine()) != null){
                    if(headerLine.length() == 0){
                        break;
                    }
                    final int index = headerLine.indexOf(":");
                    if(index != -1){
                        if(headerMap == null){
                            headerMap = new HashMap();
                        }
                        String name = headerLine.substring(0, index);
                        String val = headerLine.substring(index + 1);
                        String[] vals = (String[])headerMap.get(name);
                        if(vals == null){
                            vals = new String[1];
                            vals[0] = val;
                            headerMap.put(name, vals);
                        }else{
                            final String[] newVals = new String[vals.length + 1];
                            System.arraycopy(vals, 0, newVals, 0, vals.length);
                            newVals[newVals.length - 1] = val;
                            headerMap.put(name, newVals);
                        }
                    }else{
                        break;
                    }
                }
                if(headerLine != null){
                    String line = headerLine.length() == 0 ? br.readLine() : headerLine;
                    if(line != null && line.startsWith("sleep ")){
                        sleep = Long.parseLong(line.split(" ")[1]);
                        line = br.readLine();
                    }
                    if("interpreter:start".equals(line)){
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        while((line = br.readLine()) != null){
                            if("interpreter:end".equals(line)){
                                line = br.readLine();
                                break;
                            }
                            pw.println(line);
                        }
                        pw.flush();
                        interpretScript = sw.toString();
                    }
                    if(line != null){
                        responseType = line;
                        if("text".equals(responseType)){
                            StringWriter sw = new StringWriter();
                            char[] chars = new char[1024];
                            int readLen = 0;
                            while ((readLen = br.read(chars, 0, chars.length)) != -1) {
                                sw.write(chars, 0, readLen);
                            }
                            response = sw.toString();
                        }else if("binary".equals(responseType)){
                            response = br.readLine();
                        }
                    }
                }
            }finally{
                fis.close();
                isr.close();
            }
        }
    }

    private void sort(File[] files){
        Arrays.sort(files, new Comparator() {
            public int compare(Object f1, Object f2){
                return ((File)f1).getName().compareTo(((File)f2).getName());
            }
        });
    }
}