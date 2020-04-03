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
package jp.ossc.nimbus.service.scheduler2.k8s;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.Config;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.PropertyAccess;
import jp.ossc.nimbus.beans.Property;
import jp.ossc.nimbus.beans.NoSuchPropertyException;
import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.service.scheduler2.*;
import jp.ossc.nimbus.util.converter.*;

/**
 * Kuberneteseのコントロールプレーンを呼び出すスケジュール実行。<p>
 *
 * @author M.Takata
 */
public class KuberneteseScheduleExecutorService extends AbstractScheduleExecutorService implements KuberneteseScheduleExecutorServiceMBean{
    
    {
        type = DEFAULT_EXECUTOR_TYPE;
    }
    
    protected String url;
    protected boolean isValidateSSL = true;
    protected String user;
    protected String password;
    protected String token;
    protected String configFilePath;
    protected String configFileEncoding;
    protected int writeTimeout = 3000;
    protected int readTimeout = 3000;
    protected Class[] apiClasses;
    
    protected PropertyAccess propertyAccess;
    protected transient ApiClient client;
    protected transient Map apiMap;
    protected Set notApiMethodNames;
    
    public void setApiClasses(Class[] classes){
        apiClasses = classes;
    }
    public Class[] getApiClasses(){
        return apiClasses;
    }
    
    public void setURL(String url){
        this.url = url;
    }
    public String getURL(){
        return url;
    }
    
    public void setValidateSSL(boolean isValidate){
        isValidateSSL = isValidate;
    }
    public boolean isValidateSSL(){
        return isValidateSSL;
    }
    
    public void setUser(String user){
        this.user = user;
    }
    public String getUser(){
        return user;
    }
    
    public void setPassword(String password){
        this.password = password;
    }
    public String getPassword(){
        return password;
    }
    
    public void setToken(String token){
        this.token = token;
    }
    public String getToken(){
        return token;
    }
    
    public void setConfigFilePath(String path){
        configFilePath = path;
    }
    public String getConfigFilePath(){
        return configFilePath;
    }
    
    public void setConfigFileEncoding(String encode){
        configFileEncoding = encode;
    }
    public String getConfigFileEncoding(){
        return configFileEncoding;
    }
    
    public void setWriteTimeout(int millis){
        writeTimeout = millis;
    }
    public int getWriteTimeout(){
        return writeTimeout;
    }
    
    public void setReadTimeout(int millis){
        readTimeout = millis;
    }
    public int getReadTimeout(){
        return readTimeout;
    }
    
    public void setNotApiMethodNames(Set methodNames){
        notApiMethodNames = methodNames;
    }
    public Set getNotApiMethodNames(){
        return notApiMethodNames == null ? null : new HashSet(notApiMethodNames);
    }
    
    public void createService() throws Exception{
        propertyAccess = new PropertyAccess();
    }
    
    public void startService() throws Exception{
        
        if(url != null){
            if(user != null){
                client = Config.fromUserPassword(url, user, password, isValidateSSL);
            }else if(token != null){
                client = Config.fromToken(url, token, isValidateSSL);
            }else{
                client = Config.fromUrl(url, isValidateSSL);
            }
        }else if(configFilePath != null){
            File configFile = new File(configFilePath);
            if(!configFile.exists()){
                File serviceDefDir = null;
                if(getServiceNameObject() != null){
                    ServiceMetaData metaData = ServiceManagerFactory.getServiceMetaData(getServiceNameObject());
                    if(metaData != null){
                        jp.ossc.nimbus.core.ServiceLoader loader = metaData.getServiceLoader();
                        if(loader != null){
                            String filePath = loader.getServiceURL().getFile();
                            if(filePath != null){
                                serviceDefDir = new File(filePath).getParentFile();
                            }
                        }
                    }
                }
                if(serviceDefDir != null){
                    File file = new File(serviceDefDir, configFilePath);
                    if(file.exists()){
                        configFile = file;
                    }
                }
            }
            Reader reader = configFileEncoding == null ? new FileReader(configFile) : new InputStreamReader(new FileInputStream(configFile), configFileEncoding);
            client = Config.fromConfig(reader);
        }else{
            client = Config.fromCluster();
        }
        
        if(writeTimeout > 0){
            client.setWriteTimeout(writeTimeout);
        }
        if(readTimeout > 0){
            client.setReadTimeout(readTimeout);
        }
        if(notApiMethodNames == null){
            notApiMethodNames = new HashSet();
        }
        notApiMethodNames.add("setApiClient");
        Method[] methods = Object.class.getMethods();
        for(int i = 0; i < methods.length; i++){
            notApiMethodNames.add(methods[i].getName());
        }
        
        if(apiClasses == null || apiClasses.length == 0){
            throw new IllegalArgumentException("ApiClasses is null");
        }
        apiMap = new HashMap();
        for(int i = 0; i < apiClasses.length; i++){
            apiMap.put(apiClasses[i].getSimpleName(), createAPI(client, apiClasses[i]));
        }
        BeanJSONConverter beanJSONConverter = new BeanJSONConverter();
        beanJSONConverter.setConvertType(BeanJSONConverter.OBJECT_TO_JSON);
        beanJSONConverter.setIgnoreUnknownProperty(true);
        beanJSONConverter.setOutputNullProperty(false);
        DateFormatConverter dfc = new DateFormatConverter();
        dfc.setFormat("yyyy/MM/dd HH:mm:ss.SSS");
        dfc.setConvertType(DateFormatConverter.DATE_TO_STRING);
        beanJSONConverter.setFormatConverter(java.util.Date.class, dfc);
        
        StringStreamConverter stringStreamConverter = new StringStreamConverter();
        stringStreamConverter.setConvertType(StringStreamConverter.STREAM_TO_STRING);
        
        CustomConverter customConverter = new CustomConverter();
        customConverter.add(beanJSONConverter);
        customConverter.add(stringStreamConverter);
        
        addAutoInputConvertMappings(beanJSONConverter);
        addAutoOutputConvertMappings(customConverter);
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
    
    public boolean controlState(String id, int cntrolState) throws ScheduleStateControlException {
        return false;
    }
    
    protected Schedule executeInternal(Schedule schedule) throws Throwable{
        String taskName = schedule.getTaskName();
        if(taskName == null || taskName.indexOf('.') == -1){
            throw new IllegalArgumentException("illgal task name. task name is \"apiClass.methodName\". taskName=" + taskName);
        }
        String apiName = taskName.substring(0, taskName.indexOf('.'));
        Object api = apiMap.get(apiName);
        if(api == null){
            throw new IllegalArgumentException("Not supported api. api=" + apiName);
        }
        String methodName = taskName.substring(taskName.indexOf('.') + 1);
        Object response = executeRequest(api, methodName, (Record)schedule.getInput());
        schedule.setOutput(response);
        return schedule;
    }
    
    protected Object executeRequest(Object api, String methodName, Record params) throws Throwable{
        Class[] paramTypes = new Class[params == null ? 0 : params.size()];
        for(int i = 0; i < paramTypes.length; i++){
            paramTypes[i] = params.getRecordSchema().getPropertySchema(i).getType();
        }
        Method method = api.getClass().getMethod(methodName, paramTypes);
        Object[] paramValues = new Object[paramTypes.length];
        for(int i = 0; i < paramTypes.length; i++){
            paramValues[i] = params.getProperty(i);
        }
        try{
            return method.invoke(api, paramValues);
        }catch(InvocationTargetException e){
            throw e.getTargetException();
        }
    }
    
    protected Object createAPI(ApiClient client, Class apiClass) throws Exception{
        Object api = apiClass.newInstance();
        apiClass.getMethod(
            "setApiClient",
            new Class[]{ApiClient.class}
        ).invoke(
            api,
            new Object[]{client}
        );
        return api;
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
        Iterator entries = apiMap.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry)entries.next();
            Class apiClass = entry.getValue().getClass();
            Method[] methods = apiClass.getMethods();
            for(int i = 0; i < methods.length; i++){
                if(notApiMethodNames.contains(methods[i].getName())){
                    continue;
                }
                String taskName = entry.getKey() + "." + methods[i].getName();
                if(getInputConvertMapping(taskName) == null){
                    BindingConvertMapping bindingMapping = new BindingConvertMapping();
                    bindingMapping.setTaskName(taskName);
                    StringBuilder schema = new StringBuilder();
                    Class[] paramTypes = methods[i].getParameterTypes();
                    for(int j = 0; j < paramTypes.length; j++){
                        if(j != 0){
                            schema.append('\n');
                        }
                        schema.append(':')
                              .append('p').append(j).append(',')
                              .append(paramTypes[j].getName());
                    }
                    Record record = new Record(schema.toString());
                    bindingMapping.setBindObject(record);
                    bindingMapping.setConverter(converter);
                    addInputConvertMapping(bindingMapping);
                }
            }
        }
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
