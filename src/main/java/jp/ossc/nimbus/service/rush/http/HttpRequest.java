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
package jp.ossc.nimbus.service.rush.http;

import java.util.*;
import java.io.File;
import java.io.OutputStreamWriter;

import jp.ossc.nimbus.beans.PropertyFactory;
import jp.ossc.nimbus.beans.Property;
import jp.ossc.nimbus.beans.dataset.RecordSchema;
import jp.ossc.nimbus.beans.dataset.RecordList;
import jp.ossc.nimbus.service.rush.RushClient;
import jp.ossc.nimbus.service.rush.Request;
import jp.ossc.nimbus.service.rush.RequestParameterSelector;
import jp.ossc.nimbus.service.rush.DefaultRequestParameterSelector;
import jp.ossc.nimbus.service.http.HttpClient;
import jp.ossc.nimbus.service.http.HttpResponse;

/**
 * HTTPリクエスト。<p>
 *
 * @author M.Takata
 */
public class HttpRequest extends Request{
    private String action;
    
    private Map headerMap;
    private Map sessionToHeaderMap;
    
    private Map parameterMap;
    private RequestParameterSelector parameterSelector;
    private String parameterString;
    private File parameterFile;
    private RecordSchema parameterListSchema;
    private RecordList parameterList;
    private Map sessionToParameterMap;
    
    private RequestParameterSelector requestStreamSelector;
    private String requestStreamString;
    private File requestStreamFile;
    private RecordSchema requestStreamListSchema;
    private RecordList requestStreamList;
    private String requestStreamTemplate;
    private File requestStreamTemplateFile;
    private Object requestObject;
    
    private Map responseHeaderToSessionMap;
    private Map responseToSessionMap;
    
    private HttpRushClientService rushClient;
    private String templateName;
    
    /**
     * {@link jp.ossc.nimbus.service.http.HttpRequest HttpRequest}のアクション名を設定する。<p>
     *
     * @param action アクション名
     */
    public void setAction(String action){
        this.action = action;
    }
    
    protected String getAction(){
        return action;
    }
    
    /**
     * HTTPヘッダを設定する。<p>
     *
     * @param name HTTPヘッダ名
     * @param value HTTPヘッダ
     */
    public void setHeader(String name, String value){
        if(headerMap == null){
            headerMap = new HashMap();
        }
        headerMap.put(name, new String[]{value});
    }
    
    /**
     * HTTPヘッダを設定する。<p>
     *
     * @param name HTTPヘッダ名
     * @param value HTTPヘッダ配列
     */
    public void setHeaders(String name, String[] value){
        if(headerMap == null){
            headerMap = new HashMap();
        }
        headerMap.put(name, value);
    }
    
    /**
     * セッションから取得するプロパティ名とリクエストヘッダとして設定するヘッダ名のマッピングを設定する。<p>
     *
     * @param map セッションから取得するプロパティ名とリクエストヘッダとして設定するヘッダ名のマッピング
     */
    public void setSessionHeaderMap(Map map) throws IllegalArgumentException{
        Map tmpMap = new HashMap();
        Iterator entries = map.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry)entries.next();
            tmpMap.put(
                PropertyFactory.createProperty(entry.getKey().toString()),
                entry.getValue().toString()
            );
        }
        sessionToHeaderMap = tmpMap;
    }
    
    /**
     * リクエストパラメータを設定する。<p>
     *
     * @param name リクエストパラメータ名
     * @param value リクエストパラメータ
     */
    public void setParameter(String name, String value){
        if(parameterMap == null){
            parameterMap = new LinkedHashMap();
        }
        String[] vals = (String[])parameterMap.get(name);
        if(vals == null){
            vals = new String[]{value};
            parameterMap.put(name, vals);
        }else{
            final String[] newVals = new String[vals.length + 1];
            System.arraycopy(vals, 0, newVals, 0, vals.length);
            newVals[newVals.length - 1] = value;
            parameterMap.put(name, newVals);
        }
    }
    
    /**
     * リクエストパラメータを設定する。<p>
     *
     * @param name リクエストパラメータ名
     * @param value リクエストパラメータ
     */
    public void setParameters(String name, String[] value){
        if(parameterMap == null){
            parameterMap = new LinkedHashMap();
        }
        parameterMap.put(name, value);
    }
    
    /**
     * リクエストパラメータを設定する。<p>
     *
     * @param name リクエストパラメータ名
     * @param file 送信ファイル
     */
    public void setFileParameter(String name, File file) throws java.io.FileNotFoundException{
        setFileParameter(name, file, null, null);
    }
    
    /**
     * リクエストパラメータを設定する。<p>
     *
     * @param name リクエストパラメータ名
     * @param file 送信ファイル
     * @param fileName 送信ファイル名
     * @param contentType コンテントタイプ
     */
    public void setFileParameter(String name, File file, String fileName, String contentType) throws java.io.FileNotFoundException{
        if(parameterMap == null){
            parameterMap = new LinkedHashMap();
        }
        parameterMap.put(name, new FileParameter(file, fileName, contentType));
    }
    
    /**
     * リクエストパラメータを選択する{@link RequestParameterSelector}を設定する。<p>
     *
     * @param selector RequestParameterSelector
     */
    public void setParameterSelector(RequestParameterSelector selector){
        parameterSelector = selector;
    }
    
    /**
     * リクエストパラメータの選択肢となるRecordListの文字列表現を設定する。<p>
     *
     * @param str RecordListの文字列表現
     */
    public void setParameterString(String str){
        parameterString = str;
    }
    
    /**
     * リクエストパラメータの選択肢となるRecordListの文字列表現のファイルを設定する。<p>
     *
     * @param file RecordListの文字列表現のファイル
     */
    public void setParameterFile(File file){
        parameterFile = file;
    }
    
    /**
     * リクエストパラメータの選択肢となるRecordListのスキーマを設定する。<p>
     *
     * @param schema RecordListのスキーマ
     */
    public void setParameterListSchema(String schema){
        parameterListSchema = RecordSchema.getInstance(schema);
    }
    
    /**
     * リクエストパラメータの選択肢となるRecordListを設定する。<p>
     *
     * @param list RecordList
     */
    public void setParameterList(RecordList list){
        parameterList = list;
    }
    
    /**
     * セッションから取得するプロパティ名とリクエストパラメータとして設定するパラメータ名のマッピングを設定する。<p>
     *
     * @param map セッションから取得するプロパティ名とリクエストパラメータとして設定するパラメータ名のマッピング
     */
    public void setSessionToParameterMap(Map map) throws IllegalArgumentException{
        Map tmpMap = new HashMap();
        Iterator entries = map.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry)entries.next();
            tmpMap.put(
                PropertyFactory.createProperty(entry.getKey().toString()),
                entry.getValue().toString()
            );
        }
        sessionToParameterMap = tmpMap;
    }
    
    /**
     * リクエストストリームへのパラメータを選択する{@link RequestParameterSelector}を設定する。<p>
     *
     * @param selector RequestParameterSelector
     */
    public void setRequestStreamSelector(RequestParameterSelector selector){
        requestStreamSelector = selector;
    }
    
    /**
     * リクエストストリームへのパラメータの選択肢となるRecordListの文字列表現を設定する。<p>
     *
     * @param str RecordListの文字列表現
     */
    public void setRequestStreamString(String str){
        requestStreamString = str;
    }
    
    /**
     * リクエストストリームへのパラメータの選択肢となるRecordListの文字列表現のファイルを設定する。<p>
     *
     * @param file RecordListの文字列表現のファイル
     */
    public void setRequestStreamFile(File file){
        requestStreamFile = file;
    }
    
    /**
     * リクエストストリームへのパラメータの選択肢となるRecordListのスキーマを設定する。<p>
     *
     * @param schema RecordListのスキーマ
     */
    public void setRequestStreamListSchema(String schema){
        requestStreamListSchema = RecordSchema.getInstance(schema);
    }
    
    /**
     * リクエストストリームへのパラメータの選択肢となるRecordListを設定する。<p>
     *
     * @param list RecordList
     */
    public void setRequestStreamList(RecordList list){
        requestStreamList = list;
    }
    
    /**
     * リクエストストリームのテンプレート文字列を設定する。<p>
     *
     * @param template テンプレート文字列
     */
    public void setRequestStreamTemplate(String template){
        requestStreamTemplate = template;
    }
    
    /**
     * リクエストストリームのテンプレートファイルを設定する。<p>
     *
     * @param file テンプレートファイル
     */
    public void setRequestStreamTemplateFile(File file){
        requestStreamTemplateFile = file;
    }
    
    /**
     * リクエストオブジェクトを設定する。<p>
     *
     * @param obj リクエストオブジェクト
     */
    public void setRequestObject(Object obj){
        requestObject = obj;
    }
    
    /**
     * レスポンスヘッダから取得するヘッダ名とセッションに格納するキー名のマッピングを設定する。<p>
     *
     * @param map レスポンスヘッダから取得するヘッダ名とセッションに格納するキー名のマッピング
     */
    public void setResponseHeaderToSessionMap(Map map) throws IllegalArgumentException{
        Map tmpMap = new HashMap();
        Iterator entries = map.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry)entries.next();
            tmpMap.put(
                entry.getKey().toString(),
                entry.getValue().toString()
            );
        }
        responseHeaderToSessionMap = tmpMap;
    }
    
    /**
     * {@link HttpResponse#getObject()}から取得するプロパティ名とセッションに格納するキー名のマッピングを設定する。<p>
     *
     * @param map {@link HttpResponse#getObject()}から取得するプロパティ名とセッションに格納するキー名のマッピング
     */
    public void setResponseToSessionMap(Map map) throws IllegalArgumentException{
        Map tmpMap = new HashMap();
        Iterator entries = map.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry)entries.next();
            tmpMap.put(
                PropertyFactory.createProperty(entry.getKey().toString()),
                entry.getValue().toString()
            );
        }
        responseToSessionMap = tmpMap;
    }
    
    public void init(RushClient client, int requestId) throws Exception{
        rushClient = (HttpRushClientService)client;
        if(parameterString != null){
            parameterList = rushClient.convertToRecordList(parameterString, parameterListSchema == null ? null : new RecordList(null, parameterListSchema));
        }else if(parameterFile != null){
            parameterList = rushClient.convertToRecordList(parameterFile, parameterListSchema == null ? null : new RecordList(null, parameterListSchema));
        }
        if(parameterList != null && parameterSelector == null){
            parameterSelector = new DefaultRequestParameterSelector();
        }
        
        if(requestStreamString != null){
            requestStreamList = rushClient.convertToRecordList(requestStreamString, requestStreamListSchema == null ? null : new RecordList(null, requestStreamListSchema));
        }else if(requestStreamFile != null){
            requestStreamList = rushClient.convertToRecordList(requestStreamFile, requestStreamListSchema == null ? null : new RecordList(null, requestStreamListSchema));
        }
        if(requestStreamList != null && requestStreamSelector == null){
            requestStreamSelector = new DefaultRequestParameterSelector();
        }
        if(requestStreamTemplate != null || requestStreamTemplateFile != null){
            templateName = requestId + '_' + action;
            if(requestStreamTemplate != null){
                rushClient.setTemplate(templateName, requestStreamTemplate);
            }else if(requestStreamTemplateFile != null){
                rushClient.setTemplateFile(templateName, requestStreamTemplateFile);
            }
        }
    }
    
    public void setupRequest(
        HttpClient client,
        jp.ossc.nimbus.service.http.HttpRequest request,
        Map session,
        int id,
        int roopCount,
        int count
    ) throws Exception{
        if(headerMap != null){
            Iterator entries = headerMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                request.setHeaders((String)entry.getKey(), (String[])entry.getValue());
            }
        }
        if(sessionToHeaderMap != null){
            Iterator entries = sessionToHeaderMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                Object headerValue = ((Property)entry.getKey()).getProperty(session);
                if(headerValue != null){
                    if(headerValue instanceof String){
                        request.setHeader((String)entry.getValue(), (String)headerValue);
                    }else if(headerValue instanceof String[]){
                        request.setHeaders((String)entry.getValue(), (String[])headerValue);
                    }else{
                        request.setHeader((String)entry.getValue(), headerValue.toString());
                    }
                }
            }
        }
        
        if(parameterMap != null){
            Iterator entries = parameterMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                Object param = entry.getValue();
                if(param instanceof String[]){
                    request.setParameters((String)entry.getKey(), (String[])param);
                }else{
                    FileParameter fileParameter = (FileParameter)param;
                    request.setFileParameter((String)entry.getKey(), fileParameter.file, fileParameter.fileName, fileParameter.contentType);
                }
            }
        }
        if(parameterSelector != null && parameterList != null){
            Map paramMap = parameterSelector.getParameter(id, roopCount, count, parameterList);
            Iterator entries = paramMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                Object param = entry.getValue();
                if(param instanceof String){
                    request.setParameter((String)entry.getKey(), (String)param);
                }else if(param instanceof String[]){
                    request.setParameters((String)entry.getKey(), (String[])param);
                }else{
                    FileParameter fileParameter = (FileParameter)param;
                    request.setFileParameter((String)entry.getKey(), fileParameter.file, fileParameter.fileName, fileParameter.contentType);
                }
            }
        }
        if(sessionToParameterMap != null){
            Iterator entries = sessionToParameterMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                Object param = ((Property)entry.getKey()).getProperty(session);
                if(param != null){
                    if(param instanceof String){
                        request.setParameter((String)entry.getValue(), (String)param);
                    }else if(param instanceof String[]){
                        request.setParameters((String)entry.getValue(), (String[])param);
                    }else{
                        request.setParameter((String)entry.getValue(), param.toString());
                    }
                }
            }
        }
        if(requestObject != null){
            request.setObject(requestObject);
        }else if(templateName != null){
            Map paramMap = null;
            if(requestStreamSelector != null && requestStreamList != null){
                paramMap = requestStreamSelector.getParameter(id, roopCount, count, requestStreamList);
            }else{
                paramMap = new HashMap();
            }
            paramMap.put("session", session);
            OutputStreamWriter osw = request.getCharacterEncoding() == null
                    ? new OutputStreamWriter(request.getOutputStream())
                        : new OutputStreamWriter(request.getOutputStream(), request.getCharacterEncoding());
            rushClient.transform(
                templateName,
                paramMap,
                osw
            );
            osw.flush();
        }
    }
    
    public void handleResponse(
        HttpClient client,
        HttpResponse response,
        Map session,
        int id,
        int roopCount,
        int count
    ) throws Exception{
        if(responseHeaderToSessionMap != null){
            Iterator entries = responseHeaderToSessionMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                String[] headers = response.getHeaders((String)entry.getKey());
                session.put((String)entry.getValue(), headers);
            }
        }
        if(responseToSessionMap != null){
            Object responseObj = response.getObject();
            Iterator entries = responseToSessionMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                Object param = ((Property)entry.getKey()).getProperty(responseObj);
                session.put((String)entry.getValue(), param);
            }
        }
    }
    
    public static class FileParameter{
        public File file;
        public String fileName;
        public String contentType;
        
        public FileParameter(File file, String fileName, String contentType){
            this.file = file;
            this.fileName = fileName;
            this.contentType = contentType;
        }
    }
}