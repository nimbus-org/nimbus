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
package jp.ossc.nimbus.service.aop.interceptor.servlet;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jp.ossc.nimbus.beans.dataset.DataSet;
import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.beans.dataset.RecordList;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.connection.ConnectionFactory;
import jp.ossc.nimbus.service.connection.ConnectionFactoryException;
import jp.ossc.nimbus.service.connection.PersistentException;
import jp.ossc.nimbus.service.connection.PersistentManager;

/**
 * データベース認証ストア。
 * <p>
 *
 * @author M.Takata
 */
public class DatabaseAuthenticateStoreService extends ServiceBase implements AuthenticateStore, DatabaseAuthenticateStoreServiceMBean {

    private static final long serialVersionUID = 1001424917332828547L;
    private ServiceName connectionFactoryServiceName;
    private ConnectionFactory connectionFactory;

    private ServiceName persistentManagerServiceName;
    private PersistentManager persistentManager;

    private Class authenticatedInfoClass;
    private Object authenticatedInfoTemplate;

    private String hostName;
    private String selectQueryOnCreateUser;
    private String selectQueryOnFindUser;
    private String insertQuery;
    private String updateQueryOnCreate;
    private String deleteQueryOnCreate;
    private String updateQueryOnActivate;
    private String updateQueryOnDeactivate;
    private String deleteQuery;

    private boolean isDeleteFindUser;

    public void setConnectionFactoryServiceName(ServiceName name) {
        connectionFactoryServiceName = name;
    }

    public ServiceName getConnectionFactoryServiceName() {
        return connectionFactoryServiceName;
    }

    public void setPersistentManagerServiceName(ServiceName name) {
        persistentManagerServiceName = name;
    }

    public ServiceName getPersistentManagerServiceName() {
        return persistentManagerServiceName;
    }

    public void setAuthenticatedInfoClass(Class clazz) {
        authenticatedInfoClass = clazz;
    }

    public Class getAuthenticatedInfoClass() {
        return authenticatedInfoClass;
    }

    public void setAuthenticatedInfoTemplate(Object template) {
        authenticatedInfoTemplate = template;
    }

    public Object getAuthenticatedInfoTemplate() {
        return authenticatedInfoTemplate;
    }

    public void setHostName(String name) {
        hostName = name;
    }

    public String getHostName() {
        return hostName;
    }

    public void setSelectQueryOnCreateUser(String query) {
        selectQueryOnCreateUser = query;
    }

    public String getSelectQueryOnCreateUser() {
        return selectQueryOnCreateUser;
    }

    public void setSelectQueryOnFindUser(String query) {
        selectQueryOnFindUser = query;
    }

    public String getSelectQueryOnFindUser() {
        return selectQueryOnFindUser;
    }

    public void setInsertQuery(String query) {
        insertQuery = query;
    }

    public String getInsertQuery() {
        return insertQuery;
    }

    public void setUpdateQueryOnCreate(String query) {
        updateQueryOnCreate = query;
    }

    public String getUpdateQueryOnCreate() {
        return updateQueryOnCreate;
    }

    public void setDeleteQueryOnCreate(String query) {
        deleteQueryOnCreate = query;
    }

    public String getDeleteQueryOnCreate() {
        return deleteQueryOnCreate;
    }

    public void setUpdateQueryOnActivate(String query) {
        updateQueryOnActivate = query;
    }

    public String getUpdateQueryOnActivate() {
        return updateQueryOnActivate;
    }

    public void setUpdateQueryOnDeactivate(String query) {
        updateQueryOnDeactivate = query;
    }

    public String getUpdateQueryOnDeactivate() {
        return updateQueryOnDeactivate;
    }

    public void setDeleteQuery(String query) {
        deleteQuery = query;
    }

    public String getDeleteQuery() {
        return deleteQuery;
    }

    public void setDeleteFindUser(boolean isDelete) {
        isDeleteFindUser = isDelete;
    }

    public boolean isDeleteFindUser() {
        return isDeleteFindUser;
    }

    public void setConnectionFactory(ConnectionFactory factory) {
        connectionFactory = factory;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setPersistentManager(PersistentManager manager) {
        persistentManager = manager;
    }

    public PersistentManager getPersistentManager() {
        return persistentManager;
    }

    /**
     * サービスの開始処理を行う。
     * <p>
     *
     * @exception Exception サービスの開始に失敗した場合
     */
    public void startService() throws Exception {
        if (connectionFactory == null && connectionFactoryServiceName == null) {
            throw new IllegalArgumentException("ConnectionFactory is null.");
        }
        if (connectionFactoryServiceName != null) {
            connectionFactory = (ConnectionFactory) ServiceManagerFactory.getServiceObject(connectionFactoryServiceName);
        }
        if (persistentManager == null && persistentManagerServiceName == null) {
            throw new IllegalArgumentException("PersistentManager is null.");
        }
        if (persistentManagerServiceName != null) {
            persistentManager = (PersistentManager) ServiceManagerFactory.getServiceObject(persistentManagerServiceName);
        }
        if (hostName == null) {
            hostName = InetAddress.getLocalHost().getHostName();
        }
    }

    protected Object createInput(HttpServletRequest request, HttpSession session, Object auth) {
        Map input = new HashMap();
        input.put(INPUT_KEY_TIMESTAMP, new Timestamp(System.currentTimeMillis()));
        if (session == null && request != null) {
            session = request.getSession(false);
        }
        if (session != null) {
            input.put(INPUT_KEY_HTTP_SESSION_ID, session.getId());
        }
        if (hostName != null) {
            input.put(INPUT_KEY_HOST, hostName);
        }
        input.put(INPUT_KEY_AUTH, auth);
        return input;
    }

    public void create(HttpServletRequest request, Object authenticatedInfo) throws AuthenticateStoreException {
        if (insertQuery == null) {
            return;
        }
        Connection con = null;
        try {
            con = connectionFactory.getConnection();
        } catch (ConnectionFactoryException e) {
            throw new AuthenticateStoreException(e);
        }
        try {
            boolean isExists = false;
            Object input = createInput(request, null, authenticatedInfo);
            if (selectQueryOnCreateUser != null) {
                List list = (List) persistentManager.loadQuery(con, selectQueryOnCreateUser, input, null);
                if (list.size() == 0 || (list.get(0) instanceof Number && ((Number) list.get(0)).intValue() <= 0)
                        || (list.get(0) instanceof Boolean && !((Boolean) list.get(0)).booleanValue())
                        || (list.get(0) instanceof String && !((String) list.get(0)).equals("0"))) {
                    throw new AuthenticateStoreException("Already exists." + authenticatedInfo);
                }
            }
            if (selectQueryOnFindUser != null) {
                List list = (List) persistentManager.loadQuery(con, selectQueryOnFindUser, input, null);
                isExists = list != null && list.size() != 0;
            }
            if (isExists) {
                if (isDeleteFindUser) {
                    if (deleteQueryOnCreate != null) {
                        persistentManager.persistQuery(con, deleteQueryOnCreate, input);
                        persistentManager.persistQuery(con, insertQuery, input);
                    }
                } else {
                    if (updateQueryOnCreate != null) {
                        persistentManager.persistQuery(con, updateQueryOnCreate, input);
                    }
                }
            } else {
                persistentManager.persistQuery(con, insertQuery, input);
            }
        } catch (PersistentException e) {
            throw new AuthenticateStoreException(e);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
            }
        }
    }

    public Object activate(HttpServletRequest request, Object authenticatedKey) throws AuthenticateStoreException {
        if (selectQueryOnFindUser == null) {
            return null;
        }
        Connection con = null;
        try {
            con = connectionFactory.getConnection();
        } catch (ConnectionFactoryException e) {
            throw new AuthenticateStoreException(e);
        }
        try {
            Object authenticatedInfo = null;
            if (selectQueryOnFindUser != null) {
                if (authenticatedInfoClass != null) {
                    List list = (List) persistentManager.loadQuery(con, selectQueryOnFindUser, createInput(request, null, authenticatedKey),
                            authenticatedInfoClass);
                    if (list.size() == 0) {
                        return null;
                    }
                    authenticatedInfo = list.get(0);
                } else {
                    if (authenticatedInfoTemplate instanceof DataSet) {
                        authenticatedInfo = ((DataSet) authenticatedInfoTemplate).cloneSchema();
                    } else if (authenticatedInfoTemplate instanceof RecordList) {
                        authenticatedInfo = ((RecordList) authenticatedInfoTemplate).cloneSchema();
                    } else if (authenticatedInfoTemplate instanceof Record) {
                        authenticatedInfo = ((Record) authenticatedInfoTemplate).cloneSchema();
                    } else if (authenticatedInfoTemplate instanceof Cloneable) {
                        try {
                            authenticatedInfo = authenticatedInfoTemplate.getClass().getMethod("clone", (Class[]) null)
                                    .invoke(authenticatedInfoTemplate, (Object[]) null);
                        } catch (NoSuchMethodException e) {
                            throw new AuthenticateStoreException(e);
                        } catch (IllegalAccessException e) {
                            throw new AuthenticateStoreException(e);
                        } catch (InvocationTargetException e) {
                            throw new AuthenticateStoreException(e);
                        }
                    }
                    authenticatedInfo = persistentManager.loadQuery(con, selectQueryOnFindUser, createInput(request, null, authenticatedKey),
                            authenticatedInfo);
                }
            }
            if (authenticatedInfo != null && updateQueryOnActivate != null) {
                if (request.getSession(false) == null) {
                    request.getSession(true);
                }
                persistentManager.persistQuery(con, updateQueryOnActivate, createInput(request, null, authenticatedInfo));
            }
            return authenticatedInfo;
        } catch (PersistentException e) {
            throw new AuthenticateStoreException(e);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
            }
        }
    }

    public void deactivate(HttpSession session, Object authenticatedInfo) throws AuthenticateStoreException {
        if (updateQueryOnDeactivate == null) {
            return;
        }
        Connection con = null;
        try {
            con = connectionFactory.getConnection();
        } catch (ConnectionFactoryException e) {
            throw new AuthenticateStoreException(e);
        }
        try {
            persistentManager.persistQuery(con, updateQueryOnDeactivate, createInput(null, session, authenticatedInfo));
        } catch (PersistentException e) {
            throw new AuthenticateStoreException(e);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
            }
        }
    }

    public void destroy(HttpServletRequest request, Object authenticatedKey) throws AuthenticateStoreException {
        if (deleteQuery == null) {
            return;
        }
        Connection con = null;
        try {
            con = connectionFactory.getConnection();
        } catch (ConnectionFactoryException e) {
            throw new AuthenticateStoreException(e);
        }
        try {
            persistentManager.persistQuery(con, deleteQuery, createInput(request, null, authenticatedKey));
        } catch (PersistentException e) {
            throw new AuthenticateStoreException(e);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
            }
        }
    }
}