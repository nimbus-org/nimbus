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
package jp.ossc.nimbus.util.converter;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.math.BigDecimal;

import jp.ossc.nimbus.beans.Property;
import jp.ossc.nimbus.beans.PropertyAccess;
import jp.ossc.nimbus.beans.SimpleProperty;
import jp.ossc.nimbus.beans.IndexedProperty;
import jp.ossc.nimbus.beans.NestedProperty;
import jp.ossc.nimbus.beans.NoSuchPropertyException;
import jp.ossc.nimbus.beans.dataset.DataSet;
import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.beans.dataset.RecordList;
import jp.ossc.nimbus.beans.dataset.RecordSchema;
import jp.ossc.nimbus.beans.dataset.PropertySchema;
import jp.ossc.nimbus.beans.dataset.RecordPropertySchema;
import jp.ossc.nimbus.beans.dataset.RecordListPropertySchema;
import jp.ossc.nimbus.beans.dataset.PropertySchemaDefineException;
import jp.ossc.nimbus.util.ClassMappingTree;

/**
 * Bean交換コンバータ。<p>
 * 
 * @author M.Takata
 */
public class BeanExchangeConverter implements BindingConverter{
    
    /**
     * キャメルスネーク変換を行わないを表す変換定数。<p>
     */
    public static final int CAMEL_SNAKE_NON = 0;
    
    /**
     * キャメル→スネークを表す変換定数。<p>
     */
    public static final int CAMEL_TO_SNAKE = 1;
    
    /**
     * スネーク→キャメルを表す変換定数。<p>
     */
    public static final int SNAKE_TO_CAMEL = 2;
    
    private Object output;
    private Map propertyMapping;
    private Map partPropertyMapping;
    private PropertyAccess propertyAccess = new PropertyAccess();
    private boolean isCloneOutput = false;
    private boolean isFieldOnly = false;
    private boolean isAccessorOnly = true;
    private ClassMappingTree propertyAccessTypeMap;
    private ClassMappingTree implementsTypeMap;
    private boolean isMakeSchema;
    private boolean isNarrowCast;
    private ClassMappingTree nestConvertClassTypeMap;
    private int camelSnakeConvertMode = 0;
    private String[] camelSnakeIgnorePropertyNames;
    private boolean isUpperCaseStartCamelProperty = false;
    private boolean isIgnoreNullProperty = false;
    
    /**
     * 空のインスタンスを生成する。<p>
     */
    public BeanExchangeConverter(){
    }
    
    /**
     * 指定した交換マッピングを持つインスタンスを生成する。<p>
     *
     * @param mapping 交換する入力オブジェクトと出力オブジェクトのプロパティのマッピング
     */
    public BeanExchangeConverter(Map mapping){
        setPropertyMappings(mapping);
    }
    
    /**
     * 指定した交換マッピングと出力オブジェクトを持つインスタンスを生成する。<p>
     *
     * @param mapping 交換する入力オブジェクトと出力オブジェクトのプロパティのマッピング
     * @param output 出力オブジェクト
     */
    public BeanExchangeConverter(Map mapping, Object output){
        setPropertyMappings(mapping);
        setOutputObject(output);
    }
    
    /**
     * 出力オブジェクトを設定する。<p>
     * 
     * @param obj 出力オブジェクト
     */
    public void setOutputObject(Object obj){
        output = obj;
    }
    
    /**
     * 出力オブジェクトを取得する。<p>
     * 
     * @return 出力オブジェクト
     */
    public Object getOutputObject(){
        return output;
    }
    
    /**
     * 交換する入力オブジェクトと出力オブジェクトのプロパティのマッピングを設定する。<p>
     * 出力オブジェクトが配列やリストの場合で、入力オブジェクト側のプロパティ（配列やリスト）を展開したい場合は、inputPropertyで指定するプロパティ名の末尾に"-<"を付加する。<br>
     *
     * @param inputProperty 入力オブジェクト側のプロパティ
     * @param outputProperty 値が出力オブジェクト側のプロパティ
     */
    public void setPropertyMapping(String inputProperty, String outputProperty){
        setPropertyMapping(inputProperty, outputProperty, null);
    }
    
    /**
     * 交換する入力オブジェクトと出力オブジェクトのプロパティのマッピングを設定する。<p>
     * 出力オブジェクトが配列やリストの場合で、入力オブジェクト側のプロパティ（配列やリスト）を展開したい場合は、inputPropertyで指定するプロパティ名の末尾に"-<"を付加する。<br>
     *
     * @param inputProperty 入力オブジェクト側のプロパティ
     * @param outputProperty 値が出力オブジェクト側のプロパティ
     * @param converter 入力オブジェクト側のプロパティを出力オブジェクト側のプロパティに設定する際に使用するConverter
     */
    public void setPropertyMapping(String inputProperty, String outputProperty, Converter converter){
        if(propertyMapping == null){
            propertyMapping = new HashMap();
        }
        putPropertyMapping(propertyMapping, inputProperty, new ExchangeMapping(outputProperty, converter));
    }
    
    /**
     * 交換する入力オブジェクトと出力オブジェクトのプロパティのマッピングを設定する。<p>
     * 出力オブジェクトが配列やリストの場合で、入力オブジェクト側のインデックス付きプロパティを展開したい場合は、inputPropertyで指定するプロパティ名の末尾に"-<"を付加する。<br>
     *
     * @param inputProperty 入力オブジェクト側のインデックス付きプロパティ
     * @param outputProperty 出力オブジェクト側のプロパティ
     * @param from 入力オブジェクト側のインデックス付きプロパティの開始インデックス
     * @param to 入力オブジェクト側のインデックス付きプロパティの終了インデックス
     */
    public void setIndexedPropertyMapping(String inputProperty, String outputProperty, int from, int to){
        setIndexedPropertyMapping(inputProperty, outputProperty, from, to, null);
    }
    
    /**
     * 交換する入力オブジェクトと出力オブジェクトのプロパティのマッピングを設定する。<p>
     * 出力オブジェクトが配列やリストの場合で、入力オブジェクト側のインデックス付きプロパティを展開したい場合は、inputPropertyで指定するプロパティ名の末尾に"-<"を付加する。<br>
     *
     * @param inputProperty 入力オブジェクト側のインデックス付きプロパティ
     * @param outputProperty 出力オブジェクト側のプロパティ
     * @param from 入力オブジェクト側のインデックス付きプロパティの開始インデックス
     * @param to 入力オブジェクト側のインデックス付きプロパティの終了インデックス
     * @param converter 入力オブジェクト側のプロパティを出力オブジェクト側のプロパティに設定する際に使用するConverter
     */
    public void setIndexedPropertyMapping(String inputProperty, String outputProperty, int from, int to, Converter converter){
        setPropertyMapping(inputProperty + '[' + from + ',' + to + "]>-", outputProperty, converter);
    }
    
    /**
     * 指定した入力オブジェクトのプロパティに対する出力オブジェクトのプロパティのマッピングを取得する。<p>
     *
     * @param inputProperty キーが入力オブジェクト側のプロパティ
     * @return 出力オブジェクト側のプロパティまたはそのリスト
     */
    public Object getPropertyMapping(String inputProperty){
        if(propertyMapping == null){
            return null;
        }
        return propertyMapping.get(inputProperty);
    }
    
    /**
     * 交換する入力オブジェクトと出力オブジェクトのプロパティのマッピングを設定する。<p>
     *
     * @param mapping キーが入力オブジェクト側のプロパティ、値が出力オブジェクト側のプロパティとなるマッピング
     */
    public void setPropertyMappings(Map mapping){
        if(mapping == null){
            clearPropertyMappings();
            return;
        }
        Iterator entries = mapping.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry)entries.next();
            setPropertyMapping((String)entry.getKey(), (String)entry.getValue());
        }
    }
    
    /**
     * 交換する入力オブジェクトと出力オブジェクトのプロパティのマッピングを取得する。<p>
     *
     * @return キーが入力オブジェクト側のプロパティ、値が出力オブジェクト側のプロパティとなるマッピング
     */
    public Map getPropertyMappings(){
        return propertyMapping;
    }
    
    /**
     * 交換する入力オブジェクトと出力オブジェクトのプロパティのマッピングを削除する。<p>
     */
    public void clearPropertyMappings(){
        propertyMapping = null;
    }
    
    /**
     * 出力オブジェクトのプロパティを基準にして、入力オブジェクトと出力オブジェクトのプロパティ交換を行う場合に、一部のプロパティ交換のみ個別に設定する。<p>
     * 交換時の出力オブジェクト側のプロパティを、このマッピングのキーで指定した値と異なるプロパティにしたい場合は、{@link #setPartPropertyMapping(String,String,String)}で個別に指定する必要がある。<br>
     *
     * @param mapping キーが個別に指定する出力オブジェクト側のプロパティ、値が入力オブジェクト側のプロパティとなるマッピング
     */
    public void setPartPropertyMappings(Map mapping){
        if(mapping == null){
            clearPartPropertyMappings();
            return;
        }
        Iterator entries = mapping.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry)entries.next();
            setPartPropertyMapping((String)entry.getKey(), (String)entry.getValue(), null);
        }
    }
    
    /**
     * 出力オブジェクトのプロパティを基準にして、入力オブジェクトと出力オブジェクトのプロパティ交換を行う場合に、一部のプロパティ交換のみ個別に設定する。<p>
     * 出力オブジェクトが配列やリストの場合で、入力オブジェクト側のプロパティ（配列やリスト）を展開したい場合は、inputPropertyで指定するプロパティ名の末尾に"-<"を付加する。<br>
     *
     * @param partOutputProperty 個別に指定する出力オブジェクト側のプロパティ
     * @param inputProperty 入力オブジェクト側のプロパティ。partOutputPropertyと同じで良い場合は、null
     * @param outputProperty 出力オブジェクト側のプロパティ。partOutputPropertyと同じで良い場合は、null
     */
    public void setPartPropertyMapping(String partOutputProperty, String inputProperty, String outputProperty){
        setPartPropertyMapping(partOutputProperty, inputProperty, outputProperty, null);
    }
    
    /**
     * 出力オブジェクトのプロパティを基準にして、入力オブジェクトと出力オブジェクトのプロパティ交換を行う場合に、一部のプロパティ交換のみ個別に設定する。<p>
     * 出力オブジェクトが配列やリストの場合で、入力オブジェクト側のプロパティ（配列やリスト）を展開したい場合は、inputPropertyで指定するプロパティ名の末尾に"-<"を付加する。<br>
     *
     * @param partOutputProperty 個別に指定する出力オブジェクト側のプロパティ
     * @param inputProperty 入力オブジェクト側のプロパティ。partOutputPropertyと同じで良い場合は、null
     * @param outputProperty 出力オブジェクト側のプロパティ。partOutputPropertyと同じで良い場合は、null
     * @param converter 入力オブジェクト側のプロパティを出力オブジェクト側のプロパティに設定する際に使用するConverter
     */
    public void setPartPropertyMapping(String partOutputProperty, String inputProperty, String outputProperty, Converter converter){
        if(partPropertyMapping == null){
            partPropertyMapping = new HashMap();
        }
        if(inputProperty == null){
            inputProperty = partOutputProperty;
        }
        if(outputProperty == null){
            outputProperty = partOutputProperty;
        }
        Object inOutProp = partPropertyMapping.get(partOutputProperty);
        if(inOutProp == null){
            partPropertyMapping.put(partOutputProperty, new Object[]{inputProperty, new ExchangeMapping(outputProperty, converter)});
        }else{
            List inOutProps = null;
            if(inOutProp instanceof Object[]){
                inOutProps = new ArrayList();
                inOutProps.add(inOutProp);
                partPropertyMapping.put(partOutputProperty, inOutProps);
            }else{
                inOutProps = (List)inOutProp;
            }
            inOutProps.add(new Object[]{inputProperty, new ExchangeMapping(outputProperty, converter)});
        }
    }
    
    /**
     * 出力オブジェクトのプロパティを基準にして、入力オブジェクトと出力オブジェクトのプロパティ交換を行う場合に、一部のプロパティ交換のみ個別に設定する。<p>
     * 出力オブジェクトが配列やリストの場合で、入力オブジェクト側のインデックス付きプロパティを展開したい場合は、inputPropertyで指定するプロパティ名の末尾に"-<"を付加する。<br>
     *
     * @param partOutputProperty 個別に指定する出力オブジェクト側のプロパティ
     * @param inputProperty 入力オブジェクト側のインデックス付きプロパティ。partOutputPropertyと同じで良い場合は、null
     * @param outputProperty 出力オブジェクト側のプロパティ。partOutputPropertyと同じで良い場合は、null
     * @param from 入力オブジェクト側のインデックス付きプロパティの開始インデックス
     * @param to 入力オブジェクト側のインデックス付きプロパティの終了インデックス
     */
    public void setPartIndexedPropertyMapping(String partOutputProperty, String inputProperty, String outputProperty, int from, int to){
        setPartIndexedPropertyMapping(partOutputProperty, inputProperty, outputProperty, from, to, null);
    }
    
    /**
     * 出力オブジェクトのプロパティを基準にして、入力オブジェクトと出力オブジェクトのプロパティ交換を行う場合に、一部のプロパティ交換のみ個別に設定する。<p>
     * 出力オブジェクトが配列やリストの場合で、入力オブジェクト側のインデックス付きプロパティを展開したい場合は、inputPropertyで指定するプロパティ名の末尾に"-<"を付加する。<br>
     *
     * @param partOutputProperty 個別に指定する出力オブジェクト側のプロパティ
     * @param inputProperty 入力オブジェクト側のインデックス付きプロパティ。partOutputPropertyと同じで良い場合は、null
     * @param outputProperty 出力オブジェクト側のプロパティ。partOutputPropertyと同じで良い場合は、null
     * @param from 入力オブジェクト側のインデックス付きプロパティの開始インデックス
     * @param to 入力オブジェクト側のインデックス付きプロパティの終了インデックス
     * @param converter 入力オブジェクト側のプロパティを出力オブジェクト側のプロパティに設定する際に使用するConverter
     */
    public void setPartIndexedPropertyMapping(String partOutputProperty, String inputProperty, String outputProperty, int from, int to, Converter converter){
        setPartPropertyMapping(partOutputProperty, inputProperty + '[' + from + ',' + to + "]>-", outputProperty, converter);
    }
    
    /**
     * 一部の交換する入力オブジェクトと出力オブジェクトのプロパティのマッピングを削除する。<p>
     */
    public void clearPartPropertyMappings(){
        partPropertyMapping = null;
    }
    
    /**
     * 交換する入力オブジェクトと出力オブジェクトのプロパティのマッピングを全て削除する。<p>
     * 
     * @see #clearPropertyMappings()
     * @see #clearPartPropertyMappings()
     */
    public void clearMappings(){
        clearPropertyMappings();
        clearPartPropertyMappings();
    }
    
    /**
     * 出力オブジェクトを複製するかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isClone 複製する場合、true
     */
    public void setCloneOutput(boolean isClone){
        isCloneOutput = isClone;
    }
    
    /**
     * 出力オブジェクトを複製するかどうかを判定する。<p>
     *
     * @return trueの場合、複製する
     */
    public boolean isCloneOutput(){
        return isCloneOutput;
    }
    
    /**
     * 変換時にJavaオブジェクトのpublicフィールドのみを対象とするかどうかを判定する。<p>
     *
     * @return trueの場合、publicフィールドのみを対象とする
     */
    public boolean isFieldOnly(){
        return isFieldOnly;
    }
    
    /**
     * 変換時にJavaオブジェクトのpublicフィールドのみを対象とするかどうかを設定する。<p>
     * デフォルトは、falseでpublicフィールドのみを対象にはしない。<br>
     *
     * @param isFieldOnly publicフィールドのみを対象とする場合は、true
     */
    public void setFieldOnly(boolean isFieldOnly){
        this.isFieldOnly = isFieldOnly;
    }
    
    /**
     * 変換時にJavaオブジェクトのpublicフィールドのみを対象とするかどうかを判定する。<p>
     *
     * @param type 対象のクラス
     * @return trueの場合、publicフィールドのみを対象とする
     */
    public boolean isFieldOnly(Class type){
        PropertyAccessType pat = propertyAccessTypeMap == null ? null : (PropertyAccessType)propertyAccessTypeMap.getValue(type);
        return pat == null ? isFieldOnly : pat.isFieldOnly;
    }
    
    /**
     * 変換時にJavaオブジェクトのpublicフィールドのみを対象とするかどうかを設定する。<p>
     * デフォルトは、falseでpublicフィールドのみを対象にはしない。<br>
     *
     * @param type 対象のクラス
     * @param isFieldOnly publicフィールドのみを対象とする場合は、true
     */
    public void setFieldOnly(Class type, boolean isFieldOnly){
        if(propertyAccessTypeMap == null){
            propertyAccessTypeMap = new ClassMappingTree();
        }
        PropertyAccessType pat = (PropertyAccessType)propertyAccessTypeMap.getValueOf(type);
        if(pat == null){
            pat = new PropertyAccessType();
            propertyAccessTypeMap.add(type, pat);
        }
        pat.isFieldOnly = isFieldOnly;
    }
    
    /**
     * 変換時に出力しないプロパティ名を設定する。<p>
     *
     * @param names プロパティ名の配列
     */
    public void setDisabledPropertyNames(String[] names){
        setDisabledPropertyNames(Object.class, names);
    }
    
    /**
     * 変換時に出力しないプロパティ名を設定する。<p>
     *
     * @param type 対象のクラス
     * @param names プロパティ名の配列
     */
    public void setDisabledPropertyNames(Class type, String[] names){
        if(propertyAccessTypeMap == null){
            propertyAccessTypeMap = new ClassMappingTree();
        }
        PropertyAccessType pat = (PropertyAccessType)propertyAccessTypeMap.getValueOf(type);
        if(pat == null){
            pat = new PropertyAccessType();
            propertyAccessTypeMap.add(type, pat);
        }
        if(names == null || names.length == 0){
            pat.disabledPropertyNames = null;
        }else{
            if(pat.disabledPropertyNames == null){
                pat.disabledPropertyNames = new HashSet();
            }else{
                pat.disabledPropertyNames.clear();
            }
            for(int i = 0; i < names.length; i++){
                pat.disabledPropertyNames.add(names[i]);
            }
        }
    }
    
    /**
     * 変換時に出力するプロパティ名を設定する。<p>
     *
     * @param names プロパティ名の配列
     */
    public void setEnabledPropertyNames(String[] names){
        setEnabledPropertyNames(Object.class, names);
    }
    
    /**
     * 変換時に出力するプロパティ名を設定する。<p>
     *
     * @param type 対象のクラス
     * @param names プロパティ名の配列
     */
    public void setEnabledPropertyNames(Class type, String[] names){
        if(propertyAccessTypeMap == null){
            propertyAccessTypeMap = new ClassMappingTree();
        }
        PropertyAccessType pat = (PropertyAccessType)propertyAccessTypeMap.getValueOf(type);
        if(pat == null){
            pat = new PropertyAccessType();
            propertyAccessTypeMap.add(type, pat);
        }
        if(names == null || names.length == 0){
            pat.enabledPropertyNames = null;
        }else{
            if(pat.enabledPropertyNames == null){
                pat.enabledPropertyNames = new HashSet();
            }else{
                pat.enabledPropertyNames.clear();
            }
            for(int i = 0; i < names.length; i++){
                pat.enabledPropertyNames.add(names[i]);
            }
        }
    }
    
    /**
     * 変換時に出力するプロパティ名かどうかを判定する。<p>
     *
     * @param name プロパティ名
     * @return 出力する場合true
     */
    public boolean isEnabledPropertyName(String name){
        return isEnabledPropertyName(Object.class, name);
    }
    
    /**
     * 変換時に出力するプロパティ名かどうかを判定する。<p>
     *
     * @param type 対象のクラス
     * @param name プロパティ名
     * @return 出力する場合true
     */
    public boolean isEnabledPropertyName(Class type, String name){
        PropertyAccessType pat = propertyAccessTypeMap == null ? null : (PropertyAccessType)propertyAccessTypeMap.getValue(type);
        if(pat == null){
            return true;
        }
        if(pat.disabledPropertyNames != null && pat.disabledPropertyNames.contains(name)){
            return false;
        }
        if(pat.enabledPropertyNames != null){
            return pat.enabledPropertyNames.contains(name);
        }
        return true;
    }
    
    /**
     * 変換時にJavaオブジェクトのpublicなgetterのみを対象とするかどうかを判定する。<p>
     *
     * @return trueの場合、publicなgetterのみを対象とする
     */
    public boolean isAccessorOnly(){
        return isAccessorOnly;
    }
    
    /**
     * 変換時にJavaオブジェクトのpublicなgetterのみを対象とするかどうかを設定する。<p>
     * デフォルトは、trueでpublicなgetterのみを対象にする。<br>
     *
     * @param isAccessorOnly publicなgetterのみを対象とする場合、true
     */
    public void setAccessorOnly(boolean isAccessorOnly){
        this.isAccessorOnly = isAccessorOnly;
    }
    
    /**
     * 変換時にJavaオブジェクトのpublicなgetterのみを対象とするかどうかを判定する。<p>
     *
     * @param type 対象のクラス
     * @return trueの場合、publicなgetterのみを対象とする
     */
    public boolean isAccessorOnly(Class type){
        PropertyAccessType pat = propertyAccessTypeMap == null ? null : (PropertyAccessType)propertyAccessTypeMap.getValue(type);
        return pat == null ? isAccessorOnly : pat.isAccessorOnly;
    }
    
    /**
     * 変換時にJavaオブジェクトのpublicなgetterのみを対象とするかどうかを設定する。<p>
     * デフォルトは、trueでpublicなgetterのみを対象にする。<br>
     *
     * @param type 対象のクラス
     * @param isAccessorOnly publicなgetterのみを対象とする場合、true
     */
    public void setAccessorOnly(Class type, boolean isAccessorOnly){
        if(propertyAccessTypeMap == null){
            propertyAccessTypeMap = new ClassMappingTree();
        }
        PropertyAccessType pat = (PropertyAccessType)propertyAccessTypeMap.getValueOf(type);
        if(pat == null){
            pat = new PropertyAccessType();
            propertyAccessTypeMap.add(type, pat);
        }
        pat.isAccessorOnly = isAccessorOnly;
    }
    
    /**
     * Javaオブジェクト⇔JSON変換時に使用するプロパティ名に対する型を設定する。<p>
     * setterやgetterがオーバーロードされていて、型が正しく判別できない場合に使用する。<br>
     *
     * @param type 対象のクラス
     * @param propName プロパティ名
     * @param propType プロパティ型
     */
    public void setPropertyType(Class type, String propName, Class propType){
        if(propertyAccessTypeMap == null){
            propertyAccessTypeMap = new ClassMappingTree();
        }
        PropertyAccessType pat = (PropertyAccessType)propertyAccessTypeMap.getValueOf(type);
        if(pat == null){
            pat = new PropertyAccessType();
            propertyAccessTypeMap.add(type, pat, true);
        }
        if(pat.propertyTypes == null){
            pat.propertyTypes = new HashMap();
        }
        Property prop = propertyAccess.getProperty(propName);
        pat.propertyTypes.put(prop.getPropertyName(), propType);
    }
    
    /**
     * Javaオブジェクト⇔JSON変換時に使用するプロパティ名に対する型を取得する。<p>
     *
     * @param type 対象のクラス
     * @param propName プロパティ名
     * return propType プロパティ型
     */
    public Class getPropertyType(Class type, String propName){
        PropertyAccessType pat = propertyAccessTypeMap == null ? null : (PropertyAccessType)propertyAccessTypeMap.getValue(type);
        if(pat == null || pat.propertyTypes == null){
            return null;
        }
        Property prop = propertyAccess.getProperty(propName);
        return (Class)pat.propertyTypes.get(prop.getPropertyName());
    }
    
    protected Class getPropertyType(Property prop, Object obj) throws NoSuchPropertyException, InvocationTargetException{
        return getPropertyType(prop, obj, null);
    }
    
    protected Class getPropertyType(Property prop, Object obj, Object value) throws NoSuchPropertyException, InvocationTargetException{
        Class type = getPropertyType(obj.getClass(), prop.getPropertyName());
        if(type == null){
            if(value == null){
                return prop.getPropertyType(obj);
            }else{
                return prop.isWritable(obj, value.getClass()) ? value.getClass() : prop.getPropertyType(obj);
            }
        }else{
            return type;
        }
    }
    
    /**
     * 型に対するプロパティの交換設定を削除する。<p>
     */
    public void clearTypeSettings(){
        propertyAccessTypeMap = null;
    }
    
    /**
     * 変換後オブジェクトがRecordでスキーマ定義が行われていない場合に、変換対象オブジェクトの各フィールドの型に即したスキーマ定義を作成するかどうかを判定する。<p>
     *
     * @return trueの場合、スキーマを作成する
     */
    public boolean isMakeSchema(){
        return isMakeSchema;
    }
    
    /**
     * 変換後オブジェクトがRecordでスキーマ定義が行われていない場合に、変換対象オブジェクトの各フィールドの型に即したスキーマ定義を作成するかどうかを設定する。<p>
     * デフォルトはfalseで、作成しない。<br>
     *
     * @param isMake スキーマを作成する場合、true
     */
    public void setMakeSchema(boolean isMake){
        isMakeSchema = isMake;
    }
    
    /**
     * 変換時に、インタフェース型（または抽象型）に対してどの実装クラスを生成するかを設定する。<p>
     * 
     * @param interfaceType インタフェース型（または抽象型）
     * @param implementsType 実装クラス型
     */
    public void setImplementsType(Class interfaceType,  Class implementsType){
        if(!interfaceType.isInterface() && !Modifier.isAbstract(interfaceType.getModifiers())){
            throw new IllegalArgumentException("interfaceType must be interface or abstract. interfaceType=" + interfaceType.getName());
        }
        if(implementsType.isInterface() || Modifier.isAbstract(implementsType.getModifiers())){
            throw new IllegalArgumentException("implementsType must not be interface or abstract. implementsType=" + implementsType.getName());
        }
        if(implementsTypeMap == null){
            implementsTypeMap = new ClassMappingTree();
        }
        implementsTypeMap.add(interfaceType, implementsType, true);
    }
    
    /**
     * 変換時に、インタフェース型（または抽象型）に対してどの実装クラスを生成するかを取得する。<p>
     * 
     * @param interfaceType インタフェース型（または抽象型）
     * @return 実装クラス型
     */
    public Class getImplementsType(Class interfaceType){
        if(implementsTypeMap == null){
            return null;
        }
        return (Class)implementsTypeMap.getValue(interfaceType);
    }
    
    /**
     * 変換時に、指定した型に対して適切な実装クラスを取得する。<p>
     * 
     * @param type 型
     * @return 実装クラス型
     */
    protected Class findImplementsType(Class type){
        if(implementsTypeMap == null){
            return type;
        }
        Class result = (Class)implementsTypeMap.getValue(type);
        return result == null ? type : result;
    }
    
    /**
     * 変換時に、数値型のプロパティのダウンキャストを許可するかどうかを判定する。<p>
     *
     * @return trueの場合、許可する
     */
    public boolean isNarrowCast(){
        return isNarrowCast;
    }
    
    /**
     * 変換時に、数値型のプロパティのダウンキャストを許可するかどうかを設定する。<p>
     * デフォルトは、falseで許可しない。<br>
     *
     * @param isNarrowCast 許可する場合、true
     */
    public void setNarrowCast(boolean isNarrowCast){
        this.isNarrowCast = isNarrowCast;
    }
    
    /**
     * 変換時にネストしてコンバートする対象のClassをリストに追加する。<p>
     * 
     * @param clazz 変換時にネストしてコンバートする対象のClass
     */
    public void addNestConvertClass(Class clazz) {
        if(nestConvertClassTypeMap == null) {
            nestConvertClassTypeMap = new ClassMappingTree();
        }
        nestConvertClassTypeMap.add(clazz, clazz, true);
    }
    
    /**
     * プロパティ名のキャメルケースとスネークケース変換モードを設定する。<p>
     * デフォルトは、0で変換しない。<br>
     * 
     * @param mode プロパティ名のキャメルケースとスネークケース変換モード
     * @see #CAMEL_SNAKE_NON
     * @see #CAMEL_TO_SNAKE
     * @see #SNAKE_TO_CAMEL
     */
    public void setCamelSnakeConvertMode(int mode) {
        camelSnakeConvertMode = mode;
    }
    
    /**
     * プロパティ名のキャメルケースとスネークケース変換モードを取得する。<p>
     * 
     * @return プロパティ名のキャメルケースとスネークケース変換モード
     */
    public int getCamelSnakeConvertMode() {
        return camelSnakeConvertMode;
    }
    
    /**
     * プロパティ名のキャメルケースとスネークケース変換を行う際の無視プロパティ名の配列を設定する。<p>
     * 
     * @param propertyNames プロパティ名のキャメルケースとスネークケース変換を行う際の無視プロパティ名の配列
     */
    public void setCamelSnakeIgnorePropertyNames(String[] propertyNames) {
        camelSnakeIgnorePropertyNames = propertyNames;
    }
    
    /**
     * プロパティ名のキャメルケースとスネークケース変換を行う際の無視プロパティ名の配列を取得する。<p>
     * 
     * @return プロパティ名のキャメルケースとスネークケース変換を行う際の無視プロパティ名の配列
     */
    public String[] getCamelSnakeIgnorePropertyNames() {
        return camelSnakeIgnorePropertyNames;
    }
    
    /**
     * プロパティ名をキャメルケース変換する際に最初の文字を大文字設定するかを取得する。<p>
     * デフォルトは、fasleで大文字設定しない。<br>
     * 
     * @return プロパティ名をキャメルケース変換する際に最初の文字を大文字設定するか
     */
    public boolean isUpperCaseStartCamelProperty() {
        return isUpperCaseStartCamelProperty;
    }
    
    /**
     * プロパティ名をキャメルケース変換する際に最初の文字を大文字設定するかを設定する。<p>
     * デフォルトは、fasleで大文字設定しない。<br>
     * 
     * @param isUpperCaseStart プロパティ名をキャメルケース変換する際に最初の文字を大文字設定するか
     */
    public void setUpperCaseStartCamelProperty(boolean isUpperCaseStart) {
        isUpperCaseStartCamelProperty = isUpperCaseStart;
    }
    
    /**
     * null参照のプロパティを取得しようとした場合に、例外をthrowするかどうか、また、null値を設定するかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isIgnore null参照の時に例外をthrowしない場合はtrue
     */
    public void setIgnoreNullProperty(boolean isIgnore){
        isIgnoreNullProperty = isIgnore;
        propertyAccess.setIgnoreNullProperty(isIgnore);
    }
    
    /**
     * null参照のプロパティを取得しようとした場合に、例外をthrowするかどうか、また、null値を設定するかどうかを設定する。<p>
     *
     * @return null参照の時に例外をthrowしない、また、null値を設定しない場合はtrue
     */
    public boolean isIgnoreNullProperty(){
        return isIgnoreNullProperty;
    }
    
    /**
     * 指定されたオブジェクトを変換する。<p>
     *
     * @param obj 変換対象のオブジェクト
     * @return 変換後のオブジェクト
     * @exception ConvertException 変換に失敗した場合
     */
    public Object convert(Object obj) throws ConvertException{
        return convert(obj, output);
    }
    
    /**
     * 指定されたオブジェクトを変換する。<p>
     *
     * @param input 変換対象のオブジェクト
     * @param output 変換後のオブジェクト
     * @return 変換後のオブジェクト
     * @exception ConvertException 変換に失敗した場合
     */
    public Object convert(Object input, Object output) throws ConvertException{
        return convert(input, output, isCloneOutput);
    }
    
    private Object convert(Object input, Object output, boolean isClone) throws ConvertException{
        if(output == null){
            throw new ConvertException("Output is null.");
        }
        if(isClone){
            if(!(output instanceof Cloneable)){
                throw new ConvertException("Output is not cloneable.");
            }
            if(output instanceof DataSet){
                output = ((DataSet)output).cloneSchema();
            }else if(output instanceof RecordList){
                output = ((RecordList)output).cloneSchema();
            }else if(output instanceof Record){
                output = ((Record)output).cloneSchema();
            }else{
                try{
                    output = output.getClass().getMethod("clone", (Class[])null).invoke(output, (Object[])null);
                }catch(NoSuchMethodException e){
                    throw new ConvertException(e);
                }catch(IllegalAccessException e){
                    throw new ConvertException(e);
                }catch(InvocationTargetException e){
                    throw new ConvertException(e);
                }
            }
        }
        if(input == null){
            return output;
        }
        
        Object[] inputs = null;
        if(input instanceof Collection){
            inputs = ((Collection)input).toArray();
        }else if(input.getClass().isArray()){
            inputs = (Object[])input;
        }
        if(inputs != null){
            if(inputs.length == 0){
                return output;
            }
            if(output instanceof RecordList){
                RecordList list = (RecordList)output;
                for(int i = 0; i < inputs.length; i++){
                    Record record = null;
                    if(i < list.size()){
                        record = list.getRecord(i);
                    }else{
                        record = list.createRecord();
                    }
                    record = (Record)convert(inputs[i], record, false);
                    if(record != null && i >= list.size()){
                        list.add(record);
                    }
                }
                return list;
            }else if(output instanceof Collection){
                Object[] outputs = ((Collection)output).toArray();
                if(outputs.length == 0){
                    Type type = output.getClass().getGenericSuperclass();
                    if(type != null && type instanceof ParameterizedType){
                        Type[] argTypes = ((ParameterizedType)type).getActualTypeArguments();
                        if(argTypes != null && argTypes.length == 1 && argTypes[0] instanceof Class){
                            outputs = (Object[])Array.newInstance((Class)argTypes[0], inputs.length);
                            try{
                                for(int i = 0; i < outputs.length; i++){
                                    outputs[i] = findImplementsType((Class)argTypes[0]).newInstance();
                                    ((Collection)output).add(outputs[i]);
                                }
                            }catch(IllegalAccessException e){
                                throw new ConvertException("Length of array is 0.", e);
                            }catch(InstantiationException e){
                                throw new ConvertException("Length of array is 0.", e);
                            }
                        }
                    }
                    if(outputs.length == 0){
                        throw new ConvertException("Size of collection is 0.");
                    }
                }
                for(int i = 0, imax = Math.min(inputs.length, outputs.length); i < imax; i++){
                    outputs[i] = convert(inputs[i], outputs[i], false);
                }
                return output;
            }else if(output.getClass().isArray()){
                Object[] outputs = (Object[])output;
                final Class componentType = output.getClass().getComponentType();
                if(outputs.length == 0){
                    if(componentType.isPrimitive()){
                        throw new ConvertException("Length of array is 0.");
                    }
                    outputs = (Object[])Array.newInstance(componentType, inputs.length);
                    try{
                        for(int i = 0; i < outputs.length; i++){
                            outputs[i] = findImplementsType(componentType).newInstance();
                        }
                    }catch(IllegalAccessException e){
                        throw new ConvertException("Length of array is 0.", e);
                    }catch(InstantiationException e){
                        throw new ConvertException("Length of array is 0.", e);
                    }
                }
                for(int i = 0, imax = Math.min(inputs.length, outputs.length); i < imax; i++){
                    if(outputs[i] == null){
                        if(componentType.isPrimitive()){
                            throw new ConvertException("Element of array is null.");
                        }
                        try{
                            outputs[i] = findImplementsType(componentType).newInstance();
                        }catch(IllegalAccessException e){
                            throw new ConvertException("Element of array is null.", e);
                        }catch(InstantiationException e){
                            throw new ConvertException("Element of array is null.", e);
                        }
                    }
                    outputs[i] = convert(inputs[i], outputs[i], false);
                }
                return outputs;
            }
        }
        Map propMapping = propertyMapping;
        boolean isOutputAutoMapping = false;
        boolean isInputAutoMapping = false;
        if(propMapping == null || propMapping.size() == 0){
            if(propMapping == null){
                propMapping = new HashMap();
            }
            if(output instanceof Record){
                Record record = (Record)output;
                RecordSchema schema = record.getRecordSchema();
                if(schema != null){
                    for(int i = 0, imax = schema.getPropertySize(); i < imax; i++){
                        String inputProp = schema.getPropertyName(i);
                        String outputProp = inputProp;
                        if(isEnabledPropertyName(output.getClass(), outputProp)){
                            if(partPropertyMapping != null && partPropertyMapping.containsKey(outputProp)){
                                Object propMap = partPropertyMapping.get(outputProp);
                                if(propMap instanceof List){
                                    Iterator propMaps = ((List)propMap).iterator();
                                    while(propMaps.hasNext()){
                                        Object[] inOutProp = (Object[])propMaps.next();
                                        putPropertyMapping(propMapping, (String)inOutProp[0], (ExchangeMapping)inOutProp[1]);
                                    }
                                }else{
                                    putPropertyMapping(propMapping, (String)((Object[])propMap)[0], (ExchangeMapping)((Object[])propMap)[1]);
                                }
                            }else{
                                if(isCamelSnakeConvert()){
                                    inputProp = camelSnakeConvert((String)inputProp);
                                }
                                putPropertyMapping(propMapping, (String)inputProp, new ExchangeMapping(outputProp, null));
                            }
                        }
                    }
                }
                if(propMapping.size() != 0){
                    isOutputAutoMapping = true;
                }
            }else if(output instanceof RecordList){
                RecordList recordList = (RecordList)output;
                RecordSchema schema = recordList.getRecordSchema();
                if(schema != null){
                    for(int i = 0, imax = schema.getPropertySize(); i < imax; i++){
                        String inputProp = schema.getPropertyName(i);
                        String outputProp = inputProp;
                        if(isEnabledPropertyName(recordList.getRecordClass(), outputProp)){
                            if(partPropertyMapping != null && partPropertyMapping.containsKey(outputProp)){
                                Object propMap = partPropertyMapping.get(outputProp);
                                if(propMap instanceof List){
                                    Iterator propMaps = ((List)propMap).iterator();
                                    while(propMaps.hasNext()){
                                        Object[] inOutProp = (Object[])propMaps.next();
                                        putPropertyMapping(propMapping, (String)inOutProp[0], (ExchangeMapping)inOutProp[1]);
                                    }
                                }else{
                                    putPropertyMapping(propMapping, (String)((Object[])propMap)[0], (ExchangeMapping)((Object[])propMap)[1]);
                                }
                            }else{
                                if(isCamelSnakeConvert()){
                                    inputProp = camelSnakeConvert((String)inputProp);
                                }
                                putPropertyMapping(propMapping, (String)inputProp, new ExchangeMapping(outputProp, null));
                            }
                        }
                    }
                }
                if(propMapping.size() != 0){
                    isOutputAutoMapping = true;
                }
            }else{
                final SimpleProperty[] props = isFieldOnly(output.getClass()) ? SimpleProperty.getFieldProperties(output) : SimpleProperty.getProperties(output, !isAccessorOnly(output.getClass()));
                for(int i = 0; i < props.length; i++){
                    String inputProp = props[i].getPropertyName();
                    String outputProp = inputProp;
                    if(isEnabledPropertyName(output.getClass(), outputProp) && props[i].isWritable(output.getClass())){
                        if(partPropertyMapping != null && partPropertyMapping.containsKey(outputProp)){
                            Object propMap = partPropertyMapping.get(outputProp);
                            if(propMap instanceof List){
                                Iterator propMaps = ((List)propMap).iterator();
                                while(propMaps.hasNext()){
                                    Object[] inOutProp = (Object[])propMaps.next();
                                    putPropertyMapping(propMapping, (String)inOutProp[0], (ExchangeMapping)inOutProp[1]);
                                }
                            }else{
                                putPropertyMapping(propMapping, (String)((Object[])propMap)[0], (ExchangeMapping)((Object[])propMap)[1]);
                            }
                        }else{
                            if(isCamelSnakeConvert()){
                                inputProp = camelSnakeConvert((String)inputProp);
                            }
                            putPropertyMapping(propMapping, (String)inputProp, new ExchangeMapping(outputProp, null));
                        }
                    }
                }
                if(propMapping.size() != 0){
                    isOutputAutoMapping = true;
                }
            }
            if(propMapping.size() == 0){
                return output;
            }
        }
        if(isMakeSchema 
            &&(((output instanceof Record) && ((Record)output).getRecordSchema() == null)
                || ((output instanceof RecordList) && ((RecordList)output).getRecordSchema() == null))
        ){
            StringBuilder buf = new StringBuilder();
            final Iterator entries = propMapping.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                String inputProp = (String)entry.getKey();
                Object outputProp = entry.getValue();
                try{
                    Property prop = propertyAccess.getProperty(inputProp);
                    Class propType = getPropertyType(prop, input);
                    if(outputProp instanceof ExchangeMapping){
                        buf.append(':').append(((ExchangeMapping)outputProp).outputProperty);
                        buf.append(',').append(propType.getName()).append('\n');
                    }else{
                        List outputProps = (List)outputProp;
                        for(int i = 0, imax = outputProps.size(); i < imax; i++){
                            buf.append(':').append(((ExchangeMapping)outputProps.get(i)).outputProperty);
                            buf.append(',').append(propType.getName()).append('\n');
                        }
                    }
                }catch(IllegalArgumentException e){
                    throw new ConvertException("Input property parse error. property=" + inputProp, e);
                }catch(NoSuchPropertyException e){
                    if(isOutputAutoMapping){
                        continue;
                    }
                    throw new ConvertException("Input property get error. input=" + input + ", property=" + inputProp, e);
                }catch(InvocationTargetException e){
                    throw new ConvertException("Input property get error. input=" + input + ", property=" + inputProp, e);
                }
            }
            if(buf.length() != 0){
                try{
                    if(output instanceof Record){
                        ((Record)output).setSchema(buf.toString());
                    }else{
                        ((RecordList)output).setSchema(buf.toString());
                    }
                }catch(PropertySchemaDefineException e){
                    // 起こらない
                }
            }
        }
        final Iterator entries = propMapping.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry)entries.next();
            String inputProp = (String)entry.getKey();
            final boolean isIndexed = inputProp.endsWith(">-");
            int from = 0;
            int to = 0;
            if(isIndexed){
                final String suffix = inputProp.substring(inputProp.lastIndexOf('['));
                inputProp = inputProp.substring(0, inputProp.lastIndexOf('['));
                try{
                    from = Integer.parseInt(suffix.substring(1, suffix.indexOf(',')));
                    to = Integer.parseInt(suffix.substring(suffix.indexOf(',') + 1, suffix.lastIndexOf(']')));
                }catch(NumberFormatException e){
                }
            }
            final boolean isExpands = inputProp.endsWith("-<");
            if(isExpands){
                inputProp = inputProp.substring(0, inputProp.length() - 2);
            }
            Object value = null;
            try{
                if(isIndexed){
                    Object[] values = new Object[to - from];
                    for(int i = from; i < to; i++){
                        Property property = propertyAccess.getProperty(inputProp + "[0]");
                        IndexedProperty indexedProperty = null;
                        if(property instanceof IndexedProperty){
                            indexedProperty = (IndexedProperty)property;
                        }else{
                            indexedProperty = (IndexedProperty)((NestedProperty)property).getNestedProperty();
                        }
                        values[i - from] = indexedProperty.getProperty(input, i);
                    }
                    value = values;
                }else{
                    value = propertyAccess.get(input, inputProp);
                }
            }catch(IllegalArgumentException e){
                throw new ConvertException("Input property get error. input=" + input + ", property=" + inputProp, e);
            }catch(NoSuchPropertyException e){
                if(isOutputAutoMapping){
                    continue;
                }
                throw new ConvertException("Input property get error. input=" + input + ", property=" + inputProp, e);
            }catch(InvocationTargetException e){
                throw new ConvertException("Input property get error. input=" + input + ", property=" + inputProp, e);
            }
            setOutputProperty(output, entry.getValue(), value, isExpands, isInputAutoMapping);
        }
        return output;
    }
    
    private void setOutputProperty(Object output, Object outputProp, Object value, boolean isExpands, boolean isInputAutoMapping) throws ConvertException{
        if(outputProp instanceof ExchangeMapping){
            ExchangeMapping exchangeMapping = (ExchangeMapping)outputProp;
            Object[] values = null;
            if(isExpands){
                if(value == null){
                    values = null;
                }else if(value instanceof Collection){
                    values = ((Collection)value).toArray();
                }else if(value.getClass().isArray()){
                    values = (Object[])value;
                }
            }
            if(output instanceof RecordList){
                RecordList recordList = (RecordList)output;
                if(isExpands){
                    if(values != null){
                        for(int i = 0; i < values.length; i++){
                            if(recordList.size() == 0){
                                Record rec = recordList.createRecord();
                                setOutputProperty(rec, exchangeMapping, values[i], false, isInputAutoMapping);
                                recordList.add(rec);
                            }else if(i < recordList.size()){
                                setOutputProperty(recordList.get(i), exchangeMapping, values[i], false, isInputAutoMapping);
                            }else{
                                Record preRec = recordList.getRecord(i - 1);
                                Record rec = preRec.cloneRecord();
                                setOutputProperty(rec, exchangeMapping, values[i], false, isInputAutoMapping);
                                recordList.add(rec);
                            }
                        }
                    }
                }else{
                    if(recordList.size() == 0){
                        Record rec = recordList.createRecord();
                        setOutputProperty(rec, exchangeMapping, value, false, isInputAutoMapping);
                        recordList.add(rec);
                    }else{
                        for(int i = 0, imax = recordList.size(); i < imax; i++){
                            setOutputProperty(recordList.get(i), exchangeMapping, value, false, isInputAutoMapping);
                        }
                    }
                }
            }else if(output instanceof Collection){
                Object[] outputs = ((Collection)output).toArray();
                if(outputs.length == 0){
                    Type type = output.getClass().getGenericSuperclass();
                    if(type != null && type instanceof ParameterizedType){
                        Type[] argTypes = ((ParameterizedType)type).getActualTypeArguments();
                        if(argTypes != null && argTypes.length == 1 && argTypes[0] instanceof Class){
                            outputs = (Object[])Array.newInstance((Class)argTypes[0], values.length);
                            try{
                                for(int i = 0; i < outputs.length; i++){
                                    outputs[i] = findImplementsType((Class)argTypes[0]).newInstance();
                                    ((Collection)output).add(outputs[i]);
                                }
                            }catch(IllegalAccessException e){
                                throw new ConvertException("Length of array is 0.", e);
                            }catch(InstantiationException e){
                                throw new ConvertException("Length of array is 0.", e);
                            }
                        }
                    }
                    if(outputs.length == 0){
                        throw new ConvertException("Size of collection is 0.");
                    }
                }
                if(isExpands){
                    for(int i = 0, imax = Math.min(values.length, outputs.length); i < imax; i++){
                        setOutputProperty(outputs[i], exchangeMapping, values[i], false, isInputAutoMapping);
                    }
                }else{
                    for(int i = 0; i < outputs.length; i++){
                        setOutputProperty(outputs[i], exchangeMapping, value, false, isInputAutoMapping);
                    }
                }
            }else if(output.getClass().isArray()){
                Object[] outputs = (Object[])output;
                final Class componentType = output.getClass().getComponentType();
                if(outputs.length == 0){
                    if(componentType.isPrimitive()){
                        throw new ConvertException("Length of array is 0.");
                    }
                    outputs = (Object[])Array.newInstance(componentType, values.length);
                    try{
                        for(int i = 0; i < outputs.length; i++){
                            outputs[i] = findImplementsType(componentType).newInstance();
                        }
                    }catch(IllegalAccessException e){
                        throw new ConvertException("Length of array is 0.", e);
                    }catch(InstantiationException e){
                        throw new ConvertException("Length of array is 0.", e);
                    }
                }
                if(isExpands){
                    for(int i = 0, imax = Math.min(values.length, outputs.length); i < imax; i++){
                        if(outputs[i] == null){
                            if(componentType.isPrimitive()){
                                throw new ConvertException("Element of array is null.");
                            }
                            try{
                                outputs[i] = findImplementsType(componentType).newInstance();
                            }catch(IllegalAccessException e){
                                throw new ConvertException("Element of array is null.", e);
                            }catch(InstantiationException e){
                                throw new ConvertException("Element of array is null.", e);
                            }
                        }
                        setOutputProperty(outputs[i], exchangeMapping, values[i], false, isInputAutoMapping);
                    }
                }else{
                    for(int i = 0; i < outputs.length; i++){
                        if(outputs[i] == null){
                            if(componentType.isPrimitive()){
                                throw new ConvertException("Element of array is null.");
                            }
                            try{
                                outputs[i] = findImplementsType(componentType).newInstance();
                            }catch(IllegalAccessException e){
                                throw new ConvertException("Element of array is null.", e);
                            }catch(InstantiationException e){
                                throw new ConvertException("Element of array is null.", e);
                            }
                        }
                        setOutputProperty(outputs[i], exchangeMapping, value, false, isInputAutoMapping);
                    }
                }
            }else{
                if(value != null
                    && output instanceof Record
                    && ((Record)output).getRecordSchema() != null
                ){
                    Record record = (Record)output;
                    RecordSchema schema = record.getRecordSchema();
                    Property outProp = propertyAccess.getProperty(exchangeMapping.outputProperty);
                    if(outProp instanceof SimpleProperty){
                        PropertySchema propSchema = schema.getPropertySchema(exchangeMapping.outputProperty);
                        if(propSchema != null
                            && (propSchema instanceof RecordPropertySchema || propSchema instanceof RecordListPropertySchema)
                        ){
                            if(propSchema instanceof RecordPropertySchema){
                                Object propValue = record.getProperty(propSchema.getName());
                                if(propValue == null){
                                    DataSet ds = record.getDataSet();
                                    if(ds == null){
                                        throw new ConvertException("NestedRecord can not create, because DataSet is null. propertyName=" + exchangeMapping.outputProperty);
                                    }
                                    propValue = ds.createNestedRecord(((RecordPropertySchema)propSchema).getRecordName());
                                }
                                value = convert(value, propValue, false);
                            }else if(propSchema instanceof RecordListPropertySchema){
                                Object propValue = record.getProperty(propSchema.getName());
                                if(propValue == null){
                                    DataSet ds = record.getDataSet();
                                    if(ds == null){
                                        throw new ConvertException("NestedRecordList can not create, because DataSet is null. propertyName=" + exchangeMapping.outputProperty);
                                    }
                                    propValue = ds.createNestedRecordList(((RecordListPropertySchema)propSchema).getRecordListName());
                                }
                                value = convert(value, propValue, false);
                            }
                        }
                    }else if(outProp instanceof NestedProperty){
                        NestedProperty nestedProp = (NestedProperty)outProp;
                        createNestedPropertyOfRecord(record, nestedProp);
                    }
                }
                try{
                    Property outProp = propertyAccess.getProperty(exchangeMapping.outputProperty);
                    if(exchangeMapping.converter != null){
                        value = exchangeMapping.converter.convert(value);
                    }
                    if(value != null){
                        Class inPropType = value.getClass();
                        Class outPropType = getPropertyType(outProp, output, value);
                        if(!isAssignableFrom(outPropType, inPropType)){
                            if(isNarrowCast() && isNarrowCast(inPropType, outPropType)){
                                value = castPrimitiveWrapper(outPropType, (Number)value);
                            }else if(Collection.class.isAssignableFrom(inPropType) && outPropType.isArray()){
                                if(((Collection)value).size() == 0){
                                    value = Array.newInstance(outPropType.getComponentType(), 0);
                                }else{
                                    Class inComponentType = inPropType.getComponentType();
                                    Iterator itr = ((Collection)value).iterator();
                                    while(itr.hasNext()){
                                        Object element = itr.next();
                                        if(element != null){
                                            inComponentType = element.getClass();
                                            break;
                                        }
                                    }
                                    Object outPropValue = null;
                                    if(outProp.isReadable(output)){
                                        outPropValue = outProp.getProperty(output);
                                    }
                                    if(outPropValue == null){
                                        outPropValue = Array.newInstance(outPropType.getComponentType(), ((Collection)value).size());
                                    }
                                    if(!isAssignableFrom(inComponentType, outPropType.getComponentType())){
                                        value = convert(((Collection)value).toArray(), outPropValue, false);
                                    }else{
                                        System.arraycopy(((Collection)value).toArray(), 0, outPropValue, 0, Array.getLength(outPropValue));
                                        value = outPropValue;
                                    }
                                }
                            }else if(inPropType.isArray() && outPropType.isArray()){
                                if(Array.getLength(value) == 0){
                                    value = Array.newInstance(outPropType.getComponentType(), 0);
                                }else{
                                    Class inComponentType = inPropType.getComponentType();
                                    for(int i = 0; i < Array.getLength(value); i++){
                                        Object element = Array.get(value, i);
                                        if(element != null){
                                            inComponentType = element.getClass();
                                            break;
                                        }
                                    }
                                    Object outPropValue = null;
                                    if(outProp.isReadable(output)){
                                        outPropValue = outProp.getProperty(output);
                                    }
                                    if(outPropValue == null){
                                        outPropValue = Array.newInstance(outPropType.getComponentType(), Array.getLength(value));
                                    }
                                    if(!isAssignableFrom(inComponentType, outPropType.getComponentType())){
                                        value = convert(value, outPropValue, false);
                                    }else{
                                        System.arraycopy(value, 0, outPropValue, 0, Array.getLength(outPropValue));
                                        value = outPropValue;
                                    }
                                }
                            }else if(nestConvertClassTypeMap != null && nestConvertClassTypeMap.getValue(outPropType) != null){
                                Object outPropValue = null;
                                if(outProp.isReadable(output)){
                                    outPropValue = outProp.getProperty(output);
                                }
                                if(outPropValue == null){
                                    outPropValue = findImplementsType(outPropType).newInstance();
                                }
                                value = convert(value, outPropValue, false);
                            }
                        }
                    }
                    if(value != null || !isIgnoreNullProperty){
                        outProp.setProperty(output, value);
                    }
                }catch(IllegalAccessException e){
                    throw new ConvertException("Output property set error. output=" + output + ", property=" + exchangeMapping.outputProperty + ", value=" + value, e);
                }catch(InstantiationException e){
                    throw new ConvertException("Output property set error. output=" + output + ", property=" + exchangeMapping.outputProperty + ", value=" + value, e);
                }catch(IllegalArgumentException e){
                    throw new ConvertException("Output property set error. output=" + output + ", property=" + exchangeMapping.outputProperty + ", value=" + value, e);
                }catch(NoSuchPropertyException e){
                    if(!isInputAutoMapping){
                        throw new ConvertException("Output property set error. output=" + output + ", property=" + exchangeMapping.outputProperty + ", value=" + value, e);
                    }
                }catch(InvocationTargetException e){
                    throw new ConvertException("Output property set error. output=" + output + ", property=" + exchangeMapping.outputProperty + ", value=" + value, e);
                }
            }
        }else{
            List outputProps = (List)outputProp;
            for(int i = 0, imax = outputProps.size(); i < imax; i++){
                setOutputProperty(output, outputProps.get(i), value, false, isInputAutoMapping);
            }
        }
    }

    private void createNestedPropertyOfRecord(Record record, NestedProperty nestedProp) throws ConvertException{
        try{
            Object nestedObj = nestedProp.getThisProperty().getProperty(record);
            if(nestedObj == null){
                if(nestedProp.getThisProperty() instanceof NestedProperty){
                    createNestedPropertyOfRecord(record, (NestedProperty)nestedProp.getThisProperty());
                    return;
                }
                RecordSchema schema = record.getRecordSchema();
                if(schema != null){
                    PropertySchema propSchema = schema.getPropertySchema(nestedProp.getThisProperty().getPropertyName());
                    if(propSchema != null
                        && (propSchema instanceof RecordPropertySchema || propSchema instanceof RecordListPropertySchema)
                    ){
                        if(propSchema instanceof RecordPropertySchema){
                            DataSet ds = record.getDataSet();
                            if(ds == null){
                                throw new ConvertException("NestedRecord can not create, because DataSet is null. propertyName=" + nestedProp.getPropertyName());
                            }
                            Record nestedRecord = ds.createNestedRecord(((RecordPropertySchema)propSchema).getRecordName());
                            nestedProp.getThisProperty().setProperty(record, nestedRecord);
                        }else if(propSchema instanceof RecordListPropertySchema){
                            DataSet ds = record.getDataSet();
                            if(ds == null){
                                throw new ConvertException("NestedRecord can not create, because DataSet is null. propertyName=" + nestedProp.getPropertyName());
                            }
                            RecordList nestedRecordList = ds.createNestedRecordList(((RecordListPropertySchema)propSchema).getRecordListName());
                            nestedProp.getThisProperty().setProperty(record, nestedRecordList);
                        }
                    }
                }
            }else if(nestedObj instanceof Record){
                Record thisRecord = (Record)nestedObj;
                RecordSchema schema = thisRecord.getRecordSchema();
                if(schema != null){
                    PropertySchema propSchema = schema.getPropertySchema(nestedProp.getNestedProperty().getPropertyName());
                    if(propSchema != null
                        && (propSchema instanceof RecordPropertySchema || propSchema instanceof RecordListPropertySchema)
                    ){
                        if(propSchema instanceof RecordPropertySchema){
                            DataSet ds = record.getDataSet();
                            if(ds == null){
                                throw new ConvertException("NestedRecord can not create, because DataSet is null. propertyName=" + nestedProp.getPropertyName());
                            }
                            Record nestedRecord = ds.createNestedRecord(((RecordPropertySchema)propSchema).getRecordName());
                            nestedProp.getNestedProperty().setProperty(thisRecord, nestedRecord);
                        }else if(propSchema instanceof RecordListPropertySchema){
                            DataSet ds = record.getDataSet();
                            if(ds == null){
                                throw new ConvertException("NestedRecord can not create, because DataSet is null. propertyName=" + nestedProp.getPropertyName());
                            }
                            RecordList nestedRecordList = ds.createNestedRecordList(((RecordListPropertySchema)propSchema).getRecordListName());
                            nestedProp.getNestedProperty().setProperty(thisRecord, nestedRecordList);
                        }
                    }
                }
            }
        }catch(NoSuchPropertyException e){
        }catch(InvocationTargetException e){
        }
    }
    
    private void putPropertyMapping(Map propMapping, String inputProperty, ExchangeMapping outputMapping){
        if(propMapping.containsKey(inputProperty)){
            Object om = propMapping.get(inputProperty);
            List list = null;
            if(om instanceof ExchangeMapping){
                list = new ArrayList();
                list.add(om);
                propMapping.put(inputProperty, list);
            }else{
                list = (List)om;
            }
            list.add(outputMapping);
        }else{
            propMapping.put(inputProperty, outputMapping);
        }
    }
    
    private boolean isNarrowCast(Class from, Class to){
        if(from == null || to == null
            || !Number.class.isAssignableFrom(from)
            || (!Number.class.isAssignableFrom(to)
                    && (!to.isPrimitive() || to.equals(Boolean.TYPE)))
            || from.equals(to)
        ){
            return false;
        }
        if(Byte.class.equals(from)
            && !Byte.TYPE.equals(to)
        ){
            return true;
        }else if(Short.class.equals(from)
            && !Short.TYPE.equals(to)
            && (Byte.TYPE.equals(to)
                || Byte.class.equals(to))
        ){
            return true;
        }else if(Integer.class.equals(from)
            && !Integer.TYPE.equals(to)
            && (Short.TYPE.equals(to)
                || Short.class.equals(to)
                || Byte.TYPE.equals(to)
                || Byte.class.equals(to))
        ){
            return true;
        }else if(Long.class.equals(from)
            && !Long.TYPE.equals(to)
            && (Integer.TYPE.equals(to)
                || Integer.class.equals(to)
                || Short.TYPE.equals(to)
                || Short.class.equals(to)
                || Byte.TYPE.equals(to)
                || Byte.class.equals(to))
        ){
            return true;
        }else if(Float.class.equals(from)
            && !Float.TYPE.equals(to)
            && (Long.TYPE.equals(to)
                || Long.class.equals(to)
                || Integer.TYPE.equals(to)
                || Integer.class.equals(to)
                || Short.TYPE.equals(to)
                || Short.class.equals(to)
                || Byte.TYPE.equals(to)
                || Byte.class.equals(to))
        ){
            return true;
        }else if(Double.class.equals(from)
            && !Double.TYPE.equals(to)
            && (Float.TYPE.equals(to)
                || Float.class.equals(to)
                || Long.TYPE.equals(to)
                || Long.class.equals(to)
                || Integer.TYPE.equals(to)
                || Integer.class.equals(to)
                || Short.TYPE.equals(to)
                || Short.class.equals(to)
                || Byte.TYPE.equals(to)
                || Byte.class.equals(to))
        ){
            return true;
        }else if(BigInteger.class.equals(from)
            && !Double.TYPE.equals(to)
            && (Double.class.equals(to)
                || Float.TYPE.equals(to)
                || Float.class.equals(to)
                || Long.TYPE.equals(to)
                || Long.class.equals(to)
                || Integer.TYPE.equals(to)
                || Integer.class.equals(to)
                || Short.TYPE.equals(to)
                || Short.class.equals(to)
                || Byte.TYPE.equals(to)
                || Byte.class.equals(to))
        ){
            return true;
        }else if(BigDecimal.class.equals(from)
            && !BigInteger.class.equals(to)
            && (Double.TYPE.equals(to)
                || Double.class.equals(to)
                || Float.TYPE.equals(to)
                || Float.class.equals(to)
                || Long.TYPE.equals(to)
                || Long.class.equals(to)
                || Integer.TYPE.equals(to)
                || Integer.class.equals(to)
                || Short.TYPE.equals(to)
                || Short.class.equals(to)
                || Byte.TYPE.equals(to)
                || Byte.class.equals(to))
        ){
            return true;
        }
        return false;
    }
    
    private Number castPrimitiveWrapper(Class clazz, Number val){
        if(Byte.TYPE.equals(clazz) || Byte.class.equals(clazz)){
            return Byte.valueOf(val.byteValue());
        }else if(Short.TYPE.equals(clazz) || Short.class.equals(clazz)){
            return Short.valueOf(val.shortValue());
        }else if(Integer.TYPE.equals(clazz) || Integer.class.equals(clazz)){
            return Integer.valueOf(val.intValue());
        }else if(Long.TYPE.equals(clazz) || Long.class.equals(clazz)){
            return new Long(val.longValue());
        }else if(Float.TYPE.equals(clazz) || Float.class.equals(clazz)){
            return new Float(val.floatValue());
        }else if(Double.TYPE.equals(clazz) || Double.class.equals(clazz)){
            return new Double(val.doubleValue());
        }else if(BigInteger.class.equals(clazz)){
            return BigInteger.valueOf(val.longValue());
        }else if(BigDecimal.class.equals(clazz)){
            if(val instanceof BigInteger){
                return new BigDecimal((BigInteger)val);
            }else{
                try{
                    return new BigDecimal(val.toString());
                }catch(NumberFormatException e){
                    return new BigDecimal(val.doubleValue());
                }
            }
        }else{
            return val;
        }
    }
    
    private boolean isNumber(Class clazz){
        if(clazz == null){
            return false;
        }
        if(clazz.isPrimitive()){
            if(Byte.TYPE.equals(clazz)
                || Short.TYPE.equals(clazz)
                || Integer.TYPE.equals(clazz)
                || Long.TYPE.equals(clazz)
                || Float.TYPE.equals(clazz)
                || Double.TYPE.equals(clazz)){
                return true;
            }else{
                return false;
            }
        }else if(Number.class.isAssignableFrom(clazz)){
            return true;
        }else{
            return false;
        }
    }
    
    private boolean isAssignableFrom(Class thisClass, Class thatClass){
        if(isNumber(thisClass) && isNumber(thatClass)){
            if(Byte.TYPE.equals(thisClass)
                || Byte.class.equals(thisClass)){
                return Byte.TYPE.equals(thatClass)
                    || Byte.class.equals(thatClass);
            }else if(Short.TYPE.equals(thisClass)
                || Short.class.equals(thisClass)){
                return Short.TYPE.equals(thatClass)
                    || Short.class.equals(thatClass)
                    || Byte.TYPE.equals(thatClass)
                    || Byte.class.equals(thatClass);
            }else if(Integer.TYPE.equals(thisClass)
                || Integer.class.equals(thisClass)){
                return Integer.TYPE.equals(thatClass)
                    || Integer.class.equals(thatClass)
                    || Short.TYPE.equals(thatClass)
                    || Short.class.equals(thatClass)
                    || Byte.TYPE.equals(thatClass)
                    || Byte.class.equals(thatClass);
            }else if(Long.TYPE.equals(thisClass)
                || Long.class.equals(thisClass)){
                return Long.TYPE.equals(thatClass)
                    || Long.class.equals(thatClass)
                    || Integer.TYPE.equals(thatClass)
                    || Integer.class.equals(thatClass)
                    || Short.TYPE.equals(thatClass)
                    || Short.class.equals(thatClass)
                    || Byte.TYPE.equals(thatClass)
                    || Byte.class.equals(thatClass);
            }else if(BigInteger.class.equals(thisClass)){
                return BigInteger.class.equals(thatClass)
                    || Long.TYPE.equals(thatClass)
                    || Long.class.equals(thatClass)
                    || Integer.TYPE.equals(thatClass)
                    || Integer.class.equals(thatClass)
                    || Short.TYPE.equals(thatClass)
                    || Short.class.equals(thatClass)
                    || Byte.TYPE.equals(thatClass)
                    || Byte.class.equals(thatClass);
            }else if(Float.TYPE.equals(thisClass)
                || Float.class.equals(thisClass)){
                return Float.TYPE.equals(thatClass)
                    || Float.class.equals(thatClass)
                    || Long.TYPE.equals(thatClass)
                    || Long.class.equals(thatClass)
                    || Integer.TYPE.equals(thatClass)
                    || Integer.class.equals(thatClass)
                    || Short.TYPE.equals(thatClass)
                    || Short.class.equals(thatClass)
                    || Byte.TYPE.equals(thatClass)
                    || Byte.class.equals(thatClass);
            }else if(Double.TYPE.equals(thisClass)
                || Double.class.equals(thisClass)){
                return Double.TYPE.equals(thatClass)
                    || Double.class.equals(thatClass)
                    || Float.TYPE.equals(thatClass)
                    || Float.class.equals(thatClass)
                    || Long.TYPE.equals(thatClass)
                    || Long.class.equals(thatClass)
                    || Integer.TYPE.equals(thatClass)
                    || Integer.class.equals(thatClass)
                    || Short.TYPE.equals(thatClass)
                    || Short.class.equals(thatClass)
                    || Byte.TYPE.equals(thatClass)
                    || Byte.class.equals(thatClass);
            }else if(BigDecimal.class.equals(thisClass)){
                return BigDecimal.class.equals(thatClass)
                    || Double.TYPE.equals(thatClass)
                    || Double.class.equals(thatClass)
                    || Float.TYPE.equals(thatClass)
                    || Float.class.equals(thatClass)
                    || BigInteger.class.equals(thatClass)
                    || Long.TYPE.equals(thatClass)
                    || Long.class.equals(thatClass)
                    || Integer.TYPE.equals(thatClass)
                    || Integer.class.equals(thatClass)
                    || Short.TYPE.equals(thatClass)
                    || Short.class.equals(thatClass)
                    || Byte.TYPE.equals(thatClass)
                    || Byte.class.equals(thatClass);
            }
            return true;
        }else if((thisClass.equals(Boolean.class) && thatClass.equals(Boolean.TYPE))
            || (thisClass.equals(Boolean.TYPE) && thatClass.equals(Boolean.class))
        ){
            return true;
        }else if((thisClass.equals(Character.class) && thatClass.equals(Character.TYPE))
            || (thisClass.equals(Character.TYPE) && thatClass.equals(Character.class))
        ){
            return true;
        }else{
            return thisClass.isAssignableFrom(thatClass);
        }
    }
    
    private boolean isCamelSnakeConvert(){
        return (camelSnakeConvertMode == SNAKE_TO_CAMEL || camelSnakeConvertMode == CAMEL_TO_SNAKE);
    }
    
    private String camelSnakeConvert(String str) {
        if(camelSnakeConvertMode != SNAKE_TO_CAMEL && camelSnakeConvertMode != CAMEL_TO_SNAKE) {
            return str;
        }
        if(camelSnakeIgnorePropertyNames != null && Arrays.asList(camelSnakeIgnorePropertyNames).contains(str)) {
            return str;
        }
        StringBuilder sb = null;
        if(camelSnakeConvertMode == CAMEL_TO_SNAKE) {
            sb = new StringBuilder(str.toLowerCase());
            if(isUpperCaseStartCamelProperty && Character.isLowerCase(sb.charAt(0))) {
                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            }
            int i = 0;
            while ((i = sb.indexOf("_")) > 0) {
                char c = sb.charAt(i + 1);
                c = Character.toUpperCase(c);
                sb.setCharAt(i + 1, c);
                sb.deleteCharAt(i);
            }
        } else if(camelSnakeConvertMode == SNAKE_TO_CAMEL) {
            sb = new StringBuilder(str);
            if(isUpperCaseStartCamelProperty && Character.isUpperCase(sb.charAt(0))) {
                sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
            }
            for (int i = 0; i < sb.length(); i++) {
                char c = sb.charAt(i);
                if (Character.isUpperCase(c)) {
                    char c2 = sb.charAt(i + 1);
                    if (Character.isUpperCase(c2)) {
                        sb.setCharAt(i, Character.toLowerCase(c));
                    } else {
                        sb.insert(i, '_');
                        sb.setCharAt(i + 1, Character.toLowerCase(c));
                        i++;
                    }
                }
            }
        }
       return sb.toString();
    }
    
    private class PropertyAccessType{
        
        /**
         * 変換時にJavaオブジェクトのpublicフィールドのみを対象とするかどうかのフラグ。<p>
         * デフォルトは、falseでpublicフィールドのみを対象にはしない。<br>
         */
        public boolean isFieldOnly = false;
        
        /**
         * 変換時にJavaオブジェクトのpublicなgetterのみを対象とするかどうかのフラグ。<p>
         * デフォルトは、trueでpublicなgetterのみを対象にする。<br>
         */
        public boolean isAccessorOnly = true;
        
        /**
         * 出力しないプロパティ名の集合。<p>
         */
        public Set disabledPropertyNames;
        
        /**
         * 出力するプロパティ名の集合。<p>
         */
        public Set enabledPropertyNames;
        
        /**
         * プロパティの名前と型のマップ。<p>
         */
        public Map propertyTypes;
    }
    
    public class ExchangeMapping{
        public String outputProperty;
        public Converter converter;
        
        public ExchangeMapping(String outputProperty, Converter converter){
            this.outputProperty = outputProperty;
            this.converter = converter;
        }
    }
}
