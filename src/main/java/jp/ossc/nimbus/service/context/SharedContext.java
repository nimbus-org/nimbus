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

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Collection;

import jp.ossc.nimbus.beans.BeanTableIndexKeyFactory;
import jp.ossc.nimbus.service.interpreter.EvaluateException;

/**
 * 共有コンテキスト。<p>
 *
 * @author M.Takata
 */
public interface SharedContext extends Context{
    
    /**
     * 指定されたキーのロックを獲得する。<p>
     *
     * @param key キー
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public void lock(Object key) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 指定されたキーのロックを獲得する。<p>
     *
     * @param key キー
     * @param timeout タイムアウト[ms]
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public void lock(Object key, long timeout) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 指定されたキーのロックを獲得する。<p>
     *
     * @param key キー
     * @param ifAcquireable ロックを待機ぜずに獲得可能な場合のみ獲得する時は、true。待機してでも獲得する時は、false
     * @param ifExist 指定されたキーが存在する場合のみロックを獲得する場合、true。キーが存在しなくてもロックを取得する場合は、false
     * @param timeout タイムアウト[ms]
     * @return ifAcquireableがtrueで、ロックが獲得できなかった場合は、false
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public boolean lock(Object key, boolean ifAcquireable, boolean ifExist, long timeout) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 指定されたキーのロックを開放する。<p>
     *
     * @param key キー
     * @return ロック開放できた場合は、true
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public boolean unlock(Object key) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 指定されたキーのロックを開放する。<p>
     *
     * @param key キー
     * @param force 強制フラグ
     * @return ロック開放できた場合は、true
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public boolean unlock(Object key, boolean force) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 指定されたキーのロックを開放する。<p>
     *
     * @param key キー
     * @param force 強制フラグ
     * @param timeout タイムアウト[ms]
     * @return ロック開放できた場合は、true
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public boolean unlock(Object key, boolean force, long timeout) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 指定されたキー集合のロックを獲得する。<p>
     *
     * @param keys キー集合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public void locks(Set keys) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 指定されたキー集合のロックを獲得する。<p>
     *
     * @param keys キー集合
     * @param timeout タイムアウト[ms]
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public void locks(Set keys, long timeout) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 指定されたキー集合のロックを獲得する。<p>
     *
     * @param keys キー集合
     * @param ifAcquireable ロックを待機ぜずに獲得可能な場合のみ獲得する時は、true。待機してでも獲得する時は、false
     * @param ifExist 指定されたキーが存在する場合のみロックを獲得する場合、true。キーが存在しなくてもロックを取得する場合は、false
     * @param timeout タイムアウト[ms]
     * @return ifAcquireableがtrueで、ロックが獲得できなかった場合は、false
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public boolean locks(Set keys, boolean ifAcquireable, boolean ifExist, long timeout) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 指定されたキー集合のロックを開放する。<p>
     *
     * @param keys キー集合
     * @return ロック開放できた場合は、null。解放できなかった場合は、解放できなかったキー集合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public Set unlocks(Set keys) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 指定されたキーのロックを開放する。<p>
     *
     * @param keys キー集合
     * @param force 強制フラグ
     * @return ロック開放できた場合は、null。解放できなかった場合は、解放できなかったキー集合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public Set unlocks(Set keys, boolean force) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 指定されたキーのロックを開放する。<p>
     *
     * @param keys キー集合
     * @param force 強制フラグ
     * @param timeout タイムアウト[ms]
     * @return ロック開放できた場合は、null。解放できなかった場合は、解放できなかったキー集合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public Set unlocks(Set keys, boolean force, long timeout) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 指定されたキーのロックを保有しているノードのIDを取得する。<p>
     *
     * @param key キー
     * @return ロックを保有しているノードのID
     */
    public Object getLockOwner(Object key);
    
    /**
     * 指定されたキーのロックを待ってる数を取得する。<p>
     *
     * @param key キー
     * @return ロックを待ってる数
     */
    public int getLockWaitCount(Object key);
    
    /**
     * 指定したキーで、指定した値を追加する。<p>
     *
     * @param key キー
     * @param value 値
     * @param timeout タイムアウト[ms]
     * @return 古い値
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public Object put(Object key, Object value, long timeout) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 指定したキーで、指定した値をローカルに追加する。<p>
     *
     * @param key キー
     * @param value 値
     * @return 古い値
     */
    public Object putLocal(Object key, Object value);
    
    /**
     * 指定したキーで、指定した値を非同期に追加する。<p>
     *
     * @param key キー
     * @param value 値
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     */
    public void putAsynch(Object key, Object value) throws SharedContextSendException;
    
    /**
     * 指定したキーで、指定した差分を更新する。<p>
     *
     * @param key キー
     * @param diff 差分
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     */
    public void update(Object key, SharedContextValueDifference diff) throws SharedContextSendException;
    
    /**
     * 指定したキーで、指定した差分を更新する。<p>
     *
     * @param key キー
     * @param diff 差分
     * @param timeout タイムアウト[ms]
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public void update(Object key, SharedContextValueDifference diff, long timeout) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 指定したキーで、指定した差分をローカルに更新する。<p>
     *
     * @param key キー
     * @param diff 差分
     * @exception SharedContextUpdateException 更新に失敗した場合
     */
    public void updateLocal(Object key, SharedContextValueDifference diff) throws SharedContextUpdateException;
    
    /**
     * 指定したキーで、指定した差分で非同期に更新する。<p>
     *
     * @param key キー
     * @param diff 差分
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     */
    public void updateAsynch(Object key, SharedContextValueDifference diff) throws SharedContextSendException;
    
    /**
     * 指定したキーが存在すれば、指定した差分を更新する。<p>
     *
     * @param key キー
     * @param diff 差分
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     */
    public void updateIfExists(Object key, SharedContextValueDifference diff) throws SharedContextSendException;
    
    /**
     * 指定したキーが存在すれば、指定した差分を更新する。<p>
     *
     * @param key キー
     * @param diff 差分
     * @param timeout タイムアウト[ms]
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public void updateIfExists(Object key, SharedContextValueDifference diff, long timeout) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 指定したキーが存在すれば、指定した差分をローカルに更新する。<p>
     *
     * @param key キー
     * @param diff 差分
     * @exception SharedContextUpdateException 更新に失敗した場合
     */
    public void updateLocalIfExists(Object key, SharedContextValueDifference diff) throws SharedContextUpdateException;
    
    /**
     * 指定したキーが存在すれば、指定した差分で非同期に更新する。<p>
     *
     * @param key キー
     * @param diff 差分
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     */
    public void updateAsynchIfExists(Object key, SharedContextValueDifference diff) throws SharedContextSendException;
    
    /**
     * 指定したマップを追加する。<p>
     *
     * @param t マップ
     * @param timeout タイムアウト[ms]
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public void putAll(Map t, long timeout) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 指定したマップをローカルに追加する。<p>
     *
     * @param t マップ
     */
    public void putAllLocal(Map t);
    
    /**
     * 指定したマップを非同期で追加する。<p>
     *
     * @param t マップ
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     */
    public void putAllAsynch(Map t) throws SharedContextSendException;
    
    /**
     * 指定したキーの値を取得する。<p>
     *
     * @param key キー
     * @param timeout タイムアウト[ms]
     * @return 値
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public Object get(Object key, long timeout) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 指定したキーの値を取得する。<p>
     *
     * @param key キー
     * @param timeout タイムアウト[ms]
     * @param withTransaction trueの場合、トランザクションを考慮する
     * @return 値
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public Object get(Object key, long timeout, boolean withTransaction) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 指定したキーのローカル値を取得する。<p>
     *
     * @param key キー
     * @return 値
     */
    public Object getLocal(Object key);
    
    /**
     * 指定したキーを削除する。<p>
     *
     * @param key キー
     * @param timeout タイムアウト[ms]
     * @return 削除した値
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public Object remove(Object key, long timeout) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 指定したキーをローカルから削除する。<p>
     *
     * @param key キー
     * @return 削除した値
     */
    public Object removeLocal(Object key);
    
    /**
     * 指定したキーを非同期で削除する。<p>
     *
     * @param key キー
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     */
    public void removeAsynch(Object key) throws SharedContextSendException;
    
    /**
     * 全て削除する。<p>
     *
     * @param timeout タイムアウト[ms]
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public void clear(long timeout) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * ローカルを全て削除する。<p>
     */
    public void clearLocal();
    
    /**
     * 全て非同期で削除する。<p>
     *
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     */
    public void clearAsynch() throws SharedContextSendException;
    
    /**
     * キーの集合を取得する。<p>
     *
     * @param timeout タイムアウト[ms]
     * @return キーの集合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public Set keySet(long timeout) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * ローカルのキーの集合を取得する。<p>
     *
     * @return キーの集合
     */
    public Set keySetLocal();
    
    /**
     * 登録されているキーの件数を取得する。<p>
     *
     * @param timeout タイムアウト[ms]
     * @return キーの件数
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public int size(long timeout) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * ローカルに登録されているキーの件数を取得する。<p>
     *
     * @return キーの件数
     */
    public int sizeLocal();
    
    /**
     * 空かどうか判定する。<p>
     *
     * @return trueの場合、空
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     */
    public boolean isEmpty() throws SharedContextSendException;
    
    /**
     * ローカルが空かどうか判定する。<p>
     *
     * @return trueの場合、空
     */
    public boolean isEmptyLocal();
    
    /**
     * 指定されたキーが登録されているかを判定する。<p>
     *
     * @param key キー
     * @param timeout タイムアウト[ms]
     * @return 登録されている場合true
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public boolean containsKey(Object key, long timeout) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 指定されたキーがローカルに登録されているかを判定する。<p>
     *
     * @param key キー
     * @return 登録されている場合true
     */
    public boolean containsKeyLocal(Object key);
    
    /**
     * 指定された値が登録されているかを判定する。<p>
     *
     * @param value 値
     * @param timeout タイムアウト[ms]
     * @return 登録されている場合true
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public boolean containsValue(Object value, long timeout) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 指定された値がローカルに登録されているかを判定する。<p>
     *
     * @param value 値
     * @return 登録されている場合true
     */
    public boolean containsValueLocal(Object value);
    
    /**
     * このコンテキストのローカルの内容を全て含むマップを取得する。<p>
     *
     * @return コンテキストのローカルの内容を全て含むマップ
     */
    public Map allLocal();
    
    /**
     * このコンテキストのローカルのエントリー集合を取得する。<p>
     *
     * @return コンテキストのローカルのエントリー集合
     */
    public Set entrySetLocal();
    
    /**
     * このコンテキストのローカルの値の集合を取得する。<p>
     *
     * @return コンテキストのローカルの値の集合
     */
    public Collection valuesLocal();
    
    /**
     * コンテキスト同期を行う。<p>
     * クライアントモードの場合は、ローカルのコンテキストをクリアする。また、サーバモードで主ノードの場合は、全てのノードに同期命令を出す。<br>
     *
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public void synchronize() throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * コンテキスト同期を行う。<p>
     * クライアントモードの場合は、ローカルのコンテキストをクリアする。また、サーバモードで主ノードの場合は、全てのノードに同期命令を出す。<br>
     *
     * @param timeout タイムアウト
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public void synchronize(long timeout) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * コンテキスト読み込みを行う。<p>
     * 主ノードでない場合、主ノードに読み込みを依頼する。<br>
     *
     * @param timeout 分散サーバへの通信タイムアウト[ms]
     * @exception Exception コンテキスト読み込みに失敗した場合
     */
    public void load(long timeout) throws Exception;
    
    /**
     * コンテキストのキーの読み込みを行う。<p>
     * 主ノードでない場合、主ノードに読み込みを依頼する。<br>
     *
     * @param timeout 分散サーバへの通信タイムアウト[ms]
     * @exception Exception コンテキスト読み込みに失敗した場合
     */
    public void loadKey(long timeout) throws Exception;
    
    /**
     * 指定したキーに該当する値のコンテキスト読み込みを行う。<p>
     * 主ノードでない場合、主ノードに読み込みを依頼する。<br>
     *
     * @param key キー
     * @param timeout 分散サーバへの通信タイムアウト[ms]
     * @exception Exception コンテキスト読み込みに失敗した場合
     */
    public void load(Object key, long timeout) throws Exception;
    
    /**
     * コンテキスト保存を行う。<p>
     * 主ノードでない場合、主ノードに保存を依頼する。<br>
     *
     * @param timeout 分散サーバへの通信タイムアウト[ms]
     * @exception Exception コンテキスト保存に失敗した場合
     */
    public void save(long timeout) throws Exception;
    
    /**
     * 指定したキーに該当する値のコンテキスト保存を行う。<p>
     * 主ノードでない場合、主ノードに保存を依頼する。<br>
     *
     * @param key キー
     * @param timeout 分散サーバへの通信タイムアウト[ms]
     * @exception Exception コンテキスト保存に失敗した場合
     */
    public void save(Object key, long timeout) throws Exception;
    
    /**
     * クライアント/サーバモードを判定する。<p>
     *
     * @return trueの場合、クライアントモード
     */
    public boolean isClient();
    
    /**
     * 主ノードかどうかを判定する。<p>
     *
     * @return 主ノードの場合true
     */
    public boolean isMain();
    
    /**
     * ノードIDを取得する。<p>
     *
     * @return ノードID
     */
    public Object getId();
    
    /**
     * 主ノードのノードIDを取得する。<p>
     *
     * @return ノードID
     */
    public Object getMainId();
    
    /**
     * 全ノードのノードIDのリストを取得する。<p>
     *
     * @return ノードIDのリスト
     */
    public List getMemberIdList();
    
    /**
     * クライアントノードのノードIDの集合を取得する。<p>
     *
     * @return ノードIDの集合
     */
    public Set getClientMemberIdSet();
    
    /**
     * サーバノードのノードIDの集合を取得する。<p>
     *
     * @return ノードIDの集合
     */
    public Set getServerMemberIdSet();
    
    /**
     * 共有コンテキスト更新リスナーを登録する。<p>
     *
     * @param listener 共有コンテキスト更新リスナー
     */
    public void addSharedContextUpdateListener(SharedContextUpdateListener listener);
    
    /**
     * 共有コンテキスト更新リスナーを削除する。<p>
     *
     * @param listener 共有コンテキスト更新リスナー
     */
    public void removeSharedContextUpdateListener(SharedContextUpdateListener listener);
    
    /**
     * インデックスを追加する。<p>
     * インデックには、単一のプロパティで構成される単純インデックスと、複数のプロパティで構成される複合インデックスが存在する。<br>
     * 複合インデックスを追加した場合は、自動的にその要素となる単一プロパティの単純インデックスも内部的に生成される。<p>
     * 但し、自動生成された単一インデックスは、インデックス名を持たないため、インデックス名では指定できず、プロパティ名で指定して使用する。<br>
     * インデックスの種類によって、使用できる検索機能が異なる。単純インデックスは、一致検索と範囲検索の両方が可能だが、複合インデックスは、一致検索のみ可能である。<br>
     *
     * @param name インデックス名
     * @param props インデックスを張るBeanのプロパティ名配列
     */
    public void setIndex(String name, String[] props);
    
    /**
     * カスタマイズしたインデックスを追加する。<p>
     *
     * @param name インデックス名
     * @param keyFactory インデックスのキーを生成するファクトリ
     * @see #setIndex(String, String[])
     */
    public void setIndex(String name, BeanTableIndexKeyFactory keyFactory);
    
    /**
     * インデックスを削除する。<p>
     *
     * @param name インデックス名
     */
    public void removeIndex(String name);
    
    /**
     * インデックスを再解析する。<p>
     *
     * @param name インデックス名
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public void analyzeIndex(String name) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * インデックスを再解析する。<p>
     *
     * @param name インデックス名
     * @param timeout タイムアウト
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public void analyzeIndex(String name, long timeout) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 検索を行うビューを作成する。<p>
     * 
     * @return 検索ビュー
     */
    public SharedContextView createView();
    
    /**
     * クエリをデータノードでインタープリタ実行する。<p>
     * クエリの文法は、{@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}の実装に依存する。<br>
     * クエリ中では、コンテキストを変数名"context"で参照できる。<br>
     *
     * @param query クエリ
     * @param variables クエリ中で使用する変数マップ
     * @return 実行結果
     * @exception EvaluateException クエリの実行で例外が発生した場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public Object executeInterpretQuery(String query, Map variables) throws EvaluateException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * クエリをデータノードでインタープリタ実行する。<p>
     *
     * @param query クエリ
     * @param variables クエリ中で使用する変数マップ
     * @param timeout タイムアウト
     * @return 実行結果
     * @exception EvaluateException クエリの実行で例外が発生した場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public Object executeInterpretQuery(String query, Map variables, long timeout) throws EvaluateException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 分散サーバとの通信の健全性をチェックする。<p>
     * 
     * @param isContainsClient 健全性をチェックする対象として、クライアントモードも含める場合は、true
     * @param timeout タイムアウト
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public void healthCheck(boolean isContainsClient, long timeout) throws SharedContextSendException, SharedContextTimeoutException;
}