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
package jp.ossc.nimbus.service.scheduler2.aws;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Map;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.scheduler2.AbstractScheduleExecutorService;
import jp.ossc.nimbus.service.scheduler2.Schedule;
import jp.ossc.nimbus.service.scheduler2.ScheduleStateControlException;
import jp.ossc.nimbus.util.converter.BeanJSONConverter;
import jp.ossc.nimbus.util.converter.StringStreamConverter;

/**
 * AWS Lambdaを呼び出すスケジュール実行。<p>
 *
 * @author M.Ishida
 */
public class AWSLambdaScheduleExecutorService extends AbstractScheduleExecutorService implements AWSLambdaScheduleExecutorServiceMBean {
    
    private static final long serialVersionUID = 6075236051813713742L;
    
    protected ServiceName lambdaServiceName;
    protected ServiceName lambdaClientBuilderServiceName;
    protected Integer clientExecutionTimeout = null;
    protected Integer requestTimeout = null;
    protected String encoding = DEFAULT_ENCODING;
    
    protected AWSLambda lambda;
    protected AWSLambdaClientBuilder lambdaClientBuilder;
    
    {
        type = DEFAULT_EXECUTOR_TYPE;
    }
    
    public ServiceName getAWSLambdaServiceName() {
        return lambdaServiceName;
    }
    
    public void setAWSLambdaServiceName(ServiceName serviceName) {
        lambdaServiceName = serviceName;
    }
    
    public ServiceName getAWSLambdaClientBuilderServiceName() {
        return lambdaClientBuilderServiceName;
    }

    public void setAWSLambdaClientBuilderServiceName(ServiceName serviceName) {
        lambdaClientBuilderServiceName = serviceName;
    }

    public int getClientExecutionTimeout() {
        return clientExecutionTimeout;
    }

    public void setClientExecutionTimeout(int timeout) {
        clientExecutionTimeout = timeout;
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int timeout) {
        requestTimeout = timeout;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public AWSLambda getAWSLambda() {
        return lambda;
    }
    
    public void setAWSLambda(AWSLambda awsLambda) {
        lambda = awsLambda;
    }
    
    public AWSLambdaClientBuilder getAWSLambdaClientBuilder() {
        return lambdaClientBuilder;
    }

    public void setAWSLambdaClientBuilder(AWSLambdaClientBuilder builder) {
        lambdaClientBuilder = builder;
    }

    public void startService() throws Exception {
        if(lambda == null && lambdaServiceName != null){
            lambda = (AWSLambda) ServiceManagerFactory.getServiceObject(lambdaServiceName);
        }
        if(lambda == null && lambdaClientBuilder == null && lambdaClientBuilderServiceName != null){
            lambdaClientBuilder = (AWSLambdaClientBuilder) ServiceManagerFactory.getServiceObject(lambdaClientBuilderServiceName);
        }
        if(lambda == null && lambdaClientBuilder != null) {
            lambda = lambdaClientBuilder.build();
        }
        if(lambda == null){
            lambda = AWSLambdaClientBuilder.standard().build();
        }
        if(encoding != null && !Charset.isSupported(encoding)) {
            throw new IllegalArgumentException("encoding is not support. encoding=" + encoding);
        }
        super.startService();
    }
    
    protected void checkPreExecute(Schedule schedule) throws Exception{
        Object input = schedule.getInput();
        if(!(input == null || input instanceof String || input instanceof Map)){
            throw new IllegalArgumentException("Input is not support. type=" + input.getClass().getName());
        }
    }
    
    protected Schedule executeInternal(Schedule schedule) throws Throwable {
        String functionName = schedule.getTaskName();
        if(functionName == null || functionName.length() == 0) {
            throw new IllegalArgumentException("TaskName is null or empty.");
        }
        Object input = schedule.getInput();
        String inputJSON = null;
        if(input instanceof String) {
            inputJSON = (String)input;
        } else if(input instanceof Map) {
            BeanJSONConverter bjConverter = new BeanJSONConverter();
            StringStreamConverter ssConverter = new StringStreamConverter();
            inputJSON = (String)ssConverter.convertToObject(bjConverter.convertToStream(input));
        }
        InvokeRequest request = new InvokeRequest();
        request.setFunctionName(functionName);
        if(inputJSON != null) {
            request.setPayload(inputJSON);
        }
        if(clientExecutionTimeout != null) {
            request.setSdkClientExecutionTimeout(clientExecutionTimeout.intValue());
        }
        if(requestTimeout != null) {
            request.setSdkRequestTimeout(requestTimeout.intValue());
        }
        request.setInvocationType(InvocationType.RequestResponse);
        InvokeResult result = lambda.invoke(request);
        
        ByteBuffer buffer = result.getPayload();
        String resultString = encoding == null ? new String(buffer.array()) : new String(buffer.array(), Charset.forName(encoding));
        schedule.setOutput(resultString);
        return schedule;
    }
    
    public boolean controlState(String id, int cntrolState) throws ScheduleStateControlException {
        return false;
    }
    
}