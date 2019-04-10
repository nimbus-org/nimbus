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

import java.util.List;
import java.lang.reflect.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.PropertyAccess;
import jp.ossc.nimbus.beans.Property;
import jp.ossc.nimbus.beans.NoSuchPropertyException;
import jp.ossc.nimbus.service.scheduler2.*;
import jp.ossc.nimbus.util.converter.Converter;
import jp.ossc.nimbus.util.converter.ConvertException;

import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.client.builder.AwsClientBuilder;

/**
 * AWS Webサービスを呼び出すスケジュール実行。<p>
 *
 * @author M.Takata
 */
public class AWSWebServiceScheduleExecutorService extends AbstractScheduleExecutorService implements AWSWebServiceScheduleExecutorServiceMBean{
    
    protected ServiceName awsClientBuilderServiceName;
    protected AwsClientBuilder awsClientBuilder;
    protected int sdkClientExecutionTimeout;
    protected int sdkRequestTimeout;
    
    protected PropertyAccess propertyAccess;
    
    protected AmazonWebServiceClient webServiceClient;
    
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
    
    public void setAwsClientBuilder(AwsClientBuilder builder){
        awsClientBuilder = builder;
    }
    
    public void createService() throws Exception{
        propertyAccess = new PropertyAccess();
    }
    
    public void startService() throws Exception{
        if(awsClientBuilderServiceName != null){
            awsClientBuilder = (AwsClientBuilder)ServiceManagerFactory.getServiceObject(awsClientBuilderServiceName);
        }
        if(awsClientBuilder == null){
            throw new IllegalArgumentException("AwsClientBuilder is null.");
        }
        webServiceClient = (AmazonWebServiceClient)awsClientBuilder.build();
    }
    
    protected void convertInput(Schedule schedule) throws ConvertException{
        if(schedule.getInput() != null && schedule.getInput() instanceof String){
            String input = (String)schedule.getInput();
            input = Utility.replaceSystemProperty(input);
            input = Utility.replaceManagerProperty(getServiceManager(), input);
            input = Utility.replaceServerProperty(input);
            input = replaceScheduleProperty(schedule, input);
            schedule.setInput(input);
        }
        super.convertInput(schedule);
    }
    
    protected Schedule executeInternal(Schedule schedule) throws Throwable{
        Object response = executeRequest(schedule.getTaskName(), (AmazonWebServiceRequest)schedule.getInput());
        schedule.setOutput(response);
        return schedule;
    }
    
    public boolean controlState(String id, int cntrolState) throws ScheduleStateControlException{
        return false;
    }
    
    /**
     * 指定された文字列内のプロパティ参照文字列をスケジュールのプロパティの値で置換する。<p>
     *
     * @param schedule スケジュール
     * @param str 文字列
     * @return プロパティ参照文字列をスケジュールのプロパティの値に置換した文字列
     */
    protected String replaceScheduleProperty(Schedule schedule, String str){
        String result = str;
        if(result == null){
            return null;
        }
        final int startIndex = result.indexOf(Utility.SYSTEM_PROPERTY_START);
        if(startIndex == -1){
            return result;
        }
        final int endIndex = result.indexOf(Utility.SYSTEM_PROPERTY_END, startIndex);
        if(endIndex == -1){
            return result;
        }
        final String propStr = result.substring(
            startIndex + Utility.SYSTEM_PROPERTY_START.length(),
            endIndex
        );
        String prop = null;
        if(propStr != null && propStr.length() != 0){
            Property p = propertyAccess.getProperty(propStr);
            if(p.isReadable(schedule)){
                try{
                    Object val = p.getProperty(schedule);
                    if(val != null){
                        prop = val.toString();
                    }
                }catch(NoSuchPropertyException e){
                }catch(InvocationTargetException e){
                }
            }
        }
        if(prop == null){
            return result.substring(0, endIndex + Utility.SYSTEM_PROPERTY_END.length())
             + replaceScheduleProperty(
                schedule,
                result.substring(endIndex + Utility.SYSTEM_PROPERTY_END.length())
             );
        }else{
            result = result.substring(0, startIndex) + prop
                 + result.substring(endIndex + Utility.SYSTEM_PROPERTY_END.length());
        }
        if(result.indexOf(Utility.SYSTEM_PROPERTY_START) != -1){
            return replaceScheduleProperty(schedule, result);
        }
        return result;
    }
    
    protected Object executeRequest(String methodName, AmazonWebServiceRequest request) throws Throwable{
        setupRequest(request);
        Method method = webServiceClient.getClass().getMethod(methodName, new Class[]{request.getClass()});
        try{
            return method.invoke(webServiceClient, new Object[]{request});
        }catch(InvocationTargetException e){
            throw e.getTargetException();
        }
    }
    
    protected AmazonWebServiceRequest setupRequest(AmazonWebServiceRequest request){
        if(sdkClientExecutionTimeout > 0){
            request.setSdkClientExecutionTimeout(sdkClientExecutionTimeout);
        }
        if(sdkRequestTimeout > 0){
            request.setSdkRequestTimeout(sdkRequestTimeout);
        }
        return request;
    }
    
    protected ConvertMapping getConvertMapping(List convertMappings, String taskName){
        if(convertMappings == null){
            return null;
        }
        DefaultSchedule schedule = new DefaultSchedule();
        schedule.setTaskName(taskName);
        for(int i = 0; i < convertMappings.size(); i++){
            ConvertMapping mapping = (ConvertMapping)convertMappings.get(i);
            if(mapping.isMatch(schedule)){
                return mapping;
            }
        }
        return null;
    }
    
    protected ConvertMapping getInputConvertMapping(String taskName){
        return getConvertMapping(inputConvertMappings, taskName);
    }
    
    protected ConvertMapping getOutputConvertMapping(String taskName){
        return getConvertMapping(outputConvertMappings, taskName);
    }
    
    protected void addAutoInputConvertMappings(Converter converter) throws Exception{
        Class bindType = null;
        Method[] methods = webServiceClient.getClass().getMethods();
        for(int i = 0; i < methods.length; i++){
            if(methods[i].getParameterTypes().length == 1
                && AmazonWebServiceRequest.class.isAssignableFrom(methods[i].getParameterTypes()[0])
            ){
                String taskName = methods[i].getName();
                if(getInputConvertMapping(taskName) == null){
                    BindingConvertMapping bindingMapping = new BindingConvertMapping();
                    bindingMapping.setTaskName(taskName);
                    bindingMapping.setBindType(methods[i].getParameterTypes()[0]);
                    bindingMapping.setConverter(converter);
                    addInputConvertMapping(bindingMapping);
                }
            }
        }
    }
    
    protected void addInputConvertMapping(String taskName, Converter converter) throws Exception{
        Class bindType = null;
        Method[] methods = webServiceClient.getClass().getMethods();
        for(int i = 0; i < methods.length; i++){
            if(taskName.equals(methods[i].getName())
                && methods[i].getParameterTypes().length == 1
                && AmazonWebServiceRequest.class.isAssignableFrom(methods[i].getParameterTypes()[0])
            ){
                bindType = methods[i].getParameterTypes()[0];
                break;
            }
        }
        if(bindType == null){
            throw new IllegalArgumentException("Method is not found. client=" + webServiceClient.getClass().getName() + ", taskName=" + taskName);
        }
        BindingConvertMapping bindingMapping = new BindingConvertMapping();
        bindingMapping.setTaskName(taskName);
        bindingMapping.setBindType(bindType);
        bindingMapping.setConverter(converter);
        addInputConvertMapping(bindingMapping);
    }
    
    protected void addAutoOutputConvertMappings(Converter converter) throws Exception{
        if(getOutputConvertMapping(null) == null){
            addOutputConvertMapping(null, converter);
        }
    }
    
    protected void addOutputConvertMapping(String taskName, Converter converter) throws Exception{
        ConvertMapping mapping = new ConvertMapping();
        mapping.setTaskName(taskName);
        mapping.setConverter(converter);
        addOutputConvertMapping(mapping);
    }
}
