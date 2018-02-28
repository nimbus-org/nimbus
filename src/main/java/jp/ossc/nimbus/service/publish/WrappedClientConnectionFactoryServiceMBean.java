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

import java.util.*;

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link WrappedClientConnectionFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see WrappedClientConnectionFactoryService
 */
public interface WrappedClientConnectionFactoryServiceMBean extends ServiceBaseMBean{
    
    /**
     * {@link ClientConnectionFactory}サービスのサービス名を設定する。<p>
     *
     * @param name ClientConnectionFactoryサービスのサービス名
     */
    public void setClientConnectionFactoryServiceName(ServiceName name);
    
    /**
     * {@link ClientConnectionFactory}サービスのサービス名を取得する。<p>
     *
     * @return ClientConnectionFactoryサービスのサービス名
     */
    public ServiceName getClientConnectionFactoryServiceName();
    
    /**
     * {@link ClientConnection}インタフェースのラッパークラスを設定する。<p>
     * 引数に指定するクラスは、ClientConnectionを引数に持ったコンストラクタを持ち、ClientConnectionインタフェースを実装したクラスである必要がある。<br>
     *
     * @param clazz ラッパークラス
     */
    public void setClientConnectionWrapperClass(Class clazz);
    
    /**
     * {@link ClientConnection}インタフェースのラッパークラスを取得する。<p>
     *
     * @return ラッパークラス
     */
    public Class getClientConnectionWrapperClass();
    
    /**
     * {@link ClientConnection}をラップするクラスのインスタンスに設定するプロパティを設定する。<p>
     * ClientConnectionをラップするクラスのインスタンスの、指定されたマップのキー名に該当するsetterを使って、キー名に該当する値を設定する。<br>
     *
     * @param prop ClientConnectionをラップするクラスのインスタンスに設定するプロパティマップ
     */
    public void setWrapperProperties(Map prop);
    
    /**
     * {@link ClientConnection}をラップするクラスのインスタンスに設定するプロパティを取得する。<p>
     *
     * @return ClientConnectionをラップするクラスのインスタンスに設定するプロパティマップ
     */
    public Map getWrapperProperties();
}
