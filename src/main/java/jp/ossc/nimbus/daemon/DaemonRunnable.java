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
 * デーモンスレッドで実行させる処理を実装するためのインタフェース。<p>
 *
 * @author H.Nakano
 * @see Daemon
 */
public interface DaemonRunnable{
    
    /**
     * デーモンスレッドが開始しようとする時に実行する処理を行う。<p>
     *
     * @return trueの場合、デーモンスレッドが開始する。falseの場合、デーモンスレッドは開始しない。
     */
    public boolean onStart() ;
    
    /**
     * デーモンスレッドが停止しようとする時に実行する処理を行う。<p>
     *
     * @return trueの場合、デーモンスレッドが停止する。falseの場合、デーモンスレッドは停止しない。
     */
    public boolean onStop();
    
    /**
     * デーモンスレッドが一時停止しようとする時に実行する処理を行う。<p>
     *
     * @return trueの場合、デーモンスレッドが一時停止する。falseの場合、デーモンスレッドは一時停止しない。
     */
    public boolean onSuspend();
    
    /**
     * デーモンスレッドが再開しようとする時に実行する処理を行う。<p>
     *
     * @return trueの場合、デーモンスレッドが再開する。falseの場合、デーモンスレッドは再開しない。
     */
    public boolean onResume();
    
    /**
     * デーモンスレッドで処理する任意のオブジェクトを供給する。<p>
     * 
     * @param ctrl デーモンスレッドの実行を制御するDaemonControl
     * @return 任意のオブジェクト
     * @exception Throwable オブジェクトの供給において問題が発生した場合
     */
    public Object provide(DaemonControl ctrl) throws Throwable;
    
    /**
     * {@link #provide(DaemonControl)}によって供給された任意のオブジェクトを消費する。<p>
     * 
     * @param paramObj 供給された任意のオブジェクト
     * @param ctrl デーモンスレッドの実行を制御するDaemonControl
     * @exception Throwable オブジェクトの消費において問題が発生した場合
     */
    public void consume(Object paramObj, DaemonControl ctrl) throws Throwable;
    
    /**
     * デーモンスレッドが停止する時にガベージ処理を行う。<p>
     */
    public void garbage();
}
