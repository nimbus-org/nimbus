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

/**
 * 汎用サービスファクトリサービス。<p>
 *
 * @author M.Takata
 */
public class GenericsServiceFactoryService extends GenericsFactoryService{
    
    private static final long serialVersionUID = 2328082601184640004L;
    
    /**
     * このファクトリの開始処理を行う。<p>
     * {@link #getManagedInstanceSet()}を呼び出して、このファクトリが管理しているサービスを取得し、開始させる。<br>
     * 
     * @exception Exception このファクトリが管理しているサービスの開始に失敗した場合
     */
    public void startService() throws Exception{
        
        super.startService();
        
        final Set managedInstances = getManagedInstanceSet();
        if(managedInstances != null){
            final Iterator services = managedInstances.iterator();
            while(services.hasNext()){
                final Service service = (Service)services.next();
                service.start();
            }
        }
    }
    
    /**
     * このファクトリの停止処理を行う。<p>
     * {@link #getManagedInstanceSet()}を呼び出して、このファクトリが管理しているサービスを取得し、停止させる。<br>
     * 
     * @exception Exception このファクトリが管理しているサービスの停止に失敗した場合
     */
    public void stopService() throws Exception{
        final Set managedInstances = getManagedInstanceSet();
        if(managedInstances != null){
            final Iterator services = managedInstances.iterator();
            while(services.hasNext()){
                final Service service = (Service)services.next();
                service.stop();
            }
        }
        super.stopService();
    }
    
    /**
     * このファクトリの破棄処理を行う。<p>
     * {@link #getManagedInstanceSet()}を呼び出して、このファクトリが管理しているサービスを取得し、破棄させる。<br>
     * 
     * @exception Exception このファクトリが管理しているサービスの破棄に失敗した場合
     */
    public void destroyService() throws Exception{
        final Set managedInstances = getManagedInstanceSet();
        if(managedInstances != null){
            final Iterator services = managedInstances.iterator();
            while(services.hasNext()){
                final Service service = (Service)services.next();
                service.destroy();
            }
        }
        super.destroyService();
    }
    
    /**
     * Service内のログ出力に使用する{@link jp.ossc.nimbus.service.log.Logger}サービスの名前を設定する。<p>
     * {@link #getManagedInstanceSet()}を呼び出して、このファクトリが管理しているサービスを取得し、サービスが{@link ServiceBase}のインスタンスであれば、{@link ServiceBase#setSystemLoggerServiceName(ServiceName)}を呼び出す。<br>
     *
     * @param name Service内のログ出力に使用する{@link jp.ossc.nimbus.service.log.Logger}サービスの名前
     * @see #getSystemLoggerServiceName()
     */
    public void setSystemLoggerServiceName(ServiceName name){
        super.setSystemLoggerServiceName(name);
        final Set managedInstances = getManagedInstanceSet();
        if(managedInstances != null){
            final Iterator services = managedInstances.iterator();
            while(services.hasNext()){
                final Service service = (Service)services.next();
                if(service instanceof ServiceBase){
                    ((ServiceBase)service).setSystemLoggerServiceName(name);
                }
            }
        }
    }
    
    /**
     * Service内でのメッセージ取得に使用する{@link jp.ossc.nimbus.service.message.MessageRecordFactory}サービスの名前を設定する。<p>
     * {@link #getManagedInstanceSet()}を呼び出して、このファクトリが管理しているサービスを取得し、サービスが{@link ServiceBase}のインスタンスであれば、{@link ServiceBase#setSystemMessageRecordFactoryServiceName(ServiceName)}を呼び出す。<br>
     *
     * @param name Service内でのメッセージ取得に使用する{@link jp.ossc.nimbus.service.message.MessageRecordFactory}サービスの名前
     * @see #getSystemMessageRecordFactoryServiceName()
     */
    public void setSystemMessageRecordFactoryServiceName(
        final ServiceName name
    ){
        super.setSystemMessageRecordFactoryServiceName(name);
        final Set managedInstances = getManagedInstanceSet();
        if(managedInstances != null){
            final Iterator services = managedInstances.iterator();
            while(services.hasNext()){
                final Service service = (Service)services.next();
                if(service instanceof ServiceBase){
                    ((ServiceBase)service)
                        .setSystemMessageRecordFactoryServiceName(name);
                }
            }
        }
    }
    
    /**
     * このファクトリが提供するオブジェクトのインスタンスを生成する。<p>
     *
     * @return このファクトリが提供するオブジェクトのインスタンス
     * @exception Exception 生成中に例外が発生した場合
     */
    protected Object createInstance() throws Exception{
        final Service service = (Service)instantiateClass.newInstance();
        if(service == null){
            return null;
        }
        if(isManagement() && getServiceManagerName() != null){
            service.setServiceName(
                getServiceName() + '$' + getManagedInstanceSet().size()
            );
            service.setServiceManagerName(getServiceManagerName());
        }else  if(service instanceof ServiceBase){
            final ServiceBase base = (ServiceBase)service;
            if(manager != null){
                base.logger.setDefaultLogger(manager.getLogger());
                if(getSystemLoggerServiceName() == null){
                    base.logger.setLogger(manager.getLogger());
                }
                base.message.setDefaultMessageRecordFactory(
                    manager.getMessageRecordFactory()
                );
                if(getSystemMessageRecordFactoryServiceName() == null){
                    base.message.setMessageRecordFactory(
                        manager.getMessageRecordFactory()
                    );
                }
            }
            if(getSystemLoggerServiceName() != null){
                base.setSystemLoggerServiceName(
                    getSystemLoggerServiceName()
                );
            }
            if(getSystemMessageRecordFactoryServiceName() != null){
                base.setSystemMessageRecordFactoryServiceName(
                    getSystemMessageRecordFactoryServiceName()
                );
            }
        }
        service.create();
        setAttributes(service);
        service.start();
        return service;
    }
    
    public void release(Object obj){
        final Service service = (Service)obj;
        service.stop();
        service.destroy();
        super.release(service);
    }
}