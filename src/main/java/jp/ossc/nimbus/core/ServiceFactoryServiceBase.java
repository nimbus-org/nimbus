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
 * サービスファクトリサービス基底クラス。<p>
 * {@link FactoryService}の中でも、サービスを生成するFactoryServiceを実装する際の基底クラスである。<p>
 *
 * @author M.Takata
 */
public abstract class ServiceFactoryServiceBase extends FactoryServiceBase{
    
    private static final long serialVersionUID = 3957308112143950640L;
    
    /**
     * コンストラクタ。<p>
     */
    public ServiceFactoryServiceBase(){
        super();
    }
    
    public void release(Object obj){
        final Service service = (Service)obj;
        service.stop();
        service.destroy();
        super.release(service);
    }
    
    /**
     * このファクトリが提供するオブジェクトのインスタンスを生成する。<p>
     * {@link #createServiceInstance()}で生成したサービスのインスタンスに対して、以下の処理を行う。<br>
     * <ol>
     *   <li>{@link #isManagement()}がtrueの場合、生成したサービスにサービス名とサービスマネージャ名を設定する。この際、サービス名は、このファクトリサービスのサービス名の後ろに"$" + "管理されている生成したサービスの通し番号"を付与したものである。また、サービスマネージャ名は、このファクトリサービスのサービスマネージャ名と同じである。</li>
     *   <li>生成したサービスの生成処理（{@link Service#create()}）。</li>
     *   <li>生成したサービスの開始処理（{@link Service#start()}）。</li>
     *   <li>生成したサービスが{@link ServiceBase}を継承している場合は、このファクトリサービスに設定されている{@link jp.ossc.nimbus.service.log.Logger Logger}と{@link jp.ossc.nimbus.service.message.MessageRecordFactory MessageRecordFactory}を、生成したサービスにも設定する。</li>
     * </ol>
     *
     * @return このファクトリが提供するオブジェクトのインスタンス
     * @exception Exception 生成中に例外が発生した場合
     */
    protected final Object createInstance() throws Exception{
        return createInstance(false);
    }
    
    protected Object createTemplate() throws Exception{
        return createInstance(true);
    }
    
    protected final Object createInstance(boolean isTemplate) throws Exception{
        final Service service = createServiceInstance();
        if(service == null){
            return null;
        }
        if(!isTemplate && isManagement() && getServiceManagerName() != null){
            service.setServiceName(
                getServiceName() + '$' + getManagedInstanceSet().size()
            );
            service.setServiceManagerName(getServiceManagerName());
        }
        if(service.getState() == DESTROYED){
            service.create();
        }
        if(service instanceof ServiceBase){
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
        if(service.getState() == CREATED){
            service.start();
        }
        return service;
    }
    
    /**
     * このファクトリが提供するサービスのインスタンスを生成する。<p>
     *
     * @return このファクトリが提供するサービスのインスタンス
     * @exception Exception 生成中に例外が発生した場合
     */
    protected abstract Service createServiceInstance() throws Exception;
    
    /**
     * このファクトリの開始後処理を行う。<p>
     * {@link #getManagedInstanceSet()}を呼び出して、このファクトリが管理しているサービスを取得し、開始させる。<br>
     * 
     * @exception Exception このファクトリが管理しているサービスの開始に失敗した場合
     */
    protected void postStartService() throws Exception{
        
        super.postStartService();
        
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
     * このファクトリの停止後処理を行う。<p>
     * {@link #getManagedInstanceSet()}を呼び出して、このファクトリが管理しているサービスを取得し、停止させる。<br>
     * 
     * @exception Exception このファクトリが管理しているサービスの停止に失敗した場合
     */
    protected void postStopService() throws Exception{
        final Set managedInstances = getManagedInstanceSet();
        if(managedInstances != null){
            final Iterator services = managedInstances.iterator();
            while(services.hasNext()){
                final Service service = (Service)services.next();
                service.stop();
            }
        }
        super.postStopService();
    }
    
    /**
     * このファクトリの破棄後処理を行う。<p>
     * {@link #getManagedInstanceSet()}を呼び出して、このファクトリが管理しているサービスを取得し、破棄させる。<br>
     * 
     * @exception Exception このファクトリが管理しているサービスの破棄に失敗した場合
     */
    protected void postDestroyService() throws Exception{
        final Set managedInstances = getManagedInstanceSet();
        if(managedInstances != null){
            final Iterator services = managedInstances.iterator();
            while(services.hasNext()){
                final Service service = (Service)services.next();
                service.destroy();
            }
        }
        super.postDestroyService();
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
}
