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

import java.util.Properties;

/**
 * {@link HttpServletRequestCheckInterceptorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see HttpServletRequestCheckInterceptorService
 */
public interface HttpServletRequestCheckInterceptorServiceMBean
 extends ServletFilterInterceptorServiceMBean{
    
    /**
     * リクエストヘッダのContent-Lengthの最大値を設定する。<p>
     * 設定しない場合は、Content-Lengthの最大値はチェックされない。<br>
     *
     * @param max Content-Lengthの最大値
     */
    public void setMaxContentLength(int max);
    
    /**
     * リクエストヘッダのContent-Lengthの最大値を取得する。<p>
     *
     * @return Content-Lengthの最大値
     */
    public int getMaxContentLength();
    
    /**
     * リクエストヘッダのContent-Lengthの最小値を設定する。<p>
     * 設定しない場合は、Content-Lengthの最小値はチェックされない。<br>
     *
     * @param min Content-Lengthの最小値
     */
    public void setMinContentLength(int min);
    
    /**
     * リクエストヘッダのContent-Lengthの最小値を取得する。<p>
     *
     * @return Content-Lengthの最小値
     */
    public int getMinContentLength();
    
    /**
     * リクエストヘッダのContent-Typeがnullである事を許容するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isAllow Content-Typeがnullである事を許容する場合、true
     */
    public void setAllowNullContentType(boolean isAllow);
    
    /**
     * リクエストヘッダのContent-Typeがnullである事を許容するかどうかを判定する。<p>
     *
     * @return trueの場合、Content-Typeがnullである事を許容する
     */
    public boolean isAllowNullContentType();
    
    /**
     * リクエストヘッダのContent-Typeの値として有効な値を設定する。<p>
     * 設定しない場合は、Content-Typeの値はチェックされない。<br>
     *
     * @param types Content-Typeの値として有効な値の文字列配列
     */
    public void setValidContentTypes(String[] types);
    
    /**
     * リクエストヘッダのContent-Typeの値として有効な値を取得する。<p>
     *
     * @return Content-Typeの値として有効な値の文字列配列
     */
    public String[] getValidContentTypes();
    
    /**
     * リクエストヘッダのContent-Typeの値として無効な値を設定する。<p>
     * 設定しない場合は、Content-Typeの値はチェックされない。<br>
     *
     * @param types Content-Typeの値として無効な値の文字列配列
     */
    public void setInvalidContentTypes(String[] types);
    
    /**
     * リクエストヘッダのContent-Typeの値として無効な値を取得する。<p>
     *
     * @return Content-Typeの値として無効な値の文字列配列
     */
    public String[] getInvalidContentTypes();
    
    /**
     * リクエストボディの文字エンコーディングが指定されていない事を許容するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isAllow リクエストボディの文字エンコーディングが指定されていない事を許容する場合、true
     */
    public void setAllowNullCharacterEncoding(boolean isAllow);
    
    /**
     * リクエストボディの文字エンコーディングが指定されていない事を許容するかどうかを判定する。<p>
     *
     * @return trueの場合、リクエストボディの文字エンコーディングが指定されていない事を許容する
     */
    public boolean isAllowNullCharacterEncoding();
    
    /**
     * リクエストボディの文字エンコーディングとして有効な値を設定する。<p>
     * 設定しない場合は、文字エンコーディングはチェックされない。<br>
     *
     * @param encodings 文字エンコーディングとして有効な値の文字列配列
     */
    public void setValidCharacterEncodings(String[] encodings);
    
    /**
     * リクエストボディの文字エンコーディングとして有効な値を取得する。<p>
     *
     * @return 文字エンコーディングとして有効な値の文字列配列
     */
    public String[] getValidCharacterEncodings();
    
    /**
     * リクエストボディの文字エンコーディングとして無効な値を設定する。<p>
     * 設定しない場合は、文字エンコーディングはチェックされない。<br>
     *
     * @param encodings 文字エンコーディングとして無効な値の文字列配列
     */
    public void setInvalidCharacterEncodings(String[] encodings);
    
    /**
     * リクエストボディの文字エンコーディングとして無効な値を取得する。<p>
     *
     * @return 文字エンコーディングとして無効な値の文字列配列
     */
    public String[] getInvalidCharacterEncodings();
    
    /**
     * リクエストヘッダのAccept-Languageがnullである事を許容するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isAllow Accept-Languageがnullである事を許容する場合、true
     */
    public void setAllowNullLocale(boolean isAllow);
    
    /**
     * リクエストヘッダのAccept-Languageがnullである事を許容するかどうかを判定する。<p>
     *
     * @return trueの場合、Accept-Languageがnullである事を許容する
     */
    public boolean isAllowNullLocale();
    
    /**
     * リクエストヘッダのAccept-Languageの値として有効な値を設定する。<p>
     * 設定しない場合は、Accept-Languageの値はチェックされない。<br>
     *
     * @param locales Accept-Languageの値として有効な値（正規表現）の文字列配列
     */
    public void setValidLocales(String[] locales);
    
    /**
     * リクエストヘッダのAccept-Languageの値として有効な値を取得する。<p>
     *
     * @return Accept-Languageの値として有効な値（正規表現）の文字列配列
     */
    public String[] getValidLocales();
    
    /**
     * リクエストのプロトコルの値として有効な値を設定する。<p>
     * 設定しない場合は、プロトコルの値はチェックされない。<br>
     *
     * @param protocols プロトコルの値として有効な値（正規表現）の文字列配列
     */
    public void setValidProtocols(String[] protocols);
    
    /**
     * リクエストのプロトコルの値として有効な値を取得する。<p>
     *
     * @return プロトコルの値として有効な値（正規表現）の文字列配列
     */
    public String[] getValidProtocols();
    
    /**
     * クライアントのIPアドレスの値として有効な値を設定する。<p>
     * 設定しない場合は、IPアドレスの値はチェックされない。<br>
     *
     * @param addrs IPアドレスの値として有効な値（正規表現）の文字列配列
     */
    public void setValidRemoteAddrs(String[] addrs);
    
    /**
     * クライアントのIPアドレスの値として有効な値を取得する。<p>
     *
     * @return IPアドレスの値として有効な値（正規表現）の文字列配列
     */
    public String[] getValidRemoteAddrs();
    
    /**
     * クライアントのホスト名として有効な値を設定する。<p>
     * 設定しない場合は、ホスト名はチェックされない。<br>
     *
     * @param hosts ホスト名として有効な値（正規表現）の文字列配列
     */
    public void setValidRemoteHosts(String[] hosts);
    
    /**
     * クライアントのホスト名として有効な値を取得する。<p>
     *
     * @return ホスト名として有効な値（正規表現）の文字列配列
     */
    public String[] getValidRemoteHosts();
    
    /**
     * クライアントのポート番号として有効な値を設定する。<p>
     * 設定しない場合は、ポート番号はチェックされない。<br>
     *
     * @param ports ポート番号として有効な値の配列
     */
    public void setValidRemotePorts(int[] ports);
    
    /**
     * クライアントのポート番号として有効な値を取得する。<p>
     *
     * @return ポート番号として有効な値の配列
     */
    public int[] getValidRemotePorts();
    
    /**
     * リクエストURLのスキーマの値として有効な値を設定する。<p>
     * 設定しない場合は、スキーマの値はチェックされない。<br>
     *
     * @param schemata スキーマの値として有効な値の文字列配列
     */
    public void setValidSchemata(String[] schemata);
    
    /**
     * リクエストURLのスキーマの値として有効な値を取得する。<p>
     *
     * @return スキーマの値として有効な値の文字列配列
     */
    public String[] getValidSchemata();
    
    /**
     * リクエストURLのホスト名の値として有効な値を設定する。<p>
     * 設定しない場合は、ホスト名の値はチェックされない。<br>
     *
     * @param names ホスト名の値として有効な値（正規表現）の文字列配列
     */
    public void setValidServerNames(String[] names);
    
    /**
     * リクエストURLのホスト名の値として有効な値を取得する。<p>
     *
     * @return ホスト名の値として有効な値（正規表現）の文字列配列
     */
    public String[] getValidServerNames();
    
    /**
     * HTTPリクエストのメソッド名の値として有効な値を設定する。<p>
     * 設定しない場合は、メソッド名の値はチェックされない。<br>
     *
     * @param methods メソッド名の値として有効な値の文字列配列
     */
    public void setValidMethods(String[] methods);
    
    /**
     * HTTPリクエストのメソッド名の値として有効な値を取得する。<p>
     *
     * @return メソッド名の値として有効な値の文字列配列
     */
    public String[] getValidMethods();
    
    /**
     * HTTPリクエストのメソッド名の値として無効な値を設定する。<p>
     * 設定しない場合は、メソッド名の値はチェックされない。<br>
     *
     * @param methods メソッド名の値として無効な値の文字列配列
     */
    public void setInvalidMethods(String[] methods);
    
    /**
     * HTTPリクエストのメソッド名の値として無効な値を取得する。<p>
     *
     * @return メソッド名の値として無効な値の文字列配列
     */
    public String[] getInvalidMethods();
    
    /**
     * 任意のリクエストヘッダの値として有効な値を設定する。<p>
     *
     * @param cond 任意のリクエストヘッダ名と有効な値（正規表現）のマッピング。リクエストヘッダ名=値（正規表現）
     */
    public void setHeaderEquals(Properties cond);
    
    /**
     * 任意のリクエストヘッダの値として有効な値を取得する。<p>
     *
     * @return 任意のリクエストヘッダ名と有効な値（正規表現）のマッピング
     */
    public Properties getHeaderEquals();
    
    /**
     * チェックエラーになった場合に返すHTTPレスポンスのステータスを設定する。<p>
     * デフォルトは、400。<br>
     * {@link #isThrowOnError()}がtrueの場合は、この設定は無効である。<br>
     *
     * @param status HTTPレスポンスのステータス
     */
    public void setErrorStatus(int status);
    
    /**
     * チェックエラーになった場合に返すHTTPレスポンスのステータスを取得する。<p>
     *
     * @return HTTPレスポンスのステータス
     */
    public int getErrorStatus();
    
    /**
     * チェックエラーになった場合に例外をthrowするかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isThrow チェックエラーになった場合に例外をthrowする場合、true
     */
    public void setThrowOnError(boolean isThrow);
    
    /**
     * チェックエラーになった場合に例外をthrowするかどうかを判定する。<p>
     *
     * @return trueの場合、チェックエラーになった場合に例外をthrowする
     */
    public boolean isThrowOnError();
}
