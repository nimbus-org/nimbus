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
package jp.ossc.nimbus.service.performance;
//インポート
import jp.ossc.nimbus.core.*;

//
/**
 *	スタティスティクス管理クラス。<BR>
 *	スタティスティクスの検索、登録を行う。<BR>
 * @author H.Nakano
 * @version 1.00 
 */
public interface FileReportPerformanceStatisticsServiceMBean extends ServiceBaseMBean{
	/**	キー名ソート						*/		
	static public final int C_NAME = 0;
	static public final String C_NAME_STR = "NAME";
	/**	ベストパフォーマンスソート			*/
	static public final int	C_BEST = 1;
	static public final String C_BEST_STR = "BEST";
	/**	ワーストパフォーマンスソート		*/	
	static public final int C_WORST = 2;
	static public final String C_WORST_STR = "WORST";
	/**	平均パフォーマンスソート			*/	
	static public final int C_AVERAGE = 3;
	static public final String C_AVERAGE_STR = "AVERAGE";
	/**	コール回数ソート					*/	
	static public final int C_COUNT = 4;
	static public final String C_COUNT_STR = "COUNT";
	//
	/**
	 *	パフォーマンスHASHをクリアする。
	 */
	public void clear() ;
	//
	/**
	 * 文字出力メソッド<BR>
	 * 指定のソートキーでソートを行う。<BR>
	 * @param sortKey	ソートキー
	 * @param isUpset		昇順、降順の指定
	 * @return String[] ソート結果
	 */
	public String[] toStringAry (int sortKey,boolean isUpset);
	/**
	 * Method setRecordClassName.
	 * @param className
	 */
	public void setRecordClassName(String className);
	/**
	 * Method getRecordClassName.
	 * @return String
	 */
	public String getRecordClassName() ;
	/**
	 * キュー部品コンポーネント名を設定する.
	 * @param name - キュー部品コンポーネント名
	 */
	public void setQueueServiceName(ServiceName name) ;
	/**
	 * キュー部品コンポーネント名を取得する。
	 * @return ServiceName - キュー部品コンポーネント名
	 */
	public ServiceName getQueueServiceName() ;
	/**
	 * ファイル書き出しコンポーネント名を設定する。
	 * @param name - ファイル書き出しコンポーネント名
	 */
	public void setWriterServiceName(ServiceName name) ;
	/**
	 * ファイル書き出しコンポーネント名を設定する。
	 * @return - ファイル書き出しコンポーネント名
	 */
	public ServiceName getWriterServiceName() ;
	/**
	 * ファイル書き出しレコードファクトリコンポーネント名を設定する。
	 * @param name - ファイル書き出しレコードファクトリコンポーネント名
	 */
	public void setWriteableRecordFactoryServiceName(ServiceName name) ;
	/**
	 * ファイル書き出しレコードファクトリコンポーネント名を取得する。
	 * @return ComponentName - ファイル書き出しレコードファクトリコンポーネント名
	 */
	public ServiceName getWriteableRecordFactoryServiceName() ;
	/**
	 * ファイル書き出しインターバル(秒)を設定する。
	 * @param intervalSec - インターバル(秒)
	 */
	public void setWritableInterval(String intervalSec);
	/**
	 * ファイル書き出しインターバル(秒)を取得する。
	 * @return String - インターバル(秒)
	 */
	public String getWritableInterval();
	/**
	 * ソートキーを設定する。("NAME","BEST","WORST","AVERAGE","COUNT")
	 * @param sortKey - ソートキー
	 */
	public void setSortKey(String sortKey);
	/**
	 * ソートキーを取得する。("NAME","BEST","WORST","AVERAGE","COUNT")
	 * @return ソートキー
	 */
	public String getSortKey();
}
