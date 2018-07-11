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
package jp.ossc.nimbus.service.connection;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Iterator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ParameterMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.Blob;
import java.sql.Clob;

import jp.ossc.nimbus.beans.NimbusPropertyEditorManager;
import jp.ossc.nimbus.beans.Property;
import jp.ossc.nimbus.beans.PropertyAccess;
import jp.ossc.nimbus.beans.NestedProperty;
import jp.ossc.nimbus.beans.NoSuchPropertyException;
import jp.ossc.nimbus.beans.dataset.DataSet;
import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.beans.dataset.RecordList;
import jp.ossc.nimbus.beans.dataset.PropertyGetException;
import jp.ossc.nimbus.core.ServiceBase;

/**
 * 永続管理。<p>
 *
 * @author M.Takata
 */
public class DefaultPersistentManagerService extends ServiceBase
 implements PersistentManager, DefaultPersistentManagerServiceMBean{
    
    private static final long serialVersionUID = 301756347991573032L;
    
    private PropertyAccess propertyAccess;
    private boolean isIgnoreNullProperty;
    private Map resultSetJDBCTypeMap;
    
    public void setIgnoreNullProperty(boolean isIgnore){
        isIgnoreNullProperty = isIgnore;
    }
    public boolean isIgnoreNullProperty(){
        return isIgnoreNullProperty;
    }
    
    public void setResultSetJDBCTypeMap(Map mapping){
        resultSetJDBCTypeMap = mapping;
    }
    public Map getResultSetJDBCTypeMap(){
        return resultSetJDBCTypeMap;
    }
    
    public void setResultSetJDBCType(String jdbcType, Class javaType) throws IllegalArgumentException{
        Integer type = null;
        try{
            final Field field = Types.class.getField(jdbcType);
            type = (Integer)field.get(null);
        }catch(NoSuchFieldException e){
            throw new IllegalArgumentException(e.toString());
        }catch(IllegalAccessException e){
            throw new IllegalArgumentException(e.toString());
        }
        resultSetJDBCTypeMap.put(type, javaType);
    }
    
    public void createService() throws Exception{
        resultSetJDBCTypeMap = new HashMap();
        resultSetJDBCTypeMap.put(new Integer(Types.CHAR), String.class);
        resultSetJDBCTypeMap.put(new Integer(Types.VARCHAR), String.class);
        resultSetJDBCTypeMap.put(new Integer(Types.LONGVARCHAR), String.class);
        resultSetJDBCTypeMap.put(new Integer(Types.NUMERIC), java.math.BigDecimal.class);
        resultSetJDBCTypeMap.put(new Integer(Types.DECIMAL), java.math.BigDecimal.class);
        resultSetJDBCTypeMap.put(new Integer(Types.BIT), Boolean.TYPE);
        resultSetJDBCTypeMap.put(new Integer(Types.TINYINT), Byte.TYPE);
        resultSetJDBCTypeMap.put(new Integer(Types.SMALLINT), Short.TYPE);
        resultSetJDBCTypeMap.put(new Integer(Types.INTEGER), Integer.TYPE);
        resultSetJDBCTypeMap.put(new Integer(Types.BIGINT), Long.TYPE);
        resultSetJDBCTypeMap.put(new Integer(Types.REAL), Float.TYPE);
        resultSetJDBCTypeMap.put(new Integer(Types.DOUBLE), Double.TYPE);
        resultSetJDBCTypeMap.put(new Integer(Types.BINARY), byte[].class);
        resultSetJDBCTypeMap.put(new Integer(Types.VARBINARY), byte[].class);
        resultSetJDBCTypeMap.put(new Integer(Types.LONGVARBINARY), byte[].class);
        resultSetJDBCTypeMap.put(new Integer(Types.DATE), java.sql.Date.class);
        resultSetJDBCTypeMap.put(new Integer(Types.TIME), java.sql.Time.class);
        resultSetJDBCTypeMap.put(new Integer(Types.TIMESTAMP), java.sql.Timestamp.class);
        resultSetJDBCTypeMap.put(new Integer(Types.CLOB), java.sql.Clob.class);
        resultSetJDBCTypeMap.put(new Integer(Types.BLOB), java.sql.Blob.class);
        resultSetJDBCTypeMap.put(new Integer(Types.ARRAY), java.sql.Array.class);
        resultSetJDBCTypeMap.put(new Integer(Types.STRUCT), java.sql.Struct.class);
        resultSetJDBCTypeMap.put(new Integer(Types.REF), java.sql.Ref.class);
    }
    
    public void startService() throws Exception{
        propertyAccess = new PropertyAccess();
        propertyAccess.setIgnoreNullProperty(isIgnoreNullProperty);
    }
    
    public Object loadQuery(Connection con, String query, Object input, Object output) throws PersistentException{
        return loadQuery(con, query, input, output, null, null);
    }
    
    public Object loadQuery(Connection con, String query, Object input, Object output, Map statementProps, Map resultSetProps) throws PersistentException{
        final StringBuilder buf = new StringBuilder(query);
        final List inputProps = parseInput(buf);
        final List outputProps = parseOutput(buf);
        String sql = buf.toString();
        return load(con, sql, input, inputProps, output, outputProps, statementProps, resultSetProps);
    }
    
    private List parseInput(StringBuilder query) throws PersistentException{
        return (List)parseQuery(query, "<-{", false);
    }
    
    private List parseOutput(StringBuilder query) throws PersistentException{
        return parseQuery(query, "->{", true);
    }
    
    private List parseQuery(StringBuilder query, String prefix, boolean isSet) throws PersistentException{
        Collection result = null;
        while(true){
            int startIndex = query.indexOf(prefix);
            if(startIndex == -1){
                break;
            }
            boolean isDoubleQuoteEscape = query.charAt(startIndex + 3) == '"';
            boolean isQuoteEscape = query.charAt(startIndex + 3) == '\'';
            int propStartIndex = 0;
            int propEndIndex = 0;
            int endIndex = 0;
            if(isDoubleQuoteEscape){
                propStartIndex = startIndex + 4;
                propEndIndex = query.indexOf("\"}", propStartIndex);
                endIndex = propEndIndex + 2;
            }else if(isQuoteEscape){
                propStartIndex = startIndex + 4;
                propEndIndex = query.indexOf("'}", propStartIndex);
                endIndex = propEndIndex + 2;
            }else{
                propStartIndex = startIndex + 3;
                propEndIndex = query.indexOf("}", propStartIndex);
                endIndex = propEndIndex + 1;
            }
            if(propEndIndex == -1){
                throw new PersistentException("Illegal query : " + query);
            }
            final String propStr = query.substring(propStartIndex, propEndIndex).trim();
            if(propStr.length() == 0){
                throw new PersistentException("Illegal query : " + query);
            }
            if(result == null){
                result = isSet ? (Collection)new LinkedHashSet() : (Collection)new ArrayList();
            }
            result.add(propStr);
            query.delete(startIndex, endIndex);
        }
        return result == null ? null : (isSet ? new ArrayList(result) : (List)result);
    }
    
    public Object load(Connection con, String sql, Object input, Object inputProps, Object output, Object outputProps) throws PersistentException{
        return load(con, sql, input, inputProps, output, outputProps, null, null);
    }
    
    public Object load(Connection con, String sql, Object input, Object inputProps, Object output, Object outputProps, Map statementProps, Map resultSetProps) throws PersistentException{
        List inputPropList = null;
        if(inputProps != null){
            if(inputProps.getClass().isArray()){
                inputPropList = (List)Arrays.asList((Object[])inputProps);
            }else if(inputProps instanceof List){
                inputPropList = (List)inputProps;
            }else if(inputProps instanceof String){
                inputPropList = new ArrayList();
                inputPropList.add(inputProps);
            }else{
                throw new PersistentException("No supported inputProps type." + inputProps);
            }
        }else if(input != null && input instanceof Map){
            inputPropList = new ArrayList(((Map)input).keySet());
        }
        List outputPropList = null;
        Map outputPropMap = null;
        if(outputProps != null){
            if(outputProps.getClass().isArray()){
                outputPropList = (List)Arrays.asList((Object[])outputProps);
            }else if(outputProps instanceof List){
                outputPropList = (List)outputProps;
            }else if(outputProps instanceof Map){
                outputPropMap = (Map)outputProps;
            }else if(outputProps instanceof String){
                outputPropList = new ArrayList();
                outputPropList.add(outputProps);
            }else{
                throw new PersistentException("No supported outputProps type." + outputProps);
            }
        }
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try{
            try{
                statement = con.prepareStatement(sql);
            }catch(SQLException e){
                throw new PersistentException("Illegal sql : " + sql, e);
            }
            setStatementProperties(statement, statementProps);
            try{
                try{
                    final ParameterMetaData metadata
                        = statement.getParameterMetaData();
                    if(inputPropList != null
                        && inputPropList.size() != metadata.getParameterCount()){
                        throw new PersistentException("Illegal sql : " + sql);
                    }
                }catch(IncompatibleClassChangeError e){
                }
                if(input != null){
                    if(inputPropList != null){
                        for(int i = 0, imax = inputPropList.size(); i < imax; i++){
                            Object param = propertyAccess.get(
                                input,
                                inputPropList.get(i).toString()
                            );
                            setObject(statement, i + 1, param);
                        }
                    }else if(input.getClass().isArray()){
                        for(int i = 0, imax = Array.getLength(input); i < imax; i++){
                            Object param = Array.get(input, i);
                            setObject(statement, i + 1, param);
                        }
                    }else if(input instanceof List){
                        List list = (List)input;
                        for(int i = 0, imax = list.size(); i < imax; i++){
                            Object param = list.get(i);
                            setObject(statement, i + 1, param);
                        }
                    }else{
                        setObject(statement, 1, input);
                    }
                }
            }catch(NoSuchPropertyException e){
                throw new PersistentException(
                    "Input bean get error.",
                    e
                );
            }catch(InvocationTargetException e){
                throw new PersistentException(
                    "Input bean get error.",
                    e.getTargetException()
                );
            }catch(SQLException e){
                throw new PersistentException(
                    "The parameter is not suitable for SQL.",
                    e
                );
            }
            try{
                resultSet = statement.executeQuery();
            }catch(SQLException e){
                throw new PersistentException("SQL execute error : " + sql, e);
            }
            setResultSetProperties(resultSet, resultSetProps);
            if(outputPropList != null){
                try{
                    ResultSetMetaData metadata = resultSet.getMetaData();
                    if((outputPropList.size() != metadata.getColumnCount())){
                        throw new PersistentException("Illegal sql : " + sql);
                    }
                    outputPropMap = new LinkedHashMap();
                    for(int i = 0, imax = outputPropList.size(); i < imax; i++){
                        outputPropMap.put(
                            metadata.getColumnName(i + 1),
                            outputPropList.get(i).toString()
                        );
                    }
                }catch(SQLException e){
                    throw new PersistentException(
                        "The parameter is not suitable for SQL.",
                        e
                    );
                }
            }
            return fillOutput(resultSet, output, outputPropMap, false);
        }finally{
            if(statement != null){
                try{
                    statement.close();
                }catch(SQLException e){
                }
                statement = null;
            }
            if(resultSet != null){
                try{
                    resultSet.close();
                }catch(SQLException e){
                }
                resultSet = null;
            }
        }
    }
    
    private Object fillOutput(
        ResultSet resultSet,
        Object output,
        Map outputMapping,
        boolean isCursor
    ) throws PersistentException{
        if(output == null){
            if(isCursor){
                output = new LinkedHashMap();
            }else{
                output = new ArrayList();
            }
        }
        try{
            final ResultSetMetaData metadata = resultSet.getMetaData();
            final int colCount = metadata.getColumnCount();
            boolean isOutputMappingFromMetaData = false;
            if(outputMapping == null
                && (output instanceof RecordList
                        || output instanceof Record
                        || !(output instanceof List))
            ){
                outputMapping = new LinkedHashMap();
                for(int i = 1; i <= colCount; i++){
                    outputMapping.put(
                        metadata.getColumnName(i),
                        metadata.getColumnName(i).toUpperCase()
                    );
                }
                isOutputMappingFromMetaData = true;
            }
            if(output instanceof DataSet){
                final Set headerSet = new LinkedHashSet();
                final Map recordListMap = new LinkedHashMap();
                final Map recordListPropMap = new LinkedHashMap();
                Iterator itr = outputMapping.entrySet().iterator();
                while(itr.hasNext()){
                    final Map.Entry entry = (Map.Entry)itr.next();
                    final Property prop = propertyAccess.getProperty(
                        (String)entry.getValue()
                    );
                    if(prop instanceof NestedProperty){
                        Object obj = ((NestedProperty)prop).getThisProperty()
                            .getProperty(output);
                        if(obj instanceof RecordList){
                            recordListMap.put(entry.getKey(), (RecordList)obj);
                            recordListPropMap.put(
                                entry.getKey(),
                                ((NestedProperty)prop).getNestedProperty()
                            );
                        }else{
                            headerSet.add(entry.getKey());
                        }
                    }else{
                        throw new PersistentException(
                            "Output bean fill error."
                        );
                    }
                }
                final Map recordMap = new HashMap();
                while(true){
                    if(!isCursor){
                        if(!resultSet.next()){
                            break;
                        }
                    }
                    if(headerSet.size() != 0){
                        itr = headerSet.iterator();
                        while(itr.hasNext()){
                            final String columnName = (String)itr.next();
                            setValue(
                                output,
                                (String)outputMapping.get(columnName),
                                resultSet,
                                columnName,
                                isOutputMappingFromMetaData
                            );
                        }
                        headerSet.clear();
                    }
                    recordMap.clear();
                    itr = recordListMap.entrySet().iterator();
                    while(itr.hasNext()){
                        final Map.Entry entry = (Map.Entry)itr.next();
                        final RecordList list = (RecordList)entry.getValue();
                        Record record = (Record)recordMap.get(list);
                        if(record == null){
                            record = list.createRecord();
                            recordMap.put(list, record);
                            list.addRecord(record);
                        }
                        Property prop = (Property)recordListPropMap.get(entry.getKey());
                        prop.setProperty(
                            record,
                            getValue(record, prop, resultSet, (String)entry.getKey())
                        );
                    }
                    if(isCursor){
                        break;
                    }
                }
                return output;
            }else if(output instanceof RecordList){
                RecordList list = (RecordList)output;
                if(list.getSchema() == null){
                    list.setSchema(createSchema(metadata));
                }
                while(true){
                    if(!isCursor){
                        if(!resultSet.next()){
                            break;
                        }
                    }
                    final Record record = list.createRecord();
                    final Iterator itr = outputMapping.entrySet().iterator();
                    while(itr.hasNext()){
                        final Map.Entry entry = (Map.Entry)itr.next();
                        setValue(
                            record,
                            (String)entry.getValue(),
                            resultSet,
                            (String)entry.getKey(),
                            isOutputMappingFromMetaData
                        );
                    }
                    list.addRecord(record);
                    if(isCursor){
                        break;
                    }
                }
                return list;
            }else if(output instanceof List){
                List list = (List)output;
                while(true){
                    if(!isCursor){
                        if(!resultSet.next()){
                            break;
                        }
                    }
                    final Map record = new LinkedHashMap();
                    for(int i = 1; i <= colCount; i++){
                        record.put(
                            metadata.getColumnName(i),
                            resultSet.getObject(i)
                        );
                    }
                    list.add(record);
                    if(isCursor){
                        break;
                    }
                }
                return list;
            }else if(output instanceof Class){
                final Class outputClass = (Class)output;
                if(Record.class.isAssignableFrom(outputClass)){
                    if(!isCursor){
                        if(!resultSet.next()){
                            return null;
                        }
                    }
                    Record record = null;
                    try{
                        record = (Record)outputClass.newInstance();
                        if(record.getSchema() == null){
                            record.setSchema(createSchema(metadata));
                        }
                    }catch(InstantiationException e){
                        throw new PersistentException(
                            "Output bean instantiate error.",
                            e
                        );
                    }catch(IllegalAccessException e){
                        throw new PersistentException(
                            "Output bean instantiate error.",
                            e
                        );
                    }
                    final Iterator itr = outputMapping.entrySet().iterator();
                    while(itr.hasNext()){
                        final Map.Entry entry = (Map.Entry)itr.next();
                        setValue(
                            record,
                            (String)entry.getValue(),
                            resultSet,
                            (String)entry.getKey(),
                            isOutputMappingFromMetaData
                        );
                    }
                    return record;
                }else if(RecordList.class.isAssignableFrom(outputClass)){
                    RecordList list = null;
                    try{
                        list = (RecordList)outputClass.newInstance();
                        if(list.getSchema() == null){
                            list.setSchema(createSchema(metadata));
                        }
                    }catch(InstantiationException e){
                        throw new PersistentException(
                            "Output bean instantiate error.",
                            e
                        );
                    }catch(IllegalAccessException e){
                        throw new PersistentException(
                            "Output bean instantiate error.",
                            e
                        );
                    }
                    while(true){
                        if(!isCursor){
                            if(!resultSet.next()){
                                break;
                            }
                        }
                        final Record record = list.createRecord();
                        final Iterator itr = outputMapping.entrySet().iterator();
                        while(itr.hasNext()){
                            final Map.Entry entry = (Map.Entry)itr.next();
                            setValue(
                                record,
                                (String)entry.getValue(),
                                resultSet,
                                (String)entry.getKey(),
                                isOutputMappingFromMetaData
                            );
                        }
                        list.addRecord(record);
                        if(isCursor){
                            break;
                        }
                    }
                    return list;
                }else if(outputClass.isArray()){
                    List list = new ArrayList();
                    while(true){
                        Object bean = fillOutput(
                            resultSet,
                            outputClass.getComponentType(),
                            outputMapping,
                            isCursor
                        );
                        if(bean == null && (resultSet.isAfterLast() || resultSet.getRow() == 0)){
                            break;
                        }
                        list.add(bean);
                        if(isCursor){
                            break;
                        }
                    }
                    return listToArray(list, outputClass.getComponentType());
                }else if(String.class.equals(outputClass) || outputClass.isPrimitive() || Number.class.isAssignableFrom(outputClass)){
                    if(!isCursor){
                        if(!resultSet.next()){
                            return null;
                        }
                    }
                    return getValue(outputClass, resultSet, 1);
                }else{
                    if(isCursor){
                        Object bean = null;
                        try{
                            bean = outputClass.newInstance();
                        }catch(InstantiationException e){
                            throw new PersistentException(
                                "Output bean instantiate error.",
                                e
                            );
                        }catch(IllegalAccessException e){
                            throw new PersistentException(
                                "Output bean instantiate error.",
                                e
                            );
                        }
                        final Iterator itr = outputMapping.entrySet().iterator();
                        while(itr.hasNext()){
                            final Map.Entry entry = (Map.Entry)itr.next();
                            setValue(
                                bean,
                                (String)entry.getValue(),
                                resultSet,
                                (String)entry.getKey(),
                                isOutputMappingFromMetaData
                            );
                        }
                        return bean;
                    }else{
                        final List list = new ArrayList();
                        while(resultSet.next()){
                            Object bean = null;
                            try{
                                bean = outputClass.newInstance();
                            }catch(InstantiationException e){
                                if(isOutputMappingFromMetaData
                                    && outputMapping.size() == 1
                                    && list.size() == 0
                                ){
                                    return getValue(outputClass, resultSet, 1);
                                }
                                throw new PersistentException(
                                    "Output bean instantiate error.",
                                    e
                                );
                            }catch(IllegalAccessException e){
                                if(isOutputMappingFromMetaData
                                    && outputMapping.size() == 1
                                    && list.size() == 0
                                ){
                                    return getValue(outputClass, resultSet, 1);
                                }
                                throw new PersistentException(
                                    "Output bean instantiate error.",
                                    e
                                );
                            }
                            final Iterator itr = outputMapping.entrySet().iterator();
                            while(itr.hasNext()){
                                final Map.Entry entry = (Map.Entry)itr.next();
                                try{
                                    setValue(
                                        bean,
                                        (String)entry.getValue(),
                                        resultSet,
                                        (String)entry.getKey(),
                                        isOutputMappingFromMetaData
                                    );
                                }catch(PersistentException e){
                                    if(isOutputMappingFromMetaData
                                        && outputMapping.size() == 1
                                        && list.size() == 0
                                    ){
                                        return getValue(outputClass, resultSet, 1);
                                    }
                                    throw e;
                                }
                            }
                            list.add(bean);
                        }
                        return list;
                    }
                }
            }else{
                if(!isCursor){
                    if(!resultSet.next()){
                        return null;
                    }
                }
                if(output instanceof Record){
                    Record record = (Record)output;
                    if(record.getSchema() == null){
                        record.setSchema(createSchema(metadata));
                    }
                }
                final Iterator itr = outputMapping.entrySet().iterator();
                while(itr.hasNext()){
                    final Map.Entry entry = (Map.Entry)itr.next();
                    setValue(
                        output,
                        (String)entry.getValue(),
                        resultSet,
                        (String)entry.getKey(),
                        isOutputMappingFromMetaData
                    );
                }
                return output;
            }
        }catch(IllegalArgumentException e){
            throw new PersistentException(
                "Output bean fill error.",
                e
            );
        }catch(NoSuchPropertyException e){
            throw new PersistentException(
                "Output bean fill error.",
                e
            );
        }catch(InvocationTargetException e){
            throw new PersistentException(
                "Output bean fill error.",
                e.getTargetException()
            );
        }catch(SQLException e){
            throw new PersistentException(
                "Output bean fill error.",
                e
            );
        }
    }
    
    private Object listToArray(List list, Class componentType){
        if(componentType.isPrimitive()){
            Object array = (Object)Array.newInstance(componentType, list.size());
            if(Byte.TYPE.equals(componentType)){
                for(int i = 0, imax = list.size(); i < imax; i++){
                    Array.setByte(array, i, ((Number)list.get(i)).byteValue());
                }
            }else if(Short.TYPE.equals(componentType)){
                for(int i = 0, imax = list.size(); i < imax; i++){
                    Array.setShort(array, i, ((Number)list.get(i)).shortValue());
                }
            }else if(Integer.TYPE.equals(componentType)){
                for(int i = 0, imax = list.size(); i < imax; i++){
                    Array.setInt(array, i, ((Number)list.get(i)).intValue());
                }
            }else if(Long.TYPE.equals(componentType)){
                for(int i = 0, imax = list.size(); i < imax; i++){
                    Array.setLong(array, i, ((Number)list.get(i)).longValue());
                }
            }else if(Float.TYPE.equals(componentType)){
                for(int i = 0, imax = list.size(); i < imax; i++){
                    Array.setFloat(array, i, ((Number)list.get(i)).floatValue());
                }
            }else if(Double.TYPE.equals(componentType)){
                for(int i = 0, imax = list.size(); i < imax; i++){
                    Array.setDouble(array, i, ((Number)list.get(i)).doubleValue());
                }
            }else if(Boolean.TYPE.equals(componentType)){
                for(int i = 0, imax = list.size(); i < imax; i++){
                    Array.setBoolean(array, i, ((Boolean)list.get(i)).booleanValue());
                }
            }
            return array;
        }else{
            return list.toArray((Object[])Array.newInstance(componentType, list.size()));
        }
    }
    
    private void setValue(
        Object target,
        String propName,
        ResultSet rs,
        String cloumnName,
        boolean isIgnoreCase
    ) throws PersistentException{
        int index = 0;
        try{
            index = rs.findColumn(cloumnName);
        }catch(SQLException e){
            throw new PersistentException(
                "Output bean fill error.",
                e
            );
        }
        setValue(target, propName, rs, index, isIgnoreCase);
    }
    
    private void setValue(
        Object target,
        String propName,
        ResultSet rs,
        int index,
        boolean isIgnoreCase
    ) throws PersistentException{
        try{
            propertyAccess.set(
                target,
                propName,
                getValue(target, propName, rs, index)
            );
        }catch(IllegalArgumentException e){
            throw new PersistentException(
                "Output bean fill error.",
                e
            );
        }catch(NoSuchPropertyException e){
            if(isIgnoreCase){
                try{
                    propertyAccess.set(
                        target,
                        propName.toLowerCase(),
                        getValue(target, propName, rs, index)
                    );
                }catch(IllegalArgumentException e2){
                    throw new PersistentException(
                        "Output bean fill error.",
                        e2
                    );
                }catch(NoSuchPropertyException e2){
                    throw new PersistentException(
                        "Output bean fill error.",
                        e2
                    );
                }catch(InvocationTargetException e2){
                    throw new PersistentException(
                        "Output bean fill error.",
                        e2.getTargetException()
                    );
                }
            }else{
                throw new PersistentException(
                    "Output bean fill error.",
                    e
                );
            }
        }catch(InvocationTargetException e){
            Throwable targetException = e.getTargetException();
            if(isIgnoreCase && (targetException instanceof PropertyGetException)){
                try{
                    propertyAccess.set(
                        target,
                        propName.toLowerCase(),
                        getValue(target, propName, rs, index)
                    );
                }catch(IllegalArgumentException e2){
                    throw new PersistentException(
                        "Output bean fill error.",
                        e2
                    );
                }catch(NoSuchPropertyException e2){
                    throw new PersistentException(
                        "Output bean fill error.",
                        e2
                    );
                }catch(InvocationTargetException e2){
                    throw new PersistentException(
                        "Output bean fill error.",
                        e2.getTargetException()
                    );
                }
            }else{
                throw new PersistentException(
                    "Output bean fill error.",
                    e.getTargetException()
                );
            }
        }
    }
    
    private Object getValue(
        Object target,
        String propName,
        ResultSet rs,
        int index
    ) throws PersistentException{
        try{
            return getValue(target, propertyAccess.getProperty(propName), rs, index);
        }catch(IllegalArgumentException e){
            throw new PersistentException(
                "Output bean fill error.",
                e
            );
        }
    }
    
    private Object getValue(
        Object target,
        Property prop,
        ResultSet rs,
        String cloumnName
    ) throws PersistentException{
        int index = 0;
        try{
            index = rs.findColumn(cloumnName);
        }catch(SQLException e){
            throw new PersistentException(
                "Output bean fill error.",
                e
            );
        }
        return getValue(target, prop, rs, index);
    }
    
    private Object getValue(
        Object target,
        Property prop,
        ResultSet rs,
        int index
    ) throws PersistentException{
        try{
            return getValue(prop.getPropertyType(target), rs, index);
        }catch(NoSuchPropertyException e){
            throw new PersistentException(
                "Output bean fill error.",
                e
            );
        }catch(InvocationTargetException e){
            throw new PersistentException(
                "Output bean fill error.",
                e.getTargetException()
            );
        }
    }
    
    private Object getValue(
        Class type,
        ResultSet rs,
        int index
    ) throws PersistentException{
        try{
            final ResultSetMetaData metadata = rs.getMetaData();
            final int jdbcType = metadata.getColumnType(index);
            Object value = null;
            switch(jdbcType){
            case Types.BLOB:
            case Types.BINARY:
            case Types.CLOB:
                switch(jdbcType){
                case Types.BLOB:
                    Blob blob = null;
                    if(byte[].class.equals(type)){
                        blob = rs.getBlob(index);
                        if(blob != null){
                            value = blob.getBytes(1l, (int)blob.length());
                        }
                    }else if(InputStream.class.equals(type)){
                        value = rs.getBinaryStream(index);
                    }else if(String.class.equals(type)){
                        blob = rs.getBlob(index);
                        if(blob != null){
                            value = new String(blob.getBytes(1l, (int)blob.length()));
                        }
                    }else{
                        value = rs.getBlob(index);
                    }
                    break;
                case Types.BINARY:
                    if(InputStream.class.equals(type)){
                        value = rs.getBinaryStream(index);
                    }else if(String.class.equals(type)){
                        value = new String(rs.getBytes(index));
                    }else{
                        value = rs.getBytes(index);
                    }
                    break;
                case Types.CLOB:
                    Clob clob = null;
                    if(char[].class.equals(type)){
                        clob = rs.getClob(index);
                        if(clob != null){
                            Reader r = clob.getCharacterStream();
                            value = new char[(int)clob.length()];
                            r.read((char[])value);
                            r.close();
                        }
                    }else if(InputStream.class.equals(type)){
                        value = rs.getAsciiStream(index);
                    }else if(Reader.class.equals(type)){
                        clob = rs.getClob(index);
                        if(clob != null){
                            value = clob.getCharacterStream();
                        }
                    }else if(String.class.equals(type)){
                        clob = rs.getClob(index);
                        if(clob != null){
                            value = clob.getSubString(1l, (int)clob.length());
                        }
                    }else{
                        value = rs.getClob(index);
                    }
                    break;
                }
                break;
            case Types.DECIMAL:
            case Types.NUMERIC:
                if(Byte.TYPE.equals(type) || Byte.class.equals(type)){
                    value = new Byte(rs.getByte(index));
                }else if(Short.TYPE.equals(type) || Short.class.equals(type)){
                    value = new Short(rs.getShort(index));
                }else if(Integer.TYPE.equals(type) || Integer.class.equals(type)){
                    value = new Integer(rs.getInt(index));
                }else if(Long.TYPE.equals(type) || Long.class.equals(type)){
                    value = new Long(rs.getLong(index));
                }else if(Float.TYPE.equals(type) || Float.class.equals(type)){
                    value = new Float(rs.getFloat(index));
                }else if(Double.TYPE.equals(type) || Double.class.equals(type)){
                    value = new Double(rs.getDouble(index));
                }else if(BigInteger.class.equals(type)){
                    value = new BigInteger(rs.getString(index));
                }else if(BigDecimal.class.equals(type)){
                    value = rs.getBigDecimal(index);
                }else if(String.class.equals(type)){
                    value = rs.getObject(index);
                    if(value != null){
                        value = value.toString();
                    }
                }else{
                    value = rs.getObject(index);
                }
                if(rs.wasNull()){
                    value = null;
                }
                break;
            case Types.CHAR:
            case Types.VARCHAR:
                if(!String.class.equals(type)){
                    PropertyEditor editor = NimbusPropertyEditorManager.findEditor(type);
                    if(editor != null){
                        editor.setAsText(rs.getString(index));
                        value = editor.getValue();
                    }else{
                        value = rs.getObject(index);
                    }
                }else{
                    value = rs.getString(index);
                }
                break;
            case Types.TIME:
                value = rs.getTime(index);
                break;
            case Types.TIMESTAMP:
                value = rs.getTimestamp(index);
                break;
            default:
                value = rs.getObject(index);
                break;
            }
            return value;
        }catch(IOException e){
            throw new PersistentException(
                "Output bean fill error.",
                e
            );
        }catch(SQLException e){
            throw new PersistentException(
                "Output bean fill error.",
                e
            );
        }
    }
    
    private void setObject(
        PreparedStatement statement,
        int index,
        Object value
    ) throws PersistentException{
        try{
            if(value != null){
                if(value instanceof byte[]){
                    statement.setBinaryStream(
                        index,
                        new ByteArrayInputStream((byte[])value),
                        ((byte[])value).length
                    );
                    return;
                }else if(value instanceof InputStream){
                    InputStream is = (InputStream)value;
                    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] bytes = new byte[1024];
                    int length = 0;
                    try{
                        while((length = is.read(bytes)) != -1){
                            baos.write(bytes, 0, length);
                        }
                    }catch(IOException e){
                        throw new PersistentException(e);
                    }
                    bytes = baos.toByteArray();
                    statement.setBinaryStream(
                        index,
                        new ByteArrayInputStream(bytes),
                        bytes.length
                    );
                    return;
                }else if(value instanceof char[]){
                    statement.setCharacterStream(
                        index,
                        new CharArrayReader((char[])value),
                        ((char[])value).length
                    );
                    return;
                }else if(value instanceof Reader){
                    Reader reader = (Reader)value;
                    final CharArrayWriter caw = new CharArrayWriter();
                    char[] chars = new char[1024];
                    int length = 0;
                    try{
                        while((length = reader.read(chars)) != -1){
                            caw.write(chars, 0, length);
                        }
                    }catch(IOException e){
                        throw new PersistentException(e);
                    }
                    chars = caw.toCharArray();
                    statement.setCharacterStream(
                        index,
                        new CharArrayReader(chars),
                        chars.length
                    );
                    return;
                }
            }
            statement.setObject(index, value);
        }catch(SQLException e){
            throw new PersistentException(
                "The parameter is not suitable for SQL.",
                e
            );
        }
    }
    
    private String createSchema(ResultSetMetaData metadata) throws SQLException{
        final int colCount = metadata.getColumnCount();
        final StringBuilder buf = new StringBuilder();
        for(int i = 1; i <= colCount; i++){
            final Class type = (Class)resultSetJDBCTypeMap
                .get(new Integer(metadata.getColumnType(i)));
            buf.append(':')
               .append(metadata.getColumnName(i));
            if(type != null){
                buf.append(',')
                   .append(type.getName());
            }
            if(i != colCount){
                buf.append('\n');
            }
        }
        return buf.toString();
    }
    
    public Cursor createQueryCursor(Connection con, String query, Object input) throws PersistentException{
        return createQueryCursor(con, query, input, null, null);
    }
    
    public Cursor createQueryCursor(Connection con, String query, Object input, Map statementProps, Map resultSetProps) throws PersistentException{
        final StringBuilder buf = new StringBuilder(query);
        final List inputProps = parseInput(buf);
        final List outputProps = parseOutput(buf);
        String sql = buf.toString();
        return createCursor(con, sql, input, inputProps, outputProps, statementProps, resultSetProps);
    }
    
    public Cursor createCursor(Connection con, String sql, Object input, Object inputProps, Object outputProps) throws PersistentException{
        return createCursor(con, sql, input, inputProps, outputProps, null, null);
    }
    
    public Cursor createCursor(Connection con, String sql, Object input, Object inputProps, Object outputProps, Map statementProps, Map resultSetProps) throws PersistentException{
        List inputPropList = null;
        if(inputProps != null){
            if(inputProps.getClass().isArray()){
                inputPropList = (List)Arrays.asList((Object[])inputProps);
            }else if(inputProps instanceof List){
                inputPropList = (List)inputProps;
            }else if(inputProps instanceof String){
                inputPropList = new ArrayList();
                inputPropList.add(inputProps);
            }else{
                throw new PersistentException("No supported inputProps type." + inputProps);
            }
        }else if(input != null && input instanceof Map){
            inputPropList = new ArrayList(((Map)input).keySet());
        }
        List outputPropList = null;
        Map outputPropMap = null;
        if(outputProps != null){
            if(outputProps.getClass().isArray()){
                outputPropList = (List)Arrays.asList((Object[])outputProps);
            }else if(outputProps instanceof List){
                outputPropList = (List)outputProps;
            }else if(outputProps instanceof Map){
                outputPropMap = (Map)outputProps;
            }else if(outputProps instanceof String){
                outputPropList = new ArrayList();
                outputPropList.add(outputProps);
            }else{
                throw new PersistentException("No supported outputProps type." + outputProps);
            }
        }
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try{
            statement = con.prepareStatement(sql);
        }catch(SQLException e){
            throw new PersistentException("Illegal sql : " + sql, e);
        }
        setStatementProperties(statement, statementProps);
        try{
            try{
                final ParameterMetaData metadata
                    = statement.getParameterMetaData();
                if(inputPropList != null
                    && inputPropList.size() != metadata.getParameterCount()){
                    throw new PersistentException("Illegal sql : " + sql);
                }
            }catch(IncompatibleClassChangeError e){
            }
            if(input != null){
                if(inputPropList != null){
                    for(int i = 0, imax = inputPropList.size(); i < imax; i++){
                        Object param = propertyAccess.get(
                            input,
                            inputPropList.get(i).toString()
                        );
                        setObject(statement, i + 1, param);
                    }
                }else if(input.getClass().isArray()){
                    for(int i = 0, imax = Array.getLength(input); i < imax; i++){
                        Object param = Array.get(input, i);
                        setObject(statement, i + 1, param);
                    }
                }else if(input instanceof List){
                    List list = (List)input;
                    for(int i = 0, imax = list.size(); i < imax; i++){
                        Object param = list.get(i);
                        setObject(statement, i + 1, param);
                    }
                }else{
                    setObject(statement, 1, input);
                }
            }
        }catch(NoSuchPropertyException e){
            throw new PersistentException(
                "Input bean get error.",
                e
            );
        }catch(InvocationTargetException e){
            throw new PersistentException(
                "Input bean get error.",
                e.getTargetException()
            );
        }catch(SQLException e){
            throw new PersistentException(
                "The parameter is not suitable for SQL.",
                e
            );
        }
        try{
            resultSet = statement.executeQuery();
        }catch(SQLException e){
            throw new PersistentException("SQL execute error : " + sql, e);
        }
        setResultSetProperties(resultSet, resultSetProps);
        if(outputPropList != null){
            try{
                ResultSetMetaData metadata = resultSet.getMetaData();
                if((outputPropList.size() != metadata.getColumnCount())){
                    throw new PersistentException("Illegal sql : " + sql);
                }
                outputPropMap = new LinkedHashMap();
                for(int i = 0, imax = outputPropList.size(); i < imax; i++){
                    outputPropMap.put(
                        metadata.getColumnName(i + 1),
                        outputPropList.get(i).toString()
                    );
                }
            }catch(SQLException e){
                throw new PersistentException(
                    "The parameter is not suitable for SQL.",
                    e
                );
            }
        }
        return new CursorImpl(
            statement,
            resultSet,
            outputPropMap
        );
    }
    
    public int persistQuery(Connection con, String query, Object input) throws PersistentException{
        return persistQuery(con, query, input, null);
    }
    
    public int persistQuery(Connection con, String query, Object input, Map statementProps) throws PersistentException{
        final StringBuilder buf = new StringBuilder(query);
        final List inputProps = parseInput(buf);
        String sql = buf.toString();
        return persist(con, sql, input, inputProps, statementProps);
    }
    
    public int persist(Connection con, String sql, Object input, Object inputProps) throws PersistentException{
        return persist(con, sql, input, inputProps, null);
    }
    
    public int persist(Connection con, String sql, Object input, Object inputProps, Map statementProps) throws PersistentException{
        List inputPropList = null;
        if(inputProps != null){
            if(inputProps.getClass().isArray()){
                inputPropList = (List)Arrays.asList((Object[])inputProps);
            }else if(inputProps instanceof List){
                inputPropList = (List)inputProps;
            }else if(inputProps instanceof String){
                inputPropList = new ArrayList();
                inputPropList.add(inputProps);
            }else{
                throw new PersistentException("No supported inputProps type." + inputProps);
            }
        }
        PreparedStatement statement = null;
        try{
            try{
                statement = con.prepareStatement(sql);
                final ParameterMetaData metadata
                    = statement.getParameterMetaData();
                if(inputPropList != null
                    && (inputPropList.size() != metadata.getParameterCount())){
                    throw new PersistentException("Illegal sql : " + sql);
                }
            }catch(SQLException e){
                throw new PersistentException("Illegal sql : " + sql, e);
            }catch(IncompatibleClassChangeError e){
            }
            setStatementProperties(statement, statementProps);
            return persistQueryInternal(sql, statement, input, inputPropList, false);
        }finally{
            if(statement != null){
                try{
                    statement.close();
                }catch(SQLException e){
                }
                statement = null;
            }
        }
    }
    
    private int persistQueryInternal(
        String sql,
        PreparedStatement statement,
        Object input,
        List inputProps,
        boolean isBatch
    ) throws PersistentException{
        if(input instanceof DataSet){
            if(inputProps == null){
                throw new PersistentException(
                    "Input bean get error."
                );
            }
            final List beans = new ArrayList();
            final List properties = new ArrayList();
            int count = -1;
            try{
                final Iterator itr = inputProps.iterator();
                while(itr.hasNext()){
                    final Object propStr = itr.next();
                    final Property prop = propertyAccess.getProperty(propStr.toString());
                    if(prop instanceof NestedProperty){
                        Object obj = ((NestedProperty)prop).getThisProperty()
                            .getProperty(input);
                        if(obj instanceof RecordList){
                            final int size = ((RecordList)obj).size();
                            if(count == -1){
                                count = size;
                            }else if(count != size){
                                throw new PersistentException(
                                    "Input bean get error."
                                );
                            }
                        }
                        beans.add(obj);
                        properties.add(
                            ((NestedProperty)prop).getNestedProperty()
                        );
                    }else{
                        throw new PersistentException(
                            "Input bean get error."
                        );
                    }
                }
            }catch(NoSuchPropertyException e){
                throw new PersistentException(
                    "Input bean get error.",
                    e
                );
            }catch(InvocationTargetException e){
                throw new PersistentException(
                    "Input bean get error.",
                    e.getTargetException()
                );
            }
            if(count == 0){
                return 0;
            }
            if(count == -1){
                try{
                    for(int i = 0, imax = beans.size(); i < imax; i++){
                        Object param = ((Property)properties.get(i)).getProperty(
                            beans.get(i)
                        );
                        setObject(statement, i + 1, param);
                    }
                }catch(NoSuchPropertyException e){
                    throw new PersistentException(
                        "Input bean get error.",
                        e
                    );
                }catch(InvocationTargetException e){
                    throw new PersistentException(
                        "Input bean get error.",
                        e.getTargetException()
                    );
                }
                try{
                    if(isBatch){
                        statement.addBatch();
                        return -1;
                    }else{
                        return statement.executeUpdate();
                    }
                }catch(SQLException e){
                    throw new PersistentException("SQL execute error : " + sql, e);
                }
            }else{
                for(int i = 0; i < count; i++){
                    try{
                        for(int j = 0, jmax = beans.size(); j < jmax; j++){
                            Object bean = beans.get(j);
                            if(bean instanceof RecordList){
                                bean = ((RecordList)bean).get(i);
                            }
                            Object param = ((Property)properties.get(j)).getProperty(
                                bean
                            );
                            setObject(statement, j + 1, param);
                        }
                    }catch(NoSuchPropertyException e){
                        throw new PersistentException(
                            "Input bean get error.",
                            e
                        );
                    }catch(InvocationTargetException e){
                        throw new PersistentException(
                            "Input bean get error.",
                            e.getTargetException()
                        );
                    }
                    try{
                        statement.addBatch();
                    }catch(SQLException e){
                        throw new PersistentException("SQL add batch error : " + sql, e);
                    }
                }
                if(isBatch){
                    return -1;
                }else{
                    int[] updateCounts = null;
                    try{
                        updateCounts = statement.executeBatch();
                    }catch(SQLException e){
                        throw new PersistentException("SQL execute error : " + sql, e);
                    }
                    int result = 0;
                    for(int i = 0; i < updateCounts.length; i++){
                        if(updateCounts[i] > 0){
                            result += updateCounts[i];
                        }
                    }
                    if(result == 0){
                        try{
                            result = statement.getUpdateCount();
                        }catch(SQLException e){
                        }
                    }
                    return result;
                }
            }
        }else if((input instanceof List)
            || (input != null && input.getClass().isArray())
        ){
            List list = null;
            if(input instanceof List){
                list = (List)input;
            }else{
                list = (List)Arrays.asList((Object[])input);
            }
            if(inputProps != null){
                if(list.size() == 0){
                    return 0;
                }
                for(int j = 0, jmax = list.size(); j < jmax; j++ ){
                    Object bean = list.get(j);
                    try{
                        for(int i = 0, imax = inputProps.size(); i < imax; i++){
                            Object param = propertyAccess.get(
                                bean,
                                inputProps.get(i).toString()
                            );
                            setObject(statement, i + 1, param);
                        }
                    }catch(NoSuchPropertyException e){
                        throw new PersistentException(
                            "Input bean get error.",
                            e
                        );
                    }catch(InvocationTargetException e){
                        throw new PersistentException(
                            "Input bean get error.",
                            e.getTargetException()
                        );
                    }
                    try{
                        statement.addBatch();
                    }catch(SQLException e){
                        throw new PersistentException("SQL add batch error : " + sql, e);
                    }
                }
                if(isBatch){
                    return -1;
                }else{
                    int[] updateCounts = null;
                    try{
                        updateCounts = statement.executeBatch();
                    }catch(SQLException e){
                        throw new PersistentException("SQL execute error : " + sql, e);
                    }
                    int result = 0;
                    for(int i = 0; i < updateCounts.length; i++){
                        if(updateCounts[i] > 0){
                            result += updateCounts[i];
                        }
                    }
                    if(result == 0){
                        try{
                            result = statement.getUpdateCount();
                        }catch(SQLException e){
                        }
                    }
                    return result;
                }
            }else{
                if(list.size() == 0){
                    return 0;
                }
                int result = 0;
                for(int i = 0, imax = list.size(); i < imax; i++){
                    Object bean = list.get(i);
                    if(bean instanceof Map){
                        try{
                            Iterator propItr = ((Map)bean).keySet().iterator();
                            int j = 0;
                            while(propItr.hasNext()){
                                Object param = propertyAccess.get(
                                    bean,
                                    propItr.next().toString()
                                );
                                setObject(statement, ++j, param);
                            }
                        }catch(NoSuchPropertyException e){
                            throw new PersistentException(
                                "Input bean get error.",
                                e
                            );
                        }catch(InvocationTargetException e){
                            throw new PersistentException(
                                "Input bean get error.",
                                e.getTargetException()
                            );
                        }
                        try{
                            statement.addBatch();
                        }catch(SQLException e){
                            throw new PersistentException("SQL add batch error : " + sql, e);
                        }
                        if(i == imax - 1){
                            if(isBatch){
                                result = -1;
                            }else{
                                int updateCount = 0;
                                int[] updateCounts = null;
                                try{
                                    updateCounts = statement.executeBatch();
                                }catch(SQLException e){
                                    throw new PersistentException("SQL execute error : " + sql, e);
                                }
                                for(int j = 0; j < updateCounts.length; j++){
                                    if(updateCounts[j] > 0){
                                        updateCount += updateCounts[j];
                                    }
                                }
                                if(updateCount == 0){
                                    try{
                                        updateCount = statement.getUpdateCount();
                                    }catch(SQLException e){
                                    }
                                }
                                result = updateCount;
                            }
                        }
                    }else{
                        setObject(statement, i + 1, bean);
                        if(i == imax - 1){
                            if(isBatch){
                                try{
                                    statement.addBatch();
                                }catch(SQLException e){
                                    throw new PersistentException("SQL add batch error : " + sql, e);
                                }
                                result = -1;
                            }else{
                                try{
                                    result = statement.executeUpdate();
                                }catch(SQLException e){
                                    throw new PersistentException("SQL execute error : " + sql, e);
                                }
                            }
                        }
                    }
                }
                return result;
            }
        }else{
            try{
                if(input != null){
                    if(inputProps != null){
                        for(int i = 0, imax = inputProps.size(); i < imax; i++){
                            Object param = propertyAccess.get(
                                input,
                                inputProps.get(i).toString()
                            );
                            setObject(statement, i + 1, param);
                        }
                    }else{
                        if(input instanceof Map){
                            Iterator propItr = ((Map)input).keySet().iterator();
                            int i = 0;
                            while(propItr.hasNext()){
                                Object param = propertyAccess.get(
                                    input,
                                    propItr.next().toString()
                                );
                                setObject(statement, ++i, param);
                            }
                        }else{
                            setObject(statement, 1, input);
                        }
                    }
                }else{
                    if(inputProps != null){
                        for(int i = 0, imax = inputProps.size(); i < imax; i++){
                            setObject(statement, i + 1, null);
                        }
                    }else{
                        int parameterCount = 0;
                        try{
                            ParameterMetaData paramMetaData = statement.getParameterMetaData();
                            parameterCount = paramMetaData.getParameterCount();
                        }catch(SQLException e){
                            throw new PersistentException("Illegal sql : " + sql, e);
                        }catch(IncompatibleClassChangeError e){
                        }
                        if(parameterCount != 0){
                            setObject(statement, 1, input);
                        }
                    }
                }
            }catch(NoSuchPropertyException e){
                throw new PersistentException(
                    "Input bean get error.",
                    e
                );
            }catch(InvocationTargetException e){
                throw new PersistentException(
                    "Input bean get error.",
                    e.getTargetException()
                );
            }
            if(isBatch){
                try{
                    statement.addBatch();
                }catch(SQLException e){
                    throw new PersistentException("SQL add batch error : " + sql, e);
                }
                return -1;
            }else{
                try{
                    return statement.executeUpdate();
                }catch(SQLException e){
                    throw new PersistentException("SQL execute error : " + sql, e);
                }
            }
        }
    }
    
    public BatchExecutor createQueryBatchExecutor(Connection con, String query) throws PersistentException{
        return createQueryBatchExecutor(con, query, null);
    }
    
    public BatchExecutor createQueryBatchExecutor(Connection con, String query, Map statementProps) throws PersistentException{
        final StringBuilder buf = new StringBuilder(query);
        final List inputProps = parseInput(buf);
        String sql = buf.toString();
        return new BatchExecutorImpl(con, sql, inputProps, statementProps);
    }
    
    public BatchExecutor createBatchExecutor(Connection con, String sql, Object inputProps) throws PersistentException{
        return createBatchExecutor(con, sql, inputProps, null);
    }
    
    public BatchExecutor createBatchExecutor(Connection con, String sql, Object inputProps, Map statementProps) throws PersistentException{
        List inputPropList = null;
        if(inputProps != null){
            if(inputProps.getClass().isArray()){
                inputPropList = (List)Arrays.asList((Object[])inputProps);
            }else if(inputProps instanceof List){
                inputPropList = (List)inputProps;
            }else if(inputProps instanceof String){
                inputPropList = new ArrayList();
                inputPropList.add(inputProps);
            }else{
                throw new PersistentException("No supported inputProps type." + inputProps);
            }
        }
        return new BatchExecutorImpl(con, sql, inputPropList, statementProps);
    }
    
    private void setStatementProperties(PreparedStatement st, Map statementProps) throws PersistentException{
        if(statementProps == null || statementProps.size() == 0){
            return;
        }
        try{
            Iterator entries = statementProps.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                propertyAccess.set(
                    st,
                    (String)entry.getKey(),
                    entry.getValue()
                );
            }
        }catch(NoSuchPropertyException e){
            throw new PersistentException(
                "Statement property set error.",
                e
            );
        }catch(InvocationTargetException e){
            throw new PersistentException(
                "Statement property set error.",
                e.getTargetException()
            );
        }
    }
    
    private void setResultSetProperties(ResultSet rs, Map resultSetProps) throws PersistentException{
        if(resultSetProps == null || resultSetProps.size() == 0){
            return;
        }
        try{
            Iterator entries = resultSetProps.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                propertyAccess.set(
                    rs,
                    (String)entry.getKey(),
                    entry.getValue()
                );
            }
        }catch(NoSuchPropertyException e){
            throw new PersistentException(
                "ResultSet property set error.",
                e
            );
        }catch(InvocationTargetException e){
            throw new PersistentException(
                "ResultSet property set error.",
                e.getTargetException()
            );
        }
    }
    
    /**
     * 読み込みカーソル。<p>
     *
     * @author M.Takata
     */
    private class CursorImpl implements Cursor{
        private PreparedStatement statement;
        private ResultSet resultSet;
        private Map outputMapping;
        
        public CursorImpl(
            PreparedStatement statement,
            ResultSet resultSet,
            Map outputMapping
        ){
            this.statement = statement;
            this.resultSet = resultSet;
            this.outputMapping = outputMapping;
        }
        
        public boolean next() throws PersistentException{
            if(resultSet == null){
                throw new PersistentException("Closed");
            }
            try{
                return resultSet.next();
            }catch(SQLException e){
                throw new PersistentException(e);
            }
        }
        
        public boolean previous() throws PersistentException{
            if(resultSet == null){
                throw new PersistentException("Closed");
            }
            try{
                return resultSet.previous();
            }catch(SQLException e){
                throw new PersistentException(e);
            }
        }
        
        public boolean first() throws PersistentException{
            if(resultSet == null){
                throw new PersistentException("Closed");
            }
            try{
                return resultSet.first();
            }catch(SQLException e){
                throw new PersistentException(e);
            }
        }
        
        public boolean last() throws PersistentException{
            if(resultSet == null){
                throw new PersistentException("Closed");
            }
            try{
                return resultSet.last();
            }catch(SQLException e){
                throw new PersistentException(e);
            }
        }
        
        public void beforeFirst() throws PersistentException{
            if(resultSet == null){
                throw new PersistentException("Closed");
            }
            try{
                resultSet.beforeFirst();
            }catch(SQLException e){
                throw new PersistentException(e);
            }
        }
        
        public void afterLast() throws PersistentException{
            if(resultSet == null){
                throw new PersistentException("Closed");
            }
            try{
                resultSet.afterLast();
            }catch(SQLException e){
                throw new PersistentException(e);
            }
        }
        
        public boolean absolute(int row) throws PersistentException{
            if(resultSet == null){
                throw new PersistentException("Closed");
            }
            try{
                return resultSet.absolute(row);
            }catch(SQLException e){
                throw new PersistentException(e);
            }
        }
        
        public boolean relative(int rows) throws PersistentException{
            if(resultSet == null){
                throw new PersistentException("Closed");
            }
            try{
                return resultSet.relative(rows);
            }catch(SQLException e){
                throw new PersistentException(e);
            }
        }
        
        public boolean isFirst() throws PersistentException{
            if(resultSet == null){
                throw new PersistentException("Closed");
            }
            try{
                return resultSet.isFirst();
            }catch(SQLException e){
                throw new PersistentException(e);
            }
        }
        
        public boolean isLast() throws PersistentException{
            if(resultSet == null){
                throw new PersistentException("Closed");
            }
            try{
                return resultSet.isLast();
            }catch(SQLException e){
                throw new PersistentException(e);
            }
        }
        
        public boolean isBeforeFirst() throws PersistentException{
            if(resultSet == null){
                throw new PersistentException("Closed");
            }
            try{
                return resultSet.isBeforeFirst();
            }catch(SQLException e){
                throw new PersistentException(e);
            }
        }
        
        public boolean isAfterLast() throws PersistentException{
            if(resultSet == null){
                throw new PersistentException("Closed");
            }
            try{
                return resultSet.isAfterLast();
            }catch(SQLException e){
                throw new PersistentException(e);
            }
        }
        
        public void setFetchDirection(int direction) throws PersistentException{
            if(resultSet == null){
                throw new PersistentException("Closed");
            }
            try{
                resultSet.setFetchDirection(direction);
            }catch(SQLException e){
                throw new PersistentException(e);
            }
        }
        
        public int getFetchDirection() throws PersistentException{
            if(resultSet == null){
                throw new PersistentException("Closed");
            }
            try{
                return resultSet.getFetchDirection();
            }catch(SQLException e){
                throw new PersistentException(e);
            }
        }
        
        public void setFetchSize(int rows) throws PersistentException{
            if(resultSet == null){
                throw new PersistentException("Closed");
            }
            try{
                resultSet.setFetchSize(rows);
            }catch(SQLException e){
                throw new PersistentException(e);
            }
        }
        
        public int getFetchSize() throws PersistentException{
            if(resultSet == null){
                throw new PersistentException("Closed");
            }
            try{
                return resultSet.getFetchSize();
            }catch(SQLException e){
                throw new PersistentException(e);
            }
        }
        
        public int getRow() throws PersistentException{
            if(resultSet == null){
                throw new PersistentException("Closed");
            }
            try{
                return resultSet.getRow();
            }catch(SQLException e){
                throw new PersistentException(e);
            }
        }
        
        public Object load(Object output) throws PersistentException{
            if(resultSet == null){
                throw new PersistentException("Closed");
            }
            return fillOutput(resultSet, output, outputMapping, true);
        }
        
        public boolean isClosed(){
            return statement == null;
        }
        
        public void close(){
            if(statement != null){
                try{
                    statement.close();
                }catch(SQLException e){
                }
                statement = null;
            }
            if(resultSet != null){
                try{
                    resultSet.close();
                }catch(SQLException e){
                }
                resultSet = null;
            }
            outputMapping = null;
        }
    }
    
    /**
     * バッチ実行。<p>
     *
     * @author M.Takata
     */
    private class BatchExecutorImpl implements BatchExecutor{
        
        private Connection connection;
        private String sql;
        private PreparedStatement statement;
        private List inputProps;
        private int currentBatchCount;
        private int autoBatchPersistCount;
        private boolean isAutoCommitOnPersist;
        
        BatchExecutorImpl(Connection con, String sql, List inputProps, Map statementProps) throws PersistentException{
            connection = con;
            this.sql = sql;
            this.inputProps = inputProps;
            try{
                statement = con.prepareStatement(sql);
                if(inputProps != null){
                    final ParameterMetaData metadata
                        = statement.getParameterMetaData();
                    if(inputProps.size() != metadata.getParameterCount()){
                        throw new PersistentException("Illegal sql : " + sql);
                    }
                }
            }catch(SQLException e){
                throw new PersistentException("Illegal sql : " + sql, e);
            }catch(IncompatibleClassChangeError e){
            }
            setStatementProperties(statement, statementProps);
        }
        
        public void setAutoBatchPersistCount(int count){
            autoBatchPersistCount = count;
        }
        
        public int getAutoBatchPersistCount(){
            return autoBatchPersistCount;
        }
        
        public void setAutoCommitOnPersist(boolean isCommit){
            isAutoCommitOnPersist = isCommit;
        }
        
        public boolean isAutoCommitOnPersist(){
            return isAutoCommitOnPersist;
        }
        
        public int addBatch(Object input) throws PersistentException{
            if(statement == null){
                throw new PersistentException("Closed");
            }
            persistQueryInternal(sql, statement, input, inputProps, true);
            currentBatchCount++;
            if(autoBatchPersistCount > 0 && currentBatchCount >= autoBatchPersistCount){
                return persist();
            }else{
                return 0;
            }
        }
        
        public int persist() throws PersistentException{
            if(statement == null){
                throw new PersistentException("Closed");
            }
            int[] updateCounts = null;
            try{
                updateCounts = statement.executeBatch();
            }catch(SQLException e){
                throw new PersistentException("Batch execute error.", e);
            }
            int result = 0;
            for(int i = 0; i < updateCounts.length; i++){
                if(updateCounts[i] > 0){
                    result += updateCounts[i];
                }
            }
            if(result == 0){
                try{
                    result = statement.getUpdateCount();
                }catch(SQLException e){
                }
            }
            currentBatchCount = 0;
            try{
                if(isAutoCommitOnPersist && !connection.getAutoCommit()){
                    connection.commit();
                }
            }catch(SQLException e){
                throw new PersistentException(e);
            }
            return result;
        }
        
        public void clearBatch() throws PersistentException{
            if(statement != null){
                try{
                    statement.clearBatch();
                }catch(SQLException e){
                    throw new PersistentException("Batch clear error.", e);
                }
            }
            currentBatchCount = 0;
        }
        
        public void close(){
            if(statement != null){
                try{
                    statement.close();
                }catch(SQLException e){
                }
                statement = null;
            }
            if(inputProps != null){
                inputProps = null;
            }
            connection = null;
            currentBatchCount = 0;
        }
    }
}