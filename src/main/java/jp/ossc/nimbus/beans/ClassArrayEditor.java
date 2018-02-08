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
package jp.ossc.nimbus.beans;

import java.util.*;

/**
 * {@link Class}�z��^��PropertyEditor�N���X�B<p>
 * �J���}��؂�̃N���X���������java.lang.Class�z��^�̃I�u�W�F�N�g�ɕϊ�����B<br>
 * �󔒂̓g���������B
 * �󔒂́A{@link java.lang.Character#isWhitespace(char)}�Ŕ��肳���B
 * �܂��A"&lt;!--"��"--&gt;"�Ɉ͂܂ꂽ������̓R�����g�Ɖ��߂��ꖳ�������B
 * �܂��A"${"��"}"�Ɉ͂܂ꂽ������́A�����̃V�X�e���v���p�e�B�ƒu�������B<br>
 * <p>
 * ��F<br>
 * &nbsp;&nbsp;java.lang.Integer,java.lang.Boolean<br>
 * <br>
 * �̂悤�ȕ�����<br>
 * <br>
 * &nbsp;&nbsp;new Class[]{Class.forName("java.lang.Integer", true, NimbusClassLoader.getInstance()), Class.forName("java.lang.Boolean", true, NimbusClassLoader.getInstance())}<br>
 * <br>
 * �̂悤�ɕϊ������B<br>
 *
 * @author M.Takata
 */
public class ClassArrayEditor extends ArrayEditor
 implements java.io.Serializable{
    
    private static final long serialVersionUID = 3752687582698392704L;
    
    protected Object createArray(List strList){
        final Class[] array = new Class[strList.size()];
        try{
            for(int i = 0; i < array.length; i++){
                array[i] = Utility.convertStringToClass(
                    ((String)strList.get(i)).trim()
                );
            }
        }catch(ClassNotFoundException e){
            throw new IllegalArgumentException(e.getMessage());
        }
        return array;
    }
    
    /**
     * �v���p�e�B��������擾����B<p>
     *
     * @return �v���p�e�B������
     */
    public String getAsText(){
        final Class[] classArray = (Class[])getValue();
        if(classArray == null){
            return null;
        }
        final StringBuffer buf = new StringBuffer();
        for(int i = 0, max = classArray.length; i < max; i++){
            buf.append(classArray[i].getName());
            if(i != max - 1){
                buf.append(',');
            }
        }
        return buf.toString();
    }
}
