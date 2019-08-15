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
import java.io.File;
import java.net.URL;

import jp.ossc.nimbus.io.RecurciveSearchFile;

/**
 * サービス定義をディレクトリ指定でロードするサービス。<p>
 *
 * @author M.Takata
 */
public class DefaultServiceLoadersService extends ServiceBase implements DefaultServiceLoadersServiceMBean{
    
    protected String serviceDir;
    protected String serviceFileFilter;
    protected boolean isManagedServiceLoader;
    protected ServiceName cryptServiceName;
    protected boolean isValidate;
    protected boolean isCheckLoadManagerCompleted;
    protected String[] checkLoadManagerNames;
    protected String serviceManagerClassName;
    
    protected RecurciveSearchFile serviceDirFile;
    protected List serviceLoaders;
    
    public void setServiceDir(String path){
        serviceDir = path;
    }
    public String getServiceDir(){
        return serviceDir;
    }
    
    public void setServiceFileFilter(String regex){
        serviceFileFilter = regex;
    }
    public String getServiceFileFilter(){
        return serviceFileFilter;
    }
    
    public void setManagedServiceLoader(boolean isManage){
        isManagedServiceLoader = isManage;
    }
    public boolean isManagedServiceLoader(){
        return isManagedServiceLoader;
    }
    
    public void setCryptServiceName(ServiceName name){
        cryptServiceName = name;
    }
    public ServiceName getCryptServiceName(){
        return cryptServiceName;
    }
    
    public void setValidate(boolean validate){
        isValidate = validate;
    }
    public boolean isValidate(){
        return isValidate;
    }
    
    public void setCheckLoadManagerCompleted(boolean isCheck){
        isCheckLoadManagerCompleted = isCheck;
    }
    public boolean isCheckLoadManagerCompleted(){
        return isCheckLoadManagerCompleted;
    }
    
    public void setCheckLoadManagerCompletedBy(String[] managerNames){
        checkLoadManagerNames = managerNames;
    }
    public String[] getCheckLoadManagerCompletedBy(){
        return checkLoadManagerNames;
    }
    
    public void setServiceManagerClassName(String className){
        serviceManagerClassName = className;
    }
    public String getServiceManagerClassName(){
        return serviceManagerClassName;
    }
    
    public void createService() throws Exception{
    }
    
    public void startService() throws Exception{
        if(serviceDir == null || serviceDir.length() == 0){
            throw new IllegalArgumentException("ServiceDir must be specified.");
        }
        
        serviceDirFile = new RecurciveSearchFile(serviceDir);
        if(!serviceDirFile.exists()){
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL serviceDirURL = classLoader.getResource(serviceDir);
            if(serviceDirURL == null){
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
                    RecurciveSearchFile tmpDir = new RecurciveSearchFile(serviceDefDir, serviceDir);
                    if(tmpDir.exists()){
                        serviceDirFile = tmpDir;
                    }
                }
            }else{
                serviceDirFile = new RecurciveSearchFile(serviceDirURL.getFile());
            }
        }
        String[] paths = serviceFileFilter == null ? serviceDirFile.listAllTree() : serviceDirFile.listAllTree(serviceFileFilter);
        if(paths == null || paths.length == 0){
            throw new IllegalArgumentException("Service definition file not found. dir=" + serviceDirFile + ", filter=" + serviceFileFilter);
        }
        Arrays.sort(paths);
        serviceLoaders = new ArrayList();
        for(int i = 0, imax = paths.length; i < imax; i++){
            DefaultServiceLoaderService serviceLoader = new DefaultServiceLoaderService();
            if(getServiceManagerName() != null && getServiceName() != null && isManagedServiceLoader){
                serviceLoader.setServiceManagerName(getServiceManagerName());
                serviceLoader.setServiceName(getServiceName() + "$" + paths[i]);
            }
            serviceLoader.create();
            serviceLoader.setServiceURL(Utility.convertServicePathToURL(paths[i]));
            if(cryptServiceName != null){
                serviceLoader.setCryptServiceName(cryptServiceName);
            }
            serviceLoader.setValidate(isValidate);
            if(serviceManagerClassName != null){
                serviceLoader.setServiceManagerClassName(serviceManagerClassName);
            }
            if(i == imax - 1 && isCheckLoadManagerCompleted){
                serviceLoader.setCheckLoadManagerCompleted(isCheckLoadManagerCompleted);
                if(checkLoadManagerNames != null){
                    serviceLoader.setCheckLoadManagerCompletedBy(checkLoadManagerNames);
                }
            }
            serviceLoaders.add(serviceLoader);
        }
        for(int i = 0; i < serviceLoaders.size(); i++){
            DefaultServiceLoaderService serviceLoader = (DefaultServiceLoaderService)serviceLoaders.get(i);
            serviceLoader.start();
        }
    }
    
    public void stopService() throws Exception{
        if(serviceLoaders != null){
            for(int i = serviceLoaders.size(); --i >= 0;){
                DefaultServiceLoaderService serviceLoader = (DefaultServiceLoaderService)serviceLoaders.get(i);
                serviceLoader.stop();
                serviceLoader.destroy();
                serviceLoaders.remove(i);
            }
        }
    }
}