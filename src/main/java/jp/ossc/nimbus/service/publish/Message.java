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
package jp.ossc.nimbus.service.publish;

import java.util.Set;

/**
 * メッセージインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface Message{
    
    /**
     * サブジェクトを取得する。<br>
     *
     * @return サブジェクト
     */
    public String getSubject();
    
    /**
     * サブジェクト集合を取得する。<br>
     *
     * @return サブジェクト集合
     */
    public Set getSubjects();
    
    /**
     * サブジェクトを設定する。<br>
     *
     * @param sbj サブジェクト
     * @param key キー
     */
    public void setSubject(String sbj, String key);
    
    /**
     * キーを取得する。<br>
     *
     * @return キー
     */
    public String getKey();
    
    /**
     * キーを取得する。<br>
     *
     * @param sbj サブジェクト
     * @return キー
     */
    public String getKey(String sbj);
    
    /**
     * メッセージのデータを設定する。<br>
     *
     * @param obj メッセージのデータオブジェクト
     * @exception MessageException データオブジェクトの設定に失敗した場合
     */
    public void setObject(Object obj) throws MessageException;
    
    /**
     * メッセージのデータを取得する。<br>
     *
     * @return データオブジェクト
     * @exception MessageException データオブジェクトの取得に失敗した場合
     */
    public Object getObject() throws MessageException;
    
    /**
     * メッセージの直列化バイト配列を設定する。<br>
     *
     * @param bytes メッセージの直列化バイト配列
     */
    public void setSerializedBytes(byte[] bytes);
    
    /**
     * メッセージの直列化バイト配列を取得する。<br>
     *
     * @return メッセージの直列化バイト配列
     */
    public byte[] getSerializedBytes();
    
    /**
     * 送信時刻を取得する。<p>
     *
     * @return 送信時刻
     */
    public long getSendTime();
    
    /**
     * 受信時刻を取得する。<p>
     *
     * @return 受信時刻
     */
    public long getReceiveTime();
    
    /**
     * 送信先IDの集合を取得する。<br>
     *
     * @return 送信先IDの集合
     */
    public Set getDestinationIds();
    
    /**
     * 送信先IDの集合を設定する。<br>
     *
     * @param ids 送信先IDの集合
     */
    public void setDestinationIds(Set ids);
    
    /**
     * 送信先IDを追加する。<br>
     *
     * @param id 送信先ID
     */
    public void addDestinationId(Object id);
    
    /**
     * 送信先IDを削除する。<br>
     *
     * @param id 送信先ID
     */
    public void removeDestinationId(Object id);
    
    /**
     * 送信先IDをクリアする。<br>
     */
    public void clearDestinationIds();
    
    /**
     * 指定したIDが送信先IDに含まれているか判定する。<br>
     *
     * @param id ID
     * @return 送信先IDに含まれる場合true
     */
    public boolean containsDestinationId(Object id);
    
    /**
     * このオブジェクトを再利用するように促す。<br>
     */
    public void recycle();
    
    /**
     * メッセージを複製する。<p>
     *
     * @return メッセージの複製
     */
    public Object clone();
}