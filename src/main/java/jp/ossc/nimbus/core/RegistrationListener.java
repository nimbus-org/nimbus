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
 * 登録状態を監視するリスナインタフェース。<p>
 * {@link RegistrationBroadcaster}に、このリスナを登録する事で、RegistrationBroadcasterで管理される任意のオブジェクトの登録状態を監視する事ができる。<br>
 * 監視できる状態は、登録、削除で、それぞれ、{@link #registered(RegistrationEvent)}、{@link #unregistered(RegistrationEvent)}で通知される。<br>
 *
 * @author M.Takata
 * @see RegistrationBroadcaster
 * @see RegistrationEvent
 */
public interface RegistrationListener extends EventListener{
    
    /**
     * RegistrationBroadcasterで管理される任意のオブジェクトが登録された時に呼び出される。<p>
     *
     * @param e RegistrationEventオブジェクト
     */
    public void registered(RegistrationEvent e);
    
    /**
     * RegistrationBroadcasterで管理される任意のオブジェクトが削除された時に呼び出される。<p>
     *
     * @param e RegistrationEventオブジェクト
     */
    public void unregistered(RegistrationEvent e);
}