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
package jp.ossc.nimbus.service.system;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.beans.dataset.RecordList;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.connection.ConnectionFactory;
import jp.ossc.nimbus.service.connection.PersistentManager;

/**
 * Databaseホスト情報取得サービス。
 * <p>
 *
 * @author M.Ishida
 */
public class DatabaseHostResolverService extends ServiceBase implements HostResolver, DatabaseHostResolverServiceMBean {
    
    private static final long serialVersionUID = -2159903335103754963L;
    
    protected ServiceName connectionFactoryServiceName;
    protected ConnectionFactory connectionFactory;
    
    protected ServiceName persistentManagerServiceName;
    protected PersistentManager persistentManager;
    
    protected String selectSql;
    
    protected Map hostMap;
    
    public ServiceName getConnectionFactoryServiceName() {
        return connectionFactoryServiceName;
    }
    
    public void setConnectionFactoryServiceName(ServiceName name) {
        connectionFactoryServiceName = name;
    }
    
    public ServiceName getPersistentManagerServiceName() {
        return persistentManagerServiceName;
    }
    
    public void setPersistentManagerServiceName(ServiceName name) {
        persistentManagerServiceName = name;
    }
    
    public String getSelectSql() {
        return selectSql;
    }
    
    public void setSelectSql(String sql) {
        selectSql = sql;
    }
    
    public Map getHostMap() {
        return hostMap;
    }
    
    public void createService() throws Exception {
        hostMap = new HashMap();
    }
    
    public void startService() throws Exception {
        
        if (selectSql == null || "".equals(selectSql)) {
            throw new IllegalArgumentException("SelectSql must be specified.");
        }
        
        if (connectionFactoryServiceName == null) {
            throw new IllegalArgumentException("DbConnectionFactoryServiceName must be specified.");
        }
        connectionFactory = (ConnectionFactory) ServiceManagerFactory.getServiceObject(connectionFactoryServiceName);
        
        if (persistentManagerServiceName == null) {
            throw new IllegalArgumentException("PersistentManagerServiceName must be specified.");
        }
        persistentManager = (PersistentManager) ServiceManagerFactory.getServiceObject(persistentManagerServiceName);
        
        reloadHostMap();
    }
    
    public void reloadHostMap() throws Exception {
        hostMap.clear();
        RecordList result = (RecordList) persistentManager.loadQuery(connectionFactory.getConnection(), selectSql, null, new RecordList());
        for (int i = 0; i < result.size(); i++) {
            Record record = (Record) result.get(i);
            try {
                InetAddress inetAddress = InetAddress.getByName(record.getStringProperty(1));
                hostMap.put(record.getStringProperty(0), inetAddress);
            } catch (UnknownHostException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
    
    // HostResolverのJavaDoc
    public InetAddress getLocalHost() {
        InetAddress localhost = null;
        try {
            localhost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            // 取得できない場合はNullを返却
            return null;
        }
        InetAddress result = getHost(localhost.getHostName()) != null ? getHost(localhost.getHostName()) : getHost(localhost.getHostAddress());
        if (result == null) {
            result = localhost;
        }
        return result;
    }
    
    // HostResolverのJavaDoc
    public InetAddress getHost(String hostName) {
        return (InetAddress) hostMap.get(hostName);
    }
    
}