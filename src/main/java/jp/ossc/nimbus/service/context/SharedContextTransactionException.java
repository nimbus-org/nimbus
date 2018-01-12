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
 *      this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *      this list of conditions and the following disclaimer in the documentation
 *      and/or other materials provided with the distribution.
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
package jp.ossc.nimbus.service.context;

public class SharedContextTransactionException extends SharedContextException{
    
    private static final long serialVersionUID = 3114683838194045542L;
    
    /**
     * トランザクション状態：開始。<p>
     */
    private static final String LABEL_STATE_BEGIN      = "BEGIN";
    
    /**
     * トランザクション状態：コミット。<p>
     */
    private static final String LABEL_STATE_COMMIT     = "COMMIT";
    
    /**
     * トランザクション状態：ロールバック。<p>
     */
    private static final String LABEL_STATE_ROLLBACK   = "ROLLBACK";
    
    /**
     * トランザクション状態：コミット完了。<p>
     */
    private static final String LABEL_STATE_COMMITTED  = "COMMITTED";
    
    /**
     * トランザクション状態：ロールバック完了。<p>
     */
    private static final String LABEL_STATE_ROLLBACKED = "ROLLBACKED";
    
    /**
     * トランザクション状態：ロールバック失敗。<p>
     */
    private static final String LABEL_STATE_ROLLBACK_FAILED = "ROLLBACK_FAILED";
    
    /**
     * トランザクション状態：不明。<p>
     */
    private static final String LABEL_STATE_UNKNOWN = "UNKNOWN";
    
    private int transactionState;
    
    public SharedContextTransactionException(int state){
        super("state=" + toStateLabel(state));
        transactionState = state;
    }
    public SharedContextTransactionException(String message, int state){
        super(message + " : state=" + toStateLabel(state));
        transactionState = state;
    }
    public SharedContextTransactionException(String message, Throwable cause, int state){
        super(message + " : state=" + toStateLabel(state), cause);
        transactionState = state;
    }
    public SharedContextTransactionException(Throwable cause, int state){
        super("state=" + toStateLabel(state), cause);
        transactionState = state;
    }
    
    public int getTransactionState(){
        return transactionState;
    }
    
    private static final String toStateLabel(final int state){
        switch(state){
        case SharedContextTransactionManager.SharedContextTransaction.STATE_BEGIN:
            return LABEL_STATE_BEGIN;
        case SharedContextTransactionManager.SharedContextTransaction.STATE_COMMIT:
            return LABEL_STATE_COMMIT;
        case SharedContextTransactionManager.SharedContextTransaction.STATE_ROLLBACK:
            return LABEL_STATE_ROLLBACK;
        case SharedContextTransactionManager.SharedContextTransaction.STATE_COMMITTED:
            return LABEL_STATE_COMMITTED;
        case SharedContextTransactionManager.SharedContextTransaction.STATE_ROLLBACKED:
            return LABEL_STATE_ROLLBACKED;
        case SharedContextTransactionManager.SharedContextTransaction.STATE_ROLLBACK_FAILED:
            return LABEL_STATE_ROLLBACK_FAILED;
        default:
            return LABEL_STATE_UNKNOWN;
        }
    }
}
