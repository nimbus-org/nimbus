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
// インポート
package jp.ossc.nimbus.ioc.ejb.command;

import java.rmi.RemoteException;
import java.lang.reflect.Method;

import javax.naming.*;
import javax.ejb.*;
import jp.ossc.nimbus.ioc.*;
import jp.ossc.nimbus.ioc.ejb.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aspect.interfaces.InterceptorChainInvokerFactory;
import jp.ossc.nimbus.service.aspect.interfaces.InterceptorChainInvoker;
import jp.ossc.nimbus.service.aspect.interfaces.InterceptorException;
import jp.ossc.nimbus.service.aspect.interfaces.TargetCheckedException;
import jp.ossc.nimbus.service.aspect.interfaces.TargetUncheckedException;
import jp.ossc.nimbus.service.beancontrol.interfaces.*;
import jp.ossc.nimbus.service.log.*;
import jp.ossc.nimbus.service.aop.InvocationContext;
import jp.ossc.nimbus.service.aop.InterceptorChainFactory;
import jp.ossc.nimbus.service.aop.InterceptorChain;
import jp.ossc.nimbus.service.aop.DefaultMethodInvocationContext;

/**
 * ステートレスセッションBeanのコマンドクラス。<p>
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class SLSBCommandBean extends BeanFlowClient implements SessionBean {
    
    private static final long serialVersionUID = 3745197383168852889L;
    
	private static final String C_INTERCEPTOR_SERVICE_NAME_JNDI_KEY = "java:comp/env/interceptorChainInvokerFactoryServiceNameJNDIKey"; 
	private static final String C_BEAN_FLOW_SERVICE_NAME_JNDI_KEY = "java:comp/env/beanFlowFactoryServiceNameJNDIKey";
	private static final String C_LOGGER_JNDI_KEY = "java:comp/env/logger";
	private static Method CALLBACK_METHOD;
	static{
	    try{
            CALLBACK_METHOD = SLSBCommandBean.class.getMethod("invokeBeanFlow", new Class[]{Command.class});
	    }catch(NoSuchMethodException e){
	        e.printStackTrace();
	    }
	}

	/**
	 * EJBを作成する。
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() 
		throws javax.ejb.CreateException{
	
		InitialContext ctx = null;
		try {
			ctx = new InitialContext() ;
			//ログの取得
			String value = null;
			//Logger
			try{
				value = (String)ctx.lookup(C_LOGGER_JNDI_KEY);
			}catch(NamingException e){}
			ServiceName serviceName = null ;
			Logger logger = null ;
			if(value != null && value.length() > 0){
				serviceName = UtilTool.convertServiceName(value);
				logger = (Logger)ServiceManagerFactory.getService(serviceName);	
			}
			//インターセプチェインタファクトリーサービスの取得
			InterceptorChainInvokerFactory iciFactory = null;
			InterceptorChainFactory icFactory = null;
			value = (String)ctx.lookup(C_INTERCEPTOR_SERVICE_NAME_JNDI_KEY);
			if(value != null && value.length() > 0){
    			serviceName = UtilTool.convertServiceName(value);
    			Object factory = ServiceManagerFactory.getService(serviceName);
    			if(factory instanceof InterceptorChainInvokerFactory){
    			    iciFactory = (InterceptorChainInvokerFactory)factory;
    			}else{
    			    icFactory = (InterceptorChainFactory)factory;
    			}
			}
			// Beanフローファクトリーサービスの取得
			value = (String)ctx.lookup(C_BEAN_FLOW_SERVICE_NAME_JNDI_KEY);
			serviceName = UtilTool.convertServiceName(value);
			BeanFlowInvokerFactory bfFactory = (BeanFlowInvokerFactory)ServiceManagerFactory.getService(serviceName);
			super.init(logger,iciFactory,icFactory,bfFactory) ;
			
		} catch (NamingException e) {
			CreateException ce = new CreateException("InitialContext Error") ;
			ce.initCause(e) ;
			throw ce ;
		}
		
	}

	/**
	 * コマンドを実行する。
	 * @param cmd コマンド
	 * @return 実行後のコマンド
	 */
	public Command invokeCommand(Command cmd) {
		if(this.getLogger() != null){
			this.getLogger().write("IOC__00006");
		}
		// インターセプタで実行した際にも同じフローキーで実行させるため保持しておく。
		String key = cmd.getFlowKey();
		InterceptorChainInvoker ici = null ;
		InterceptorChain ic = null ;
		if(this.getIciFactory() !=null){
			ici = this.getIciFactory().createInterceptorInvoker(key) ;
		}else if(this.getIcFactory() !=null){
			ic = this.getIcFactory().getInterceptorChain(key) ;
		}
		Object ret = null ;
		if(ici==null && ic == null){
			// 直接実行
			if(this.getLogger() !=null ){
				this.getLogger().write("IOC__00007");
			}
			try {
				ret = invokeBeanFlow(cmd) ;
			} catch (Exception e) {
				if(e instanceof RuntimeException){
					throw (RuntimeException)e ;
				}else{
					cmd.setException(e) ;
				}
			}catch (Throwable e){
				throw new IOCException("SLSBCommandBean invokeCommand Unrecognize Exception",e) ;
			}
			
		}else if(ici != null){
			// インターセプタを通して実行
			try {
				if(this.getLogger()!= null){
					this.getLogger().write("IOC__00008");
				}
				ret = ici.invokeChain(this,cmd) ;
				if(this.getLogger() != null){
					this.getLogger().write("IOC__00012");
				}
				
			} catch (InterceptorException e) {
				if(this.getLogger() != null){
					this.getLogger().write("IOC__00009",e);
				}
				throw new IOCException("SLSBCommandBean invokeCommand InterceptorException",e) ;
			} catch (TargetCheckedException e) {
				if(this.getLogger() != null){
					this.getLogger().write("IOC__00010",e);
				}
				cmd.setException(e.getCause()) ;
				return cmd ;
			} catch (TargetUncheckedException e) {
				if(this.getLogger() != null){
					this.getLogger().write("IOC__00011",e.getCause());
				}
				if(e.getCause() instanceof RuntimeException){
					throw (RuntimeException)e.getCause() ;
				}else{
					throw new IOCException("SLSBCommandBean invokeCommand TargetUncheckedException",e.getCause()) ;
				}
			}catch (Throwable e){
				throw new IOCException("SLSBCommandBean invokeCommand Unrecognize Exception",e) ;
			}
		}else{
			// インターセプタを通して実行
			try {
				if(this.getLogger()!= null){
					this.getLogger().write("IOC__00008");
				}
    		    ret = ic.invokeNext(createInvocationContext(cmd));
				if(this.getLogger() != null){
					this.getLogger().write("IOC__00012");
				}
				
			} catch (RuntimeException e) {
				if(this.getLogger() != null){
					this.getLogger().write("IOC__00011",e);
				}
				throw e;
			} catch (Exception e) {
				if(this.getLogger() != null){
					this.getLogger().write("IOC__00010",e);
				}
				cmd.setException(e) ;
				return cmd ;
			}catch (Throwable e){
				throw new IOCException("SLSBCommandBean invokeCommand Unrecognize Exception",e) ;
			}
		}
		cmd.setOutObject(ret) ;
		return cmd ;
	}
	

	protected InvocationContext createInvocationContext(Object input){
	    return new DefaultMethodInvocationContext(
	        this,
	        CALLBACK_METHOD,
	        new Object[]{input}
	    );
	}

	/* (非 Javadoc)
	 * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
	 */
	public void setSessionContext(SessionContext arg0)
		throws EJBException, RemoteException {

	}

	/* (非 Javadoc)
	 * @see javax.ejb.SessionBean#ejbRemove()
	 */
	public void ejbRemove() throws EJBException, RemoteException {
	}

	/* (非 Javadoc)
	 * @see javax.ejb.SessionBean#ejbActivate()
	 */
	public void ejbActivate() throws EJBException, RemoteException {
	}

	/* (非 Javadoc)
	 * @see javax.ejb.SessionBean#ejbPassivate()
	 */
	public void ejbPassivate() throws EJBException, RemoteException {
	}

}
