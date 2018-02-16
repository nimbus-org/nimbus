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
package jp.ossc.nimbus.service.proxy;

import javax.ejb.*;

import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.aop.*;

/**
 * リモート呼び出しサーバEJBObjectインタフェース。<p>
 *
 * @author M.Takata
 */
public interface RemoteServiceServerEJBObject
 extends EJBObject, RemoteServerInvoker{
    
    /**
     * リモートサーバ側のプロキシ処理を行う。<p>
     * 
     * @param context 呼び出しコンテキスト
     * @return サービスの呼び出し結果
     * @exception java.rmi.RemoteException プロキシ処理に失敗した場合
     * @exception Exception プロキシ処理に失敗した場合
     */
    public Object invoke(InvocationContext context)
     throws Exception, java.rmi.RemoteException;
    
    /**
     * 指定されたサービスが生存しているかどうかを判定する。<p>
     * 
     * @return 生存している場合true
     * @exception java.rmi.RemoteException プロキシ処理に失敗した場合
     */
    public boolean isAlive(ServiceName name) throws Exception, java.rmi.RemoteException;
}