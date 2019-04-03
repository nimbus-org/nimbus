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
package jp.ossc.nimbus.service.queue;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.SdkClientException;
import com.amazonaws.http.exception.HttpRequestTimeoutException;
import com.amazonaws.http.timers.client.ClientExecutionTimeoutException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import jp.ossc.nimbus.beans.NoSuchPropertyException;
import jp.ossc.nimbus.beans.PropertyAccess;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.sequence.Sequence;
import jp.ossc.nimbus.util.converter.BASE64StringConverter;
import jp.ossc.nimbus.util.converter.Converter;
import jp.ossc.nimbus.util.converter.CustomConverter;
import jp.ossc.nimbus.util.converter.SerializeStreamConverter;


/**
 * AWS SQS(Simple Queue Service)の{@link Queue}インタフェース実装サービス。<p>
 *
 * @author M.Takata
 */
public class AWSSQSQueueService extends ServiceBase implements Queue, AWSSQSQueueServiceMBean{

    protected ServiceName sqsClientBuilderServiceName;
    protected String queueName;
    protected Map queueAttributes;
    protected Map messageAttributes;
    protected boolean isCreateQueueOnStart;
    protected boolean isDeleteQueueOnStop;
    protected ServiceName messageBodyFormatConverterServiceName;
    protected ServiceName messageBodyParseConverterServiceName;
    protected ServiceName messageDeduplicationIdSequenceServiceName;
    protected String propertyNameOfMessageDeduplicationId;
    protected String propertyNameOfMessageGroupId;
    protected String messageGroupId;
    protected int delaySeconds;

    protected AmazonSQSClientBuilder sqsClientBuilder;
    protected AmazonSQS sqs;
    protected Converter messageBodyFormatConverter;
    protected Converter messageBodyParseConverter;
    protected Sequence messageDeduplicationIdSequence;
    protected PropertyAccess propertyAccess;
    protected long count;
    protected int waitCount;

    public void setAmazonSQSClientBuilderServiceName(ServiceName name){
        sqsClientBuilderServiceName = name;
    }
    public ServiceName getAmazonSQSClientBuilderServiceName(){
        return sqsClientBuilderServiceName;
    }

    public void setQueueName(String name){
        queueName = name;
    }
    public String getQueueName(){
        return queueName;
    }

    public void setCreateQueueOnStart(boolean isCreate){
        isCreateQueueOnStart = isCreate;
    }
    public boolean isCreateQueueOnStart(){
        return isCreateQueueOnStart;
    }

    public void setDeleteQueueOnStop(boolean isDelete){
        isDeleteQueueOnStop = isDelete;
    }
    public boolean isDeleteQueueOnStop(){
        return isDeleteQueueOnStop;
    }

    public void setQueueAttributes(Map attributes){
        queueAttributes = attributes;
    }
    public Map getQueueAttributes(){
        return queueAttributes;
    }

    public void setQueueAttribute(String name, String value){
        if(queueAttributes == null){
            queueAttributes = new HashMap();
        }
        queueAttributes.put(name, value);
    }

    public void setMessageAttributes(Map attributes){
        messageAttributes = attributes;
    }
    public Map getMessageAttributes(){
        return messageAttributes;
    }

    public void setMessageAttribute(String name, String value){
        if(messageAttributes == null){
            messageAttributes = new HashMap();
        }
        messageAttributes.put(name, value);
    }

    public void setDelaySeconds(int seconds){
        delaySeconds = seconds;
    }
    public int getDelaySeconds(){
        return delaySeconds;
    }

    public void setMessageBodyFormatConverterServiceName(ServiceName name){
        messageBodyFormatConverterServiceName = name;
    }
    public ServiceName getMessageBodyFormatConverterServiceName(){
        return messageBodyFormatConverterServiceName;
    }

    public void setMessageBodyParseConverterServiceName(ServiceName name){
        messageBodyParseConverterServiceName = name;
    }
    public ServiceName getMessageBodyParseConverterServiceName(){
        return messageBodyParseConverterServiceName;
    }

    public void setMessageDeduplicationIdSequenceServiceName(ServiceName name){
        messageDeduplicationIdSequenceServiceName = name;
    }
    public ServiceName getMessageDeduplicationIdSequenceServiceName(){
        return messageDeduplicationIdSequenceServiceName;
    }

    public void setPropertyNameOfMessageDeduplicationId(String name){
        propertyNameOfMessageDeduplicationId = name;
    }
    public String getPropertyNameOfMessageDeduplicationId(){
        return propertyNameOfMessageDeduplicationId;
    }

    public void setPropertyNameOfMessageGroupId(String name){
        propertyNameOfMessageGroupId = name;
    }
    public String getPropertyNameOfMessageGroupId(){
        return propertyNameOfMessageGroupId;
    }

    public void setMessageGroupId(String id){
        messageGroupId = id;
    }
    public String getMessageGroupId(){
        return messageGroupId;
    }

    public void setAmazonSQSClientBuilder(AmazonSQSClientBuilder builder){
        sqsClientBuilder = builder;
    }

    public void setMmessageBodyFormatConverter(Converter converter){
        messageBodyFormatConverter = converter;
    }
    public void setMessageBodyParseConverter(Converter converter){
        messageBodyParseConverter = converter;
    }
    public void setMessageDeduplicationIdSequence(Sequence sequence){
        messageDeduplicationIdSequence = sequence;
    }

    public void createService() throws Exception{
        propertyAccess = new PropertyAccess();
    }

    public void startService() throws Exception{
        if(sqsClientBuilderServiceName != null){
            sqsClientBuilder = (AmazonSQSClientBuilder)ServiceManagerFactory.getServiceObject(sqsClientBuilderServiceName);
        }
        if(sqsClientBuilder == null){
            sqsClientBuilder = AmazonSQSClientBuilder.standard();
        }
        if(messageDeduplicationIdSequenceServiceName != null){
            messageDeduplicationIdSequence = (Sequence)ServiceManagerFactory.getServiceObject(messageDeduplicationIdSequenceServiceName);
        }
        sqs = (AmazonSQS)sqsClientBuilder.build();
        if(queueName == null){
            if(getServiceNameObject() != null){
                queueName = getServiceNameObject().toString();
                if(messageDeduplicationIdSequence != null || propertyNameOfMessageDeduplicationId != null){
                    queueName += ".fifo";
                }
            }
            if(queueName == null){
                throw new IllegalArgumentException("QueueName is null.");
            }
        }
        if(isCreateQueueOnStart){
            CreateQueueRequest request = new CreateQueueRequest().withQueueName(queueName);
            if(queueAttributes != null){
                request.setAttributes(queueAttributes);
            }
            sqs.createQueue(request);
        }
        if(messageBodyFormatConverterServiceName != null){
            messageBodyFormatConverter = (Converter)ServiceManagerFactory.getServiceObject(messageBodyFormatConverterServiceName);

            if(messageBodyParseConverterServiceName != null){
                messageBodyParseConverter = (Converter)ServiceManagerFactory.getServiceObject(messageBodyParseConverterServiceName);
            }
            if(messageBodyParseConverter == null){
                throw new IllegalArgumentException("MessageBodyParseConverter is null.");
            }
        }

        if(messageBodyFormatConverter == null){
            CustomConverter formatConverter = new CustomConverter();
            formatConverter.add(new SerializeStreamConverter(SerializeStreamConverter.OBJECT_TO_STREAM));
            BASE64StringConverter encoder = new BASE64StringConverter();
            encoder.setConvertType(BASE64StringConverter.ENCODE_STREAM_TO_STRING);
            formatConverter.add(encoder);
            messageBodyFormatConverter = formatConverter;

            CustomConverter parseConverter = new CustomConverter();
            BASE64StringConverter decoder = new BASE64StringConverter();
            decoder.setConvertType(BASE64StringConverter.DECODE_STRING_TO_STREAM);
            parseConverter.add(decoder);
            parseConverter.add(new SerializeStreamConverter(SerializeStreamConverter.STREAM_TO_OBJECT));
            messageBodyParseConverter = parseConverter;
        }
    }

    public void stopService() throws Exception{
        if(isDeleteQueueOnStop){
            DeleteQueueRequest request = new DeleteQueueRequest().withQueueUrl(sqs.getQueueUrl(queueName).getQueueUrl());
            sqs.deleteQueue(request);
        }
        release();
    }

    public void destroyService() throws Exception{
        propertyAccess = null;
    }

    // QueueのJavaDoc
    public void push(Object item){
        push(item, -1l);
    }

    // QueueのJavaDoc
    public boolean push(Object item, long timeout){
        if(sqs == null){
            return false;
        }
        final SendMessageRequest request = new SendMessageRequest()
            .withQueueUrl(sqs.getQueueUrl(queueName).getQueueUrl());
        if(messageAttributes != null){
            request.setMessageAttributes(messageAttributes);
        }
        String messageDeduplicationId = null;
        if(messageDeduplicationIdSequence != null){
            messageDeduplicationId = messageDeduplicationIdSequence.increment();
        }else if(propertyNameOfMessageDeduplicationId != null){
            try{
                messageDeduplicationId = propertyAccess.get(item, propertyNameOfMessageDeduplicationId).toString();
            }catch(NoSuchPropertyException e){
            }catch(InvocationTargetException e){
            }
        }
        if(messageDeduplicationId != null){
            request.setMessageDeduplicationId(messageDeduplicationId);
            String messageGroupId = getServiceName();
            if(propertyNameOfMessageGroupId != null){
                try{
                    messageGroupId = propertyAccess.get(item, propertyNameOfMessageGroupId).toString();
                }catch(NoSuchPropertyException e){
                }catch(InvocationTargetException e){
                }
            }
            request.setMessageGroupId(messageGroupId);
        }
        if(delaySeconds > 0){
            request.setDelaySeconds(delaySeconds);
        }
        if(timeout > 0){
            request.setSdkRequestTimeout((int)timeout);
        }
        if(item != null){
            request.setMessageBody((String)messageBodyFormatConverter.convert(item));
        }
        try{
            sqs.sendMessage(request);
            count++;
        }catch(ClientExecutionTimeoutException e){
            return false;
        }catch(SdkClientException e){
            Throwable cause = e.getCause();
            if(cause instanceof HttpRequestTimeoutException){
                return false;
            }
            throw e;
        }
        return true;
    }

    public Object get(){
        return get(-1);
    }

    public Object get(long timeOutMs){
        return get(timeOutMs, true);
    }

    protected Object get(long timeOutMs, boolean isRemove){
        if(sqs == null){
            return null;
        }
        synchronized(sqs){
            waitCount++;
        }
        try{
            List messages = getMessageList(1, timeOutMs, isRemove);
            if(messages == null || messages.size() == 0){
                return null;
            }
            Message message = (Message)messages.get(0);
            if(isRemove){
                sqs.deleteMessage(sqs.getQueueUrl(queueName).getQueueUrl(), message.getReceiptHandle());
            }
            String body = message.getBody();
            if(body == null){
                return null;
            }
            return messageBodyParseConverter.convert(body);
        }finally{
            synchronized(sqs){
                waitCount--;
            }
        }
    }

    protected List getMessageList(int maxSize, long timeOutMs, boolean isRemove){
        ReceiveMessageRequest request = new ReceiveMessageRequest()
            .withQueueUrl(sqs.getQueueUrl(queueName).getQueueUrl());
        if(maxSize > 0){
            request.setMaxNumberOfMessages(Integer.valueOf(maxSize));
        }
        if(timeOutMs > 0){
            request.setSdkRequestTimeout((int)timeOutMs);
            request.setWaitTimeSeconds(Integer.valueOf((int)(timeOutMs / 1000)));
        }
        if(!isRemove){
            request.setVisibilityTimeout(Integer.valueOf(0));
        }
        List messages = null;
        try{
            ReceiveMessageResult result = sqs.receiveMessage(request);
            messages = result.getMessages();
        }catch(ClientExecutionTimeoutException e){
            return null;
        }catch(SdkClientException e){
            Throwable cause = e.getCause();
            if(cause instanceof HttpRequestTimeoutException){
                return null;
            }
            throw e;
        }
        return messages;
    }

    public Object peek(){
        return get(-1l, false);
    }

    public Object peek(long timeOutMs){
        return get(timeOutMs, false);
    }

    public Object remove(Object item){
        throw new UnsupportedOperationException();
    }

    public void clear(){
        if(sqs == null){
            return;
        }
        if(size() == 0){
            return;
        }
        List messages = getMessageList(-1, 10000, true);
        if(messages == null || messages.size() == 0){
            return;
        }
        final String url = sqs.getQueueUrl(queueName).getQueueUrl();
        for(int i = 0; i < messages.size(); i++){
            Message message = (Message)messages.get(i);
            sqs.deleteMessage(url, message.getReceiptHandle());
        }
    }

    public int size(){
        if(sqs == null){
            return 0;
        }
        List messages = getMessageList(-1, 0, false);
        return messages == null ? 0 : messages.size();
    }

    public long getCount(){
        return count;
    }

    public int getWaitCount(){
        return waitCount;
    }

    public void accept(){
    }

    public void release(){
        sqs = null;
    }
}
