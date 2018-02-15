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
package jp.ossc.nimbus.service.soap;

import java.util.*;

import javax.xml.rpc.handler.HandlerInfo;

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link PortFactoryService}のMBeanインタフェース。<p>
 *
 * @author M.Takata
 */
public interface PortFactoryServiceMBean
 extends ServiceBaseMBean {
    
    /**
     * JAX-RPCサービスファクトリ名を設定する。<p>
     * 
     * @return JAX-RPCサービスファクトリ名
     */
    public ServiceName getJaxRpcServiceFactoryName();

    /**
     * JAX-RPCサービスファクトリ名を設定する。<p>
     * 
     * @param serviceName JAX-RPCサービスファクトリ名
     */
    public void setJaxRpcServiceFactoryName(ServiceName serviceName);

    /**
     * ポートエイリアスプロパティを取得する。<p>
     * 
     * @return キーに[ポートエイリアス名]、値に[ポート名,サービスエンドポイントインターフェース名]のプロパティ
     */
    public Properties getPortAliasProp();
    
    /**
     * ポートエイリアスプロパティを設定する。<p>
     * 
     * @param prop キーに[ポートエイリアス名]、値に[ポート名,サービスエンドポイントインターフェース名]のプロパティ
     */
    public void setPortAliasProp(Properties prop);
    
    /**
     * ハンドラ情報のリストを設定する。<p>
     *
     * @param infos ハンドラ情報のリスト
     */
    public void setHandlerInfos(List infos);
    
    /**
     * ハンドラ情報を追加する。<p>
     *
     * @param info ハンドラ情報
     */
    public void addHandlerInfo(HandlerInfo info);
    
    /**
     * ハンドラ情報のリストを取得する。<p>
     *
     * @return ハンドラ情報のリスト
     */
    public List getHandlerInfos();
    
    /**
     * ハンドラ情報をクリアする。<p>
     */
    public void clearHandlerInfos();
    
    /**
     * スタブに対して設定するプロパティを設定する。<p>
     *
     * @param name プロパティ名
     * @param value プロパティ値
     */
    public void setStubProperty(String name, Object value);
    
    /**
     * スタブに対して設定するプロパティを取得する。<p>
     *
     * @param name プロパティ名
     * @return プロパティ値
     */
    public Object getStubProperty(String name);
    
    /**
     * スタブに対して設定するプロパティのマップを取得する。<p>
     *
     * @return プロパティのマップ
     */
    public Map getStubPropertyMap();
}
