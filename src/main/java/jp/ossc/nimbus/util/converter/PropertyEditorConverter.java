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

import java.util.*;
import java.beans.PropertyEditor;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.*;

/**
 * {@link PropertyEditor}コンバータ。<p>
 * 
 * @author M.Takata
 */
public class PropertyEditorConverter implements FormatConverter{
    
    private static final long serialVersionUID = 1236107411843329600L;
    
    /**
     * 変換種別。<p>
     */
    protected int convertType;
    
    /**
     * フォーマット。<p>
     */
    protected Class type;
    
    /**
     * {@link PropertyEditor}のマップ。<p>
     */
    protected Map editorMap;
    
    /**
     * {@link PropertyEditor}に設定するプロパティのマップ。<p>
     */
    protected Map propertyMap;
    
    /**
     * java.lang.String型オブジェクト→文字列変換を行うコンバータを生成する。<p>
     */
    public PropertyEditorConverter(){
        this(OBJECT_TO_STRING, String.class.getName());
    }
    
    /**
     * 指定された変換種別のコンバータを生成する。<p>
     *
     * @param type 変換種別
     * @param className 変換するオブジェクトの完全修飾クラス名
     * @see #OBJECT_TO_STRING
     * @see #STRING_TO_OBJECT
     */
    public PropertyEditorConverter(int type, String className){
        convertType = type;
        setFormat(className);
    }
    
    /**
     * 変換種別を設定する。<p>
     *
     * @param type 変換種別
     * @see #getConvertType()
     * @see #OBJECT_TO_STRING
     * @see #STRING_TO_OBJECT
     */
    public void setConvertType(int type){
        convertType = type;
    }
    
    /**
     * 変換種別を取得する。<p>
     *
     * @return 変換種別
     * @see #setConvertType(int)
     */
    public int getConvertType(){
        return convertType;
    }
    
    /**
     * 変換するオブジェクトのクラス名を設定する。<p>
     *
     * @param className 変換するオブジェクトの完全修飾クラス名
     */
    public void setFormat(String className){
        try{
            type = Utility.convertStringToClass(
                className,
                false
            );
        }catch(ClassNotFoundException e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    
    /**
     * 変換するオブジェクトのクラス名を取得する。<p>
     *
     * @return 変換するオブジェクトの完全修飾クラス名
     * @see #setFormat(String)
     */
    public String getFormat(){
        return type == null ? java.lang.String.class.getName() : type.getName();
    }
    
    /**
     * {@link PropertyEditor}に設定するプロパティを設定する。<p>
     *
     * @param name プロパティ名
     * @param value プロパティ値
     */
    public void setEditorProperty(String name, Object value){
        if(propertyMap == null){
            propertyMap = new HashMap();
        }
        propertyMap.put(PropertyFactory.createProperty(name), value);
    }
    
    /**
     * {@link PropertyEditor}を登録する。<p>
     *
     * @param clazz 編集する型
     * @param editorClass PropertyEditorの型
     */
    public void registerEditor(Class clazz, Class editorClass){
        if(editorMap == null){
            editorMap = Collections.synchronizedMap(new HashMap());
        }
        editorMap.put(clazz, editorClass);
    }
    
    /**
     * {@link PropertyEditor}を見つける。<p>
     * 
     * @param clazz 編集する型
     * @return PropertyEditor
     */
    protected PropertyEditor findEditor(Class clazz){
        PropertyEditor editor = null;
        final Class editorClass = editorMap == null ? null : (Class)editorMap.get(clazz);
        if(editorClass != null){
            try{
                editor = (PropertyEditor)editorClass.newInstance();
            }catch(ClassCastException e){
            }catch(InstantiationException e){
            }catch(IllegalAccessException e){
            }
        }
        if(editor == null){
            editor = NimbusPropertyEditorManager.findEditor(clazz);
        }
        if(editor != null && propertyMap != null){
            Iterator entries = propertyMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                try{
                    ((Property)entry.getKey()).setProperty(editor, entry.getValue());
                }catch(NoSuchPropertyException e){
                }catch(java.lang.reflect.InvocationTargetException e){
                }
            }
        }
        return editor;
    }
    
    /**
     * 指定されたオブジェクトを変換する。<p>
     *
     * @param obj 変換対象のオブジェクト
     * @return 変換後のオブジェクト
     * @exception ConvertException 変換に失敗した場合
     */
    public Object convert(Object obj) throws ConvertException{
        if(obj == null){
            return null;
        }
        final Class inType = obj.getClass();
        PropertyEditor editor = null;
        switch(convertType){
        case STRING_TO_OBJECT:
            String str = null;
            if(obj instanceof String){
                str = (String)obj;
            }else{
                str = obj.toString();
            }
            editor = findEditor(type);
            if(editor == null){
                throw new ConvertException(
                    "PropertyEditor not found. type=" + type.getName()
                );
            }
            try{
                editor.setAsText(str);
                return editor.getValue();
            }catch(Exception e){
                throw new ConvertException(e);
            }
        case OBJECT_TO_STRING:
            if(obj instanceof String){
                return obj;
            }
            editor = findEditor(inType);
            if(editor == null){
                throw new ConvertException(
                    "PropertyEditor not found. type=" + inType.getName()
                );
            }
            try{
                editor.setValue(obj);
                return editor.getAsText();
            }catch(Exception e){
                throw new ConvertException(e);
            }
        default:
            throw new ConvertException(
                "Invalid convert type : " + convertType
            );
        }
    }
}
