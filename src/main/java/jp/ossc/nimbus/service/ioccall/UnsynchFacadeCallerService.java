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
package jp.ossc.nimbus.service.ioccall;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.ioc.FacadeValue;
import jp.ossc.nimbus.service.queue.Queue;
import jp.ossc.nimbus.service.queue.QueueHandler;

/**
 * {@link FacadeCaller}サービスの非同期呼び出しを{@link Queue}サービスを使って行う場合の{@link QueueHandler}サービス。<p>
 * 
 * @author M.Takata
 */
public class UnsynchFacadeCallerService extends ServiceBase
 implements QueueHandler, UnsynchFacadeCallerServiceMBean{
    
    private static final long serialVersionUID = 2814309373911051419L;
    
    /**
     * {@link Queue}から取り出した{@link UnsyncRequest}の処理を行う。<p>
     *
     * @param obj Queueから取り出したUnsyncRequest
     * @exception Throwable IOC Facade EJB呼び出しに失敗した場合
     */
    public void handleDequeuedObject(Object obj) throws Throwable{
        if(obj == null){
            return;
        }
        final UnsyncRequest request = (UnsyncRequest)obj;
        final FacadeValue facadeValue = request.getFacadeValue();
        Object result = null;
        try{
            result = request.getFacadeCaller().syncFacadeCall(facadeValue);
        }catch(RuntimeException e){
            result = e;
        }
        final Queue replyQueue = request.getReplyQueue();
        if(replyQueue != null){
            replyQueue.push(result);
        }
    }
    public boolean handleError(Object obj, Throwable th) throws Throwable{
        throw th;
    }
    public void handleRetryOver(Object obj, Throwable th) throws Throwable{
        throw th;
    }
}