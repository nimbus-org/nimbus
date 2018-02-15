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
 * {@link RemoteClientEJBCallInvokerService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see RemoteClientEJBCallInvokerService
 */
public interface RemoteClientEJBCallInvokerServiceMBean
 extends ServiceBaseMBean{
    
    /**
     * {@link jp.ossc.nimbus.service.proxy.RemoteServerInvoker RemoteServerInvoker}インタフェースを実装したEJBを取得する{@link jp.ossc.nimbus.service.ejb.EJBFactory EJBFactory}サービスのサービス名を設定する。<p>
     *
     * @param name EJBFactoryサービスのサービス名
     */
    public void setEJBFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.proxy.RemoteServerInvoker RemoteServerInvoker}インタフェースを実装したEJBを取得する{@link jp.ossc.nimbus.service.ejb.EJBFactory EJBFactory}サービスのサービス名を取得する。<p>
     *
     * @return EJBFactoryサービスのサービス名
     */
    public ServiceName getEJBFactoryServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.proxy.RemoteServerInvoker RemoteServerInvoker}インタフェースを実装したEJBのEJBHomeのJNDI名を設定する。<p>
     *
     * @param name RemoteServerInvoker EJBのEJBHomeのJNDI名
     */
    public void setRemoteServerEJBJndiName(String name);
    
    /**
     * {@link jp.ossc.nimbus.service.proxy.RemoteServerInvoker RemoteServerInvoker}インタフェースを実装したEJBのEJBHomeのJNDI名を取得する。<p>
     *
     * @return RemoteServerInvoker EJBのEJBHomeのJNDI名
     */
    public String getRemoteServerEJBJndiName();
    
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
}
