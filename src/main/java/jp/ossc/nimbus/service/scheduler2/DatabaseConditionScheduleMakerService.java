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
package jp.ossc.nimbus.service.scheduler2;

import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.sql.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.connection.*;


/**
 * データベース条件スケジュール作成サービス。<p>
 * スケジュールの作成有無の判定をSQLに委譲する。<br>
 *
 * @author M.Takata
 */
public class DatabaseConditionScheduleMakerService
 extends DefaultScheduleMakerService
 implements jp.ossc.nimbus.service.scheduler.DateEvaluator, DatabaseConditionScheduleMakerServiceMBean{
    
    private static final long serialVersionUID = -5158949647626532438L;
    protected ServiceName connectionFactoryServiceName;
    protected ConnectionFactory connectionFactory;
    
    protected String query;
    protected String dateFormat;
    
    // DatabaseConditionScheduleMakerServiceMBeanのJavaDoc
    public void setConnectionFactoryServiceName(ServiceName name){
        connectionFactoryServiceName = name;
    }
    // DatabaseConditionScheduleMakerServiceMBeanのJavaDoc
    public ServiceName getConnectionFactoryServiceName(){
        return connectionFactoryServiceName;
    }
    
    // DatabaseConditionScheduleMakerServiceMBeanのJavaDoc
    public void setQuery(String query){
        this.query = query;
    }
    // DatabaseConditionScheduleMakerServiceMBeanのJavaDoc
    public String getQuery(){
        return query;
    }
    
    // DatabaseConditionScheduleMakerServiceMBeanのJavaDoc
    public void setDateFormat(String format){
        dateFormat = format;
    }
    // DatabaseConditionScheduleMakerServiceMBeanのJavaDoc
    public String getDateFormat(){
        return dateFormat;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        
        if(connectionFactoryServiceName != null){
            connectionFactory = (ConnectionFactory)ServiceManagerFactory
                .getServiceObject(connectionFactoryServiceName);
        }
        if(connectionFactory == null){
            throw new IllegalArgumentException("ConnectionFactory is null.");
        }
        if(query == null){
            throw new IllegalArgumentException("Query is null.");
        }
    }
    
    /**
     * {@link ConnectionFactory}を設定する。<p>
     *
     * @param factory ConnectionFactory
     */
    public void setConnectionFactory(ConnectionFactory factory){
        connectionFactory = factory;
    }
    
    /**
     * {@link ConnectionFactory}を取得する。<p>
     *
     * @return ConnectionFactory
     */
    public ConnectionFactory getConnectionFactory(){
        return connectionFactory;
    }
    
    /**
     * この日付で、スケジュールを作成する必要があるかどうかを判定する。<p>
     * {@link #setQuery(String)}で指定されたSQLの埋め込みパラメータに、引数で指定されたdateを設定して実行する。<br>
     * 実行結果は、Boolean型、数値型、文字列型のいずれかを想定しており、<br>
     * Boolean型の場合は、そのまま戻り値とする。<br>
     * 数値型の場合は、0以外の値の場合、戻り値をtrueとする。<br>
     * 文字列型の場合は、"0"以外の値の場合、戻り値をtrueとする。<br>
     *
     * @param date 作成日
     * @param master スケジュールマスタ
     * @return trueの場合、作る必要がある
     * @exception ScheduleMakeException 判定に失敗した場合
     */
    protected boolean isNecessaryMake(Date date, ScheduleMaster master)
     throws ScheduleMakeException{
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
        }catch(ConnectionFactoryException e){
            throw new ScheduleMakeException(e);
        }
        
        boolean result = false;
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = con.prepareStatement(query);
            ParameterMetaData paramData = st.getParameterMetaData();
            if(paramData == null){
                if(dateFormat == null){
                    st.setDate(1, new java.sql.Date(date.getTime()));
                }else{
                    st.setString(1, new SimpleDateFormat(dateFormat).format(date));
                }
            }else{
                for(int i = 0; i < paramData.getParameterCount(); i++){
                    if(dateFormat == null){
                        st.setDate(i + 1, new java.sql.Date(date.getTime()));
                    }else{
                        st.setString(i + 1, new SimpleDateFormat(dateFormat).format(date));
                    }
                }
            }
            rs = st.executeQuery();
            if(!rs.next()){
                throw new ScheduleMakeException("Size of ResultSet is 0.");
            }
            Object ret = rs.getObject(1);
            if(rs.wasNull()){
                throw new ScheduleMakeException("Result value is null.");
            }
            if(ret instanceof Boolean){
                result = ((Boolean)ret).booleanValue();
            }else if(ret instanceof Number){
                result = ((Number)ret).intValue() != 0;
            }else{
                result = !"0".equals(ret.toString());
            }
        }catch(SQLException e){
            throw new ScheduleMakeException(e);
        }finally{
            if(st != null){
                try{
                    st.close();
                }catch(SQLException e){
                }
            }
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
            }
        }
        return result;
    }
    
    public boolean equalsDate(String key, Calendar cal) throws Exception{
        return isNecessaryMake(cal.getTime(), null);
    }
}