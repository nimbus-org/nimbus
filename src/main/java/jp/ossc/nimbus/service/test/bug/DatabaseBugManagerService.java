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
package jp.ossc.nimbus.service.test.bug;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.connection.ConnectionFactory;
import jp.ossc.nimbus.service.connection.ConnectionFactoryException;
import jp.ossc.nimbus.service.connection.PersistentException;
import jp.ossc.nimbus.service.connection.PersistentManager;
import jp.ossc.nimbus.service.sequence.Sequence;

/**
 * Databaseを使用した{@link BugManager}インタフェース実装クラス。
 *
 * @author M.Ishida
 */
public class DatabaseBugManagerService extends ServiceBase implements BugManager, DatabaseBugManagerServiceMBean {

    private static final long serialVersionUID = 8859338691513147568L;
    
    protected ServiceName connectionFactoryServiceName;
    protected ConnectionFactory connectionFactory;
    
    protected ServiceName persistentManagerServiceName;
    protected PersistentManager persistentManager;
    
    protected ServiceName sequenceServiceName;
    protected Sequence sequence;
    
    protected String addSql;
    protected String updateSql;
    protected String deleteSql;
    protected String listSql;
    protected String getSql;
    
    protected BugRecord templateRecord;
    
    public ServiceName getConnectionFactoryServiceName() {
        return connectionFactoryServiceName;
    }

    public void setConnectionFactoryServiceName(ServiceName serviceName) {
        connectionFactoryServiceName = serviceName;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory factory) {
        connectionFactory = factory;
    }

    public ServiceName getPersistentManagerServiceName() {
        return persistentManagerServiceName;
    }

    public void setPersistentManagerServiceName(ServiceName serviceName) {
        persistentManagerServiceName = serviceName;
    }

    public PersistentManager getPersistentManager() {
        return persistentManager;
    }

    public void setPersistentManager(PersistentManager manager) {
        persistentManager = manager;
    }

    public ServiceName getSequenceServiceName() {
        return sequenceServiceName;
    }

    public void setSequenceServiceName(ServiceName serviceName) {
        sequenceServiceName = serviceName;
    }

    public Sequence getSequence() {
        return sequence;
    }

    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
    }

    public String getAddSql() {
        return addSql;
    }

    public void setAddSql(String sql) {
        addSql = sql;
    }

    public String getUpdateSql() {
        return updateSql;
    }

    public void setUpdateSql(String sql) {
        updateSql = sql;
    }

    public String getDeleteSql() {
        return deleteSql;
    }

    public void setDeleteSql(String sql) {
        deleteSql = sql;
    }

    public String getListSql() {
        return listSql;
    }
    
    public void setListSql(String sql) {
        listSql = sql;
    }

    public String getGetSql() {
        return getSql;
    }

    public void setGetSql(String sql) {
        getSql = sql;
    }
    
    public BugRecord getTemplateRecord() {
        return templateRecord;
    }
    
    public void setTemplateRecord(BugRecord record) {
        templateRecord = record;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(connectionFactoryServiceName != null){
            connectionFactory = (ConnectionFactory)ServiceManagerFactory.getServiceObject(connectionFactoryServiceName);
        }
        if(connectionFactory == null){
            throw new IllegalArgumentException("ConnectionFactory is null.");
        }
        if(persistentManagerServiceName != null){
            persistentManager = (PersistentManager)ServiceManagerFactory.getServiceObject(persistentManagerServiceName);
        }
        if(persistentManager == null){
            throw new IllegalArgumentException("PersistentManager is null.");
        }
        if(addSql == null) {
            throw new IllegalArgumentException("addSql is null.");
        }
        if(updateSql == null) {
            throw new IllegalArgumentException("updateSql is null.");
        }
        if(deleteSql == null) {
            throw new IllegalArgumentException("deleteSql is null.");
        }
        if(listSql == null) {
            throw new IllegalArgumentException("listSql is null.");
        }
        if(getSql == null) {
            throw new IllegalArgumentException("getSql is null.");
        }
        if(templateRecord == null) {
            throw new IllegalArgumentException("templateRecord is null.");
        }
        if(sequenceServiceName != null){
            sequence = (Sequence)ServiceManagerFactory.getServiceObject(sequenceServiceName);
        }
    }
    
    public BugRecord add(BugRecord record) throws BugManageException {
        if(record.getId() == null && sequence != null) {
            record.setId(sequence.increment());
        }
        if(record.getId() == null) {
            throw new BugManageException("Id is null.");
        }
        Connection con;
        try {
            con = connectionFactory.getConnection();
        } catch (ConnectionFactoryException e) {
            throw new BugManageException(e);
        }
        try {
            persistentManager.persistQuery(con, addSql, record);
        } catch (PersistentException e) {
            throw new BugManageException(e);
        } finally {
            if(con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                }
            }
        }
        return record;
    }
    
    public void update(BugRecord record) throws BugManageException  {
        Connection con;
        try {
            con = connectionFactory.getConnection();
        } catch (ConnectionFactoryException e) {
            throw new BugManageException(e);
        }
        try {
            record.setUpdateDate(new Date());
            persistentManager.persistQuery(con, updateSql, record);
        } catch (PersistentException e) {
            throw new BugManageException(e);
        } finally {
            if(con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                }
            }
        }
    }
    
    public void delete(String id) throws BugManageException {
        BugRecord record = new BugRecord();
        record.setId(id);
        delete(record);
    }
    
    public void delete(BugRecord record) throws BugManageException {
        Connection con;
        try {
            con = connectionFactory.getConnection();
        } catch (ConnectionFactoryException e) {
            throw new BugManageException(e);
        }
        try {
            persistentManager.persistQuery(con, deleteSql, record);
        } catch (PersistentException e) {
            throw new BugManageException(e);
        } finally {
            if(con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                }
            }
        }
    }
    
    public List list() throws BugManageException {
        Connection con;
        try {
            con = connectionFactory.getConnection();
        } catch (ConnectionFactoryException e) {
            throw new BugManageException(e);
        }
        try {
            Object result = persistentManager.loadQuery(con, listSql, null, templateRecord.cloneBugAttribute());
            if(result == null) {
                return new ArrayList();
            }else if(result instanceof List) {
                return (List)result;
            } else {
                List list = new ArrayList();
                list.add(result);
                return list;
            }
        } catch (PersistentException e) {
            throw new BugManageException(e);
        } finally {
            if(con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                }
            }
        }
    }
    
    public BugRecord get(String id) throws BugManageException {
        BugRecord record = templateRecord.cloneBugAttribute();
        record.setId(id);
        return get(record);
    }
    
    public BugRecord get(BugRecord record) throws BugManageException {
        Connection con;
        try {
            con = connectionFactory.getConnection();
        } catch (ConnectionFactoryException e) {
            throw new BugManageException(e);
        }
        try {
            Object result = persistentManager.loadQuery(con, getSql, record, templateRecord.cloneBugAttribute());
            if(result instanceof List) {
                List list =(List)result;
                if(list.size() == 0) {
                    return null;
                }else if(list.size() > 1) {
                    throw new BugManageException("More than one record was acquired.");
                }
                return (BugRecord)list.get(0);
            } else {
                return (BugRecord)result;
            }
        } catch (PersistentException e) {
            throw new BugManageException(e);
        } finally {
            if(con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                }
            }
        }
    }
}
