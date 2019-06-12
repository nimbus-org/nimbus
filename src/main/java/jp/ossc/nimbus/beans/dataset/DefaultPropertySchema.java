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

import java.beans.PropertyEditor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.math.*;
import java.io.Serializable;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.io.CSVReader;
import jp.ossc.nimbus.util.converter.*;
import jp.ossc.nimbus.util.validator.*;

/**
 * デフォルトのプロパティスキーマ実装クラス。<p>
 * このクラスには、プロパティのスキーマ情報として、以下の情報が定義できる。<br>
 * <ul>
 *   <li>名前</li>
 *   <li>型</li>
 *   <li>入力変換種類</li>
 *   <li>出力変換種類</li>
 *   <li>制約</li>
 *   <li>主キーフラグ</li>
 * </ul>
 * プロパティスキーマ定義のフォーマットは、<br>
 * <pre>
 *    名前,型,入力変換種類,出力変換種類,制約,主キーフラグ
 * </pre>
 * となっており、名前以外は省略可能である。但し、途中の項目を省略する場合は、区切り子であるカンマは必要である。<br>
 * <p>
 * 次に、各項目の詳細を説明する。<br>
 * <p>
 * 名前は、プロパティの名前を意味し、{@link Record レコード}からプロパティ値を取得する際のキーとなる。<br>
 * <p>
 * 型は、プロパティの型を意味し、Javaの完全修飾クラス名で指定する。<br>
 * <p>
 * 変換種類は、{@link Record#setParseProperty(String, Object)}で入力オブジェクトをプロパティの型に変換し値を設定したり、{@link Record#getFormatProperty(String)}でプロパティを変換し何らかのフォーマットした値を取得するためのものである。<br>
 * 変換には、{@link Converter コンバータ}を使用するため、コンバータの完全修飾クラス名（"jp.ossc.nimbus.util.converter"パッケージの場合は省略可能）または、サービス名を指定することができる。<br>
 * また、コンバータのクラス名を指定する場合は、デフォルトコンストラクタを持つコンバータである必要がある。更に、コンバータクラスに対しては、コンバータのプロパティを指定することができる。<br>
 * コンバータのプロパティの指定は、<br>
 * <pre>
 *   "コンバータの完全修飾クラス名{プロパティ1=値;プロパティ2="値,値";プロパティ3:値の型=値;…}"
 * </pre>
 * というように行う。<br>
 * また、コンバータのクラス名を指定する場合で、複数のコンバータを組み合わせたい場合は、<br>
 * <pre>
 *   "コンバータの完全修飾クラス名{プロパティ1=値;プロパティ2="値,値";…}+コンバータの完全修飾クラス名{プロパティ1=値;プロパティ2="値,値";…}"
 * </pre>
 * というように、コンバータの定義を"+"で連結する。<br>
 * <p>
 * 制約は、プロパティに値を設定する際の、値に対する制約式、または{@link Validator バリデータ}を使用するため、バリデータの完全修飾クラス名（"jp.ossc.nimbus.util.validator"パッケージの場合は省略可能）または、サービス名を指定することができる。<br>
 * 制約式は、等号、不等号、論理演算、四則演算などが可能であるが、式の結果はbooleanとなるようにしなければならない。式言語は、The Apache Jakarta Projectの Commons Jexl(http://jakarta.apache.org/commons/jexl/)の仕様に従う。<br>
 * 値は、"@value@"という文字で表現する。例えば、NOT NULL制約を掛けたければ、"@value@ != null"という制約式になる。<br>
 * また値に対して、プロパティアクセスする事が可能である。例えば、String型のプロパティに長さ５以上という制約を掛けたければ、"@value.length@ >= 5"という制約式になる。プロパティアクセスは、{@link PropertyFactory プロパティファクトリ}の仕様に従う。<br>
 * また、バリデータのクラス名を指定する場合は、デフォルトコンストラクタを持つバリデータである必要がある。更に、バリデータクラスに対しては、バリデータのプロパティを指定することができる。<br>
 * バリデータのプロパティの指定は、コンバータと同様である。"+"による連結は、AND連結となる。<br>
 * <p>
 * 主キーフラグは、{@link RecordList}のスキーマ情報として使用する場合に、このプロパティが主キーである事を指定するもので、主キーな場合は、"1"で指定する。<br>
 * 
 * @author M.Takata
 */
public class DefaultPropertySchema implements PropertySchema, Serializable{
    
    private static final long serialVersionUID = -7076284202113630114L;

    /**
     * オブジェクトのプロパティ集合の区切り接頭辞。<p>
     */
    protected static final String CLASS_PROPERTY_PREFIX = "{";
    
    /**
     * オブジェクトのプロパティ集合の区切り接尾辞。<p>
     */
    protected static final String CLASS_PROPERTY_SUFFIX = "}";
    
    /**
     * オブジェクトの管理用マップ。<p>
     * キーはオブジェクト文字列、値はオブジェクト。<br>
     */
    protected static final ConcurrentMap objectManager = new ConcurrentHashMap();
    
    /**
     * スキーマ文字列。<p>
     */
    protected String schema;
    
    /**
     * プロパティの名前。<p>
     */
    protected String name;
    
    /**
     * プロパティの型。<p>
     */
    protected Class type;
    
    /**
     * プロパティ値のフォーマットコンバータ。<p>
     */
    protected transient Converter formatConverter;
    
    /**
     * プロパティ値のフォーマットコンバータサービス名。<p>
     */
    protected ServiceName formatConverterName;
    
    /**
     * プロパティ値のパースコンバータ。<p>
     */
    protected transient Converter parseConverter;
    
    /**
     * プロパティ値のパースコンバータサービス名。<p>
     */
    protected ServiceName parseConverterName;
    
    /**
     * プロパティ値の設定制約。<p>
     */
    protected transient Constrain constrainExpression;
    
    /**
     * プロパティ値のバリデータ。<p>
     */
    protected transient Validator validator;
    
    /**
     * プロパティ値のバリデータサービス名。<p>
     */
    protected ServiceName validatorName;
    
    /**
     * 主キーかどうかのフラグ。<p>
     * 主キーの場合はtrueで、デフォルトはfalse。<br>
     */
    protected boolean isPrimaryKey;
    
    /**
     * 空のプロパティスキーマを生成する。<p>
     */
    public DefaultPropertySchema(){
    }
    
    /**
     * プロパティスキーマを生成する。<p>
     *
     * @param schema プロパティのスキーマ定義
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public DefaultPropertySchema(String schema) throws PropertySchemaDefineException{
        setSchema(schema);
    }
    
    // PropertySchemaのJavaDoc
    public void setSchema(String schema) throws PropertySchemaDefineException{
        if(schema == null || schema.length() == 0){
            throw new PropertySchemaDefineException(
                schema,
                "The schema is insufficient."
            );
        }
        parseSchemata(schema, parseCSV(schema));
        this.schema = schema;
    }
    
    // PropertySchemaのJavaDoc
    public String getSchema(){
        return schema;
    }
    
    /**
     * CSV文字列をパースする。<p>
     * ,を区切り文字、\を1文字エスケープ、""で囲むとブロックエスケープとして、パースする。<br>
     *
     * @param text CSV文字列
     * @return セパレートされた文字列のリスト
     */
    protected static List parseCSV(String text){
        return CSVReader.toList(
            text,
            null,
            ',',
            '\\',
            '"',
            "",
            null,
            true,
            true,
            true,
            false
        );
    }
    
    /**
     * プロパティスキーマの各項目をパースする。<p>
     *
     * @param schema プロパティスキーマ全体
     * @param schemata スキーマ項目のリスト
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    protected void parseSchemata(String schema, List schemata)
     throws PropertySchemaDefineException{
        if(schemata.size() == 0){
            throw new PropertySchemaDefineException("Name must be specified.");
        }
        for(int i = 0, max = schemata.size(); i < max; i++){
            parseSchema(schema, i, (String)schemata.get(i));
        }
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
            parseParseConverter(schema, val);
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
     * プロパティスキーマの名前の項目をパースする。<p>
     *
     * @param schema プロパティスキーマ全体
     * @param val スキーマ項目
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    protected void parseName(String schema, String val)
     throws PropertySchemaDefineException{
        name = val;
    }
    
    /**
     * プロパティスキーマの型の項目をパースする。<p>
     *
     * @param schema プロパティスキーマ全体
     * @param val スキーマ項目
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    protected void parseType(String schema, String val)
     throws PropertySchemaDefineException{
        if(val != null && val.length() != 0){
            try{
                type = jp.ossc.nimbus.core.Utility.convertStringToClass(val, false);
            }catch(ClassNotFoundException e){
                throw new PropertySchemaDefineException(
                    schema,
                    "The type is illegal.",
                    e
                );
            }
        }
    }
    
    /**
     * プロパティスキーマの入力変換の項目をパースする。<p>
     *
     * @param schema プロパティスキーマ全体
     * @param val スキーマ項目
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    protected void parseParseConverter(String schema, String val)
     throws PropertySchemaDefineException{
        Object conv = parseConverter(schema, val);
        if(conv != null){
            if(conv instanceof ServiceName){
                parseConverterName = (ServiceName)conv;
            }else{
                parseConverter = (Converter)conv;
            }
        }
    }
    
    /**
     * プロパティスキーマの出力変換の項目をパースする。<p>
     *
     * @param schema プロパティスキーマ全体
     * @param val スキーマ項目
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    protected void parseFormatConverter(String schema, String val)
     throws PropertySchemaDefineException{
        Object conv = parseConverter(schema, val);
        if(conv != null){
            if(conv instanceof ServiceName){
                formatConverterName = (ServiceName)conv;
            }else{
                formatConverter = (Converter)conv;
            }
        }
    }
    
    /**
     * オブジェクト文字列をパースする。<p>
     *
     * @param schema プロパティスキーマ全体
     * @param val オブジェクト文字列
     * @return オブジェクト
     * @exception ClassNotFoundException 指定されたクラス名のクラスが見つからない場合
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    protected Object parseObject(String schema, String val, String packageName)
     throws ClassNotFoundException, PropertySchemaDefineException{
        Object object = objectManager.get(val);
        if(object != null){
            return object;
        }
        String className = val;
        List properties = null;
        final int propStartIndex = className.indexOf(CLASS_PROPERTY_PREFIX);
        if(propStartIndex != -1
             && className.endsWith(CLASS_PROPERTY_SUFFIX)){
            properties = CSVReader.toList(
                className.substring(
                    propStartIndex + 1,
                    className.length() - 1
                ),
                null,
                ';',
                '\\',
                '"',
                null,
                null,
                true,
                false,
                true,
                false
            );
            className = className.substring(0, propStartIndex);
        }
        Class clazz = null;
        try{
            clazz = jp.ossc.nimbus.core.Utility.convertStringToClass(
                className,
                true
            );
        }catch(ClassNotFoundException e){
            if(className.indexOf('.') == -1){
                try{
                    clazz = jp.ossc.nimbus.core.Utility.convertStringToClass(
                        packageName + '.' + className,
                        true
                    );
                }catch(ClassNotFoundException e2){
                    throw e;
                }
            }else{
                throw e;
            }
        }
        try{
            object = clazz.newInstance();
        }catch(InstantiationException e){
            throw new PropertySchemaDefineException(schema, "Illegal object format : " + val, e);
        }catch(IllegalAccessException e){
            throw new PropertySchemaDefineException(schema, "Illegal object format : " + val, e);
        }
        
        if(properties != null && properties.size() != 0){
            for(int i = 0, imax = properties.size(); i < imax; i++){
                String property = (String)properties.get(i);
                if(property == null || property.length() < 2){
                    throw new PropertySchemaDefineException(schema, "Illegal object format : " + val);
                }
                final int index = property.indexOf('=');
                if(index == -1 || index == 0){
                    throw new PropertySchemaDefineException(schema, "Illegal object format : " + val);
                }
                String propName = property.substring(0, index);
                Class propType = null;
                int index2 = propName.indexOf(':');
                if(index2 != -1 && index2 != 0 && index2 != propName.length() - 1){
                    String propTypeStr = propName.substring(index2 + 1);
                    try{
                        propType = jp.ossc.nimbus.core.Utility.convertStringToClass(propTypeStr, false);
                        propName = propName.substring(0, index2);
                    }catch(ClassNotFoundException e){
                        throw new PropertySchemaDefineException(
                            schema,
                            "The type of property is illegal. property=" + propName + ",type=" + propTypeStr,
                            e
                        );
                    }
                }
                String propValStr = property.substring(index + 1);
                Property prop = null;
                try{
                    prop = PropertyFactory.createProperty(propName);
                }catch(IllegalArgumentException e){
                    throw new PropertySchemaDefineException(schema, "Illegal object format : " + val, e);
                }
                try{
                    if(propType == null){
                        propType = prop.getPropertyType(object);
                    }
                    if(propType == null){
                        propType = java.lang.String.class;
                    }
                    PropertyEditor editor = NimbusPropertyEditorManager.findEditor(propType);
                    Object propVal = propValStr;
                    if(editor == null){
                        index2 = propValStr.lastIndexOf(".");
                        if(index2 > 0 && index2 != propValStr.length() - 1){
                            className = propValStr.substring(0, index2);
                            final String fieldName = propValStr.substring(index2 + 1);
                            try{
                                Class clazz2 = null;
                                try{
                                    clazz2 = jp.ossc.nimbus.core.Utility.convertStringToClass(className, false);
                                }catch(ClassNotFoundException e){
                                    if(className.indexOf('.') == -1){
                                        try{
                                            clazz2 = jp.ossc.nimbus.core.Utility.convertStringToClass(
                                                packageName + '.' + className,
                                                false
                                            );
                                        }catch(ClassNotFoundException e2){
                                            throw e;
                                        }
                                    }else{
                                        throw e;
                                    }
                                }
                                Field field = clazz2.getField(fieldName);
                                if(propType.isAssignableFrom(field.getType())){
                                    propVal = field.get(null);
                                }
                            }catch(ClassNotFoundException e){
                            }catch(NoSuchFieldException e){
                            }catch(SecurityException e){
                            }catch(IllegalArgumentException e){
                            }catch(IllegalAccessException e){
                            }
                        }
                    }else if(editor != null){
                        try{
                            editor.setAsText(propValStr);
                        }catch(RuntimeException e){
                            try{
                                editor.setAsText(clazz.getName() + '.' + propValStr);
                            }catch(RuntimeException e2){
                                throw e;
                            }
                        }
                        propVal = editor.getValue();
                    }
                    prop.setProperty(object, propVal);
                }catch(NoSuchPropertyException e){
                    throw new PropertySchemaDefineException(schema, "Illegal object format : " + val, e);
                }catch(InvocationTargetException e){
                    throw new PropertySchemaDefineException(schema, "Illegal object format : " + val, e);
                }catch(RuntimeException e){
                    throw new PropertySchemaDefineException(schema, "Illegal object format : " + val, e);
                }
            }
        }
        Object old = objectManager.putIfAbsent(val, object);
        if(old != null){
            object = old;
        }
        return object;
    }
    
    /**
     * プロパティスキーマの変換の項目をパースする。<p>
     *
     * @param schema プロパティスキーマ全体
     * @param val スキーマ項目
     * @return {@link Converter コンバータ}またはコンバータの{@link ServiceName サービス名}
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    protected Object parseConverter(String schema, String val)
     throws PropertySchemaDefineException{
        if(val != null && val.length() != 0){
            if(val.indexOf('+') == -1){
                try{
                    Object obj = parseObject(schema, val, Converter.class.getPackage().getName());
                    if(!(obj instanceof Converter)){
                        throw new PropertySchemaDefineException(schema, "Converter dose not implement Converter.");
                    }
                    return obj;
                }catch(ClassNotFoundException e){
                    final ServiceNameEditor serviceNameEditor
                         = new ServiceNameEditor();
                    try{
                        serviceNameEditor.setAsText(val);
                    }catch(IllegalArgumentException e2){
                        throw new PropertySchemaDefineException(
                            schema,
                            "Converter is illegal.",
                            e2
                        );
                    }
                    return (ServiceName)serviceNameEditor.getValue();
                }
            }
            List converterStrList = CSVReader.toList(
                val,
                null,
                '+',
                '\\',
                '"',
                "",
                null,
                true,
                true,
                true,
                false
            );
            Converter[] converters = new Converter[converterStrList.size()];
            for(int i = 0; i < converterStrList.size(); i++){
                try{
                    Object obj = parseObject(schema, (String)converterStrList.get(i), Converter.class.getPackage().getName());
                    if(!(obj instanceof Converter)){
                        throw new PropertySchemaDefineException(schema, "Converter dose not implement Converter.");
                    }
                    converters[i] = (Converter)obj;
                }catch(ClassNotFoundException e){
                    throw new PropertySchemaDefineException(
                        schema,
                        "Converter is illegal.",
                        e
                    );
                }
            }
            return new CustomConverter(converters);
        }
        return null;
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
            try{
                constrainExpression = new Constrain(val);
            }catch(Exception e){
                try{
                    Object valid = parseValidator(schema, val);
                    if(valid != null){
                        if(valid instanceof ServiceName){
                            parseConverterName = (ServiceName)valid;
                        }else{
                            validator = (Validator)valid;
                        }
                        return;
                    }
                }catch(PropertySchemaDefineException e2){
                }
                throw new PropertySchemaDefineException(
                    this.toString(),
                    "Illegal constrain : " + val,
                    e
                );
            }
        }
    }
    
    /**
     * プロパティスキーマの制約の項目をValidatorとしてパースする。<p>
     *
     * @param schema プロパティスキーマ全体
     * @param val スキーマ項目
     * @return {@link Converter コンバータ}またはコンバータの{@link ServiceName サービス名}
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    protected Object parseValidator(String schema, String val)
     throws PropertySchemaDefineException{
        if(val != null && val.length() != 0){
            if(val.indexOf('+') == -1){
                try{
                    Object obj = parseObject(schema, val, Validator.class.getPackage().getName());
                    if(!(obj instanceof Validator)){
                        throw new PropertySchemaDefineException(schema, "Validator dose not implement Validator.");
                    }
                    return obj;
                }catch(ClassNotFoundException e){
                    final ServiceNameEditor serviceNameEditor
                         = new ServiceNameEditor();
                    try{
                        serviceNameEditor.setAsText(val);
                    }catch(IllegalArgumentException e2){
                        throw new PropertySchemaDefineException(
                            schema,
                            "Converter is illegal.",
                            e2
                        );
                    }
                    return (ServiceName)serviceNameEditor.getValue();
                }
            }
            List validatorStrList = CSVReader.toList(
                val,
                null,
                '+',
                '\\',
                '"',
                "",
                null,
                true,
                true,
                true,
                false
            );
            CombinationValidator comb = new CombinationValidator();
            for(int i = 0; i < validatorStrList.size(); i++){
                try{
                    Object obj = parseObject(schema, (String)validatorStrList.get(i), Validator.class.getPackage().getName());
                    if(!(obj instanceof Validator)){
                        throw new PropertySchemaDefineException(schema, "Validator dose not implement Validator.");
                    }
                    if(i == 0){
                        comb.add((Validator)obj);
                    }else{
                        comb.and((Validator)obj);
                    }
                }catch(ClassNotFoundException e){
                    throw new PropertySchemaDefineException(
                        schema,
                        "Validator is illegal.",
                        e
                    );
                }
            }
            return comb;
        }
        return null;
    }
    
    /**
     * プロパティスキーマの主キーの項目をパースする。<p>
     *
     * @param schema プロパティスキーマ全体
     * @param val スキーマ項目
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    protected void parsePrimaryKey(String schema, String val)
     throws PropertySchemaDefineException{
        isPrimaryKey = val != null && "1".equals(val) ? true : false;
    }
    
    // PropertySchemaのJavaDoc
    public String getName(){
        return name;
    }
    
    // PropertySchemaのJavaDoc
    public Class getType(){
        return type == null ? Object.class : type;
    }
    
    public boolean isPrimaryKey(){
        return isPrimaryKey;
    }
    
    /**
     * パース用のコンバータを取得する。<p>
     *
     * @return コンバータ
     */
    public Converter getParseConverter(){
        if(parseConverter != null){
            return parseConverter;
        }
        if(parseConverterName != null){
            return (Converter)ServiceManagerFactory
                .getServiceObject(parseConverterName);
        }
        return null;
    }
    
    /**
     * フォーマット用のコンバータを取得する。<p>
     *
     * @return コンバータ
     */
    public Converter getFormatConverter(){
        if(formatConverter != null){
            return formatConverter;
        }
        if(formatConverterName != null){
            return (Converter)ServiceManagerFactory
                .getServiceObject(formatConverterName);
        }
        return null;
    }
    
    /**
     * 制約を取得する。<p>
     *
     * @return 制約
     */
    public String getConstrain(){
        return constrainExpression == null
             ? null : constrainExpression.constrain;
    }
    
    /**
     * バリデータを取得する。<p>
     *
     * @return バリデータ
     */
    public Validator getValidator(){
        if(validator != null){
            return validator;
        }
        if(validatorName != null){
            return (Validator)ServiceManagerFactory
                .getServiceObject(validatorName);
        }
        return null;
    }
    
    // PropertySchemaのJavaDoc
    public Object set(Object val) throws PropertySetException{
        return checkSchema(val);
    }
    
    // PropertySchemaのJavaDoc
    public Object get(Object val) throws PropertyGetException{
        return val;
    }
    
    // PropertySchemaのJavaDoc
    public Object format(Object val) throws PropertyGetException{
        Object result = val;
        Converter converter = null;
        try{
            converter = getFormatConverter();
        }catch(ServiceNotFoundException e){
            throw new PropertyGetException(this, e);
        }
        if(converter == null){
            if(result == null){
                return result;
            }
            final Class type = getType();
            if(type != null){
                final PropertyEditor editor
                     = NimbusPropertyEditorManager.findEditor(type);
                if(editor != null){
                    try{
                        editor.setValue(result);
                        result = editor.getAsText();
                    }catch(RuntimeException e){
                        throw new PropertySetException(this, e);
                    }
                }
            }
        }else{
            try{
                result = converter.convert(result);
            }catch(ConvertException e){
                throw new PropertyGetException(this, e);
            }
        }
        return result;
    }
    
    // PropertySchemaのJavaDoc
    public Object parse(Object val) throws PropertySetException{
        Object result = val;
        Converter converter = null;
        try{
            converter = getParseConverter();
        }catch(ServiceNotFoundException e){
            throw new PropertySetException(this, e);
        }
        if(converter != null){
            try{
                result = converter.convert(result);
            }catch(ConvertException e){
                throw new PropertySetException(this, e);
            }
        }
        if(result == null){
            return result;
        }
        final Class type = getType();
        if(type == null){
            return result;
        }
        final Class inType = result.getClass();
        if(type.isAssignableFrom(inType)){
            return result;
        }
        if(result instanceof String){
            result = parseByPropertyEditor((String)result, type);
        }else if(type.isArray() && inType.equals(String[].class)){
            final String[] array = (String[])result;
            final Class componentType = type.getComponentType();
            result = Array.newInstance(
                componentType,
                array.length
            );
            for(int i = 0; i < array.length; i++){
                Array.set(
                    result,
                    i,
                    parseByPropertyEditor(array[i], componentType)
                );
            }
        }else{
            throw new PropertySetException(this, "Counld not parse, because type is unmatch. in=" + inType.getName() + ", out=" + type.getName());
        }
        return result;
    }
    
    private Object parseByPropertyEditor(String str, Class editType)
     throws PropertySetException{
        if(str.length() == 0
            && (Number.class.isAssignableFrom(editType)
                || Boolean.class.equals(editType))
        ){
            return null;
        }
        if(editType.isPrimitive()
            && str.length() == 0
        ){
            if(editType.equals(Boolean.TYPE)){
                return Boolean.FALSE;
            }else if(editType.equals(Byte.TYPE)){
                return new Byte((byte)0);
            }else if(editType.equals(Short.TYPE)){
                return new Short((short)0);
            }else if(editType.equals(Integer.TYPE)){
                return new Integer(0);
            }else if(editType.equals(Long.TYPE)){
                return new Long(0l);
            }else if(editType.equals(Float.TYPE)){
                return new Float(0f);
            }else if(editType.equals(Double.TYPE)){
                return new Double(0d);
            }
        }
        final PropertyEditor editor
             = NimbusPropertyEditorManager.findEditor(editType);
        if(editor != null){
            try{
                editor.setAsText(str);
                return editor.getValue();
            }catch(RuntimeException e){
                throw new PropertySetException(this, e);
            }
        }
        return str;
    }
    
    /**
     * プロパティの値がスキーマ定義に適合しているかチェックする。<p>
     *
     * @param val プロパティの値
     * @return プロパティの値
     * @exception PropertySchemaCheckException プロパティの値がスキーマ定義に適合していない場合
     */
    protected Object checkSchema(Object val) throws PropertySchemaCheckException{
        val = checkType(val);
        return val;
    }
    
    /**
     * プロパティの値がスキーマ定義の型に適合しているかチェックする。<p>
     *
     * @param val プロパティの値
     * @return プロパティの値
     * @exception PropertySchemaCheckException プロパティの値がスキーマ定義に適合していない場合
     */
    protected Object checkType(Object val) throws PropertySchemaCheckException{
        if(type == null || val == null){
            return val;
        }
        
        Class clazz = val.getClass();
        if(!isAssignableFrom(type, clazz)){
            try{
                val = parse(val);
                clazz = val.getClass();
            }catch(PropertySetException e){
                throw new PropertySchemaCheckException(
                    this,
                    "The type is unmatch. type=" + clazz.getName(),
                    e
                );
            }
        }
        if(Number.class.isAssignableFrom(clazz)
             && ((!type.isPrimitive() && !type.equals(clazz))
                    || (type.isPrimitive() && !type.equals(getPrimitiveClass(clazz))))
        ){
            val = castPrimitiveWrapper(type, (Number)val);
        }
        return val;
    }
    
    private Class getPrimitiveClass(Class type){
        if(type.equals(Byte.class)){
            return Byte.TYPE;
        }else if(type.equals(Short.class)){
            return Short.TYPE;
        }else if(type.equals(Integer.class)){
            return Integer.TYPE;
        }else if(type.equals(Long.class)){
            return Long.TYPE;
        }else if(type.equals(Float.class)){
            return Float.TYPE;
        }else if(type.equals(Double.class)){
            return Double.TYPE;
        }else{
            return null;
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
    
    private Number castPrimitiveWrapper(Class clazz, Number val){
        if(Byte.class.equals(clazz) || Byte.TYPE.equals(clazz)){
            return new Byte(val.byteValue());
        }else if(Short.class.equals(clazz) || Short.TYPE.equals(clazz)){
            return new Short(val.shortValue());
        }else if(Integer.class.equals(clazz) || Integer.TYPE.equals(clazz)){
            return new Integer(val.intValue());
        }else if(Long.class.equals(clazz) || Long.TYPE.equals(clazz)){
            return new Long(val.longValue());
        }else if(BigInteger.class.equals(clazz)){
            return BigInteger.valueOf(val.longValue());
        }else if(Float.class.equals(clazz) || Float.TYPE.equals(clazz)){
            return new Float(val.floatValue());
        }else if(Double.class.equals(clazz) || Double.TYPE.equals(clazz)){
            return new Double(val.doubleValue());
        }else if(BigDecimal.class.equals(clazz)){
            if(val instanceof BigInteger){
                return new BigDecimal((BigInteger)val);
            }else{
                return new BigDecimal(val.doubleValue());
            }
        }else{
            return val;
        }
    }
    
    // PropertySchemaのJavaDoc
    public boolean validate(Object val) throws PropertyValidateException{
        if(constrainExpression != null){
            try{
                return constrainExpression.evaluate(val);
            }catch(Exception e){
                throw new PropertyValidateException(
                    this,
                    "The constrain is illegal."
                        + "constrain=" + constrainExpression.constrain
                        + ", value=" + val,
                    e
                );
            }
        }else{
            Validator validator = getValidator();
            if(validator == null){
                return true;
            }else{
                try{
                    return validator.validate(val);
                }catch(ValidateException e){
                    throw new PropertyValidateException(
                        this,
                        "Validate error."
                            + "validator=" + validator
                            + ", value=" + val,
                        e
                    );
                }
            }
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
            .append(constrainExpression == null
                 ? (validator == null ? (validatorName == null ? null : validatorName) : validator) : constrainExpression.constrain);
        if(isPrimaryKey){
            buf.append(",isPrimaryKey=").append(isPrimaryKey);
        }
        buf.append('}');
        return buf.toString();
    }
    
    /**
     * 制約。<p>
     *
     * @author M.Takata
     */
    protected static class Constrain{
        
        /**
         * 制約式中のプロパティ値を表す文字。<p>
         */
        protected static final String CONSTRAIN_TARGET_KEY = "value";
        /**
         * 制約式中のプロパティ値を表す文字。<p>
         */
        protected static final String CONSTRAIN_DELIMITER = "@";
        
        /**
         * 制約。<p>
         */
        public final String constrain;
        
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
        protected Expression expression;
        
        /**
         * 制約を生成する。<p>
         *
         * @param constrain 制約式文字列
         * @exception Exception 制約式文字列の解釈に失敗した場合
         */
        public Constrain(String constrain) throws Exception{
            this.constrain = constrain;
            
            StringTokenizer token = new StringTokenizer(constrain, CONSTRAIN_DELIMITER, true);
            
            boolean keyFlg = false;
            
            String beforeToken = null;
            StringBuilder buf = new StringBuilder();
            
            while(token.hasMoreTokens()){
                String str = token.nextToken();
                if(!keyFlg){
                    if(CONSTRAIN_DELIMITER.equals(str)){
                        keyFlg = true;
                    }else{
                        buf.append(str);
                    }
                }else if(CONSTRAIN_DELIMITER.equals(str)){
                    keyFlg = false;
                    if(beforeToken != null){
                        final String tmpKey = "_constrainKey" + keyList.size();
                        keyList.add(tmpKey);
                        buf.append(tmpKey);
                        if(!beforeToken.startsWith(CONSTRAIN_TARGET_KEY)){
                            throw new IllegalArgumentException(constrain);
                        }
                        if(CONSTRAIN_TARGET_KEY.equals(beforeToken)){
                            properties.add(null);
                        }else{
                            if(beforeToken.charAt(CONSTRAIN_TARGET_KEY.length()) == '.'){
                                beforeToken = beforeToken.substring(CONSTRAIN_TARGET_KEY.length() + 1);
                            }else{
                                beforeToken = beforeToken.substring(CONSTRAIN_TARGET_KEY.length());
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
            
            expression = ExpressionFactory.createExpression(buf.toString());
            evaluate("", true);
        }
        
        /**
         * 指定された値が制約に適合しているか評価する。<p>
         *
         * @param object 制約対象の値
         * @return 制約に適合している場合true
         * @exception Exception 評価に失敗した場合
         */
        public boolean evaluate(Object object) throws Exception{
            return evaluate(object, false);
        }
        
        /**
         * 指定された値が制約に適合しているか評価する。<p>
         *
         * @param object 制約対象の値
         * @param isTest 制約式の結果の型が制約対象の値に依存する場合、制約式結果がbooleanとなる事を保障できないので、型チェックを行わないようにするフラグ
         * @return 制約に適合している場合true
         * @exception Exception 評価に失敗した場合
         */
        protected boolean evaluate(Object object, boolean isTest) throws Exception{
            JexlContext jexlContext = JexlHelper.createContext();
            jexlContext.getVars().put(CONSTRAIN_TARGET_KEY, object);
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
                    }catch(java.lang.reflect.InvocationTargetException e){
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
    } 
}
