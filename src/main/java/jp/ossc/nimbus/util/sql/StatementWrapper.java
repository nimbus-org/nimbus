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

import java.io.Serializable;
import java.sql.*;
import java.util.*;
import java.lang.reflect.*;

import jp.ossc.nimbus.beans.*;

/**
 * Statementラッパー。<p>
 *
 * @author M.Takata
 */
public class StatementWrapper implements Statement, Serializable {
    
    private static final long serialVersionUID = -3678006522511003450L;
    
    protected Connection connection;
    
    protected Statement statement;
    
    protected Class resultSetWrapperClass;
    
    protected Map resultSetProperties;
    
    /**
     * 指定したStatementをラップするインスタンスを生成する。<p>
     *
     * @param st ラップするStatement
     */
    public StatementWrapper(Statement st){
        this(null, st);
    }
    
    /**
     * 指定したStatementをラップするインスタンスを生成する。<p>
     *
     * @param con このStatementを生成したConnection
     * @param st ラップするStatement
     */
    public StatementWrapper(Connection con, Statement st){
        connection = con;
        statement = st;
    }
    
    /**
     * ラップするStatementを設定する。<p>
     *
     * @param st ラップするStatement
     */
    public void setStatement(Statement st){
        statement = st;
    }
    
    /**
     * ラップしているStatementを取得する。<p>
     *
     * @return ラップしているStatement
     */
    public Statement getStatement(){
        return statement;
    }
    
    /**
     * ラップする{@link ResultSetWrapper}の実装クラスを設定する。<p>
     *
     * @param clazz ラップするResultSetWrapperの実装クラス
     * @exception IllegalArgumentException 指定したクラスがResultSetWrapperのサブクラスでない場合
     */
    public void setResultSetWrapperClass(Class clazz)
     throws IllegalArgumentException{
        
        if(clazz != null
             && !ResultSetWrapper.class.isAssignableFrom(clazz)){
            throw new IllegalArgumentException(
                "Illegal class : " + clazz.getName()
            );
        }
        resultSetWrapperClass = clazz;
    }
    
    /**
     * ラップする{@link ResultSetWrapper}の実装クラスを取得する。<p>
     *
     * @return ラップするResultSetWrapperの実装クラス
     */
    public Class getResultSetWrapperClass(){
        return resultSetWrapperClass;
    }
    
    /**
     * {@link ResultSetWrapper}にプロパティを設定する。<p>
     *
     * @param name プロパティ名
     * @param value 値
     */
    public void setResultSetProperty(String name, Object value){
        if(resultSetProperties == null){
            resultSetProperties = new LinkedHashMap();
        }
        final Property prop = PropertyFactory.createProperty(name);
        resultSetProperties.put(prop, value);
    }
    
    /**
     * {@link ResultSetWrapper}のプロパティを取得する。<p>
     *
     * @param name プロパティ名
     * @return 値
     */
    public Object getResultSetProperty(String name){
        if(resultSetProperties == null){
            return null;
        }
        final Iterator props = resultSetProperties.keySet().iterator();
        while(props.hasNext()){
            final Property prop = (Property)props.next();
            if(prop.getPropertyName().equals(name)){
                return resultSetProperties.get(prop);
            }
        }
        return null;
    }
    
    protected ResultSet createResultSetWrapper(ResultSet rs)
     throws SQLException{
        if(resultSetWrapperClass == null){
            return rs;
        }
        ResultSetWrapper result = null;
        try{
            final Constructor constructor
                 = resultSetWrapperClass.getConstructor(
                    new Class[]{ResultSet.class}
                );
            result = (ResultSetWrapper)constructor.newInstance(
                new Object[]{rs}
            );
            applyResultSetProperties(result);
        }catch(InvocationTargetException e){
            throw new SQLException(e.getTargetException().getMessage());
        }catch(Exception e){
            throw new SQLException(e.getMessage());
        }
        return result;
    }
    
    protected void applyResultSetProperties(ResultSetWrapper rsw)
     throws Exception{
        if(resultSetProperties == null || resultSetProperties.size() == 0){
            return;
        }
        final Iterator props = resultSetProperties.keySet().iterator();
        while(props.hasNext()){
            final Property prop = (Property)props.next();
            prop.setProperty(rsw, resultSetProperties.get(prop));
        }
    }
    
    public int getFetchDirection() throws SQLException {
        return statement.getFetchDirection();
    }
    
    public int getFetchSize() throws SQLException {
        return statement.getFetchSize();
    }
    
    public int getMaxFieldSize() throws SQLException {
        return statement.getMaxFieldSize();
    }
    
    public int getMaxRows() throws SQLException {
        return statement.getMaxRows();
    }
    
    public int getQueryTimeout() throws SQLException {
        return statement.getQueryTimeout();
    }
    
    public int getResultSetConcurrency() throws SQLException {
        return statement.getResultSetConcurrency();
    }
    
    public int getResultSetHoldability() throws SQLException {
        return statement.getResultSetHoldability();
    }
    
    public int getResultSetType() throws SQLException {
        return statement.getResultSetType();
    }
    
    public int getUpdateCount() throws SQLException {
        return statement.getUpdateCount();
    }
    
    public void cancel() throws SQLException {
        statement.cancel();
    }
    
    public void clearBatch() throws SQLException {
        statement.clearBatch();
    }
    
    public void clearWarnings() throws SQLException {
        statement.clearWarnings();
    }
    
    public void close() throws SQLException {
        statement.close();
    }
    
    public boolean getMoreResults() throws SQLException {
        return statement.getMoreResults();
    }
    
    public int[] executeBatch() throws SQLException {
        return statement.executeBatch();
    }
    
    public void setFetchDirection(int arg0) throws SQLException {
        statement.setFetchDirection(arg0);
    }
    
    public void setFetchSize(int arg0) throws SQLException {
        statement.setFetchSize(arg0);
    }
    
    public void setMaxFieldSize(int arg0) throws SQLException {
        statement.setMaxFieldSize(arg0);
    }
    
    public void setMaxRows(int arg0) throws SQLException {
        statement.setMaxRows(arg0);
    }
    
    public void setQueryTimeout(int arg0) throws SQLException {
        statement.setQueryTimeout(arg0);
    }
    
    public boolean getMoreResults(int arg0) throws SQLException {
        return statement.getMoreResults(arg0);
    }
    
    public void setEscapeProcessing(boolean arg0) throws SQLException {
        statement.setEscapeProcessing(arg0);
    }
    
    public int executeUpdate(String arg0) throws SQLException {
        return statement.executeUpdate(arg0);
    }
    
    public void addBatch(String arg0) throws SQLException {
        statement.addBatch(arg0);
    }
    
    public void setCursorName(String arg0) throws SQLException {
        statement.setCursorName(arg0);
    }
    
    public boolean execute(String arg0) throws SQLException {
        return statement.execute(arg0);
    }
    
    public int executeUpdate(String arg0, int arg1) throws SQLException {
        return statement.executeUpdate(arg0, arg1);
    }
    
    public boolean execute(String arg0, int arg1) throws SQLException {
        return statement.execute(arg0, arg1);
    }
    
    public int executeUpdate(String arg0, int[] arg1) throws SQLException {
        return statement.executeUpdate(arg0, arg1);
    }
    
    public boolean execute(String arg0, int[] arg1) throws SQLException {
        return statement.execute(arg0, arg1);
    }
    
    public Connection getConnection() throws SQLException {
        if(connection == null){
            return statement.getConnection();
        }else{
            return connection;
        }
    }
    
    public ResultSet getGeneratedKeys() throws SQLException {
        return createResultSetWrapper(statement.getGeneratedKeys());
    }
    
    public ResultSet getResultSet() throws SQLException {
        return createResultSetWrapper(statement.getResultSet());
    }
    
    public SQLWarning getWarnings() throws SQLException {
        return statement.getWarnings();
    }
    
    public int executeUpdate(String arg0, String[] arg1) throws SQLException {
        return statement.executeUpdate(arg0, arg1);
    }
    
    public boolean execute(String arg0, String[] arg1) throws SQLException {
        return statement.execute(arg0, arg1);
    }
    
    public ResultSet executeQuery(String arg0) throws SQLException {
        return createResultSetWrapper(statement.executeQuery(arg0));
    }
    

    public boolean isPoolable() throws SQLException{
        return statement.isPoolable();
    }
    
    public void setPoolable(boolean isPoolable) throws SQLException{
        statement.setPoolable(isPoolable);
    }
    
    public boolean isClosed() throws SQLException{
        return statement.isClosed();
    }
    
    public boolean isWrapperFor(Class<?> iface) throws SQLException{
        return statement.isWrapperFor(iface);
    }
    
    public <T> T unwrap(Class<T> iface) throws SQLException{
        return statement.unwrap(iface);
    }


    
    public void closeOnCompletion() throws SQLException{
        statement.closeOnCompletion();
    }
    
    public boolean isCloseOnCompletion() throws SQLException{
        return statement.isCloseOnCompletion();
    }

}
