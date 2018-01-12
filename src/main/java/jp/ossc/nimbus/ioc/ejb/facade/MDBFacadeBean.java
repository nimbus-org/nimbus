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
// パッケージ
package jp.ossc.nimbus.ioc.ejb.facade;
//インポート
import java.rmi.RemoteException;
import java.lang.reflect.Method;

import javax.ejb.*;
import javax.jms.*;
import javax.naming.*;
import jp.ossc.nimbus.service.aspect.interfaces.InterceptorChainInvokerFactory;
import jp.ossc.nimbus.service.aspect.interfaces.InterceptorChainInvoker;
import jp.ossc.nimbus.service.aspect.interfaces.InterceptorException;
import jp.ossc.nimbus.service.aspect.interfaces.TargetCheckedException;
import jp.ossc.nimbus.service.aspect.interfaces.TargetUncheckedException;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.ioc.ejb.*;
import jp.ossc.nimbus.ioc.*;
import jp.ossc.nimbus.service.aop.DefaultMethodInvocationContext;
import jp.ossc.nimbus.service.aop.InterceptorChainFactory;
import jp.ossc.nimbus.service.aop.InterceptorChain;

/**
 * ファイル操作クラス<p>
 * ファイルのコピーやリネームと言った操作を行う
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class MDBFacadeBean implements MessageDrivenBean, MessageListener {
	
    private static final long serialVersionUID = 3583637962924535281L;
    
    /** ファサード実行ローカルインターフェース取得キー */
	private static final String C_FACADE_HOME_JNDI_KEY = "java:comp/env/facadeJNDIKey";
	private static final String C_INTERCEPTOR_SERVICE_NAME_JNDI_KEY = "java:comp/env/interceptorChainFactoryServiceNameJNDIKey"; 
	private static final String C_INTERCEPTOR_CREATE_JNDI_KEY =       "java:comp/env/lookupKey"; 
	/** ファサード実行ローカルインターフェース */
	private SLSBFacadeLocal mFacadeLocal ;
	/** インターセプターチェイン実行ローカルインターフェイス */
	private InterceptorChainInvokerFactory mFactory = null; 
	private InterceptorChainFactory chainFactory = null; 
	/** インターセプターキー */
	private String mInterceptorKey = null ;
	private MessageDrivenContext mContext = null ; 
	private static Method CALLBACK_METHOD;
	static{
	    try{
            CALLBACK_METHOD = MDBFacadeBean.class.getMethod("invokeUnitOfWorkBase", new Class[]{Object.class});
	    }catch(NoSuchMethodException e){
	        e.printStackTrace();
	    }
	}
	/**
	 * EJBを作成する。
	 * @throws RemoteException
	 * @throws CreateException
	 */
	public void ejbCreate()  {
		try {
			
			InitialContext ctx = new InitialContext() ;
			// FacadeLocal取得
			String value = (String)ctx.lookup(C_FACADE_HOME_JNDI_KEY);
			SLSBFacadeHomeLocal facadeHomeLocal = (SLSBFacadeHomeLocal)ctx.lookup(value) ;
			mFacadeLocal = facadeHomeLocal.create() ;
			// インターセプターファクトリ取得
			value = (String)ctx.lookup(C_INTERCEPTOR_SERVICE_NAME_JNDI_KEY);
			if(value == null || value.length()==0){
				mFactory = null ;
			}else{
				ServiceName serviceName = UtilTool.convertServiceName(value);
				Object factory = ServiceManagerFactory.getServiceObject(serviceName);
				if(factory instanceof InterceptorChainInvokerFactory){
				    mFactory = (InterceptorChainInvokerFactory)factory;
				}else{
				    chainFactory = (InterceptorChainFactory)factory;
				}
				//interceptor Factory Key
				value = (String)ctx.lookup(C_INTERCEPTOR_CREATE_JNDI_KEY);
				mInterceptorKey = value ;
			}
		} catch(NamingException ne) {
			IOCException ee =  new IOCException("MDBFacadeBean create Error NamingException") ;
			ee.initCause(ne) ;
			ee.printStackTrace();
			throw ee ;
		} catch (CreateException e) {
			IOCException ee =  new IOCException("MDBFacadeBean create Error NamingException") ;
			ee.initCause(e) ;
			ee.printStackTrace();
			throw ee ;
		}
	}
	
	/* (非 Javadoc)
	 * @see javax.ejb.MessageDrivenBean#setMessageDrivenContext(javax.ejb.MessageDrivenContext)
	 */
	public void setMessageDrivenContext(MessageDrivenContext arg0)
		throws EJBException{
        mContext = arg0;
	}
    
    protected MessageDrivenContext getMessageDrivenContext(){
        return mContext;
    }

	/* (非 Javadoc)
	 * @see javax.ejb.MessageDrivenBean#ejbRemove()
	 */
	public void ejbRemove() throws EJBException {
		// TODO 自動生成されたメソッド・スタブ

	}

	/**
	 * メッセージ受付を行います。
	 * @param message 入力オブジェクト
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	public void onMessage(Message message)  {
		try {
			invokeInterceptor(message);
			return ;
		} catch (InterceptorException e) {
			throw new EJBException("MDBFacadeBean onMessage Interceptor Error",e) ; 
		} catch (TargetCheckedException e) {
			throw new EJBException("MDBFacadeBean onMessage CheckedError",e) ; 
		} catch (TargetUncheckedException e) {
			throw new EJBException("MDBFacadeBean onMessage UnCheckedError",e) ; 
		}catch(Throwable e){
			IOCException ee =  new IOCException("MDBFacadeBeane onMessage UnRecognize Error",e) ;
			throw new EJBException("MDBFacadeBean onMessage UnRecognize Error",ee) ; 
		}
	}
	/**
	 * インターセプタを通してUnitOfWorkを実行する。
	 * インターセプタが指定されていなければ直接UnitOfWorkを実行する。
	 * @param input
	 * @return 出力オブジェクト
	 * @throws InterceptorException
	 * @throws TargetCheckedException
	 * @throws TargetUncheckedException
	 */
	public Object invokeInterceptor(Object input)
		throws InterceptorException, TargetCheckedException, TargetUncheckedException{
		Object ret = null;
		if(mFactory == null && chainFactory == null){
			try{
				ret =invokeUnitOfWorkBase(input) ;
			}catch(IOCException e){
				throw new TargetUncheckedException(e.getCause()) ;
			}catch(Throwable e){
				throw new TargetUncheckedException(e) ;
			}
		}else if(mFactory != null){
			InterceptorChainInvoker ici = this.mFactory.createInterceptorInvoker(this.mInterceptorKey) ;
			ret = ici.invokeChain(this,input);
		}else{
		    try{
    			InterceptorChain chain = chainFactory.getInterceptorChain(mInterceptorKey) ;
    			ret = chain.invokeNext(
            	    new DefaultMethodInvocationContext(
            	        this,
            	        CALLBACK_METHOD,
            	        new Object[]{input}
            	    )
    			);
			}catch(TargetUncheckedException e){
			    throw e;
			}catch(TargetCheckedException e){
			    throw e;
			}catch(InterceptorException e){
			    throw e;
			}catch(RuntimeException e){
			    throw e;
			}catch(Exception e){
			    throw new IOCException(e);
			}catch(Throwable th){
			    if(th instanceof Error){
    			    throw (Error)th;
			    }
			}
		}
		return ret;
	}
	/**
	 * ユニットオブワークを実行する
	 * @param uow		入力ユニットオブワーク
	 * @return	出力ユニットオブワーク
	 * @throws Throwable
	 */
	public  Object invokeUnitOfWorkBase(Object uow) throws Exception  {
		Object ret = null ;
		try{
			ret = this.mFacadeLocal.invoke(uow);
		}catch(EJBException e){
			Exception ex = e.getCausedByException(); 
			throw ex ;
		}catch(Throwable e){
			IOCException ex = new IOCException("MDBFacadeBean#invokeUnitOfWorkBase Unrecognized Error ", e); 
			throw ex ;
		}
		return ret ;
	}

}
