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

/**
 * セマフォインターフェイス。<p>
 * 
 * @author H.Nakano
 */
public interface Semaphore{
    
    /**
     * セマフォのリソース獲得待ちをする。<p>
     * 
     * @param timeOutMiliSecond 最大待ち時間[ms]
     * @param maxWaitCount 最大同時獲得待ち数
     * @param forceFreeMiliSecond 強制開放時間[ms]
     * @return 獲得成功の場合、true
     */
    public boolean getResource(
        long timeOutMiliSecond,
        int maxWaitCount,
        long forceFreeMiliSecond
    );
    
    /**
     * セマフォのリソース獲得待ちをする。<p>
     * 
     * @param timeOutMiliSecond 最大待ち時間[ms]
     * @param maxWaitCount 最大同時獲得待ち数
     * @return 獲得成功の場合、true
     */
    public boolean getResource(long timeOutMiliSecond, int maxWaitCount);
    
    /**
     * セマフォのリソース獲得待ちをする。<p>
     * 
     * @param timeOutMiliSecond 最大待ち時間[ms]
     * @return 獲得成功の場合、true
     */
    public boolean getResource(long timeOutMiliSecond);
    
    /**
     * セマフォのリソース獲得待ちをする。<p>
     * 
     * @param maxWaitCount 最大同時獲得待ち数
     * @return 獲得成功の場合、true
     */
    public boolean getResource(int maxWaitCount);
    
    /**
     * セマフォのリソース獲得無限待ちをする。<p>
     * 
     * @return 獲得成功の場合、true
     */
    public boolean getResource();
    
    /**
     * 獲得したセマフォのリソースを開放する。<p>
     */
    public void freeResource();
    
    /**
     * セマフォのリソース総数を取得する。<p>
     *
     * @return セマフォのリソース総数
     */
    public int getResourceCapacity();
    
    /**
     * セマフォのリソース総数を設定する。<p>
     *
     * @param capa セマフォのリソース総数
     */
    public void setResourceCapacity(int capa);
    
    /**
     * セマフォに対して無限獲得待ちをするスレッドがsleepする時間を設定する。<p>
     * 自分がセマフォ待ちの先頭でない場合は、再びsleepする。<br>
     *
     * @param millis セマフォに対して無限獲得待ちをするスレッドがsleepする時間[ms]
     */
    public void setSleepTime(long millis);
    
    /**
     * セマフォに対して無限獲得待ちをするスレッドがsleepする時間を取得する。<p>
     *
     * @return セマフォに対して無限獲得待ちをするスレッドがsleepする時間[ms]
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
     * セマフォの残りリソース数を取得する。<p>
     * 
     * @return セマフォの残りリソース数
     */
    public int getResourceRemain();
    
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
     * セマフォ獲得待ちをしているスレッド数を取得する。<p>
     * 
     * @return セマフォ獲得待ちをしているスレッド数
     */
    public int getWaitingCount();
    
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
