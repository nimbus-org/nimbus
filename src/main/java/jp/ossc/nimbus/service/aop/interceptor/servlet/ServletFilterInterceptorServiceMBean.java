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
package jp.ossc.nimbus.service.aop.interceptor.servlet;

import jp.ossc.nimbus.core.*;

/**
 * {@link ServletFilterInterceptorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see ServletFilterInterceptorService
 */
public interface ServletFilterInterceptorServiceMBean
 extends ServiceBaseMBean{
    
    /**
     * このインターセプタを有効にするURLを設定する。<p>
     * リクエストURLが指定されたURLに該当する場合だけ、インターセプタの処理が行われる。<br>
     * 設定しない場合は、全てのURLに対して有効になる。<br>
     *
     * @param urls このインターセプタを有効にするURL（正規表現）の配列
     */
    public void setEnabledURLs(String[] urls);
    
    /**
     * このインターセプタを有効にするURLを取得する。<p>
     *
     * @return このインターセプタを有効にするURL（正規表現）の配列
     */
    public String[] getEnabledURLs();
    
    /**
     * このインターセプタを無効にするURLを設定する。<p>
     * リクエストURLが指定されたURLに該当する場合だけ、インターセプタの処理が行われない。<br>
     * 設定しない場合は、全てのURLに対して有効になる。<br>
     *
     * @param urls このインターセプタを無効にするURL（正規表現）の配列
     */
    public void setDisabledURLs(String[] urls);
    
    /**
     * このインターセプタを無効にするURLを取得する。<p>
     *
     * @return このインターセプタを無効にするURL（正規表現）の配列
     */
    public String[] getDisabledURLs();
    
    /**
     * このインターセプタを有効にするURIを設定する。<p>
     * リクエストURIが指定されたURIに該当する場合だけ、インターセプタの処理が行われる。<br>
     * 設定しない場合は、全てのURIに対して有効になる。<br>
     *
     * @param uris このインターセプタを有効にするURI（正規表現）の配列
     */
    public void setEnabledURIs(String[] uris);
    
    /**
     * このインターセプタを有効にするURIを取得する。<p>
     *
     * @return このインターセプタを有効にするURI（正規表現）の配列
     */
    public String[] getEnabledURIs();
    
    /**
     * このインターセプタを無効にするURIを設定する。<p>
     * リクエストURIが指定されたURIに該当する場合だけ、インターセプタの処理が行われない。<br>
     * 設定しない場合は、全てのURIに対して有効になる。<br>
     *
     * @param uris このインターセプタを無効にするURI（正規表現）の配列
     */
    public void setDisabledURIs(String[] uris);
    
    /**
     * このインターセプタを無効にするURIを取得する。<p>
     *
     * @return このインターセプタを無効にするURI（正規表現）の配列
     */
    public String[] getDisabledURIs();
    
    /**
     * このインターセプタを有効にするサーブレットパスを設定する。<p>
     * リクエストサーブレットパスが指定されたサーブレットパスに該当する場合だけ、インターセプタの処理が行われる。<br>
     * 設定しない場合は、全てのサーブレットパスに対して有効になる。<br>
     *
     * @param paths このインターセプタを有効にするサーブレットパス（正規表現）の配列
     */
    public void setEnabledPaths(String[] paths);
    
    /**
     * このインターセプタを有効にするサーブレットパスを取得する。<p>
     *
     * @return このインターセプタを有効にするサーブレットパス（正規表現）の配列
     */
    public String[] getEnabledPaths();
    
    /**
     * このインターセプタを無効にするサーブレットパスを設定する。<p>
     * リクエストサーブレットパスが指定されたサーブレットパスに該当する場合だけ、インターセプタの処理が行われない。<br>
     * 設定しない場合は、全てのサーブレットパスに対して有効になる。<br>
     *
     * @param paths このインターセプタを無効にするサーブレットパス（正規表現）の配列
     */
    public void setDisabledPaths(String[] paths);
    
    /**
     * このインターセプタを無効にするサーブレットパスを取得する。<p>
     *
     * @return このインターセプタを無効にするサーブレットパス（正規表現）の配列
     */
    public String[] getDisabledPaths();
}
