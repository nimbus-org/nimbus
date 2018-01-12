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
package jp.ossc.nimbus.service.ejb.transaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.lang.*;
import jp.ossc.nimbus.service.jndi.*;
import javax.naming.NamingException;
import javax.transaction.*;
//
/**
 * EJBトランザクションマネージャーコントロールクラス<p>
 * @author   nakano
 * @version  1.00 作成: 2003/11/28 -　H.Nakano
 */
public class TransactionControlService
	extends ServiceBase
	implements TransactionControl, TransactionControlServiceMBean {
	
    private static final long serialVersionUID = -6413299874051386643L;
    
    /** TransactionManager取得モード */
	private boolean mIsJNDIMode = true ;
	/** JNDIサービス名 */
	private ServiceName mJNDIServiceName = null ;
	/** JNDIサービス */
	private JndiFinder mJNDIFinder = null ;
	/** TransactionManagerFactoryクラス名 */
	private String mTransactionManagerFactoryClassName = null ;
	/** TransactionManagerFactoryクラス */
	private Class mTmFactoryClass = null ;
	/** TransactionManager取得メソッド名 */
	private String mGetTransactionManagerMethodName = null ;
	/** TransactionManager取得メソッド */
	private Method mGetTmFactoryMethod = null ;
	/** TransactionState管理スレッドローカル */
	private ThreadLocal mTransactionLocal = null ;
	/** TransactionManager管理スレッドローカル */
	private ThreadLocal mTransactionManagerLocal = null ;
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.core.ServiceBaseSupport#createService()
	 */
	public void createService(){
		mTransactionLocal = new ThreadLocal() ;
		mTransactionManagerLocal = new ThreadLocal() ;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.core.ServiceBaseSupport#startService()
	 */
	public void startService(){
		if(this.mIsJNDIMode){
			if(mJNDIServiceName == null ){
				throw new ServiceException("TransactionControl001","JNDIServiceName is not set") ; //$NON-NLS-1$ //$NON-NLS-2$
			}
			mJNDIFinder = (JndiFinder)ServiceManagerFactory.getServiceObject(this.mJNDIServiceName) ;
			if(mJNDIFinder == null ){
				throw new ServiceException("TransactionControl002","JNDIFinderService not exist") ;  //$NON-NLS-1$//$NON-NLS-2$
			}
		}else{
			if(this.mTransactionManagerFactoryClassName == null){
				throw new ServiceException("TransactionControl003","TransactionManagerFactoryClassName is not set") ; //$NON-NLS-1$ //$NON-NLS-2$
			}
			try {
				mTmFactoryClass = Class.forName(
					mTransactionManagerFactoryClassName,
					true,
					NimbusClassLoader.getInstance()
				);
			} catch (ClassNotFoundException e) {
				throw new ServiceException("TransactionControl004","TransactionManagerFactoryClass not found" ,e) ; //$NON-NLS-1$ //$NON-NLS-2$
			}
			if(this.mGetTransactionManagerMethodName == null){
				throw new ServiceException("TransactionControl005","GetTransactionManagerMethodName is not set") ;  //$NON-NLS-1$//$NON-NLS-2$
			}
			try {
				this.mGetTmFactoryMethod = mTmFactoryClass.getMethod(this.mGetTransactionManagerMethodName,(Class[])null) ;
			} catch (SecurityException e1) {
				throw new ServiceException("TransactionControl006","SecurityException methodname is " + this.mGetTransactionManagerMethodName ,e1) ; //$NON-NLS-1$ //$NON-NLS-2$
			} catch (NoSuchMethodException e1) {
				throw new ServiceException("TransactionControl007","TransactionManagerFactoryClass not found" ,e1) ; //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.core.Service#getState()
	 */
	public int getState() {
		TransactionState state = (TransactionState)this.mTransactionLocal.get() ;
		if(state == null){
			return INIT_STATE;
		}else{
			state = this.getCurrentTransactionState(state) ;
			return state.getState() ;
		}
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.ejb.transaction.TransactionControl#suspend()
	 */
	public void suspend() {
		TransactionState state = (TransactionState)this.mTransactionLocal.get() ;
		TransactionManager tm = null ;
		//初回サスペンド時
		if(state == null ){
			state = new TransactionState();
			tm = this.getTransactionManager() ;
			try {
				this.changeSuspend(state,tm) ;
			} catch (SystemException e) {
				throw new ServiceException("TransactionControl700","Transaction suspend error" ,e) ; //$NON-NLS-1$ //$NON-NLS-2$
			}
			this.mTransactionLocal.set(state) ;
			this.mTransactionManagerLocal.set(tm) ;
		//ネストサスペンド時
		}else{
			TransactionState addState = new TransactionState();
			addState.setParentState(state) ;
			tm = (TransactionManager)mTransactionManagerLocal.get() ;
			state = getCurrentTransactionState(state) ;
			//新しいトランザクションが開始してある場合のみ
			if(state.getState() == INIT_STATE ){
				try {
					changeSuspend(addState,tm) ;
				} catch (SystemException e) {
					throw new ServiceException("TransactionControl702","Transaction suspend error" ,e) ; //$NON-NLS-1$ //$NON-NLS-2$
				}
				state.setChildState(addState) ;
			}else{
				//異常な呼び出し
				throw new ServiceException("TransactionControl703","Transaction suspend state invalid") ; //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	public void terminateTransactioinControl(){
		this.mTransactionLocal.set(null) ;
		this.mTransactionManagerLocal.set(null) ;
	}

	/**
	 * changeSuspend
	 * @param state
	 * @param tm
	 * @throws SystemException
	 */
	protected void changeSuspend(TransactionState state,
									TransactionManager tm) 
		throws SystemException{
		Transaction old = null ;
		old = tm.suspend() ;
		state.setCurrentTransaction(old) ;
		state.setState(SUSPEND_STATE) ;
	}
	/**
	 * getCurrentTransactionState
	 * @param state
	 * @return
	 */
	protected TransactionState getCurrentTransactionState(TransactionState state){
		TransactionState ret = null ;
		ret = state ;
		while(true){
			if(ret.getChildState() == null){
				break ;		
			}
		}
		return ret ;
	}
	/**
	 * getTransactionManager
	 * @return
	 */
	protected TransactionManager getTransactionManager(){
		TransactionManager tm = null ;
		if(this.mIsJNDIMode){
			try {
				tm = (TransactionManager)mJNDIFinder.lookup() ;
			} catch (NamingException e) {
				throw new ServiceException("TransactionControl100","TransactionManager get Error NamingException" ,e) ;  //$NON-NLS-1$//$NON-NLS-2$
			}
		}else{
			try {
				tm = (TransactionManager)this.mGetTmFactoryMethod.invoke(this.mTmFactoryClass,(Object[])null) ;
			} catch (IllegalArgumentException e) {
				throw new ServiceException("TransactionControl101","TransactionManager get Error IllegalArgumentException" ,e) ; //$NON-NLS-1$ //$NON-NLS-2$
			} catch (IllegalAccessException e) {
				throw new ServiceException("TransactionControl102","TransactionManager get Error IllegalAccessException" ,e) ; //$NON-NLS-1$ //$NON-NLS-2$
			} catch (InvocationTargetException e) {
				throw new ServiceException("TransactionControl103","TransactionManager get Error InvocationTargetException" ,e) ;  //$NON-NLS-1$//$NON-NLS-2$
			}					
		}
		return tm ;
		
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.ejb.transaction.TransactionControl#resume()
	 */
	public void resume() {
		TransactionState state = (TransactionState)this.mTransactionLocal.get() ;
		if(state !=null){
			state = this.getCurrentTransactionState(state) ;
			//SUSPEND状態の場合のみ反応
			if(state.getState() == SUSPEND_STATE){
				TransactionManager tm = (TransactionManager)mTransactionManagerLocal.get() ;
				Transaction tran = state.getCurrentTransaction() ;
				//resume
				try {
					tm.resume(tran) ;
				} catch (InvalidTransactionException e) {
					throw new ServiceException("TransactionControl300","TransactionManager resume Error InvalidTransactionException" ,e) ;  //$NON-NLS-1$//$NON-NLS-2$
				} catch (IllegalStateException e) {
					throw new ServiceException("TransactionControl301","TransactionManager resume Error IllegalStateException" ,e) ;  //$NON-NLS-1$//$NON-NLS-2$
				} catch (SystemException e) {
					throw new ServiceException("TransactionControl302","TransactionManager resume Error SystemException" ,e) ; //$NON-NLS-1$ //$NON-NLS-2$
				}
				//管理部の構造を設定
				TransactionState parentState = state.getParentState() ;
				if(parentState == null){
					terminateTransactioinControl() ;
				}else{
					parentState.setChildState(null) ;
				}
			}else{
				throw new ServiceException("TransactionControl303","Transaction state invalid") ; //$NON-NLS-1$ //$NON-NLS-2$
			}
		}else{
			throw new ServiceException("TransactionControl304","Transaction state none") ; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.ejb.transaction.TransactionControl#beginNewTransaction()
	 */
	public void beginNewTransaction() {
		TransactionState state = (TransactionState)this.mTransactionLocal.get() ;
		if(state != null){
			state = this.getCurrentTransactionState(state) ;
			if(state.getState() == SUSPEND_STATE){
				TransactionManager tm = (TransactionManager)mTransactionManagerLocal.get() ;
				try {
					tm.begin() ;
				} catch (NotSupportedException e) {
					throw new ServiceException("TransactionControl400","TransactionManager begin Error NotSupportedException" ,e) ;  //$NON-NLS-1$//$NON-NLS-2$
				} catch (SystemException e) {
					throw new ServiceException("TransactionControl401","TransactionManager begin Error SystemException" ,e) ; //$NON-NLS-1$ //$NON-NLS-2$
				}
				Transaction newTran = null ;
				try {
					newTran = tm.getTransaction();
				} catch (SystemException e1) {
					try {
						tm.rollback() ;
					} catch (Throwable e2) {
					}
					throw new ServiceException("TransactionControl402","New Transaction get Error SystemException" ,e1) ;  //$NON-NLS-1$//$NON-NLS-2$
				}		
				TransactionState newState = new TransactionState() ;
				newState.setParentState(state) ;
				newState.setCurrentTransaction(newTran) ;
				newState.setState(INIT_STATE) ;
				state.setChildState(newState) ;
			}else{
				throw new ServiceException("TransactionControl403","New Transaction state invalid") ;  //$NON-NLS-1$//$NON-NLS-2$
			}
		}else{
			throw new ServiceException("TransactionControl404","New Transaction state invalid") ;  //$NON-NLS-1$//$NON-NLS-2$
		}
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.ejb.transaction.TransactionControl#commitNewTransaction()
	 */
	public void commitNewTransaction() {
		TransactionState state = (TransactionState)this.mTransactionLocal.get() ;
		if(state != null){
			state = this.getCurrentTransactionState(state) ;
			if(state.getState() == INIT_STATE){
				TransactionManager tm = (TransactionManager)mTransactionManagerLocal.get() ;
				Transaction tran = state.getCurrentTransaction() ;
				try {
					tran.commit();
				} catch (SecurityException e) {
					throw new ServiceException("TransactionControl500","Transaction Commit Error SecurityException" ,e) ;  //$NON-NLS-1$//$NON-NLS-2$
				} catch (RollbackException e) {
					throw new ServiceException("TransactionControl501","Transaction Commit Error RollbackException" ,e) ;  //$NON-NLS-1$//$NON-NLS-2$
				} catch (HeuristicMixedException e) {
					throw new ServiceException("TransactionControl502","Transaction Commit Error HeuristicMixedException" ,e) ;  //$NON-NLS-1$//$NON-NLS-2$
				} catch (HeuristicRollbackException e) {
					throw new ServiceException("TransactionControl503","Transaction Commit Error HeuristicRollbackException " ,e) ;  //$NON-NLS-1$//$NON-NLS-2$
				} catch (SystemException e) {
					throw new ServiceException("TransactionControl504","Transaction Commit Error SystemException" ,e) ;  //$NON-NLS-1$//$NON-NLS-2$
				}
				try {
					tm.commit() ;
				} catch (SecurityException e1) {
					throw new ServiceException("TransactionControl510","TransactionManager Commit Error SecurityException" ,e1) ;  //$NON-NLS-1$//$NON-NLS-2$
				} catch (IllegalStateException e1) {
					throw new ServiceException("TransactionControl511","TransactionManager Commit Error IllegalStateException" ,e1) ;  //$NON-NLS-1$//$NON-NLS-2$
				} catch (RollbackException e1) {
					throw new ServiceException("TransactionControl512","TransactionManager Commit Error RollbackException" ,e1) ;  //$NON-NLS-1$//$NON-NLS-2$
				} catch (HeuristicMixedException e1) {
					throw new ServiceException("TransactionControl513","TransactionManager Commit Error HeuristicMixedException" ,e1) ;  //$NON-NLS-1$//$NON-NLS-2$
				} catch (HeuristicRollbackException e1) {
					throw new ServiceException("TransactionControl514","TransactionManager Commit Error HeuristicRollbackException" ,e1) ;  //$NON-NLS-1$//$NON-NLS-2$
				} catch (SystemException e1) {
					throw new ServiceException("TransactionControl515","TransactionManager Commit Error SystemException" ,e1) ;  //$NON-NLS-1$//$NON-NLS-2$
				}
				TransactionState parent = state.getParentState() ;
				parent.setChildState(null);
			}else{
				throw new ServiceException("TransactionControl516","TransactionManager Commit Error state invalid" ) ;  //$NON-NLS-1$//$NON-NLS-2$
			}
		}else{
			throw new ServiceException("TransactionControl517","TransactionManager Commit Error state invalid" ) ;  //$NON-NLS-1$//$NON-NLS-2$
		}
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.ejb.transaction.TransactionControl#rollBackNewTransaction()
	 */
	public void rollBackNewTransaction() {
		TransactionState state = (TransactionState)this.mTransactionLocal.get() ;
		if(state != null){
			state = this.getCurrentTransactionState(state) ;
			if(state.getState() == INIT_STATE){
				Transaction tran = state.getCurrentTransaction() ;
				TransactionManager tm = (TransactionManager)mTransactionManagerLocal.get() ;
				try {
					tran.rollback();
				} catch (SecurityException e) {
					throw new ServiceException("TransactionControl600","Transaction RollBack Error SecurityException" ,e) ;  //$NON-NLS-1$//$NON-NLS-2$
				} catch (SystemException e) {
					throw new ServiceException("TransactionControl601","Transaction RollBack Error SecurityException" ,e) ;  //$NON-NLS-1$//$NON-NLS-2$
				}
				try {
					tm.commit() ;
				} catch (RollbackException e2) {
					throw new ServiceException("TransactionControl610","TransactionManager Commit Error RollbackException" ,e2) ;  //$NON-NLS-1$//$NON-NLS-2$
				} catch (HeuristicMixedException e2) {
					throw new ServiceException("TransactionControl611","TransactionManager Commit Error HeuristicMixedException" ,e2) ;  //$NON-NLS-1$//$NON-NLS-2$
				} catch (HeuristicRollbackException e2) {
					throw new ServiceException("TransactionControl612","TransactionManager Commit Error HeuristicRollbackException" ,e2) ;  //$NON-NLS-1$//$NON-NLS-2$
				} catch (SystemException e2) {
					throw new ServiceException("TransactionControl613","TransactionManager Commit Error SystemException" ,e2) ;  //$NON-NLS-1$//$NON-NLS-2$
				}
				TransactionState parent = state.getParentState() ;
				parent.setChildState(null);
			}else{
				throw new ServiceException("TransactionControl614","Transaction eollbacl Error State Invalid") ;  //$NON-NLS-1$//$NON-NLS-2$
			}
		}else{
			throw new ServiceException("TransactionControl615","Transaction eollbacl Error State Invalid") ;  //$NON-NLS-1$//$NON-NLS-2$
		}
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.ejb.transaction.TransactionControlServiceMBean#setJNDIMode(boolean)
	 */
	public void setJNDIMode(boolean isJNDIMode) {
		this.mIsJNDIMode = isJNDIMode ;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.ejb.transaction.TransactionControlServiceMBean#setJNDIServiceName(jp.ossc.nimbus.core.ServiceName)
	 */
	public void setJNDIServiceName(ServiceName name) {
		this.mJNDIServiceName = name ;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.ejb.transaction.TransactionControlServiceMBean#setTransactionFactoryClassName(java.lang.String)
	 */
	public void setTransactionManagerFactoryClassName(String clsName) {
		this.mTransactionManagerFactoryClassName = clsName ;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.ejb.transaction.TransactionControlServiceMBean#setGetTransactinManagerMethodName(java.lang.String)
	 */
	public void setGetTransactinManagerMethodName(String methodName) {
		this.mGetTransactionManagerMethodName = methodName ;
	}
    
    /**
     * JndiFinderを設定する。
     */
    public void setJndiFinder(JndiFinder finder) {
        mJNDIFinder = finder;
    }
    
	//
	/**
	 * トランザクション状態管理構造体クラス 
	 * @author   nakano
	 * @version  1.00 作成: 2003/11/29 -　H.Nakano
	 */
	private class TransactionState {
		private int mState = SUSPEND_STATE ;
		private Transaction mCurrentTransaction = null ;
		private TransactionState mChildState= null ;
		private TransactionState mParentState= null ;
		/**
		 * getChildState
		 * @return
		 */
		public TransactionState getChildState() {
			return mChildState;
		}

		/**
		 * getCurrentTransaction
		 * @return
		 */
		public Transaction getCurrentTransaction() {
			return mCurrentTransaction;
		}

		/**
		 * getState
		 * @return
		 */
		public int getState() {
			return mState;
		}

		/**
		 * setChildState
		 * @param state
		 */
		public void setChildState(TransactionState state) {
			mChildState = state;
		}

		/**
		 * setCurrentTransaction
		 * @param transaction
		 */
		public void setCurrentTransaction(Transaction transaction) {
			mCurrentTransaction = transaction;
		}

		/**
		 * setState
		 * @param i
		 */
		public void setState(int i) {
			mState = i;
		}

		/**
		 * getParentState
		 * @return
		 */
		public TransactionState getParentState() {
			return mParentState;
		}

		/**
		 * setParentState
		 * @param state
		 */
		public void setParentState(TransactionState state) {
			mParentState = state;
		}

	}

}
