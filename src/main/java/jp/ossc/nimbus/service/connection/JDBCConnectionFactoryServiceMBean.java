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

import java.util.Properties;

import jp.ossc.nimbus.core.*;

/**
 * {@link JDBCConnectionFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see JDBCConnectionFactoryService
 */
public interface JDBCConnectionFactoryServiceMBean
 extends ServiceBaseMBean{
    
    /**
     * JDBCドライバのクラス名を設定する。<p>
     *
     * @param name JDBCドライバのクラス名
     */
    public void setDriverName(String name);
    
    /**
     * JDBCドライバのクラス名を取得する。<p>
     *
     * @return JDBCドライバのクラス名
     */
    public String getDriverName();
    
    /**
     * JDBC接続URLを設定する。<p>
     *
     * @param url JDBC接続URL
     */
    public void setConnectionURL(String url);
    
    /**
     * JDBC接続URLを取得する。<p>
     *
     * @return JDBC接続URL
     */
    public String getConnectionURL();
    
    /**
     * JDBC接続ユーザ名を設定する。<p>
     *
     * @param name JDBC接続ユーザ名
     */
    public void setUserName(String name);
    
    /**
     * JDBC接続ユーザ名を取得する。<p>
     *
     * @return JDBC接続ユーザ名
     */
    public String getUserName();
    
    /**
     * JDBC接続パスワードを設定する。<p>
     *
     * @param password JDBC接続パスワード
     */
    public void setPassword(String password);
    
    /**
     * JDBC接続パスワードを取得する。<p>
     *
     * @return JDBC接続パスワード
     */
    public String getPassword();
    
    /**
     * JDBC接続プロパティを設定する。<p>
     * {@link #setUserName(String)}、{@link #setPassword(String)}と併用する事はできない。<br>
     *
     * @param prop JDBC接続プロパティ
     */
    public void setConnectionProperties(Properties prop);
    
    /**
     * JDBC接続プロパティを取得する。<p>
     *
     * @return JDBC接続プロパティ
     */
    public Properties getConnectionProperties();
    
    /**
     * 自動コミットを設定する。<p>
     * デフォルトは、true。
     *
     * @param isAuto 自動コミットにする場合は、true
     */
    public void setAutoCommit(boolean isAuto);
    
    /**
     * 自動コミットか判定する。<p>
     *
     * @return 自動コミットの場合は、true
     */
    public boolean isAutoCommit();
}
