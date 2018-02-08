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
package jp.ossc.nimbus.util.validator;

import java.io.*;
import java.util.*;
import java.lang.reflect.InvocationTargetException;

import jp.ossc.nimbus.beans.*;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;

/**
 * 条件バリデータ。<p>
 * The Apache Jakarta Projectの Commons Jexl(http://jakarta.apache.org/commons/jexl/)を使用した条件式で検証する。<br>
 * 
 * @author M.Takata
 */
public class ConditionValidator implements Validator, Serializable{
    
    private static final long serialVersionUID = -8050401813918879584L;
    
    private Condition condition;
    
    /**
     * 条件式を設定する。<p>
     * 条件式は、The Apache Jakarta Projectの Commons Jexl(http://jakarta.apache.org/commons/jexl/)の文法に従う。<br>
     * {@link #validate(Object obj)}の引数objを条件式内で参照するには、"value"と記述する。<br>
     * また、objのプロパティを参照する場合は、"@プロパティ名@"のように記述する。<br>
     * ここで言う、プロパティの概念は、Java Beansのプロパティの概念より広く、{@link jp.ossc.nimbus.beans.PropertyFactory PropertyFactory}の規約に従う。<br>
     * 例：value != null && @length@ &gt; 3<br>
     *
     * @param condition 条件式
     * @exception ValidateException 条件式が不正な場合
     */
    public void setCondition(String condition) throws ValidateException{
        this.condition = new Condition(condition);
    }
    
    /**
     * 条件式を取得する。<p>
     *
     * @return 条件式
     */
    public String getCondition(){
        return condition == null ? null : condition.toString();
    }
    
    /**
     * 指定されたオブジェクトが条件式に合致するかを検証する。<p>
     *
     * @param obj 検証対象のオブジェクト
     * @return 検証結果。検証成功の場合true
     * @exception ValidateException 検証に失敗した場合
     */
    public boolean validate(Object obj) throws ValidateException{
        return condition == null ? true : condition.evaluate(obj);
    }
    
    private static class Condition implements Serializable{
        
        private static final long serialVersionUID = 7720035195219210280L;
        
        private transient List properties;
        private transient Expression expression;
        private transient List keyList;
        private String condition;
        
        private static final String DELIMITER = "@";
        private static final String VALUE = "value";
        
        Condition() throws ValidateException{
            this("true");
        }
        
        Condition(String cond) throws ValidateException{
            initCondition(cond);
        }
        
        private void initCondition(String cond) throws ValidateException{
            final StringTokenizer token
                 = new StringTokenizer(cond, DELIMITER, true);
            
            boolean keyFlg = false;
            
            String beforeToken = null;
            final StringBuffer condBuf = new StringBuffer();
            
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
                        if(!beforeToken.startsWith(VALUE)){
                            throw new ValidateException(
                                "Illegal condition. " + cond
                            );
                        }
                        if(keyList == null){
                            keyList = new ArrayList();
                        }
                        final String tmpKey = "_constrainKey$" + keyList.size();
                        keyList.add(tmpKey);
                        condBuf.append(tmpKey);
                        if(properties == null){
                            properties = new ArrayList();
                        }
                        if(VALUE.equals(beforeToken)){
                            properties.add(null);
                        }else{
                            if(beforeToken.charAt(VALUE.length()) == '.'){
                                beforeToken = beforeToken.substring(VALUE.length() + 1);
                            }else{
                                beforeToken = beforeToken.substring(VALUE.length());
                            }
                            Property prop = PropertyFactory.createProperty(beforeToken);
                            prop.setIgnoreNullProperty(true);
                            properties.add(prop);
                        }
                    }else{
                        condBuf.append(str);
                    }
                }
                beforeToken = str;
            }
            
            try{
                expression = ExpressionFactory
                    .createExpression(condBuf.toString());
            }catch(Exception e){
                throw new ValidateException(e);
            }
            evaluate("", true);
            condition = cond;
        }
        
        public boolean evaluate(Object object) throws ValidateException{
            return evaluate(object, false);
        }
        
        protected boolean evaluate(Object object, boolean isTest)
         throws ValidateException{
            JexlContext jexlContext = JexlHelper.createContext();
            jexlContext.getVars().put(VALUE, object);
            if(object != null && keyList != null){
                for(int i = 0, size = keyList.size(); i < size; i++){
                    final String keyString = (String)keyList.get(i);
                    final Property property = (Property)properties.get(i);
                    Object val = null;
                    if(property == null){
                        val = object;
                    }else{
                        try{
                            val = property.getProperty(object);
                        }catch(NoSuchPropertyException e){
                        }catch(InvocationTargetException e){
                        }
                    }
                    jexlContext.getVars().put(keyString, val);                
                }
            }
            
            try{
                Object exp = expression.evaluate(jexlContext);
                if(exp instanceof Boolean){
                    return ((Boolean)exp).booleanValue();
                }else{
                    if(exp == null && isTest){
                        return true;
                    }
                    throw new ValidateException(expression.getExpression());
                }
            }catch(Exception e){
                throw new ValidateException(e);
            }
        }
        
        public String toString(){
            return condition;
        }
        
        private void readObject(ObjectInputStream in)
         throws IOException, ClassNotFoundException{
            in.defaultReadObject();
            try{
                initCondition(condition);
            }catch(Exception e){
                // 起こらないはず
            }
        }
    }
}