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

import jp.ossc.nimbus.ioc.ejb.*;
import javax.ejb.*;
import javax.naming.*;
import jp.ossc.nimbus.ioc.*;
import jp.ossc.nimbus.service.aspect.interfaces.InterceptorException;
import jp.ossc.nimbus.service.aspect.interfaces.TargetCheckedException;
import jp.ossc.nimbus.service.aspect.interfaces.TargetUncheckedException;
import jp.ossc.nimbus.service.aop.InvocationContext;
import jp.ossc.nimbus.service.aop.DefaultMethodInvocationContext;

/**
 * ファサードEJBクラス<p>
 * このEJBはRiquireでデプロイされる
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class SLSBFacadeBean 
	extends EJBDriveDispatcher implements SessionBean {
	
    private static final long serialVersionUID = 4992129166341785864L;
    
    private static final String C_UNITOFWORK_HOME_JNDI_KEY = "java:comp/env/ioc/unitOfWorkJNDIKey";
	private static final String C_COMMAND_HOME_JNDI_KEY = "java:comp/env/ioc/commandJNDIKey";
	private static final String C_INTERCEPTOR_SERVICE_NAME_JNDI_KEY = "java:comp/env/interceptorChainFactoryServiceNameJNDIKey"; 
	private static final String C_INTERCEPTOR_CREATE_JNDI_KEY =       "java:comp/env/lookupKey"; 
	private static final String C_LOGGER_JNDI_KEY = "java:comp/env/logger"; 
	private static Method CALLBACK_METHOD;
	static{
	    try{
            CALLBACK_METHOD = SLSBFacadeBean.class.getMethod("invokeUnitOfWorkBase", new Class[]{UnitOfWork.class});
	    }catch(NoSuchMethodException e){
	        e.printStackTrace();
	    }
	}
	public SLSBFacadeBean(){
		super() ;
	}
	/**
	 * EJBを作成する。
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate()
		throws javax.ejb.CreateException{
		try{
			this.init(C_UNITOFWORK_HOME_JNDI_KEY,
				C_COMMAND_HOME_JNDI_KEY,
				C_INTERCEPTOR_SERVICE_NAME_JNDI_KEY,
				C_INTERCEPTOR_CREATE_JNDI_KEY,
			    C_LOGGER_JNDI_KEY) ;
		}catch(NamingException e){
			CreateException ee =  new CreateException("SLSBFacadeBean NamingException Error") ;
			ee.initCause(e) ;
			throw ee ;
		} catch (CreateException e) {
			throw e;
		}
			
	}
	

	/* (非 Javadoc)
	 * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
	 */
	public void setSessionContext(SessionContext arg0)
		throws EJBException {
		this.setContext(arg0) ;
	}

	/* (非 Javadoc)
	 * @see javax.ejb.SessionBean#ejbRemove()
	 */
	public void ejbRemove(){}
	
	/* (非 Javadoc)
	 * @see javax.ejb.SessionBean#ejbActivate()
	 */
	public void ejbActivate(){}

	/* (非 Javadoc)
	 * @see javax.ejb.SessionBean#ejbPassivate()
	 */
	public void ejbPassivate() {}
	
	/**
	 * IoCコンテナのコマンド受付ファサード
	 * 本来パラメータとしてFacadeValueを受け取るが
	 * インターセプターにより変換される場合を考慮して
	 * ゆるいInterfaceとしている。
	 * @param value
	 * @return 実行結果
	 * @throws RemoteException
	 */
	public Object invoke(Object value) {
		Object ret = null;
		try {
			ret = this.invokeInterceptor(value);
		} catch (InterceptorException e) {
			IOCException ee =  new IOCException("SLSBFacadeBean invoke InterceptorError",e) ;
			throw ee ;
		} catch (TargetCheckedException e) {
			IOCException ee =  new IOCException("SLSBFacadeBean invoke CheckedError",e.getCause()) ;
			throw ee ;
		} catch (TargetUncheckedException e) {
			if(this.getLogger() != null){
				this.getLogger().write("IOC__00005",e) ;
			}
			Throwable the = e.getCause();
			if(the instanceof RuntimeException){
				throw (RuntimeException)the ;
			}else{
				IOCException ee =  new IOCException("SLSBFacadeBean invokeFacade UnCheckedError",the) ;
				throw ee ;
			}
		}catch(Throwable e){
			IOCException ee =  new IOCException("SLSBFacadeBean invokeFacade UnRecognize Error",e) ;
			throw ee ;
		}
		return ret ;
	}
	
	/**
	 * IoCのファサードメソッド
	 * @param fv
	 * @return　FacadeValue
	 * @throws RemoteException
	 */
	public FacadeValue invokeFacade(FacadeValue fv) {
		FacadeValue ret = null;
		try {
			ret = (FacadeValue)super.invokeInterceptor(fv);
		} catch (InterceptorException e) {
			IOCException ee =  new IOCException("SLSBFacadeBean invokeFacade InterceptorError",e) ;
			throw ee ;
		} catch (TargetCheckedException e) {
			//ここに飛んでくることはない。
			//コマンド層でキャッチされ処理済みのはず。
			IOCException ee =  new IOCException("SLSBFacadeBean invokeFacade CheckedError",e.getCause()) ;
			throw ee ;
		} catch (TargetUncheckedException e) {
			Throwable the = e.getCause();
			if(the instanceof RuntimeException){
				throw (RuntimeException)the ;
			}else{
				IOCException ee =  new IOCException("SLSBFacadeBean invokeFacade UnCheckedError",the) ;
				throw ee ;
			}
		}catch(Throwable e){
			IOCException ee =  new IOCException("SLSBFacadeBean invokeFacade UnRecognize Error",e) ;
			throw ee ;
		}
		return ret ;
	}

	protected InvocationContext createInvocationContext(Object input){
	    return new DefaultMethodInvocationContext(
	        this,
	        CALLBACK_METHOD,
	        new Object[]{input}
	    );
	}
	
}
