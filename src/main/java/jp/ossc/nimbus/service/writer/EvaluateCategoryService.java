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

import jp.ossc.nimbus.beans.NoSuchPropertyException;
import jp.ossc.nimbus.beans.Property;
import jp.ossc.nimbus.beans.PropertyFactory;
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
    
    // EvaluateCategoryServiceMBeanのJavaDoc
    public void setWritableConditions(String conditions[]){
        writableConditions = conditions;
    }
    
    // EvaluateCategoryServiceMBeanのJavaDoc
    public String[] getWritableConditions(){
        return writableConditions;
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
        public List properties;
        public Expression expression;
        public List keyList;
        
        public static final String DELIMITER = "@";
        
        Condition(String cond) throws Exception{
            keyList = new ArrayList();
            properties = new ArrayList();
            
            StringTokenizer token = new StringTokenizer(cond, DELIMITER, true);
            
            boolean keyFlg = false;
            
            String beforeToken = null;
            StringBuffer condBuf = new StringBuffer();
            
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
            
            if(keyList.size() == 0){
                throw new IllegalArgumentException(cond);
            }
            
            expression = ExpressionFactory.createExpression(condBuf.toString());
            evaluate("", true);
        }
        
        public boolean evaluate(Object object) throws MessageWriteException{
            return evaluate(object, false);
        }
        
        protected boolean evaluate(Object object, boolean isTest) throws MessageWriteException{
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
        }
    }
}
