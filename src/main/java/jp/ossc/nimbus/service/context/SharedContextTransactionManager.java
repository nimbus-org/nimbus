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

/**
 * 共有コンテキスト用のトランザクション管理インタフェース。<p>
 *
 * @author M.Takata
 */
public interface SharedContextTransactionManager{
    
    /**
     * トランザクションのロックモード：悲観ロック。<p>
     */
    public static final int LOCK_MODE_PESSIMISTIC = 1;
    
    /**
     * トランザクションのロックモード：楽観ロック。<p>
     */
    public static final int LOCK_MODE_OPTIMISTIC  = 2;
    
    /**
     * トランザクションを開始する。<p>
     * ロックモードは、設定に依存する。<br>
     */
    public void begin();
    
    /**
     * トランザクションを開始する。<p>
     *
     * @param lockMode ロックモード
     * @see #LOCK_MODE_PESSIMISTIC
     * @see #LOCK_MODE_OPTIMISTIC
     */
    public void begin(int lockMode);
    
    /**
     * トランザクションをコミットする。<p>
     * 
     * @exception SharedContextTransactionException トランザクションの処理に失敗した場合
     */
    public void commit() throws SharedContextTransactionException;
    
    /**
     * トランザクションをロールバックする。<p>
     * 
     * @exception SharedContextTransactionException トランザクションの処理に失敗した場合
     */
    public void rollback() throws SharedContextTransactionException;
    
    /**
     * トランザクションのタイムアウト[ms]を設定する。<p>
     * 指定しない場合は、タイムアウトしない。<br>
     *
     * @param timeout タイムアウト[ms]
     */
    public void setTransactionTimeout(long timeout);
    
    /**
     * トランザクションのタイムアウト[ms]を取得する。<p>
     *
     * @return タイムアウト[ms]
     */
    public long getTransactionTimeout();
    
    /**
     * 現在のスレッドが開始しているトランザクションを取得する。<p>
     *
     * @return トランザクション。トランザクションが開始されていない場合は、null。
     */
    public SharedContextTransaction getTransaction();
    
    /**
     * 共有コンテキストのトランザクション。<p>
     * 
     * @author M.Takata
     */
    public interface SharedContextTransaction{
        
        /**
         * トランザクション状態：開始前。<p>
         */
        public static final int STATE_BEFORE_BEGIN = 0;
        
        /**
         * トランザクション状態：開始。<p>
         */
        public static final int STATE_BEGIN      = 1;
        
        /**
         * トランザクション状態：コミット。<p>
         */
        public static final int STATE_COMMIT     = 2;
        
        /**
         * トランザクション状態：ロールバック。<p>
         */
        public static final int STATE_ROLLBACK   = 3;
        
        /**
         * トランザクション状態：コミット完了。<p>
         */
        public static final int STATE_COMMITTED  = 5;
        
        /**
         * トランザクション状態：ロールバック完了。<p>
         */
        public static final int STATE_ROLLBACKED = 6;
        
        /**
         * トランザクション状態：ロールバック失敗。<p>
         */
        public static final int STATE_ROLLBACK_FAILED = 7;
        
        /**
         * トランザクションの状態を取得する。<p>
         *
         * @return トランザクションの状態
         */
        public int getState();
        
        /**
         * トランザクションをコミットする。<p>
         * 
         * @exception SharedContextTransactionException トランザクションの処理に失敗した場合
         */
        public void commit() throws SharedContextTransactionException;
        
        /**
         * トランザクションをロールバックする。<p>
         * 
         * @exception SharedContextTransactionException トランザクションの処理に失敗した場合
         */
        public void rollback() throws SharedContextTransactionException;
        
        /**
         * トランザクションに指定されたキーが登録されているかを判定する。<p>
         *
         * @param context 共有コンテキスト
         * @param key キー
         * @return 登録されている場合true
         */
        public boolean containsKey(SharedContext context, Object key);
        
        /**
         * トランザクションに登録されたキーの値を取得する。<p>
         *
         * @param context 共有コンテキスト
         * @param key キー
         * @param timeout タイムアウト[ms]
         * @return 値
         * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
         * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
         */
        public Object get(SharedContext context, Object key, long timeout) throws SharedContextSendException, SharedContextTimeoutException;
        
        /**
         * 指定したキーで、指定した値を追加するようにトランザクションを更新する。<p>
         *
         * @param context 共有コンテキスト
         * @param key キー
         * @param value 値
         * @param timeout タイムアウト[ms]
         * @return 古い値
         * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
         * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
         */
        public Object put(SharedContext context, Object key, Object value, long timeout) throws SharedContextSendException, SharedContextTimeoutException;
        
        /**
         * 指定したキーで、指定した差分を更新するようにトランザクションを更新する。<p>
         *
         * @param context 共有コンテキスト
         * @param key キー
         * @param diff 差分
         * @param timeout タイムアウト[ms]
         * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
         * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
         */
        public void update(SharedContext context, Object key, SharedContextValueDifference diff, long timeout) throws SharedContextSendException, SharedContextTimeoutException;
        
        /**
         * 指定したキーが存在すれば、指定した差分を更新するようにトランザクションを更新する。<p>
         *
         * @param context 共有コンテキスト
         * @param key キー
         * @param diff 差分
         * @param timeout タイムアウト[ms]
         * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
         * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
         */
        public void updateIfExists(SharedContext context, Object key, SharedContextValueDifference diff, long timeout) throws SharedContextSendException, SharedContextTimeoutException;
        
        /**
         * 指定したキーを削除するようにトランザクションを更新する。<p>
         *
         * @param context 共有コンテキスト
         * @param key キー
         * @param timeout タイムアウト[ms]
         * @return 削除した値
         * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
         * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
         */
        public Object remove(SharedContext context, Object key, long timeout) throws SharedContextSendException, SharedContextTimeoutException;
    }
}