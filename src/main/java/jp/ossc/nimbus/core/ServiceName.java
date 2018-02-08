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
 * {@link Service}�����ʂ��邽�߂̖��O�B<p>
 * 
 * @author M.Takata
 */
public class ServiceName implements java.io.Serializable, Comparable{
    
    private static final long serialVersionUID = 3004514157528131335L;
    
    /**
     * {@link Service}���o�^����Ă���{@link ServiceManager}�̖��O�B<p>
     */
    private String managerName;
    
    /**
     * {@link Service}�̖��O�B<p>
     */
    private String serviceName;
    
    public ServiceName(){}
    
    /**
     * {@link Service}�̖��O���w�肵�āA�T�[�r�X�̎��ʖ��C���X�^���X�𐶐�����B<p>
     * Service���o�^����Ă���{@link ServiceManager}�̖��O�́A{@link ServiceManager#DEFAULT_NAME}�ƂȂ�B<br>
     *
     * @param service Service�̖��O
     */
    public ServiceName(String service){
        this(ServiceManager.DEFAULT_NAME, service);
    }
    
    /**
     * {@link Service}���o�^����Ă���{@link ServiceManager}�̖��O��Service�̖��O���w�肵�āA�T�[�r�X�̎��ʖ��C���X�^���X�𐶐�����B<p>
     *
     * @param manager Service���o�^����Ă���ServiceManager�̖��O
     * @param service Service�̖��O
     */
    public ServiceName(String manager, String service){
        managerName = manager;
        serviceName = service;
    }
    
    /**
     * {@link Service}���o�^����Ă���{@link ServiceManager}�̖��O���擾����B<p>
     * 
     * @return {@link Service}���o�^����Ă���{@link ServiceManager}�̖��O
     */
    public String getServiceManagerName(){
        return managerName;
    }
    
    /**
     * {@link Service}�̖��O���擾����B<p>
     * 
     * @return {@link Service}�̖��O
     */
    public String getServiceName(){
        return serviceName;
    }
    
    /**
     * ���̃C���X�^���X�̕�����\����Ԃ��B<p>
     *
     * @return [{@link ServiceManager}�̖��O]#[{@link Service}�̖��O]
     */
    public String toString(){
        StringBuffer buf = new StringBuffer();
        if(managerName != null){
            buf.append(managerName);
        }
        if(managerName != null || serviceName != null){
            buf.append('#');
        }
        if(serviceName != null){
            buf.append(serviceName);
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
        if(obj instanceof ServiceName){
            final ServiceName name = (ServiceName)obj;
            if((managerName == null && name.managerName != null)
                || (managerName != null && name.managerName == null)){
                return false;
            }else if(managerName != null && name.managerName != null
                && !managerName.equals(name.managerName)){
                return false;
            }
            if((serviceName == null && name.serviceName != null)
                || (serviceName != null && name.serviceName == null)){
                return false;
            }else if(serviceName != null && name.serviceName != null
                && !serviceName.equals(name.serviceName)){
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
        return (managerName != null ? managerName.hashCode() : 0)
            + (serviceName != null ? serviceName.hashCode() : 0);
    }
    
    /**
     * ���̃I�u�W�F�N�g�Ǝw�肳�ꂽ�I�u�W�F�N�g�̏������r����B<p>
     *
     * @param obj ��r�ΏƂ̃I�u�W�F�N�g
     * @return ���̃I�u�W�F�N�g���w�肳�ꂽ�I�u�W�F�N�g��菬�����ꍇ�͕��̐����A�������ꍇ�̓[���A�傫���ꍇ�͐��̐�����Ԃ��B
     */
    public int compareTo(Object obj){
        if(obj instanceof ServiceName){
            final ServiceName name = (ServiceName)obj;
            if(managerName == null){
                if(name.managerName != null){
                    return  -1;
                }
            }else{
                if(name.managerName == null){
                    return 1;
                }else{
                    final int ret = managerName.compareTo(name.managerName);
                    if(ret != 0){
                        return ret;
                    }
                }
            }
            if(serviceName == null){
                if(name.serviceName != null){
                    return  -1;
                }
            }else{
                if(name.serviceName == null){
                    return 1;
                }else{
                    return serviceName.compareTo(name.serviceName);
                }
            }
        }
        return 0;
    }
}