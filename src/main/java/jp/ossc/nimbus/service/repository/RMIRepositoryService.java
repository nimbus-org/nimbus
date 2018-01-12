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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import jp.ossc.nimbus.core.ServiceBase;

/**
 * RMIリポジトリサービス。<p>
 *
 * @author M.Takata
 */
public class RMIRepositoryService extends ServiceBase implements Repository, RMIRepositoryServiceMBean{
    
    private static final long serialVersionUID = -6700424417078458836L;
    
    private String host;
    private int port = Registry.REGISTRY_PORT;
    private boolean isCreateRegistry;
    
    private Registry registry;
    private Set registeredNameSet;
    
    public void setHostName(String host){
        this.host = host;
    }
    public String getHostName(){
        return host;
    }
    
    public void setPort(int port){
        this.port = port;
    }
    public int getPort(){
        return port;
    }
    
    public boolean isCreateRegistry(){
        return isCreateRegistry;
    }
    public void setCreateRegistry(boolean isCreate){
        isCreateRegistry = isCreate;
    }
    
    public void createService() throws Exception{
        registeredNameSet = Collections.synchronizedSet(new HashSet());
    }
    
    public void startService() throws Exception{
        if(isCreateRegistry){
            try{
                registry = LocateRegistry.createRegistry(port);
            }catch(RemoteException e){
                registry = LocateRegistry.getRegistry(port);
            }
        }else{
            registry = LocateRegistry.getRegistry(host, port);
        }
    }
    
    public void stopService() throws Exception{
        String[] registeredNames = (String[])registeredNameSet.toArray(new String[registeredNameSet.size()]);
        for(int i = 0; i < registeredNames.length; i++){
            unregister(registeredNames[i]);
        }
        if(isCreateRegistry){
            UnicastRemoteObject.unexportObject(registry, true);
        }
        registry = null;
    }
    
    public void destroyService() throws Exception{
        registeredNameSet = null;
        
    }
    
    public Object get(String name){
        try{
            return registry == null ? null : registry.lookup(name);
        }catch(RemoteException e){
            return null;
        }catch(NotBoundException e){
            return null;
        }
    }
    
    public boolean register(String name, Object obj){
        if(registry == null || !(obj instanceof Remote)){
            return false;
        }
        try{
            registry.bind(name, (Remote)obj);
            registeredNameSet.add(name);
            return true;
        }catch(AlreadyBoundException e){
            return false;
        }catch(RemoteException e){
            return false;
        }
    }
    
    public boolean unregister(String name){
        if(registry == null){
            return false;
        }
        try{
            registry.unbind(name);
            registeredNameSet.remove(name);
            return true;
        }catch(NotBoundException e){
            return false;
        }catch(RemoteException e){
            return false;
        }
    }
    
    public boolean isRegistered(String name){
        return registeredNameSet == null ? false : registeredNameSet.contains(name);
    }
    
    public Set nameSet(){
        return new HashSet(registeredNameSet);
        
    }
    
    public Set registeredSet(){
        Set result = new HashSet();
        Iterator names = nameSet().iterator();
        while(names.hasNext()){
            Object obj = get((String)names.next());
            if(obj != null){
                result.add(obj);
            }
        }
        return result;
    }
}