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

import java.util.ArrayList;
import jp.ossc.nimbus.core.ServiceBaseMBean;
//
/**
 *	スタティスティクス管理クラス。<BR>
 *	スタティスティクスの検索、登録を行う。<BR>
 *	@author 	NRI Hirotaka.Nakano
 *				更新：
 */
public interface CachedPerformanceStatisticsServiceMBean extends ServiceBaseMBean{
	/**	キー名ソート						*/		
	static public final int C_NAME = 0;
	/**	ベストパフォーマンスソート			*/
	static public final int	C_BEST = 1;
	/**	ワーストパフォーマンスソート		*/	
	static public final int C_WORST = 2;
	/**	平均パフォーマンスソート			*/	
	static public final int C_AVERAGE = 3;
	/**	コール回数ソート					*/	
	static public final int C_COUNT = 4;
	//
	/**
	 *	パフォーマンスHASHをクリアする。
	 */
	public void clear() ;
	//
	/**
	 *	文字出力メソッド<BR>
	 *	指定のソートキーでソートを行う。<BR>
	 * @param sortKey	ソートキー
	 * @param isUpset		昇順、降順の指定
	 * @return String[] ソート結果
	 */
	public String[] toStringAry (int sortKey,boolean isUpset);
	/**
	 *	Listデータ取得<BR>
	 *	指定のソートキーでソートを行う。<BR>
	 * @param sortKey	ソートキー
	 * @param isUpset	昇順、降順の指定
	 * @return ArrayList	ソート統計情報(PerformanceRecordの配列)
	 */
	public ArrayList toAry (int sortKey,boolean isUpset);
	/**
	 * Method setRecordClassName.
	 * @param className
	 */
	//
	public void setRecordClassName(String className) ;
	/**
	 * Method getRecordClassName.
	 * @return String
	 */
	public String getRecordClassName() ;
}
