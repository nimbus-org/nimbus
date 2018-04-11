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
package jp.ossc.nimbus.service.test.stub.tcp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceLoader;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceMetaData;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.io.RecurciveSearchFile;
import jp.ossc.nimbus.service.server.Request;
import jp.ossc.nimbus.service.server.RequestContext;
import jp.ossc.nimbus.service.server.Response;
import jp.ossc.nimbus.service.interpreter.Interpreter;
import jp.ossc.nimbus.service.queue.QueueHandler;
import jp.ossc.nimbus.service.queue.QueueHandlerContainer;
import jp.ossc.nimbus.service.server.DefaultServerService;
import jp.ossc.nimbus.service.test.StubResourceManager;
import jp.ossc.nimbus.service.test.TestStub;

/**
 * TCPでのテキストでの送受信用のテストスタブ。
 * <p>
 * {@link jp.ossc.nimbus.service.server.DefaultServerService DefaultServerService}に、
 * {@link jp.ossc.nimbus.service.queue.QueueHandlerContainer QueueHandlerContainer}サービスとして設定する事で使用する。<br>
 * シナリオ開始の通知を受けると、{@link StubResourceManager}から、シナリオ単位でスタブのリソースをダウンロードし、配置する。<br>
 * テストケース開始の通知を受けると、テストケースIDを保持し、TCPリクエストに備える。<br>
 * TCPリクエストを受けると、そのテストケース内のリソースファイルから、リクエストに合致するファイルを特定して、
 * その内容に従ってTCPレスポンスを応答する。<br>
 * また、同時にTCPリクエストの内容をリクエストファイルとして出力する。<br>
 * リソースファイルのフォーマットは、以下。<br>
 *
 * <pre>
 * textPattern
 * sleep millsec
 * interpreter:start
 * script
 * interpreter:end
 * responseMessage
 * </pre>
 *
 * textPatternは、リクエストされたボディの内容とマッチングする正規表現を指定する。ボディでマッチングさせる必要がない場合は、空文字を指定する。<br>
 * sleepは、応答時間を調整するために、指定されたミリ秒の間スリープする場合に、指定する。必要がない場合は、この行は必要ない。<br>
 * interpreter:startとinterpreter:endの行で挟んで、応答するボディを編集するスクリプトをscriptに指定できる。
 * scriptは、{@link Interpreter#evaluate(String,Map)}で評価され、引数の変数マップには、
 * "request"で{@link Request}が、"responseMessage"でボディ文字列が渡される。
 * スクリプトを指定する必要がない場合は、この行は必要ない。<br>
 * responseMessageはレスポンスのメッセージを任意の文字列で指定する。<br>
 * <p>
 * 受信したリクエストを出力するファイルは、".req"を付加したファイル名になる。<br>
 *
 * @author Y.Nakashima
 */
public class TCPTextTestStubService extends ServiceBase implements TestStub, QueueHandler, TCPTextTestStubServiceMBean {

    private static final long serialVersionUID = 356502420591126756L;

    protected static final String NOT_FOUND = "NOT_FOUND";

    protected String id;
    protected ServiceName stubResourceManagerServiceName;
    protected StubResourceManager stubResourceManager;
    protected ServiceName interpreterServiceName;
    protected Interpreter interpreter;
    protected File resourceDirectory;
    protected String fileEncoding;
    protected String requestEncoding;
    protected String responseEncoding;

    protected boolean isAllowRepeatRequest;
    protected boolean isSafeMultithread = true;
    protected boolean isSaveRequestFile = true;
    protected boolean isCacheResponse;

    protected Map responseMap;
    protected Map evidenceMap;

    protected String userId;
    protected String scenarioGroupId;
    protected String scenarioId;
    protected String testcaseId;
    protected Object lock = new String("lock");
    protected Map responseCacheMap;

    protected String notFoundResponseMessage = "NOT_FOUND";

    protected String defaultResponseMessage = "";

    protected String newLineCode;

    private Map<String, Pattern> patterns;


    public String getNewLineCode() {
        return newLineCode;
    }

    public void setNewLineCode(String newLineCode) {
        this.newLineCode = newLineCode;
    }

    public String getNotFoundMessage() {
        return notFoundResponseMessage;
    }

    /**
     * リソースファイルフォーマットにおいて、responseMessageが空欄の場合に返す文字列を設定する。
     * @param defaultResponseMessage
     */
    public void setDefaultResponseMessage(String defaultResponseMessage) {
        this.defaultResponseMessage = defaultResponseMessage;
    }

    /**
     * リクエストに対するレスポンスのリソースファイルが見つからない場合に返す文字列を設定する。
     * @param notFoundResponseMessage
     */
    public void setNotFoundMessage(String notFoundMessage) {
        this.notFoundResponseMessage = notFoundMessage;
    }

    public String getRequestEncoding() {
        return requestEncoding;
    }

    /**
     * リクエストのエンコードを指定する。
     * @param requestEncoding
     */
    public void setRequestEncoding(String requestEncoding) {
        this.requestEncoding = requestEncoding;
    }

    public String getResponseEncoding() {
        return responseEncoding;
    }

    /**
     * レスポンスのエンコードを指定する。
     * @param responseEncoding
     */
    public void setResponseEncoding(String responseEncoding) {
        this.responseEncoding = responseEncoding;
    }

    public String getId() {
        return id;
    }

    /**
     * スタブサーバを識別するIDを指定する
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * リソースファイルのエンコードを指定する。
     */
    public void setFileEncoding(String encoding) {
        fileEncoding = encoding;
    }

    public String getFileEncoding() {
        return fileEncoding;
    }

    /**
     * {@link StubResourceManager}のサービス名を指定する
     */
    public void setStubResourceManagerServiceName(ServiceName name) {
        stubResourceManagerServiceName = name;
    }

    public ServiceName getStubResourceManagerServiceName() {
        return stubResourceManagerServiceName;
    }

    /**
     * {@link Interpreter}のサービス名を指定する
     */
    public void setInterpreterServiceName(ServiceName name) {
        interpreterServiceName = name;
    }

    public ServiceName getInterpreterServiceName() {
        return interpreterServiceName;
    }

    /**
     * サーバリソースファイル置き場を指定する
     * デフォルトはこのクラスもちいたサービス定義ファイルの親ディレクトリ
     */
    public void setResourceDirectory(File dir) {
        resourceDirectory = dir;
    }

    public File getResourceDirectory() {
        return resourceDirectory;
    }

    public boolean isSafeMultithread() {
        return isSafeMultithread;
    }

    /**
     * リクエスト/レスポンス処理にロックをかける
     */
    public void setSafeMultithread(boolean isSafe) {
        isSafeMultithread = isSafe;
    }

    public boolean isSaveRequestFile() {
        return isSaveRequestFile;
    }

    /**
     * リクエストファイルを保存するかを設定する
     * デフォルトはtrue
     */
    public void setSaveRequestFile(boolean isSave) {
        isSaveRequestFile = isSave;
    }

    public boolean isCacheResponse() {
        return isCacheResponse;
    }

    /**
     * レスポンスをキャッシュするか設定する
     * 高速性が必要な場合trueにする
     * デフォルトはfalse
     */
    public void setCacheResponse(boolean isCache) {
        isCacheResponse = isCache;
    }

    /**
     * {@link StubResourceManager}を設定する
     * デフォルトはnull<br>
     * {@link #setStubResourceManagerServiceName(ServiceName)}で指定されたサービスが優先される
     * @param manager
     */
    public void setStubResourceManager(StubResourceManager manager) {
        stubResourceManager = manager;
    }

    /**
     * {@link Interpreter}を設定する
     * デフォルトはnull<br>
     * {@link #setInterpreterServiceName(ServiceName)}で指定されたサービスが優先される
     * @param interpreter
     */
    public void setInterpreter(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    /**
     * サービスのライフサイクル create 時に呼ばれる
     * 内部のコレクションオブジェクトを生成する
     */
    public void createService() throws Exception {
        responseMap = Collections.synchronizedMap(new HashMap());
        evidenceMap = Collections.synchronizedMap(new HashMap());
        responseCacheMap = Collections.synchronizedMap(new HashMap());
    }

    /**
     * サービスのライフサイクル start 時に呼ばれる
     * メンバに従いサービスを設定する
     */
    public void startService() throws Exception {
        if (id == null) {
            throw new IllegalArgumentException("Id is null.");
        }

        if (stubResourceManagerServiceName != null) {
            stubResourceManager = (StubResourceManager) ServiceManagerFactory
                    .getServiceObject(stubResourceManagerServiceName);
        }
        if (stubResourceManager == null) {
            throw new IllegalArgumentException("StubResourceManager is null.");
        }
        if (interpreterServiceName != null) {
            interpreter = (Interpreter) ServiceManagerFactory.getServiceObject(interpreterServiceName);
        }

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
        if (resourceDirectory == null) {
            resourceDirectory = serviceDefDir == null ? new File(id) : new File(serviceDefDir, id);
        } else if (!resourceDirectory.isAbsolute() && !resourceDirectory.exists() && serviceDefDir != null) {
            resourceDirectory = new File(serviceDefDir, resourceDirectory.getPath());
        }
        if (!resourceDirectory.exists()) {
            resourceDirectory.mkdirs();
        }
    }

    /**
     * サービスのライフサイクル start 時に呼ばれる
     * {@link #cancelScenario()}を呼び出す
     */
    public void stopService() throws Exception {
        cancelScenario();
    }

    /**
     * テストフレームワーク シナリオ開始時に呼び出される
     * {@link #stubResourceManager}からリソースファイルを取得する
     */
    public void startScenario(String userId, String scenarioGroupId, String scenarioId) throws Exception {
        synchronized (lock) {
            if (this.scenarioGroupId != null && this.scenarioId != null) {
                throw new Exception("Scenario started. scenarioGroupId=" + this.scenarioGroupId + ", scenarioId="
                        + this.scenarioId + ", userId=" + this.userId);
            }
            if (scenarioGroupId.equals(this.scenarioGroupId) && scenarioId.equals(this.scenarioId)) {
                return;
            }
            RecurciveSearchFile scenarioDir = new RecurciveSearchFile(new File(resourceDirectory, scenarioGroupId),
                    scenarioId);
            if (!scenarioDir.deleteAllTree()) {
                throw new Exception("Resource directory can not delete. path=" + scenarioDir);
            }
            if (!scenarioDir.mkdirs()) {
                throw new Exception("Resource directory can not make. path=" + scenarioDir);
            }
            stubResourceManager.downloadScenarioResource(scenarioDir, scenarioGroupId, scenarioId, id);
            responseMap.clear();
            evidenceMap.clear();
            File[] testcaseDirs = scenarioDir.listFiles();
            if (testcaseDirs == null || testcaseDirs.length == 0) {
                this.userId = userId;
                this.scenarioGroupId = scenarioGroupId;
                this.scenarioId = scenarioId;
                this.testcaseId = null;
                return;
            }
            for (int i = 0; i < testcaseDirs.length; i++) {
                if (!testcaseDirs[i].isDirectory()) {
                    continue;
                }
                String testcaseId = testcaseDirs[i].getName();
                File[] files = testcaseDirs[i].listFiles();
                if (files == null) {
                    continue;
                }
                sort(files);
                for (int j = 0; j < files.length; j++) {
                    File file = files[j];
                    if (file.isDirectory()) {
                        continue;
                    }

                    Map map = (Map) responseMap.get(testcaseId);
                    if (map == null) {
                        map = new LinkedHashMap();
                        responseMap.put(testcaseId, map);
                    }
                    FileInputStream fis = new FileInputStream(file);
                    InputStreamReader isr = fileEncoding == null ? new InputStreamReader(fis)
                            : new InputStreamReader(fis, fileEncoding);
                    String reqKey = null;
                    BufferedReader br = null;
                    try {
                        br = new BufferedReader(isr);
                        reqKey = br.readLine();
                    } finally {
                        if(br != null) {br.close();}
                        fis.close();
                        isr.close();
                    }
                    if (reqKey == null) {
                        throw new Exception("textPattern not contains in Response file. path=" + file);
                    }
                    ResponseList list = (ResponseList) map.get(reqKey);
                    if (list == null) {
                        list = new ResponseList();
                        map.put(reqKey, list);
                    }
                    list.add(file);
                    list.sort();
                }
            }
            this.userId = userId;
            this.scenarioGroupId = scenarioGroupId;
            this.scenarioId = scenarioId;
            this.testcaseId = null;
        }
    }

    /**
     * シナリオキャンセル時に呼び出される
     * リソース、エビデンス、キャッシュをクリアし、配置したリソースファイルを削除する
     */
    public void cancelScenario() throws Exception {
        synchronized (lock) {
            if (scenarioGroupId == null && scenarioId == null) {
                return;
            }
            responseMap.clear();
            evidenceMap.clear();
            responseCacheMap.clear();
            userId = null;
            RecurciveSearchFile scenarioDir = new RecurciveSearchFile(new File(resourceDirectory, scenarioGroupId),
                    scenarioId);
            scenarioDir.deleteAllTree();
            this.scenarioGroupId = null;
            this.scenarioId = null;
            this.testcaseId = null;
        }
    }

    /**
     * テストフレームワーク シナリオ終了時に呼び出される
     * リソース、エビデンス、キャッシュをクリアし、配置したリソースファイルを削除する
     */
    public void endScenario() throws Exception {
        synchronized (lock) {
            if (scenarioGroupId == null && scenarioId == null) {
                return;
            }
            RecurciveSearchFile scenarioDir = new RecurciveSearchFile(new File(resourceDirectory, scenarioGroupId),
                    scenarioId);
            responseMap.clear();
            evidenceMap.clear();
            responseCacheMap.clear();
            userId = null;
            this.scenarioGroupId = null;
            this.scenarioId = null;
            this.testcaseId = null;
            scenarioDir.deleteAllTree();
        }
    }

    /**
     * テストフレームワーク テストケース開始時に呼び出される
     * テストに対応するリソースファイルが無ければ例外発生
     */
    public void startTestCase(String testcaseId) throws Exception {
        synchronized (lock) {
            if (this.scenarioGroupId != null && this.scenarioId != null && this.testcaseId != null) {
                throw new Exception("Testcase started. scenarioGroupId=" + this.scenarioGroupId + ", scenarioId="
                        + this.scenarioId + ", testcaseId=" + this.testcaseId + ", userId=" + this.userId);
            }
            if (this.scenarioGroupId == null || this.scenarioId == null) {
                throw new Exception("Scenario not started. scenarioGroupId=" + this.scenarioGroupId + ", scenarioId="
                        + this.scenarioId + ", testcaseId=" + this.testcaseId);
            }
            if (!responseMap.containsKey(testcaseId)) {
                throw new Exception("Testcase not found. scenarioGroupId=" + this.scenarioGroupId + ", scenarioId="
                        + this.scenarioId + ", testcaseId=" + testcaseId);
            }
            this.testcaseId = testcaseId;
        }
    }

    /**
     * テストフレームワーク テストケース終了時に呼び出される
     * テストケースディレクトリにリソースファイルをアップロードする
     */
    public void endTestCase() throws Exception {
        synchronized (lock) {
            if (scenarioGroupId == null || scenarioId == null || testcaseId == null) {
                return;
            }
            File testcaseDir = new File(new File(new File(resourceDirectory, scenarioGroupId), scenarioId), testcaseId);
            if (testcaseDir.exists()) {
                stubResourceManager.uploadTestCaseResource(testcaseDir, scenarioGroupId, scenarioId, testcaseId, id);
            }
            this.testcaseId = null;
        }
    }



    protected File saveRequestFile(String requestMsg, File file) throws IOException {
        final File requestFile = new File(file.getParentFile(), file.getName() + ".req");
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(requestFile)));
        try {
            pw.println(requestMsg);
            pw.flush();
        } finally {
            pw.close();
            pw = null;
        }

        return requestFile;
    }

    private String readInputStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try{
            InputStreamReader reader = new InputStreamReader(is, Charset.forName(requestEncoding));

            int read;
            char[] buf = new char[128];
            while((read = reader.read(buf)) != -1){
                sb.append(buf, 0, read);
            };
        }catch (IOException e) {
            throw e;
        }

        return sb.toString();
    }

    protected void responseNotFound(String msg, Response response, String scenarioGroupId, String scenarioId,
            String testcaseId, Map evidenceMapByTestCase) throws IOException {
        List notFoundList = (List) evidenceMapByTestCase.get(NOT_FOUND);
        File file = null;
        synchronized (notFoundList) {
            file = new File(new File(new File(new File(resourceDirectory, scenarioGroupId), scenarioId), testcaseId),
                    NOT_FOUND + "_" + (notFoundList.size() + 1) + ".txt");
        }
        if (isSaveRequestFile) {
            File requestFile = saveRequestFile(msg, file);
            notFoundList.add(requestFile);
        }

        OutputStreamWriter osw = null;
        try{
            String encoding = responseEncoding;
            osw = encoding == null ? new OutputStreamWriter(response.getOutputStream())
                    : new OutputStreamWriter(response.getOutputStream(), encoding);

            if(notFoundResponseMessage != null) {
                osw.write(notFoundResponseMessage, 0, notFoundResponseMessage.length());
            }
            if(newLineCode != null) {
                osw.write(newLineCode, 0, newLineCode.length());
            }

            osw.flush();
        } finally {
            if (osw != null) {
                osw.close();
            }
        }
    }

    protected class ResponseList {
        private List list = new ArrayList();
        private int requestCount = 0;

        public void add(File file) {
            list.add(file);
        }

        public void sort() {
            Collections.sort(list);
        }

        public synchronized File get() {
            if (list.size() > requestCount) {
                return (File) list.get(requestCount++);
            } else {
                if (isAllowRepeatRequest) {
                    requestCount = 0;
                    return get();
                } else {
                    return null;
                }
            }
        }
    }

    protected class ResponseData {
        public long sleep;
        public String interpretScript;
        public String responseMessage;

        public void read(File file) throws Exception {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = fileEncoding == null ? new InputStreamReader(fis)
                    : new InputStreamReader(fis, fileEncoding);
            BufferedReader br = null;
            try {
                br = new BufferedReader(isr);
                String textPattern = br.readLine();// 読み捨て

                String line = br.readLine();
                if (line != null && line.startsWith("sleep ")) {
                    sleep = Long.parseLong(line.split(" ")[1]);
                    line = br.readLine();
                }
                if ("interpreter:start".equals(line)) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    while ((line = br.readLine()) != null) {
                        if ("interpreter:end".equals(line)) {
                            line = br.readLine();
                            break;
                        }
                        pw.println(line);
                    }
                    pw.flush();
                    interpretScript = sw.toString();
                }
                if (line != null) {
                    responseMessage = br.readLine();
                } else if (defaultResponseMessage != null) {
                    responseMessage = defaultResponseMessage;
                }

            } finally {
                fis.close();
                isr.close();
                if(br != null) {
                    br.close();
                }
            }
        }
    }

    private void sort(File[] files) {
        Arrays.sort(files, new Comparator() {
            public int compare(Object f1, Object f2) {
                return ((File) f1).getName().compareTo(((File) f2).getName());
            }
        });
    }


    /* QueueHandlerContainer実装 */
    /**
     * {@link Request}からメッセージを受け取り、それに対応するレスポンスを{@ Response}のメッセージとして送信する
     * 対応したレスポンスがない場合、{@link #defaultResponseMessage}を返す。<br>
     * 対応したレスポンスがある場合、ResponseのOutputStreamに書き込んで返す。<br>
     * 返す際には、{@link #newLineCode}を文字列末に付与する。<br>
     * @param request
     * @param response
     * @throws Exception
     */
    public void doProcess(Request request, Response response) throws Exception {
        if (isSafeMultithread) {
            synchronized (lock) {
                doProcessInternal(request, response);
            }
        } else {
            doProcessInternal(request, response);
        }
    }

    private Matcher getMacher(String str, String pattern) {
        if(patterns == null) {
            patterns = new HashMap<String, Pattern>();
        }
        if(!patterns.containsKey(pattern)) {
            patterns.put(pattern, Pattern.compile(pattern));
        }

        return patterns.get(pattern).matcher(str == null ? "" : str);
    }

    /*
     * QueueにpushされたRequestとResonseオブジェクトを用いたメイン処理。
     * RequestのInputStreamから読込んだ文字列に対応したレスポンスをResponseのOutputStreamに書き込んでいく。
     * 対応したレスポンスがない場合、responseNotFound()によりメッセージを返す。
     * 対応したレスポンスがある場合、ResponseのOutputStreamに書き込んで返す。
     * 返す際には、newLineCodeを文字列末に付与する。
     */
    protected void doProcessInternal(Request request, Response response) throws Exception {
        if (scenarioGroupId == null || scenarioId == null || testcaseId == null) {
            String msg = "Testcase not started. scenarioGroupId=" + scenarioGroupId + ", scenarioId=" + scenarioId + ", testcaseId=" + testcaseId;
            byte[] msgByte = msg.getBytes();
            if(newLineCode != null) {
                msgByte = ArrayUtils.addAll(msgByte, newLineCode.getBytes());
            }
            response.getOutputStream().write(msgByte);
            return;
        }

        Map evidenceMapByTestCase = (Map) evidenceMap.get(testcaseId);
        if (evidenceMapByTestCase == null) {
            synchronized (evidenceMap) {
                evidenceMapByTestCase = (Map) evidenceMap.get(testcaseId);
                if (evidenceMapByTestCase == null) {
                    evidenceMapByTestCase = Collections.synchronizedMap(new HashMap());
                    evidenceMap.put(testcaseId, evidenceMapByTestCase);
                    evidenceMapByTestCase.put(NOT_FOUND, Collections.synchronizedList(new ArrayList()));
                }
            }
        }
        if (responseMap.size() == 0) {
            responseNotFound("", response, scenarioGroupId, scenarioId, testcaseId, evidenceMapByTestCase);
            return;
        }
        Map map = (Map) responseMap.get(testcaseId);
        if (map == null) {
            responseNotFound("", response, scenarioGroupId, scenarioId, testcaseId, evidenceMapByTestCase);
            return;
        }

        String text = readInputStream(request.getInputStream());

        Iterator entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            String textPattern = (String) entry.getKey();

            Matcher textMatcher = getMacher(text,textPattern);

            if (textMatcher.matches()) {
                List evidenceList = (List) evidenceMapByTestCase.get(textPattern);
                if (evidenceList == null) {
                    synchronized (evidenceMapByTestCase) {
                        evidenceList = (List) evidenceMapByTestCase.get(textPattern);
                        if (evidenceList == null) {
                            evidenceList = Collections.synchronizedList(new ArrayList());
                            evidenceMapByTestCase.put(textPattern, evidenceList);
                        }
                    }
                }
                ResponseList list = (ResponseList) entry.getValue();
                File file = null;
                file = list.get();
                if (file == null) {
                    responseNotFound(text, response, scenarioGroupId, scenarioId, testcaseId, evidenceMapByTestCase);
                    return;
                }

                if (isSaveRequestFile) {
                    File requestFile = saveRequestFile(text, file);
                    evidenceList.add(requestFile);
                }
                evidenceList.add(file);
                ResponseData data = null;
                if (isCacheResponse && responseCacheMap.containsKey(file)) {
                    data = (ResponseData) responseCacheMap.get(file);
                } else {
                    data = new ResponseData();
                    data.read(file);
                    if (isCacheResponse) {
                        responseCacheMap.put(file, data);
                    }
                }
                OutputStreamWriter osw = null;
                try {
                    if (data.sleep > 0) {
                        Thread.sleep(data.sleep);
                    }

                    String responseStr = data.responseMessage;
                    if (interpreter != null && data.interpretScript != null) {
                        Map variables = new HashMap();
                        variables.put("request", request);
                        variables.put("responseMessage", responseStr);
                        responseStr = (String) interpreter.evaluate(data.interpretScript, variables);
                    }
                    if (responseStr != null) {
                        String encoding = responseEncoding;
                        osw = encoding == null ? new OutputStreamWriter(response.getOutputStream())
                                : new OutputStreamWriter(response.getOutputStream(), encoding);
                        String sendMessage = responseStr;

                        if(newLineCode != null) {
                            sendMessage += newLineCode;
                        }

                        osw.write(sendMessage, 0, sendMessage.length());
                        osw.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (osw != null) {
                        osw.close();
                    }
                }
                return;
            }
        }
        responseNotFound(text, response, scenarioGroupId, scenarioId, testcaseId, evidenceMapByTestCase);
    }

    public boolean isAllowRepeatRequest() {
        return isAllowRepeatRequest;
    }

    /**
     * 同じリクエストを繰り返してよいか設定する
     * デフォルトはfalse<br>
     * falseの場合、２回目の同じリクエストはレスポンスがnot found 扱いとなる
     */
    public void setAllowRepeatRequest(boolean isAllowRepeatRequest) {
        this.isAllowRepeatRequest = isAllowRepeatRequest;
    }

    public boolean handleError(Object obj, Throwable th) throws Throwable {
        // nop
        return false;
    }

    public void handleRetryOver(Object obj, Throwable th) throws Throwable {
        // nop
    }

    /**
     * Queueにenqueueされた{@link RequestContext}が渡され、それを@{link {@link #doProcess(Request, Response)}に渡す
     * Queueからメインの処理へのエントリポイント
     */
    public void handleDequeuedObject(Object obj) throws Throwable {
        RequestContext requestContext = (RequestContext)obj;
        doProcess(requestContext.getRequest(), requestContext.getResponse());
    }
}