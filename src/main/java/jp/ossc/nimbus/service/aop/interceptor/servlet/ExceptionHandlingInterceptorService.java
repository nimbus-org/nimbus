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
package jp.ossc.nimbus.service.aop.interceptor.servlet;

import java.util.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.*;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.util.*;
import jp.ossc.nimbus.service.aop.*;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;

/**
 * ��O�����C���^�[�Z�v�^�B<p>
 * �ȉ��ɁANullPointerException�����������ꍇ�́AWARN���O���o�͂�warn.jsp�ɓ]�����AIllegalArgumentException�����������ꍇ�́AERROR���O���o�͂�error.jsp�ɓ]�����A����ȊO�̗�O�����������ꍇ�́AFATAL���O���o�͂�HTTP�X�e�[�^�X��500��Ԃ���O�����C���^�[�Z�v�^�̃T�[�r�X��`��������B<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="ExceptionHandlingInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.servlet.ExceptionHandlingInterceptorService"&gt;
 *             &lt;attribute name="ExceptionAndHandlerMapping"&gt;
 *                 java.lang.NullPointerException=#NullPointerExceptionHandler
 *                 java.lang.IllegalArgumentException=#IllegalArgumentExceptionHandler
 *             &lt;/attribute&gt;
 *             &lt;attribute name="DefaultExceptionHandlerServiceName"&gt;#DefaultExceptionHandler&lt;/attribute&gt;
 *             &lt;depends&gt;
 *                 &lt;service name="NullPointerExceptionHandler"
 *                             code="jp.ossc.nimbus.service.aop.interceptor.servlet.DefaultExceptionHandlerService"&gt;
 *                     &lt;attribute name="LogMessageCode"&gt;WARN&lt;/attribute&gt;
 *                     &lt;attribute name="ForwardPath"&gt;/warn.jsp&lt;/attribute&gt;
 *                 &lt;/service&gt;
 *             &lt;/depends&gt;
 *             &lt;depends&gt;
 *                 &lt;service name="IllegalArgumentExceptionHandler"
 *                             code="jp.ossc.nimbus.service.aop.interceptor.servlet.DefaultExceptionHandlerService"&gt;
 *                     &lt;attribute name="LogMessageCode"&gt;ERROR&lt;/attribute&gt;
 *                     &lt;attribute name="ForwardPath"&gt;/error.jsp&lt;/attribute&gt;
 *                 &lt;/service&gt;
 *             &lt;/depends&gt;
 *             &lt;depends&gt;
 *                 &lt;service name="DefaultExceptionHandler"
 *                             code="jp.ossc.nimbus.service.aop.interceptor.servlet.DefaultExceptionHandlerService"&gt;
 *                     &lt;attribute name="LogMessageCode"&gt;FATAL&lt;/attribute&gt;
 *                     &lt;attribute name="HttpResponseStatus"&gt;500&lt;/attribute&gt;
 *                 &lt;/service&gt;
 *             &lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class ExceptionHandlingInterceptorService
 extends ServletFilterInterceptorService
 implements ExceptionHandlingInterceptorServiceMBean{
    
    private static final long serialVersionUID = -5888057404214069278L;
    
    private static final String JMS_EXCEPTION_NAME = "javax.jms.JMSException";
    private static final String GET_LINKED_EXCEPTION_METHOD = "getLinkedException";
    
    private static final String EHI__00001 = "EHI__00001";
    
    protected Map exceptionAndHandlerMapping;
    protected ClassMappingTree exceptionMapForHandler;
    protected ServiceName defaultExceptionHandlerServiceName;
    protected ExceptionHandler defaultExceptionHandler;
    
    // ExceptionHandlingInterceptorServiceMBean��JavaDoc
    public void setExceptionAndHandlerMapping(Map map){
        exceptionAndHandlerMapping = map;
    }
    
    // ExceptionHandlingInterceptorServiceMBean��JavaDoc
    public Map getExceptionAndHandlerMapping(){
        return exceptionAndHandlerMapping;
    }
    
    // ExceptionHandlingInterceptorServiceMBean��JavaDoc
    public void setDefaultExceptionHandlerServiceName(ServiceName name){
        defaultExceptionHandlerServiceName = name;
    }
    
    // ExceptionHandlingInterceptorServiceMBean��JavaDoc
    public ServiceName getDefaultExceptionHandlerServiceName(){
        return defaultExceptionHandlerServiceName;
    }
    
    /**
     * �T�[�r�X�̊J�n�������s���B<p>
     *
     * @exception Exception �T�[�r�X�̊J�n�Ɏ��s�����ꍇ
     */
    public void startService() throws Exception{
        if(exceptionAndHandlerMapping != null){
            exceptionMapForHandler = new ClassMappingTree();
            final ClassLoader loader = NimbusClassLoader.getInstance();
            final ServiceNameEditor editor = new ServiceNameEditor();
            editor.setServiceManagerName(getServiceManagerName());
            final Iterator exNames
                 = exceptionAndHandlerMapping.keySet().iterator();
            while(exNames.hasNext()){
                String exName = (String)exNames.next();
                final String name
                     = (String)exceptionAndHandlerMapping.get(exName);
                String condition = null;
                final int index = exName.indexOf('(');
                if(index != -1 && exName.charAt(exName.length() - 1) == ')'){
                    condition = exName.substring(
                        index + 1,
                        exName.length() - 1
                    );
                    exName = exName.substring(0, index);
                }
                final Class clazz = Class.forName(exName, true, loader);
                editor.setAsText(name);
                final ServiceName serviceName = (ServiceName)editor.getValue();
                final ExceptionHandler handler
                     = (ExceptionHandler)ServiceManagerFactory
                        .getServiceObject(serviceName);
                if(condition == null){
                    exceptionMapForHandler.add(
                        clazz,
                        handler
                    );
                }else{
                    Condition cond = new Condition(condition, handler);
                    try{
                        cond.evaluate(
                            (Throwable)clazz.newInstance(),
                            null,
                            null,
                            true
                        );
                    }catch(InstantiationException e){
                    }catch(IllegalAccessException e){
                    }
                    exceptionMapForHandler.add(clazz, cond);
                }
            }
        }
        if(defaultExceptionHandlerServiceName != null){
            defaultExceptionHandler = (ExceptionHandler)ServiceManagerFactory
                .getServiceObject(defaultExceptionHandlerServiceName);
        }
    }
    
    /**
     * ���̃C���^�[�Z�v�^���Ăяo���A�n���h�����O�Ώۂ̗�O����������Ɨ�O�������s��JSP�Ƀt�H���[�h����B<p>
     * �T�[�r�X���J�n����Ă��Ȃ��ꍇ�́A���������Ɏ��̃C���^�[�Z�v�^���Ăяo���B<br>
     *
     * @param context �Ăяo���̃R���e�L�X�g���
     * @param chain ���̃C���^�[�Z�v�^���Ăяo�����߂̃`�F�[��
     * @return �Ăяo�����ʂ̖߂�l
     * @exception Throwable �Ăяo����ŗ�O�����������ꍇ�A�܂��͂��̃C���^�[�Z�v�^�ŔC�ӂ̗�O�����������ꍇ�B�A���A�{���Ăяo����鏈����throw���Ȃ�RuntimeException�ȊO�̗�O��throw���Ă��A�Ăяo�����ɂ͓`�d����Ȃ��B
     */
    public Object invokeFilter(
        ServletFilterInvocationContext context,
        InterceptorChain chain
    ) throws Throwable{
        if(getState() == STARTED){
            Object ret = null;
            try{
                ret = chain.invokeNext(context);
            }catch(Throwable th){
                final ServletRequest request = context.getServletRequest();
                final ServletResponse response = context.getServletResponse();
                boolean isHandled = false;
                ExceptionHandler handler = getTargetExceptionHandlerCause(
                    th,
                    request,
                    response
                );
                if(handler != null){
                    // �n���h�����O�Ώۂ̗�O
                    Throwable targetTh = getTargetException(
                        exceptionMapForHandler,
                        th,
                        request,
                        response
                    );
                    if(targetTh == null){
                        targetTh = th;
                    }
                    handler.handleException(targetTh, request, response);
                    isHandled = true;
                }
                if(!isHandled){
                    throw th;
                }
            }
            return ret;
        }else{
            return chain.invokeNext(context);
        }
    }
    
    /**
     * �w�肳�ꂽ��O�̃n���h���N���X���}�b�v���猩���ĕԂ��B<p>
     *
     * @param th ��O(�n���h�����O�Ώۂ̗�O���܂�ł���)
     * @param request ���N�G�X�g
     * @param response ���X�|���X
     * @return ��O�n���h��
     */
    protected ExceptionHandler getTargetExceptionHandlerCause(
        Throwable th,
        ServletRequest request,
        ServletResponse response
    ){
        ExceptionHandler handler = (ExceptionHandler)getTargetHandlerCause(
            exceptionMapForHandler,
            th,
            request,
            response
        );
        return handler == null ? defaultExceptionHandler : handler;
    }
    
    /**
     * �w�肳�ꂽ��O�̃n���h�����}�b�v���猩���ĕԂ��B<p>
     *
     * @param handlerMap �n���h���̃}�b�v
     * @param th ��O(�n���h�����O�Ώۂ̗�O���܂�ł���)
     * @param request ���N�G�X�g
     * @param response ���X�|���X
     * @return ��O�n���h��
     */
    protected ExceptionHandler getTargetHandlerCause(
        ClassMappingTree handlerMap,
        Throwable th,
        ServletRequest request,
        ServletResponse response
    ){
        if(handlerMap == null){
            return null;
        }
        // ��O�N���X�Ɋ֘A�t���Ă����O�n���h�����擾
        List handlers = handlerMap.getValueList(th.getClass());
        if(handlers != null){
            for(int i = 0, imax = handlers.size(); i < imax; i++){
                Object handler = handlers.get(i);
                if(handler instanceof Condition){
                    Condition condition = (Condition)handler;
                    try{
                        if(condition.evaluate(th, request, response)){
                            return condition.handler;
                        }
                    }catch(Exception e){
                        getLogger().write(EHI__00001, e);
                    }
                }else{
                    return (ExceptionHandler)handler;
                }
            }
        }
        Throwable cause = getCause(th);
        return cause == null ? null : getTargetHandlerCause(handlerMap, cause, request, response);
    }
    
    /**
     * �w�肳�ꂽ��O����A�n���h�����O�Ώۂ̗�O�����o���B<p>
     * 
     * @param handlerMap �n���h���̃}�b�v
     * @param th ��O
     * @param request ���N�G�X�g
     * @param response ���X�|���X
     * @return �n���h�����O�Ώۂ̗�O
     */
    protected Throwable getTargetException(
        ClassMappingTree handlerMap,
        Throwable th,
        ServletRequest request,
        ServletResponse response
    ) {
        if(handlerMap == null){
            return th;
        }
        // ��O�N���X�Ɋ֘A�t���Ă����O�n���h�����擾
        List handlers = handlerMap.getValueList(th.getClass());
        if(handlers != null){
            for(int i = 0, imax = handlers.size(); i < imax; i++){
                Object handler = handlers.get(i);
                if(handler instanceof Condition){
                    Condition condition = (Condition)handler;
                    try{
                        if(condition.evaluate(th, request, response)){
                            return th;
                        }
                    }catch(Exception e){
                        getLogger().write(EHI__00001, e);
                    }
                }else{
                    return th;
                }
            }
        }
        
        Throwable cause = getCause(th);
        return cause == null ? null : getTargetException(
            handlerMap,
            cause,
            request,
            response
        );
    }
    
    /**
     * �w�肳�ꂽ��O���猴�����擾����B<p>
     *
     * @param th ��O
     * @return ����
     */
    protected Throwable getCause(Throwable th){
        Throwable cause = null;
        if(th instanceof ServletException){
            // ��O��ServletException�̏ꍇ�́A���[�g�̌������擾
            cause = ((ServletException)th).getRootCause();
        }else if(th.getClass().getName().equals(JMS_EXCEPTION_NAME)){
            // ��O��JMSException�̏ꍇ�́A�����N��O���擾
            try{
                cause = (Exception)th.getClass()
                    .getMethod(GET_LINKED_EXCEPTION_METHOD, (Class[])null).invoke(th, (Object[])null);
            }catch(NoSuchMethodException e){
            }catch(IllegalAccessException e){
            }catch(InvocationTargetException e){
            }
        }else{
            cause = th.getCause();
        }
        return cause == th ? null : cause;
    }
    
    protected static class Condition implements Serializable{
        
        private static final long serialVersionUID = 1115982642791231865L;
        
        public static final String EXCEPTION_KEY = "exception";
        public static final String REQUEST_KEY = "request";
        public static final String RESPONSE_KEY = "response";
        
        public ExceptionHandler handler;
        
        protected transient List properties;
        protected transient Expression expression;
        protected transient List keyList;
        public String condition;
        
        protected static final String DELIMITER = "@";
        
        public Condition(String cond, ExceptionHandler handler) throws Exception{
            initCondition(cond);
            condition = cond;
            this.handler = handler;
        }
        
        protected void initCondition(String cond) throws Exception{
            keyList = new ArrayList();
            properties = new ArrayList();
            
            StringTokenizer token = new StringTokenizer(cond, DELIMITER, true);
            
            boolean keyFlg = false;
            
            String beforeToken = null;
            StringBuilder condBuf = new StringBuilder();
            
            while(token.hasMoreTokens()){
                String str = token.nextToken();
                if(!keyFlg){
                    if(DELIMITER.equals(str)){
                        keyFlg = true;
                    }else{
                        condBuf.append(str);
                    }
                }else if(DELIMITER.equals(str)){
                    keyFlg = false;
                    if(beforeToken != null){
                        final String tmpKey = "_conditionKey$" + keyList.size();
                         keyList.add(tmpKey);
                        condBuf.append(tmpKey);
                        Property prop = PropertyFactory.createProperty(beforeToken);
                        prop.setIgnoreNullProperty(true);
                        properties.add(prop);
                    }else{
                        condBuf.append(str);
                    }
                }
                beforeToken = str;    
            }
            expression = ExpressionFactory.createExpression(condBuf.toString());
        }
        
        public boolean evaluate(
            Throwable th,
            ServletRequest request,
            ServletResponse response
        ) throws Exception{
            return evaluate(th, request, response, false);
        }
        
        public boolean evaluate(
            Throwable th,
            ServletRequest request,
            ServletResponse response,
            boolean isTest
        ) throws Exception{
            JexlContext jexlContext = JexlHelper.createContext();
            Map vars = jexlContext.getVars();
            vars.put(EXCEPTION_KEY, th);
            vars.put(REQUEST_KEY, request);
            vars.put(RESPONSE_KEY, response);
            for(int i = 0, size = keyList.size(); i < size; i++){
                final String keyString = (String)keyList.get(i);
                final Property property = (Property)properties.get(i);
                Object val = null;
                try{
                    val = property.getProperty(vars);
                }catch(NullNestPropertyException e){
                }catch(NullKeyPropertyException e){
                }catch(NullIndexPropertyException e){
                }catch(InvocationTargetException e){
                    Throwable th2 = e.getTargetException();
                    if(th2 instanceof Exception){
                        throw (Exception)th2;
                    }else if(th2 instanceof Error){
                        throw (Error)th2;
                    }else{
                        throw e;
                    }
                }
                jexlContext.getVars().put(keyString, val);                
            }
            
            Object exp = expression.evaluate(jexlContext);
            if(exp instanceof Boolean){
                return ((Boolean)exp).booleanValue();
            }else{
                if(exp == null && isTest){
                    return true;
                }
                throw new IllegalArgumentException(expression.getExpression());
            }
        }
        
        private void readObject(ObjectInputStream in)
         throws IOException, ClassNotFoundException{
            in.defaultReadObject();
            try{
                initCondition(condition);
            }catch(Exception e){
                // �N����Ȃ��͂�
            }
        }
    }
}