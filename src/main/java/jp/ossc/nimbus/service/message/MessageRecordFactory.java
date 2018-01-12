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
 * メッセージレコード生成インターフェイス。<p>
 *
 * @author H.Nakano
 */
public interface MessageRecordFactory {
    
    /**
     * 指定したロケールのメッセージレコードを読み込む。<p>
     * 
     * @param lo ロケール
     */
    public void findLocale(Locale lo);
    
    /**
     * メッセージIDの一覧を取得する。<p>
     *
     * @return メッセージIDの一覧
     */
    public String[] getMessageIds();
    
    /**
     * 指定したメッセージIDのメッセージレコードを取得する。<p>
     *
     * @param messageId メッセージID
     * @return メッセージレコード
     */
    public MessageRecord findMessageRecord(String messageId);
    
    /**
     * 指定したメッセージIDのテンプレートメッセージを取得する。<p>
     * テンプレートメッセージには、埋め込みメッセージのキーワードもそのまま含まれる。<br>
     *
     * @param messageId メッセージID
     * @return テンプレートメッセージ
     */
    public String findMessageTemplete(String messageId);
    
    /**
     * 指定したメッセージIDの指定ロケールテンプレートメッセージを取得する。<p>
     * テンプレートメッセージには、埋め込みメッセージのキーワードもそのまま含まれる。<br>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @return 指定ロケールテンプレートメッセージ
     */
    public String findMessageTemplete(Locale lo, String messageId);
    
    /**
     * 指定したメッセージIDのメッセージを取得する。<p>
     *
     * @param messageId メッセージID
     * @return メッセージ
     */
    public String findMessage(String messageId);
    
    /**
     * 指定したメッセージIDの指定ロケールメッセージを取得する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @return 指定ロケールメッセージ
     */
    public String findMessage(Locale lo, String messageId);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージを取得する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @return 埋め込みメッセージ
     */
    public String findEmbedMessage(String messageId, Object[] embeds);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージを取得する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @return 埋め込みメッセージ
     */
    public String findEmbedMessage(String messageId, byte[] embeds);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージを取得する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @return 埋め込みメッセージ
     */
    public String findEmbedMessage(String messageId, short[] embeds);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージを取得する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @return 埋め込みメッセージ
     */
    public String findEmbedMessage(String messageId, char[] embeds);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージを取得する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @return 埋め込みメッセージ
     */
    public String findEmbedMessage(String messageId, int[] embeds);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージを取得する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @return 埋め込みメッセージ
     */
    public String findEmbedMessage(String messageId, long[] embeds);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージを取得する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @return 埋め込みメッセージ
     */
    public String findEmbedMessage(String messageId, float[] embeds);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージを取得する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @return 埋め込みメッセージ
     */
    public String findEmbedMessage(String messageId, double[] embeds);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージを取得する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @return 埋め込みメッセージ
     */
    public String findEmbedMessage(String messageId, boolean[] embeds);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージを取得する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @return 指定ロケール埋め込みメッセージ
     */
    public String findEmbedMessage(
        Locale lo,
        String messageId,
        Object[] embeds
    );
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージを取得する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @return 指定ロケール埋め込みメッセージ
     */
    public String findEmbedMessage(Locale lo, String messageId, byte[] embeds);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージを取得する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @return 指定ロケール埋め込みメッセージ
     */
    public String findEmbedMessage(Locale lo, String messageId, short[] embeds);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージを取得する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @return 指定ロケール埋め込みメッセージ
     */
    public String findEmbedMessage(Locale lo, String messageId, char[] embeds);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージを取得する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @return 指定ロケール埋め込みメッセージ
     */
    public String findEmbedMessage(Locale lo, String messageId, int[] embeds);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージを取得する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @return 指定ロケール埋め込みメッセージ
     */
    public String findEmbedMessage(Locale lo, String messageId, long[] embeds);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージを取得する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @return 指定ロケール埋め込みメッセージ
     */
    public String findEmbedMessage(Locale lo, String messageId, float[] embeds);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージを取得する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @return 指定ロケール埋め込みメッセージ
     */
    public String findEmbedMessage(
        Locale lo,
        String messageId,
        double[] embeds
    );
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージを取得する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @return 指定ロケール埋め込みメッセージ
     */
    public String findEmbedMessage(
        Locale lo,
        String messageId,
        boolean[] embeds
    );
    
    /**
     * 指定したメッセージIDの埋め込みメッセージを取得する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @return 埋め込みメッセージ
     */
    public String findEmbedMessage(String messageId, Object embed);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージを取得する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @return 埋め込みメッセージ
     */
    public String findEmbedMessage(String messageId, byte embed);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージを取得する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @return 埋め込みメッセージ
     */
    public String findEmbedMessage(String messageId, short embed);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージを取得する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @return 埋め込みメッセージ
     */
    public String findEmbedMessage(String messageId, char embed);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージを取得する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @return 埋め込みメッセージ
     */
    public String findEmbedMessage(String messageId, int embed);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージを取得する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @return 埋め込みメッセージ
     */
    public String findEmbedMessage(String messageId, long embed);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージを取得する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @return 埋め込みメッセージ
     */
    public String findEmbedMessage(String messageId, float embed);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージを取得する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @return 埋め込みメッセージ
     */
    public String findEmbedMessage(String messageId, double embed);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージを取得する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @return 埋め込みメッセージ
     */
    public String findEmbedMessage(String messageId, boolean embed);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージを取得する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @return 指定ロケール埋め込みメッセージ
     */
    public String findEmbedMessage(Locale lo, String messageId, Object embed);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージを取得する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @return 指定ロケール埋め込みメッセージ
     */
    public String findEmbedMessage(Locale lo, String messageId, byte embed);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージを取得する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @return 指定ロケール埋め込みメッセージ
     */
    public String findEmbedMessage(Locale lo, String messageId, short embed);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージを取得する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @return 指定ロケール埋め込みメッセージ
     */
    public String findEmbedMessage(Locale lo, String messageId, char embed);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージを取得する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @return 指定ロケール埋め込みメッセージ
     */
    public String findEmbedMessage(Locale lo, String messageId, int embed);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージを取得する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @return 指定ロケール埋め込みメッセージ
     */
    public String findEmbedMessage(Locale lo, String messageId, long embed);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージを取得する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @return 指定ロケール埋め込みメッセージ
     */
    public String findEmbedMessage(Locale lo, String messageId, float embed);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージを取得する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @return 指定ロケール埋め込みメッセージ
     */
    public String findEmbedMessage(Locale lo, String messageId, double embed);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージを取得する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @return 指定ロケール埋め込みメッセージ
     */
    public String findEmbedMessage(Locale lo, String messageId, boolean embed);
}
