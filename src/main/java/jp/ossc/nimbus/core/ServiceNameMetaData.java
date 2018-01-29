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

import java.io.*;
import org.w3c.dom.*;

import jp.ossc.nimbus.beans.ServiceNameEditor;

/**
 * �T�[�r�X����\���v�f���^�f�[�^�B<p>
 * 
 * @author M.Takata
 */
public class ServiceNameMetaData extends MetaData implements Serializable{
    
    private static final long serialVersionUID = 3198561088823261679L;
    
    /**
     * �T�[�r�X����\���v�f��manager-name�����̑�����������B<p>
     */
    protected static final String MANAGER_NAME_ATTRIBUTE_NAME = "manager-name";
    
    /**
     * ���̗v�f�̖��O�B<p>
     */
    protected String tagName;
    
    /**
     * �T�[�r�X����\���v�f��manager-name�����̒l�B<p>
     */
    protected String managerName;
    
    /**
     * �T�[�r�X����\���v�f�̓��e�Ŏw�肳�ꂽ�T�[�r�X���B<p>
     */
    protected String serviceName;
    
    /**
     * �e�v�f�̃��^�f�[�^�����C���X�^���X�𐶐�����B<p>
     * 
     * @param parent �e�v�f�̃��^�f�[�^
     */
    public ServiceNameMetaData(MetaData parent){
        super(parent);
    }
    
    /**
     * �e�v�f�̃��^�f�[�^�����C���X�^���X�𐶐�����B<p>
     * 
     * @param parent �e�v�f�̃��^�f�[�^
     * @param manager �T�[�r�X���o�^�����{@link ServiceManager}�̖��O
     */
    public ServiceNameMetaData(MetaData parent, String manager){
        super(parent);
        managerName = manager;
    }
    
    /**
     * �e�v�f�̃��^�f�[�^�����C���X�^���X�𐶐�����B<p>
     * 
     * @param parent �e�v�f�̃��^�f�[�^
     * @param manager �T�[�r�X���o�^�����{@link ServiceManager}�̖��O
     * @param service �T�[�r�X�̖��O
     */
    public ServiceNameMetaData(MetaData parent, String manager, String service){
        super(parent);
        managerName = manager;
        serviceName = service;
    }
    
    /**
     * �e�v�f�̃��^�f�[�^�����C���X�^���X�𐶐�����B<p>
     * 
     * @param parent �e�v�f�̃��^�f�[�^
     * @param name ���̗v�f�̖��O
     * @param manager �T�[�r�X���o�^�����{@link ServiceManager}�̖��O
     * @param service �T�[�r�X�̖��O
     */
    public ServiceNameMetaData(MetaData parent, String name, String manager, String service){
        super(parent);
        tagName = name;
        managerName = manager;
        serviceName = service;
    }
    
    /**
     * �T�[�r�X����\���v�f��manager-name�����̒l���擾����B<p>
     * manager-name�������ȗ�����Ă����ꍇ�́A{@link ServiceManager#DEFAULT_NAME}��Ԃ��B<br>
     * 
     * @return �T�[�r�X����\���v�f��manager-name�����̒l
     */
    public String getManagerName(){
        return managerName;
    }
    
    /**
     * �T�[�r�X����\���v�f��manager-name�����̒l��ݒ肷��B<p>
     * 
     * @param name �T�[�r�X����\���v�f��manager-name�����̒l
     */
    public void setManagerName(String name){
        managerName = name;
    }
    
    /**
     * �T�[�r�X����\���v�f�̓��e�Ŏw�肳�ꂽ�T�[�r�X�����擾����B<p>
     * ���e���w�肳��Ă��Ȃ��ꍇ�́Anull��Ԃ��B<br>
     * 
     * @return �T�[�r�X����\���v�f�̓��e
     */
    public String getServiceName(){
        return serviceName;
    }
    
    /**
     * �T�[�r�X����\���v�f�̓��e�Ŏw�肳�ꂽ�T�[�r�X����ݒ肷��B<p>
     * 
     * @param name �T�[�r�X����\���v�f�̓��e
     */
    public void setServiceName(String name){
        serviceName = name;
    }
    
    /**
     * �T�[�r�X����\���v�f��Element���p�[�X���āA�������g�̏��������s���B<p>
     *
     * @param element �T�[�r�X����\���v�f��Element
     * @exception DeploymentException �T�[�r�X����\���v�f�̉�͂Ɏ��s�����ꍇ
     */
    public void importXML(Element element) throws DeploymentException{
        super.importXML(element);
        
        tagName = element.getTagName();
        
        managerName = getOptionalAttribute(
            element,
            MANAGER_NAME_ATTRIBUTE_NAME,
            managerName == null ? ServiceManager.DEFAULT_NAME : managerName
        );
        
        String content = getElementContent(element);
        if(content != null && content.length() != 0){
            if(content != null){
                // �V�X�e���v���p�e�B�̒u��
                content = Utility.replaceSystemProperty(content);
                final MetaData parent = getParent();
                if(parent != null && parent instanceof ObjectMetaData){
                    ObjectMetaData objData = (ObjectMetaData)parent;
                    if(objData.getServiceLoader() != null){
                        // �T�[�r�X���[�_�\���v���p�e�B�̒u��
                        content = Utility.replaceServiceLoderConfig(
                            content,
                            objData.getServiceLoader().getConfig()
                        );
                    }
                }
                if(parent != null && parent instanceof ServiceMetaData){
                    ServiceMetaData serviceData = (ServiceMetaData)parent;
                    if(serviceData.getManager() != null){
                        // �}�l�[�W���v���p�e�B�̒u��
                        content = Utility.replaceManagerProperty(serviceData.getManager(), content);
                    }
                }
                // �T�[�o�v���p�e�B�̒u��
                content = Utility.replaceServerProperty(content);
            }
            if(content.indexOf('#') != -1){
                final ServiceNameEditor editor = new ServiceNameEditor();
                editor.setServiceManagerName(managerName);
                editor.setAsText(content);
                final ServiceName editName = (ServiceName)editor.getValue();
                if(!editName.getServiceManagerName().equals(managerName)){
                    managerName = editName.getServiceManagerName();
                }
                serviceName = editName.getServiceName();
            }else{
                serviceName = content;
            }
        }else{
            throw new DeploymentException(
                "Content of '" + tagName + "' element must not be null."
            );
        }
    }
    
    public StringBuilder toXML(StringBuilder buf){
        appendComment(buf);
        buf.append('<').append(tagName).append('>');
        if(managerName != null){
            buf.append(managerName);
        }
        if(serviceName != null){
            buf.append('#').append(serviceName);
        }
        buf.append("</").append(tagName).append('>');
        return buf;
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
        if(obj instanceof ServiceNameMetaData){
            final ServiceNameMetaData name = (ServiceNameMetaData)obj;
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
}
