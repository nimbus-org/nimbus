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

import java.util.*;
import java.net.*;
import java.io.*;

import jp.ossc.nimbus.service.repository.*;
import jp.ossc.nimbus.service.log.*;
import jp.ossc.nimbus.service.message.*;
import jp.ossc.nimbus.util.WaitSynchronizeMonitor;

/**
 * �T�[�r�X�Ǘ��t�@�N�g���B<p>
 * {@link ServiceLoader}�ɃT�[�r�X��`�̃��[�h��v�����āA�T�[�r�X���Ǘ�����
 * {@link ServiceManager}�𐶐�����t�@�N�g���ł���B<br>
 * ��������ServiceManager�́A{@link Repository}�ŊǗ����A���O�ŃA�N�Z�X�ł���B<br>
 * <p>
 * �܂��AServiceManager�ɑ΂��āA���̖��O��ServiceManager�̊e�����static�ɁA
 * �A�N�Z�X���郉�b�p�[�I�ȋ@�\�����B<br>
 *
 * @author M.Takata
 * @see <a href="ServiceManagerFactoryUsage.txt">�T�[�r�X�Ǘ��t�@�N�g���R�}���h�g�p���@</a>
 */
public class ServiceManagerFactory implements Serializable{
    
    private static final long serialVersionUID = -1120514470640321429L;
    
    private static final String USAGE_RESOURCE
         = "jp/ossc/nimbus/core/ServiceManagerFactoryUsage.txt";
    
    /**
     * �f�t�H���g�̃��O�o�͂��s��{@link LogService}�I�u�W�F�N�g�B<p>
     */
    static final LogService DEFAULT_LOGGER;
    
    /**
     * �f�t�H���g�̃��b�Z�[�W�������s��{@link MessageRecordFactoryService}�I�u�W�F�N�g�B<p>
     */
    static final MessageRecordFactoryService DEFAULT_MESSAGE;
    
    // ���b�Z�[�WID��`
    private static final String SVCMF = "SVCMF";
    private static final String SVCMF0 = SVCMF + 0;
    private static final String SVCMF00 = SVCMF0 + 0;
    private static final String SVCMF000 = SVCMF00 + 0;
    private static final String SVCMF0000 = SVCMF000 + 0;
    private static final String SVCMF00001 = SVCMF0000 + 1;
    private static final String SVCMF00002 = SVCMF0000 + 2;
    private static final String SVCMF00003 = SVCMF0000 + 3;
    private static final String SVCMF00004 = SVCMF0000 + 4;
    private static final String SVCMF00005 = SVCMF0000 + 5;
    private static final String SVCMF00006 = SVCMF0000 + 6;
    private static final String SVCMF00007 = SVCMF0000 + 7;
    private static final String SVCMF00008 = SVCMF0000 + 8;
    private static final String SVCMF00009 = SVCMF0000 + 9;
    private static final String SVCMF00010 = SVCMF000 + 10;
    private static final String SVCMF00011 = SVCMF000 + 11;
    private static final String SVCMF00012 = SVCMF000 + 12;
    private static final String SVCMF00013 = SVCMF000 + 13;
    private static final String SVCMF00014 = SVCMF000 + 14;
    private static final String SVCMF00015 = SVCMF000 + 15;
    private static final String SVCMF00016 = SVCMF000 + 16;
    private static final String SVCMF00017 = SVCMF000 + 17;
    private static final String SVCMF00018 = SVCMF000 + 18;
    private static final String SVCMF00019 = SVCMF000 + 19;
    private static final String SVCMF00020 = SVCMF000 + 20;
    private static final String SVCMF00021 = SVCMF000 + 21;
    private static final String SVCMF00022 = SVCMF000 + 22;
    private static final String SVCMF00023 = SVCMF000 + 23;
    private static final String SVCMF00024 = SVCMF000 + 24;
    private static final String SVCMF00025 = SVCMF000 + 25;
    private static final String SVCMF00026 = SVCMF000 + 26;
    private static final String SVCMF00027 = SVCMF000 + 27;
    private static final String SVCMF00028 = SVCMF000 + 28;
    private static final String SVCMF00029 = SVCMF000 + 29;
    private static final String SVCMF00030 = SVCMF000 + 30;
    private static final String SVCMF00031 = SVCMF000 + 31;
    
    /**
     * {@link ServiceLoader}�̎����N���X���w�肷��V�X�e���v���p�e�B�̃L�[�B<p>
     */
    private static final String LOADER_IMPL_CLASS_KEY
         = "jp.ossc.nimbus.core.loader";
    
    /**
     * {@link ServiceManager}�̎����N���X���w�肷��V�X�e���v���p�e�B�̃L�[�B<p>
     */
    private static final String MANAGER_IMPL_CLASS_KEY
         = "jp.ossc.nimbus.core.manager";
    
    /**
     * ���O�o�͂��s��{@link Logger}�̃��b�p�[�I�u�W�F�N�g�B<p>
     */
    private static LoggerWrapper logger;
    
    /**
     * ���b�Z�[�W�������s��{@link MessageRecordFactory}�̃��b�p�[�I�u�W�F�N�g�B<p>
     */
    private static MessageRecordFactoryWrapper message;
    
    /**
     * ���s�����B<p>
     */
    private static final String LINE_SEPARAOTR
         = System.getProperty("line.separator");
    
    /**
     * �ҋ@���̃T�[�r�X�̌����ƂȂ�T�[�r�X��\������ۂ̐ړ���������B<p>
     */
    private static final String CAUSE_SERVICES = " causes ";
    
    /**
     * �N���Ɏ��s�����T�[�r�X�̌����ƂȂ��O��\������ۂ̐ړ���������B<p>
     */
    private static final String CAUSE_THROWABLE = " cause ";
    
    /**
     * ServiceLoader�̃f�t�H���g�����N���X�B<p>
     */
    private static final Class DEFAULT_SERVICE_LOADER_CLASS
         = DefaultServiceLoaderService.class;
    
    /**
     * �T�[�r�X��`�����[�h����{@link ServiceLoader}���Ǘ�����}�b�v�B<p>
     * <table border="1">
     *   <tr bgcolor="#CCCCFF"><th colspan="2">�L�[</th><th colspan="2">�l</th></tr>
     *   <tr bgcolor="#CCCCFF"><th>�^</th><th>���e</th><th>�^</th><th>���e</th></tr>
     *   <tr><td>java.net.URL</td><td>�T�[�r�X��`��URL</td><td>ServiceLoader</td><td>�L�[��URL�̃T�[�r�X��`�����[�h����{@link ServiceLoader}</td></tr>
     * </table>
     */
    private static final Map loaders = Collections.synchronizedMap(new HashMap());
    
    /**
     * {@link ServiceManager}���Ǘ�����{@link Repository}�B<p>
     * �f�t�H���g�ł́AMap������Repositoty�B<br>
     */
    private static Repository repository = new DefaultRepository();
    
    private static class DefaultRepository implements Repository{
        private final Map managerMap = new Hashtable();
        
        public Object get(String name){
            return (Service)managerMap.get(name);
        }
        
        public boolean register(String name, Object manager){
            if(managerMap.containsKey(name)){
                return false;
            }
            managerMap.put(name, manager);
            return true;
        }
        
        public boolean unregister(String name){
            managerMap.remove(name);
            return true;
        }
        
        public boolean isRegistered(String name){
            return managerMap.containsKey(name);
        }
        
        public Set nameSet(){
            return new HashSet(managerMap.keySet());
        }
        
        public Set registeredSet(){
            return new HashSet(managerMap.values());
        }
    };
    
    /**
     * ����ServiceManagerFactory�ɓo�^���ꂽ�o�^��ԃ��X�i�̃��X�g�B<p>
     */
    private static List registrationListeners = new ArrayList();
    
    /**
     * {@link ServiceLoader}�����N���X�B<p>
     */
    private static Class loaderClass = DEFAULT_SERVICE_LOADER_CLASS;
    
    /**
     * {@link ServiceManager}�����N���X�B<p>
     */
    private static Class managerClass;
    
    static{
        DEFAULT_LOGGER = new LogService();
        try{
            DEFAULT_LOGGER.create();
            DEFAULT_LOGGER.start();
            DEFAULT_LOGGER.setSystemDebugEnabled(false);
            DEFAULT_LOGGER.setSystemInfoEnabled(true);
            DEFAULT_LOGGER.setSystemWarnEnabled(true);
            DEFAULT_LOGGER.setSystemErrorEnabled(true);
            DEFAULT_LOGGER.setSystemFatalEnabled(true);
        }catch(Exception e){
            e.printStackTrace();
        }
        logger = new LoggerWrapper(DEFAULT_LOGGER);
        
        DEFAULT_MESSAGE = new MessageRecordFactoryService();
        try{
            DEFAULT_MESSAGE.create();
            DEFAULT_MESSAGE.start();
        }catch(Exception e){
            e.printStackTrace();
        }
        message = new MessageRecordFactoryWrapper(DEFAULT_MESSAGE);
    }
    
    private static Properties properties = new Properties();
    
    /**
     * �R���X�g���N�^�B<p>
     */
    private ServiceManagerFactory(){
        super();
    }
    
    /**
     * �f�t�H���g�̃T�[�r�X��`�����[�h����B<p>
     * {@link #loadManager(URL)}������null�ŌĂяo���B<br>
     * ���̃��\�b�h�ɂ���ă��[�h�����T�[�r�X��`�t�@�C���́A{@link Utility#getDefaultServiceURL()}�Ŏ擾�����URL�̃T�[�r�X��`�t�@�C���ł���B
     * 
     * @return ���[�h�ɐ��������ꍇtrue�B�A���A�����Ō��������́A�K�������T�[�r�X��`�ɒ�`���ꂽ�T�[�r�X���S�Đ���ɋN����������ۏ؂�����̂ł́A����܂���B�T�[�r�X��`�̃��[�h�Ɏg�p����ServiceLoader������ɋN�����ꂽ���������܂��B
     */
    public static synchronized boolean loadManager(){
        return loadManager((URL)null);
    }
    
    /**
     * �w�肵���p�X�̃T�[�r�X��`�����[�h����B<p>
     * �w�肵���p�X�́A{@link Utility#convertServicePathToURL(String)}��URL�ɕϊ�����A{@link #loadManager(URL)}���Ăяo���B<br>
     *
     * @param path �T�[�r�X��`�t�@�C���̃p�X
     * @return ���[�h�ɐ��������ꍇtrue�B�A���A�����Ō��������́A�K�������T�[�r�X��`�ɒ�`���ꂽ�T�[�r�X���S�Đ���ɋN����������ۏ؂�����̂ł́A����܂���B�T�[�r�X��`�̃��[�h�Ɏg�p����ServiceLoader������ɋN�����ꂽ���������܂��B
     * @exception IllegalArgumentException �w�肵���p�X���s���ȏꍇ�A�܂��͑��݂��Ȃ��ꍇ
     */
    public static synchronized boolean loadManager(String path){
        return loadManager(path, false, false);
    }
    
    /**
     * �w�肵��URL�̃T�[�r�X��`�����[�h����B<p>
     * {@link #loadManager(URL, boolean)}���AloadManager(url, false)�ŌĂяo���B<br>
     *
     * @param url �T�[�r�X��`�t�@�C����URL
     * @return ���[�h�ɐ��������ꍇtrue�B�A���A�����Ō��������́A�K�������T�[�r�X��`�ɒ�`���ꂽ�T�[�r�X���S�Đ���ɋN����������ۏ؂�����̂ł́A����܂���B�T�[�r�X��`�̃��[�h�Ɏg�p����ServiceLoader������ɋN�����ꂽ���������܂��B
     */
    public static synchronized boolean loadManager(URL url){
        return loadManager(url, false);
    }
    
    /**
     * �w�肵���p�X�̃T�[�r�X��`�����[�h����B<p>
     * �w�肵���p�X�́A{@link Utility#convertServicePathToURL(String)}��URL�ɕϊ�����A{@link #loadManager(URL, boolean)}���Ăяo���B<br>
     *
     * @param path �T�[�r�X��`�t�@�C���̃p�X
     * @param isReload ���Ƀ��[�h�����T�[�r�X��`���ă��[�h����ꍇ�ɂ́Atrue
     * @return ���[�h�ɐ��������ꍇtrue�B�A���A�����Ō��������́A�K�������T�[�r�X��`�ɒ�`���ꂽ�T�[�r�X���S�Đ���ɋN����������ۏ؂�����̂ł́A����܂���B�T�[�r�X��`�̃��[�h�Ɏg�p����ServiceLoader������ɋN�����ꂽ���������܂��B
     */
    public static synchronized boolean loadManager(
        String path,
        boolean isReload
    ){
        return loadManager(
            path,
            isReload,
            false
        );
    }
    
    /**
     * �w�肵��URL�̃T�[�r�X��`�����[�h����B<p>
     * {@link #loadManager(URL, boolean, boolean)}���AloadManager(url, isReload, false)�ŌĂяo���B<br>
     *
     * @param url �T�[�r�X��`�t�@�C����URL
     * @param isReload ���Ƀ��[�h�����T�[�r�X��`���ă��[�h����ꍇ�ɂ́Atrue
     * @return ���[�h�ɐ��������ꍇtrue�B�A���A�����Ō��������́A�K�������T�[�r�X��`�ɒ�`���ꂽ�T�[�r�X���S�Đ���ɋN����������ۏ؂�����̂ł́A����܂���B�T�[�r�X��`�̃��[�h�Ɏg�p����ServiceLoader������ɋN�����ꂽ���������܂��B
     */
    public static synchronized boolean loadManager(URL url, boolean isReload){
        return loadManager(url, isReload, false);
    }
    
    /**
     * �w�肵���p�X�̃T�[�r�X��`�����[�h����B<p>
     * �w�肵���p�X�́A{@link Utility#convertServicePathToURL(String)}��URL�ɕϊ�����A{@link #loadManager(URL, boolean, boolean)}���Ăяo���B<br>
     *
     * @param path �T�[�r�X��`�t�@�C���̃p�X
     * @param isReload ���Ƀ��[�h�����T�[�r�X��`���ă��[�h����ꍇ�ɂ́Atrue
     * @param isValidate �T�[�r�X��`�t�@�C����]�����邩�ǂ����B�]������ꍇ��true
     * @return ���[�h�ɐ��������ꍇtrue�B�A���A�����Ō��������́A�K�������T�[�r�X��`�ɒ�`���ꂽ�T�[�r�X���S�Đ���ɋN����������ۏ؂�����̂ł́A����܂���B�T�[�r�X��`�̃��[�h�Ɏg�p����ServiceLoader������ɋN�����ꂽ���������܂��B
     */
    public static synchronized boolean loadManager(
        String path,
        boolean isReload,
        boolean isValidate
    ){
        URL url = null;
        try{
            url = Utility.convertServicePathToURL(path);
        }catch(IllegalArgumentException e){
            logger.write(SVCMF00030, path, e);
            return false;
        }
        if(url == null){
            logger.write(SVCMF00030, path);
            return false;
        }
        return loadManager(url, isReload, isValidate);
    }
    
    /**
     * �w�肵���p�X�̃T�[�r�X��`�����[�h����B<p>
     * �w�肵���p�X�́A{@link Utility#convertServicePathToURL(String)}��URL�ɕϊ�����A{@link #loadManager(URL, boolean, boolean)}���Ăяo���B<br>
     *
     * @param path �T�[�r�X��`�t�@�C���̃p�X
     * @param config �T�[�r�X���[�_�\�����
     * @param isReload ���Ƀ��[�h�����T�[�r�X��`���ă��[�h����ꍇ�ɂ́Atrue
     * @param isValidate �T�[�r�X��`�t�@�C����]�����邩�ǂ����B�]������ꍇ��true
     * @return ���[�h�ɐ��������ꍇtrue�B�A���A�����Ō��������́A�K�������T�[�r�X��`�ɒ�`���ꂽ�T�[�r�X���S�Đ���ɋN����������ۏ؂�����̂ł́A����܂���B�T�[�r�X��`�̃��[�h�Ɏg�p����ServiceLoader������ɋN�����ꂽ���������܂��B
     */
    public static synchronized boolean loadManager(
        String path,
        ServiceLoaderConfig config,
        boolean isReload,
        boolean isValidate
    ){
        URL url = null;
        try{
            url = Utility.convertServicePathToURL(path);
        }catch(IllegalArgumentException e){
            logger.write(SVCMF00030, path, e);
            return false;
        }
        if(url == null){
            logger.write(SVCMF00030, path);
            return false;
        }
        return loadManager(url, config, isReload, isValidate);
    }
    
    /**
     * �w�肵��URL�̃T�[�r�X��`�����[�h����B<p>
     *
     * @param url �T�[�r�X��`�t�@�C����URL
     * @param isReload ���Ƀ��[�h�����T�[�r�X��`���ă��[�h����ꍇ�ɂ́Atrue
     * @param isValidate �T�[�r�X��`�t�@�C����]�����邩�ǂ����B�]������ꍇ��true
     * @return ���[�h�ɐ��������ꍇtrue�B�A���A�����Ō��������́A�K�������T�[�r�X��`�ɒ�`���ꂽ�T�[�r�X���S�Đ���ɋN����������ۏ؂�����̂ł́A����܂���B�T�[�r�X��`�̃��[�h�Ɏg�p����ServiceLoader������ɋN�����ꂽ���������܂��B
     */
    public static synchronized boolean loadManager(
        URL url,
        boolean isReload,
        boolean isValidate
    ){
        return loadManager(url, null, isReload, isValidate);
    }
    
    /**
     * �w�肵��URL�̃T�[�r�X��`�����[�h����B<p>
     *
     * @param url �T�[�r�X��`�t�@�C����URL
     * @param config �T�[�r�X���[�_�\�����
     * @param isReload ���Ƀ��[�h�����T�[�r�X��`���ă��[�h����ꍇ�ɂ́Atrue
     * @param isValidate �T�[�r�X��`�t�@�C����]�����邩�ǂ����B�]������ꍇ��true
     * @return ���[�h�ɐ��������ꍇtrue�B�A���A�����Ō��������́A�K�������T�[�r�X��`�ɒ�`���ꂽ�T�[�r�X���S�Đ���ɋN����������ۏ؂�����̂ł́A����܂���B�T�[�r�X��`�̃��[�h�Ɏg�p����ServiceLoader������ɋN�����ꂽ���������܂��B
     */
    public static synchronized boolean loadManager(
        URL url,
        ServiceLoaderConfig config,
        boolean isReload,
        boolean isValidate
    ){
        logger.write(
            SVCMF00001,
            new Object[]{url, isReload ? Boolean.TRUE : Boolean.FALSE}
        );
        
        if(url == null){
            url = Utility.getDefaultServiceURL();
        }
        
        if(url == null){
            return false;
        }
        
        ServiceLoader loader = null;
        if(!loaders.containsKey(url)){
            final String loaderClassName
                 = System.getProperty(LOADER_IMPL_CLASS_KEY);
            if(loaderClassName != null && loaderClassName.length() != 0){
                try{
                    final Class clazz = Class.forName(
                        loaderClassName,
                        true,
                        NimbusClassLoader.getInstance()
                    );
                    setServiceLoaderClass(clazz);
                }catch(ClassNotFoundException e){
                    logger.write(
                        SVCMF00002,
                        new Object[]{ServiceLoader.class, loaderClassName},
                        e
                    );
                }catch(IllegalArgumentException e){
                    logger.write(SVCMF00004, loaderClassName, e);
                }
            }
            try{
                loader = (ServiceLoader)getServiceLoaderClass().newInstance();
            }catch(InstantiationException e){
                logger.write(SVCMF00005, getServiceLoaderClass(), e);
                return false;
            }catch(IllegalAccessException e){
                logger.write(SVCMF00006, getServiceLoaderClass(), e);
                return false;
            }
            String managerClassName
                 = System.getProperty(MANAGER_IMPL_CLASS_KEY);
            if((managerClassName == null || managerClassName.length() == 0)
                && getServiceManagerClass() != null){
                managerClassName = getServiceManagerClass().getName();
            }
            if(managerClassName != null && managerClassName.length() != 0){
                try{
                    loader.setServiceManagerClassName(managerClassName);
                }catch(ClassNotFoundException e){
                    logger.write(
                        SVCMF00002,
                        new Object[]{ServiceManager.class, managerClassName},
                        e
                    );
                }catch(IllegalArgumentException e){
                    logger.write(SVCMF00031, managerClassName, e);
                }
            }
            try{
                loader.setServiceURL(url);
            }catch(IllegalArgumentException e){
                logger.write(SVCMF00007, url, e);
                return false;
            }
        }else if(isReload){
            loader = (ServiceLoader)loaders.get(url);
            loader.stop();
            loader.destroy();
            unregisterLoader(loader);
        }else{
            return true;
        }
        
        loader.setValidate(isValidate);
        loader.setConfig(config);
        
        try{
            loader.create();
            loader.start();
        }catch(Exception e){
            logger.write(SVCMF00008, url, e);
            loader.destroy();
            return false;
        }
        
        registerLoader(loader);
        logger.write(SVCMF00009, url);
        return true;
    }
    
    /**
     * �f�t�H���g�̃T�[�r�X��`���A�����[�h����B<p>
     * {@link #unloadManager(URL)}������null�ŌĂяo���B<br>
     *
     * @return �T�[�r�X��`�̃A�����[�h�������s�����ꍇtrue
     */
    public static synchronized boolean unloadManager(){
        return unloadManager((URL)null);
    }
    
    /**
     * �w�肳�ꂽ�p�X�̃T�[�r�X��`���A�����[�h����B<p>
     * �w�肵���p�X�́A{@link Utility#convertServicePathToURL(String)}��URL�ɕϊ�����A{@link #unloadManager(URL)}���Ăяo���B<br>
     *
     * @param path �T�[�r�X��`�t�@�C���̃p�X
     * @return �T�[�r�X��`�̃A�����[�h�������s�����ꍇtrue
     */
    public static synchronized boolean unloadManager(String path){
        URL url = null;
        try{
            url = Utility.convertServicePathToURL(path);
        }catch(IllegalArgumentException e){
            try{
                url = new File(path).toURL();
            }catch(MalformedURLException ee){
                // ���̗�O�͔������Ȃ��͂�
                return false;
            }
        }
        return unloadManager(url);
    }
    
    /**
     * �w�肳�ꂽ�p�X�̃T�[�r�X��`���A�����[�h����B<p>
     *
     * @param url �T�[�r�X��`�t�@�C����URL
     * @return �T�[�r�X��`�̃A�����[�h�������s�����ꍇtrue
     */
    public static synchronized boolean unloadManager(URL url){
        
        logger.write(SVCMF00010, url);
        
        if(url == null){
            url = Utility.getDefaultServiceURL();
        }
        
        if(url == null){
            return false;
        }
        
        if(!loaders.containsKey(url)){
            
            logger.write(SVCMF00011, url);
            return false;
        }else{
            Service service = (Service)loaders.get(url);
            service.stop();
            service.destroy();
            
            logger.write(SVCMF00012, url);
        }
        return true;
    }
    
    /**
     * ���[�h�����T�[�r�X��`�ɒ�`���ꂽ�T�[�r�X���S�ċN������Ă��邩���ׂ�B<p>
     * @return �S�ċN������Ă���ꍇtrue
     */
    public static boolean checkLoadManagerCompleted(){
        return checkLoadManagerCompleted(null);
    }
    
    /**
     * ���[�h�����T�[�r�X��`�ɒ�`���ꂽ�T�[�r�X���S�ċN������Ă��邩���ׂ�B<p>
     * �N������Ă��Ȃ��T�[�r�X����{@link ServiceName}�Ƃ��āAnotStarted�Ɋi�[���ĕԂ��B<br>
     * 
     * @param notStarted �N���ł��Ȃ������T�[�r�X���̏W�����i�[����Z�b�g
     * @return �S�ċN������Ă���ꍇtrue
     */
    public static boolean checkLoadManagerCompleted(Set notStarted){
        
        logger.write(SVCMF00013);
        final Set tmpNotStarted = new HashSet();
        final ServiceManager[] managers = findManagers();
        final StringBuffer message = new StringBuffer();
        for(int i = 0, max = managers.length; i < max; i++){
            final ServiceManager manager = managers[i];
            final String managerName = manager.getServiceName();
            if(manager.existFailedService()){
                final Iterator failedServices
                     = manager.getFailedServices().iterator();
                while(failedServices.hasNext()){
                    final String failedService
                         = (String)failedServices.next();
                    final ServiceName name
                         = new ServiceName(managerName, failedService);
                    tmpNotStarted.add(name);
                    message.append(name);
                    final Throwable cause
                         = manager.getFailedCause(failedService);
                    if(cause != null){
                        message.append(CAUSE_THROWABLE);
                        message.append(cause);
                    }
                    if(failedServices.hasNext()){
                        message.append(LINE_SEPARAOTR);
                    }
                }
                if(i != max - 1){
                    message.append(LINE_SEPARAOTR);
                }
            }
        }
        boolean mustInsertLine = message.length() != 0;
        for(int i = 0, max = managers.length; i < max; i++){
            final ServiceManager manager = managers[i];
            final String managerName = manager.getServiceName();
            if(manager.existWaitingService()){
                final Iterator waitingServices
                     = manager.getWaitingServices().iterator();
                while(waitingServices.hasNext()){
                    final String waitingService
                         = (String)waitingServices.next();
                    final ServiceName name
                         = new ServiceName(managerName, waitingService);
                    if(!tmpNotStarted.contains(name)
                        && !waitingService.equals(managerName)){
                        tmpNotStarted.add(name);
                    }else{
                        continue;
                    }
                    if(mustInsertLine){
                        message.append(LINE_SEPARAOTR);
                        mustInsertLine = false;
                    }
                    message.append(name);
                    final Set causes = manager.getWaitingCauses(waitingService);
                    if(causes != null && causes.size() != 0){
                        message.append(CAUSE_SERVICES);
                        message.append(causes);
                    }
                    if(waitingServices.hasNext()){
                        message.append(LINE_SEPARAOTR);
                    }
                }
                if(i != max - 1){
                    message.append(LINE_SEPARAOTR);
                }
            }
        }
        final boolean isSuccess = tmpNotStarted.size() == 0;
        if(!isSuccess){
            if(notStarted != null){
                notStarted.addAll(tmpNotStarted);
            }
            logger.write(SVCMF00014, message.toString());
        }
        return isSuccess;
    }
    
    /**
     * ���[�h�����T�[�r�X��`�ɒ�`���ꂽ�T�[�r�X���S�ċN������Ă��邩���ׂ�B<p>
     * @param managerNames �`�F�b�N����}�l�[�W�����̏W��
     * @return �S�ċN������Ă���ꍇtrue
     */
    public static boolean checkLoadManagerCompletedBy(Set managerNames){
        return checkLoadManagerCompletedBy(managerNames, null);
    }
    
    /**
     * ���[�h�����T�[�r�X��`�ɒ�`���ꂽ�T�[�r�X���S�ċN������Ă��邩���ׂ�B<p>
     * �N������Ă��Ȃ��T�[�r�X����{@link ServiceName}�Ƃ��āAnotStarted�Ɋi�[���ĕԂ��B<br>
     * 
     * @param managerNames �`�F�b�N����}�l�[�W�����̏W��
     * @param notStarted �N���ł��Ȃ������T�[�r�X���̏W�����i�[����Z�b�g
     * @return �S�ċN������Ă���ꍇtrue
     */
    public static boolean checkLoadManagerCompletedBy(
        Set managerNames,
        Set notStarted
    ){
        logger.write(SVCMF00013);
        final Set tmpNotStarted = new HashSet();
        final ServiceManager[] managers = findManagers();
        final StringBuffer message = new StringBuffer();
        for(int i = 0, max = managers.length; i < max; i++){
            final ServiceManager manager = managers[i];
            final String managerName = manager.getServiceName();
            if(!managerNames.contains(managerName)){
                continue;
            }
            if(manager.existFailedService()){
                final Iterator failedServices
                     = manager.getFailedServices().iterator();
                while(failedServices.hasNext()){
                    final String failedService
                         = (String)failedServices.next();
                    final ServiceName name
                         = new ServiceName(managerName, failedService);
                    tmpNotStarted.add(name);
                    message.append(name);
                    final Throwable cause
                         = manager.getFailedCause(failedService);
                    if(cause != null){
                        message.append(CAUSE_THROWABLE);
                        message.append(cause);
                    }
                    if(failedServices.hasNext()){
                        message.append(LINE_SEPARAOTR);
                    }
                }
                if(i != max - 1){
                    message.append(LINE_SEPARAOTR);
                }
            }
        }
        boolean mustInsertLine = message.length() != 0;
        for(int i = 0, max = managers.length; i < max; i++){
            final ServiceManager manager = managers[i];
            final String managerName = manager.getServiceName();
            if(!managerNames.contains(managerName)){
                continue;
            }
            if(manager.existWaitingService()){
                final Iterator waitingServices
                     = manager.getWaitingServices().iterator();
                while(waitingServices.hasNext()){
                    final String waitingService
                         = (String)waitingServices.next();
                    final ServiceName name
                         = new ServiceName(managerName, waitingService);
                    if(!tmpNotStarted.contains(name)
                        && !waitingService.equals(managerName)){
                        tmpNotStarted.add(name);
                    }else{
                        continue;
                    }
                    if(mustInsertLine){
                        message.append(LINE_SEPARAOTR);
                        mustInsertLine = false;
                    }
                    message.append(name);
                    final Set causes = manager.getWaitingCauses(waitingService);
                    if(causes != null && causes.size() != 0){
                        message.append(CAUSE_SERVICES);
                        message.append(causes);
                    }
                    if(waitingServices.hasNext()){
                        message.append(LINE_SEPARAOTR);
                    }
                }
                if(i != max - 1){
                    message.append(LINE_SEPARAOTR);
                }
            }
        }
        final boolean isSuccess = tmpNotStarted.size() == 0;
        if(!isSuccess){
            if(notStarted != null){
                notStarted.addAll(tmpNotStarted);
            }
            logger.write(SVCMF00014, message.toString());
        }
        return isSuccess;
    }
    
    /**
     * ���[�h�����T�[�r�X��`�ɒ�`���ꂽ�T�[�r�X���S�ċN������Ă��邩���ׂ�B<p>
     * @param managerName �`�F�b�N����}�l�[�W����
     * @return �S�ċN������Ă���ꍇtrue
     */
    public static boolean checkLoadManagerCompletedBy(String managerName){
        return checkLoadManagerCompletedBy(managerName, null);
    }
    
    /**
     * ���[�h�����T�[�r�X��`�ɒ�`���ꂽ�T�[�r�X���S�ċN������Ă��邩���ׂ�B<p>
     * �N������Ă��Ȃ��T�[�r�X����{@link ServiceName}�Ƃ��āAnotStarted�Ɋi�[���ĕԂ��B<br>
     * 
     * @param managerName �`�F�b�N����}�l�[�W����
     * @param notStarted �N���ł��Ȃ������T�[�r�X���̏W�����i�[����Z�b�g
     * @return �S�ċN������Ă���ꍇtrue
     */
    public static boolean checkLoadManagerCompletedBy(
        String managerName,
        Set notStarted
    ){
        logger.write(SVCMF00013);
        final Set tmpNotStarted = new HashSet();
        final ServiceManager manager = findManager(managerName);
        final StringBuffer message = new StringBuffer();
        if(manager.existFailedService()){
            final Iterator failedServices
                 = manager.getFailedServices().iterator();
            while(failedServices.hasNext()){
                final String failedService
                     = (String)failedServices.next();
                final ServiceName name
                     = new ServiceName(managerName, failedService);
                tmpNotStarted.add(name);
                message.append(name);
                final Throwable cause
                     = manager.getFailedCause(failedService);
                if(cause != null){
                    message.append(CAUSE_THROWABLE);
                    message.append(cause);
                }
                if(failedServices.hasNext()){
                    message.append(LINE_SEPARAOTR);
                }
            }
        }
        boolean mustInsertLine = message.length() != 0;
        if(manager.existWaitingService()){
            final Iterator waitingServices
                 = manager.getWaitingServices().iterator();
            while(waitingServices.hasNext()){
                final String waitingService
                     = (String)waitingServices.next();
                final ServiceName name
                     = new ServiceName(managerName, waitingService);
                if(!tmpNotStarted.contains(name)
                    && !waitingService.equals(managerName)){
                    tmpNotStarted.add(name);
                }else{
                    continue;
                }
                if(mustInsertLine){
                    message.append(LINE_SEPARAOTR);
                    mustInsertLine = false;
                }
                message.append(name);
                final Set causes = manager.getWaitingCauses(waitingService);
                if(causes != null && causes.size() != 0){
                    message.append(CAUSE_SERVICES);
                    message.append(causes);
                }
                if(waitingServices.hasNext()){
                    message.append(LINE_SEPARAOTR);
                }
            }
        }
        final boolean isSuccess = tmpNotStarted.size() == 0;
        if(!isSuccess){
            if(notStarted != null){
                notStarted.addAll(tmpNotStarted);
            }
            logger.write(SVCMF00014, message.toString());
        }
        return isSuccess;
    }
    
    /**
     * ���[�h���ꂽ�S�Ă�ServiceManager��T���B<p>
     *
     * @return ���[�h���ꂽ�S�Ă�ServiceManager�̔z��B�P�����[�h����Ă��Ȃ��ꍇ�́A�����O�̔z���Ԃ��B
     */
    public static ServiceManager[] findManagers(){
        final Set managerSet = repository.registeredSet();
        final ServiceManager[] managers = new ServiceManager[managerSet.size()];
        managerSet.toArray(managers);
        return managers;
    }
    
    /**
     * �f�t�H���g�̖��O������ServiceManager��T���B<p>
     * �����Ō����A�f�t�H���g�̖��O�́A{@link ServiceManager#DEFAULT_NAME}�ł���B<br>
     *
     * @return �f�t�H���g�̖��O������ServiceManager�B������Ȃ��ꍇ�́Anull��Ԃ��B
     */
    public static ServiceManager findManager(){
        return findManager(ServiceManager.DEFAULT_NAME);
    }
    
    /**
     * �w�肳�ꂽ���O������ServiceManager��T���B<p>
     *
     * @param name ServiceManager�̖��O
     * @return �w�肳�ꂽ���O������ServiceManager�B������Ȃ��ꍇ�́Anull��Ԃ��B
     */
    public static ServiceManager findManager(String name){
        if(name == null){
            return null;
        }
        return (ServiceManager)repository.get(name);
    }
    
    /**
     * ServiceManager��o�^����B<p>
     * ServiceManager�̃f�t�H���g�����N���X�ł���{@link DefaultServiceManagerService}���g�p����B<br>
     *
     * @param name ServiceManager�̓o�^��
     * @return �o�^�ł����ꍇtrue
     * @see #registerManager(String, ServiceManager)
     */
    public static boolean registerManager(String name){
        final DefaultServiceLoaderService loader
             = new DefaultServiceLoaderService();
        final ServerMetaData serverData = new ServerMetaData(loader, null);
        final ManagerMetaData managerData = new ManagerMetaData(loader, serverData);
        managerData.setName(name);
        serverData.addManager(managerData);
        loader.setServerMetaData(serverData);
        try{
            loader.create();
            loader.start();
        }catch(Exception e){
            // �N����Ȃ��͂�
            loader.destroy();
            return false;
        }
        return true;
    }
    
    /**
     * ServiceManager��o�^����B<p>
     * �㏑���o�^�ł��邩�ǂ����́AServiceManager�̊Ǘ��ɗp����{@link Repository}�̎����Ɉˑ�����B�f�t�H���g��Repository�́A�㏑���o�^�͋����Ȃ��B<br>
     * <p>
     * �o�^�ł����ꍇ�́A{@link #processRegisterd(ServiceManager)}���Ăяo���āA�o�^����Ă���{@link RegistrationListener}�ɓo�^��ʒm����B<br>
     *
     * @param name ServiceManager�̓o�^��
     * @param manager �o�^����ServiceManager�I�u�W�F�N�g
     * @return �o�^�ł����ꍇtrue
     */
    public static boolean registerManager(String name, ServiceManager manager){
        logger.write(SVCMF00015, new Object[]{name, manager});
        final boolean result = repository.register(name, manager);
        if(result){
            logger.write(SVCMF00016, name);
            if(manager != null){
                processRegisterd(manager);
            }
        }else{
            logger.write(SVCMF00017, name);
        }
        return result;
    }
    
    /**
     * �w�肵�����O������ServiceManager�̓o�^����������B<p>
     * �o�^�������ł����ꍇ�́A{@link #processUnregisterd(ServiceManager)}���Ăяo���āA�o�^����Ă���{@link RegistrationListener}�ɓo�^������ʒm����B<br>
     *
     * @param name ServiceManager�̓o�^��
     * @return �o�^�������ł����ꍇtrue
     */
    public static boolean unregisterManager(String name){
        logger.write(SVCMF00018, name);
        final ServiceManager manager = findManager(name);
        final boolean result = repository.unregister(name);
        if(result){
            logger.write(SVCMF00019, name);
            if(manager != null){
                processUnregisterd(manager);
            }
        }else{
            logger.write(SVCMF00020, name);
        }
        return result;
    }
    
    /**
     * �w�肳�ꂽ���O�̃}�l�[�W�����o�^����Ă��邩���ׂ�B<p>
     *
     * @param name �}�l�[�W����
     * @return �o�^����Ă����ꍇtrue
     */
    public static boolean isRegisteredManager(String name){
        return repository.isRegistered(name); 
    }
    
    /**
     * �w�肳�ꂽ���O��ServiceManager����A�w�肳�ꂽ�T�[�r�X���̃T�[�r�X���擾����B<p>
     * ServiceLoader�Ń��[�h���ꂽ�T�[�r�X�́A{@link Service}�C���^�t�F�[�X���������Ă��Ȃ��Ă��AService�C���^�t�F�[�X�������������b�p�[�ł���܂�ēo�^�����B<br>
     * ���̃��\�b�h�́A���̓����𐶂����AServiceLoader�Ń��[�h���ꂽ�T�[�r�X��Service�I�u�W�F�N�g�Ƃ��Ď擾���郁�\�b�h�ł���B<br>
     * �ʏ�A�T�[�r�X�̋N���A��~�Ȃǂ̏������s�������ꍇ�ɁA���̃��\�b�h�ŃT�[�r�X���擾����B<br>
     *
     * @param managerName ServiceManager�̖��O
     * @param serviceName �T�[�r�X��
     * @return �T�[�r�X
     * @exception ServiceNotFoundException �w�肳�ꂽ�T�[�r�X��������Ȃ��ꍇ
     */
    public static Service getService(String managerName, String serviceName)
     throws ServiceNotFoundException{
        final ServiceManager manager = findManager(managerName);
        if(manager != null){
            return manager.getService(serviceName);
        }
        throw new ServiceNotFoundException(managerName, serviceName);
    }
    
    /**
     * �w�肳�ꂽ�T�[�r�X���̃T�[�r�X���擾����B<p>
     *
     * @param serviceName �T�[�r�X��
     * @return �T�[�r�X
     * @exception ServiceNotFoundException �w�肳�ꂽ�T�[�r�X��������Ȃ��ꍇ
     * @see #getService(String, String)
     */
    public static Service getService(ServiceName serviceName)
     throws ServiceNotFoundException{
        if(serviceName == null){
            throw new ServiceNotFoundException(null, null);
        }
        return getService(
            serviceName.getServiceManagerName(),
            serviceName.getServiceName()
        );
    }
    
    /**
     * �f�t�H���g�̖��O��ServiceManager����A�w�肳�ꂽ�T�[�r�X���̃T�[�r�X���擾����B<p>
     *
     * @param serviceName �T�[�r�X��
     * @return �T�[�r�X
     * @exception ServiceNotFoundException �w�肳�ꂽ�T�[�r�X��������Ȃ��ꍇ
     * @see #getService(String, String)
     */
    public static Service getService(String serviceName)
     throws ServiceNotFoundException{
        final ServiceManager manager = findManager();
        if(manager != null){
            return manager.getService(serviceName);
        }
        throw new ServiceNotFoundException(
            ServiceManager.DEFAULT_NAME,
            serviceName
        );
    }
    
    /**
     * �w�肳�ꂽ���O��ServiceManager����A�w�肳�ꂽ�T�[�r�X���̃T�[�r�X�̒�`�����擾����B<p>
     *
     * @param managerName ServiceManager�̖��O
     * @param serviceName �T�[�r�X��
     * @return �T�[�r�X��`���
     * @exception ServiceNotFoundException �w�肳�ꂽ�T�[�r�X��������Ȃ��ꍇ
     */
    public static ServiceMetaData getServiceMetaData(
        String managerName,
        String serviceName
    ) throws ServiceNotFoundException{
        final ServiceManager manager = findManager(managerName);
        if(manager != null){
            return manager.getServiceMetaData(serviceName);
        }
        throw new ServiceNotFoundException(managerName, serviceName);
    }
    
    /**
     * �w�肳�ꂽ�T�[�r�X���̃T�[�r�X�̒�`�����擾����B<p>
     *
     * @param serviceName �T�[�r�X��
     * @return �T�[�r�X��`���
     * @exception ServiceNotFoundException �w�肳�ꂽ�T�[�r�X��������Ȃ��ꍇ
     */
    public static ServiceMetaData getServiceMetaData(ServiceName serviceName)
     throws ServiceNotFoundException{
        if(serviceName == null){
            throw new ServiceNotFoundException(null);
        }
        return getServiceMetaData(
            serviceName.getServiceManagerName(),
            serviceName.getServiceName()
        );
    }
    
    /**
     * �f�t�H���g�̖��O��ServiceManager����A�w�肳�ꂽ�T�[�r�X���̃T�[�r�X��`�����擾����B<p>
     *
     * @param serviceName �T�[�r�X��
     * @return �T�[�r�X��`���
     * @exception ServiceNotFoundException �w�肳�ꂽ�T�[�r�X��������Ȃ��ꍇ
     */
    public static ServiceMetaData getServiceMetaData(
        String serviceName
    ) throws ServiceNotFoundException{
        final ServiceManager manager = findManager();
        if(manager != null){
            return manager.getServiceMetaData(serviceName);
        }
        throw new ServiceNotFoundException(
            ServiceManager.DEFAULT_NAME,
            serviceName
        );
    }
    
    /**
     * �w�肳�ꂽ���O��ServiceManager����A�w�肳�ꂽ�T�[�r�X���̃T�[�r�X�I�u�W�F�N�g���擾����B<p>
     * ServiceLoader�Ń��[�h���ꂽ�T�[�r�X�́A{@link Service}�C���^�t�F�[�X���������Ă��Ȃ��Ă��AService�C���^�t�F�[�X�������������b�p�[�ł���܂�ēo�^�����B<br>
     * {@link #getService(String, String)}���\�b�h�ł́AServiceLoader�Ń��[�h���ꂽ�T�[�r�X��Service�I�u�W�F�N�g�Ƃ��Ď擾���邪�A���̃��\�b�h�́AServiceLoader�Ń��[�h���ꂽ�T�[�r�X�I�u�W�F�N�g���̂��̂��擾����B<br>
     * �ʏ�A�T�[�r�X�̃A�v���P�[�V���������̋@�\���g�p����ꍇ�ɁA���̃��\�b�h�ŃT�[�r�X���擾���āA�K�v�ȃC���^�t�F�[�X�ɃL���X�g���Ďg�p����B<br>
     *
     * @param managerName ServiceManager�̖��O
     * @param serviceName �T�[�r�X��
     * @return �T�[�r�X
     * @exception ServiceNotFoundException �w�肳�ꂽ�T�[�r�X��������Ȃ��ꍇ
     */
    public static Object getServiceObject(
        String managerName,
        String serviceName
    ) throws ServiceNotFoundException{
        final ServiceManager manager = findManager(managerName);
        if(manager != null){
            return manager.getServiceObject(serviceName);
        }
        throw new ServiceNotFoundException(managerName, serviceName);
    }
    
    /**
     * �w�肳�ꂽ�T�[�r�X���̃T�[�r�X�I�u�W�F�N�g���擾����B<p>
     *
     * @param serviceName �T�[�r�X��
     * @return �T�[�r�X�I�u�W�F�N�g
     * @exception ServiceNotFoundException �w�肳�ꂽ�T�[�r�X��������Ȃ��ꍇ
     * @see #getServiceObject(String, String)
     */
    public static Object getServiceObject(ServiceName serviceName)
     throws ServiceNotFoundException{
        if(serviceName == null){
            throw new ServiceNotFoundException(serviceName);
        }
        return getServiceObject(
            serviceName.getServiceManagerName(),
            serviceName.getServiceName()
        );
    }
    
    /**
     * �f�t�H���g�̖��O��ServiceManager����A�w�肳�ꂽ�T�[�r�X���̃T�[�r�X�I�u�W�F�N�g���擾����B<p>
     *
     * @param serviceName �T�[�r�X��
     * @return �T�[�r�X�I�u�W�F�N�g
     * @exception ServiceNotFoundException �w�肳�ꂽ�T�[�r�X��������Ȃ��ꍇ
     * @see #getServiceObject(String, String)
     */
    public static Object getServiceObject(
        String serviceName
    ) throws ServiceNotFoundException{
        final ServiceManager manager = findManager();
        if(manager != null){
            return manager.getServiceObject(serviceName);
        }
        throw new ServiceNotFoundException(
            ServiceManager.DEFAULT_NAME,
            serviceName
        );
    }
    
    /**
     * �w�肳�ꂽ���O��ServiceManager����A�w�肳�ꂽ�T�[�r�X���̃T�[�r�X�̏�ԕύX��ʒm����ServiceStateBroadcaster���擾����B<p>
     * ServiceLoader�Ń��[�h���ꂽ�T�[�r�X�́AServiceStateBroadcaster���������Ă��Ă��ǂ��B�w�肳�ꂽ�T�[�r�X��ServiceStateBroadcaster���������Ă���ꍇ�́A�����Ԃ��B�������Ă��Ȃ��ꍇ�́Anull��Ԃ��B<br>
     *
     * @param managerName ServiceManager�̖��O
     * @param serviceName �T�[�r�X��
     * @return ServiceStateBroadcaster�I�u�W�F�N�g
     * @exception ServiceNotFoundException �w�肳�ꂽ�T�[�r�X��������Ȃ��ꍇ
     */
    public static ServiceStateBroadcaster getServiceStateBroadcaster(
        String managerName,
        String serviceName
    ) throws ServiceNotFoundException{
        final ServiceManager manager = findManager(managerName);
        if(manager != null){
            return manager.getServiceStateBroadcaster(serviceName);
        }
        throw new ServiceNotFoundException(managerName, serviceName);
    }
    
    /**
     * �w�肳�ꂽ�T�[�r�X���̃T�[�r�X�̏�ԕύX��ʒm����ServiceStateBroadcaster���擾����B<p>
     *
     * @param serviceName �T�[�r�X��
     * @return ServiceStateBroadcaster�I�u�W�F�N�g
     * @exception ServiceNotFoundException �w�肳�ꂽ�T�[�r�X��������Ȃ��ꍇ
     * @see #getServiceStateBroadcaster(String, String)
     */
    public static ServiceStateBroadcaster getServiceStateBroadcaster(
        ServiceName serviceName
    ) throws ServiceNotFoundException{
        if(serviceName == null){
            throw new ServiceNotFoundException(null, null);
        }
        return getServiceStateBroadcaster(
            serviceName.getServiceManagerName(),
            serviceName.getServiceName()
        );
    }
    
    /**
     * �f�t�H���g�̖��O��ServiceManager����A�w�肳�ꂽ�T�[�r�X���̃T�[�r�X�̏�ԕύX��ʒm����ServiceStateBroadcaster���擾����B<p>
     *
     * @param serviceName �T�[�r�X��
     * @return ServiceStateBroadcaster�I�u�W�F�N�g
     * @exception ServiceNotFoundException �w�肳�ꂽ�T�[�r�X��������Ȃ��ꍇ
     * @see #getServiceStateBroadcaster(String, String)
     */
    public static ServiceStateBroadcaster getServiceStateBroadcaster(
        String serviceName
    ) throws ServiceNotFoundException{
        final ServiceManager manager = findManager();
        if(manager != null){
            return manager.getServiceStateBroadcaster(serviceName);
        }
        throw new ServiceNotFoundException(
            ServiceManager.DEFAULT_NAME,
            serviceName
        );
    }
    
    /**
     * �w�肳�ꂽ�T�[�r�X��`���^�f�[�^�ɏ]�����T�[�r�X���A�w�肳�ꂽ���O��ServiceManager�ɁA�w�肳�ꂽ�T�[�r�X���ŃT�[�r�X�Ƃ��ēo�^����B<p>
     *
     * @param managerName ServiceManager�̖��O
     * @param serviceData �T�[�r�X��`���^�f�[�^
     * @return �o�^�ł����ꍇ�́Atrue�B�w�肳�ꂽ���O��ServiceManager�����݂��Ȃ��ꍇ��AServiceManager���o�^�Ɏ��s�����ꍇ�́Afalse��Ԃ��B
     * @exception Exception �T�[�r�X�̃C���X�^���X���Ɏ��s�����ꍇ
     */
    public static boolean registerService(
        String managerName,
        ServiceMetaData serviceData
    ) throws Exception{
        final ServiceManager manager = findManager(managerName);
        if(manager != null){
            return manager.registerService(serviceData);
        }
        return false;
    }
    
    /**
     * �w�肳�ꂽ�I�u�W�F�N�g���A�w�肳�ꂽ���O��ServiceManager�ɁA�w�肳�ꂽ�T�[�r�X���ŃT�[�r�X�Ƃ��ēo�^����B<p>
     * �w�肳�ꂽ�I�u�W�F�N�g��Service�C���^�t�F�[�X�������Ă��Ȃ��ꍇ�́AService�C���^�t�F�[�X�������������b�p�[�ł���܂�ēo�^�����B<br>
     *
     * @param managerName ServiceManager�̖��O
     * @param serviceName �T�[�r�X��
     * @param obj �T�[�r�X�I�u�W�F�N�g
     * @return �o�^�ł����ꍇ�́Atrue�B�w�肳�ꂽ���O��ServiceManager�����݂��Ȃ��ꍇ��AServiceManager���o�^�Ɏ��s�����ꍇ�́Afalse��Ԃ��B
     * @exception Exception �T�[�r�X�̓o�^�Ɏ��s�����ꍇ
     */
    public static boolean registerService(
        String managerName,
        String serviceName,
        Object obj
    ) throws Exception{
        final ServiceManager manager = findManager(managerName);
        if(manager != null){
            return manager.registerService(serviceName, obj);
        }
        return false;
    }
    
    /**
     * �w�肳�ꂽ�T�[�r�X���A�w�肳�ꂽ���O��ServiceManager�ɁA�w�肳�ꂽ�T�[�r�X���œo�^����B<p>
     *
     * @param managerName ServiceManager�̖��O
     * @param serviceName �T�[�r�X��
     * @param service �T�[�r�X
     * @return �o�^�ł����ꍇ�́Atrue�B�w�肳�ꂽ���O��ServiceManager�����݂��Ȃ��ꍇ��AServiceManager���o�^�Ɏ��s�����ꍇ�́Afalse��Ԃ��B
     * @exception Exception �T�[�r�X�̓o�^�Ɏ��s�����ꍇ
     */
    public static boolean registerService(
        String managerName,
        String serviceName,
        Service service
    ) throws Exception{
        final ServiceManager manager = findManager(managerName);
        if(manager != null){
            return manager.registerService(serviceName, service);
        }
        return false;
    }
    
    
    /**
     * �w�肳�ꂽ���O��ServiceManager����A�w�肳�ꂽ�T�[�r�X���̃T�[�r�X�̓o�^����������B<p>
     *
     * @param managerName ServiceManager�̖��O
     * @param serviceName �T�[�r�X��
     * @return �o�^�����ł����ꍇ�́Atrue�B�܂��A�w�肳�ꂽ���O��ServiceManager�����݂��Ȃ��ꍇ��true�BServiceManager���o�^�̉����Ɏ��s�����ꍇ�́Afalse��Ԃ��B
     */
    public static boolean unregisterService(
        String managerName,
        String serviceName
    ){
        final ServiceManager manager = findManager(managerName);
        if(manager != null){
            return manager.unregisterService(serviceName);
        }
        return true;
    }
    
    /**
     * �w�肳�ꂽ���O��ServiceManager�ɁA�w�肳�ꂽ�T�[�r�X���̃T�[�r�X���o�^����Ă��邩���ׂ�B<p>
     *
     * @param managerName �}�l�[�W����
     * @param serviceName �T�[�r�X��
     * @return �o�^����Ă����ꍇtrue
     */
    public static boolean isRegisteredService(
        String managerName,
        String serviceName
    ){
        final ServiceManager manager = findManager(managerName);
        if(manager != null){
            return manager.isRegisteredService(serviceName);
        }
        return false;
    }
    
    /**
     * �w�肳�ꂽ�T�[�r�X���̃T�[�r�X���o�^����Ă��邩���ׂ�B<p>
     *
     * @param serviceName �T�[�r�X��
     * @return �o�^����Ă����ꍇtrue
     */
    public static boolean isRegisteredService(ServiceName serviceName){
        if(serviceName == null){
            return false;
        }
        final ServiceManager manager = findManager(
            serviceName.getServiceManagerName()
        );
        if(manager != null){
            return manager.isRegisteredService(serviceName.getServiceName());
        }
        return false;
    }
    
    /**
     * ServiceManager�̊Ǘ��Ɏg�p����Repository�T�[�r�X��ݒ肷��B<p>
     * ���݂�Repository�ɓo�^����Ă���ServiceManager���A�S�ĐV����Repository�ɓo�^�ł����ꍇ�̂݁ARepository��ύX����B��ł��A�o�^�Ɏ��s����ServiceManager�����݂���ꍇ�ɂ́A���̏�Ԃɖ߂��B<br>
     *
     * @param name ServiceManager�̊Ǘ��Ɏg�p����Repository�T�[�r�X�̃T�[�r�X��
     * @return Repository�̓���ւ��ɐ��������ꍇtrue
     */
    public static boolean setManagerRepository(ServiceName name){
        return setManagerRepository(
            name.getServiceManagerName(),
            name.getServiceName()
        );
    }
    
    /**
     * ServiceManager�̊Ǘ��Ɏg�p����Repository�T�[�r�X��ݒ肷��B<p>
     * ���݂�Repository�ɓo�^����Ă���ServiceManager���A�S�ĐV����Repository�ɓo�^�ł����ꍇ�̂݁ARepository��ύX����B��ł��A�o�^�Ɏ��s����ServiceManager�����݂���ꍇ�ɂ́A���̏�Ԃɖ߂��B<br>
     *
     * @param manager ServiceManager�̊Ǘ��Ɏg�p����Repository�T�[�r�X���o�^����Ă���}�l�[�W����
     * @param service ServiceManager�̊Ǘ��Ɏg�p����Repository�T�[�r�X�̃T�[�r�X��
     * @return Repository�̓���ւ��ɐ��������ꍇtrue
     */
    public static boolean setManagerRepository(
        final String manager,
        final String service
    ){
        if(isRegisteredService(manager, service)
             && getService(manager, service).getState() == Service.STARTED
        ){
            return setManagerRepository(
                (Repository)getServiceObject(manager, service)
            );
        }else{
            addServiceStateListener(
                manager,
                service,
                new ServiceStateListener(){
                    public void stateChanged(ServiceStateChangeEvent e)
                     throws Exception{
                        ServiceManagerFactory.removeServiceStateListener(
                            manager,
                            service,
                            this
                        );
                        ServiceManagerFactory
                            .setManagerRepository(manager, service);
                    }
                    public boolean isEnabledState(int st){
                        return st == Service.STARTED;
                    }
                }
            );
            return false;
        }
    }
    
    /**
     * ServiceManager�̊Ǘ��Ɏg�p����Repository��ݒ肷��B<p>
     * ���݂�Repository�ɓo�^����Ă���ServiceManager���A�S�ĐV����Repository�ɓo�^�ł����ꍇ�̂݁ARepository��ύX����B��ł��A�o�^�Ɏ��s����ServiceManager�����݂���ꍇ�ɂ́A���̏�Ԃɖ߂��B<br>
     *
     * @param newRep ServiceManager�̊Ǘ��Ɏg�p����Repository�I�u�W�F�N�g
     * @return Repository�̓���ւ��ɐ��������ꍇtrue
     */
    public static boolean setManagerRepository(Repository newRep){
        logger.write(SVCMF00021, newRep);
        synchronized(repository){
            if(newRep == null){
                newRep = new DefaultRepository();
            }
            if(repository.equals(newRep)){
                return true;
            }
            boolean success = true;
            final Set registered = new HashSet();
            Iterator names = repository.nameSet().iterator();
            while(names.hasNext()){
                final String name = (String)names.next();
                final Object manager = repository.get(name);
                if(manager != null){
                    if(!newRep.register(name, manager)){
                        logger.write(SVCMF00022, name);
                        success = false;
                    }else{
                        registered.add(name);
                    }
                }
            }
            if(!success){
                logger.write(SVCMF00023, newRep);
                names = registered.iterator();
                while(names.hasNext()){
                    final String name = (String)names.next();
                    newRep.unregister(name);
                }
                return false;
            }
            logger.write(SVCMF00024, newRep);
            names = newRep.nameSet().iterator();
            while(names.hasNext()){
                final String name = (String)names.next();
                repository.unregister(name);
            }
            repository = newRep;
        }
        return true;
    }
    
    /**
     * �T�[�r�X��`�����[�h����ServiceLoader��o�^����B<p>
     * 
     * @param loader �o�^����ServiceLoader�I�u�W�F�N�g
     */
    public static void registerLoader(ServiceLoader loader){
        final URL url = loader.getServiceURL();
        if(loaders.size() == 0){
            if(logger != null){
                logger.start();
            }
        }
        if(!loaders.containsKey(url)){
            loaders.put(url, loader);
        }
    }
    
    /**
     * �T�[�r�X��`�����[�h����ServiceLoader�̓o�^����������B<p>
     * 
     * @param loader �o�^����������ServiceLoader�I�u�W�F�N�g
     */
    public static void unregisterLoader(ServiceLoader loader){
        loaders.remove(loader.getServiceURL());
        if(loaders.size() == 0){
            if(logger != null){
                logger.stop();
            }
        }
    }
    
    /**
     * �w�肳�ꂽURL�̃T�[�r�X��`�����[�h����ServiceLaoder���擾����B<p>
     *
     * @param url �T�[�r�X��`��URL
     * @return �w�肳�ꂽURL�̃T�[�r�X��`�����[�h����ServiceLaoder�B���݂��Ȃ��ꍇ�́Anull��Ԃ��B
     */
    public static ServiceLoader getLoader(URL url){
        return (ServiceLoader)loaders.get(url);
    }
    
    /**
     * �T�[�r�X��`�����[�h�����S�Ă�ServiceLaoder���擾����B<p>
     *
     * @return �T�[�r�X��`�����[�h����ServiceLaoder�̏W���B���݂��Ȃ��ꍇ�́A��̏W����Ԃ��B
     */
    public static Collection getLoaders(){
        return new HashSet(loaders.values());
    }
    
    /**
     * �T�[�r�X��`�t�@�C�������[�h����ServiceLoader�N���X��ݒ肷��B<p>
     * �A���A�V�X�e���v���p�e�B"jp.ossc.nimbus.core.loader"�Ŏw�肵��ServiceLoader�N���X�̕����D�悳���B<br>
     *
     * @param loader �T�[�r�X��`�t�@�C�������[�h����ServiceLoader�N���X
     */
    public static void setServiceLoaderClass(Class loader)
     throws IllegalArgumentException{
        if(loader == null){
            loaderClass = DEFAULT_SERVICE_LOADER_CLASS;
        }else if(ServiceLoader.class.isAssignableFrom(loader)){
            loaderClass = loader;
        }else{
            throw new IllegalArgumentException(
                message.findEmbedMessage(SVCMF00003, loader)
            );
        }
        logger.write(SVCMF00025, loaderClass);
    }
    
    /**
     * �T�[�r�X��`�t�@�C�������[�h����ServiceLoader�N���X���擾����B<p>
     *
     * @return �T�[�r�X��`�t�@�C�������[�h����ServiceLoader�N���X
     */
    public static Class getServiceLoaderClass(){
        return loaderClass;
    }
    
    /**
     * ���̃t�@�N�g���Ő�������ServiceManager�N���X��ݒ肷��B<p>
     * �A���A�V�X�e���v���p�e�B"jp.ossc.nimbus.core.manager"�Ŏw�肵��ServiceManager�N���X�̕����D�悳���B<br>
     *
     * @param manager ���̃t�@�N�g���Ő�������ServiceManager�N���X
     */
    public static void setServiceManagerClass(Class manager)
     throws IllegalArgumentException{
        if(manager == null){
            managerClass = null;
        }else if(ServiceManager.class.isAssignableFrom(manager)){
            managerClass = manager;
        }else{
            throw new IllegalArgumentException(
                message.findEmbedMessage(SVCMF00027, manager)
            );
        }
        logger.write(SVCMF00026, managerClass);
    }
    
    /**
     * �T�[�r�X��`�t�@�C�������[�h����ServiceManager�N���X���擾����B<p>
     *
     * @return ���̃t�@�N�g���Ő�������ServiceManager�N���X
     */
    public static Class getServiceManagerClass(){
        return managerClass;
    }
    
    /**
     * ���O�o�͂��s��Logger���擾����B<p>
     *
     * @return Logger�I�u�W�F�N�g
     */
    public static Logger getLogger(){
        return logger;
    }
    
    /**
     * ���O�o�͂��s��Logger�T�[�r�X��ݒ肷��B<p>
     *
     * @param name �T�[�r�X�̃T�[�r�X��
     */
    public static void setLogger(ServiceName name){
        setLogger(name.getServiceManagerName(), name.getServiceName());
    }
    
    /**
     * ���O�o�͂��s��Logger�T�[�r�X��ݒ肷��B<p>
     *
     * @param manager Logger�T�[�r�X���o�^����Ă���ServiceManager�̖��O
     * @param service Logger�T�[�r�X�̃T�[�r�X��
     */
    public static void setLogger(final String manager, final String service){
        if(isRegisteredService(manager, service)
             && getService(manager, service).getState() == Service.STARTED
        ){
            final Logger newLogger = (Logger)getServiceObject(manager, service);
            final Service newLoggerService = getService(manager, service);
            logger.write(SVCMF00028, new Object[]{manager, service});
            logger.setLogger(newLogger, newLoggerService);
        }else{
            addServiceStateListener(
                manager,
                service,
                new ServiceStateListener(){
                    public void stateChanged(ServiceStateChangeEvent e)
                     throws Exception{
                        ServiceManagerFactory.removeServiceStateListener(
                            manager,
                            service,
                            this
                        );
                        ServiceManagerFactory.setLogger(manager, service);
                    }
                    public boolean isEnabledState(int st){
                        return st == Service.STARTED;
                    }
                }
            );
        }
    }
    
    /**
     * ���b�Z�[�W�������s��MessageRecordFactory���擾����B<p>
     *
     * @return MessageRecordFactory�I�u�W�F�N�g
     */
    public static MessageRecordFactory getMessageRecordFactory(){
        return message;
    }
    
    /**
     * ���b�Z�[�W�������s��MessageRecordFactory�T�[�r�X��ݒ肷��B<p>
     *
     * @param name MessageRecordFactory�T�[�r�X�̃T�[�r�X��
     */
    public static void setMessageRecordFactory(ServiceName name){
        setMessageRecordFactory(
            name.getServiceManagerName(),
            name.getServiceName()
        );
    }
    
    /**
     * ���b�Z�[�W�������s��MessageRecordFactory�T�[�r�X��ݒ肷��B<p>
     *
     * @param manager MessageRecordFactory�T�[�r�X���o�^����Ă���ServiceManager�̖��O
     * @param service MessageRecordFactory�T�[�r�X�̃T�[�r�X��
     */
    public static void setMessageRecordFactory(
        final String manager,
        final String service
    ){
        if(isRegisteredService(manager, service)
             && getService(manager, service).getState() == Service.STARTED
        ){
            final MessageRecordFactory newMessage
                 = (MessageRecordFactory)getServiceObject(manager, service);
            final Service newMessageService = getService(manager, service);
            logger.write(SVCMF00029, new Object[]{manager, service});
            message.setMessageRecordFactory(newMessage, newMessageService);
        }else{
            addServiceStateListener(
                manager,
                service,
                new ServiceStateListener(){
                    public void stateChanged(ServiceStateChangeEvent e)
                     throws Exception{
                        ServiceManagerFactory.removeServiceStateListener(
                            manager,
                            service,
                            this
                        );
                        ServiceManagerFactory.setMessageRecordFactory(
                            manager,
                            service
                        );
                    }
                    public boolean isEnabledState(int st){
                        return st == Service.STARTED;
                    }
                }
            );
        }
    }
    
    /**
     * �o�^��Ԃ��ύX���ꂽ�����Ď�����RegistrationListener��ǉ�����B<p>
     *
     * @param listener RegistrationListener�I�u�W�F�N�g
     */
    public static void addRegistrationListener(RegistrationListener listener){
        if(!registrationListeners.contains(listener)){
            registrationListeners.add(listener);
        }
    }
    
    /**
     * �o�^��Ԃ��ύX���ꂽ�����Ď�����RegistrationListener���폜����B<p>
     *
     * @param listener RegistrationListener�I�u�W�F�N�g
     */
    public static void removeRegistrationListener(
        RegistrationListener listener
    ){
        registrationListeners.remove(listener);
    }
    
    /**
     * ServiceManager���o�^���ꂽ����RegistrationListener�ɒʒm����B<p>
     * 
     * @param manager �o�^���ꂽServiceManager
     * @see RegistrationListener
     * @see RegistrationEvent
     */
    protected static void processRegisterd(ServiceManager manager){
        final Iterator listeners
             = new ArrayList(registrationListeners).iterator();
        while(listeners.hasNext()){
            final RegistrationListener listener
                 = (RegistrationListener)listeners.next();
            listener.registered(new RegistrationEvent(manager));
        }
    }
    
    /**
     * ServiceManager���폜���ꂽ����RegistrationListener�ɒʒm����B<p>
     * 
     * @param manager �폜���ꂽServiceManager
     * @see RegistrationListener
     * @see RegistrationEvent
     */
    protected static void processUnregisterd(ServiceManager manager){
        final Iterator listeners
             = new ArrayList(registrationListeners).iterator();
        while(listeners.hasNext()){
            final RegistrationListener listener
                 = (RegistrationListener)listeners.next();
            listener.unregistered(new RegistrationEvent(manager));
        }
    }
    
    /**
     * �T�[�r�X�̏�Ԃ��ύX���ꂽ�����Ď�����ServiceStateListener��ǉ�����B<p>
     * �w�肵��{@link ServiceManager}���o�^����Ă��Ȃ��ꍇ�ARegistrationListener��o�^����BServiceManager���o�^�����ƁA{@link ServiceManager#addServiceStateListener(String, ServiceStateListener)}�ŁAServiceStateListener���o�^�����B<br>
     *
     * @param managerName �}�l�[�W����
     * @param serviceName �T�[�r�X��
     * @param listener ServiceStateListener�I�u�W�F�N�g
     */
    public static void addServiceStateListener(
        final String managerName,
        final String serviceName,
        final ServiceStateListener listener
    ){
        if(isRegisteredManager(managerName)){
            final ServiceManager manager = findManager(managerName);
            manager.addServiceStateListener(serviceName, listener);
            return;
        }
        addRegistrationListener(
            new RegistrationListener(){
                public void registered(RegistrationEvent e){
                    final ServiceManager manager
                         = (ServiceManager)e.getRegistration();
                    if(!manager.getServiceName().equals(managerName)){
                        return;
                    }
                    removeRegistrationListener(this);
                    manager.addServiceStateListener(serviceName, listener);
                }
                public void unregistered(RegistrationEvent e){
                }
            }
        );
    }
    
    /**
     * �T�[�r�X�̏�Ԃ��ύX���ꂽ�����Ď�����ServiceStateListener��ǉ�����B<p>
     * �w�肵��{@link ServiceManager}���o�^����Ă��Ȃ��ꍇ�ARegistrationListener��o�^����BServiceManager���o�^�����ƁA{@link ServiceManager#addServiceStateListener(String, ServiceStateListener)}�ŁAServiceStateListener���o�^�����B<br>
     *
     * @param serviceName �T�[�r�X��
     * @param listener ServiceStateListener�I�u�W�F�N�g
     */
    public static void addServiceStateListener(
        ServiceName serviceName,
        ServiceStateListener listener
    ){
        addServiceStateListener(
            serviceName.getServiceManagerName(),
            serviceName.getServiceName(),
            listener
        );
    }
    
    /**
     * �T�[�r�X�̏�Ԃ��ύX���ꂽ�����Ď�����ServiceStateListener��ǉ�����B<p>
     * �f�t�H���g��{@link ServiceManager}���o�^����Ă��Ȃ��ꍇ�ARegistrationListener��o�^����B�f�t�H���g��ServiceManager���o�^�����ƁA{@link ServiceManager#addServiceStateListener(String, ServiceStateListener)}�ŁAServiceStateListener���o�^�����B<br>
     *
     * @param serviceName �T�[�r�X��
     * @param listener ServiceStateListener�I�u�W�F�N�g
     */
    public static void addServiceStateListener(
        final String serviceName,
        final ServiceStateListener listener
    ){
        final ServiceManager manager = findManager(serviceName);
        if(manager != null){
            manager.addServiceStateListener(serviceName, listener);
            return;
        }
        addRegistrationListener(
            new RegistrationListener(){
                public void registered(RegistrationEvent e){
                    final ServiceManager manager
                         = (ServiceManager)e.getRegistration();
                    if(!manager.getServiceName()
                        .equals(ServiceManager.DEFAULT_NAME)){
                        return;
                    }
                    removeRegistrationListener(this);
                    manager.addServiceStateListener(serviceName, listener);
                }
                public void unregistered(RegistrationEvent e){
                }
            }
        );
    }
    
    /**
     * �T�[�r�X�̏�Ԃ��ύX���ꂽ�����Ď�����ServiceStateListener���폜����B<p>
     *
     * @param managerName �}�l�[�W����
     * @param serviceName �T�[�r�X��
     * @param listener ServiceStateListener�I�u�W�F�N�g
     */
    public static void removeServiceStateListener(
        String managerName,
        String serviceName,
        ServiceStateListener listener
    ){
        if(isRegisteredManager(managerName)){
            final ServiceManager manager = findManager(managerName);
            manager.removeServiceStateListener(serviceName, listener);
        }
    }
    
    /**
     * �T�[�r�X�̏�Ԃ��ύX���ꂽ�����Ď�����ServiceStateListener���폜����B<p>
     *
     * @param serviceName �T�[�r�X��
     * @param listener ServiceStateListener�I�u�W�F�N�g
     */
    public static void removeServiceStateListener(
        ServiceName serviceName,
        ServiceStateListener listener
    ){
        removeServiceStateListener(
            serviceName.getServiceManagerName(),
            serviceName.getServiceName(),
            listener
        );
    }
    
    /**
     * �T�[�r�X�̏�Ԃ��ύX���ꂽ�����Ď�����ServiceStateListener���폜����B<p>
     *
     * @param serviceName �T�[�r�X��
     * @param listener ServiceStateListener�I�u�W�F�N�g
     */
    public static void removeServiceStateListener(
        String serviceName,
        ServiceStateListener listener
    ){
        final ServiceManager manager = findManager();
        if(manager != null){
            manager.removeServiceStateListener(serviceName, listener);
        }
    }
    
    /**
     * �T�[�o�v���p�e�B���擾����B<p>
     * 
     * @param name �v���p�e�B��
     * @return �T�[�o�v���p�e�B
     */
    public static String getProperty(String name){
        return properties.getProperty(name);
    }
    
    /**
     * �T�[�o�v���p�e�B��ݒ肷��B<p>
     * 
     * @param name �v���p�e�B��
     * @param value �T�[�o�v���p�e�B
     */
    public static void setProperty(String name, String value){
        properties.setProperty(name, value);
    }
    
    /**
     * �g�p���@��W���o�͂ɕ\������B<p>
     */
    private static void usage(){
        try{
            System.out.println(
                getResourceString(USAGE_RESOURCE)
            );
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    /**
     * ���\�[�X�𕶎���Ƃ��ēǂݍ��ށB<p>
     *
     * @param name ���\�[�X��
     * @exception IOException ���\�[�X�����݂��Ȃ��ꍇ
     */
    private static String getResourceString(String name) throws IOException{
        
        // ���\�[�X�̓��̓X�g���[�����擾
        InputStream is = ServiceManagerFactory.class.getClassLoader()
            .getResourceAsStream(name);
        
        // ���b�Z�[�W�̓ǂݍ���
        StringBuffer buf = new StringBuffer();
        BufferedReader reader = null;
        final String separator = System.getProperty("line.separator");
        try{
            reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while((line = reader.readLine()) != null){
                buf.append(line).append(separator);
            }
        }finally{
            if(reader != null){
                try{
                    reader.close();
                }catch(IOException e){
                }
            }
        }
        return unicodeConvert(buf.toString());
    }
    
    /**
     * ���j�R�[�h�G�X�P�[�v��������܂�ł���\���̂��镶������f�t�H���g�G���R�[�f�B���O�̕�����ɕϊ�����B<p>
     *
     * @param str ���j�R�[�h�G�X�P�[�v��������܂�ł���\���̂��镶����
     * @return �f�t�H���g�G���R�[�f�B���O�̕�����
     */
    private static String unicodeConvert(String str){
        char c;
        int len = str.length();
        StringBuffer buf = new StringBuffer(len);
        
        for(int i = 0; i < len; ){
            c = str.charAt(i++);
            if(c == '\\'){
                c = str.charAt(i++);
                if(c == 'u'){
                    int value = 0;
                    for(int j = 0; j < 4; j++){
                        c = str.charAt(i++);
                        switch(c){
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            value = (value << 4) + (c - '0');
                            break;
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                            value = (value << 4) + 10 + (c - 'a');
                            break;
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                            value = (value << 4) + 10 + (c - 'A');
                            break;
                        default:
                            throw new IllegalArgumentException(
                                "Failed to convert unicode : " + c
                            );
                        }
                    }
                    buf.append((char)value);
                }else{
                    switch(c){
                    case 't':
                        c = '\t';
                        break;
                    case 'r':
                        c = '\r';
                        break;
                    case 'n':
                        c = '\n';
                        break;
                    case 'f':
                        c = '\f';
                        break;
                    default:
                    }
                    buf.append(c);
                }
            }else{
                buf.append(c);
            }
        }
        return buf.toString();
    }
    
    /**
     * �R���p�C���R�}���h�����s����B<p>
     * <pre>
     * �R�}���h�g�p���@�F
     *  java jp.ossc.nimbus.core.ServiceManagerFactory [options] [paths]
     * 
     * [options]
     * 
     *  [-validate]
     *   �T�[�r�X��`��DTD�Ō��؂���B
     * 
     *  [-server]
     *   ���C���X���b�h��ҋ@�����āA�T�[�o�Ƃ��ē������B
     * 
     *  [-help]
     *   �w���v��\�����܂��B
     * 
     * [paths]
     *  ���[�h����T�[�r�X��`�t�@�C���̃p�X
     * 
     * �g�p�� : 
     *    java -classpath classes;lib/nimbus.jar jp.ossc.nimbus.core.ServiceManagerFactory service-definition.xml
     * </pre>
     *
     * @param args �R�}���h����
     * @exception Exception �R���p�C�����ɖ�肪���������ꍇ
     */
    public static void main(String[] args) throws Exception{
        
        if(args.length != 0 && args[0].equals("-help")){
            usage();
            return;
        }
        
        final List servicePaths = new ArrayList();
        boolean validate = false;
        boolean server = false;
        for(int i = 0; i < args.length; i++){
            if(args[i].equals("-server")){
                server = true;
            }else if(args[i].equals("-validate")){
                validate = true;
            }else{
                servicePaths.add(args[i]);
            }
        }
        
        if(servicePaths.size() == 0){
            usage();
            return;
        }
        
        Runtime.getRuntime().addShutdownHook(
            new Thread(
                new Runnable(){
                    public void run(){
                        for(int i = servicePaths.size(); --i >= 0;){
                            ServiceManagerFactory.unloadManager((String)servicePaths.get(i));
                        }
                    }
                }
            )
        );
        
        for(int i = 0, max = servicePaths.size(); i < max; i++){
            if(!ServiceManagerFactory.loadManager((String)servicePaths.get(i), false, validate)){
                Thread.sleep(1000);
                System.exit(-1);
            }
        }
        if(!ServiceManagerFactory.checkLoadManagerCompleted()){
            Thread.sleep(1000);
            System.exit(-1);
        }
        if(server){
            WaitSynchronizeMonitor lock = new WaitSynchronizeMonitor();
            synchronized(lock){
               lock.initMonitor();
                try{
                   lock.waitMonitor();
                }catch(InterruptedException ignore){}
            }
        }
    }
}