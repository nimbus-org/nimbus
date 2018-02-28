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

/**
 * サービスがみつからない事を示す例外。<p>
 * サービスが見つからない場合にthrowされます。<br>
 *
 * @author M.Takata
 */
public class ServiceNotFoundException extends RuntimeException{
    
    private static final long serialVersionUID = 6536031104339439001L;
    
    /**
     * マネージャ名。<p>
     */
    private final String managerName;
    
    /**
     * サービス名。<p>
     */
    private final String serviceName;
    
    /**
     * 見つからなかったサービス名を持った例外オブジェクトを生成する。<p>
     *
     * @param name ServiceNameオブジェクト
     */
    public ServiceNotFoundException(ServiceName name){
        this(
            name != null? name.getServiceManagerName() : null,
            name != null? name.getServiceName() : null
        );
    }
    
    /**
     * 見つからなかったサービス名を持った例外オブジェクトを生成する。<p>
     *
     * @param manager マネージャ名
     * @param service サービス名
     */
    public ServiceNotFoundException(String manager, String service){
        managerName = manager;
        serviceName = service;
    }
    
    /**
     * 見つからなかったサービスが登録されていると思われていたマネージャ名を取得する。<p>
     *
     * @return マネージャ名
     */
    public String getServiceManagerName(){
        return managerName;
    }
    
    /**
     * 見つからなかったサービス名を取得する。<p>
     *
     * @return サービス名
     */
    public String getServiceName(){
        return serviceName;
    }
    
    /**
     * メッセージを取得する。<p>
     * メッセージが、コンストラクタで明示的に指定されない場合は、見つからなかったサービス名を示すメッセージを返す。<br>
     * 
     * @return メッセージ
     */
    public String getMessage(){
        return managerName + '#' + serviceName;
    }
}