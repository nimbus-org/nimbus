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
// �p�b�P�[�W
package jp.ossc.nimbus.service.crypt;

import jp.ossc.nimbus.core.ServiceBase;

/**
 * �Œ�̕������t�����邾���̈Í��T�[�r�X�B<p>
 * 
 * @author A.Kokubu
 */
public class FixCryptService extends ServiceBase
 implements FixCryptServiceMBean, Crypt {
    
    private static final long serialVersionUID = 1117606145962089601L;
    
    private String fixPrefix;
    private String fixSuffix;
    
    public void setFixPrefix(String prefix){
        fixPrefix = prefix;
    }
    public String getFixPrefix(){
        return fixPrefix;
    }
    
    public void setFixSuffix(String suffix){
        fixSuffix = suffix;
    }
    public String getFixSuffix(){
        return fixSuffix;
    }
    
    /**
     * �Í����Ώۂ̕�����ɐړ���y�ѐڔ����t�����ĕԂ��B<p>
     * 
     * @param str �Í����Ώە�����
     * @return �Í���������
     */
    public String doEncode(String str) {
        if((fixPrefix == null || fixPrefix.length() == 0)
            && (fixSuffix == null || fixSuffix.length() == 0)){
            return str;
        }
        StringBuilder buf = new StringBuilder();
        if(fixPrefix != null && fixPrefix.length() != 0){
            buf.append(fixPrefix);
        }
        buf.append(str);
        if(fixSuffix != null && fixSuffix.length() != 0){
            buf.append(fixSuffix);
        }
        return buf.toString();
    }
    
    /**
     * �������Ώۂ̕����񂩂�ړ���y�ѐڔ��������ĕԂ��B<p>
     * 
     * @param str �������Ώە�����
     * @return ������������
     */
    public String doDecode(String str) {
        if((fixPrefix == null || fixPrefix.length() == 0)
            && (fixSuffix == null || fixSuffix.length() == 0)){
            return str;
        }
        String tmp = str;
        if(fixPrefix != null && fixPrefix.length() != 0
            && tmp.startsWith(fixPrefix)){
            tmp = tmp.substring(fixPrefix.length());
        }
        if(fixSuffix != null && fixSuffix.length() != 0
            && tmp.endsWith(fixSuffix)){
            tmp = tmp.substring(0, tmp.length() - fixSuffix.length());
        }
        return tmp;
    }
    
    /**
     * �n�b�V���Ώۂ̕���������̂܂ܕԂ��B<p>
     * 
     * @param str �n�b�V���Ώە�����
     * @return �n�b�V��������
     */
    public String doHash(String str) {
        return str;
    }
}
