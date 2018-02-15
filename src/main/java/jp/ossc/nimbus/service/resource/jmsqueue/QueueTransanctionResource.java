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
package jp.ossc.nimbus.service.resource.jmsqueue;

import javax.jms.*;

import jp.ossc.nimbus.service.resource.*;
import jp.ossc.nimbus.service.semaphore.*;
//
/**
 *    
 *    @author    y-tokuda
 *    @version    1.00 作成：2003/10/24－ y-tokuda<BR>
 *                更新：
 */
public class QueueTransanctionResource implements TransactionResource {
    //メンバ変数
    /** QueueSession */
    private QueueSession mSession = null;
    /** QueueConnection */
    private QueueConnection mQueueConnection = null;
    /** セマフォ */
    private Semaphore mSemaphore;
    
    /**
     * コンストラクタ
     */    
    QueueTransanctionResource(QueueSession session,QueueConnection conn,Semaphore sem){
        mSession = session;
        mQueueConnection = conn;
        mSemaphore = sem;
    }

    /**
     * コミット。JMSExceptionが発生したら、ServiceExceptionをスローする。
     */
    public void commit() throws JMSException  {
        if(mSession.getTransacted()){
            mSession.commit();
        }
    }

    /**
     * ロールバック。JMSExceptionが発生したら、ServiceExceptionをスローする。
     */
    public void rollback() throws JMSException {
        if(mSession.getTransacted()){
            mSession.rollback();
        }
    }

    /**
     * クローズ。JMSExceptionが発生したら、ServiceExceptionをスローする。
     */
    public void close() throws JMSException {
        try{
            mSession.close();
        }finally{
            if(mSemaphore != null){
                mSemaphore.freeResource();
            }
        }
    }

    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.jmsresource.TransanctionObject#getObject()
     */
    public Object getObject() {
        // TODO 自動生成されたメソッド・スタブ
        return mSession;
    }
    /**
     * QueueConnection取得メソッド
     * @return QueueConnection
     */
    public QueueConnection getConnectionObject(){
        return mQueueConnection;
    }

}
