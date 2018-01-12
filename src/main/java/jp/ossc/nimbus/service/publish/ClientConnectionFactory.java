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
package jp.ossc.nimbus.service.publish;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * メッセージ受信用のクライアントコネクション生成インタフェース。<p>
 * メッセージ受信を行う{@link ClientConnection}を生成するファクトリインタフェース。<br>
 * 
 * @author M.Takata
 */
public interface ClientConnectionFactory extends Remote{
    
    /**
     * デフォルトの{@link ClientConnectionFactory}リモートオブジェクトのJNDI名。<br>
     */
    public static final String DEFAULT_JNDI_NAME = "publish/ClientConnectionFactory";
    
    /**
     * クライアントコネクションを取得する。<br>
     * 
     * @return クライアントコネクション
     * @exception ConnectionCreateException クライアントコネクションの生成に失敗した場合
     * @exception RemoteException リモート呼び出しに失敗した場合
     */
    public ClientConnection getClientConnection() throws ConnectionCreateException, RemoteException;
    
    /**
     * 現在接続しているクライアント数を取得する。<p>
     *
     * @return クライアント数
     * @exception RemoteException リモート呼び出しに失敗した場合
     */
    public int getClientCount() throws RemoteException;
}