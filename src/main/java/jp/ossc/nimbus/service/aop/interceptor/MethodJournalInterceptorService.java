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

import java.lang.reflect.*;
import java.util.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.context.*;
import jp.ossc.nimbus.service.journal.*;
import jp.ossc.nimbus.service.journal.editor.*;
import jp.ossc.nimbus.service.journal.editorfinder.*;

/**
 * メソッドジャーナルインターセプタ。<p>
 * メソッド呼び出しのジャーナルを取得するインターセプタである。ジャーナルの出力は、別途ジャーナルサービスの定義が必要である。<br>
 * このインターセプタで出力されるジャーナル情報は、メソッド呼び出し情報（{@link MethodCallJournalData}）、メソッド戻り値情報（{@link MethodReturnJournalData}）、メソッド例外情報（{@link MethodThrowJournalData}）である。<br>
 * 以下に、メソッドの呼び出しジャーナルをコンソールに出力するインターセプタのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="MethodJournalInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.MethodJournalInterceptorService"&gt;
 *             &lt;attribute name="JournalServiceName"&gt;#Journal&lt;/attribute&gt;
 *             &lt;depends&gt;Journal&lt;/depends&gt;
 *         &lt;/service&gt;
 * &lt;!-- 以下はジャーナルサービス定義 --&gt;
 *         &lt;service name="Journal"
 *                  code="jp.ossc.nimbus.service.journal.ThreadManagedJournalService"&gt;
 *             &lt;attribute name="EditorFinderName"&gt;#JournalEditorFinder&lt;/attribute&gt;
 *             &lt;attribute name="WritableElementKey"&gt;Journal for Sample&lt;/attribute&gt;
 *             &lt;attribute name="CategoryServiceNames"&gt;#JournalCategory&lt;/attribute&gt;
 *             &lt;depends&gt;JournalEditorFinder&lt;/depends&gt;
 *             &lt;depends&gt;JournalCategory&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="JournalCategory"
 *                  code="jp.ossc.nimbus.service.writer.SimpleCategoryService"&gt;
 *             &lt;attribute name="MessageWriterServiceName"&gt;#JournalWriter&lt;/attribute&gt;
 *             &lt;attribute name="WritableRecordFactoryServiceName"&gt;#JournalWritableRecordFactory&lt;/attribute&gt;
 *             &lt;attribute name="CategoryServiceNames"&gt;#JournalCategory&lt;/attribute&gt;
 *             &lt;depends&gt;JournalWriter&lt;/depends&gt;
 *             &lt;depends&gt;JournalWritableRecordFactory&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="JournalWritableRecordFactory"
 *                  code="jp.ossc.nimbus.service.writer.WritableRecordFactoryService"&gt;
 *             &lt;attribute name="Format"&gt;%Journal for Sample%&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="JournalWriter"
 *                  code="jp.ossc.nimbus.service.writer.ConsoleWriterService"/&gt;
 *         
 *         &lt;service name="JournalEditorFinder"
 *                  code="jp.ossc.nimbus.service.journal.editorfinder.ObjectMappedEditorFinderService"&gt;
 *             &lt;attribute name="EditorProperties"&gt;
 *                 java.lang.Object=#ObjectJournalEditor
 *                 java.lang.Class=#ClassJournalEditor
 *                 java.util.Date=#DateJournalEditor
 *                 jp.ossc.nimbus.service.journal.RequestJournal=#RequestJournalEditor
 *                 jp.ossc.nimbus.service.journal.editor.MethodJournalData=#MethodJournalEditor
 *                 jp.ossc.nimbus.service.journal.editor.MethodCallJournalData=#MethodCallJournalEditor
 *                 jp.ossc.nimbus.service.journal.editor.MethodReturnJournalData=#MethodReturnJournalEditor
 *             &lt;/attribute&gt;
 *             &lt;depends&gt;ObjectJournalEditor&lt;/depends&gt;
 *             &lt;depends&gt;ClassJournalEditor&lt;/depends&gt;
 *             &lt;depends&gt;DateJournalEditor&lt;/depends&gt;
 *             &lt;depends&gt;RequestJournalEditor&lt;/depends&gt;
 *             &lt;depends&gt;MethodJournalEditor&lt;/depends&gt;
 *             &lt;depends&gt;MethodCallJournalEditor&lt;/depends&gt;
 *             &lt;depends&gt;MethodReturnJournalEditor&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="ObjectJournalEditor"
 *                  code="jp.ossc.nimbus.service.journal.editor.ObjectJournalEditorService"/&gt;
 *         
 *         &lt;service name="ClassJournalEditor"
 *                  code="jp.ossc.nimbus.service.journal.editor.ClassJournalEditorService"&gt;
 *             &lt;attribute name="ShortClassName"&gt;true&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="DateJournalEditor"
 *                  code="jp.ossc.nimbus.service.journal.editor.DateJournalEditorService"&gt;
 *             &lt;attribute name="Format"&gt;yyyy/MM/dd HH:mm:ss.SSS&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="RequestJournalEditor"
 *                  code="jp.ossc.nimbus.service.journal.editor.SimpleRequestJournalEditorService"/&gt;
 *         
 *         &lt;service name="MethodJournalEditor"
 *                  code="jp.ossc.nimbus.service.journal.editor.MethodJournalEditorService"/&gt;
 *         
 *         &lt;service name="MethodCallJournalEditor"
 *                  code="jp.ossc.nimbus.service.journal.editor.MethodCallJournalEditorService"/&gt;
 *         
 *         &lt;service name="MethodReturnJournalEditor"
 *                  code="jp.ossc.nimbus.service.journal.editor.MethodReturnJournalEditorService"/&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 * @see Journal
 * @see EditorFinder
 * @see Context
 */
public class MethodJournalInterceptorService extends ServiceBase
 implements Interceptor, MethodJournalInterceptorServiceMBean{
    
    private static final long serialVersionUID = 6121765320688713719L;
    
    private ServiceName threadContextName;
    private Context threadContext;
    
    private ServiceName journalName;
    private Journal journal;
    
    private ServiceName requestEditorFinderName;
    private ServiceName methodCallEditorFinderName;
    private ServiceName methodReturnEditorFinderName;
    private EditorFinder requestEditorFinder;
    private EditorFinder methodCallEditorFinder;
    private EditorFinder methodReturnEditorFinder;
    
    private String requestJournalKey = DEFAULT_REQUEST_JOURNAL_KEY;
    private String methodCallJournalKey = DEFAULT_METHOD_CALL_JOURNAL_KEY;
    private String methodReturnJournalKey = DEFAULT_METHOD_RETURN_JOURNAL_KEY;
    
    private String requestIdKey = ThreadContextKey.REQUEST_ID;
    private boolean isEnabled = true;
    private boolean isBushingCallBlock = false;
    private Map contextJournalMap;
    private Map invocationContextJournalMap;
    
    protected ThreadLocal callStack;
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public void setRequestIdKey(String key){
        requestIdKey = key;
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public String getRequestIdKey(){
        return requestIdKey;
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public void setThreadContextServiceName(ServiceName name){
        threadContextName = name;
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public ServiceName getThreadContextServiceName(){
        return threadContextName;
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public void setJournalServiceName(ServiceName name){
        journalName = name;
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public ServiceName getJournalServiceName(){
        return journalName;
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public void setRequestEditorFinderServiceName(ServiceName name){
        requestEditorFinderName = name;
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public ServiceName getRequestEditorFinderServiceName(){
        return requestEditorFinderName;
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public void setMethodCallEditorFinderServiceName(ServiceName name){
        methodCallEditorFinderName = name;
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public ServiceName getMethodCallEditorFinderServiceName(){
        return methodCallEditorFinderName;
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public void setMethodReturnEditorFinderServiceName(ServiceName name){
        methodReturnEditorFinderName = name;
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public ServiceName getMethodReturnEditorFinderServiceName(){
        return methodReturnEditorFinderName;
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public void setRequestJournalKey(String key){
        requestJournalKey = key;
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public String getRequestJournalKey(){
        return requestJournalKey;
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public void setMethodCallJournalKey(String key){
        methodCallJournalKey = key;
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public String getMethodCallJournalKey(){
        return methodCallJournalKey;
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public void setMethodReturnJournalKey(String key){
        methodReturnJournalKey = key;
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public String getMethodReturnJournalKey(){
        return methodReturnJournalKey;
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public void setEnabled(boolean enable){
        isEnabled = enable;
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public boolean isEnabled(){
        return isEnabled;
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public void setBushingCallBlock(boolean isBlock){
        isBushingCallBlock = isBlock;
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public boolean isBushingCallBlock(){
        return isBushingCallBlock;
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public void setContextJournalMapping(String contextKey, String journalKey){
        contextJournalMap.put(contextKey, journalKey);
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public String getContextJournalMapping(String contextKey){
        return (String)contextJournalMap.get(contextKey);
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public Map getContextJournalMap(){
        return contextJournalMap;
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public void setInvocationContextJournalMapping(String attributeName, String journalKey){
        invocationContextJournalMap.put(attributeName, journalKey);
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public String getInvocationContextJournalMapping(String attributeName){
        return (String)invocationContextJournalMap.get(attributeName);
    }
    
    // MethodJournalInterceptorServiceMBeanのJavaDoc
    public Map getInvocationContextJournalMap(){
        return invocationContextJournalMap;
    }
    
    /**
     * ジャーナルを出力する{@link jp.ossc.nimbus.service.journal.Journal Journal}を設定する。<p>
     *
     * @param journal Journal
     */
    public void setJournal(Journal journal) {
        this.journal = journal;
    }
    
    /**
     * メソッド呼び出しのジャーナル編集に使用する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}を設定する。<p>
     *
     * @param editorFinder EditorFinder
     */
    public void setMethodCallEditorFinder(EditorFinder editorFinder) {
        methodCallEditorFinder = editorFinder;
    }
    
    /**
     * メソッド戻りのジャーナル編集に使用する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}を設定する。<p>
     *
     * @param editorFinder EditorFinder
     */
    public void setMethodReturnEditorFinder(EditorFinder editorFinder) {
        methodReturnEditorFinder = editorFinder;
    }
    
    /**
     * ジャーナル開始のジャーナル編集に使用する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}を設定する。<p>
     *
     * @param editorFinder EditorFinder
     */
    public void setRequestEditorFinder(EditorFinder editorFinder) {
        requestEditorFinder = editorFinder;
    }
    
    /**
     * リクエストIDを取得する{@link jp.ossc.nimbus.service.context.Context}を設定する。<p>
     *
     * @param context Context
     */
    public void setThreadContext(Context context) {
        threadContext = context;
    }
    
    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception 生成処理に失敗した場合
     */
    public void createService() throws Exception{
        contextJournalMap = new HashMap();
        invocationContextJournalMap = new HashMap();
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception 指定された{@link Journal}、及び{@link EditorFinder}、{@link Context}サービスが見つからない場合
     */
    public void startService() throws Exception{
        if(journalName != null){
            journal = (Journal)ServiceManagerFactory.getServiceObject(journalName);
        }
        if(requestEditorFinderName != null){
            requestEditorFinder = (EditorFinder)ServiceManagerFactory
                .getServiceObject(requestEditorFinderName);
        }
        if(methodCallEditorFinderName != null){
            methodCallEditorFinder = (EditorFinder)ServiceManagerFactory
                .getServiceObject(methodCallEditorFinderName);
        }
        if(methodReturnEditorFinderName != null){
            methodReturnEditorFinder = (EditorFinder)ServiceManagerFactory
                .getServiceObject(methodReturnEditorFinderName);
        }
        if(threadContextName != null){
            threadContext = (Context)ServiceManagerFactory
                .getServiceObject(threadContextName);
        }
        if(isBushingCallBlock){
            callStack = new ThreadLocal(){
                protected Object initialValue(){
                    return new CallStack();
                }
            };
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception 停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        callStack = null;
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception 破棄処理に失敗した場合
     */
    public void destroyService(){
        journal = null;
        requestEditorFinder = null;
        methodCallEditorFinder = null;
        methodReturnEditorFinder = null;
        contextJournalMap = null;
        invocationContextJournalMap = null;
    }
    
    /**
     * メソッド呼び出し開始のジャーナルを出力して、次のインターセプタを呼び出し、戻ってきたところで、メソッド呼び出し終了のジャーナルを出力する。<p>
     * サービスが開始されていない場合は、ジャーナル出力を行わずに次のインターセプタを呼び出す。<br>
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
        final MethodInvocationContext ctx = (MethodInvocationContext)context;
        
        if(getState() == STARTED && isEnabled()
            && (callStack == null
                 || ((CallStack)callStack.get()).stackIndex == 0)){
            Object ret = null;
            try{
                preNext(ctx);
                if(callStack != null){
                    ((CallStack)callStack.get()).stackIndex++;
                }
                ret = chain.invokeNext(ctx);
                postNext(ctx, ret);
            }catch(RuntimeException e){
                throw throwRuntimeException(ctx, e);
            }catch(Exception e){
                throw throwException(ctx, e);
            }catch(Error e){
                throw throwError(ctx, e);
            }finally{
                if(callStack != null){
                    ((CallStack)callStack.get()).stackIndex--;
                }
                finallyNext(ctx, ret);
            }
            return ret;
        }else{
            return chain.invokeNext(ctx);
        }
    }
    
    /**
     * 次のインターセプタを呼び出す前処理を行う。<p>
     * ジャーナルレコードを開始する。また、{@link #setThreadContextServiceName(ServiceName)}で、{@link Context}サービスが設定されている場合は、{@link #setRequestIdKey(String)}で設定されたキーでContextサービスからリクエストIDを取得して、ジャーナルレコードに設定（{@link Journal#setRequestId(String)}）する。<br>
     * {@link #setMethodCallJournalKey(String)}で設定されたキーで、{@link MethodCallJournalData}をジャーナルに出力（{@link Journal#addInfo(String, Object)}）する。<br>
     *
     * @param context 呼び出しのコンテキスト情報
     * @exception Throwable 前処理に失敗した場合
     */
    protected void preNext(MethodInvocationContext context) throws Throwable{
        if(journal == null){
            return;
        }
        journal.startJournal(requestJournalKey, requestEditorFinder);
        if(threadContext != null && requestIdKey != null){
            journal.setRequestId((String)threadContext.get(requestIdKey));
        }
        if(threadContext != null && contextJournalMap.size() != 0){
            Iterator entries = contextJournalMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                journal.addInfo((String)entry.getValue(), threadContext.get((String)entry.getKey()));
            }
        }
        if(invocationContextJournalMap.size() != 0){
            Iterator entries = invocationContextJournalMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                journal.addInfo((String)entry.getValue(), context.getAttribute((String)entry.getKey()));
            }
        }
        
        final Method method = context.getTargetMethod();
        final MethodCallJournalData data = new MethodCallJournalData(
            context.getTargetObject(),
            method.getDeclaringClass(),
            method.getName(),
            method.getParameterTypes(),
            context.getParameters()
        );
        journal.addInfo(methodCallJournalKey, data, methodCallEditorFinder);
    }
    
    /**
     * 次のインターセプタを呼び出した後処理を行う。<p>
     * {@link #setMethodReturnJournalKey(String)}で設定されたキーで、{@link MethodReturnJournalData}をジャーナルに出力（{@link Journal#addInfo(String, Object)}）する。<br>
     *
     * @param context 呼び出しのコンテキスト情報
     * @param ret 呼び出しの戻り値
     * @exception Throwable 後処理に失敗した場合
     */
    protected void postNext(MethodInvocationContext context, Object ret)
     throws Throwable{
        if(journal == null){
            return;
        }
        
        final Method method = context.getTargetMethod();
        final MethodReturnJournalData data = new MethodReturnJournalData(
            context.getTargetObject(),
            method.getDeclaringClass(),
            method.getName(),
            method.getParameterTypes(),
            ret
        );
        journal.addInfo(methodReturnJournalKey, data, methodReturnEditorFinder);
    }
    
    /**
     * 次のインターセプタを呼び出した時にRuntimeExceptionが発生した場合の後処理を行う。<p>
     * {@link #setMethodReturnJournalKey(String)}で設定されたキーで、{@link MethodThrowJournalData}をジャーナルに出力（{@link Journal#addInfo(String, Object)}）する。<br>
     *
     * @param context 呼び出しのコンテキスト情報
     * @param e 呼び出し時に発生したRuntimeException
     * @return 引数で指定されたRuntimeException
     * @exception Throwable 後処理に失敗した場合
     */
    protected RuntimeException throwRuntimeException(
        MethodInvocationContext context,
        RuntimeException e
    ) throws Throwable{
        if(journal == null){
            return e;
        }
        final Method method = context.getTargetMethod();
        final MethodThrowJournalData data = new MethodThrowJournalData(
            context.getTargetObject(),
            method.getDeclaringClass(),
            method.getName(),
            method.getParameterTypes(),
            e
        );
        journal.addInfo(methodReturnJournalKey, data, methodReturnEditorFinder);
        return e;
    }
    
    /**
     * 次のインターセプタを呼び出した時にRuntimeException以外のExceptionが発生した場合の後処理を行う。<p>
     * {@link #setMethodReturnJournalKey(String)}で設定されたキーで、{@link MethodThrowJournalData}をジャーナルに出力（{@link Journal#addInfo(String, Object)}）する。<br>
     *
     * @param context 呼び出しのコンテキスト情報
     * @param e 呼び出し時に発生したException
     * @return 引数で指定されたException
     * @exception Throwable 後処理に失敗した場合
     */
    protected Exception throwException(
        MethodInvocationContext context,
        Exception e
    ) throws Throwable{
        if(journal == null){
            return e;
        }
        final Method method = context.getTargetMethod();
        final MethodThrowJournalData data = new MethodThrowJournalData(
            context.getTargetObject(),
            method.getDeclaringClass(),
            method.getName(),
            method.getParameterTypes(),
            e
        );
        journal.addInfo(methodReturnJournalKey, data, methodReturnEditorFinder);
        return e;
    }
    
    /**
     * 次のインターセプタを呼び出した時にErrorが発生した場合の後処理を行う。<p>
     * {@link #setMethodReturnJournalKey(String)}で設定されたキーで、{@link MethodThrowJournalData}をジャーナルに出力（{@link Journal#addInfo(String, Object)}）する。<br>
     *
     * @param context 呼び出しのコンテキスト情報
     * @param error 呼び出し時に発生したError
     * @return 引数で指定されたError
     * @exception Throwable 後処理に失敗した場合
     */
    protected Error throwError(
        MethodInvocationContext context,
        Error error
    ) throws Throwable{
        if(journal == null){
            return error;
        }
        final Method method = context.getTargetMethod();
        final MethodThrowJournalData data = new MethodThrowJournalData(
            context.getTargetObject(),
            method.getDeclaringClass(),
            method.getName(),
            method.getParameterTypes(),
            error
        );
        journal.addInfo(methodReturnJournalKey, data, methodReturnEditorFinder);
        return error;
    }
    
    /**
     * 次のインターセプタを呼び出した後のfinally節での処理を行う。<p>
     * ジャーナルレコードを終了する。<br>
     *
     * @param context 呼び出しのコンテキスト情報
     * @param ret 呼び出しの戻り値
     * @exception Throwable 後処理に失敗した場合
     */
    protected void finallyNext(MethodInvocationContext context, Object ret)
     throws Throwable{
        if(journal == null){
            return;
        }
        
        journal.endJournal();
    }
    
    protected static class CallStack{
        public int stackIndex;
    }
}