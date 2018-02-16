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
 * 共有コンテキスト更新リスナー。<p>
 *
 * @author M.Takata
 */
public interface SharedContextUpdateListener{
    
    /**
     * 共有コンテキストに追加される前に呼び出される。<p>
     *
     * @param context 共有コンテキスト
     * @param isLocal ローカルからの追加の場合、true。リモートからの追加の場合、false。
     * @param key 追加されるキー
     * @param value 追加される値
     * @return 共有コンテキストに追加させない場合は、false
     */
    public boolean onPutBefore(SharedContext context, boolean isLocal, Object key, Object value);
    
    /**
     * 共有コンテキストに追加された後に呼び出される。<p>
     *
     * @param context 共有コンテキスト
     * @param isLocal ローカルからの追加の場合、true。リモートからの追加の場合、false。
     * @param key 追加されたキー
     * @param value 追加された値
     * @param old 以前の値。但し、クライアントモードなどで、そこに値が存在しない場合はnull
     */
    public void onPutAfter(SharedContext context, boolean isLocal, Object key, Object value, Object old);
    
    /**
     * 同期によって共有コンテキストに追加される前に呼び出される。<p>
     *
     * @param context 共有コンテキスト
     * @param key 追加されるキー
     * @param value 追加される値
     * @return 共有コンテキストに追加させない場合は、false
     */
    public boolean onPutSynchronize(SharedContext context, Object key, Object value);
    
    /**
     * 共有コンテキストに更新される前に呼び出される。<p>
     *
     * @param context 共有コンテキスト
     * @param isLocal ローカルからの更新の場合、true。リモートからの更新の場合、false。
     * @param key 更新されるキー
     * @param diff 更新される差分
     * @return 共有コンテキストに更新させない場合は、false
     */
    public boolean onUpdateBefore(SharedContext context, boolean isLocal, Object key, SharedContextValueDifference diff);
    
    /**
     * 共有コンテキストに更新された後に呼び出される。<p>
     *
     * @param context 共有コンテキスト
     * @param isLocal ローカルからの更新の場合、true。リモートからの更新の場合、false。
     * @param key 更新されたキー
     * @param diff 更新された差分
     */
    public void onUpdateAfter(SharedContext context, boolean isLocal, Object key, SharedContextValueDifference diff);
    
    /**
     * 共有コンテキストから削除される前に呼び出される。<p>
     *
     * @param context 共有コンテキスト
     * @param isLocal ローカルからの削除の場合、true。リモートからの削除の場合、false。
     * @param key 削除されるキー
     * @return 共有コンテキストから削除させない場合は、false
     */
    public boolean onRemoveBefore(SharedContext context, boolean isLocal, Object key);
    
    /**
     * 共有コンテキストから削除された後に呼び出される。<p>
     *
     * @param context 共有コンテキスト
     * @param isLocal ローカルからの削除の場合、true。リモートからの削除の場合、false。
     * @param key 削除されたキー
     * @param removed 削除された値。但し、クライアントモードなどで、そこに値が存在しない場合はnull
     */
    public void onRemoveAfter(SharedContext context, boolean isLocal, Object key, Object removed);
    
    /**
     * 同期によって共有コンテキストがクリアされる前に呼び出される。<p>
     *
     * @param context 共有コンテキスト
     */
    public void onClearSynchronize(SharedContext context);
    
    /**
     * メンバの変更によって主ノードとなる前に呼び出される。<p>
     *
     * @param context 共有コンテキスト
     */
    public void onChangeMain(SharedContext context);
    
    /**
     * メンバの変更によって従ノードとなる前に呼び出される。<p>
     *
     * @param context 共有コンテキスト
     */
    public void onChangeSub(SharedContext context);
}