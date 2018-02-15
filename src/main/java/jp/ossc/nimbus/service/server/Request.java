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
package jp.ossc.nimbus.service.server;

import java.util.Date;
import java.io.IOException;
import java.io.EOFException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * クライアントからのリクエスト。<p>
 * 電文のフォーマットは、
 * <pre>
 * [任意のデータ]
 * </pre>
 *
 * @author M.Takata
 */
public class Request{
    
    protected InputStream requestInputStream;
    protected String requestId;
    protected Date date;
    protected String remoteHost;
    protected int remotePort;
    protected boolean isAccept;
    protected boolean isFirst;
    
    /**
     * ソケット受付を読み込む。<p>
     *
     * @param channel ソケットチャネル
     * @exception IOException 読み込みに失敗した場合
     */
    public void accept(SocketChannel channel) throws IOException{
        if(remoteHost == null){
            remoteHost = channel.socket().getInetAddress().getHostAddress();
        }
        if(remotePort == 0){
            remotePort = channel.socket().getPort();
        }
        if(date == null){
            date = new Date();
        }
    }
    
    /**
     * リクエストを読み込む。<p>
     *
     * @param channel ソケットチャネル
     * @return リクエストの続きを読み込み待ちする必要がある場合、false。リクエストを読み切った場合は、true。
     * @exception IOException 読み込みに失敗した場合
     */
    public boolean read(SocketChannel channel) throws IOException{
        if(remoteHost == null){
            remoteHost = channel.socket().getInetAddress().getHostAddress();
        }
        if(remotePort == 0){
            remotePort = channel.socket().getPort();
        }
        if(date == null){
            date = new Date();
        }
        return createRequestInputStream(channel);
    }
    
    protected boolean createRequestInputStream(SocketChannel channel) throws IOException{
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        byte[] bytes = new byte[1024];
        int readLen = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        do{
            readLen = channel.read(buffer);
            if(readLen == -1){
                throw new EOFException();
            }
            if(readLen > 0){
                buffer.flip();
                buffer.get(bytes, 0, readLen);
                baos.write(bytes, 0, readLen);
                buffer.flip();
            }
        }while(readLen > 0 && readLen != bytes.length);
        requestInputStream = new ByteArrayInputStream(baos.toByteArray());
        return true;
    }
    
    /**
     * ソケット受付時のリクエストかどうかを設定する。<p>
     *
     * @param isAccept ソケット受付時のリクエストの場合、true
     */
    public void setAccept(boolean isAccept){
        this.isAccept = isAccept;
    }
    
    /**
     * ソケット受付時のリクエストかどうかを判定する。<p>
     * ソケット受付時のリクエストの場合、{@link #getInputStream()}は、nullを返す。<br>
     *
     * @return ソケット受付時のリクエストの場合、true
     */
    public boolean isAccept(){
        return isAccept;
    }
    
    /**
     * ソケット受付後の初回リクエストかどうかを設定する。<p>
     *
     * @param isFirst ソケット受付後の初回リクエストの場合、true
     */
    public void setFirst(boolean isFirst){
        this.isFirst = isFirst;
    }
    
    /**
     * ソケット受付後の初回リクエストかどうかを判定する。<p>
     *
     * @return trueの場合、ソケット受付後の初回リクエスト
     */
    public boolean isFirst(){
        return isFirst;
    }
    
    /**
     * リクエストIDを取得する。<p>
     *
     * @return リクエストID
     */
    public String getRequestId(){
        return requestId;
    }
    /**
     * リクエストIDを設定する。<p>
     *
     * @param id リクエストID
     */
    protected void setRequestId(String id){
        this.requestId = id;
    }
    
    /**
     * リクエスト受付時刻を取得する。<p>
     *
     * @return リクエスト受付時刻
     */
    public Date getDate(){
        return date;
    }
    /**
     * リクエスト受付時刻を設定する。<p>
     *
     * @param date リクエスト受付時刻
     */
    protected void setDate(Date date){
        this.date = date;
    }
    
    /**
     * クライアントのIPアドレスを取得する。<p>
     *
     * @return クライアントのIPアドレス
     */
    public String getRemoteHost(){
        return remoteHost;
    }
    
    /**
     * クライアントの接続ポート番号を取得する。<p>
     *
     * @return クライアントの接続ポート番号
     */
    public int getRemotePort(){
        return remotePort;
    }
    
    /**
     * 入力ストリームを取得する。<p>
     *
     * @return 入力ストリーム
     */
    public InputStream getInputStream(){
        return requestInputStream;
    }
    
    public String toString(){
        StringBuilder buf = new StringBuilder(super.toString());
        buf.append('{');
        buf.append("isAccept=").append(isAccept);
        buf.append(", isFirst=").append(isFirst);
        buf.append(", requestId=").append(requestId);
        buf.append(", date=").append(date);
        buf.append(", remoteHost=").append(remoteHost);
        buf.append(", remotePort=").append(remotePort);
        buf.append('}');
        return buf.toString();
    }
}
