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
import java.lang.reflect.*;
import java.beans.*;
import java.io.*;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.service.repository.*;
import jp.ossc.nimbus.service.log.Logger;
import jp.ossc.nimbus.service.message.MessageRecordFactory;

/**
 * �f�t�H���g�T�[�r�X�}�l�[�W���T�[�r�X�B<p>
 * �T�[�r�X���Ǘ�����{@link ServiceManager}�̃f�t�H���g�����ł��B<br>
 *
 * @author M.Takata
 */
public class DefaultServiceManagerService extends ServiceBase
 implements ServiceManager, RegistrationBroadcaster,
            DefaultServiceManagerServiceMBean{
    
    private static final long serialVersionUID = 4663655905756505766L;
    
    // ���b�Z�[�WID��`
    private static final String SVCM_ = "SVCM_";
    private static final String SVCM_0 = SVCM_ + 0;
    private static final String SVCM_00 = SVCM_0 + 0;
    private static final String SVCM_000 = SVCM_00 + 0;
    private static final String SVCM_0000 = SVCM_000 + 0;
    private static final String SVCM_00001 = SVCM_0000 + 1;
    private static final String SVCM_00002 = SVCM_0000 + 2;
    private static final String SVCM_00003 = SVCM_0000 + 3;
    private static final String SVCM_00004 = SVCM_0000 + 4;
    private static final String SVCM_00005 = SVCM_0000 + 5;
    private static final String SVCM_00006 = SVCM_0000 + 6;
    private static final String SVCM_00007 = SVCM_0000 + 7;
    private static final String SVCM_00008 = SVCM_0000 + 8;
    private static final String SVCM_00009 = SVCM_0000 + 9;
    private static final String SVCM_00010 = SVCM_000 + 10;
    private static final String SVCM_00011 = SVCM_000 + 11;
    private static final String SVCM_00012 = SVCM_000 + 12;
    private static final String SVCM_00013 = SVCM_000 + 13;
    private static final String SVCM_00014 = SVCM_000 + 14;
    private static final String SVCM_00015 = SVCM_000 + 15;
    private static final String SVCM_00016 = SVCM_000 + 16;
    private static final String SVCM_00017 = SVCM_000 + 17;
    private static final String SVCM_00018 = SVCM_000 + 18;
    private static final String SVCM_00019 = SVCM_000 + 19;
    private static final String SVCM_00020 = SVCM_000 + 20;
    private static final String SVCM_00021 = SVCM_000 + 21;
    private static final String SVCM_00022 = SVCM_000 + 22;
    private static final String SVCM_00023 = SVCM_000 + 23;
    private static final String SVCM_00024 = SVCM_000 + 24;
    private static final String SVCM_00025 = SVCM_000 + 25;
    private static final String SVCM_00026 = SVCM_000 + 26;
    private static final String SVCM_00027 = SVCM_000 + 27;
    private static final String SVCM_00028 = SVCM_000 + 28;
    private static final String SVCM_00029 = SVCM_000 + 29;
    private static final String SVCM_00030 = SVCM_000 + 30;
    private static final String SVCM_00031 = SVCM_000 + 31;
    private static final String SVCM_00032 = SVCM_000 + 32;
    private static final String SVCM_00033 = SVCM_000 + 33;
    private static final String SVCM_00034 = SVCM_000 + 34;
    private static final String SVCM_00035 = SVCM_000 + 35;
    private static final String SVCM_00036 = SVCM_000 + 36;
    private static final String SVCM_00037 = SVCM_000 + 37;
    private static final String SVCM_00038 = SVCM_000 + 38;
    private static final String SVCM_00039 = SVCM_000 + 39;
    private static final String SVCM_00040 = SVCM_000 + 40;
    private static final String SVCM_00041 = SVCM_000 + 41;
    private static final String SVCM_00042 = SVCM_000 + 42;
    private static final String SVCM_00043 = SVCM_000 + 43;
    private static final String SVCM_00044 = SVCM_000 + 44;
    private static final String SVCM_00045 = SVCM_000 + 45;
    private static final String SVCM_00046 = SVCM_000 + 46;
    private static final String SVCM_00047 = SVCM_000 + 47;
    
    /**
     * ���̃T�[�r�X�}�l�[�W���ɓo�^����Ă���Service�����[�h����{@link ServiceLoader}�̏W���B<p>
     */
    private final Set myLoaders = Collections.synchronizedSet(new HashSet());
    
    /**
     * ���̃T�[�r�X�}�l�[�W����{@link ManagerMetaData}�̏W���B<p>
     */
    private final Set managerDatas = new HashSet();
    
    /**
     * �}�l�[�W���v���p�e�B�B<p>
     */
    private final Properties managerProperties = new Properties();
    
    /**
     * �f�v���C�ҋ@���̃T�[�r�X�̃}�b�v�B<p>
     * <table border="1">
     *   <tr bgcolor="#CCCCFF"><th colspan="2">�L�[</th><th colspan="3">�l</th></tr>
     *   <tr bgcolor="#CCCCFF"><th>�^</th><th>���e</th><th>�^</th><th colspan="2">���e</th></tr>
     *   <tr rowspan="3"><td rowspan="3">String</td><td rowspan="3">�f�v���C�ҋ@���̃T�[�r�X��</td><td rowspan="3">java.util.Set</td><td colspan="2">�f�v���C�ҋ@���̃T�[�r�X���A�҂��Ă���T�[�r�X�̏W��</td></tr>
     *   <tr bgcolor="#CCCCFF"><th>�^</th><th>���e</th></tr>
     *   <tr><td>ServiceName</td><td>�T�[�r�X��</td></tr>
     * </table>
     */
    private final Map waitingServices = new Hashtable();
    
    /**
     * �f�v���C�Ɏ��s�����T�[�r�X�̃}�b�v�B<p>
     * <table border="1">
     *   <tr bgcolor="#CCCCFF"><th colspan="2">�L�[</th><th colspan="2">�l</th></tr>
     *   <tr bgcolor="#CCCCFF"><th>�^</th><th>���e</th><th>�^</th><th>���e</th></tr>
     *   <tr><td>String</td><td>�f�v���C�Ɏ��s�����T�[�r�X��</td><td>Throwable</td><td colspan="2">�f�v���C�Ɏ��s��������</td></tr>
     * </table>
     */
    private final Map failedServices = new Hashtable();
    
    /**
     * �����ŊǗ�����Ă���T�[�r�X��o�^����{@link Repository}
     */
    private Repository repository = new DefaultRepository();
    
    /**
     * �T�[�r�X�̓o�^��{@link Repository}�̃f�t�H���g�����N���X�B<p>
     *
     * @author M.Takata
     */
    private class DefaultRepository implements Repository, Serializable{
        
        private static final long serialVersionUID = 1719730608499127551L;
        
        private final Map serviceMap = Collections.synchronizedMap(new HashMap());
        
        public Object get(String name){
            return serviceMap.get(name);
        }
        
        public boolean register(String name, Object service){
            if(serviceMap.containsKey(name)){
                return false;
            }
            synchronized(serviceMap){
                serviceMap.put(name, service);
            }
            return true;
        }
        
        public boolean unregister(String name){
            synchronized(serviceMap){
                serviceMap.remove(name);
            }
            return true;
        }
        
        public boolean isRegistered(String name){
            return serviceMap.containsKey(name);
        }
        
        public Set nameSet(){
            synchronized(serviceMap){
                return new HashSet(serviceMap.keySet());
            }
        }
        
        public Set registeredSet(){
            synchronized(serviceMap){
                return new HashSet(serviceMap.values());
            }
        }
    };
    
    /**
     * ����ServiceManager�ɓo�^���ꂽ�o�^��ԃ��X�i�̃��X�g�B<p>
     */
    private List registrationListeners = new ArrayList();
    
    private Map attributePropCache = new HashMap();
    
    /**
     * �C���X�^���X�𐶐�����B<p>
     */
    public DefaultServiceManagerService(){
        super();
    }
    
    // ServiceManager��JavaDoc
    public Service getService(String name) throws ServiceNotFoundException{
        final Object obj = repository.get(name);
        if(obj == null){
            throw new ServiceNotFoundException(getServiceName(), name);
        }
        Service service = null;
        if(obj instanceof Service){
            service = (Service)obj;
        }else{
            service = convertObjectToService(name, obj);
        }
        return service;
    }
    
    // ServiceManager��JavaDoc
    public Object getServiceObject(String name) throws ServiceNotFoundException{
        Object target = convertServiceToObject(getService(name));
        if(target != null && target instanceof FactoryService){
            target = ((FactoryService)target).newInstance();
            if(target instanceof Service){
                target = convertServiceToObject((Service)target);
            }
        }
        return target;
    }
    
    // ServiceManager��JavaDoc
    public ServiceStateBroadcaster getServiceStateBroadcaster(String name)
     throws ServiceNotFoundException{
        final Object obj = repository.get(name);
        if(obj == null){
            throw new ServiceNotFoundException(getServiceName(), name);
        }
        ServiceStateBroadcaster broadcaster = null;
        if(obj instanceof ServiceStateBroadcaster){
            broadcaster = (ServiceStateBroadcaster)obj;
        }
        return broadcaster;
    }

    
    // ServiceManager��JavaDoc
    public ServiceMetaData getServiceMetaData(String name)
     throws ServiceNotFoundException{
        final Iterator loaders = getServiceLoaders().iterator();
        while(loaders.hasNext()){
            final ServiceLoader loader = (ServiceLoader)loaders.next();
            final ServiceMetaData serviceData
                 = loader.getServiceMetaData(getServiceName(), name);
            if(serviceData != null){
                return serviceData;
            }
        }
        throw new ServiceNotFoundException(getServiceName(), name);
    }
    
    // ServiceManager��JavaDoc
    public boolean registerService(String name, Object obj) throws Exception{
        return registerService(name, convertObjectToService(name, obj));
    }
    
    // ServiceManager��JavaDoc
    public boolean registerService(String name, Service service) throws Exception{
        final Logger logger = getLogger();
        
        logger.write(SVCM_00001, new Object[]{name, service});
        
        final boolean result = repository.register(name, service); 
        if(result){
            service.setServiceManagerName(getServiceName());
            service.setServiceName(name);
            processRegisterd(service);
            logger.write(SVCM_00002, new Object[]{getServiceName(), name});
            if(getState() == STARTED){
                createService(name);
            }
        }else{
            logger.write(SVCM_00003, new Object[]{getServiceName(), name});
        }
        return result;
    }
    
    // ServiceManager��JavaDoc
    public boolean registerService(ServiceMetaData serviceData)
     throws Exception{
        ServiceLoader loader = serviceData.getServiceLoader();
        if(loader == null){
            Iterator loaders = getServiceLoaders().iterator();
            if(loaders.hasNext()){
                loader = (ServiceLoader)loaders.next();
                loader.setServiceMetaData(
                    getServiceName(),
                    serviceData
                );
            }
        }
        if(loader != null){
            serviceData.setManagerName(getServiceName());
            Service service = instanciateService(serviceData);
            return registerService(serviceData.getName(), service);
        }
        return false;
    }
    
    // ServiceManager��JavaDoc
    public boolean unregisterService(String name){
        final Logger logger = getLogger();
        
        logger.write(SVCM_00004, name);
        
        Service service = null;
        try{
            service = getService(name);
        }catch(ServiceNotFoundException e){
            return true;
        }
        final boolean result = repository.unregister(name); 
        if(result){
            processUnregisterd(service);
            logger.write(SVCM_00005, new Object[]{getServiceName(), name});
        }else{
            logger.write(SVCM_00006, new Object[]{getServiceName(), name});
        }
        return result;
    }
    
    // ServiceManager��JavaDoc
    public boolean isRegisteredService(String name){
        return repository.isRegistered(name); 
    }
    
    // ServiceManager��JavaDoc
    public Set serviceNameSet(){
        return repository.nameSet();
    }
    
    // ServiceManager��JavaDoc
    public Set serviceSet(){
        return repository.registeredSet();
    }
    
    // ServiceManager��JavaDoc
    public Set serviceObjectSet(){
        final Iterator serviceNames = serviceNameSet().iterator();
        final Set result = new HashSet();
        while(serviceNames.hasNext()){
            Object obj = null;
            try{
                obj = getServiceObject((String)serviceNames.next());
            }catch(ServiceNotFoundException e){
                continue;
            }
            result.add(obj);
        }
        return result;
    }
    
    // ServiceManager��JavaDoc
    public boolean setServiceRepository(
        final String manager,
        final String service
    ){
        if(ServiceManagerFactory.isRegisteredService(manager, service)
             && ServiceManagerFactory.getService(manager, service).getState()
                 == Service.STARTED
        ){
            return setServiceRepository(
                ServiceManagerFactory.getService(manager, service)
            );
        }else{
            ServiceManagerFactory.addServiceStateListener(
                manager,
                service,
                new SetServiceRepositoryServiceStateListener(
                    getServiceName(),
                    manager,
                    service,
                    null
                )
            );
            return false;
        }
    }
    
    /**
     * �T�[�r�X��o�^����{@link Repository}�T�[�r�X��ݒ肷��B<p>
     *
     * @param repositoryService Repository�T�[�r�X
     * @return Repository�T�[�r�X�̐ݒ�ɐ��������ꍇtrue
     */
    private boolean setServiceRepository(Service repositoryService){
        Repository newRep = null;
        final String managerName = repositoryService.getServiceManagerName();
        final String serviceName = repositoryService.getServiceName();
        try{
            newRep = (Repository)ServiceManagerFactory.getServiceObject(
                managerName,
                serviceName
            );
        }catch(ServiceNotFoundException e){
            return false;
        }
        final boolean isReplaced = setServiceRepository(newRep);
        if(isReplaced){
            ServiceManagerFactory.addServiceStateListener(
                managerName,
                serviceName,
                new SetServiceRepositoryServiceStateListener(
                    getServiceName(),
                    managerName,
                    serviceName,
                    new DefaultRepository()
                )
            );
        }
        return isReplaced;
    }
    
    private static class SetServiceRepositoryServiceStateListener
     implements ServiceStateListener, Serializable{
        private static final long serialVersionUID = 2107809742131214956L;
        private String targetManagerName;
        private String manager;
        private String service;
        private Repository defaultRep;
        public SetServiceRepositoryServiceStateListener(
            String targetManagerName,
            String manager,
            String service,
            Repository defaultRep
        ){
            this.targetManagerName = targetManagerName;
            this.manager = manager;
            this.service = service;
            this.defaultRep = defaultRep;
        }
        public void stateChanged(ServiceStateChangeEvent e)
         throws Exception{
            ServiceManager mng = ServiceManagerFactory.findManager(targetManagerName);
            if(mng == null){
                switch(e.getService().getState()){
                case Service.STARTED:
                    if(defaultRep == null){
                        ServiceManagerFactory
                            .removeServiceStateListener(manager, service, SetServiceRepositoryServiceStateListener.this);
                    }
                    ServiceManagerFactory.addServiceStateListener(
                        targetManagerName,
                        targetManagerName,
                        this
                    );
                    break;
                case Service.STOPPED:
                default:
                }
            }else{
                switch(e.getService().getState()){
                    case Service.STARTED:
                        if(defaultRep == null){
                            ServiceManagerFactory
                                .removeServiceStateListener(manager, service, SetServiceRepositoryServiceStateListener.this);
                        }
                        mng.setServiceRepository(manager, service);
                        break;
                    case Service.STOPPED:
                        if(defaultRep != null){
                            mng.setServiceRepository(defaultRep);
                        }
                        break;
                    default:
                }
            }
        }
        public boolean isEnabledState(int st){
            if(defaultRep == null){
                return st == Service.STARTED;
            }else{
                return st == Service.STARTED || st == Service.STOPPED;
            }
        }
        public int hashCode(){
            return (targetManagerName == null ? 0 : targetManagerName.hashCode())
                + (manager == null ? 0 : manager.hashCode())
                + (service == null ? 0 : service.hashCode())
                + (defaultRep == null ? 0 : 1);
        }
        public boolean equals(Object obj){
            if(obj == null
                || !(obj instanceof SetServiceRepositoryServiceStateListener)){
                return false;
            }
            SetServiceRepositoryServiceStateListener comp
                = (SetServiceRepositoryServiceStateListener)obj;
            if((targetManagerName != null || comp.targetManagerName != null)
                && (targetManagerName == null || comp.targetManagerName == null
                    || !targetManagerName.equals(comp.targetManagerName))){
                return false;
            }
            if((manager != null || comp.manager != null)
                && (manager == null || comp.manager == null
                    || !manager.equals(comp.manager))){
                return false;
            }
            if((service != null || comp.service != null)
                && (service == null || comp.service == null
                    || !service.equals(comp.service))){
                return false;
            }
            if(!(defaultRep == null && comp.defaultRep == null)
                || !(defaultRep != null && comp.defaultRep != null)){
                return false;
            }
            return true;
        }
    }
    
    // ServiceManager��JavaDoc
    public boolean setServiceRepository(Repository newRep){
        final Logger logger = getLogger();
        synchronized(repository){
            if(newRep == null || repository.equals(newRep)){
                return false;
            }
            boolean success = true;
            final Set registered = new HashSet();
            Iterator names = repository.nameSet().iterator();
            while(names.hasNext()){
                final String name = (String)names.next();
                final Object service = repository.get(name);
                if(service != null){
                    if(!newRep.register(name, service)){
                        success = false;
                    }else{
                        registered.add(name);
                    }
                }
            }
            if(!success){
                names = registered.iterator();
                while(names.hasNext()){
                    final String name = (String)names.next();
                    newRep.unregister(name);
                }
                logger.write(SVCM_00007, newRep);
                return false;
            }
            names = newRep.nameSet().iterator();
            while(names.hasNext()){
                final String name = (String)names.next();
                repository.unregister(name);
            }
            repository = newRep;
            logger.write(SVCM_00008, newRep);
        }
        return true;
    }
    
    // ServiceManager��JavaDoc
    public Repository getServiceRepository(){
        return repository;
    }
    
    /**
     * {@link Service}�C���^�t�F�[�X����������I�u�W�F�N�g���e�T�[�r�X��񋟂���T�[�r�X�I�u�W�F�N�g�ɕϊ�����B<p>
     *
     * @param service {@link Service}�C���^�t�F�[�X����������I�u�W�F�N�g
     * @return �e�T�[�r�X��񋟂���T�[�r�X�I�u�W�F�N�g
     */
    private Object convertServiceToObject(Service service){
        Object target = service;
        while(target instanceof ServiceProxy){
            Object child = ((ServiceProxy)target).getTarget();
            if(child == target){
                break;
            }
            target = child;
        }
        return target;
    }
    
    // ServiceManager��JavaDoc
    public Service convertObjectToService(String name, Object obj){
        if(obj == null){
            return null;
        }
        Service service = null;
        if(obj instanceof Service){
            service = (Service)obj;
        }else if(obj instanceof ServiceBaseSupport){
            final ServiceBaseSupport support = (ServiceBaseSupport)obj;
            try{
                service = ServiceProxyFactory.createServiceBaseProxy(support);
            }catch(Exception e){
                return null;
            }
        }else{
            try{
                service = ServiceProxyFactory.createServiceBaseProxy(obj);
            }catch(Exception e){
                return null;
            }
        }
        service.setServiceManagerName(getServiceName());
        service.setServiceName(name);
        return service;
    }
    
    // ServiceManager��JavaDoc
    public void addServiceLoader(ServiceLoader loader){
        if(!myLoaders.contains(loader)){
            synchronized(myLoaders){
                myLoaders.add(loader);
            }
            final ManagerMetaData managerData
                 = loader.getServerMetaData().getManager(getServiceName());
            managerDatas.add(managerData);
            final Iterator propKeys = managerData.getPropertyNameSet().iterator();
            while(propKeys.hasNext()){
                String propKey = (String)propKeys.next();
                String prop = managerData.getProperty(propKey);
                // �V�X�e���v���p�e�B�̒u��
                prop = Utility.replaceSystemProperty(prop);
                // �T�[�r�X���[�_�\���v���p�e�B�̒u��
                prop = Utility.replaceServiceLoderConfig(
                    prop,
                    loader.getConfig()
                );
                // �}�l�[�W���v���p�e�B�̒u��
                prop = Utility.replaceManagerProperty(
                    this,
                    prop
                );
                // �T�[�o�v���p�e�B�̒u��
                prop = Utility.replaceServerProperty(prop);
                managerProperties.setProperty(propKey, prop);
            }
        }
    }
    
    // ServiceManager��JavaDoc
    public void removeServiceLoader(ServiceLoader loader){
        if(myLoaders.contains(loader)){
            synchronized(myLoaders){
                myLoaders.remove(loader);
            }
            final ManagerMetaData managerData
                 = loader.getServerMetaData().getManager(getServiceName());
            managerDatas.remove(managerData);
            final Iterator keys
                 = managerData.getProperties().keySet().iterator();
            while(keys.hasNext()){
                managerProperties.remove(keys.next());
            }
        }
    }
    
    // ServiceManager��JavaDoc
    public Set getServiceLoaders(){
        synchronized(myLoaders){
            return new HashSet(myLoaders);
        }
    }
    
    // ServiceManager��JavaDoc
    public Set getManagerMetaDatas(){
        return managerDatas;
    }
    
    // ServiceManager��JavaDoc
    public String getProperty(String name){
        return managerProperties.getProperty(name);
    }
    
    /**
     * �T�[�r�X�𐶐�����K�v�����邩���ׂ�B<p>
     * �K��true��Ԃ��B<br>
     *
     * @return �T�[�r�X�𐶐�����K�v������ꍇtrue�A�����łȂ��ꍇfalse
     */
    protected boolean isNecessaryToCreate(){
        return true;
    }
    
    /**
     * ���̃T�[�r�X�̐����O�������s���B<p>
     *
     * @exception Exception �����O�����Ɏ��s�����ꍇ
     */
    protected void preCreateService() throws Exception{
        final Logger logger = getLogger();
        
        state = CREATING;
        processStateChanged(CREATING);
        
        if(getServiceName() != null){
            if(isRegisteredService(getServiceName())){
                final Service registeredService = getService(getServiceName());
                if(registeredService != this){
                    logger.write(
                        SVCM_00013,
                        new Object[]{getServiceName(), getServiceName()}
                    );
                    stopService(getServiceName());
                    destroyService(getServiceName());
                    if(isRegisteredService(getServiceName())){
                        unregisterService(getServiceName());
                    }
                    registerService(getServiceName(), this);
                }
            }else{
                registerService(getServiceName(), this);
            }
            
            if(ServiceManagerFactory.isRegisteredManager(getServiceName())){
                final ServiceManager registeredManager
                     = ServiceManagerFactory.findManager(getServiceName());
                if(registeredManager != this){
                    logger.write(SVCM_00014, getServiceName());
                    registeredManager.destroy();
                    if(ServiceManagerFactory
                        .isRegisteredManager(getServiceName())){
                        ServiceManagerFactory
                            .unregisterManager(getServiceName());
                    }
                    ServiceManagerFactory.registerManager(
                        getServiceName(),
                        this
                    );
                }
            }else{
                ServiceManagerFactory.registerManager(getServiceName(), this);
            }
        }
    }
    
    /**
     * �T�[�r�X���J�n����K�v�����邩���ׂ�B<p>
     * �T�[�r�X��Ԃ�{@link #DESTROYED}�܂��́A{@link #FAILED}�ŁA�T�[�r�X���J�n���悤�Ƃ����ꍇ�A��O��throw����B����ȊO�́Atrue��Ԃ��B<br>
     *
     * @return �T�[�r�X���J�n����K�v������ꍇtrue�A�����łȂ��ꍇfalse
     * @exception Exception �T�[�r�X��Ԃ�{@link #DESTROYED}�܂��́A{@link #FAILED}�ŁA�T�[�r�X���J�n���悤�Ƃ����ꍇ
     */
    protected boolean isNecessaryToStart() throws Exception{
        if(state == DESTROYED || state == FAILED){
            final MessageRecordFactory message = getMessageRecordFactory();
            throw new IllegalStateException(
                message.findEmbedMessage(
                    SVCM_00009,
                    new Object[]{getServiceName(), STATES[state]}
                )
            );
        }
        return true;
    }
    
    /**
     * ���̃T�[�r�X�̔j���O�������s���B<p>
     *
     * @exception Exception �j���O�����Ɏ��s�����ꍇ
     */
    protected void preDestroyService() throws Exception{
        if(state != STOPPED){
            stop();
        }
        
        state = DESTROYING;
        processStateChanged(DESTROYING);
        
        if(getServiceName() != null){
            unregisterService(getServiceName());
        }
    }
    
    /**
     * ���̃T�[�r�X�̔j���㏈�����s���B<p>
     *
     * @exception Exception �j���㏈���Ɏ��s�����ꍇ
     */
    protected void postDestroyService() throws Exception{
        if(getServiceName() != null){
            if(ServiceManagerFactory.findManager(getServiceName()) == this){
                ServiceManagerFactory.unregisterManager(getServiceName());
            }
        }
        
        state = DESTROYED;
        processStateChanged(DESTROYED);
    }
    
    /**
     * ���̃T�[�r�X�̐����������s���B<p>
     * ���̃}�l�[�W���ɓo�^����Ă���S�ẴT�[�r�X�ɑ΂��Ĉȉ��̏����������s���B<br>
     * <ol>
     *   <li>�T�[�r�X�̐��������i{@link Service#create()}�j���s���B</li>
     *   <li>�T�[�r�X�̑����̐ݒ���s���B</li>
     * </ol>
     *
     * @exception Exception ���������Ɏ��s�����ꍇ
     */
    public void createService() throws Exception{
       createAllService();
    }
    
    /**
     * ���̃T�[�r�X�̊J�n�������s���B<p>
     * ���̃}�l�[�W���ɓo�^����Ă���S�ẴT�[�r�X�̊J�n����({@link Service#start()})���s���B<br>
     * �T�[�r�X�̊J�n���ɂ́A�J�n���悤�Ƃ��Ă���T�[�r�X���ˑ����Ă���T�[�r�X�̊J�n���D�悳��čs����B<br>
     * �ˑ��֌W�̂���T�[�r�X�̃��[�h���������Ă��Ȃ��ꍇ�ɂ́A���̃T�[�r�X�����[�h����J�n�����܂ŁA�ˑ�����Ă���T�[�r�X�̊J�n�͑ҋ@�����B<br>
     * <p>
     * �܂��A���̃}�l�[�W������`����Ă���T�[�r�X��`�t�@�C����server�v�f�̎q�v�f�ɁAmanager-repository�v�f�Alog�v�f�Amessage�v�f����`����Ă���ꍇ�AServiceManagerFactory��Logger�AMessageRecordFactory�ARepository�̐ݒ���s���B<br>
     * ���l�ɁA���̃}�l�[�W������`����Ă���manager�v�f�̎q�v�f�ɁArepository�v�f�Alog�v�f�Amessage�v�f����`����Ă���ꍇ�A���̃}�l�[�W����Logger�AMessageRecordFactory�ARepository�̐ݒ���s���B<br>
     *
     * @exception Exception �J�n�����Ɏ��s�����ꍇ
     */
    public void startService() throws Exception{
        final Logger logger = getLogger();
        
        startAllService();
        
        if(existWaitingService()){
            final Iterator waitingServices = getWaitingServices().iterator();
            while(waitingServices.hasNext()){
                final String waitingServiceName
                     = (String)waitingServices.next();
                final Set causes = getWaitingCauses(waitingServiceName);
                logger.write(
                    SVCM_00024,
                    new Object[]{getServiceName(), waitingServiceName, causes}
                );
            }
        }
    }
    
    /**
     * ���̃T�[�r�X�̒�~�������s���B<p>
     * ���̃}�l�[�W���ɓo�^����Ă���S�ẴT�[�r�X�̒�~�����i{@link Service#stop()}�j���s���B<p>
     * �T�[�r�X�̒�~���ɂ́A��~���悤�Ƃ��Ă���T�[�r�X���ˑ�����Ă���T�[�r�X�̒�~���D�悳��čs����B<br>
     *
     * @exception Exception ��~�����Ɏ��s�����ꍇ
     */
    public void stopService() throws Exception{
        stopAllService();
        clearWaitingServices();
        if(existFailedService()){
            final Iterator failedServiceNames = getFailedServices().iterator();
            while(failedServiceNames.hasNext()){
                final String name = (String)failedServiceNames.next();
                try{
                    createService(name);
                }catch(Exception e){}
            }
            clearFailedServices();
        }
        attributePropCache.clear();
    }
    
    /**
     * ���̃T�[�r�X�̔j���������s���B<p>
     * ���̃}�l�[�W���ɓo�^����Ă���S�ẴT�[�r�X�̔j���������s���B<p>
     *
     * @exception Exception �j�������Ɏ��s�����ꍇ
     */
    public void destroyService() throws Exception{
        destroyAllService();
    }
    
    // ServiceManager��JavaDoc
    public void createService(String name) throws Exception{
        createService(name, new HashSet());
    }
    
    // ServiceManager��JavaDoc
    public void createService(String name, Set completed)
     throws Exception{
        changeServiceState(name, CREATING, completed);
    }
    
    // ServiceManager��JavaDoc
    public void createService(Set names){
        changeServiceState(names, CREATING);
    }
    
    // ServiceManager��JavaDoc
    public void createService(Service service, ServiceMetaData serviceData)
     throws Exception{
        changeServiceState(service, serviceData, CREATING);
    }
    
    // ServiceManager��JavaDoc
    public void createAllService(){
        changeAllServiceState(CREATING);
    }
    
    // ServiceManager��JavaDoc
    public void startService(String name) throws Exception{
        startService(name, new HashSet());
    }
    
    // ServiceManager��JavaDoc
    public void startService(String name, Set completed) throws Exception{
        changeServiceState(name, STARTING, completed);
    }
    
    // ServiceManager��JavaDoc
    public void startService(Set names){
        changeServiceState(names, STARTING);
    }
    
    // ServiceManager��JavaDoc
    public void startService(Service service, ServiceMetaData serviceData)
     throws Exception{
        changeServiceState(service, serviceData, STARTING);
    }
    
    // ServiceManager��JavaDoc
    public void startAllService(){
        changeAllServiceState(STARTING);
    }
    
    // ServiceManager��JavaDoc
    public void restartService(String name) throws Exception{
        restartService(name, new HashSet());
    }
    
    // ServiceManager��JavaDoc
    public void restartService(String name, Set completed) throws Exception{
        stopService(name, completed);
        completed.clear();
        startService(name, completed);
    }
    
    // ServiceManager��JavaDoc
    public void restartService(Set names){
        stopService(names);
        startService(names);
    }
    
    // ServiceManager��JavaDoc
    public void restartService(Service service, ServiceMetaData serviceData)
     throws Exception{
        stopService(service, serviceData);
        startService(service, serviceData);
    }
    
    // ServiceManager��JavaDoc
    public void restartAllService(){
        stopAllService();
        startAllService();
    }
    
    // ServiceManager��JavaDoc
    public void stopService(String name){
        stopService(name, new HashSet());
    }
    
    // ServiceManager��JavaDoc
    public void stopService(String name, Set completed){
        final Logger logger = getLogger();
        try{
            changeServiceState(name, STOPPING, completed);
        }catch(Exception e){
            //  �N���蓾�Ȃ�
            logger.write(SVCM_00035, e);
        }
    }
    
    // ServiceManager��JavaDoc
    public void stopService(Set names){
        changeServiceState(names, STOPPING);
    }
    
    // ServiceManager��JavaDoc
    public void stopService(Service service, ServiceMetaData serviceData){
        try{
            changeServiceState(service, serviceData, STOPPING);
        }catch(Exception e){
            //  �N���蓾�Ȃ�
            logger.write(SVCM_00035, e);
        }
    }
    
    // ServiceManager��JavaDoc
    public void stopAllService(){
        changeAllServiceState(STOPPING);
    }
    
    // ServiceManager��JavaDoc
    public void destroyService(String name){
        destroyService(name, new HashSet());
    }
    
    // ServiceManager��JavaDoc
    public void destroyService(String name, Set completed){
        changeServiceState(name, DESTROYING, completed);
    }
    
    // ServiceManager��JavaDoc
    public void destroyService(Set names){
        changeServiceState(names, DESTROYING);
    }
    
    // ServiceManager��JavaDoc
    public void destroyService(Service service, ServiceMetaData serviceData){
        try{
            changeServiceState(service, serviceData, DESTROYING);
        }catch(Exception e){
            //  �N���蓾�Ȃ�
            logger.write(SVCM_00035, e);
        }
    }
    
    // ServiceManager��JavaDoc
    public void destroyAllService(){
        changeAllServiceState(DESTROYING);
    }
    
    /**
     * ���̃}�l�[�W���ɓo�^����Ă���S�ẴT�[�r�X�̏�Ԃ��A�w�肳�ꂽ��Ԃ̊����`�̏�ԂւƑJ�ڂ�����B<p>
     *
     * @param state �ڍs�������J�ڏ��
     * @see #changeServiceState(String, int, Set)
     */
    protected void changeAllServiceState(int state){
        changeServiceState(serviceNameSet(), state);
    }
    
    /**
     * �w�肳�ꂽ�T�[�r�X�W���̏�Ԃ��A�w�肳�ꂽ��Ԃ̊����`�̏�ԂւƑJ�ڂ�����B<p>
     *
     * @param serviceNames �T�[�r�X��������̏W��
     * @param state �ڍs�������J�ڏ��
     * @see #changeServiceState(String, int, Set)
     */
    protected void changeServiceState(Set serviceNames, int state){
        final Set completed = new HashSet();
        final String myName = getServiceName();
        final Iterator names = serviceNames.iterator();
        while(names.hasNext()){
            final String name = (String)names.next();
            if(name.equals(myName) || completed.contains(name)){
                continue;
            }
            changeServiceState(name, state, completed);
        }
    }
    
    /**
     * �w�肳�ꂽ�T�[�r�X�̏�Ԃ��A�w�肳�ꂽ��Ԃ̊����`�̏�ԂւƑJ�ڂ�����B<p>
     * state�Ɏw��\�ȏ�Ԃ́A{@link Service#CREATING}�A{@link Service#STARTING}�A{@link Service#STOPPING}�A{@link Service#DESTROYING}�̂S�ł���B���ꂼ����w�肵�ď�ԕύX���s�������ʂ́A���ꂼ��{@link Service#CREATED}�A{@link Service#STARTED}�A{@link Service#STOPPED}�A{@link Service#DESTROYED}�ƂȂ�B<br>
     *
     * @param name �T�[�r�X��
     * @param state �ڍs�������J�ڏ��
     * @param completed ��ԑJ�ڂ��ꂽ�T�[�r�X���̏W���B�ˑ��֌W�ɂ���ԑJ�ڂ��ꂽ�T�[�r�X���܂ށB
     */
    protected void changeServiceState(
        String name,
        int state,
        Set completed
    ){
        if(getServiceName().equals(name)){
            return;
        }
        Service service = null;
        try{
            service = getService(name);
        }catch(ServiceNotFoundException e){
            return;
        }
        if(getServiceName().equals(service.getServiceManagerName())){
            boolean changeState = false;
            switch(state){
            case CREATING:
                if(completed == null || !completed.contains(name)){
                    changeState = changeServiceState(service, state);
                    if(completed != null){
                        completed.add(name);
                    }
                }
                break;
            case STARTING:
                ServiceMetaData serviceData = null;
                try{
                    serviceData = getServiceMetaData(name);
                }catch(ServiceNotFoundException e){
                }
                if(serviceData != null
                     && serviceData.getServiceLoader() != null
                     && serviceData.getServiceLoader().getState() != STARTED
                     && serviceData.getInitStateValue() < STARTING){
                    if(completed != null){
                        completed.add(name);
                    }
                    break;
                }
                if(processDepends(this, name, state, completed)){
                    if(completed == null || !completed.contains(name)){
                        changeState = changeServiceState(service, state);
                        if(completed != null){
                            completed.add(name);
                        }
                        if(!changeState){
                            return;
                        }
                    }
                    processDepended(this, name, state, completed);
                }
                break;
            case STOPPING:
                if(processDepended(this, name, state, completed)){
                    if(completed == null || !completed.contains(name)){
                        changeState = changeServiceState(service, state);
                        if(completed != null){
                            completed.add(name);
                        }
                    }
                }
                break;
            case DESTROYING:
                if(service.getState() != STOPPED){
                    changeServiceState(name, STOPPING, new HashSet());
                }
                if(completed == null || !completed.contains(name)){
                    changeState = changeServiceState(service, state);
                    if(completed != null){
                        completed.add(name);
                    }
                }
                break;
            default:
                final MessageRecordFactory message
                     = getMessageRecordFactory();
                throw new IllegalArgumentException(
                    message.findEmbedMessage(
                        SVCM_00010,
                        new Object[]{
                            getServiceName(),
                            name,
                            STATES[state],
                            getStateString()
                        }
                    )
                );
            }
        }
    }
    
    /**
     * �w�肵���T�[�r�X��ҋ@���̃T�[�r�X�Ƃ��Ēǉ�����B<p>
     *
     * @param causeService �ҋ@�̌����ƂȂ��Ă���T�[�r�X�̃T�[�r�X��
     * @param waitService �ҋ@���̃T�[�r�X�̃T�[�r�X��
     */
    private void addWaitingServiceCause(
        ServiceName causeService,
        String waitService
    ){
        Set causes = getWaitingCauses(waitService);
        if(causes == null){
            causes = new HashSet();
            waitingServices.put(waitService, causes);
        }
        if(!causes.contains(causeService)){
            causes.add(causeService);
        }
    }
    
    /**
     * �w�肵���T�[�r�X��ҋ@���̃T�[�r�X����폜����B<p>
     *
     * @param causeService �ҋ@�̌����ƂȂ��Ă���T�[�r�X�̃T�[�r�X��
     * @param waitService �ҋ@���̃T�[�r�X�̃T�[�r�X��
     */
    private void removeWaitingServiceCause(
        ServiceName causeService,
        String waitService
    ){
        Set causes = getWaitingCauses(waitService);
        if(causes == null){
            return;
        }
        if(causes.contains(causeService)){
            causes.remove(causeService);
            if(causes.size() == 0){
                waitingServices.remove(waitService);
            }
        }
    }
    
    // ServiceManager��JavaDoc
    public Set getWaitingCauses(String waitService){
        Set causes = null;
        if(waitingServices.containsKey(waitService)){
            causes = (Set)waitingServices.get(waitService);
        }
        return causes;
    }
    
    // ServiceManager��JavaDoc
    public void clearWaitingServices(){
        waitingServices.clear();
    }
    
    // ServiceManager��JavaDoc
    public boolean existWaitingService(){
        return waitingServices.size() != 0;
    }
    
    // ServiceManager��JavaDoc
    public Set getWaitingServices(){
        return new HashSet(waitingServices.keySet());
    }
    
    /**
     * �w�肵���T�[�r�X���N�����s�ɂ��ҋ@���̃T�[�r�X�Ƃ��Ēǉ�����B<p>
     *
     * @param causeService �ҋ@�̌����ƂȂ��Ă���T�[�r�X�̃T�[�r�X��
     * @param waitService �ҋ@���̃T�[�r�X�̃T�[�r�X��
     */
    private void addFailedServiceCause(
        String failedService,
        Throwable cause
    ){
        failedServices.put(failedService, cause);
    }
    
    /**
     * �w�肵���T�[�r�X���N�����s�ɂ��ҋ@���̃T�[�r�X����폜����B<p>
     *
     * @param causeService �ҋ@�̌����ƂȂ��Ă���T�[�r�X�̃T�[�r�X��
     * @param waitService �ҋ@���̃T�[�r�X�̃T�[�r�X��
     */
    private void removeFailedServiceCause(
        String failedService
    ){
        if(failedService == null){
            return;
        }
        failedServices.remove(failedService);
    }
    
    // ServiceManager��JavaDoc
    public Throwable getFailedCause(String failedService){
        Throwable cause = null;
        if(failedServices.containsKey(failedService)){
            cause = (Throwable)failedServices.get(failedService);
        }
        return cause;
    }
    
    // ServiceManager��JavaDoc
    public void clearFailedServices(){
        failedServices.clear();
    }
    
    // ServiceManager��JavaDoc
    public boolean existFailedService(){
        return failedServices.size() != 0;
    }
    
    // ServiceManager��JavaDoc
    public Set getFailedServices(){
        return new HashSet(failedServices.keySet());
    }
    
    /**
     * �ˑ�����T�[�r�X�̏�ԕύX��ҋ@���āA��ԕύX���ꂽ�^�C�~���O�őΏۂ̃T�[�r�X�̏�ԕύX���s����悤�ɓo�^����B<p>
     *
     * @param target ��ԕύX���Ď��������T�[�r�X
     * @param manager ��ԕύX��ҋ@����T�[�r�X���o�^����Ă���ServiceManager
     * @param waitService ��ԕύX��ҋ@����T�[�r�X
     * @param state ��ԕύX��ҋ@����T�[�r�X���J�ڂ��������
     * @param completed ��ԑJ�ڂ��ꂽ�T�[�r�X���̏W���B�ˑ��֌W�ɂ���ԑJ�ڂ��ꂽ�T�[�r�X���܂ށB
     */
    private void waitServiceStateProcess(
        Service target,
        final ServiceManager manager,
        final String waitService,
        final int state,
        final Set completed
    ){
        final Logger logger = getLogger();
        
        Service targetService = null;
        if(!(target instanceof ServiceStateBroadcaster)){
            final String managerName = target.getServiceManagerName();
            final ServiceManager mng = ServiceManagerFactory.findManager(
                managerName
            );
            targetService = mng;
        }else{
            targetService = target;
        }
        
        final ServiceStateBroadcaster broad
             = (ServiceStateBroadcaster)targetService;
        final ServiceName cause = new ServiceName(
            target.getServiceManagerName(),
            target.getServiceName()
        );
        addWaitingServiceCause(cause, waitService);
        class ServiceWaitServiceStateListener implements ServiceStateListener{
            private String managerName;
            private String serviceName;
            public ServiceWaitServiceStateListener(
                String managerName,
                String serviceName
            ){
                this.managerName = managerName;
                this.serviceName = serviceName;
            }
            public void stateChanged(ServiceStateChangeEvent e){
                removeWaitingServiceCause(cause, waitService);
                broad.removeServiceStateListener(this);
                if(!completed.contains(waitService)){
                    try{
                        switch(state){
                        case CREATING:
                            manager.createService(waitService, completed);
                            break;
                        case STARTING:
                            manager.startService(waitService, completed);
                            break;
                        case STOPPING:
                            manager.stopService(waitService, completed);
                            break;
                        case DESTROYING:
                            manager.destroyService(waitService, completed);
                            break;
                        default:
                            // �N���蓾�Ȃ�
                        }
                    }catch(Exception ex){
                        String messageId = null;
                        switch(getState()){
                        case CREATING:
                            messageId = SVCM_00033;
                            break;
                        case STARTING:
                            messageId = SVCM_00034;
                            break;
                        case STOPPING:
                        case DESTROYING:
                        default:
                            // �N���蓾�Ȃ�
                            logger.write(SVCM_00035, ex);
                            return;
                        }
                        logger.write(
                            messageId,
                            new Object[]{
                                manager.getServiceName(),
                                waitService
                            },
                            ex
                        );
                    }
                }
            }
            public boolean isEnabledState(int st){
                if(state == CREATING){
                    return st == CREATED;
                }else if(state == STARTING){
                    return st == STARTED;
                }else{
                    return false;
                }
            }
            public int hashCode(){
                return (managerName == null ? 0 : managerName.hashCode())
                    + (serviceName == null ? 0 : serviceName.hashCode());
            }
            public boolean equals(Object obj){
                if(obj == null
                    || !(obj instanceof ServiceWaitServiceStateListener)){
                    return false;
                }
                ServiceWaitServiceStateListener comp
                    = (ServiceWaitServiceStateListener)obj;
                if((managerName != null || comp.managerName != null)
                    && (managerName == null || comp.managerName == null
                        || !managerName.equals(comp.managerName))){
                    return false;
                }
                if((serviceName != null || comp.serviceName != null)
                    && (serviceName == null || comp.serviceName == null
                        || !serviceName.equals(comp.serviceName))){
                    return false;
                }
                return true;
            }
        }
        broad.addServiceStateListener(
            new ServiceWaitServiceStateListener(
                getServiceName(),
                waitService
            )
        );
        
        String messageId = null;
        switch(state){
        case CREATING:
            messageId = SVCM_00025;
            break;
        case STARTING:
            messageId = SVCM_00026;
            break;
        case STOPPING:
            messageId = SVCM_00027;
            break;
        case DESTROYING:
            messageId = SVCM_00028;
            break;
        default:
            break;
        }
        logger.write(
            messageId,
            new Object[]{
                targetService.getServiceManagerName(),
                targetService.getServiceName(),
                getServiceName(),
                waitService
            }
        );
    }
    
    /**
     * �ˑ�����T�[�r�X�}�l�[�W���̓o�^��ҋ@���āA�o�^���ꂽ�^�C�~���O�ŏ�ԕύX�Ď��Ώۂ̃T�[�r�X�̏�Ԃ��`�F�b�N���āA�Ώۂ̃T�[�r�X�̏�ԕύX���s����悤�ɓo�^����B<p>
     *
     * @param targetMng ServiceManagerFactory�ւ̓o�^���Ď�������ServiceManager�̖��O
     * @param targetService ��ԕύX���Ď��������T�[�r�X�̖��O
     * @param waitService ��ԕύX��ҋ@����T�[�r�X
     * @param completed ��ԑJ�ڂ��ꂽ�T�[�r�X���̏W���B�ˑ��֌W�ɂ���ԑJ�ڂ��ꂽ�T�[�r�X���܂ށB
     * @param isInit ���[�h�����ǂ����̃t���O
     */
    private void waitRegistrationManagerProcess(
        final String targetMng,
        final String targetService,
        final String waitService,
        final Set completed,
        final boolean isInit
    ){
        final Logger logger = getLogger();
        
        final ServiceName cause = new ServiceName(
            targetMng,
            targetMng
        );
        addWaitingServiceCause(cause, waitService);
        ServiceManagerFactory.addRegistrationListener(
            new RegistrationListener(){
                private final int state = getState();
                public void registered(RegistrationEvent e){
                    final ServiceManager manager
                         = (ServiceManager)e.getRegistration();
                    if(!manager.getServiceName().equals(targetMng)){
                        return;
                    }
                    ServiceManagerFactory.removeRegistrationListener(this);
                    removeWaitingServiceCause(cause, waitService);
                    Service service = null;
                    try{
                        service = manager.getService(targetService);
                    }catch(ServiceNotFoundException ex){
                        waitRegistrationServiceProcess(
                            manager,
                            targetService,
                            waitService,
                            state,
                            completed,
                            isInit
                        );
                        return;
                    }
                    if(!isMatchingState(service, state, isInit)){
                        waitServiceStateProcess(
                            service,
                            DefaultServiceManagerService.this,
                            waitService,
                            state,
                            completed
                        );
                    }else if(!completed.contains(waitService)){
                        DefaultServiceManagerService.this.changeServiceState(
                            waitService,
                            state,
                            completed
                        );
                    }
                }
                public void unregistered(RegistrationEvent e){
                }
            }
        );
        
        String messageId = null;
        switch(getState()){
        case CREATING:
            messageId = SVCM_00031;
            break;
        case STARTING:
            messageId = SVCM_00032;
            break;
        default:
            break;
        }
        logger.write(
            messageId,
            new Object[]{
                targetMng,
                getServiceName(),
                waitService
            }
        );
    }
    
    private void waitRegistrationServiceProcess(
        final ServiceManager targetMng,
        final String targetService,
        final String waitService,
        final int state,
        final Set completed,
        final boolean isInit
    ){
        final Logger logger = getLogger();
        
        final ServiceName cause = new ServiceName(
            targetMng.getServiceName(),
            targetService
        );
        addWaitingServiceCause(cause, waitService);
        
        targetMng.addRegistrationListener(
            new RegistrationListener(){
                public void registered(RegistrationEvent e){
                    final Service service
                         = (Service)e.getRegistration();
                    if(!service.getServiceName().equals(targetService)){
                        return;
                    }
                    removeWaitingServiceCause(cause, waitService);
                    targetMng.removeRegistrationListener(this);
                    if(!isMatchingState(service, state, isInit)){
                        waitServiceStateProcess(
                            service,
                            DefaultServiceManagerService.this,
                            waitService,
                            state,
                            completed
                        );
                    }else if(!completed.contains(waitService)){
                        DefaultServiceManagerService.this.changeServiceState(
                            waitService,
                            state,
                            completed
                        );
                    }
                }
                public void unregistered(RegistrationEvent e){
                }
            }
        );
        
        String messageId = null;
        switch(state){
        case CREATING:
            messageId = SVCM_00029;
            break;
        case STARTING:
            messageId = SVCM_00030;
            break;
        default:
            break;
        }
        logger.write(
            messageId,
            new Object[]{
                targetMng.getServiceName(),
                targetService,
                getServiceName(),
                waitService
            }
        );
    }
    
    private boolean processDepends(
        ServiceManager manager,
        final String serviceName,
        int state,
        Set completed
    ){
        boolean isInit = false;
        try{
            final ServiceMetaData myData = manager.getServiceMetaData(serviceName);
            final ServiceLoader myLoader = myData.getServiceLoader();
            isInit = myLoader != null && myLoader.getState() != STARTED;
        }catch(ServiceNotFoundException e){
        }
        final Logger logger = getLogger();
        boolean result = true;
        final Iterator loaders = getServiceLoaders().iterator();
        while(loaders.hasNext()){
            final ServiceLoader loader = (ServiceLoader)loaders.next();
            final List dependsList = loader.getDepends(
                manager.getServiceName(),
                serviceName
            );
            if(dependsList == null){
                continue;
            }
            final Iterator services = dependsList.iterator();
            while(services.hasNext()){
                final ServiceMetaData.DependsMetaData dependsData
                     = (ServiceMetaData.DependsMetaData)services.next();
                final String managerName = dependsData.getManagerName();
                final String dependsServiceName = dependsData.getServiceName();
                
                ServiceManager mng = null;
                Service dependsService = null;
                final boolean isMyService
                     = managerName.equals(getServiceName());
                if(isMyService){
                    mng = manager;
                    try{
                        dependsService = getService(dependsServiceName);
                    }catch(ServiceNotFoundException e){
                        dependsService = null;
                    }
                }else{
                    mng = ServiceManagerFactory.findManager(managerName);
                    if(mng == null){
                        waitRegistrationManagerProcess(
                            managerName,
                            dependsServiceName,
                            serviceName,
                            new HashSet(),
                            isInit
                        );
                        result = false;
                        continue;
                    }
                    try{
                        dependsService = mng.getService(dependsServiceName);
                    }catch(ServiceNotFoundException e){
                        dependsService = null;
                    }
                }
                
                if(dependsService == null){
                    waitRegistrationServiceProcess(
                        mng,
                        dependsServiceName,
                        serviceName,
                        state,
                        completed,
                        isInit
                    );
                    result = false;
                    continue;
                }
                
                // ���݈ˑ��`�F�b�N
                if(isDepends(mng, dependsServiceName, manager, serviceName)){
                    logger.write(
                        SVCM_00036,
                        new Object[]{
                            manager.getServiceName(),
                            serviceName,
                            mng.getServiceName(),
                            dependsServiceName
                        }
                    );
                    waitServiceStateProcess(
                        dependsService,
                        manager,
                        serviceName,
                        state,
                        isMyService ? completed : new HashSet()
                    );
                    result = false;
                    continue;
                }
                
                if(!isMatchingState(dependsService, state, isInit)){
                    if(!isMyService){
                        waitServiceStateProcess(
                            dependsService,
                            manager,
                            serviceName,
                            state,
                            new HashSet()
                        );
                        result = false;
                        continue;
                    }else if(!completed.contains(dependsServiceName)){
                        changeServiceState(
                            dependsServiceName,
                            state,
                            completed
                        );
                        if(!completed.contains(dependsServiceName)){
                            waitServiceStateProcess(
                                dependsService,
                                manager,
                                serviceName,
                                state,
                                completed
                            );
                            result = false;
                            continue;
                        }
                    }else{
                        result = false;
                        continue;
                    }
                }
            }
        }
        return result;
    }
    
    private boolean processDepended(
        ServiceManager manager,
        String serviceName,
        int state,
        Set completed
    ){
        boolean isInit = false;
        try{
            final ServiceMetaData myData = manager.getServiceMetaData(serviceName);
            final ServiceLoader myLoader = myData.getServiceLoader();
            isInit = myLoader != null && myLoader.getState() != STARTED;
        }catch(ServiceNotFoundException e){
        }
        
        final Logger logger = getLogger();
        boolean result = true;
        final Iterator loaders = ServiceManagerFactory.getLoaders().iterator();
        while(loaders.hasNext()){
            final ServiceLoader loader = (ServiceLoader)loaders.next();
            if(loader.getState() != STARTING
                 && loader.getState() != STARTED
                 && loader.getState() != STOPPING){
                loaders.remove();
                continue;
            }
            final List dependedList = loader.getDependedServices(
                manager.getServiceName(),
                serviceName
            );
            final Iterator services = dependedList.iterator();
            while(services.hasNext()){
                final ServiceMetaData serviceData
                     = (ServiceMetaData)services.next();
                final String dependsManagerName
                     = serviceData.getManager().getName();
                final ServiceManager mng = ServiceManagerFactory.findManager(
                    dependsManagerName
                );
                if(mng == null){
                    continue;
                }
                final String dependsServiceName = serviceData.getName();
                Service dependsService = null;
                try{
                    dependsService = mng.getService(dependsServiceName);
                }catch(ServiceNotFoundException e){
                    if(state != CREATING){
                        continue;
                    }
                    try{
                        serviceData.getServiceLoader().deployService(serviceData);
                    }catch(DeploymentException ee){
                        continue;
                    }
                    dependsService = mng.getService(dependsServiceName);
                }
                
                if(isDepends(manager, serviceName, mng, dependsServiceName)){
                    logger.write(
                        SVCM_00036,
                        new Object[]{
                            manager.getServiceName(),
                            serviceName,
                            mng.getServiceName(),
                            dependsServiceName
                        }
                    );
                    result = false;
                    continue;
                }
                
                if(!isMatchingState(dependsService, state, isInit)){
                   if(!completed.contains(dependsServiceName)){
                        switch(state){
                        case STARTING:
                            if(mng.getState() == STARTED){
                                try{
                                    mng.startService(
                                        dependsServiceName,
                                        completed
                                    );
                                }catch(Exception e){
                                    continue;
                                }
                            }
                            break;
                        case STOPPING:
                            mng.stopService(dependsServiceName, completed);
                            break;
                        case DESTROYING:
                        case CREATING:
                        default:
                            //  �N���蓾�Ȃ�
                        }
                    }
                }
            }
        }
        return result;
    }
    
    private boolean isDepends(
        ServiceManager manager1,
        String service1,
        ServiceManager manager2,
        String service2
    ){
        final Set depends = getDepends(
            manager1,
            service1,
            new HashSet()
        );
        if(depends == null){
            return false;
        }
        final Iterator services = depends.iterator();
        while(services.hasNext()){
            final ServiceMetaData.DependsMetaData dependsData
                 = (ServiceMetaData.DependsMetaData)services.next();
            
            if(dependsData.getServiceName().equals(service2)
                && dependsData.getManagerName()
                    .equals(manager2.getServiceName())
            ){
                return true;
            }
        }
        return false;
    }
    
    
    private Set getDepends(
        ServiceManager manager,
        String service,
        Set depends
    ){
        final Iterator loaders = manager.getServiceLoaders().iterator();
        while(loaders.hasNext()){
            final ServiceLoader loader = (ServiceLoader)loaders.next();
            final List dependsList = loader.getDepends(
                manager.getServiceName(),
                service
            );
            if(dependsList == null){
                continue;
            }
            final Iterator services = dependsList.iterator();
            while(services.hasNext()){
                final ServiceMetaData.DependsMetaData dependsData
                     = (ServiceMetaData.DependsMetaData)services.next();
                if(depends.contains(dependsData)){
                    continue;
                }
                final ServiceManager mng = ServiceManagerFactory.findManager(
                    dependsData.getManagerName()
                );
                if(mng == null){
                    continue;
                }
                depends.add(dependsData);
                depends = getDepends(
                    mng,
                    dependsData.getServiceName(),
                    depends
                );
            }
        }
        return depends;
    }
    
    private boolean isMatchingState(Service service, int state, boolean isInit)
     throws IllegalStateException{
        final int serviceState = service.getState();
        ServiceMetaData serviceData = null;
        try{
            serviceData = ServiceManagerFactory.getServiceMetaData(
                service.getServiceManagerName(),
                service.getServiceName()
            );
        }catch(ServiceNotFoundException e){
        }
        if(state < STARTED
             && serviceData != null
             && isInit
             && serviceData.getInitStateValue() < state
        ){
            return true;
        }
        switch(state){
        case CREATING:
        case CREATED:
            return serviceState == CREATED || serviceState == STARTING
             || serviceState == STARTED;
        case STARTING:
        case STARTED:
            return serviceState == STARTED;
        case STOPPING:
        case STOPPED:
            return serviceState == STOPPED || serviceState == DESTROYING
                 || serviceState == DESTROYED;
        case DESTROYING:
        case DESTROYED:
            return serviceState == DESTROYED;
        default:
            final MessageRecordFactory message
                 = getMessageRecordFactory();
            throw new IllegalStateException(
                message.findEmbedMessage(
                    SVCM_00011,
                    new Object[]{
                        getServiceName(),
                        name,
                        STATES[state],
                        STATES[serviceState]
                    }
                )
            );
        }
    }
    
    private boolean changeServiceState(Service service, int state){
        final Logger logger = getLogger();
        final String managerName = service.getServiceManagerName();
        final String serviceName = service.getServiceName();
        if(getServiceName().equals(serviceName)){
            return false;
        }
        ServiceManager manager = null;
        if(managerName.equals(getServiceName())){
            manager = this;
        }else{
            manager = ServiceManagerFactory.findManager(managerName);
        }
        ServiceMetaData serviceData = null;
        try{
            serviceData = manager.getServiceMetaData(serviceName);
        }catch(ServiceNotFoundException e){
        }
        try{
            return changeServiceState(service, serviceData, state);
        }catch(Exception e){
            switch(state){
            case CREATING:
                logger.write(
                    SVCM_00033,
                    new Object[]{managerName, serviceName},
                    e
                );
                if(managerName.equals(getServiceName())){
                    addFailedServiceCause(serviceName, e);
                }
                return false;
            case STARTING:
                logger.write(
                    SVCM_00034,
                    new Object[]{managerName, serviceName},
                    e
                );
                if(managerName.equals(getServiceName())){
                    addFailedServiceCause(serviceName, e);
                }
                return false;
            case STOPPING:
            case DESTROYING:
            default:
                return true;
            }
        }
    }
    
    private boolean changeServiceState(
        Object obj,
        ServiceMetaData serviceData,
        int state
    ) throws Exception{
        synchronized(obj){
            String managerName = null;
            String serviceName = null;
            Service service = null;
            Object serviceObj = obj;
            if(obj instanceof Service){
                service = (Service)obj;
                managerName = service.getServiceManagerName();
                serviceName = service.getServiceName();
                if(getServiceName().equals(serviceName)){
                    return true;
                }
                serviceObj = convertServiceToObject(service);
            }
            switch(state){
            case CREATING:
                if(service instanceof ServiceBase
                        && !((ServiceBase)service).isNecessaryToCreate()){
                    return false;
                }
                if(serviceData != null && !serviceData.isFactory()){
                    callInvokes(serviceObj, serviceData, CREATING);
                }
                if(service != null){
                    service.create();
                }
                if(serviceData != null && !serviceData.isFactory()){
                    setFields(serviceObj, serviceData, CREATED);
                    setAttributes(serviceObj, serviceData, CREATED);
                    callInvokes(serviceObj, serviceData, CREATED);
                }
                removeFailedServiceCause(serviceName);
                break;
            case STARTING:
                if(service instanceof ServiceBase
                        && !((ServiceBase)service).isNecessaryToStart()){
                    return false;
                }
                if(serviceData != null && !serviceData.isFactory()){
                    setFields(serviceObj, serviceData, STARTING);
                    setAttributes(serviceObj, serviceData, STARTING);
                    callInvokes(serviceObj, serviceData, STARTING);
                }
                if(service != null){
                    service.start();
                }
                if(serviceData != null && !serviceData.isFactory()){
                    callInvokes(serviceObj, serviceData, STARTED);
                }
                break;
            case STOPPING:
                if(service instanceof ServiceBase
                        && !((ServiceBase)service).isNecessaryToStop()){
                    return false;
                }
                if(serviceData != null && !serviceData.isFactory()){
                    callInvokes(serviceObj, serviceData, STOPPING);
                }
                if(service != null){
                    service.stop();
                }
                if(serviceData != null && !serviceData.isFactory()){
                    callInvokes(serviceObj, serviceData, STOPPED);
                }
                break;
            case DESTROYING:
                if(service instanceof ServiceBase
                        && !((ServiceBase)service).isNecessaryToDestroy()){
                    return false;
                }
                if(serviceData != null && !serviceData.isFactory()){
                    callInvokes(serviceObj, serviceData, DESTROYING);
                }
                if(service != null){
                    service.destroy();
                }
                if(serviceData != null && !serviceData.isFactory()){
                    callInvokes(serviceObj, serviceData, DESTROYED);
                }
                break;
            default:
                final MessageRecordFactory message
                     = getMessageRecordFactory();
                throw new IllegalStateException(
                    message.findEmbedMessage(
                        SVCM_00012,
                        new Object[]{managerName, serviceName, STATES[state]}
                    )
                );
            }
        }
        return true;
    }
    
    public Service instanciateService(ServiceMetaData data) throws Exception{
        Object obj = instanciateObject(data);
        final Service service = convertObjectToService(
            data.getName(),
            obj
        );
        return service;
    }
    
    public Object createObject(ServiceMetaData data) throws Exception{
        Object obj = instanciateObject(data);
        
        if(!(obj instanceof ServiceBase) && obj instanceof ServiceBaseSupport){
            final ServiceBaseSupport support = (ServiceBaseSupport)obj;
            try{
                obj = ServiceProxyFactory.createServiceBaseProxy(support);
            }catch(Exception e){
                return null;
            }
        }
        
        if(obj != null){
            changeServiceState(obj, data, CREATING);
            changeServiceState(obj, data, STARTING);
        }
        return obj;
    }
    
    
    private Object getValueOfServiceRef(ServiceRefMetaData serviceRefData)
     throws ServiceNotFoundException{
        return ServiceManagerFactory.getServiceObject(
            serviceRefData.getManagerName(),
            serviceRefData.getServiceName()
        );
    }
    
    private Object getValueOfArgument(ArgumentMetaData argData)
     throws Exception{
        if(argData.isNullValue()){
            return null;
        }
        Object value = argData.getValue();
        if(value instanceof ServiceRefMetaData){
            return getValueOfServiceRef((ServiceRefMetaData)value);
        }else if(value instanceof ObjectMetaData){
            return getValueOfObject((ObjectMetaData)value);
        }else if(value instanceof StaticInvokeMetaData){
            return callInvoke(null, null, (StaticInvokeMetaData)value);
        }else if(value instanceof StaticFieldRefMetaData){
            return getStaticFieldValue((StaticFieldRefMetaData)value);
        }else{
            Class type = null;
            if(argData.getValueType() != null){
                type = Utility.convertStringToClass(argData.getValueType());
            }else if(argData.getType() != null){
                type = Utility.convertStringToClass(argData.getType());
            }else{
                type = String.class;
            }
            return getValueOfText(
                argData.getParentObjectMetaData(),
                type,
                (String)value
            );
        }
    }
    
    private Object getValueOfTarget(InvokeMetaData invokeData)
     throws Exception{
        MetaData targetData = invokeData.getTarget();
        if(targetData == null){
            return null;
        }
        if(targetData instanceof ServiceRefMetaData){
            return getValueOfServiceRef((ServiceRefMetaData)targetData);
        }else if(targetData instanceof ObjectMetaData){
            return getValueOfObject((ObjectMetaData)targetData);
        }else if(targetData instanceof StaticInvokeMetaData){
            return callInvoke(null, null, (StaticInvokeMetaData)targetData);
        }else if(targetData instanceof StaticFieldRefMetaData){
            return getStaticFieldValue((StaticFieldRefMetaData)targetData);
        }else if(targetData instanceof InvokeMetaData){
            return callInvoke(
                getValueOfTarget((InvokeMetaData)targetData),
                null,
                (InvokeMetaData)targetData
            );
        }
        return null;
    }
    
    private Object instanciateObject(ObjectMetaData objData) throws Exception{
        Object value = null;
        final Class clazz = Utility.convertStringToClass(objData.getCode());
        if((objData instanceof ServiceMetaData)
            && ((ServiceMetaData)objData).isFactory()){
            final ServiceMetaData serviceData = (ServiceMetaData)objData;
            final GenericsFactoryServiceProxy proxy
                 = new GenericsFactoryServiceProxy(
                    clazz,
                    serviceData.isManagement()
                 );
            if(ServiceMetaData.INSTANCE_TYPE_THREADLOCAL
                .equals(serviceData.getInstance())){
                proxy.setThreadLocal(true);
            }
            proxy.setCreateTemplateOnStart(serviceData.isCreateTemplate());
            value = proxy;
        }else{
            
            final ConstructorMetaData constData = objData.getConstructor();
            if(constData == null){
                if(clazz.isArray()){
                    final Class elementType = clazz.getComponentType();
                    value = Array.newInstance(elementType, 0);
                }else{
                    value = clazz.newInstance();
                }
            }else{
                if(constData.getInvoke() != null){
                    InvokeMetaData invoke = constData.getInvoke();
                    Object target = getValueOfTarget(invoke);
                    value = callInvoke(target, null, invoke);
                    if(value == null){
                        final MessageRecordFactory message
                             = getMessageRecordFactory();
                        throw new InstantiationException(
                            message.findEmbedMessage(
                                SVCM_00015,
                                new Object[]{
                                    target == null
                                         ? null : target.getClass().getName(),
                                    invoke.getName()
                                }
                            )
                        );
                    }
                }else if(constData.getStaticInvoke() != null){
                    StaticInvokeMetaData invoke = constData.getStaticInvoke();
                    value = callInvoke(null, null, invoke);
                    if(value == null){
                        final MessageRecordFactory message
                             = getMessageRecordFactory();
                        throw new InstantiationException(
                            message.findEmbedMessage(
                                SVCM_00015,
                                new Object[]{invoke.getCode(), invoke.getName()}
                            )
                        );
                    }
                }else if(constData.getStaticFieldRef() != null){
                    StaticFieldRefMetaData field
                         = constData.getStaticFieldRef();
                    value = getStaticFieldValue(field);
                    if(value == null){
                        final MessageRecordFactory message
                             = getMessageRecordFactory();
                        throw new InstantiationException(
                            message.findEmbedMessage(
                                SVCM_00016,
                                new Object[]{field.getCode(), field.getName()}
                            )
                        );
                    }
                }else if(clazz.isArray()){
                    final Class elementType = clazz.getComponentType();
                    final Collection argCollection = constData.getArguments();
                    Object argVals = Array.newInstance(
                        elementType,
                        argCollection.size()
                    );
                    final Iterator args = argCollection.iterator();
                    int i = 0;
                    while(args.hasNext()){
                        final ArgumentMetaData argData
                             = (ArgumentMetaData)args.next();
                        Array.set(argVals, i, getValueOfArgument(argData));
                        i++;
                    }
                    value = argVals;
                }else{
                    final Collection argCollection = constData.getArguments();
                    final Class[] argTypes = new Class[argCollection.size()];
                    final Object[] argVals = new Object[argCollection.size()];
                    final Iterator args = argCollection.iterator();
                    int i = 0;
                    while(args.hasNext()){
                        final ArgumentMetaData argData
                             = (ArgumentMetaData)args.next();
                        argVals[i] = getValueOfArgument(argData);
                        if(argData.getType() != null){
                            argTypes[i] = Utility.convertStringToClass(
                                argData.getType()
                            );
                        }else if(argVals[i] != null){
                            argTypes[i] = argVals[i].getClass();
                        }else{
                            argTypes[i] = String.class;
                        }
                        i++;
                    }
                    final Constructor constructor
                         = clazz.getConstructor(argTypes);
                    value = constructor.newInstance(argVals);
                }
            }
        }
        return value;
    }
    
    private Object getValueOfObject(ObjectMetaData objData) throws Exception{
        Object value = instanciateObject(objData);
        setFields(value, objData, CREATED);
        setAttributes(value, objData, CREATED);
        callInvokes(value, objData);
        return value;
    }
    
    private Object getStaticFieldValue(StaticFieldRefMetaData refData) throws Exception{
        final Class clazz = Utility.convertStringToClass(refData.getCode());
        final Field field = clazz.getField(refData.getName());
        return field.get(null);
    }
    
    private Object getValueOfText(
        ObjectMetaData objData,
        Class type,
        String textValue
    ) throws Exception{
        final Logger logger = getLogger();
        
        // �V�X�e���v���p�e�B�̒u��
        textValue = Utility.replaceSystemProperty(textValue);
        // �T�[�r�X���[�_�\���v���p�e�B�̒u��
        textValue = Utility.replaceServiceLoderConfig(
            textValue,
            objData.getServiceLoader().getConfig()
        );
        // �}�l�[�W���v���p�e�B�̒u��
        textValue = Utility.replaceManagerProperty(
            this,
            textValue
        );
        // �T�[�o�v���p�e�B�̒u��
        textValue = Utility.replaceServerProperty(textValue);
        if(type == null || Object.class.equals(type)){
            return textValue;
        }
        
        final PropertyEditor editor
             = objData.getServiceLoader().findEditor(type);
        if(editor == null){
            logger.write(SVCM_00040, type);
            return null;
        }
        if(editor instanceof ServiceNameEditor){
            ((ServiceNameEditor)editor).setServiceManagerName(
                objData.getManagerName()
            );
        }else if(editor instanceof ServiceNameArrayEditor){
            ((ServiceNameArrayEditor)editor).setServiceManagerName(
                objData.getManagerName()
            );
        }else if(editor instanceof ServiceNameRefEditor){
            ((ServiceNameRefEditor)editor).setServiceManagerName(
                objData.getManagerName()
            );
        }else if(editor instanceof ServiceNameRefArrayEditor){
            ((ServiceNameRefArrayEditor)editor).setServiceManagerName(
                objData.getManagerName()
            );
        }
        editor.setAsText(textValue);
        return editor.getValue();
    }
    
    private void setFields(
        Object target,
        ObjectMetaData objData,
        int state
    ){
        if(objData == null){
            return;
        }
        final Iterator fields = objData.getFields().iterator();
        while(fields.hasNext()){
            final FieldMetaData field
                 = (FieldMetaData)fields.next();
            Object value = field.getValue();
            if(value != null){
                if(objData instanceof ServiceMetaData){
                    if(state == STARTING){
                        if(value instanceof ServiceRefMetaData){
                            setField(target, objData, field);
                        }
                    }else{
                        if(!(value instanceof ServiceRefMetaData)){
                            setField(target, objData, field);
                        }
                    }
                }else{
                    setField(target, objData, field);
                }
            }
        }
    }
    
    private void setField(
        Object target,
        ObjectMetaData objData,
        FieldMetaData field
    ){
        final Logger logger = getLogger();
        
        final String name = field.getName();
        
        final Class targetClazz = target.getClass();
        Field f = null;
        try{
            f = targetClazz.getField(name);
        }catch(NoSuchFieldException e){
            if(name.length() != 0 && Character.isUpperCase(name.charAt(0))){
                StringBuffer tmpName = new StringBuffer();
                tmpName.append(Character.toLowerCase(name.charAt(0)));
                if(name.length() > 1){
                    tmpName.append(name.substring(1));
                }
                try{
                    f = targetClazz.getField(tmpName.toString());
                }catch(NoSuchFieldException e2){
                }
            }
            if(f == null){
                if(objData instanceof ServiceMetaData){
                    ServiceMetaData serviceData = (ServiceMetaData)objData;
                    logger.write(
                        SVCM_00038,
                        new Object[]{
                            serviceData.getManager().getName(),
                            serviceData.getName(),
                            name
                        }
                    );
                }else{
                    logger.write(
                        SVCM_00037,
                        new Object[]{
                            objData.getCode(),
                            name
                        }
                    );
                }
                return;
            }
        }
        
        Object value = field.getValue();
        try{
            if(value instanceof ServiceRefMetaData){
                value = getValueOfServiceRef((ServiceRefMetaData)value);
            }else if(value instanceof ObjectMetaData){
                value = getValueOfObject((ObjectMetaData)value);
            }else if(value instanceof StaticInvokeMetaData){
                value = callInvoke(null, null, (StaticInvokeMetaData)value);
            }else if(value instanceof StaticFieldRefMetaData){
                value = getStaticFieldValue((StaticFieldRefMetaData)value);
            }else{
                Class type = null;
                if(field.getType() != null){
                    type = Utility.convertStringToClass(field.getType());
                }else{
                    type = f.getType();
                }
                if(type == null || Object.class.equals(type)){
                    type = String.class;
                }
                value = getValueOfText(objData, type, (String)value);
            }
        }catch(Exception e){
            if(objData instanceof ServiceMetaData){
                ServiceMetaData serviceData = (ServiceMetaData)objData;
                logger.write(
                    SVCM_00042,
                    new Object[]{
                        serviceData.getManager().getName(),
                        serviceData.getName(),
                        name,
                        value,
                    },
                    e
                );
            }else{
                logger.write(
                    SVCM_00041,
                    new Object[]{
                        objData.getCode(),
                        name,
                        value,
                    },
                    e
                );
            }
            return;
        }
        
        try{
            f.set(target, value);
            if(objData instanceof ServiceMetaData){
                ServiceMetaData serviceData = (ServiceMetaData)objData;
                logger.write(
                    SVCM_00045,
                    new Object[]{
                        serviceData.getManager().getName(),
                        serviceData.getName(),
                        name,
                        value
                    }
                );
            }else{
                logger.write(
                    SVCM_00044,
                    new Object[]{
                        objData.getCode(),
                        name,
                        value
                    }
                );
            }
        }catch(Exception e){
            if(objData instanceof ServiceMetaData){
                ServiceMetaData serviceData = (ServiceMetaData)objData;
                logger.write(
                    SVCM_00043,
                    new Object[]{
                        serviceData.getManager().getName(),
                        serviceData.getName(),
                        name,
                        value
                    },
                    e
                );
            }else{
                logger.write(
                    SVCM_00039,
                    new Object[]{
                        objData.getCode(),
                        name,
                        value
                    },
                    e
                );
            }
        }
    }
    
    private void callInvokes(
        Object target,
        ObjectMetaData objData,
        int state
    ){
        if(objData == null){
            return;
        }
        final Iterator invokes = objData.getInvokes().iterator();
        while(invokes.hasNext()){
            final InvokeMetaData invokeData = (InvokeMetaData)invokes.next();
            if(state == invokeData.getCallStateValue()){
                try{
                    callInvoke(target, objData, invokeData);
                }catch(Exception e){
                    // ��������
                }
            }
        }
    }
    
    private void callInvokes(
        Object target,
        ObjectMetaData objData
    ){
        if(objData == null){
            return;
        }
        final Iterator invokes = objData.getInvokes().iterator();
        while(invokes.hasNext()){
            try{
                callInvoke(target, objData, (InvokeMetaData)invokes.next());
            }catch(Exception e){
                // ��������
            }
        }
    }
    
    private boolean isAccessableClass(Class clazz){
        final int modifier = clazz.getModifiers();
        return Modifier.isPublic(modifier)
            || ((Modifier.isProtected(modifier)
                || (!Modifier.isPublic(modifier)
                    && !Modifier.isProtected(modifier)
                    && !Modifier.isPrivate(modifier)))
                && DefaultServiceManagerService.class.getPackage().equals(clazz.getPackage()));
    }
    
    private Object callInvoke(
        Object target,
        ObjectMetaData objData,
        InvokeMetaData invoke
    ) throws Exception{
        final Logger logger = getLogger();
        
        final String methodName = invoke.getName();
        
        Class clazz = null;
        Method method = null;
        Object[] argVals = null;
        try{
            if(invoke instanceof StaticInvokeMetaData){
                clazz = Utility.convertStringToClass(
                    ((StaticInvokeMetaData)invoke).getCode()
                );
            }else{
                if(target == null){
                    logger.write(
                        SVCM_00017,
                        new Object[]{invoke}
                    );
                    return null;
                }
                clazz = target.getClass();
            }
            final Collection argCollection = invoke.getArguments();
            Class[] argTypes = null;
            if(argCollection.size() == 0){
            }else{
                argTypes = new Class[argCollection.size()];
                argVals = new Object[argCollection.size()];
                final Iterator args = argCollection.iterator();
                int i = 0;
                while(args.hasNext()){
                    final ArgumentMetaData argData
                         = (ArgumentMetaData)args.next();
                    argVals[i] = getValueOfArgument(argData);
                    if(argData.getType() != null){
                        argTypes[i] = Utility.convertStringToClass(
                            argData.getType()
                        );
                    }else if(argVals[i] != null){
                        argTypes[i] = argVals[i].getClass();
                    }else{
                        argTypes[i] = String.class;
                    }
                    i++;
                }
            }
            if(!isAccessableClass(clazz)){
                final Class[] interfaces = clazz.getInterfaces();
                for(int i = 0; i < interfaces.length; i++){
                    if(isAccessableClass(interfaces[i])){
                        try{
                            method = interfaces[i].getMethod(
                                methodName,
                                argTypes
                            );
                            clazz = interfaces[i];
                            break;
                        }catch(NoSuchMethodException e){
                        }
                    }
                }
            }
            if(method == null){
                method = clazz.getMethod(methodName, argTypes);
            }
        }catch(Exception e){
            if(objData == null){
                logger.write(
                    SVCM_00019,
                    new Object[]{
                        clazz.getName(),
                        invoke
                    },
                    e
                );
            }else if(objData instanceof ServiceMetaData){
                ServiceMetaData serviceData = (ServiceMetaData)objData;
                logger.write(
                    SVCM_00020,
                    new Object[]{
                        serviceData.getManager().getName(),
                        serviceData.getName(),
                        invoke
                    },
                    e
                );
            }else{
                logger.write(
                    SVCM_00021,
                    new Object[]{
                        objData.getCode(),
                        invoke
                    },
                    e
                );
            }
            throw e;
        }
        try{
            return method.invoke(target, argVals);
        }catch(Exception e){
            if(objData == null){
                logger.write(
                    SVCM_00018,
                    new Object[]{
                        clazz.getName(),
                        method
                    },
                    e
                );
            }else if(objData instanceof ServiceMetaData){
                ServiceMetaData serviceData = (ServiceMetaData)objData;
                logger.write(
                    SVCM_00022,
                    new Object[]{
                        serviceData.getManager().getName(),
                        serviceData.getName(),
                        method
                    },
                    e
                );
            }else{
                logger.write(
                    SVCM_00023,
                    new Object[]{
                        objData.getCode(),
                        method
                    },
                    e
                );
            }
            throw e;
        }
    }
    
    private void setAttributes(
        Object target,
        ObjectMetaData objData,
        int state
    ){
        if(objData == null){
            return;
        }
        final Iterator attrs = objData.getAttributes().iterator();
        while(attrs.hasNext()){
            final AttributeMetaData attr
                 = (AttributeMetaData)attrs.next();
            Object value = attr.getValue();
            if(objData instanceof ServiceMetaData){
                if(state == STARTING){
                    if(value instanceof ServiceRefMetaData){
                        setAttribute(target, objData, attr);
                    }
                }else{
                    if(!(value instanceof ServiceRefMetaData)){
                        setAttribute(target, objData, attr);
                    }
                }
            }else{
                setAttribute(target, objData, attr);
            }
        }
    }
    
    private void setAttribute(
        Object target,
        ObjectMetaData objData,
        AttributeMetaData attr
    ){
        final String name = attr.getName();
        Object value = attr.getValue();
        Property prop = (Property)attributePropCache.get(attr);
        if(prop == null){
            try{
                prop = PropertyFactory.createProperty(name);
                attributePropCache.put(attr, prop);
            }catch(IllegalArgumentException e){
                if(objData instanceof ServiceMetaData){
                    ServiceMetaData serviceData = (ServiceMetaData)objData;
                    logger.write(
                        SVCM_00046,
                        new Object[]{
                            serviceData.getManager().getName(),
                            serviceData.getName(),
                            name
                        },
                        e
                    );
                }else{
                    logger.write(
                        SVCM_00047,
                        new Object[]{
                            objData.getCode(),
                            name
                        },
                        e
                    );
                }
                return;
            }
        }
        try{
            if(value != null){
                if(value instanceof ServiceRefMetaData){
                    value = getValueOfServiceRef((ServiceRefMetaData)value);
                }else if(value instanceof ObjectMetaData){
                    value = getValueOfObject((ObjectMetaData)value);
                }else if(value instanceof StaticInvokeMetaData){
                    value = callInvoke(null, null, (StaticInvokeMetaData)value);
                }else if(value instanceof StaticFieldRefMetaData){
                    value = getStaticFieldValue((StaticFieldRefMetaData)value);
                }else if(!(value instanceof org.w3c.dom.Element)){
                    Class type = null;
                    if(attr.getType() != null){
                        type = Utility.convertStringToClass(attr.getType());
                    }else{
                        try{
                            type = prop.getPropertyType(target);
                        }catch(NoSuchPropertyException e){
                        }
                    }
                    if(type == null || Object.class.equals(type)){
                        type = String.class;
                    }
                    value = getValueOfText(objData, type, (String)value);
                }
            }
        }catch(Exception e){
            if(objData instanceof ServiceMetaData){
                ServiceMetaData serviceData = (ServiceMetaData)objData;
                logger.write(
                    SVCM_00042,
                    new Object[]{
                        serviceData.getManager().getName(),
                        serviceData.getName(),
                        name,
                        value,
                    },
                    e
                );
            }else{
                logger.write(
                    SVCM_00041,
                    new Object[]{
                        objData.getCode(),
                        name,
                        value,
                    },
                    e
                );
            }
            return;
        }
        
        try{
            prop.setProperty(target, value);
            if(objData instanceof ServiceMetaData){
                ServiceMetaData serviceData = (ServiceMetaData)objData;
                logger.write(
                    SVCM_00045,
                    new Object[]{
                        serviceData.getManager().getName(),
                        serviceData.getName(),
                        name,
                        value
                    }
                );
            }else{
                logger.write(
                    SVCM_00044,
                    new Object[]{
                        objData.getCode(),
                        name,
                        value
                    }
                );
            }
        }catch(NoSuchPropertyException e){
            if(objData instanceof ServiceMetaData){
                ServiceMetaData serviceData = (ServiceMetaData)objData;
                logger.write(
                    SVCM_00038,
                    new Object[]{
                        serviceData.getManager().getName(),
                        serviceData.getName(),
                        name
                    }
                );
            }else{
                logger.write(
                    SVCM_00037,
                    new Object[]{
                        objData.getCode(),
                        name
                    }
                );
            }
        }catch(InvocationTargetException e){
            if(objData instanceof ServiceMetaData){
                ServiceMetaData serviceData = (ServiceMetaData)objData;
                logger.write(
                    SVCM_00043,
                    new Object[]{
                        serviceData.getManager().getName(),
                        serviceData.getName(),
                        name,
                        value
                    },
                    e.getTargetException()
                );
            }else{
                logger.write(
                    SVCM_00039,
                    new Object[]{
                        objData.getCode(),
                        name,
                        value
                    },
                    e.getTargetException()
                );
            }
        }
    }
    
    /**
     * �o�^��Ԃ��ύX���ꂽ�����Ď�����RegistrationListener��ǉ�����B<p>
     *
     * @param listener RegistrationListener�I�u�W�F�N�g
     */
    public void addRegistrationListener(RegistrationListener listener){
        if(!registrationListeners.contains(listener)){
            registrationListeners.add(listener);
        }
    }
    
    /**
     * �o�^��Ԃ��ύX���ꂽ�����Ď�����RegistrationListener���폜����B<p>
     *
     * @param listener RegistrationListener�I�u�W�F�N�g
     */
    public void removeRegistrationListener(
        RegistrationListener listener
    ){
        registrationListeners.remove(listener);
    }
    
    /**
     * Service���o�^���ꂽ����RegistrationListener�ɒʒm����B<p>
     * 
     * @param service �o�^���ꂽService
     * @see RegistrationListener
     * @see RegistrationEvent
     */
    protected void processRegisterd(Service service){
        final Iterator listeners
             = new ArrayList(registrationListeners).iterator();
        while(listeners.hasNext()){
            final RegistrationListener listener
                 = (RegistrationListener)listeners.next();
            listener.registered(new RegistrationEvent(service));
        }
    }
    
    /**
     * Service���폜���ꂽ����RegistrationListener�ɒʒm����B<p>
     * 
     * @param service �폜���ꂽService
     * @see RegistrationListener
     * @see RegistrationEvent
     */
    protected void processUnregisterd(Service service){
        final Iterator listeners
             = new ArrayList(registrationListeners).iterator();
        while(listeners.hasNext()){
            final RegistrationListener listener
                 = (RegistrationListener)listeners.next();
            listener.unregistered(new RegistrationEvent(service));
        }
    }
    
    // ServiceManager��JavaDoc
    public void addServiceStateListener(
        final String name,
        final ServiceStateListener listener
    ){
        if(isRegisteredService(name)){
            final ServiceStateBroadcaster broadcaster
                 = getServiceStateBroadcaster(name);
            if(broadcaster != null){
                broadcaster.addServiceStateListener(listener);
                return;
            }
        }
        addRegistrationListener(
            new RegistrationListener(){
                public void registered(RegistrationEvent e){
                    final Service service
                         = (Service)e.getRegistration();
                    if(!service.getServiceName().equals(name)){
                        return;
                    }
                    removeRegistrationListener(this);
                    addServiceStateListener(name, listener);
                }
                public void unregistered(RegistrationEvent e){
                }
            }
        );
    }
    
    // ServiceManager��JavaDoc
    public void removeServiceStateListener(
        String name,
        ServiceStateListener listener
    ){
        if(isRegisteredService(name)){
            final ServiceStateBroadcaster broadcaster
                 = getServiceStateBroadcaster(name);
            if(broadcaster != null){
                broadcaster.removeServiceStateListener(listener);
            }
        }
    }
}