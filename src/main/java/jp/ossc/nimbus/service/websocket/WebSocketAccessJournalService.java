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

import java.util.Map;

import javax.websocket.CloseReason;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.journal.Journal;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;
import jp.ossc.nimbus.service.sequence.Sequence;

/**
 * WebSocketを使ったメッセージハンドラファクトリサービスクラス。
 * <p>
 *
 * @author M.Ishida
 */
public class WebSocketAccessJournalService extends ServiceBase implements WebSocketAccessJournalServiceMBean {

    protected ServiceName journalServiceName;
    protected ServiceName requestEditorFinderServiceName;
    protected ServiceName sequenceServiceName;

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
    protected String authResultJournalKey = DEFAULT_CLOSE_REASON_JOURNAL_KEY;
    protected String exceptionJournalKey = DEFAULT_EXCEPTION_JOURNAL_KEY;

    protected Journal journal;
    protected EditorFinder editorFinder;
    protected Sequence sequence;

    @Override
    public void setJournalServiceName(ServiceName name) {
        journalServiceName = name;
    }

    @Override
    public ServiceName getJournalServiceName() {
        return journalServiceName;
    }

    @Override
    public void setEditorFinderServiceName(ServiceName name) {
        requestEditorFinderServiceName = name;
    }

    @Override
    public ServiceName getEditorFinderServiceName() {
        return requestEditorFinderServiceName;
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
    public void setSequenceServiceName(ServiceName name) {
        sequenceServiceName = name;
    }

    @Override
    public ServiceName getSequenceServiceName() {
        return sequenceServiceName;
    }

    @Override
    public void startService() throws Exception {
        if (journalServiceName != null) {
            journal = (Journal) ServiceManagerFactory.getServiceObject(journalServiceName);
            if (requestEditorFinderServiceName != null) {
                editorFinder = (EditorFinder) ServiceManagerFactory.getServiceObject(requestEditorFinderServiceName);
            }
            if (sequenceServiceName != null) {
                sequence = (Sequence) ServiceManagerFactory.getServiceObject(sequenceServiceName);
            } else {
                throw new IllegalArgumentException("SequenceServiceName is null.");
            }
        }
    }

    public void startJournal() {
        journal.startJournal(accessJournalKey, editorFinder);
        journal.setRequestId(sequence.increment());
    }

    public void endJournal() {
        journal.endJournal();
    }

    public boolean isStartJournal() {
        return journal.isStartJournal();
    }

    public void addIdInfo(String id) {
        journal.addInfo(idJournalKey, id);
    }

    public void addTicketInfo(String ticket) {
        journal.addInfo(ticketJournalKey, ticket);
    }

    public void addWebSocketSessionIdInfo(String webSocketSessionId) {
        journal.addInfo(webSocketSessionIdJournalKey, webSocketSessionId);
    }

    public void addHttpSessionIdInfo(String HttpSessionId) {
        journal.addInfo(httpSessionIdJournalKey, HttpSessionId);
    }

    public void addPathInfo(String path) {
        journal.addInfo(pathJournalKey, path);
    }

    public void addIpInfo(String ip) {
        journal.addInfo(ipJournalKey, ip);
    }

    public void addPortInfo(String port) {
        journal.addInfo(portJournalKey, port);
    }

    public void addHeaderInfo(Map header) {
        journal.addInfo(headerJournalKey, header.toString());
    }

    public void addParameterInfo(Map parameter) {
        journal.addInfo(parameterJournalKey, parameter.toString());
    }

    public void addRequestMessageInfo(String requestMessage) {
        journal.addInfo(requestMessageJournalKey, requestMessage);
    }

    public void addCloseReasonInfo(CloseReason closeReason) {
        journal.addInfo(closeReasonJournalKey, closeReason.toString());
    }

    public void addAuthResultInfo(AuthResult authResult) {
        journal.addInfo(authResultJournalKey, authResult.toString());
    }

    public void addExceptionMessageInfo(Exception e) {
        journal.addInfo(exceptionJournalKey, e);
    }

}
