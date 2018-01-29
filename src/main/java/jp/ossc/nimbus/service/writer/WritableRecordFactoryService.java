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

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.lang.*;
import java.util.*;
import java.io.Serializable;

/**
 * java.util.Map��WritableRecord�ɕϊ�����{@link WritableRecordFactory}�T�[�r�X�B<p>
 *
 * @author Y.Tokuda
 */
public class WritableRecordFactoryService extends ServiceBase
 implements WritableRecordFactory, WritableRecordFactoryServiceMBean,
            Serializable{
    
    private static final long serialVersionUID = 5249532509800152052L;
    
    //�����o�ϐ�
    /** �V���v���G�������g�����N���X�� */
    protected static final String SIMPLE_ELEMENT_NAME
         = SimpleElement.class.getName();
    /** �t�H�[�}�b�g��`������ */
    protected String mFormat;
    /** �t�H�[�}�b�g��`�����񂩂琶�������ParsedElement�^��ArrayList */
    protected List mParsedElements;
    /** WritableElement�̎����N���X���n�b�V�� */
    protected Properties mImplClasses;
    /** WritableElement�̎����T�[�r�X���n�b�V�� */
    protected Properties mImplServiceNames;
    protected Map mImplServiceNameMap;
    
    // WritableRecordFactoryServiceMBean��JavaDoc
    public void setImplementClasses(Properties prop){
        mImplClasses = prop;
    }
    
    // WritableRecordFactoryServiceMBean��JavaDoc
    public Properties getImplementClasses(){
        return mImplClasses;
    }
    
    // WritableRecordFactoryServiceMBean��JavaDoc
    public void setImplementServiceNames(Properties prop){
        mImplServiceNames = prop;
    }
    
    // WritableRecordFactoryServiceMBean��JavaDoc
    public Properties getImplementServiceNames(){
        return mImplServiceNames;
    }
    
    // WritableRecordFactoryServiceMBean��JavaDoc
    public void setFormat(String fmt){
        mFormat = fmt;
    }
    
    // WritableRecordFactoryServiceMBean��JavaDoc
    public String getFormat(){
        return mFormat;
    }
    
    /**
     * �����������s���B<p>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     * <li> �Z�b�^�[�ŃL�[���[�h,WritableElement�̎����N���X���̑g���i�[����Properties���^�����Ȃ������ꍇ�A���Properties�������o�ϐ�mImplClasses�ɃZ�b�g����B<br>
     * </ol>
     *
     * @exception Exception �T�[�r�X�̐��������Ɏ��s�����ꍇ
     */
    public void createService() throws Exception {
        if(mImplClasses == null){
            mImplClasses = new Properties();
        }
        mImplServiceNameMap = new HashMap();
    }
    
    /**
     * �J�n�������s���B<p>
     * ���̃��\�b�h�ɂ́A�ȉ��̎����������Ȃ��Ă���B<br>
     * <ol>
     * <li>�t�H�[�}�b�g��������p�[�X����B</li>
     * <li>WritableElement�̎����N���X���C���X�^���X���\���`�F�b�N����B</li>
     * </ol>
     *
     * @exception Exception �T�[�r�X�̊J�n�����Ɏ��s�����ꍇ
     */    
    public void startService()throws Exception {
        try{
            mParsedElements = parseFormat(mFormat);
        }
        catch(IllegalArgumentException ex){
            //�s���t�H�[�}�b�g�̏ꍇ
            getLogger().write("SSWRF00001",mFormat,ex);
            throw ex;        
        }
        //�^����ꂽ�����N���X���ŃC���X�^���X�𐶐��ł��邩�ǂ����m�F
        Collection implClasses = mImplClasses.values();
        Iterator it = implClasses.iterator();
        while(it.hasNext()){
            String implClassName = (String)it.next();
            try{
                getInstance(implClassName);    
            }
            catch(ServiceException e){
                //�s�������N���X���̏ꍇ
                getLogger().write("SSWRF00002",implClassName,e);
                throw e;    
            }
        }
        
        if(mImplServiceNames != null){
            mImplServiceNameMap.clear();
            final ServiceNameEditor editor = new ServiceNameEditor();
            editor.setServiceManagerName(getServiceManagerName());
            final Iterator keys = mImplServiceNames.keySet().iterator();
            while(keys.hasNext()){
                final String key = (String)keys.next();
                editor.setAsText(mImplServiceNames.getProperty(key));
                mImplServiceNameMap.put(
                    key,
                    editor.getValue()
                );
            }
        }
    }
    
    /**
     * ��~�������s���B<p>
     * ���̃��\�b�h�ɂ́A�ȉ��̎����������Ȃ��Ă���B<br>
     * <ol>
     * <li>mParsedElement��j������</li>
     * </ol>
     *
     * @exception Exception �T�[�r�X�̒�~�����Ɏ��s�����ꍇ
     */    
    public void stopService() throws Exception {
        mParsedElements = null;
    }
    /**
     * �j���������s���B<p>
     * ���̃��\�b�h�ɂ́A�ȉ��̎����������Ȃ��Ă���B<br>
     * <ol>
     * <li>mImplClasses��j������</li>
     * </ol>
     *
     * @exception Exception �T�[�r�X�̔j�������Ɏ��s�����ꍇ
     */            
    public void destroyService() throws Exception {
        mImplClasses = null;
    }
    
    /**
     * �w�肳�ꂽ�o�͗v�f��Map����{@link WritableRecord}�𐶐�����B<p>
     * {@link #setFormat(String)}�Ŏw�肳�ꂽ�t�H�[�}�b�g�ɏ]���āA{@link WritableElement}�𐶐����A����������i�[����WritableRecord��Ԃ��B<br>
     * setFormat(String)�Ŏw�肳�ꂽ�t�H�[�}�b�g���ɁA�L�[���w�肳��Ă���ꍇ�́A�o�͗v�f��Map����l�����o���āA{@link #setImplementClasses(Properties)}�܂���{@link #setImplementServiceNames(Properties)}�Ń}�b�s���O���ꂽWritableElement�C���X�^���X�Ɋi�[����B<br>
     * 
     * @param elements �o�͗v�f��Map�I�u�W�F�N�g
     * @return MessageWriter�T�[�r�X�̓��̓I�u�W�F�N�g�ƂȂ�WritableRecord
     */
    public WritableRecord createRecord(Object elements){
        if(getState() != STARTED){
            throw new IllegalServiceStateException(this);
        }
        WritableRecord writableRec = new WritableRecord();
        if(mFormat == null || mFormat.length() == 0){
            //�P��elements�̓��e��WritableRecord��add
            final Iterator keys = getElementKeys(elements).iterator();
            while(keys.hasNext()){
                final String key = keys.next().toString();
                WritableElement elem = createElement(
                    key,
                    getElementValue(key, elements)
                );
                writableRec.addElement(elem);
            }
        }
        else{
            //�t�H�[�}�b�g�ɏ]����WritableRecord�𐶐�
            for(int rCnt=0, max = mParsedElements.size();rCnt < max;rCnt++){
                ParsedElement parsedElem = (ParsedElement)mParsedElements.get(rCnt);
                if(parsedElem.isKeyElement()){
                    //�L�[�Ȃ̂ŁA�Y���L�[�������ڂ�elements����T���AWritableRecord��add����B
                    final String key = parsedElem.getValue();
                    WritableElement elem = createElement(
                        key,
                        getElementValue(key, elements)
                    );
                    if(elem != null){
                        writableRec.addElement(elem);
                    }
                }
                else{
                    //�\���p������v�f��writableRecord��add
                    SimpleElement simpleElem = new SimpleElement();
                    simpleElem.setKey(simpleElem);
                    simpleElem.setValue(parsedElem.getValue());
                    writableRec.addElement(simpleElem);
                }
            }
        }
        return writableRec;
    }
    
    /**
     * �w�肳�ꂽ�o�͗v�f��Map�����L�[���̏W�����擾����B<p>
     *
     * @param elements �o�͗v�f�̃}�b�v
     * @return �L�[���̏W��
     */
    protected Set getElementKeys(Object elements){
        return ((Map)elements).keySet();
    }
    
    /**
     * �o�͗v�f����w�肳�ꂽ�L�[�̒l�����o���āA�Ή�����{@link WritableElement}�𐶐�����B<p>
     * {@link #getElementValue(String, Object)}�ŁA�o�͗v�f����w�肳�ꂽ�L�[�̒l�����o���B{@link #getImplementClass(String)}�܂���{@link #getImplementServiceName(String)}�ŁA�L�[�ɊY������{@link WritableElement}����肵�A���̃C���X�^���X���擾���āA�L�[�ƒl��ݒ肵�ĕԂ��B<br>
     * �L�[�ɊY������{@link WritableElement}������ł��Ȃ��ꍇ�́A{@link SimpleElement}�N���X���g�p����B<br>
     * 
     * @param key �L�[
     * @param val �o�͗v�f
     * @return WritableElement�C���X�^���X
     */
    protected WritableElement createElement(String key, Object val){
        WritableElement writableElem = null;
        if(getImplementClass(key) == null){
            if(getImplementServiceName(key) == null){
                //�����N���X�����擾�ł��Ȃ�����SimpleElement�ɂ���B
                writableElem = getInstance(SIMPLE_ELEMENT_NAME);
            }else{
                try{
                    writableElem = (WritableElement)ServiceManagerFactory.getServiceObject(getImplementServiceName(key));
                }catch(ServiceNotFoundException e){
                    writableElem = getInstance(SIMPLE_ELEMENT_NAME);
                }
            }
        }
        else{
            String implClassName = getImplementClass(key);
            writableElem = getInstance(implClassName);
        }
        //�L�[�ƒl��ݒ�
        writableElem.setKey(key);
        writableElem.setValue(val);
        postCreateElement(writableElem);
        return writableElem;
    }
    
    /**
     * �w�肳�ꂽ�L�[�ɑ΂��Ďw�肳�ꂽ�N���X����{@link WritableElement}�����N���X���}�b�s���O����B<p>
     *
     * @param key �L�[
     * @param className WritableElement�����N���X��
     */
    protected void setImplementClass(String key, String className){
        mImplClasses.put(key, className);
    }
    
    /**
     * �w�肳�ꂽ�L�[�ɑ΂���{@link WritableElement}�����N���X�����擾����B<p>
     *
     * @param key �L�[
     * @return WritableElement�����N���X��
     */
    protected String getImplementClass(String key){
        return (String)mImplClasses.get(key);
    }
    
    /**
     * �w�肳�ꂽ�L�[�ɑ΂���{@link WritableElement}�����T�[�r�X�����擾����B<p>
     *
     * @param key �L�[
     * @return WritableElement�����T�[�r�X��
     */
    protected ServiceName getImplementServiceName(String key){
        return (ServiceName)mImplServiceNameMap.get(key);
    }
    
    /**
     * {@link WritableElement}�𐶐�������̏������s���B<p>
     * {@link #createElement(String, Object)}�Ő�������WritableElement�ɑ΂��ĔC�ӂ̏������s���B<br>
     * ���̃N���X���p������N���X�ŕK�v�Ȏ������s���B�����ł͋�����B<br>
     * 
     * @param elem �����Ώۂ�WritableElement
     */
    protected void postCreateElement(WritableElement elem){
        //�����
        ;
    }
    
    /**
     * �w�肳�ꂽ�o�͗v�f�̃}�b�v����A�w�肳�ꂽ�L�[�̒l���擾����B<p>
     *
     * @param key �o�͗v�f�}�b�v���̃L�[
     * @param elements �o�͗v�f�}�b�v
     * @return �o�͗v�f���̃L�[�ɊY������l
     */
    protected Object getElementValue(String key, Object elements){
        return ((Map)elements).get(key);
    }
    
    /**
     * �w�肵���N���X�̃C���X�^���X���擾����B<p>
     * 
     * @param className �C���X�^���X������N���X�̖��O
     * @return �C���X�^���X
     */
    protected WritableElement getInstance(String className){
        WritableElement writableElem = null;
        try{
            Class clazz = Class.forName(
                className,
                true,
                NimbusClassLoader.getInstance()
            );
            writableElem = (WritableElement)clazz.newInstance();
        }catch(IllegalAccessException e){
            throw new ServiceException(
                "WRITABLERECORDFACTORY002",
                "IllegalAccess When creatCuiOperator() ",
                e
            );
        }catch(InstantiationException e){
            throw new ServiceException(
                "WRITABLERECORDFACTORY003",
                "Instanting failed",
                e
            );
        }catch(ClassNotFoundException e){
            throw new ServiceException(
                "WRITABLERECORDFACTORY004",
                "Class not found. name is " + className,
                e
            );
        }
        return writableElem;
    }
    
    /**
     * �t�H�[�}�b�g������p�[�X���\�b�h�B<p>
     * setFormat(String)�ŁA�Z�b�g���ꂽ�t�H�[�}�b�g������mFormat���p�[�X���AParsedElement��List��Ԃ�
     * <ol>
     * <li>'%'�ň͂񂾕�������L�[���[�h�ƔF������B</li>
     * <li>'\%'�L�q����ƁA�������'%'�Ƃ��ĔF�������B</li>
     * <li>'\\'�ƋL�q����ƁA�������'\'�Ƃ��ĔF�������B</li>
     * <li>�t�H�[�}�b�g������null�܂��͋󕶎��̂Ƃ��Anull��Ԃ��B</li>
     * <li>�t�H�[�}�b�g������ɂ����āA"%"�Ŏn�߂��L�[���[�h�̋L�q��%�ŕ����Ă��Ȃ��ꍇ�AIllegalArgumentException��throw����B</li>
     * <li>(��) �t�H�[�}�b�g������"������ %D% �ł��B"�̏ꍇ�A3��ParsedElement���܂�List���ԋp�����B</li> 
     *         <ul>
     *            <li>�ŏ���ParsedElement��mValue��"������ "�AmIsKeyWord��false</li>
     *            <li>2�Ԗڂ�ParsedElement��mValue��"D"�AmIsKeyWord��true</li>
     *            <li>3�Ԗڂ�ParsedElement��mValue��" �ł��B"�AmIsKeyWord��false</li>
     *         </ul>
     * <li>�t�H�[�}�b�g������null�������͋󕶎��̏ꍇ�Anull��Ԃ��B</li>
     * </ol>
     * 
     * @return ParsedElement��List
     */
    protected List parseFormat(String format){
        //������
        if(format == null || format.length() == 0 ){
            //null��Ԃ�
            return null;
        }
        List result = new ArrayList();
        StringBuilder word = new StringBuilder("");
        boolean isStartKey = false;
        boolean isEscape = false;
        for(int i = 0, max = format.length(); i < max; i++){
            final char c = format.charAt(i);
            switch(c){
            case '%':
                if(isEscape){
                    word.append(c);
                    isEscape = false;
                }else if(isStartKey){
                    if(word.length() != 0){
                        //�L�[���[�h�Ƃ��Ēǉ�
                        final ParsedElement elem = new ParsedElement(
                            word.toString(),
                            true
                        );
                        result.add(elem);
                        word.setLength(0);
                        isStartKey = false;
                    }else{
                        throw new IllegalArgumentException(
                            "Keyword must not be null. : " + format
                        );
                    }
                }else{
                    if(word.length() > 0){
                        //�Œ胁�b�Z�[�W�Ƃ��Ēǉ�
                        final ParsedElement elem = new ParsedElement(
                            word.toString(),
                            false
                        );
                        result.add(elem);
                        word.setLength(0);
                    }
                    isStartKey = true;
                }
                break;
            case '\\':
                if(isEscape){
                    word.append(c);
                    isEscape = false;
                }else{
                    isEscape = true;
                }
                break;
            default:
                if(isEscape){
                    throw new IllegalArgumentException(
                        "'\' is escape character. : " + format
                    );
                }else{
                    word.append(c);
                }
                break;
            }
        }
        if(isEscape){
            throw new IllegalArgumentException(
                "'\' is escape character. : " + format
            );
        }
        if(isStartKey){
            throw new IllegalArgumentException(
                "'%' is key separator character. : " + format
            );
        }
        if(word.length() > 0){
            //�Œ胁�b�Z�[�W�Ƃ��Ēǉ�
            final ParsedElement elem = new ParsedElement(
                word.toString(),
                false
            );
            result.add(elem);
            word.setLength(0);
        }
        return result;
    }
    
    /**
     * �p�[�X���ꂽ�t�H�[�}�b�g�̗v�f��\���N���X�B<p>
     * String�̒l�ƁAkey���ۂ��̎���(boolean)�l�����B
     * 
     * @author Y.Tokuda
     */
    protected static class ParsedElement implements Serializable{
        
        private static final long serialVersionUID = 6554326776504636150L;
        
        //�����o�ϐ�
        protected String mVal;
        protected boolean mIsKeyWord;
        
        /**
         * �R���X�g���N�^�B<p>
         *
         * @param val �t�H�[�}�b�g�̗v�f������
         * @param isKey �L�[���ǂ����������t���O
         */
        public ParsedElement(String val, boolean isKey){
            mVal = val;
            mIsKeyWord = isKey;
        }
        
        /**
         * �t�H�[�}�b�g�̗v�f��������擾����B<p>
         *
         * @return �t�H�[�}�b�g�̗v�f������
         */
        public String getValue(){
            return mVal;
        }
        
        /**
         * ���̗v�f���L�[���ǂ����𔻒肷��B<p>
         *
         * @return ���̗v�f���L�[�̏ꍇtrue
         */
        public boolean isKeyElement(){
            return mIsKeyWord ;
        }
    }
}
