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
package jp.ossc.nimbus.service.websocket;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.server.ServerEndpointConfig.Configurator;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceBaseSupport;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceMetaData;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.Utility;
import jp.ossc.nimbus.service.context.Context;

/**
 * {@link Configurator}を継承したConfiguratorサービスクラス。
 * <p>
 * エンドポイントクラスのインスタンス生成、セッションオープン時のハンドシェイク事前処理を行う。
 *
 * @author M.Ishida
 */
public class DefaultConfiguratorService extends Configurator implements ServiceBaseSupport,
        DefaultConfiguratorServiceMBean {

    protected ServiceName endpointServiceName;
    protected ServiceName threadContextServiceName;
    protected String path;
    protected String idKey = DEFAULT_HANDSHAKE_ID_KEY;
    protected String ticketKey = DEFAULT_HANDSHAKE_TICKET_KEY;
    protected String contextIpKey = DEFAULT_CONTEXT_IP_KEY;
    protected String contextPortKey = DEFAULT_CONTEXT_PORT_KEY;

    protected ServiceBase service;
    protected Context threadContext;

    @Override
    public ServiceName getEndpointServiceName() {
        return endpointServiceName;
    }

    @Override
    public void setEndpointServiceName(ServiceName name) {
        endpointServiceName = name;
    }

    @Override
    public ServiceName getThreadContextServiceName() {
        return threadContextServiceName;
    }

    @Override
    public void setThreadContextServiceName(ServiceName name) {
        threadContextServiceName = name;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getIdKey() {
        return idKey;
    }

    @Override
    public void setIdKey(String key) {
        idKey = key;
    }

    @Override
    public String getTicketKey() {
        return ticketKey;
    }

    @Override
    public void setTicketKey(String key) {
        ticketKey = key;
    }

    @Override
    public String getContextIpKey() {
        return contextIpKey;
    }

    @Override
    public void setContextIpKey(String key) {
        contextIpKey = key;
    }

    @Override
    public String getContextPortKey() {
        return contextPortKey;
    }

    @Override
    public void setContextPortKey(String key) {
        contextPortKey = key;
    }

    @Override
    public Class getEndpointClass() throws ClassNotFoundException {
        ServiceMetaData metaData = ServiceManagerFactory.getServiceMetaData(getEndpointServiceName());
        return Utility.convertStringToClass(metaData.getCode());
    }

    @Override
    public void setServiceBase(ServiceBase service) {
        this.service = service;
    }

    @Override
    public void createService() throws Exception {
    }

    @Override
    public void startService() throws Exception {
        if (path == null || "".equals(path)) {
            throw new IllegalArgumentException("Path is null or Empty.");
        }
        if (endpointServiceName == null) {
            throw new IllegalArgumentException("EndpointServiceName is null.");
        }
        if (threadContextServiceName == null) {
            throw new IllegalArgumentException("ThreadContextServiceName is null.");
        }
        threadContext = (Context) ServiceManagerFactory.getServiceObject(threadContextServiceName);
    }

    @Override
    public void stopService() throws Exception {
    }

    @Override
    public void destroyService() throws Exception {
    }

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        // Endpointサービスオブジェクトを返却する。
        return (T) ServiceManagerFactory.getServiceObject(endpointServiceName);
    }

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {

        String id = null;
        String ticket = null;
        String ip = null;
        String port = null;
        List list = null;

        // Headerもしくは、リクエストパラメータからIDとチケットの情報を取得し、ServerEndpointConfigのUserPropertiesに格納する。
        if (request.getHeaders().containsKey(idKey) && request.getHeaders().containsKey(ticketKey)) {
            list = request.getHeaders().get(idKey);
            if (list != null && list.size() > 0) {
                id = (String) list.get(0);
            }
            list = request.getHeaders().get(ticketKey);
            if (list != null && list.size() > 0) {
                ticket = (String) list.get(0);
            }
        } else {
            list = request.getParameterMap().get(idKey);
            if (list != null && list.size() > 0) {
                id = (String) list.get(0);
            }
            list = request.getParameterMap().get(ticketKey);
            if (list != null && list.size() > 0) {
                ticket = (String) list.get(0);
            }
        }
        if(threadContext.containsKey(contextIpKey)){
            ip = (String) threadContext.get(contextIpKey);
        }
        if(threadContext.containsKey(contextPortKey)){
            port = ((Integer)threadContext.get(contextPortKey)).toString();
        }

        SessionProperties prop = new SessionProperties();
        prop.setId(id);
        prop.setTicket(ticket);
        prop.setIp(ip);
        prop.setPort(port);
        prop.setPath(path);
        prop.setHeaders(request.getHeaders());
        prop.setParameterMap(request.getParameterMap());
        Object httpSession = request.getHttpSession();
        if (httpSession != null && (httpSession instanceof HttpSession)) {
            prop.setHttpSessionId(((HttpSession) httpSession).getId());
        }
        sec.getUserProperties().put(SessionProperties.SESSION_PROPERTY_KEY, prop);
    }

}
