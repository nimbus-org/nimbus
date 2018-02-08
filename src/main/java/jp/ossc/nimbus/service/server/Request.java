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
 * �N���C�A���g����̃��N�G�X�g�B<p>
 * �d���̃t�H�[�}�b�g�́A
 * <pre>
 * [�C�ӂ̃f�[�^]
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
     * �\�P�b�g��t��ǂݍ��ށB<p>
     *
     * @param channel �\�P�b�g�`���l��
     * @exception IOException �ǂݍ��݂Ɏ��s�����ꍇ
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
     * ���N�G�X�g��ǂݍ��ށB<p>
     *
     * @param channel �\�P�b�g�`���l��
     * @return ���N�G�X�g�̑�����ǂݍ��ݑ҂�����K�v������ꍇ�Afalse�B���N�G�X�g��ǂݐ؂����ꍇ�́Atrue�B
     * @exception IOException �ǂݍ��݂Ɏ��s�����ꍇ
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
     * �\�P�b�g��t���̃��N�G�X�g���ǂ�����ݒ肷��B<p>
     *
     * @param isAccept �\�P�b�g��t���̃��N�G�X�g�̏ꍇ�Atrue
     */
    public void setAccept(boolean isAccept){
        this.isAccept = isAccept;
    }
    
    /**
     * �\�P�b�g��t���̃��N�G�X�g���ǂ����𔻒肷��B<p>
     * �\�P�b�g��t���̃��N�G�X�g�̏ꍇ�A{@link #getInputStream()}�́Anull��Ԃ��B<br>
     *
     * @return �\�P�b�g��t���̃��N�G�X�g�̏ꍇ�Atrue
     */
    public boolean isAccept(){
        return isAccept;
    }
    
    /**
     * �\�P�b�g��t��̏��񃊃N�G�X�g���ǂ�����ݒ肷��B<p>
     *
     * @param isFirst �\�P�b�g��t��̏��񃊃N�G�X�g�̏ꍇ�Atrue
     */
    public void setFirst(boolean isFirst){
        this.isFirst = isFirst;
    }
    
    /**
     * �\�P�b�g��t��̏��񃊃N�G�X�g���ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A�\�P�b�g��t��̏��񃊃N�G�X�g
     */
    public boolean isFirst(){
        return isFirst;
    }
    
    /**
     * ���N�G�X�gID���擾����B<p>
     *
     * @return ���N�G�X�gID
     */
    public String getRequestId(){
        return requestId;
    }
    /**
     * ���N�G�X�gID��ݒ肷��B<p>
     *
     * @param id ���N�G�X�gID
     */
    protected void setRequestId(String id){
        this.requestId = id;
    }
    
    /**
     * ���N�G�X�g��t�������擾����B<p>
     *
     * @return ���N�G�X�g��t����
     */
    public Date getDate(){
        return date;
    }
    /**
     * ���N�G�X�g��t������ݒ肷��B<p>
     *
     * @param date ���N�G�X�g��t����
     */
    protected void setDate(Date date){
        this.date = date;
    }
    
    /**
     * �N���C�A���g��IP�A�h���X���擾����B<p>
     *
     * @return �N���C�A���g��IP�A�h���X
     */
    public String getRemoteHost(){
        return remoteHost;
    }
    
    /**
     * �N���C�A���g�̐ڑ��|�[�g�ԍ����擾����B<p>
     *
     * @return �N���C�A���g�̐ڑ��|�[�g�ԍ�
     */
    public int getRemotePort(){
        return remotePort;
    }
    
    /**
     * ���̓X�g���[�����擾����B<p>
     *
     * @return ���̓X�g���[��
     */
    public InputStream getInputStream(){
        return requestInputStream;
    }
    
    public String toString(){
        StringBuffer buf = new StringBuffer(super.toString());
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
