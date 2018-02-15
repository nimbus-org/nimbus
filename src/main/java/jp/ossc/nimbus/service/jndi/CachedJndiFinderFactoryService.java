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
package jp.ossc.nimbus.service.jndi;

import java.util.*;
import javax.naming.NamingException;

import jp.ossc.nimbus.core.*;

/**
 * JNDIファインダーサービスファクトリ。<p>
 * {@link CachedJndiFinderService}を生成するファクトリサービスである。<br>
 *
 * @author M.Takata
 * @see CachedJndiFinderService
 */
public class CachedJndiFinderFactoryService extends ServiceFactoryServiceBase
 implements CachedJndiFinderFactoryServiceMBean{
    
    private static final long serialVersionUID = -6703211248228870723L;
    
    private final CachedJndiFinderService template
         = new CachedJndiFinderService();
    
    /**
     * {@link CachedJndiFinderService}サービスを生成する。<p>
     *
     * @return CashedJndiFinderServiceサービス
     * @exception Exception CashedJndiFinderServiceの生成・起動に失敗した場合
     * @see CachedJndiFinderService
     */
    protected Service createServiceInstance() throws Exception{
        CachedJndiFinderService finder = new CachedJndiFinderService();
        finder.setEnvironment(getEnvironment());
        finder.setPrefix(getPrefix());
        finder.setCacheMapServiceName(getCacheMapServiceName());
        finder.setRetryCount(getRetryCount());
        finder.setRetryInterval(getRetryInterval());
        return finder;
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public void setEnvironment(Properties prop){
        template.setEnvironment(prop);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final CachedJndiFinderService finder
                 = (CachedJndiFinderService)instances.next();
            finder.setEnvironment(prop);
        }
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public Properties getEnvironment() throws NamingException{
        return template.getEnvironment();
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public void setPrefix(String prefix){
        template.setPrefix(prefix);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final CachedJndiFinderService finder
                 = (CachedJndiFinderService)instances.next();
            finder.setPrefix(prefix);
        }
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public String getPrefix(){
        return template.getPrefix();
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public void setCacheMapServiceName(ServiceName name){
        template.setCacheMapServiceName(name);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final CachedJndiFinderService finder
                 = (CachedJndiFinderService)instances.next();
            finder.setCacheMapServiceName(name);
        }
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public ServiceName getCacheMapServiceName(){
        return template.getCacheMapServiceName();
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public void setRetryCount(int num){
        template.setRetryCount(num);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final CachedJndiFinderService finder
                 = (CachedJndiFinderService)instances.next();
            finder.setRetryCount(num);
        }
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public int getRetryCount(){
        return template.getRetryCount();
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public void setRetryInterval(long interval){
        template.setRetryInterval(interval);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final CachedJndiFinderService finder
                 = (CachedJndiFinderService)instances.next();
            finder.setRetryInterval(interval);
        }
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public long getRetryInterval(){
        return template.getRetryInterval();
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public void setRetryExceptionClassNames(String[] classNames){
        template.setRetryExceptionClassNames(classNames);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final CachedJndiFinderService finder
                 = (CachedJndiFinderService)instances.next();
            finder.setRetryExceptionClassNames(classNames);
        }
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public String[] getRetryExceptionClassNames(){
        return template.getRetryExceptionClassNames();
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public void setAliveCheckJNDIServer(boolean isCheck){
        template.setAliveCheckJNDIServer(isCheck);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final CachedJndiFinderService finder
                 = (CachedJndiFinderService)instances.next();
            finder.setAliveCheckJNDIServer(isCheck);
        }
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public boolean isAliveCheckJNDIServer(){
        return template.isAliveCheckJNDIServer();
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public void setAliveCheckJNDIServerInterval(long interval){
        template.setAliveCheckJNDIServerInterval(interval);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final CachedJndiFinderService finder
                 = (CachedJndiFinderService)instances.next();
            finder.setAliveCheckJNDIServerInterval(interval);
        }
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public long getAliveCheckJNDIServerInterval(){
        return template.getAliveCheckJNDIServerInterval();
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public void setLoggingDeadJNDIServer(boolean isOutput){
        template.setLoggingDeadJNDIServer(isOutput);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final CachedJndiFinderService finder
                 = (CachedJndiFinderService)instances.next();
            finder.setLoggingDeadJNDIServer(isOutput);
        }
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public boolean isLoggingDeadJNDIServer(){
        return template.isLoggingDeadJNDIServer();
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public void setLoggingRecoverJNDIServer(boolean isOutput){
        template.setLoggingRecoverJNDIServer(isOutput);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final CachedJndiFinderService finder
                 = (CachedJndiFinderService)instances.next();
            finder.setLoggingRecoverJNDIServer(isOutput);
        }
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public boolean isLoggingRecoverJNDIServer(){
        return template.isLoggingRecoverJNDIServer();
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public void setDeadJNDIServerLogMessageId(String id){
        template.setDeadJNDIServerLogMessageId(id);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final CachedJndiFinderService finder
                 = (CachedJndiFinderService)instances.next();
            finder.setDeadJNDIServerLogMessageId(id);
        }
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public String getDeadJNDIServerLogMessageId(){
        return template.getDeadJNDIServerLogMessageId();
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public void setRecoverJNDIServerLogMessageId(String id){
        template.setRecoverJNDIServerLogMessageId(id);
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final CachedJndiFinderService finder
                 = (CachedJndiFinderService)instances.next();
            finder.setRecoverJNDIServerLogMessageId(id);
        }
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public String getRecoverJNDIServerLogMessageId(){
        return template.getRecoverJNDIServerLogMessageId();
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public boolean isAliveJNDIServer(){
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        if(instances.hasNext()){
            final CachedJndiFinderService finder
                 = (CachedJndiFinderService)instances.next();
            return finder.isAliveJNDIServer();
        }else{
            return false;
        }
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public void clearCache(){
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final CachedJndiFinderService finder
                 = (CachedJndiFinderService)instances.next();
            finder.clearCache();
        }
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public void clearCache(String name){
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final CachedJndiFinderService finder
                 = (CachedJndiFinderService)instances.next();
            finder.clearCache(name);
        }
    }
    
    // CashedJndiFinderServiceFactoryMBeanのJavaDoc
    public String listContext() throws NamingException{
        final Set instanceSet = getManagedInstanceSet();
        final Iterator instances = instanceSet.iterator();
        while(instances.hasNext()){
            final CachedJndiFinderService finder
                 = (CachedJndiFinderService)instances.next();
            return finder.listContext();
        }
        return null;
    }
}
