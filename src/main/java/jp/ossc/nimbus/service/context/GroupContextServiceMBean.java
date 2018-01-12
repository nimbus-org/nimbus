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
package jp.ossc.nimbus.service.context;

import jp.ossc.nimbus.core.*;

/**
 * {@link GroupContextService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see GroupContextService
 */
public interface GroupContextServiceMBean extends ServiceBaseMBean, Context {
    
    /**
     * グルーピングするコンテキストサービスのサービス名を設定する。<p>
     *
     * @param names グルーピングするコンテキストサービスのサービス名配列
     */
    public void setContextServiceNames(ServiceName[] names);
    
    /**
     * グルーピングするコンテキストサービスのサービス名を取得する。<p>
     *
     * @return グルーピングするコンテキストサービスのサービス名配列
     */
    public ServiceName[] getContextServiceNames();
    
    /**
     * 指定されたキーに関連付けられたコンテキスト情報を取得する。<p>
     *
     * @param key キー
     * @return キーに関連付けられたコンテキスト情報。該当するコンテキスト情報がない場合は、null
     */
    public Object get(String key);
    
    /**
     * 指定されたキーに関連付けられたコンテキスト情報を削除する。<p>
     *
     * @param key キー
     * @return 削除されたコンテキスト情報。削除するコンテキスト情報がない場合は、null
     */
    public Object remove(String key);
    
    /**
     * 「名前(keyのtoString()) : 値(valueのtoString()) 改行」という形式でリスト出力する。<p>
     *
     * @return リスト文字列
     */
    public String list();
}
