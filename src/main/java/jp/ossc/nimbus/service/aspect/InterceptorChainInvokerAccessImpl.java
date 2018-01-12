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
package jp.ossc.nimbus.service.aspect;
//インポート
import java.lang.reflect.*;
import java.util.*;
import jp.ossc.nimbus.service.aspect.interfaces.*;
import jp.ossc.nimbus.service.log.*;
import jp.ossc.nimbus.service.aspect.util.*;
import jp.ossc.nimbus.core.*;

/**
 * インターセプター実行クラス<p>
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class InterceptorChainInvokerAccessImpl
	implements InterceptorChainInvoker, InterceptorChainInvokerAccess {
	// インターセプタリスト
	protected IntreceptorChainList mList = null;
	// リストイテレータ
	protected Iterator mIte = null;
	// コールバック対象のオブジェクト
	protected Object mCallBackObject = null;
	// コールバックメソッド
	protected Method mCallBackmethod = null;
	protected Logger mLogger = null; 
	/**
	 * コンストラクタ
	 */
	public InterceptorChainInvokerAccessImpl(){
		super();
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.aspect.InterceptorChainInvokerAccess#setLogger(jp.ossc.nimbus.service.log.Logger)
	 */
	public void setLogger(Logger logger){
		this.mLogger = logger ;		
	} 

	/**
	 * インターセプタリスト返却<br>
	 * @return IntreceptorChainList			インターセプタのリストが含まれるオブジェクト
	 */
	protected IntreceptorChainList getInterceptorChainList(){
		return this.mList;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.aspect.interfaces.InterceptorChainInvoker#invokeChain(java.lang.Object, java.lang.Object)
	 */
	public Object invokeChain(Object callBackObject, Object inputObj)
		throws
			InterceptorException,
			TargetCheckedException,
			TargetUncheckedException {
		if(mList == null){
			if(mLogger != null){
				mLogger.write("AOP__00013");
			}
			throw new InterceptorException("InterceptorChainList is null");
		}
		if(callBackObject == null){
			if(mLogger != null){
				mLogger.write("AOP__00014");
			}
			throw new InterceptorException("callBackObject is null");
		}
		if(mCallBackmethod == null){
			if(mLogger != null){
				mLogger.write("AOP__00015");
			}
			throw new InterceptorException("CallBackmethod is null");
		}
		if(mLogger != null){
			mLogger.write("AOP__00008");
		}
		// コールバックオブジェクトを保持
		mCallBackObject = callBackObject;
		// Iterator作成
		if(mLogger != null){
			mLogger.write("AOP__00009",mList.size());
		}
		mIte = mList.iterator();
		
		// チェイン実行
		Object retObject = null ;
		try{
			retObject = invokeChain(inputObj);
			
		}catch(TargetUncheckedException e){
			if(mLogger != null){
				mLogger.write("AOP__00016",e) ;
			}
			throw e ;
		}catch(TargetCheckedException e){
			if(mLogger != null){
				mLogger.write("AOP__00017",e);
			}	
			throw e ;		
		}catch(InterceptorException e){
			if(mLogger != null){
				mLogger.write("AOP__00020",e);
			}			
			throw e ;		
		}
		
		return retObject;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.aspect.interfaces.InterceptorChain#invokeChain(java.lang.Object)
	 */
	public Object invokeChain(Object inputObj) 
		throws InterceptorException, 
				TargetCheckedException,
				TargetUncheckedException{
		Object retObject = null;
		// インターセプタのイテレータから有無確認
		if(mIte.hasNext()){
			// コンポーネント名(インターセプタ名)を取得
			final ServiceName name = (ServiceName)mIte.next();
			// コンポーネント名に対応するインターセプタを取得
			final Interceptor interceptor = UtilTool.getInterceptor(name);
			// インターセプタインスタンスがnullのとき
			if(interceptor == null){
				// 例外発生
				throw new InterceptorException("interceptor[" + name + "] is null");
			}
			if(mLogger != null){
				mLogger.write("AOP__00010",name);
			}
			try{
				retObject = interceptor.invokeChain(inputObj, this);	
			}catch(TargetUncheckedException e){
				if(mLogger != null){
					mLogger.write("AOP__00016",e) ;
				}
				throw e ;
			}catch(TargetCheckedException e){
				if(mLogger != null){
					mLogger.write("AOP__00017",e);
				}	
				throw e ;		
			}catch(InterceptorException e){
				if(mLogger != null){
					mLogger.write("AOP__00020",e);
				}			
				throw e ;		
			}
			
		}else{
			try{
				if(mLogger != null){
					mLogger.write("AOP__00011",this.mCallBackmethod.getName());
				}
				retObject = mCallBackmethod.invoke(mCallBackObject,
				  new Object[]{inputObj}
				);
				if(mLogger != null){
					mLogger.write("AOP__00012",this.mCallBackmethod.getName());
				}
			}catch(InvocationTargetException e){
				Throwable cause = e.getCause() ;
				if(cause instanceof RuntimeException ){
					if(mLogger != null){
						mLogger.write("AOP__00016",cause);
					}
					throw new TargetUncheckedException(cause) ;			
				}else if(cause instanceof InterceptorException ){
					throw (InterceptorException)cause ;			
				}else if(cause instanceof Exception ){
					if(mLogger != null){
						mLogger.write("AOP__00017",cause);
					}
					throw new TargetCheckedException(cause) ;			
				}else{
					if(mLogger != null){
						mLogger.write("AOP__00018",cause);
					}
					throw new TargetUncheckedException(cause) ;			
				}
			}catch(IllegalArgumentException e) {
				// FrameworkExceptionに内包しスロー
				if(mLogger != null){
					mLogger.write("AOP__00019",e);
				}
				throw new InterceptorException("root callback error",e);
			}catch(IllegalAccessException e) {
				// FrameworkExceptionに内包しスロー
				if(mLogger != null){
					mLogger.write("AOP__00020",e);
				}
				throw new InterceptorException("root callback error",e);
			}catch(Throwable e){
				if(mLogger != null){
					mLogger.write("AOP__00021",e);
				}
				throw new InterceptorException("root callback error",e);
			}
		}
		return retObject;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.aspect.InterceptorChainInvokerAccess#setInterceptorChainList(jp.ossc.nimbus.service.aspect.IntreceptorChainList)
	 */
	public void setInterceptorChainList(IntreceptorChainList interceptorChainList) {
		this.mList = interceptorChainList ;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.aspect.InterceptorChainInvokerAccess#setCallBackmethod(java.lang.reflect.Method)
	 */
	public void setCallBackmethod(Method callBackmethod) {
		this.mCallBackmethod = callBackmethod ;
	}
	/**
	 * コールバック対象のメソッドを返却<br>
	 * インターセプタ管理コンポーネントから呼び出される。
	 * @return Method					コールバック対象のメソッド
	 */
	protected Method getCallBackmethod(){
		return mCallBackmethod;
	}

}
