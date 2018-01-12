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

import java.util.Map;
import java.util.List;

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link GroupConnectionFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see GroupConnectionFactoryService
 */
public interface GroupConnectionFactoryServiceMBean extends ServiceBaseMBean{
    
    /**
     * グルーピングしたコネクションを生成する{@link ClientConnectionFactory}をバインドするJNDI名を設定する。<p>
     * デフォルトは、{@link ClientConnectionFactory#DEFAULT_JNDI_NAME}。<br>
     *
     * @param name JNDI名
     * @see ClientConnectionFactory#DEFAULT_JNDI_NAME
     */
    public void setJndiName(String name);
    
    /**
     * グルーピングしたコネクションを生成する{@link ClientConnectionFactory}をバインドするJNDI名を取得する。<p>
     *
     * @return JNDI名
     */
    public String getJndiName();
    
    /**
     * グルーピングしたコネクションを生成する{@link ClientConnectionFactory}をバインドする{@link jp.ossc.nimbus.service.repository.Repository Repository}サービスのサービス名を設定する。<p>
     *
     * @param name Repositoryサービスのサービス名
     */
    public void setJndiRepositoryServiceName(ServiceName name);
    
    /**
     * グルーピングしたコネクションを生成する{@link ClientConnectionFactory}をバインドする{@link jp.ossc.nimbus.service.repository.Repository Repository}サービスのサービス名を取得する。<p>
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
    
    /**
     * 指定されたサブジェクトにマッピングされている{@link GroupConnectionFactoryService.SubjectMapping SubjectMapping}のリストを取得する。<p>
     * 
     * @return SubjectMappingのリスト
     */
    public List getSubjectMappings(String subject);
    
    /**
     * サブジェクトのマッピングを取得する。<p>
     * 
     * @return キーがサブジェクト、値がSubjectMappingのリストとなるマップ
     */
    public Map getSubjectMappingMap();
}
