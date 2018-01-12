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
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;
import java.lang.reflect.*;
import javax.sql.*;
import javax.naming.Name;
import javax.naming.Referenceable;
import javax.naming.Reference;
import javax.naming.RefAddr;
import javax.naming.StringRefAddr;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import javax.naming.Context;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.repository.Repository;
import jp.ossc.nimbus.service.repository.JNDIRepositoryService;

/**
 * データソースラップ。<p>
 * 指定されたデータソースをラップして、コネクションラッパでラップされたコネクションを返すデータソースをJNDIにバインドする。<br>
 * コネクションラッパクラスは、java.sql.Connectionインタフェースを実装し、引数にjava.sql.Connectionを持つコンストラクタを持たなければならない。<br>
 * 以下に、サービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="WrappedDataSource"
 *                  code="jp.ossc.nimbus.service.connection.WrappedDataSourceService"&gt;
 *             &lt;attribute name="ConnectionWrapperClassName"&gt;sample.sql.ConnectionWrapper&lt;/attribute&gt;
 *             &lt;attribute name="SourceJNDIName"&gt;java:/DefaultDS&lt;/attribute&gt;
 *             &lt;attribute name="WrappedJNDIName"&gt;java:comp/WrappedDS&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class WrappedDataSourceService extends ServiceBase
 implements DataSource, Referenceable, WrappedDataSourceServiceMBean, Serializable{
    
    private static final long serialVersionUID = 1524707347811252995L;
    
    private ServiceName jndiRepositoryServiceName;
    private Repository jndiRepository;
    
    /**
     * ラップするDataSourceのJNDI名。<p>
     */
    private String sourceJNDIName;
    
    /**
     * ラップしたDataSourceのJNDI名。<p>
     */
    private String wrappedJNDIName;
    
    /**
     * {@link java.sql.Connection}をラップするクラスのクラス名。<p>
     * ここで指定できるクラスは、java.sql.Connectionインタフェースを実装しており、引数にjava.sql.Connectionを持つコンストラクタを実装しているクラスである。<br>
     */
    private String connectionWrapperClassName;
    
    /**
     * {@link java.sql.Connection}をラップするクラス。<p>
     */
    private Class connectionWrapperClass;
    
    private Map connectionWrapperProperties;
    
    private Map properties;
    
    private Reference reference;
    
    // WrappedDataSourceServiceMBeanのJavaDoc
    public void setJNDIRepositoryServiceName(ServiceName name){
        jndiRepositoryServiceName = name;
    }
    
    // WrappedDataSourceServiceMBeanのJavaDoc
    public ServiceName getJNDIRepositoryServiceName(){
        return jndiRepositoryServiceName;
    }
    
    // WrappedDataSourceServiceMBeanのJavaDoc
    public void setSourceJNDIName(String name){
        sourceJNDIName = name;
    }
    
    // WrappedDataSourceServiceMBeanのJavaDoc
    public String getSourceJNDIName(){
        return sourceJNDIName;
    }
    
    // WrappedDataSourceServiceMBeanのJavaDoc
    public void setWrappedJNDIName(String name){
        wrappedJNDIName = name;
    }
    
    // WrappedDataSourceServiceMBeanのJavaDoc
    public String getWrappedJNDIName(){
        return wrappedJNDIName;
    }
    
    // WrappedDataSourceServiceMBeanのJavaDoc
    public void setConnectionWrapperClassName(String className){
        connectionWrapperClassName = className;
    }
    
    // WrappedDataSourceServiceMBeanのJavaDoc
    public String getConnectionWrapperClassName(){
        return connectionWrapperClassName;
    }
    
    // WrappedDataSourceServiceMBeanのJavaDoc
    public void setConnectionWrapperProperties(Map prop){
        connectionWrapperProperties = prop;
    }
    
    // WrappedDataSourceServiceMBeanのJavaDoc
    public Map getConnectionWrapperProperties(){
        return connectionWrapperProperties;
    }
    
    /**
     * 開始処理を行う。<p>
     *
     * @exception Exception 開始処理に失敗した場合
     */
    public void startService() throws Exception{
        
        if(sourceJNDIName == null){
            throw new IllegalArgumentException(
                "SourceJNDIName is null."
            );
        }
        if(wrappedJNDIName == null){
            throw new IllegalArgumentException(
                "WrappedJNDIName is null."
            );
        }
        
        if(jndiRepositoryServiceName != null){
            jndiRepository = (Repository)ServiceManagerFactory.getServiceObject(jndiRepositoryServiceName);
        }
        if(jndiRepository == null){
            JNDIRepositoryService jndiRepositoryService = new JNDIRepositoryService();
            jndiRepositoryService.create();
            jndiRepositoryService.start();
            jndiRepository = jndiRepositoryService;
        }
        
        if(getConnectionWrapperClassName() != null){
            connectionWrapperClass = Class.forName(
                getConnectionWrapperClassName(),
                true,
                NimbusClassLoader.getInstance()
            );
        }
        
        if(connectionWrapperProperties != null
            && connectionWrapperProperties.size() != 0){
            properties = new LinkedHashMap();
            final Iterator props
                 = connectionWrapperProperties.keySet().iterator();
            while(props.hasNext()){
                final String propName = (String)props.next();
                final Object val = connectionWrapperProperties.get(propName);
                final Property property
                     = PropertyFactory.createProperty(propName);
                properties.put(property, val);
            }
        }
        
        if(getSourceDataSource() == null){
            throw new IllegalArgumentException(
                "SourceJNDIName can not get from JNDIRepository."
            );
        }
        
        StringRefAddr addr = new StringRefAddr(
            "nimbus/service",
            getServiceNameObject().toString()
        );
        reference = new Reference(
            getClass().getName(),
            addr,
            WrappedDataSourceObjectFactory.class.getName(),
            null
        );
        if(!jndiRepository.register(wrappedJNDIName, reference)){
            throw new IllegalArgumentException(
                "WrappedJNDIName can not register to JNDIRepository."
            );
        }
    }
    
    /**
     * 停止処理を行う。<p>
     *
     * @exception Exception 停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        jndiRepository.unregister(wrappedJNDIName);
    }
    
    private DataSource getSourceDataSource(){
        return (DataSource)jndiRepository.get(sourceJNDIName);
    }
    
    private Connection wrapConnection(Connection con) throws SQLException{
        if(con != null && connectionWrapperClass != null){
            try{
                final Constructor constructor
                     = connectionWrapperClass.getConstructor(
                        new Class[]{Connection.class}
                    );
                con = (Connection)constructor.newInstance(new Object[]{con});
                if(properties != null){
                    final Iterator props = properties.keySet().iterator();
                    while(props.hasNext()){
                        final Property prop = (Property)props.next();
                        final Object val = properties.get(prop);
                        prop.setProperty(con, val);
                    }
                }
            }catch(InstantiationException e){
                throw new SQLException(e.toString());
            }catch(IllegalAccessException e){
                throw new SQLException(e.toString());
            }catch(InvocationTargetException e){
                throw new SQLException(e.getTargetException().toString());
            }catch(NoSuchMethodException e){
                throw new SQLException(e.toString());
            }catch(NoSuchPropertyException e){
                throw new SQLException(e.toString());
            }
        }
        return con;
    }
    
    // DataSourceのJavaDoc
    public Connection getConnection() throws SQLException{
        Connection con = getSourceDataSource().getConnection();
        return wrapConnection(con);
    }
    
    // DataSourceのJavaDoc
    public Connection getConnection(String username, String password) throws SQLException{
        Connection con = getSourceDataSource().getConnection(username, password);
        return wrapConnection(con);
    }
    
    // DataSourceのJavaDoc
    public PrintWriter getLogWriter() throws SQLException{
        return getSourceDataSource().getLogWriter();
    }
    
    // DataSourceのJavaDoc
    public void setLogWriter(PrintWriter out) throws SQLException{
        getSourceDataSource().setLogWriter(out);
    }
    
    // DataSourceのJavaDoc
    public void setLoginTimeout(int seconds) throws SQLException{
        getSourceDataSource().setLoginTimeout(seconds);
    }
    
    // DataSourceのJavaDoc
    public int getLoginTimeout() throws SQLException{
        return getSourceDataSource().getLoginTimeout();
    }
    

    // DataSourceのJavaDoc
    public <T> T unwrap(Class<T> iface) throws SQLException{
        return getSourceDataSource().unwrap(iface);
    }
    
    // DataSourceのJavaDoc
    public boolean isWrapperFor(Class<?> iface) throws SQLException{
        return getSourceDataSource().isWrapperFor(iface);
    }
    

    

    // DataSourceのJavaDoc
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException{
        return getSourceDataSource().getParentLogger();
    }

    
    public void setJNDIRepository(Repository repository) {
        jndiRepository = repository;
    }
    
    public Reference getReference() throws NamingException{
        return reference;
    }
    
    public static class WrappedDataSourceObjectFactory implements ObjectFactory{
        public Object getObjectInstance(
            Object obj,
            Name name,
            Context nameCtx,
            Hashtable environment
        ) throws Exception{
            Reference ref = (Reference)obj;
            RefAddr addr = ref.get("nimbus/service");
            String serviceNameStr = (String)addr.getContent();
            ServiceNameEditor editor = new ServiceNameEditor();
            editor.setAsText(serviceNameStr);
            ServiceName serviceName = (ServiceName)editor.getValue();
            return ServiceManagerFactory.getServiceObject(serviceName);
        }
    }
}
