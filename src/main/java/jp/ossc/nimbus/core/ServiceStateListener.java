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
package jp.ossc.nimbus.core;

import java.util.*;

/**
 * サービスの状態を監視するリスナインタフェース。<p>
 * {@link ServiceStateBroadcaster}に、このリスナを登録する事で、ServiceStateBroadcasterで管理されるサービスの状態を監視する事ができる。<br>
 * 監視できる状態は、{@link Service}インタフェースに定義された各状態で、{@link #stateChanged(ServiceStateChangeEvent)}で通知される。<br>
 * また、通知して欲しい状態を{@link #isEnabledState(int)}で指定する事もできる。<br>
 *
 * @author M.Takata
 * @see ServiceStateBroadcaster
 * @see ServiceStateChangeEvent
 */
public interface ServiceStateListener extends EventListener{
    
    /**
     * サービスの状態が変更された時に呼び出される。<p>
     *
     * @param e サービス状態変更イベント
     */
    public void stateChanged(ServiceStateChangeEvent e) throws Exception;
    
    /**
     * 通知されるべきサービスの状態変更を調べる。<p>
     *
     * @param state サービスの状態
     */
    public boolean isEnabledState(int state);
}