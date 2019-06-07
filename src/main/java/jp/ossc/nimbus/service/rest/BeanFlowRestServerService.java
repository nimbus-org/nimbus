/*
 * This software is distributed under following license based on modified BSD
 * style license.
 * ----------------------------------------------------------------------
 * 
 * Copyright 2009 The Nimbus2 Project. All rights reserved.
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
 * policies, either expressed or implied, of the Nimbus2 Project.
 */
package jp.ossc.nimbus.service.rest;

import java.beans.PropertyEditor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import jp.ossc.nimbus.beans.NimbusPropertyEditorManager;
import jp.ossc.nimbus.beans.NoSuchPropertyException;
import jp.ossc.nimbus.beans.Property;
import jp.ossc.nimbus.beans.PropertyAccess;
import jp.ossc.nimbus.core.DeploymentException;
import jp.ossc.nimbus.core.MetaData;
import jp.ossc.nimbus.core.NimbusClassLoader;
import jp.ossc.nimbus.core.NimbusEntityResolver;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceMetaData;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.Utility;
import jp.ossc.nimbus.service.aop.interceptor.ThreadContextKey;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvoker;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.service.journal.Journal;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;
import jp.ossc.nimbus.service.sequence.Sequence;
import jp.ossc.nimbus.util.converter.ConvertException;
import jp.ossc.nimbus.util.converter.StreamConverter;
import jp.ossc.nimbus.util.converter.BindingStreamConverter;
import jp.ossc.nimbus.util.converter.StreamStringConverter;

/**
 * アプリケーション処理をBeanFlowに委譲する{@link RestServer}インタフェース実装サービス。<p>
 *
 * @author M.Takata
 * @see <a href="restserver_1_0.dtd">RESTサーバ定義ファイルDTD</a>
 */
public class BeanFlowRestServerService extends ServiceBase implements RestServer, BeanFlowRestServerServiceMBean{
    
    static{
        NimbusEntityResolver.registerDTD(
            "-//Nimbus//DTD Nimbus RestServer definition 1.0//JA",
            "jp/ossc/nimbus/service/rest/restserver_1_0.dtd"
        );
    }
    
    private static final long serialVersionUID = 7754807146567199126L;
    
    protected static final String HTTP_HEADER_NAME_ACCEPT = "Accept";
    protected static final String HTTP_HEADER_NAME_ACCEPT_CHARSET = "Accept-Charset";
    protected static final String HTTP_HEADER_NAME_CONTENT_TYPE = "Content-Type";
    protected static final String MEDIA_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";
    protected static final String HTTP_HEADER_NAME_CONTENT_ENCODING = "Content-Encoding";
    protected static final String CONTENT_ENCODING_DEFLATE = "deflate";
    protected static final String CONTENT_ENCODING_GZIP = "gzip";
    protected static final String CONTENT_ENCODING_X_GZIP = "x-gzip";
    
    protected String serverDefinitionPath;
    protected String documentBuilderFactoryClassName;
    protected boolean isValidate;
    protected String validateFlowPrefix = DEFAULT_VALIDATE_FLOW_PREFIX;
    protected String postMethodFlowPostfix = DEFAULT_POST_METHOD_FLOW_POSTFIX;
    protected String getMethodFlowPostfix = DEFAULT_GET_METHOD_FLOW_POSTFIX;
    protected String headMethodFlowPostfix = DEFAULT_HEAD_METHOD_FLOW_POSTFIX;
    protected String putMethodFlowPostfix = DEFAULT_PUT_METHOD_FLOW_POSTFIX;
    protected String deleteMethodFlowPostfix = DEFAULT_DELETE_METHOD_FLOW_POSTFIX;
    protected ServiceName beanFlowInvokerFactoryServiceName;
    protected ServiceName journalServiceName;
    protected ServiceName editorFinderServiceName;
    protected ServiceName sequenceServiceName;
    protected ServiceName contextServiceName;
    protected String requestIdKey = ThreadContextKey.REQUEST_ID;
    
    protected BeanFlowInvokerFactory beanFlowInvokerFactory;
    protected Journal journal;
    protected EditorFinder editorFinder;
    protected Sequence sequence;
    protected Context context;
    protected RestServerMetaData restServerMetaData;
    protected PropertyAccess propertyAccess;
    protected Map requestConverterServiceNameMapping;
    protected Map requestConverterMapping;
    protected Map responseConverterServiceNameMapping;
    protected Map responseConverterMapping;
    
    public void setServerDefinitionPath(String path){
        serverDefinitionPath = path;
    }
    public String getServerDefinitionPath(){
        return serverDefinitionPath;
    }
    
    public void setDocumentBuilderFactoryClassName(String name){
        documentBuilderFactoryClassName = name;
    }
    public String getDocumentBuilderFactoryClassName(){
        return documentBuilderFactoryClassName;
    }
    
    public void setValidate(boolean validate){
        isValidate = validate;
    }
    public boolean isValidate(){
        return isValidate;
    }
    
    public void setValidateFlowPrefix(String prefix){
        validateFlowPrefix = prefix;
    }
    public String getValidateFlowPrefix(){
        return validateFlowPrefix;
    }
    
    public void setPostMethodFlowPostfix(String postfix){
        postMethodFlowPostfix = postfix;
    }
    public String getPostMethodFlowPostfix(){
        return postMethodFlowPostfix;
    }
    
    public void setGetMethodFlowPostfix(String postfix){
        getMethodFlowPostfix = postfix;
    }
    public String getGetMethodFlowPostfix(){
        return getMethodFlowPostfix;
    }
    
    public void setHeadMethodFlowPostfix(String postfix){
        headMethodFlowPostfix = postfix;
    }
    public String getHeadMethodFlowPostfix(){
        return headMethodFlowPostfix;
    }
    
    public void setPutMethodFlowPostfix(String postfix){
        putMethodFlowPostfix = postfix;
    }
    public String getPutMethodFlowPostfix(){
        return putMethodFlowPostfix;
    }
    
    public void setDeleteMethodFlowPostfix(String postfix){
        deleteMethodFlowPostfix = postfix;
    }
    public String getDeleteMethodFlowPostfix(){
        return deleteMethodFlowPostfix;
    }
    
    public void setBeanFlowInvokerFactoryServiceName(ServiceName name){
        beanFlowInvokerFactoryServiceName = name;
    }
    public ServiceName getBeanFlowInvokerFactoryServiceName(){
        return beanFlowInvokerFactoryServiceName;
    }
    
    public void setJournalServiceName(ServiceName name){
        journalServiceName = name;
    }
    public ServiceName getJournalServiceName(){
        return journalServiceName;
    }
    
    public void setEditorFinderServiceName(ServiceName name){
        editorFinderServiceName = name;
    }
    public ServiceName getEditorFinderServiceName(){
        return editorFinderServiceName;
    }
    
    public void setSequenceServiceName(ServiceName name){
        sequenceServiceName = name;
    }
    public ServiceName getSequenceServiceName(){
        return sequenceServiceName;
    }
    
    public void setRequestIdKey(String key){
        requestIdKey = key;
    }
    public String getRequestIdKey(){
        return requestIdKey;
    }
    
    public void setContextServiceName(ServiceName name){
        contextServiceName = name;
    }
    public ServiceName getContextServiceName(){
        return contextServiceName;
    }
    
    public void setRequestConverterServiceNames(Map mapping){
        requestConverterServiceNameMapping = mapping;
    }
    public Map getRequestConverterServiceNames(){
        return requestConverterServiceNameMapping;
    }
    
    public void setRequestConverterServiceName(String mediaType, ServiceName name){
        if(requestConverterServiceNameMapping == null){
            requestConverterServiceNameMapping = new LinkedHashMap();
        }
        requestConverterServiceNameMapping.put(mediaType, name);
    }
    public ServiceName getRequestConverterServiceName(String mediaType){
        if(requestConverterServiceNameMapping == null){
            return null;
        }
        return (ServiceName)requestConverterServiceNameMapping.get(mediaType);
    }
    
    public void setResponseConverterServiceNames(Map mapping){
        responseConverterServiceNameMapping = mapping;
    }
    public Map getResponseConverterServiceNames(){
        return responseConverterServiceNameMapping;
    }
    
    public void setResponseConverterServiceName(String mediaType, ServiceName name){
        if(responseConverterServiceNameMapping == null){
            responseConverterServiceNameMapping = new HashMap();
        }
        responseConverterServiceNameMapping.put(mediaType, name);
    }
    public ServiceName getResponseConverterServiceName(String mediaType){
        if(responseConverterServiceNameMapping == null){
            return null;
        }
        return (ServiceName)responseConverterServiceNameMapping.get(mediaType);
    }
    
    public void setRequestConverter(String mediaType, BindingStreamConverter converter){
        if(requestConverterMapping == null){
            requestConverterMapping = new LinkedHashMap();
        }
        requestConverterMapping.put(mediaType, converter);
    }
    
    public BindingStreamConverter getRequestConverter(String mediaType){
        if(requestConverterMapping == null){
            return null;
        }
        return (BindingStreamConverter)requestConverterMapping.get(mediaType);
    }
    
    public void setResponseConverter(String mediaType, StreamConverter converter){
        if(responseConverterMapping == null){
            responseConverterMapping= new HashMap();
        }
        responseConverterMapping.put(mediaType, converter);
    }
    
    public StreamConverter getResponseConverter(String mediaType){
        if(responseConverterMapping == null){
            return null;
        }
        return (StreamConverter)responseConverterMapping.get(mediaType);
    }
    
    public void setBeanFlowInvokerFactory(BeanFlowInvokerFactory factory){
        beanFlowInvokerFactory = factory;
    }
    public BeanFlowInvokerFactory getBeanFlowInvokerFactory(){
        return beanFlowInvokerFactory;
    }
    
    public void setJournal(Journal journal){
        this.journal = journal;
    }
    public Journal getJournal(){
        return journal;
    }
    
    public void setEditorFinder(EditorFinder editorFinder){
        this.editorFinder = editorFinder;
    }
    public EditorFinder getEditorFinder(){
        return editorFinder;
    }
    
    public void setSequence(Sequence seq){
        sequence = seq;
    }
    public Sequence getSequence(){
        return sequence;
    }
    
    public void setContext(Context ctx){
        context = ctx;
    }
    public Context getContext(){
        return context;
    }
    
    public void createService() throws Exception{
        propertyAccess = new PropertyAccess();
        propertyAccess.setIgnoreNullProperty(true);
    }
    
    public void startService() throws Exception{
        if(requestConverterServiceNameMapping != null){
            Iterator entries = requestConverterServiceNameMapping.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                if(requestConverterMapping == null){
                    requestConverterMapping = new LinkedHashMap();
                }
                requestConverterMapping.put(entry.getKey(), (BindingStreamConverter)ServiceManagerFactory.getServiceObject((ServiceName)entry.getValue()));
            }
        }
        if(responseConverterServiceNameMapping != null){
            Iterator entries = responseConverterServiceNameMapping.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                if(responseConverterMapping == null){
                    responseConverterMapping = new HashMap();
                }
                responseConverterMapping.put(entry.getKey(), (StreamConverter)ServiceManagerFactory.getServiceObject((ServiceName)entry.getValue()));
            }
        }
        if(contextServiceName != null){
            context = (Context)ServiceManagerFactory.getServiceObject(contextServiceName);
        }
        if(beanFlowInvokerFactoryServiceName != null){
            beanFlowInvokerFactory = (BeanFlowInvokerFactory)ServiceManagerFactory.getServiceObject(beanFlowInvokerFactoryServiceName);
        }
        if(beanFlowInvokerFactory == null){
            throw new IllegalArgumentException("BeanFlowFactory is null");
        }
        if(journalServiceName != null){
            journal = (Journal)ServiceManagerFactory.getServiceObject(journalServiceName);
        }
        if(editorFinderServiceName != null){
            editorFinder = (EditorFinder)ServiceManagerFactory.getServiceObject(editorFinderServiceName);
        }
        
        reload();
    }
    
    public synchronized void reload() throws Exception{
        if(serverDefinitionPath == null || serverDefinitionPath.length() == 0){
            throw new IllegalArgumentException("ServerDefinitionPath is null");
        }
        
        File serviceDefDir = null;
        if(getServiceNameObject() != null){
            ServiceMetaData metaData = ServiceManagerFactory.getServiceMetaData(getServiceNameObject());
            if(metaData != null){
                jp.ossc.nimbus.core.ServiceLoader loader = metaData.getServiceLoader();
                if(loader != null){
                    String filePath = loader.getServiceURL().getFile();
                    if(filePath != null){
                        serviceDefDir = new File(filePath).getParentFile();
                    }
                }
            }
        }
        
        URL url = null;
        File localFile = new File(serverDefinitionPath);
        if(!localFile.exists() && serviceDefDir != null){
            localFile = new File(serviceDefDir, serverDefinitionPath);
        }
        if(localFile.exists()){
            if(!localFile.isFile()){
                throw new IllegalArgumentException(
                    "ServerDefinitionPath must be file : " + localFile
                );
            }
            try{
                url = localFile.toURI().toURL();
            }catch(MalformedURLException e){
                // この例外は発生しないはず
            }
        }else{
            final ClassLoader classLoader
                 = Thread.currentThread().getContextClassLoader();
            final URL resource = classLoader.getResource(serverDefinitionPath);
            if(resource != null){
                url = resource;
            }
        }
        if(url == null){
            throw new IllegalArgumentException(
                "ServerDefinitionPath not found : " + serverDefinitionPath
            );
        }
        final InputSource inputSource = new InputSource(url.openStream());
        DocumentBuilderFactory domFactory = null;
        if(documentBuilderFactoryClassName == null){
            domFactory = DocumentBuilderFactory.newInstance();
        }else{
            domFactory = (DocumentBuilderFactory)Class.forName(
                documentBuilderFactoryClassName,
                true,
                NimbusClassLoader.getInstance()
            ).newInstance();
        }
        domFactory.setValidating(isValidate());
        final DocumentBuilder builder = domFactory.newDocumentBuilder();
        final NimbusEntityResolver resolver = new NimbusEntityResolver();
        builder.setEntityResolver(resolver);
        final MyErrorHandler handler = new MyErrorHandler();
        builder.setErrorHandler(handler);
        final Document doc = builder.parse(inputSource);
        if(handler.isError()){
            throw new DeploymentException("Failed to parse ServerDefinition");
        }
        final RestServerMetaData serverData = new RestServerMetaData();
        serverData.importXML(doc.getDocumentElement());
        restServerMetaData = serverData;
    }
    
    public RestServerMetaData getRestServerMetaData(){
        return restServerMetaData;
    }
    
    protected boolean processCheckAccept(
        RestRequest request,
        RestResponse response
    ) throws Exception{
        String acceptStr = request.request.getHeader(HTTP_HEADER_NAME_ACCEPT);
        if(journal != null){
            journal.addInfo(JOURNAL_KEY_ACCEPT_HEADER, acceptStr);
        }
        if(acceptStr == null){
            return true;
        }
        if(responseConverterMapping != null){
            String acceptCharsetStr = request.request.getHeader(HTTP_HEADER_NAME_ACCEPT_CHARSET);
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_ACCEPT_CHARSET_HEADER, acceptCharsetStr);
            }
            if(acceptCharsetStr != null){
                AcceptCharset acceptCharset = null;
                try{
                    acceptCharset = new AcceptCharset(acceptCharsetStr);
                }catch(IllegalArgumentException e){
                    getLogger().write("BFRS_00029", acceptCharsetStr, e);
                    if(journal != null){
                        journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                    }
                    response.setResult(HttpServletResponse.SC_NOT_ACCEPTABLE);
                    return false;
                }
                boolean isSupported = false;
                for(int i = 0; i < acceptCharset.charsetRanges.size(); i++){
                    CharsetRange cr = (CharsetRange)acceptCharset.charsetRanges.get(i);
                    if(Charset.isSupported(cr.getCharset())){
                        isSupported = true;
                        response.getResponse().setCharacterEncoding(
                            Charset.forName(cr.getCharset()).name()
                        );
                        break;
                    }
                }
                if(!isSupported){
                    getLogger().write("BFRS_00030", acceptCharsetStr);
                    response.setResult(HttpServletResponse.SC_NOT_ACCEPTABLE);
                    return false;
                }
            }
            Accept accept = null;
            try{
                accept = new Accept(acceptStr);
            }catch(IllegalArgumentException e){
                getLogger().write("BFRS_00028", acceptStr, e);
                if(journal != null){
                    journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                }
                response.setResult(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return false;
            }
            for(int i = 0; i < accept.mediaRanges.size(); i++){
                MediaRange mr = (MediaRange)accept.mediaRanges.get(i);
                if(responseConverterMapping.containsKey(mr.getMediaType())){
                    return true;
                }
            }
        }
        response.setResult(HttpServletResponse.SC_NOT_ACCEPTABLE);
        return false;
    }
    
    protected boolean processCheckContentType(
        RestRequest request,
        RestResponse response
    ) throws Exception{
        String contentType = request.request.getHeader(HTTP_HEADER_NAME_CONTENT_TYPE);
        if(journal != null){
            journal.addInfo(JOURNAL_KEY_CONTENT_TYPE_HEADER, contentType);
        }
        if(contentType == null){
            return true;
        }
        final MediaType mediaType = new MediaType(contentType);
        if(MEDIA_TYPE_FORM_URLENCODED.equals(mediaType.getMediaType())){
            return true;
        }
        if(requestConverterMapping == null){
            response.setResult(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return false;
        }else if(!requestConverterMapping.containsKey(mediaType.getMediaType())){
            response.setResult(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return false;
        }
        return true;
    }
    
    protected ResourceMetaData processFindResource(
        RestRequest request,
        RestResponse response,
        List paths
    ) throws Exception{
        final ResourceMetaData resource = restServerMetaData.resourceTree.getResource(paths);
        if(resource == null){
            response.setResult(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        if(journal != null){
            journal.addInfo(JOURNAL_KEY_RESOURCE_PATH, resource.resourcePath.path);
        }
        return resource;
    }
    
    protected boolean processParsePathParameters(
        RestRequest request,
        RestResponse response,
        List paths,
        ResourceMetaData resource
    ) throws Exception{
        try{
            Map pathParameters = resource.resourcePath.parseParameter(paths, null);
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_PATH_PARAMETERS, pathParameters);
            }
            request.setPathParameterMap(pathParameters);
        }catch(IndexOutOfBoundsException e){
            getLogger().write("BFRS_00025", new Object[]{resource.resourcePath.path, paths}, e);
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
            }
            response.setResult(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }
        return true;
    }
    
    protected boolean processCreateRequestObject(
        RestRequest request,
        RestResponse response,
        List paths,
        ResourceMetaData resource,
        RequestMetaData requestData
    ) throws Exception{
        if(requestData == null){
            return true;
        }
        Map pathParameters = request.getPathParameterMap();
        Object requestObj = null;
        if(beanFlowInvokerFactory.containsFlow(requestData.code)){
            final String flowName = requestData.code;
            BeanFlowInvoker flow = null;
            try{
                flow = beanFlowInvokerFactory.createFlow(flowName);
                requestObj = flow.invokeFlow(new RestContext(request, response));
                request.setRequestObject(requestObj);
            }catch(Throwable th){
                getLogger().write("BFRS_00004", new Object[]{resource.resourcePath.path, requestData.code}, th);
                if(journal != null){
                    journal.addInfo(JOURNAL_KEY_EXCEPTION, th);
                }
                response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return false;
            }
        }else{
            Class requestObjClass = null;
            try{
                requestObjClass = Utility.convertStringToClass(requestData.code, true);
            }catch(ClassNotFoundException e){
                getLogger().write("BFRS_00004", new Object[]{resource.resourcePath.path, requestData.code}, e);
                if(journal != null){
                    journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                }
                response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return false;
            }
            if(String.class.equals(requestObjClass)){
                if(pathParameters != null && pathParameters.size() != 0){
                    requestObj = pathParameters.values().iterator().next();
                }
                request.setRequestObject(requestObj);
                return true;
            }else if(String[].class.equals(requestObjClass)){
                if(pathParameters != null && pathParameters.size() != 0){
                    requestObj = pathParameters.values().toArray();
                }
                request.setRequestObject(requestObj);
                return true;
            }else if(Number.class.isAssignableFrom(requestObjClass)){
                if(pathParameters != null && pathParameters.size() != 0){
                    String valStr = (String)pathParameters.values().iterator().next();
                    try{
                        if(Byte.class.isAssignableFrom(requestObjClass)){
                            requestObj = Byte.valueOf(valStr);
                        }else if(Short.class.isAssignableFrom(requestObjClass)){
                            requestObj = Short.valueOf(valStr);
                        }else if(Integer.class.isAssignableFrom(requestObjClass)){
                            requestObj = Integer.valueOf(valStr);
                        }else if(Long.class.isAssignableFrom(requestObjClass)){
                            requestObj = Long.valueOf(valStr);
                        }else if(Float.class.isAssignableFrom(requestObjClass)){
                            requestObj = Float.valueOf(valStr);
                        }else if(Double.class.isAssignableFrom(requestObjClass)){
                            requestObj = Double.valueOf(valStr);
                        }else if(BigInteger.class.isAssignableFrom(requestObjClass)){
                            requestObj = new BigInteger(valStr);
                        }else if(BigDecimal.class.isAssignableFrom(requestObjClass)){
                            requestObj = new BigDecimal(valStr);
                        }else{
                            getLogger().write("BFRS_00005", new Object[]{resource.resourcePath.path, requestObjClass.getName()});
                            response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            return false;
                        }
                    }catch(NumberFormatException e){
                        getLogger().write("BFRS_00026", new Object[]{resource.resourcePath.path, pathParameters.keySet().iterator().next(), valStr}, e);
                        if(journal != null){
                            journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                        }
                        response.setResult(
                            HttpServletResponse.SC_BAD_REQUEST,
                            "Illegal number format. val=" + valStr
                        );
                        return false;
                    }
                }
                request.setRequestObject(requestObj);
                return true;
            }else{
                try{
                    requestObj = requestObjClass.newInstance();
                    request.setRequestObject(requestObj);
                }catch(InstantiationException e){
                    getLogger().write("BFRS_00006", new Object[]{resource.resourcePath.path, requestObjClass.getName()}, e);
                    if(journal != null){
                        journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                    }
                    response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return false;
                }catch(IllegalAccessException e){
                    getLogger().write("BFRS_00006", new Object[]{resource.resourcePath.path, requestObjClass.getName()}, e);
                    if(journal != null){
                        journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                    }
                    response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return false;
                }
            }
        }
        if(pathParameters != null && pathParameters.size() != 0){
            Iterator entries = pathParameters.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                Property prop = null;
                try{
                    prop = propertyAccess.getProperty((String)entry.getKey());
                }catch(IllegalArgumentException e){
                    getLogger().write("BFRS_00007", new Object[]{resource.resourcePath.path, entry.getKey()}, e);
                    if(journal != null){
                        journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                    }
                    response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return false;
                }
                if(!prop.isReadable(requestObj)){
                    continue;
                }
                Class propType = null;
                try{
                    propType = prop.getPropertyType(requestObj);
                }catch(NoSuchPropertyException e){
                    getLogger().write("BFRS_00008", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName()}, e);
                    if(journal != null){
                        journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                    }
                    response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return false;
                }catch(InvocationTargetException e){
                    getLogger().write("BFRS_00008", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName()}, e);
                    if(journal != null){
                        journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                    }
                    response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return false;
                }
                if(!prop.isWritable(requestObj, propType)){
                    continue;
                }
                if(propType.isAssignableFrom(String.class)){
                    try{
                        prop.setProperty(requestObj, propType, entry.getValue());
                    }catch(NoSuchPropertyException e){
                        getLogger().write("BFRS_00009", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName()}, e);
                        if(journal != null){
                            journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                        }
                        response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return false;
                    }catch(InvocationTargetException e){
                        getLogger().write("BFRS_00009", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName()}, e);
                        if(journal != null){
                            journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                        }
                        response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return false;
                    }
                }else{
                    PropertyEditor editor = NimbusPropertyEditorManager.findEditor(propType);
                    if(editor == null){
                        getLogger().write("BFRS_00010", new Object[]{resource.resourcePath.path, entry.getKey(), propType.getName()});
                        response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return false;
                    }
                    editor.setAsText((String)entry.getValue());
                    try{
                        prop.setProperty(requestObj, propType, editor.getValue());
                    }catch(NoSuchPropertyException e){
                        getLogger().write("BFRS_00009", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName()}, e);
                        if(journal != null){
                            journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                        }
                        response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return false;
                    }catch(InvocationTargetException e){
                        getLogger().write("BFRS_00009", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName()}, e);
                        if(journal != null){
                            journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                        }
                        response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    protected boolean processSetupResponseObject(
        RestRequest request,
        RestResponse response,
        List paths,
        ResourceMetaData resource,
        ResponseMetaData responseData
    ) throws Exception{
        if(responseData == null){
            return true;
        }
        if(beanFlowInvokerFactory.containsFlow(responseData.code)){
            final String flowName = responseData.code;
            BeanFlowInvoker flow = null;
            try{
                flow = beanFlowInvokerFactory.createFlow(flowName);
                Object responseObj = flow.invokeFlow(new RestContext(request, response));
                response.setResponseObject(responseObj);
            }catch(Throwable th){
                getLogger().write("BFRS_00011", new Object[]{resource.resourcePath.path, responseData.code}, th);
                if(journal != null){
                    journal.addInfo(JOURNAL_KEY_EXCEPTION, th);
                }
                response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return false;
            }
        }else{
            Class responseObjClass = null;
            try{
                responseObjClass = Utility.convertStringToClass(responseData.code, true);
            }catch(ClassNotFoundException e){
                getLogger().write("BFRS_00011", new Object[]{resource.resourcePath.path, responseData.code}, e);
                if(journal != null){
                    journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                }
                response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return false;
            }
            response.setResponseObjectClass(responseObjClass);
        }
        return true;
    }
    
    protected boolean processReadQuery(
        RestRequest request,
        RestResponse response,
        List paths,
        ResourceMetaData resource
    ) throws Exception{
        if(request.getRequest().getQueryString() == null){
            return true;
        }
        return processReadParameter(request, response, paths, resource);
    }
    
    protected boolean processReadParameter(
        RestRequest request,
        RestResponse response,
        List paths,
        ResourceMetaData resource
    ) throws Exception{
        if(journal != null){
            journal.addInfo(JOURNAL_KEY_REQUEST_PARAMETERS, request.getRequest().getParameterMap());
        }
        if(request.getRequestObject() == null){
            request.setRequestObject(request.getRequest().getParameterMap());
        }else{
            final Object requestObj = request.getRequestObject();
            Iterator entries = request.getRequest().getParameterMap().entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                Property prop = null;
                try{
                    prop = propertyAccess.getProperty((String)entry.getKey());
                }catch(IllegalArgumentException e){
                    getLogger().write("BFRS_00027", new Object[]{resource.resourcePath.path, entry.getKey()}, e);
                    if(journal != null){
                        journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                    }
                    response.setResult(HttpServletResponse.SC_BAD_REQUEST);
                    return false;
                }
                if(!prop.isReadable(requestObj)){
                    continue;
                }
                Class propType = null;
                try{
                    propType = prop.getPropertyType(requestObj);
                }catch(NoSuchPropertyException e){
                    getLogger().write("BFRS_00012", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName()}, e);
                    if(journal != null){
                        journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                    }
                    response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return false;
                }catch(InvocationTargetException e){
                    getLogger().write("BFRS_00012", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName()}, e);
                    if(journal != null){
                        journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                    }
                    response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return false;
                }
                if(!prop.isWritable(requestObj, propType)){
                    continue;
                }
                if(propType.isAssignableFrom(String.class)){
                    try{
                        prop.setProperty(requestObj, propType, ((String[])entry.getValue())[0]);
                    }catch(NoSuchPropertyException e){
                        getLogger().write("BFRS_00013", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName()}, e);
                        if(journal != null){
                            journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                        }
                        response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return false;
                    }catch(InvocationTargetException e){
                        getLogger().write("BFRS_00013", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName()}, e);
                        if(journal != null){
                            journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                        }
                        response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return false;
                    }
                }else if(propType.isAssignableFrom(String[].class)){
                    try{
                        prop.setProperty(requestObj, propType, entry.getValue());
                    }catch(NoSuchPropertyException e){
                        getLogger().write("BFRS_00013", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName()}, e);
                        if(journal != null){
                            journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                        }
                        response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return false;
                    }catch(InvocationTargetException e){
                        getLogger().write("BFRS_00013", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName()}, e);
                        if(journal != null){
                            journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                        }
                        response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return false;
                    }
                }else if(propType.isAssignableFrom(List.class) || propType.isAssignableFrom(Set.class)){
                    Collection collection = null;
                    try{
                        collection = (Collection)prop.getProperty(requestObj);
                    }catch(NoSuchPropertyException e){
                        getLogger().write("BFRS_00014", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName()}, e);
                        if(journal != null){
                            journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                        }
                        response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return false;
                    }catch(InvocationTargetException e){
                        getLogger().write("BFRS_00014", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName()}, e);
                        if(journal != null){
                            journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                        }
                        response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return false;
                    }
                    if(collection == null){
                        if(propType.isInterface()){
                            collection = propType.isAssignableFrom(List.class) ? (Collection)new ArrayList() : new HashSet();
                        }else{
                            try{
                                collection = (Collection)propType.newInstance();
                            }catch(InstantiationException e){
                                getLogger().write("BFRS_00015", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName(), propType}, e);
                                if(journal != null){
                                    journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                                }
                                response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                return false;
                            }catch(IllegalAccessException e){
                                getLogger().write("BFRS_00015", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName(), propType}, e);
                                if(journal != null){
                                    journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                                }
                                response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                return false;
                            }
                        }
                    }
                    Type propGenericType = null;
                    try{
                        propGenericType = prop.getPropertyGenericType(requestObj);
                    }catch(NoSuchPropertyException e){
                        getLogger().write("BFRS_00012", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName()}, e);
                        if(journal != null){
                            journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                        }
                        response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return false;
                    }catch(InvocationTargetException e){
                        getLogger().write("BFRS_00012", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName()}, e);
                        if(journal != null){
                            journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                        }
                        response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return false;
                    }
                    if(propGenericType instanceof ParameterizedType){
                        Type[] elementTypes = ((ParameterizedType)propGenericType).getActualTypeArguments();
                        if(elementTypes.length == 0 || !(elementTypes[0] instanceof Class)){
                            getLogger().write("BFRS_00016", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName(), propGenericType});
                            response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            return false;
                        }
                        Class elementType = (Class)elementTypes[0];
                        try{
                            PropertyEditor editor = NimbusPropertyEditorManager.findEditor(elementType);
                            if(editor == null){
                                getLogger().write("BFRS_00017", new Object[]{resource.resourcePath.path, entry.getKey(), elementType.getName()});
                                response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                return false;
                            }
                            String[] params = (String[])entry.getValue();
                            for(int i = 0; i < params.length; i++){
                                editor.setAsText(params[i]);
                                collection.add(editor.getValue());
                            }
                            prop.setProperty(requestObj, propType, collection);
                        }catch(NoSuchPropertyException e){
                            getLogger().write("BFRS_00013", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName()}, e);
                            if(journal != null){
                                journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                            }
                            response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            return false;
                        }catch(InvocationTargetException e){
                            getLogger().write("BFRS_00013", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName()}, e);
                            if(journal != null){
                                journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                            }
                            response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            return false;
                        }
                    }else{
                        try{
                            String[] params = (String[])entry.getValue();
                            for(int i = 0; i < params.length; i++){
                                collection.add(params[i]);
                            }
                            prop.setProperty(requestObj, propType, collection);
                        }catch(NoSuchPropertyException e){
                            getLogger().write("BFRS_00013", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName()}, e);
                            if(journal != null){
                                journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                            }
                            response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            return false;
                        }catch(InvocationTargetException e){
                            getLogger().write("BFRS_00013", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName()}, e);
                            if(journal != null){
                                journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                            }
                            response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            return false;
                        }
                    }
                }else if(propType.isArray()){
                    Class componentType = propType.getComponentType();
                    PropertyEditor editor = NimbusPropertyEditorManager.findEditor(componentType);
                    if(editor == null){
                        getLogger().write("BFRS_00017", new Object[]{resource.resourcePath.path, entry.getKey(), componentType.getName()});
                        response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return false;
                    }
                    String[] params = (String[])entry.getValue();
                    Object array = Array.newInstance(componentType, params.length);
                    try{
                        for(int i = 0; i < params.length; i++){
                            editor.setAsText(((String[])entry.getValue())[0]);
                            Array.set(array, i, editor.getValue());
                        }
                        prop.setProperty(requestObj, propType, array);
                    }catch(IllegalArgumentException e){
                        getLogger().write("BFRS_00013", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName()}, e);
                        if(journal != null){
                            journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                        }
                        response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return false;
                    }catch(NoSuchPropertyException e){
                        getLogger().write("BFRS_00013", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName()}, e);
                        if(journal != null){
                            journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                        }
                        response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return false;
                    }catch(InvocationTargetException e){
                        getLogger().write("BFRS_00013", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName()}, e);
                        if(journal != null){
                            journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                        }
                        response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return false;
                    }
                }else{
                    PropertyEditor editor = NimbusPropertyEditorManager.findEditor(propType);
                    if(editor == null){
                        getLogger().write("BFRS_00017", new Object[]{resource.resourcePath.path, entry.getKey(), propType.getName()});
                        response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return false;
                    }
                    editor.setAsText(((String[])entry.getValue())[0]);
                    try{
                        prop.setProperty(requestObj, propType, editor.getValue());
                    }catch(NoSuchPropertyException e){
                        getLogger().write("BFRS_00013", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName()}, e);
                        if(journal != null){
                            journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                        }
                        response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return false;
                    }catch(InvocationTargetException e){
                        getLogger().write("BFRS_00013", new Object[]{resource.resourcePath.path, entry.getKey(), requestObj.getClass().getName()}, e);
                        if(journal != null){
                            journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                        }
                        response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    protected boolean processReadRequestBody(
        RestRequest request,
        RestResponse response,
        List paths,
        ResourceMetaData resource
    ) throws Exception{
        String contentType = request.request.getHeader(HTTP_HEADER_NAME_CONTENT_TYPE);
        BindingStreamConverter requestConverter = null;
        if(contentType == null){
            if(requestConverterMapping != null){
                requestConverter = (BindingStreamConverter)requestConverterMapping.values().iterator().next();
            }
        }else{
            final MediaType mediaType = new MediaType(contentType);
            if(requestConverterMapping != null){
                requestConverter = (BindingStreamConverter)requestConverterMapping.get(mediaType.getMediaType());
            }
        }
        if(requestConverter == null){
            return processReadParameter(request, response, paths, resource);
        }else if(request.getRequestObject() != null){
            String encode = null;
            if(requestConverter instanceof StreamStringConverter){
                if(request.getRequest().getCharacterEncoding() != null
                    && !request.getRequest().getCharacterEncoding().equals(((StreamStringConverter)requestConverter).getCharacterEncodingToObject())){
                    requestConverter = (BindingStreamConverter)((StreamStringConverter)requestConverter)
                        .cloneCharacterEncodingToObject(request.getRequest().getCharacterEncoding());
                }
                encode = ((StreamStringConverter)requestConverter).getCharacterEncodingToObject();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ByteArrayInputStream bais = null;
            try{
                final ServletInputStream sis = request.getRequest().getInputStream();
                byte[] bytes = new byte[2048];
                try{
                    int size = 0;
                    while((size = sis.read(bytes)) != -1){
                        baos.write(bytes, 0, size);
                    }
                }finally{
                    if(sis != null){
                        sis.close();
                    }
                    bytes = null;
                }
                decompress(request.getRequest(), baos);
                if(journal != null){
                    Object requestBody = null;
                    if(encode == null){
                        requestBody = baos.toByteArray();
                    }else{
                        try{
                            requestBody = new String(baos.toByteArray(), encode);
                        }catch(UnsupportedEncodingException e){
                            requestBody = baos.toByteArray();
                        }
                    }
                    journal.addInfo(JOURNAL_KEY_REQUEST_BODY, requestBody);
                }
                bais = new ByteArrayInputStream(baos.toByteArray());
                baos = null;
                request.setRequestObject(requestConverter.convertToObject(bais, request.getRequestObject()));
            }catch(ConvertException e){
                getLogger().write("BFRS_00018", new Object[]{resource.resourcePath.path, request.getRequestObject().getClass().getName()}, e);
                if(journal != null){
                    journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
                }
                response.setResult(HttpServletResponse.SC_BAD_REQUEST);
                return false;
            }finally{
                if(bais != null){
                    try{
                        bais.close();
                    }catch(IOException e){}
                }
            }
        }
        return true;
    }
    
    protected ByteArrayOutputStream decompress(HttpServletRequest request, ByteArrayOutputStream baos) throws IOException {
        // ヘッダー[Content-Encoding]の値を取得
        Enumeration encodeEnum = (Enumeration)request.getHeaders(HTTP_HEADER_NAME_CONTENT_ENCODING);
        if(encodeEnum == null || !encodeEnum.hasMoreElements()){
            return baos;
        }
        InputStream in = new ByteArrayInputStream(baos.toByteArray());
        try{
            // 圧縮された逆順で解凍
            List encodes = new ArrayList();
            while(encodeEnum.hasMoreElements()){
                encodes.add(encodeEnum.nextElement());
            }
            for(int i = (encodes.size() - 1); i >= 0; i--){
                final String encode = (String)encodes.get(i);
                if(encode != null){
                    if(encode.indexOf(CONTENT_ENCODING_DEFLATE) != -1){
                        // deflate圧縮解除
                        in = new InflaterInputStream(in);
                    }else if(encode.indexOf(CONTENT_ENCODING_GZIP) != -1
                                || encode.indexOf(CONTENT_ENCODING_X_GZIP) != -1){
                        // gzip圧縮解除
                        in = new GZIPInputStream(in);
                    }else{
                        throw new IOException("Can not decompress. [" + encode + "]");
                    }
                }
            }
            baos.reset();
            final byte[] bytes = new byte[1024];
            int length = 0;
            while((length = in.read(bytes)) != -1){
                baos.write(bytes, 0, length);
            }
        }finally{
            try{
                in.close();
            }catch(IOException e){}
        }
        return baos;
    }
    
    protected boolean processValidateRequestObject(
        RestRequest request,
        RestResponse response,
        List paths,
        ResourceMetaData resource
    ) throws Exception{
        if(journal != null){
            journal.addInfo(JOURNAL_KEY_REQUEST_OBJECT, request.getRequestObject());
        }
        final String flowName = validateFlowPrefix + resource.resourcePath.path;
        if(!beanFlowInvokerFactory.containsFlow(flowName)){
            return true;
        }
        if(journal != null){
            journal.addInfo(JOURNAL_KEY_VALIDATE_FLOW, flowName);
        }
        BeanFlowInvoker flow = null;
        try{
            flow = beanFlowInvokerFactory.createFlow(flowName);
        }catch(Exception e){
            getLogger().write("BFRS_00019", new Object[]{resource.resourcePath.path, flowName}, e);
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
            }
            response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }
        try{
            final Object ret = flow.invokeFlow(new RestContext(request, response));
            boolean result = false;
            if(ret != null && ret instanceof Boolean){
                result = ((Boolean)ret).booleanValue();
            }
            if(!result){
                response.setResult(HttpServletResponse.SC_BAD_REQUEST);
            }
            return result;
        }catch(Throwable th){
            getLogger().write("BFRS_00020", new Object[]{resource.resourcePath.path, flowName}, th);
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_EXCEPTION, th);
            }
            response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }
    }
    
    protected boolean processExecute(
        RestRequest request,
        RestResponse response,
        List paths,
        ResourceMetaData resource,
        String methodPostfix
    ) throws Exception{
        final String flowName = resource.resourcePath.path + methodPostfix;
        if(!beanFlowInvokerFactory.containsFlow(flowName)){
            getLogger().write("BFRS_00021", new Object[]{resource.resourcePath.path, flowName});
            response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }
        if(journal != null){
            journal.addInfo(JOURNAL_KEY_FLOW, flowName);
        }
        BeanFlowInvoker flow = null;
        try{
            flow = beanFlowInvokerFactory.createFlow(flowName);
        }catch(Exception e){
            getLogger().write("BFRS_00022", new Object[]{resource.resourcePath.path, flowName}, e);
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
            }
            response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }
        try{
            flow.invokeFlow(new RestContext(request, response));
            return true;
        }catch(Throwable th){
            getLogger().write("BFRS_00023", new Object[]{resource.resourcePath.path, flowName}, th);
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_EXCEPTION, th);
            }
            response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }
    }
    
    protected boolean processWriteResponseBody(
        RestRequest request,
        RestResponse response,
        List paths,
        ResourceMetaData resource
    ) throws Exception{
        Object responseObject = response.getResponseObject();
        if(responseObject == null){
            return true;
        }
        if(journal != null){
            journal.addInfo(JOURNAL_KEY_RESPONSE_OBJECT, responseObject);
        }
        String acceptStr = request.request.getHeader(HTTP_HEADER_NAME_ACCEPT);
        StreamConverter responseConverter = null;
        String mediaType = null;
        if(acceptStr == null){
            mediaType = (String)responseConverterMapping.keySet().iterator().next();
            responseConverter = (StreamConverter)responseConverterMapping.get(mediaType);
        }else{
            Accept accept = null;
            try{
                accept = new Accept(acceptStr);
            }catch(IllegalArgumentException e){
                // 事前にチェックするため起こらないはず
            }
            for(int i = 0; i < accept.mediaRanges.size(); i++){
                MediaRange mr = (MediaRange)accept.mediaRanges.get(i);
                responseConverter = (StreamConverter)responseConverterMapping.get(mr.getMediaType());
                if(responseConverter != null){
                    mediaType = mr.getMediaType();
                    break;
                }
            }
        }
        String encode = null;
        if(responseConverter instanceof StreamStringConverter){
            if(response.getResponse().getCharacterEncoding() != null
                && !response.getResponse().getCharacterEncoding().equals(((StreamStringConverter)responseConverter).getCharacterEncodingToStream())){
                responseConverter = ((StreamStringConverter)responseConverter)
                    .cloneCharacterEncodingToStream(response.getResponse().getCharacterEncoding());
            }
            encode = ((StreamStringConverter)responseConverter).getCharacterEncodingToStream();
        }
        response.getResponse().setContentType(
            new ContentType(
                mediaType,
                response.getResponse().getCharacterEncoding()
            ).toString()
        );
        InputStream is = null;
        try{
            is = responseConverter.convertToStream(responseObject);
        }catch(ConvertException e){
            getLogger().write("BFRS_00024", new Object[]{resource.resourcePath.path, responseObject.getClass().getName()}, e);
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
            }
            response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }
        final ServletOutputStream sos = response.getResponse().getOutputStream();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes = new byte[2048];
        int readLen = 0;
        while((readLen = is.read(bytes)) != -1){
            baos.write(bytes, 0, readLen);
            sos.write(bytes, 0, readLen);
        }
        if(journal != null){
            Object responseBody = null;
            if(encode == null){
                responseBody = baos.toByteArray();
            }else{
                try{
                    responseBody = new String(baos.toByteArray(), encode);
                }catch(UnsupportedEncodingException e){
                    responseBody = baos.toByteArray();
                }
            }
            journal.addInfo(JOURNAL_KEY_RESPONSE_BODY, responseBody);
        }
        return true;
    }
    
    public void processPost(PostRestRequest request, PostRestResponse response) throws Throwable{
        ResourceMetaData resource = null;
        try{
            if(journal != null){
                journal.startJournal(JOURNAL_KEY_REST_PROCESS, editorFinder);
                if(sequence != null){
                    String sequenceId = sequence.increment();
                    if(context != null){
                        context.put(requestIdKey, sequenceId);
                    }
                    journal.setRequestId(sequenceId);
                }else if(context != null){
                    journal.setRequestId(
                        (String)context.get(requestIdKey)
                    );
                }
                journal.addInfo(JOURNAL_KEY_REQUEST_URI, request.getURI());
                journal.addInfo(JOURNAL_KEY_METHOD, request.getRequest().getMethod());
            }
            if(!processCheckAccept(request, response)){
                return;
            }
            if(!processCheckContentType(request, response)){
                return;
            }
            final List paths = ResourcePath.splitPath(request.getURI());
            resource = processFindResource(request, response, paths);
            if(resource == null){
                return;
            }
            if(resource.postData == null){
                response.setResult(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            if(!processParsePathParameters(request, response, paths, resource)){
                return;
            }
            if(!processCreateRequestObject(request, response, paths, resource, resource.postData.requestData)){
                return;
            }
            if(!processSetupResponseObject(request, response, paths, resource, resource.postData.responseData)){
                return;
            }
            if(!processReadRequestBody(request, response, paths, resource)){
                return;
            }
            if(processValidateRequestObject(request, response, paths, resource)){
                if(!processExecute(request, response, paths, resource, postMethodFlowPostfix)){
                    return;
                }
            }
            processWriteResponseBody(request, response, paths, resource);
        }catch(Throwable th){
            getLogger().write("BFRS_00031", new Object[]{resource == null ? request.getURI() : resource.resourcePath.path}, th);
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_EXCEPTION, th);
            }
            response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }finally{
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_RESULT_STATUS, new Integer(response.getResultStatus()));
                journal.endJournal();
            }
        }
    }
    
    public void processGet(GetRestRequest request, GetRestResponse response) throws Throwable{
        ResourceMetaData resource = null;
        try{
            if(journal != null){
                journal.startJournal(JOURNAL_KEY_REST_PROCESS, editorFinder);
                if(sequence != null){
                    String sequenceId = sequence.increment();
                    if(context != null){
                        context.put(requestIdKey, sequenceId);
                    }
                    journal.setRequestId(sequenceId);
                }else if(context != null){
                    journal.setRequestId(
                        (String)context.get(requestIdKey)
                    );
                }
                journal.addInfo(JOURNAL_KEY_REQUEST_URI, request.getURI());
                journal.addInfo(JOURNAL_KEY_METHOD, request.getRequest().getMethod());
            }
            if(!processCheckAccept(request, response)){
                return;
            }
            final List paths = ResourcePath.splitPath(request.getURI());
            resource = processFindResource(request, response, paths);
            if(resource == null){
                return;
            }
            if(resource.getData == null){
                response.setResult(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            if(!processParsePathParameters(request, response, paths, resource)){
                return;
            }
            if(!processCreateRequestObject(request, response, paths, resource, resource.getData.requestData)){
                return;
            }
            if(!processSetupResponseObject(request, response, paths, resource, resource.getData.responseData)){
                return;
            }
            if(!processReadQuery(request, response, paths, resource)){
                return;
            }
            if(processValidateRequestObject(request, response, paths, resource)){
                if(!processExecute(request, response, paths, resource, getMethodFlowPostfix)){
                    return;
                }
            }
            if(response.getResponseObject() == null && response.getResultStatus() == HttpServletResponse.SC_OK){
                response.setResult(HttpServletResponse.SC_NO_CONTENT);
            }
            processWriteResponseBody(request, response, paths, resource);
        }catch(Throwable th){
            getLogger().write("BFRS_00031", new Object[]{resource == null ? request.getURI() : resource.resourcePath.path}, th);
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_EXCEPTION, th);
            }
            response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }finally{
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_RESULT_STATUS, new Integer(response.getResultStatus()));
                journal.endJournal();
            }
        }
    }
    
    public void processHead(HeadRestRequest request, HeadRestResponse response) throws Throwable{
        ResourceMetaData resource = null;
        try{
            if(journal != null){
                journal.startJournal(JOURNAL_KEY_REST_PROCESS, editorFinder);
                if(sequence != null){
                    String sequenceId = sequence.increment();
                    if(context != null){
                        context.put(requestIdKey, sequenceId);
                    }
                    journal.setRequestId(sequenceId);
                }else if(context != null){
                    journal.setRequestId(
                        (String)context.get(requestIdKey)
                    );
                }
                journal.addInfo(JOURNAL_KEY_REQUEST_URI, request.getURI());
                journal.addInfo(JOURNAL_KEY_METHOD, request.getRequest().getMethod());
            }
            final List paths = ResourcePath.splitPath(request.getURI());
            resource = processFindResource(request, response, paths);
            if(resource == null){
                return;
            }
            if(resource.headData == null){
                response.setResult(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            if(!processParsePathParameters(request, response, paths, resource)){
                return;
            }
            if(!processCreateRequestObject(request, response, paths, resource, resource.headData.requestData)){
                return;
            }
            if(!processReadQuery(request, response, paths, resource)){
                return;
            }
            if(processValidateRequestObject(request, response, paths, resource)){
                if(!processExecute(request, response, paths, resource, headMethodFlowPostfix)){
                    return;
                }
            }
        }catch(Throwable th){
            getLogger().write("BFRS_00031", new Object[]{resource == null ? request.getURI() : resource.resourcePath.path}, th);
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_EXCEPTION, th);
            }
            response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }finally{
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_RESULT_STATUS, new Integer(response.getResultStatus()));
                journal.endJournal();
            }
        }
    }
    
    public void processPut(PutRestRequest request, PutRestResponse response) throws Throwable{
        ResourceMetaData resource = null;
        try{
            if(journal != null){
                journal.startJournal(JOURNAL_KEY_REST_PROCESS, editorFinder);
                if(sequence != null){
                    String sequenceId = sequence.increment();
                    if(context != null){
                        context.put(requestIdKey, sequenceId);
                    }
                    journal.setRequestId(sequenceId);
                }else if(context != null){
                    journal.setRequestId(
                        (String)context.get(requestIdKey)
                    );
                }
                journal.addInfo(JOURNAL_KEY_REQUEST_URI, request.getURI());
                journal.addInfo(JOURNAL_KEY_METHOD, request.getRequest().getMethod());
            }
            if(!processCheckAccept(request, response)){
                return;
            }
            if(!processCheckContentType(request, response)){
                return;
            }
            final List paths = ResourcePath.splitPath(request.getURI());
            resource = processFindResource(request, response, paths);
            if(resource == null){
                return;
            }
            if(resource.putData == null){
                response.setResult(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            if(!processParsePathParameters(request, response, paths, resource)){
                return;
            }
            if(!processCreateRequestObject(request, response, paths, resource, resource.putData.requestData)){
                return;
            }
            if(!processSetupResponseObject(request, response, paths, resource, resource.putData.responseData)){
                return;
            }
            if(!processReadRequestBody(request, response, paths, resource)){
                return;
            }
            if(processValidateRequestObject(request, response, paths, resource)){
                if(!processExecute(request, response, paths, resource, putMethodFlowPostfix)){
                    return;
                }
            }
            processWriteResponseBody(request, response, paths, resource);
        }catch(Throwable th){
            getLogger().write("BFRS_00031", new Object[]{resource == null ? request.getURI() : resource.resourcePath.path}, th);
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_EXCEPTION, th);
            }
            response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }finally{
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_RESULT_STATUS, new Integer(response.getResultStatus()));
                journal.endJournal();
            }
        }
    }
    
    public void processDelete(DeleteRestRequest request, DeleteRestResponse response) throws Throwable{
        ResourceMetaData resource = null;
        try{
            if(journal != null){
                journal.startJournal(JOURNAL_KEY_REST_PROCESS, editorFinder);
                if(sequence != null){
                    String sequenceId = sequence.increment();
                    if(context != null){
                        context.put(requestIdKey, sequenceId);
                    }
                    journal.setRequestId(sequenceId);
                }else if(context != null){
                    journal.setRequestId(
                        (String)context.get(requestIdKey)
                    );
                }
                journal.addInfo(JOURNAL_KEY_REQUEST_URI, request.getURI());
                journal.addInfo(JOURNAL_KEY_METHOD, request.getRequest().getMethod());
            }
            if(!processCheckAccept(request, response)){
                return;
            }
            final List paths = ResourcePath.splitPath(request.getURI());
            resource = processFindResource(request, response, paths);
            if(resource == null){
                return;
            }
            if(resource.deleteData == null){
                response.setResult(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            if(!processParsePathParameters(request, response, paths, resource)){
                return;
            }
            if(!processCreateRequestObject(request, response, paths, resource, resource.deleteData.requestData)){
                return;
            }
            if(!processSetupResponseObject(request, response, paths, resource, resource.deleteData.responseData)){
                return;
            }
            if(!processReadQuery(request, response, paths, resource)){
                return;
            }
            if(processValidateRequestObject(request, response, paths, resource)){
                if(!processExecute(request, response, paths, resource, deleteMethodFlowPostfix)){
                    return;
                }
            }
            processWriteResponseBody(request, response, paths, resource);
        }catch(Throwable th){
            getLogger().write("BFRS_00031", new Object[]{resource == null ? request.getURI() : resource.resourcePath.path}, th);
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_EXCEPTION, th);
            }
            response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }finally{
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_RESULT_STATUS, new Integer(response.getResultStatus()));
                journal.endJournal();
            }
        }
    }
    
    public void processOptions(OptionsRestRequest request, OptionsRestResponse response) throws Throwable{
        ResourceMetaData resource = null;
        try{
            if(journal != null){
                journal.startJournal(JOURNAL_KEY_REST_PROCESS, editorFinder);
                if(sequence != null){
                    String sequenceId = sequence.increment();
                    if(context != null){
                        context.put(requestIdKey, sequenceId);
                    }
                    journal.setRequestId(sequenceId);
                }else if(context != null){
                    journal.setRequestId(
                        (String)context.get(requestIdKey)
                    );
                }
                journal.addInfo(JOURNAL_KEY_REQUEST_URI, request.getURI());
                journal.addInfo(JOURNAL_KEY_METHOD, request.getRequest().getMethod());
            }
            final List paths = ResourcePath.splitPath(request.getURI());
            resource = processFindResource(request, response, paths);
            if(resource == null){
                return;
            }
            if(resource.optionsData == null){
                response.setResult(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            if(resource.postData != null){
                response.allowPost();
            }
            if(resource.getData != null){
                response.allowGet();
            }
            if(resource.headData != null){
                response.allowHead();
            }
            if(resource.putData != null){
                response.allowPut();
            }
            if(resource.deleteData != null){
                response.allowDelete();
            }
            response.allowOptions();
        }catch(Throwable th){
            getLogger().write("BFRS_00031", new Object[]{resource == null ? request.getURI() : resource.resourcePath.path}, th);
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_EXCEPTION, th);
            }
            response.setResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }finally{
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_RESULT_STATUS, new Integer(response.getResultStatus()));
                journal.endJournal();
            }
        }
    }
    
    protected class MyErrorHandler implements ErrorHandler{
        
        private boolean isError;
        
        public void warning(SAXParseException e) throws SAXException{
            getLogger().write(
                "BFRS_00001",
                new Object[]{e.getMessage(), serverDefinitionPath, new Integer(e.getLineNumber()), new Integer(e.getColumnNumber())}
            );
        }
        public void error(SAXParseException e) throws SAXException{
            isError = true;
            getLogger().write(
                "BFRS_00002",
                new Object[]{e.getMessage(), serverDefinitionPath, new Integer(e.getLineNumber()), new Integer(e.getColumnNumber())}
            );
        }
        public void fatalError(SAXParseException e) throws SAXException{
            isError = true;
            getLogger().write(
                "BFRS_00003",
                new Object[]{e.getMessage(), serverDefinitionPath, new Integer(e.getLineNumber()), new Integer(e.getColumnNumber())}
            );
        }
        public boolean isError(){
            return isError;
        }
    }
    
    public static class ResourceTree{
        protected Map treeMap = new HashMap();
        
        public void addResource(ResourceMetaData resource) throws DeploymentException{
            ResourceTreeElement currentElement = null;
            for(int i = 0, imax = resource.resourcePath.pathElements.size(); i < imax; i++){
                Object pathElement = resource.resourcePath.pathElements.get(i);
                if(pathElement instanceof ResourcePath.ParameterPath){
                    if(currentElement == null){
                        throw new DeploymentException("Root path must not be parameter. resource=" + resource.resourcePath);
                    }
                    if(currentElement.parameterPath != null && !currentElement.parameterPath.equals(pathElement)){
                        throw new DeploymentException("Resource is duplicated. resource1=" + currentElement.parameterChild.resource.resourcePath + ", resource2=" + resource.resourcePath);
                    }
                    if(currentElement.parameterPath == null){
                        currentElement.parameterPath = (ResourcePath.ParameterPath)pathElement;
                        currentElement.parameterChild = new ResourceTreeElement();
                        currentElement = currentElement.parameterChild;
                    }
                }else{
                    String subPath = (String)pathElement;
                    Map currentMap = currentElement == null ? treeMap : currentElement.children;
                    currentElement = (ResourceTreeElement)currentMap.get(subPath);
                    if(currentElement == null){
                        currentElement = new ResourceTreeElement();
                        currentMap.put(subPath, currentElement);
                    }
                }
                if(i == imax - 1){
                    if(currentElement.resource != null){
                        throw new DeploymentException("Resource is duplicated. resource1=" + currentElement.resource.resourcePath + ", resource2=" + resource.resourcePath);
                    }
                    currentElement.resource = resource;
                }else{
                    if(currentElement.children == null){
                        currentElement.children = new HashMap();
                    }
                }
            }
        }
        
        public ResourceMetaData getResource(List paths){
            ResourceTreeElement currentElement = null;
            for(int i = 0, imax = paths.size(); i < imax; i++){
                Map currentMap = currentElement == null ? treeMap : currentElement.children;
                ResourceTreeElement element = (ResourceTreeElement)currentMap.get(paths.get(i));
                if(element == null && currentElement != null && currentElement.parameterChild != null){
                    element = currentElement.parameterChild;
                }
                if(element == null){
                    return null;
                }else{
                    if(i == imax - 1){
                        return element.resource;
                    }else{
                        currentElement = element;
                    }
                }
            }
            return null;
        }
        
        public static class ResourceTreeElement{
            public ResourceMetaData resource;
            public ResourcePath.ParameterPath parameterPath;
            public ResourceTreeElement parameterChild;
            public Map children;
        }
    }
    
    public static class ResourcePath{
        
        protected String path;
        protected List pathElements = new ArrayList();
        protected List parameterPathIndex;
        
        public ResourcePath(String path) throws IllegalArgumentException{
            if(path.length() == 0){
                throw new IllegalArgumentException("empty path : path=" + path);
            }
            this.path = path;
            List elements = splitPath(path);
            for(int i = 0; i < elements.size(); i++){
                String element = (String)elements.get(i);
                if(ParameterPath.isParameterPath(element)){
                    if(parameterPathIndex == null){
                        parameterPathIndex = new ArrayList();
                    }
                    if(pathElements.size() == 0){
                        throw new IllegalArgumentException("First path must not be parameter : path=" + path);
                    }
                    parameterPathIndex.add(new Integer(pathElements.size()));
                    pathElements.add(new ParameterPath(element));
                }else{
                    pathElements.add(element);
                }
            }
        }
        
        public String getPath(){
            return path;
        }
        
        public List getPathElementList(){
            return pathElements;
        }
        
        public int getParameterPathSize(){
            return parameterPathIndex == null ? 0 : parameterPathIndex.size();
        }
        
        public ParameterPath getParameterPathIndex(int index){
            return parameterPathIndex == null || parameterPathIndex.size() <= index ? null : (ParameterPath)pathElements.get(((Integer)parameterPathIndex.get(index)).intValue());
        }
        
        public static List splitPath(String path){
            String[] paths = path.split("/");
            List result = new ArrayList();
            for(int i = 0; i < paths.length; i++){
                String p = (String)paths[i];
                if(p.length() == 0){
                    continue;
                }
                result.add("/" + p);
            }
            return result;
        }
        
        public boolean hasParameterPath(){
            return parameterPathIndex != null;
        }
        
        public Map parseParameter(List paths, Map result) throws IndexOutOfBoundsException{
            if(!hasParameterPath()){
                return result;
            }
            for(int i = 0; i < parameterPathIndex.size(); i++){
                Integer index = (Integer)parameterPathIndex.get(i);
                ParameterPath pp = (ParameterPath)pathElements.get(index.intValue());
                result = pp.parseParameter((String)paths.get(index.intValue()), result);
            }
            return result;
        }
        
        public String toString(){
            return path;
        }
        
        public static class ParameterPath{
            
            protected static final Pattern PARAMETER_PATTERN = Pattern.compile("\\{.+?\\}");
            
            protected final String path;
            protected final int paramCount;
            protected List paramElements = new ArrayList();
            
            public ParameterPath(String path) throws IllegalArgumentException{
                this.path = path;
                Matcher m = PARAMETER_PATTERN.matcher(path);
                int offset = 0;
                int count = 0;
                while(m.find()){
                    if(offset == 0 && m.start() != offset){
                        paramElements.add(path.substring(offset, m.start()));
                    }else{
                        throw new IllegalArgumentException("deletemer not exists. path=" + path);
                    }
                    paramElements.add(new ParameterElement(m.group()));
                    count++;
                    offset = m.end();
                }
                if(offset != path.length()){
                    paramElements.add(path.substring(offset));
                }
                paramCount = count;
            }
            
            public String getPath(){
                return path;
            }
            
            public int getParameterCount(){
                return paramCount;
            }
            
            public List getParameterElementList(){
                return paramElements;
            }
            
            public Map parseParameter(String path, Map result) throws IndexOutOfBoundsException{
                if(result == null){
                    result = new LinkedHashMap();
                }
                int offset = 0;
                ParameterElement prePe = null;
                for(int i = 0; i < paramElements.size(); i++){
                    Object element = paramElements.get(i);
                    if(prePe == null){
                        if(element instanceof String){
                            offset += ((String)element).length();
                        }else{
                            prePe = (ParameterElement)element;
                        }
                    }else{
                        result.put(prePe.name, path.substring(offset, path.indexOf((String)element, offset)));
                        offset += ((String)element).length();
                        prePe = null;
                    }
                }
                if(prePe != null){
                    result.put(prePe.name, path.substring(offset));
                }
                return result;
            }
            
            public String toString(){
                return path;
            }
            
            public int hashCode(){
                return path.hashCode();
            }
            public boolean equals(Object obj){
                if(obj == null){
                    return false;
                }
                if(obj == this){
                    return true;
                }
                if(!(obj instanceof ParameterPath)){
                    return false;
                }
                return path.equals(((ParameterPath)obj).path);
            }
            
            public static boolean isParameterPath(String path){
                Matcher m = PARAMETER_PATTERN.matcher(path);
                return m.find();
            }
            
            protected static class ParameterElement{
                protected final String name;
                public ParameterElement(String element){
                    name = element.substring(1, element.length() - 1);
                }
                public String getName(){
                    return name;
                }
            }
        }
    }
    
    public static class RestServerMetaData extends MetaData{
        
        private static final long serialVersionUID = -8221854228229536891L;
        
        public static final String TAG_NAME = "restserver";
        
        protected List resources = new ArrayList();
        protected ResourceTree resourceTree = new ResourceTree();
        
        public RestServerMetaData(){
        }
        
        public List getResourceMetaDataList(){
            return resources;
        }
        
        public void importXML(Element element) throws DeploymentException{
            super.importXML(element);
            
            if(!element.getTagName().equals(TAG_NAME)){
                throw new DeploymentException(
                    "Root tag must be " + TAG_NAME + " : "
                     + element.getTagName()
                );
            }
            
            final Iterator propElements = getChildrenByTagName(
                element,
                ResourceMetaData.TAG_NAME
            );
            while(propElements.hasNext()){
                ResourceMetaData resourceData = new ResourceMetaData(RestServerMetaData.this);
                resourceData.importXML((Element)propElements.next());
                resources.add(resourceData);
                resourceTree.addResource(resourceData);
            }
        }
    }
    
    public static class ResourceMetaData extends MetaData{
        
        private static final long serialVersionUID = -9068306933796302144L;
        
        public static final String TAG_NAME = "resource";
        public static final String ATTRIBUTE_NAME_NAME = "name";
        public static final String DESCRIPTION_TAG_NAME = "description";
        
        protected ResourcePath resourcePath;
        protected PostMetaData postData;
        protected GetMetaData getData;
        protected HeadMetaData headData;
        protected PutMetaData putData;
        protected DeleteMetaData deleteData;
        protected OptionsMetaData optionsData;
        
        public ResourceMetaData(RestServerMetaData parent){
            super(parent);
        }
        
        public ResourcePath getResourcePath(){
            return resourcePath;
        }
        
        public PostMetaData getPostMetaData(){
            return postData;
        }
        
        public GetMetaData getGetMetaData(){
            return getData;
        }
        
        public HeadMetaData getHeadMetaData(){
            return headData;
        }
        
        public PutMetaData getPutMetaData(){
            return putData;
        }
        
        public DeleteMetaData getDeleteMetaData(){
            return deleteData;
        }
        
        public OptionsMetaData getOptionsMetaData(){
            return optionsData;
        }
        
        public void importXML(Element element) throws DeploymentException{
            super.importXML(element);
            if(!element.getTagName().equals(TAG_NAME)){
                throw new DeploymentException(
                    "tag must be " + TAG_NAME + " : "
                     + element.getTagName()
                );
            }
            final String name = getUniqueAttribute(element, ATTRIBUTE_NAME_NAME);
            try{
                resourcePath = new ResourcePath(name);
            }catch(IllegalArgumentException e){
                throw new DeploymentException("Resource name is illegal. name=" + name, e);
            }
            
            Element postElement = getOptionalChild(
                element,
                PostMetaData.TAG_NAME
            );
            if(postElement != null){
                postData = new PostMetaData(ResourceMetaData.this);
                postData.importXML(postElement);
            }
            
            Element getElement = getOptionalChild(
                element,
                GetMetaData.TAG_NAME
            );
            if(getElement != null){
                getData = new GetMetaData(ResourceMetaData.this);
                getData.importXML(getElement);
            }
            
            Element headElement = getOptionalChild(
                element,
                HeadMetaData.TAG_NAME
            );
            if(headElement != null){
                headData = new HeadMetaData(ResourceMetaData.this);
                headData.importXML(headElement);
            }
            
            Element putElement = getOptionalChild(
                element,
                PutMetaData.TAG_NAME
            );
            if(putElement != null){
                putData = new PutMetaData(ResourceMetaData.this);
                putData.importXML(putElement);
            }
            
            Element deleteElement = getOptionalChild(
                element,
                DeleteMetaData.TAG_NAME
            );
            if(deleteElement != null){
                deleteData = new DeleteMetaData(ResourceMetaData.this);
                deleteData.importXML(deleteElement);
            }
            
            Element optionsElement = getOptionalChild(
                element,
                OptionsMetaData.TAG_NAME
            );
            if(optionsElement != null){
                optionsData = new OptionsMetaData(ResourceMetaData.this);
                optionsData.importXML(optionsElement);
            }
        }
    }
    
    public static class RequestMetaData extends MetaData{
        
        private static final long serialVersionUID = 6894091068389999950L;
        
        public static final String TAG_NAME = "request";
        public static final String ATTRIBUTE_NAME_CODE = "code";
        
        protected String code;
        
        public RequestMetaData(MetaData parent){
            super(parent);
        }
        
        public String getClassName(){
            return code;
        }
        
        public void importXML(Element element) throws DeploymentException{
            super.importXML(element);
            if(!element.getTagName().equals(TAG_NAME)){
                throw new DeploymentException(
                    "tag must be " + TAG_NAME + " : "
                     + element.getTagName()
                );
            }
            code = getUniqueAttribute(element, ATTRIBUTE_NAME_CODE);
        }
    }
    
    public static class ResponseMetaData extends MetaData{
        
        private static final long serialVersionUID = 4090435433096913841L;
        
        public static final String TAG_NAME = "response";
        public static final String ATTRIBUTE_NAME_CODE = "code";
        
        protected String code;
        
        public ResponseMetaData(MetaData parent){
            super(parent);
        }
        
        public String getClassName(){
            return code;
        }
        
        public void importXML(Element element) throws DeploymentException{
            super.importXML(element);
            if(!element.getTagName().equals(TAG_NAME)){
                throw new DeploymentException(
                    "tag must be " + TAG_NAME + " : "
                     + element.getTagName()
                );
            }
            code = getUniqueAttribute(element, ATTRIBUTE_NAME_CODE);
        }
    }
    
    public static class PostMetaData extends MetaData{
        
        private static final long serialVersionUID = -6372696917063102603L;
        
        public static final String TAG_NAME = "post";
        
        protected String description;
        protected RequestMetaData requestData;
        protected ResponseMetaData responseData;
        
        public PostMetaData(ResourceMetaData parent){
            super(parent);
        }
        
        public String getDescription(){
            return description;
        }
        
        public RequestMetaData getRequestMetaData(){
            return requestData;
        }
        
        public ResponseMetaData getResponseMetaData(){
            return responseData;
        }
        
        public void importXML(Element element) throws DeploymentException{
            super.importXML(element);
            if(!element.getTagName().equals(TAG_NAME)){
                throw new DeploymentException(
                    "tag must be " + TAG_NAME + " : "
                     + element.getTagName()
                );
            }
            
            Element descElement = getOptionalChild(
                element,
                ResourceMetaData.DESCRIPTION_TAG_NAME
            );
            if(descElement != null){
                description = getElementContent(descElement);
            }
            
            Element requestElement = getOptionalChild(
                element,
                RequestMetaData.TAG_NAME
            );
            if(requestElement != null){
                requestData = new RequestMetaData(PostMetaData.this);
                requestData.importXML(requestElement);
            }
            
            Element responseElement = getOptionalChild(
                element,
                ResponseMetaData.TAG_NAME
            );
            if(responseElement != null){
                responseData = new ResponseMetaData(PostMetaData.this);
                responseData.importXML(responseElement);
            }
        }
    }
    
    public static class GetMetaData extends MetaData{
        
        private static final long serialVersionUID = -7298962823068474227L;
        
        public static final String TAG_NAME = "get";
        
        protected String description;
        protected RequestMetaData requestData;
        protected ResponseMetaData responseData;
        
        public GetMetaData(ResourceMetaData parent){
            super(parent);
        }
        
        public String getDescription(){
            return description;
        }
        
        public RequestMetaData getRequestMetaData(){
            return requestData;
        }
        
        public ResponseMetaData getResponseMetaData(){
            return responseData;
        }
        
        public void importXML(Element element) throws DeploymentException{
            super.importXML(element);
            if(!element.getTagName().equals(TAG_NAME)){
                throw new DeploymentException(
                    "tag must be " + TAG_NAME + " : "
                     + element.getTagName()
                );
            }
            
            Element descElement = getOptionalChild(
                element,
                ResourceMetaData.DESCRIPTION_TAG_NAME
            );
            if(descElement != null){
                description = getElementContent(descElement);
            }
            
            Element requestElement = getOptionalChild(
                element,
                RequestMetaData.TAG_NAME
            );
            if(requestElement != null){
                requestData = new RequestMetaData(GetMetaData.this);
                requestData.importXML(requestElement);
            }
            
            Element responseElement = getOptionalChild(
                element,
                ResponseMetaData.TAG_NAME
            );
            if(responseElement != null){
                responseData = new ResponseMetaData(GetMetaData.this);
                responseData.importXML(responseElement);
            }
        }
    }
    
    public static class HeadMetaData extends MetaData{
        
        private static final long serialVersionUID = -5269682659140303058L;
        
        public static final String TAG_NAME = "head";
        
        protected String description;
        protected RequestMetaData requestData;
        
        public HeadMetaData(ResourceMetaData parent){
            super(parent);
        }
        
        public String getDescription(){
            return description;
        }
        
        public RequestMetaData getRequestMetaData(){
            return requestData;
        }
        
        public void importXML(Element element) throws DeploymentException{
            super.importXML(element);
            if(!element.getTagName().equals(TAG_NAME)){
                throw new DeploymentException(
                    "tag must be " + TAG_NAME + " : "
                     + element.getTagName()
                );
            }
            
            Element descElement = getOptionalChild(
                element,
                ResourceMetaData.DESCRIPTION_TAG_NAME
            );
            if(descElement != null){
                description = getElementContent(descElement);
            }
            
            Element requestElement = getOptionalChild(
                element,
                RequestMetaData.TAG_NAME
            );
            if(requestElement != null){
                requestData = new RequestMetaData(HeadMetaData.this);
                requestData.importXML(requestElement);
            }
        }
    }
    
    public static class PutMetaData extends MetaData{
        
        private static final long serialVersionUID = -5753138646526386529L;
        
        public static final String TAG_NAME = "put";
        
        protected String description;
        protected RequestMetaData requestData;
        protected ResponseMetaData responseData;
        
        public PutMetaData(ResourceMetaData parent){
            super(parent);
        }
        
        public String getDescription(){
            return description;
        }
        
        public RequestMetaData getRequestMetaData(){
            return requestData;
        }
        
        public ResponseMetaData getResponseMetaData(){
            return responseData;
        }
        
        public void importXML(Element element) throws DeploymentException{
            super.importXML(element);
            if(!element.getTagName().equals(TAG_NAME)){
                throw new DeploymentException(
                    "tag must be " + TAG_NAME + " : "
                     + element.getTagName()
                );
            }
            
            Element descElement = getOptionalChild(
                element,
                ResourceMetaData.DESCRIPTION_TAG_NAME
            );
            if(descElement != null){
                description = getElementContent(descElement);
            }
            
            Element requestElement = getOptionalChild(
                element,
                RequestMetaData.TAG_NAME
            );
            if(requestElement != null){
                requestData = new RequestMetaData(PutMetaData.this);
                requestData.importXML(requestElement);
            }
            
            Element responseElement = getOptionalChild(
                element,
                ResponseMetaData.TAG_NAME
            );
            if(responseElement != null){
                responseData = new ResponseMetaData(PutMetaData.this);
                responseData.importXML(responseElement);
            }
        }
    }
    
    public static class DeleteMetaData extends MetaData{
        
        private static final long serialVersionUID = -3735047733753523513L;
        
        public static final String TAG_NAME = "delete";
        
        protected String description;
        protected RequestMetaData requestData;
        protected ResponseMetaData responseData;
        
        public DeleteMetaData(ResourceMetaData parent){
            super(parent);
        }
        
        public String getDescription(){
            return description;
        }
        
        public RequestMetaData getRequestMetaData(){
            return requestData;
        }
        
        public ResponseMetaData getResponseMetaData(){
            return responseData;
        }
        
        public void importXML(Element element) throws DeploymentException{
            super.importXML(element);
            if(!element.getTagName().equals(TAG_NAME)){
                throw new DeploymentException(
                    "tag must be " + TAG_NAME + " : "
                     + element.getTagName()
                );
            }
            
            Element descElement = getOptionalChild(
                element,
                ResourceMetaData.DESCRIPTION_TAG_NAME
            );
            if(descElement != null){
                description = getElementContent(descElement);
            }
            
            Element requestElement = getOptionalChild(
                element,
                RequestMetaData.TAG_NAME
            );
            if(requestElement != null){
                requestData = new RequestMetaData(DeleteMetaData.this);
                requestData.importXML(requestElement);
            }
            
            Element responseElement = getOptionalChild(
                element,
                ResponseMetaData.TAG_NAME
            );
            if(responseElement != null){
                responseData = new ResponseMetaData(DeleteMetaData.this);
                responseData.importXML(responseElement);
            }
        }
    }
    
    public static class OptionsMetaData extends MetaData{
        
        private static final long serialVersionUID = -9220229909119120614L;
        
        public static final String TAG_NAME = "options";
        
        public OptionsMetaData(ResourceMetaData parent){
            super(parent);
        }
        
        public void importXML(Element element) throws DeploymentException{
            super.importXML(element);
            if(!element.getTagName().equals(TAG_NAME)){
                throw new DeploymentException(
                    "tag must be " + TAG_NAME + " : "
                     + element.getTagName()
                );
            }
        }
    }
    
    protected static class HeaderValue{
        protected String value;
        protected Map parameters;
        protected int hashCode;
        
        public HeaderValue(){
        }
        
        public HeaderValue(String header){
            String[] types = header.split(";");
            value = types[0].trim();
            hashCode = value.hashCode();
            if(types.length > 1){
                parameters = new HashMap();
                for(int i = 1; i < types.length; i++){
                    String parameter = types[i].trim();
                    final int index = parameter.indexOf('=');
                    if(index != -1){
                        parameters.put(parameter.substring(0, index).toLowerCase(), parameter.substring(index + 1).toLowerCase());
                    }else{
                        parameters.put(parameter.toLowerCase(), null);
                    }
                }
                hashCode += parameters.hashCode();
            }
        }
        public String getValue(){
            return value;
        }
        public void setValue(String val){
            value = val;
        }
        public String getParameter(String name){
            return parameters == null ? null : (String)parameters.get(name);
        }
        public void setParameter(String name, String value){
            if(parameters == null){
                parameters = new HashMap();
            }
            parameters.put(name, value);
        }
        
        public String toString(){
            StringBuilder buf = new StringBuilder();
            buf.append(value);
            if(parameters != null){
                Iterator entries = parameters.entrySet().iterator();
                while(entries.hasNext()){
                    Map.Entry entry = (Map.Entry)entries.next();
                    buf.append(';').append(entry.getKey()).append('=').append(entry.getValue());
                }
            }
            return buf.toString();
        }
        
        public boolean equals(Object obj){
            if(obj == null || !(obj instanceof HeaderValue)){
                return false;
            }
            if(obj == this){
                return true;
            }
            HeaderValue cmp = (HeaderValue)obj;
            if(!value.equals(cmp.value)){
                return false;
            }
            if((parameters == null && cmp.parameters != null)
                || (parameters != null && cmp.parameters == null)
                || (parameters != null && !parameters.equals(cmp.parameters))){
                return false;
            }
            return true;
        }
        public int hashCode(){
            return hashCode;
        }
    }
    
    protected static class MediaType extends HeaderValue{
        public MediaType(){
        }
        public MediaType(String header){
            super(header);
        }
        public String getMediaType(){
            return getValue();
        }
        public void setMediaType(String type){
            setValue(type);
        }
    }
    
    protected static class ContentType extends MediaType{
        public ContentType(String mediaType, String charset){
            setMediaType(mediaType);
            setCharset(charset);
        }
        public String getCharset(){
            return getParameter("charset");
        }
        public void setCharset(String charset){
            setParameter("charset", charset);
        }
    }
    
    protected static class MediaRange extends MediaType{
        protected float q = 1.0f;
        public MediaRange(String header) throws IllegalArgumentException{
            super(header);
            String qvalue = getParameter("q");
            if(qvalue != null){
                try{
                    q = Float.parseFloat(qvalue);
                }catch(NumberFormatException e){
                    throw new IllegalArgumentException("qvalue is illegal. q=" + qvalue);
                }
            }
        }
    }
    
    protected static class CharsetRange extends HeaderValue{
        protected float q = 1.0f;
        public CharsetRange(String header) throws IllegalArgumentException{
            super(header);
            String qvalue = getParameter("q");
            if(qvalue != null){
                try{
                    q = Float.parseFloat(qvalue);
                }catch(NumberFormatException e){
                    throw new IllegalArgumentException("qvalue is illegal. q=" + qvalue);
                }
            }
        }
        public String getCharset(){
            return getValue();
        }
        public void setCharset(String charset){
            setValue(charset);
        }
    }
    
    protected static class Accept{
        protected final List mediaRanges;
        public Accept(String header) throws IllegalArgumentException{
            String[] mediaRangeArray = header.split(",");
            mediaRanges = new ArrayList(mediaRangeArray.length);
            for(int i = 0; i < mediaRangeArray.length; i++){
                mediaRanges.add(new MediaRange(mediaRangeArray[i]));
            }
            Collections.sort(
                mediaRanges,
                new Comparator(){
                    public int compare(Object o1, Object o2){
                        return ((MediaRange)o1).q == ((MediaRange)o2).q ? 0 : ((MediaRange)o1).q > ((MediaRange)o2).q ? -1 : 1;
                    }
                }
            );
        }
    }
    
    protected static class AcceptCharset{
        protected final List charsetRanges;
        public AcceptCharset(String header) throws IllegalArgumentException{
            String[] charsetRangeArray = header.split(",");
            charsetRanges = new ArrayList(charsetRangeArray.length);
            for(int i = 0; i < charsetRangeArray.length; i++){
                charsetRanges.add(new CharsetRange(charsetRangeArray[i]));
            }
            Collections.sort(
                charsetRanges,
                new Comparator(){
                    public int compare(Object o1, Object o2){
                        return ((CharsetRange)o1).q == ((CharsetRange)o2).q ? 0 : ((CharsetRange)o1).q > ((CharsetRange)o2).q ? -1 : 1;
                    }
                }
            );
        }
    }
}