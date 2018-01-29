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

import java.beans.*;
import java.util.*;
import java.lang.reflect.Method;

/**
 * {@link java.lang.reflect.Method}�^��PropertyEditor�N���X�B<p>
 * ���\�b�h�������java.lang.reflect.Method�^�̃I�u�W�F�N�g�ɕϊ�����B<br>
 * "${"��"}"�Ɉ͂܂ꂽ������́A�����̃V�X�e���v���p�e�B�ƒu�������B<br>
 * <p>
 * ��F<br>
 * &nbsp;&nbsp;java.util.HashMap#put(java.lang.Object,java.lang.Object)<br>
 * <br>
 * �̂悤�ȕ�����<br>
 * <br>
 * &nbsp;&nbsp;java.util.HashMap.class.getMethod("put", new Class[]{java.lang.Object.class, java.lang.Object.class})<br>
 * <br>
 * �̂悤�ɕϊ������B<br>
 *
 * @author M.Takata
 */
public class MethodEditor extends PropertyEditorSupport
 implements java.io.Serializable{
    
    private static final long serialVersionUID = 5524131172598421268L;
    
    /**
     * �w�肳�ꂽ���������͂��ăv���p�e�B�l��ݒ肷��B<p>
     *
     * @param text ��͂���镶����
     */
    public void setAsText(String text){
        if(text == null){
            setValue(null);
            return;
        }
        final String tmp = Utility.replaceSystemProperty(text);
        try{
            int index = tmp.indexOf('#');
            if(index == -1 || index == 0 || tmp.length() - index <= 3){
                throw new IllegalArgumentException("format is classname#methodname(paramType1,paramType2,.....) : " + tmp);
            }
            final String className = tmp.substring(0, index).trim();
            final Class clazz = Utility.convertStringToClass(className);
            int startIndex = tmp.indexOf('(', index + 1);
            if(startIndex == -1 || index + 1 == startIndex){
                throw new IllegalArgumentException("format is classname#methodname(paramType1,paramType2,.....) : " + tmp);
            }
            final String methodName
                 = tmp.substring(index + 1, startIndex).trim();
            int endIndex = tmp.indexOf(')', startIndex + 1);
            if(endIndex == -1 || endIndex != tmp.length() - 1){
                throw new IllegalArgumentException("format is classname#methodname(paramType1,paramType2,.....) : " + tmp);
            }
            String paramTypesStr
                 = tmp.substring(startIndex + 1, endIndex).trim();
            Class[] paramTypes = null;
            if(paramTypesStr.length() != 0){
                final List paramTypeList = new ArrayList();
                do{
                    index = paramTypesStr.indexOf(',');
                    String paramType = paramTypesStr;
                    if(index != -1){
                        if(index == paramTypesStr.length() - 1){
                            break;
                        }
                        paramType = paramTypesStr.substring(0, index);
                        paramTypesStr = paramTypesStr.substring(index + 1);
                    }
                    paramTypeList.add(
                        Utility.convertStringToClass(paramType.trim())
                    );
                }while(index != -1);
                paramTypes = (Class[])paramTypeList
                    .toArray(new Class[paramTypeList.size()]);
            }
            setValue(clazz.getMethod(methodName, paramTypes));
        }catch(ClassNotFoundException e){
            throw new IllegalArgumentException(tmp + " : " + e.getMessage());
        }catch(NoSuchMethodException e){
            throw new IllegalArgumentException(tmp + " : " + e.getMessage());
        }
    }
    
    /**
     * �v���p�e�B��������擾����B<p>
     *
     * @return �v���p�e�B������
     */
    public String getAsText(){
        final Method method = (Method)getValue();
        if(method == null){
            return null;
        }
        final Class clazz = method.getDeclaringClass();
        final StringBuilder buf = new StringBuilder(clazz.getName());
        buf.append('#');
        buf.append(method.getName());
        buf.append('(');
        final Class[] paramTypes = method.getParameterTypes();
        for(int i = 0; i < paramTypes.length; i++){
            buf.append(paramTypes[i].getName());
            if(i != paramTypes.length - 1){
                buf.append(',');
            }
        }
        buf.append(')');
        return buf.toString();
    }
}
