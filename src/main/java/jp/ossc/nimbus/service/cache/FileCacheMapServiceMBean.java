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
package jp.ossc.nimbus.service.cache;

import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link FileCacheMapService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see FileCacheMapService
 */
public interface FileCacheMapServiceMBean extends AbstractCacheMapServiceMBean{
    
    /**
     * キャッシュしたオブジェクトをシリアライズしてファイルとして出力する際のファイル名のデフォルトサフィックス。<p>
     */
    public static final String DEFAULT_SUFFIX = ".obj";
    
    /**
     * キャッシュしたオブジェクトをシリアライズしてファイルとして出力する際の出力先ディレクトリを設定する。<p>
     * 出力先ディレクトリが指定されていない場合は、JVMのテンポラリディレクトリを使用する。但し、{@link #setFileShared(boolean)}でtrueが設定されている場合は、出力先ディレクトリを必ず指定しなければならない。<br>
     *
     * @param path 出力ディレクトリパス
     * @exception IllegalArgumentException 指定されたパスのディレクトリが存在しない場合
     */
    public void setOutputDirectory(String path)
     throws IllegalArgumentException;
    
    /**
     * キャッシュしたオブジェクトをシリアライズしてファイルとして出力する際の出力先ディレクトリを取得する。<p>
     *
     * @return 出力ディレクトリパス
     */
    public String getOutputDirectory();
    
    /**
     * キャッシュしたオブジェクトをシリアライズしてファイルとして出力したキャッシュファイルを他の{@link FileCacheMapService}と共有するかどうかを設定する。<p>
     * trueを指定すると、キャッシュファイルを共有する。そのため、他のサービスによって、キャッシュファイルが削除されたり、追加されたりするので、キャッシュを参照する都度、キャッシュの最新化を行う。<br>
     *
     * @param isShared キャッシュファイルを共有する場合true
     */
    public void setFileShared(boolean isShared);
    
    /**
     * キャッシュしたオブジェクトをシリアライズしてファイルとして出力したキャッシュファイルを他の{@link FileCacheMapService}と共有するかどうかを判定する。<p>
     *
     * @return キャッシュファイルを共有する場合true
     */
    public boolean isFileShared();
    
    /**
     * キャッシュしたオブジェクトをシリアライズしてファイルとして出力する際のファイル名のプレフィクスを設定する。<p>
     * この出力ファイルプレフィクスが指定されていない場合は、キャッシュするオブジェクトのtoString()が使用される。<br>
     *
     * @param prefix 出力ファイルプレフィクス
     */
    public void setOutputPrefix(String prefix);
    
    /**
     * キャッシュしたオブジェクトをシリアライズしてファイルとして出力する際のファイル名のプレフィクスを取得する。<p>
     *
     * @return 出力ファイルプレフィクス
     */
    public String getOutputPrefix();
    
    /**
     * キャッシュしたオブジェクトをシリアライズしてファイルとして出力する際のファイル名のサフィックスを設定する。<p>
     * この出力ファイルサフィックスが指定されていない場合は、".obj"が使用される。<br>
     *
     * @param suffix 出力ファイルサフィックス
     */
    public void setOutputSuffix(String suffix);
    
    /**
     * キャッシュしたオブジェクトをシリアライズしてファイルとして出力する際のファイル名のプレフィクスを取得する。<p>
     *
     * @return 出力ファイルプレフィクス
     */
    public String getOutputSuffix();
    
    /**
     * キャッシュしたオブジェクトをシリアライズしてファイルとして出力したキャッシュファイルをサービスの開始時にロードするかどうかを設定する。<p>
     * デフォルトは、falseで、サービスの開始時にロードしない。<br>
     *
     * @param isLoad サービスの開始時にロードする場合true
     */
    public void setLoadOnStart(boolean isLoad);
    
    /**
     * キャッシュしたオブジェクトをシリアライズしてファイルとして出力したキャッシュファイルをサービスの開始時にロードするかどうかを判定する。<p>
     *
     * @return サービスの開始時にロードする場合true
     */
    public boolean isLoadOnStart();
    
    /**
     * キャッシュしたオブジェクトをシリアライズしてファイルとして出力したキャッシュファイルをJVMの終了時に削除するかどうかを設定する。<p>
     * デフォルトは、trueで、JVM終了時に削除する。<br>
     *
     * @param isDeleteOnExit 削除する場合true
     */
    public void setDeleteOnExitWithJVM(boolean isDeleteOnExit);
    
    /**
     * キャッシュしたオブジェクトをシリアライズしてファイルとして出力したキャッシュファイルをJVMの終了時に削除するかどうかを判定する。<p>
     *
     * @return trueの場合、削除する
     */
    public boolean isDeleteOnExitWithJVM();
    
    /**
     * キャッシュファイルのロードに失敗した場合に削除するかどうかを設定する。<p>
     * デフォルトは、falseで、削除しない。<br>
     *
     * @param isDelete 削除する場合true
     */
    public void setDeleteOnLoadError(boolean isDelete);
    
    /**
     * キャッシュファイルのロードに失敗した場合に削除するかどうかを判定する。<p>
     *
     * @return trueの場合、削除する
     */
    public boolean isDeleteOnLoadError();
    
    /**
     * ファイルに直列化する際に直列化を行う{@link jp.ossc.nimbus.service.io.Externalizer Externalizer}サービスのサービス名を設定する。<p>
     *
     * @param name Externalizerサービスのサービス名
     */
    public void setExternalizerServiceName(ServiceName name);
    
    /**
     * ファイルに直列化する際に直列化を行う{@link jp.ossc.nimbus.service.io.Externalizer Externalizer}サービスのサービス名を取得する。<p>
     *
     * @return Externalizerサービスのサービス名
     */
    public ServiceName getExternalizerServiceName();
}
