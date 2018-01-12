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

import java.util.*;
import java.lang.reflect.*;

import jp.ossc.nimbus.beans.NoSuchPropertyException;
import jp.ossc.nimbus.beans.Property;
import jp.ossc.nimbus.beans.SimpleProperty;
import jp.ossc.nimbus.beans.PropertyFactory;
import jp.ossc.nimbus.service.log.*;

/**
 * プロパティWritableRecordファクトリサービス。<p>
 * {@link #setFormat(String)}で設定されたフォーマットのキーに該当する値を{@link #createRecord(Object)}の引数で指定された任意のオブジェクト内から、プロパティアクセスして取得する。<br>
 * プロパティアクセスとは、Beanのプロパティに対するアクセスの事で、用意されているアクセス方法は、{@link PropertyFactory}を参照。<br>
 * 
 * @author M.Takata
 */
public class PropertyWritableRecordFactoryService
 extends WritableRecordFactoryService
 implements PropertyWritableRecordFactoryServiceMBean, java.io.Serializable{
    
    private static final long serialVersionUID = 6929876971079349458L;
    
    // メッセージID定義
    private static final String PWRF_ = "PWRF_";
    private static final String PWRF_0 = PWRF_ + 0;
    private static final String PWRF_00 = PWRF_0 + 0;
    private static final String PWRF_000 = PWRF_00 + 0;
    private static final String PWRF_0000 = PWRF_000 + 0;
    private static final String PWRF_00001 = PWRF_0000 + 1;
    
    private static final String ITERATE_SUFFIX = "*";
    
    private Properties formatKeyMapping;
    private Map writableRecordPropertyMapping;
    
    private Map iterateFormatKeyMappings;
    private Map iterateWritableRecordPropertyMappings;
    
    private Map iterateFormats;
    private Map iterateFormatMappings;
    
    // PropertyWritableRecordFactoryServiceMBeanのJavaDoc
    public void setFormatKeyMapping(Properties mapping){
        formatKeyMapping = mapping;
    }
    // PropertyWritableRecordFactoryServiceMBeanのJavaDoc
    public Properties getFormatKeyMapping(){
        return formatKeyMapping;
    }
    
    // PropertyWritableRecordFactoryServiceMBeanのJavaDoc
    public void setIterateFormatKeyMapping(String key, Properties mapping){
        iterateFormatKeyMappings.put(key, mapping);
    }
    // PropertyWritableRecordFactoryServiceMBeanのJavaDoc
    public Properties getIterateFormatKeyMapping(String key){
        return (Properties)iterateFormatKeyMappings.get(key);
    }
    
    // PropertyWritableRecordFactoryServiceMBeanのJavaDoc
    public void setIterateFormat(String key, String format){
        iterateFormats.put(key, format);
    }
    // PropertyWritableRecordFactoryServiceMBeanのJavaDoc
    public String getIterateFormat(String key){
        return (String)iterateFormats.get(key);
    }
    
    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception{
        super.createService();
        writableRecordPropertyMapping = new HashMap();
        iterateFormatKeyMappings = new HashMap();
        iterateWritableRecordPropertyMappings = new HashMap();
        iterateFormats = new HashMap();
        iterateFormatMappings = new HashMap();
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        super.startService();
        if(formatKeyMapping != null){
            final Iterator keys = formatKeyMapping.keySet().iterator();
            while(keys.hasNext()){
                final String key = (String)keys.next();
                final String prop = formatKeyMapping.getProperty(key);
                if(prop != null && prop.length() != 0){
                    Property property = PropertyFactory.createProperty(prop);
                    property.setIgnoreNullProperty(true);
                    writableRecordPropertyMapping.put(key, property);
                }
            }
        }
        if(iterateFormats.size() != 0){
            final Iterator itrKeys = iterateFormats.keySet().iterator();
            while(itrKeys.hasNext()){
                final String itrKey = (String)itrKeys.next();
                final String format = (String)iterateFormats.get(itrKey);
                iterateFormatMappings.put(itrKey, parseFormat(format));
                if(!iterateFormatKeyMappings.containsKey(itrKey)){
                    throw new IllegalArgumentException(
                        "IterateFormatKeyMapping that corresponds to \"" + itrKey + "\" is not specified."
                    );
                }
            }
        }
        if(iterateFormatKeyMappings.size() != 0){
            final Iterator itrKeys = iterateFormatKeyMappings.keySet().iterator();
            while(itrKeys.hasNext()){
                final String itrKey = (String)itrKeys.next();
                final Properties props = (Properties)iterateFormatKeyMappings.get(itrKey);
                final Map mapping = new HashMap();
                final Iterator keys = props.keySet().iterator();
                while(keys.hasNext()){
                    final String key = (String)keys.next();
                    final String prop = props.getProperty(key);
                    if(prop != null && prop.length() != 0){
                        Property property = PropertyFactory.createProperty(prop);
                        property.setIgnoreNullProperty(true);
                        mapping.put(key, property);
                    }
                }
                iterateWritableRecordPropertyMappings.put(itrKey, mapping);
            }
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        writableRecordPropertyMapping.clear();
        iterateWritableRecordPropertyMappings.clear();
        iterateFormatMappings.clear();
        super.stopService();
    }
    /**
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception サービスの破棄処理に失敗した場合
     */
    public void destroyService() throws Exception{
        writableRecordPropertyMapping = null;
        iterateFormatKeyMappings = null;
        iterateWritableRecordPropertyMappings = null;
        iterateFormats = null;
        iterateFormatMappings = null;
        super.destroyService();
    }
    
    /**
     * 指定された出力要素が持つプロパティ名の集合を取得する。<p>
     *
     * @param elements 出力要素
     * @return プロパティ名の集合
     */
    protected Set getElementKeys(Object elements){
        if(elements instanceof Map){
            return super.getElementKeys(elements);
        }
        return SimpleProperty.getPropertyNames(elements);
    }
    
    protected WritableElement createElement(String key, Object val){
        return createElement(
            key,
            val,
            writableRecordPropertyMapping
        );
    }
    
    protected WritableElement createElement(
        String key,
        Object val,
        Map propMapping
    ){
        if(key.endsWith(ITERATE_SUFFIX)){
            Object[] array = null;
            if(val == null){
                return super.createElement(key, val);
            }else if(val instanceof Collection){
                Collection col = (Collection)val;
                array = col.toArray(new Object[col.size()]);
            }else if(val.getClass().isArray()){
                array = (Object[])val;
            }else{
                return super.createElement(key, val);
            }
            if(array == null || array.length == 0){
                return null;
            }
            IterateWritableElement itrElement = new IterateWritableElement(key);
            if(iterateFormatMappings.containsKey(key)){
                final List parsedElements
                     = (List)iterateFormatMappings.get(key);
                final Map itrPropMapping
                     = (Map)iterateWritableRecordPropertyMappings.get(key);
                for(int i = 0; i < array.length; i++){
                    for(int j = 0, jmax = parsedElements.size(); j < jmax; j++){
                        final ParsedElement parsedElem
                             = (ParsedElement)parsedElements.get(j);
                        WritableElement element = null;
                        if(parsedElem.isKeyElement()){
                            final String itrElementKey = parsedElem.getValue();
                            element = createElement(
                                itrElementKey,
                                getElementValue(
                                    itrElementKey,
                                    array[i],
                                    itrPropMapping
                                )
                            );
                        }else{
                            element = new SimpleElement(parsedElem.getValue());
                        }
                        if(element != null){
                            itrElement.addElement(element);
                        }
                    }
                }
            }else{
                for(int i = 0; i < array.length; i++){
                    final WritableElement element
                         = super.createElement(key, array[i]);
                    if(element != null){
                        itrElement.addElement(element);
                    }
                }
            }
            return itrElement;
        }else{
            return super.createElement(key, val);
        }
    }
    
    /**
     * 指定された出力要素から、指定されたプロパティ名の値を取得する。<p>
     *
     * @param key プロパティ名
     * @param elements 出力要素
     * @return 出力要素内のプロパティ値
     */
    protected Object getElementValue(String key, Object elements){
        return getElementValue(
            key,
            elements,
            writableRecordPropertyMapping
        );
    }
    
    protected Object getElementValue(
        String key,
        Object elements,
        Map propMapping
    ){
        if(propMapping != null && propMapping.containsKey(key)){
            final Logger logger = getLogger();
            final Property prop = (Property)propMapping.get(key);
            try{
                return prop.getProperty(elements);
            }catch(NoSuchPropertyException e){
                return null;
            }catch(InvocationTargetException e){
                logger.write(PWRF_00001, key, e);
                return null;
            }
        }else{
            return super.getElementValue(key, elements);
        }
    }
}
