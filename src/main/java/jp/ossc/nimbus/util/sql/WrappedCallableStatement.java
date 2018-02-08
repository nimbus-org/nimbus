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
package jp.ossc.nimbus.util.sql;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;

public class WrappedCallableStatement extends WrappedPreparedStatement
 implements CallableStatement{
    
    private static final long serialVersionUID = 8677700753645579462L;
    
    private static final int PARAM_MODE_IN = 1;
    private static final int PARAM_MODE_INOUT = 2;
    private static final int PARAM_MODE_OUT = 4;
    
    private static final String DISPLAY_PARAM_OUT = "@OUT@";
    
    /**
     * 指定したCallableStatementをラップするインスタンスを生成する。<p>
     *
     * @param st ラップするCallableStatement
     * @param sql SQL文字列
     */
    public WrappedCallableStatement(CallableStatement st, String sql){
        super(st, sql);
    }
    
    /**
     * 指定したCallableStatementをラップするインスタンスを生成する。<p>
     *
     * @param con このCallableStatementを生成したConnection
     * @param st ラップするCallableStatement
     * @param sql SQL文字列
     */
    public WrappedCallableStatement(
        Connection con,
        CallableStatement st,
        String sql
    ){
        super(con, st, sql);
    }
    
    protected void addArg(int index, Object arg){
        addInArg(index, arg);
    }
    
    protected void addInArg(int index, Object arg){
        addArg(index, PARAM_MODE_IN, 0, arg);
    }
    
    protected void addOutArg(int index, int sqlType){
        addArg(index, PARAM_MODE_OUT, sqlType, null);
    }
    
    protected void addArg(int index, int mode, int sqlType, Object arg){
        if(argList == null){
            argList = new ArrayList();
        }
        int tmpIndex = index - 1;
        if(argList.size()  > tmpIndex){
            ParameterData data = (ParameterData)argList.get(tmpIndex);
            if(data == null){
                argList.set(tmpIndex, new ParameterData(mode, sqlType, arg));
            }else{
                //既に設定されている場合
                data.setParameterMode(mode);
                if(data.getParameterMode() == PARAM_MODE_INOUT
                     && mode == PARAM_MODE_OUT){
                    //先にINパラメータ設定が設定され、その後OUTパラメータをセットするケース
                }else{
                    //その他のケースは値を上書き
                    data.setParameterValue(arg);  
                }
            }
            
        }else{
            for(int i = argList.size(); i <= tmpIndex; i++){
                if(i == tmpIndex){
                    argList.add(new ParameterData(mode, sqlType, arg));
                }else{
                    argList.add(null);
                }
            }
        }
    }
    
    protected String createSQL(){
        if(preparedSql == null){
            return null;
        }else if(preparedSql.indexOf('?') == -1){
            return preparedSql;
        }else if(argList == null || argList.size() == 0){
            return preparedSql;
        }
        
        final StringBuffer tmpSql = new StringBuffer(preparedSql);
        final StringBuffer tmpVal = new StringBuffer();
        for(int i = 0, max = argList.size(); i < max; i++){
            final int index = tmpSql.indexOf(PREPARED_KEY);
            if(index == -1){
                break;
            }
            
            try{
                final ParameterData param = (ParameterData)argList.get(i);
                if(param != null){
                    Object val = param.getParameterValue();
                    tmpVal.setLength(0);
                        
                    if(val == null
                         && param.getParameterMode() == PARAM_MODE_OUT){
                        tmpSql.replace(
                            index,
                            index + 1,
                            DISPLAY_PARAM_OUT
                        );
                    }else{
                        tmpSql.replace(
                            index,
                            index + 1,
                            escapeSQLValue(val, tmpVal)
                        );
                    }
                }else{
                    //該当項目なし
                }
            }catch(IndexOutOfBoundsException e){
                    //該当項目なし
            }
        }
        return tmpSql.toString();
    }
    
    public void registerOutParameter(int arg0, int arg1) throws SQLException{
        ((CallableStatement)statement).registerOutParameter(arg0, arg1);
        addOutArg(arg0, arg1);
    }
    
    public void registerOutParameter(int arg0, int arg1, int arg2)
     throws SQLException{
        ((CallableStatement)statement).registerOutParameter(arg0, arg1, arg2);
        addOutArg(arg0, arg1);
    }
    
    public void registerOutParameter(int arg0, int arg1, String arg2)
     throws SQLException{
        ((CallableStatement)statement).registerOutParameter(arg0, arg1, arg2);
        addOutArg(arg0, arg1);
    }
    
    public void registerOutParameter(String arg0, int arg1)
     throws SQLException{
        ((CallableStatement)statement).registerOutParameter(arg0, arg1);
    }
    
    public void registerOutParameter(String arg0, int arg1, int arg2)
     throws SQLException{
        ((CallableStatement)statement).registerOutParameter(arg0, arg1, arg2);
    }
    
    public void registerOutParameter(String arg0, int arg1, String arg2) 
     throws SQLException{
        ((CallableStatement)statement).registerOutParameter(arg0, arg1, arg2);
    }
    
    public void setNull(String arg0, int arg1, String arg2)
     throws SQLException{
        ((CallableStatement)statement).setNull(arg0, arg1, arg2);
    }
    
    public void setString(String arg0, String arg1) throws SQLException{
        ((CallableStatement)statement).setString(arg0, arg1);
    }
    
    public void setBigDecimal(String arg0, BigDecimal arg1)
     throws SQLException{
        ((CallableStatement)statement).setBigDecimal(arg0, arg1);
    }
    
    public void setURL(String arg0, URL arg1) throws SQLException{
        ((CallableStatement)statement).setURL(arg0, arg1);
    }
    
    public void setDate(String arg0, java.sql.Date arg1) throws SQLException{
        ((CallableStatement)statement).setDate(arg0, arg1);
    }
    
    public void setTime(String arg0, Time arg1) throws SQLException{
        ((CallableStatement)statement).setTime(arg0, arg1);
    }
    
    public void setTimestamp(String arg0, Timestamp arg1)
     throws SQLException{
        ((CallableStatement)statement).setTimestamp(arg0, arg1);
    }
    
    public void setDate(String arg0, java.sql.Date arg1, Calendar arg2)
     throws SQLException{
        ((CallableStatement)statement).setDate(arg0, arg1, arg2);
    }
    
    public void setTime(String arg0, Time arg1, Calendar arg2)
     throws SQLException{
        ((CallableStatement)statement).setTime(arg0, arg1, arg2);
    }
    
    public void setTimestamp(String arg0, Timestamp arg1, Calendar arg2)
     throws SQLException{
        ((CallableStatement)statement).setTimestamp(arg0, arg1, arg2);
    }
    
    public void setByte(String arg0, byte arg1) throws SQLException{
        ((CallableStatement)statement).setByte(arg0, arg1);
    }
    
    public void setDouble(String arg0, double arg1) throws SQLException{
        ((CallableStatement)statement).setDouble(arg0, arg1);
    }
    
    public void setFloat(String arg0, float arg1) throws SQLException{
        ((CallableStatement)statement).setFloat(arg0, arg1);
    }
    
    public void setInt(String arg0, int arg1) throws SQLException{
        ((CallableStatement)statement).setInt(arg0, arg1);
    }
    
    public void setNull(String arg0, int arg1) throws SQLException{
        ((CallableStatement)statement).setNull(arg0, arg1);
    }
    
    public void setLong(String arg0, long arg1) throws SQLException{
        ((CallableStatement)statement).setLong(arg0, arg1);
    }
    
    public void setShort(String arg0, short arg1) throws SQLException{
        ((CallableStatement)statement).setShort(arg0, arg1);
    }
    
    public void setBoolean(String arg0, boolean arg1) throws SQLException{
        ((CallableStatement)statement).setBoolean(arg0, arg1);
    }
    
    public void setBytes(String arg0, byte[] arg1) throws SQLException{
        ((CallableStatement)statement).setBytes(arg0, arg1);
    }
    
    public void setAsciiStream(String arg0, InputStream arg1, int arg2)
     throws SQLException{
        ((CallableStatement)statement).setAsciiStream(arg0, arg1, arg2);
    }
    
    public void setBinaryStream(String arg0, InputStream arg1, int arg2)
     throws SQLException{
        ((CallableStatement)statement).setBinaryStream(arg0, arg1, arg2);
    }
    
    public void setCharacterStream(String arg0, Reader arg1, int arg2)
     throws SQLException{
        ((CallableStatement)statement).setCharacterStream(arg0, arg1, arg2);
    }
    
    public void setObject(String arg0, Object arg1) throws SQLException{
        ((CallableStatement)statement).setObject(arg0, arg1);
    }
    
    public void setObject(String arg0, Object arg1, int arg2)
     throws SQLException{
        ((CallableStatement)statement).setObject(arg0, arg1, arg2);
    }
    
    public void setObject(String arg0, Object arg1, int arg2, int arg3) 
     throws SQLException{
        ((CallableStatement)statement).setObject(arg0, arg1, arg2, arg3);
    }
    
    public boolean wasNull() throws SQLException{
        return ((CallableStatement)statement).wasNull();
    }
    
    public byte getByte(int arg0) throws SQLException{
        return ((CallableStatement)statement).getByte(arg0);
    }
    
    public double getDouble(int arg0) throws SQLException{
        return ((CallableStatement)statement).getDouble(arg0);
    }
    
    public float getFloat(int arg0) throws SQLException{
        return ((CallableStatement)statement).getFloat(arg0);
    }
    
    public int getInt(int arg0) throws SQLException{
        return ((CallableStatement)statement).getInt(arg0);
    }
    
    public long getLong(int arg0) throws SQLException{
        return ((CallableStatement)statement).getLong(arg0);
    }
    
    public short getShort(int arg0) throws SQLException{
        return ((CallableStatement)statement).getShort(arg0);
    }
    
    public boolean getBoolean(int arg0) throws SQLException{
        return ((CallableStatement)statement).getBoolean(arg0);
    }
    
    public byte[] getBytes(int arg0) throws SQLException{
        return ((CallableStatement)statement).getBytes(arg0);
    }
    
    public Object getObject(int arg0) throws SQLException{
        return ((CallableStatement)statement).getObject(arg0);
    }
    
    public String getString(int arg0) throws SQLException{
        return ((CallableStatement)statement).getString(arg0);
    }
    
    public byte getByte(String arg0) throws SQLException{
        return ((CallableStatement)statement).getByte(arg0);
    }
    
    public double getDouble(String arg0) throws SQLException{
        return ((CallableStatement)statement).getDouble(arg0);
    }
    
    public float getFloat(String arg0) throws SQLException{
        return ((CallableStatement)statement).getFloat(arg0);
    }
    
    public int getInt(String arg0) throws SQLException{
        return ((CallableStatement)statement).getInt(arg0);
    }
    
    public long getLong(String arg0) throws SQLException{
        return ((CallableStatement)statement).getLong(arg0);
    }
    
    public short getShort(String arg0) throws SQLException{
        return ((CallableStatement)statement).getShort(arg0);
    }
    
    public boolean getBoolean(String arg0) throws SQLException{
        return ((CallableStatement)statement).getBoolean(arg0);
    }
    
    public byte[] getBytes(String arg0) throws SQLException{
        return ((CallableStatement)statement).getBytes(arg0);
    }
    
    public BigDecimal getBigDecimal(int arg0) throws SQLException{
        return ((CallableStatement)statement).getBigDecimal(arg0);
    }
    
    public BigDecimal getBigDecimal(int arg0, int arg1)
     throws SQLException{
        return ((CallableStatement)statement).getBigDecimal(arg0, arg1);
    }
    
    public URL getURL(int arg0) throws SQLException{
        return ((CallableStatement)statement).getURL(arg0);
    }
    
    public Array getArray(int arg0) throws SQLException{
        return ((CallableStatement)statement).getArray(arg0);
    }
    
    public Blob getBlob(int arg0) throws SQLException{
        return ((CallableStatement)statement).getBlob(arg0);
    }
    
    public Clob getClob(int arg0) throws SQLException{
        return ((CallableStatement)statement).getClob(arg0);
    }
    
    public java.sql.Date getDate(int arg0) throws SQLException{
        return ((CallableStatement)statement).getDate(arg0);
    }
    
    public Ref getRef(int arg0) throws SQLException{
        return ((CallableStatement)statement).getRef(arg0);
    }
    
    public Time getTime(int arg0) throws SQLException{
        return ((CallableStatement)statement).getTime(arg0);
    }
    
    public Timestamp getTimestamp(int arg0) throws SQLException{
        return ((CallableStatement)statement).getTimestamp(arg0);
    }
    
    public Object getObject(String arg0) throws SQLException{
        return ((CallableStatement)statement).getObject(arg0);
    }
    
    public Object getObject(int arg0, Map arg1) throws SQLException{
        return ((CallableStatement)statement).getObject(arg0, arg1);
    }
    
    public String getString(String arg0) throws SQLException{
        return ((CallableStatement)statement).getString(arg0);
    }
    
    public BigDecimal getBigDecimal(String arg0) throws SQLException{
        return ((CallableStatement)statement).getBigDecimal(arg0);
    }
    
    public URL getURL(String arg0) throws SQLException{
        return ((CallableStatement)statement).getURL(arg0);
    }
    
    public Array getArray(String arg0) throws SQLException{
        return ((CallableStatement)statement).getArray(arg0);
    }
    
    public Blob getBlob(String arg0) throws SQLException{
        return ((CallableStatement)statement).getBlob(arg0);
    }
    
    public Clob getClob(String arg0) throws SQLException{
        return ((CallableStatement)statement).getClob(arg0);
    }
    
    public java.sql.Date getDate(String arg0) throws SQLException{
        return ((CallableStatement)statement).getDate(arg0);
    }
    
    public java.sql.Date getDate(int arg0, Calendar arg1) throws SQLException{
        return ((CallableStatement)statement).getDate(arg0, arg1);
    }
    
    public Ref getRef(String arg0) throws SQLException{
        return ((CallableStatement)statement).getRef(arg0);
    }
    
    public Time getTime(String arg0) throws SQLException{
        return ((CallableStatement)statement).getTime(arg0);
    }
    
    public Time getTime(int arg0, Calendar arg1) throws SQLException{
        return ((CallableStatement)statement).getTime(arg0, arg1);
    }
    
    public Timestamp getTimestamp(String arg0) throws SQLException{
        return ((CallableStatement)statement).getTimestamp(arg0);
    }
    
    public Timestamp getTimestamp(int arg0, Calendar arg1)
     throws SQLException{
        return ((CallableStatement)statement).getTimestamp(arg0, arg1);
    }
    
    public Object getObject(String arg0, Map arg1) throws SQLException{
        return ((CallableStatement)statement).getObject(arg0, arg1);
    }
    
    public java.sql.Date getDate(String arg0, Calendar arg1)
     throws SQLException{
        return ((CallableStatement)statement).getDate(arg0, arg1);
    }
    
    public Time getTime(String arg0, Calendar arg1) throws SQLException{
        return ((CallableStatement)statement).getTime(arg0, arg1);
    }
    
    public Timestamp getTimestamp(String arg0, Calendar arg1)
     throws SQLException{
        return ((CallableStatement)statement).getTimestamp(arg0, arg1);
    }
    
    

    public void setAsciiStream(String parameterName, InputStream inputStream) throws SQLException{
        ((CallableStatement)statement).setAsciiStream(parameterName, inputStream);
    }
    
    public void setAsciiStream(String parameterName, InputStream inputStream, long length) throws SQLException{
        ((CallableStatement)statement).setAsciiStream(parameterName, inputStream, length);
    }
    
    public void setBinaryStream(String parameterName, InputStream inputStream) throws SQLException{
        ((CallableStatement)statement).setBinaryStream(parameterName, inputStream);
    }
    
    public void setBinaryStream(String parameterName, InputStream inputStream, long length) throws SQLException{
        ((CallableStatement)statement).setBinaryStream(parameterName, inputStream, length);
    }
    
    public void setBlob(String parameterName, Blob value) throws SQLException{
        ((CallableStatement)statement).setBlob(parameterName, value);
    }
    
    public void setBlob(String parameterName, InputStream inputStream) throws SQLException{
        ((CallableStatement)statement).setBlob(parameterName, inputStream);
    }
    
    public Reader getCharacterStream(int parameterIndex) throws SQLException{
        return ((CallableStatement)statement).getCharacterStream(parameterIndex);
    }
    
    public Reader getCharacterStream(String parameterName) throws SQLException{
        return ((CallableStatement)statement).getCharacterStream(parameterName);
    }
    
    public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException{
        ((CallableStatement)statement).setBlob(parameterName, inputStream, length);
    }
    
    public void setClob(String parameterName, Clob value) throws SQLException{
        ((CallableStatement)statement).setClob(parameterName, value);
    }
    
    public void setClob(String parameterName, Reader reader) throws SQLException{
        ((CallableStatement)statement).setClob(parameterName, reader);
    }
    
    public void setClob(String parameterName, Reader reader, long length) throws SQLException{
        ((CallableStatement)statement).setClob(parameterName, reader, length);
    }
    
    public NClob getNClob(int parameterIndex) throws SQLException{
        return ((CallableStatement)statement).getNClob(parameterIndex);
    }
    
    public NClob getNClob(String parameterName) throws SQLException{
        return ((CallableStatement)statement).getNClob(parameterName);
    }
    
    public void setNClob(String parameterName, NClob value) throws SQLException{
        ((CallableStatement)statement).setNClob(parameterName, value);
    }
    
    public void setNClob(String parameterName, Reader reader) throws SQLException{
        ((CallableStatement)statement).setNClob(parameterName, reader);
    }
    
    public void setNClob(String parameterName, Reader reader, long length) throws SQLException{
        ((CallableStatement)statement).setNClob(parameterName, reader, length);
    }
    
    public void setNCharacterStream(String parameterName, Reader reader) throws SQLException{
        ((CallableStatement)statement).setNCharacterStream(parameterName, reader);
    }
    
    public void setNCharacterStream(String parameterName, Reader reader, long length) throws SQLException{
        ((CallableStatement)statement).setNCharacterStream(parameterName, reader, length);
    }
    
    public Reader getNCharacterStream(int parameterIndex) throws SQLException{
        return ((CallableStatement)statement).getNCharacterStream(parameterIndex);
    }
    
    public Reader getNCharacterStream(String parameterName) throws SQLException{
        return ((CallableStatement)statement).getNCharacterStream(parameterName);
    }
    
    public void setCharacterStream(String parameterName, Reader reader) throws SQLException{
        ((CallableStatement)statement).setCharacterStream(parameterName, reader);
    }
    
    public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException{
        ((CallableStatement)statement).setCharacterStream(parameterName, reader, length);
    }
    
    public void setNString(String parameterName, String value) throws SQLException{
        ((CallableStatement)statement).setNString(parameterName, value);
    }
    
    public String getNString(int parameterIndex) throws SQLException{
        return ((CallableStatement)statement).getNString(parameterIndex);
    }
    
    public String getNString(String parameterName) throws SQLException{
        return ((CallableStatement)statement).getNString(parameterName);
    }
    
    public SQLXML getSQLXML(int parameterIndex) throws SQLException{
        return ((CallableStatement)statement).getSQLXML(parameterIndex);
    }
    
    public SQLXML getSQLXML(String parameterName) throws SQLException{
        return ((CallableStatement)statement).getSQLXML(parameterName);
    }
    
    public void setSQLXML(String parameterName, SQLXML value) throws SQLException{
        ((CallableStatement)statement).setSQLXML(parameterName, value);
    }
    
    public void setRowId(String parameterName, RowId x) throws SQLException{
        ((CallableStatement)statement).setRowId(parameterName, x);
    }
    
    public RowId getRowId(int parameterIndex) throws SQLException{
        return ((CallableStatement)statement).getRowId(parameterIndex);
    }
    
    public RowId getRowId(String parameterName) throws SQLException{
        return ((CallableStatement)statement).getRowId(parameterName);
    }


    public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException{
        return ((CallableStatement)statement).getObject(parameterIndex, type);
    }
    
    public <T> T getObject(String parameterName, Class<T> type) throws SQLException{
        return ((CallableStatement)statement).getObject(parameterName, type);
    }

    
    protected static class ParameterData implements Serializable{
        
        private static final long serialVersionUID = -7325867762248170149L;
        
        private int mode;
        private int sqlType;
        private String javaType; 
        private Object value;
        
        public ParameterData(int mode, int sqlType, Object value){
            setParameterMode(mode);
            setParameterType(sqlType);
            setParameterValue(value);
        }
        
        public int getParameterMode(){
            return mode;
        }
        
        public int getParameterType(){
            return sqlType;
        }
        
        public String getParameterJavaType(){
            return javaType;
        }
        
        public Object getParameterValue(){
            return value;
        }
        
        public void setParameterMode(int mode){
            if(this.mode != 0 && this.mode != mode){
                ////this.modeがIN,OUTに設定されていて異なるモードをセットする場合
                this.mode = PARAM_MODE_INOUT;
            }else if(this.mode == 0){
                this.mode = mode;
            }else{
                //this.mode == PARAM_MODE_INOUTのケース
            }
         }
        
        public void setParameterType(int sqlType){
            this.sqlType = sqlType;
        }
        
        public void setParameterValue(Object value){
            this.value = value;
            if(value != null){
                this.javaType = value.getClass().getName();
            }else{
                this.javaType = null;
            }
        }
    }
}
