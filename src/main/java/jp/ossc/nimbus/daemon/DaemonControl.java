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
package jp.ossc.nimbus.daemon;

/**
 * デーモンスレッドの実行を制御するコントローラ。<p>
 * 
 * @author H.Nakano
 */
public interface DaemonControl{
    
    /**
     * 稼動状態を判定する。<p>
     * 
     * @return trueの場合、稼動中
     */
    public boolean isRunning();
    
    /**
     * 稼動状態を設定する。<p>
     * 
     * @param runFlg 稼動中に設定したい場合true
     */
    public void setRunning(boolean runFlg);
    
    /**
     * ブロッキング状態を判定する。<p>
     * 
     * @return trueの場合、ブロック中
     */
    public boolean isBlocking();
    
    /**
     * ブロッキング状態を設定する。<p>
     * 
     * @param blockFlg ブロック中に設定したい場合true
     */
    public void setBlocking(boolean blockFlg);
    
    /**
     * スレッドを一時停止する。<p>
     */
    public void suspend();
    
    /**
     * スレッドを再開する。<p>
     */
    public void resume();
    
    /**
     * {@link DaemonRunnable#consume(Object,DaemonControl)}が前回呼び出された時刻から、指定された時間だけsleepする。<p>
     *
     * @param interval スリープする時間[ms]
     * @param isFirstSleep 初回にsleepするかどうかを示すフラグ。trueの場合は、sleepする
     * @exception InterruptedException インターラプトされた場合
     */
    public void sleep(long interval, boolean isFirstSleep) throws InterruptedException;
}
