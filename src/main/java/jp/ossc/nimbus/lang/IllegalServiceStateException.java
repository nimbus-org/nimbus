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
package jp.ossc.nimbus.lang;

import jp.ossc.nimbus.core.*;

/**
 * サービスの状態が不正な事を示す例外。<p>
 * サービスの状態が不正で、サービスが提供できない場合にthrowされます。<br>
 *
 * @author M.Takata
 */
public class IllegalServiceStateException extends RuntimeException{
    
    private static final long serialVersionUID = 1340027189376383082L;
    
    /**
     * マネージャ名。<p>
     */
    private final String managerName;
    
    /**
     * サービス名。<p>
     */
    private final String serviceName;
    
    /**
     * サービスの状態。<p>
     */
    private final int serviceState;
    
    /**
     * 例外オブジェクトを生成する。<p>
     *
     * @param service Serviceオブジェクト
     */
    public IllegalServiceStateException(Service service){
        this(
            service != null? service.getServiceManagerName() : null,
            service != null? service.getServiceName() : null,
            service != null? service.getState() : Service.UNKNOWN
        );
    }
    
    /**
     * 例外オブジェクトを生成する。<p>
     *
     * @param name ServiceNameオブジェクト
     * @param state サービスの状態
     */
    public IllegalServiceStateException(ServiceName name, int state){
        this(
            name != null? name.getServiceManagerName() : null,
            name != null? name.getServiceName() : null,
            state
        );
    }
    
    /**
     * 例外オブジェクトを生成する。<p>
     *
     * @param manager マネージャ名
     * @param service サービス名
     * @param state サービスの状態
     */
    public IllegalServiceStateException(
        String manager,
        String service,
        int state
    ){
        managerName = manager;
        serviceName = service;
        serviceState = state;
    }
    
    /**
     * サービスの状態が不正なサービスが登録されているマネージャ名を取得する。<p>
     *
     * @return マネージャ名
     */
    public String getServiceManagerName(){
        return managerName;
    }
    
    /**
     * サービスの状態が不正なサービスのサービス名を取得する。<p>
     *
     * @return サービス名
     */
    public String getServiceName(){
        return serviceName;
    }
    
    /**
     * サービスの状態が不正なサービスの状態を取得する。<p>
     *
     * @return サービスの状態
     */
    public int getServiceState(){
        return serviceState;
    }
    
    /**
     * メッセージを取得する。<p>
     * 
     * @return メッセージ
     */
    public String getMessage(){
        final StringBuilder buf = new StringBuilder();
        buf.append(managerName).append('#').append(serviceName)
            .append(',').append(Service.STATES[serviceState]);
        return buf.toString();
    }
}