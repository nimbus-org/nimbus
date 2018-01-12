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
package jp.ossc.nimbus.service.semaphore;

import jp.ossc.nimbus.core.ServiceBaseMBean;

/**
 * {@link DefaultSemaphoreService}のMBeanインターフェイス。<p>
 * 
 * @author H.Nakano
 */
public interface DefaultSemaphoreServiceMBean extends ServiceBaseMBean{
    
    /**
     * セマフォ実装クラス名を設定する。<p>
     * デフォルトは、{@link MemorySemaphore}。
     *
     * @param name セマフォ実装クラス名
     */
    public void setSemaphoreClassName(String name);
    
    /**
     * セマフォ実装クラス名を取得する。<p>
     *
     * @return セマフォ実装クラス名
     */
    public String getSemaphoreClassName();
    
    /**
     * セマフォのリソース総数を返す。<p>
     *
     * @return リソース総数
     */
    public int getResourceCapacity() ;
    
    /**
     * セマフォのリソース総数を設定する。<p>
     *
     * @param capa リソース総数
     */
    public void setResourceCapacity(int capa) ;
    
    /**
     * セマフォに対して無限取得待ちをするスレッドがsleepする時間を設定する。<p>
     * 自分がセマフォ待ちの先頭でない場合は、再びsleepする。<br>
     *
     * @param millis セマフォに対して無限取得待ちをするスレッドがsleepする時間[ms]
     */
    public void setSleepTime(long millis);
    
    /**
     * セマフォに対して無限取得待ちをするスレッドがsleepする時間を取得する。<p>
     *
     * @return セマフォに対して無限取得待ちをするスレッドがsleepする時間[ms]
     */
    public long getSleepTime();
    
    /**
     * リソースが空いているか定期的にチェックする時間間隔[ms]を設定する。<p>
     * リソースが空いていて待っているスレッドがいる場合は、そのスレッドを起こす。<br>
     * デフォルトは、チェックしない。
     *
     * @param millis リソースが空いているか定期的にチェックする時間間隔[ms]
     */
    public void setCheckInterval(long millis);
    
    /**
     * リソースが空いているか定期的にチェックする時間間隔[ms]を取得する。<p>
     *
     * @return リソースが空いているか定期的にチェックする時間間隔[ms]
     */
    public long getCheckInterval();
    
    /**
     * セマフォ獲得までの最大待ち時間[ms]を取得する。<p>
     *
     * @return 最大待ち時間[ms]
     */
    public long getTimeoutMillis();
    
    /**
     * セマフォ獲得までの最大待ち時間[ms]を設定する。<p>
     *
     * @param timeout 最大待ち時間[ms]
     */
    public void setTimeoutMillis(long timeout);
    
    /**
     * セマフォ獲得待ちの最大数を取得する。<p>
     *
     * @return 最大同時獲得待ち数
     */
    public int getMaxWaitCount();
    
    /**
     * セマフォ獲得待ちの最大数を設定する。<p>
     *
     * @param count 最大同時獲得待ち数
     */
    public void setMaxWaitCount(int count);
    
    /**
     * セマフォ獲得後の強制セマフォ開放時間[ms]を取得する。<p>
     *
     * @return 強制セマフォ開放時間
     */
    public long getForceFreeTimeoutMillis();
    
    /**
     * セマフォ獲得後の強制セマフォ開放時間[ms]を設定する。<p>
     *
     * @param timeout 強制セマフォ開放時間
     */
    public void setForceFreeTimeoutMillis(long timeout);
    
    /**
     * リーソースの取得と解放のスレッドを関連付けるかどうかを設定する。<p>
     * デフォルトはtrueで、リソースを取得したスレッドからの解放しか受け付けない。<br>
     * また、falseにした場合は、強制解放タイムアウトは無効となる。<br>
     *
     * @param isBinding リソースを取得したスレッドからの解放しか受け付けないようにする場合は、true
     */
    public void setThreadBinding(boolean isBinding);
    
    /**
     * リーソースの取得と解放のスレッドを関連付けるかどうかを判定する。<p>
     *
     * @return trueの場合は、リソースを取得したスレッドからの解放しか受け付けない
     */
    public boolean isThreadBinding();
    
    /**
     * セマフォの残りリソース数を返す。<p>
     *
     * @return リソース数
     */
    public int getResourceRemain();
    
    /**
     * セマフォ取得待ちをしている数を取得する。<p>
     * 
     * @return セマフォ取得待ちをしている数
     */
    public int getWaitingCount();
    
    /**
     * セマフォ獲得待ちスレッドを開放し、セマフォ獲得待ちを受け付けないようにする。<p>
     */
    public void release();
    
    /**
     * セマフォ獲得待ちの受付を開始する。<p>
     * {@link #release()}呼出し後に、セマフォ獲得待ちを受け付けるようにする。
     */
    public void accept();
    
    /**
     * セマフォの最大使用実績を取得する。<p>
     *
     * @return 最大使用実績
     */
    public int getMaxUsedResource();
    
    /**
     * セマフォの最大待ち数実績を取得する。<p>
     *
     * @return 最大待ち数実績
     */
    public int getMaxWaitedCount();
}
