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
package jp.ossc.nimbus.util.converter;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * HttpServletRequest⇒FileItemコンバータ。<p>
 * 変換後オブジェクトはFileItemのListとなる。<br>
 *
 * @author M.Ishida
 */
public class HttpServletRequestFileConverter implements Converter {

    // HttpServletRequestからFileに変換する際の一時ファイルをメモリ上で管理できるデータサイズの上限。
    private int sizeThreshold = DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD;
    // ディスク上に一時的に保存する際のディレクトリ。
    private String repositoryPath;
    // Requestのデータサイズの上限。
    private long requestSizeThreshold = -1L;
    private String headerEncoding;

    /**
     * メモリ上で管理するデータサイズ（上限）を設定する。
     * <p>
     * デフォルトは{@link DiskFileItemFactory#DEFAULT_SIZE_THRESHOLD}。
     * この値を超えると、ディスク上に一時的に保存される。<br>
     * ここで保存されたファイルは適当なタイミングで勝手に消される。<br>
     *
     * @see DiskFileItemFactory#setSizeThreshold(int)
     * @param size 上限サイズ
     */
    public void setSizeThreshold(int size) {
        sizeThreshold = size;
    }

    /**
     * メモリ上で管理するデータサイズ（上限）を取得する。
     * <p>
     *
     * @return 上限サイズ
     */
    public int getSizeThreshold() {
        return sizeThreshold;
    }

    /**
     * ディスク上に一時的に保存する際のディレクトリを指定する。
     * <p>
     *
     * @see DiskFileItemFactory#setRepository(File)
     * @param path ディスク上に一時的に保存する際のディレクトリパス
     */
    public void setRepositoryPath(String path) {
        repositoryPath = path;
    }

    /**
     * ディスク上に一時的に保存する際のディレクトリを取得する。
     * <p>
     *
     * @return ディスク上に一時的に保存する際のディレクトリパス
     */
    public String getRepositoryPath() {
        return repositoryPath;
    }

    /**
     * HttpServletRequestのContentLengthの最大値を設定する。
     * <p>
     *
     * @param size ContentLengthの最大値
     */
    public void setRequestSizeThreshold(long size) {
        requestSizeThreshold = size;
    }

    /**
     * HttpServletRequestのContentLengthの最大値を取得する。
     * <p>
     *
     * @return ContentLengthの最大値
     */
    public long getRequestSizeThreshold() {
        return requestSizeThreshold;
    }

    /**
     * HTTPヘッダの文字コードを設定する。<p>
     *
     * @param encoding 文字コード
     */
    public void setHeaderEncoding(String encoding){
        headerEncoding = encoding;
    }
    
    /**
     * HTTPヘッダの文字コードを取得する。<p>
     *
     * @return 文字コード
     */
    public String getHeaderEncoding(){
        return headerEncoding;
    }
    
    public Object convert(Object obj) throws ConvertException {
        if (!(obj instanceof HttpServletRequest)) {
            throw new ConvertException("Parameter is not instancce of HttpServletRequest.");
        }
        DiskFileItemFactory factory = new DiskFileItemFactory();
        if (repositoryPath != null) {
            factory.setRepository(new File(repositoryPath));
        }
        factory.setSizeThreshold(sizeThreshold);
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(requestSizeThreshold);
        if(headerEncoding != null){
            upload.setHeaderEncoding(headerEncoding);
        }
        try {
            return upload.parseRequest((HttpServletRequest) obj);
        } catch (FileUploadException e) {
            throw new ConvertException(e);
        }
    }

}
