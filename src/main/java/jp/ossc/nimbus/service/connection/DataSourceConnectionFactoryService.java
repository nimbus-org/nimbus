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
import javax.sql.DataSource;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.jndi.*;
import jp.ossc.nimbus.service.context.Context;

/**
 * データソースコネクションファクトリ。<p>
 * JNDIから、指定されたデータソース名のデータソースを取得して、コネクションを取得する。<br>
 * 以下に、サービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="ConnectionFactory"
 *                  code="jp.ossc.nimbus.service.connection.DataSourceConnectionFactoryService"&gt;
 *             &lt;attribute name="Name"&gt;java:DefaultDS&lt;/attribute&gt;
 *             &lt;attribute name="JndiFinderServiceName"&gt;#DataSourceFinder&lt;/attribute&gt;
 *             &lt;depends&gt;DataSourceFinder&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="DataSourceFinder"
 *                  code="jp.ossc.nimbus.service.jndi.CachedJndiFinderService"&gt;
 *             &lt;attribute name="Environment"&gt;
 *                 java.naming.factory.initial=org.jnp.interfaces.NamingContextFactory
 *                 java.naming.factory.url.pkgs=org.jboss.naming:org.jnp.interfaces
 *                 java.naming.provider.url=localhost
 *             &lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 * @see jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder
 */
public class DataSourceConnectionFactoryService extends ServiceBase
 implements ConnectionFactory, DataSourceConnectionFactoryServiceMBean, Serializable{
    
    private static final long serialVersionUID = -5837939620922806932L;
    
    /**
     * データソース名。<p>
     */
    private String dataSourceName;
    
    /**
     * {@link JndiFinder}オブジェクト。<p>
     */
    private JndiFinder jndiFinder;
    
    /**
     * {@link JndiFinder}サービス名。<p>
     */
    private ServiceName jndiFinderName;
    
    /**
     * {@link Context}オブジェクト。<p>
     */
    private Context context;
    
    /**
     * {@link Context}サービス名。<p>
     */
    private ServiceName contextName;
    
    /**
     * {@link Context}上のデータソース名のキー。<p>
     */
    private String dataSourceNameKey = DEFAULT_DATASOURCE_NAME_KEY;
    
    /**
     * データソースサービス名。<p>
     */
    private ServiceName dataSourceServiceName;
    
    /**
     * データソース。<p>
     */
    private DataSource dataSource;
    
    // DataSourceConnectionFactoryServiceMBeanのJavaDoc
    public void setName(String name){
        dataSourceName = name;
    }
    
    // DataSourceConnectionFactoryServiceMBeanのJavaDoc
    public String getName(){
        return dataSourceName;
    }
    
    // DataSourceConnectionFactoryServiceMBeanのJavaDoc
    public void setJndiFinderServiceName(ServiceName name){
        jndiFinderName = name;
    }
    
    // DataSourceConnectionFactoryServiceMBeanのJavaDoc
    public ServiceName getJndiFinderServiceName(){
        return jndiFinderName;
    }
    
    // DataSourceConnectionFactoryServiceMBeanのJavaDoc
    public void setContextServiceName(ServiceName name){
        contextName = name;
    }
    
    // DataSourceConnectionFactoryServiceMBeanのJavaDoc
    public ServiceName getContextServiceName(){
        return contextName;
    }
    
    // DataSourceConnectionFactoryServiceMBeanのJavaDoc
    public void setDataSourceNameKey(String key){
        dataSourceNameKey = key;
    }
    
    // DataSourceConnectionFactoryServiceMBeanのJavaDoc
    public String getDataSourceNameKey(){
        return dataSourceNameKey;
    }
    
    // DataSourceConnectionFactoryServiceMBeanのJavaDoc
    public void setDataSourceServiceName(ServiceName name){
        dataSourceServiceName = name;
    }
    // DataSourceConnectionFactoryServiceMBeanのJavaDoc
    public ServiceName getDataSourceServiceName(){
        return dataSourceServiceName;
    }
    
    public JndiFinder getJndiFinderService(){
        return jndiFinder;
    }
    public void setJndiFinderService(JndiFinder finder){
        jndiFinder = finder;
    }
    
    public Context getContextService(){
        return context;
    }
    public void setContextService(Context context){
        this.context = context;
    }
    
    public void setDataSourceService(DataSource ds){
        dataSource = ds;
    }
    public DataSource getDataSourceService(){
        return dataSource;
    }
    
    /**
     * 開始処理を行う。<p>
     * {@link JndiFinder}サービス名が設定されていない場合は例外をthrowする。<br>
     * データソース名が設定されていない場合は例外をthrowする。<br>
     *
     * @exception Exception 開始処理に失敗した場合
     */
    public void startService() throws Exception{
        
        if(getDataSourceServiceName() != null){
            dataSource = (DataSource)ServiceManagerFactory.getServiceObject(
                getDataSourceServiceName()
            );
        }
        if(getJndiFinderServiceName() != null){
            jndiFinder = (JndiFinder)ServiceManagerFactory.getServiceObject(
                getJndiFinderServiceName()
            );
        }
        if(jndiFinder == null && dataSource == null) {
            if(jndiFinder == null){
                throw new IllegalArgumentException("Argument : JndiFinderServiceName or JndiFinderService is null.");
            }
            if(dataSource == null){
                throw new IllegalArgumentException("Argument : DataSourceServiceName or DataSourceService is null.");
            }
        }
        
        if(getContextServiceName() != null){
            context = (Context)ServiceManagerFactory.getServiceObject(
                getContextServiceName()
            );
        }
        if(context == null && getName() == null){
            throw new IllegalArgumentException("DataSource name is null.");
        }
    }
    
    // ConnectionFactoryのJavaDoc
    public Connection getConnection() throws ConnectionFactoryException{
        Connection con = null;
        DataSource ds = dataSource;
        try{
            if(ds == null){
                String name = getName();
                if(context != null){
                    final String tmpName
                         = (String)context.get(getDataSourceNameKey());
                    if(tmpName != null){
                        name = tmpName;
                    }
                }
                ds = (DataSource)jndiFinder.lookup(name);
            }
            con = ds.getConnection();
        }catch(SQLException e){
            throw new ConnectionFactoryException(e);
        }catch(javax.naming.NamingException e){
            throw new ConnectionFactoryException(e);
        }
        return con;
    }
}
