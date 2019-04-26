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
import java.util.Timer;
import java.util.TimerTask;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.service.writer.MessageWriteException;
import jp.ossc.nimbus.service.writer.MessageWriter;
import jp.ossc.nimbus.service.writer.WritableRecord;

/**
 * 抽象AWSCloudWatchMetricsWriterサービス。<p>
 * 
 * @author M.Ishida
 */
public abstract class AbstractAWSCloudWatchMetricsWriterService extends ServiceBase
        implements MessageWriter, AbstractAWSCloudWatchMetricsWriterServiceMBean, java.io.Serializable {
    
    private static final long serialVersionUID = -7302571662304711784L;
    
    protected ServiceName awsClientBuilderServiceName;
    protected ServiceName contextServiceName;
    protected int sdkClientExecutionTimeout;
    protected int sdkRequestTimeout;
    protected String namespace;
    protected String contextDimensionMapKey = DEFAULT_CONTEXT_DIMENSION_MAP_KEY;
    protected int storageResolution = 1;
    protected Map dimensionMap;
    protected String timestampKey = DEFAULT_RECORD_KEY_TIMESTAMP;
    protected String timestampFormat = DEFAULT_TIMESTAMP_FORMAT;
    
    protected List recordBuffer;
    protected int bufferSize;
    protected long bufferTimeout;
    
    protected AwsClientBuilder awsClientBuilder;
    protected AmazonCloudWatchClient amazonCloudWatchClient;
    protected Context context;
    protected Timer bufferTimeoutTimer;
    protected TimerTask bufferTimeoutTimerTask;
    
    public ServiceName getAwsClientBuilderServiceName() {
        return awsClientBuilderServiceName;
    }
    
    public void setAwsClientBuilderServiceName(ServiceName serviceName) {
        awsClientBuilderServiceName = serviceName;
    }
    
    public ServiceName getContextServiceName() {
        return contextServiceName;
    }
    
    public void setContextServiceName(ServiceName serviceName) {
        contextServiceName = serviceName;
    }
    
    public int getSdkClientExecutionTimeout() {
        return sdkClientExecutionTimeout;
    }
    
    public void setSdkClientExecutionTimeout(int timeout) {
        sdkClientExecutionTimeout = timeout;
    }
    
    public int getSdkRequestTimeout() {
        return sdkRequestTimeout;
    }
    
    public void setSdkRequestTimeout(int timeout) {
        sdkRequestTimeout = timeout;
    }
    
    public String getNamespace() {
        return namespace;
    }
    
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    
    public String getContextDimensionMapKey() {
        return contextDimensionMapKey;
    }
    
    public void setContextDimensionMapKey(String key) {
        contextDimensionMapKey = key;
    }
    
    public int getStorageResolution() {
        return storageResolution;
    }
    
    public void setStorageResolution(int value) {
        storageResolution = value;
    }
    
    public Map getDimensionMap() {
        return dimensionMap;
    }
    
    public void setDimensionMap(Map map) {
        dimensionMap = map;
    }
    
    public void setDimension(String key, String value) {
        if(dimensionMap == null) {
            dimensionMap = new HashMap();
        }
        dimensionMap.put(key, value);
    }
    
    public int getBufferSize() {
        return bufferSize;
    }
    
    public void setBufferSize(int size) {
        bufferSize = size;
    }
    
    public long getBufferTimeout() {
        return bufferTimeout;
    }
    
    public void setBufferTimeout(long timeout) {
        bufferTimeout = timeout;
    }
    
    public String getTimestampKey() {
        return timestampKey;
    }
    
    public void setTimestampKey(String key) {
        timestampKey = key;
    }
    
    public String getTimestampFormat() {
        return timestampFormat;
    }
    
    public void setTimestampFormat(String format) {
        timestampFormat = format;
    }
    
    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void preCreateService() throws Exception {
        dimensionMap = new HashMap();
        recordBuffer = new ArrayList();
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception {
        if(awsClientBuilderServiceName != null){
            awsClientBuilder = (AwsClientBuilder) ServiceManagerFactory.getServiceObject(awsClientBuilderServiceName);
        }
        if(awsClientBuilder == null){
            throw new IllegalArgumentException("AwsClientBuilder is null.");
        }
        if(namespace == null){
            throw new IllegalArgumentException("Namespace is null.");
        }
        if(contextServiceName != null){
            context = (Context) ServiceManagerFactory.getServiceObject(contextServiceName);
        }
        amazonCloudWatchClient = (AmazonCloudWatchClient) awsClientBuilder.build();
        if(bufferTimeout > 0 && bufferSize > 0){
            bufferTimeoutTimer = new Timer(true);
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception {
        if(bufferTimeoutTimer != null){
            synchronized (bufferTimeoutTimer){
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
    
    public void write(WritableRecord rec) throws MessageWriteException {
        if(bufferSize <= 0){
            List records = new ArrayList();
            records.add(rec);
            writeInternal(records);
        }else{
            if(bufferTimeoutTimer != null && bufferTimeoutTimerTask == null){
                synchronized (bufferTimeoutTimer){
                    if(bufferTimeoutTimer != null && bufferTimeoutTimerTask == null){
                        bufferTimeoutTimerTask = new TimerTask() {
                            public void run() {
                                try{
                                    writeBuffer();
                                }catch (MessageWriteException e){
                                }
                            }
                        };
                        bufferTimeoutTimer.schedule(bufferTimeoutTimerTask, bufferTimeout);
                    }
                }
            }
            synchronized (recordBuffer){
                recordBuffer.add(rec);
            }
            if(recordBuffer.size() >= bufferSize){
                writeBuffer();
            }
        }
    }
    
    protected void writeBuffer() throws MessageWriteException {
        writeBuffer(false);
    }
    
    protected void writeBuffer(boolean force) throws MessageWriteException {
        synchronized (recordBuffer){
            if(bufferTimeoutTimer != null && bufferTimeoutTimerTask != null){
                synchronized (bufferTimeoutTimer){
                    if(bufferTimeoutTimer != null && bufferTimeoutTimerTask != null){
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
            List records = new ArrayList();
            for(int i = 0; i < maxSize; i++){
                final WritableRecord rec = (WritableRecord) recordBuffer.get(i);
                records.add(rec);
            }
            writeInternal(records);
            for(int i = 0; i < maxSize; i++){
                recordBuffer.remove(0);
            }
            if(recordBuffer.size() >= bufferSize){
                writeBuffer();
            }
            if(bufferTimeoutTimer != null && bufferTimeoutTimerTask == null && recordBuffer.size() != 0){
                synchronized (bufferTimeoutTimer){
                    if(bufferTimeoutTimer != null && bufferTimeoutTimerTask == null){
                        bufferTimeoutTimerTask = new TimerTask() {
                            public void run() {
                                try{
                                    writeBuffer();
                                }catch (MessageWriteException e){
                                }
                            }
                        };
                        bufferTimeoutTimer.schedule(bufferTimeoutTimerTask, bufferTimeout);
                    }
                }
            }
        }
    }
    
    protected abstract void writeInternal(List records) throws MessageWriteException;
    
}