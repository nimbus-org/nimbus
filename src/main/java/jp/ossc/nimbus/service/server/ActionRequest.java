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
 * �N���C�A���g����̃A�N�V�����w�胊�N�G�X�g�B<p>
 * �d���̃t�H�[�}�b�g�́A
 * <pre>
 * [�A�N�V������������]
 * [�C�ӂ̃f�[�^]
 * </pre>
 *
 * @author M.Takata
 */
public class ActionRequest extends Request{
    
    protected String action;
    
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
        
        StringBuffer actionBuf = new StringBuffer();
        int c = 0;
        while((c = requestInputStream.read()) != -1 && c != '\n'){
            if(c == '\r'){
                continue;
            }
            actionBuf.append((char)c);
        }
        action = actionBuf.toString();
        return true;
    }
    
    /**
     * �A�N�V�������擾����B<p>
     *
     * @return �A�N�V����
     */
    public String getAction(){
        return action;
    }
    
    public String toString(){
        StringBuffer buf = new StringBuffer(super.toString());
        buf.deleteCharAt(buf.length() - 1);
        buf.append(", action=").append(action);
        buf.append('}');
        return buf.toString();
    }
}
