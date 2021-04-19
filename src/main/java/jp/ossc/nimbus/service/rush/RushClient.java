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
package jp.ossc.nimbus.service.rush;

/**
 * ラッシュクライアント。<p>
 *
 * @author M.Takata
 */
public interface RushClient{
    
    /**
     * クライアントIDを設定する。<p>
     *
     * @param id クライアントID
     */
    public void setId(int id);
    
    /**
     * クライアントIDを設定取得する。<p>
     *
     * @return クライアントID
     */
    public int getId();
    
    /**
     * 初期化処理する。<p>
     *
     * @exception Exception 初期化処理に失敗した場合
     */
    public void init() throws Exception;
    
    /**
     * 接続要求を処理する。<p>
     *
     * @param request リクエスト
     * @exception Exception 要求に失敗した場合
     */
    public void connect(Request request) throws Exception;
    
    /**
     * 要求を処理する。<p>
     *
     * @param roopCount 現在のループ番号
     * @param count 現在の要求番号
     * @param request リクエスト
     * @exception Exception 要求に失敗した場合
     */
    public void request(int roopCount, int count, Request request) throws Exception;
    
    /**
     * 切断要求を処理する。<p>
     *
     * @param request リクエスト
     * @exception Exception 要求に失敗した場合
     */
    public void close(Request request) throws Exception;
    
    /**
     * 終了処理する。<p>
     *
     * @exception Exception 終了処理に失敗した場合
     */
    public void close() throws Exception;
}