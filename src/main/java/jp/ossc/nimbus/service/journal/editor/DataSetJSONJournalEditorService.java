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
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * {@link DataSet}オブジェクトをJSONフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class DataSetJSONJournalEditorService extends ImmutableJournalEditorServiceBase
 implements DataSetJSONJournalEditorServiceMBean{
    
    private static final long serialVersionUID = 3264007026021831179L;
    
    private static final String STRING_ENCLOSURE = "\"";
    
    private static final String ARRAY_SEPARATOR = ",";
    private static final String ARRAY_ENCLOSURE_START = "[";
    private static final String ARRAY_ENCLOSURE_END = "]";
    
    private static final String OBJECT_ENCLOSURE_START = "{";
    private static final String OBJECT_ENCLOSURE_END = "}";
    private static final String PROPERTY_SEPARATOR = ":";
    
    private static final String NULL_VALUE = "null";
    private static final String BOOLEAN_VALUE_TRUE = "true";
    private static final String BOOLEAN_VALUE_FALSE = "false";
    
    private static final String NAME_SCHEMA = "schema";
    private static final String NAME_HEADER = "header";
    private static final String NAME_RECORD_LIST = "recordList";
    private static final String NAME_NESTED_RECORD_LIST = "nestedRecordList";
    private static final String NAME_NESTED_RECORD = "nestedRecord";
    
    private static final char ESCAPE = '\\';
    
    private static final char QUOTE = '"';
    private static final char BACK_SLASH = '\\';
    private static final char SLASH = '/';
    private static final char BACK_SPACE = '\b';
    private static final char CHANGE_PAGE = '\f';
    private static final char LF = '\n';
    private static final char CR = '\r';
    private static final char TAB = '\t';
    
    private static final String ESCAPE_QUOTE = "\\\"";
    private static final String ESCAPE_BACK_SLASH = "\\\\";
    private static final String ESCAPE_SLASH = "\\/";
    private static final String ESCAPE_BACK_SPACE = "\\b";
    private static final String ESCAPE_CHANGE_PAGE = "\\f";
    private static final String ESCAPE_LF = "\\n";
    private static final String ESCAPE_CR = "\\r";
    private static final String ESCAPE_TAB = "\\b";
    
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
    
    protected String toString(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
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
                    appendValue(buf, null, header.getSchema());
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
                    appendValue(buf, null, recList.getSchema());
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
                    appendValue(buf, null, recSchema.getSchema());
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
                    appendValue(buf, null, recSchema.getSchema());
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
                appendValue(buf, null, header);
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
                appendArray(buf, recList);
                if(i != imax - 1){
                    buf.append(ARRAY_SEPARATOR);
                }
            }
            buf.append(OBJECT_ENCLOSURE_END);
            isOutput = true;
        }
        
        buf.append(OBJECT_ENCLOSURE_END);
        buf.append(OBJECT_ENCLOSURE_END);
        return buf.toString();
    }
    
    private StringBuilder appendName(StringBuilder buf, String name){
        buf.append(STRING_ENCLOSURE);
        buf.append(escape(name));
        buf.append(STRING_ENCLOSURE);
        return buf;
    }
    
    private StringBuilder appendValue(StringBuilder buf, Class type, Object value){
        if(type == null && value != null){
            type = value.getClass();
        }
        if(value == null){
            if(type == null){
                buf.append(NULL_VALUE);
            }else if(Number.class.isAssignableFrom(type)
                || (type.isPrimitive()
                    && (Byte.TYPE.equals(type)
                        || Short.TYPE.equals(type)
                        || Integer.TYPE.equals(type)
                        || Long.TYPE.equals(type)
                        || Float.TYPE.equals(type)
                        || Double.TYPE.equals(type)))
            ){
                buf.append('0');
            }else if(Boolean.class.equals(type)
                || Boolean.TYPE.equals(type)
            ){
                buf.append(BOOLEAN_VALUE_FALSE);
            }else{
                buf.append(NULL_VALUE);
            }
        }else if(Boolean.class.equals(type)
            || Boolean.TYPE.equals(type)
        ){
            if(((Boolean)value).booleanValue()){
                buf.append(BOOLEAN_VALUE_TRUE);
            }else{
                buf.append(BOOLEAN_VALUE_FALSE);
            }
        }else if(Number.class.isAssignableFrom(type)
            || (type.isPrimitive()
                && (Byte.TYPE.equals(type)
                    || Short.TYPE.equals(type)
                    || Integer.TYPE.equals(type)
                    || Long.TYPE.equals(type)
                    || Float.TYPE.equals(type)
                    || Double.TYPE.equals(type)))
        ){
            buf.append(value);
        }else if(type.isArray() || Collection.class.isAssignableFrom(type)){
            appendArray(buf, value);
        }else if(Record.class.isAssignableFrom(type)){
            Record rec = (Record)value;
            RecordSchema schema = rec.getRecordSchema();
            PropertySchema[] propSchemata = schema.getPropertySchemata();
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
            for(int i = 0, imax = propSchemata.length; i < imax; i++){
                Object prop = rec.getProperty(i);
                PropertySchema propSchema = propSchemata[i];
                boolean hasConverter = false;
                if(propSchema instanceof DefaultPropertySchema){
                    hasConverter = ((DefaultPropertySchema)propSchema).getFormatConverter() != null;
                }
                if(isOutputPropertyName){
                    appendName(buf, propSchema.getName());
                    buf.append(PROPERTY_SEPARATOR);
                }
                if(prop == null){
                    appendValue(buf, propSchema.getType(), null);
                }else{
                    Class propType = propSchema.getType();
                    if(propType == null){
                        propType = prop.getClass();
                    }
                    if(propType.isArray()
                        || Collection.class.isAssignableFrom(propType)){
                        appendArray(buf, rec.getProperty(i));
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
                            propType,
                            hasConverter
                                ? rec.getFormatProperty(i) : rec.getProperty(i)
                        );
                    }else{
                        appendValue(buf, null, rec.getFormatProperty(i));
                    }
                }
                if(i != imax - 1){
                    buf.append(ARRAY_SEPARATOR);
                }
            }
            if(isOutputPropertyName){
                buf.append(OBJECT_ENCLOSURE_END);
            }else{
                buf.append(ARRAY_ENCLOSURE_END);
            }
        }else{
            buf.append(STRING_ENCLOSURE);
            buf.append(escape(value.toString()));
            buf.append(STRING_ENCLOSURE);
        }
        return buf;
    }
    
    private StringBuilder appendArray(StringBuilder buf, Object array){
        buf.append(ARRAY_ENCLOSURE_START);
        if(array.getClass().isArray()){
            for(int i = 0, imax = Array.getLength(array); i < imax; i++){
                appendValue(buf, null, Array.get(array, i));
                if(i != imax - 1){
                    buf.append(ARRAY_SEPARATOR);
                }
            }
        }else if(List.class.isAssignableFrom(array.getClass())){
            List list = (List)array;
            for(int i = 0, imax = list.size(); i < imax; i++){
                appendValue(buf, null, list.get(i));
                if(i != imax - 1){
                    buf.append(ARRAY_SEPARATOR);
                }
            }
        }else if(Collection.class.isAssignableFrom(array.getClass())){
            Iterator itr = ((Collection)array).iterator();
            while(itr.hasNext()){
                appendValue(buf, null, itr.next());
                if(itr.hasNext()){
                    buf.append(ARRAY_SEPARATOR);
                }
            }
        }
        buf.append(ARRAY_ENCLOSURE_END);
        return buf;
    }
    
    private String escape(String str){
        if(str == null || str.length() == 0){
            return str;
        }
        boolean isEscape = false;
        final StringBuilder buf = new StringBuilder();
        for(int i = 0, imax = str.length(); i < imax; i++){
            final char c = str.charAt(i);
            
            switch(c){
            case QUOTE:
                buf.append(ESCAPE_QUOTE);
                isEscape = true;
                break;
            case BACK_SLASH:
                buf.append(ESCAPE_BACK_SLASH);
                isEscape = true;
                break;
            case SLASH:
                buf.append(ESCAPE_SLASH);
                isEscape = true;
                break;
            case BACK_SPACE:
                buf.append(ESCAPE_BACK_SPACE);
                isEscape = true;
                break;
            case CHANGE_PAGE:
                buf.append(ESCAPE_CHANGE_PAGE);
                isEscape = true;
                break;
            case LF:
                buf.append(ESCAPE_LF);
                isEscape = true;
                break;
            case CR:
                buf.append(ESCAPE_CR);
                isEscape = true;
                break;
            case TAB:
                buf.append(ESCAPE_TAB);
                isEscape = true;
                break;
            default:
                if(!(c == 0x20
                     || c == 0x21
                     || (0x23 <= c && c <= 0x5B)
                     || (0x5D <= c && c <= 0x7E))
                ){
                    isEscape = true;
                    toUnicode(c, buf);
                }else{
                    buf.append(c);
                }
            }
        }
        return isEscape ? buf.toString() : str;
    }
    
    private StringBuilder toUnicode(char c, StringBuilder buf){
        buf.append(ESCAPE);
        buf.append('u');
        int mask = 0xf000;
        for(int i = 0; i < 4; i++){
            mask = 0xf000 >> (i * 4);
            int val = c & mask;
            val = val << (i * 4);
            switch(val){
            case 0x0000:
                buf.append('0');
                break;
            case 0x1000:
                buf.append('1');
                break;
            case 0x2000:
                buf.append('2');
                break;
            case 0x3000:
                buf.append('3');
                break;
            case 0x4000:
                buf.append('4');
                break;
            case 0x5000:
                buf.append('5');
                break;
            case 0x6000:
                buf.append('6');
                break;
            case 0x7000:
                buf.append('7');
                break;
            case 0x8000:
                buf.append('8');
                break;
            case 0x9000:
                buf.append('9');
                break;
            case 0xa000:
                buf.append('a');
                break;
            case 0xb000:
                buf.append('b');
                break;
            case 0xc000:
                buf.append('c');
                break;
            case 0xd000:
                buf.append('d');
                break;
            case 0xe000:
                buf.append('e');
                break;
            case 0xf000:
                buf.append('f');
                break;
            default:
            }
        }
        return buf;
    }
}
