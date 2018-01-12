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
package jp.ossc.nimbus.service.publish;

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link DistributedConnectionFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see DistributedConnectionFactoryService
 */
public interface DistributedConnectionFactoryServiceMBean extends ServiceBaseMBean{
    
    /**
     * 分散する物理コネクション数を設定する。<p>
     * デフォルトは、1で分散しない。<br>
     *
     * @param size 物理コネクション数
     */
    public void setDistributedSize(int size);
    
    /**
     * 分散する物理コネクション数を取得する。<p>
     *
     * @return 物理コネクション数
     */
    public int getDistributedSize();
    
    /**
     * 物理コネクションを生成する{@link ServerConnectionFactory}サービスのファクトリサービス名を設定する。<p>
     *
     * @param name ServerConnectionFactoryサービスのファクトリサービス名
     */
    public void setConnectionFactoryFactoryServiceName(ServiceName name);
    
    /**
     * 物理コネクションを生成する{@link ServerConnectionFactory}サービスのファクトリサービス名を取得する。<p>
     *
     * @return ServerConnectionFactoryサービスのファクトリサービス名
     */
    public ServiceName getConnectionFactoryFactoryServiceName();
    
    /**
     * 分散させた物理コネクションをまとめた論理コネクションを生成する{@link ClientConnectionFactory}をバインドするJNDI名を設定する。<p>
     * デフォルトは、{@link ClientConnectionFactory#DEFAULT_JNDI_NAME}。<br>
     *
     * @param name JNDI名
     * @see ClientConnectionFactory#DEFAULT_JNDI_NAME
     */
    public void setJndiName(String name);
    
    /**
     * 分散させた物理コネクションをまとめた論理コネクションを生成する{@link ClientConnectionFactory}をバインドするJNDI名を取得する。<p>
     *
     * @return JNDI名
     */
    public String getJndiName();
    
    /**
     * 分散させた物理コネクションをまとめた論理コネクションを生成する{@link ClientConnectionFactory}をバインドする{@link jp.ossc.nimbus.service.repository.Repository Repository}サービスのサービス名を設定する。<p>
     *
     * @param name Repositoryサービスのサービス名
     */
    public void setJndiRepositoryServiceName(ServiceName name);
    
    /**
     * 分散させた物理コネクションをまとめた論理コネクションを生成する{@link ClientConnectionFactory}をバインドする{@link jp.ossc.nimbus.service.repository.Repository Repository}サービスのサービス名を取得する。<p>
     *
     * @return Repositoryサービスのサービス名
     */
    public ServiceName getJndiRepositoryServiceName();
    
    /**
     * リモートオブジェクトが呼び出しを受信するポート番号を設定する。<p>
     * 指定しない場合は、匿名ポートが使用される。<br>
     *
     * @param port ポート番号
     */
    public void setRMIPort(int port);
    
    /**
     * リモートオブジェクトが呼び出しを受信するポート番号を取得する。<p>
     *
     * @return ポート番号
     */
    public int getRMIPort();
}
