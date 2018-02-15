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

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceBaseSupport;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.journal.Journal;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;
import jp.ossc.nimbus.service.performance.ResourceUsage;
import jp.ossc.nimbus.service.sequence.Sequence;

/**
 * {@link Endpoint}を継承したEndpointサービスクラス。
 * <p>
 *
 * @author M.Ishida
 */
public class DefaultEndpointService extends Endpoint implements ServiceBaseSupport, DefaultEndpointServiceMBean, ResourceUsage {

    protected ServiceName[] messageHandlerServiceNames;
    protected ServiceName authenticatorServiceName;
    protected ServiceName exceptionHandlerMappingServiceName;
    protected ServiceName webSocketAccessJournalServiceName;
    protected ServiceName editorFinderServiceName;
    protected ServiceName sequenceServiceName;
    protected int maxClientSize = -1;
    protected long maxIdleTimeout = -1;
    protected int maxTextMessageBufferSize = -1;
    protected int maxBinaryMessageBufferSize = -1;

    protected String accessJournalKey = DEFAULT_ACCESS_JOURNAL_KEY;
    protected String idJournalKey = DEFAULT_ID_JOURNAL_KEY;
    protected String ticketJournalKey = DEFAULT_TICKET_JOURNAL_KEY;
    protected String webSocketSessionIdJournalKey = DEFAULT_WEBSOCKET_SESSION_ID_JOURNAL_KEY;
    protected String httpSessionIdJournalKey = DEFAULT_HTTP_SESSION_ID_JOURNAL_KEY;
    protected String pathJournalKey = DEFAULT_PATH_JOURNAL_KEY;
    protected String ipJournalKey = DEFAULT_IP_JOURNAL_KEY;
    protected String portJournalKey = DEFAULT_PORT_JOURNAL_KEY;
    protected String headerJournalKey = DEFAULT_HEADER_JOURNAL_KEY;
    protected String parameterJournalKey = DEFAULT_PARAMETER_JOURNAL_KEY;
    protected String requestMessageJournalKey = DEFAULT_REQUEST_MESSAGE_JOURNAL_KEY;
    protected String closeReasonJournalKey = DEFAULT_CLOSE_REASON_JOURNAL_KEY;
    protected String authResultJournalKey = DEFAULT_AUTH_RESULT_JOURNAL_KEY;
    protected String exceptionJournalKey = DEFAULT_EXCEPTION_JOURNAL_KEY;

    protected String illegalRequestMessageId = DEFAULT_ILLEGAL_REQUEST_MESSAGE_ID;
    protected String maxClientSizeOverMessageId = DEFAULT_MAX_CLIENT_SIZE_OVER_MESSAGE_ID;
    protected String abnormalCloseMessageId = DEFAULT_ABNORMAL_CLOSE_MESSAGE_ID;

    protected Authenticator authenticator;
    protected ExceptionHandlerMappingService exceptionHandler;
    protected Journal accessJournal;
    protected EditorFinder editorFinder;
    protected Sequence sequence;

    protected Set sessionSet;

    protected ServiceBase service;

    @Override
    public ServiceName[] getMessageHandlerServiceNames() {
        return messageHandlerServiceNames;
    }

    @Override
    public void setMessageHandlerServiceNames(ServiceName[] names) {
        messageHandlerServiceNames = names;
    }

    @Override
    public ServiceName getAuthenticatorServiceName() {
        return authenticatorServiceName;
    }

    @Override
    public void setAuthenticatorServiceName(ServiceName name) {
        this.authenticatorServiceName = name;
    }

    @Override
    public ServiceName getExceptionHandlerMappingServiceName() {
        return exceptionHandlerMappingServiceName;
    }

    @Override
    public void setExceptionHandlerMappingServiceName(ServiceName name) {
        this.exceptionHandlerMappingServiceName = name;
    }

    @Override
    public ServiceName getWebSocketAccessJournalServiceName() {
        return webSocketAccessJournalServiceName;
    }

    @Override
    public void setWebSocketAccessJournalServiceName(ServiceName name) {
        webSocketAccessJournalServiceName = name;
    }

    @Override
    public ServiceName getEditorFinderServiceName() {
        return editorFinderServiceName;
    }

    @Override
    public void setEditorFinderServiceName(ServiceName name) {
        editorFinderServiceName = name;
    }

    @Override
    public ServiceName getSequenceServiceName() {
        return sequenceServiceName;
    }

    @Override
    public void setSequenceServiceName(ServiceName name) {
        sequenceServiceName = name;
    }

    @Override
    public void setAccessJournalKey(String key) {
        accessJournalKey = key;
    }

    @Override
    public String getAccessJournalKey() {
        return accessJournalKey;
    }

    @Override
    public void setIdJournalKey(String key) {
        idJournalKey = key;
    }

    @Override
    public String getIdJournalKey() {
        return idJournalKey;
    }

    @Override
    public void setTicketJournalKey(String key) {
        ticketJournalKey = key;
    }

    @Override
    public String getTicketJournalKey() {
        return ticketJournalKey;
    }

    @Override
    public void setWebSocketSessionIdJournalKey(String key) {
        webSocketSessionIdJournalKey = key;
    }

    @Override
    public String getWebSocketSessionIdJournalKey() {
        return webSocketSessionIdJournalKey;
    }

    @Override
    public String getHttpSessionIdJournalKey() {
        return httpSessionIdJournalKey;
    }

    @Override
    public void setHttpSessionIdJournalKey(String key) {
        httpSessionIdJournalKey = key;
    }

    @Override
    public String getPathJournalKey() {
        return pathJournalKey;
    }

    @Override
    public void setPathJournalKey(String key) {
        pathJournalKey = key;
    }

    @Override
    public String getIpJournalKey() {
        return ipJournalKey;
    }

    @Override
    public void setIpJournalKey(String key) {
        ipJournalKey = key;
    }

    @Override
    public String getPortJournalKey() {
        return portJournalKey;
    }

    @Override
    public void setPortJournalKey(String key) {
        portJournalKey = key;
    }

    @Override
    public String getHeaderJournalKey() {
        return headerJournalKey;
    }

    @Override
    public void setHeaderJournalKey(String key) {
        headerJournalKey = key;
    }

    @Override
    public String getParameterJournalKey() {
        return parameterJournalKey;
    }

    @Override
    public void setParameterJournalKey(String key) {
        parameterJournalKey = key;
    }

    @Override
    public void setRequestMessageJournalKey(String key) {
        requestMessageJournalKey = key;
    }

    @Override
    public String getRequestMessageJournalKey() {
        return requestMessageJournalKey;
    }

    @Override
    public void setCloseReasonJournalKey(String key) {
        closeReasonJournalKey = key;
    }

    @Override
    public String getCloseReasonJournalKey() {
        return closeReasonJournalKey;
    }

    @Override
    public void setAuthResultJournalKey(String key) {
        authResultJournalKey = key;
    }

    @Override
    public String getAuthResultJournalKey() {
        return authResultJournalKey;
    }

    @Override
    public void setExceptionJournalKey(String key) {
        exceptionJournalKey = key;
    }

    @Override
    public String getExceptionJournalKey() {
        return exceptionJournalKey;
    }

    @Override
    public int getMaxClientSize() {
        return maxClientSize;
    }

    @Override
    public void setMaxClientSize(int size) {
        this.maxClientSize = size;
    }

    @Override
    public long getMaxIdleTimeout() {
        return maxIdleTimeout;
    }

    @Override
    public void setMaxIdleTimeout(long time) {
        maxIdleTimeout = time;
    }

    @Override
    public int getMaxTextMessageBufferSize() {
        return maxTextMessageBufferSize;
    }

    @Override
    public void setMaxTextMessageBufferSize(int size) {
        maxTextMessageBufferSize = size;
    }

    @Override
    public int getMaxBinaryMessageBufferSize() {
        return maxBinaryMessageBufferSize;
    }

    @Override
    public void setMaxBinaryMessageBufferSize(int size) {
        maxBinaryMessageBufferSize = size;
    }

    @Override
    public String getIllegalRequestMessageId() {
        return illegalRequestMessageId;
    }

    @Override
    public void setIllegalRequestMessageId(String id) {
        illegalRequestMessageId = id;
    }

    @Override
    public String getMaxClientSizeOverMessageId() {
        return maxClientSizeOverMessageId;
    }

    @Override
    public void setMaxClientSizeOverMessageId(String id) {
        maxClientSizeOverMessageId = id;
    }

    @Override
    public String getAbnormalCloseMessageId() {
        return abnormalCloseMessageId;
    }

    @Override
    public void setAbnormalCloseMessageId(String id) {
        abnormalCloseMessageId = id;
    }

    @Override
    public int getClientSize() {
        return sessionSet.size();
    }

    @Override
    public String getAllClientSessionProperties() {
        Set result = new HashSet();
        synchronized (sessionSet) {
            Iterator it = sessionSet.iterator();
            while (it.hasNext()) {
                Session session = (Session) it.next();
                result.add(SessionProperties.getSessionProperty(session));
            }
        }
        return result.toString();
    }

    @Override
    public Set findClientSessionPropertiesFromSessionId(String sessionId) {
        Set result = new HashSet();
        if (sessionId != null) {
            synchronized (sessionSet) {
                Iterator it = sessionSet.iterator();
                while (it.hasNext()) {
                    Session session = (Session) it.next();
                    SessionProperties prop = SessionProperties.getSessionProperty(session);
                    if (sessionId.equals(prop.getWebSocketSessionId())) {
                        result.add(prop);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void closeClientSessionFromSessionId(String id) {
        if (id != null) {
            synchronized (sessionSet) {
                Iterator it = sessionSet.iterator();
                while (it.hasNext()) {
                    Session session = (Session) it.next();
                    SessionProperties prop = SessionProperties.getSessionProperty(session);
                    if (id.equals(prop.getWebSocketSessionId())) {
                        CloseReason reason = new CustomCloseReason(CustomCloseReason.CloseCodes.SYSTEM_FORCED_DISCONNECTION, "Forced disconnection");
                        try {
                            session.close(reason);
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }

    @Override
    public Set findClientSessionPropertiesFromIp(String ip) {
        Set result = new HashSet();
        if (ip != null) {
            synchronized (sessionSet) {
                Iterator it = sessionSet.iterator();
                while (it.hasNext()) {
                    Session session = (Session) it.next();
                    SessionProperties prop = SessionProperties.getSessionProperty(session);
                    if (ip.equals(prop.getIp())) {
                        result.add(prop);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void closeClientSessionFromIp(String ip) {
        if (ip != null) {
            synchronized (sessionSet) {
                Iterator it = sessionSet.iterator();
                while (it.hasNext()) {
                    Session session = (Session) it.next();
                    SessionProperties prop = SessionProperties.getSessionProperty(session);
                    if (ip.equals(prop.getIp())) {
                        CloseReason reason = new CustomCloseReason(CustomCloseReason.CloseCodes.SYSTEM_FORCED_DISCONNECTION, "Forced disconnection");
                        try {
                            session.close(reason);
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }

    @Override
    public Set findClientSessionPropertiesFromId(String id) {
        Set result = new HashSet();
        if (id != null) {
            synchronized (sessionSet) {
                Iterator it = sessionSet.iterator();
                while (it.hasNext()) {
                    Session session = (Session) it.next();
                    SessionProperties prop = SessionProperties.getSessionProperty(session);
                    if (id.equals(prop.getId())) {
                        result.add(prop);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void closeClientSessionFromId(String id) {
        if (id != null) {
            synchronized (sessionSet) {
                Iterator it = sessionSet.iterator();
                while (it.hasNext()) {
                    Session session = (Session) it.next();
                    SessionProperties prop = SessionProperties.getSessionProperty(session);
                    if (id.equals(prop.getId())) {
                        CloseReason reason = new CustomCloseReason(CustomCloseReason.CloseCodes.SYSTEM_FORCED_DISCONNECTION, "Forced disconnection");
                        try {
                            session.close(reason);
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }

    @Override
    public void setServiceBase(ServiceBase service) {
        this.service = service;
    }

    @Override
    public void createService() throws Exception {
        sessionSet = new HashSet();
    }

    @Override
    public void startService() throws Exception {
        if (messageHandlerServiceNames == null || messageHandlerServiceNames.length == 0) {
            throw new IllegalArgumentException("MessageHandlerServiceNames is null or empty.");
        }
        if (authenticatorServiceName != null) {
            authenticator = (Authenticator) ServiceManagerFactory.getServiceObject(authenticatorServiceName);
        }
        if (exceptionHandlerMappingServiceName != null) {
            exceptionHandler = (ExceptionHandlerMappingService) ServiceManagerFactory.getServiceObject(exceptionHandlerMappingServiceName);
        }
        if (webSocketAccessJournalServiceName != null) {
            accessJournal = (Journal) ServiceManagerFactory.getServiceObject(webSocketAccessJournalServiceName);
            if (editorFinderServiceName != null) {
                editorFinder = (EditorFinder) ServiceManagerFactory.getServiceObject(editorFinderServiceName);
            }
            if (sequenceServiceName != null) {
                sequence = (Sequence) ServiceManagerFactory.getServiceObject(sequenceServiceName);
            } else {
                throw new IllegalArgumentException("SequenceServiceName is null.");
            }
        }
    }

    @Override
    public void stopService() throws Exception {
        if(sessionSet != null){
            Iterator it = sessionSet.iterator();
            while (it.hasNext()) {
                Session session = (Session) it.next();
                if(session.isOpen()){
                    session.close();
                }
            }
        }
    }

    @Override
    public void destroyService() throws Exception {
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        try {
            SessionProperties prop = (SessionProperties) config.getUserProperties().get(SessionProperties.SESSION_PROPERTY_KEY);
            prop.setWebSocketSessionId(session.getId());
            // Journal出力
            if (accessJournal != null) {
                accessJournal.startJournal(accessJournalKey, editorFinder);
                accessJournal.setRequestId(sequence.increment());
                accessJournal.addInfo(idJournalKey, prop.getId());
                accessJournal.addInfo(ticketJournalKey, prop.getTicket());
                accessJournal.addInfo(webSocketSessionIdJournalKey, prop.getWebSocketSessionId());
                accessJournal.addInfo(httpSessionIdJournalKey, prop.getHttpSessionId());
                accessJournal.addInfo(pathJournalKey, prop.getPath());
                accessJournal.addInfo(ipJournalKey, prop.getIp());
                accessJournal.addInfo(portJournalKey, prop.getPort());
                accessJournal.addInfo(headerJournalKey, prop.getHeaders());
                accessJournal.addInfo(parameterJournalKey, prop.getParameterMap());
            }
            // IDとチケットは必須
            if (prop.getId() == null && prop.getTicket() == null) {
                CloseReason reason = new CustomCloseReason(CustomCloseReason.CloseCodes.SERVER_ACCESS_DENIED, "Server access denied");
                try {
                    session.close(reason);
                } catch (IOException e) {
                }
                service.getLogger().write(illegalRequestMessageId, prop.getIp());
                return;
            }
            // クライアント数チェック
            if (maxClientSize != -1 && sessionSet.size() >= maxClientSize) {
                CloseReason reason = new CustomCloseReason(CustomCloseReason.CloseCodes.MAX_CLIENT_SIZE_OVER, "MaxClientSizeOver");
                try {
                    session.close(reason);
                } catch (Exception e) {
                }
                service.getLogger().write(maxClientSizeOverMessageId, prop);
                return;
            }
            // 認証処理
            if (authenticator != null) {
                boolean result = false;
                try {
                    result = authenticator.handshake(prop.getId(), prop.getTicket());
                } catch (AuthenticateException e) {
                    handleException(session, e);
                }
                if (!result) {
                    CloseReason reason = new CustomCloseReason(CustomCloseReason.CloseCodes.HANDSHAKE_AUTH_FAILED, "handshakeAuthFailed");
                    try {
                        session.close(reason);
                    } catch (Exception e) {
                    }
                    return;
                }
            }

            SessionProperties.put(session, prop);

            if (maxIdleTimeout != -1) {
                session.setMaxIdleTimeout(maxIdleTimeout);
            }
            if (maxTextMessageBufferSize != -1) {
                session.setMaxTextMessageBufferSize(maxTextMessageBufferSize);

            }
            if (maxBinaryMessageBufferSize != -1) {
                session.setMaxBinaryMessageBufferSize(maxBinaryMessageBufferSize);
            }

            for (ServiceName handlerName : messageHandlerServiceNames) {
                Object service = ServiceManagerFactory.getServiceObject(handlerName);
                if (service instanceof MessageHandler) {
                    session.addMessageHandler((MessageHandler) service);
                }
                if (service instanceof SessionMessageHandler) {
                    ((SessionMessageHandler) service).onOpen(session, config);
                }
            }
            synchronized (sessionSet) {
                sessionSet.add(session);
            }
        } finally {
            if (accessJournal != null && accessJournal.isStartJournal()) {
                accessJournal.endJournal();
            }
        }
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        try {
            if (accessJournal != null) {
                SessionProperties prop = SessionProperties.getSessionProperty(session);
                accessJournal.startJournal(accessJournalKey, editorFinder);
                accessJournal.setRequestId(sequence.increment());
                if (prop != null) {
                    accessJournal.addInfo(idJournalKey, prop.getId());
                    accessJournal.addInfo(ticketJournalKey, prop.getTicket());
                    accessJournal.addInfo(webSocketSessionIdJournalKey, prop.getWebSocketSessionId());
                    accessJournal.addInfo(httpSessionIdJournalKey, prop.getHttpSessionId());
                    accessJournal.addInfo(pathJournalKey, prop.getPath());
                    accessJournal.addInfo(ipJournalKey, prop.getIp());
                    accessJournal.addInfo(portJournalKey, prop.getPort());
                }
                accessJournal.addInfo(closeReasonJournalKey, closeReason);
            }
            Set messageHandlers = session.getMessageHandlers();
            Iterator itr = messageHandlers.iterator();
            while (itr.hasNext()) {
                Object handler = itr.next();
                if (handler instanceof SessionMessageHandler) {
                    ((SessionMessageHandler) handler).onClose(session, closeReason);
                }
            }
            synchronized (sessionSet) {
                sessionSet.remove(session);
            }
            boolean isNormalClose = closeReason.getCloseCode().getCode() == CloseReason.CloseCodes.NORMAL_CLOSURE.getCode();
            if (authenticator != null && closeReason.getCloseCode().getCode() != CustomCloseReason.CloseCodes.HANDSHAKE_AUTH_FAILED.getCode()) {
                SessionProperties prop = SessionProperties.getSessionProperty(session);
                if (prop != null) {
                    String id = prop.getId();
                    String ticket = prop.getTicket();
                    try {
                        authenticator.logout(id, ticket, !isNormalClose);
                    } catch (AuthenticateException e) {
                        handleException(session, e);
                    }
                }
            }
            if (!isNormalClose) {
                service.getLogger().write(abnormalCloseMessageId, new Object[] { SessionProperties.getSessionProperty(session), closeReason });
            }
        } finally {
            if (accessJournal != null && accessJournal.isStartJournal()) {
                accessJournal.endJournal();
            }
        }
    }

    @Override
    public void onError(Session session, Throwable thr) {
        Set messageHandlers = session.getMessageHandlers();
        Iterator itr = messageHandlers.iterator();
        while (itr.hasNext()) {
            Object handler = itr.next();
            if (handler instanceof SessionMessageHandler) {
                ((SessionMessageHandler) handler).onError(session, thr);
            }
        }
        handleException(session, thr);
    }

    protected void handleException(Session session, Throwable thr) {
        if (exceptionHandler != null) {
            try {
                exceptionHandler.handleException(session, thr);
            } catch (Throwable e) {
            }
        }
    }

    public Comparable getUsage() {
        return new Double((double) getClientSize() / (double) getMaxClientSize());
    }

}
