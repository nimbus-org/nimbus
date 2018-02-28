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
package jp.ossc.nimbus.service.debug;
//インポート
import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;
/**
 * デバッグサービスMBeanインターフェイス
 * @version $Name:  $
 * @author K.Nagai
 * @since 1.0
 */
public interface DebugServiceMBean extends ServiceBaseMBean {
    /**デバッグレベル：デバッグ*/
    public final static int DEBUG_LEVEL_DEBUG = 0;
    /**デバッグレベル：情報*/
    public final static int DEBUG_LEVEL_INFO  = 20;
    /**デバッグレベル：警告*/
    public final static int DEBUG_LEVEL_WARN  = 40;
    /**デバッグレベル：エラー*/
    public final static int DEBUG_LEVEL_ERROR = 60;
    /**デバッグレベル：致命的エラー*/
    public final static int DEBUG_LEVEL_FATALERROR = 80;
    /**デバッグレベル：致命的エラー*/
    public final static int DEBUG_LEVEL_NOOUTPUT = 81;
    //ログのキー
    //例外つき
    public final static String DEBUG_DEBUG_WRITE_KEY1  ="DEBUG00001";    
    //例外なし
    public final static String DEBUG_DEBUG_WRITE_KEY2  ="DEBUG00002";    
    //DUMP
    public final static String DEBUG_DEBUG_DUMP_KEY1  ="DEBUG00003";    
    //DUMP []
    public final static String DEBUG_DEBUG_DUMP_KEY2  ="DEBUG00004";    
    //message + DUMP
    public final static String DEBUG_DEBUG_MSG_DUMP_KEY1  ="DEBUG00005";    
    //message + DUMP []
    public final static String DEBUG_DEBUG_MSG_DUMP_KEY2  ="DEBUG00006";    
	//ネストレベル異常発見時
	public static final String DEBUG_NESTLEVEL_ERR_KEY = "DEBUG00007";    /**
     * getDebugLevel<p>
     * デバッグレベルを取得
     * @return デバッグレベル
     */
    public int getDebugLevel();
    /**
     * setDebugLevel<p>
     * デバッグレベルを設定。設定できるレベルに関してはこのインターフェイスの定数値を参照。
     * @param level デバッグレベル
     */
    public void setDebugLevel(int level);
    /**
     * setLoggerServiceName<p>
     * ログサービス名を取得する。
     * @return デバッグレベル
     */
    public ServiceName getLogServiceName();
    /**
     * setLoggerServiceName<p>
     * ログサービス名を設定する。
     * @param svn ログサービス名
     */
    public void setLogServiceName(ServiceName svn);
    /**
     * setNestedLevel
     * @param level コールスタックの何番目を関数スタックとして
     * 表示するか
     */
    public void setNestedLevel(int level);
    /**
     * setEditorFinderServiceName
     * @param name エディタファインダ名
     */
    public void setEditorFinderServiceName(ServiceName name);
    /**
     * getEditorFinderServiceName
     * @return エディタファインダ名
     * 表示するか
     */
    public ServiceName getEditorFinderServiceName();
    /**
     * getSeparator
     * @return セパレーター
     * 表示するか
     */
	public String getSeparator() ;
    /**
     * setSeparator
     * @param separator セパレーター
     * 表示するか
     */
	public void setSeparator(String separator);
}
