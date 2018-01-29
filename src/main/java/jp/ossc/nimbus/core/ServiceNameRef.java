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
package jp.ossc.nimbus.core;

/**
 * {@link Service}���Q�Ƃ��邽�߂̎Q�Ɩ��B<p>
 * 
 * @author M.Takata
 */
public class ServiceNameRef implements java.io.Serializable, Comparable{
    
    private static final long serialVersionUID = 408293095266607083L;

    /**
     * �Q�Ƃ���{@link Service}�̎��T�[�r�X���B<p>
     */
    private final ServiceName realName;
    
    /**
     * {@link Service}�̎Q�Ɩ��B<p>
     */
    private final String refName;
    
    /**
     * {@link Service}���o�^����Ă���{@link ServiceManager}�̖��O��Service�̖��O���w�肵�āA�T�[�r�X�̎��ʖ��C���X�^���X�𐶐�����B<p>
     *
     * @param refName Service�̎Q�Ɩ�
     * @param realName �Q�Ƃ���Service�̎��T�[�r�X���B
     */
    public ServiceNameRef(String refName, ServiceName realName){
        this.refName = refName;
        this.realName = realName;
    }
    
    /**
     * {@link Service}�̎Q�Ɩ����擾����B<p>
     * 
     * @return Service�̎Q�Ɩ�
     */
    public String getReferenceServiceName(){
        return refName;
    }
    
    /**
     * {@link Service}�̖��O���擾����B<p>
     * 
     * @return Service�̖��O
     */
    public ServiceName getServiceName(){
        return realName;
    }
    
    /**
     * ���̃C���X�^���X�̕�����\����Ԃ��B<p>
     *
     * @return [{@link Service}�̎Q�Ɩ�]=[Service�̖��O]
     */
    public String toString(){
        StringBuilder buf = new StringBuilder();
        if(refName != null){
            buf.append(refName);
        }
        buf.append('=');
        if(realName != null){
            buf.append(realName);
        }
        return buf.toString();
    }
    
    /**
     * ������obj�����̃I�u�W�F�N�g�Ɠ����������ׂ�B<p>
     * {@link Service}���o�^����Ă���{@link ServiceManager}�̖��O��Service�̖��O�̗������������ꍇ�̂�true��Ԃ��B<br>
     *
     * @param obj ��r�Ώۂ̃I�u�W�F�N�g
     * @return �������ꍇtrue
     */
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(this == obj){
            return true;
        }
        if(obj instanceof ServiceNameRef){
            final ServiceNameRef name = (ServiceNameRef)obj;
            if((refName == null && name.refName != null)
                || (refName != null && name.refName == null)){
                return false;
            }else if(refName != null && name.refName != null
                && !refName.equals(name.refName)){
                return false;
            }
            if((realName == null && name.realName != null)
                || (realName != null && name.realName == null)){
                return false;
            }else if(realName != null && name.realName != null
                && !realName.equals(name.realName)){
                return false;
            }
            return true;
        }
        return false;
    }
    
    /**
     * �n�b�V���l���擾����B<p>
     *
     * @return �n�b�V���l
     */
    public int hashCode(){
        return (refName != null ? refName.hashCode() : 0)
            + (realName != null ? realName.hashCode() : 0);
    }
    
    /**
     * ���̃I�u�W�F�N�g�Ǝw�肳�ꂽ�I�u�W�F�N�g�̏������r����B<p>
     *
     * @param obj ��r�ΏƂ̃I�u�W�F�N�g
     * @return ���̃I�u�W�F�N�g���w�肳�ꂽ�I�u�W�F�N�g��菬�����ꍇ�͕��̐����A�������ꍇ�̓[���A�傫���ꍇ�͐��̐�����Ԃ��B
     */
    public int compareTo(Object obj){
        if(obj instanceof ServiceNameRef){
            final ServiceNameRef name = (ServiceNameRef)obj;
            if(refName == null){
                if(name.refName != null){
                    return  -1;
                }
            }else{
                if(name.refName == null){
                    return 1;
                }else{
                    final int ret = refName.compareTo(name.refName);
                    if(ret != 0){
                        return ret;
                    }
                }
            }
            if(realName == null){
                if(name.realName != null){
                    return  -1;
                }
            }else{
                if(name.realName == null){
                    return 1;
                }else{
                    return realName.compareTo(name.realName);
                }
            }
        }
        return -1;
    }
}