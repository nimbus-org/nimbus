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
package jp.ossc.nimbus.service.keepalive;

import java.util.List;

import jp.ossc.nimbus.core.*;

/**
 * クラスタ内でメインとなる時だけ対象となるサービスを起動させる{@link ClusterListener}サービス。<p>
 * 
 * @author M.Takata
 */
public class DefaultClusterListenerService extends ServiceBase
 implements ClusterListener, ServiceStateListener, DefaultClusterListenerServiceMBean{
    
    private static final long serialVersionUID = -1602329265459610639L;
    protected ServiceName targetServiceName;
    protected Service targetService;
    
    protected ServiceName clusterServiceName;
    protected ClusterService clusterService;
    
    public void setTargetServiceName(ServiceName name){
        targetServiceName = name;
    }
    public ServiceName getTargetServiceName(){
        return targetServiceName;
    }
    
    public void setClusterServiceName(ServiceName name){
        clusterServiceName = name;
    }
    public ServiceName getClusterServiceName(){
        return clusterServiceName;
    }
    
    public void setTargetService(Service service){
        targetService = service;
    }
    public Service getTargetService(){
        return targetService;
    }
    
    public void setClusterService(ClusterService service){
        clusterService = service;
    }
    public ClusterService getClusterService(){
        return clusterService;
    }
    
    public void startService() throws Exception{
        if(targetServiceName != null){
            ServiceManagerFactory.addServiceStateListener(
                targetServiceName,
                this
            );
        }else if(targetService != null){
            ServiceManagerFactory.addServiceStateListener(
                targetService.getServiceNameObject(),
                this
            );
        }
    }
    
    public void stopService() throws Exception{
        if(targetServiceName != null){
            ServiceManagerFactory.removeServiceStateListener(
                targetServiceName,
                this
            );
        }else if(targetService != null){
            ServiceManagerFactory.removeServiceStateListener(
                targetService.getServiceNameObject(),
                this
            );
        }
    }
    
    public void memberInit(Object myId, List members){}
    public void memberChange(List oldMembers, List newMembers){}
    public void changeMain() throws Exception{
        Service service = targetService;
        if(service == null && targetServiceName != null){
            service = ServiceManagerFactory.getService(targetServiceName);
        }
        if(service != null){
            service.start();
        }
    }
    public void changeSub(){
        Service service = targetService;
        if(service == null && targetServiceName != null){
            service = ServiceManagerFactory.getService(targetServiceName);
        }
        if(service != null){
            service.stop();
        }
    }
    
    public void stateChanged(ServiceStateChangeEvent e) throws Exception{
        ClusterService cluster = clusterService;
        if(cluster == null && clusterServiceName != null){
            try{
                cluster = (ClusterService)ServiceManagerFactory
                    .getServiceObject(clusterServiceName);
            }catch(ServiceNotFoundException ex){}
        }
        if(cluster != null){
            switch(e.getService().getState()){
            case STARTED:
                if(cluster.getState() == STOPPED){
                    try{
                        cluster.start();
                    }catch(Exception ex){
                        // TODO エラーログ出力
                    }
                }
                break;
            case STOPPING:
                if(cluster.getState() == STARTED){
                    cluster.stop();
                }
                break;
            default:
            }
        }
    }
    
    public boolean isEnabledState(int state){
        return state == STARTED || state == STOPPING;
    }
}