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
package jp.ossc.nimbus.service.repository;

import java.io.*;
import java.util.*;
import java.lang.management.*;
import javax.management.*;

import jp.ossc.nimbus.core.*;

/**
 * MBeanをJMXサーバに登録する{@link Repository}サービス。<p>
 * 
 * @author M.Takata
 */
public class MBeanServerRepositoryService extends ServiceBase
 implements Serializable, NotificationListener,
            NotificationFilter, MBeanServerRepositoryServiceMBean{
    
    private static final long serialVersionUID = -6480474182037404159L;
    
    private static final String NAME = "name";
    private static final String CLASS = "class";
    
    private final Map mbeanNames = new Hashtable();
    private final Map mbeans = new Hashtable();
    private int serverIndex;
    private String serverDomain;
    private String serverDefaultDomain;
    private String objectNameDomain;
    private MBeanServer server;
    private boolean isCreateMBeanServer;
    
    // MBeanServerRepositoryServiceMBeanのJavaDoc
    public void setMBeanServerDomain(String domain){
        this.serverDomain = domain;
    }
    
    // MBeanServerRepositoryServiceMBeanのJavaDoc
    public String getMBeanServerDomain(){
        return serverDomain;
    }
    
    // MBeanServerRepositoryServiceMBeanのJavaDoc
    public void setMBeanServerDefaultDomain(String domain){
        this.serverDefaultDomain = domain;
    }
    
    // MBeanServerRepositoryServiceMBeanのJavaDoc
    public String getMBeanServerDefaultDomain(){
        return serverDefaultDomain;
    }
    
    // MBeanServerRepositoryServiceMBeanのJavaDoc
    public void setMBeanServerIndex(int index){
        this.serverIndex = index;
    }
    
    // MBeanServerRepositoryServiceMBeanのJavaDoc
    public int getMBeanServerIndex(){
        return serverIndex;
    }
    
    // MBeanServerRepositoryServiceMBeanのJavaDoc
    public void setObjectNameDomain(String domain){
        this.objectNameDomain = domain;
    }
    
    // MBeanServerRepositoryServiceMBeanのJavaDoc
    public String getObjectNameDomain(){
        return objectNameDomain;
    }
    
    // MBeanServerRepositoryServiceMBeanのJavaDoc
    public void setCreateMBeanServer(boolean isCreate){
        isCreateMBeanServer = isCreate;
    }
    
    // MBeanServerRepositoryServiceMBeanのJavaDoc
    public boolean isCreateMBeanServer(){
        return isCreateMBeanServer;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        List servers = MBeanServerFactory.findMBeanServer(serverDomain);
        if(servers == null || servers.size() == 0){
            if(servers == null){
                servers = new ArrayList();
            }
            MBeanServer server = null;
            if(isCreateMBeanServer){
                server = MBeanServerFactory.createMBeanServer(serverDomain);
            }else if(serverDomain == null){
                server = ManagementFactory.getPlatformMBeanServer();
            }else{
                throw new Exception("MBeanServer not found : " + serverDomain);
            }
            servers.add(server);
        }
        if(serverDefaultDomain != null){
            for(int i = 0; i < servers.size(); i++){
                if(serverDefaultDomain.equals(((MBeanServer)servers.get(i)).getDefaultDomain())){
                    server = (MBeanServer)servers.get(i);
                    break;
                }
            }
            if(server == null){
                throw new Exception("MBeanServer not found : " + serverDefaultDomain);
            }
        }else{
            server = (MBeanServer)servers.get(servers.size() > serverIndex ? serverIndex : 0);
        }
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception サービスの破棄処理に失敗した場合
     */
    public void destroyService() throws Exception{
        server = null;
    }
    
    // RepositoryのJavaDoc
    public Object get(String name){
        final ObjectName mbeanName = (ObjectName)mbeanNames.get(name);
        if(mbeanName == null){
            return null;
        }
        if(!server.isRegistered(mbeanName)){
            return null;
        }
        return mbeans.get(name);
    }
    
    // RepositoryのJavaDoc
    public boolean register(String name, Object obj){
        try{
            final ObjectName mbeanName = convertStringToObjectName(
                name,
                obj
            );
            if(isRegistered(name)){
                unregister(name);
            }
            server.registerMBean(obj, mbeanName);
//            server.addNotificationListener(
//                mbeanName,
//                this,
//                this,
//                new MBeanHandback(mbeanName, obj)
//            );
            mbeanNames.put(name, mbeanName);
            mbeans.put(name, obj);
        }catch(InstanceAlreadyExistsException e){
            e.printStackTrace();
            return false;
        }catch(MBeanRegistrationException e){
            e.printStackTrace();
            return false;
        }catch(NotCompliantMBeanException e){
            e.printStackTrace();
            return false;
        }catch(MalformedObjectNameException e){
            // この例外は発生しないはず
            e.printStackTrace();
            return false;
//        }catch(InstanceNotFoundException e){
//            // この例外は発生しないはず
//            e.printStackTrace();
//            return false;
        }
        return true;
    }
    
    // RepositoryのJavaDoc
    public boolean unregister(String name){
        final ObjectName mbeanName = (ObjectName)mbeanNames.get(name);
        if(mbeanName == null){
            return false;
        }
        try{
//            try{
//                server.removeNotificationListener(mbeanName, this);
//            }catch(ListenerNotFoundException e){
//            }
            server.unregisterMBean(mbeanName);
            mbeanNames.remove(name);
            mbeans.remove(name);
        }catch(InstanceNotFoundException e){
            e.printStackTrace();
            return false;
        }catch(MBeanRegistrationException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    // RepositoryのJavaDoc
    public boolean isRegistered(String name){
        final ObjectName mbeanName = (ObjectName)mbeanNames.get(name);
        if(mbeanName == null){
            return false;
        }
        return server.isRegistered(mbeanName);
    }
    
    // RepositoryのJavaDoc
    public Set nameSet(){
        return new HashSet(mbeanNames.keySet());
    }
    
    // RepositoryのJavaDoc
    public Set registeredSet(){
        return new HashSet(mbeans.values());
    }
    
    public void handleNotification(Notification notification, Object handback){
        final String type = notification.getType();
        if(MBeanServerNotification.REGISTRATION_NOTIFICATION.equals(type)){
            final ObjectName mbeanName = ((MBeanHandback)handback).name;
            final String name = convertObjectNameToString(mbeanName);
            final Object obj = ((MBeanHandback)handback).mbean;
            mbeanNames.put(name, mbeanName);
            mbeans.put(name, obj);
        }else if(MBeanServerNotification
                .UNREGISTRATION_NOTIFICATION.equals(type)){
            final ObjectName name = ((MBeanHandback)handback).name;
            mbeanNames.remove(name);
            mbeans.remove(name);
        }
    }
    
    public boolean isNotificationEnabled(Notification notification){
        final String type = notification.getType();
        return MBeanServerNotification.REGISTRATION_NOTIFICATION.equals(type)
            || MBeanServerNotification.UNREGISTRATION_NOTIFICATION.equals(type);
    }
    
    private ObjectName convertStringToObjectName(String name, Object obj)
     throws MalformedObjectNameException{
        final Hashtable prop = new Hashtable();
        Object target = obj;
        while(target instanceof ServiceProxy){
            Object child = ((ServiceProxy)target).getTarget();
            if(child == target){
                break;
            }
            target = child;
        }
        final String className = target.getClass().getName();
        prop.put(CLASS, className);
        prop.put(NAME, name);
        String domainName = objectNameDomain;
        if(domainName == null){
            domainName = getServiceManagerName();
        }
        return new ObjectName(domainName, prop);
    }
    
    private String convertObjectNameToString(ObjectName name){
        return name.getKeyProperty(NAME);
    }
    
    private class MBeanHandback{
        public ObjectName name;
        public Object mbean;
        public MBeanHandback(ObjectName name, Object mbean){
            this.name = name;
            this.mbean = mbean;
        }
    }
}
