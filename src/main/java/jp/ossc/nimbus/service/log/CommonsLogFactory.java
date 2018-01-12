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
package jp.ossc.nimbus.service.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;

/**
 * Jakarta Commons Logging用のログファクトリインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface CommonsLogFactory{
    
    /**
     * 引数で指定したクラスオブジェクトに関連付いた{@link Log}インスタンスを取得する。<p>
     *
     * @param clazz 取得するLogインスタンスを識別するキーとなるクラスオブジェクト
     * @return 引数で指定したクラスオブジェクトに関連付いた{@link Log}インスタンス
     * @exception LogConfigurationException Logインスタンスの作成に失敗した場合
     */
    public Log getInstance(Class clazz) throws LogConfigurationException;
    
    /**
     * 引数で指定した名前に関連付いた{@link Log}インスタンスを取得する。<p>
     *
     * @param name 取得するLogインスタンスを識別する名前
     * @return 引数で指定した名前に関連付いた{@link Log}インスタンス
     * @exception LogConfigurationException Logインスタンスの作成に失敗した場合
     */
    public Log getInstance(String name) throws LogConfigurationException;
    
    /**
     * 作成した{@link Log}インスタンスを開放する。<p>
     */
    public void release();
    
    /**
     * 属性値を取得する。<p>
     * "commons-logging.properties"で設定したプロパティが属性として格納される。<p>
     *
     * @param name 属性名
     * @return 属性値
     * @see #getAttributeNames()
     * @see #removeAttribute(String)
     * @see #setAttribute(String, Object)
     */
    public Object getAttribute(String name);
    
    /**
     * 属性名の配列を取得する。<p>
     * "commons-logging.properties"で設定したプロパティが属性として格納される。<p>
     *
     * @return 属性名の配列
     * @see #getAttribute(String)
     * @see #removeAttribute(String)
     * @see #setAttribute(String, Object)
     */
    public String[] getAttributeNames();
    
    /**
     * 属性を削除する。<p>
     * "commons-logging.properties"で設定したプロパティが属性として格納される。<p>
     *
     * @param name 属性名
     * @see #getAttribute(String)
     * @see #getAttributeNames()
     * @see #setAttribute(String, Object)
     */
    public void removeAttribute(String name);
    
    /**
     * 属性を設定する。<p>
     * "commons-logging.properties"で設定したプロパティが属性として格納される。<p>
     *
     * @param name 属性名
     * @param value 属性値
     * @see #getAttribute(String)
     * @see #getAttributeNames()
     * @see #removeAttribute(String)
     */
    public void setAttribute(String name, Object value);
}
