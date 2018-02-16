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
package jp.ossc.nimbus.service.sequence;

import jp.ossc.nimbus.core.*;

/**
 * {@link StringSequenceService}のMBeanインタフェース。<p>
 * 
 * @author H.Nakano
 */
public interface StringSequenceServiceMBean extends ServiceBaseMBean {
    
    /**
     * 発番する番号のフォーマットを設定する。<p>
     * フォーマットの指定方法は、以下。<br>
     * <ul>
     *   <li>固定要素は、そのまま指定する。</li>
     *   <li>増加要素は、"開始文字,終了文字"で指定する。また、開始文字、終了文字共に1文字で指定しなければならない。</li>
     *   <li>コンテキスト変数要素は、"%コンテキストキー名%"で指定する。</li>
     *   <li>時刻通番要素は、"TIME_SEQ(時刻フォーマット,通番桁数)"で指定する。</li>
     * </ul>
     * また、各要素は、;で区切る。<br>
     * <pre>
     *  設定例：
     *    ID_;%HOST_NAME%;_;0,9;0,9;_;TIME_SEQ(HHmmss,3)
     *    
     *  発番結果：
     *    ID_server1_00001
     * </pre>
     * 
     * @param format 発番する番号のフォーマット文字列
     */
    public void setFormat(String format);
    
    /**
     * 発番する番号のフォーマットを取得する。<p>
     * 
     * @return 発番する番号のフォーマット文字列
     */
    public String getFormat();
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を設定する。<p>
     * 発番する番号にコンテキスト変数要素を使う場合に、取得元のContextサービスを設定する。<br>
     *
     * @param name Contextサービスのサービス名
     */
    public void setContextServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を取得する。<p>
     *
     * @return Contextサービスのサービス名
     */
    public ServiceName getContextServiceName();
    
    /**
     * 発番した番号を永続化するファイル名を設定する。<p>
     * この属性を設定すると、発番した番号をファイルに永続化する。<br>
     * また、サービスの開始時に、永続化ファイルが存在する場合は、読み込んで番号を復元して、最終発番番号とする。<br>
     *
     * @param file 発番した番号を永続化するファイル名
     */
    public void setPersistFile(String file);
    
    /**
     * 発番した番号を永続化するファイル名を取得する。<p>
     *
     * @return 発番した番号を永続化するファイル名
     */
    public String getPersistFile();
    
    /**
     * 番号を発番する毎に永続化するかどうかを設定する。<p>
     * {@link #setPersistFile(String)}で永続化ファイルを設定している場合のみ、有効である。<br>
     * デフォルトは、falseで、サービスの停止時のみ永続化する。その場合、プロセスをkillするなど、サービスの停止処理が動かなかった場合には、永続化されない事がある。<br>
     *
     * @param isEveryTime 番号を発番する毎に永続化する場合はtrue
     */
    public void setPersistEveryTime(boolean isEveryTime);
    
    /**
     * 番号を発番する毎に永続化するかどうかを判定する。<p>
     *
     * @return trueの場合、番号を発番する毎に永続化する
     */
    public boolean isPersistEveryTime();
}
