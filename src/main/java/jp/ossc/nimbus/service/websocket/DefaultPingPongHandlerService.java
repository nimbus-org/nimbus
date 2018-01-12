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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.PongMessage;
import javax.websocket.Session;

import jp.ossc.nimbus.core.Service;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceFactoryServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.daemon.Daemon;
import jp.ossc.nimbus.daemon.DaemonControl;
import jp.ossc.nimbus.daemon.DaemonRunnable;
import jp.ossc.nimbus.service.queue.QueueHandler;
import jp.ossc.nimbus.service.queue.QueueHandlerContainer;
import jp.ossc.nimbus.service.queue.QueueHandlerContainerService;

/**
 * Ping/Pongメッセージの送受信を行うサービスのFactoryサービスクラス。
 * <p>
 *
 * @author M.Ishida
 */
public class DefaultPingPongHandlerService extends ServiceFactoryServiceBase implements DaemonRunnable, DefaultPingPongHandlerServiceMBean {

    protected ServiceName pingSendQueueHandlerContainerServiceName;

    protected int queueHandlerSize = DEFAULT_QUEUE_SIZE;
    protected String pingMessage = DEFAULT_PING_MESSAGE;
    protected long pingSendInterval = DEFAULT_PING_SEND_INTERVAL;
    protected String pingSendErrorMessageId = DEFAULT_PING_SEND_ERROR_MESSAGE_ID;
    protected boolean isAllowNoPong = DEFAULT_ALLOW_NO_PONG;

    protected QueueHandlerContainer queue;
    protected Daemon daemon;
    protected Set sessionSet;
    protected ByteBuffer pingByteBuffer;

    protected Object lock = new String();

    @Override
    public ServiceName getPingSendQueueHandlerContainerServiceName() {
        return pingSendQueueHandlerContainerServiceName;
    }

    @Override
    public void setPingSendQueueHandlerContainerServiceName(ServiceName name) {
        pingSendQueueHandlerContainerServiceName = name;
    }

    @Override
    public int getQueueHandlerSize() {
        return queueHandlerSize;
    }

    @Override
    public void setQueueHandlerSize(int size) {
        queueHandlerSize = size;
    }

    @Override
    public String getPingMessage() {
        return pingMessage;
    }

    @Override
    public void setPingMessage(String message) {
        pingMessage = message;

    }

    @Override
    public long getPingSendInterval() {
        return pingSendInterval;
    }

    @Override
    public void setPingSendInterval(long interval) {
        pingSendInterval = interval;
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
    public void createService() throws Exception {
        sessionSet = new HashSet();
        daemon = new Daemon(this);
        daemon.setName("Nimbus WebSocket PingSendAndPongCheckDaemon " + getServiceNameObject());
    }

    @Override
    public void startService() throws Exception {
        if (pingSendQueueHandlerContainerServiceName != null) {
            queue = (QueueHandlerContainer) ServiceManagerFactory.getServiceObject(pingSendQueueHandlerContainerServiceName);
        } else {
            QueueHandlerContainerService qhc = new QueueHandlerContainerService();
            qhc.setQueueHandlerSize(queueHandlerSize);
            qhc.setMaxRetryCount(3);
            queue = qhc;
        }
        pingByteBuffer = ByteBuffer.wrap(pingMessage.getBytes());
        queue.setQueueHandler(new PingSendQueueHandler());
        queue.accept();
        daemon.start();
    }

    @Override
    public void stopService() throws Exception {
        daemon.stop();
        if (queue != null) {
            queue.release();
            queue.stop();
        }
    }

    @Override
    public void destroyService() throws Exception {
        daemon = null;
        if (queue != null) {
            queue = null;
        }
    }

    @Override
    protected Service createServiceInstance() throws Exception {
        return new DefaultKeepAliveHandlerService();
    }

    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public boolean onStop() {
        return true;
    }

    @Override
    public boolean onSuspend() {
        return true;
    }

    @Override
    public boolean onResume() {
        return true;
    }

    @Override
    public Object provide(DaemonControl ctrl) throws Throwable {
        if (getState() != STARTED) {
            return null;
        }
        try {
            ctrl.sleep(pingSendInterval, false);
        } catch (InterruptedException e) {

        }
        List list = new ArrayList();
        Set tempSet;
        synchronized (lock) {
            tempSet = new HashSet(sessionSet);
        }
        Iterator itr = tempSet.iterator();
        while (itr.hasNext()) {
            Session session = (Session) itr.next();
            if (session != null && session.isOpen()) {
                SessionProperties prop = SessionProperties.getSessionProperty(session);
                if (!isPongReceive(prop)) {
                    CloseReason reason = new CloseReason(CloseReason.CloseCodes.GOING_AWAY, "PongReceiveCheck");
                    try {
                        if (session.isOpen()) {
                            session.close(reason);
                        }
                    } catch (Exception e) {
                    }
                    continue;
                }
                if (prop != null) {
                    if (prop.getPingRequestTime() != -1) {
                        if (prop.getPingSendTime() != -1) {
                            if (prop.getPingRequestTime() > prop.getPingSendTime()) {
                                continue;
                            }
                        } else {
                            continue;
                        }
                    }
                    prop.setPingRequestTime(System.currentTimeMillis());
                    list.add(session);
                }
            }
        }
        return list;
    }

    @Override
    public void consume(Object paramObj, DaemonControl ctrl) throws Throwable {
        if (paramObj != null) {
            List list = (List) paramObj;
            for (int i = 0; i < list.size(); i++) {
                queue.push(list.get(i));
            }
        }
    }

    @Override
    public void garbage() {
    }

    /**
     * SessionをPing/Pong管理対象に追加する。
     *
     * @param session WebSocketセッション
     */
    protected void regist(Session session) {
        synchronized (lock) {
            sessionSet.add(session);
        }
    }

    /**
     * SessionをPing/Pong管理対象から除外する。
     *
     * @param session WebSocketセッション
     */
    protected void unregist(Session session) {
        synchronized (lock) {
            sessionSet.remove(session);
        }
    }

    private boolean isPongReceive(SessionProperties prop) {
        if (isAllowNoPong) {
            return true;
        }
        if (prop != null && prop.getPingSendTime() != -1 && (prop.getPingSendTime() > prop.getPongReceiveTime())) {
            return false;
        }
        return true;

    }

    public class DefaultKeepAliveHandlerService extends ServiceBase implements SessionMessageHandler, MessageHandler.Whole<PongMessage> {

        /**
         * WebsocketのSession。
         * <p>
         */
        protected Session session;

        @Override
        public void onMessage(PongMessage message) {
            SessionProperties.getSessionProperty(session).setPongReceiveTime(System.currentTimeMillis());
        }

        @Override
        public void onOpen(Session session, EndpointConfig config) {
            this.session = session;
            regist(session);
        }

        @Override
        public void onClose(Session session, CloseReason closeReason) {
            unregist(session);
            try {
                super.stopService();
            } catch (Exception e) {
            }
        }

        @Override
        public void onError(Session session, Throwable thr) {
        }
    }

    /**
     * Pingメッセージを配信する際に使用するQueueHandlerクラス。
     */
    protected class PingSendQueueHandler implements QueueHandler {
        @Override
        public void handleDequeuedObject(Object obj) throws Throwable {
            if (obj == null) {
                return;
            }
            Session session = (Session) obj;
            if (session.isOpen()) {
                SessionProperties.getSessionProperty(session).setPingSendTime(System.currentTimeMillis());
                session.getBasicRemote().sendPing(pingByteBuffer);
            }
        }

        @Override
        public boolean handleError(Object obj, Throwable th) throws Throwable {
            Session session = (Session) obj;
            return session.isOpen();
        }

        @Override
        public void handleRetryOver(Object obj, Throwable th) throws Throwable {
            Session session = (Session) obj;
            if (session.isOpen()) {
                getLogger().write(pingSendErrorMessageId, SessionProperties.getSessionProperty(session), th);
            }
        }
    }

}
