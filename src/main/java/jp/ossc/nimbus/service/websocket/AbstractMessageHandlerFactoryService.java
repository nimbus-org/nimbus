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
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import jp.ossc.nimbus.core.Service;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceFactoryServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.journal.Journal;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;
import jp.ossc.nimbus.service.sequence.Sequence;

/**
 * WebSocketを使ったメッセージハンドラファクトリサービス抽象クラス。
 * <p>
 * Configuratorにて、Session毎のHandlerを生成する際に本ファクトリクラスにてインスタンスを生成する。<br>
 *
 * @author M.Ishida
 */
public abstract class AbstractMessageHandlerFactoryService extends ServiceFactoryServiceBase implements
        AbstractMessageHandlerFactoryServiceMBean {

    protected ServiceName webSocketAccessJournalServiceName;
    protected ServiceName editorFinderServiceName;
    protected ServiceName sequenceServiceName;

    protected String accessJournalKey = DEFAULT_ACCESS_JOURNAL_KEY;
    protected String idJournalKey = DEFAULT_ID_JOURNAL_KEY;
    protected String ticketJournalKey = DEFAULT_TICKET_JOURNAL_KEY;
    protected String webSocketSessionIdJournalKey = DEFAULT_WEBSOCKET_SESSION_ID_JOURNAL_KEY;
    protected String httpSessionIdJournalKey = DEFAULT_HTTP_SESSION_ID_JOURNAL_KEY;
    protected String pathJournalKey = DEFAULT_PATH_JOURNAL_KEY;
    protected String ipJournalKey = DEFAULT_IP_JOURNAL_KEY;
    protected String portJournalKey = DEFAULT_PORT_JOURNAL_KEY;
    protected String requestMessageJournalKey = DEFAULT_REQUEST_MESSAGE_JOURNAL_KEY;
    protected String pingSendErrorMessageId = DEFAULT_PING_SEND_ERROR_MESSAGE_ID;

    protected String clientPingMessage;
    protected String returnPongMessage;

    protected Journal accessJournal;
    protected EditorFinder editorFinder;
    protected Sequence sequence;

    protected String messageEncoding = DEFAULT_ENCODE;

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
    public void setRequestMessageJournalKey(String key) {
        requestMessageJournalKey = key;
    }

    @Override
    public String getRequestMessageJournalKey() {
        return requestMessageJournalKey;
    }

    @Override
    public String getMessageEncoding() {
        return messageEncoding;
    }

    @Override
    public void setMessageEncoding(String encoding) {
        this.messageEncoding = encoding;
    }

    @Override
    public String getClientPingMessage() {
        return clientPingMessage;
    }

    @Override
    public void setClientPingMessage(String message) {
        clientPingMessage = message;
    }

    @Override
    public String getReturnPongMessage() {
        return returnPongMessage;
    }

    @Override
    public void setReturnPongMessage(String message) {
        returnPongMessage = message;
    }

    @Override
    public String getPingSendErrorMessageId() {
        return pingSendErrorMessageId;
    }

    @Override
    public void setPingSendErrorMessageId(String messageId) {
        pingSendErrorMessageId = messageId;
    }

    @Override
    protected void preStartService() throws Exception {
        super.preStartService();
        if (!Charset.isSupported(messageEncoding)) {
            throw new IllegalArgumentException(messageEncoding + " is not supported .");
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
    protected abstract Service createServiceInstance() throws Exception;

    /**
     * Configuratorにて生成されるMessageHandler抽象クラス。
     * <p>
     * 受信したバイト配列を文字列に変換し、後続処理に渡す。<br>
     * 受信したメッセージのジャーナリングを行う。<br>
     *
     * @author m-ishida
     */
    public abstract class AbstractMessageHandlerService extends ServiceBase implements
            MessageHandler.Whole<ByteBuffer>, SessionMessageHandler {

        protected Session session;

        @Override
        public void onOpen(Session session, EndpointConfig config) {
            this.session = session;
            onOpenProcess(session, config);
        }

        @Override
        public void onClose(Session session, CloseReason closeReason) {
            onCloseProcess(session, closeReason);
            try {
                super.stopService();
            } catch (Exception e) {
            }
        }

        @Override
        public void onError(Session session, Throwable thr) {
            onErrorProcess(session, thr);
        }

        @Override
        public void onMessage(ByteBuffer msg) {
            String message = null;
            if (accessJournal != null) {
                SessionProperties prop = SessionProperties.getSessionProperty(session);
                accessJournal.startJournal(accessJournalKey, editorFinder);
                accessJournal.setRequestId(sequence.increment());
                accessJournal.addInfo(idJournalKey, prop.getId());
                accessJournal.addInfo(ticketJournalKey, prop.getTicket());
                accessJournal.addInfo(webSocketSessionIdJournalKey, prop.getWebSocketSessionId());
                accessJournal.addInfo(httpSessionIdJournalKey, prop.getHttpSessionId());
                accessJournal.addInfo(pathJournalKey, prop.getPath());
                accessJournal.addInfo(ipJournalKey, prop.getIp());
                accessJournal.addInfo(portJournalKey, prop.getPort());
            }
            try {
                byte[] byteArray = new byte[msg.remaining()];
                msg.get(byteArray);
                message = new String(byteArray, messageEncoding);
                if (clientPingMessage != null && returnPongMessage != null && clientPingMessage.equals(message)) {
                    try {
                        session.getBasicRemote().sendBinary(
                                ByteBuffer.wrap(returnPongMessage.getBytes(messageEncoding)));
                    } catch (IOException e) {
                        if (session.isOpen()) {
                            getLogger().write(pingSendErrorMessageId, SessionProperties.getSessionProperty(session),
                                    e);
                        }
                    }
                    return;
                }
                onMessageProcess(message);
            } catch (UnsupportedEncodingException e) {
                // startServiceでチェックしているので起こらない。
            } finally {
                if (accessJournal != null) {
                    accessJournal.addInfo(requestMessageJournalKey, message);
                    accessJournal.endJournal();
                }
            }
        }

        /**
         * セッションオープン時のイベント処理。
         * <p>
         *
         * @param session WebSocketセッション
         * @param config EndpointConfig
         */
        protected abstract void onOpenProcess(Session session, EndpointConfig config);

        /**
         * セッションクローズ時のイベント処理。
         * <p>
         *
         * @param session WebSocketセッション
         * @param closeReason CloseReason
         */
        protected abstract void onCloseProcess(Session session, CloseReason closeReason);

        /**
         * エラー発生時のイベント処理。
         * <p>
         *
         * @param session WebSocketセッション
         * @param thr 発生した例外
         */
        protected abstract void onErrorProcess(Session session, Throwable thr);

        /**
         * クライアントからのメッセージ受信時のイベント処理。
         * <p>
         *
         * @param message メセージ文字列
         */
        protected abstract void onMessageProcess(String message);
    }

}
