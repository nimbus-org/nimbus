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
package jp.ossc.nimbus.service.http;

import java.io.*;
import java.util.*;

/**
 * HTTPリクエスト。<p>
 *
 * @author M.Takata
 */
public interface HttpRequest{
    
    /**
     * リクエストを一意に識別する論理アクション名を取得する。<p>
     *
     * @return アクション名
     */
    public String getActionName();
    
    /**
     * リクエストするURLを取得する。<p>
     *
     * @return URL
     */
    public String getURL();
    
    /**
     * リクエストするURLを設定する。<p>
     *
     * @param url URL
     */
    public void setURL(String url);
    
    /**
     * リクエストするHTTPのバージョンを取得する。<p>
     *
     * @return HTTPのバージョン
     */
    public String getHttpVersion();
    
    /**
     * リクエストするHTTPのバージョンを設定する。<p>
     *
     * @param version HTTPのバージョン
     */
    public void setHttpVersion(String version);
    
    /**
     * リクエストするHTTPヘッダ名の集合を取得する。<p>
     *
     * @return HTTPヘッダ名の集合
     */
    public Set getHeaderNameSet();
    
    /**
     * 指定された名前のHTTPヘッダを取得する。<p>
     * 同一ヘッダ名で複数の値がある場合は、最初の値。<br>
     *
     * @return HTTPヘッダ
     */
    public String getHeader(String name);
    
    /**
     * 指定された名前のHTTPヘッダを取得する。<p>
     *
     * @return HTTPヘッダ配列
     */
    public String[] getHeaders(String name);
    
    /**
     * HTTPヘッダを設定する。<p>
     *
     * @param name HTTPヘッダ名
     * @param value HTTPヘッダ
     */
    public void setHeader(String name, String value);
    
    /**
     * HTTPヘッダを設定する。<p>
     *
     * @param name HTTPヘッダ名
     * @param value HTTPヘッダ配列
     */
    public void setHeaders(String name, String[] value);
    
    /**
     * HTTPヘッダを追加する。<p>
     *
     * @param name HTTPヘッダ名
     * @param value HTTPヘッダ
     */
    public void addHeader(String name, String value);
    
    /**
     * リクエストのコンテントタイプを取得する。<p>
     *
     * @return コンテントタイプ
     */
    public String getContentType();
    
    /**
     * リクエストのコンテントタイプを設定する。<p>
     *
     * @param type コンテントタイプ
     */
    public void setContentType(String type);
    
    /**
     * リクエストの文字エンコーディングを取得する。<p>
     *
     * @return 文字エンコーディング
     */
    public String getCharacterEncoding();
    
    /**
     * リクエストの文字エンコーディングを設定する。<p>
     *
     * @param encoding 文字エンコーディング
     */
    public void setCharacterEncoding(String encoding);
    
    /**
     * リクエストのクエリを取得する。<p>
     *
     * @return クエリ文字列
     */
    public String getQueryString();
    
    /**
     * リクエストのクエリを設定する。<p>
     *
     * @param query クエリ文字列
     */
    public void setQueryString(String query);
    
    /**
     * リクエストパラメータ名の集合を取得する。<p>
     *
     * @return リクエストパラメータ名の集合
     */
    public Set getParameterNameSet();
    
    /**
     * 指定された名前のリクエストパラメータを取得する。<p>
     * 同一リクエストパラメータ名で複数の値がある場合は、最初の値。<br>
     *
     * @return リクエストパラメータ
     */
    public String getParameter(String name);
    
    /**
     * 指定された名前のリクエストパラメータを取得する。<p>
     *
     * @return リクエストパラメータ
     */
    public String[] getParameters(String name);
    
    /**
     * リクエストパラメータを設定する。<p>
     *
     * @param name リクエストパラメータ名
     * @param value リクエストパラメータ
     */
    public void setParameter(String name, String value);
    
    /**
     * リクエストパラメータを設定する。<p>
     *
     * @param name リクエストパラメータ名
     * @param value リクエストパラメータ
     */
    public void setParameters(String name, String[] value);
    
    /**
     * リクエストパラメータを設定する。<p>
     *
     * @param name リクエストパラメータ名
     * @param file 送信ファイル
     */
    public void setFileParameter(String name, File file) throws java.io.FileNotFoundException;
    
    /**
     * リクエストパラメータを設定する。<p>
     *
     * @param name リクエストパラメータ名
     * @param file 送信ファイル
     * @param fileName 送信ファイル名
     * @param contentType コンテントタイプ
     */
    public void setFileParameter(String name, File file, String fileName, String contentType) throws java.io.FileNotFoundException;
    
    /**
     * リクエストストリームに書き込むための入力ストリームを設定する。<p>
     *
     * @param is 入力ストリーム
     */
    public void setInputStream(InputStream is);
    
    /**
     * リクエストストリームを取得する。<p>
     *
     * @return リクエストストリーム
     */
    public OutputStream getOutputStream();
    
    /**
     * リクエストストリームに書き込むための入力オブジェクトを設定する。<p>
     *
     * @param input 入力オブジェクト
     */
    public void setObject(Object input);
    
    /**
     * リクエストストリームに書き込むための入力オブジェクトを取得する。<p>
     *
     * @return 入力オブジェクト
     */
    public Object getObject();
}