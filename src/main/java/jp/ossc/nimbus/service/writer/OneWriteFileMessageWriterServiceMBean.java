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
package jp.ossc.nimbus.service.writer;

import jp.ossc.nimbus.core.*;

/**
 * {@link OneWriteFileMessageWriterService}サービスのMBeanインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface OneWriteFileMessageWriterServiceMBean
 extends ServiceBaseMBean{
    
    /**
     * 出力ファイルの文字エンコーディングを設定する。<p>
     *
     * @param encoding 文字エンコーディング
     */
    public void setEncoding(String encoding);
    
    /**
     * 出力ファイルの文字エンコーディングを取得する。<p>
     *
     * @return 文字エンコーディング
     */
    public String getEncoding();
    
    /**
     * 出力先のファイル名を指定する。<p>
     *
     * @param file 出力先ファイル名
     */
    public void setFile(String file);
    
    /**
     * 出力先ファイル名を取得する。<p>
     *
     * @return 出力先ファイル名
     */
    public String getFile();
    
    /**
     * 出力先ファイル名のプレフィクスを設定する。<p>
     * プレフィクスには、固定文字の他に、%で囲んだキーを指定する事ができる。<br>
     * キー指定された値は、コンテキストまたは入力のWritableRecordから取得される。<br>
     *
     * @param prefix 出力先ファイル名のプレフィクス
     */
    public void setFilePrefix(String prefix);
    
    /**
     * 出力先ファイル名のプレフィクスを取得する。<p>
     *
     * @return 出力先ファイル名のプレフィクス
     */
    public String getFilePrefix();
    
    /**
     * 出力先ファイル名のポストフィクスを設定する。<p>
     * ポストフィクスには、固定文字の他に、%で囲んだキーを指定する事ができる。<br>
     * キー指定された値は、コンテキストまたは入力のWritableRecordから取得される。<br>
     *
     * @param postfix 出力先ファイル名のポストフィクス
     */
    public void setFilePostfix(String postfix);
    
    /**
     * 出力先ファイル名のポストフィクスを取得する。<p>
     *
     * @return 出力先ファイル名のポストフィクス
     */
    public String getFilePostfix();
    
    /**
     * ファイルに追記するかどうかを設定する。<p>
     * 
     * @param isAppend ファイルに追記する場合、true
     */
    public void setAppend(boolean isAppend);
    
    /**
     * ファイルに追記するかどうかを取得する。<p>
     * 
     * @return trueの場合、ファイルに追記する。
     */
    public boolean isAppend();
    
    /**
     * ファイルのヘッダを設定する。<p>
     * ファイルが存在しない時、または、追加書込みでない場合に出力する。<br>
     *
     * @param header ヘッダ
     */
    public void setHeader(String header);
    
    /**
     * ファイルのヘッダを取得する。<p>
     *
     * @return ヘッダ
     */
    public String getHeader();
    
    /**
     * 
     * 毎回出力ストリームを閉じるかどうかを設定する。<p>
     * trueを指定した場合、毎回ストリームを開閉する。falseを指定した場合は、ストリームは開きっ放しである。<br>
     * 但し、キー指定を含む出力先ファイル名のプレフィクス及びポストフィクスが指定されている場合は、ファイル名が動的になる可能性があるため、falseにはできない。<br>
     *
     * @param isClose 毎回出力ストリームを閉じる場合、true
     */
    public void setEveryTimeCloseStream(boolean isClose);
    
    /**
     * 毎回出力ストリームを閉じるかどうかを判定する。<p>
     * 
     * @return trueの場合、毎回出力ストリームを閉じる
     */
    public boolean isEveryTimeCloseStream();
    
    /**
     * コンテキストサービス名を設定する。<p>
     *
     * @param name コンテキストサービス名
     */
    public void setContextServiceName(ServiceName name);
    
    /**
     * コンテキストサービス名を取得する。<p>
     *
     * @return コンテキストサービス名
     */
    public ServiceName getContextServiceName();
    
    /**
     * ファイル名に使用したWritableElementをファイルに出力するかどうかを設定する。<p>
     * デフォルトは、trueで出力する。<br>
     *
     * @param isOutput ファイル名に使用したWritableElementをファイルに出力する場合true
     */
    public void setOutputKey(boolean isOutput);
    
    /**
     * ファイル名に使用したWritableElementをファイルに出力するかどうかを判定する。<p>
     *
     * @return trueの場合、ファイル名に使用したWritableElementをファイルに出力する
     */
    public boolean isOutputKey();
    
    /**
     * 追記するたびに最後に書き込まれるセパレータを設定する。<p>
     * 追記する設定の場合のみ使用されます。
     * 
     * @param separator セパレータ
     */
    public void setSeparator(String separator);
    
    /**
     * 追記するたびに最後に書き込まれるセパレータを取得する。<p>
     * 
     * @return セパレータ
     */
    public String getSeparator();
}
