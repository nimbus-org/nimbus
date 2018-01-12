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
package jp.ossc.nimbus.service.message;

import java.util.*;

/**
 * メッセージレコードの管理用インタフェース。<p>
 * メッセージレコード毎の使用回数の取得、リセットなどを行うインターフェイスを定義する。
 *
 * @author H.Nakano
 */
public interface MessageRecordOperator{
    
    /**
     * メッセージ定義ファイルの1行を読み込む。<p>
     *
     * @param defString メッセージ定義ファイルの1行の文字列
     */
    public void rec2Obj(String defString) throws MessageRecordParseException;
    
    /**
     * ロケール毎のメッセージを追加する。<p>
     * 
     * @param message メッセージ文字列
     * @param locale ロケール指定文字列
     */
    public void addMessage(String message, String locale);
    
    /**
     * 使用回数を取得する。<p>
     * 
     * @return 使用回数
     */
    public long getUsedCount();
    
    /**
     * 使用回数をクリアする。<p>
     */
    public void clearUsedCount();
    
    /**
     * 使用最終日時を取得する。<p>
     * 
     * @return 最終使用日時
     */
    public Date getLastUsedDate();
    
    /**
     * 秘密埋め込みメッセージを秘密文字でマスクするかどうかを設定する。<p>
     * 
     * @param flg 秘密文字でマスクする場合true
     */
    public void setSecret(boolean flg);
    
    /**
     * 秘密文字を設定する。<p>
     * 設定しない場合は、メッセージ定義のままで出力される。<br>
     * 
     * @param secret 秘密文字
     */
    public void setSecretString(String secret);
    
    /**
     * メッセージレコードファクトリを設定する。<p>
     * 
     * @param fac メッセージレコードファクトリ
     */
    public void setFactory(MessageRecordFactory fac);
}
