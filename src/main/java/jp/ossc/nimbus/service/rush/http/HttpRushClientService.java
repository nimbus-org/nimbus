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

import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.Writer;
import java.io.IOException;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.dataset.RecordList;
import jp.ossc.nimbus.service.rush.*;
import jp.ossc.nimbus.service.http.HttpClientFactory;
import jp.ossc.nimbus.service.http.HttpClient;
import jp.ossc.nimbus.service.http.HttpResponse;
import jp.ossc.nimbus.service.http.HttpException;
import jp.ossc.nimbus.service.interpreter.Interpreter;
import jp.ossc.nimbus.service.interpreter.CompiledInterpreter;
import jp.ossc.nimbus.service.interpreter.EvaluateException;
import jp.ossc.nimbus.service.template.TemplateEngine;
import jp.ossc.nimbus.service.template.TemplateTransformException;
import jp.ossc.nimbus.util.converter.BindingStreamConverter;
import jp.ossc.nimbus.util.converter.StreamStringConverter;
import jp.ossc.nimbus.util.converter.RecordListCSVConverter;
import jp.ossc.nimbus.util.converter.ConvertException;

/**
 * HTTPラッシュクライアント。<p>
 *
 * @author M.Takata
 */
public class HttpRushClientService extends ServiceBase implements RushClient, HttpRushClientServiceMBean{
    
    private ServiceName httpClientFactoryServiceName;
    private ServiceName templateEngineServiceName;
    private ServiceName recordListConverterServiceName;
    private ServiceName interpreterServiceName;
    private String encoding;
    
    private int id;
    private HttpClientFactory httpClientFactory;
    private HttpClient client;
    private Map session;
    private TemplateEngine templateEngine;
    private Interpreter interpreter;
    private StreamStringConverter recordListConverter;
    
    public void setHttpClientFactoryServiceName(ServiceName name){
        httpClientFactoryServiceName = name;
    }
    public ServiceName getHttpClientFactoryServiceName(){
        return httpClientFactoryServiceName;
    }
    
    public void setTemplateEngineServiceName(ServiceName name){
        templateEngineServiceName = name;
    }
    public ServiceName getTemplateEngineServiceName(){
        return templateEngineServiceName;
    }
    
    public void setRecordListStreamConverterServiceName(ServiceName name){
        recordListConverterServiceName = name;
    }
    public ServiceName getRecordListStreamConverterServiceName(){
        return recordListConverterServiceName;
    }
    
    public void setInterpreterServiceName(ServiceName name){
        interpreterServiceName = name;
    }
    public ServiceName getInterpreterServiceName(){
        return interpreterServiceName;
    }
    
    public void setEncoding(String encoding){
        this.encoding = encoding;
    }
    public String getEncoding(){
        return encoding;
    }
    
    public void createService() throws Exception{
        session = new HashMap();
    }
    
    public void startService() throws Exception{
        if(httpClientFactoryServiceName != null){
            httpClientFactory = (HttpClientFactory)ServiceManagerFactory.getServiceObject(httpClientFactoryServiceName);
        }
        if(httpClientFactory == null){
            throw new IllegalArgumentException("HttpClientFactory is null");
        }
        if(templateEngineServiceName != null){
            templateEngine = (TemplateEngine)ServiceManagerFactory.getServiceObject(templateEngineServiceName);
        }
        if(interpreterServiceName != null){
            interpreter = (Interpreter)ServiceManagerFactory.getServiceObject(interpreterServiceName);
        }
        
        if(recordListConverterServiceName != null){
            recordListConverter = (StreamStringConverter)ServiceManagerFactory.getServiceObject(recordListConverterServiceName);
        }
        if(recordListConverter == null){
            recordListConverter = new RecordListCSVConverter(
                RecordListCSVConverter.CSV_TO_RECORDLIST
            );
            ((RecordListCSVConverter)recordListConverter).setExistsHeader(true);
        }
        if(encoding != null){
            recordListConverter.setCharacterEncodingToObject(encoding);
        }
    }
    
    public void setTemplateFile(String name, File templateFile){
        templateEngine.setTemplateFile(name, templateFile, encoding);
    }
    
    public void setTemplate(String name, String template){
        templateEngine.setTemplate(name, template, encoding);
    }
    
    public void transform(String name, Map dataMap, Writer writer) throws TemplateTransformException, IOException{
        templateEngine.transform(name, dataMap, writer);
    }
    
    public RecordList convertToRecordList(String str, RecordList list) throws ConvertException, IOException{
        if(list == null){
            return (RecordList)recordListConverter.convertToObject(new ByteArrayInputStream(str.getBytes(encoding)));
        }else{
            return (RecordList)((BindingStreamConverter)recordListConverter).convertToObject(new ByteArrayInputStream(str.getBytes(encoding)), list);
        }
    }
    
    public RecordList convertToRecordList(File file, RecordList list) throws ConvertException, IOException{
        if(list == null){
            return (RecordList)recordListConverter.convertToObject(new FileInputStream(file));
        }else{
            return (RecordList)((BindingStreamConverter)recordListConverter).convertToObject(new FileInputStream(file), list);
        }
    }
    
    public CompiledInterpreter compileInterpreter(String src) throws EvaluateException{
        return interpreter.isCompilable() ? interpreter.compile(src) : null;
    }
    
    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return id;
    }
    
    public void init() throws Exception{
        if(client == null){
            client = httpClientFactory.createHttpClient();
        }
        session.clear();
    }
    
    public String isRequest(Request request) throws Exception{
        return ((HttpRequest)request).checkCondition(interpreter, session);
    }
    
    public void connect(Request request) throws Exception{
        if(request != null){
            request(-1, -1, request);
        }
    }
    
    public void request(int roopCount, int count, Request request) throws Exception{
        HttpRequest httpRequest = (HttpRequest)request;
        jp.ossc.nimbus.service.http.HttpRequest req = httpClientFactory.createRequest(httpRequest.getAction());
        httpRequest.setupRequest(client, interpreter, req, session, id, roopCount, count);
        HttpResponse response = null;
        try{
            response = client.executeRequest(req);
        }catch(Exception e){
            httpRequest.handleException(client, e, session, id, roopCount, count);
            throw e;
        }
        if(response != null){
            httpRequest.handleResponse(client, interpreter, response, session, id, roopCount, count);
        }
    }
    
    public void close(Request request) throws Exception{
        if(request != null){
            request(-1, -1, request);
        }
    }
    
    public void close() throws Exception{
        session.clear();
        if(client != null){
            client.close();
            client = null;
        }
    }
}