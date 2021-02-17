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
package jp.ossc.nimbus.service.test.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.BufferedReader;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.io.CSVReader;
import jp.ossc.nimbus.service.http.HttpClient;
import jp.ossc.nimbus.service.http.HttpClientFactory;
import jp.ossc.nimbus.service.http.HttpRequest;
import jp.ossc.nimbus.service.http.HttpResponse;
import jp.ossc.nimbus.service.http.RESTHttpRequest;
import jp.ossc.nimbus.service.interpreter.Interpreter;
import jp.ossc.nimbus.service.test.TestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;
import jp.ossc.nimbus.service.test.ChainTestAction;
import jp.ossc.nimbus.service.test.TestContext;

/**
 * HTTPリクエストテストアクション。<p>
 * HTTPリクエストを送信して、HTTPレスポンスをファイルに出力する。<br>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 * 
 * @author M.Takata
 */
public class HttpRequestActionService extends ServiceBase implements TestAction, ChainTestAction.TestActionProcess, TestActionEstimation, HttpRequestActionServiceMBean{
    
    private static final long serialVersionUID = -6266672726524592951L;
    protected ServiceName httpClientFactoryServiceName;
    protected HttpClientFactory httpClientFactory;
    protected ServiceName interpreterServiceName;
    protected Interpreter interpreter;
    protected double expectedCost = Double.NaN;
    
    public void setHttpClientFactoryServiceName(ServiceName name){
        httpClientFactoryServiceName = name;
    }
    public ServiceName getHttpClientFactoryServiceName(){
        return httpClientFactoryServiceName;
    }
    
    public void setInterpreterServiceName(ServiceName name){
        interpreterServiceName = name;
    }
    public ServiceName getInterpreterServiceName(){
        return interpreterServiceName;
    }
    
    public void setHttpClientFactory(HttpClientFactory factory){
        httpClientFactory = factory;
    }
    
    public void setInterpreter(Interpreter interpreter){
        this.interpreter = interpreter;
    }
    
    public void startService() throws Exception{
        if(httpClientFactoryServiceName != null){
            httpClientFactory = (HttpClientFactory)ServiceManagerFactory.getServiceObject(httpClientFactoryServiceName);
        }
        if(httpClientFactory == null){
            throw new IllegalArgumentException("HttpClientFactory is null.");
        }
        if(interpreterServiceName != null){
            interpreter = (Interpreter)ServiceManagerFactory.getServiceObject(interpreterServiceName);
        }
    }
    
    /**
     * HTTPリクエストを送信して、HTTPレスポンスをファイルに出力する。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * clientId
     * actionName
     * replaceValueId-&gt;replaceKey
     * 
     * headerName:headerValue
     * 
     * urlKey=keyValue
     * 
     * bodyType
     * body
     * </pre>
     * clientIdは、{@link HttpClient}オブジェクトを再利用する場合に指定するもので、同一テストケース中に、このTestActionより前に、このクラスのTestActionが存在する場合は、そのアクションIDを指定する。また、同一シナリオ中に、このTestActionより前に、このクラスのTestActionが存在する場合は、テストケースIDとアクションIDをカンマ区切りで指定する。再利用の必要がない場合は、空文字を指定する。<br>
     * actionNameは、{@link HttpClientFactory#createRequest(String)}の引数に指定するアクション名を指定する。<br>
     * replaceValueIdは、リクエストヘッダ及びボディ、URLキーに対する置換を行う値をTestActionの結果から取得するために指定するもので、同一テストケース中のTestActionの結果を取得する場合は、そのアクションIDを指定する。また、同一シナリオ中の他のテストケースのTestActionの結果を取得する場合は、テストケースIDとアクションIDをカンマ区切りで指定する。それに続いて"-&gt;"を挟んで、置換対象の文字列をreplaceKeyとして指定する。この行は、複数指定が可能なため、終わりを示すために、空行を１行入れる。置換が不要な場合は、指定する必要はない。<br>
     * headerNameは、HTTPヘッダ名を指定する。それに続いて":"を挟んで、ヘッダ値をheaderValueとして指定する。この行は、複数指定が可能なため、終わりを示すために、空行を１行入れる。ヘッダが不要な場合は、指定する必要はない。<br>
     * urlKeyは、URLの一部を置換するキーを指定する。それに続いて"="を挟んで、置換する値をkeyValueとして指定する。この行は、複数指定が可能なため、終わりを示すために、空行を１行入れる。URL置換が不要な場合は、指定する必要はない。また、この機能を利用する場合は、{@link RESTHttpRequest}を使用する必要がある。<br>
     * bodyTypeは、"parameter"、"text"、"binary"、"object"、"multipart"のいずれかを指定する。HTTPボディが必要ない場合は、この行以下は必要ない。<br>
     * bodyは、bodyTypeによって、記述方法が異なる。<br>
     * <ul>
     * <li>bodyTypeが"parameter"の場合<br>パラメータをname=valueで指定する。複数ある場合は、改行して指定する。</li>
     * <li>bodyTypeが"text"の場合<br>任意の文字列で指定する。</li>
     * <li>bodyTypeが"binary"の場合<br>バイナリファイルのパスを指定する。パスは、絶対パスまたは、相対パスで指定する。</li>
     * <li>bodyTypeが"object"の場合<br>{@link HttpRequest#setObject(Object)}に設定するオブジェクトを生成するスクリプト文字列を指定する。スクリプト文字列は、{@link Interpreter#evaluate(String)}で評価され、その戻り値がオブジェクトとして使用される。</li>
     * <li>bodyTypeが"multipart"の場合<br>パラメータの場合、name=valueで指定する。ファイルの場合、file:name=filePath,fileName,contentTypeで指定する。fileName,contentTypeは省略可能。複数ある場合は、改行して指定する。</li>
     * </ul>
     * <p>
     * HTTPレスポンスを出力するファイルは、ヘッダファイルとボディファイルの２つである。<br>
     * ヘッダファイルは、アクションIDに拡張子".h.rsp"を付加したファイル名になる。<br>
     * ヘッダファイルのフォーマットは、以下。<br>
     * <pre>
     * HTTP status
     * HTTP message
     * headerName:headerValue
     * </pre>
     * <p>
     * ボディファイルは、アクションIDに拡張子".b.rsp"を付加したファイル名になる。<br>
     * ボディファイルは、HTTPボディをそのまま出力する。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return Map。キー"client"で、HttpClientオブジェクト。キー"response"で、HttpResponse。
     */
    public Object execute(TestContext context, String actionId, Reader resource) throws Exception{
        return execute(context, actionId, null, resource);
    }
    
    /**
     * HTTPリクエストを送信して、HTTPレスポンスをファイルに出力する。<p>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param preResult 1つ前のアクションの戻り値
     * @param resource リソース
     * @return JMXでMBeanを呼び出した戻り値
     * @return Map。キー"client"で、HttpClientオブジェクト。キー"response"で、HttpResponse。
     */
    public Object execute(TestContext context, String actionId, Object preResult, Reader resource) throws Exception{
        BufferedReader br = new BufferedReader(resource);
        Map result = new LinkedHashMap();
        try{
            final String clientId = br.readLine();
            if(clientId == null){
                throw new Exception("Unexpected EOF on clientId");
            }
            HttpClient client = null;
            if(clientId.length() == 0){
                if(preResult != null && (preResult instanceof Map)){
                    client = (HttpClient)((Map)preResult).get("client");
                }else{
                    client = httpClientFactory.createHttpClient();
                }
            }else{
                Object actionResult = null;
                if(clientId.indexOf(",") == -1){
                    actionResult = context.getTestActionResult(clientId);
                }else{
                    String[] ids = clientId.split(",");
                    if(ids.length != 2){
                        throw new Exception("Illegal clientId format. id=" + clientId);
                    }
                    actionResult = context.getTestActionResult(ids[0], ids[1]);
                }
                if(actionResult == null){
                    throw new Exception("TestActionResult not found. id=" + clientId);
                }
                if(actionResult == null || !(actionResult instanceof Map)){
                    throw new Exception("TestActionResult is not Map. result=" + actionResult);
                }
                client = (HttpClient)((Map)actionResult).get("client");
            }
            result.put("client", client);
            final String actionName = br.readLine();
            if(actionName == null){
                throw new Exception("Unexpected EOF on actionName");
            }
            HttpRequest request = httpClientFactory.createRequest(actionName);
            String line = null;
            Map replaceMap = null;
            while((line = br.readLine()) != null && line.length() != 0){
                int index = line.lastIndexOf("->");
                if(index == -1){
                    break;
                }
                if(replaceMap == null){
                    replaceMap = new LinkedHashMap();
                }
                
                String replaceValueId = line.substring(0, index);
                Object replaceValue = null;
                if(replaceValueId != null && replaceValueId.length() != 0){
                    if(replaceValueId.indexOf(",") == -1){
                        replaceValue = context.getTestActionResult(replaceValueId);
                    }else{
                        String[] ids = replaceValueId.split(",");
                        if(ids.length != 2){
                            throw new Exception("Illegal replaceValueId format. id=" + replaceValueId);
                        }
                        replaceValue = context.getTestActionResult(ids[0], ids[1]);
                    }
                }
                replaceMap.put(line.substring(index + 2), replaceValue == null ? "" : replaceValue.toString());
            }
            
            if(line != null && line.length() == 0){
                line = br.readLine();
            }
            do{
                int index = line.indexOf(":");
                if(index == -1){
                    break;
                }
                request.setHeader(line.substring(0, index), replace(line.substring(index + 1), replaceMap));
            }while((line = br.readLine()) != null && line.length() != 0);
            
            if(line != null && line.length() == 0){
                line = br.readLine();
            }
            
            String bodyType = null;
            do{
                int index = line.indexOf("=");
                if(index == -1){
                    bodyType = line;
                    break;
                }
                if(request instanceof RESTHttpRequest){
                    ((RESTHttpRequest)request).setKey(line.substring(0, index), replace(line.substring(index + 1), replaceMap));
                }
            }while((line = br.readLine()) != null && line.length() != 0);
            
            if(line != null && bodyType == null){
                bodyType = br.readLine();
            }
            if(bodyType != null){
                if("parameter".equals(bodyType)){
                    while((line = br.readLine()) != null && line.length() != 0){
                        int index = line.indexOf("=");
                        if(index == -1){
                            throw new Exception("Illegal parameter format. parameter=" + line);
                        }
                        request.setParameter(line.substring(0, index), replace(line.substring(index + 1), replaceMap));
                    }
                }else if("text".equals(bodyType)){
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    String text = null;
                    try{
                        while((line = br.readLine()) != null){
                            pw.println(line);
                        }
                        pw.flush();
                        text = sw.toString();
                    }finally{
                        sw.close();
                        pw.close();
                    }
                    if(request.getCharacterEncoding() != null){
                        request.getOutputStream().write(
                            replace(text, replaceMap).getBytes(request.getCharacterEncoding())
                        );
                    }else{
                        text = replace(text, replaceMap);
                        request.getOutputStream().write(
                            request.getCharacterEncoding() == null ? text.getBytes() : text.getBytes(request.getCharacterEncoding())
                        );
                    }
                }else if("binary".equals(bodyType)){
                    String filePath = br.readLine();
                    if(filePath == null){
                        throw new Exception("Unexpected EOF on body");
                    }
                    File binaryFile = new File(filePath);
                    if(!binaryFile.exists()){
                        binaryFile = new File(context.getCurrentDirectory(), filePath);
                    }
                    if(!binaryFile.exists()){
                        throw new Exception("File of body not found: " + filePath);
                    }
                    final FileInputStream fis = new FileInputStream(binaryFile);
                    try{
                        byte[] bytes = new byte[1024];
                        int len = 0;
                        while((len = fis.read(bytes)) > 0){
                            request.getOutputStream().write(bytes, 0, len);
                        }
                    }finally{
                        fis.close();
                    }
                }else if("object".equals(bodyType)){
                    if(interpreter == null){
                        throw new UnsupportedOperationException("Interpreter is null.");
                    }
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    String script = null;
                    try{
                        while((line = br.readLine()) != null){
                            pw.println(line);
                        }
                        pw.flush();
                        script = sw.toString();
                    }finally{
                        sw.close();
                        pw.close();
                    }
                    script = replace(script, replaceMap);
                    Object requestObject = interpreter.evaluate(script);
                    request.setObject(requestObject);
                }else if("multipart".equals(bodyType)){
                    while((line = br.readLine()) != null && line.length() != 0){
                        int index = line.indexOf("=");
                        if(index == -1){
                            throw new Exception("Illegal parameter format. parameter=" + line);
                        }
                        if(line.startsWith("file:")){
                            String paramName = line.substring(5, index);
                            final String[] params = CSVReader.toArray(
                                line.substring(index + 1),
                                ',',
                                '\\',
                                '"',
                                "",
                                null,
                                true,
                                false,
                                true,
                                false
                            );
                            String filePath = params[0];
                            String fileName = (params.length > 1 && params[1].length() != 0) ? params[1] : null;
                            String contentType = (params.length > 2 && params[2].length() != 0) ? params[2] : null;
                            File file = new File(filePath);
                            if(!file.exists()){
                                file = new File(context.getCurrentDirectory(), filePath);
                            }
                            request.setFileParameter(paramName, file, fileName, contentType);
                        }else{
                            request.setParameter(line.substring(0, index), replace(line.substring(index + 1), replaceMap));
                        }
                    }
                }else if("querystring".equals(bodyType)){
                    String query = br.readLine();
                    if(query == null){
                        throw new Exception("Unexpected EOF on querystring");
                    }
                    query = replace(query, replaceMap);
                    request.setQueryString(query);
                }else{
                    throw new Exception("Unknown bodyType : " + bodyType);
                }
            }
            HttpResponse response = client.executeRequest(request);
            result.put("response", response);
            final File responseHeaderFile = new File(context.getCurrentDirectory(), actionId + ".h.rsp");
            PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(responseHeaderFile))));
            try{
                pw.println(response.getStatusCode());
                pw.println(response.getStatusMessage());
                final Iterator headerNames = response.getHeaderNameSet().iterator();
                while(headerNames.hasNext()){
                    final String headerName = (String)headerNames.next();
                    final String[] values = response.getHeaders(headerName);
                    pw.print(headerName);
                    pw.print(": ");
                    for(int i = 0, imax = values.length; i < imax; i++){
                        pw.print(values[i]);
                        if(i == imax - 1){
                            pw.println();
                        }else{
                            pw.print("; ");
                        }
                    }
                }
                pw.flush();
            }finally{
                pw.close();
                pw = null;
            }
            final File responseBodyFile = new File(context.getCurrentDirectory(), actionId + ".b.rsp");
            final InputStream is = response.getInputStream();
            if(is == null) {
                responseBodyFile.createNewFile();
            } else {
                FileOutputStream fos = new FileOutputStream(responseBodyFile);
                try{
                    byte[] bytes = new byte[1024];
                    int len = 0;
                    while((len = is.read(bytes)) > 0){
                        fos.write(bytes, 0, len);
                    }
                }finally{
                    fos.close();
                    fos = null;
                }
            }
        }finally{
            br.close();
        }
        return result;
    }
    
    protected String replace(String src, Map replaceMap){
        if(replaceMap == null || replaceMap.size() == 0){
            return src;
        }
        String result = src;
        final Iterator entries = replaceMap.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry)entries.next();
            result = result.replaceAll((String)entry.getKey(), (String)entry.getValue());
        }
        return result;
    }
    
    public void setExpectedCost(double cost) {
        expectedCost = cost;
    }
    
    public double getExpectedCost() {
        return expectedCost;
    }
}