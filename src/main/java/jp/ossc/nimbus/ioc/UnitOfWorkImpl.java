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
package jp.ossc.nimbus.ioc;
//インポート
import java.util.*;
/**
 * ファイル操作クラス<p>
 * ファイルのコピーやリネームと言った操作を行う
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 * @see CommandBase
 */
public class UnitOfWorkImpl 
	implements UnitOfWork,java.io.Serializable {
	
    private static final long serialVersionUID = -870753524503723215L;
    
    private ArrayList mCommandAry ;
	/**
	* コンストラクター
	*/
	public UnitOfWorkImpl() {
		super();
		mCommandAry = new ArrayList() ;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.ioc.CommandBase#isCommand()
	 */
	public boolean isCommand() {
		return false;
	}
	/*
	 * コマンド配列のサイズを出力する。
	 * @return　コマンドサイズ
	 */
	public int size(){
		return this.mCommandAry.size() ;
	}
	/*
	 * コマンド配列に格納されたオブジェクトを出力する。
	 * @param index　配列番号
	 * @return　CommandBaseインターフェイス
	 * @see CommandBase
	 */
	public CommandBase getCommand(int index){
		return (CommandBase)this.mCommandAry.get(index) ;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.ioc.CommandBase#getExceptionCount()
	 */
	public int getExceptionCount() {
		int ret = 0;
		for(int rcnt=0;rcnt<this.size();rcnt++){
			CommandBase tmp = this.getCommand(rcnt) ;
            if(tmp == null){
                continue;
            }
			if(tmp.isCommand()){
				ret += tmp.getExceptionCount() ;
			// リカーシブコール
			}else{
				UnitOfWorkImpl uow = (UnitOfWorkImpl)tmp ;
				for(int ccnt = 0;ccnt<uow.size();ccnt++){
					CommandBase tmp1 = uow.getCommand(ccnt) ;
                    if(tmp1 == null){
                        continue;
                    }
					ret += tmp1.getExceptionCount();
				}
			}
		}
		return ret ;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.ioc.CommandBase#getExceptions()
	 */
	public Throwable[] getExceptions() {
		ArrayList list = new ArrayList() ;
		for(int rcnt=0;rcnt<this.size();rcnt++){
			CommandBase tmp = this.getCommand(rcnt) ;
            if(tmp == null){
                continue;
            }
			if(tmp.isCommand()){
				Throwable[] exp = tmp.getExceptions() ;
				for(int ecnt= 0; ecnt<exp.length;ecnt++){
					list.add(exp[ecnt]) ;
				}
			// リカーシブコール
			}else{
				UnitOfWorkImpl uow = (UnitOfWorkImpl)tmp ;
				for(int ccnt = 0;ccnt<uow.size();ccnt++){
					CommandBase tmp1 = uow.getCommand(ccnt) ;
                    if(tmp1 == null){
                        continue;
                    }
					Throwable[] exp = tmp1.getExceptions() ;
					for(int ecnt= 0; ecnt<exp.length;ecnt++){
						list.add(exp[ecnt]) ;
					}
				}
			}
		}
		Throwable[] ret = new Throwable[list.size()] ;
		for(int lcnt=0;lcnt<list.size();lcnt++){
			ret[lcnt] = (Throwable)list.get(lcnt) ; 
		}
		return ret ;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.ioc.CommandBase#findErrorCommand(java.lang.Exception)
	 */
	public Command findErrorCommand(Throwable e) {
		Command ret = null ;
		for(int rcnt= 0;rcnt<this.size();rcnt++){
			CommandBase tmp = this.getCommand(rcnt) ;
            if(tmp == null){
                continue;
            }
			if(tmp.isCommand()){
				ret = tmp.findErrorCommand(e) ;
				if(ret != null){
					break ;		
				}
			// リカーシブコール
			}else{
				UnitOfWorkImpl uow = (UnitOfWorkImpl)tmp ;
				for(int ccnt = 0;ccnt<uow.size();ccnt++){
					ret = tmp.findErrorCommand(e) ;
					if(ret != null){
						break ;		
					}
				}
				if(ret != null){
					break ;		
				}
			}
		}
		return ret ;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.ioc.CommandBase#commandSize()
	 */
	public int commandSize() {
		int ret = 0;
		for(int rcnt= 0;rcnt<this.size();rcnt++){
			CommandBase tmp = this.getCommand(rcnt) ;
            if(tmp == null){
                continue;
            }
			if(tmp.isCommand()){
				ret += tmp.commandSize() ;
			// リカーシブコール
			}else{
				UnitOfWorkImpl uow = (UnitOfWorkImpl)tmp ;
				for(int ccnt = 0;ccnt<uow.size();ccnt++){
					ret += tmp.commandSize() ;
				}
			}			
		}
		return ret ;		
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.ioc.CommandBase#commandExecuteSize()
	 */
	public int commandExecuteSize() {
		int ret = 0;
		for(int rcnt= 0;rcnt<this.size();rcnt++){
			CommandBase tmp = this.getCommand(rcnt) ;
            if(tmp == null){
                continue;
            }
			if(tmp.isCommand()){
				ret += tmp.commandExecuteSize();
			// リカーシブコール
			}else{
				UnitOfWorkImpl uow = (UnitOfWorkImpl)tmp ;
				for(int ccnt = 0;ccnt<uow.size();ccnt++){
					ret += tmp.commandExecuteSize() ;
				}
			}			
		}
		return ret ;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.ioc.CommandBase#getStatus()
	 */
	public int getStatus() {
		int status = C_STATUS_BEFORE  ;
		for(int rcnt= 0;rcnt<this.size();rcnt++){
			CommandBase tmp = this.getCommand(rcnt) ;
            if(tmp == null){
                continue;
            }
			if(tmp.isCommand()){
				status = tmp.getStatus() ;
			// リカーシブコール
			}else{
				UnitOfWorkImpl uow = (UnitOfWorkImpl)tmp ;
				for(int ccnt = 0;ccnt<uow.size();ccnt++){
					status = uow.getStatus() ;	
					if(status == C_STATUS_BEFORE ||
						status == C_STATUS_ERROR ){
						break ;		
					}
				}
			}			
			if(status == C_STATUS_BEFORE ||
				status == C_STATUS_ERROR ){
				break ;		
			}

		}
		return status ;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.ioc.CommandBase#unitOfWorkSize()
	 */
	public int unitOfWorkSize() {
		int ret = 1;
		for(int rcnt= 0;rcnt<this.size();rcnt++){
			CommandBase tmp = this.getCommand(rcnt) ;
            if(tmp == null){
                continue;
            }
			// リカーシブコール
			if(!tmp.isCommand()){
				UnitOfWorkImpl uow = (UnitOfWorkImpl)tmp ;
				for(int ccnt = 0;ccnt<uow.size();ccnt++){
					ret += tmp.unitOfWorkSize() ;
				}
			}			
		}
		return ret ;
	}
	/*
	 * ユニットオブワークを追加する
	 * @param uow　ユニットオブワークオブジェクト
	 */
	public void addUnitOfWork(UnitOfWork uow){
		mCommandAry.add(uow) ;
	}
	/*
	 * コマンドを追加する
	 * @param cmd　コマンドオブジェクト　
	 */
	public void addCommand(Command cmd){
		this.mCommandAry.add(cmd);		
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.ioc.CommandBase#unitOfWorkExecuteSize()
	 */
	public int unitOfWorkExecuteSize() {
		int ret = 0;
		for(int rcnt= 0;rcnt<this.size();rcnt++){
			CommandBase tmp = this.getCommand(rcnt) ;
            if(tmp == null){
                continue;
            }
			// リカーシブコール
			if(!tmp.isCommand()){
				UnitOfWorkImpl uow = (UnitOfWorkImpl)tmp ;
				for(int ccnt = 0;ccnt<uow.size();ccnt++){
					ret += tmp.unitOfWorkSize() ;
				}
			}			
		}
		return ret ;
	}

}
