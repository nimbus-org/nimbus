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
package jp.ossc.nimbus.service.context;

import java.io.*;

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link FileContextStoreService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see FileContextStoreService
 */
public interface FileContextStoreServiceMBean extends ServiceBaseMBean{
    
    /**
     * 永続化先のルートとなるディレクトリを設定する。<p>
     *
     * @param dir 永続化先のルートとなるディレクトリ
     */
    public void setRootDirectory(File dir);
    
    /**
     * 永続化先のルートとなるディレクトリを取得する。<p>
     *
     * @return 永続化先のルートとなるディレクトリ
     */
    public File getRootDirectory();
    
    /**
     * キー単位での保存/読み込みをサポートするかどうかを設定する。<p>
     * デフォルトは、falseで、サポートしない。<br>
     * キー単位の保存と、全体の保存は、基本的に別々に管理される。ただし、全体保存のファイルが存在し、キー単位の保存ファイルが存在しない場合に、キー単位の読み込みを行った場合、全体保存ファイルからキー単位ファイルへの展開が行われる。<br>
     * つまり、キー単位の保存のみを行い、全体の読み込みをした場合、全体の保存ファイルにはキー単位の保存が反映されないため、同期されない。一方、キー単位の保存の後に、ストアを削除し、全体の保存を行い、キー単位の読み込みを行った場合は、全体保存ファイルからキー単位ファイルへの展開が行われるため、同期される。
     *
     * @param isSupport サポートする場合は、true
     */
    public void setSupportByKey(boolean isSupport);
    
    /**
     * キー単位での保存/読み込みをサポートするかどうかを判定する。<p>
     * デフォルトは、falseで、サポートしない。<br>
     *
     * @param isSupport サポートする場合は、true
     */
    public boolean isSupportByKey();
    
    /**
     * キー単位での保存/読み込みを有効にした場合のキーを管理するファイルのファイル名を設定する。<p>
     * デフォルトは、"keys"。<br>
     *
     * @param name キーを管理するファイルのファイル名
     */
    public void setKeyFileName(String name);
    
    /**
     * キー単位での保存/読み込みを有効にした場合のキーを管理するファイルのファイル名を取得する。<p>
     *
     * @return キーを管理するファイルのファイル名
     */
    public String getKeyFileName();
    
    /**
     * キー単位での保存/読み込みを有効にした場合の値を保存するファイルのディレクトリ名を設定する。<p>
     * デフォルトは、"values"。<br>
     *
     * @param name 値を保存するファイルのディレクトリ名
     */
    public void setValueDirectoryName(String name);
    
    /**
     * キー単位での保存/読み込みを有効にした場合の値を保存するファイルのディレクトリ名を取得する。<p>
     *
     * @return 値を保存するファイルのディレクトリ名
     */
    public String getValueDirectoryName();
    
    /**
     * キー単位での保存/読み込みを有効にした場合の値を保存するファイル名のプレフィクスを設定する。<p>
     * デフォルトは、"val"。<br>
     *
     * @param name 値を保存するファイル名のプレフィクス
     */
    public void setValueFileNamePrefix(String name);
    
    /**
     * キー単位での保存/読み込みを有効にした場合の値を保存するファイル名のプレフィクスを取得する。<p>
     *
     * @return 値を保存するファイル名のプレフィクス
     */
    public String getValueFileNamePrefix();
    
    /**
     * キー単位での保存/読み込みを有効にした場合の値を保存するファイル名のサフィックスを設定する。<p>
     * デフォルトは、null。<br>
     *
     * @param name 値を保存するファイル名のサフィックス
     */
    public void setValueFileNameSuffix(String name);
    
    /**
     * キー単位での保存/読み込みを有効にした場合の値を保存するファイル名のサフィックスを取得する。<p>
     *
     * @return 値を保存するファイル名のサフィックス
     */
    public String getValueFileNameSuffix();
    
    /**
     * キーと値をファイルに直列化/非直列化する際に使用する{@link jp.ossc.nimbus.service.io.Externalizer Externalizer}サービスのサービス名を設定する。<p>
     * 指定しない場合は、{@link jp.ossc.nimbus.service.io.NimbusExternalizerService NimbusExternalizerService}が適用される。<br>
     *
     * @param name Externalizerサービスのサービス名
     */
    public void setExternalizerServiceName(ServiceName name);
    
    /**
     * キーと値をファイルに直列化/非直列化する際に使用する{@link jp.ossc.nimbus.service.io.Externalizer Externalizer}サービスのサービス名を取得する。<p>
     *
     * @return Externalizerサービスのサービス名
     */
    public ServiceName getExternalizerServiceName();
    
    /**
     * {@link ShareContext}に読み込む際に、{@link ShareContext}をロックするかどうかを設定する。<p>
     * デフォルトは、falseでロックしない。<br>
     *
     * @param isLock ロックする場合は、true
     */
    public void setLockOnLoad(boolean isLock);
    
    /**
     * {@link ShareContext}に読み込む際に、{@link ShareContext}をロックするかどうかを判定する。<p>
     *
     * @return trueの場合、ロックする
     */
    public boolean isLockOnLoad();
}
