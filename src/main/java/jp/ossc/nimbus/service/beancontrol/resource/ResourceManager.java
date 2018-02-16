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
package jp.ossc.nimbus.service.beancontrol.resource;
//インポート
import jp.ossc.nimbus.core.*;
/**
 * BLフロー内で扱うリソースを管理する。<p>
 * addResource<br>
 * getResource<br>
 * commitResource,rollBackResource<br>
 * commitAllResources,rollbbackAllResources<br>
 * の順番で使用する。 
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public interface ResourceManager {
	/**
	 * ResourceManagerが管理するリソースを開放する。
	 */
	public void terminateResourceManager() ;
	/**
	 * リソースを確保する
	 * @param key			リソース名称キー
	 * @param resourceKey	リソース特定キー
	 * @param serviceName	リソース提供サービス名
	 * @param isTrnControl	トランザクションコントロールするか
	 * @param isTrnClose	開放時にクローズするか
	 */
	public void addResource(String key,
							String resourceKey,
							ServiceName serviceName,
							boolean isTrnControl,
							boolean isTrnClose) ;
	/**
	 * リソースをコミットする
	 * @param key		リソース名称キー
	 * @param isClose	リソースコミット後クローズするか
	 */
	public void commitResource(String key,boolean isClose) ;		
	/**
	 * リソースをロールバックする
	 * @param key		リソース名称キー
	 * @param isClose	リソースコミット後クローズするか
	 */
	public void rollBackResource(String key,boolean isClose) ;		
	/**
	 * すべてのリソースをコミットする
	 */
	public void commitAllResources() ;
	/**
	 * すべてのリソースをロールバックする
	 */
	public void rollbbackAllResources() ;
	/**
	 * リソースを出力する
	 * @param key	リソース名称
	 * @return	リソースオブジェクト
	 */
	public Object getResource(String key) ;
}
