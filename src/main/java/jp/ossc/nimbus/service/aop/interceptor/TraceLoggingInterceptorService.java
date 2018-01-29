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
package jp.ossc.nimbus.service.aop.interceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.MethodEditor;
import jp.ossc.nimbus.beans.PropertyAccess;
import jp.ossc.nimbus.beans.NoSuchPropertyException;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.sequence.Sequence;

/**
 * トレースロギングインターセプタ。<p>
 * メソッドの呼び出しに対して、ログ出力を行うインターセプタである。<br>
 * 以下に、トレースロギングインターセプタのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="TraceLoggingInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.TraceLoggingInterceptorService"/&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class TraceLoggingInterceptorService extends ServiceBase
 implements Interceptor, TraceLoggingInterceptorServiceMBean{
    
    private static final long serialVersionUID = -6275466936441558184L;
    private String requestMessageId = DEFAULT_TRACE_REQUEST_MESSAGE_ID;
    private String responseMessageId = DEFAULT_TRACE_RESPONSE_MESSAGE_ID;
    
    private boolean isEnabled = true;
    private boolean isOutputRequestLog = true;
    private boolean isOutputResponseLog = false;
    private boolean isOutputTarget = true;
    private boolean isOutputMethod = true;
    private boolean isOutputParameter = false;
    private boolean isOutputCallStackTrace = false;
    private boolean isOutputReturn = true;
    private boolean isOutputThrowable = false;
    private boolean isOutputPerformance = false;
    private boolean isOutputTargetOnResponse = false;
    private boolean isOutputMethodOnResponse = false;
    private boolean isOutputParameterOnResponse = false;
    private String[] outputTargetProperties;
    private String[] outputParameterProperties;
    private String[] outputReturnProperties;
    private PropertyAccess propertyAccess;
    private ServiceName sequenceServiceName;
    private Sequence sequence;
    
    public void setEnabled(boolean enable){
        isEnabled = enable;
    }
    public boolean isEnabled(){
        return isEnabled;
    }
    
    public void setRequestMessageId(String id){
        requestMessageId = id;
    }
    public String getRequestMessageId(){
        return requestMessageId;
    }
    
    public void setResponseMessageId(String id){
        responseMessageId = id;
    }
    public String getResponseMessageId(){
        return responseMessageId;
    }
    
    public void setOutputRequestLog(boolean isOutput){
        isOutputRequestLog = isOutput;
    }
    public boolean  isOutputRequestLog(){
        return isOutputRequestLog;
    }
    
    public void setOutputResponseLog(boolean isOutput){
        isOutputResponseLog = isOutput;
    }
    public boolean  isOutputResponseLog(){
        return isOutputResponseLog;
    }
    
    public void setOutputTarget(boolean isOutput){
        isOutputTarget = isOutput;
    }
    public boolean  isOutputTarget(){
        return isOutputTarget;
    }
    
    public void setOutputMethod(boolean isOutput){
        isOutputMethod = isOutput;
    }
    public boolean  isOutputMethod(){
        return isOutputMethod;
    }
    
    public void setOutputParameter(boolean isOutput){
        isOutputParameter = isOutput;
    }
    public boolean  isOutputParameter(){
        return isOutputParameter;
    }
    
    public void setOutputCallStackTrace(boolean isOutput){
        isOutputCallStackTrace = isOutput;
    }
    public boolean  isOutputCallStackTrace(){
        return isOutputCallStackTrace;
    }
    
    public void setOutputReturn(boolean isOutput){
        isOutputReturn = isOutput;
    }
    public boolean  isOutputReturn(){
        return isOutputReturn;
    }
    
    public void setOutputThrowable(boolean isOutput){
        isOutputThrowable = isOutput;
    }
    public boolean  isOutputThrowable(){
        return isOutputThrowable;
    }
    
    public void setOutputPerformance(boolean isOutput){
        isOutputPerformance = isOutput;
    }
    public boolean  isOutputPerformance(){
        return isOutputPerformance;
    }
    
    public void setOutputTargetOnResponse(boolean isOutput){
        isOutputTargetOnResponse = isOutput;
    }
    public boolean  isOutputTargetOnResponse(){
        return isOutputTargetOnResponse;
    }
    
    public void setOutputMethodOnResponse(boolean isOutput){
        isOutputMethodOnResponse = isOutput;
    }
    public boolean  isOutputMethodOnResponse(){
        return isOutputMethodOnResponse;
    }
    
    public void setOutputParameterOnResponse(boolean isOutput){
        isOutputParameterOnResponse = isOutput;
    }
    public boolean  isOutputParameterOnResponse(){
        return isOutputParameterOnResponse;
    }
    
    public void setOutputTargetProperties(String[] props){
        outputTargetProperties = props;
    }
    public String[] getOutputTargetProperties(){
        return outputTargetProperties;
    }
    
    public void setOutputParameterProperties(String[] props){
        outputParameterProperties = props;
    }
    public String[] getOutputParameterProperties(){
        return outputParameterProperties;
    }
    
    public void setOutputReturnProperties(String[] props){
        outputReturnProperties = props;
    }
    public String[] getOutputReturnProperties(){
        return outputReturnProperties;
    }
    
    public void setSequenceServiceName(ServiceName name){
        sequenceServiceName = name;
    }
    public ServiceName getSequenceServiceName(){
        return sequenceServiceName;
    }
    
    public void setSequence(Sequence sequence){
        this.sequence = sequence;
    }
    
    public void startService() throws Exception{
        if((outputParameterProperties != null && outputParameterProperties.length != 0)
            || (outputReturnProperties != null && outputReturnProperties.length != 0)
            || (outputTargetProperties != null && outputTargetProperties.length != 0)){
            propertyAccess = new PropertyAccess();
            propertyAccess.setIgnoreNullProperty(true);
        }
        if(sequenceServiceName != null){
            sequence = (Sequence)ServiceManagerFactory
                .getServiceObject(sequenceServiceName);
        }
    }
    
    /**
     * ログを出力して、次のインターセプタを呼び出す。<p>
     * サービスが開始されていない場合は、次のインターセプタを呼び出す。<br>
     *
     * @param context 呼び出しのコンテキスト情報
     * @param chain 次のインターセプタを呼び出すためのチェーン
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合、またはこのインターセプタで任意の例外が発生した場合。但し、本来呼び出される処理がthrowしないRuntimeException以外の例外をthrowしても、呼び出し元には伝播されない。
     */
    public Object invoke(
        InvocationContext context,
        InterceptorChain chain
    ) throws Throwable{
        if(getState() != STARTED || !isEnabled()){
            return chain.invokeNext(context);
        }
        final long start = System.currentTimeMillis();
        StringBuilder buf = null;
        StringBuilder targetBuf = null;
        StringBuilder methodBuf = null;
        StringBuilder parameterBuf = null;
        String sequenceId = null;
        if(sequence != null){
            sequenceId = sequence.increment();
            if(buf == null){
                buf = new StringBuilder();
            }
            buf.append(sequenceId);
        }
        if(isOutputTarget || isOutputTargetOnResponse){
            targetBuf = new StringBuilder();
            Object target = context.getTargetObject();
            if(outputTargetProperties != null && outputTargetProperties.length != 0){
                targetBuf.append('[');
                for(int i = 0; i < outputTargetProperties.length; i++){
                    Object prop = null;
                    try{
                        prop = propertyAccess.get(target, outputTargetProperties[i]);
                    }catch(IllegalArgumentException e){
                    }catch(NoSuchPropertyException e){
                    }catch(InvocationTargetException e){
                    }
                    printObject(targetBuf, prop);
                    if(i != outputTargetProperties.length - 1){
                        targetBuf.append(',');
                    }
                }
                targetBuf.append(']');
            }else{
                printObject(targetBuf, target);
            }
            if(isOutputTarget){
                if(buf == null){
                    buf = new StringBuilder();
                }else{
                    buf.append(',');
                }
                buf.append(targetBuf);
            }
        }
        if(context instanceof MethodInvocationContext){
            MethodInvocationContext methodContext = (MethodInvocationContext)context;
            if(isOutputMethod || isOutputMethodOnResponse){
                methodBuf = new StringBuilder();
                Method method = methodContext.getTargetMethod();
                if(method != null){
                    MethodEditor editor = new MethodEditor();
                    editor.setValue(method);
                    methodBuf.append(editor.getAsText());
                }else{
                    methodBuf.append("null");
                }
                if(isOutputMethod){
                    if(buf == null){
                        buf = new StringBuilder();
                    }else{
                        buf.append(',');
                    }
                    buf.append(methodBuf);
                }
            }
            if(isOutputParameter || isOutputParameterOnResponse){
                parameterBuf = new StringBuilder();
                Object[] params = methodContext.getParameters();
                if(params != null){
                    parameterBuf.append('[');
                    if(outputParameterProperties != null && outputParameterProperties.length != 0){
                        for(int i = 0; i < outputParameterProperties.length; i++){
                            Object prop = null;
                            try{
                                prop = propertyAccess.get(params, outputParameterProperties[i]);
                            }catch(IllegalArgumentException e){
                            }catch(NoSuchPropertyException e){
                            }catch(InvocationTargetException e){
                            }
                            printObject(parameterBuf, prop);
                            if(i != outputParameterProperties.length - 1){
                                parameterBuf.append(',');
                            }
                        }
                    }else{
                        printObject(parameterBuf, params);
                    }
                    parameterBuf.append(']');
                }else{
                    parameterBuf.append("null");
                }
                if(isOutputParameter){
                    if(buf == null){
                        buf = new StringBuilder();
                    }else{
                        buf.append(',');
                    }
                    buf.append(parameterBuf);
                }
            }
        }
        if(isOutputRequestLog && getLogger().isWrite(requestMessageId)){
            if(isOutputCallStackTrace){
                getLogger().write(requestMessageId, buf == null ? "" : buf.toString(), new Exception("Call stack"));
            }else{
                getLogger().write(requestMessageId, buf == null ? "" : buf.toString());
            }
        }
        if(buf != null){
            buf.setLength(0);
        }
        Throwable throwable = null;
        Object ret = null;
        try{
            ret = chain.invokeNext(context);
            return ret;
        }catch(Throwable th){
            throwable = th;
            throw th;
        }finally{
            final long end = System.currentTimeMillis();
            if(isOutputResponseLog && getLogger().isWrite(responseMessageId)){
                if(buf == null){
                    buf = new StringBuilder();
                }
                if(sequenceId != null){
                    buf.append(sequenceId);
                }
                if(isOutputTargetOnResponse){
                    if(buf.length() != 0){
                        buf.append(',');
                    }
                    buf.append(targetBuf);
                }
                if(isOutputMethodOnResponse){
                    if(buf.length() != 0){
                        buf.append(',');
                    }
                    buf.append(methodBuf);
                }
                if(isOutputParameterOnResponse){
                    if(buf.length() != 0){
                        buf.append(',');
                    }
                    buf.append(parameterBuf);
                }
                if(throwable == null){
                    if(isOutputReturn){
                        if(buf.length() != 0){
                            buf.append(',');
                        }
                        if(outputReturnProperties != null && outputReturnProperties.length != 0){
                            buf.append('[');
                            for(int i = 0; i < outputReturnProperties.length; i++){
                                Object prop = null;
                                try{
                                    prop = propertyAccess.get(ret, outputReturnProperties[i]);
                                }catch(IllegalArgumentException e){
                                }catch(NoSuchPropertyException e){
                                }catch(InvocationTargetException e){
                                }
                                printObject(buf, prop);
                                if(i != outputReturnProperties.length - 1){
                                    buf.append(',');
                                }
                            }
                            buf.append(']');
                        }else{
                            printObject(buf, ret);
                        }
                    }
                }
                if(isOutputPerformance){
                    if(buf.length() != 0){
                        buf.append(',');
                    }
                    buf.append(end - start);
                }
                if(throwable != null && isOutputThrowable){
                    getLogger().write(responseMessageId, buf.toString(), throwable);
                }else{
                    getLogger().write(responseMessageId, buf.toString());
                }
            }
        }
    }
    
    private StringBuilder printObject(StringBuilder buf, Object value){
        if(value == null){
            buf.append(value);
        }else if(value.getClass().isArray()){
            buf.append('[');
            for(int i = 0, imax = Array.getLength(value); i < imax; i++){
                printObject(buf, Array.get(value, i));
                if(i != imax - 1){
                    buf.append(',');
                }
            }
            buf.append(']');
        }else if(value.getClass().isPrimitive() || value instanceof Boolean || value instanceof Number){
            buf.append(value);
        }else{
            buf.append('"').append(value).append('"');
        }
        return buf;
    }
}
