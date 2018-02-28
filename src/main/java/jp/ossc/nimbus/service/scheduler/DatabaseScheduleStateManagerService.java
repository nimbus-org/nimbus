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
package jp.ossc.nimbus.service.scheduler;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.connection.ConnectionFactory;
import jp.ossc.nimbus.service.connection.ConnectionFactoryException;

/**
 * データベースでスケジュール状態管理を行うサービス。<p>
 *
 * @author M.Takata
 */
public class DatabaseScheduleStateManagerService extends ServiceBase
 implements ScheduleStateManager, DatabaseScheduleStateManagerServiceMBean{
    
    private static final long serialVersionUID = 8555788220745051629L;
    
    private static final String MSG_ID_00001 = "DBSM_00001";
    private static final String MSG_ID_00002 = "DBSM_00002";
    private static final String MSG_ID_00003 = "DBSM_00002";
    
    private ServiceName connectionFactoryServiceName;
    private ConnectionFactory connectionFactory;
    private String scheduleStateInsertQuery;
    private String scheduleStateSelectQuery;
    private String scheduleStateUpdateQuery;
    private String scheduleStateDeleteQuery;
    private String scheduleStateTruncateQuery;
    
    public void setConnectionFactoryServiceName(ServiceName name){
        connectionFactoryServiceName = name;
    }
    public ServiceName getConnectionFactoryServiceName(){
        return connectionFactoryServiceName;
    }
    
    public void setScheduleStateInsertQuery(String query){
        scheduleStateInsertQuery = query;
    }
    public String getScheduleStateInsertQuery(){
        return scheduleStateInsertQuery;
    }
    
    public void setScheduleStateSelectQuery(String query){
        scheduleStateSelectQuery = query;
    }
    public String getScheduleStateSelectQuery(){
        return scheduleStateSelectQuery;
    }
    
    public void setScheduleStateUpdateQuery(String query){
        scheduleStateUpdateQuery = query;
    }
    public String getScheduleStateUpdateQuery(){
        return scheduleStateUpdateQuery;
    }
    
    public void setScheduleStateDeleteQuery(String query){
        scheduleStateDeleteQuery = query;
    }
    public String getScheduleStateDeleteQuery(){
        return scheduleStateDeleteQuery;
    }
    
    public void setScheduleStateTruncateQuery(String query){
        scheduleStateTruncateQuery = query;
    }
    public String getScheduleStateTruncateQuery(){
        return scheduleStateTruncateQuery;
    }
    
    
    public void setConnectionFactory(ConnectionFactory factory){
        connectionFactory = factory;
    }
    
    public void startService() throws Exception{
        if(scheduleStateSelectQuery == null){
            throw new IllegalArgumentException("ScheduleStateSelectQuery must be specified.");
        }
        if(scheduleStateUpdateQuery == null){
            throw new IllegalArgumentException("ScheduleStateUpdateQuery must be specified.");
        }
        if(scheduleStateTruncateQuery == null){
            throw new IllegalArgumentException("ScheduleStateTruncateQuery must be specified.");
        }
        if(connectionFactoryServiceName == null && connectionFactory == null){
            throw new IllegalArgumentException("ConnectionFactoryServiceName or  ConnectionFactory must be specified.");
        }
        if(connectionFactoryServiceName != null){
            connectionFactory = (ConnectionFactory)ServiceManagerFactory
                .getServiceObject(connectionFactoryServiceName);
        }
    }
    
    // ScheduleStateManagerのJavaDoc
    public void changeState(String name, int state){
        Connection con = null;
        PreparedStatement selectPs = null;
        PreparedStatement updatePs = null;
        PreparedStatement insertPs = null;
        ResultSet rs = null;
        try{
            con = connectionFactory.getConnection();
            selectPs = con.prepareStatement(scheduleStateSelectQuery);
            selectPs.setString(1, name);
            rs = selectPs.executeQuery();
            if(rs.next()){
                int oldState = rs.getInt(1);
                if(rs.wasNull()){
                    oldState = STATE_UNKNOWN;
                }
                if(state != oldState){
                    updatePs = con.prepareStatement(scheduleStateUpdateQuery);
                    updatePs.setInt(1, state);
                    updatePs.setTimestamp(
                        2,
                        new Timestamp(System.currentTimeMillis())
                    );
                    updatePs.setString(3, name);
                    updatePs.executeUpdate();
                }
            }else{
                insertPs = con.prepareStatement(scheduleStateInsertQuery);
                insertPs.setString(1, name);
                insertPs.setInt(2, state);
                insertPs.setTimestamp(
                    3,
                    new Timestamp(System.currentTimeMillis())
                );
                insertPs.executeUpdate();
            }
        }catch(ConnectionFactoryException e){
            getLogger().write(
                MSG_ID_00001,
                new Object[]{name, new Integer(state)},
                e
            );
        }catch(SQLException e){
            getLogger().write(
                MSG_ID_00001,
                new Object[]{name, new Integer(state)},
                e
            );
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(selectPs != null){
                try{
                    selectPs.close();
                }catch(SQLException e){
                }
            }
            if(updatePs != null){
                try{
                    updatePs.close();
                }catch(SQLException e){
                }
            }
            if(insertPs != null){
                try{
                    insertPs.close();
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
    }
    
    // ScheduleStateManagerのJavaDoc
    public int getState(String name){
        Connection con = null;
        PreparedStatement selectPs = null;
        ResultSet rs = null;
        try{
            con = connectionFactory.getConnection();
            selectPs = con.prepareStatement(scheduleStateSelectQuery);
            selectPs.setString(1, name);
            rs = selectPs.executeQuery();
            if(rs.next()){
                int state = rs.getInt(1);
                if(rs.wasNull()){
                    state = STATE_UNKNOWN;
                }
                return state;
            }else{
                return STATE_UNKNOWN;
            }
        }catch(ConnectionFactoryException e){
            getLogger().write(MSG_ID_00002, name, e);
            return STATE_UNKNOWN;
        }catch(SQLException e){
            getLogger().write(MSG_ID_00002, name, e);
            return STATE_UNKNOWN;
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
            }
            if(selectPs != null){
                try{
                    selectPs.close();
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
    }
    
    // ScheduleStateManagerのJavaDoc
    public void clearState(String name){
        Connection con = null;
        PreparedStatement updatePs = null;
        PreparedStatement deletePs = null;
        try{
            con = connectionFactory.getConnection();
            if(scheduleStateDeleteQuery == null){
                updatePs = con.prepareStatement(scheduleStateUpdateQuery);
                updatePs.setInt(1, STATE_UNKNOWN);
                updatePs.setString(2, name);
                updatePs.setTimestamp(
                    3,
                    new Timestamp(System.currentTimeMillis())
                );
                updatePs.executeUpdate();
            }else{
                deletePs = con.prepareStatement(scheduleStateDeleteQuery);
                deletePs.setString(1, name);
                deletePs.executeUpdate();
            }
        }catch(ConnectionFactoryException e){
            getLogger().write(
                MSG_ID_00001,
                new Object[]{name, new Integer(STATE_UNKNOWN)},
                e
            );
        }catch(SQLException e){
            getLogger().write(
                MSG_ID_00001,
                new Object[]{name, new Integer(STATE_UNKNOWN)},
                e
            );
        }finally{
            if(updatePs != null){
                try{
                    updatePs.close();
                }catch(SQLException e){
                }
            }
            if(deletePs != null){
                try{
                    deletePs.close();
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
    }
    
    // ScheduleStateManagerのJavaDoc
    public void clearAllStates(){
        Connection con = null;
        Statement st = null;
        try{
            con = connectionFactory.getConnection();
            st = con.createStatement();
            st.executeQuery(scheduleStateTruncateQuery);
        }catch(ConnectionFactoryException e){
            getLogger().write(MSG_ID_00003, e);
        }catch(SQLException e){
            getLogger().write(MSG_ID_00003, e);
        }finally{
            if(st != null){
                try{
                    st.close();
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
        
    }
}