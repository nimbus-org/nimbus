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
package jp.ossc.nimbus.service.ioccall.interceptor;

import jp.ossc.nimbus.core.*;

/**
 * {@link DefaultAsynchCallInterceptorService}のMBeanインタフェース。<p>
 * 
 * @author Y.Tokuda
 */
public interface DefaultAsynchCallInterceptorServiceMBean
 extends ServiceBaseMBean{
    
    public static final String DELIVERY_MODE_PERSISTENT = "PERSISTENT";
    public static final String DELIVERY_MODE_NON_PERSISTENT = "NON_PERSISTENT";
    
    /**
     * QueueSessionを生成する{@link jp.ossc.nimbus.service.resource.ResourceFactory ResourceFactory}サービスのサービス名を設定する。<p>
     * {@link jp.ossc.nimbus.service.ioccall.FacadeCaller#syncParallelFacadeCall}メソッドを使用する場合は、設定する必要がある。<br>
     *
     * @param name ResourceFactoryサービスのサービス名
     */
    public void setQueueSessionFactoryServiceName(ServiceName name);
    
    /**
     * QueueSessionを生成する{@link jp.ossc.nimbus.service.resource.ResourceFactory ResourceFactory}サービスのサービス名を取得する。<p>
     *
     * @return ResourceFactoryサービスのサービス名
     */
    public ServiceName getQueueSessionFactoryServiceName();
    
    /**
     * JMS Queueに送信する際の配信モードを設定する。<p>
     * 平行処理及び非同期処理をしない場合は、設定する必要はない。<br>
     *
     * @param mode 配信モード
     * @see #DELIVERY_MODE_PERSISTENT
     * @see #DELIVERY_MODE_NON_PERSISTENT
     */
    public void setDeliveryMode(String mode);
    
    /**
     * JMS Queueに送信する際の配信モードを取得する。<p>
     *
     * @return 配信モード
     */
    public String getDeliveryMode();
    
    /**
     * JMS Queueに送信する際のメッセージ優先順位を設定する。<p>
     * 平行処理及び非同期処理をしない場合は、設定する必要はない。<br>
     *
     * @param priority メッセージ優先順位
     */
    public void setPriority(int priority);
    
    /**
     * JMS Queueに送信する際のメッセージ優先順位を取得する。<p>
     *
     * @return メッセージ優先順位
     */
    public int getPriority();
    
    /**
     * JMS Queueに送信する際のメッセージ寿命を設定する。<p>
     * 平行処理及び非同期処理をしない場合は、設定する必要はない。<br>
     *
     * @param millis メッセージ寿命[ms]
     */
    public void setTimeToLive(long millis);
    
    /**
     * JMS Queueに送信する際のメッセージ寿命を取得する。<p>
     *
     * @return メッセージ寿命[ms]
     */
    public long getTimeToLive();
    
    /**
     * MDBへの再送メッセージを無視するかどうかを設定する。<p>
     * デフォルトは、false。
     *
     * @param ignore 無視する場合true
     */
    public void setIgnoreRedelivery(boolean ignore);
    
    /**
     * MDBへの再送メッセージを無視するかどうかを判定する。<p>
     *
     * @return trueの場合、無視する
     */
    public boolean isIgnoreRedelivery();
    
    /**
     * 再送メッセージを実行する前に一定時間待機する待機時間を設定する。<p>
     * デフォルトは、0で待機しない。<br>
     *
     * @param millis 待機時間[ms]
     */
    public void setRedeliveryInterval(long millis);
    
    /**
     * 再送メッセージを実行する前に一定時間待機する待機時間を取得する。<p>
     *
     * @return 待機時間[ms]
     */
    public long getRedeliveryInterval();
}
