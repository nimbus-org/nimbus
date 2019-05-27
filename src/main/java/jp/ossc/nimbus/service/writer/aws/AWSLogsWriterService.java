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
package jp.ossc.nimbus.service.writer.aws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import com.amazonaws.AbortedException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.logs.AWSLogsClient;
import com.amazonaws.services.logs.model.CreateLogGroupRequest;
import com.amazonaws.services.logs.model.CreateLogStreamRequest;
import com.amazonaws.services.logs.model.DescribeLogStreamsRequest;
import com.amazonaws.services.logs.model.DescribeLogStreamsResult;
import com.amazonaws.services.logs.model.InputLogEvent;
import com.amazonaws.services.logs.model.InvalidSequenceTokenException;
import com.amazonaws.services.logs.model.PutLogEventsRequest;
import com.amazonaws.services.logs.model.PutLogEventsResult;
import com.amazonaws.services.logs.model.RejectedLogEventsInfo;
import com.amazonaws.services.logs.model.ResourceAlreadyExistsException;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.writer.MessageWriteException;
import jp.ossc.nimbus.service.writer.WritableRecord;

/**
 * AWS CloudWatch Logs Writerサービス。<p>
 *
 * @author M.Takata
 */
public class AWSLogsWriterService extends ServiceBase implements AWSLogsWriterServiceMBean, java.io.Serializable{

    protected ServiceName awsClientBuilderServiceName;
    protected AwsClientBuilder awsClientBuilder;
    protected int sdkClientExecutionTimeout;
    protected int sdkRequestTimeout;
    protected boolean isCreateLogGroupOnStart;
    protected boolean isCreateLogStreamOnStart;
    protected String logGroupName;
    protected String kmsKeyId;
    protected Properties tags;
    protected String logStreamName;
    protected int bufferSize;
    protected long bufferTimeout;

    protected String sequenceToken;
    protected AWSLogsClient awsLogsClient;
    protected List recordBuffer;
    protected Timer bufferTimeoutTimer;
    protected TimerTask bufferTimeoutTimerTask;
    protected boolean isIgnoreAbortion;

    public void setAwsClientBuilderServiceName(ServiceName name){
        awsClientBuilderServiceName = name;
    }
    public ServiceName getAwsClientBuilderServiceName(){
        return awsClientBuilderServiceName;
    }

    public void setSdkClientExecutionTimeout(int timeout){
        sdkClientExecutionTimeout = timeout;
    }
    public int getSdkClientExecutionTimeout(){
        return sdkClientExecutionTimeout;
    }

    public void setSdkRequestTimeout(int timeout){
        sdkRequestTimeout = timeout;
    }
    public int getSdkRequestTimeout(){
        return sdkRequestTimeout;
    }

    public void setCreateLogGroupOnStart(boolean isCreate){
        isCreateLogGroupOnStart = isCreate;
    }
    public boolean isCreateLogGroupOnStart(){
        return isCreateLogGroupOnStart;
    }

    public void setCreateLogStreamOnStart(boolean isCreate){
        isCreateLogStreamOnStart = isCreate;
    }
    public boolean isCreateLogStreamOnStart(){
        return isCreateLogStreamOnStart;
    }

    public void setLogGroupName(String name){
        logGroupName = name;
    }
    public String getLogGroupName(){
        return logGroupName;
    }

    public void setKMSKeyId(String id){
        kmsKeyId = id;
    }
    public String getKMSKeyId(){
        return kmsKeyId;
    }

    public void setTags(Properties tags){
        this.tags = tags;
    }
    public Properties getTags(){
        return tags;
    }

    public void setLogStreamName(String name){
        logStreamName = name;
    }
    public String getLogStreamName(){
        return logStreamName;
    }

    public void setBufferSize(int size){
        bufferSize = size;
    }
    public int getBufferSize(){
        return bufferSize;
    }

    public void setBufferTimeout(long timeout){
        bufferTimeout = timeout;
    }
    public long getBufferTimeout(){
        return bufferTimeout;
    }

    public void setAwsClientBuilder(AwsClientBuilder builder){
        awsClientBuilder = builder;
    }

	public void setIgnoreAbortion(boolean isIgnoreabortion) {
		isIgnoreAbortion = isIgnoreabortion;
	}
	public boolean getIgnoreAbortion() {
		return isIgnoreAbortion;
	}
    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception{
        recordBuffer = new ArrayList();
    }

    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(awsClientBuilderServiceName != null){
            awsClientBuilder = (AwsClientBuilder)ServiceManagerFactory.getServiceObject(awsClientBuilderServiceName);
        }
        if(awsClientBuilder == null){
            throw new IllegalArgumentException("AwsClientBuilder is null.");
        }
        awsLogsClient = (AWSLogsClient)awsClientBuilder.build();

        if(isCreateLogGroupOnStart){
            try{
                CreateLogGroupRequest request = new CreateLogGroupRequest()
                    .withLogGroupName(logGroupName);
                if(kmsKeyId != null){
                    request.setKmsKeyId(kmsKeyId);
                }
                if(tags != null){
                    Map map = new HashMap();
                    map.putAll(tags);
                    request.setTags(map);
                }
                awsLogsClient.createLogGroup(request);
            }catch(ResourceAlreadyExistsException e){
            }
        }

        if(isCreateLogStreamOnStart){
            try{
                CreateLogStreamRequest request = new CreateLogStreamRequest()
                    .withLogGroupName(logGroupName)
                    .withLogStreamName(logStreamName);
                awsLogsClient.createLogStream(request);
            }catch(ResourceAlreadyExistsException e){
            }
        }

        DescribeLogStreamsResult result = awsLogsClient.describeLogStreams(
            new DescribeLogStreamsRequest()
                .withLogGroupName(logGroupName)
                .withLogStreamNamePrefix(logStreamName)
        );

        if(result.getLogStreams().size() >= 2) {
        	throw new MessageWriteException(" LogStreams should be unique " );
        }
        sequenceToken = result.getLogStreams().get(0).getUploadSequenceToken();
        if(bufferTimeout > 0 && bufferSize > 0){
            bufferTimeoutTimer = new Timer(true);
        }
    }

    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        if(bufferTimeoutTimer != null){
            synchronized(bufferTimeoutTimer){
                bufferTimeoutTimer.cancel();
                bufferTimeoutTimer = null;
                bufferTimeoutTimerTask = null;
            }
        }
        if(bufferSize > 0 && recordBuffer.size() > 0){
            writeBuffer(true);
        }
        recordBuffer.clear();
    }

    public void write(WritableRecord rec) throws MessageWriteException{
        if(bufferSize <= 0){
            PutLogEventsRequest logEvent = new PutLogEventsRequest()
                .withLogGroupName(logGroupName)
                .withLogStreamName(logStreamName)
                .withSequenceToken(sequenceToken)
                .withLogEvents(
                    new InputLogEvent()
                        .withMessage(rec.toString())
                        .withTimestamp(new Long(System.currentTimeMillis()))
                );
            try{
            	executePutLogEventsRequest(logEvent);
            }catch(InvalidSequenceTokenException iste) {
                sequenceToken = iste.getExpectedSequenceToken();
                PutLogEventsRequest logEvent_overwrite = new PutLogEventsRequest()
                     .withLogGroupName(logGroupName)
                     .withLogStreamName(logStreamName)
                     .withSequenceToken(sequenceToken)
                     .withLogEvents(
                         new InputLogEvent()
                             .withMessage(rec.toString())
                             .withTimestamp(new Long(System.currentTimeMillis()))
                     );
                try {
                	 executePutLogEventsRequest(logEvent_overwrite);
                }catch(AmazonServiceException e){
                    throw new MessageWriteException(e);
                }
            }catch(AmazonServiceException e){
                throw new MessageWriteException(e);
            }catch(AbortedException ae) {
                if (isIgnoreAbortion) {
                    try {
                    	Thread.interrupted();
                    	executePutLogEventsRequest(logEvent);
                    }catch(AmazonServiceException e){
                        throw new MessageWriteException(e);
                    }catch(Exception e){
                        throw new MessageWriteException(e);
                    }
                }
            }
        }else{
            if(bufferTimeoutTimer != null && bufferTimeoutTimerTask == null){
                synchronized(bufferTimeoutTimer){
                    if(bufferTimeoutTimer != null
                         && bufferTimeoutTimerTask == null){
                        bufferTimeoutTimerTask = new TimerTask(){
                            public void run(){
                                try{
                                    writeBuffer();
                                }catch(MessageWriteException e){
                                }
                            }
                        };
                        bufferTimeoutTimer.schedule(
                            bufferTimeoutTimerTask,
                            bufferTimeout
                        );
                    }
                }
            }
            synchronized(recordBuffer){
                recordBuffer.add(rec);
            }
            if(recordBuffer.size() >= bufferSize){
                writeBuffer();
            }
        }
    }

    protected void writeBuffer() throws MessageWriteException{
        writeBuffer(false);
    }
    protected void writeBuffer(boolean force) throws MessageWriteException{
        synchronized(recordBuffer){
            if(bufferTimeoutTimer != null && bufferTimeoutTimerTask != null){
                synchronized(bufferTimeoutTimer){
                    if(bufferTimeoutTimer != null
                         && bufferTimeoutTimerTask != null){
                        bufferTimeoutTimerTask.cancel();
                        bufferTimeoutTimerTask = null;
                    }
                }
            }
            if(recordBuffer.size() == 0){
                return;
            }
            int maxSize = recordBuffer.size();
            if(!force && maxSize > bufferSize){
                maxSize = bufferSize;
            }
            List logEvents = new ArrayList();
            for(int i = 0; i < maxSize; i++){
                final WritableRecord rec
                    = (WritableRecord)recordBuffer.get(i);
                logEvents.add(
                    new InputLogEvent()
                        .withMessage(rec.toString())
                        .withTimestamp(new Long(System.currentTimeMillis()))
                );
            }
            PutLogEventsRequest request = new PutLogEventsRequest()
                .withLogGroupName(logGroupName)
                .withLogStreamName(logStreamName)
                .withSequenceToken(sequenceToken)
                .withLogEvents(logEvents);
            try{
                executePutLogEventsRequest(request);
            }catch(InvalidSequenceTokenException iste) {
            	try {
                    sequenceToken = iste.getExpectedSequenceToken();
                    PutLogEventsRequest logEvent_overwrite = new PutLogEventsRequest()
                     .withLogGroupName(logGroupName)
                     .withLogStreamName(logStreamName)
                     .withSequenceToken(sequenceToken)
                     .withLogEvents(logEvents);
                    executePutLogEventsRequest(logEvent_overwrite);
                }catch (AmazonServiceException e) {
                  throw new MessageWriteException(e);
                }
            }catch(AmazonServiceException e){
                throw new MessageWriteException(e);
            }catch(AbortedException ae) {
                if (isIgnoreAbortion) {
                    try {
                    	Thread.interrupted();
                    	executePutLogEventsRequest(request);
                    }catch(AmazonServiceException e){
                        throw new MessageWriteException(e);
                    }catch(Exception e){
                        throw new MessageWriteException(e);
                    }
                }
            }
            for(int i = 0; i < maxSize; i++){
                recordBuffer.remove(0);
            }
            if(recordBuffer.size() >= bufferSize){
                writeBuffer();
            }
            if(bufferTimeoutTimer != null && bufferTimeoutTimerTask == null
                && recordBuffer.size() != 0){
                synchronized(bufferTimeoutTimer){
                    if(bufferTimeoutTimer != null
                         && bufferTimeoutTimerTask == null){
                        bufferTimeoutTimerTask = new TimerTask(){
                            public void run(){
                                try{
                                    writeBuffer();
                                }catch(MessageWriteException e){
                                }
                            }
                        };
                        bufferTimeoutTimer.schedule(
                            bufferTimeoutTimerTask,
                            bufferTimeout
                        );
                    }
                }
            }
        }
    }

    private void executePutLogEventsRequest(PutLogEventsRequest putlogeventrequest) throws MessageWriteException {
    	PutLogEventsResult result = awsLogsClient.putLogEvents(putlogeventrequest);
        sequenceToken = result.getNextSequenceToken();
        RejectedLogEventsInfo rejected = result.getRejectedLogEventsInfo();
        if (rejected != null) {
            throw new MessageWriteException("Log rejected. reason=" + rejected);
        }
    }

}