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
package jp.ossc.nimbus.ioc;

import java.io.Serializable;

/**
 * ファイル操作クラス<p>
 * ファイルのコピーやリネームと言った操作を行う
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class CommandImpl 
	implements Command, Serializable {
	
    private static final long serialVersionUID = -1332971654887985829L;
    
    private Object mInputObj ;
	private Object mOutputObj ;
	private Throwable mException ;
	private String mFlowKey ;
	private int mStatus ;
   /**
	* コンストラクター
	*/
	public CommandImpl() {
		super();
		this.mStatus = C_STATUS_BEFORE ;
	}
	/*
	 * 実行するBeanフローのフローキーを出力する
	 * @return	フローキー
	 */
	public String getFlowKey() {
		return this.mFlowKey;
	}
	/*
	 * 実行するBeanフローのフローキーを設定する
	 * @param flowKey	フローキー
	 */
	public void setFlowKey(String flowKey) {
		this.mFlowKey = flowKey ;
	}


	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.ioc.Command#setInputObject(java.lang.Object)
	 */
	public void setInputObject(Object obj) {
		this.mInputObj = obj ;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.ioc.Command#setOutObject(java.lang.Object)
	 */
	public void setOutObject(Object obj) {
		this.mOutputObj = obj ;
		this.mStatus = C_STATUS_COMPLETE ;

	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.ioc.Command#getOutputObject()
	 */
	public Object getOutputObject() {
		return this.mOutputObj;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.ioc.Command#getInputObject()
	 */
	public Object getInputObject() {
		return this.mInputObj;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.ioc.Command#setException(java.lang.Throwable)
	 */
	public void setException(Throwable e) {
		this.mStatus = C_STATUS_ERROR ;
		this.mException = e ;
	}
    public Throwable getException(){
        return mException;
    }
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.ioc.CommandBase#isCommand()
	 */
	public boolean isCommand() {
		return true;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.ioc.CommandBase#getExceptionCount()
	 */
	public int getExceptionCount() {
		if(this.mException != null){
			return 1;
		}
		return 0;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.ioc.CommandBase#getExceptions()
	 */
	public Throwable[] getExceptions() {
		Throwable[] ret ;
		if(this.mException != null){
			ret = new Throwable[1] ;
			ret[0]= this.mException ;
		}else{
			ret = new Throwable[0] ;
		}
		return ret ;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.ioc.CommandBase#findErrorCommand(java.lang.Throwable)
	 */
	public Command findErrorCommand(Throwable e) {
		Command ret = null ;
		if(this.mException != null){
			if(e.equals(this.mException)){
				ret = this ; 
			}
		}
		return ret;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.ioc.CommandBase#commandSize()
	 */
	public int commandSize() {
		return 1;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.ioc.CommandBase#commandExecuteSize()
	 */
	public int commandExecuteSize() {
		if(this.mStatus == C_STATUS_COMPLETE){
			return 1;
		}else{
			return 0;
		}
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.ioc.CommandBase#getStatus()
	 */
	public int getStatus() {
		return this.mStatus;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.ioc.CommandBase#unitOfWorkSize()
	 */
	public int unitOfWorkSize() {
		return 0;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.ioc.CommandBase#unitOfWorkExecuteSize()
	 */
	public int unitOfWorkExecuteSize() {
		return 0;
	}

}
