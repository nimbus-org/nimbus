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
package jp.ossc.nimbus.service.keepalive.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jp.ossc.nimbus.service.keepalive.*;
import jp.ossc.nimbus.service.system.HostResolver;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.connection.ConnectionFactory;

/**
 * JDBCサーバの稼動状態をチェックする{@link KeepAliveChecker}インタフェース実装サービス。<p>
 * 
 * @author M.Takata
 */
public class JDBCKeepAliveCheckerService extends AbstractKeepAliveCheckerService
 implements JDBCKeepAliveCheckerServiceMBean{
    
    private static final long serialVersionUID = 7743915494898892292L;
    
    private ServiceName connectionFactoryServiceName;
    private String checkQuery;
    
    protected HostResolver hostResolver;
    protected ServiceName hostResolverServiceName;
    
    private ConnectionFactory connectionFactory;
    
    public void setConnectionFactoryServiceName(ServiceName name){
        connectionFactoryServiceName = name;
    }
    public ServiceName getConnectionFactoryServiceName(){
        return connectionFactoryServiceName;
    }
    
    public void setHostResolverServiceName(ServiceName name){
        hostResolverServiceName = name;
    }
    public ServiceName getHostResolverServiceName(){
        return hostResolverServiceName;
    }
    
    public void setCheckQuery(String query){
        checkQuery = query;
    }
    public String getCheckQuery(){
        return checkQuery;
    }
    
    public void setConnectionFactory(ConnectionFactory factory){
        connectionFactory = factory;
    }
    public ConnectionFactory getConnectionFactory(){
        return connectionFactory;
    }
    
    public void startService() throws Exception{
        if(connectionFactoryServiceName != null){
            connectionFactory = (ConnectionFactory)ServiceManagerFactory.getServiceObject(connectionFactoryServiceName);
        }
        if(connectionFactory == null){
            throw new IllegalArgumentException("ConnectionFactory is null.");
        }
        if(checkQuery == null){
            throw new IllegalArgumentException("CheckQuery is null.");
        }
        if (hostResolverServiceName != null) {
            hostResolver = (HostResolver) ServiceManagerFactory.getServiceObject(hostResolverServiceName);
        }
    }
    
    public boolean checkAlive() throws Exception{
        Connection con = connectionFactory.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = con.prepareStatement(checkQuery);
            rs = ps.executeQuery();
            rs.next();
        }finally{
            if(ps != null){
                try{
                    ps.close();
                }catch(SQLException e){
                }
                ps = null;
            }
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
                rs = null;
            }
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                }
                con = null;
            }
        }
        return true;
    }
    
    public Object getHostInfo() {
        return hostResolver == null ? null : hostResolver.getLocalHost();
    }
}