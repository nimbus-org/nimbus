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
package jp.ossc.nimbus.service.queue;

/**
 * QueueHandlerインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface QueueHandler{
    
    /**
     * {@link jp.ossc.nimbus.service.queue.Queue Queue}から取り出したオブジェクトの処理を行う。<p>
     *
     * @param obj {@link jp.ossc.nimbus.service.queue.Queue Queue}から取り出したオブジェクト
     * @exception Throwable
     */
    public void handleDequeuedObject(Object obj) throws Throwable;
    
    /**
     * {@link jp.ossc.nimbus.service.queue.Queue Queue}から取り出したオブジェクトの処理で例外が発生した場合に呼び出される。<p>
     *
     * @param obj {@link jp.ossc.nimbus.service.queue.Queue Queue}から取り出したオブジェクト
     * @param th {@link jp.ossc.nimbus.service.queue.Queue Queue}から取り出したオブジェクトの処理で発生した例外
     * @return リトライを中断する場合は、false
     * @exception Throwable
     */
    public boolean handleError(Object obj, Throwable th) throws Throwable;
    
    /**
     * {@link jp.ossc.nimbus.service.queue.Queue Queue}から取り出したオブジェクトの処理で例外が発生し、最大リトライ回数に越えた場合に呼び出される。<p>
     *
     * @param obj {@link jp.ossc.nimbus.service.queue.Queue Queue}から取り出したオブジェクト
     * @param th {@link jp.ossc.nimbus.service.queue.Queue Queue}から取り出したオブジェクトの処理で発生した例外
     * @exception Throwable
     */
    public void handleRetryOver(Object obj, Throwable th) throws Throwable;
}
