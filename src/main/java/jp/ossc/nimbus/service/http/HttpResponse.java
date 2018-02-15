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
 * HTTPレスポンス。<p>
 *
 * @author M.Takata
 */
public interface HttpResponse{
    
    /**
     * レスポンスのHTTPヘッダ名の集合を取得する。<p>
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
     * レスポンスの文字エンコーディングを取得する。<p>
     *
     * @return 文字エンコーディング
     */
    public String getCharacterEncoding();
    
    /**
     * レスポンスのHTTPステータスを取得する。<p>
     *
     * @return HTTPステータス
     */
    public int getStatusCode();
    
    /**
     * レスポンスのHTTPステータスメッセージを取得する。<p>
     *
     * @return HTTPステータスメッセージ
     */
    public String getStatusMessage();
    
    /**
     * レスポンスストリームを取得する。<p>
     *
     * @return レスポンスストリーム
     */
    public InputStream getInputStream() throws IOException;
    
    /**
     * レスポンスストリームから読み込んだ応答オブジェクトを取得する。<p>
     *
     * @return 応答オブジェクト
     */
    public Object getObject();
    
    /**
     * レスポンスストリームから読み込んだ応答オブジェクトを取得する。<p>
     *
     * @param bind 応答オブジェクト
     * @return 応答オブジェクト
     */
    public Object getObject(Object bind);
    
    /**
     * 明示的に接続を切断する。<p>
     */
    public void close();
}