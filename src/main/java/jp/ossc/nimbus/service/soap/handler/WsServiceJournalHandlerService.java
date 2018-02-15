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
package jp.ossc.nimbus.service.soap.handler;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.service.journal.Journal;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;
import jp.ossc.nimbus.service.sequence.Sequence;

/**
 * Webサービスジャーナルハンドラサービス。
 * <p>
 *
 * @author M.Ishida
 */
public class WsServiceJournalHandlerService extends ServiceBase implements SOAPHandler<SOAPMessageContext>,
        WsServiceJournalHandlerServiceMBean {

    protected ServiceName journalServiceName;
    protected ServiceName sequenceServiceName;
    protected ServiceName threadContextServiceName;
    protected ServiceName accessEditorFinderServiceName;
    protected ServiceName requestEditorFinderServiceName;
    protected ServiceName responseEditorFinderServiceName;

    protected String accessJournalKey = DEFAULT_ACCESS_JOURNAL_KEY;
    protected String requestJournalKey = DEFAULT_REQUEST_JOURNAL_KEY;
    protected String responseJournalKey = DEFAULT_RESPONSE_JOURNAL_KEY;

    protected String requestSoapMessageContextJournalKey = DEFAULT_REQUEST_SOAP_MESSAGE_CONTEXT_JOURNAL_KEY;
    protected String requestPartJournalKey = DEFAULT_REQUEST_PART_JOURNAL_KEY;

    protected String responseSoapMessageContextJournalKey = DEFAULT_REQUEST_SOAP_MESSAGE_CONTEXT_JOURNAL_KEY;
    protected String responsePartJournalKey = DEFAULT_RESPONSE_PART_JOURNAL_KEY;

    protected String faultJournalKey = DEFAULT_FAULT_JOURNAL_KEY;

    protected String requestIdKey = DEFAULT_REQUEST_ID_KEY;

    protected Journal journal;
    protected Sequence sequence;
    protected Context threadContext;
    protected EditorFinder accessEditorFinder;
    protected EditorFinder requestEditorFinder;
    protected EditorFinder responseEditorFinder;

    public ServiceName getJournalServiceName() {
        return journalServiceName;
    }

    public void setJournalServiceName(ServiceName name) {
        journalServiceName = name;
    }

    public ServiceName getSequenceServiceName() {
        return sequenceServiceName;
    }

    public void setSequenceServiceName(ServiceName name) {
        sequenceServiceName = name;
    }

    public ServiceName getThreadContextServiceName() {
        return threadContextServiceName;
    }

    public void setThreadContextServiceName(ServiceName name) {
        threadContextServiceName = name;
    }

    public ServiceName getAccessEditorFinderServiceName() {
        return accessEditorFinderServiceName;
    }

    public void setAccessEditorFinderServiceName(ServiceName name) {
        accessEditorFinderServiceName = name;
    }

    public ServiceName getRequestEditorFinderServiceName() {
        return requestEditorFinderServiceName;
    }

    public void setRequestEditorFinderServiceName(ServiceName name) {
        requestEditorFinderServiceName = name;
    }

    public ServiceName getResponseEditorFinderServiceName() {
        return responseEditorFinderServiceName;
    }

    public void setResponseEditorFinderServiceName(ServiceName name) {
        responseEditorFinderServiceName = name;
    }

    public String getAccessJournalKey() {
        return accessJournalKey;
    }

    public void setAccessJournalKey(String key) {
        accessJournalKey = key;
    }

    public String getRequestJournalKey() {
        return requestJournalKey;
    }

    public void setRequestJournalKey(String key) {
        requestJournalKey = key;
    }

    public String getResponseJournalKey() {
        return responseJournalKey;
    }

    public void setResponseJournalKey(String key) {
        responseJournalKey = key;
    }

    public String getRequestSoapMessageContextJournalKey() {
        return requestSoapMessageContextJournalKey;
    }

    public void setRequestSoapMessageContextJournalKey(String key) {
        requestSoapMessageContextJournalKey = key;
    }

    public String getResponseSoapMessageContextJournalKey() {
        return responseSoapMessageContextJournalKey;
    }

    public void setResponseSoapMessageContextJournalKey(String key) {
        responseSoapMessageContextJournalKey = key;
    }

    public String getFaultJournalKey() {
        return faultJournalKey;
    }

    public void setFaultJournalKey(String key) {
        faultJournalKey = key;
    }

    public String getRequestIdKey() {
        return requestIdKey;
    }

    public void setRequestIdKey(String key) {
        requestIdKey = key;
    }

    public void startService() throws Exception {

        if (journalServiceName == null) {
            throw new IllegalArgumentException("JournalServiceName must be specified.");
        }
        journal = (Journal) ServiceManagerFactory.getServiceObject(journalServiceName);

        if (accessEditorFinderServiceName != null) {
            accessEditorFinder = (EditorFinder) ServiceManagerFactory.getServiceObject(accessEditorFinderServiceName);
        }

        if (requestEditorFinderServiceName != null) {
            requestEditorFinder = (EditorFinder) ServiceManagerFactory.getServiceObject(requestEditorFinderServiceName);
        }

        if (responseEditorFinderServiceName != null) {
            responseEditorFinder = (EditorFinder) ServiceManagerFactory
                    .getServiceObject(responseEditorFinderServiceName);
        }

        if (sequenceServiceName != null) {
            sequence = (Sequence) ServiceManagerFactory.getServiceObject(sequenceServiceName);
        }

        if (threadContextServiceName != null) {
            threadContext = (Context) ServiceManagerFactory.getServiceObject(threadContextServiceName);
        }

    }

    public boolean handleMessage(SOAPMessageContext context) {
        Boolean outboundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (outboundProperty.booleanValue()) {
            startJournal(context);
        } else {
            endJournal(context);
        }
        return true;
    }

    public boolean handleFault(SOAPMessageContext context) {
        endJournal(context);
        return true;
    }

    public void close(MessageContext context) {
    }

    public Set<QName> getHeaders() {
        return null;
    }

    protected void startJournal(SOAPMessageContext context) {
        try {
            journal.startJournal(accessJournalKey, accessEditorFinder);
            if (sequence != null) {
                journal.setRequestId(sequence.increment());
            } else if (threadContext != null) {
                journal.setRequestId((String) threadContext.get(requestIdKey));
            }
            journal.addStartStep(requestJournalKey, requestEditorFinder);
            journal.addInfo(requestSoapMessageContextJournalKey, context);
            journal.addInfo(requestPartJournalKey, context.getMessage().getSOAPPart());
        } finally {
            if (journal.isStartJournal()) {
                journal.addEndStep();
            }
        }
    }

    protected void endJournal(SOAPMessageContext context) {
        if (journal.isStartJournal()) {
            try {
                try {
                    journal.addStartStep(responseJournalKey, responseEditorFinder);
                    journal.addInfo(responseSoapMessageContextJournalKey, context);
                    journal.addInfo(responsePartJournalKey, context.getMessage().getSOAPPart());
                    try {
                        if(context.getMessage().getSOAPBody().hasFault()){
                            journal.addInfo(faultJournalKey, context.getMessage().getSOAPBody().getFault());
                        }
                    } catch (SOAPException e) {
                    }
                } finally {
                    journal.addEndStep();
                }
            } finally {
                journal.endJournal();
            }
        }
    }
}
