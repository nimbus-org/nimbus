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

import jp.ossc.nimbus.service.log.*;
import jp.ossc.nimbus.service.message.*;

/**
 * �T�[�r�X���N���X�B<p>
 * {@link ServiceManager}�ŊǗ��A����\�ȃT�[�r�X�̊��N���X�ł���B<br>
 * �T�[�r�X�̊J���҂́A�ʏ�A���̃N���X���p�����āA�T�[�r�X����������B<br>
 * <pre>
 *   public class MyService extends ServiceBase{
 *             �F
 * </pre>
 * ���̃N���X�̃T�u�N���X�́AServiceManager�ɂ���āA�����i{@link #create()}�j�A�N���i{@link #start()}�j�A��~�i{@link #stop()}�j�A�p���i{@link #destroy()}�j�̌_�@�𐧌䂷�鎖���\�ł���B���ꂼ��̓���̃��\�b�h�icreate()�Astart()�Astop()�Adestroy()�j�ɂ́A�������s���Ă���̂ŁA�ʏ�́A�I�[�o�[���C�h���Ă͂����Ȃ��B���̃N���X���g���ăT�[�r�X�̐���̎������s���ɂ́A{@link #createService()}�A{@link #startService()}�A{@link #stopService()}�A{@link #destroyService()}���A������ƂȂ��Ă���̂ŁA�K�v�ɉ����ăI�[�o�[���C�h���邱�ƁB<br>
 * <pre>
 *   public class MyService extends ServiceBase{
 *             �F
 *       public void createService() throws Exception{
 *                 �F
 *       }
 *       public void startService() throws Exception{
 *                 �F
 *       }
 *       public void stopService() throws Exception{
 *                 �F
 *       }
 *       public void destroyService() throws Exception{
 *                 �F
 *       }
 *             �F
 * </pre>
 * create()�Astart()�Astop()�Adestroy()�̂S�̓���̎����ŁA��Ԃ̐��䂪�s���Ă�B�܂��Acreate()�ł́A{@link #setServiceManagerName(String)}�Őݒ肳�ꂽServiceManager�ɁA{@link #setServiceName(String)}�Őݒ肳�ꂽ���O�Ŏ������g��o�^����B���l�ɁAdestroy()�ł́AServiceManager���玩�����g��o�^��������B<br>
 * 
 * @author M.Takata
 * @see ServiceManager
 */
public abstract class ServiceBase
 implements ServiceBaseMBean, ServiceProxy,
            ServiceStateBroadcaster, Serializable{
    
    private static final long serialVersionUID = -2021965433743797247L;
    
    // ���b�Z�[�WID��`
    private static final String SVC__ = "SVC__";
    private static final String SVC__0 = SVC__ + 0;
    private static final String SVC__00 = SVC__0 + 0;
    private static final String SVC__000 = SVC__00 + 0;
    private static final String SVC__0000 = SVC__000 + 0;
    private static final String SVC__00001 = SVC__0000 + 1;
    private static final String SVC__00002 = SVC__0000 + 2;
    private static final String SVC__00003 = SVC__0000 + 3;
    private static final String SVC__00004 = SVC__0000 + 4;
    private static final String SVC__00005 = SVC__0000 + 5;
    private static final String SVC__00006 = SVC__0000 + 6;
    private static final String SVC__00007 = SVC__0000 + 7;
    private static final String SVC__00008 = SVC__0000 + 8;
    private static final String SVC__00009 = SVC__0000 + 9;
    private static final String SVC__00010 = SVC__000 + 10;
    private static final String SVC__00011 = SVC__000 + 11;
    private static final String SVC__00012 = SVC__000 + 12;
    private static final String SVC__00013 = SVC__000 + 13;
    private static final String SVC__00014 = SVC__000 + 14;
    private static final String SVC__00015 = SVC__000 + 15;
    private static final String SVC__00016 = SVC__000 + 16;
    private static final String SVC__00017 = SVC__000 + 17;
    private static final String SVC__00018 = SVC__000 + 18;
    private static final String SVC__00019 = SVC__000 + 19;
    private static final String SVC__00020 = SVC__000 + 20;
    private static final String SVC__00021 = SVC__000 + 21;
    private static final String SVC__00022 = SVC__000 + 22;
    private static final String SVC__00023 = SVC__000 + 23;
    private static final String SVC__00024 = SVC__000 + 24;
    private static final String SVC__00025 = SVC__000 + 25;
    private static final String SVC__00026 = SVC__000 + 26;
    private static final String SVC__00027 = SVC__000 + 27;
    private static final String SVC__00028 = SVC__000 + 28;
    private static final String SVC__00029 = SVC__000 + 29;
    private static final String SVC__00030 = SVC__000 + 30;
    private static final String SVC__00031 = SVC__000 + 31;
    private static final String SVC__00032 = SVC__000 + 32;
    private static final String SVC__00033 = SVC__000 + 33;
    private static final String SVC__00034 = SVC__000 + 34;
    private static final String SVC__00035 = SVC__000 + 35;
    private static final String SVC__00036 = SVC__000 + 36;
    private static final String SVC__00037 = SVC__000 + 37;
    private static final String SVC__00038 = SVC__000 + 38;
    private static final String SVC__00039 = SVC__000 + 39;
    private static final String SVC__00040 = SVC__000 + 40;
    private static final String SVC__00041 = SVC__000 + 41;
    private static final String SVC__00042 = SVC__000 + 42;
    private static final String SVC__00043 = SVC__000 + 43;
    private static final String SVC__00044 = SVC__000 + 44;
    private static final String SVC__00045 = SVC__000 + 45;
    private static final String SVC__00046 = SVC__000 + 46;
    private static final String SVC__00047 = SVC__000 + 47;
    private static final String SVC__00048 = SVC__000 + 48;
    private static final String SVC__00049 = SVC__000 + 49;
    private static final String SVC__00050 = SVC__000 + 50;
    private static final String SVC__00051 = SVC__000 + 51;
    private static final String SVC__00052 = SVC__000 + 52;
    private static final String SVC__00053 = SVC__000 + 53;
    private static final String SVC__00054 = SVC__000 + 54;
    private static final String SVC__00055 = SVC__000 + 55;
    private static final String SVC__00056 = SVC__000 + 56;
    private static final String SVC__00057 = SVC__000 + 57;
    private static final String SVC__00058 = SVC__000 + 58;
    private static final String SVC__00059 = SVC__000 + 59;
    private static final String SVC__00060 = SVC__000 + 60;
    private static final String SVC__00061 = SVC__000 + 61;
    private static final String SVC__00062 = SVC__000 + 62;
    private static final String SVC__00063 = SVC__000 + 63;
    private static final String SVC__00064 = SVC__000 + 64;
    private static final String SVC__00065 = SVC__000 + 65;
    private static final String SVC__00066 = SVC__000 + 66;
    private static final String SVC__00067 = SVC__000 + 67;
    private static final String SVC__00068 = SVC__000 + 68;
    private static final String SVC__00069 = SVC__000 + 69;
    private static final String SVC__00070 = SVC__000 + 70;
    private static final String SVC__00071 = SVC__000 + 71;
    private static final String SVC__00072 = SVC__000 + 72;
    private static final String SVC__00073 = SVC__000 + 73;
    private static final String SVC__00074 = SVC__000 + 74;
    private static final String SVC__00075 = SVC__000 + 75;
    private static final String SVC__00076 = SVC__000 + 76;
    private static final String SVC__00077 = SVC__000 + 77;
    private static final String SVC__00078 = SVC__000 + 78;
    private static final String SVC__00079 = SVC__000 + 79;
    private static final String SVC__00080 = SVC__000 + 80;
    private static final String SVC__00081 = SVC__000 + 81;
    private static final String SVC__00082 = SVC__000 + 82;
    private static final String SVC__00083 = SVC__000 + 83;
    private static final String SVC__00084 = SVC__000 + 84;
    private static final String SVC__00085 = SVC__000 + 85;
    private static final String SVC__00086 = SVC__000 + 86;
    private static final String SVC__00087 = SVC__000 + 87;
    private static final String SVC__00088 = SVC__000 + 88;
    
    /**
     * �T�[�r�X�̏�Ԃ�\���l�B<p>
     * �����l�́A{@link #DESTROYED}�ł���B
     * 
     * @see #getState()
     */
    protected volatile int state = DESTROYED;
    
    /**
     * �T�[�r�X�̖��O�B<p>
     *
     * @see #setServiceName(String)
     * @see #getServiceName()
     */
    protected String name;
    
    /**
     * �T�[�r�X�̖��O�B<p>
     *
     * @see #getServiceNameObject()
     */
    protected ServiceName nameObj;
    
    /**
     * ���̃T�[�r�X���o�^�����ServiceManager�B<p>
     */
    protected transient ServiceManager manager;
    
    /**
     * ���̃T�[�r�X���o�^�����ServiceManager�̃T�[�r�X���B<p>
     *
     * @see #setServiceManagerName(String)
     * @see #getServiceManagerName()
     */
    protected String managerName;
    
    /**
     * ���b�v����{@link ServiceBaseSupport}�I�u�W�F�N�g�B<p>
     * ServiceBaseSupport�C���^�t�F�[�X�����������I�u�W�F�N�g���R���X�g���N�^�̈����ɓn�����ŁA���̃N���X���p�����Ȃ��Ă��A���̃N���X�̎����𗘗p�ł���悤�ɂ��邽�߂̂��́B
     */
    protected ServiceBaseSupport support;
    
    /**
     * ���̃T�[�r�X�ɓo�^���ꂽ�T�[�r�X��ԃ��X�i�̃��X�g�B<p>
     * 
     * @see #addServiceStateListener(ServiceStateListener)
     * @see #removeServiceStateListener(ServiceStateListener)
     */
    protected transient List serviceStateListeners = new ArrayList();
    
    /**
     * Service���̃��O�o�͂Ɏg�p����{@link jp.ossc.nimbus.service.log.Logger}�T�[�r�X�̖��O�B<p>
     * 
     * @see #getSystemLoggerServiceName()
     * @see #setSystemLoggerServiceName(ServiceName)
     */
    protected ServiceName loggerServiceName;
    
    /**
     * Service���̃��O�o�͂Ɏg�p����{@link jp.ossc.nimbus.service.log.Logger}�T�[�r�X�B<p>
     */
    protected transient LoggerWrapper logger
         = new LoggerWrapper(ServiceManagerFactory.getLogger());
    
    /**
     * Service���ł̃��b�Z�[�W�擾�Ɏg�p����{@link jp.ossc.nimbus.service.message.MessageRecordFactory}�T�[�r�X�̖��O�B<p>
     * 
     * @see #getSystemMessageRecordFactoryServiceName()
     * @see #setSystemMessageRecordFactoryServiceName(ServiceName)
     */
    protected ServiceName messageServiceName;
    
    /**
     * Service���ł̃��b�Z�[�W�擾�Ɏg�p����{@link jp.ossc.nimbus.service.message.MessageRecordFactory}�T�[�r�X�B<p>
     */
    protected transient MessageRecordFactoryWrapper message
         = new MessageRecordFactoryWrapper(
            ServiceManagerFactory.getMessageRecordFactory()
         );
    
    /**
     * �R���X�g���N�^�B<p>
     */
    public ServiceBase(){
    }
    
    /**
     * {@link ServiceBaseSupport}�C���^�t�F�[�X�����������T�[�r�X�N���X�����b�v���āAServiceBase�̎����𗘗p�ł���悤�ɂ��邽�߂̃R���X�g���N�^�B<p>
     * 
     * @param support ServiceBaseSupport�C���^�t�F�[�X�����������N���X�̃C���X�^���X
     */
    protected ServiceBase(ServiceBaseSupport support){
        this();
        this.support = support;
        if(support != null){
            try{
                support.setServiceBase(this);
            }catch(AbstractMethodError e){
                // �݊����S�ۂ̂��ߗ�O�������͖���
            }
        }
    }
    
    /**
     * ���̃T�[�r�X���Ǘ�����{@link ServiceManager}��ݒ肷��B<p>
     * ServiceManager���C���X�^���X�ϐ��Ɋi�[����Ɠ����ɁA{@link ServiceManager#getLogger()}�Ŏ擾����{@link Logger}���A���̃T�[�r�X�̎���{@link LoggerWrapper}�̃f�t�H���g��Logger�ɐݒ肷��B�܂��A{@link #setSystemLoggerServiceName(ServiceName)}��Logger�T�[�r�X���ݒ肳��Ă��Ȃ��ꍇ�́ALoggerWrapper�̃J�����g��Logger�ɂ��ݒ肷��B<br>
     *
     * @param mng ���̃T�[�r�X���Ǘ�����ServiceManager
     */
    protected void setServiceManager(ServiceManager mng){
        if(manager != null && manager.equals(mng)){
            return;
        }
        manager = mng;
        if(manager != null){
            logger.setDefaultLogger(manager.getLogger());
            if(loggerServiceName == null){
                logger.setLogger(manager.getLogger());
            }
            message.setDefaultMessageRecordFactory(
                manager.getMessageRecordFactory()
            );
            if(messageServiceName == null){
                message.setMessageRecordFactory(
                    manager.getMessageRecordFactory()
                );
            }
        }
    }
    
    /**
     * ���̃T�[�r�X���Ǘ�����{@link ServiceManager}���擾����B<p>
     * 
     * @return ���̃T�[�r�X���Ǘ�����ServiceManager
     */
    public ServiceManager getServiceManager(){
        return manager;
    }
    
    /**
     * ���̃T�[�r�X�����[�h����{@link ServiceLoader}���擾����B<p>
     *
     * @return ���̃T�[�r�X�����[�h����{@link ServiceLoader}
     */
    public ServiceLoader getServiceLoader(){
        if(manager == null){
            return null;
        }
        ServiceMetaData metaData = null;
        try{
            metaData = manager.getServiceMetaData(name);
        }catch(ServiceNotFoundException e){
            return null;
        }
        return metaData.getServiceLoader();
    }
    
    /**
     * �T�[�r�X�𐶐�����B<p>
     * <b><i>���̃��\�b�h�́A�ʏ�A�I�[�o�[���C�h���Ă͂����Ȃ��B</i></b><br>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     *   <li>{@link #isNecessaryToCreate()}�̌Ăяo���B�߂�l��false�̏ꍇ�́A���������𒆎~����B</li>
     *   <li>{@link #preCreateService()}�̌Ăяo���B��O�����������ꍇ�́A{@link #FAILED}�ɑJ�ڂ���B</li>
     *   <li>{@link #createService()}�̌Ăяo���B��O�����������ꍇ�́AFAILED�ɑJ�ڂ���B</li>
     *   <li>{@link #postCreateService()}�̌Ăяo���B��O�����������ꍇ�́AFAILED�ɑJ�ڂ���B</li>
     * </ol>
     *
     * @exception Exception preCreateService()�AcreateService()�ApostCreateService()�ŗ�O�����������ꍇ
     * @see #isNecessaryToCreate()
     * @see #preCreateService()
     * @see #createService()
     * @see #postCreateService()
     */
    public synchronized void create() throws Exception{
        
        if(manager == null && managerName != null){
            setServiceManager(ServiceManagerFactory.findManager(managerName));
        }
        
        Object[] serviceName = null;
        if(managerName != null){
            serviceName = new Object[]{managerName, name};
            logger.write(SVC__00001, serviceName);
        }else{
            if(name == null){
                setServiceName(toString());
            }
            logger.write(SVC__00002, name);
        }
        
        try{
            if(!isNecessaryToCreate()){
                if(managerName != null){
                    logger.write(
                        SVC__00003,
                        new Object[]{managerName, name, getStateString()}
                    );
                }else{
                    logger.write(
                        SVC__00004,
                        new Object[]{name, getStateString()}
                    );
                }
                return;
            }
            
            if(managerName != null){
                logger.write(SVC__00005, serviceName);
            }else{
                logger.write(SVC__00006, name);
            }
            preCreateService();
            if(managerName != null){
                logger.write(SVC__00007, serviceName);
            }else{
                logger.write(SVC__00008, name);
            }
            
            if(managerName != null){
                logger.write(SVC__00009, serviceName);
            }else{
                logger.write(SVC__00010, name);
            }
            createService();
            if(managerName != null){
                logger.write(SVC__00011, serviceName);
            }else{
                logger.write(SVC__00012, name);
            }
            
            if(managerName != null){
                logger.write(SVC__00013, serviceName);
            }else{
                logger.write(SVC__00014, name);
            }
            postCreateService();
            if(managerName != null){
                logger.write(SVC__00015, serviceName);
            }else{
                logger.write(SVC__00016, name);
            }
        }catch(Exception e){
            if(managerName != null){
                logger.write(SVC__00017, serviceName, e);
            }else{
                logger.write(SVC__00018, name, e);
            }
            state = FAILED;
            processStateChanged(FAILED);
            throw e;
        }
        
        if(managerName != null){
            logger.write(SVC__00019, serviceName);
        }else{
            logger.write(SVC__00020, name);
        }
    }
    
    /**
     * �T�[�r�X�𐶐�����K�v�����邩���ׂ�B<p>
     * �T�[�r�X��Ԃ�{@link #CREATING}�܂��́A{@link #CREATED}�A{@link #STARTED}�̏ꍇ�A�T�[�r�X�𐶐�����K�v���Ȃ��Ɣ��f����false�Ԃ��B<br>
     *
     * @return �T�[�r�X�𐶐�����K�v������ꍇtrue�A�����łȂ��ꍇfalse
     * @exception Exception �s���ȏ�ԂŃT�[�r�X�𐶐����悤�Ƃ����ꍇ�B�����ł́Athrow����Ȃ��B�I�[�o�[���C�h�����ꍇ�ɁA�K�v�Ȃ�Η�O��throw�ł���B
     * @see #create()
     */
    protected boolean isNecessaryToCreate() throws Exception{
        return !(state == CREATED || state == CREATING || state == STARTED);
    }
    
    /**
     * �T�[�r�X�𐶐�����O�������s���B<p>
     * <b><i>���̃��\�b�h�́A�ʏ�A�I�[�o�[���C�h���Ă͂����Ȃ��B</i></b><br>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     *   <li>�T�[�r�X��Ԃ̑J�ځB{@link #CREATING}�ɑJ�ڂ���B</li>
     * </ol>
     *
     * @exception Exception {@link #processStateChanged(int)}�ŗ�O�����������ꍇ
     * @see #create()
     */
    protected void preCreateService() throws Exception{
        
        state = CREATING;
        processStateChanged(CREATING);
    }
    
    /**
     * �T�[�r�X�𐶐�����㏈�����s���B<p>
     * <b><i>���̃��\�b�h�́A�ʏ�A�I�[�o�[���C�h���Ă͂����Ȃ��B</i></b><br>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     *   <li>ServiceManager�ւ̓o�^�B{@link #getServiceManagerName()}�Ŏ擾�ł���T�[�r�X����ServiceManager�ɁA{@link #getServiceName()}�Ŏ擾�ł���T�[�r�X���ŁA�������g��o�^����B�ǂ��炩��null�̏ꍇ�A�o�^����Ȃ��B</li>
     *   <li>�T�[�r�X��Ԃ̑J�ځBcreateService()�̌Ăяo��������ɍs����ƁA{@link #CREATED}�ɑJ�ڂ���B</li>
     * </ol>
     *
     * @exception Exception {@link #processStateChanged(int)}�ŗ�O�����������ꍇ
     * @see #create()
     */
    protected void postCreateService() throws Exception{
        
        if(manager == null && managerName != null){
            setServiceManager(ServiceManagerFactory.findManager(managerName));
        }
        if(manager != null && getServiceName() != null){
            if(manager.isRegisteredService(getServiceName())){
                final Service registeredService
                     = manager.getService(getServiceName());
                if(registeredService != this){
                    logger.write(
                        SVC__00088,
                        new Object[]{managerName, name}
                    );
                    manager.stopService(getServiceName());
                    manager.destroyService(getServiceName());
                    if(manager.isRegisteredService(getServiceName())){
                        manager.unregisterService(getServiceName());
                    }
                    manager.registerService(getServiceName(), this);
                }
            }else{
                manager.registerService(getServiceName(), this);
            }
        }
        
        state = CREATED;
        processStateChanged(CREATED);
    }
    
    // ServiceBaseMBean ��JavaDoc
    public synchronized void restart() throws Exception{
        stop();
        start();
    }
    
    /**
     * �T�[�r�X���J�n����B<p>
     * <b><i>���̃��\�b�h�́A�ʏ�A�I�[�o�[���C�h���Ă͂����Ȃ��B</i></b><br>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     *   <li>{@link #isNecessaryToStart()}�̌Ăяo���B�߂�l��false�̏ꍇ�́A�J�n�����𒆎~����B</li>
     *   <li>{@link #preStartService()}�̌Ăяo���B��O�����������ꍇ�́A{@link #FAILED}�ɑJ�ڂ���B</li>
     *   <li>{@link #startService()}�̌Ăяo���B��O�����������ꍇ�́AFAILED�ɑJ�ڂ���B</li>
     *   <li>{@link #postStartService()}�̌Ăяo���B��O�����������ꍇ�́AFAILED�ɑJ�ڂ���B</li>
     * </ol>
     *
     * @exception IllegalStateException �T�[�r�X��ԃ`�F�b�N�Ɏ��s�����ꍇ
     * @exception Exception preStartService()�AstartService()�ApostStartService()�ŗ�O�����������ꍇ
     * @see #preStartService()
     * @see #startService()
     * @see #postStartService()
     */
    public synchronized void start() throws Exception{
        
        Object[] serviceName = null;
        if(managerName != null){
            serviceName = new Object[]{managerName, name};
            logger.write(SVC__00021, serviceName);
        }else{
            logger.write(SVC__00022, name);
        }
        
        try{
            if(!isNecessaryToStart()){
                if(managerName != null){
                    logger.write(
                        SVC__00023,
                        new Object[]{managerName, name, getStateString()}
                    );
                }else{
                    logger.write(
                        SVC__00024,
                        new Object[]{name, getStateString()}
                    );
                }
                return;
            }
            
            if(managerName != null){
                logger.write(SVC__00025, serviceName);
            }else{
                logger.write(SVC__00026, name);
            }
            preStartService();
            if(managerName != null){
                logger.write(SVC__00027, serviceName);
            }else{
                logger.write(SVC__00028, name);
            }
            
            if(managerName != null){
                logger.write(SVC__00029, serviceName);
            }else{
                logger.write(SVC__00030, name);
            }
            startService();
            if(managerName != null){
                logger.write(SVC__00031, serviceName);
            }else{
                logger.write(SVC__00032, name);
            }
            
            if(managerName != null){
                logger.write(SVC__00033, serviceName);
            }else{
                logger.write(SVC__00034, name);
            }
            postStartService();
            if(managerName != null){
                logger.write(SVC__00035, serviceName);
            }else{
                logger.write(SVC__00036, name);
            }
        }catch(Exception e){
            if(managerName != null){
                logger.write(SVC__00037, serviceName, e);
            }else{
                logger.write(SVC__00038, name, e);
            }
            state = FAILED;
            processStateChanged(FAILED);
            throw e;
        }
        
        if(managerName != null){
            logger.write(SVC__00039, serviceName);
        }else{
            logger.write(SVC__00040, name);
        }
    }
    
    /**
     * �T�[�r�X���J�n����K�v�����邩���ׂ�B<p>
     * �T�[�r�X��Ԃ�{@link #STARTING}�܂��́A{@link #STARTED}�̏ꍇ�A�T�[�r�X���J�n����K�v���Ȃ��Ɣ��f����false�Ԃ��B<br>
     *
     * @return �T�[�r�X���J�n����K�v������ꍇtrue�A�����łȂ��ꍇfalse
     * @exception Exception �T�[�r�X��Ԃ�{@link #DESTROYED}�܂��́A{@link #FAILED}�ŁA�T�[�r�X���J�n���悤�Ƃ����ꍇ
     * @see #start()
     */
    protected boolean isNecessaryToStart() throws Exception{
        if(state == DESTROYED){
            throw new IllegalStateException(
                message.findEmbedMessage(
                    SVC__00041,
                    new Object[]{STATES[STARTING], getStateString()}
                )
            );
        }
        if(state == FAILED){
            // TODO �ҋ@������H
            return false;
        }
        if(state == STARTED || state == STARTING){
            return false;
        }
        return true;
    }
    
    /**
     * �T�[�r�X���J�n����O�������s���B<p>
     * <b><i>���̃��\�b�h�́A�ʏ�A�I�[�o�[���C�h���Ă͂����Ȃ��B</i></b><br>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     *   <li>�T�[�r�X��Ԃ̑J�ځB{@link #STARTING}�ɑJ�ڂ���B</li>
     * </ol>
     *
     * @exception Exception {@link #processStateChanged(int)}�ŗ�O�����������ꍇ
     * @see #start()
     */
    protected void preStartService() throws Exception{
        state = STARTING;
        processStateChanged(STARTING);
    }
    
    /**
     * �T�[�r�X���J�n����B<p>
     * <b><i>���̃��\�b�h�́A�ʏ�A�I�[�o�[���C�h���Ă͂����Ȃ��B</i></b><br>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     *   <li>�T�[�r�X��Ԃ̑J�ځBstartService()�̌Ăяo��������ɍs����ƁA{@link #STARTED}�ɑJ�ڂ���B</li>
     * </ol>
     *
     * @exception Exception {@link #processStateChanged(int)}�ŗ�O�����������ꍇ
     * @see #start()
     */
    protected void postStartService() throws Exception{
        state = STARTED;
        processStateChanged(STARTED);
    }
    
    /**
     * �T�[�r�X���~����B<p>
     * <b><i>���̃��\�b�h�́A�ʏ�A�I�[�o�[���C�h���Ă͂����Ȃ��B</i></b><br>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     *   <li>{@link #isNecessaryToStop()}�̌Ăяo���B�߂�l��false�̏ꍇ�́A��~�����𒆎~����B</li>
     *   <li>{@link #preStopService()}�̌Ăяo���B��O�����������ꍇ�́A{@link #FAILED}�ɑJ�ڂ���B</li>
     *   <li>{@link #stopService()}�̌Ăяo���B��O�����������ꍇ�́AFAILED�ɑJ�ڂ���B</li>
     *   <li>{@link #postStopService()}�̌Ăяo���B��O�����������ꍇ�́AFAILED�ɑJ�ڂ���B</li>
     * </ol>
     *
     * @see #preStopService()
     * @see #stopService()
     * @see #postStopService()
     */
    public synchronized void stop(){
        
        Object[] serviceName = null;
        if(managerName != null){
            serviceName = new Object[]{managerName, name};
            logger.write(SVC__00042, serviceName);
        }else{
            logger.write(SVC__00043, name);
        }
        
        try{
            if(!isNecessaryToStop()){
                if(managerName != null){
                    logger.write(
                        SVC__00044,
                        new Object[]{managerName, name, getStateString()}
                    );
                }else{
                    logger.write(
                        SVC__00045,
                        new Object[]{name, getStateString()}
                    );
                }
                return;
            }
            
            if(managerName != null){
                logger.write(SVC__00046, serviceName);
            }else{
                logger.write(SVC__00047, name);
            }
            preStopService();
            if(managerName != null){
                logger.write(SVC__00048, serviceName);
            }else{
                logger.write(SVC__00049, name);
            }
            
            if(managerName != null){
                logger.write(SVC__00050, serviceName);
            }else{
                logger.write(SVC__00051, name);
            }
            stopService();
            if(managerName != null){
                logger.write(SVC__00052, serviceName);
            }else{
                logger.write(SVC__00053, name);
            }
            
            if(managerName != null){
                logger.write(SVC__00054, serviceName);
            }else{
                logger.write(SVC__00055, name);
            }
            postStopService();
            if(managerName != null){
                logger.write(SVC__00056, serviceName);
            }else{
                logger.write(SVC__00057, name);
            }
        }catch(Exception e){
            if(managerName != null){
                logger.write(SVC__00058, serviceName, e);
            }else{
                logger.write(SVC__00059, name, e);
            }
            state = FAILED;
            try{
                processStateChanged(FAILED);
            }catch(Exception ex){
                if(managerName != null){
                    logger.write(
                        SVC__00060,
                        new Object[]{managerName, name, getStateString()},
                        ex
                    );
                }else{
                    logger.write(
                        SVC__00061,
                        new Object[]{name, getStateString()},
                        ex
                    );
                }
                state = FAILED;
                return;
            }
            return;
        }
        
        if(managerName != null){
            logger.write(SVC__00062, serviceName);
        }else{
            logger.write(SVC__00063, name);
        }
    }
    
    /**
     * �T�[�r�X���~����K�v�����邩���ׂ�B<p>
     * �T�[�r�X��Ԃ�{@link #STARTED}�̏ꍇ�A�T�[�r�X���~����K�v������Ɣ��f����true�Ԃ��B<br>
     *
     * @return �T�[�r�X���~����K�v������ꍇtrue�A�����łȂ��ꍇfalse
     * @exception Exception �s���ȏ�ԂŃT�[�r�X���~���悤�Ƃ����ꍇ�B�����ł́Athrow����Ȃ��B�I�[�o�[���C�h�����ꍇ�ɁA�K�v�Ȃ�Η�O��throw�ł���B
     * @see #stop()
     */
    protected boolean isNecessaryToStop() throws Exception{
        return state == STARTED;
    }
    
    /**
     * �T�[�r�X���~����O�������s���B<p>
     * <b><i>���̃��\�b�h�́A�ʏ�A�I�[�o�[���C�h���Ă͂����Ȃ��B</i></b><br>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     *   <li>�T�[�r�X��ԃ`�F�b�N�B�T�[�r�X��Ԃ�{@link #STARTED}�łȂ��ꍇ�A�������s�킸�ɕԂ��B</li>
     *   <li>�T�[�r�X��Ԃ̑J�ځB�T�[�r�X��ԃ`�F�b�N��ʉ߂���ƁA{@link #STOPPING}�ɑJ�ڂ���B</li>
     * </ol>
     *
     * @exception Exception {@link #processStateChanged(int)}�ŗ�O�����������ꍇ
     * @see #stop()
     */
    protected void preStopService() throws Exception{
        state = STOPPING;
        processStateChanged(STOPPING);
    }
    
    /**
     * �T�[�r�X���~����㏈�����s���B<p>
     * <b><i>���̃��\�b�h�́A�ʏ�A�I�[�o�[���C�h���Ă͂����Ȃ��B</i></b><br>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     *   <li>�T�[�r�X��Ԃ̑J�ځBstopService()�̌Ăяo��������ɍs����ƁA{@link #STOPPED}�ɑJ�ڂ���B</li>
     * </ol>
     *
     * @exception Exception {@link #processStateChanged(int)}�ŗ�O�����������ꍇ
     * @see #stop()
     */
    protected void postStopService() throws Exception{
        state = STOPPED;
        processStateChanged(STOPPED);
    }
    
    /**
     * �T�[�r�X��j������B<p>
     * <b><i>���̃��\�b�h�́A�ʏ�A�I�[�o�[���C�h���Ă͂����Ȃ��B</i></b><br>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     *   <li>{@link #isNecessaryToDestroy()}�̌Ăяo���B�߂�l��false�̏ꍇ�́A�j�������𒆎~����B</li>
     *   <li>{@link #preDestroyService()}�̌Ăяo���B��O�����������ꍇ�́A{@link #FAILED}�ɑJ�ڂ���B</li>
     *   <li>{@link #destroyService()}�̌Ăяo���B��O�����������ꍇ�́AFAILED�ɑJ�ڂ���B</li>
     *   <li>{@link #postDestroyService()}�̌Ăяo���B��O�����������ꍇ�́AFAILED�ɑJ�ڂ���B</li>
     * </ol>
     *
     * @see #preDestroyService()
     * @see #destroyService()
     * @see #postDestroyService()
     */
    public synchronized void destroy(){
        
        Object[] serviceName = null;
        if(managerName != null){
            serviceName = new Object[]{managerName, name};
            logger.write(SVC__00064, serviceName);
        }else{
            logger.write(SVC__00065, name);
        }
        
        try{
            if(!isNecessaryToDestroy()){
                if(managerName != null){
                    logger.write(
                        SVC__00066,
                        new Object[]{managerName, name, getStateString()}
                    );
                }else{
                    logger.write(
                        SVC__00067,
                        new Object[]{name, getStateString()}
                    );
                }
                return;
            }
            
            if(managerName != null){
                logger.write(SVC__00068, serviceName);
            }else{
                logger.write(SVC__00069, name);
            }
            preDestroyService();
            if(managerName != null){
                logger.write(SVC__00070, serviceName);
            }else{
                logger.write(SVC__00071, name);
            }
            
            if(managerName != null){
                logger.write(SVC__00072, serviceName);
            }else{
                logger.write(SVC__00073, name);
            }
            destroyService();
            if(managerName != null){
                logger.write(SVC__00074, serviceName);
            }else{
                logger.write(SVC__00075, name);
            }
            
            if(managerName != null){
                logger.write(SVC__00076, serviceName);
            }else{
                logger.write(SVC__00077, name);
            }
            postDestroyService();
            if(managerName != null){
                logger.write(SVC__00078, serviceName);
            }else{
                logger.write(SVC__00079, name);
            }
        }catch(Exception e){
            if(managerName != null){
                logger.write(SVC__00080, serviceName, e);
            }else{
                logger.write(SVC__00081, name, e);
            }
            state = FAILED;
            try{
                processStateChanged(FAILED);
            }catch(Exception ex){
                if(managerName != null){
                    logger.write(
                        SVC__00060,
                        new Object[]{managerName, name, getStateString()},
                        ex
                    );
                }else{
                    logger.write(
                        SVC__00061,
                        new Object[]{name, getStateString()},
                        ex
                    );
                }
                state = FAILED;
                return;
            }
            return;
        }
        
        if(managerName != null){
            logger.write(SVC__00082, serviceName);
        }else{
            logger.write(SVC__00083, name);
        }
    }
    
    /**
     * �T�[�r�X��j������K�v�����邩���ׂ�B<p>
     * �T�[�r�X��Ԃ�{@link #DESTROYING}�܂���{@link #DESTROYED}�̏ꍇ�A�T�[�r�X��j������K�v���Ȃ��Ɣ��f����false�Ԃ��B<br>
     *
     * @return �T�[�r�X��j������K�v������ꍇtrue�A�����łȂ��ꍇfalse
     * @exception Exception �s���ȏ�ԂŃT�[�r�X��j�����悤�Ƃ����ꍇ�B�����ł́Athrow����Ȃ��B�I�[�o�[���C�h�����ꍇ�ɁA�K�v�Ȃ�Η�O��throw�ł���B
     * @see #stop()
     */
    protected boolean isNecessaryToDestroy() throws Exception{
        return !(state == DESTROYED || state == DESTROYING);
    }
    
    /**
     * �T�[�r�X��j������O�������s���B<p>
     * <b><i>���̃��\�b�h�́A�ʏ�A�I�[�o�[���C�h���Ă͂����Ȃ��B</i></b><br>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     *   <li>�T�[�r�X��Ԃ�{@link #STOPPED}�łȂ��ꍇ�A{@link #stop()}���Ăяo���B</li>
     *   <li>�T�[�r�X��Ԃ̑J�ځB{@link #DESTROYING}�ɑJ�ڂ���B</li>
     *   <li>ServiceManager����̍폜�B{@link #getServiceManagerName()}�Ŏ擾�ł���T�[�r�X����ServiceManager����A{@link #getServiceName()}�Ŏ擾�ł���T�[�r�X���ŁA�������g���폜����B�ǂ��炩��null�̏ꍇ�A�폜����Ȃ��B</li>
     * </ol>
     *
     * @exception Exception {@link #processStateChanged(int)}�ŗ�O�����������ꍇ
     * @see #destroy()
     */
    protected void preDestroyService() throws Exception{
        if(state != STOPPED){
            stop();
        }
        
        state = DESTROYING;
        processStateChanged(DESTROYING);
        
        if(manager == null && managerName != null){
            setServiceManager(ServiceManagerFactory.findManager(managerName));
        }
        if(manager != null && getServiceName() != null){
            Service service = null;
            try{
                service = manager.getService(getServiceName());
            }catch(ServiceNotFoundException e){
            }
            if(service == this){
                manager.unregisterService(getServiceName());
            }
        }
    }
    
    /**
     * �T�[�r�X��j������㏈�����s���B<p>
     * <b><i>���̃��\�b�h�́A�ʏ�A�I�[�o�[���C�h���Ă͂����Ȃ��B</i></b><br>
     * ���̃��\�b�h�ɂ́A�ȉ��̎������s���Ă���B<br>
     * <ol>
     *   <li>�T�[�r�X��Ԃ̑J�ځBdestroyService()�̌Ăяo��������ɍs����ƁA{@link #DESTROYED}�ɑJ�ڂ���B</li>
     * </ol>
     *
     * @exception Exception {@link #processStateChanged(int)}�ŗ�O�����������ꍇ
     * @see #destroy()
     */
    protected void postDestroyService() throws Exception{
        state = DESTROYED;
        processStateChanged(DESTROYED);
    }
    
    /**
     * �T�[�r�X����ݒ肷��B<p>
     * {@link ServiceLoader}�ŃT�[�r�X�����[�h����ꍇ�́AServiceLoader���ݒ肷��B<br>
     *
     * @param name �T�[�r�X��
     * @see #getServiceName()
     */
    public void setServiceName(String name){
        if(name != null && name.length() != 0){
            this.name = name;
            if(managerName != null){
                nameObj = new ServiceName(managerName, name);
            }
        }
    }
    
    // Service��JavaDoc
    public String getServiceName(){
        return name;
    }
    
    // Service��JavaDoc
    public ServiceName getServiceNameObject(){
        return nameObj;
    }
    
    // Service��JavaDoc
    public int getState(){
        return state;
    }
    
    // Service��JavaDoc
    public String getStateString(){
        return STATES[state];
    }
    
    /**
     * �T�[�r�X�𐶐�����B<p>
     * ���̃T�[�r�X�ɕK�v�ȃI�u�W�F�N�g�̐����Ȃǂ̏������������s���B<br>
     * ���̃N���X���p�����ăT�[�r�X����������T�[�r�X�J���҂́A�T�[�r�X�̐����������A���̃��\�b�h���I�[�o�[���C�h���Ď������邱�ƁB�f�t�H���g�����́A��ł���B<br>
     *
     * @exception Exception �T�[�r�X�̐��������Ɏ��s�����ꍇ
     * @see #create()
     */
    public void createService() throws Exception{
        if(support != null){
            support.createService();
        }
    }
    
    /**
     * �T�[�r�X���J�n����B<p>
     * ���̃T�[�r�X�𗘗p�\�ȏ�Ԃɂ���B���̃��\�b�h�̌Ăяo����́A���̃T�[�r�X�̋@�\�𗘗p�ł��鎖���ۏ؂����B<br>
     * ���̃N���X���p�����ăT�[�r�X����������T�[�r�X�J���҂́A�T�[�r�X�̊J�n�������A���̃��\�b�h���I�[�o�[���C�h���Ď������邱�ƁB�f�t�H���g�����́A��ł���B<br>
     *
     * @exception Exception �T�[�r�X�̊J�n�����Ɏ��s�����ꍇ
     * @see #start()
     */
    public void startService() throws Exception{
        if(support != null){
            support.startService();
        }
    }
    
    /**
     * �T�[�r�X���~����B<p>
     * ���̃T�[�r�X�𗘗p�s�\�ȏ�Ԃɂ���B���̃��\�b�h�̌Ăяo����́A���̃T�[�r�X�̋@�\�𗘗p�ł��鎖�͕ۏ؂���Ȃ��B<br>
     * ���̃N���X���p�����ăT�[�r�X����������T�[�r�X�J���҂́A�T�[�r�X�̒�~�������A���̃��\�b�h���I�[�o�[���C�h���Ď������邱�ƁB�f�t�H���g�����́A��ł���B<br>
     *
     * @exception Exception �T�[�r�X�̒�~�����Ɏ��s�����ꍇ�B�A���Astop()�ň���ׂ���āA�����͑��s�����B
     * @see #stop()
     */
    public void stopService() throws Exception{
        if(support != null){
            support.stopService();
        }
    }
    
    /**
     * �T�[�r�X��j������B<p>
     * ���̃T�[�r�X�Ŏg�p���郊�\�[�X���J������B���̃��\�b�h�̌Ăяo����́A���̃T�[�r�X�̋@�\�𗘗p�ł��鎖�͕ۏ؂���Ȃ��B<br>
     * ���̃N���X���p�����ăT�[�r�X����������T�[�r�X�J���҂́A�T�[�r�X�̔j���������A���̃��\�b�h���I�[�o�[���C�h���Ď������邱�ƁB�f�t�H���g�����́A��ł���B<br>
     *
     * @exception Exception �T�[�r�X�̔j�������Ɏ��s�����ꍇ�B�A���Adestroy()�ň���ׂ���āA�����͑��s�����B
     * @see #destroy()
     */
    public void destroyService() throws Exception{
        if(support != null){
            support.destroyService();
        }
    }
    
    // ServiceBaseMBean��JavaDoc
    public void setSystemLoggerServiceName(final ServiceName name){
        if(ServiceManagerFactory.isRegisteredService(name)
             && ServiceManagerFactory.getService(name).getState()
                 == Service.STARTED
        ){
            loggerServiceName = name;
            if(logger != null){
                if(managerName != null){
                    logger.write(
                        SVC__00084,
                        new Object[]{managerName, this.name, loggerServiceName}
                    );
                }else{
                    logger.write(
                        SVC__00085,
                        new Object[]{this.name, loggerServiceName}
                    );
                }
                logger.setLogger(
                    (Logger)ServiceManagerFactory
                        .getServiceObject(loggerServiceName),
                    ServiceManagerFactory.getService(loggerServiceName)
                );
            }
        }else{
            ServiceManagerFactory.addServiceStateListener(
                name,
                new ServiceStateListener(){
                    public void stateChanged(ServiceStateChangeEvent e)
                     throws Exception{
                        ServiceManagerFactory
                            .removeServiceStateListener(name, this);
                        setSystemLoggerServiceName(name);
                    }
                    public boolean isEnabledState(int st){
                        return st == Service.STARTED;
                    }
                }
            );
        }
    }
    
    // ServiceBaseMBean��JavaDoc
    public ServiceName getSystemLoggerServiceName(){
        return loggerServiceName;
    }
    
    // ServiceBaseMBean��JavaDoc
    public void setSystemMessageRecordFactoryServiceName(
        final ServiceName name
    ){
        if(ServiceManagerFactory.isRegisteredService(name)
             && ServiceManagerFactory.getService(name).getState()
                 == Service.STARTED
        ){
            messageServiceName = name;
            if(message != null){
                if(managerName != null){
                    logger.write(
                        SVC__00086,
                        new Object[]{managerName, this.name, messageServiceName}
                    );
                }else{
                    logger.write(
                        SVC__00087,
                        new Object[]{this.name, messageServiceName}
                    );
                }
                message.setMessageRecordFactory(
                    (MessageRecordFactory)ServiceManagerFactory
                        .getServiceObject(messageServiceName),
                    ServiceManagerFactory.getService(messageServiceName)
                );
            }
        }else{
            ServiceManagerFactory.addServiceStateListener(
                name,
                new ServiceStateListener(){
                    public void stateChanged(ServiceStateChangeEvent e)
                     throws Exception{
                        ServiceManagerFactory
                            .removeServiceStateListener(name, this);
                        setSystemMessageRecordFactoryServiceName(name);
                    }
                    public boolean isEnabledState(int st){
                        return st == Service.STARTED;
                    }
                }
            );
        }
    }
    
    // ServiceBaseMBean��JavaDoc
    public ServiceName getSystemMessageRecordFactoryServiceName(){
        return messageServiceName;
    }
    
    /**
     * Service���̃��O�o�͂Ɏg�p����{@link jp.ossc.nimbus.service.log.Logger}���擾����B<p>
     *
     * @return Service���̃��O�o�͂Ɏg�p����{@link jp.ossc.nimbus.service.log.Logger}
     */
    public Logger getLogger(){
        return logger;
    }
    
    /**
     * Service���̃��O�o�͂Ɏg�p����{@link jp.ossc.nimbus.service.log.Logger}��ݒ肷��B<p>
     *
     * @param log Service���̃��O�o�͂Ɏg�p����{@link jp.ossc.nimbus.service.log.Logger}
     */
    public void setLogger(Logger log){
        if(log != null){
            if(log instanceof Service){
                final Service logService = (Service)log;
                final String managerName = logService.getServiceManagerName();
                final String serviceName = logService.getServiceName();
                if(managerName != null && serviceName != null){
                    setSystemLoggerServiceName(
                        new ServiceName(managerName, serviceName)
                    );
                    return;
                }
            }
            if(managerName != null){
                logger.write(
                    SVC__00084,
                    new Object[]{managerName, this.name, null}
                );
            }else{
                logger.write(
                    SVC__00085,
                    new Object[]{this.name, null}
                );
            }
            logger.setLogger(
                log,
                (log instanceof Service) ? (Service)log : null
            );
        }
    }
    
    /**
     * Service���ł̃��b�Z�[�W�擾�Ɏg�p����{@link jp.ossc.nimbus.service.message.MessageRecordFactory}�T�[�r�X���擾����B<p>
     *
     * @return Service���ł̃��b�Z�[�W�擾�Ɏg�p����{@link jp.ossc.nimbus.service.message.MessageRecordFactory}�T�[�r�X
     */
    public MessageRecordFactory getMessageRecordFactory(){
        return message;
    }
    
    /**
     * Service���ł̃��b�Z�[�W�擾�Ɏg�p����{@link jp.ossc.nimbus.service.message.MessageRecordFactory}��ݒ肷��B<p>
     *
     * @param msg Service���ł̃��b�Z�[�W�擾�Ɏg�p����{@link jp.ossc.nimbus.service.message.MessageRecordFactory}
     */
    public void setMessageRecordFactory(MessageRecordFactory msg){
        if(msg != null){
            if(msg instanceof Service){
                final Service msgService = (Service)msg;
                final String managerName = msgService.getServiceManagerName();
                final String serviceName = msgService.getServiceName();
                if(managerName != null && serviceName != null){
                    setSystemMessageRecordFactoryServiceName(
                        new ServiceName(managerName, serviceName)
                    );
                    return;
                }
            }
            if(managerName != null){
                logger.write(
                    SVC__00086,
                    new Object[]{managerName, this.name, null}
                );
            }else{
                logger.write(
                    SVC__00087,
                    new Object[]{this.name, null}
                );
            }
            message.setMessageRecordFactory(
                msg,
                (msg instanceof Service) ? (Service)msg : null
            );
        }
    }
    
    /**
     * ���̃T�[�r�X��o�^����{@link ServiceManager}�̃T�[�r�X�����擾����B<p>
     *
     * @return ServiceManager�̃T�[�r�X��
     * @see #setServiceManagerName(String)
     */
    public String getServiceManagerName(){
        return managerName;
    }
    
    /**
     * ���̃T�[�r�X��o�^����{@link ServiceManager}�̃T�[�r�X����ݒ肷��B<p>
     * {@link ServiceLoader}�ŃT�[�r�X�����[�h����ꍇ�́AServiceLoader���A�o�^���ׂ�ServiceManager��m���Ă���A�Y������ServiceManager�ɓo�^���鎞�ɁA�o�^����ServiceManager�ɂ���Đݒ肳���B<br>
     *
     * @param name ServiceManager�̃T�[�r�X��
     * @see #getServiceManagerName()
     */
    public void setServiceManagerName(String name){
        managerName = name;
        if(name != null){
            if(getServiceName() != null){
                nameObj = new ServiceName(managerName, getServiceName());
            }
        }
    }
    
    /**
     * {@link ServiceBaseSupport}�̃��b�v�Ƃ��Đ������ꂽ�ꍇ�ɁA���b�v����ServiceBaseSupport�̃C���X�^���X���擾����B<p>
     *
     * @see #ServiceBase(ServiceBaseSupport)
     */
    public Object getTarget(){
        if(support != null){
            return support;
        }
        return this;
    }
    
    /**
     * �T�[�r�X�̏�Ԃ��ύX���ꂽ�����Ď�����ServiceStateListener��ǉ�����B<p>
     *
     * @param listener ServiceStateListener�I�u�W�F�N�g
     */
    public void addServiceStateListener(ServiceStateListener listener){
        if(!serviceStateListeners.contains(listener)){
            serviceStateListeners.add(listener);
        }
    }
    
    /**
     * �T�[�r�X�̏�Ԃ��ύX���ꂽ�����Ď�����ServiceStateListener���폜����B<p>
     *
     * @param listener ServiceStateListener�I�u�W�F�N�g
     */
    public void removeServiceStateListener(ServiceStateListener listener){
        serviceStateListeners.remove(listener);
    }
    
    /**
     * �T�[�r�X�̏�Ԃ��ύX���ꂽ����ServiceStateListener�ɒʒm����B<p>
     * �T�[�r�X�̏�Ԃ��ύX���ꂽ�ꍇ�ɁA�Ăяo����܂��B�A���A�I�[�o�[���C�h����ꍇ�́A�K��super.processStateChanged(int)���Ăяo�����ƁB<br>
     * 
     * @param state �ύX��̃T�[�r�X�̏��
     * @see ServiceStateListener
     */
    protected void processStateChanged(int state) throws Exception{
        final Iterator listeners
             = new ArrayList(serviceStateListeners).iterator();
        while(listeners.hasNext()){
            final ServiceStateListener listener
                 = (ServiceStateListener)listeners.next();
            if(listener.isEnabledState(state)){
                listener.stateChanged(new ServiceStateChangeEvent(this));
            }
        }
    }
    
    /**
     * �f�V���A���C�Y���s���B<p>
     *
     * @param in �f�V���A���C�Y�̌����ƂȂ�X�g���[��
     * @exception IOException �ǂݍ��݂Ɏ��s�����ꍇ
     * @exception ClassNotFoundException �f�V���A���C�Y���悤�Ƃ����I�u�W�F�N�g�̃N���X��������Ȃ��ꍇ
     */
    private void readObject(java.io.ObjectInputStream in)
     throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        logger = new LoggerWrapper(ServiceManagerFactory.getLogger());
        message = new MessageRecordFactoryWrapper(
            ServiceManagerFactory.getMessageRecordFactory()
        );
        serviceStateListeners = new ArrayList();
        if(manager == null && managerName != null){
            setServiceManager(ServiceManagerFactory.findManager(managerName));
        }
        logger = new LoggerWrapper(ServiceManagerFactory.getLogger());
        if(loggerServiceName != null){
            setSystemLoggerServiceName(loggerServiceName);
        }
        message = new MessageRecordFactoryWrapper(
            ServiceManagerFactory.getMessageRecordFactory()
        );
        if(messageServiceName != null){
            setSystemMessageRecordFactoryServiceName(messageServiceName);
        }
    }
    
    /**
     * ���̃I�u�W�F�N�g�Ƒ��̃I�u�W�F�N�g�����������ǂ����������B<p>
     * �ȉ��̏��ŁA������r���s���B<br>
     * <ol>
     *   <li>obj��null�̏ꍇ�Afalse</li>
     *   <li>obj�̎Q�Ƃ����̃C���X�^���X�̎Q�ƂƓ������ꍇ�Atrue</li>
     *   <li>{@link #getServiceName()}��null�̏ꍇ�A{Object#equals(Object)}�ɈϏ�</li>
     *   <li>obj��Service�̃C���X�^���X�łȂ��ꍇ�Afalse</li>
     *   <li>obj��Service�̃C���X�^���X�ŁA����{@link Service#getServiceManagerName()}�Ƃ��̃C���X�^���X��{@link #getServiceManagerName()}���������A����{@link Service#getServiceName()}�Ƃ��̃C���X�^���X��{@link #getServiceName()}���������ꍇ�Atrue�B�����łȂ��ꍇ�Afalse</li>
     * </ol>
     *
     * @param obj ��r�Ώۂ̎Q�ƃI�u�W�F�N�g
     * @return obj �����Ɏw�肳�ꂽ�I�u�W�F�N�g�Ƃ��̃I�u�W�F�N�g���������ꍇ��true�A�����łȂ��ꍇ��false
     */
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(this == obj){
            return true;
        }
        if(name == null){
            return super.equals(obj);
        }
        if(obj instanceof Service){
            final Service service = (Service)obj;
            final String mngName = service.getServiceManagerName();
            if(managerName == null){
                return mngName != null
                     ? false : super.equals(obj);
            }else{
                if(managerName.equals(mngName)
                     && name.equals(service.getServiceName())){
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * �I�u�W�F�N�g�̃n�b�V���R�[�h�l��Ԃ��B<p>
     *
     * @return �n�b�V���R�[�h�l
     */
    public int hashCode(){
        if(name == null){
            return super.hashCode();
        }else{
            return managerName != null
                 ? managerName.hashCode() + name.hashCode() : name.hashCode();
        }
    }
    
    
    /**
     * ���̃C���X�^���X�̕�����\����Ԃ��B<p>
     *
     * @return ���̃C���X�^���X�̕�����\��
     */
    public String toString(){
        StringBuilder buf = new StringBuilder();
        if(support != null){
            buf.append(support.toString());
        }else{
            buf.append(super.toString());
        }
        if(managerName != null || name != null){
            buf.append(':');
        }
        if(managerName != null){
            buf.append(managerName);
        }
        if(managerName != null){
            buf.append('#');
        }
        if(name != null){
            buf.append(name);
        }
        return buf.toString();
    }
    
    /**
     * �T�[�r�X����~�y�єj�����ꂸ�ɃK�x�[�W����悤�Ƃ����ꍇ�ɁA��~�����y�єj���������s���B<p>
     *
     * @exception Throwable ��~�A�y�єj�������Ɏ��s�����ꍇ
     */
    protected void finalize() throws Throwable{
        try{
            final int state = getState();
            if(state <= STARTED){
                final String name = getServiceName();
                final ServiceManager manager = getServiceManager();
                if(name == null && manager == null){
                    if(isNecessaryToStop()) stop();
                    if(isNecessaryToDestroy()) destroy();
                }else{
                    Service service = null;
                    try{
                        service = manager.getService(name);
                    }catch(ServiceNotFoundException e){
                    }
                    if(service == this){
                        manager.stopService(name);
                        manager.destroyService(name);
                    }
                }
            }
        }catch(Throwable th){
        }
        super.finalize();
    }
}