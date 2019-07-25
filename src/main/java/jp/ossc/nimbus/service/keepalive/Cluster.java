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

import java.io.Serializable;
import java.util.Set;
import java.util.List;

/**
 * クラスタ。<p>
 * 分散環境下でクラスタを構成し、クラスタの構成メンバーの状態を{@link ClusterListener}に通知する。<br>
 *
 * @author M.Takata
 */
public interface Cluster{
    
    /**
     * クラスタリスナを登録する。<p>
     *
     * @param listener クラスタリスナ
     */
    public void addClusterListener(ClusterListener listener);
    
    /**
     * クラスタリスナを削除する。<p>
     *
     * @param listener クラスタリスナ
     */
    public void removeClusterListener(ClusterListener listener);
    
    /**
     * このクラスタのUIDを取得する。<p>
     *
     * @return このクラスタのUID
     */
    public ClusterUID getUID();
    
    /**
     * このクラスタに参加しているメンバーのUIDリストを取得する。<p>
     *
     * @return このクラスタに参加しているメンバーのUIDリスト
     */
    public List getMembers();
    
    /**
     * このクラスタのクライアントとなるメンバのUID集合を取得する。<p>
     *
     * @return このクラスタのクライアントとなるメンバのUID集合
     */
    public Set getClientMembers();
    
    /**
     * このクラスタが、クラスタメンバ全体の主ノードかどうかを判定する。<p>
     *
     * @return trueの場合、主ノード
     */
    public boolean isMain();
    
    /**
     * このクラスタが、クラスタに参加しているかどうかを判定する。<p>
     *
     * @return trueの場合、参加している
     */
    public boolean isJoin();
    
    /**
     * クラスタに参加する。<p>
     *
     * @exception Exception クラスタへの参加に失敗した場合
     */
    public void join() throws Exception;
    
    /**
     * クラスタから離脱する。<p>
     */
    public void leave();
    
    /**
     * クラスタのクライアントメンバとなるかどうかを設定する。<p>
     * 
     * @param isClient クライアントメンバとなる場合、true
     */
    public void setClient(boolean isClient);
    
    /**
     * クラスタのクライアントメンバとなるかどうかを判定する。<p>
     * 
     * @return trueの場合、クライアントメンバとなる
     */
    public boolean isClient();
    
    /**
     * クライアントメンバとなるクラスタを生成する。<p>
     *
     * @return クライアントメンバとなるクラスタ
     */
    public Cluster createClient();
    
    /**
     * 付属情報を設定する。<p>
     *
     * @param opt 付属情報
     */
    public void setOption(Serializable opt);
    
    /**
     * 付属情報を取得する。<p>
     *
     * @return 付属情報
     */
    public Serializable getOption();
    
    /**
     * 指定されたキーに関連する付属情報を設定する。<p>
     *
     * @param key キー
     * @param opt 付属情報
     */
    public void setOption(String key, Serializable opt);
    
    /**
     * 指定されたキーに関連する付属情報を取得する。<p>
     *
     * @param key キー
     * @return 付属情報
     */
    public Serializable getOption(String key);
}