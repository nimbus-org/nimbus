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
package jp.ossc.nimbus.service.writer.log4j;

/**
 * {@link FileAppenderWriterService}サービスのMBeanインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface FileAppenderWriterServiceMBean
 extends WriterAppenderWriterServiceMBean{
    
    /**
     * ファイル追加書き込みモードを設定する。<p>
     *
     * @param append 追加書き込みを行う場合true
     */
    public void setAppend(boolean append);
    
    /**
     * ファイル追加書き込みモードかどうかを調べる。<p>
     *
     * @return trueの場合、追加書き込みを行う
     */
    public boolean isAppend();
    
    /**
     * ファイル書き込みでバッファリングを行うかを設定する。<p>
     *
     * @param bufferedIO BufferedWriterを使用してバッファリングする場合true
     */
    public void setBufferedIO(boolean bufferedIO);
    
    /**
     * バッファリングしてファイル書き込みを行うか調べる。<p>
     *
     * @return trueの場合、バッファリングする
     */
    public boolean isBufferedIO();
    
    /**
     * バッファサイズを設定する。<p>
     * {@link #isBufferedIO()}がtrueの場合、有効になる。<br>
     *
     * @param bufferSize バッファサイズ
     */
    public void setBufferSize(int bufferSize);
    
    /**
     * バッファサイズを取得する。<p>
     *
     * @return バッファサイズ
     */
    public int getBufferSize();
    
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
}