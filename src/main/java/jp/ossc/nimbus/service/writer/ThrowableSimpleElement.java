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

import java.lang.reflect.*;

/**
 * ��O�L�q�v�f�B<p>
 * �ݒ肳�ꂽ��O���X�^�b�N�g���[�X�t���ŏo�͂���L�q�v�f�N���X�ł���B<br>
 *
 * @author K.Nagai
 */
public class ThrowableSimpleElement extends SimpleElement {
    
    private static final long serialVersionUID = -6342673505293153134L;
    
    private static final String LINE_SEP = System.getProperty("line.separator");
    private static final String TAB = "\t";
    private static final String MSG_TITLE = "Exception occuers :";
    private static final String MSG_CAUSE = "Caused by:";
    private static final String SERVLET_EXCEPTION_NAME = "javax.servlet.ServletException";
    private static final String GET_ROOT_CAUSE_METHOD = "getRootCause";
    private static final String JMS_EXCEPTION_NAME = "javax.jms.JMSException";
    private static final String GET_LINKED_EXCEPTION_METHOD = "getLinkedException";
    
    private boolean isOutputCause = true;
    private boolean isOutputTab = true;
    
    /**
     * ��̃C���X�^���X�𐶐�����B<p>
     */
    public ThrowableSimpleElement(){
    }
    
    /**
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param value �ϊ��Ώۂ̃I�u�W�F�N�g
     */
    public ThrowableSimpleElement(Object value){
        super(value);
    }
    
    /**
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param key �L�[
     * @param value �ϊ��Ώۂ̃I�u�W�F�N�g
     */
    public ThrowableSimpleElement(Object key, Object value){
        super(key, value);
    }
    
    /**
     * ���̗v�f���ێ������O�̌����ƂȂ��O��{@link #toString()}�ŏo�͂��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Atrue�B
     *
     * @param output �o�͂���ꍇtrue
     */
    public void setOutputCause(boolean output){
        isOutputCause = output;
    }
    
    /**
     * ���̗v�f���ێ������O�̌����ƂȂ��O��{@link #toString()}�ŏo�͂��邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�o�͂���
     */
    public boolean getOutputCause() {
        return isOutputCause;
    }
    
    /**
     * �X�^�b�N�g���[�X�̏o�͎��Ƀ^�u�ŃC���f���g���邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Atrue�B
     *
     * @param outputTab �^�u�ŃC���f���g����ꍇ�́Atrue
     */
    public void setOutputTab(boolean outputTab) {
        isOutputTab = outputTab;
    }
    
    /**
     * �X�^�b�N�g���[�X�̏o�͎��Ƀ^�u�ŃC���f���g���邩�ǂ����𔻒肷��B<p>
     *
     * @return �^�u�ŃC���f���g����ꍇ�́Atrue
     */
    public boolean getOutputTab() {
        return isOutputTab;
    }
    
    /**
     * ���̗�O�̃X�^�b�N�g���[�X�⌴���ƂȂ��O�̃X�^�b�N�g���[�X������ɂ��Ď擾����B<p>
     * 
     * @return ���̗�O�̃X�^�b�N�g���[�X�⌴���ƂȂ��O�̃X�^�b�N�g���[�X������
     */
    public String toString(){
        if(mValue == null){
            return super.toString();
        }
        Throwable e = (Throwable)mValue;
        final StringBuffer buf = new StringBuffer();
        buf.append(MSG_TITLE).append(e).append(LINE_SEP);
        final StackTraceElement[] elemss = e.getStackTrace();
        if(elemss != null){
            for(int i = 0, max = elemss.length; i < max; i++){
                if(isOutputTab){
                    buf.append(TAB);
                }
                buf.append(elemss[i]);
                if(i != max - 1){
                    buf.append(LINE_SEP);
                }
            }
        }
        if(isOutputCause){
            for(Throwable ee = getCause(e); ee != null; ee = getCause(ee)){
                buf.append(LINE_SEP).append(MSG_CAUSE)
                    .append(ee).append(LINE_SEP);
                final StackTraceElement[] elems = ee.getStackTrace();
                if(elems != null){
                    for(int i = 0, max = elems.length; i < max; i++){
                        if(isOutputTab){
                            buf.append(TAB);
                        }
                        buf.append(elems[i]);
                        if(i != max - 1){
                            buf.append(LINE_SEP);
                        }
                    }
                }
            }
        }
        return convertString(buf.toString());
    }
    
    private Throwable getCause(Throwable th){
        Throwable cause = null;
        String thClassName = th.getClass().getName();
        if(thClassName.equals(SERVLET_EXCEPTION_NAME)){
            // ��O��ServletException�̏ꍇ�́A���[�g�̌������擾
            try{
                cause = (Throwable)th.getClass()
                    .getMethod(GET_ROOT_CAUSE_METHOD, (Class[])null).invoke(th, (Object[])null);
            }catch(NoSuchMethodException e){
            }catch(IllegalAccessException e){
            }catch(InvocationTargetException e){
            }
        }else if(thClassName.equals(JMS_EXCEPTION_NAME)){
            // ��O��JMSException�̏ꍇ�́A�����N��O���擾
            try{
                cause = (Exception)th.getClass()
                    .getMethod(GET_LINKED_EXCEPTION_METHOD, (Class[])null).invoke(th, (Object[])null);
            }catch(NoSuchMethodException e){
            }catch(IllegalAccessException e){
            }catch(InvocationTargetException e){
            }
        }else{
            cause = th.getCause();
        }
        return cause == th ? null : cause;
    }
    
    /**
     * ���̗v�f�̃I�u�W�F�N�g�����̂܂܎擾����B<p>
     * {@link #getValue()}�Ɠ����l��Ԃ��B<br>
     * 
     * @return ���̗v�f�̃I�u�W�F�N�g
     */
    public Object toObject(){
        return getValue();
    }
}
