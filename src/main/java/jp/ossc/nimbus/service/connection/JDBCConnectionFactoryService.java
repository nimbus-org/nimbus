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

import java.io.Serializable;
import java.sql.*;
import java.util.*;

import jp.ossc.nimbus.core.*;

/**
 * JDBCコネクションファクトリ。<p>
 * java.sql.DriverManagerを使って、コネクションを取得する。<br>
 * 以下に、サービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="ConnectionFactory"
 *                  code="jp.ossc.nimbus.service.connection.JDBCConnectionFactoryService"&gt;
 *             &lt;attribute name="DriverName"&gt;com.mysql.jdbc.Driver&lt;/attribute&gt;
 *             &lt;attribute name="ConnectionURL"&gt;jdbc:mysql://localhost/sample?useUnicode=true&amp;characterEncoding=MS932&lt;/attribute&gt;
 *             &lt;attribute name="UserName"&gt;hoge&lt;/attribute&gt;
 *             &lt;attribute name="Password"&gt;fuga&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class JDBCConnectionFactoryService extends ServiceBase
 implements ConnectionFactory, JDBCConnectionFactoryServiceMBean, Serializable{
    
    private static final long serialVersionUID = -1395958772628393323L;
    
    /**
     * JDBCドライバ名。<p>
     */
    private String driverName;
    
    /**
     * JDBC接続URL。<p>
     */
    private String connectionURL;
    
    /**
     * JDBC接続ユーザ名。<p>
     */
    private String userName;
    
    /**
     * JDBC接続パスワード。<p>
     */
    private String password;
    
    /**
     * JDBC接続プロパティ。<p>
     */
    private Properties info = new Properties();
    
    /**
     * 自動コミットフラグ。<p>
     * 自動コミットの場合、true。
     */
    private boolean isAutoCommit = true;
    
    /**
     * 開始処理を行う。<p>
     * ドライバ名が設定されていない場合は例外をthrowする。<br>
     * JDBC接続URLが設定されていない場合は例外をthrowする。<br>
     * JDBC接続ユーザ名とJDBC接続パスワードが片方だけ設定されている場合は例外をthrowする。<br>
     * JDBC接続ユーザ名とJDBC接続プロパティが同時に設定されている場合は例外をthrowする。<br>
     *
     * @exception Exception 開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(getConnectionURL() == null){
            throw new IllegalArgumentException("Connection URL is null.");
        }
        if((getUserName() != null && getPassword() == null)
            || (getUserName() == null && getPassword() != null)){
            throw new IllegalArgumentException(
                "Only one of the two cannot specify \"UserName\""
                 + " and \"Password.\""
            );
        }
        if(getUserName() != null && getConnectionProperties().size() != 0){
            throw new IllegalArgumentException(
                "It cannot specify simultaneously \"UserName\""
                 + " and \"ConnectionProperties.\""
            );
        }
        if(getDriverName() != null){
            Class.forName(getDriverName(), true, NimbusClassLoader.getInstance());
        }
    }
    
    // ConnectionFactoryのJavaDoc
    public Connection getConnection() throws ConnectionFactoryException{
        Connection con = null;
        try{
            if(getUserName() != null){
                con = DriverManager.getConnection(
                    getConnectionURL(),
                    getUserName(),
                    getPassword()
                );
            }else if(getConnectionProperties().size() != 0){
                con = DriverManager.getConnection(
                    getConnectionURL(),
                    getConnectionProperties()
                );
            }else{
                con = DriverManager.getConnection(getConnectionURL());
            }
            con.setAutoCommit(isAutoCommit);
        }catch(Exception e){
            throw new ConnectionFactoryException(e);
        }
        return con;
    }
    
    // JDBCConnectionFactoryServiceMBeanのJavaDoc
    public void setDriverName(String name){
        driverName = name;
    }
    
    // JDBCConnectionFactoryServiceMBeanのJavaDoc
    public String getDriverName(){
        return driverName;
    }
    
    // JDBCConnectionFactoryServiceMBeanのJavaDoc
    public void setConnectionURL(String url){
        connectionURL = url;
    }
    
    // JDBCConnectionFactoryServiceMBeanのJavaDoc
    public String getConnectionURL(){
        return connectionURL;
    }
    
    // JDBCConnectionFactoryServiceMBeanのJavaDoc
    public void setUserName(String name){
        userName = name;
    }
    
    // JDBCConnectionFactoryServiceMBeanのJavaDoc
    public String getUserName(){
        return userName;
    }
    
    // JDBCConnectionFactoryServiceMBeanのJavaDoc
    public void setPassword(String password){
        this.password = password;
    }
    
    // JDBCConnectionFactoryServiceMBeanのJavaDoc
    public String getPassword(){
        return password;
    }
    
    // JDBCConnectionFactoryServiceMBeanのJavaDoc
    public void setConnectionProperties(Properties prop){
        info.putAll(prop);
    }
    
    // JDBCConnectionFactoryServiceMBeanのJavaDoc
    public Properties getConnectionProperties(){
        return info;
    }
    
    // JDBCConnectionFactoryServiceMBeanのJavaDoc
    public void setAutoCommit(boolean isAuto){
        isAutoCommit = isAuto;
    }
    
    // JDBCConnectionFactoryServiceMBeanのJavaDoc
    public boolean isAutoCommit(){
        return isAutoCommit;
    }
}
