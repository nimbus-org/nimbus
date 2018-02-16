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
package jp.ossc.nimbus.service.publish.websocket;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.queue.DistributedQueueHandlerContainerService;
import jp.ossc.nimbus.service.queue.DistributedQueueSelector;
import jp.ossc.nimbus.service.queue.QueueHandler;
import jp.ossc.nimbus.service.queue.QueueHandlerContainer;
import jp.ossc.nimbus.service.websocket.ExceptionHandlerMappingService;

/**
 * メッセージディスパッチャーサービス抽象クラス。 受信した配信メッセージを配信が必要なメッセージ送信対象に送信する。
 * <p>
 *
 * @author M.Ishida
 */
public abstract class AbstractPublishMessageDispatcherService extends ServiceBase implements MessageDispatcher,
        AbstractPublishMessageDispatcherServiceMBean {

    protected ServiceName messageListenerQueueHandlerContainerServiceName;
    protected ServiceName messageListenerQueueSelectorServiceName;
    protected ServiceName messageSendQueueHandlerContainerServiceName;
    protected ServiceName messageSendQueueSelectorServiceName;
    protected ServiceName messageSendExceptionHandlerMappingServiceName;

    protected String sendErrorMessageId = DEFAULT_SEND_ERROR_MESSAGE_ID;
    protected int messageSendParameterRecycleListSize = DEFAULT_MESSAGE_SEND_PARAMETER_RECYCLE_LIST_SIZE;

    protected QueueHandlerContainer messageListenerQueueHandlerContainer;
    protected DistributedQueueHandlerContainerService messageSendQueueHandlerContainer;
    protected DistributedQueueSelector messageSendDistributedQueueSelector;
    protected ExceptionHandlerMappingService messageSendExceptionHandler;

    protected List messageSendParameterRecycleList;

    protected long messageSendCount;

    @Override
    public ServiceName getMessageListenerQueueHandlerContainerServiceName() {
        return messageListenerQueueHandlerContainerServiceName;
    }

    @Override
    public void setMessageListenerQueueHandlerContainerServiceName(ServiceName name) {
        this.messageListenerQueueHandlerContainerServiceName = name;
    }

    @Override
    public ServiceName getMessageSendQueueHandlerContainerServiceName() {
        return messageSendQueueHandlerContainerServiceName;
    }

    @Override
    public void setMessageSendQueueHandlerContainerServiceName(ServiceName name) {
        messageSendQueueHandlerContainerServiceName = name;
    }

    @Override
    public ServiceName getMessageListenerQueueSelectorServiceName() {
        return messageListenerQueueSelectorServiceName;
    }

    @Override
    public void setMessageListenerQueueSelectorServiceName(ServiceName name) {
        messageListenerQueueSelectorServiceName = name;
    }

    @Override
    public ServiceName getMessageSendExceptionHandlerMappingServiceName() {
        return messageSendExceptionHandlerMappingServiceName;
    }

    @Override
    public void setMessageSendExceptionHandlerMappingServiceName(ServiceName name) {
        this.messageSendExceptionHandlerMappingServiceName = name;
    }

    @Override
    public ServiceName getMessageSendQueueSelectorServiceName() {
        return messageSendQueueSelectorServiceName;
    }

    @Override
    public void setMessageSendQueueSelectorServiceName(ServiceName name) {
        messageSendQueueSelectorServiceName = name;
    }

    @Override
    public String getSendErrorMessageId() {
        return sendErrorMessageId;
    }

    @Override
    public void setSendErrorMessageId(String messageId) {
        sendErrorMessageId = messageId;
    }

    @Override
    public int getMessageSendParameterRecycleListSize() {
        return messageSendParameterRecycleListSize;
    }

    @Override
    public void setMessageSendParameterRecycleListSize(int size) {
        messageSendParameterRecycleListSize = size;
    }

    @Override
    public long getMessageSendCount() {
        return messageSendCount;
    }

    @Override
    protected void preCreateService() throws Exception {
        super.preCreateService();
        messageSendParameterRecycleList = new ArrayList();
    }

    @Override
    protected void preStartService() throws Exception {
        super.preStartService();
        if (messageListenerQueueHandlerContainerServiceName != null) {
            messageListenerQueueHandlerContainer = (QueueHandlerContainer) ServiceManagerFactory
                    .getServiceObject(messageListenerQueueHandlerContainerServiceName);
        } else {
            if (messageListenerQueueSelectorServiceName != null) {
                messageListenerQueueHandlerContainer = new DistributedQueueHandlerContainerService();
                ((DistributedQueueHandlerContainerService) messageListenerQueueHandlerContainer).create();
                ((DistributedQueueHandlerContainerService) messageListenerQueueHandlerContainer)
                .setDistributedQueueSelectorServiceName(messageListenerQueueSelectorServiceName);
                ((DistributedQueueHandlerContainerService) messageListenerQueueHandlerContainer).start();
            }
        }
        if (messageListenerQueueHandlerContainer != null) {
            messageListenerQueueHandlerContainer.setQueueHandler(new MessageListenerQueueHandler());
        }
        if (messageSendQueueHandlerContainerServiceName != null) {
            messageSendQueueHandlerContainer = (DistributedQueueHandlerContainerService) ServiceManagerFactory
                    .getServiceObject(messageSendQueueHandlerContainerServiceName);

        } else {
            if (messageSendQueueSelectorServiceName != null) {
                messageSendQueueHandlerContainer = new DistributedQueueHandlerContainerService();
                messageSendQueueHandlerContainer.create();
                messageSendQueueHandlerContainer
                        .setDistributedQueueSelectorServiceName(messageSendQueueSelectorServiceName);
                messageSendQueueHandlerContainer.start();
            }
        }
        messageSendDistributedQueueSelector = (DistributedQueueSelector) ServiceManagerFactory
                .getServiceObject(messageSendQueueHandlerContainer.getDistributedQueueSelectorServiceName());
        if (messageSendQueueHandlerContainer != null) {
            messageSendQueueHandlerContainer.setQueueHandler(new MessageSendQueueHandler());
            messageSendQueueHandlerContainer.accept();
        }
        if (messageSendExceptionHandlerMappingServiceName != null) {
            messageSendExceptionHandler = (ExceptionHandlerMappingService) ServiceManagerFactory
                    .getServiceObject(messageSendExceptionHandlerMappingServiceName);
        }
    }

    @Override
    protected void postStopService() throws Exception {
        if (messageListenerQueueHandlerContainer != null) {
            messageListenerQueueHandlerContainer.stop();
        }
        if (messageSendQueueHandlerContainer != null) {
            messageSendQueueHandlerContainer.stop();
        }
        super.postStopService();
    }

    @Override
    protected void postDestroyService() throws Exception {
        messageListenerQueueHandlerContainer = null;
        messageSendQueueHandlerContainer = null;
        super.postDestroyService();
    }

    @Override
    public void addMessageSender(MessageSender sender) {
        addMessageSenderProcess(sender);
    }

    @Override
    public void removeMessageSender(MessageSender sender) {
        removeMessageSenderProcess(sender);
        if (messageSendDistributedQueueSelector instanceof SessionIdMessageSendDistributedQueueSelectorService) {
            ((SessionIdMessageSendDistributedQueueSelectorService) messageSendDistributedQueueSelector).remove(sender
                    .getSession().getId());
        }
    }

    @Override
    public void addKey(Object key, MessageSender sender) {
        addKeyProcess(key, sender);
    }

    @Override
    public void removeKey(Object key, MessageSender sender) {
        removeKeyProcess(key, sender);
    }

    /**
     * メッセージ送信対象を追加する
     *
     * @param sender メッセージ送信対象
     */
    public abstract void addMessageSenderProcess(MessageSender sender);

    /**
     * メッセージ送信対象を削除する
     *
     * @param sender メッセージ送信対象
     */
    public abstract void removeMessageSenderProcess(MessageSender sender);

    /**
     * 配信メッセージに対するキーオブジェクトに対してメッセージ送信対象を追加する
     *
     * @param key 配信メッセージに対するキーオブジェクト
     * @param sender メッセージ送信対象
     */
    public abstract void addKeyProcess(Object key, MessageSender sender);

    /**
     * 配信メッセージに対するキーオブジェクトに対してメッセージ送信対象を削除する
     *
     * @param key 配信メッセージに対するキーオブジェクト
     * @param sender メッセージ送信対象
     */
    public abstract void removeKeyProcess(Object key, MessageSender sender);

    /**
     * 配信メッセージの受信処理
     *
     * @param message メッセージ
     */
    protected void onMessageProcess(Object message) {
        if (message != null) {
            if (messageListenerQueueHandlerContainer == null) {
                Set<MessageSender> senders = getMessageSendTarget(message);
                if (senders != null && senders.size() != 0) {
                    sendMessageSenders(senders, message);
                }
            } else {
                messageListenerQueueHandlerContainer.push(message);
            }
        }
    }

    /**
     * 配信メッセージからメッセージ送信対象を取得する。
     *
     * @param message 配信メッセージ
     * @return メッセージ送信対象のList
     */
    protected abstract Set<MessageSender> getMessageSendTarget(Object message);

    /**
     * メッセージ送信対象にメッセージを送信する。 メッセージ送信のQueueHandlerContainerが存在する場合は、キューイングする。
     *
     * @param senders メッセージ送信対象
     * @param message 配信メッセージ
     */
    protected void sendMessageSenders(Set<MessageSender> senders, Object message) {
        for (MessageSender sender:senders) {
            if (messageSendQueueHandlerContainer == null) {
                sendMessage(sender, message);
            } else {
                messageSendQueueHandlerContainer.push(getSendParamObject(sender, message));
            }
        }
    }

    /**
     * メッセージ送信対象にメッセージを送信する。
     *
     * @param sender メッセージ送信対象
     * @param message 配信メッセージ
     */
    protected void sendMessage(MessageSender sender, Object message) {
        try {
            sender.sendMessage(message);
            messageSendCount++;
        } catch (Exception e) {
            if (messageSendExceptionHandler != null) {
                try {
                    messageSendExceptionHandler.handleException(sender.getSession(), e);
                } catch (Throwable thr) {
                }
            }
        }
    }

    /**
     * メッセージ配信を受信するためのQueueHandlerクラス。
     *
     * @author m-ishida
     */
    protected class MessageListenerQueueHandler implements QueueHandler {
        @Override
        public void handleDequeuedObject(Object message) throws Throwable {
            if (message == null) {
                return;
            }
            Set<MessageSender> senders = getMessageSendTarget(message);
            if (senders != null && senders.size() != 0) {
                sendMessageSenders(senders, message);
            }
        }

        @Override
        public boolean handleError(Object obj, Throwable th) throws Throwable {
            return false;
        }

        @Override
        public void handleRetryOver(Object obj, Throwable th) throws Throwable {
        }
    }

    /**
     * メッセージを送信するためのQueueHandlerクラス。
     *
     * @author m-ishida
     */
    protected class MessageSendQueueHandler implements QueueHandler {
        @Override
        public void handleDequeuedObject(Object obj) throws Throwable {
            if (obj == null) {
                return;
            }
            MessageSendParameter param = (MessageSendParameter) obj;
            sendMessage(param.getSender(), param.getMessage());
            recycleSendParamObject(param);
        }

        @Override
        public boolean handleError(Object obj, Throwable th) throws Throwable {
            MessageSendParameter param = (MessageSendParameter) obj;
            return param.getSender().getSession().isOpen();
        }

        @Override
        public void handleRetryOver(Object obj, Throwable th) throws Throwable {
            MessageSendParameter param = (MessageSendParameter) obj;
            recycleSendParamObject(param);
        }
    }

    protected MessageSendParameter getSendParamObject(MessageSender sender, Object message) {
        MessageSendParameter obj = null;
        if(messageSendParameterRecycleList.isEmpty()){
            obj = new MessageSendParameter();
        }else{
            synchronized(messageSendParameterRecycleList){
                if(messageSendParameterRecycleList.isEmpty()){
                    obj = new MessageSendParameter();
                }else{
                    obj = (MessageSendParameter) messageSendParameterRecycleList.remove(0);
                }
            }
        }
        obj.setSender(sender);
        obj.setMessage(message);
        return obj;
    }

    protected void recycleSendParamObject(MessageSendParameter param) {
        if (messageSendParameterRecycleList.size() < messageSendParameterRecycleListSize) {
            param.clear();
            synchronized(messageSendParameterRecycleList){
                messageSendParameterRecycleList.add(param);
            }
        }
    }

    public class MessageSendParameter {

        private MessageSender sender;
        private Object message;

        public void clear() {
            sender = null;
            message = null;
        }

        public MessageSender getSender() {
            return sender;
        }

        public void setSender(MessageSender sender) {
            this.sender = sender;
        }

        public Object getMessage() {
            return message;
        }

        public void setMessage(Object message) {
            this.message = message;
        }

    }

}
