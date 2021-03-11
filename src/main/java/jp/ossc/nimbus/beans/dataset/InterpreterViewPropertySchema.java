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
 * Interpreterビュープロパティスキーマ実装クラス。<p>
 * このクラスには、プロパティのスキーマ情報として、以下の情報が定義できる。<br>
 * <ul>
 *   <li>名前</li>
 *   <li>型</li>
 *   <li>出力式</li>
 *   <li>出力変換種類</li>
 *   <li>制約</li>
 *   <li>主キーフラグ</li>
 * </ul>
 * 出力式に、{@link Interpreter}サービス名{出力式}を指定する事で、Interpreterを使用して出力する値を決定できる。出力式内では、"dataSet"で親のデータセットを、"record"で、レコードを参照できる。それ以外は、{@link DefaultPropertySchema}の仕様に従う。<br>
 * 
 * @author M.Takata
 */
public class InterpreterViewPropertySchema extends DefaultPropertySchema{
    
    /**
     * {@link Interpreter}サービスのサービス名。<p>
     */
    protected ServiceName interpreterServiceName;
    
    /**
     * {@link Interpreter}サービス<p>
     */
    protected Interpreter interpreter;
    
    /**
     * コンパイル済みの{@link CompiledInterpreter}。<p>
     */
    protected CompiledInterpreter compiledInterpreter;
    
    /**
     * 出力式。<p>
     */
    protected String viewExpression;
    
    /**
     * 空のプロパティスキーマを生成する。<p>
     */
    public InterpreterViewPropertySchema(){
    }
    
    /**
     * プロパティスキーマを生成する。<p>
     *
     * @param schema プロパティのスキーマ定義
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public InterpreterViewPropertySchema(String schema) throws PropertySchemaDefineException{
        super(schema);
    }
    
    /**
     * プロパティスキーマの各項目をパースする。<p>
     *
     * @param schema プロパティスキーマ全体
     * @param index スキーマ項目のインデックス
     * @param val スキーマ項目
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    protected void parseSchema(String schema, int index, String val)
     throws PropertySchemaDefineException{
        switch(index){
        case 0:
            parseName(schema, val);
            break;
        case 1:
            parseType(schema, val);
            break;
        case 2:
            parseViewExpression(schema, val);
            break;
        case 3:
            parseFormatConverter(schema, val);
            break;
        case 4:
            parseConstrain(schema, val);
            break;
        case 5:
            parsePrimaryKey(schema, val);
            break;
        }
    }
    
    /**
     * 出力式を取得する。<p>
     *
     * @return 出力式
     */
    public String getViewExpression(){
        return viewExpression;
    }
    
    /**
     * プロパティスキーマの出力式の項目をパースする。<p>
     *
     * @param schema プロパティスキーマ全体
     * @param val スキーマ項目
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    protected void parseViewExpression(String schema, String val)
     throws PropertySchemaDefineException{
        if(val != null && val.length() != 0){
            final ServiceNameEditor serviceNameEditor
                 = new ServiceNameEditor();
            final int startIndex = val.indexOf('{');
            if(startIndex >= 0 && val.charAt(val.length() - 1) == '}'){
                
                if(startIndex > 0){
                    try{
                        serviceNameEditor.setAsText(val.substring(0, startIndex));
                    }catch(IllegalArgumentException e){
                        throw new PropertySchemaDefineException(
                            schema,
                            "ViewExpression is illegal.",
                            e
                        );
                    }
                    interpreterServiceName = (ServiceName)serviceNameEditor.getValue();
                }else{
                    ScriptEngineInterpreterService service = new ScriptEngineInterpreterService();
                    try{
                        service.create();
                        service.start();
                    }catch(Exception e){
                        throw new PropertySchemaDefineException(schema, e);
                    }
                    interpreter = service;
                }
                viewExpression = val.substring(startIndex + 1, val.length() - 1);
            }else{
                throw new PropertySchemaDefineException(
                    schema,
                    "ViewExpression is illegal."
                );
            }
        }
    }
    
    /**
     * プロパティの値を取得する時に呼び出される。<p>
     *
     * @param ds 親データセット
     * @param rec このプロパティを持つレコード
     * @param val 取得しようとしているプロパティの値
     * @return 取得されるプロパティの値
     * @exception PropertyGetException プロパティの取得に失敗した場合
     */
    public Object get(DataSet ds, Record rec, Object val) throws PropertyGetException{
        Object ret = null;
        try{
            Map params = new HashMap();
            params.put("dataSet", ds);
            params.put("record", rec);
            if(compiledInterpreter == null){
                if(interpreter == null){
                    interpreter = (Interpreter)ServiceManagerFactory
                        .getServiceObject(interpreterServiceName);
                }
                if(interpreter.isCompilable()){
                    synchronized(interpreter){
                        if(compiledInterpreter == null){
                            compiledInterpreter = interpreter.compile(viewExpression);
                        }
                    }
                }
            }
            if(compiledInterpreter != null){
                ret = compiledInterpreter.evaluate(params);
            }else{
                ret = interpreter.evaluate(viewExpression, params);
            }
            if(ret != null && !isAssignableFrom(type, ret.getClass())){
                throw new PropertyValidateException(
                    this,
                    "Illegal type of result. viewExpression=" + viewExpression + ", resultType=" + ret.getClass()
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
        return ret;
    }
    
    // PropertySchemaのJavaDoc
    public Object set(Object val) throws PropertySetException{
        throw new PropertySetException(this, "Unsupported operation. Counld not set value to view.");
    }
    public Object parse(Object val) throws PropertySetException{
        throw new PropertySetException(this, "Unsupported operation. Counld not set value to view.");
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
        buf.append(",viewExpression=").append(viewExpression);
        if(formatConverter == null && formatConverterName == null){
            buf.append(",formatConverter=null");
        }else if(formatConverter != null){
            buf.append(",formatConverter=").append(formatConverter);
        }else{
            buf.append(",formatConverter=").append(formatConverterName);
        }
        buf.append(",constrain=")
            .append(constrainExpression == null
                 ? (validator == null ? (validatorName == null ? null : validatorName) : validator) : constrainExpression.constrain);
        if(isPrimaryKey){
            buf.append(",isPrimaryKey=").append(isPrimaryKey);
        }
        buf.append('}');
        return buf.toString();
    }
}