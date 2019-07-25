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
package jp.ossc.nimbus.service.keepalive;

import java.util.List;

/**
 * クラスタリスナ。<p>
 * {@link Cluster}によって構成されたクラスタメンバーの状態変化の通知を受け取る。<br>
 *
 * @author M.Takata
 */
public interface ClusterListener{
    
    /**
     * クラスタメンバの初期化時に呼び出される。<p>
     *
     * @param myId 自身のID。クライアントの場合は、null
     * @param members クラスタメンバ
     */
    public void memberInit(Object myId, List members);
    
    /**
     * クラスタメンバに変更があった場合に呼び出される。<p>
     *
     * @param oldMembers 変更前のクラスタメンバ
     * @param newMembers 変更後のクラスタメンバ
     */
    public void memberChange(List oldMembers, List newMembers);
    
    /**
     * クラスタメンバの主系メンバになる時に呼び出される。<p>
     */
    public void changeMain() throws Exception;
    
    /**
     * クラスタメンバの副系メンバになる時に呼び出される。<p>
     */
    public void changeSub();
}