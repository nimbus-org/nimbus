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
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import jp.ossc.nimbus.beans.PropertyFactory;
import jp.ossc.nimbus.beans.Property;
import jp.ossc.nimbus.beans.IndexedProperty;
import jp.ossc.nimbus.beans.dataset.DataSet;
import jp.ossc.nimbus.beans.dataset.RecordList;
import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.beans.dataset.PropertySetException;
import jp.ossc.nimbus.beans.NestedProperty;
import jp.ossc.nimbus.beans.NoSuchPropertyException;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvoker;

/**
 * サーブレットリクエストパラメータ→DataSetコンバータ。<p>
 * {@link javax.servlet.ServletRequest#getParameterValues(String)}で取得できるパラメータを{@link jp.ossc.nimbus.beans.dataset.DataSet DataSet}の{@link jp.ossc.nimbus.beans.dataset.Header Header}や、{@link jp.ossc.nimbus.beans.dataset.RecordList RecordList}の持つ{@link Record Record}のプロパティに設定して、DataSetオブジェクトに変換する。<br>
 * リクエストパラメータで、データセット名とデータセットにどのように値を設定するかのプロパティ表現を指定する事で、リクエストパラメータとDataSetとのマッピングを行う。<br>
 * <p>
 * DataSetは、{@link #setDataSet(String, jp.ossc.nimbus.beans.dataset.DataSet) setDataSet(String, DataSet)}で予めこのConverter自身に登録しておく。この場合、第1引数がデータセット名となる。<br>
 * または、{@link #setBeanFlowInvokerFactory(BeanFlowInvokerFactory)}で設定したBeanFlowInvokerFactoryに、DataSetを戻り値とするBeanFlowを定義しておく。この場合、BeanFlow名がデータセット名となる。<br>
 * <p>
 * リクエストパラメータの指定方法は、以下の通り。また、プロパティ表現は、{@link jp.ossc.nimbus.beans.PropertyFactory PropertyFactory}を参照。<br>
 * <table border="1">
 *   <tr>
 *     <td>リクエストパラメータとHeaderのマッピング方法</td>
 *     <td>&lt;input name="ds1:Header(h1).prop1" type="text" value="a"&gt;</td>
 *   </tr>
 *   <tr>
 *     <td rowspan="2">リクエストパラメータとRecordListのマッピング方法</td>
 *     <td>&lt;input name="ds1:RecordList(l1).prop1" type="text" value="a"&gt;<br>&lt;input name="ds1:RecordList(l1).prop1" type="text" value="a"&gt;</td>
 *   </tr>
 *   <tr>
 *     <td>&lt;input name="ds1:RecordList[0](l1).prop1" type="text" value="a"&gt;<br>&lt;input name="ds1:RecordList[1](l1).prop1" type="text" value="a"&gt;</td>
 *   </tr>
 * </table>
 * <p>
 * また、データセット名の指定は、全てのリクエストパラメータを同じDataSetに格納する場合は一括で指定する事もでき、以下のようにする。<br>
 * <pre>
 *   &lt;input type="hidden" name="ds" value="ds1"&gt;
 *   &lt;input name=":Header(h1).prop1" type="text" value="a"&gt;
 *   &lt;input name=":Header(h1).prop2" type="text" value="b"&gt;
 * </pre>
 * 
 * @author M.Takata
 */
public class DataSetServletRequestParameterConverter implements Converter{
    
    public static final String DEFAULT_DATASET_PARAMETER_NAME = "ds";
    
    public static final String DEFAULT_DATASET_DELIMITER = ":";
    
    public static final String DEFAULT_DATASET_PREFIX = "dataset";
    
    /**
     * データセットマッピング。<p>
     */
    protected Map dataSetMap = new HashMap();
    
    /**
     * BeanFlowInvokerFactory。<p>
     */
    protected BeanFlowInvokerFactory beanFlowInvokerFactory;
    
    /**
     * PropertyをキャッシュするMap。<p>
     */
    protected ConcurrentMap propertyCache = new ConcurrentHashMap();
    
    /**
     * データセット名を決定するパラメータ名。<p>
     */
    protected String dataSetParameterName = DEFAULT_DATASET_PARAMETER_NAME;
    
    /**
     * データセット名の区切り子。<p>
     */
    protected String datasetDelimiter = DEFAULT_DATASET_DELIMITER;
    
    /**
     * データセット名をパスから決定する場合に、パスに付加する前置詞。<p>
     * デフォルトは、{@link #DEFAULT_DATASET_PREFIX}。<br>
     */
    protected String dataSetPathPrefix = DEFAULT_DATASET_PREFIX;
    
    /**
     * データセットに存在しないパラメータを無視するかどうかのフラグ。<p>
     * デフォルトは、falseで、変換エラーとする。<br>
     */
    protected boolean isIgnoreUnknownParameter;
    
    /**
     * データセット名とデータセットのマッピングを設定する。<p>
     * サーブレットリクエストパラメータ→データセット変換を行う際に、データセット名からデータセットを特定するのに使用する。<br>
     * 
     * @param name データセット名
     * @param dataSet データセット
     */
    public void setDataSet(String name, DataSet dataSet){
        if(dataSet.getName() == null){
            dataSet.setName(name);
        }
        dataSetMap.put(name, dataSet);
    }
    
    /**
     * DataSetをBeanFlowで取得する場合に使用する{@link BeanFlowInvokerFactory}を設定する。<p>
     *
     * @param factory BeanFlowInvokerFactory
     */
    public void setBeanFlowInvokerFactory(BeanFlowInvokerFactory factory){
        beanFlowInvokerFactory = factory;
    }
    
    /**
     * DataSet名を一括で指定するリクエストパラメータ名を設定する。<p>
     * デフォルトは、{@link #DEFAULT_DATASET_PARAMETER_NAME}。<br>
     *
     * @param name DataSet名を一括で指定するリクエストパラメータ名
     */
    public void setDataSetParameterName(String name){
        dataSetParameterName = name;
    }
    
    /**
     * DataSet名を一括で指定するリクエストパラメータ名を取得する。<p>
     *
     * @return DataSet名を一括で指定するリクエストパラメータ名
     */
    public String getDataSetParameterName(){
        return dataSetParameterName;
    }
    
    /**
     * データセット名の区切り子を設定する。<p>
     * デフォルトは、{@link #DEFAULT_DATASET_DELIMITER}。<br>
     *
     * @param delim データセット名の区切り子
     */
    public void setDataSetDelimiter(String delim){
        datasetDelimiter = delim;
    }
    
    /**
     * データセット名の区切り子を取得する。<p>
     *
     * @return データセット名の区切り子
     */
    public String getDataSetDelimiter(){
        return datasetDelimiter;
    }
    
    /**
     * DataSet名をパスから決定する場合の前置詞を設定する。<p>
     * デフォルトは、{@link #DEFAULT_DATASET_PREFIX}。<br>
     *
     * @param prefix DataSet名をパスから決定する場合の前置詞
     */
    public void setDataSetPathPrefix(String prefix){
        dataSetPathPrefix = prefix;
    }
    
    /**
     * DataSet名をパスから決定する場合の前置詞を取得する。<p>
     *
     * @return DataSet名をパスから決定する場合の前置詞
     */
    public String getDataSetPathPrefix(){
        return dataSetPathPrefix;
    }
    
    /**
     * データセットに存在しないパラメータを無視するかどうかを設定する。<p>
     * デフォルトは、falseで、変換エラーとなる。<br>
     * 
     * @param isIgnore trueの場合、無視する
     */
    public void setIgnoreUnknownParameter(boolean isIgnore){
        isIgnoreUnknownParameter = isIgnore;
    }
    
    /**
     * データセットに存在しないパラメータを無視するかどうかを判定する。<p>
     * 
     * @return trueの場合、無視する
     */
    public boolean isIgnoreUnknownParameter(){
        return isIgnoreUnknownParameter;
    }
    
    /**
     * 指定されたオブジェクトを変換する。<p>
     *
     * @param obj 変換対象のオブジェクト
     * @return 変換後のオブジェクト
     * @exception ConvertException 変換に失敗した場合
     */
    public Object convert(Object obj) throws ConvertException{
        if(!(obj instanceof ServletRequest)){
            return null;
        }
        ServletRequest request = (ServletRequest)obj;
        final Map paramMap = request.getParameterMap();
        if(paramMap == null || paramMap.size() == 0){
            return null;
        }
        String defaultDsName = request.getParameter(dataSetParameterName);
        if((defaultDsName == null || defaultDsName.length() == 0)
            && request instanceof HttpServletRequest){
            HttpServletRequest httpReq = (HttpServletRequest)request;
            String path = httpReq.getServletPath();
            if(httpReq.getPathInfo() != null){
                path = path + httpReq.getPathInfo();
            }
            if(path != null){
                int index = path.lastIndexOf('.');
                if(index != -1){
                    path = path.substring(0, index);
                }
                defaultDsName = dataSetPathPrefix + path;
            }
        }
        final Map currentDsMap = new HashMap();
        final Iterator entries = paramMap.entrySet().iterator();
        while(entries.hasNext()){
            final Map.Entry entry = (Map.Entry)entries.next();
            final String key = (String)entry.getKey();
            final int index = key.indexOf(datasetDelimiter);
            if(index == -1 || index == key.length() - 1){
                continue;
            }
            String dsName = null;
            if(index == 0){
                dsName = defaultDsName;
            }else{
                dsName = key.substring(0, index);
            }
            if(dsName == null){
                continue;
            }
            DataSet ds = (DataSet)currentDsMap.get(dsName);
            if(ds == null){
                if(dataSetMap.containsKey(dsName)){
                    ds = ((DataSet)dataSetMap.get(dsName)).cloneSchema();
                }else if(beanFlowInvokerFactory != null
                            && beanFlowInvokerFactory.containsFlow(dsName)
                ){
                    final BeanFlowInvoker beanFlowInvoker
                        = beanFlowInvokerFactory.createFlow(dsName);
                    Object ret = null;
                    try{
                        ret = beanFlowInvoker.invokeFlow(null);
                    }catch(Exception e){
                        throw new ConvertException("Exception occured in BeanFlow '" + dsName + "'", e);
                    }
                    if(!(ret instanceof DataSet)){
                        throw new ConvertException("Result of BeanFlow '" + dsName + "' is not DataSet.");
                    }
                    ds = (DataSet)ret;
                }else{
                    if(isIgnoreUnknownParameter){
                        continue;
                    }else{
                        throw new ConvertException("Unknown DataSet : " + dsName);
                    }
                }
                currentDsMap.put(dsName, ds);
            }
            final String propStr = key.substring(index + 1);
            Property prop = (Property)propertyCache.get(propStr);
            if(prop == null){
                try{
                    prop = PropertyFactory.createProperty(propStr);
                    if(isIgnoreUnknownParameter){
                        prop.setIgnoreNullProperty(true);
                    }
                }catch(IllegalArgumentException e){
                    throw new ConvertException("Parameter '" + key + "' is illegal.", e);
                }
                Property old = (Property)propertyCache.putIfAbsent(propStr, prop);
                if(old != null){
                    prop = old;
                }
            }
            final String[] vals = (String[])entry.getValue();
            try{
                if(prop instanceof NestedProperty){
                    Property thisProp = ((NestedProperty)prop).getThisProperty();
                    if(thisProp instanceof NestedProperty){
                        Property nestedProp = ((NestedProperty)prop).getNestedProperty();
                        Property nestedProp2 = ((NestedProperty)thisProp).getNestedProperty();
                        if(nestedProp2 instanceof IndexedProperty){
                            Property thisProp2 = ((NestedProperty)thisProp).getThisProperty();
                            Object thisObj = thisProp2.getProperty(ds);
                            if(thisObj == null){
                                if(isIgnoreUnknownParameter){
                                    continue;
                                }else{
                                    throw new ConvertException("Parameter '" + key + "' is illegal.");
                                }
                            }
                            if(thisObj instanceof RecordList){
                                setRecordListProperty(
                                    (RecordList)thisObj,
                                    nestedProp.getPropertyName(),
                                    ((IndexedProperty)nestedProp2).getIndex(),
                                    vals
                                );
                            }else{
                                // ありえない
                                prop.setProperty(
                                    ds,
                                    vals[vals.length - 1]
                                );
                            }
                        }else{
                            Object thisObj = thisProp.getProperty(ds);
                            if(thisObj == null){
                                if(isIgnoreUnknownParameter){
                                    continue;
                                }else{
                                    throw new ConvertException("Parameter '" + key + "' is illegal.");
                                }
                            }
                            if(thisObj instanceof RecordList){
                                setRecordListProperty(
                                    (RecordList)thisObj,
                                    nestedProp.getPropertyName(),
                                    vals
                                );
                            }else if(thisObj instanceof Record){
                                setRecordProperty(
                                    (Record)thisObj,
                                    nestedProp.getPropertyName(),
                                    nestedProp.getPropertyType(thisObj),
                                    vals
                                );
                            }else{
                                nestedProp.setProperty(
                                    thisObj,
                                    vals[vals.length - 1]
                                );
                            }
                        }
                    }else{
                        Object thisObj = thisProp.getProperty(ds);
                        if(thisObj == null){
                            if(isIgnoreUnknownParameter){
                                continue;
                            }else{
                                throw new ConvertException("Parameter '" + key + "' is illegal.");
                            }
                        }
                        Property nestedProp = ((NestedProperty)prop).getNestedProperty();
                        if(thisObj instanceof RecordList){
                            setRecordListProperty(
                                (RecordList)thisObj,
                                nestedProp.getPropertyName(),
                                vals
                            );
                        }else if(thisObj instanceof Record){
                            setRecordProperty(
                                (Record)thisObj,
                                nestedProp.getPropertyName(),
                                nestedProp.getPropertyType(thisObj),
                                vals
                            );
                        }else{
                            nestedProp.setProperty(
                                thisObj,
                                vals[vals.length - 1]
                            );
                        }
                    }
                }else{
                    throw new ConvertException("Parameter '" + key + "' is illegal.");
                }
            }catch(PropertySetException e){
                Throwable cause = e.getCause();
                if(cause instanceof ConvertException){
                    throw (ConvertException)cause;
                }
                if(isIgnoreUnknownParameter){
                    continue;
                }else{
                    throw new ConvertException("Parameter '" + key + "' is illegal.", e);
                }
            }catch(NoSuchPropertyException e){
                if(isIgnoreUnknownParameter){
                    continue;
                }else{
                    throw new ConvertException("Parameter '" + key + "' is illegal.", e);
                }
            }catch(InvocationTargetException e){
                throw new ConvertException("Parameter '" + key + "' is illegal.", e.getTargetException());
            }
        }
        if(currentDsMap.size() == 0){
            return null;
        }else if(currentDsMap.size() == 1){
            return currentDsMap.values().iterator().next();
        }else{
            return currentDsMap;
        }
    }
    
    protected void setRecordProperty(
        Record record,
        String name,
        Class propType,
        String[] vals
    ) throws PropertySetException{
        if(propType == null
            || propType.equals(java.lang.Object.class)
            || propType.equals(String[].class)
        ){
            record.setProperty(
                name,
                vals
            );
        }else{
            if(propType.isArray() && vals.length != 1){
                record.setParseProperty(
                    name,
                    vals
                );
            }else{
                record.setParseProperty(
                    name,
                    vals[vals.length - 1]
                );
            }
        }
    }
    
    protected void setRecordListProperty(
        RecordList recList,
        String name,
        String[] vals
    ) throws PropertySetException{
        for(int i = 0; i < vals.length; i++){
            Record rec = null;
            if(recList.size() > i){
                rec = recList.getRecord(i);
            }else{
                rec = recList.createRecord();
                recList.addRecord(rec);
            }
            rec.setParseProperty(name, vals[i]);
        }
    }
    
    protected void setRecordListProperty(
        RecordList recList,
        String name,
        int index,
        String[] vals
    ) throws PropertySetException{
        Record rec = null;
        if(recList.size() > index){
            rec = recList.getRecord(index);
        }else{
            for(int i = recList.size(); i <= index; i++){
                rec = recList.createRecord();
                recList.addRecord(rec);
            }
        }
        rec.setParseProperty(name, vals[0]);
    }
}
