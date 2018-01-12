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

import jp.ossc.nimbus.lang.*;
import java.util.*;
import java.io.*;
/**
 *  DataInputStepクラス
 *	1個のコマンド入力Stepに対応する。
 *	@author	y-tokuda
 *	@version	1.00 作成：2003/10/29－ y-tokuda<BR>
 *				更新：
 */
public class DataInputStep 
	implements CuiTagDefine{
	//メンバ変数
	/** 名称 */
	private String mMyName;
	/** メニュー表示およびチェックインターフェイス */
	private ArrayList mDisplayList = null;
	/** データチェックオブジェクト */
	private InputChecker mChecker = null;
	/** 遷移先ハッシュ */
	private HashMap mWhereToGoHash = null;
	/** このステップで選択された値 */
	private String mSelectedValue = null;
	/** 次のステップ */
	private String mNextStepName = null;
	/** 終了時表示メッセージ */
	private String mEndMessage = "";
	/**
	 * 
	 * コンストラクタ
	 */
	public DataInputStep(String name){
		mMyName = name;
		mDisplayList = new ArrayList() ;
		mWhereToGoHash = new HashMap() ;
		mEndMessage = "";
	}
	/**
	 * 実行
	 * 次に実行するStepの名称を返す
	 */
	public String invoke() {
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		while(true){
			//選択値を初期化
			mSelectedValue = null;
			//メニュー表示
			for(ListIterator iterator = this.mDisplayList.listIterator();iterator.hasNext();){
				DisplayConstructer ds = (DisplayConstructer)iterator.next() ;
				System.out.println(ds.display());
			}
			//入力受付
			String answer = null;
			try{
				answer = input.readLine();
			}
			catch(IOException e){
				//readLine中にExceptionが発生したらもういちど入力待ちにする。
				continue;
			}
			//中断もしくは最初からやり直しを指示する入力だったら、そのままreturn
			if (answer.equals(REDO) || answer.equals(INTERRUPT)){
				return answer;
			}
			//入力値チェック	
			mSelectedValue = mChecker.check(answer);
			if (mSelectedValue == null){
				//無効な入力。もう一度
				continue;
			}
			String distination = (String)mWhereToGoHash.get(mSelectedValue);
			if(distination != null){
				//選択値に応じたStepに遷移
				return distination;
			}else{
				//次のステップに遷移
				return mNextStepName;
			}
		}
	}
	/**
	 * バッチ用invokeメソッド
	 * 
	 */
	public String invoke(String in){
		while(true){
			//有効な入力値かチェック
			mSelectedValue = mChecker.check(in);
			if (mSelectedValue == null){
				//バッチで無効な入力値であれば、ランタイムエラー
				throw new ServiceException();
			}
			String distination = (String)mWhereToGoHash.get(mSelectedValue);
			if(distination != null){
				//選択値に応じたStepに遷移
				return distination;
			}else{
				//次のステップに遷移
				return mNextStepName;
			}
		}
	}
	
	/**
	 * このステップで選択された値を返す
	 *				
	 */
	public String getSelectedValue(){
		return mSelectedValue;
	}
	/**
	 * Displayオブジェクトを追加する。
	 * @param display
	 */
	public void addDisplay(DisplayConstructer display){
		mDisplayList.add(display);
	}
	/**
	 * 次Stepを設定する。
	 * 入力値をkeyに、mWhereToGoハッシュから値を取得できなかった
	 * とき、この変数に指定されたStepに遷移することになる。
	 */
	public void setNextStepName(String name){
		mNextStepName = name;
	}
	/**
	 * 次Step名を返す。
	 * @return
	 */
	public String getNextStepName(){
		return mNextStepName;
	}
	/**
	 * 遷移先ハッシュを設定する。
	 * @param condition
	 * @param gotoStepName
	 */
	public void addWhereToGo(String condition,String gotoStepName){
		mWhereToGoHash.put(condition,gotoStepName);
	}
	/**
	 * 終了時メッセージを設定する。
	 *	
	 */
	public void setEndMessage(String msg){
		mEndMessage = msg;
	}
	/**
	 * 終了時メッセージを取得する。
	 * @return
	 */
	public String getEndMessage(){
		return mEndMessage;
	}
	/**
	 * 自分の名前を返す
	 * @return
	 */
	public String getName(){
		return mMyName;
	}
	/**
	 * 入力チェックオブジェクトのゲッター
	 */
	public InputChecker getChecker() {
		return mChecker;
	}

	/**
	 * 入力チェックオブジェクトのセッター
	 */
	public void setChecker(InputChecker checker) {
		mChecker = checker;
	}

}
