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
package jp.ossc.nimbus.service.performance;

import java.text.SimpleDateFormat;
import java.util.Date;
//
/**
 *	各パフォーマンスの登録、出力を行う。
 *	@author 	NRI Hirotaka.Nakano
 *				更新：
 */
public class PerformanceRecordImpl implements PerformanceRecord,
												PerformanceRecordOperator {
	//##	メンバー変数宣言	##
	/** リソースＩＤ					*/	protected String	mResourceId;
	/** 最終実行時間					*/	protected Date	mLastProcTime;
	/** ベスト処理時刻					*/	protected Date	mBestTime;
	/** ワースト処理時刻					*/	protected Date	mWorstTime;
	/** 実行回数						*/	protected long	mProcTimes;
	/** ベストパフォーマンス				*/	protected long	mBestPerformance;
	/** ワーストパフォーマンス			*/	protected long	mWorstPerformance;
	/** アベレージパフォーマンス			*/	protected long 	mAveragePerformance;
	/** ログ配列						*/	protected String[] logMsg = new String[2];
	/** 総処理時間 						*/	protected long mTotalTime;
	//
	/**
	 * コンストラクタ。<BR>
	 * 各メンバー変数を初期化する。<BR>
	 * リソースキーを引数から設定する。
	 */
	public PerformanceRecordImpl () {
		/** 各メンバー変数の初期化を行う	*/
		this.mLastProcTime = null;
		this.mProcTimes = 0;
		this.mBestPerformance = 0;
		this.mWorstPerformance = 0;
		this.mBestTime = null;
		this.mWorstTime = null;
		this.mAveragePerformance = 0;
		this.mTotalTime = 0;
	}

	//
	/**
	 *	パフォーマンスアップメソッド<BR>
	 *	コール回数をＵＰする。<BR>
	 *	ベストパフォーマンスとの比較、登録処理を行う。<BR>
	 *	ワーストパフォーマンスとの比較、登録処理を行う。<BR>
	 *	最終コール日時を登録する。
	 * @param msec パフォーマンスタイム
	 */
	public void entry (long msec){
		this.mLastProcTime = new Date();
		if(this.mProcTimes == 0){
			this.mBestPerformance = msec;
			this.mWorstPerformance = msec;
			this.mBestTime = this.mLastProcTime;
			this.mWorstTime = this.mLastProcTime;	
		}
		else{
			if (msec < this.mBestPerformance){
				this.mBestPerformance = msec;
				this.mBestTime = this.mLastProcTime;
			}
			if (msec > this.mWorstPerformance){
				this.mWorstPerformance = msec;
				this.mWorstTime = this.mLastProcTime;
			}
		}
		this.mTotalTime = this.mTotalTime + msec;
		this.mProcTimes++;
		this.mAveragePerformance = mTotalTime/this.mProcTimes;
	}
	//
	/**
	 *	文字列を作成する。
	 * @return String パフォーマンス情報
	 */
	public String toString() {
		StringBuffer retStr = new StringBuffer();
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss:SS");
		String LastProcTime = formatter.format(mLastProcTime);
		String WorstTime = formatter.format(mWorstTime);
		String BestTime = formatter.format(mBestTime);
		retStr.append("[").append(mResourceId).append("]:");
		retStr.append("[ProcTimes=").append(mProcTimes).append("]:");
		retStr.append("[LastProcTime=").append(LastProcTime).append("]:");
		retStr.append("[BestPerformance=").append(mBestPerformance).append(",").append(BestTime).append("]:");
		retStr.append("[WorstPerformance=").append(mWorstPerformance).append(",").append(WorstTime).append("]:");
		retStr.append("[AveragePerformance=").append(mAveragePerformance).append("]");
		return retStr.toString();
	}
	//
	/**
	 *	リソースＩＤを出力する。
	 * @param id String リソースID
	 */
	public void setResourceId(String id) {
		this.mResourceId = id ;
	}
	//
	/**
	 *	リソースＩＤを出力する。
	 * @return String リソースID
	 */
	public String getResourceId() {
		return this.mResourceId;
	}
	//
	/**
	 *	呼び出し回数を出力する。
	 *	@return long 呼び出し回数
	 */
	public long getCallTime() {
		return this.mProcTimes;
	}
	//
	/**
	 *	最終コール日時を出力する。
	 * @return Date 最終コール日時
	 */
	public Date getLastCallTime() {
		return this.mLastProcTime;
	}
	//
	/**
	 *	ベストパフォーマンスを出力する。
	 * @return long ベストパフォーマンス
	 */
	public long getBestPerformance() {
		return this.mBestPerformance;
	}
	//
	/**
	 *	ベストパフォーマンス日時を出力する。
	 * @return Date ベストパフォーマンス日時
	 */
	public Date getBestPerformanceTime() {
		return this.mBestTime;
	}
	//
	/**
	 *	ワーストパフォーマンスを出力する。
	 * @return long ワーストパフォーマンス
	 */
	public long getWorstPerformance() {
		return this.mWorstPerformance;
	}
	//
	/**
	 *	ワーストパフォーマンス日時を出力する。
	 * @return Date ワーストパフォーマンス日時
	 */
	public Date getWorstPerformanceTime() {
		return this.mWorstTime;
	}
	//
	/**
	 *	アベレージパフォーマンスを出力する。
	 * @return Date ワーストパフォーマンス日時
	 */
	public long getAveragePerformance() {
		return this.mAveragePerformance;
	}
	//
}
