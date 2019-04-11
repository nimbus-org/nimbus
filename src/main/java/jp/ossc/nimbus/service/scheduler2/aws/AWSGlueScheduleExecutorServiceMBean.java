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
package jp.ossc.nimbus.service.scheduler2.aws;

/**
 * {@link AWSGlueScheduleExecutorService}のMBeanインタフェース。<p>
 * 
 * @author M.Ishida
 */
public interface AWSGlueScheduleExecutorServiceMBean extends AWSWebServiceScheduleExecutorServiceMBean {
    
    /**
     * デフォルトのスケジュール実行種別。<p>
     */
    public static final String DEFAULT_EXECUTOR_TYPE = "GLUE";

    /**
     * デフォルトのCrawlerのReady状態ステータス文字列。<p>
     */
    public static final String DEFAULT_CRAWLER_READY_STR = "READY";
    
    /**
     * デフォルトのCrawlerのRunning状態ステータス文字列。<p>
     */
    public static final String DEFAULT_CRAWLER_RUNNING_STR = "RUNNING";
    
    /**
     * デフォルトのJobのSucceeded状態ステータス文字列。<p>
     */
    public static final String DEFAULT_JOB_SUCCEEDED_STR = "SUCCEEDED";
    
    /**
     * デフォルトのJobのStoped状態ステータス文字列。<p>
     */
    public static final String DEFAULT_JOB_STOPPED_STR = "STOPPED";
    
    /**
     * デフォルトのJobのRunning状態ステータス文字列。<p>
     */
    public static final String DEFAULT_JOB_RUNNING_STR = "RUNNING";
    
    /**
     * 終了待ちの確認を行う間隔[s]を設定する。<p>
     * デフォルトは、1[s]。<br>
     *
     * @param interval 終了待ちの確認を行う間隔[s]
     */
    public void setWaitPollingInterval(int interval);
    
    /**
     * 終了待ちの確認を行う間隔[s]を取得する。<p>
     *
     * @return 終了待ちの確認を行う間隔[s]
     */
    public int getWaitPollingInterval();
    
    /**
     * CrawlerのReady状態ステータス文字列を取得する。<p>
     * 
     * @return CrawlerのReady状態ステータス文字列
     */
    public String getCrawlerReadyString();
    
    /**
     * CrawlerのReady状態ステータス文字列を設定する。<p>
     * 
     * @param str CrawlerのReady状態ステータス文字列
     */
    public void setCrawlerReadyString(String str);
    
    /**
     * CrawlerのRunning状態ステータス文字列を取得する。<p>
     * 
     * @return CrawlerのRunning状態ステータス文字列
     */
    public String getCrawlerRunningString();
    
    /**
     * CrawlerのRunning状態ステータス文字列を設定する。<p>
     * 
     * @param str CrawlerのRunning状態ステータス文字列
     */
    public void setCrawlerRunningString(String str);
    
    /**
     * JobのSucceeded状態ステータス文字列を取得する。<p>
     * 
     * @return JobのSucceeded状態ステータス文字列
     */
    public String getJobSucceededString();
    
    /**
     * JobのSucceeded状態ステータス文字列を設定する。<p>
     * 
     * @param str JobのSucceeded状態ステータス文字列
     */
    public void setJobSucceededString(String str);
    
    /**
     * JobのStopped状態ステータス文字列を取得する。<p>
     * 
     * @return JobのStopped状態ステータス文字列
     */
    public String getJobStoppedString();
    
    /**
     * JobのStopped状態ステータス文字列を設定する。<p>
     * 
     * @param str JobのStopped状態ステータス文字列
     */
    public void setJobStoppedString(String str);
    
    /**
     * JobのRunning状態ステータス文字列を取得する。<p>
     * 
     * @return JobのRunning状態ステータス文字列
     */
    public String getJobRunningString();
    
    /**
     * JobのRunning状態ステータス文字列を設定する。<p>
     * 
     * @param str JobのRunning状態ステータス文字列
     */
    public void setJobRunningString(String str);
    
}