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
import java.util.*;
import org.w3c.dom.*;

/**
 * �T�[�r�X��`&lt;manager&gt;�v�f���^�f�[�^�B<p>
 * �T�[�r�X��`�t�@�C����&lt;manager&gt;�v�f�ɋL�q���ꂽ���e���i�[���郁�^�f�[�^�R���e�i�ł���B<p>
 *
 * @author M.Takata
 * @see <a href="nimbus-service_1_0.dtd">�T�[�r�X��`�t�@�C��DTD</a>
 */
public class ManagerMetaData extends MetaData implements Serializable{
    
    private static final long serialVersionUID = 710269838167389543L;
    
    /**
     * &lt;manager&gt;�v�f�̗v�f��������B<p>
     */
    public static final String MANAGER_TAG_NAME = "manager";
    
    /**
     * &lt;manager&gt;�v�f�̎q�v�f&lt;repository&gt;�v�f�̗v�f��������B<p>
     */
    private static final String REPOSITORY_TAG_NAME = "repository";
    
    /**
     * &lt;manager&gt;�v�f�̎q�v�f&lt;log&gt;�v�f�̗v�f��������B<p>
     */
    private static final String LOG_TAG_NAME = "log";
    
    /**
     * &lt;manager&gt;�v�f�̎q�v�f&lt;message&gt;�v�f�̗v�f��������B<p>
     */
    private static final String MESSAGE_TAG_NAME = "message";
    
    /**
     * &lt;manager&gt;�v�f��name�����̑�����������B<p>
     */
    private static final String NAME_ATTRIBUTE_NAME = "name";
    
    /**
     * &lt;manager&gt;�v�f��shutdown-hook�����̑�����������B<p>
     */
    private static final String SHUTDOWN_HOOK_ATTRIBUTE_NAME = "shutdown-hook";
    
    /**
     * name�����B<p>
     *
     * @see #getName()
     */
    private String name;
    
    /**
     * shutdown-hook�����B<p>
     *
     * @see #isExistShutdownHook()
     */
    private boolean isExistShutdownHook;
    
    /**
     * &lt;manager&gt;�v�f�̎q�v�f�Ƃ��Ē�`���ꂽ&lt;service&gt;�v�f�̃��^�f�[�^���i�[����}�b�v�B<p>
     * <table border="1">
     *   <tr bgcolor="#CCCCFF"><th colspan="2">�L�[</th><th colspan="2">�l</th></tr>
     *   <tr bgcolor="#CCCCFF"><th>�^</th><th>���e</th><th>�^</th><th>���e</th></tr>
     *   <tr><td>String</td><td>&lt;service&gt;�v�f��name�����̒l</td><td>{@link ServiceMetaData}</td><td>&lt;service&gt;�v�f�̃��^�f�[�^</td></tr>
     * </table>
     *
     * @see #getService(String)
     * @see #getServices()
     * @see #addService(ServiceMetaData)
     */
    private final Map services = new LinkedHashMap();
    
    /**
     * &lt;repository&gt;�v�f�̃��^�f�[�^�B<p>
     *
     * @see #getRepository()
     */
    private ServiceNameMetaData repository;
    
    /**
     * &lt;log&gt;�v�f�Ŏw�肳�ꂽLogger�̃��^�f�[�^�B<p>
     *
     * @see #getLog()
     */
    private ServiceNameMetaData log;
    
    /**
     * &lt;message&gt;�v�f�Ŏw�肳�ꂽMessageRecordFactory�̃��^�f�[�^�B<p>
     *
     * @see #getMessage()
     */
    private ServiceNameMetaData message;
    
    /**
     * ���������[�h����ServiceLoader
     */
    private ServiceLoader myLoader;
    
    /**
     * &lt;manager-property&gt;�v�f�Ŏw�肳�ꂽ�v���p�e�B�B<p>
     */
    private Map properties = new LinkedHashMap();
    
    private List ifDefMetaDataList;
    
    /**
     * ��̃C���X�^���X�𐶐�����B<p>
     */
    public ManagerMetaData(){
    }
    
    /**
     * �e�v�f�̃��^�f�[�^�����C���X�^���X�𐶐�����B<p>
     * ManagerMetaData�̐e�v�f�́A&lt;server&gt;�v�f��\��ServerMetaData�ł���B<br>
     * 
     * @param loader ���������[�h����ServiceLoader
     * @param parent �e�v�f�̃��^�f�[�^
     * @see ServerMetaData
     */
    public ManagerMetaData(ServiceLoader loader, MetaData parent){
        super(parent);
        myLoader = loader;
    }
    
    /**
     * ���������[�h����{@link ServiceLoader}���擾����B<p>
     *
     * @return ���������[�h����ServiceLoader 
     */
    public ServiceLoader getServiceLoader(){
        return myLoader;
    }
    
    /**
     * ���������[�h����{@link ServiceLoader}��ݒ肷��B<p>
     *
     * @param loader ���������[�h����ServiceLoader 
     */
    public void setServiceLoader(ServiceLoader loader){
        myLoader = loader;
    }
    
    /**
     * ����&lt;manager&gt;�v�f��name�����̒l���擾����B<p>
     * name�������ȗ�����Ă����ꍇ�́A{@link ServiceManager#DEFAULT_NAME}��Ԃ��B<br>
     * 
     * @return name�����̒l
     */
    public String getName(){
        if(name == null){
            return ServiceManager.DEFAULT_NAME;
        }
        return name;
    }
    
    /**
     * ����&lt;manager&gt;�v�f��name�����̒l��ݒ肷��B<p>
     * 
     * @param name name�����̒l
     */
    public void setName(String name){
        this.name = name;
    }
    
    /**
     * ����&lt;manager&gt;�v�f��shutdown-hook�����̒l���擾����B<p>
     * true�̏ꍇ�́A����&lt;manager&gt;�v�f���\��{@link ServiceManager}�̔p���������s��ShutdownHook�𐶐����Đݒ肷��Bfalse�̏ꍇ�́AShutdownHook�𐶐����Ȃ��B<br>
     * shutdown-hook�������ȗ�����Ă����ꍇ�́Afalse��Ԃ��B<br>
     * 
     * @return shutdown-hook�����̒l
     */
    public boolean isExistShutdownHook(){
        return isExistShutdownHook;
    }
    
    /**
     * ����&lt;manager&gt;�v�f��shutdown-hook�����̒l��ݒ肷��B<p>
     * 
     * @param flg shutdown-hook�����̒l
     */
    public void setExistShutdownHook(boolean flg){
        isExistShutdownHook = flg;
    }
    
    /**
     * ����&lt;manager&gt;�v�f�̎q�v�f&lt;repository&gt;�v�f�Ɏw�肳�ꂽ���|�W�g���̃��^�f�[�^���擾����B<p>
     * &lt;repository&gt;�v�f���w�肳��Ă��Ȃ��ꍇ�́Anull��Ԃ��B���̏ꍇ�ARepository�̓T�[�r�X�Ƃ��ċN�����ꂸ�AMap������{@link jp.ossc.nimbus.service.repository.Repository Repository}���g�p�����B<br>
     *
     * @return &lt;repository&gt;�v�f�Ɏw�肳�ꂽ���|�W�g���̃��^�f�[�^
     * @see jp.ossc.nimbus.service.repository.Repository Repository
     */
    public ServiceNameMetaData getRepository(){
        return repository;
    }
    
    /**
     * ����&lt;manager&gt;�v�f�̎q�v�f&lt;repository&gt;�v�f�Ɏw�肳�ꂽ���|�W�g���̃��^�f�[�^��ݒ肷��B<p>
     *
     * @param data &lt;repository&gt;�v�f�Ɏw�肳�ꂽ���|�W�g���̃��^�f�[�^
     */
    public void setRepository(ServiceNameMetaData data){
        repository = data;
    }
    
    /**
     * &lt;log&gt;�v�f�Ŏw�肳�ꂽLogger�̃��^�f�[�^���擾����B<p>
     * &lt;log&gt;�v�f���w�肳��Ă��Ȃ��ꍇ�́Anull��Ԃ��B<br>
     *
     * @return &lt;log&gt;�v�f�Ŏw�肳�ꂽLogger�̃��^�f�[�^
     */
    public ServiceNameMetaData getLog(){
        return log;
    }
    
    /**
     * &lt;log&gt;�v�f�Ŏw�肳�ꂽLogger�̃��^�f�[�^��ݒ肷��B<p>
     *
     * @param data &lt;log&gt;�v�f�Ŏw�肳�ꂽLogger�̃��^�f�[�^
     */
    public void setLog(ServiceNameMetaData data){
        log = data;
    }
    
    /**
     * &lt;message&gt;�v�f�Ŏw�肳�ꂽMessageRecordFactory�̃��^�f�[�^���擾����B<p>
     * &lt;message&gt;�v�f���w�肳��Ă��Ȃ��ꍇ�́Anull��Ԃ��B<br>
     *
     * @return &lt;log&gt;�v�f�Ŏw�肳�ꂽLogger�̃��^�f�[�^
     */
    public ServiceNameMetaData getMessage(){
        return message;
    }
    
    /**
     * &lt;message&gt;�v�f�Ŏw�肳�ꂽMessageRecordFactory�̃��^�f�[�^��ݒ肷��B<p>
     *
     * @param data &lt;log&gt;�v�f�Ŏw�肳�ꂽLogger�̃��^�f�[�^
     */
    public void setMessage(ServiceNameMetaData data){
        message = data;
    }
    
    /**
     * ����&lt;manager&gt;�v�f�̎q�v�f����A�w�肳�ꂽ�T�[�r�X��������&lt;service&gt;�v�f�̃��^�f�[�^���擾����B<p>
     * �w�肳�ꂽ�T�[�r�X����&lt;service&gt;�v�f���A����&lt;manager&gt;�v�f�̎q�v�f�ɒ�`����ĂȂ��ꍇ�́Anull��Ԃ��B<br>
     *
     * @param name �T�[�r�X��
     * @return &lt;service&gt;�v�f�̃��^�f�[�^
     */
    public ServiceMetaData getService(String name){
        return (ServiceMetaData)services.get(name);
    }
    
    /**
     * ����&lt;manager&gt;�v�f�̎q�v�f�Ƃ��Ē�`����Ă���&lt;service&gt;�v�f�̃}�b�s���O���擾����B<p>
     * ����&lt;manager&gt;�v�f�̎q�v�f��&lt;service&gt;�v�f���P����`����Ă��Ȃ��ꍇ�́A���Map�C���X�^���X��Ԃ��B<br>
     * <p>
     * �����ŁA�擾�ł���Map�́A�ȉ��̂悤�ȃ}�b�s���O�ł���B<br>
     * <table border="1">
     *   <tr bgcolor="#CCCCFF"><th colspan="2">�L�[</th><th colspan="2">�l</th></tr>
     *   <tr bgcolor="#CCCCFF"><th>�^</th><th>���e</th><th>�^</th><th>���e</th></tr>
     *   <tr><td>String</td><td>&lt;service&gt;�v�f��name�����̒l</td><td>{@link ServiceMetaData}</td><td>&lt;service&gt;�v�f�̃��^�f�[�^</td></tr>
     * </table>
     * �܂��A�擾�����Map�C���X�^���X�́A����ManagerMetaData�C���X�^���X�̓����ɕێ�����Map�̎Q�Ƃł���B���̂��߁A����Map�ɑ΂���ύX�́A���̃C���X�^���X�ɉe�����y�ڂ��B<br>
     * 
     * @return &lt;service&gt;�v�f�̃}�b�s���O
     */
    public Map getServices(){
        return services;
    }
    
    /**
     * &lt;manager&gt;�v�f�̎q�v�f�Ƃ��Ē�`����Ă���&lt;service&gt;�v�f�̃��^�f�[�^��o�^����B<p>
     *
     * @param service &lt;service&gt;�v�f�̃��^�f�[�^
     */
    public void addService(ServiceMetaData service){
        service.setServiceLoader(getServiceLoader());
        service.setManager(this);
        services.put(service.getName(), service);
    }
    
    /**
     * &lt;manager&gt;�v�f�̎q�v�f�Ƃ��Ē�`����Ă���&lt;service&gt;�v�f�̃��^�f�[�^���폜����B<p>
     *
     * @param name &lt;service&gt;�v�f��name�����̒l
     */
    public void removeService(String name){
        services.remove(name);
    }
    
    /**
     * &lt;manager&gt;�v�f�̎q�v�f�Ƃ��Ē�`����Ă���&lt;service&gt;�v�f�̃��^�f�[�^��S�č폜����B<p>
     */
    public void clearServices(){
        services.clear();
    }
    
    /**
     * �v���p�e�B���̏W�����擾����B<p>
     *
     * @return &lt;manager-property&gt;�v�f�Ŏw�肳�ꂽ�v���p�e�B���̏W��
     */
    public Set getPropertyNameSet(){
        return properties.keySet();
    }
    
    /**
     * �w�肳�ꂽ�v���p�e�B����&lt;manager-property&gt;�v�f�Ŏw�肳�ꂽ�v���p�e�B�l���擾����B<p>
     * �Y������v���p�e�B����&lt;manager-property&gt;�v�f���w�肳��Ă��Ȃ��ꍇ�́Anull��Ԃ��B<br>
     *
     * @param property �v���p�e�B��
     * @return &lt;manager-property&gt;�v�f�Ŏw�肳�ꂽ�v���p�e�B�l
     */
    public String getProperty(String property){
        ManagerPropertyMetaData propData
            = (ManagerPropertyMetaData)properties.get(property);
        return propData == null ? null : propData.getValue();
    }
    
    /**
     * &lt;manager-property&gt;�v�f�Ŏw�肳�ꂽ�v���p�e�B���擾����B<p>
     * &lt;manager-property&gt;�v�f���w�肳��Ă��Ȃ��ꍇ�́A���Properties��Ԃ��B<br>
     *
     * @return &lt;manager-property&gt;�v�f�Ŏw�肳�ꂽ�v���p�e�B
     */
    public Properties getProperties(){
        final Properties props = new Properties();
        Iterator propDatas = properties.values().iterator();
        while(propDatas.hasNext()){
            ManagerPropertyMetaData propData = (ManagerPropertyMetaData)propDatas.next();
            props.setProperty(propData.getName(), propData.getValue() == null ? "" : propData.getValue());
        }
        return props;
    }
    
    /**
     * �w�肳�ꂽ�v���p�e�B����&lt;manager-property&gt;�v�f�Ŏw�肳�ꂽ�v���p�e�B�l��ݒ肷��B<p>
     *
     * @param property �v���p�e�B��
     * @param value &lt;manager-property&gt;�v�f�Ŏw�肳�ꂽ�v���p�e�B�l
     */
    public void setProperty(String property, String value){
        ManagerPropertyMetaData propData
            = (ManagerPropertyMetaData)properties.get(property);
        if(propData == null){
            propData = new ManagerPropertyMetaData(this);
        }
        propData.setName(property);
        propData.setValue(value);
        properties.put(property, propData);
    }
    
    /**
     * �w�肳�ꂽ�v���p�e�B����&lt;manager-property&gt;�v�f�Ŏw�肳�ꂽ�v���p�e�B���폜����B<p>
     *
     * @param property �v���p�e�B��
     */
    public void removeProperty(String property){
        properties.remove(property);
    }
    
    /**
     * &lt;manager-property&gt;�v�f�Ŏw�肳�ꂽ�S�Ẵv���p�e�B���폜����B<p>
     */
    public void clearProperties(){
        properties.clear();
    }
    
    /**
     * &lt;repository&gt;�v�f�𐶐�����B<p>
     *
     * @param managerName ���|�W�g���T�[�r�X���o�^����Ă���}�l�[�W����
     * @param serviceName ���|�W�g���T�[�r�X�̃T�[�r�X��
     * @return &lt;repository&gt;�v�f�̃��^�f�[�^
     */
    public ServiceNameMetaData createRepositoryMetaData(
        String managerName,
        String serviceName
    ){
        return new ServiceNameMetaData(
            this,
            REPOSITORY_TAG_NAME,
            managerName,
            serviceName
        );
    }
    
    /**
     * &lt;log&gt;�v�f�𐶐�����B<p>
     *
     * @param managerName ���O�T�[�r�X���o�^����Ă���}�l�[�W����
     * @param serviceName ���O�T�[�r�X�̃T�[�r�X��
     * @return &lt;log&gt;�v�f�̃��^�f�[�^
     */
    public ServiceNameMetaData createLogMetaData(
        String managerName,
        String serviceName
    ){
        return new ServiceNameMetaData(
            this,
            LOG_TAG_NAME,
            managerName,
            serviceName
        );
    }
    
    /**
     * &lt;message&gt;�v�f�𐶐�����B<p>
     *
     * @param managerName ���b�Z�[�W�T�[�r�X���o�^����Ă���}�l�[�W����
     * @param serviceName ���b�Z�[�W�T�[�r�X�̃T�[�r�X��
     * @return &lt;message&gt;�v�f�̃��^�f�[�^
     */
    public ServiceNameMetaData createMessageMetaData(
        String managerName,
        String serviceName
    ){
        return new ServiceNameMetaData(
            this,
            MESSAGE_TAG_NAME,
            managerName,
            serviceName
        );
    }
    
    /**
     * &lt;manager&gt;�v�f��Element���p�[�X���āA�������g�̏������A�y�юq�v�f�̃��^�f�[�^�̐������s���B<p>
     *
     * @param element &lt;manager&gt;�v�f��Element
     * @exception DeploymentException &lt;manager&gt;�v�f�̉�́A���̌��ʂɂ�郁�^�f�[�^�̐����Ɏ��s�����ꍇ
     */
    public void importXML(Element element) throws DeploymentException{
        super.importXML(element);
        
        if(!element.getTagName().equals(MANAGER_TAG_NAME)){
            throw new DeploymentException(
                "Tag must be " + MANAGER_TAG_NAME + " : "
                 + element.getTagName()
            );
        }
        
        name = getOptionalAttribute(element, NAME_ATTRIBUTE_NAME);
        final String shutdownHook
             = getOptionalAttribute(element, SHUTDOWN_HOOK_ATTRIBUTE_NAME);
        if(shutdownHook != null && shutdownHook.length() != 0){
            isExistShutdownHook = Boolean.valueOf(shutdownHook).booleanValue();
        }
        
        importXMLInner(element, null);
        
        final Iterator ifDefElements = getChildrenByTagName(
            element,
            IfDefMetaData.IFDEF_TAG_NAME
        );
        while(ifDefElements.hasNext()){
            if(ifDefMetaDataList == null){
                ifDefMetaDataList = new ArrayList();
            }
            final IfDefMetaData ifdefData
                 = new IfDefMetaData(this);
            ifdefData.importXML((Element)ifDefElements.next());
            ifDefMetaDataList.add(ifdefData);
        }
        
        if(ifDefMetaDataList == null || ifDefMetaDataList.size() == 0){
            return;
        }
        
        for(int i = 0, imax = ifDefMetaDataList.size(); i < imax; i++){
            IfDefMetaData ifdefData = (IfDefMetaData)ifDefMetaDataList.get(i);
            Element ifDefElement = ifdefData.getElement();
            if(ifDefElement == null){
                continue;
            }
            
            importXMLInner(ifDefElement, ifdefData);
            
            ifdefData.setElement(null);
        }
    }
    protected void importXMLInner(Element element, IfDefMetaData ifdefData) throws DeploymentException{
        
        final boolean ifdefMatch
            = ifdefData == null ? true : ifdefData.isMatch();
        
        final Iterator propElements = getChildrenByTagName(
            element,
            ManagerPropertyMetaData.MANAGER_PROPERTY_TAG_NAME
        );
        while(propElements.hasNext()){
            ManagerPropertyMetaData propData
                = new ManagerPropertyMetaData(this);
            if(ifdefData != null){
                propData.setIfDefMetaData(ifdefData);
                ifdefData.addChild(propData);
            }
            propData.importXML((Element)propElements.next());
            if(ifdefMatch){
                properties.put(
                    propData.getName(),
                    propData
                );
                String prop = propData.getValue();
                // �V�X�e���v���p�e�B�̒u��
                prop = Utility.replaceSystemProperty(prop);
                // �T�[�r�X���[�_�\���v���p�e�B�̒u��
                prop = Utility.replaceServiceLoderConfig(
                    prop,
                    myLoader == null ? null : myLoader.getConfig()
                );
                // �T�[�o�v���p�e�B�̒u��
                prop = Utility.replaceServerProperty(prop);
                propData.setValue(prop);
            }
        }
        
        final Element repositoryElement
             = getOptionalChild(element, REPOSITORY_TAG_NAME);
        if(repositoryElement != null){
            if(ifdefMatch && repository != null){
                throw new DeploymentException("Element of " + REPOSITORY_TAG_NAME + " is duplicated.");
            }
            ServiceNameMetaData tmp = new ServiceNameMetaData(this, getName());
            if(ifdefData != null){
                tmp.setIfDefMetaData(ifdefData);
                ifdefData.addChild(tmp);
            }
            tmp.importXML(repositoryElement);
            if(ifdefMatch){
                repository = tmp;
            }
        }
        
        final Element logElement
             = getOptionalChild(element, LOG_TAG_NAME);
        if(logElement != null){
            if(ifdefMatch && log != null){
                throw new DeploymentException("Element of " + LOG_TAG_NAME + " is duplicated.");
            }
            ServiceNameMetaData tmp = new ServiceNameMetaData(this, getName());
            if(ifdefData != null){
                tmp.setIfDefMetaData(ifdefData);
                ifdefData.addChild(tmp);
            }
            tmp.importXML(logElement);
            if(ifdefMatch){
                log = tmp;
            }
        }
        
        final Element messageElement = getOptionalChild(
            element,
            MESSAGE_TAG_NAME
        );
        if(messageElement != null){
            if(ifdefMatch && message != null){
                throw new DeploymentException("Element of " + MESSAGE_TAG_NAME + " is duplicated.");
            }
            ServiceNameMetaData tmp = new ServiceNameMetaData(this, getName());
            if(ifdefData != null){
                tmp.setIfDefMetaData(ifdefData);
                ifdefData.addChild(tmp);
            }
            tmp.importXML(messageElement);
            if(ifdefMatch){
                message = tmp;
            }
        }
        
        final Iterator serviceElements = getChildrenByTagName(
            element,
            ServiceMetaData.SERVICE_TAG_NAME
        );
        while(serviceElements.hasNext()){
            final ServiceMetaData serviceData
                 = new ServiceMetaData(myLoader, this, this);
            if(ifdefData != null){
                serviceData.setIfDefMetaData(ifdefData);
                ifdefData.addChild(serviceData);
            }
            serviceData.importXML((Element)serviceElements.next());
            if(ifdefMatch && services.containsKey(serviceData.getName())){
                throw new DeploymentException(
                    "Dupulicated service name : " + serviceData.getName()
                );
            }
            if(ifdefMatch){
                addService(serviceData);
                addDependsService(serviceData);
            }
        }
    }
    
    protected void addDependsService(ServiceMetaData serviceData) throws DeploymentException{
        List dependsList = serviceData.getDepends();
        for(int i = 0; i < dependsList.size(); i++){
            ServiceMetaData.DependsMetaData dependsData = (ServiceMetaData.DependsMetaData)dependsList.get(i);
            ServiceMetaData dependsServiceData = dependsData.getService();
            if(dependsServiceData != null){
                if(services.containsKey(dependsServiceData.getName())){
                    throw new DeploymentException(
                        "Dupulicated service name : " + dependsServiceData.getName()
                    );
                }
                addService(dependsServiceData);
                addDependsService(dependsServiceData);
            }
        }
    }
    
    public StringBuilder toXML(StringBuilder buf){
        appendComment(buf);
        buf.append('<').append(MANAGER_TAG_NAME);
        if(name != null){
            buf.append(' ').append(NAME_ATTRIBUTE_NAME)
                .append("=\"").append(name).append("\"");
        }
        if(isExistShutdownHook){
            buf.append(' ').append(SHUTDOWN_HOOK_ATTRIBUTE_NAME)
                .append("=\"").append(isExistShutdownHook).append("\"");
        }
        buf.append('>');
        if(properties.size() != 0){
            buf.append(LINE_SEPARATOR);
            final StringBuilder subBuf = new StringBuilder();
            final Iterator props = properties.values().iterator();
            while(props.hasNext()){
                final ManagerPropertyMetaData propData
                    = (ManagerPropertyMetaData)props.next();
                if(propData.getIfDefMetaData() != null){
                    continue;
                }
                propData.toXML(subBuf);
                if(props.hasNext()){
                    subBuf.append(LINE_SEPARATOR);
                }
            }
            buf.append(addIndent(subBuf));
        }
        if(repository != null && repository.getIfDefMetaData() == null){
            buf.append(LINE_SEPARATOR);
            buf.append(
                addIndent(repository.toXML(new StringBuilder()))
            );
        }
        if(log != null && log.getIfDefMetaData() == null){
            buf.append(LINE_SEPARATOR);
            buf.append(
                addIndent(log.toXML(new StringBuilder()))
            );
        }
        if(message != null && message.getIfDefMetaData() == null){
            buf.append(LINE_SEPARATOR);
            buf.append(
                addIndent(message.toXML(new StringBuilder()))
            );
        }
        if(services.size() != 0){
            buf.append(LINE_SEPARATOR);
            final Iterator datas = services.values().iterator();
            while(datas.hasNext()){
                MetaData data = (MetaData)datas.next();
                if(data.getIfDefMetaData() != null){
                    continue;
                }
                buf.append(
                    addIndent(data.toXML(new StringBuilder()))
                );
                if(datas.hasNext()){
                    buf.append(LINE_SEPARATOR);
                }
            }
        }
        buf.append(LINE_SEPARATOR);
        if(ifDefMetaDataList != null && ifDefMetaDataList.size() != 0){
            for(int i = 0, imax = ifDefMetaDataList.size(); i < imax; i++){
                IfDefMetaData ifdefData = (IfDefMetaData)ifDefMetaDataList.get(i);
                buf.append(
                    addIndent(ifdefData.toXML(new StringBuilder()))
                );
                buf.append(LINE_SEPARATOR);
            }
        }
        buf.append("</").append(MANAGER_TAG_NAME).append('>');
        return buf;
    }
    
    /**
     * ���̃C���X�^���X�̕�����\�����擾����B<p>
     *
     * @return ������\��
     */
    public String toString(){
        final StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        buf.append('{');
        if(getName() != null){
            buf.append(NAME_ATTRIBUTE_NAME);
            buf.append('=');
            buf.append(getName());
            buf.append(',');
        }
        buf.append(SHUTDOWN_HOOK_ATTRIBUTE_NAME);
        buf.append('=');
        buf.append(isExistShutdownHook);
        buf.append('}');
        return buf.toString();
    }
}
