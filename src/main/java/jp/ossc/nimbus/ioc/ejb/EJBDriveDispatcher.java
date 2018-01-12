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
package jp.ossc.nimbus.ioc.ejb;
//インポート
import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.ioc.Command;
import jp.ossc.nimbus.ioc.CommandBase;
import jp.ossc.nimbus.ioc.IOCException;
import jp.ossc.nimbus.ioc.UnitOfWork;
import jp.ossc.nimbus.ioc.FacadeValueAccess;
import jp.ossc.nimbus.ioc.ejb.command.SLSBCommandHomeLocal;
import jp.ossc.nimbus.ioc.ejb.command.SLSBCommandLocal;
import jp.ossc.nimbus.ioc.ejb.unitofwork.SLSBUnitOfWorkHomeLocal;
import jp.ossc.nimbus.ioc.ejb.unitofwork.SLSBUnitOfWorkLocal;
import jp.ossc.nimbus.service.aspect.interfaces.InterceptorChainInvoker;
import jp.ossc.nimbus.service.aspect.interfaces.InterceptorChainInvokerFactory;
import jp.ossc.nimbus.service.aspect.interfaces.InterceptorException;
import jp.ossc.nimbus.service.aspect.interfaces.TargetCheckedException;
import jp.ossc.nimbus.service.aspect.interfaces.TargetUncheckedException;
import jp.ossc.nimbus.service.aop.InterceptorChainFactory;
import jp.ossc.nimbus.service.aop.InterceptorChain;
import jp.ossc.nimbus.service.aop.InvocationContext;
import jp.ossc.nimbus.service.log.Logger;
/**
 * EJB実行コマンドインターフェイス<p>
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public abstract class EJBDriveDispatcher {
	/** セッションコンテキスト */
	private SessionContext mContext = null ;
	/** UnitOfWorkHomeインターフェイス */
	private SLSBUnitOfWorkHomeLocal mUnitOfWorkInvokerHome = null;
	/** UnitOfWorkローカルインターフェイス */
	private SLSBUnitOfWorkLocal mUnitOfWorkInvokerRemote = null ;
	/** CommandHOMEインターフェイス */
	private SLSBCommandHomeLocal mCommandInvokerHome = null ;
	/** Commandローカルインターフェイス */
	private SLSBCommandLocal mCommandInvokerRemote = null ;
	/** インターセプターチェイン実行ローカルインターフェイス */
	private InterceptorChainInvokerFactory mFactory = null; 
	private InterceptorChainFactory chainFactory = null; 
	/** インターセプターキー */
	private String mInterceptorKey = null ;
	/** Logger*/
	private Logger mLogger = null ;
	protected final String IOC__00001 = "IOC__00001" ;
	protected final String IOC__00002 = "IOC__00002" ;
	protected final String IOC__00003 = "IOC__00003" ;
	
	/**
	 * EJBローカルを取得する。
	 * @param unitOfWorkKey
	 * @param commandKey
	 * @param intercetorFactrySvcName
	 * @param interceptorCreateKey
	 * @param loggerServiceName
	 * @throws NamingException
	 * @throws CreateException
	 * @throws RemoteException
	 */
	public void init(	String unitOfWorkKey,
							String commandKey,
							String intercetorFactrySvcName,
							String interceptorCreateKey,
							String loggerServiceName ) 
		throws NamingException,  CreateException{
		InitialContext ctx = new InitialContext() ;
		String value = null;
		//Logger
		try{
			value = (String)ctx.lookup(loggerServiceName);
		}catch(NamingException e){}
		if(value == null || value.length()==0){
			this.mLogger = null ;
		}else{
			ServiceName serviceName = UtilTool.convertServiceName(value);
			this.mLogger = (Logger)ServiceManagerFactory.getService(serviceName);
		}
		// ユニットオブワークLocal取得
		value = (String)ctx.lookup(unitOfWorkKey);
		mUnitOfWorkInvokerHome = (SLSBUnitOfWorkHomeLocal)ctx.lookup(value) ;
		// コマンドLocal取得
		value = (String)ctx.lookup(commandKey);
		mCommandInvokerHome = (SLSBCommandHomeLocal)ctx.lookup(value) ;
		// インターセプターファクトリ取得
		value = (String)ctx.lookup(intercetorFactrySvcName);
		if(value == null || value.length()==0){
			mFactory = null ;
		}else{
			ServiceName serviceName = UtilTool.convertServiceName(value);
			Object factory = ServiceManagerFactory.getService(serviceName);
			if(factory instanceof InterceptorChainInvokerFactory){
			    mFactory = (InterceptorChainInvokerFactory)factory;
			}else if(factory instanceof InterceptorChainFactory){
			    chainFactory = (InterceptorChainFactory)factory;
			}
			
			//interceptor Factory Key
			value = (String)ctx.lookup(interceptorCreateKey);
			mInterceptorKey = value ;
		}
		if(this.getLogger() != null){
			this.getLogger().write(IOC__00001,"InterceptorChainInvokerFactory get completed.") ;
		}
	}
	/**
	 * Loggerを出力する
	 * @return Logger
	 */
	protected Logger getLogger(){
		return this.mLogger ;
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
				ret =(UnitOfWork)invokeUnitOfWorkBase((UnitOfWork)input) ;
			}catch(IOCException e){
				throw new TargetUncheckedException(e.getCause()) ;
			}catch(Throwable e){
				if(this.getLogger() != null){
					this.getLogger().write("IOC__00018",e);
				}
				throw new TargetUncheckedException(e) ;
			}
		}else{
			if(this.getLogger() != null){
				this.getLogger().write("IOC__00004",this.getInterceptorKey()) ;
			}
		    if(mFactory != null){
    			InterceptorChainInvoker ici = this.mFactory.createInterceptorInvoker(this.getInterceptorKey()) ;
    			ret = ici.invokeChain(this,input);
    		}else if(chainFactory != null){
    			try{
        			InterceptorChain chain = chainFactory.getInterceptorChain(getInterceptorKey()) ;
        		    ret = chain.invokeNext(createInvocationContext(input));
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
		    
		}
		return ret;
	}
	/**
	 * ユニットオブワークを実行する
	 * @param uow		入力ユニットオブワーク
	 * @return	出力ユニットオブワーク
	 * @throws Throwable
	 */
	public  UnitOfWork invokeUnitOfWorkBase(UnitOfWork uow) throws Exception  {
		UnitOfWork retUow = FacadeValueAccess.createCommandsValue() ;
		boolean cmdErrFlg = false;
		//配列分実行する
		if(this.getLogger() != null){this.getLogger().write(IOC__00002) ;}
		for(int rcnt= 0 ;rcnt < uow.size();rcnt++){
			CommandBase tmp = uow.getCommand(rcnt) ;
			try{
				if(tmp.isCommand()){
					if (!cmdErrFlg) {
						if(mCommandInvokerRemote==null){
							mCommandInvokerRemote = mCommandInvokerHome.create() ;
							if(this.getLogger() != null){
								this.getLogger().write(IOC__00001,"SLSBCommandHomeLocal get cmpleted.") ;
							}
						}
						Command ret = this.mCommandInvokerRemote.invokeCommand((Command)tmp);
						if (ret.getStatus() == CommandBase.C_STATUS_ERROR) {
							if(this.getLogger() != null){
								this.getLogger().write("IOC__00020");
							}
							this.getContext().setRollbackOnly() ;
							cmdErrFlg = true;
						}
						retUow.addCommand(ret) ;
					}else{
						if(this.getLogger() != null){
							this.getLogger().write("IOC__00021");
						}
						retUow.addCommand((Command)tmp) ;
					}
				}else{
					if(this.getLogger() != null){
						this.getLogger().write("IOC__00015");
					}
					if(mUnitOfWorkInvokerRemote == null){
						mUnitOfWorkInvokerRemote = mUnitOfWorkInvokerHome.create() ;
						if(this.getLogger() != null){
							this.getLogger().write(IOC__00001,"SLSBUnitOfWorkHomeLocal get cmpleted.") ;
						}
					}
					UnitOfWork ret = this.mUnitOfWorkInvokerRemote.invokeUnitOfWork((UnitOfWork)tmp);
					retUow.addUnitOfWork(ret) ;
				}
			}catch(EJBException e){
				if(this.getLogger() != null){
					this.getLogger().write("IOC__00017",e.getCausedByException());
				}
				Exception ex = (Exception) e.getCausedByException(); 
				throw ex ;
			}catch(Throwable e){
				if(this.getLogger() != null){
					this.getLogger().write("IOC__00017",e);
				}
				IOCException ex = new IOCException("EJBDispatcher#invokeUnitOfWorkBase Unrecognized Error ", e); 
				throw ex ;
			}
		}
		return retUow ;
	}


	/**
	 * @return 
	 */
	public SessionContext getContext() {
		return mContext;
	}

	/**
	 * @return 
	 */
	public InterceptorChainInvokerFactory getFactory() {
		return mFactory;
	}

	/**
	 * @return 
	 */
	public String getInterceptorKey() {
		return mInterceptorKey;
	}


	/**
	 * @param context
	 */
	public void setContext(SessionContext context) {
		mContext = context;
	}

	/**
	 * @param string
	 */
	public void setInterceptorKey(String string) {
		mInterceptorKey = string;
	}
	
	public InterceptorChainFactory getInterceptorChainFactory() {
		return chainFactory;
	}

	protected abstract InvocationContext createInvocationContext(Object input);

}
