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
import jp.ossc.nimbus.service.keepalive.*;
import jp.ossc.nimbus.service.keepalive.smtp.SmtpKeepAliveChecker;
import jp.ossc.nimbus.service.log.*;
import jp.ossc.nimbus.util.*;
import jp.ossc.nimbus.lang.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
//
/**
 * 
 * @author   NRI. Hirotaka Nakano
 * @version  1.00 作成: 2003/10/09 -　H.Nakano
 */
public class SimpleMailSenderFactoryService
	extends ServiceBase
	implements MailSenderFactory, 
		SimpleMailSenderFactoryServiceMBean,
		SenderFactoryCallBack {
	
    private static final long serialVersionUID = 8050150448734670282L;
    
    //## 属性			##
	// インスタンス変数の宣言
	/** ラウンドロビン		*/	
	protected boolean mIsRoundrobin = false;
	/** 送信サーバリスト		*/	
	protected Set mCheckerNameHash ;
	/** Return-Path			*/	
	protected String mReturnPath = null;
	/** Reply-To			*/	
	protected String mReplyTo = null;
	/** mailer				*/	
	protected String mMailer = null;
	/** Mime-Version		*/	
	protected String mMimeVersion = null;
	/** Transfer-encoding	*/	
	protected String mTransferEncoding = null;
	/** Content-Tyep		*/	
	protected String mContentType = null;
	/** エンコーディング		*/	
	protected String mEncoding = null;
	/** 送信抑止モード		*/	
	protected boolean mNotSendAnywhere = false;
	/** 送信抑止モード		*/	
	protected String mSendingTo = null;
	/** 最大リトライ回数		*/	
	protected volatile int mRetryMax = 10;
	/** リトライ間隔(ミリ秒)	*/	
	protected volatile long mRetryInterval = 500;
	/** コネクションタイムアウト(ミリ秒)	*/	
	protected String mConnectionTimeOut = null;
	/** 送信タイムアウト(ミリ秒)	*/	
	protected String mSendTimeOut = null;
	/** KeepAliveServer名			*/	
	protected ServiceName mKeepAliveServiceName = null; 
	/** メールセンダクラス名			*/	
	protected String mClassName = null; 
	/** 文字列変換リスト　*/
	protected String mConvertString = null ; 
	protected List mConvertStringList = null ;
	protected ServiceName mLoggerName = null; 
	protected Logger mLogger = null; 
	/** キープアライブサービス */
	QueryKeepAlive mKeepAlive = null ;
	static protected final String C_HOST_PROP_KRY = "mail.smtp.host" ; //$NON-NLS-1$
	static protected final String C_CAMMA = "," ; //$NON-NLS-1$
	static protected final String C_SEMICOLON = ";" ; //$NON-NLS-1$
	static protected final String C_CONNECTIONTIMEOUT_PROP_KEY = "mail.smtp.connectiontimeout" ; //$NON-NLS-1$
	static protected final String C_SENDTIMEOUT_PROP_KEY = "mail.smtp.timeout" ; //$NON-NLS-1$
	static protected final String C_HEADER_RETURN_PATH = "Return-Path" ; //$NON-NLS-1$
	static protected final String C_HEADER_XMAILER = "X-Mailer" ; //$NON-NLS-1$
	static protected final String C_HEADER_CHARSET = "; charset=" ; //$NON-NLS-1$
	static protected final String C_HEADER_CONTENT_TYPE = "Content-Type" ; //$NON-NLS-1$
	static protected final String C_HEADER_TRANCEFER_ENCODE = "Content-Transfer-Encoding" ; //$NON-NLS-1$
	static protected final String C_HEADER_MIME_VERSION = "Mime-Version" ; //$NON-NLS-1$
	/**
	 * Constructor for SimpleMailSenderFactoryService.
	 */
	public SimpleMailSenderFactoryService() {
		super();
		mCheckerNameHash = Collections.synchronizedSet(new HashSet());
	}
	/**
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#setReturnPath(String)
	 */
	public void setReturnPath(String returnPath) {
		synchronized(this){
			mReturnPath = returnPath ;
		}
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#setMailSenderClassName(java.lang.String)
	 */
	public void setMailSenderClassName(String clsName) {
		this.mClassName = clsName;
	}

    /**
     * Loggerを設定する。
     */
	public void setLogger(Logger logger) {
        mLogger = logger;
    }
    
    /**
     * QueryKeepAliveを設定する。
     */
    public void setKeepAlive(QueryKeepAlive keepAlive) {
        mKeepAlive = keepAlive;
    }
    
    public void startService() throws ServiceException {
		if(this.mLoggerName != null){
			this.mLogger = (Logger)ServiceManagerFactory.getServiceObject(this.mLoggerName) ;
		}
		mKeepAlive = (QueryKeepAlive)ServiceManagerFactory.getServiceObject(this.mKeepAliveServiceName) ;
		if(mKeepAlive == null ){
			throw new ServiceException("MAILSENDER002","KeepAliveService don't exist name = " + mKeepAliveServiceName) ;   //$NON-NLS-1$//$NON-NLS-2$
		}
		CsvArrayList list = new CsvArrayList()  ;
		if(mConvertString!= null){
			list.split(this.mConvertString,";") ;
			for(int cnt = 0; cnt< list.size();cnt++){
				String tmp = list.getStr(cnt) ;
				CsvArrayList cnv = new CsvArrayList() ;
				cnv.split(tmp,",") ;
				list.set(cnt,cnv) ;
			}
		}
		this.mConvertStringList = list ;
	}
	/**
	 * setConnectionTimeOut
	 * @param timeoutMiliSeconds
	 */
	public void setConnectionTimeOut(long timeoutMiliSeconds){
		this.mConnectionTimeOut = new Long(timeoutMiliSeconds).toString();
	}
	/**
	 * setSendTimeOut
	 * @param timeoutMiliSeconds
	 */
	public void setSendTimeOut(long timeoutMiliSeconds){
		 this.mSendTimeOut = new Long(timeoutMiliSeconds).toString();
	}

	/**
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#getReturnPath()
	 */
	public String getReturnPath() {
		return mReturnPath;
	}

	/**
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#setReplyTo(String)
	 */
	public void setReplyTo(String replyTo) {
		mReplyTo = replyTo ;
	}

	/**
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#getReplyTo()
	 */
	public String getReplyTo() {
		return mReplyTo;
	}

	/**
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#setMailer(String)
	 */
	public void setMailer(String mailer) {
		mMailer = mailer ;
	}

	/**
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#getMailer()
	 */
	public String getMailer() {
		return mMailer;		
	}

	/**
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#setContentType(String)
	 */
	public void setContentType(String contentType) {
		CsvArrayList parser = new CsvArrayList();
		try{
			parser.split(contentType, C_CAMMA);
			mContentType = parser.getStr(0);
			mEncoding = parser.getStr(1);
		}catch(Exception e){
		}
	}

	/**
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#getContentType()
	 */
	public String getContentType() {
		synchronized(this){
			return mContentType + C_CAMMA + mEncoding;
		}
	}

	/**
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#setTransferEncoding(String)
	 */
	public void setTransferEncoding(String transferEncode) {
		mTransferEncoding = transferEncode ;
	}

	/**
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#getTransferEncoding()
	 */
	public String getTransferEncoding() {
		return mTransferEncoding;
	}

	/**
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#setMimeVersion(String)
	 */
	public void setMimeVersion(String mimeVersion) {
		mMimeVersion = mimeVersion ;
	}

	/**
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#getMimeVersion()
	 */
	public String getMimeVersion() {
		return mMimeVersion;
	}

	/**
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#setNotSending(String)
	 */
	public void setNotSending(String sendToAdr) {
		mNotSendAnywhere = true ;
		mSendingTo = sendToAdr ;			
	}

	/**
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#unsetNotSending()
	 */
	public void unsetNotSending() {
		mNotSendAnywhere = false ;
		mSendingTo = null ;			
	}

	/**
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#isNotSendingStatus()
	 */
	public boolean isNotSendingStatus() {
		return mNotSendAnywhere ;
	}

	/**
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#setRetryMax(int)
	 */
	public void setRetryMax(int retryCnt) {
		mRetryMax =retryCnt ;
	}

	/**
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#getRetryMax()
	 */
	public int getRetryMax() {
		return mRetryMax;
	}

	/**
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#setRetryIntervalMiliSeconds(long)
	 */
	public void setRetryIntervalMiliSeconds(long milisecs) {
		mRetryInterval = milisecs ;
	}

	/**
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#getRetryIntervalMiliSeconds()
	 */
	public long getRetryIntervalMiliSeconds() {
		synchronized(mCheckerNameHash){
			return mRetryInterval;
		}
	}

	/**
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#setUsingCheckerServiceNames(ServiceName[])
	 */
	public void setUsingCheckerServiceNames(ServiceName serviceNames[]) {
		synchronized(mCheckerNameHash){
			mCheckerNameHash.clear() ;
			for(int cnt = 0;cnt<serviceNames.length;cnt++){
				mCheckerNameHash.add(serviceNames[cnt]) ;
			}
		}
	}

	/**
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#getUsingCheckerServiceNames()
	 */
	public ServiceName[] getUsingCheckerServiceNames() {
		ServiceName[] ret = null ;
		synchronized(mCheckerNameHash){
			ret = new ServiceName[mCheckerNameHash.size()] ;
			int cnt= 0 ;
			for(Iterator keys = mCheckerNameHash.iterator();keys.hasNext();cnt++){
				ret[cnt] = (ServiceName)keys.next();
			}
		}
		return ret;
	}

	/**
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#setUseRoudRobin(boolean)
	 */
	public void setUseRoudRobin(boolean isRoundrobin) {
		mIsRoundrobin = isRoundrobin ;
	}

	/**
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#isUseRoudRobin()
	 */
	public boolean isUseRoudRobin() {
		return mIsRoundrobin;
	}

	/**
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#setKeepAliveServiceName(ServiceName)
	 */
	public void setKeepAliveServiceName(ServiceName name) {
		mKeepAliveServiceName = name;
	}
	public ServiceName sendSmtp(MailSenderOperator op) throws ServiceException{
		if ((op.getTo() == null) || (op.getFrom() == null) || (op.getBody() == null)) {
			throw new ServiceException("MAILSENDER010","Parameter is invalid"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		long retryCount = 0;
		boolean ret = false ;
		ServiceName sendServer = null ; 
		while(true)	{
			if (mRetryMax < retryCount) {
				break ;
			}
			//サーバリスト取得
			List serverList = null ;
			synchronized(this.mCheckerNameHash){
				if(this.mIsRoundrobin){
					if(mCheckerNameHash.size() == 0){
						serverList = this.mKeepAlive.getRoundrobinAry() ;
					}else{
						serverList = this.mKeepAlive.getRoundrobinAry(mCheckerNameHash) ;
					}
				}else{
					if(mCheckerNameHash.size() == 0){
						serverList = this.mKeepAlive.getPriolityAry() ;
					}else{
						serverList = this.mKeepAlive.getPriolityAry(mCheckerNameHash) ;
					}
				}	
			}
			for (int cnt = 0; cnt < serverList.size(); cnt++) {
				// 送信サーバ情報取得
                SmtpKeepAliveChecker info = null;
                Object value = serverList.get(cnt);
                if (value instanceof ServiceName) {
                    info = (SmtpKeepAliveChecker)ServiceManagerFactory.getServiceObject((ServiceName)value);
                    
                }else if(value instanceof SmtpKeepAliveChecker) {
                    info = (SmtpKeepAliveChecker)value;
                }
				// 送信
				try{
					ret = send(info,op);
				}catch(MessagingException e){
					if(this.mLogger != null){
						this.mLogger.write("MAIL000001",e) ;
					}
					if (mRetryMax <= retryCount && 
						cnt == serverList.size() -1) {
						throw new ServiceException("MAILSENDER202","MailSend Error " ,e); //$NON-NLS-1$ //$NON-NLS-2$
					}else{
						ret = false ;
					}
				}
				if(ret){
					sendServer = (ServiceName)serverList.get(cnt) ; 
					break;
				}else{
					this.mKeepAlive.updateTbl((ServiceName)serverList.get(cnt),false) ;
				}
			}
			if (ret) {
				break;
			}
			else {
				if (mRetryMax != 0) {
					retryCount++ ;
				}
				try {
					Thread.sleep(mRetryInterval);
				} catch (InterruptedException e) {
				}
			}
		}
		if(sendServer == null){
			if(this.mLogger != null){
				this.mLogger.write("MAIL000003") ;
			}
			throw new ServiceException("MAILSENDER203","MailSend Error NotFound"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return sendServer;
	}
	//
	/**
	 *	宛先文字列を１宛先毎の配列にして返す。
	 */
	protected String[] toArrayOfTo(String toStr) {
		ArrayList list = new ArrayList();
		CsvArrayList parser = new CsvArrayList();
		parser.split(toStr, C_SEMICOLON);
		for (int i = 0; i < parser.size(); i++) {
			String to = parser.getStr(i).trim();
			if (to.length() > 0) {
				list.add(to);
			}
		}
		return (String[])list.toArray(new String[0]);
	}
	private String convBody(String input){
		String tmp = input ;
		for(int cnt = 0;cnt<this.mConvertStringList.size();cnt++){
			CsvArrayList cnvList = (CsvArrayList)this.mConvertStringList.get(cnt) ;
			tmp = StringOperator.replaceString(tmp,cnvList.getStr(0),cnvList.getStr(1)) ;
		}
		return tmp ;
	}
	//
	/**
	 *	送信
	 */
	protected boolean send(SmtpKeepAliveChecker info,MailSenderOperator op) throws MessagingException {
		// セッション取得
		String host = info.getHostIp();
		Properties props = new Properties();
		props.put(C_HOST_PROP_KRY, host);
		if(this.mConnectionTimeOut != null ){
			props.put(C_CONNECTIONTIMEOUT_PROP_KEY,this.mConnectionTimeOut);
		}
		if(this.mSendTimeOut != null){
			props.put(C_SENDTIMEOUT_PROP_KEY, this.mSendTimeOut);
		}
		Session session = Session.getInstance(props);
		// メッセージ作成
		MimeMessage msg = new MimeMessage(session);
		try {
			CsvArrayList ps = new CsvArrayList();
			int num = ps.split(op.getFrom(),C_SEMICOLON) ;
			// 送信先・送信元
			if (!mNotSendAnywhere) {		
				if(num<2){
					msg.setFrom(new InternetAddress(op.getFrom()));
				}else{
					msg.setFrom(new InternetAddress(ps.getStr(0),ps.getStr(1),mEncoding));
				}
				String[] array = toArrayOfTo(op.getTo());
				for (int i = 0; i < array.length; i++) {
					msg.addRecipient(Message.RecipientType.TO, new InternetAddress(array[i]));
				}
			}else{
				msg.setFrom(new InternetAddress(this.mSendingTo));
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(mSendingTo));
			}
			// 件名・本文
			msg.setSubject(op.getSubject(),mEncoding);
			String body = convBody(op.getBody());
			msg.setText(body,mEncoding);

			// ヘッダー情報
			Address[] replyTo = new Address[1];
			replyTo[0] = new InternetAddress(mReplyTo);
			msg.setReplyTo(replyTo);
			msg.setHeader(C_HEADER_RETURN_PATH,mReturnPath);
			msg.setHeader(C_HEADER_XMAILER, mMailer);
			String contentType = mContentType + C_HEADER_CHARSET + mEncoding;
			msg.setHeader(C_HEADER_CONTENT_TYPE, contentType);
			msg.setHeader(C_HEADER_TRANCEFER_ENCODE, mTransferEncoding);
			msg.setSentDate(new Date()) ;
			//MimeVersionは任意項目
			if(mMimeVersion != null){
				msg.addHeader(C_HEADER_MIME_VERSION, mMimeVersion);
			}
			msg.saveChanges();
			if(this.mLogger != null){
				this.mLogger.write("MAIL000004") ;
			}
		}
		catch (Exception ex) {
			if(this.mLogger != null){
				this.mLogger.write("MAIL000002",ex) ;
			}
			throw new ServiceException("MAILSENDER201","MailSend Environment invalid " ,ex); //$NON-NLS-1$ //$NON-NLS-2$
		}
		// 送信
		Transport.send(msg);
		if(this.mLogger != null){
			this.mLogger.write("MAIL000005") ;
		}
		return true;
	}

	/**
	 * @see jp.ossc.nimbus.service.mail.MailSenderFactory#createMailSender()
	 */
	public MailSender createMailSender() {
		Class cls = null ;
		MailSenderOperator ret = null ;
		try {
			cls = Class.forName(
				mClassName,
				true,
				NimbusClassLoader.getInstance()
			);
		} catch (ClassNotFoundException e) {
			throw new ServiceException("MAILSENDER101","ClassNotFoundException clsname = "+mClassName ,e) ;  //$NON-NLS-1$ //$NON-NLS-2$
		}
		try {
			ret = (MailSenderOperator)cls.newInstance() ;
		} catch (InstantiationException e) {
			throw new ServiceException("MAILSENDER102","InstantiationException clsname = "+ mClassName ,e) ;   //$NON-NLS-1$//$NON-NLS-2$
		} catch (IllegalAccessException e) {
			throw new ServiceException("MAILSENDER103","IllegalAccessException clsname = "+ mClassName ,e) ;   //$NON-NLS-1$//$NON-NLS-2$
		}		
		ret.setFactory(this) ;
		return (MailSender)ret ;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#setConvertString(java.lang.String)
	 */
	public void setConvertString(String cnvStr) {
		mConvertString = cnvStr ; 
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.mail.SimpleMailSenderFactoryServiceMBean#setLoggerServiceName(jp.ossc.nimbus.core.ServiceName)
	 */
	public void setLoggerServiceName(ServiceName name) {
		this.mLoggerName = name ;
	}
}
