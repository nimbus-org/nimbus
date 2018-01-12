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
package jp.ossc.nimbus.service.log;

import jp.ossc.nimbus.lang.*;
import java.util.*;

/**
 * ログインターフェイス。<p>
 * 
 * @author Y.Tokuda
 */
public interface Logger {
    
    /**
     * 指定したメッセージIDのメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     */
    public void write(String messageId);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     */
    public void write(String messageId, Object embed);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     */
    public void write(String messageId, byte embed);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     */
    public void write(String messageId, short embed);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     */
    public void write(String messageId, char embed);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     */
    public void write(String messageId, int embed);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     */
    public void write(String messageId, long embed);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     */
    public void write(String messageId, float embed);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     */
    public void write(String messageId, double embed);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     */
    public void write(String messageId, boolean embed);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     */
    public void write(String messageId, Object[] embeds);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     */
    public void write(String messageId, byte[] embeds);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     */
    public void write(String messageId, short[] embeds);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     */
    public void write(String messageId, char[] embeds);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     */
    public void write(String messageId, int[] embeds);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     */
    public void write(String messageId, long[] embeds);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     */
    public void write(String messageId, float[] embeds);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     */
    public void write(String messageId, double[] embeds);
    
    /**
     * 指定したメッセージIDの埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     */
    public void write(String messageId, boolean[] embeds);
    
    /**
     * 指定したメッセージIDの例外付きメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param oException 例外
     */
    public void write(String messageId, Throwable oException);
    
    /**
     * 指定したメッセージIDの例外付き埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @param oException 例外
     */
    public void write(String messageId, Object embed, Throwable oException);
    
    /**
     * 指定したメッセージIDの例外付き埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @param oException 例外
     */
    public void write(String messageId, byte embed, Throwable oException);
    
    /**
     * 指定したメッセージIDの例外付き埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @param oException 例外
     */
    public void write(String messageId, short embed, Throwable oException);
    
    /**
     * 指定したメッセージIDの例外付き埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @param oException 例外
     */
    public void write(String messageId, char embed, Throwable oException);
    
    /**
     * 指定したメッセージIDの例外付き埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @param oException 例外
     */
    public void write(String messageId, int embed, Throwable oException);
    
    /**
     * 指定したメッセージIDの例外付き埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @param oException 例外
     */
    public void write(String messageId, long embed, Throwable oException);
    
    /**
     * 指定したメッセージIDの例外付き埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @param oException 例外
     */
    public void write(String messageId, float embed, Throwable oException);
    
    /**
     * 指定したメッセージIDの例外付き埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @param oException 例外
     */
    public void write(String messageId, double embed, Throwable oException);
    
    /**
     * 指定したメッセージIDの例外付き埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @param oException 例外
     */
    public void write(String messageId, boolean embed, Throwable oException);
    
    /**
     * 指定したメッセージIDの例外付き埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @param oException 例外
     */
    public void write(String messageId, Object[] embeds, Throwable oException) ;
    
    /**
     * 指定したメッセージIDの例外付き埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @param oException 例外
     */
    public void write(String messageId, byte[] embeds, Throwable oException);
    
    /**
     * 指定したメッセージIDの例外付き埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @param oException 例外
     */
    public void write(String messageId, short[] embeds, Throwable oException);
    
    /**
     * 指定したメッセージIDの例外付き埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @param oException 例外
     */
    public void write(String messageId, char[] embeds, Throwable oException);
    
    /**
     * 指定したメッセージIDの例外付き埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @param oException 例外
     */
    public void write(String messageId, int[] embeds, Throwable oException);
    
    /**
     * 指定したメッセージIDの例外付き埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @param oException 例外
     */
    public void write(String messageId, long[] embeds, Throwable oException);
    
    /**
     * 指定したメッセージIDの例外付き埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @param oException 例外
     */
    public void write(String messageId, float[] embeds, Throwable oException);
    
    /**
     * 指定したメッセージIDの例外付き埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @param oException 例外
     */
    public void write(String messageId, double[] embeds, Throwable oException);
    
    /**
     * 指定したメッセージIDの例外付き埋め込みメッセージをログに出力する。<p>
     *
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @param oException 例外
     */
    public void write(String messageId, boolean[] embeds, Throwable oException);
    
    /**
     * 指定したメッセージIDの指定ロケールメッセージをログに出力する。<p>
     * 指定したロケールのメッセージが見つからない場合には、より近いロケールのメッセージを出力する。<br>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     */
    public void write(Locale lo, String messageId);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージをログに出力する。<p>
     * 指定したロケールのメッセージが見つからない場合には、より近いロケールのメッセージを出力する。<br>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     */
    public void write(Locale lo, String messageId, Object embed);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージをログに出力する。<p>
     * 指定したロケールのメッセージが見つからない場合には、より近いロケールのメッセージを出力する。<br>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     */
    public void write(Locale lo, String messageId, byte embed);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージをログに出力する。<p>
     * 指定したロケールのメッセージが見つからない場合には、より近いロケールのメッセージを出力する。<br>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     */
    public void write(Locale lo, String messageId, short embed);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージをログに出力する。<p>
     * 指定したロケールのメッセージが見つからない場合には、より近いロケールのメッセージを出力する。<br>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     */
    public void write(Locale lo, String messageId, char embed);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージをログに出力する。<p>
     * 指定したロケールのメッセージが見つからない場合には、より近いロケールのメッセージを出力する。<br>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     */
    public void write(Locale lo, String messageId, int embed);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージをログに出力する。<p>
     * 指定したロケールのメッセージが見つからない場合には、より近いロケールのメッセージを出力する。<br>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     */
    public void write(Locale lo, String messageId, long embed);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージをログに出力する。<p>
     * 指定したロケールのメッセージが見つからない場合には、より近いロケールのメッセージを出力する。<br>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     */
    public void write(Locale lo, String messageId, float embed);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージをログに出力する。<p>
     * 指定したロケールのメッセージが見つからない場合には、より近いロケールのメッセージを出力する。<br>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     */
    public void write(Locale lo, String messageId, double embed);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージをログに出力する。<p>
     * 指定したロケールのメッセージが見つからない場合には、より近いロケールのメッセージを出力する。<br>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     */
    public void write(Locale lo, String messageId, boolean embed);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージをログに出力する。<p>
     * 指定したロケールのメッセージが見つからない場合には、より近いロケールのメッセージを出力する。<br>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     */
    public void write(Locale lo, String messageId, Object[] embeds);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージをログに出力する。<p>
     * 指定したロケールのメッセージが見つからない場合には、より近いロケールのメッセージを出力する。<br>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     */
    public void write(Locale lo, String messageId, byte[] embeds);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージをログに出力する。<p>
     * 指定したロケールのメッセージが見つからない場合には、より近いロケールのメッセージを出力する。<br>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     */
    public void write(Locale lo, String messageId, short[] embeds);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージをログに出力する。<p>
     * 指定したロケールのメッセージが見つからない場合には、より近いロケールのメッセージを出力する。<br>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     */
    public void write(Locale lo, String messageId, char[] embeds);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージをログに出力する。<p>
     * 指定したロケールのメッセージが見つからない場合には、より近いロケールのメッセージを出力する。<br>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     */
    public void write(Locale lo, String messageId, int[] embeds);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージをログに出力する。<p>
     * 指定したロケールのメッセージが見つからない場合には、より近いロケールのメッセージを出力する。<br>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     */
    public void write(Locale lo, String messageId, long[] embeds);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージをログに出力する。<p>
     * 指定したロケールのメッセージが見つからない場合には、より近いロケールのメッセージを出力する。<br>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     */
    public void write(Locale lo, String messageId, float[] embeds);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージをログに出力する。<p>
     * 指定したロケールのメッセージが見つからない場合には、より近いロケールのメッセージを出力する。<br>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     */
    public void write(Locale lo, String messageId, double[] embeds);
    
    /**
     * 指定したメッセージIDの指定ロケール埋め込みメッセージをログに出力する。<p>
     * 指定したロケールのメッセージが見つからない場合には、より近いロケールのメッセージを出力する。<br>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     */
    public void write(Locale lo, String messageId, boolean[] embeds);
    
    /**
     * 指定したメッセージIDの例外付き指定ロケールメッセージをログに出力する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param oException 例外
     */
    public void write(Locale lo, String messageId, Throwable oException);
    
    /**
     * 指定したメッセージIDの例外付き指定ロケール埋め込みメッセージをログに出力する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @param oException 例外
     */
    public void write(
        Locale lo,
        String messageId,
        Object embed,
        Throwable oException
    );
    
    /**
     * 指定したメッセージIDの例外付き指定ロケール埋め込みメッセージをログに出力する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @param oException 例外
     */
    public void write(
        Locale lo,
        String messageId,
        byte embed,
        Throwable oException
    );
    
    /**
     * 指定したメッセージIDの例外付き指定ロケール埋め込みメッセージをログに出力する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @param oException 例外
     */
    public void write(
        Locale lo,
        String messageId,
        short embed,
        Throwable oException
    );
    
    /**
     * 指定したメッセージIDの例外付き指定ロケール埋め込みメッセージをログに出力する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @param oException 例外
     */
    public void write(
        Locale lo,
        String messageId,
        char embed,
        Throwable oException
    );
    
    /**
     * 指定したメッセージIDの例外付き指定ロケール埋め込みメッセージをログに出力する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @param oException 例外
     */
    public void write(
        Locale lo,
        String messageId,
        int embed,
        Throwable oException
    );
    
    /**
     * 指定したメッセージIDの例外付き指定ロケール埋め込みメッセージをログに出力する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @param oException 例外
     */
    public void write(
        Locale lo,
        String messageId,
        long embed,
        Throwable oException
    );
    
    /**
     * 指定したメッセージIDの例外付き指定ロケール埋め込みメッセージをログに出力する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @param oException 例外
     */
    public void write(
        Locale lo,
        String messageId,
        float embed,
        Throwable oException
    );
    
    /**
     * 指定したメッセージIDの例外付き指定ロケール埋め込みメッセージをログに出力する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @param oException 例外
     */
    public void write(
        Locale lo,
        String messageId,
        double embed,
        Throwable oException
    );
    
    /**
     * 指定したメッセージIDの例外付き指定ロケール埋め込みメッセージをログに出力する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embed 埋め込みパラメータ
     * @param oException 例外
     */
    public void write(
        Locale lo,
        String messageId,
        boolean embed,
        Throwable oException
    );
    
    /**
     * 指定したメッセージIDの例外付き指定ロケール埋め込みメッセージをログに出力する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @param oException 例外
     */
    public void write(
        Locale lo,
        String messageId,
        Object[] embeds,
        Throwable oException
    );
    
    /**
     * 指定したメッセージIDの例外付き指定ロケール埋め込みメッセージをログに出力する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @param oException 例外
     */
    public void write(
        Locale lo,
        String messageId,
        byte[] embeds,
        Throwable oException
    );
    
    /**
     * 指定したメッセージIDの例外付き指定ロケール埋め込みメッセージをログに出力する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @param oException 例外
     */
    public void write(
        Locale lo,
        String messageId,
        short[] embeds,
        Throwable oException
    );
    
    /**
     * 指定したメッセージIDの例外付き指定ロケール埋め込みメッセージをログに出力する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @param oException 例外
     */
    public void write(
        Locale lo,
        String messageId,
        char[] embeds,
        Throwable oException
    );
    
    /**
     * 指定したメッセージIDの例外付き指定ロケール埋め込みメッセージをログに出力する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @param oException 例外
     */
    public void write(
        Locale lo,
        String messageId,
        int[] embeds,
        Throwable oException
    );
    
    /**
     * 指定したメッセージIDの例外付き指定ロケール埋め込みメッセージをログに出力する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @param oException 例外
     */
    public void write(
        Locale lo,
        String messageId,
        long[] embeds,
        Throwable oException
    );
    
    /**
     * 指定したメッセージIDの例外付き指定ロケール埋め込みメッセージをログに出力する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @param oException 例外
     */
    public void write(
        Locale lo,
        String messageId,
        float[] embeds,
        Throwable oException
    );
    
    /**
     * 指定したメッセージIDの例外付き指定ロケール埋め込みメッセージをログに出力する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @param oException 例外
     */
    public void write(
        Locale lo,
        String messageId,
        double[] embeds,
        Throwable oException
    );
    
    /**
     * 指定したメッセージIDの例外付き指定ロケール埋め込みメッセージをログに出力する。<p>
     *
     * @param lo ロケール
     * @param messageId メッセージID
     * @param embeds 埋め込みパラメータ配列
     * @param oException 例外
     */
    public void write(
        Locale lo,
        String messageId,
        boolean[] embeds,
        Throwable oException
    );
    
    /**
     * 指定したメッセージID付き例外の例外付きメッセージをログに出力する。<p>
     *
     * @param e メッセージID付き例外
     */
    public void write(AppException e);
    
    /**
     * 指定したメッセージID付き例外の例外付き指定ロケールメッセージをログに出力する。<p>
     *
     * @param lo ロケール
     * @param e メッセージID付き例外
     */
    public void write(Locale lo, AppException e);
    
    /**
     * 指定されたメッセージIDのメッセージがログ出力されるかどうかを判定する。<p>
     *
     * @param messageId メッセージID
     * @return 指定されたメッセージIDのメッセージがログ出力される場合true
     */
    public boolean isWrite(String messageId);
    
    /**
     * 指定したメッセージのデバッグログを出力する。<p>
     *
     * @param msg メッセージ
     */
    public void debug(Object msg);
    
    /**
     * 指定したメッセージの例外付きデバッグログを出力する。<p>
     *
     * @param msg メッセージ
     * @param oException 例外
     */
    public void debug(Object msg, Throwable oException);
    
    /**
     * デバッグログが出力されるかどうかを判定する。<p>
     *
     * @return デバッグログが出力される場合true
     */
    public boolean isDebugWrite();
}
