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
package jp.ossc.nimbus.ioc.ejb.unitofwork;
// インポート
import java.rmi.RemoteException;
import java.lang.reflect.Method;

import javax.ejb.*;
import javax.naming.*;
import jp.ossc.nimbus.ioc.*;
import jp.ossc.nimbus.ioc.ejb.*;
import jp.ossc.nimbus.service.aspect.interfaces.InterceptorException;
import jp.ossc.nimbus.service.aspect.interfaces.TargetCheckedException;
import jp.ossc.nimbus.service.aspect.interfaces.TargetUncheckedException;
import jp.ossc.nimbus.service.aop.InvocationContext;
import jp.ossc.nimbus.service.aop.DefaultMethodInvocationContext;

/**
 * UnitOfWork実行EJBクラス<p>
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class SLSBUnitOfWorkBean extends EJBDriveDispatcher implements SessionBean {
	
    private static final long serialVersionUID = 426509298145817079L;
    
	private static final String C_UNITOFWORK_HOME_JNDI_KEY = "java:comp/env/ioc/unitOfWorkJNDIKey";
	private static final String C_COMMAND_HOME_JNDI_KEY = "java:comp/env/ioc/commandJNDIKey";
	private static final String C_INTERCEPTOR_SERVICE_NAME_JNDI_KEY = "java:comp/env/interceptorChainFactoryServiceNameJNDIKey"; 
	private static final String C_INTERCEPTOR_CREATE_JNDI_KEY = "java:comp/env/lookupKey"; 
	private static final String C_LOGGER_JNDI_KEY = "java:comp/env/logger"; 
	private static Method CALLBACK_METHOD;
	static{
	    try{
            CALLBACK_METHOD = SLSBUnitOfWorkBean.class.getMethod("invokeUnitOfWorkBase", new Class[]{UnitOfWork.class});
	    }catch(NoSuchMethodException e){
	        e.printStackTrace();
	    }
	}
	public SLSBUnitOfWorkBean(){
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
		}catch(CreateException e){
			throw e;
		}catch(NamingException e){
			CreateException ee =  new CreateException("SLSBUnitOfWorkBean create Error");
			ee.initCause(e) ;
			throw ee ;
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
	 * UnitOfWork実行メソッド
	 * @param fv
	 * @return　FacadeValue
	 * @throws RemoteException
	 */
	public UnitOfWork invokeUnitOfWork(UnitOfWork fv) {
		UnitOfWork ret = null;
		try {
			ret = (UnitOfWork)this.invokeInterceptor(fv);
		} catch (InterceptorException e) {
			IOCException ee =  new IOCException("SLSBUnitOfWorkBean invokeUnitOfWork InterceptorError",e) ;
			throw ee;
		} catch (TargetCheckedException e) {
			//ここに飛んでくることはない。
			//コマンド層でキャッチされ処理済みのはず。
			IOCException ee =  new IOCException("SLSBUnitOfWorkBean invokeUnitOfWork CheckedError",e.getCause()) ;
			throw ee ;
		} catch (TargetUncheckedException e) {
			Throwable the = e.getCause();
			if(the instanceof RuntimeException){
				throw (RuntimeException)the ;
			}else{
				IOCException ee =  new IOCException("SLSBUnitOfWorkBean invokeUnitOfWork UnCheckedError",the) ;
				throw ee ;
			}
		}catch(Throwable e){
			IOCException ee =  new IOCException("SLSBUnitOfWorkBean invokeUnitOfWork UnRecognize Error",e) ;
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
