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
import java.net.*;
import org.w3c.dom.*;

/**
 * �T�[�r�X��`&lt;server&gt;�v�f���^�f�[�^�B<p>
 * �T�[�r�X��`�t�@�C����&lt;server&gt;�v�f�ɋL�q���ꂽ���e���i�[���郁�^�f�[�^�R���e�i�ł���B<p>
 *
 * @author M.Takata
 * @see <a href="nimbus-service_1_0.dtd">�T�[�r�X��`�t�@�C��DTD</a>
 */
public class ServerMetaData extends MetaData implements Serializable{
    
    private static final long serialVersionUID = -5309966428718081110L;
    
    /**
     * &lt;server&gt;�v�f�̗v�f��������B<p>
     */
    public static final String SERVER_TAG_NAME = "server";
    
    /**
     * &lt;server&gt;�v�f�̎q�v�f&lt;manager-repository&gt;�v�f�̗v�f��������B<p>
     */
    private static final String REPOSITORY_TAG_NAME = "manager-repository";
    
    /**
     * &lt;server&gt;�v�f�̎q�v�f&lt;log&gt;�v�f�̗v�f��������B<p>
     */
    private static final String LOG_TAG_NAME = "log";
    
    /**
     * &lt;server&gt;�v�f�̎q�v�f&lt;message&gt;�v�f�̗v�f��������B<p>
     */
    private static final String MESSAGE_TAG_NAME = "message";
    
    /**
     * &lt;server&gt;�v�f�̎q�v�f�Ƃ��Ē�`���ꂽ&lt;manager&gt;�v�f�̃��^�f�[�^���i�[����}�b�v�B<p>
     * <table border="1">
     *   <tr bgcolor="#CCCCFF"><th colspan="2">�L�[</th><th colspan="2">�l</th></tr>
     *   <tr bgcolor="#CCCCFF"><th>�^</th><th>���e</th><th>�^</th><th>���e</th></tr>
     *   <tr><td>String</td><td>&lt;manager&gt;�v�f��name�����̒l</td><td>{@link ManagerMetaData}</td><td>&lt;manager&gt;�v�f�̃��^�f�[�^</td></tr>
     * </table>
     *
     * @see #getManager(String)
     * @see #getManagers()
     * @see #addManager(ManagerMetaData)
     */
    private final Map managers = new LinkedHashMap();
    
    /**
     * &lt;ref-url&gt;�v�f�Ŏw�肳�ꂽURL�̏W���B<p>
     *
     * @see #getReferenceURL()
     */
    private final Set referenceURL = new LinkedHashSet();
    
    /**
     * &lt;server&gt;�v�f�̎q�v�f�Ƃ��Ē�`���ꂽ&lt;property-editors&gt;�v�f�̃��^�f�[�^�B<p>
     *
     * @see #getPropertyEditors()
     * @see #addPropertyEditor(String, String)
     */
    private PropertyEditorsMetaData propertyEditors;
    
    /**
     * ���̃��^�f�[�^����`����Ă���T�[�r�X��`�t�@�C����URL�B<p>
     */
    private final URL myUrl;
    
    /**
     * &lt;manager-repository&gt;�v�f�̃��^�f�[�^�B<p>
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
     * &lt;default-log&gt;�v�f�Ŏw�肳�ꂽLogger�̃��^�f�[�^�B<p>
     *
     * @see #getDefaultLog()
     */
    private DefaultLogMetaData defaultLog;
    
    /**
     * ���������[�h����ServiceLoader
     */
    private ServiceLoader myLoader;
    
    /**
     * &lt;server-property&gt;�v�f�Ŏw�肳�ꂽ�v���p�e�B�B<p>
     */
    private Map properties = new LinkedHashMap();
    
    private String encoding;
    private String docType;
    
    private List ifDefMetaDataList;
    
    /**
     * ���̃��^�f�[�^����`����Ă���T�[�r�X��`�t�@�C����URL���������A���[�g���^�f�[�^�𐶐�����B<p>
     *
     * @param loader ���������[�h����ServiceLoader
     * @param url ���̃��^�f�[�^����`����Ă���T�[�r�X��`�t�@�C����URL
     */
    public ServerMetaData(ServiceLoader loader, URL url){
        super();
        myUrl = url;
        myLoader = loader;
    }
    
    /**
     * ���̃��^�f�[�^����`����Ă���T�[�r�X��`�t�@�C����URL���擾����B<p>
     * 
     * @return ���̃��^�f�[�^����`����Ă���T�[�r�X��`�t�@�C����URL
     */
    public URL getURL(){
        return myUrl;
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
     * ����&lt;server&gt;�v�f�̎q�v�f����A�w�肳�ꂽ�T�[�r�X��������&lt;manager&gt;�v�f�̃��^�f�[�^���擾����B<p>
     * �w�肳�ꂽ�T�[�r�X����&lt;manager&gt;�v�f���A����&lt;server&gt;�v�f�̎q�v�f�ɒ�`����ĂȂ��ꍇ�́Anull��Ԃ��B<br>
     *
     * @param name �T�[�r�X��
     * @return &lt;manager&gt;�v�f�̃��^�f�[�^
     */
    public ManagerMetaData getManager(String name){
        return (ManagerMetaData)managers.get(name);
    }
    
    /**
     * ����&lt;server&gt;�v�f�̎q�v�f�Ƃ��Ē�`����Ă���&lt;manager&gt;�v�f�̏W�����擾����B<p>
     * ����&lt;server&gt;�v�f�̎q�v�f��&lt;manager&gt;�v�f���P����`����Ă��Ȃ��ꍇ�́A��̏W����Ԃ��B<br>
     * 
     * @return &lt;manager&gt;�v�f�̏W��
     */
    public Collection getManagers(){
        return managers.values();
    }
    
    /**
     * &lt;server&gt;�v�f�̎q�v�f�Ƃ��Ē�`����Ă���&lt;manager&gt;�v�f�̃��^�f�[�^��o�^����B<p>
     *
     * @param manager &lt;manager&gt;�v�f�̃��^�f�[�^
     */
    public void addManager(ManagerMetaData manager){
        manager.setParent(this);
        manager.setServiceLoader(getServiceLoader());
        managers.put(manager.getName(), manager);
    }
    
    /**
     * &lt;server&gt;�v�f�̎q�v�f�Ƃ��Ē�`����Ă���&lt;manager&gt;�v�f�̃��^�f�[�^���폜����B<p>
     *
     * @param name &lt;manager&gt;�v�f��name�����̒l
     */
    public void removeManager(String name){
        managers.remove(name);
    }
    
    /**
     * &lt;server&gt;�v�f�̎q�v�f�Ƃ��Ē�`����Ă���&lt;manager&gt;�v�f�̃��^�f�[�^��S�č폜����B<p>
     */
    public void clearManagers(){
        managers.clear();
    }
    
    /**
     * &lt;ref-url&gt;�v�f�Ŏw�肳�ꂽURL�̏W�����擾����B<p>
     * &lt;ref-url&gt;�v�f���w�肳��Ă��Ȃ��ꍇ�́A��̏W����Ԃ��B<br>
     *
     * @return &lt;ref-url&gt;�v�f�Ŏw�肳�ꂽ���^�f�[�^�̏W��
     */
    public Set getReferenceURL(){
        Set urls = new HashSet();
        Iterator itr = referenceURL.iterator();
        while(itr.hasNext()){
            urls.add(((RefURLMetaData)itr.next()).getURL());
        }
        return urls;
    }
    
    /**
     * &lt;ref-url&gt;�v�f�Ŏw�肳�ꂽURL��ǉ�����B<p>
     *
     * @param urlStr &lt;ref-url&gt;�v�f�Ŏw�肳�ꂽURL������
     */
    public void addReferenceURL(String urlStr){
        RefURLMetaData data = new RefURLMetaData(this);
        data.setURL(urlStr);
        referenceURL.add(data);
    }
    
    /**
     * &lt;property-editors&gt;�v�f�Ŏw�肳�ꂽ�^��java.beans.PropertyEditor�̃}�b�s���O���擾����B<p>
     *
     * @return &lt;property-editors&gt;�v�f�Ŏw�肳�ꂽ�^��java.beans.PropertyEditor�̃}�b�s���O
     */
    public Map getPropertyEditors(){
        Map result = new HashMap();
        if(propertyEditors == null){
            return result;
        }
        Iterator types = propertyEditors.getPropertyEditorTypes().iterator();
        while(types.hasNext()){
            String type = (String)types.next();
            result.put(type, propertyEditors.getPropertyEditor(type));
        }
        return result;
    }
    
    /**
     * &lt;property-editors&gt;�v�f�Ŏw�肳�ꂽ�^��java.beans.PropertyEditor�̃}�b�s���O��ǉ�����B<p>
     *
     * @param type java.beans.PropertyEditor���ҏW����N���X��
     * @param editor java.beans.PropertyEditor�����N���X��
     */
    public void addPropertyEditor(String type, String editor){
        if(propertyEditors == null){
            propertyEditors = new PropertyEditorsMetaData(this);
        }
        propertyEditors.setPropertyEditor(type, editor);
    }
    
    /**
     * &lt;property-editors&gt;�v�f�Ŏw�肳�ꂽ�^��java.beans.PropertyEditor���폜����B<p>
     *
     * @param type java.beans.PropertyEditor���ҏW����N���X��
     */
    public void removePropertyEditor(String type){
        if(propertyEditors == null){
            return;
        }
        propertyEditors.removePropertyEditor(type);
    }
    
    /**
     * &lt;property-editors&gt;�v�f�Œ�`���ꂽjava.beans.PropertyEditor�̃}�b�s���O��S�č폜����B<p>
     */
    public void clearPropertyEditors(){
        propertyEditors.clearPropertyEditors();
    }
    
    /**
     * &lt;manager-repository&gt;�v�f�Ŏw�肳�ꂽ���|�W�g���̃��^�f�[�^���擾����B<p>
     * &lt;manager-repository&gt;�v�f���w�肳��Ă��Ȃ��ꍇ�́Anull��Ԃ��B<br>
     *
     * @return &lt;manager-repository&gt;�v�f�Ŏw�肳�ꂽ���|�W�g���̃��^�f�[�^
     */
    public ServiceNameMetaData getRepository(){
        return repository;
    }
    
    /**
     * &lt;manager-repository&gt;�v�f�Ŏw�肳�ꂽ���|�W�g���̃��^�f�[�^��ݒ肷��B<p>
     *
     * @param data &lt;manager-repository&gt;�v�f�Ŏw�肳�ꂽ���|�W�g���̃��^�f�[�^
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
     * &lt;default-log&gt;�v�f�Ŏw�肳�ꂽLogger�̃��^�f�[�^���擾����B<p>
     * &lt;default-log&gt;�v�f���w�肳��Ă��Ȃ��ꍇ�́Anull��Ԃ��B<br>
     *
     * @return &lt;default-log&gt;�v�f�Ŏw�肳�ꂽLogger�̃��^�f�[�^
     */
    public DefaultLogMetaData getDefaultLog(){
        return defaultLog;
    }
    
    /**
     * &lt;default-log&gt;�v�f�Ŏw�肳�ꂽLogger�̃��^�f�[�^��ݒ肷��B<p>
     *
     * @param data &lt;default-log&gt;�v�f�Ŏw�肳�ꂽLogger�̃��^�f�[�^
     */
    public void setDefaultLog(DefaultLogMetaData data){
        defaultLog = data;
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
     * �w�肳�ꂽ�v���p�e�B����&lt;server-property&gt;�v�f�Ŏw�肳�ꂽ�v���p�e�B�l���擾����B<p>
     * �Y������v���p�e�B����&lt;server-property&gt;�v�f���w�肳��Ă��Ȃ��ꍇ�́Anull��Ԃ��B<br>
     *
     * @param property �v���p�e�B��
     * @return &lt;server-property&gt;�v�f�Ŏw�肳�ꂽ�v���p�e�B�l
     */
    public String getProperty(String property){
        ServerPropertyMetaData propData
            = (ServerPropertyMetaData)properties.get(property);
        return propData == null ? null : propData.getValue();
    }
    
    /**
     * &lt;server-property&gt;�v�f�Ŏw�肳�ꂽ�v���p�e�B���擾����B<p>
     * &lt;server-property&gt;�v�f���w�肳��Ă��Ȃ��ꍇ�́A���Properties��Ԃ��B<br>
     *
     * @return &lt;server-property&gt;�v�f�Ŏw�肳�ꂽ�v���p�e�B
     */
    public Properties getProperties(){
        final Properties props = new Properties();
        Iterator propDatas = properties.values().iterator();
        while(propDatas.hasNext()){
            ServerPropertyMetaData propData = (ServerPropertyMetaData)propDatas.next();
            props.setProperty(propData.getName(), propData.getValue() == null ? "" : propData.getValue());
        }
        return props;
    }
    
    /**
     * �w�肳�ꂽ�v���p�e�B����&lt;server-property&gt;�v�f�Ŏw�肳�ꂽ�v���p�e�B�l��ݒ肷��B<p>
     *
     * @param property �v���p�e�B��
     * @param value &lt;server-property&gt;�v�f�Ŏw�肳�ꂽ�v���p�e�B�l
     */
    public void setProperty(String property, String value){
        ServerPropertyMetaData propData
            = (ServerPropertyMetaData)properties.get(property);
        if(propData == null){
            propData = new ServerPropertyMetaData(this);
        }
        propData.setName(property);
        propData.setValue(value);
        properties.put(property, propData);
    }
    
    /**
     * �w�肳�ꂽ�v���p�e�B����&lt;server-property&gt;�v�f�Ŏw�肳�ꂽ�v���p�e�B���폜����B<p>
     *
     * @param property �v���p�e�B��
     */
    public void removeProperty(String property){
        properties.remove(property);
    }
    
    /**
     * &lt;server-property&gt;�v�f�Ŏw�肳�ꂽ�v���p�e�B��S�č폜����B<p>
     */
    public void clearProperties(){
        properties.clear();
    }
    
    /**
     * &lt;manager-repository&gt;�v�f�𐶐�����B<p>
     *
     * @param managerName ���|�W�g���T�[�r�X���o�^����Ă���}�l�[�W����
     * @param serviceName ���|�W�g���T�[�r�X�̃T�[�r�X��
     * @return &lt;manager-repository&gt;�v�f�̃��^�f�[�^
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
    
    public void setDocType(String str){
        docType = str;
    }
    
    public String getDocType(){
        return docType;
    }
    
    public void setEncoding(String encoding){
        this.encoding = encoding;
    }
    
    public String getEncoding(){
        return encoding;
    }
    
    /**
     * &lt;server&gt;�v�f��Element���p�[�X���āA�������g�̏������A�y�юq�v�f�̃��^�f�[�^�̐������s���B<p>
     *
     * @param element &lt;server&gt;�v�f��Element
     * @exception DeploymentException &lt;server&gt;�v�f�̉�́A���̌��ʂɂ�郁�^�f�[�^�̐����Ɏ��s�����ꍇ
     */
    public void importXML(Element element) throws DeploymentException{
        super.importXML(element);
        
        if(!element.getTagName().equals(SERVER_TAG_NAME)){
            throw new DeploymentException(
                "Root tag must be " + SERVER_TAG_NAME + " : "
                 + element.getTagName()
            );
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
            ServerPropertyMetaData.SERVER_PROPERTY_TAG_NAME
        );
        while(propElements.hasNext()){
            ServerPropertyMetaData propData = new ServerPropertyMetaData(this);
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
        
        final Iterator refURLElements = getChildrenByTagName(
            element,
            RefURLMetaData.REF_URL_TAG_NAME
        );
        while(refURLElements.hasNext()){
            final RefURLMetaData refURLData
                 = new RefURLMetaData(this);
            if(ifdefData != null){
                refURLData.setIfDefMetaData(ifdefData);
                ifdefData.addChild(refURLData);
            }
            refURLData.importXML((Element)refURLElements.next());
            if(ifdefMatch
                 && refURLData.getURL() != null
                 && refURLData.getURL().length() != 0){
                referenceURL.add(refURLData);
            }
        }
        
        final Element repositoryElement
             = getOptionalChild(element, REPOSITORY_TAG_NAME);
        if(repositoryElement != null){
            if(ifdefMatch && repository != null){
                throw new DeploymentException("Element of " + REPOSITORY_TAG_NAME + " is duplicated.");
            }
            ServiceNameMetaData tmp = new ServiceNameMetaData(this);
            if(ifdefData != null){
                tmp.setIfDefMetaData(ifdefData);
                ifdefData.addChild(tmp);
            }
            tmp.importXML(repositoryElement);
            if(ifdefMatch){
                repository = tmp;
            }
        }
        
        final Element logElement = getOptionalChild(
            element,
            LOG_TAG_NAME
        );
        if(logElement != null){
            if(ifdefMatch && log != null){
                throw new DeploymentException("Element of " + LOG_TAG_NAME + " is duplicated.");
            }
            ServiceNameMetaData tmp = new ServiceNameMetaData(this);
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
            ServiceNameMetaData tmp = new ServiceNameMetaData(this);
            if(ifdefData != null){
                tmp.setIfDefMetaData(ifdefData);
                ifdefData.addChild(tmp);
            }
            tmp.importXML(messageElement);
            if(ifdefMatch){
                message = tmp;
            }
        }
        
        final Element defaultLogElement = getOptionalChild(
            element,
            DefaultLogMetaData.DEFAULT_LOG_TAG_NAME
        );
        if(defaultLogElement != null){
            if(ifdefMatch && defaultLog != null){
                throw new DeploymentException("Element of " + DefaultLogMetaData.DEFAULT_LOG_TAG_NAME + " is duplicated.");
            }
            DefaultLogMetaData tmp = new DefaultLogMetaData(this);
            if(ifdefData != null){
                tmp.setIfDefMetaData(ifdefData);
                ifdefData.addChild(tmp);
            }
            tmp.importXML(defaultLogElement);
            if(ifdefMatch){
                defaultLog = tmp;
            }
        }
        
        final Element editorsElement
             = getOptionalChild(element, PropertyEditorsMetaData.PROPERTY_EDITORS_TAG_NAME);
        if(editorsElement != null){
            PropertyEditorsMetaData tmp = null;
            if(ifdefMatch){
                if(propertyEditors == null){
                    tmp = new PropertyEditorsMetaData(this);
                }else{
                    tmp = propertyEditors;
                }
            }else{
                tmp = new PropertyEditorsMetaData(this);
            }
            if(ifdefData != null){
                tmp.setIfDefMetaData(ifdefData);
                ifdefData.addChild(tmp);
            }
            tmp.importXML(editorsElement);
            if(ifdefMatch){
                propertyEditors = tmp;
            }
        }
        
        final Iterator managerElements = getChildrenByTagName(
            element,
            ManagerMetaData.MANAGER_TAG_NAME
        );
        while(managerElements.hasNext()){
            final ManagerMetaData managerData
                 = new ManagerMetaData(myLoader, this);
            if(ifdefData != null){
                managerData.setIfDefMetaData(ifdefData);
                ifdefData.addChild(managerData);
            }
            managerData.importXML((Element)managerElements.next());
            if(ifdefMatch && getManager(managerData.getName()) != null){
                throw new DeploymentException("Name of manager is duplicated. name=" + managerData.getName());
            }
            if(ifdefMatch){
                addManager(managerData);
            }
        }
    }
    
    public StringBuilder toXML(StringBuilder buf){
        buf.append("<?xml version=\"1.0\"");
        if(encoding != null){
            buf.append(" encoding=\"");
            buf.append(encoding);
            buf.append("\"");
        }
        buf.append("?>");
        buf.append(LINE_SEPARATOR);
        if(docType != null){
            buf.append(docType);
            buf.append(LINE_SEPARATOR);
            buf.append(LINE_SEPARATOR);
        }
        appendComment(buf);
        buf.append('<').append(SERVER_TAG_NAME).append('>');
        if(properties.size() != 0){
            buf.append(LINE_SEPARATOR);
            final StringBuilder subBuf = new StringBuilder();
            final Iterator props = properties.values().iterator();
            while(props.hasNext()){
                final ServerPropertyMetaData propData
                    = (ServerPropertyMetaData)props.next();
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
        if(referenceURL.size() != 0){
            buf.append(LINE_SEPARATOR);
            final Iterator urls = referenceURL.iterator();
            while(urls.hasNext()){
                final RefURLMetaData refURLData = (RefURLMetaData)urls.next();
                if(refURLData.getIfDefMetaData() != null){
                    continue;
                }
                refURLData.toXML(buf);
                if(urls.hasNext()){
                    buf.append(LINE_SEPARATOR);
                }
            }
        }
        if(propertyEditors != null
            && propertyEditors.getIfDefMetaData() == null){
            buf.append(LINE_SEPARATOR);
            propertyEditors.toXML(buf);
        }
        if(defaultLog != null && defaultLog.getIfDefMetaData() == null){
            buf.append(LINE_SEPARATOR);
            buf.append(
                addIndent(defaultLog.toXML(new StringBuilder()))
            );
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
        if(managers.size() != 0){
            final Iterator datas = managers.values().iterator();
            buf.append(LINE_SEPARATOR);
            while(datas.hasNext()){
                MetaData managerData = (MetaData)datas.next();
                if(managerData.getIfDefMetaData() != null){
                    continue;
                }
                buf.append(
                    addIndent(managerData.toXML(new StringBuilder()))
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
        buf.append("</").append(SERVER_TAG_NAME).append('>');
        return buf;
    }
}
