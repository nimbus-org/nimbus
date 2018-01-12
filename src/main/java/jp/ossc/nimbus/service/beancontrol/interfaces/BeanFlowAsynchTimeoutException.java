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
package jp.ossc.nimbus.service.beancontrol.interfaces;

/**
 * 非同期呼び出しした業務フローの応答待ちがタイムアウトした時にthrowされる例外。<p>
 * 
 * @author M.Takata
 */
public class BeanFlowAsynchTimeoutException extends RuntimeException{
    
    private static final long serialVersionUID = 2937194901541549132L;
    private String flowName;
    
    /**
     * 空の例外インスタンスを生成する。<p>
     */
    public BeanFlowAsynchTimeoutException(){
        super();
    }
    
    /**
     * 指定されたフロー名を持った例外インスタンスを生成する。<p>
     *
     * @param flowName フロー名
     */
    public BeanFlowAsynchTimeoutException(String flowName){
        super();
        setFlowName(flowName);
    }
    
    /**
     * 指定されたフロー名とメッセージを持った例外インスタンスを生成する。<p>
     *
     * @param flowName フロー名
     * @param message メッセージ
     */
    public BeanFlowAsynchTimeoutException(String flowName, String message){
        super(message);
        setFlowName(flowName);
    }
    
    public void setFlowName(String name){
        flowName = name;
    }
    
    public String getFlowName(){
        return flowName;
    }
    
    public String getMessage(){
        final StringBuffer buf = new StringBuffer();
        if(super.getMessage() != null){
            buf.append(super.getMessage()).append(':');
        }
        buf.append("flowName=").append(flowName);
        return buf.toString();
    }
}
