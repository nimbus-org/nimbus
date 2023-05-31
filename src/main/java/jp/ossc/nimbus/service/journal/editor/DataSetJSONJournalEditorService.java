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
package jp.ossc.nimbus.service.journal.editor;

import java.lang.reflect.*;
import java.util.*;

import jp.ossc.nimbus.beans.dataset.*;
import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * {@link DataSet}オブジェクトをJSONフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class DataSetJSONJournalEditorService extends JSONJournalEditorService
 implements DataSetJSONJournalEditorServiceMBean{
    
    private static final long serialVersionUID = 3264007026021831179L;
    
    private static final String NAME_SCHEMA = "schema";
    private static final String NAME_HEADER = "header";
    private static final String NAME_RECORD_LIST = "recordList";
    private static final String NAME_NESTED_RECORD_LIST = "nestedRecordList";
    private static final String NAME_NESTED_RECORD = "nestedRecord";
    
    private boolean isOutputSchema = true;
    private boolean isOutputPropertyNameOfHeader = true;
    private boolean isOutputPropertyNameOfRecordList = true;
    
    // DataSetJSONJournalEditorServiceMBeanのJavaDoc
    public void setOutputSchema(boolean isOutput){
        isOutputSchema = isOutput;
    }
    // DataSetJSONJournalEditorServiceMBeanのJavaDoc
    public boolean isOutputSchema(){
        return isOutputSchema;
    }
    
    // DataSetJSONJournalEditorServiceMBeanのJavaDoc
    public void setOutputPropertyNameOfHeader(boolean isOutput){
        isOutputPropertyNameOfHeader = isOutput;
    }
    // DataSetJSONJournalEditorServiceMBeanのJavaDoc
    public boolean isOutputPropertyNameOfHeader(){
        return isOutputPropertyNameOfHeader;
    }
    
    // DataSetJSONJournalEditorServiceMBeanのJavaDoc
    public void setOutputPropertyNameOfRecordList(boolean isOutput){
        isOutputPropertyNameOfRecordList = isOutput;
    }
    // DataSetJSONJournalEditorServiceMBeanのJavaDoc
    public boolean isOutputPropertyNameOfRecordList(){
        return isOutputPropertyNameOfRecordList;
    }
    
    protected StringBuilder appendUnknownValue(StringBuilder buf, EditorFinder finder, Class type, Object value, Stack stack){
        if(!(value instanceof DataSet)){
            return super.appendUnknownValue(buf, finder, type, value, stack);
        }
        final DataSet dataSet = (DataSet)value;
        String dsName = dataSet.getName();
        if(dsName == null){
            dsName = "";
        }
        buf.append(OBJECT_ENCLOSURE_START);
        appendName(buf, dsName);
        buf.append(PROPERTY_SEPARATOR);
        buf.append(OBJECT_ENCLOSURE_START);
        
        boolean isOutput = false;
        // スキーマ出力
        if(isOutputSchema){
            appendName(buf, NAME_SCHEMA);
            buf.append(PROPERTY_SEPARATOR);
            buf.append(OBJECT_ENCLOSURE_START);
            
            // ヘッダのスキーマ出力
            final String[] headerNames = dataSet.getHeaderNames();
            if(headerNames != null && headerNames.length > 0){
                appendName(buf, NAME_HEADER);
                buf.append(PROPERTY_SEPARATOR);
                buf.append(OBJECT_ENCLOSURE_START);
                for(int i = 0, imax = headerNames.length; i < imax; i++){
                    final Header header = dataSet.getHeader(headerNames[i]);
                    appendName(
                        buf,
                        headerNames[i] == null ? "" : headerNames[i]
                    );
                    buf.append(PROPERTY_SEPARATOR);
                    appendValue(buf, finder, null, header.getSchema(), stack);
                    if(i != imax - 1){
                        buf.append(ARRAY_SEPARATOR);
                    }
                }
                buf.append(OBJECT_ENCLOSURE_END);
                isOutput = true;
            }
            
            // レコードリストのスキーマ出力
            String[] recListNames = dataSet.getRecordListNames();
            if(recListNames != null && recListNames.length > 0){
                if(isOutput){
                    buf.append(ARRAY_SEPARATOR);
                }
                appendName(buf, NAME_RECORD_LIST);
                buf.append(PROPERTY_SEPARATOR);
                buf.append(OBJECT_ENCLOSURE_START);
                for(int i = 0, imax = recListNames.length; i < imax; i++){
                    final RecordList recList
                         = dataSet.getRecordList(recListNames[i]);
                    appendName(
                        buf,
                        recListNames[i] == null ? "" : recListNames[i]
                    );
                    buf.append(PROPERTY_SEPARATOR);
                    appendValue(buf, finder, null, recList.getSchema(), stack);
                    if(i != imax - 1){
                        buf.append(ARRAY_SEPARATOR);
                    }
                }
                buf.append(OBJECT_ENCLOSURE_END);
                isOutput = true;
            }
            
            // ネストレコードのスキーマ出力
            String[] recNames = dataSet.getNestedRecordSchemaNames();
            if(recNames != null && recNames.length > 0){
                if(isOutput){
                    buf.append(ARRAY_SEPARATOR);
                }
                appendName(buf, NAME_NESTED_RECORD);
                buf.append(PROPERTY_SEPARATOR);
                buf.append(OBJECT_ENCLOSURE_START);
                for(int i = 0, imax = recNames.length; i < imax; i++){
                    final RecordSchema recSchema
                         = dataSet.getNestedRecordSchema(recNames[i]);
                    appendName(buf, recNames[i]);
                    buf.append(PROPERTY_SEPARATOR);
                    appendValue(buf, finder, null, recSchema.getSchema(), stack);
                    if(i != imax - 1){
                        buf.append(ARRAY_SEPARATOR);
                    }
                }
                buf.append(OBJECT_ENCLOSURE_END);
                isOutput = true;
            }
            
            // ネストレコードリストのスキーマ出力
            recListNames = dataSet.getNestedRecordListSchemaNames();
            if(recListNames != null && recListNames.length > 0){
                if(isOutput){
                    buf.append(ARRAY_SEPARATOR);
                }
                appendName(buf, NAME_NESTED_RECORD_LIST);
                buf.append(PROPERTY_SEPARATOR);
                buf.append(OBJECT_ENCLOSURE_START);
                for(int i = 0, imax = recListNames.length; i < imax; i++){
                    final RecordSchema recSchema
                         = dataSet.getNestedRecordListSchema(recListNames[i]);
                    appendName(buf, recListNames[i]);
                    buf.append(PROPERTY_SEPARATOR);
                    appendValue(buf, finder, null, recSchema.getSchema(), stack);
                    if(i != imax - 1){
                        buf.append(ARRAY_SEPARATOR);
                    }
                }
                buf.append(OBJECT_ENCLOSURE_END);
                isOutput = true;
            }
            
            buf.append(OBJECT_ENCLOSURE_END);
        }
        
        // ヘッダ出力
        final String[] headerNames = dataSet.getHeaderNames();
        if(headerNames != null && headerNames.length > 0){
            if(isOutput){
                buf.append(ARRAY_SEPARATOR);
            }
            appendName(buf, NAME_HEADER);
            buf.append(PROPERTY_SEPARATOR);
            buf.append(OBJECT_ENCLOSURE_START);
            for(int i = 0, imax = headerNames.length; i < imax; i++){
                final Header header = dataSet.getHeader(headerNames[i]);
                appendName(
                    buf,
                    headerNames[i] == null ? "" : headerNames[i]
                );
                buf.append(PROPERTY_SEPARATOR);
                appendValue(buf, finder, null, header, stack);
                if(i != imax - 1){
                    buf.append(ARRAY_SEPARATOR);
                }
            }
            buf.append(OBJECT_ENCLOSURE_END);
            isOutput = true;
        }
        
        // レコードリスト出力
        String[] recListNames = dataSet.getRecordListNames();
        if(recListNames != null && recListNames.length > 0){
            if(isOutput){
                buf.append(ARRAY_SEPARATOR);
            }
            appendName(buf, NAME_RECORD_LIST);
            buf.append(PROPERTY_SEPARATOR);
            buf.append(OBJECT_ENCLOSURE_START);
            for(int i = 0, imax = recListNames.length; i < imax; i++){
                final RecordList recList
                     = dataSet.getRecordList(recListNames[i]);
                appendName(
                    buf,
                    recListNames[i] == null ? "" : recListNames[i]
                );
                buf.append(PROPERTY_SEPARATOR);
                appendArray(buf, finder, recList, stack);
                if(i != imax - 1){
                    buf.append(ARRAY_SEPARATOR);
                }
            }
            buf.append(OBJECT_ENCLOSURE_END);
            isOutput = true;
        }
        
        buf.append(OBJECT_ENCLOSURE_END);
        buf.append(OBJECT_ENCLOSURE_END);
        return buf;
    }
    
    protected StringBuilder appendValue(StringBuilder buf, EditorFinder finder, Class type, Object value, Stack stack){
        if(type == null && value != null){
            type = value.getClass();
        }
        if(value != null && Record.class.isAssignableFrom(type)){
            Record rec = (Record)value;
            RecordSchema schema = rec.getRecordSchema();
            PropertySchema[] propSchemata = schema == null ? null : schema.getPropertySchemata();
            boolean isOutputPropertyName = true;
            if((rec instanceof Header && !isOutputPropertyNameOfHeader)
                || (!(rec instanceof Header)
                    && !isOutputPropertyNameOfRecordList)
            ){
                isOutputPropertyName = false;
            }
            if(isOutputPropertyName){
                buf.append(OBJECT_ENCLOSURE_START);
            }else{
                buf.append(ARRAY_ENCLOSURE_START);
            }
            boolean isAppend = false;
            for(int i = 0, imax = propSchemata == null ? 0 : propSchemata.length; i < imax; i++){
                Object prop = rec.getProperty(i);
                PropertySchema propSchema = propSchemata[i];
                if(!isOutputProperty(propSchema.getName())){
                    continue;
                }
                if(isAppend){
                    buf.append(ARRAY_SEPARATOR);
                }
                isAppend = true;
                boolean hasConverter = false;
                if(propSchema instanceof DefaultPropertySchema){
                    hasConverter = ((DefaultPropertySchema)propSchema).getFormatConverter() != null;
                }
                if(isOutputPropertyName){
                    appendName(buf, propSchema.getName());
                    buf.append(PROPERTY_SEPARATOR);
                }
                if(prop == null){
                    appendValue(buf, finder, propSchema.getType(), null, stack);
                }else{
                    Class propType = propSchema.getType();
                    if(propType == null){
                        propType = prop.getClass();
                    }
                    if(propType.isArray()
                        || Collection.class.isAssignableFrom(propType)){
                        appendArray(buf, finder, rec.getProperty(i), stack);
                    }else if(Number.class.isAssignableFrom(propType)
                        || (propType.isPrimitive()
                            && (Byte.TYPE.equals(propType)
                                || Short.TYPE.equals(propType)
                                || Integer.TYPE.equals(propType)
                                || Long.TYPE.equals(propType)
                                || Float.TYPE.equals(propType)
                                || Double.TYPE.equals(propType)
                                || Boolean.TYPE.equals(propType)))
                        || Boolean.class.equals(propType)
                    ){
                        appendValue(
                            buf,
                            finder,
                            propType,
                            hasConverter
                                ? rec.getFormatProperty(i) : rec.getProperty(i),
                            stack
                        );
                    }else{
                        appendValue(buf, finder, null, rec.getFormatProperty(i), stack);
                    }
                }
            }
            if(isOutputPropertyName){
                buf.append(OBJECT_ENCLOSURE_END);
            }else{
                buf.append(ARRAY_ENCLOSURE_END);
            }
            return buf;
        }else{
            return super.appendValue(buf, finder, type, value, stack);
        }
    }
}
