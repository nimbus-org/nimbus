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
package jp.ossc.nimbus.service.sftp.jsch;

import jp.ossc.nimbus.core.ServiceName;

/**
 * ファイルが見つからない場合のSFTP例外。<p>
 *
 * @author M.Takata
 */
public class NoSuchFileSFTPException extends jp.ossc.nimbus.service.sftp.SFTPException {
    
    private static final long serialVersionUID = 3597685707618600307L;
    
    /**
     * コンストラクタ
     * 
     * @param name 例外が発生したサービス名
     */
    public NoSuchFileSFTPException(ServiceName name) {
        super(name);
    }
    
    /**
     * コンストラクタ
     * 
     * @param name 例外が発生したサービス名
     * @param message メッセージ
     */
    public NoSuchFileSFTPException(ServiceName name, String message) {
        super(name, message);
    }
    
    /**
     * コンストラクタ
     * 
     * @param name 例外が発生したサービス名
     * @param message メッセージ
     * @param cause 原因
     */
    public NoSuchFileSFTPException(ServiceName name, String message, Throwable cause) {
        super(name, message, cause);
    }
    
    /**
     * コンストラクタ
     * 
     * @param name 例外が発生したサービス名
     * @param cause 原因
     */
    public NoSuchFileSFTPException(ServiceName name, Throwable cause) {
        super(name, cause);
    }
    
}
