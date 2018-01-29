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

import jp.ossc.nimbus.beans.ServiceNameEditor;

/**
 * �T�[�r�X��`&lt;service&gt;�v�f���^�f�[�^�B<p>
 * �T�[�r�X��`�t�@�C����&lt;service&gt;�v�f�ɋL�q���ꂽ���e���i�[���郁�^�f�[�^�R���e�i�ł���B<p>
 *
 * @author M.Takata
 * @see <a href="nimbus-service_1_0.dtd">�T�[�r�X��`�t�@�C��DTD</a>
 */
public class ServiceMetaData extends ObjectMetaData implements Serializable{
    
    private static final long serialVersionUID = 1524948064493968357L;
    
    /**
     * &lt;service&gt;�v�f�̗v�f��������B<p>
     */
    public static final String SERVICE_TAG_NAME = "service";
    
    public static final String INSTANCE_TYPE_SINGLETON = "singleton";
    public static final String INSTANCE_TYPE_FACTORY = "factory";
    public static final String INSTANCE_TYPE_THREADLOCAL = "threadlocal";
    public static final String INSTANCE_TYPE_TEMPLATE = "template";
    
    private static final String DEPENDS_TAG_NAME = "depends";
    private static final String OPT_CONF_TAG_NAME = "optional-config";
    private static final String NAME_ATTRIBUTE_NAME = "name";
    private static final String INIT_STATE_ATTRIBUTE_NAME = "initState";
    private static final String INSTANCE_ATTRIBUTE_NAME = "instance";
    private static final String MANAGEMENT_ATTRIBUTE_NAME = "management";
    private static final String CREATE_TEMPLATE_ATTRIBUTE_NAME = "createTemplate";
    private static final String TEMPLATE_ATTRIBUTE_NAME = "template";
    
    private String name;
    
    private String initState = Service.STATES[Service.STARTED];
    
    private int initStateValue = Service.STARTED;
    
    private String instance = INSTANCE_TYPE_SINGLETON;
    
    private boolean isFactory;
    
    private boolean isTemplate;
    
    private boolean isManagement;
    
    private boolean isCreateTemplate = true;
    
    private transient Element optionalConfig;
    
    private List depends = new ArrayList();
    
    private ManagerMetaData manager;
    
    private String template;
    
    /**
     * ��̃C���X�^���X�𐶐�����B<p>
     */
    public ServiceMetaData(){
    }
    
    /**
     * �w�肵���T�[�r�X���A�����N���X���̃��^�f�[�^�����C���X�^���X�𐶐�����B<p>
     * @param name �T�[�r�X��
     * @param code �����N���X��
     */
    public ServiceMetaData(String name, String code){
        setName(name);
        setCode(code);
    }
    
    /**
     * �e�v�f�̃��^�f�[�^�����C���X�^���X�𐶐�����B<p>
     * ServiceMetaData�̐e�v�f�́A&lt;manager&gt;�v�f��\��ManagerMetaData�A�܂��́A&lt;depends&gt;�v�f��\��DependsMetaData�ł���B<br>
     * 
     * @param loader ���������[�h����ServiceLoader
     * @param parent �e�v�f�̃��^�f�[�^
     * @param manager ����&lt;service&gt;�v�f�Œ�`���ꂽ�T�[�r�X���o�^�����{@link ServiceManager}��&lt;manager&gt;�v�f��\��ManagerMetaData
     * @see ManagerMetaData
     * @see ServiceMetaData.DependsMetaData
     */
    public ServiceMetaData(
        ServiceLoader loader,
        MetaData parent,
        ManagerMetaData manager
    ){
        super(loader, parent, manager.getName());
        this.manager = manager;
    }
    
    /**
     * ����&lt;service&gt;�v�f��name�����̒l���擾����B<p>
     * 
     * @return name�����̒l
     */
    public String getName(){
        return name;
    }
    
    /**
     * ����&lt;service&gt;�v�f��name�����̒l��ݒ肷��B<p>
     * 
     * @param name name�����̒l
     */
    public void setName(String name){
        this.name = name;
    }
    
    /**
     * ����&lt;service&gt;�v�f��initState�����̒l���擾����B<p>
     * 
     * @return initState�����̒l
     */
    public String getInitState(){
        return initState;
    }
    
    /**
     * ����&lt;service&gt;�v�f��initState�����̒l��ݒ肷��B<p>
     * 
     * @param state initState�����̒l
     */
    public void setInitState(String state){
        for(int i = 0; i < Service.STATES.length; i++){
            if(Service.STATES[i].equals(state)){
                initStateValue = i;
                initState = state;
                return;
            }
        }
    }
    
    /**
     * ����&lt;service&gt;�v�f��instance�����̒l���擾����B<p>
     * 
     * @return instance�����̒l
     */
    public String getInstance(){
        return instance;
    }
    
    /**
     * ����&lt;service&gt;�v�f��instance�����̒l��ݒ肷��B<p>
     * 
     * @param val instance�����̒l
     */
    public void setInstance(String val){
        if(INSTANCE_TYPE_SINGLETON.equals(val)){
            instance = val;
        }else if(INSTANCE_TYPE_FACTORY.equals(val)
             || INSTANCE_TYPE_THREADLOCAL.equals(val)){
            isFactory = true;
            instance = val;
        }else if(INSTANCE_TYPE_TEMPLATE.equals(val)){
            isTemplate = true;
            instance = val;
        }else{
            throw new IllegalArgumentException(val);
        }
    }
    
    /**
     * ����&lt;service&gt;�v�f���\���T�[�r�X���t�@�N�g�����ǂ����𔻒肷��B<p>
     *
     * @return ����&lt;service&gt;�v�f���\���T�[�r�X���t�@�N�g���̏ꍇtrue
     */
    public boolean isFactory(){
        return isFactory;
    }
    
    /**
     * ����&lt;service&gt;�v�f���\���T�[�r�X���t�@�N�g�����ǂ�����ݒ肷��B<p>
     *
     * @param isFactory true�̏ꍇ�A����&lt;service&gt;�v�f���\���T�[�r�X���t�@�N�g��
     */
    public void setFactory(boolean isFactory){
        this.isFactory = isFactory;
    }
    
    /**
     * ����&lt;service&gt;�v�f��template�����̒l���擾����B<p>
     * 
     * @return template�����̒l
     */
    public String getTemplateName(){
        return template;
    }
    
    /**
     * ����&lt;service&gt;�v�f��template�����̒l��ݒ肷��B<p>
     * 
     * @param name template�����̒l
     */
    public void setTemplateName(String name){
        this.template = name;
    }
    
    /**
     * ����&lt;service&gt;�v�f���\���T�[�r�X���e���v���[�g���ǂ����𔻒肷��B<p>
     *
     * @return ����&lt;service&gt;�v�f���\���T�[�r�X���e���v���[�g�̏ꍇtrue
     */
    public boolean isTemplate(){
        return isTemplate;
    }
    
    /**
     * ����&lt;service&gt;�v�f���\���T�[�r�X���e���v���[�g���ǂ�����ݒ肷��B<p>
     *
     * @param isTemplate true�̏ꍇ�A����&lt;service&gt;�v�f���\���T�[�r�X���e���v���[�g
     */
    public void setTemplate(boolean isTemplate){
        this.isTemplate = isTemplate;
    }
    
    /**
     * ����&lt;service&gt;�v�f��management�����̒l���擾����B<p>
     * 
     * @return management�����̒l
     */
    public boolean isManagement(){
        return isManagement;
    }
    
    /**
     * ����&lt;service&gt;�v�f��management�����̒l��ݒ肷��B<p>
     * 
     * @param flg management�����̒l
     */
    public void setManagement(boolean flg){
        isManagement = flg;
    }
    
    /**
     * ����&lt;service&gt;�v�f��createTemplate�����̒l���擾����B<p>
     * 
     * @return createTemplate�����̒l
     */
    public boolean isCreateTemplate(){
        return isCreateTemplate;
    }
    
    /**
     * ����&lt;service&gt;�v�f��createTemplate�����̒l��ݒ肷��B<p>
     * 
     * @param flg createTemplate�����̒l
     */
    public void setCreateTemplate(boolean flg){
        isCreateTemplate = flg;
    }
    
    /**
     * ����&lt;service&gt;�v�f��initState�����̒l���擾����B<p>
     * 
     * @return initState�����̒l
     */
    public int getInitStateValue(){
        return initStateValue;
    }
    
    /**
     * ����&lt;service&gt;�v�f��initState�����̒l��ݒ肷��B<p>
     * 
     * @param state initState�����̒l
     */
    public void setInitStateValue(int state){
        if(state >= 0 && Service.STATES.length > state){
            initStateValue = state;
            initState = Service.STATES[state];
        }
    }
    
    /**
     * ����&lt;service&gt;�v�f�̎q�v�f&lt;optional-config&gt;�v�f���擾����B<p>
     *
     * @return �q�v�f&lt;optional-config&gt;�v�f
     */
    public Element getOptionalConfig(){
        return optionalConfig;
    }
    
    /**
     * ����&lt;service&gt;�v�f�̎q�v�f&lt;optional-config&gt;�v�f��ݒ肷��B<p>
     *
     * @param option �q�v�f&lt;optional-config&gt;�v�f
     */
    public void setOptionalConfig(Element option){
        optionalConfig = option;
    }
    
    /**
     * ����&lt;service&gt;�v�f�̎q�v�f&lt;depends&gt;�v�f��\��{@link ServiceMetaData.DependsMetaData}�̃��X�g���擾����B<p>
     *
     * @return �q�v�f&lt;depends&gt;�v�f��\��DependsMetaData�̃��X�g
     */
    public List getDepends(){
        return depends;
    }
    
    /**
     * ����&lt;service&gt;�v�f�̎q�v�f&lt;depends&gt;�v�f��\��{@link ServiceMetaData.DependsMetaData}��ǉ�����B<p>
     *
     * @param depends �q�v�f&lt;depends&gt;�v�f��\��DependsMetaData
     */
    public void addDepends(DependsMetaData depends){
        this.depends.add(depends);
    }
    
    /**
     * ����&lt;service&gt;�v�f�̎q�v�f&lt;depends&gt;�v�f��\��{@link ServiceMetaData.DependsMetaData}���폜����B<p>
     *
     * @param depends �q�v�f&lt;depends&gt;�v�f��\��DependsMetaData
     */
    public void removeDepends(DependsMetaData depends){
        this.depends.remove(depends);
    }
    
    /**
     * ����&lt;service&gt;�v�f�̎q�v�f&lt;depends&gt;�v�f��\��{@link ServiceMetaData.DependsMetaData}��S�č폜����B<p>
     */
    public void clearDepends(){
        this.depends.clear();
    }
    
    /**
     * ����&lt;service&gt;�v�f�Œ�`���ꂽ�T�[�r�X���o�^�����T�[�r�X�}�l�[�W�����`����&lt;manager&gt;�v�f��\��ManagerMetaData���擾����B<p>
     *
     * @return ����&lt;service&gt;�v�f�Œ�`���ꂽ�T�[�r�X���o�^�����T�[�r�X�}�l�[�W�����`����&lt;manager&gt;�v�f��\��ManagerMetaData
     */
    public ManagerMetaData getManager(){
        return manager;
    }
    
    /**
     * ����&lt;service&gt;�v�f�Œ�`���ꂽ�T�[�r�X���o�^�����T�[�r�X�}�l�[�W�����`����&lt;manager&gt;�v�f��\��ManagerMetaData��ݒ肷��B<p>
     *
     * @param manager ����&lt;service&gt;�v�f�Œ�`���ꂽ�T�[�r�X���o�^�����T�[�r�X�}�l�[�W�����`����&lt;manager&gt;�v�f��\��ManagerMetaData
     */
    public void setManager(ManagerMetaData manager){
        this.manager = manager;
    }
    
    /**
     * &lt;depends&gt;�v�f�𐶐�����B<p>
     *
     * @param managerName �ˑ�����T�[�r�X���o�^����Ă���}�l�[�W����
     * @param serviceName �ˑ�����T�[�r�X�̃T�[�r�X��
     * @return &lt;depends&gt;�v�f�̃��^�f�[�^
     */
    public DependsMetaData createDependsMetaData(
        String managerName,
        String serviceName
    ){
        return new DependsMetaData(
            managerName,
            serviceName
        );
    }
    
    /**
     * �v�f����service�ł��鎖���`�F�b�N����B<p>
     *
     * @param element service�v�f
     * @exception DeploymentException �v�f����service�łȂ��ꍇ
     */
    protected void checkTagName(Element element) throws DeploymentException{
        if(!element.getTagName().equals(SERVICE_TAG_NAME)){
            throw new DeploymentException(
                "Tag must be " + SERVICE_TAG_NAME + " : "
                 + element.getTagName()
            );
        }
    }
    
    /**
     * �e���v���[�g��K�p�������^�f�[�^�𐶐�����B<p>
     *
     * @param loader ServiceLoader
     * @return �e���v���[�g�K�p��̃��^�f�[�^
     * @exception ServiceNotFoundException �e���v���[�g�Ƃ��Ďw�肳��Ă���T�[�r�X���^�f�[�^��������Ȃ��ꍇ
     */
    public ServiceMetaData applyTemplate(ServiceLoader loader) throws ServiceNotFoundException{
        if(getTemplateName() == null){
            return this;
        }
        ServiceNameEditor editor = new ServiceNameEditor();
        if(getManager() != null){
            editor.setServiceManagerName(getManager().getName());
        }
        editor.setAsText(getTemplateName());
        ServiceName temlateServiceName = (ServiceName)editor.getValue();
        ServiceMetaData templateData = null;
        if(loader != null){
            templateData = loader.getServiceMetaData(
                temlateServiceName.getServiceManagerName(),
                temlateServiceName.getServiceName()
            );
        }
        if(templateData == null){
            templateData = ServiceManagerFactory.getServiceMetaData(temlateServiceName);
        }
        templateData = templateData.applyTemplate(loader);
        
        ServiceMetaData result = (ServiceMetaData)clone();
        if(result.constructor == null){
            result.constructor = templateData.constructor;
        }
        if(templateData.fields.size() != 0){
            Iterator entries = templateData.fields.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                if(!result.fields.containsKey(entry.getKey())){
                    result.fields.put(entry.getKey(), entry.getValue());
                }
            }
        }
        if(templateData.attributes.size() != 0){
            Iterator entries = templateData.attributes.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                if(!result.attributes.containsKey(entry.getKey())){
                    result.attributes.put(entry.getKey(), entry.getValue());
                }
            }
        }
        if(templateData.invokes.size() != 0){
            result.invokes.addAll(templateData.invokes);
        }
        if(result.optionalConfig == null){
            result.optionalConfig = templateData.optionalConfig;
        }
        if(templateData.depends.size() != 0){
            result.depends.addAll(templateData.depends);
        }
        return result;
    }
    
    /**
     * &lt;service&gt;�v�f��Element���p�[�X���āA�������g�̏������A�y�юq�v�f�̃��^�f�[�^�̐������s���B<p>
     *
     * @param element &lt;service&gt;�v�f��Element
     * @exception DeploymentException &lt;service&gt;�v�f�̉�́A���̌��ʂɂ�郁�^�f�[�^�̐����Ɏ��s�����ꍇ
     */
    public void importXML(Element element) throws DeploymentException{
        
        name = getUniqueAttribute(element, NAME_ATTRIBUTE_NAME);
        if(name != null){
            // �V�X�e���v���p�e�B�̒u��
            name = Utility.replaceSystemProperty(name);
            if(getServiceLoader() != null){
                // �T�[�r�X���[�_�\���v���p�e�B�̒u��
                name = Utility.replaceServiceLoderConfig(
                    name,
                    getServiceLoader().getConfig()
                );
            }
            if(manager != null){
                // �}�l�[�W���v���p�e�B�̒u��
                name = Utility.replaceManagerProperty(manager, name);
            }
            // �T�[�o�v���p�e�B�̒u��
            name = Utility.replaceServerProperty(name);
        }
        
        String tmpInitState
             = getOptionalAttribute(element, INIT_STATE_ATTRIBUTE_NAME);
        if(tmpInitState != null){
            // �V�X�e���v���p�e�B�̒u��
            tmpInitState = Utility.replaceSystemProperty(tmpInitState);
            if(getServiceLoader() != null){
                // �T�[�r�X���[�_�\���v���p�e�B�̒u��
                tmpInitState = Utility.replaceServiceLoderConfig(
                    tmpInitState,
                    getServiceLoader().getConfig()
                );
            }
            if(manager != null){
                // �}�l�[�W���v���p�e�B�̒u��
                tmpInitState = Utility.replaceManagerProperty(manager, tmpInitState);
            }
            // �T�[�o�v���p�e�B�̒u��
            tmpInitState = Utility.replaceServerProperty(tmpInitState);
        }
        
        if(Service.STATES[Service.CREATED].equals(tmpInitState)){
            initState = getOptionalAttribute(
                element,
                INIT_STATE_ATTRIBUTE_NAME
            );
            initStateValue = Service.CREATED;
        }else if(Service.STATES[Service.STARTED].equals(tmpInitState)){
            initState = getOptionalAttribute(
                element,
                INIT_STATE_ATTRIBUTE_NAME
            );
            initStateValue = Service.STARTED;
        }
        setInstance(
            getOptionalAttribute(
                element,
                INSTANCE_ATTRIBUTE_NAME,
                INSTANCE_TYPE_SINGLETON
            )
        );
        final String management = getOptionalAttribute(
            element,
            MANAGEMENT_ATTRIBUTE_NAME
        );
        if(management != null){
            isManagement = Boolean.valueOf(management).booleanValue();
        }
        final String createTemplate = getOptionalAttribute(
            element,
            CREATE_TEMPLATE_ATTRIBUTE_NAME
        );
        if(createTemplate != null){
            isCreateTemplate = Boolean.valueOf(createTemplate).booleanValue();
        }
        
        template = getOptionalAttribute(
            element,
            TEMPLATE_ATTRIBUTE_NAME
        );
        
        super.importXML(element);
    }
    
    protected void importXMLInner(Element element, IfDefMetaData ifdefData) throws DeploymentException{
        
        super.importXMLInner(element, ifdefData);
        
        final boolean ifdefMatch
            = ifdefData == null ? true : ifdefData.isMatch();
        
        final Element optionalConfig = getOptionalChild(element, OPT_CONF_TAG_NAME);
        if(ifdefMatch){
            this.optionalConfig = optionalConfig;
        }
        
        final Iterator dependsElements = getChildrenByTagName(
            element,
            DEPENDS_TAG_NAME
        );
        while(dependsElements.hasNext()){
            final Element dependsElement = (Element)dependsElements.next();
            final DependsMetaData dependsData = new DependsMetaData(manager.getName());
            if(ifdefData != null){
                dependsData.setIfDefMetaData(ifdefData);
                ifdefData.addChild(dependsData);
            }
            dependsData.importXML(dependsElement);
            if(ifdefMatch){
                depends.add(dependsData);
            }
        }
    }
    
    public StringBuilder toXML(StringBuilder buf){
        appendComment(buf);
        buf.append('<').append(SERVICE_TAG_NAME);
        if(name != null){
            buf.append(' ').append(NAME_ATTRIBUTE_NAME)
                .append("=\"").append(name).append("\"");
        }
        if(code != null){
            buf.append(' ').append(CODE_ATTRIBUTE_NAME)
                .append("=\"").append(code).append("\"");
        }
        if(initState != null
             && !Service.STATES[Service.STARTED].equals(initState)){
            buf.append(' ').append(INIT_STATE_ATTRIBUTE_NAME)
                .append("=\"").append(initState).append("\"");
        }
        if(instance != null && !INSTANCE_TYPE_SINGLETON.equals(instance)){
            buf.append(' ').append(INSTANCE_ATTRIBUTE_NAME)
                .append("=\"").append(instance).append("\"");
        }
        if(isManagement){
            buf.append(' ').append(MANAGEMENT_ATTRIBUTE_NAME)
                .append("=\"").append(isManagement).append("\"");
        }
        if(!isCreateTemplate){
            buf.append(' ').append(CREATE_TEMPLATE_ATTRIBUTE_NAME)
                .append("=\"").append(isCreateTemplate).append("\"");
        }
        
        if(constructor == null && fields.size() == 0
             && attributes.size() == 0 && invokes.size() == 0
             && depends.size() == 0
             && (ifDefMetaDataList == null || ifDefMetaDataList.size() == 0)
        ){
            buf.append("/>");
        }else{
            buf.append('>');
            if(constructor != null && constructor.getIfDefMetaData() == null){
                buf.append(LINE_SEPARATOR);
                buf.append(
                    addIndent(constructor.toXML(new StringBuilder()))
                );
            }
            if(fields.size() != 0){
                buf.append(LINE_SEPARATOR);
                final Iterator datas = fields.values().iterator();
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
            if(attributes.size() != 0){
                buf.append(LINE_SEPARATOR);
                final Iterator datas = attributes.values().iterator();
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
            if(invokes.size() != 0){
                buf.append(LINE_SEPARATOR);
                final Iterator datas = invokes.iterator();
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
            if(depends.size() != 0){
                buf.append(LINE_SEPARATOR);
                final Iterator datas = depends.iterator();
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
            buf.append("</").append(SERVICE_TAG_NAME).append('>');
        }
        return buf;
    }
    
    /**
     * ���̃C���X�^���X�̕����𐶐�����B<p>
     *
     * @return ���̃C���X�^���X�̕���
     */
    public Object clone(){
        ServiceMetaData clone = (ServiceMetaData)super.clone();
        clone.depends = new ArrayList(depends);
        return clone;
    }
    
    /**
     * ���̃C���X�^���X�̕�����\�����擾����B<p>
     *
     * @return ������\��
     */
    public String toString(){
        final StringBuilder buf = new StringBuilder();
        buf.append(getClass().getName());
        buf.append('@');
        buf.append(hashCode());
        buf.append('{');
        if(getName() != null){
            buf.append(NAME_ATTRIBUTE_NAME);
            buf.append('=');
            buf.append(getName());
            buf.append(',');
        }
        buf.append(CODE_ATTRIBUTE_NAME);
        buf.append('=');
        buf.append(getCode());
        buf.append('}');
        return buf.toString();
    }
    
    /**
     * �ˑ��֌W��`&lt;depends&gt;�v�f���^�f�[�^�B<p>
     * �T�[�r�X��`�t�@�C����&lt;depends&gt;�v�f�ɋL�q���ꂽ���e���i�[���郁�^�f�[�^�R���e�i�ł���B<p>
     *
     * @author M.Takata
     * @see <a href="nimbus-service_1_0.dtd">�T�[�r�X��`�t�@�C��DTD</a>
     */
    public class DependsMetaData extends ServiceNameMetaData
     implements Serializable{
        
        private static final long serialVersionUID = -2867707582371947233L;
        
        private ServiceMetaData serviceData;
        
        /**
         * �w�肳�ꂽ�T�[�r�X�}�l�[�W���ɓo�^���ꂽ�T�[�r�X�Ɉˑ����鎖��\���ˑ��֌W��`�̃C���X�^���X�𐶐�����B<p>
         *
         * @param manager �ˑ�����T�[�r�X���o�^�����T�[�r�X�}�l�[�W����
         */
        public DependsMetaData(String manager){
            super(ServiceMetaData.this, manager);
        }
        
        /**
         * �w�肳�ꂽ�T�[�r�X�Ɉˑ����鎖��\���ˑ��֌W��`�̃C���X�^���X�𐶐�����B<p>
         *
         * @param manager �ˑ�����T�[�r�X���o�^�����T�[�r�X�}�l�[�W����
         * @param service �ˑ�����T�[�r�X��
         */
        public DependsMetaData(String manager, String service){
            super(ServiceMetaData.this, DEPENDS_TAG_NAME, manager, service);
        }
        
        /**
         * &lt;depends&gt;�v�f�̎q�v�f��&lt;service&gt;�v�f��\��ServiceMetaData���擾����B<p>
         *
         * @return &lt;service&gt;�v�f��\��ServiceMetaData
         */
        public ServiceMetaData getService(){
            return serviceData;
        }
        
        /**
         * &lt;depends&gt;�v�f��Element���p�[�X���āA�������g�̏������A�y�юq�v�f�̃��^�f�[�^�̐������s���B<p>
         *
         * @param element &lt;depends&gt;�v�f��Element
         * @exception DeploymentException &lt;depends&gt;�v�f�̉�́A���̌��ʂɂ�郁�^�f�[�^�̐����Ɏ��s�����ꍇ
         */
        public void importXML(Element element) throws DeploymentException{
            
            if(!element.getTagName().equals(DEPENDS_TAG_NAME)){
                throw new DeploymentException(
                    "Tag must be " + DEPENDS_TAG_NAME + " : "
                     + element.getTagName()
                );
            }
            tagName = element.getTagName();
            final Element serviceElement = getOptionalChild(
                element,
                SERVICE_TAG_NAME
            );
            final boolean ifdefMatch
                = DependsMetaData.this.getIfDefMetaData() == null ? true : DependsMetaData.this.getIfDefMetaData().isMatch();
            if(serviceElement == null){
                final ServiceMetaData parent = (ServiceMetaData)getParent();
                if(parent != null){
                    setManagerName(parent.getManager().getName());
                }
                super.importXML(element);
            }else{
                final ServiceMetaData serviceData
                     = new ServiceMetaData(myLoader, this, manager);
                serviceData.importXML(serviceElement);
                serviceData.setIfDefMetaData(DependsMetaData.this.getIfDefMetaData());
                setServiceName(serviceData.getName());
                if(ifdefMatch){
                    this.serviceData = serviceData;
                    setManagerName(manager.getName());
                }
            }
        }
    }
}
