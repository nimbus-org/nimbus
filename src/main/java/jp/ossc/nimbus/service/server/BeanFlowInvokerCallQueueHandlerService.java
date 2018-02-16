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
package jp.ossc.nimbus.service.server;

import java.io.*;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.aop.interceptor.ThreadContextKey;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory;
import jp.ossc.nimbus.service.queue.QueueHandler;
import jp.ossc.nimbus.service.journal.Journal;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.util.converter.StreamConverter;
import jp.ossc.nimbus.util.converter.BindingStreamConverter;

/**
 * BeanFlow実行QueueHandlerサービス。<p>
 * 
 * @author M.Takata
 */
public class BeanFlowInvokerCallQueueHandlerService extends ServiceBase
 implements QueueHandler, BeanFlowInvokerCallQueueHandlerServiceMBean{
    
    private static final long serialVersionUID = 6110742740354448821L;
    
    protected ServiceName beanFlowInvokerFactoryServiceName;
    protected BeanFlowInvokerFactory beanFlowInvokerFactory;
    
    protected ServiceName requestStreamConverterServiceName;
    protected StreamConverter requestStreamConverter;
    
    protected ServiceName responseStreamConverterServiceName;
    protected StreamConverter responseStreamConverter;
    
    protected ServiceName journalServiceName;
    protected Journal journal;
    
    protected ServiceName threadContextServiceName;
    protected Context threadContext;
    
    protected String actionFlowNamePrefix = DEFAULT_ACTION_FLOW_NAME_PREFIX;
    
    protected String requestObjectFlowNamePrefix = DEFAULT_REQUEST_OBJECT_FLOW_NAME_PREFIX;
    
    protected int normalStatus = DEFAULT_STATUS_NORMAL;
    protected int notFoundStatus = DEFAULT_STATUS_NOT_FOUND;
    protected int errorStatus = DEFAULT_STATUS_ERROR;
    
    protected String errorLogMessageId;
    protected String retryOverErrorLogMessageId;
    
    public void setRequestStreamConverterServiceName(ServiceName name){
        requestStreamConverterServiceName = name;
    }
    public ServiceName getRequestStreamConverterServiceName(){
        return requestStreamConverterServiceName;
    }
    
    public void setResponseStreamConverterServiceName(ServiceName name){
        responseStreamConverterServiceName = name;
    }
    public ServiceName getResponseStreamConverterServiceName(){
        return responseStreamConverterServiceName;
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
    
    public void setThreadContextServiceName(ServiceName name){
        threadContextServiceName = name;
    }
    public ServiceName getThreadContextServiceName(){
        return threadContextServiceName;
    }
    
    public void setRequestObjectFlowNamePrefix(String prefix){
        requestObjectFlowNamePrefix = prefix;
    }
    public String getRequestObjectFlowNamePrefix(){
        return requestObjectFlowNamePrefix;
    }
    
    public void setActionFlowNamePrefix(String prefix){
        actionFlowNamePrefix = prefix;
    }
    public String getActionFlowNamePrefix(){
        return actionFlowNamePrefix;
    }
    
    public int getNormalStatus(){
        return normalStatus;
    }
    public void setNormalStatus(int status){
        normalStatus = status;
    }
    
    public int getNotFoundStatus(){
        return notFoundStatus;
    }
    public void setNotFoundStatus(int status){
        notFoundStatus = status;
    }
    
    public int getErrorStatus(){
        return errorStatus;
    }
    public void setErrorStatus(int status){
        errorStatus = status;
    }
    
    public String getErrorLogMessageId(){
        return errorLogMessageId;
    }
    public void setErrorLogMessageId(String id){
        errorLogMessageId = id;
    }
    
    public String getRetryOverErrorLogMessageId(){
        return retryOverErrorLogMessageId;
    }
    public void setRetryOverErrorLogMessageId(String id){
        retryOverErrorLogMessageId = id;
    }
    
    public void startService() throws Exception{
        if(beanFlowInvokerFactoryServiceName != null){
            beanFlowInvokerFactory = (BeanFlowInvokerFactory)ServiceManagerFactory
                .getServiceObject(beanFlowInvokerFactoryServiceName);
        }
        if(beanFlowInvokerFactory == null){
            throw new IllegalArgumentException("BeanFlowInvoker is null.");
        }
        if(requestStreamConverterServiceName == null
             && requestStreamConverter == null){
            throw new IllegalArgumentException("It is necessary to specify RequestStreamConverterServiceName or RequestStreamConverter.");
        }
        if(requestStreamConverterServiceName != null){
            requestStreamConverter = (StreamConverter)ServiceManagerFactory
                .getServiceObject(requestStreamConverterServiceName);
        }
        if(responseStreamConverterServiceName == null
             && responseStreamConverter == null){
            throw new IllegalArgumentException("It is necessary to specify ResponseStreamConverterServiceName or ResponseStreamConverter.");
        }
        if(responseStreamConverterServiceName != null){
            responseStreamConverter = (StreamConverter)ServiceManagerFactory
                .getServiceObject(responseStreamConverterServiceName);
        }
        if(journalServiceName != null){
            journal = (Journal)ServiceManagerFactory.getServiceObject(
                journalServiceName
            );
        }
        if(threadContextServiceName != null){
            threadContext = (Context)ServiceManagerFactory.getServiceObject(
                threadContextServiceName
            );
        }
    }
    
    public void setBeanFlowInvokerFactory(BeanFlowInvokerFactory factory){
        beanFlowInvokerFactory = factory;
    }
    public BeanFlowInvokerFactory getBeanFlowInvokerFactory(){
        return beanFlowInvokerFactory;
    }
    
    public void setRequestStreamConverter(StreamConverter conv){
        requestStreamConverter = conv;
    }
    public StreamConverter getRequestStreamConverter(){
        return requestStreamConverter;
    }
    
    public void setResponseStreamConverter(StreamConverter conv){
        responseStreamConverter = conv;
    }
    public StreamConverter getResponseStreamConverter(){
        return responseStreamConverter;
    }
    
    public void setJournal(Journal journal){
        this.journal = journal;
    }
    public Journal getJournal(){
        return journal;
    }
    
    public void setThreadContext(Context context){
        threadContext = context;
    }
    public Context getThreadContext(){
        return threadContext;
    }
    
    // QueueHandlerのJavaDoc
    public void handleDequeuedObject(Object obj) throws Throwable{
        if(obj == null){
            return;
        }
        RequestContext context = (RequestContext)obj;
        try{
            if(threadContext != null){
                threadContext.clear();
            }
            String requestId = context.getRequest().getRequestId();
            if(requestId != null && threadContext != null){
                threadContext.put(
                    ThreadContextKey.REQUEST_ID,
                    requestId
                );
            }
            if(journal != null){
                journal.startJournal(JOURNAL_ACCESS);
                if(requestId != null){
                    journal.setRequestId(requestId);
                }
                journal.addInfo(JOURNAL_REQUEST_ACTION, ((ActionRequest)context.getRequest()).getAction());
                journal.addInfo(JOURNAL_REQUEST_DATE, context.getRequest().getDate());
                journal.addInfo(JOURNAL_REQUEST_REMOTE_HOST, context.getRequest().getRemoteHost());
                journal.addInfo(JOURNAL_REQUEST_REMOTE_PORT, new Integer(context.getRequest().getRemotePort()));
            }
            
            String actionFlowName = actionFlowNamePrefix + ((ActionRequest)context.getRequest()).getAction();
            if(!beanFlowInvokerFactory.containsFlow(actionFlowName)){
                ((StatusResponse)context.getResponse()).setStatus(notFoundStatus);
                try{
                    context.getResponse().response();
                }catch(IOException e){
                }
                return;
            }
            Object requestObj = null;
            InputStream requestStream = context.getRequest().getInputStream();
            if(journal != null){
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] bytes = new byte[1024];
                int readLen = 0;
                while((readLen = requestStream.read(bytes)) > 0){
                    baos.write(bytes, 0, readLen);
                }
                bytes = baos.toByteArray();
                journal.addInfo(JOURNAL_REQUEST_BODY, bytes);
                requestStream = new ByteArrayInputStream(bytes);
            }
            if(requestStreamConverter instanceof BindingStreamConverter){
                String requestObjectFlowName = requestObjectFlowNamePrefix + ((ActionRequest)context.getRequest()).getAction();
                if(beanFlowInvokerFactory.containsFlow(requestObjectFlowName)){
                    requestObj = beanFlowInvokerFactory.createFlow(requestObjectFlowName).invokeFlow(context);
                }
            }
            if(requestObj != null){
                requestObj = ((BindingStreamConverter)requestStreamConverter).convertToObject(requestStream, requestObj);
            }else{
                requestObj = requestStreamConverter.convertToObject(requestStream);
            }
            
            if(journal != null){
                journal.addInfo(JOURNAL_REQUEST_OBJECT, requestObj);
            }
            Object responseObject = beanFlowInvokerFactory.createFlow(actionFlowName).invokeFlow(requestObj);
            if(journal != null){
                journal.addInfo(JOURNAL_RESPONSE_OBJECT, responseObject);
            }
            if(responseObject == null){
                ((StatusResponse)context.getResponse()).setStatus(normalStatus);
                if(journal != null){
                    journal.addInfo(JOURNAL_RESPONSE_STATUS, new Integer(normalStatus));
                }
                try{
                    context.getResponse().response();
                }catch(IOException e){
                }
            }else{
                InputStream responseStream = responseStreamConverter.convertToStream(responseObject);
                if(journal != null){
                    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] bytes = new byte[1024];
                    int readLen = 0;
                    while((readLen = responseStream.read(bytes)) > 0){
                        baos.write(bytes, 0, readLen);
                    }
                    bytes = baos.toByteArray();
                    journal.addInfo(JOURNAL_RESPONSE_BODY, bytes);
                    responseStream = new ByteArrayInputStream(bytes);
                }
                ((StatusResponse)context.getResponse()).setStatus(normalStatus);
                if(journal != null){
                    journal.addInfo(JOURNAL_RESPONSE_STATUS, new Integer(normalStatus));
                }
                try{
                    context.getResponse().response(responseStream);
                }catch(IOException e){
                }
            }
        }finally{
            if(journal != null){
                journal.endJournal();
            }
            if(threadContext != null){
                threadContext.clear();
            }
        }
    }
    
    // QueueHandlerのJavaDoc
    public boolean handleError(Object obj, Throwable th) throws Throwable{
        RequestContext context = (RequestContext)obj;
        if(errorLogMessageId != null){
            getLogger().write(errorLogMessageId, ((ActionRequest)context.getRequest()).getAction(), th);
        }
        return true;
    }
    
    // QueueHandlerのJavaDoc
    public void handleRetryOver(Object obj, Throwable th) throws Throwable{
        RequestContext context = (RequestContext)obj;
        if(retryOverErrorLogMessageId != null){
            getLogger().write(retryOverErrorLogMessageId, ((ActionRequest)context.getRequest()).getAction(), th);
        }
        ((StatusResponse)context.getResponse()).setStatus(errorStatus);
        if(journal != null){
            journal.addInfo(JOURNAL_RESPONSE_STATUS, new Integer(errorStatus));
            journal.addInfo(JOURNAL_ACCESS_EXCEPTION, th);
        }
        try{
            context.getResponse().response();
        }catch(IOException e){
        }
    }
}
