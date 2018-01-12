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
package jp.ossc.nimbus.service.proxy.invoker;

import jp.ossc.nimbus.core.*;

/**
 * {@link RemoteClientMethodCallInvokerService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see RemoteClientMethodCallInvokerService
 */
public interface RemoteClientMethodCallInvokerServiceMBean
 extends ServiceBaseMBean{
    
    /**
     * {@link jp.ossc.nimbus.service.proxy.RemoteServerInvoker RemoteServerInvoker}インタフェースを実装したRMIオブジェクトをlookupする{@link jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder}サービスのサービス名を設定する。<p>
     *
     * @param name JndiFinderサービスのサービス名
     */
    public void setJndiFinderServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.proxy.RemoteServerInvoker RemoteServerInvoker}インタフェースを実装したRMIオブジェクトをlookupする{@link jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder}サービスのサービス名を設定する。<p>
     *
     * @return JndiFinderサービスのサービス名
     */
    public ServiceName getJndiFinderServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.proxy.RemoteServerInvoker RemoteServerInvoker}インタフェースを実装したRMIオブジェクトをlookupする{@link jp.ossc.nimbus.service.repository.Repository Repository}サービスのサービス名を設定する。<p>
     *
     * @param name Repositoryサービスのサービス名
     */
    public void setJndiRepositoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.proxy.RemoteServerInvoker RemoteServerInvoker}インタフェースを実装したRMIオブジェクトをlookupする{@link jp.ossc.nimbus.service.repository.Repository Repository}サービスのサービス名を取得する。<p>
     *
     * @return Repositoryサービスのサービス名
     */
    public ServiceName getJndiRepositoryServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.proxy.RemoteServerInvoker RemoteServerInvoker}インタフェースを実装したRMIオブジェクトのJNDI名を設定する。<p>
     * この属性を設定してなく、{@link #setRemoteServiceName(ServiceName)}が設定されている場合は、そこで設定されたリモート呼び出しするサービスの{@link ServiceName}から、"マネージャ名/サービス名"というJNDI名が適用される。<br>
     * どちらも設定されていない場合でも、{@link jp.ossc.nimbus.service.aop.InvocationContext#getTargetObject() InvocationContext.getTargetObject()}でリモート呼び出しするサービスのサービス名が取得できれば、上記と同じようにJNDI名が適用される。いずれの方法でもサービス名が取得できない場合は、呼び出し時に例外が発生する。<br>
     *
     * @param name RMIオブジェクトのJNDI名
     */
    public void setRemoteServerJndiName(String name);
    
    /**
     * {@link jp.ossc.nimbus.service.proxy.RemoteServerInvoker RemoteServerInvoker}インタフェースを実装したRMIオブジェクトのJNDI名を取得する。<p>
     *
     * @return RMIオブジェクトのJNDI名
     */
    public String getRemoteServerJndiName();
    
    /**
     * 呼び出したいリモートサーバのサービスのサービス名を設定する。<p>
     * ここで指定されたサービス名は、{@link jp.ossc.nimbus.service.aop.InvocationContext#setTargetObject(Object) InvocationContext.setTargetObject(Object)}で、呼び出し対象のサービスとして伝播される。<br>
     *
     * @param name 呼び出したいリモートサーバのサービスのサービス名
     */
    public void setRemoteServiceName(ServiceName name);
    
    /**
     * 呼び出したいリモートサーバのサービスのサービス名を取得する。<p>
     *
     * @return 呼び出したいリモートサーバのサービスのサービス名
     */
    public ServiceName getRemoteServiceName();
    
    /**
     * リモートのサービスを呼び出す時の、引数及び戻り値を直列化する{@link jp.ossc.nimbus.service.io.Externalizer Externalizer}サービスのサービス名を設定する。<p>
     *
     * @param name Externalizerサービスのサービス名
     */
    public void setExternalizerServiceName(ServiceName name);
    
    /**
     * リモートのサービスを呼び出す時の、引数及び戻り値を直列化する{@link jp.ossc.nimbus.service.io.Externalizer Externalizer}サービスのサービス名を取得する。<p>
     *
     * @return Externalizerサービスのサービス名
     */
    public ServiceName getExternalizerServiceName();
    
    /**
     * このInvokerが生存しているかどうかを判定する。<p>
     *
     * @return 生存している場合true
     */
    public boolean isAlive();
}
