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

/**
 * ���s�R�[�h���폜���ĕ����s�̕������1�s�̕�����ɕϊ�����{@link WritableElement}�����N���X�B<p>
 * 
 * @author M.Takata
 */
public class LineSimpleElement extends SimpleElement {
    
    private static final long serialVersionUID = -2483151443183569493L;
    
    private boolean isTrim;
    
    /**
     * ��̃C���X�^���X�𐶐�����B<p>
     */
    public LineSimpleElement(){
    }
    
    /**
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param value �ϊ��Ώۂ̃I�u�W�F�N�g
     */
    public LineSimpleElement(Object value){
        super(value);
    }
    
    /**
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param key �L�[
     * @param value �ϊ��Ώۂ̃I�u�W�F�N�g
     */
    public LineSimpleElement(Object key, Object value){
        super(key, value);
    }
    
    /**
     * ���s����̋󔒕������g�������邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse�Ńg�������Ȃ��B<br>
     * 
     * @param trim �g��������ꍇ�́Atrue
     */
    public void setTrim(boolean trim){
        isTrim = trim;
    }
    
    /**
     * ���s����̋󔒕������g�������邩�ǂ����𔻒肷��B<p>
     * 
     * @return true�̏ꍇ�̓g��������
     */
    public boolean isTrim(){
        return isTrim;
    }
    
    /**
     * ���s�R�[�h���폜���ĕ����s�̕������1�s�̕�����ɕϊ�����B<p>
     * 
     * @return �ϊ��㕶����
     */
    public String toString(){
        final String str = super.toString();
        if(str == null
             || str.length() == 0
             || (str.indexOf('\n') == -1
                    && str.indexOf('\r') == -1)
        ){
            return str;
        }
        final StringBuffer buf = new StringBuffer();
        boolean isLineSeparator = false;
        for(int i = 0, max = str.length(); i < max; i++){
            char c = str.charAt(i);
            if(c == '\n' || c == '\r'){
                isLineSeparator = true;
            }else{
                if(!isTrim || !isLineSeparator || !Character.isWhitespace(c)){
                    buf.append(c);
                    isLineSeparator = false;
                }
            }
        }
        return buf.toString();
    }
}
