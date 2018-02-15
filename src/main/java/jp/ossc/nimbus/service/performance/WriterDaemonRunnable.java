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
// インポート
import java.util.*;
import jp.ossc.nimbus.daemon.*;
import jp.ossc.nimbus.service.writer.*;
/**
 * ファイル書き出し監視デーモン。<br>
 * 指定されたインターバル間隔で{@link FileReportPerformanceStatisticsService}を監視する。<br>
 * パフォーマンス管理HASHの内容を定期的にファイル出力する。
 * @author K.Nakamura
 * @version 1.00 作成：2004.02.16 - K.Nakamura<BR>
 */
public class WriterDaemonRunnable implements DaemonRunnable{
	protected FileReportPerformanceStatisticsService mCallBack = null;
	/**
	 * コンストラクタ
	 */
	public WriterDaemonRunnable(FileReportPerformanceStatisticsService inObj){
		this.mCallBack = inObj;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.daemon.DaemonRunnable#onStop()
	 */
	public boolean onStop(){
		return true;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.daemon.DaemonRunnable#onSuspend()
	 */
	public boolean onSuspend(){
		return true;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.daemon.DaemonRunnable#onResume()
	 */
	public boolean onResume(){
		return true;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.daemon.DaemonRunnable#provide(jp.ossc.nimbus.daemon.DaemonControl)
	 */
	public Object provide(DaemonControl ctrl) throws Exception{
		final long tn = Long.parseLong(mCallBack.getWritableInterval()) * 1000;
		Thread.sleep(tn);
		return new Object();
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.daemon.DaemonRunnable#consume(java.lang.Object, jp.ossc.nimbus.daemon.DaemonControl)
	 */
	public void consume(Object paramObj, DaemonControl ctrl) throws Exception{
		int sort = FileReportPerformanceStatisticsServiceMBean.C_NAME;
		final String sortkey = mCallBack.getSortKey();
		if(FileReportPerformanceStatisticsServiceMBean.C_NAME_STR.equalsIgnoreCase(sortkey)){
			sort = FileReportPerformanceStatisticsServiceMBean.C_NAME;
		}else if(FileReportPerformanceStatisticsServiceMBean.C_BEST_STR.equalsIgnoreCase(sortkey)){
			sort = FileReportPerformanceStatisticsServiceMBean.C_BEST;
		}else if(FileReportPerformanceStatisticsServiceMBean.C_WORST_STR.equalsIgnoreCase(sortkey)){
			sort = FileReportPerformanceStatisticsServiceMBean.C_WORST;
		}else if(FileReportPerformanceStatisticsServiceMBean.C_AVERAGE_STR.equalsIgnoreCase(sortkey)){
			sort = FileReportPerformanceStatisticsServiceMBean.C_AVERAGE;
		}else if(FileReportPerformanceStatisticsServiceMBean.C_COUNT_STR.equalsIgnoreCase(sortkey)){
			sort = FileReportPerformanceStatisticsServiceMBean.C_COUNT;
		}
		final String[] performance = mCallBack.toStringAry(sort,true);
		for(int i = 0; i < performance.length; i++){
			final Map elements = new HashMap();
			elements.put("", performance[i]);
			// ファイル書き出し
			WritableRecord rec = mCallBack.mWritableRecFac.createRecord(elements); 
			mCallBack.mWriter.write(rec);
		}
	}
	/**
	 *	スレッドの後処理を行うインターフェイス。
	 * @see jp.ossc.nimbus.daemon.DaemonRunnable#garbage() 
	 */
	public void garbage(){
	}
	/**
	 * スタート処理。
	 * @return boolean true - スタート承認、false - スタート非承認
	 */
	public boolean onStart(){
		return true;
	}
}
