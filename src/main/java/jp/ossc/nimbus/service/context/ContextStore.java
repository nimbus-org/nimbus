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
 * コンテキストストア。<p>
 *
 * @author M.Takata
 */
public interface ContextStore{
    
    /**
     * ストア先を全て削除する。<p>
     *
     * @exception Exception 失敗した場合
     */
    public void clear() throws Exception;
    
    /**
     * コンテキストを保存する。<p>
     *
     * @param context コンテキスト
     * @exception Exception 失敗した場合
     */
    public void save(Context context) throws Exception;
    
    /**
     * コンテキスト上の指定されたキーに該当する値を保存する。<p>
     *
     * @param context コンテキスト
     * @param key キー
     * @exception Exception 失敗した場合
     */
    public void save(Context context, Object key) throws Exception;
    
    /**
     * コンテキストに読み込む。<p>
     *
     * @param context コンテキスト
     * @exception Exception 失敗した場合
     */
    public void load(Context context) throws Exception;
    
    /**
     * コンテキストにキーを読み込む。<p>
     *
     * @param context コンテキスト
     * @exception Exception 失敗した場合
     */
    public void loadKey(Context context) throws Exception;
    
    /**
     * 指定されたキーに該当する値をコンテキストに読み込む。<p>
     *
     * @param context コンテキスト
     * @param key キー
     * @exception Exception 失敗した場合
     */
    public void load(Context context, Object key) throws Exception;
    
    /**
     * キー指定での保存をサポートするかどうかを判定する。<p>
     *
     * @return trueの場合、キー指定での保存をサポートする
     */
    public boolean isSupportSaveByKey();
    
    /**
     * キー指定での読み込みをサポートするかどうかを判定する。<p>
     *
     * @return trueの場合、キー指定での読み込みをサポートする
     */
    public boolean isSupportLoadByKey();
}
