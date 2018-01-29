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
 * ����&lt;ifdef&gt;�v�f���^�f�[�^�B<p>
 * �T�[�r�X��`�t�@�C����&lt;ifdef&gt;�v�f�ɋL�q���ꂽ���e���i�[���郁�^�f�[�^�R���e�i�ł���B<p>
 *
 * @author M.Takata
 * @see <a href="nimbus-service_1_0.dtd">�T�[�r�X��`�t�@�C��DTD</a>
 */
public class IfDefMetaData extends MetaData
 implements Serializable{
    
    private static final long serialVersionUID = 6757362192453652302L;
    
    /**
     * &lt;ifdef&gt;�v�f�̗v�f��������B<p>
     */
    public static final String IFDEF_TAG_NAME = "ifdef";
    
    protected static final String NAME_ATTRIBUTE_NAME = "name";
    
    protected static final String VALUE_ATTRIBUTE_NAME = "value";
    
    protected String name;
    
    protected String value;
    
    protected List childrenMetaData = new ArrayList();
    
    protected transient Element element;
    
    protected transient ServiceManager manager;
    
    protected transient ServiceLoaderConfig loaderConfig;
    
    
    /**
     * �e�v�f�̃��^�f�[�^�����C���X�^���X�𐶐�����B<p>
     * 
     * @param parent �e�v�f�̃��^�f�[�^
     */
    public IfDefMetaData(MetaData parent){
        super(parent);
    }
    
    /**
     * ����&lt;ifdef&gt;�v�f��name�����̒l���擾����B<p>
     * 
     * @return name�����̒l
     */
    public String getName(){
        return name;
    }
    
    /**
     * ����&lt;ifdef&gt;�v�f��name�����̒l��ݒ肷��B<p>
     * 
     * @param name name�����̒l
     */
    public void setName(String name){
        this.name = name;
    }
    
    /**
     * ����&lt;ifdef&gt;�v�f��value�����̒l���擾����B<p>
     * 
     * @return value�����̒l
     */
    public String getValue(){
        return value;
    }
    
    /**
     * ����&lt;ifdef&gt;�v�f��value�����̒l��ݒ肷��B<p>
     * 
     * @param value value�����̒l
     */
    public void setValue(String value){
        this.value = value;
    }
    
    public void addChild(MetaData data){
        childrenMetaData.add(data);
    }
    
    public void removeChild(MetaData data){
        childrenMetaData.remove(data);
    }
    
    public void clearChild(){
        childrenMetaData.clear();
    }
    
    public List getChildren(){
        return childrenMetaData;
    }
    
    public Element getElement(){
        return element;
    }
    
    public void setElement(Element element){
        this.element = element;
    }
    
    public boolean isMatch(){
        String prop = Utility.getProperty(
            name,
            getServiceLoaderConfig(),
            getServiceManager(),
            this
        );
        return value.equals(prop);
    }
    
    protected ServiceManager getServiceManager(){
        if(manager != null){
            return manager;
        }
        MetaData parent = this;
        while((parent = parent.getParent()) != null
            && !(parent instanceof ManagerMetaData));
        if(parent == null){
            return null;
        }
        ManagerMetaData managerData = (ManagerMetaData)parent;
        manager = ServiceManagerFactory.findManager(managerData.getName());
        return manager;
    }
    
    protected ServiceLoaderConfig getServiceLoaderConfig(){
        if(loaderConfig != null){
            return loaderConfig;
        }
        MetaData parent = this;
        while((parent = parent.getParent()) != null
            && !(parent instanceof ServerMetaData));
        if(parent == null){
            return null;
        }
        ServerMetaData serverData = (ServerMetaData)parent;
        loaderConfig = serverData.getServiceLoader().getConfig();
        return loaderConfig;
    }
    
    /**
     * &lt;ifdef&gt;�v�f��Element���p�[�X���āA�������g�̏������A�y�юq�v�f�̃��^�f�[�^�̐������s���B<p>
     *
     * @param element &lt;ifdef&gt;�v�f��Element
     * @exception DeploymentException &lt;ifdef&gt;�v�f�̉�́A���̌��ʂɂ�郁�^�f�[�^�̐����Ɏ��s�����ꍇ
     */
    public void importXML(Element element) throws DeploymentException{
        super.importXML(element);
        if(!element.getTagName().equals(IFDEF_TAG_NAME)){
            throw new DeploymentException(
                "Tag must be " + IFDEF_TAG_NAME + " : "
                 + element.getTagName()
            );
        }
        name = getUniqueAttribute(element, NAME_ATTRIBUTE_NAME);
        value = getUniqueAttribute(element, VALUE_ATTRIBUTE_NAME);
        this.element = element;
    }
    
    public StringBuilder toXML(StringBuilder buf){
        appendComment(buf);
        buf.append('<').append(IFDEF_TAG_NAME);
        buf.append(' ').append(NAME_ATTRIBUTE_NAME)
            .append("=\"").append(name).append("\"");
        buf.append(' ').append(VALUE_ATTRIBUTE_NAME)
            .append("=\"").append(value).append("\"");
        buf.append(">");
        if(childrenMetaData.size() != 0){
            buf.append(LINE_SEPARATOR);
            final Iterator datas = childrenMetaData.iterator();
            while(datas.hasNext()){
                buf.append(
                    addIndent(((MetaData)datas.next()).toXML(new StringBuilder()))
                );
                if(datas.hasNext()){
                    buf.append(LINE_SEPARATOR);
                }
            }
        }
        buf.append(LINE_SEPARATOR);
        buf.append("</").append(IFDEF_TAG_NAME).append('>');
        
        return buf;
    }
}