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
package jp.ossc.nimbus.service.aop;

import java.io.*;
import java.util.Map;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.interceptor.MetricsInfo;

/**
 * {@link InterceptorChain}�̃f�t�H���g�����B<p>
 * ���݌Ăяo����Ă���C���^�[�Z�v�^�̏����C���X�^���X�ϐ��Ɋi�[����̂ŁA�X���b�h�Z�[�t�ł͂Ȃ��C���^�[�Z�v�^�`�F�[���ł���B<br>
 *
 * @author M.Takata
 */
public class DefaultInterceptorChain
 implements InterceptorChain, java.io.Serializable, Cloneable{
    
    private static final long serialVersionUID = 3689361711046717596L;
    
    /**
     * {@link InterceptorChainList}���́A���݂̏�������{@link Interceptor}�̃C���f�b�N�X�B<p>
     * �����l�́A-1�B
     */
    protected int currentIndex = -1;
    
    /**
     * �`�F�[������C���^�[�Z�v�^�̃��X�g�B<p>
     */
    protected transient InterceptorChainList interceptorChainList;
    
    /**
     * {@link InterceptorChainList}�C���^�t�F�[�X�����������T�[�r�X�̃T�[�r�X���B<p>
     */
    protected ServiceName interceptorChainListServiceName;
    
    /**
     * �{���̌Ăяo������Ăяo��Invoker�B<p>
     */
    protected transient Invoker invoker;
    
    /**
     * {@link Invoker}�C���^�t�F�[�X�����������T�[�r�X�̃T�[�r�X���B<p>
     */
    protected ServiceName invokerServiceName;
    
    /**
     * �L�[��{@link Interceptor}�܂���{@link Invoker}�A�l��{@link MetricsInfo}�̃}�b�v�B<p>
     */
    protected Map metricsInfos;
    
    /**
     * ���퉞����Ԃ����ꍇ�����������ԓ��̌v�Z���s�����ǂ����̃t���O�B<p>
     * �f�t�H���g��false
     */
    protected boolean isCalculateOnlyNormal;
    
    /**
     * ��̃C���^�[�Z�v�^�`�F�[���𐶐�����B<p>
     */
    public DefaultInterceptorChain(){}
    
    /**
     * �w�肳�ꂽ{@link InterceptorChainList}��{@link Invoker}�̃C���^�[�Z�v�^�`�F�[���𐶐�����B<p>
     *
     * @param list �`�F�[������C���^�[�Z�v�^�̃��X�g
     * @param invoker �{���̌Ăяo������Ăяo��Invoker
     */
    public DefaultInterceptorChain(InterceptorChainList list, Invoker invoker){
        setInterceptorChainList(list);
        setInvoker(invoker);
    }
    
    /**
     * �w�肳�ꂽ{@link InterceptorChainList}�T�[�r�X��{@link Invoker}�T�[�r�X�̃C���^�[�Z�v�^�`�F�[���𐶐�����B<p>
     *
     * @param listServiceName �`�F�[������C���^�[�Z�v�^�̃��X�gInterceptorChainList�T�[�r�X�̃T�[�r�X��
     * @param invokerServiceName �{���̌Ăяo������Ăяo��Invoker�T�[�r�X�̃T�[�r�X��
     */
    public DefaultInterceptorChain(
        ServiceName listServiceName,
        ServiceName invokerServiceName
    ){
        setInterceptorChainListServiceName(listServiceName);
        setInvokerServiceName(invokerServiceName);
    }
    
    /**
     * ���\���v���i�[����}�b�v��ݒ肷��B<p>
     *
     * @param infos ���\���v���i�[����}�b�v�B�L�[��{@link Interceptor}�܂���{@link Invoker}�A�l��{@link MetricsInfo}
     */
    public void setMetricsInfoMap(Map infos){
        metricsInfos = infos;
    }
    
    /**
     * ���\���v���i�[����}�b�v���擾����B<p>
     *
     * @return ���\���v���i�[����}�b�v�B�L�[��{@link Interceptor}�܂���{@link Invoker}�A�l��{@link MetricsInfo}
     */
    public Map getMetricsInfoMap(){
        return metricsInfos;
    }
    
    /**
     * ���퉞����Ԃ����ꍇ�����������ԓ��̌v�Z���s�����ǂ�����ݒ肷��B<p>
     * �f�t�H���g��false
     *
     * @param isCalc ���퉞����Ԃ����ꍇ�����������ԓ��̌v�Z���s���ꍇ�́Atrue
     */
    public void setCalculateOnlyNormal(boolean isCalc){
        isCalculateOnlyNormal = isCalc;
    }
    
    /**
     * ���퉞����Ԃ����ꍇ�����������ԓ��̌v�Z���s�����ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�́A���퉞����Ԃ����ꍇ�����������ԓ��̌v�Z���s��
     */
    public boolean isCalculateOnlyNormal(){
        return isCalculateOnlyNormal;
    }
    
    // InterceptorChain��JavaDoc
    public Object invokeNext(InvocationContext context) throws Throwable{
        final InterceptorChainList list = getInterceptorChainList();
        boolean isError = false;
        boolean isException = false;
        long start = 0;
        if(metricsInfos != null){
            start = System.currentTimeMillis();
        }
        if(list == null){
            final Invoker ivk = getInvoker();
            if(ivk != null){
                try{
                    return ivk.invoke(context);
                }catch(Exception e){
                    isException = true;
                    throw e;
                }catch(Error err){
                    isError = true;
                    throw err;
                }finally{
                    if(metricsInfos != null){
                        long end = System.currentTimeMillis();
                        synchronized(metricsInfos){
                            MetricsInfo metricsInfo = (MetricsInfo)metricsInfos.get(ivk);
                            if(metricsInfo == null){
                                metricsInfo = new MetricsInfo(
                                    createKey(ivk),
                                    isCalculateOnlyNormal
                                );
                                metricsInfos.put(ivk, metricsInfo);
                            }
                            metricsInfo.calculate(end - start, isException, isError);
                        }
                    }
                }
            }else{
                return null;
            }
        }
        int index = getCurrentInterceptorIndex();
        try{
            setCurrentInterceptorIndex(++index);
            final Interceptor interceptor = list.getInterceptor(context, index);
            if(interceptor != null){
                try{
                    return interceptor.invoke(context, this);
                }catch(Exception e){
                    isException = true;
                    throw e;
                }catch(Error err){
                    isError = true;
                    throw err;
                }finally{
                    if(metricsInfos != null){
                        long end = System.currentTimeMillis();
                        synchronized(metricsInfos){
                            MetricsInfo metricsInfo = (MetricsInfo)metricsInfos.get(interceptor);
                            if(metricsInfo == null){
                                metricsInfo = new MetricsInfo(
                                    createKey(interceptor),
                                    isCalculateOnlyNormal
                                );
                                metricsInfos.put(interceptor, metricsInfo);
                            }
                            metricsInfo.calculate(end - start, isException, isError);
                        }
                    }
                }
            }else{
                final Invoker ivk = getInvoker();
                if(ivk != null){
                    try{
                        return ivk.invoke(context);
                    }catch(Exception e){
                        isException = true;
                        throw e;
                    }catch(Error err){
                        isError = true;
                        throw err;
                    }finally{
                        if(metricsInfos != null){
                            long end = System.currentTimeMillis();
                            synchronized(metricsInfos){
                                MetricsInfo metricsInfo = (MetricsInfo)metricsInfos.get(ivk);
                                if(metricsInfo == null){
                                    metricsInfo = new MetricsInfo(
                                        createKey(ivk),
                                        isCalculateOnlyNormal
                                    );
                                    metricsInfos.put(ivk, metricsInfo);
                                }
                                metricsInfo.calculate(end - start, isException, isError);
                            }
                        }
                    }
                }else{
                    return null;
                }
            }
        }finally{
            setCurrentInterceptorIndex(--index);
        }
    }
    
    protected String createKey(Object target){
        if(target instanceof Service){
            Service service = (Service)target;
            if(service.getServiceNameObject() != null){
                return service.getServiceNameObject().toString();
            }else if(service.getServiceName() != null){
                return service.getServiceName().toString();
            }else{
                return service.toString();
            }
        }else{
            return target.toString();
        }
    }
    
    // InterceptorChain��JavaDoc
    public int getCurrentInterceptorIndex(){
        return currentIndex;
    }
    
    // InterceptorChain��JavaDoc
    public void setCurrentInterceptorIndex(int index){
        currentIndex = index;
    }
    
    // InterceptorChain��JavaDoc
    public InterceptorChainList getInterceptorChainList(){
        if(interceptorChainListServiceName != null){
            try{
                return (InterceptorChainList)ServiceManagerFactory
                        .getServiceObject(interceptorChainListServiceName);
            }catch(ServiceNotFoundException e){
            }
        }
        return interceptorChainList;
    }
    
    /**
     * ���̃C���^�[�Z�v�^�`�F�[�������C���^�[�Z�v�^�̃��X�g��ݒ肷��B<p>
     *
     * @param list ���̃C���^�[�Z�v�^�`�F�[�������C���^�[�Z�v�^�̃��X�g
     */
    public void setInterceptorChainList(InterceptorChainList list){
        if(interceptorChainList instanceof ServiceBase){
            interceptorChainListServiceName
                 = ((ServiceBase)list).getServiceNameObject();
        }else if(interceptorChainList instanceof Service){
            final Service service = (Service)list;
            if(service.getServiceManagerName() != null){
                interceptorChainListServiceName = new ServiceName(
                    service.getServiceManagerName(),
                    service.getServiceName()
                );
            }
        }
        if(interceptorChainListServiceName == null){
            interceptorChainList = list;
        }
    }
    
    /**
     * ���̃C���^�[�Z�v�^�`�F�[�������C���^�[�Z�v�^�̃��X�gInterceptorChainList�T�[�r�X�̃T�[�r�X����ݒ肷��B<p>
     *
     * @param name ���̃C���^�[�Z�v�^�`�F�[�������C���^�[�Z�v�^�̃��X�gInterceptorChainList�T�[�r�X�̃T�[�r�X��
     */
    public void setInterceptorChainListServiceName(ServiceName name){
        interceptorChainListServiceName = name;
    }
    
    // InterceptorChain��JavaDoc
    public Invoker getInvoker(){
        if(invokerServiceName != null){
            try{
                return (Invoker)ServiceManagerFactory
                    .getServiceObject(invokerServiceName);
            }catch(ServiceNotFoundException e){
            }
        }
        return invoker;
    }
    
    /**
     * �Ō�̌Ăяo�����s��Invoker��ݒ肷��B<p>
     *
     * @param invoker �Ō�̌Ăяo�����s��Invoker
     */
    public void setInvoker(Invoker invoker){
        if(invoker instanceof ServiceBase){
            invokerServiceName = ((ServiceBase)invoker).getServiceNameObject();
        }else if(invoker instanceof Service){
            final Service service = (Service)invoker;
            if(service.getServiceManagerName() != null){
                invokerServiceName = new ServiceName(
                    service.getServiceManagerName(),
                    service.getServiceName()
                );
            }
        }
        if(invokerServiceName == null){
            this.invoker = invoker;
        }
    }
    
    /**
     * �Ō�̌Ăяo�����s��Invoker�T�[�r�X�̃T�[�r�X����ݒ肷��B<p>
     *
     * @param name �Ō�̌Ăяo�����s��Invoker�T�[�r�X�̃T�[�r�X��
     */
    public void setInvokerServiceName(ServiceName name){
        this.invokerServiceName = name;
    }
    
    // InterceptorChain��JavaDoc
    public InterceptorChain cloneChain(){
        try{
            DefaultInterceptorChain clone = (DefaultInterceptorChain)clone();
            clone.currentIndex = -1;
            return clone;
        }catch(CloneNotSupportedException e){
            return null;
        }
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException{
        out.defaultWriteObject();
        if(interceptorChainListServiceName == null){
            out.writeObject(interceptorChainList);
        }
        if(invokerServiceName == null){
            out.writeObject(invoker);
        }
    }
    
    private void readObject(ObjectInputStream in)
     throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        if(interceptorChainListServiceName == null){
            interceptorChainList = (InterceptorChainList)in.readObject();
        }else{
            interceptorChainList = (InterceptorChainList)ServiceManagerFactory
                .getServiceObject(interceptorChainListServiceName);
        }
        if(invokerServiceName == null){
            invoker = (Invoker)in.readObject();
        }else{
            invoker = (Invoker)ServiceManagerFactory
                .getServiceObject(invokerServiceName);
        }
    }
}
