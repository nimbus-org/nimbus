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
package jp.ossc.nimbus.service.proxy.invoker;

import jp.ossc.nimbus.core.*;

/**
 * {@link ClusterInvokerService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see ClusterInvokerService
 */
public interface ClusterInvokerServiceMBean
 extends ServiceBaseMBean{
    
    /**
     * 呼び出し対象をスレッドコンテキストで指定して呼び出す際に、呼び出し対象を指定するコンテキストキー。<p>
     * スレッドコンテキストの値は、{@link jp.ossc.nimbus.service.proxy.invoker.KeepAliveCheckInvoker KeepAliveCheckInvoker}のサービス名、または、{@link jp.ossc.nimbus.service.proxy.invoker.KeepAliveCheckInvoker#getHostInfo() KeepAliveCheckInvoker.getHostInfo()}の値。<br>
     */
    public static final String CONTEXT_KEY_INVOKE_TARGET = ClusterInvokerServiceMBean.class.getName().replace('.', '_') + "_TARGET";
    
    /**
     * 呼び出し時点で生存している全ての呼び出し対象をスレッドコンテキストで指定して呼び出す際に、指定するコンテキストキー。<p>
     * スレッドコンテキストの値は、Boolean.TRUEまたはBoolean.valueOf(String)がtrueを返す文字列。<br>
     */
    public static final String CONTEXT_KEY_INVOKE_BROADCAST = ClusterInvokerServiceMBean.class.getName().replace('.', '_') + "_BROADCAST";
    
    /**
     * {@link jp.ossc.nimbus.service.proxy.invoker.KeepAliveCheckInvoker KeepAliveCheckInvoker}を選択する{@link jp.ossc.nimbus.service.keepalive.KeepAliveCheckerSelector KeepAliveCheckerSelector}サービスのサービス名を設定する。<p>
     *
     * @param name KeepAliveCheckerSelectorサービスのサービス名
     */
    public void setKeepAliveCheckerSelectorServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.proxy.invoker.KeepAliveCheckInvoker KeepAliveCheckInvoker}を選択する{@link jp.ossc.nimbus.service.keepalive.KeepAliveCheckerSelector KeepAliveCheckerSelector}サービスのサービス名を設定する。<p>
     *
     * @return KeepAliveCheckerSelectorサービスのサービス名
     */
    public ServiceName getKeepAliveCheckerSelectorServiceName();
    
    /**
     * リトライする例外のクラス名とその条件を設定する。<p>
     * この条件を設定しない場合は、{@link jp.ossc.nimbus.service.proxy.RemoteServiceCallException}をキャッチした場合にリトライする。<br>
     * 条件式は、The Apache Jakarta Projectの Commons Jexl(http://jakarta.apache.org/commons/jexl/)を使用する。<br>
     * 例外のプロパティを参照する場合は、プロパティを表現する文字列を"@"で囲んで指定する。ここで言う、プロパティの概念は、Java Beansのプロパティの概念より広く、{@link jp.ossc.nimbus.beans.PropertyFactory PropertyFactory}の規約に従う。<br>
     *
     * @param conditions 例外クラス名:条件式（条件が必要ない場合は、:以下を省略可能）の文字列配列
     */
    public void setExceptionConditions(String[] conditions);
    
    /**
     * リトライする例外のクラス名とその条件を取得する。<p>
     *
     * @return 例外クラス名:条件式（条件が必要ない場合は、:以下を省略可能）の文字列配列
     */
    public String[] getExceptionConditions();
    
    /**
     * リトライする回数を設定する。<p>
     * デフォルトは、0でリトライしない。<br>
     *
     * @param count リトライする回数
     */
    public void setMaxRetryCount(int count);
    
    /**
     * リトライする回数を取得する。<p>
     *
     * @return リトライする回数
     */
    public int getMaxRetryCount();
    
    /**
     * リトライする間隔[ms]を設定する。<p>
     * デフォルトは、0で間隔をあけずにリトライする。<br>
     *
     * @param interval リトライする間隔[ms]
     */
    public void setRetryInterval(long interval);
    
    /**
     * リトライする間隔[ms]を取得する。<p>
     *
     * @return リトライする間隔[ms]
     */
    public long getRetryInterval();
    
    /**
     * 呼び出し時点で生存している全ての{@link jp.ossc.nimbus.service.proxy.invoker.KeepAliveCheckInvoker KeepAliveCheckInvoker}を呼び出すようにするかどうかを設定する。<p>
     * trueの場合、呼び出し時点で生存している全てのKeepAliveCheckInvokerを順次呼び出す。その際、リトライ機能は無効となり、どれか１つのKeepAliveCheckInvokerの呼び出しで例外が発生すると、そこで終了する。<br>
     *
     * @param isBroadcast 全てのKeepAliveCheckInvokerを呼び出す場合は、true
     */
    public void setBroadcast(boolean isBroadcast);
    
    /**
     * 呼び出し時点で生存している全ての{@link jp.ossc.nimbus.service.proxy.invoker.KeepAliveCheckInvoker KeepAliveCheckInvoker}を呼び出すようにするかどうかを判定する。<p>
     *
     * @return trueの場合、全てのKeepAliveCheckInvokerを呼び出す
     */
    public boolean isBroadcast();
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を設定する。<p>
     *
     * @param name Contextサービスのサービス名
     */
    public void setThreadContextServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を取得する。<p>
     *
     * @return Contextサービスのサービス名
     */
    public ServiceName getThreadContextServiceName();
}