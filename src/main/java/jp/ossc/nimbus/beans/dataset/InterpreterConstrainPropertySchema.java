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
package jp.ossc.nimbus.beans.dataset;

import java.util.*;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.interpreter.*;

/**
 * Interpreter制約プロパティスキーマ実装クラス。<p>
 * このクラスには、プロパティのスキーマ情報として、以下の情報が定義できる。<br>
 * <ul>
 *   <li>名前</li>
 *   <li>型</li>
 *   <li>入力変換種類</li>
 *   <li>出力変換種類</li>
 *   <li>制約</li>
 *   <li>主キーフラグ</li>
 * </ul>
 * 制約に、{@link Interpreter}サービス名{制約式}を指定する事で、Interpreterを使用して制約の検証を行う以外は、{@link DefaultPropertySchema}の仕様に従う。<br>
 * 
 * @author M.Takata
 */
public class InterpreterConstrainPropertySchema extends DefaultPropertySchema{
    
    /**
     * {@link Interpreter}サービスのサービス名。<p>
     */
    protected ServiceName interpreterServiceName;
    
    /**
     * コンパイル済みの{@link CompiledInterpreter}。<p>
     */
    protected CompiledInterpreter compiledInterpreter;
    
    /**
     * 制約式中のキーのリスト。<p>
     */
    protected final List keyList = new ArrayList();
    
    /**
     * 制約式中のキーのプロパティのリスト。<p>
     */
    protected final List properties = new ArrayList();
    
    /**
     * 制約式。<p>
     */
    protected String constrain;
    
    /**
     * 空のプロパティスキーマを生成する。<p>
     */
    public InterpreterConstrainPropertySchema(){
    }
    
    /**
     * プロパティスキーマを生成する。<p>
     *
     * @param schema プロパティのスキーマ定義
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public InterpreterConstrainPropertySchema(String schema) throws PropertySchemaDefineException{
        super(schema);
    }
    
    /**
     * 制約を取得する。<p>
     *
     * @return 制約
     */
    public String getConstrain(){
        return interpreterServiceName == null
             ? null : interpreterServiceName.toString();
    }
    
    /**
     * プロパティスキーマの制約の項目をパースする。<p>
     *
     * @param schema プロパティスキーマ全体
     * @param val スキーマ項目
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    protected void parseConstrain(String schema, String val)
     throws PropertySchemaDefineException{
        if(val != null && val.length() != 0){
            final ServiceNameEditor serviceNameEditor
                 = new ServiceNameEditor();
            final int startIndex = val.indexOf('{');
            if(startIndex > 0 && val.charAt(val.length() - 1) == '}'){
                try{
                    serviceNameEditor.setAsText(val.substring(0, startIndex));
                }catch(IllegalArgumentException e){
                    throw new PropertySchemaDefineException(
                        schema,
                        "Constrain is illegal.",
                        e
                    );
                }
                interpreterServiceName = (ServiceName)serviceNameEditor.getValue();
                constrain = val.substring(startIndex + 1, val.length() - 1);
                if(constrain.indexOf('@') != -1){
                    StringTokenizer token = new StringTokenizer(constrain, Constrain.CONSTRAIN_DELIMITER, true);
                    boolean keyFlg = false;
                    String beforeToken = null;
                    StringBuilder buf = new StringBuilder();
                    while(token.hasMoreTokens()){
                        String str = token.nextToken();
                        if(!keyFlg){
                            if(Constrain.CONSTRAIN_DELIMITER.equals(str)){
                                keyFlg = true;
                            }else{
                                buf.append(str);
                            }
                        }else if(Constrain.CONSTRAIN_DELIMITER.equals(str)){
                            keyFlg = false;
                            if(beforeToken != null){
                                final String tmpKey = "_constrainKey" + keyList.size();
                                keyList.add(tmpKey);
                                buf.append(tmpKey);
                                if(!beforeToken.startsWith(Constrain.CONSTRAIN_TARGET_KEY)){
                                    throw new IllegalArgumentException(constrain);
                                }
                                if(Constrain.CONSTRAIN_TARGET_KEY.equals(beforeToken)){
                                    properties.add(null);
                                }else{
                                    if(beforeToken.charAt(Constrain.CONSTRAIN_TARGET_KEY.length()) == '.'){
                                        beforeToken = beforeToken.substring(Constrain.CONSTRAIN_TARGET_KEY.length() + 1);
                                    }else{
                                        beforeToken = beforeToken.substring(Constrain.CONSTRAIN_TARGET_KEY.length());
                                    }
                                    Property prop = PropertyFactory.createProperty(beforeToken);
                                    prop.setIgnoreNullProperty(true);
                                    properties.add(prop);
                                }
                            }else{
                                buf.append(str);
                            }
                        }
                        beforeToken = str;
                    }
                    constrain = buf.toString();
                }
            }else{
                throw new PropertySchemaDefineException(
                    schema,
                    "Constrain is illegal."
                );
            }
        }
    }
    
    // PropertySchemaのJavaDoc
    public boolean validate(Object val) throws PropertyValidateException{
        if(interpreterServiceName == null){
            return true;
        }
        Map params = new HashMap();
        params.put(Constrain.CONSTRAIN_TARGET_KEY, val);
        for(int i = 0, size = keyList.size(); i < size; i++){
            final String keyString = (String)keyList.get(i);
            final Property property = (Property)properties.get(i);
            Object value = null;
            if(property == null){
                value = val;
            }else{
                try{
                    value = property.getProperty(val);
                }catch(NoSuchPropertyException e){
                }catch(java.lang.reflect.InvocationTargetException e){
                }
            }
            params.put(keyString, value);                
        }
        try{
            Interpreter interpreter = null;
            if(compiledInterpreter == null){
                interpreter = (Interpreter)ServiceManagerFactory
                    .getServiceObject(interpreterServiceName);
                if(interpreter.isCompilable()){
                    synchronized(interpreter){
                        if(compiledInterpreter == null){
                            compiledInterpreter = interpreter.compile(constrain);
                        }
                    }
                }
            }
            Object ret = null;
            if(compiledInterpreter != null){
                ret = compiledInterpreter.evaluate(params);
            }else{
                ret = interpreter.evaluate(constrain, params);
            }
            if(ret instanceof Boolean){
                return ((Boolean)ret).booleanValue();
            }else{
                throw new PropertyValidateException(
                    this,
                    "Illegal type of result. constrain=" + constrain + ", result=" + ret
                );
            }
        }catch(ServiceNotFoundException e){
            throw new PropertyValidateException(
                this,
                "Interpreter is not found : " + interpreterServiceName,
                e
            );
        }catch(EvaluateException e){
            throw new PropertyValidateException(
                this,
                "Evaluate error.",
                e
            );
        }
    }
    
    /**
     * このスキーマの文字列表現を取得する。<p>
     *
     * @return 文字列表現
     */
    public String toString(){
        final StringBuilder buf = new StringBuilder(getClass().getName());
        buf.append('{');
        buf.append("name=").append(name);
        buf.append(",type=").append(type == null ? null : type.getName());
        if(parseConverter == null && parseConverterName == null){
            buf.append(",parseConverter=null");
        }else if(parseConverter != null){
            buf.append(",parseConverter=").append(parseConverter);
        }else{
            buf.append(",parseConverter=").append(parseConverterName);
        }
        if(formatConverter == null && formatConverterName == null){
            buf.append(",formatConverter=null");
        }else if(formatConverter != null){
            buf.append(",formatConverter=").append(formatConverter);
        }else{
            buf.append(",formatConverter=").append(formatConverterName);
        }
        buf.append(",constrain=")
            .append(interpreterServiceName == null
                 ? null : interpreterServiceName)
            .append('{').append(constrain).append('}');
        if(isPrimaryKey){
            buf.append(",isPrimaryKey=").append(isPrimaryKey);
        }
        buf.append('}');
        return buf.toString();
    }
}