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
package jp.ossc.nimbus.service.properties;
import jp.ossc.nimbus.core.* ;
//
/**
 *	プロパティオブジェクトをキャシュするファクトリ管理インターフェイス。
 *	@author	Hirotaka.Nakano
 *	@version	1.00 作成：2003.01.01 － H.Nakano<BR>
 *				更新：
 */
public interface ResourceBundlePropertiesFactoryServiceMBean extends ServiceBaseMBean {
	//
	static final public String TIME_FORMAT = "yyyy.MM.dd hh:mm:ss" ; //$NON-NLS-1$
	//
	/**
	 *	プロパティのクラスパスを設定する<BR>
	 *	@param	classPath 指定されたデフォルト値を持つ空のプロパティリストを作成します。
	 */
	public void setClassPath(String classPath) ;
	/**
	 * プロパティのクラスパスを出力する。
	 * @return String
	 */
	public String getClassPath() ;
	//
	/**
	 *	初期化処理<BR>
	 *	@param	time 指定されたデフォルト値を持つ空のプロパティリストを作成します。
	 */
	public void setRefreshPlanTime(String time) ;
	//
	/**
	 *	初期化処理<BR>
	 */
	public void refreshNow();
	//
	/**
	 *	初期化処理<BR>
	 *	@return 指定されたデフォルト値を持つ空のプロパティリストを作成します。
	 */
	public String getRefreshedTime();
	//
	/**
	 *	初期化処理<BR>
	 *	@return 指定されたデフォルト値を持つ空のプロパティリストを作成します。
	 */
	public String getRefreshPlanTime();
	/**
	 * Method setEncode.
	 * @param encode
	 */
	public void setEncode(String encode);
	/**
	 * Method getEncode.
	 * @return String
	 */
	public String getEncode();
}
