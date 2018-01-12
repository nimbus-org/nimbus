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

import java.lang.reflect.*;
import java.io.*;

/**
 * サービスプロキシファクトリ。<p>
 * {@link ServiceManager}に登録されるサービスは、{@link Service}インタフェースを実装すべきである。しかし、既存のリソースをそのままサービスとして使用したい場合、Serviceインタフェースを実装していない。また、既存のリソースを継承してサービスを新規に作成する場合は、{@link ServiceBase}を継承する事ができない。そのような場合に、サービスとして登録したい任意のオブジェクトを、Serviceインタフェースを実装したプロキシでラップする事で、サービスとして使用可能にする。<br>
 * このクラスは、そのようなサービスのプロキシを作成するためのファクトリクラスである。<br>
 * 
 * @author M.Takata
 */
public class ServiceProxyFactory{
    
    private static final long serialVersionUID = 4530900231794013688L;
    
    private ServiceProxyFactory(){
    }
    
    /**
     * {@link ServiceBaseSupport}インタフェースを実装したクラスをラップするサービスプロキシを作成する。<p>
     *
     * @param support {@link ServiceBaseSupport}インタフェースを実装したクラスのインスタンス
     * @return サービスプロキシ
     */
    public static ServiceBase createServiceBaseProxy(
        ServiceBaseSupport support
    ) throws Exception{
        return new GenericsServiceProxy(support);
    }
    
    /**
     * {@link Service}、{@link ServiceBaseSupport}インタフェースを実装しない任意のクラスをラップするサービスプロキシを作成する。<p>
     *
     * @param obj サービスでない任意のクラスのインスタンス
     * @return サービスプロキシ
     */
    public static ServiceBase createServiceBaseProxy(
        final Object obj
    ) throws Exception{
        ServiceBaseSupport support = null;
        final Class[] interfaces = obj.getClass().getInterfaces();
        if(interfaces == null || interfaces.length == 0){
            support = new ServiceBaseSupportAdapter(obj);
        }else{
            final Class[] newInterfaces = new Class[interfaces.length + 2];
            newInterfaces[0] = ServiceBaseSupport.class;
            newInterfaces[1] = ServiceProxy.class;
            System.arraycopy(
                interfaces,
                0,
                newInterfaces,
                2,
                interfaces.length
            );
            support = (ServiceBaseSupport)Proxy.newProxyInstance(
                NimbusClassLoader.getInstance(),
                newInterfaces,
                new ServiceBaseSupportProxyHandler(obj)
            );
        }
        return createServiceBaseProxy(support);
    }
    
    private static class ServiceBaseSupportProxyHandler
     implements InvocationHandler, Serializable{
        
        private static final long serialVersionUID = 4726647102748613673L;
        
        private final static String GET_TARGET = "getTarget";
        
        private final Object targetObj;
        
        public ServiceBaseSupportProxyHandler(Object target){
            targetObj = target;
        }
        
        public Object invoke(
            Object proxy,
            Method method,
            Object[] args
        ) throws Throwable{
            if(GET_TARGET.equals(method.getName())
                && method.getParameterTypes().length == 0){
                return targetObj;
            }
            try{
                Method targetMethod = findTargetMethod(targetObj.getClass(), method);
                if(targetMethod == null){
                    return null;
                }
                return targetMethod.invoke(targetObj, args);
            }catch(InvocationTargetException e){
                throw e.getTargetException();
            }
        }
        
        private Method findTargetMethod(Class targetClass, Method method){
            Method targetMethod = null;
            if(isAccessableClass(targetClass)){
                try{
                    targetMethod = targetClass.getMethod(
                        method.getName(),
                        method.getParameterTypes()
                    );
                    if(Modifier.isNative(targetMethod.getModifiers())){
                        targetMethod = null;
                    }
                }catch(NoSuchMethodException e){
                    return null;
                }
            }
            if(targetMethod == null){
                final Class[] interfaces = targetClass.getInterfaces();
                for(int i = 0; i < interfaces.length; i++){
                    if(isAccessableClass(interfaces[i])){
                        targetMethod = findTargetMethod(interfaces[i], method);
                        if(targetMethod != null){
                            return targetMethod;
                        }
                    }
                }
                final Class superClass = targetClass.getSuperclass();
                if(superClass == null){
                    return null;
                }else{
                    return findTargetMethod(superClass, method);
                }
            }
            return targetMethod;
        }
        
        private boolean isAccessableClass(Class clazz){
            final int modifier = clazz.getModifiers();
            return Modifier.isPublic(modifier);
        }
        
        public Object getTarget(){
            return targetObj;
        }
    }
    
    private static class ServiceBaseSupportAdapter
     implements ServiceBaseSupport, ServiceProxy, Serializable{
        
        private static final long serialVersionUID = -6078404516516929266L;
        
        private final static String SET_SERVICEBASE = "setServiceBase";
        private final static String CREATE = "create";
        private final static String START = "start";
        private final static String STOP = "stop";
        private final static String DESTROY = "destroy";
        private final Method setServiceBase;
        private final Method create;
        private final Method start;
        private final Method stop;
        private final Method destroy;
        
        private final Object obj;
        
        public ServiceBaseSupportAdapter(Object obj){
            this.obj = obj;
            final Class clazz = obj.getClass();
            setServiceBase = findMethod(clazz, SET_SERVICEBASE, new Class[]{ServiceBase.class});
            create = findMethod(clazz, CREATE, null);
            start = findMethod(clazz, START, null);
            stop = findMethod(clazz, STOP, null);
            destroy = findMethod(clazz, DESTROY, null);
        }
        
        private Method findMethod(Class clazz, String name, Class[] params){
            try{
                return clazz.getMethod(name, params);
            }catch(Exception e){
                return null;
            }
        }
        
        public void setServiceBase(ServiceBase service){
            if(setServiceBase == null){
                return;
            }
            try{
                setServiceBase.invoke(obj, new Object[]{service});
            }catch(InvocationTargetException e){
                final Throwable th = e.getTargetException();
                if(th instanceof RuntimeException){
                    throw (RuntimeException)th;
                }else{
                    throw (Error)th;
                }
            }catch(IllegalAccessException e){
            }catch(IllegalArgumentException e){
            }
        }
        
        public void createService() throws Exception{
            if(create == null){
                return;
            }
            try{
                create.invoke(obj, (Object[])null);
            }catch(InvocationTargetException e){
                final Throwable th = e.getTargetException();
                if(th instanceof Exception){
                    throw (Exception)th;
                }else{
                    throw (Error)th;
                }
            }catch(Exception e){
                throw e;
            }
        }
        public void startService() throws Exception{
            if(start == null){
                return;
            }
            try{
                start.invoke(obj, (Object[])null);
            }catch(InvocationTargetException e){
                final Throwable th = e.getTargetException();
                if(th instanceof Exception){
                    throw (Exception)th;
                }else{
                    throw (Error)th;
                }
            }catch(Exception e){
                throw e;
            }
        }
        public void stopService() throws Exception{
            if(stop == null){
                return;
            }
            try{
                stop.invoke(obj, (Object[])null);
            }catch(InvocationTargetException e){
                final Throwable th = e.getTargetException();
                if(th instanceof Exception){
                    throw (Exception)th;
                }else{
                    throw (Error)th;
                }
            }catch(Exception e){
                throw e;
            }
        }
        public void destroyService() throws Exception{
            if(destroy == null){
                return;
            }
            try{
                destroy.invoke(obj, (Object[])null);
            }catch(InvocationTargetException e){
                final Throwable th = e.getTargetException();
                if(th instanceof Exception){
                    throw (Exception)th;
                }else{
                    throw (Error)th;
                }
            }catch(Exception e){
                throw e;
            }
        }
        public Object getTarget(){
            return obj;
        }
    }
}