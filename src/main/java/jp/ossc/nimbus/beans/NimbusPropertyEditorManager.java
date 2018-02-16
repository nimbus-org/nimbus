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
package jp.ossc.nimbus.beans;

import java.beans.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.util.*;

/**
 * NimbusのPropertyEditorを管理するPropertyEditorManager。<p>
 * Nimbusで開発されたPropertyEditorが全て登録されているPropertyEditorManagerクラスである。<br>
 * あらかじめ登録されてPropertyEditorは、以下のものである。<br>
 * <table border="1">
 *   <tr bgcolor="#CCCCFF"><th>編集するクラス</th><th>PropertyEditor</th></tr>
 *   <tr><td>char</td><td>{@link jp.ossc.nimbus.beans.CharacterEditor}</td></tr>
 *   <tr><td>boolean</td><td>{@link jp.ossc.nimbus.beans.BooleanEditor}</td></tr>
 *   <tr><td>short</td><td>{@link jp.ossc.nimbus.beans.ShortEditor}</td></tr>
 *   <tr><td>int</td><td>{@link jp.ossc.nimbus.beans.IntEditor}</td></tr>
 *   <tr><td>long</td><td>{@link jp.ossc.nimbus.beans.LongEditor}</td></tr>
 *   <tr><td>float</td><td>{@link jp.ossc.nimbus.beans.FloatEditor}</td></tr>
 *   <tr><td>double</td><td>{@link jp.ossc.nimbus.beans.DoubleEditor}</td></tr>
 *   <tr><td>byte[]</td><td>{@link jp.ossc.nimbus.beans.ByteArrayEditor}</td></tr>
 *   <tr><td>char[]</td><td>{@link jp.ossc.nimbus.beans.CharacterArrayEditor}</td></tr>
 *   <tr><td>short[]</td><td>{@link jp.ossc.nimbus.beans.ShortArrayEditor}</td></tr>
 *   <tr><td>int[]</td><td>{@link jp.ossc.nimbus.beans.IntArrayEditor}</td></tr>
 *   <tr><td>long[]</td><td>{@link jp.ossc.nimbus.beans.LongArrayEditor}</td></tr>
 *   <tr><td>float[]</td><td>{@link jp.ossc.nimbus.beans.FloatArrayEditor}</td></tr>
 *   <tr><td>double[]</td><td>{@link jp.ossc.nimbus.beans.DoubleArrayEditor}</td></tr>
 *   <tr><td>boolean[]</td><td>{@link jp.ossc.nimbus.beans.BooleanArrayEditor}</td></tr>
 *   <tr><td>java.math.BigInteger</td><td>{@link jp.ossc.nimbus.beans.BigIntegerEditor}</td></tr>
 *   <tr><td>java.math.BigInteger[]</td><td>{@link jp.ossc.nimbus.beans.BigIntegerArrayEditor}</td></tr>
 *   <tr><td>java.math.BigDecimal</td><td>{@link jp.ossc.nimbus.beans.BigDecimalEditor}</td></tr>
 *   <tr><td>java.math.BigDecimal[]</td><td>{@link jp.ossc.nimbus.beans.BigDecimalArrayEditor}</td></tr>
 *   <tr><td>java.lang.String</td><td>{@link jp.ossc.nimbus.beans.StringEditor}</td></tr>
 *   <tr><td>java.lang.String[]</td><td>{@link jp.ossc.nimbus.beans.StringArrayEditor}</td></tr>
 *   <tr><td>java.lang.Class</td><td>{@link jp.ossc.nimbus.beans.ClassEditor}</td></tr>
 *   <tr><td>java.lang.Class[]</td><td>{@link jp.ossc.nimbus.beans.ClassArrayEditor}</td></tr>
 *   <tr><td>java.lang.reflect.Method</td><td>{@link jp.ossc.nimbus.beans.MethodEditor}</td></tr>
 *   <tr><td>java.lang.reflect.Method[]</td><td>{@link jp.ossc.nimbus.beans.MethodArrayEditor}</td></tr>
 *   <tr><td>java.lang.reflect.Constructor</td><td>{@link jp.ossc.nimbus.beans.ConstructorEditor}</td></tr>
 *   <tr><td>java.io.File</td><td>{@link jp.ossc.nimbus.beans.FileEditor}</td></tr>
 *   <tr><td>java.io.File[]</td><td>{@link jp.ossc.nimbus.beans.FileArrayEditor}</td></tr>
 *   <tr><td>java.net.URL</td><td>{@link jp.ossc.nimbus.beans.URLEditor}</td></tr>
 *   <tr><td>java.sql.Date</td><td>{@link jp.ossc.nimbus.beans.SQLDateEditor}</td></tr>
 *   <tr><td>java.sql.Time</td><td>{@link jp.ossc.nimbus.beans.TimeEditor}</td></tr>
 *   <tr><td>java.sql.Timestamp</td><td>{@link jp.ossc.nimbus.beans.TimestampEditor}</td></tr>
 *   <tr><td>java.util.Date</td><td>{@link jp.ossc.nimbus.beans.DateEditor}</td></tr>
 *   <tr><td>java.util.Locale</td><td>{@link jp.ossc.nimbus.beans.LocaleEditor}</td></tr>
 *   <tr><td>java.util.Properties</td><td>{@link jp.ossc.nimbus.beans.PropertiesEditor}</td></tr>
 *   <tr><td>java.util.Map</td><td>{@link jp.ossc.nimbus.beans.MapEditor}</td></tr>
 *   <tr><td>java.util.SortedMap</td><td>{@link jp.ossc.nimbus.beans.SortedMapEditor}</td></tr>
 *   <tr><td>{@link jp.ossc.nimbus.core.ServiceName}</td><td>{@link jp.ossc.nimbus.beans.ServiceNameEditor}</td></tr>
 *   <tr><td>{@link jp.ossc.nimbus.core.ServiceName jp.ossc.nimbus.core.ServiceName[]}</td><td>{@link jp.ossc.nimbus.beans.ServiceNameArrayEditor}</td></tr>
 *   <tr><td>{@link jp.ossc.nimbus.core.ServiceNameRef}</td><td>{@link jp.ossc.nimbus.beans.ServiceNameRefEditor}</td></tr>
 *   <tr><td>{@link jp.ossc.nimbus.core.ServiceNameRef jp.ossc.nimbus.core.ServiceNameRef[]}</td><td>{@link jp.ossc.nimbus.beans.ServiceNameRefArrayEditor}</td></tr>
 *   <tr><td>{@link jp.ossc.nimbus.beans.Property}</td><td>{@link jp.ossc.nimbus.beans.BeanPropertyEditor}</td></tr>
 *   <tr><td>{@link jp.ossc.nimbus.beans.Property jp.ossc.nimbus.beans.Property[]}</td><td>{@link jp.ossc.nimbus.beans.PropertyArrayEditor}</td></tr>
 * </table>
 *
 * @author M.Takata
 */
public class NimbusPropertyEditorManager extends PropertyEditorManager{
    
    private static final ClassMappingTree propertyEditors
         = new ClassMappingTree();
    
    private static final Class NOT_FOUND_EDITOR_CLASS = NotFoundPropertyEditor.class;
    
    static{
        
        // デフォルトのPropertyEditorを登録する
        propertyEditors.add(java.util.Properties.class, PropertiesEditor.class);
        propertyEditors.add(ServiceName[].class, ServiceNameArrayEditor.class);
        propertyEditors.add(ServiceName.class, ServiceNameEditor.class);
        propertyEditors.add(
            ServiceNameRef[].class,
            ServiceNameRefArrayEditor.class
        );
        propertyEditors.add(ServiceNameRef.class, ServiceNameRefEditor.class);
        propertyEditors.add(String[].class, StringArrayEditor.class);
        propertyEditors.add(String.class, StringEditor.class);
        propertyEditors.add(java.net.URL.class, URLEditor.class);
        propertyEditors.add(java.util.Locale.class, LocaleEditor.class);
        propertyEditors.add(Class.class, ClassEditor.class);
        propertyEditors.add(Class[].class, ClassArrayEditor.class);
        propertyEditors.add(java.util.Date.class, DateEditor.class);
        propertyEditors.add(java.sql.Date.class, SQLDateEditor.class);
        propertyEditors.add(java.sql.Time.class, TimeEditor.class);
        propertyEditors.add(java.sql.Timestamp.class, TimestampEditor.class);
        propertyEditors.add(java.lang.reflect.Method.class, MethodEditor.class);
        propertyEditors.add(java.lang.reflect.Method[].class, MethodArrayEditor.class);
        propertyEditors.add(java.lang.reflect.Constructor.class, ConstructorEditor.class);
        propertyEditors.add(java.util.SortedMap.class, SortedMapEditor.class);
        propertyEditors.add(java.util.Map.class, MapEditor.class);
        propertyEditors.add(Character.TYPE, CharacterEditor.class);
        propertyEditors.add(Character.class, CharacterEditor.class);
        propertyEditors.add(
            Byte.class,
            PropertyEditorManager.findEditor(Byte.TYPE).getClass()
        );
        propertyEditors.add(Boolean.TYPE, BooleanEditor.class);
        propertyEditors.add(Boolean.class, BooleanEditor.class);
        propertyEditors.add(Short.TYPE, ShortEditor.class);
        propertyEditors.add(Short.class, ShortEditor.class);
        propertyEditors.add(Integer.TYPE, IntEditor.class);
        propertyEditors.add(Integer.class,IntEditor.class);
        propertyEditors.add(Long.TYPE, LongEditor.class);
        propertyEditors.add(Long.class, LongEditor.class);
        propertyEditors.add(Float.TYPE, FloatEditor.class);
        propertyEditors.add(Float.class, FloatEditor.class);
        propertyEditors.add(Double.TYPE, DoubleEditor.class);
        propertyEditors.add(Double.class, DoubleEditor.class);
        propertyEditors.add(java.math.BigDecimal.class, BigDecimalEditor.class);
        propertyEditors.add(java.math.BigInteger.class, BigIntegerEditor.class);
        propertyEditors.add(byte[].class, ByteArrayEditor.class);
        propertyEditors.add(char[].class, CharacterArrayEditor.class);
        propertyEditors.add(short[].class, ShortArrayEditor.class);
        propertyEditors.add(int[].class, IntArrayEditor.class);
        propertyEditors.add(long[].class, LongArrayEditor.class);
        propertyEditors.add(float[].class, FloatArrayEditor.class);
        propertyEditors.add(double[].class, DoubleArrayEditor.class);
        propertyEditors.add(boolean[].class, BooleanArrayEditor.class);
        propertyEditors.add(java.math.BigDecimal[].class, BigDecimalArrayEditor.class);
        propertyEditors.add(java.math.BigInteger[].class, BigIntegerArrayEditor.class);
        propertyEditors.add(java.io.File.class, FileEditor.class);
        propertyEditors.add(java.io.File[].class, FileArrayEditor.class);
        propertyEditors.add(Property.class, BeanPropertyEditor.class);
        propertyEditors.add(Property[].class, PropertyArrayEditor.class);
    }
    
    /**
     * PropertyEditorを登録する。<p>
     *
     * @param targetType PropertyEditorが編集するクラス
     * @param editorClass PropertyEditorのクラス
     */
    public static void registerEditor(Class targetType, Class editorClass){
        propertyEditors.add(targetType, editorClass);
    }
    
    /**
     * 指定されたクラスを編集するPropertyEditorを検索する。<p>
     * このクラスに登録されているPropertyEditorから検索する。見つからない場合は、java.beans.PropertyEditorManagerからも検索する。更に、見つからない場合は、nullを返す。<br>
     *
     * @param targetType PropertyEditorが編集するクラス
     * @return 指定されたクラスを編集するPropertyEditor
     */
    public static PropertyEditor findEditor(Class targetType){
        if(targetType == null){
            return null;
        }
        PropertyEditor editor = null;
        Class clazz = (Class)propertyEditors.getValue(targetType);
        if(clazz == null){
            synchronized(propertyEditors){
                clazz = (Class)propertyEditors.getValue(targetType);
                if(clazz == null){
                    editor = PropertyEditorManager.findEditor(targetType);
                    if(editor != null){
                        propertyEditors.add(targetType, editor.getClass());
                    }else{
                        propertyEditors.add(targetType, NOT_FOUND_EDITOR_CLASS);
                    }
                }
            }
        }else if(!clazz.equals(NOT_FOUND_EDITOR_CLASS)){
            try{
                editor = (PropertyEditor)clazz.newInstance();
            }catch(InstantiationException e){
                return null;
            }catch(IllegalAccessException e){
                return null;
            }
        }
        return editor;
    }
    
    public static final class NotFoundPropertyEditor extends PropertyEditorSupport{
    }
}