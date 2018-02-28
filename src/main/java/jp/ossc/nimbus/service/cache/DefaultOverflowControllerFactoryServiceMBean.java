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
package jp.ossc.nimbus.service.cache;

import jp.ossc.nimbus.core.FactoryServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link DefaultOverflowControllerFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see DefaultOverflowControllerFactoryService
 */
public interface DefaultOverflowControllerFactoryServiceMBean
 extends FactoryServiceBaseMBean{
    
    /**
     * あふれ検証を行うOverflowValidatorサービスのサービス名を設定する。<p>
     * 設定しない場合は、あふれ制御が行われない。<br>
     *
     * @param name サービス名
     */
    public void setOverflowValidatorServiceName(ServiceName name);
    
    /**
     * あふれ検証を行うOverflowValidatorサービスのサービス名を取得する。<p>
     *
     * @return サービス名
     */
    public ServiceName getOverflowValidatorServiceName();
    
    /**
     * あふれ検証結果に従ってあふれるキャッシュオブジェクトを決定するOverflowAlgorithmサービスのサービス名を設定する。<p>
     * {@link #setOverflowValidatorServiceName(ServiceName)}であふれ検証サービスが設定されている場合は、この属性も必ず設定しなければならない。<br>
     *
     * @param name サービス名
     */
    public void setOverflowAlgorithmServiceName(ServiceName name);
    
    /**
     * あふれ検証結果に従ってあふれるキャッシュオブジェクトを決定するOverflowAlgorithmサービスのサービス名を取得する。<p>
     *
     * @return サービス名
     */
    public ServiceName getOverflowAlgorithmServiceName();
    
    /**
     * あふれアルゴリズムによって決定されたあふれキャッシュオブジェクトをあふれさせるOverflowActionサービスのサービス名を設定する。<p>
     * 設定しない場合には、{@link RemoveOverflowActionService}が使用される。<br>
     *
     * @param name サービス名
     */
    public void setOverflowActionServiceName(ServiceName name);
    
    /**
     * あふれアルゴリズムによって決定されたあふれキャッシュオブジェクトをあふれさせるOverflowActionサービスのサービス名を取得する。<p>
     *
     * @return サービス名
     */
    public ServiceName getOverflowActionServiceName();
    
    /**
     * あふれ制御の要求を別スレッドで処理するために、一旦キューに溜めるためのQueueサービスのサービス名を設定する。<p>
     * 設定しない場合には、{@link jp.ossc.nimbus.service.queue.DefaultQueueService DefaultQueueService}が使用される。<br>
     *
     * @param name サービス名
     */
    public void setQueueServiceName(ServiceName name);
    
    /**
     * あふれ制御の要求を別スレッドで処理するために、一旦キューに溜めるためのQueueサービスのサービス名を取得する。<p>
     *
     * @return サービス名
     */
    public ServiceName getQueueServiceName();
    
    /**
     * 定期的にあふれ制御を行う時間間隔[ms]を設定する。<p>
     * デフォルトは0で、定期的なあふれ制御は行わない。<br>
     *
     * @param time 定期的にあふれ制御を行う時間間隔[ms]
     */
    public void setPeriodicOverflowIntervalTime(long time);
    
    /**
     * 定期的にあふれ制御を行う時間間隔[ms]を取得する。<p>
     *
     * @return 定期的にあふれ制御を行う時間間隔[ms]
     */
    public long getPeriodicOverflowIntervalTime();
    
    /**
     * あふれ制御を行うために保持している情報を初期化する。<p>
     */
    public void reset();
}
