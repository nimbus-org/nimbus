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

import java.io.*;

/**
 * 検索管理。<p>
 *
 * @author M.Takata
 */
public interface QuerySearchManager{
    
    /**
     * 指定された条件オブジェクトに合致するレコードを検索して返す。<p>
     *
     * @param key 条件オブジェクト
     * @return 条件オブジェクトに合致するレコード
     * @exception ConnectionFactoryException データベースへの接続が取得できない場合
     * @exception PersistentException データベースからの検索に失敗した場合、または検索結果の変換に失敗した場合
     */
    public Object search(Object key) throws ConnectionFactoryException, PersistentException;
    
    /**
     * 指定された条件オブジェクトに合致するレコードを検索して出力ストリームに書き出す。<p>
     *
     * @param key 条件オブジェクト
     * @return 条件オブジェクトに合致するレコード
     * @exception ConnectionFactoryException データベースへの接続が取得できない場合
     * @exception PersistentException データベースからの検索に失敗した場合、または検索結果の変換に失敗した場合
     */
    public InputStream searchAndRead(Object key) throws ConnectionFactoryException, PersistentException;
    
    /**
     * 指定された条件オブジェクトに合致するレコードを検索して出力ストリームに書き出す。<p>
     *
     * @param key 条件オブジェクト
     * @param os 条件オブジェクトに合致するレコードを書き込む出力ストリーム
     * @exception ConnectionFactoryException データベースへの接続が取得できない場合
     * @exception PersistentException データベースからの検索に失敗した場合、または検索結果の変換に失敗した場合
     * @exception IOException 書き込みに失敗した場合
     */
    public void searchAndWrite(Object key, OutputStream os) throws ConnectionFactoryException, PersistentException, IOException;
}