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
package jp.ossc.nimbus.ioc.ejb;

import jp.ossc.nimbus.ioc.Command;
import jp.ossc.nimbus.service.aspect.interfaces.InterceptorChainInvokerFactory;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvoker;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory;
import jp.ossc.nimbus.service.log.Logger;
import jp.ossc.nimbus.service.aop.InterceptorChainFactory;
import jp.ossc.nimbus.service.aop.InvocationContext;

/**
 * SLSBCommandのビーンフロー呼び出しロジック<p>
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public abstract class BeanFlowClient {
	/** ログサービス */
	private Logger mLogger = null;
	/** インターセプター実行クラスファクトリー */
	private InterceptorChainInvokerFactory mIciFactory = null;
	private InterceptorChainFactory mIcFactory = null;
	/** Beanフローのファクトリー */
	private BeanFlowInvokerFactory mBfFactory = null ;
	/**
	 * @return bfFactory を戻します。
	 */
	protected BeanFlowInvokerFactory getBfFactory() {
		return mBfFactory;
	}
	/**
	 * @return icFactory を戻します。
	 */
	protected InterceptorChainInvokerFactory getIciFactory() {
		return mIciFactory;
	}
	protected InterceptorChainFactory getIcFactory() {
		return mIcFactory;
	}
	/**
	 * @return logger を戻します。
	 */
	protected Logger getLogger() {
		return mLogger;
	}
	/** コンテキスト */
	//private jp.ossc.nimbus.service.context.Context mContext ;
	//protected static final String C_SAVE_FLOW_KEY = "key";

	/**
	 * 
	 */
	public void init(Logger logger,
						InterceptorChainInvokerFactory iciFactory,
						InterceptorChainFactory icFactory,
						BeanFlowInvokerFactory  bfFactory) {
		mLogger = logger ;
		mIciFactory = iciFactory ;
		mIcFactory = icFactory ;
		mBfFactory = bfFactory ;
	}
	/**
	 * BeanFlowを実行する。
	 * @param input 入力オブジェクト
	 * @return 出力オブジェクト
	 */
	public Object invokeBeanFlow(Command input) throws Exception{
		if(mLogger != null){
			mLogger.write("IOC__00013",input.getFlowKey());
		}
		
		BeanFlowInvoker flow = this.mBfFactory.createFlow(input.getFlowKey()) ;
		if(mLogger != null){
			if(flow == null){
				mLogger.write("IOC__00014");
			}
		}
		return flow.invokeFlow(input.getInputObject()) ;
	}

	protected abstract InvocationContext createInvocationContext(Object input);

}
