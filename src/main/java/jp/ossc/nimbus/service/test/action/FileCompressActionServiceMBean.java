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
package jp.ossc.nimbus.service.test.action;

import jp.ossc.nimbus.core.ServiceBaseMBean;

/**
 * {@link FileCompressActionService}のMBeanインタフェース<p>
 * 
 * @author M.Ishida
 * @see FileCompressActionService
 */
public interface FileCompressActionServiceMBean extends ServiceBaseMBean {
    
    /**
     * Zip圧縮用定数
     */
    public static final String ZIP = "ZIP";
    
    /**
     * gz圧縮用定数
     */
    public static final String GZ = "GZ";
    
    /**
     * LZ4圧縮用定数
     */
    public static final String LZ4 = "LZ4";
    
    /**
     * SNAPPY圧縮用定数
     */
    public static final String SNAPPY = "SNAPPY";
    
    /**
     * 圧縮用定数
     */
    public static final String ARCHIVE = "ARCHIVE";
    
    /**
     * 解凍用定数
     */
    public static final String EXTRACT = "EXTRACT";
    
    /**
     * デフォルトZip拡張子用定数
     */
    public static final String DEFAULT_ZIP_FILE_EXTENTION = ".zip";
    
    /**
     * デフォルトgz拡張子用定数
     */
    public static final String DEFAULT_GZ_FILE_EXTENTION = ".gz";
    
    /**
     * デフォルトLZ4拡張子用定数
     */
    public static final String DEFAULT_LZ4_FILE_EXTENTION = ".lz4";
    
    /**
     * デフォルトSnappy拡張子用定数
     */
    public static final String DEFAULT_SNAPPY_FILE_EXTENTION = ".snappy";
    
    /**
     * Zipファイルの拡張子を取得する。<br>
     * 
     * @return Zipファイルの拡張子
     */
    public String getZipFileExtension();

    /**
     * Zipファイルの拡張子を設定する。<br>
     * 
     * @param extension Zipファイルの拡張子
     */
    public void setZipFileExtension(String extension);

    /**
     * gzファイルの拡張子を取得する。<br>
     * 
     * @return gzファイルの拡張子
     */
    public String getGzFileExtension();

    /**
     * gzファイルの拡張子を設定する。<br>
     * 
     * @param extension gzファイルの拡張子
     */
    public void setGzFileExtension(String extension);

    /**
     * lz4ファイルの拡張子を取得する。<br>
     * 
     * @return lz4ファイルの拡張子
     */
    public String getLz4FileExtension();

    /**
     * lz4ファイルの拡張子を設定する。<br>
     * 
     * @param extension lz4ファイルの拡張子
     */
    public void setLz4FileExtension(String extension);

    /**
     * Snappyファイルの拡張子を取得する。<br>
     * 
     * @return Snappyファイルの拡張子
     */
    public String getSnappyFileExtension();

    /**
     * Snappyファイルの拡張子を設定する。<br>
     * 
     * @param extension Snappyファイルの拡張子
     */
    public void setSnappyFileExtension(String extension);
    
    /**
     * このアクションのリソース定義を作成する際のデフォルトの想定コストを設定する。<p>
     * 
     * @param cost 想定コスト
     */
    public void setExpectedCost(double cost);
    
    /**
     * このアクションのリソース定義を作成する際のデフォルトの想定コストを取得する。<p>
     * 
     * @return 想定コスト
     */
    public double getExpectedCost();
}
