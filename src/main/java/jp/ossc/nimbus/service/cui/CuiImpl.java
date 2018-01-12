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
package jp.ossc.nimbus.service.cui;

import java.util.*;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.lang.*;

/**
 * @author y-tokuda
 * CuiOperatorインターフェイス実装クラス
 * CuiOperatorインターフェイスはCuiインターフェイスを継承していて、
 * アプリ側には、Cuiインターフェイスとして利用させる。
 * CuiOperatorインターフェイスは、ファクトリー用インターフェイス。
 */
public class CuiImpl extends ServiceBase implements ServiceBaseMBean,CuiOperator,CuiTagDefine{
	
    private static final long serialVersionUID = 2060576109868585440L;
    
    //メンバ変数
	/** Step名とDataInputStepオブジェクトを関連付けるハッシュ */
	private HashMap mStepHash = null;
	/** Stepの順番を保持するArrayList */
	private ArrayList mStepArrayList = null;
	/** 入力結果を保持する Hash */
	private HashMap mResultHash = null;
	/**
	 * 入力結果ハッシュ取得
	 */
	public HashMap getResult(){
		return mResultHash;
	}
	 
	/**
	 * コンストラクタ
	 *
	 */
	public CuiImpl(){
		//初期化する。
		mStepHash = new HashMap();
		mStepArrayList = new ArrayList();
		mResultHash = new HashMap();
	}
	/**
	 * 開始
	 */
	public void startService(){
		;
	}
	/**
	 * コマンド入力受付メソッド
	 *
	 */
	public void invoke(){
		//入力結果を保持するハッシュのクリア
		mResultHash.clear();
		//ArrayListの最初のDataInputStepを起動する。
		DataInputStep theFirst = (DataInputStep)mStepArrayList.get(0);
		String key = null;
		String nextStepKey = null;
		key = theFirst.invoke();
		mResultHash.put(theFirst.getName(),theFirst.getSelectedValue());
		while(true){
			if(key.equals(INTERRUPT)){
				//中断
				mResultHash.clear();
				break;
			}
			if(key.equals(REDO)){
				//最初からやり直し
				mResultHash.clear();
				key = theFirst.invoke();
				mResultHash.put(theFirst.getName(),theFirst.getSelectedValue());
				continue;
			}
			DataInputStep Step = (DataInputStep)mStepHash.get(key);
			if(Step == null){
				//May be Invalid Definition
				throw new ServiceException("CUIFACTORYSERVICE040",key + "is not valid step name.");
			}
			nextStepKey = Step.invoke();
			//ハッシュに選択された値を格納
			mResultHash.put(key,Step.getSelectedValue());
			key = nextStepKey;
			if(nextStepKey.equals(END)){
				System.out.println(Step.getEndMessage());
				break;
			}

		}
	}
	/**
	 * バッチ処理用Invoke
	 *
	 */
	public void invoke(ArrayList list){
		//入力結果を保持するハッシュのクリア
		mResultHash.clear();
		//最初のDataInputStepを取る
		DataInputStep theFirst = (DataInputStep)mStepArrayList.get(0);
		mResultHash.put(theFirst.getName(),list.get(0));
		//最初のDataInputStepに、与えられた入力値を入れ、次のDataInputStepを
		//取得するキーを得る。
		String key = theFirst.invoke((String)list.get(0));
		for(int rCnt=1;rCnt<mStepArrayList.size();rCnt++){
			DataInputStep step = (DataInputStep)mStepHash.get(key);
			key = step.invoke((String)list.get(rCnt));
			mResultHash.put(step.getName(),step.getSelectedValue());
			if(key.equals(END)){
				break;
			}
		}		
	}
	/**
	 * DataInputStepを追加する。
	 */
	public void addStep(String key,DataInputStep step){
		mStepHash.put(key,step);
		mStepArrayList.add(step);
	}
	
}
