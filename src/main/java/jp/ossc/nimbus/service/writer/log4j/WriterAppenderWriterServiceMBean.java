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
package jp.ossc.nimbus.service.writer.log4j;

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.service.writer.*;

/**
 * {@link WriterAppenderWriterService}サービスのMBeanインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface WriterAppenderWriterServiceMBean
 extends ServiceBaseMBean, MessageWriter{
    
    /**
     * 毎回フラッシュするかを設定する。<p>
     *
     * @param flush 毎回フラッシュする場合true
     */
    public void setImmediateFlush(boolean flush);
    
    /**
     * 毎回フラッシュするかを調べる。<p>
     *
     * @return trueの場合、毎回フラッシュする
     */
    public boolean isImmediateFlush();
    
    /**
     * 出力ファイルの文字エンコーディングを設定する。<p>
     *
     * @param encoding 文字エンコーディング
     */
    public void setEncoding(String encoding);
    
    /**
     * 出力ファイルの文字エンコーディングを取得する。<p>
     *
     * @return 文字エンコーディング
     */
    public String getEncoding();
    
    /**
     * レイアウトのヘッダ文字列を設定する。<p>
     *
     * @param header ヘッダ文字列
     */
    public void setHeader(String header);
    
    /**
     * レイアウトのヘッダ文字列を取得する。<p>
     *
     * @return ヘッダ文字列
     */
    public String getHeader();
    
    /**
     * レイアウトのフッタ文字列を設定する。<p>
     *
     * @param footer フッタ文字列
     */
    public void setFooter(String footer);
    
    /**
     * レイアウトのフッタ文字列を取得する。<p>
     *
     * @return フッタ文字列
     */
    public String getFooter();
    
    /**
     * 同期的に書き込むかどうかを設定する。<p>
     * デフォルトは、falseで同期化しない。<br>
     *
     * @param isSynch 同期的に書き込む場合、true
     */
    public void setSynchronized(boolean isSynch);
    
    /**
     * 同期的に書き込むかどうかを判定する。<p>
     *
     * @return trueの場合、同期的に書き込む
     */
    public boolean isSynchronized();
}