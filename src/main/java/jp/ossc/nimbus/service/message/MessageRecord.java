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
 * メッセージレコード。<p>
 *
 * @author H.Nakano
 */
public interface MessageRecord {
    
    /**
     * メッセージIDを取得する。<p>
     *
     * @return メッセージID
     */
    public String getMessageCode();
    
    /**
     * 指定したロケールのメッセージを取得する。<p>
     * 
     * @param lc ロケール
     * @return 指定したロケールのメッセージ
     */
    public String makeMessage(Locale lc);
    
    /**
     * 指定したロケールの埋め込みメッセージを取得する。<p>
     * 
     * @param lc ロケール
     * @param embed 埋め込みパラメータ
     * @return 指定したロケールの埋め込みメッセージ
     */
    public String makeMessage(Locale lc, Object embed);
    
    /**
     * 指定したロケールの埋め込みメッセージを取得する。<p>
     * 
     * @param lc ロケール
     * @param embed 埋め込みパラメータ
     * @return 指定したロケールの埋め込みメッセージ
     */
    public String makeMessage(Locale lc, byte embed);
    
    /**
     * 指定したロケールの埋め込みメッセージを取得する。<p>
     * 
     * @param lc ロケール
     * @param embed 埋め込みパラメータ
     * @return 指定したロケールの埋め込みメッセージ
     */
    public String makeMessage(Locale lc, short embed);
    
    /**
     * 指定したロケールの埋め込みメッセージを取得する。<p>
     * 
     * @param lc ロケール
     * @param embed 埋め込みパラメータ
     * @return 指定したロケールの埋め込みメッセージ
     */
    public String makeMessage(Locale lc, char embed);
    
    /**
     * 指定したロケールの埋め込みメッセージを取得する。<p>
     * 
     * @param lc ロケール
     * @param embed 埋め込みパラメータ
     * @return 指定したロケールの埋め込みメッセージ
     */
    public String makeMessage(Locale lc, int embed);
    
    /**
     * 指定したロケールの埋め込みメッセージを取得する。<p>
     * 
     * @param lc ロケール
     * @param embed 埋め込みパラメータ
     * @return 指定したロケールの埋め込みメッセージ
     */
    public String makeMessage(Locale lc, long embed);
    
    /**
     * 指定したロケールの埋め込みメッセージを取得する。<p>
     * 
     * @param lc ロケール
     * @param embed 埋め込みパラメータ
     * @return 指定したロケールの埋め込みメッセージ
     */
    public String makeMessage(Locale lc, float embed);
    
    /**
     * 指定したロケールの埋め込みメッセージを取得する。<p>
     * 
     * @param lc ロケール
     * @param embed 埋め込みパラメータ
     * @return 指定したロケールの埋め込みメッセージ
     */
    public String makeMessage(Locale lc, double embed);
    
    /**
     * 指定したロケールの埋め込みメッセージを取得する。<p>
     * 
     * @param lc ロケール
     * @param embed 埋め込みパラメータ
     * @return 指定したロケールの埋め込みメッセージ
     */
    public String makeMessage(Locale lc, boolean embed);
    
    /**
     * 指定したロケールの埋め込みメッセージを取得する。<p>
     * 
     * @param lc ロケール
     * @param embeds 埋め込みパラメータ配列
     * @return 指定したロケールの埋め込みメッセージ
     */
    public String makeMessage(Locale lc, Object[] embeds);
    
    /**
     * 指定したロケールの埋め込みメッセージを取得する。<p>
     * 
     * @param lc ロケール
     * @param embeds 埋め込みパラメータ配列
     * @return 指定したロケールの埋め込みメッセージ
     */
    public String makeMessage(Locale lc, byte[] embeds);
    
    /**
     * 指定したロケールの埋め込みメッセージを取得する。<p>
     * 
     * @param lc ロケール
     * @param embeds 埋め込みパラメータ配列
     * @return 指定したロケールの埋め込みメッセージ
     */
    public String makeMessage(Locale lc, short[] embeds);
    
    /**
     * 指定したロケールの埋め込みメッセージを取得する。<p>
     * 
     * @param lc ロケール
     * @param embeds 埋め込みパラメータ配列
     * @return 指定したロケールの埋め込みメッセージ
     */
    public String makeMessage(Locale lc, char[] embeds);
    
    /**
     * 指定したロケールの埋め込みメッセージを取得する。<p>
     * 
     * @param lc ロケール
     * @param embeds 埋め込みパラメータ配列
     * @return 指定したロケールの埋め込みメッセージ
     */
    public String makeMessage(Locale lc, int[] embeds);
    
    /**
     * 指定したロケールの埋め込みメッセージを取得する。<p>
     * 
     * @param lc ロケール
     * @param embeds 埋め込みパラメータ配列
     * @return 指定したロケールの埋め込みメッセージ
     */
    public String makeMessage(Locale lc, long[] embeds);
    
    /**
     * 指定したロケールの埋め込みメッセージを取得する。<p>
     * 
     * @param lc ロケール
     * @param embeds 埋め込みパラメータ配列
     * @return 指定したロケールの埋め込みメッセージ
     */
    public String makeMessage(Locale lc, float[] embeds);
    
    /**
     * 指定したロケールの埋め込みメッセージを取得する。<p>
     * 
     * @param lc ロケール
     * @param embeds 埋め込みパラメータ配列
     * @return 指定したロケールの埋め込みメッセージ
     */
    public String makeMessage(Locale lc, double[] embeds);
    
    /**
     * 指定したロケールの埋め込みメッセージを取得する。<p>
     * 
     * @param lc ロケール
     * @param embeds 埋め込みパラメータ配列
     * @return 指定したロケールの埋め込みメッセージ
     */
    public String makeMessage(Locale lc, boolean[] embeds);
    
    /**
     * デフォルトロケールのメッセージを取得する。<p>
     * 
     * @return デフォルトロケールのメッセージ
     */
    public String makeMessage();
    
    /**
     * デフォルトロケールのメッセージを取得する。<p>
     * 
     * @param embed 埋め込みパラメータ
     * @return デフォルトロケールのメッセージ
     */
    public String makeMessage(Object embed);
    
    /**
     * デフォルトロケールのメッセージを取得する。<p>
     * 
     * @param embed 埋め込みパラメータ
     * @return デフォルトロケールのメッセージ
     */
    public String makeMessage(byte embed);
    
    /**
     * デフォルトロケールのメッセージを取得する。<p>
     * 
     * @param embed 埋め込みパラメータ
     * @return デフォルトロケールのメッセージ
     */
    public String makeMessage(short embed);
    
    /**
     * デフォルトロケールのメッセージを取得する。<p>
     * 
     * @param embed 埋め込みパラメータ
     * @return デフォルトロケールのメッセージ
     */
    public String makeMessage(char embed);
    
    /**
     * デフォルトロケールのメッセージを取得する。<p>
     * 
     * @param embed 埋め込みパラメータ
     * @return デフォルトロケールのメッセージ
     */
    public String makeMessage(int embed);
    
    /**
     * デフォルトロケールのメッセージを取得する。<p>
     * 
     * @param embed 埋め込みパラメータ
     * @return デフォルトロケールのメッセージ
     */
    public String makeMessage(long embed);
    
    /**
     * デフォルトロケールのメッセージを取得する。<p>
     * 
     * @param embed 埋め込みパラメータ
     * @return デフォルトロケールのメッセージ
     */
    public String makeMessage(float embed);
    
    /**
     * デフォルトロケールのメッセージを取得する。<p>
     * 
     * @param embed 埋め込みパラメータ
     * @return デフォルトロケールのメッセージ
     */
    public String makeMessage(double embed);
    
    /**
     * デフォルトロケールのメッセージを取得する。<p>
     * 
     * @param embed 埋め込みパラメータ
     * @return デフォルトロケールのメッセージ
     */
    public String makeMessage(boolean embed);
    
    /**
     * デフォルトロケールのメッセージを取得する。<p>
     * 
     * @param embeds 埋め込みパラメータ配列
     * @return デフォルトロケールのメッセージ
     */
    public String makeMessage(Object[] embeds);
    
    /**
     * デフォルトロケールのメッセージを取得する。<p>
     * 
     * @param embeds 埋め込みパラメータ配列
     * @return デフォルトロケールのメッセージ
     */
    public String makeMessage(byte[] embeds);
    
    /**
     * デフォルトロケールのメッセージを取得する。<p>
     * 
     * @param embeds 埋め込みパラメータ配列
     * @return デフォルトロケールのメッセージ
     */
    public String makeMessage(short[] embeds);
    
    /**
     * デフォルトロケールのメッセージを取得する。<p>
     * 
     * @param embeds 埋め込みパラメータ配列
     * @return デフォルトロケールのメッセージ
     */
    public String makeMessage(char[] embeds);
    
    /**
     * デフォルトロケールのメッセージを取得する。<p>
     * 
     * @param embeds 埋め込みパラメータ配列
     * @return デフォルトロケールのメッセージ
     */
    public String makeMessage(int[] embeds);
    
    /**
     * デフォルトロケールのメッセージを取得する。<p>
     * 
     * @param embeds 埋め込みパラメータ配列
     * @return デフォルトロケールのメッセージ
     */
    public String makeMessage(long[] embeds);
    
    /**
     * デフォルトロケールのメッセージを取得する。<p>
     * 
     * @param embeds 埋め込みパラメータ配列
     * @return デフォルトロケールのメッセージ
     */
    public String makeMessage(float[] embeds);
    
    /**
     * デフォルトロケールのメッセージを取得する。<p>
     * 
     * @param embeds 埋め込みパラメータ配列
     * @return デフォルトロケールのメッセージ
     */
    public String makeMessage(double[] embeds);
    
    /**
     * デフォルトロケールのメッセージを取得する。<p>
     * 
     * @param embeds 埋め込みパラメータ配列
     * @return デフォルトロケールのメッセージ
     */
    public String makeMessage(boolean[] embeds);
    
    /**
     * デフォルトロケールのテンプレートメッセージを取得する。<p>
     *
     * @return デフォルトロケールのテンプレートメッセージ
     */
    public String getMessageTemplate();
    
    /**
     * 指定したロケールのテンプレートメッセージを取得する。<p>
     *
     * @param lc ロケール
     * @return 指定したロケールのテンプレートメッセージ
     */
    public String getMessageTemplate(Locale lc);
}
