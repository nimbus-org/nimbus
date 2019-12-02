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
package jp.ossc.nimbus.service.writer.mail;

import javax.mail.*;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceBaseSupport;

/**
 * パスワード認証を行うjavax.mail.Authenticatorの実装サービス。<p>
 * 
 * @author M.Takata
 */
public class PasswordAuthenticatorService extends Authenticator implements ServiceBaseSupport, PasswordAuthenticatorServiceMBean{
    
    protected String userName;
    protected String password;
    protected PasswordAuthentication passwordAuthentication;
    
    public void setUserName(String name){
        userName = name;
    }
    public String getUserName(){
        return userName;
    }
    
    public void setPassword(String password){
        this.password = password;
    }
    public String getPassword(){
        return password;
    }
    
    public void setServiceBase(ServiceBase service){
    }
    
    public void createService() throws Exception{
    }
    
    public void startService() throws Exception{
        if(userName == null){
            throw new IllegalArgumentException("userName is null.");
        }
        if(password == null){
            throw new IllegalArgumentException("password is null.");
        }
        passwordAuthentication = new PasswordAuthentication(userName, password);
    }
    
    public void stopService() throws Exception{
    }
    
    public void destroyService() throws Exception{
    }
    
    protected PasswordAuthentication getPasswordAuthentication(){
        return passwordAuthentication;
    }
}