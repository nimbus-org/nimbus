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
package jp.ossc.nimbus.service.mail;

import jp.ossc.nimbus.core.*;
//
/**
 * メール送信管理インターフェイス
 * @author   NRI. Hirotaka Nakano
 * @version  1.00 作成: 2003/10/09 -　H.Nakano
 */
public interface SimpleMailSenderFactoryServiceMBean extends ServiceBaseMBean {
	/**
	 * setReturnPath
	 * @param returnPath
	 */
	public void setReturnPath(String returnPath);	
	/**
	 * getReturnPath
	 * @return 
	 */
	public String getReturnPath();	
	/**
	 * setReplyTo
	 * @param replyTo
	 */
	public void setReplyTo(String replyTo) ;
	/**
	 * getReplyTo
	 * @return 
	 */
	public String getReplyTo() ;
	/**
	 * setMailer
	 * @param mailer
	 */
	public void setMailer(String mailer) ;
	/**
	 * getMailer
	 * @return 
	 */
	public String getMailer() ;
	/**
	 * setContentType
	 * @param contentType
	 */
	public void setContentType(String contentType);		
	/**
	 * getContentType
	 * @return 
	 */
	public String getContentType();		
	/**
	 * setTransferEncoding
	 * @param transferEncode
	 */
	public	void setTransferEncoding(String transferEncode) ;
	/**
	 * getTransferEncoding
	 * @return 
	 */
	public	String getTransferEncoding() ;
	/**
	 * setMimeVersion
	 * @param mimeVersion
	 */
	public void setMimeVersion(String mimeVersion) ;
	/**
	 * getMimeVersion
	 * @return 
	 */
	public String getMimeVersion() ;
	/**
	 * setNotSending
	 * @param sendToAdr
	 */
	public void setNotSending(String sendToAdr) ;
	/**
	 * unsetNotSending
	 * 
	 */
	public void unsetNotSending() ;
	/**
	 * isNotSending
	 * @return 
	 */
	public boolean isNotSendingStatus() ;
	/**
	 * setRetryMax
	 * @param retryCnt
	 */
	public void setRetryMax(int retryCnt) ;
	/**
	 * getRetryMax
	 * @return 
	 */
	public int getRetryMax() ;
	/**
	 * setRetryIntervalMiliSeconds
	 * @param milisecs
	 */
	public void setRetryIntervalMiliSeconds(long milisecs) ;
	/**
	 * getRetryIntervalMiliSeconds
	 * @return 
	 */
	public long getRetryIntervalMiliSeconds() ;
	/**
	 * setUsingCheckerServiceNames
	 * @param serviceNames
	 */
	public void setUsingCheckerServiceNames(ServiceName serviceNames[]) ;
	/**
	 * getUsingCheckerServiceNames
	 * @return 
	 */
	public ServiceName[] getUsingCheckerServiceNames() ;
	/**
	 * setUseRoudRobin
	 * @param isRoundrobin
	 */
	public void setUseRoudRobin(boolean isRoundrobin) ; 
	/**
	 * isUseRoudRobin
	 * @return 
	 */
	public boolean isUseRoudRobin() ; 
	/**
	 * setKeepAliveServiceName
	 * @param name
	 */
	public void setKeepAliveServiceName(ServiceName name) ;
	/**
	 * setConnectionTimeOut
	 * @param timeoutMiliSeconds
	 */
	public void setConnectionTimeOut(long timeoutMiliSeconds) ;
	/**
	 * setSendTimeOut
	 * @param timeoutMiliSeconds
	 */
	public void setSendTimeOut(long timeoutMiliSeconds) ;
	/**
	 * setMailSenderClassName
	 * @param clsName
	 */
	public void setMailSenderClassName(String clsName) ;
	/**
	 * setConvertString
	 * @param cnvStr xx,vv;vv,ee;
	 */
	public void setConvertString(String cnvStr) ;
	/**
	 * setLoggerServiceName
	 * @param name
	 */
	public void setLoggerServiceName(ServiceName name) ;
}
