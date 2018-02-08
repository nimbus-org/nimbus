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
 * �T�[�u���b�g���N�G�X�g�p�����[�^��DataSet�R���o�[�^�B<p>
 * {@link javax.servlet.ServletRequest#getParameterValues(String)}�Ŏ擾�ł���p�����[�^��{@link jp.ossc.nimbus.beans.dataset.DataSet DataSet}��{@link jp.ossc.nimbus.beans.dataset.Header Header}��A{@link jp.ossc.nimbus.beans.dataset.RecordList RecordList}�̎���{@link Record Record}�̃v���p�e�B�ɐݒ肵�āADataSet�I�u�W�F�N�g�ɕϊ�����B<br>
 * ���N�G�X�g�p�����[�^�ŁA�f�[�^�Z�b�g���ƃf�[�^�Z�b�g�ɂǂ̂悤�ɒl��ݒ肷�邩�̃v���p�e�B�\�����w�肷�鎖�ŁA���N�G�X�g�p�����[�^��DataSet�Ƃ̃}�b�s���O���s���B<br>
 * <p>
 * DataSet�́A{@link #setDataSet(String, jp.ossc.nimbus.beans.dataset.DataSet) setDataSet(String, DataSet)}�ŗ\�߂���Converter���g�ɓo�^���Ă����B���̏ꍇ�A��1�������f�[�^�Z�b�g���ƂȂ�B<br>
 * �܂��́A{@link #setBeanFlowInvokerFactory(BeanFlowInvokerFactory)}�Őݒ肵��BeanFlowInvokerFactory�ɁADataSet��߂�l�Ƃ���BeanFlow���`���Ă����B���̏ꍇ�ABeanFlow�����f�[�^�Z�b�g���ƂȂ�B<br>
 * <p>
 * ���N�G�X�g�p�����[�^�̎w����@�́A�ȉ��̒ʂ�B�܂��A�v���p�e�B�\���́A{@link jp.ossc.nimbus.beans.PropertyFactory PropertyFactory}���Q�ƁB<br>
 * <table border="1">
 *   <tr>
 *     <td>���N�G�X�g�p�����[�^��Header�̃}�b�s���O���@</td>
 *     <td>&lt;input name="ds1:Header(h1).prop1" type="text" value="a"&gt;</td>
 *   </tr>
 *   <tr>
 *     <td rowspan="2">���N�G�X�g�p�����[�^��RecordList�̃}�b�s���O���@</td>
 *     <td>&lt;input name="ds1:RecordList(l1).prop1" type="text" value="a"&gt;<br>&lt;input name="ds1:RecordList(l1).prop1" type="text" value="a"&gt;</td>
 *   </tr>
 *   <tr>
 *     <td>&lt;input name="ds1:RecordList[0](l1).prop1" type="text" value="a"&gt;<br>&lt;input name="ds1:RecordList[1](l1).prop1" type="text" value="a"&gt;</td>
 *   </tr>
 * </table>
 * <p>
 * �܂��A�f�[�^�Z�b�g���̎w��́A�S�Ẵ��N�G�X�g�p�����[�^�𓯂�DataSet�Ɋi�[����ꍇ�͈ꊇ�Ŏw�肷�鎖���ł��A�ȉ��̂悤�ɂ���B<br>
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
     * �f�[�^�Z�b�g�}�b�s���O�B<p>
     */
    protected Map dataSetMap = new HashMap();
    
    /**
     * BeanFlowInvokerFactory�B<p>
     */
    protected BeanFlowInvokerFactory beanFlowInvokerFactory;
    
    /**
     * Property���L���b�V������Map�B<p>
     */
    protected Map propertyCache = Collections.synchronizedMap(new HashMap());
    
    /**
     * �f�[�^�Z�b�g�������肷��p�����[�^���B<p>
     */
    protected String dataSetParameterName = DEFAULT_DATASET_PARAMETER_NAME;
    
    /**
     * �f�[�^�Z�b�g���̋�؂�q�B<p>
     */
    protected String datasetDelimiter = DEFAULT_DATASET_DELIMITER;
    
    /**
     * �f�[�^�Z�b�g�����p�X���猈�肷��ꍇ�ɁA�p�X�ɕt������O�u���B<p>
     * �f�t�H���g�́A{@link #DEFAULT_DATASET_PREFIX}�B<br>
     */
    protected String dataSetPathPrefix = DEFAULT_DATASET_PREFIX;
    
    /**
     * �f�[�^�Z�b�g�ɑ��݂��Ȃ��p�����[�^�𖳎����邩�ǂ����̃t���O�B<p>
     * �f�t�H���g�́Afalse�ŁA�ϊ��G���[�Ƃ���B<br>
     */
    protected boolean isIgnoreUnknownParameter;
    
    /**
     * �f�[�^�Z�b�g���ƃf�[�^�Z�b�g�̃}�b�s���O��ݒ肷��B<p>
     * �T�[�u���b�g���N�G�X�g�p�����[�^���f�[�^�Z�b�g�ϊ����s���ۂɁA�f�[�^�Z�b�g������f�[�^�Z�b�g����肷��̂Ɏg�p����B<br>
     * 
     * @param name �f�[�^�Z�b�g��
     * @param dataSet �f�[�^�Z�b�g
     */
    public void setDataSet(String name, DataSet dataSet){
        if(dataSet.getName() == null){
            dataSet.setName(name);
        }
        dataSetMap.put(name, dataSet);
    }
    
    /**
     * DataSet��BeanFlow�Ŏ擾����ꍇ�Ɏg�p����{@link BeanFlowInvokerFactory}��ݒ肷��B<p>
     *
     * @param factory BeanFlowInvokerFactory
     */
    public void setBeanFlowInvokerFactory(BeanFlowInvokerFactory factory){
        beanFlowInvokerFactory = factory;
    }
    
    /**
     * DataSet�����ꊇ�Ŏw�肷�郊�N�G�X�g�p�����[�^����ݒ肷��B<p>
     * �f�t�H���g�́A{@link #DEFAULT_DATASET_PARAMETER_NAME}�B<br>
     *
     * @param name DataSet�����ꊇ�Ŏw�肷�郊�N�G�X�g�p�����[�^��
     */
    public void setDataSetParameterName(String name){
        dataSetParameterName = name;
    }
    
    /**
     * DataSet�����ꊇ�Ŏw�肷�郊�N�G�X�g�p�����[�^�����擾����B<p>
     *
     * @return DataSet�����ꊇ�Ŏw�肷�郊�N�G�X�g�p�����[�^��
     */
    public String getDataSetParameterName(){
        return dataSetParameterName;
    }
    
    /**
     * �f�[�^�Z�b�g���̋�؂�q��ݒ肷��B<p>
     * �f�t�H���g�́A{@link #DEFAULT_DATASET_DELIMITER}�B<br>
     *
     * @param delim �f�[�^�Z�b�g���̋�؂�q
     */
    public void setDataSetDelimiter(String delim){
        datasetDelimiter = delim;
    }
    
    /**
     * �f�[�^�Z�b�g���̋�؂�q���擾����B<p>
     *
     * @return �f�[�^�Z�b�g���̋�؂�q
     */
    public String getDataSetDelimiter(){
        return datasetDelimiter;
    }
    
    /**
     * DataSet�����p�X���猈�肷��ꍇ�̑O�u����ݒ肷��B<p>
     * �f�t�H���g�́A{@link #DEFAULT_DATASET_PREFIX}�B<br>
     *
     * @param prefix DataSet�����p�X���猈�肷��ꍇ�̑O�u��
     */
    public void setDataSetPathPrefix(String prefix){
        dataSetPathPrefix = prefix;
    }
    
    /**
     * DataSet�����p�X���猈�肷��ꍇ�̑O�u�����擾����B<p>
     *
     * @return DataSet�����p�X���猈�肷��ꍇ�̑O�u��
     */
    public String getDataSetPathPrefix(){
        return dataSetPathPrefix;
    }
    
    /**
     * �f�[�^�Z�b�g�ɑ��݂��Ȃ��p�����[�^�𖳎����邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse�ŁA�ϊ��G���[�ƂȂ�B<br>
     * 
     * @param isIgnore true�̏ꍇ�A��������
     */
    public void setIgnoreUnknownParameter(boolean isIgnore){
        isIgnoreUnknownParameter = isIgnore;
    }
    
    /**
     * �f�[�^�Z�b�g�ɑ��݂��Ȃ��p�����[�^�𖳎����邩�ǂ����𔻒肷��B<p>
     * 
     * @return true�̏ꍇ�A��������
     */
    public boolean isIgnoreUnknownParameter(){
        return isIgnoreUnknownParameter;
    }
    
    /**
     * �w�肳�ꂽ�I�u�W�F�N�g��ϊ�����B<p>
     *
     * @param obj �ϊ��Ώۂ̃I�u�W�F�N�g
     * @return �ϊ���̃I�u�W�F�N�g
     * @exception ConvertException �ϊ��Ɏ��s�����ꍇ
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
                propertyCache.put(propStr, prop);
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
                                // ���肦�Ȃ�
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
