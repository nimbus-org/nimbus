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
package jp.ossc.nimbus.service.connection;

import java.util.Map;

import jp.ossc.nimbus.core.*;

/**
 * {@link WrappedConnectionFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see WrappedConnectionFactoryService
 */
public interface WrappedConnectionFactoryServiceMBean
 extends ServiceBaseMBean{
    
    /**
     * ラップするConnectionFactoryサービスのサービス名を設定する。<p>
     *
     * @param name ラップするConnectionFactoryサービスのサービス名
     */
    public void setConnectionFactoryServiceName(ServiceName name);
    
    /**
     * ラップするConnectionFactoryサービスのサービス名を取得する。<p>
     *
     * @return ラップするConnectionFactoryサービスのサービス名
     */
    public ServiceName getConnectionFactoryServiceName();
    
    /**
     * {@link java.sql.Connection}をラップするクラスのクラス名を設定する。<p>
     * ここで指定できるクラスは、java.sql.Connectionインタフェースを実装しており、引数にjava.sql.Connectionを持つコンストラクタを実装しているクラスである。<br>
     *
     * @param className クラス名
     */
    public void setConnectionWrapperClassName(String className);
    
    /**
     * {@link java.sql.Connection}をラップするクラスのクラス名を取得する。<p>
     *
     * @return クラス名
     */
    public String getConnectionWrapperClassName();
    
    /**
     * {@link java.sql.Connection}をラップするクラスのインスタンスに設定するプロパティを設定する。<p>
     * Connectionをラップするクラスのインスタンスの、指定されたマップのキー名に該当するsetterを使って、キー名に該当する値を設定する。<br>
     *
     * @param prop Connectionをラップするクラスのインスタンスに設定するプロパティマップ
     */
    public void setConnectionWrapperProperties(Map prop);
    
    /**
     * {@link java.sql.Connection}をラップするクラスのインスタンスに設定するプロパティを取得する。<p>
     *
     * @return Connectionをラップするクラスのインスタンスに設定するプロパティマップ
     */
    public Map getConnectionWrapperProperties();
}