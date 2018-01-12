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
package jp.ossc.nimbus.service.aop;

import java.util.Map;

import jp.ossc.nimbus.core.*;

/**
 * {@link DefaultInterceptorChainFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see DefaultInterceptorChainFactoryService
 */
public interface DefaultInterceptorChainFactoryServiceMBean
 extends ServiceBaseMBean{
    
    /**
     * デフォルトの日付フォーマット。<p>
     */
    public static final String DEFAULT_DATE_FORMAT = "HH:mm:ss.SSS";
    
    /**
     * {@link InterceptorChainFactory#getInterceptorChain(Object)}の引数で指定するキー文字列として正規表現を有効にするかどうかを設定する。<p>
     * デフォルトでは、false。<br>
     *
     * @param isEnable 正規表現を有効にする場合true
     */
    public void setRegexEnabled(boolean isEnable);
    
    /**
     * {@link InterceptorChainFactory#getInterceptorChain(Object)}の引数で指定するキー文字列として正規表現を有効にするかどうかを判定する。<p>
     *
     * @return trueの場合、正規表現を有効にする
     */
    public boolean isRegexEnabled();
    
    /**
     * 正規表現比較を行う場合に使用するマッチフラグを設定する。<p>
     * 但し、{@link #isRegexEnabled()}がtrueの場合のみ有効である。<br>
     * デフォルトは、0。<br>
     *
     * @param flag マッチフラグ
     * @see java.util.regex.Pattern#CANON_EQ
     * @see java.util.regex.Pattern#CASE_INSENSITIVE
     * @see java.util.regex.Pattern#DOTALL
     * @see java.util.regex.Pattern#MULTILINE
     * @see java.util.regex.Pattern#UNICODE_CASE
     * @see java.util.regex.Pattern#UNIX_LINES
     */
    public void setRegexMatchFlag(int flag);
    
    /**
     * 正規表現比較を行う場合に使用するマッチフラグを取得する。<p>
     *
     * @return マッチフラグ
     */
    public int getRegexMatchFlag();
    
    /**
     * キーに該当する{@link InterceptorChainList}サービス名を設定する。<p>
     *
     * @param mapping キー文字列とInterceptorChainListサービス名のマッピング。キー文字列=InterceptorChainListサービス名で複数指定する
     */
    public void setInterceptorChainListMapping(Map mapping);
    
    /**
     * キーに該当する{@link InterceptorChainList}サービス名を取得する。<p>
     *
     * @return キー文字列とInterceptorChainListサービス名のマッピング
     */
    public Map getInterceptorChainListMapping();
    
    /**
     * キーに該当する{@link Interceptor}サービス名を設定する。<p>
     * 同じキーに該当する{@link #setInterceptorChainListMapping(Map)}の設定がある場合は、そちらが優先される。<br>
     *
     * @param mapping キー文字列とInterceptorサービス名のマッピング。キー文字列=Interceptorサービス名で複数指定する
     */
    public void setInterceptorMapping(Map mapping);
    
    /**
     * キーに該当する{@link Interceptor}サービス名を取得する。<p>
     *
     * @return キー文字列とInterceptorサービス名のマッピング
     */
    public Map getInterceptorMapping();
    
    /**
     * {@link #getInterceptorChainListMapping()}に該当する{@link InterceptorChainList}サービスのマッピングが存在しない場合に使用するInterceptorChainListサービスのサービス名を設定する。<p>
     *
     * @param name InterceptorChainListサービスのサービス名
     */
    public void setDefaultInterceptorChainListServiceName(ServiceName name);
    
    /**
     * {@link #getInterceptorChainListMapping()}に該当する{@link InterceptorChainList}サービスのマッピングが存在しない場合に使用するInterceptorChainListサービスのサービス名を取得する。<p>
     *
     * @return InterceptorChainListサービスのサービス名
     */
    public ServiceName getDefaultInterceptorChainListServiceName();
    
    /**
     * キーに該当する{@link Invoker}サービス名を設定する。<p>
     *
     * @param mapping キー文字列とInvokerサービス名のマッピング。キー文字列=Invokerサービス名で複数指定する
     */
    public void setInvokerMapping(Map mapping);
    
    /**
     * キーに該当する{@link Invoker}サービス名を取得する。<p>
     *
     * @return キー文字列とInvokerサービス名のマッピング
     */
    public Map getInvokerMapping();
    
    /**
     * {@link #getInvokerMapping()}に該当する{@link Invoker}サービスのマッピングが存在しない場合に使用するInvokerサービスのサービス名を設定する。<p>
     * 指定しない場合は、{@link jp.ossc.nimbus.service.aop.invoker.MethodReflectionCallInvokerService MethodReflectionCallInvokerService}が使用される。<br>
     *
     * @param name Invokerサービスのサービス名
     */
    public void setDefaultInvokerServiceName(ServiceName name);
    
    /**
     * {@link #getInvokerMapping()}に該当する{@link Invoker}サービスのマッピングが存在しない場合に使用するInvokerサービスのサービス名を取得する。<p>
     *
     * @return Invokerサービスのサービス名
     */
    public ServiceName getDefaultInvokerServiceName();
    
    /**
     * {@link InterceptorChainFactory#getInterceptorChain(Object)}の戻り値をキャッシュする{@link jp.ossc.nimbus.service.cache.CacheMap CacheMap}サービスのサービス名を設定する。<p>
     * {@link DefaultInterceptorChainFactoryService}が生成する{@link InterceptorChain}は、{@link DefaultThreadLocalInterceptorChain}なので、スレッド単位での再利用が可能である。<br>
     * この属性を指定しない場合は、キャッシュせずに毎回生成する。<br>
     *
     * @param name CacheMapサービスのサービス名
     */
    public void setInterceptorChainCacheMapServiceName(ServiceName name);
    
    /**
     * {@link InterceptorChainFactory#getInterceptorChain(Object)}の戻り値をキャッシュする{@link jp.ossc.nimbus.service.cache.CacheMap CacheMap}サービスのサービス名を取得する。<p>
     *
     * @return CacheMapサービスのサービス名
     */
    public ServiceName getInterceptorChainCacheMapServiceName();
    
    /**
     * {@link InterceptorChain}の実装クラスとして、{@link DefaultThreadLocalInterceptorChain}を使用するかどうかを設定する。<p>
     * falseの場合は、{@link DefaultInterceptorChain}を使用する。
     *
     * @param isUse 使用する場合true
     */
    public void setUseThreadLocalInterceptorChain(boolean isUse);
    
    /**
     * {@link InterceptorChain}の実装クラスとして、{@link DefaultThreadLocalInterceptorChain}を使用するかどうかを判定する。<p>
     *
     * @return trueの場合、使用する
     */
    public boolean isUseThreadLocalInterceptorChain();
    
    /**
     * メトリクス情報を表示する。<p>
     *
     * @return メトリクス情報
     */
    public String displayMetricsInfo();
    
    /**
     * 取得したメトリクス情報をリセットする。<p>
     */
    public void reset();
    
    /**
     * メトリクス取得を行うかどうかを設定する。<p>
     * デフォルトでは、false。
     *
     * @param isGet メトリクス取得を行う場合true
     */
    public void setGetMetrics(boolean isGet);
    
    /**
     * メトリクス取得を行うかどうかを判定する。<p>
     *
     * @return メトリクス取得を行う場合true
     */
    public boolean isGetMetrics();
    
    /**
     * 正常応答を返した場合だけ処理時間等の計算を行うかどうかを設定する。<p>
     * デフォルトはfalse
     *
     * @param isCalc 正常応答を返した場合だけ処理時間等の計算を行う場合は、true
     */
    public void setCalculateOnlyNormal(boolean isCalc);
    
    /**
     * 正常応答を返した場合だけ処理時間等の計算を行うかどうかを判定する。<p>
     *
     * @return trueの場合は、正常応答を返した場合だけ処理時間等の計算を行う
     */
    public boolean isCalculateOnlyNormal();
    
    /**
     * メトリクスに出力する時刻のフォーマットを設定する。<p>
     *
     * @param format 日付フォーマット
     */
    public void setDateFormat(String format);
    
    /**
     * メトリクスに出力する時刻のフォーマットを取得する。<p>
     *
     * @return 日付フォーマット
     */
    public String getDateFormat();
    
    /**
     * メトリクス取得タイムスタンプを出力するかどうかを設定する。
     * デフォルトはfalse。
     *
     * @param isOutput 出力する場合はtrue
     */
    public void setOutputTimestamp(boolean isOutput);
    
    /**
     * メトリクス取得タイムスタンプを出力するかどうかを判定する。
     *
     * @return trueの場合は出力する
     */
    public boolean isOutputTimestamp();
    
    /**
     * 呼び出し回数（正常応答）を出力するかどうかを設定する。
     * デフォルトはtrue。
     *
     * @param isOutput 出力する場合はtrue
     */
    public void setOutputCount(boolean isOutput);
    
    /**
     * 呼び出し回数（正常応答）を出力するかどうかを判定する。
     *
     * @return trueの場合は出力する
     */
    public boolean isOutputCount();
    
    /**
     * 呼び出し回数（例外応答）を出力するかどうかを設定する。
     * デフォルトはfalse。
     *
     * @param isOutput 出力する場合はtrue
     */
    public void setOutputExceptionCount(boolean isOutput);
    
    /**
     * 呼び出し回数（例外応答）を出力するかどうかを判定する。
     *
     * @return trueの場合は出力する
     */
    public boolean isOutputExceptionCount();
    
    /**
     * 呼び出し回数（エラー応答）を出力するかどうかを設定する。
     * デフォルトはfalse。
     *
     * @param isOutput 出力する場合はtrue
     */
    public void setOutputErrorCount(boolean isOutput);
    
    /**
     * 呼び出し回数（エラー応答）を出力するかどうかを判定する。
     *
     * @return trueの場合は出力する
     */
    public boolean isOutputErrorCount();
    
    /**
     * 呼び出し最終時刻を出力するかどうかを設定する。
     * デフォルトはfalse。
     *
     * @param isOutput 出力する場合はtrue
     */
    public void setOutputLastTime(boolean isOutput);
    
    /**
     * 呼び出し最終時刻を出力するかどうかを判定する。
     *
     * @return trueの場合は出力する
     */
    public boolean isOutputLastTime();
    
    /**
     * 例外発生最終時刻を出力するかどうかを設定する。
     * デフォルトはfalse。
     *
     * @param isOutput 出力する場合はtrue
     */
    public void setOutputLastExceptionTime(boolean isOutput);
    
    /**
     * 例外発生最終時刻を出力するかどうかを判定する。
     *
     * @return trueの場合は出力する
     */
    public boolean isOutputLastExceptionTime();
    
    /**
     * エラー発生最終時刻を出力するかどうかを設定する。
     * デフォルトはfalse。
     *
     * @param isOutput 出力する場合はtrue
     */
    public void setOutputLastErrorTime(boolean isOutput);
    
    /**
     * エラー発生最終時刻を出力するかどうかを判定する。
     *
     * @return trueの場合は出力する
     */
    public boolean isOutputLastErrorTime();
    
    /**
     * 最高処理時間を出力するかどうかを設定する。
     * デフォルトはtrue。
     *
     * @param isOutput 出力する場合はtrue
     */
    public void setOutputBestPerformance(boolean isOutput);
    
    /**
     * 最高処理時間を出力するかどうかを判定する。
     *
     * @return trueの場合は出力する
     */
    public boolean isOutputBestPerformance();
    
    /**
     * 最高処理時刻を出力するかどうかを設定する。
     * デフォルトはfalse。
     *
     * @param isOutput 出力する場合はtrue
     */
    public void setOutputBestPerformanceTime(boolean isOutput);
    
    /**
     * 最高処理時刻を出力するかどうかを判定する。
     *
     * @return trueの場合は出力する
     */
    public boolean isOutputBestPerformanceTime();
    
    /**
     * 最低処理時間を出力するかどうかを設定する。
     * デフォルトはtrue。
     *
     * @param isOutput 出力する場合はtrue
     */
    public void setOutputWorstPerformance(boolean isOutput);
    
    /**
     * 最低処理時間を出力するかどうかを判定する。
     *
     * @return trueの場合は出力する
     */
    public boolean isOutputWorstPerformance();
    
    /**
     * 最低処理時刻を出力するかどうかを設定する。
     * デフォルトはfalse。
     *
     * @param isOutput 出力する場合はtrue
     */
    public void setOutputWorstPerformanceTime(boolean isOutput);
    
    /**
     * 最低処理時刻を出力するかどうかを判定する。
     *
     * @return trueの場合は出力する
     */
    public boolean isOutputWorstPerformanceTime();
    
    /**
     * 平均処理時間を出力するかどうかを設定する。
     * デフォルトはtrue。
     *
     * @param isOutput 出力する場合はtrue
     */
    public void setOutputAveragePerformance(boolean isOutput);
    
    /**
     * 平均処理時間を出力するかどうかを判定する。
     *
     * @return trueの場合は出力する
     */
    public boolean isOutputAveragePerformance();
}