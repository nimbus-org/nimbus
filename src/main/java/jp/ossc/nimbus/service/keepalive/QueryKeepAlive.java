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
package jp.ossc.nimbus.service.keepalive;

import java.util.*;
//
/**
 * サーバ状態取得インターフェイス。<p>
 *
 * @author H.Nakano
 * @version  1.00 作成: 2003/10/08 - H.Nakano
 */
public interface QueryKeepAlive {
    
    /**
     * サーバ稼動状態が格納されたマップを取得する。<p>
     * サーバのキー、サーバの稼動状態（Boolean型）が格納されている。<br>
     *
     * @return サーバ稼動状態が格納されたマップ
     */
    public Map getKeepAliveMap();
    
    /**
     * サーバ稼動状態テーブルの状態を更新する。<p>
     *
     * @param msid サーバのキー
     * @param keepAlive 稼動状態（true:走行中、false:停止中）
     */
    public void updateTbl(Object msid, boolean keepAlive);
    
    /**
     * 優先順位方式で出力可能なサーバのリストを取得する。<p>
     * 
     * @return 優先順位方式で出力可能なサーバのリスト
     */
    public List getPriolityAry();
    
    /**
     * ラウンドロビン方式で出力可能なサーバのリストを取得する。<p>
     *
     * @return ラウンドロビン方式で出力可能なサーバのリスト
     */
    public List getRoundrobinAry();
    
    /**
     * 優先順位方式で出力可能なサーバのリストを取得する。<p>
     *
     * @param available 利用可能なサーバキーの集合
     * @return 優先順位方式で出力可能なサーバのリスト
     */
    public List getPriolityAry(Set available);
    
    /**
     * ラウンドロビン方式で出力可能なサーバのリストを取得する。<p>
     *
     * @param available 利用可能なサーバキーの集合
     * @return ラウンドロビン方式で出力可能なサーバのリスト
     */
    public List getRoundrobinAry(Set available);
}
