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
package jp.ossc.nimbus.service.writer;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.HashMap;

import jp.ossc.nimbus.beans.NoSuchPropertyException;
import jp.ossc.nimbus.beans.Property;
import jp.ossc.nimbus.beans.PropertyFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.aop.interceptor.ThreadContextKey;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.service.codemaster.CodeMasterFinder;
import jp.ossc.nimbus.service.interpreter.Interpreter;
import jp.ossc.nimbus.service.interpreter.CompiledInterpreter;
import jp.ossc.nimbus.service.interpreter.EvaluateException;
import jp.ossc.nimbus.service.writer.SimpleCategoryService;
import jp.ossc.nimbus.service.writer.MessageWriteException;
import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;

/**
 * 条件評価カテゴリサービス
 * <p>
 * 設定した条件を満たす場合に出力するサービス
 * <p>
 * <b>条件設定</b><br>
 * 条件は、Javaの条件式で記入できます。
 * あるプロパティ値を条件に使用したい場合は、プロパティを@～@で囲みます<br>
 * 例）
 * <li>200以上の場合　&gt;= </li>
 * <pre>@InfoAnalysis.JournalRecords.number@ &gt;= 200</pre>
 * <li>299以下の場合 &lt;= </li>
 * <pre>@InfoAnalysis.JournalRecords.number@ &lt;= 299</pre>
 * <li>not nullの場合</li>
 * <pre>@InfoAnalysis.JournalRecords.str[0]@ != <code>null</code></pre>
 * <li>"F"と同じ場合 文字列比較する際は"=="を使用</li>
 * <pre>@InfoAnalysis.JournalRecords.str@ == "F"</pre>
 * 
 * @author M.Kameda
 */
public class EvaluateCategoryService extends SimpleCategoryService
 implements EvaluateCategoryServiceMBean{
    
    private static final long serialVersionUID = -910016006137008431L;
    
    private String[] writableConditions;
    
    private List conditions;
    
    private boolean isTestOnStart = true;
    
    private ServiceName interpreterServiceName;
    private Interpreter interpreter;
    
    private ServiceName threadContextServiceName;
    private Context threadContext;
    private ServiceName codeMasterFinderServiceName;
    private CodeMasterFinder codeMasterFinder;
    private String codeMasterName;
    
    // EvaluateCategoryServiceMBeanのJavaDoc
    public void setWritableConditions(String conditions[]){
        writableConditions = conditions;
    }
    
    // EvaluateCategoryServiceMBeanのJavaDoc
    public String[] getWritableConditions(){
        return writableConditions;
    }
    
    public void setTestOnStart(boolean isTest){
        isTestOnStart = isTest;
    }
    public boolean isTestOnStart(){
        return isTestOnStart;
    }
    
    public void setInterpreterServiceName(ServiceName name){
        interpreterServiceName = name;
    }
    public ServiceName getInterpreterServiceName(){
        return interpreterServiceName;
    }
    
    public void setThreadContextServiceName(ServiceName name){
        threadContextServiceName = name;
    }
    public ServiceName getThreadContextServiceName(){
        return threadContextServiceName;
    }
    
    public void setCodeMasterFinderServiceName(ServiceName name){
        codeMasterFinderServiceName = name;
    }
    public ServiceName getCodeMasterFinderServiceName(){
        return codeMasterFinderServiceName;
    }
    
    public void setCodeMasterName(String name){
        codeMasterName = name;
    }
    public String getCodeMasterName(){
        return codeMasterName;
    }
    
    public void setInterpreter(Interpreter interpreter){
        this.interpreter = interpreter;
    }
    public void setThreadContext(Context threadContext){
        this.threadContext = threadContext;
    }
    public void setCodeMasterFinder(CodeMasterFinder codeMasterFinder){
        this.codeMasterFinder = codeMasterFinder;
    }
    
    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception{
        super.createService();
        conditions = new ArrayList();
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        super.startService();
        if(interpreterServiceName != null){
            interpreter = (Interpreter)ServiceManagerFactory.getServiceObject(interpreterServiceName);
        }
        if(threadContextServiceName != null){
            threadContext = (Context)ServiceManagerFactory.getServiceObject(threadContextServiceName);
        }
        if(codeMasterFinderServiceName != null){
            codeMasterFinder = (CodeMasterFinder)ServiceManagerFactory.getServiceObject(codeMasterFinderServiceName);
        }
        
        if(writableConditions != null){
            for(int i = 0; i < writableConditions.length; i++){
                conditions.add(new Condition(writableConditions[i]));
            }
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        conditions.clear();
        super.stopService();
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception サービスの破棄処理に失敗した場合
     */
    public void destroyService() throws Exception{
        conditions = null;
        super.destroyService();
    }
    
    /**
     * 指定された出力要素を評価して、必要ならばこのカテゴリに出力する。<p>
     *
     * @param elements WritableRecordFactoryに渡す出力要素
     * @exception MessageWriteException 出力に失敗した場合
     */
    public void write(Object elements) throws MessageWriteException {
        if(conditions.size() != 0){
            for(int i = 0, imax = conditions.size(); i < imax; i++){
                Condition cond = (Condition)conditions.get(i);
                if(!cond.evaluate(elements)){
                    return;
                }
            }
        }
        
        super.write(elements);
    }
    
    private class Condition{
        private List properties;
        private String expressionStr;
        private Expression expression;
        private List keyList;
        private CompiledInterpreter compiledInterpreter;
        
        public static final String DELIMITER = "@";
        
        Condition(String cond) throws Exception{
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
                }else{
                    if(DELIMITER.equals(str)){
                        keyFlg = false;
                        if(beforeToken != null){
                            final String tmpKey = "_evaluatectgyserv" + keyList.size();
                             keyList.add(tmpKey);
                            condBuf.append(tmpKey);
                            Property prop = PropertyFactory.createProperty(beforeToken);
                            prop.setIgnoreNullProperty(true);
                            properties.add(prop);
                        }else{
                            condBuf.append(str);
                        }
                    }else{
                        //condBuf.append(str);
                    }
                }
                beforeToken = str;
            }
            expressionStr = condBuf.toString();
            if(interpreter == null){
                expression = ExpressionFactory.createExpression(expressionStr);
            }else if(interpreter.isCompilable()){
                compiledInterpreter = interpreter.compile(expressionStr);
            }
            if(isTestOnStart){
                evaluate("", true);
            }
        }
        
        public boolean evaluate(Object object) throws MessageWriteException{
            return evaluate(object, false);
        }
        
        protected boolean evaluate(Object object, boolean isTest) throws MessageWriteException{
            if(interpreter == null){
                JexlContext jexlContext = JexlHelper.createContext();
                
                for(int i = 0, size = keyList.size(); i < size; i++){
                    final String keyString = (String)keyList.get(i);
                    final Property property = (Property)properties.get(i);
                    Object val = null;
                    try{
                        val = property.getProperty(object);
                    }catch(NoSuchPropertyException e){
                    }catch(InvocationTargetException e){
                    }
                    jexlContext.getVars().put(keyString, val);
                }
                jexlContext.getVars().put("value", object);
                Map codeMasterMap = null;
                if(threadContext != null){
                    codeMasterMap = (Map)threadContext.get(ThreadContextKey.CODEMASTER);
                }else if(codeMasterFinder != null){
                    codeMasterMap = codeMasterFinder.getCodeMasters();
                }
                Object codeMaster = codeMasterMap;
                if(codeMasterMap != null && codeMasterName != null){
                    codeMaster = codeMasterMap.get(codeMasterName);
                }
                jexlContext.getVars().put("master", codeMaster);
                try{
                    Object exp = expression.evaluate(jexlContext);
                    if(exp instanceof Boolean){
                        return ((Boolean)exp).booleanValue();
                    }else{
                        if(exp == null && isTest){
                            return true;
                        }
                        throw new MessageWriteException(expression.getExpression());
                    }
                }catch(Exception e){
                    throw new MessageWriteException(e);
                }
            }else{
                Map params = new HashMap();
                for(int i = 0, size = keyList.size(); i < size; i++){
                    final String keyString = (String)keyList.get(i);
                    final Property property = (Property)properties.get(i);
                    Object val = null;
                    try{
                        val = property.getProperty(object);
                    }catch(NoSuchPropertyException e){
                    }catch(InvocationTargetException e){
                    }
                    params.put(keyString, val);
                }
                params.put("value", object);
                Map codeMasterMap = null;
                if(threadContext != null){
                    codeMasterMap = (Map)threadContext.get(ThreadContextKey.CODEMASTER);
                }else if(codeMasterFinder != null){
                    codeMasterMap = codeMasterFinder.getCodeMasters();
                }
                Object codeMaster = codeMasterMap;
                if(codeMasterMap != null && codeMasterName != null){
                    codeMaster = codeMasterMap.get(codeMasterName);
                }
                params.put("master", codeMaster);
                try{
                    Object ret = null;
                    if(compiledInterpreter == null){
                        ret = compiledInterpreter.evaluate(params);
                    }else{
                        ret = interpreter.evaluate(expressionStr, params);
                    }
                    if(ret instanceof Boolean){
                        return ((Boolean)ret).booleanValue();
                    }else{
                        if(ret == null && isTest){
                            return true;
                        }
                        throw new MessageWriteException(expressionStr);
                    }
                }catch(EvaluateException e){
                    throw new MessageWriteException(e);
                }
            }
        }
    }
}
