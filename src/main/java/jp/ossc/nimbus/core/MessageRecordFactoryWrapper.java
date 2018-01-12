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
package jp.ossc.nimbus.core;

import java.util.*;
import java.io.Serializable;

import jp.ossc.nimbus.service.message.*;

/**
 * {@link MessageRecordFactory}ÉâÉbÉpÅB<p>
 *
 * @author M.Takata
 */
class MessageRecordFactoryWrapper
 implements MessageRecordFactory, ServiceStateListener, Serializable{

    private static final long serialVersionUID = 8434021957805850115L;

    private MessageRecordFactory defaultMessage;
    private MessageRecordFactory currentMessage;

    public MessageRecordFactoryWrapper(MessageRecordFactory defaultMessage){
        this(null, null, defaultMessage);
    }

    public MessageRecordFactoryWrapper(
        MessageRecordFactory message,
        Service messageService,
        MessageRecordFactory defaultMessage
    ){
        setDefaultMessageRecordFactory(defaultMessage);
        setMessageRecordFactory(message, messageService);
    }

    public void setDefaultMessageRecordFactory(MessageRecordFactory message){
        if(message == this){
            return;
        }
        this.defaultMessage = message;
    }

    public MessageRecordFactory getDefaultMessageRecordFactory(){
        return defaultMessage;
    }

    public void setMessageRecordFactory(MessageRecordFactory message){
        setMessageRecordFactory(message, null);
    }

    public void setMessageRecordFactory(
        MessageRecordFactory message,
        Service messageService
    ){
        if(message == this){
            return;
        }
        if(messageService != null){
            if(messageService.getState() == Service.STARTED){
                currentMessage = message;
            }else{
                currentMessage = defaultMessage;
            }
            try{
                final ServiceStateBroadcaster broadcaster
                    = ServiceManagerFactory.getServiceStateBroadcaster(
                        messageService.getServiceManagerName(),
                        messageService.getServiceName()
                    );
                if(broadcaster != null){
                    broadcaster.addServiceStateListener(this);
                }
            }catch(ServiceNotFoundException e){
            }
        }else{
            if(message != null){
                currentMessage = message;
            }else{
                currentMessage = defaultMessage;
            }
        }
    }

    public MessageRecordFactory getMessageRecordFactory(){
        return currentMessage;
    }

    public void stateChanged(ServiceStateChangeEvent e) throws Exception{
        final Service service = e.getService();
        final int state = service.getState();
        final String managerName = service.getServiceManagerName();
        final String serviceName = service.getServiceName();
        switch(state){
        case Service.STARTED:
            currentMessage = (MessageRecordFactory)ServiceManagerFactory
            .getServiceObject(
                managerName,
                serviceName
            );
            break;
        case Service.STOPPED:
            currentMessage = defaultMessage;
            break;
        default:
        }

    }

    public boolean isEnabledState(int state){
        switch(state){
        case Service.STARTED:
        case Service.STOPPED:
            return true;
        default:
            return false;
        }
    }

    // MessageRecordFactoryÇÃJavaDoc
    public MessageRecord findMessageRecord(String key){
        if(currentMessage != null){
            return currentMessage.findMessageRecord(key);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public void findLocale(Locale locale){
        if(currentMessage != null){
            currentMessage.findLocale(locale);
        }
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findMessageTemplete(String key){
        if(currentMessage != null){
            return currentMessage.findMessageTemplete(key);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findMessageTemplete(Locale lo,String key){
        if(currentMessage != null){
            return currentMessage.findMessageTemplete(lo, key);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findMessage(String key){
        if(currentMessage != null){
            return currentMessage.findMessage(key);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findMessage(Locale lo,String key){
        if(currentMessage != null){
            return currentMessage.findMessage(lo, key);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(String key,Object[] embeds){
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(key, embeds);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(String key, byte[] embeds) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(key, embeds);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(String key, short[] embeds) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(key, embeds);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(String key, char[] embeds) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(key, embeds);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(String key, int[] embeds) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(key, embeds);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(String key, long[] embeds) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(key, embeds);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(String key, float[] embeds) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(key, embeds);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(String key, double[] embeds) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(key, embeds);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(String key, boolean[] embeds) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(key, embeds);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(Locale lo,String key,Object[] embeds){
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(lo, key, embeds);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(Locale lo, String key, byte[] embeds) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(lo, key, embeds);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(Locale lo, String key, short[] embeds) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(lo, key, embeds);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(Locale lo, String key, char[] embeds) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(lo, key, embeds);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(Locale lo, String key, int[] embeds) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(lo, key, embeds);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(Locale lo, String key, long[] embeds) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(lo, key, embeds);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(Locale lo, String key, float[] embeds) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(lo, key, embeds);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(Locale lo, String key, double[] embeds) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(lo, key, embeds);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(Locale lo, String key, boolean[] embeds) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(lo, key, embeds);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(String key,Object embed){
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(key, embed);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(String key, byte embed) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(key, embed);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(String key, short embed) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(key, embed);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(String key, char embed) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(key, embed);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(String key, int embed) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(key, embed);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(String key, long embed) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(key, embed);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(String key, float embed) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(key, embed);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(String key, double embed) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(key, embed);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(String key, boolean embed) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(key, embed);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(Locale lo,String key,Object embed){
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(lo, key, embed);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(Locale lo, String key, byte embed) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(lo, key, embed);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(Locale lo, String key, short embed) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(lo, key, embed);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(Locale lo, String key, char embed) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(lo, key, embed);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(Locale lo, String key, int embed) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(lo, key, embed);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(Locale lo, String key, long embed) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(lo, key, embed);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(Locale lo, String key, float embed) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(lo, key, embed);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(Locale lo, String key, double embed) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(lo, key, embed);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String findEmbedMessage(Locale lo, String key, boolean embed) {
        if(currentMessage != null){
            return currentMessage.findEmbedMessage(lo, key, embed);
        }
        return null;
    }

    // MessageRecordFactoryÇÃJavaDoc
    public String[] getMessageIds() {
        if(currentMessage != null){
            return currentMessage.getMessageIds();
        }
        return null;
    }
}
