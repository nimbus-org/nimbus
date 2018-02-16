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
package jp.ossc.nimbus.service.ioccall.interceptor;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aspect.interfaces.InterceptorException;
import jp.ossc.nimbus.service.aspect.interfaces.TargetCheckedException;
import jp.ossc.nimbus.service.aspect.interfaces.TargetUncheckedException;
import jp.ossc.nimbus.service.aop.InvocationContext;
import jp.ossc.nimbus.service.aop.MethodInvocationContext;
import jp.ossc.nimbus.service.journal.Journal;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.service.sequence.Sequence;

/**
 * IOCジャーナルインターセプタ。<p>
 *
 * @author M.Takata
 */
public class JournalInterceptorService extends ServiceBase
 implements JournalInterceptorServiceMBean,
            jp.ossc.nimbus.service.aspect.interfaces.Interceptor,
            jp.ossc.nimbus.service.aop.Interceptor{
    
    private static final long serialVersionUID = -5639122310302405874L;
    
    protected ServiceName journalServiceName;
    protected Journal journal;
    protected ServiceName sequenceServiceName;
    protected Sequence sequence;
    protected ServiceName contextServiceName;
    protected Context context;
    
    protected ServiceName stepEditorFinderServiceName;
    protected EditorFinder stepEditorFinder;
    protected ServiceName inputEditorFinderServiceName;
    protected EditorFinder inputEditorFinder;
    protected ServiceName outputEditorFinderServiceName;
    protected EditorFinder outputEditorFinder;
    protected ServiceName exceptionEditorFinderServiceName;
    protected EditorFinder exceptionEditorFinder;
    
    protected String stepJournalKey = DEFAULT_STEP_JOURNAL_KEY;
    protected String inputJournalKey = DEFAULT_INPUT_JOURNAL_KEY;
    protected String outputJournalKey = DEFAULT_OUTPUT_JOURNAL_KEY;
    protected String exceptionJournalKey = DEFAULT_EXCEPTION_JOURNAL_KEY;
    protected String requestIdKey = DEFAULT_REQUEST_ID_KEY;
    
    // JournalInterceptorServiceMBeanのJavaDoc
    public void setJournalServiceName(ServiceName name){
        journalServiceName = name;
    }
    
    // JournalInterceptorServiceMBeanのJavaDoc
    public ServiceName getJournalServiceName(){
        return journalServiceName;
    }
    
    // JournalInterceptorServiceMBeanのJavaDoc
    public void setStepEditorFinderServiceName(ServiceName name){
        stepEditorFinderServiceName = name;
    }
    
    // JournalInterceptorServiceMBeanのJavaDoc
    public ServiceName getStepEditorFinderServiceName(){
        return stepEditorFinderServiceName;
    }
    
    // JournalInterceptorServiceMBeanのJavaDoc
    public void setInputEditorFinderServiceName(ServiceName name){
        inputEditorFinderServiceName = name;
    }
    
    // JournalInterceptorServiceMBeanのJavaDoc
    public ServiceName getInputEditorFinderServiceName(){
        return inputEditorFinderServiceName;
    }
    
    // JournalInterceptorServiceMBeanのJavaDoc
    public void setOutputEditorFinderServiceName(ServiceName name){
        outputEditorFinderServiceName = name;
    }
    
    // JournalInterceptorServiceMBeanのJavaDoc
    public ServiceName getOutputEditorFinderServiceName(){
        return outputEditorFinderServiceName;
    }
    
    // JournalInterceptorServiceMBeanのJavaDoc
    public void setExceptionEditorFinderServiceName(ServiceName name){
        exceptionEditorFinderServiceName = name;
    }
    
    // JournalInterceptorServiceMBeanのJavaDoc
    public ServiceName getExceptionEditorFinderServiceName(){
        return exceptionEditorFinderServiceName;
    }
    
    // JournalInterceptorServiceMBeanのJavaDoc
    public void setSequenceServiceName(ServiceName name){
        sequenceServiceName = name;
    }
    
    // JournalInterceptorServiceMBeanのJavaDoc
    public ServiceName getSequenceServiceName(){
        return sequenceServiceName;
    }
    
    // JournalInterceptorServiceMBeanのJavaDoc
    public void setContextServiceName(ServiceName name){
        contextServiceName = name;
    }
    
    // JournalInterceptorServiceMBeanのJavaDoc
    public ServiceName getContextServiceName(){
        return contextServiceName;
    }
    
    // JournalInterceptorServiceMBeanのJavaDoc
    public void setRequestIDKey(String key){
        requestIdKey = key;
    }
    
    // JournalInterceptorServiceMBeanのJavaDoc
    public String getRequestIDKey(){
        return requestIdKey;
    }
    
    // JournalInterceptorServiceMBeanのJavaDoc
    public void setStepJournalKey(String key){
        stepJournalKey = key;
    }
    
    // JournalInterceptorServiceMBeanのJavaDoc
    public String getStepJournalKey(){
        return stepJournalKey;
    }
    
    // JournalInterceptorServiceMBeanのJavaDoc
    public void setInputJournalKey(String key){
        inputJournalKey = key;
    }
    
    // JournalInterceptorServiceMBeanのJavaDoc
    public String getInputJournalKey(){
        return inputJournalKey;
    }
    
    // JournalInterceptorServiceMBeanのJavaDoc
    public void setOutputJournalKey(String key){
        outputJournalKey = key;
    }
    
    // JournalInterceptorServiceMBeanのJavaDoc
    public String getOutputJournalKey(){
        return outputJournalKey;
    }
    
    // JournalInterceptorServiceMBeanのJavaDoc
    public void setExceptionJournalKey(String key){
        exceptionJournalKey = key;
    }
    
    // JournalInterceptorServiceMBeanのJavaDoc
    public String getExceptionJournalKey(){
        return exceptionJournalKey;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始に失敗した場合
     */
    public void startService() throws Exception{
        if(journalServiceName == null){
            throw new IllegalArgumentException(
                "journalServiceName must be specified."
            );
        }
        journal = (Journal)ServiceManagerFactory.getServiceObject(
            journalServiceName
        );
        
        if(stepEditorFinderServiceName != null){
            stepEditorFinder = (EditorFinder)ServiceManagerFactory
                .getServiceObject(
                    stepEditorFinderServiceName
                );
        }
        
        if(inputEditorFinderServiceName != null){
            inputEditorFinder = (EditorFinder)ServiceManagerFactory
                .getServiceObject(
                    inputEditorFinderServiceName
                );
        }
        
        if(outputEditorFinderServiceName != null){
            outputEditorFinder = (EditorFinder)ServiceManagerFactory
                .getServiceObject(
                    outputEditorFinderServiceName
                );
        }
        
        if(exceptionEditorFinderServiceName != null){
            exceptionEditorFinder = (EditorFinder)ServiceManagerFactory
                .getServiceObject(
                    exceptionEditorFinderServiceName
                );
        }
        
        if(sequenceServiceName != null){
            sequence = (Sequence)ServiceManagerFactory
                .getServiceObject(
                    sequenceServiceName
                );
        }
        
        if(contextServiceName != null){
            context = (Context)ServiceManagerFactory
                .getServiceObject(
                    contextServiceName
                );
        }
    }
    
    public Object invokeChain(
        Object inputObj,
        jp.ossc.nimbus.service.aspect.interfaces.InterceptorChain interceptChain
    ) throws InterceptorException, TargetCheckedException,
             TargetUncheckedException{
        try{
            return invokeInternal(inputObj, interceptChain, null);
        }catch(InterceptorException e){
            throw e;
        }catch(TargetCheckedException e){
            throw e;
        }catch(TargetUncheckedException e){
            throw e;
        }catch(Throwable th){
            throw new InterceptorException(th);
        }
    }
    
    public Object invoke(
        InvocationContext context,
        jp.ossc.nimbus.service.aop.InterceptorChain chain
    ) throws Throwable{
        return invokeInternal(context, null, chain);
    }
    
    protected Object invokeInternal(
        Object inputObj,
        jp.ossc.nimbus.service.aspect.interfaces.InterceptorChain interceptChain,
        jp.ossc.nimbus.service.aop.InterceptorChain chain
    ) throws Throwable{
        Object input = inputObj;
        if(chain != null){
            input = ((MethodInvocationContext)input).getParameters()[0];
        }
        if(getState() == STARTED){
            try{
                journal.startJournal(stepJournalKey, stepEditorFinder);
                if(sequence != null){
                    journal.setRequestId(sequence.increment());
                }else if(context != null){
                    journal.setRequestId(
                        (String)context.get(requestIdKey)
                    );
                }
                Object ret = null;
                journal.addInfo(inputJournalKey, input, inputEditorFinder);
                if(interceptChain != null){
                    ret = interceptChain.invokeChain(input);
                }else{
                    ret = chain.invokeNext((InvocationContext)inputObj);
                }
                journal.addInfo(outputJournalKey, ret, outputEditorFinder);
                return ret;
            }catch(Throwable th){
                journal.addInfo(exceptionJournalKey, th, exceptionEditorFinder);
                throw th;
            }finally{
                journal.endJournal();
            }
        }else{
            if(interceptChain != null){
                return interceptChain.invokeChain(input);
            }else{
                return chain.invokeNext((InvocationContext)inputObj);
            }
        }
    }
}