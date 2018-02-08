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
package jp.ossc.nimbus.service.aop.javassist;

import java.io.*;
import java.security.*;
import java.util.*;
import java.util.regex.*;

import javassist.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;

/**
 * Javassist���g���āA���\�b�h��{@link jp.ossc.nimbus.service.aop.InterceptorChainList InterceptorChainList}���A�X�y�N�g����T�[�r�X�B<p>
 * �Ⴆ�΁A�ȉ��̂悤��Sample�N���X������B<br>
 * Sample.java<br>
 * <pre>
 * package sample;
 * 
 * import java.util.Map;
 * import java.util.HashMap;
 * 
 * public class Sample{
 *     
 *     private Map values = new HashMap();
 *     
 *     public Integer getValue(String key) throws IllegalArgumentException{
 *         if(key == null){
 *             throw new IllegalArgumentException("key is null.");
 *         }
 *         return (Integer)values.get(key);
 *     }
 * }
 * </pre>
 * ����Sample�N���X��getValue(String)���\�b�h�ɁA���g���N�X�擾�C���^�[�Z�v�^�i{@link jp.ossc.nimbus.service.aop.interceptor.MethodMetricsInterceptorService MethodMetricsInterceptorService}�j�Ɠ����C���^�[�Z�v�^�i{@link jp.ossc.nimbus.service.aop.interceptor.MethodSynchronizeInterceptorService MethodSynchronizeInterceptorService}�j�����ݍ��ނ悤�ɃT�[�r�X��`���s���B<br>
 * aspect-service.xml<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="SampleAspect"
 *                  code="jp.ossc.nimbus.service.aop.javassist.MethodInterceptorAspectService"&gt;
 *             &lt;attribute name="TargetClassName"&gt;sample\.Sample&lt;/attribute&gt;
 *             &lt;attribute name="TargetMethodModifiers"&gt;public&lt;/attribute&gt;
 *             &lt;attribute name="TargetMethodName"&gt;getValue&lt;/attribute&gt;
 *             &lt;attribute name="TargetParameterTypes"&gt;java.lang.String&lt;/attribute&gt;
 *             &lt;attribute name="InterceptorChainListServiceName"&gt;#SampleInterceptorChainList&lt;/attribute&gt;
 *             &lt;depends&gt;SampleInterceptorChainList&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="SampleInterceptorChainList"
 *                  code="jp.ossc.nimbus.service.aop.DefaultInterceptorChainListService"&gt;
 *             &lt;attribute name="InterceptorServiceNames"&gt;
 *                 #MethodMetricsInterceptor
 *                 #MethodSynchronizeInterceptor
 *             &lt;/attribute&gt;
 *             &lt;depends&gt;MethodMetricsInterceptor&lt;/depends&gt;
 *             &lt;depends&gt;MethodSynchronizeInterceptor&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="MethodMetricsInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.MethodMetricsInterceptorService"/&gt;
 *         
 *         &lt;service name="MethodSynchronizeInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.MethodSynchronizeInterceptorService"/&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 * ���̃T�[�r�X���N�����ꂽ��ɁA{@link NimbusClassLoader}���o�R����Sample�N���X�����[�h����ƁA�ȉ��̂悤�ɃN���X�t�@�C����ҏW���ă��[�h����B<br>
 * �A�X�y�N�g���Sample.class�̃\�[�X�C���[�W<br>
 * <pre>
 * package sample;
 * 
 * import java.util.Map;
 * import java.util.HashMap;
 * 
 * import javassist.runtime.Desc;
 * 
 * import jp.ossc.nimbus.core.ServiceName;
 * import jp.ossc.nimbus.service.aop.DefaultThreadLocalInterceptorChain;
 * import jp.ossc.nimbus.service.aop.InterceptorChain;
 * import jp.ossc.nimbus.service.aop.javassist.WrappedMethodInvocationContext;
 * 
 * public class Sample{
 *     
 *     private Map values = new HashMap();
 *     
 *     private final InterceptorChain sample$Sample$getValue$nimbus_aop$InterceptorChain1506952540$nimbus_aop = new DefaultThreadLocalInterceptorChain(
 *         new ServiceName("Sample", "SampleInterceptorChainList"),
 *         new ServiceName("Sample", "SampleAspect")
 *     );
 *     
 *     public Integer getValue(String key) throws IllegalArgumentException{
 *         try{
 *             return (Integer)sample$Sample$getValue$nimbus_aop$InterceptorChain1506952540$nimbus_aop.invokeNext(
 *                 new WrappedMethodInvocationContext(
 *                     this,
 *                     getClass().getDeclaredMethod("getValue", Desc.getParams("(Ljava/lang/String;)Ljava/lang/Integer;")),
 *                     getClass().getDeclaredMethod("sample$Sample$getValue$nimbus_aop", Desc.getParams("(Ljava/lang/String;)Ljava/lang/Integer;")),
 *                     new Object[]{key}
 *                 )
 *              );
 *          }catch(IllegalArgumentException illegalargumentexception){
 *              throw exception;
 *          }catch(RuntimeException runtimeexception){
 *              throw runtimeexception;
 *          }catch(Error error){
 *              throw error;
 *          }catch(Throwable throwable){
 *              throwable.printStackTrace();
 *          }
 *          return (Integer)null;
 *     }
 *     
 *     public Integer sample$Sample$getValue$nimbus_aop(String key) throws IllegalArgumentException{
 *         if(key == null){
 *             throw new IllegalArgumentException("key is null.");
 *         }
 *         return (Integer)values.get(key);
 *     }
 * }
 * </pre>
 * �]���āAgetValue(String)���\�b�h���Ăяo���ƁA<br>
 * <ol>
 *     <li>�t�B�[���h�ɐ錾���ꂽ{@link InterceptorChain}���Ăяo�����B</li>
 *     <li>InterceptorChain�́A�T�[�r�X��"SampleInterceptorChainList"��{@link InterceptorChainList}�ɑ���"InterceptorServiceNames"�Őݒ肳�ꂽ{Interceptor}�T�[�r�X�������Ăяo���B</li>
 *     <li>InterceptorChain����A�T�[�r�X��"MethodMetricsInterceptor"�̃��g���N�X�擾�C���^�[�Z�v�^�i{@link jp.ossc.nimbus.service.aop.interceptor.MethodMetricsInterceptorService MethodMetricsInterceptorService}�j���Ăяo�����B
 *     <li>InterceptorChain����A�T�[�r�X��"MethodSynchronizeInterceptor"�̓����C���^�[�Z�v�^�i{@link jp.ossc.nimbus.service.aop.interceptor.MethodSynchronizeInterceptorService MethodSynchronizeInterceptorService}�j���Ăяo�����B</li>
 *     <li>InterceptorChain����A�T�[�r�X��"SampleAspect"��{@link MethodInterceptorAspectService}��{@link Invoker}�T�[�r�X�Ƃ��ČĂяo�����B</li>
 *     <li>MethodInterceptorAspectService�̑���"InvokerServiceName"���ݒ肳��Ă��Ȃ��̂ŁA�f�t�H���g��{@link WrappedMethodReflectionCallInvokerService}�������I�ɐ�������A{@link Invoker}�T�[�r�X�Ƃ��ČĂяo�����B</li>
 *     <li>Invoker���ASample�N���X��sample$Sample$getValue$nimbus_aop(String)���\�b�h���Ăяo���B</li>
 * </ol>
 * �̂悤�ȓ�������āA�{����getValue(String)���\�b�h�̏����̓�����A�o����Interceptor�����ݍ��ށB<br>
 * <p>
 * ���̃A�X�y�N�g�T�[�r�X�ɂ��N���X�t�@�C���̕ύX���s�����@�́A�ÓI�A�X�y�N�g�Ɠ��I�A�X�y�N�g��2�ʂ�p�ӂ���Ă���B<br>
 * <p>
 * �ÓI�A�X�y�N�g�́A{@link Compiler}���g���āASample�N���X�̃N���X�t�@�C�������O�ɃA�X�y�N�g�R���p�C�����Ă����B<br>
 * �A�X�y�N�g�R���p�C���̃R�}���h�ɂ́A�ȉ��̂悤�ɃA�X�y�N�g�̓��e���L�q������L�̃T�[�r�X��`�t�@�C��aspect-service.xml�ƁA�A�X�y�N�g����ΏۂƂȂ�Sample�N���X���w�肷��B<br>
 * <pre>
 * java -classpath .;javassist-3.0.jar;nimbus.jar jp.ossc.nimbus.service.aop.Compiler -servicepath aspect-service.xml sample.Sample
 * </pre>
 * <p>
 * ���I�A�X�y�N�g�́A��L�̃T�[�r�X��`�t�@�C��aspect-service.xml��Sample�N���X�����[�h����O�ɓǂݍ��݁A���̃T�[�r�X���N�����Ă����B<br>
 * ���̃T�[�r�X�́A�N�����ɁA{@link NimbusClassLoader}�Ɏ������g��{@link AspectTranslator}�Ƃ��ēo�^����B<br>
 * ���̂��߁ANimbusClassLoader���o�R����Sample�N���X�����[�h����ƁA���̃T�[�r�X�ɂ���ăN���X�t�@�C�������I�ɕύX�����B<br>
 *
 * @author M.Takata
 * @see Invoker
 * @see InterceptorChainList
 */
public class MethodInterceptorAspectService extends ServiceBase
 implements AspectTranslator, Invoker, MethodInterceptorAspectServiceMBean{
    
    private static final long serialVersionUID = 7705175831745377623L;
    
    private static final String MODIFIER_PUBLIC = "public";
    private static final String MODIFIER_PROTECTED = "protected";
    private static final String MODIFIER_DEFAULT = "default";
    private static final String MODIFIER_PRIVATE = "private";
    private static final String MODIFIER_FINAL = "final";
    private static final String MODIFIER_STATIC = "static";
    private static final String MODIFIER_SYNCHRONIZED = "synchronized";
    private static final String MODIFIER_ABSTRACT = "abstract";
    
    private static final String NOT_TRANSFORMABLE_SUFFIX1 = "$nimbus_aop";
    private static final String NOT_TRANSFORMABLE_SUFFIX2 = "$";
    private static final String NOT_TRANSFORMABLE_PREFIX1 = "access$";
    private static final String INTERCEPTOR_CHAIN_CLASS_NAME
         = InterceptorChain.class.getName();
    private static final String INTERCEPTOR_CHAIN_IMPL_CLASS_NAME
         = DefaultThreadLocalInterceptorChain.class.getName();
    private static final String INVOCATION_CONTEXT_CLASS_NAME
         = WrappedMethodInvocationContext.class.getName();
    private static final Random random = new Random();
    
    private Boolean isPublicClass;
    private Boolean isProtectedClass;
    private Boolean isDefaultClass;
    private Boolean isPrivateClass;
    private Boolean isFinalClass;
    private Boolean isStaticClass;
    private Boolean isAbstractClass;
    private String targetClassModifiers;
    
    private Boolean isPublicMethod;
    private Boolean isProtectedMethod;
    private Boolean isDefaultMethod;
    private Boolean isPrivateMethod;
    private Boolean isFinalMethod;
    private Boolean isStaticMethod;
    private Boolean isSynchronizedMethod;
    private String targetMethodModifiers;
    
    private String targetClassName;
    private String noTargetClassName;
    private String targetInstanceClassName;
    private String targetMethodName;
    private String[] targetParameterTypes;
    private ServiceName interceptorChainListServiceName;
    private ServiceName invokerServiceName;
    private WrappedMethodReflectionCallInvokerService defaultInvoker;
    private Invoker invoker;
    private String aspectKey;
    private boolean isRegisterVMClassLoader = true;
    private boolean isStaticCompile;
    
    private Map classBytes;
    private final Map classPools = new WeakHashMap();
    
    // MethodInterceptorAspectServiceMBean��JavaDoc
    public void setTargetClassModifiers(String modifiers){
        Boolean isPublicClass = null;
        Boolean isProtectedClass = null;
        Boolean isDefaultClass = null;
        Boolean isPrivateClass = null;
        Boolean isFinalClass = null;
        Boolean isStaticClass = null;
        Boolean isAbstractClass = null;
        if(modifiers != null && modifiers.length() != 0){
            final StringTokenizer tokens = new StringTokenizer(modifiers, " ");
            while(tokens.hasMoreTokens()){
                final String modifier = tokens.nextToken();
                if(modifier.endsWith(MODIFIER_PUBLIC)){
                    isPublicClass = modifier.charAt(0) == '!'
                         ? Boolean.FALSE : Boolean.TRUE;
                }else if(modifier.endsWith(MODIFIER_PROTECTED)){
                    isProtectedClass = modifier.charAt(0) == '!'
                         ? Boolean.FALSE : Boolean.TRUE;
                }else if(modifier.endsWith(MODIFIER_DEFAULT)){
                    isDefaultClass = modifier.charAt(0) == '!'
                         ? Boolean.FALSE : Boolean.TRUE;
                }else if(modifier.endsWith(MODIFIER_PRIVATE)){
                    isPrivateClass = modifier.charAt(0) == '!'
                         ? Boolean.FALSE : Boolean.TRUE;
                }else if(modifier.endsWith(MODIFIER_FINAL)){
                    isFinalClass = modifier.charAt(0) == '!'
                         ? Boolean.FALSE : Boolean.TRUE;
                }else if(modifier.endsWith(MODIFIER_STATIC)){
                    isStaticClass = modifier.charAt(0) == '!'
                         ? Boolean.FALSE : Boolean.TRUE;
                }else if(modifier.endsWith(MODIFIER_ABSTRACT)){
                    isAbstractClass = modifier.charAt(0) == '!'
                         ? Boolean.FALSE : Boolean.TRUE;
                }else{
                    throw new IllegalArgumentException(
                        "Invalid class modifier : " + modifier
                    );
                }
            }
        }
        targetClassModifiers = modifiers;
        this.isPublicClass = isPublicClass;
        this.isProtectedClass = isProtectedClass;
        this.isDefaultClass = isDefaultClass;
        this.isPrivateClass = isPrivateClass;
        this.isFinalClass = isFinalClass;
        this.isStaticClass = isStaticClass;
        this.isAbstractClass = isAbstractClass;
    }
    
    // MethodInterceptorAspectServiceMBean��JavaDoc
    public String getTargetClassModifiers(){
        return targetClassModifiers;
    }
    
    // MethodInterceptorAspectServiceMBean��JavaDoc
    public void setTargetClassName(String name){
        targetClassName = name;
    }
    
    // MethodInterceptorAspectServiceMBean��JavaDoc
    public String getTargetClassName(){
        return targetClassName;
    }
    
    // MethodInterceptorAspectServiceMBean��JavaDoc
    public void setNoTargetClassName(String name){
        noTargetClassName = name;
    }
    
    // MethodInterceptorAspectServiceMBean��JavaDoc
    public String getNoTargetClassName(){
        return noTargetClassName;
    }
    
    // MethodInterceptorAspectServiceMBean��JavaDoc
    public void setTargetInstanceClassName(String name){
        targetInstanceClassName = name;
    }
    
    // MethodInterceptorAspectServiceMBean��JavaDoc
    public String getTargetInstanceClassName(){
        return targetInstanceClassName;
    }
    
    // MethodInterceptorAspectServiceMBean��JavaDoc
    public void setStaticCompile(boolean isStatic){
        isStaticCompile = isStatic;
    }
    
    // MethodInterceptorAspectServiceMBean��JavaDoc
    public boolean isStaticCompile(){
        return isStaticCompile;
    }
    
    // MethodInterceptorAspectServiceMBean��JavaDoc
    public void setTargetMethodModifiers(String modifiers){
        Boolean isPublicMethod = null;
        Boolean isProtectedMethod = null;
        Boolean isDefaultMethod = null;
        Boolean isPrivateMethod = null;
        Boolean isFinalMethod = null;
        Boolean isStaticMethod = null;
        Boolean isSynchronizedMethod = null;
        if(modifiers != null && modifiers.length() != 0){
            final StringTokenizer tokens = new StringTokenizer(modifiers, " ");
            while(tokens.hasMoreTokens()){
                final String modifier = tokens.nextToken();
                if(modifier.endsWith(MODIFIER_PUBLIC)){
                    isPublicMethod = modifier.charAt(0) == '!'
                         ? Boolean.FALSE : Boolean.TRUE;
                }else if(modifier.endsWith(MODIFIER_PROTECTED)){
                    isProtectedMethod = modifier.charAt(0) == '!'
                         ? Boolean.FALSE : Boolean.TRUE;
                }else if(modifier.endsWith(MODIFIER_DEFAULT)){
                    isDefaultMethod = modifier.charAt(0) == '!'
                         ? Boolean.FALSE : Boolean.TRUE;
                }else if(modifier.endsWith(MODIFIER_PRIVATE)){
                    isPrivateMethod = modifier.charAt(0) == '!'
                         ? Boolean.FALSE : Boolean.TRUE;
                }else if(modifier.endsWith(MODIFIER_FINAL)){
                    isFinalMethod = modifier.charAt(0) == '!'
                         ? Boolean.FALSE : Boolean.TRUE;
                }else if(modifier.endsWith(MODIFIER_STATIC)){
                    isStaticMethod = modifier.charAt(0) == '!'
                         ? Boolean.FALSE : Boolean.TRUE;
                }else if(modifier.endsWith(MODIFIER_SYNCHRONIZED)){
                    isSynchronizedMethod = modifier.charAt(0) == '!'
                         ? Boolean.FALSE : Boolean.TRUE;
                }else{
                    throw new IllegalArgumentException(
                        "Invalid class modifier : " + modifier
                    );
                }
            }
        }
        targetMethodModifiers = modifiers;
        this.isPublicMethod = isPublicMethod;
        this.isProtectedMethod = isProtectedMethod;
        this.isDefaultMethod = isDefaultMethod;
        this.isPrivateMethod = isPrivateMethod;
        this.isFinalMethod = isFinalMethod;
        this.isStaticMethod = isStaticMethod;
        this.isSynchronizedMethod = isSynchronizedMethod;
    }
    
    // MethodInterceptorAspectServiceMBean��JavaDoc
    public String getTargetMethodModifiers(){
        return targetMethodModifiers;
    }
    
    // MethodInterceptorAspectServiceMBean��JavaDoc
    public void setTargetMethodName(String name){
        targetMethodName = name;
    }
    
    // MethodInterceptorAspectServiceMBean��JavaDoc
    public String getTargetMethodName(){
        return targetMethodName;
    }
    
    // MethodInterceptorAspectServiceMBean��JavaDoc
    public void setTargetParameterTypes(String[] paramTypes){
        targetParameterTypes = paramTypes;
    }
    
    // MethodInterceptorAspectServiceMBean��JavaDoc
    public String[] getTargetParameterTypes(){
        return targetParameterTypes;
    }
    
    // MethodInterceptorAspectServiceMBean��JavaDoc
    public void setInterceptorChainListServiceName(ServiceName name){
        interceptorChainListServiceName = name;
    }
    
    // MethodInterceptorAspectServiceMBean��JavaDoc
    public ServiceName getInterceptorChainListServiceName(){
        return interceptorChainListServiceName;
    }
    
    // MethodInterceptorAspectServiceMBean��JavaDoc
    public void setInvokerServiceName(ServiceName name){
        invokerServiceName = name;
    }
    
    // MethodInterceptorAspectServiceMBean��JavaDoc
    public ServiceName getInvokerServiceName(){
        return invokerServiceName;
    }
    
    // MethodInterceptorAspectServiceMBean��JavaDoc
    public void setRegisterVMClassLoader(boolean isRegister){
        isRegisterVMClassLoader = isRegister;
    }
    
    // MethodInterceptorAspectServiceMBean��JavaDoc
    public boolean isRegisterVMClassLoader(){
        return isRegisterVMClassLoader;
    }
    
    // MethodInterceptorAspectServiceMBean��JavaDoc
    public void setAspectKey(String key){
        aspectKey = key;
    }
    
    // MethodInterceptorAspectServiceMBean��JavaDoc
    public String getAspectKey(){
        if(aspectKey == null){
            return getServiceName();
        }else{
            return aspectKey;
        }
    }

	/**
     * �T�[�r�X�̐����������s���B<p>
     *
     * @exception Exception �T�[�r�X�̐��������Ɏ��s�����ꍇ
     */
    public void createService() throws Exception{
        classBytes = new WeakHashMap();
    }
    
    /**
     * �T�[�r�X�̊J�n�������s���B<p>
     * �����Őݒ肳�ꂽ�T�[�r�X�̎擾�A�y��{@link NimbusClassLoader}�ւ�{@link AspectTranslator}�Ƃ��Ă̓o�^���s���B<br>
     *
     * @exception Exception �����Őݒ肳�ꂽ�T�[�r�X�̎擾�Ɏ��s�����ꍇ
     */
    public void startService() throws Exception{
        if(!isStaticCompile){
            if(getInvokerServiceName() == null){
                if(getDefaultInvokerService() == null){
                    final WrappedMethodReflectionCallInvokerService defaultInvoker
                         = new WrappedMethodReflectionCallInvokerService();
                    defaultInvoker.create();
                    defaultInvoker.start();
                    setDefaultInvokerService(defaultInvoker);
                }else{
                    getDefaultInvokerService().start();
                }
                setInvokerService(getDefaultInvokerService());
            }else{
                setInvokerService((Invoker)ServiceManagerFactory
                        .getServiceObject(getInvokerServiceName())
                );
            }
        }
        if(isRegisterVMClassLoader){
            NimbusClassLoader.addVMAspectTranslator(this);
        }else{
            NimbusClassLoader.getInstance().addAspectTranslator(this);
        }
    }
    
    /**
     * �T�[�r�X�̒�~�������s���B<p>
     * {@link NimbusClassLoader}�����{@link AspectTranslator}�Ƃ��Ă̓o�^�������s���B<br>
     *
     * @exception Exception �T�[�r�X�̒�~�����Ɏ��s�����ꍇ
     */
    public void stopService() throws Exception{
        if(getDefaultInvokerService() != null && getInvokerService() == getDefaultInvokerService()){
            getDefaultInvokerService().stop();
        }
        NimbusClassLoader.removeVMAspectTranslator(this);
        NimbusClassLoader.getInstance().removeAspectTranslator(this);
        classBytes.clear();
    }
    
    /**
     * �T�[�r�X�̔j���������s���B<p>
     *
     * @exception Exception �T�[�r�X�̔j�������Ɏ��s�����ꍇ
     */
    public void destroyService() throws Exception{
        if(getInvokerService() == getDefaultInvokerService()
            && getDefaultInvokerService() != null){
            getDefaultInvokerService().destroy();
            setDefaultInvokerService(null);
        }
        classBytes = null;
    }
    
    private synchronized ClassPool getClassPool(){
        final ClassLoader contextLoader
             = Thread.currentThread().getContextClassLoader();
        if(classPools.containsKey(contextLoader)){
            return (ClassPool)classPools.get(contextLoader);
        }else{
            final ClassPool classPool = new ClassPool(ClassPool.getDefault());
            classPool.appendClassPath(new LoaderClassPath(contextLoader));
            classPools.put(contextLoader, classPool);
            return classPool;
        }
    }
    
    private Map getClassBytes(){
        final ClassLoader contextLoader
             = Thread.currentThread().getContextClassLoader();
        if(classBytes.containsKey(contextLoader)){
            return (Map)classBytes.get(contextLoader);
        }else{
            final Map classBytesMap = new HashMap();
            classBytes.put(contextLoader, classBytesMap);
            return classBytesMap;
        }
    }
    
    /**
     * �ΏۂƂȂ郁�\�b�h�Ɏw�肳�ꂽ{@link InterceptorChainList}���������ނ悤�ɃN���X�t�@�C����ϊ�����B<p>
     * �ΏۊO�̃N���X�̏ꍇ�́Anull��Ԃ��B<br>
     *
     * @param loader �N���X�t�@�C�������[�h����N���X���[�_
     * @param className �N���X��
     * @param domain �N���X�̃h���C��
     * @param bytecode �N���X�t�@�C���̃o�C�g�z��
     * @return �ϊ���̃N���X�t�@�C���̃o�C�g�z��B�ϊ��ΏۂłȂ��ꍇ�́Anull��Ԃ��B
     */
    public synchronized byte[] transform(
        ClassLoader loader,
        String className,
        ProtectionDomain domain,
        byte[] bytecode
    ){
        if(getState() != STARTED){
            return null;
        }
        if(targetClassName == null && targetInstanceClassName == null){
            return null;
        }
        if(targetClassName != null
             && !targetClassName.equals(className)
             && !Pattern.matches(targetClassName, className)){
            return null;
        }
        if(noTargetClassName != null
             && (noTargetClassName.equals(className)
             || Pattern.matches(noTargetClassName, className))){
            return null;
        }
        try{
            final ClassPool classPool = getClassPool();
            CtClass clazz = null;
            if(classPool.find(className) == null){
                final ClassPath classPath
                     = new ByteArrayClassPath(className, bytecode);
                classPool.insertClassPath(classPath);
                getClassBytes().put(className, bytecode);
                clazz = classPool.get(className);
            }else{
                clazz = classPool.get(className);
                if(getClassBytes().containsKey(className)
                    && !getClassBytes().get(className).equals(bytecode)){
                    clazz.detach();
                    clazz = classPool.get(className);
                }else{
                    getClassBytes().put(className, bytecode);
                    clazz.defrost();
                }
            }
            
            if(clazz.isInterface()){
                return null;
            }
            if(targetClassModifiers != null){
                final int modifiers = clazz.getModifiers();
                if((isPublicClass != null
                         && Modifier.isPublic(modifiers)
                             != isPublicClass.booleanValue())
                    || (isProtectedClass != null
                         && Modifier.isProtected(modifiers)
                             != isProtectedClass.booleanValue())
                    || (isDefaultClass != null
                         && (!Modifier.isPublic(modifiers)
                             && !Modifier.isProtected(modifiers)
                             && !Modifier.isPrivate(modifiers))
                             != isDefaultClass.booleanValue())
                    || (isPrivateClass != null
                         && Modifier.isPrivate(modifiers)
                             != isPrivateClass.booleanValue())
                    || (isFinalClass != null
                         && Modifier.isFinal(modifiers)
                             != isFinalClass.booleanValue())
                    || (isStaticClass != null
                         && Modifier.isStatic(modifiers)
                             != isStaticClass.booleanValue())
                    || (isAbstractClass != null
                         && Modifier.isAbstract(modifiers)
                             != isAbstractClass.booleanValue())
                ){
                    return null;
                }
            }
            
            if(targetInstanceClassName != null
                 && !clazz.subtypeOf(classPool.get(targetInstanceClassName)) 
                 && !targetInstanceClassName.equals(className)
            ){
                return null;
            }
            
            final CtClass transformed = addInterceptor(classPool, clazz);
/* �e�X�g�p�ɃN���X�t�@�C�����o�͂���
if(transformed != null){
    byte[] tmp = transformed.toBytecode();
    OutputStream os = null;
    try{
        os = new FileOutputStream("tmp/" + clazz.getName() + ".class");
        os.write(tmp);
    }catch(IOException e){
        e.printStackTrace();
    }finally{
        if(os != null){
            try{
                os.close();
            }catch(IOException e){
            }
        }
    }
}
*/
            return transformed == null ? null : transformed.toBytecode();
        }catch(CannotCompileException e){
            System.err.println("Can not compile! : " + className);
            e.printStackTrace();
            return null;
        }catch(NotFoundException e){
            e.printStackTrace();
            return null;
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * �w�肳�ꂽ�N���X�̑ΏۂƂȂ郁�\�b�h�Ɏw�肳�ꂽ{@link InterceptorChainList}���������ނ悤�ɃN���X�t�@�C����ϊ�����B<p>
     * �ΏۊO�̃N���X�̏ꍇ�́Anull��Ԃ��B<br>
     *
     * @param classPool Javasisst�̃N���X�v�[��
     * @param clazz Javasisst�̃N���X�I�u�W�F�N�g
     * @return �ϊ���̃N���X��\��Javasisst�̃N���X�I�u�W�F�N�g�B�ϊ��ΏۂłȂ��ꍇ�́Anull��Ԃ��B
     */
    protected CtClass addInterceptor(ClassPool classPool, CtClass clazz)
     throws NotFoundException, CannotCompileException{
        CtClass result = clazz;
        CtClass target = clazz;
        boolean isTransfer = false;
        Set overwrideSignatures = new HashSet();
        do{
            final CtMethod[] methods = target.getDeclaredMethods();
            for(int i = 0; i < methods.length; i++){
                final String signature
                     = methods[i].getName() + methods[i].getSignature();
                if(overwrideSignatures.contains(signature)){
                    continue;
                }
                overwrideSignatures.add(signature);
                if(!clazz.equals(target)){
                    final int mod = methods[i].getModifiers();
                    if(Modifier.isPrivate(mod)
                        || (!Modifier.isPublic(mod)
                         && !Modifier.isProtected(mod)
                         && !clazz.getPackageName().equals(target.getPackageName()))
                    ){
                        continue;
                    }
                }
                final CtClass tmp = addInterceptorForMethod(
                    classPool,
                    result,
                    methods[i]
                );
                if(tmp != null){
                    isTransfer = true;
                    result = tmp;
                }
            }
        }while((target = target.getSuperclass()) != null);
        return isTransfer ? result : null;
    }
    
    /**
     * �C���^�[�Z�v�^�����ݍ��ރ��\�b�h�����b�v���郁�\�b�h�̃��\�b�h���𐶐�����B<p>
     *
     * @param clazz Javasisst�̃N���X�I�u�W�F�N�g
     * @param className �N���X��
     * @param methodName �C���^�[�Z�v�^�����ݍ��ރ��\�b�h�̃��\�b�h��
     * @return �C���^�[�Z�v�^�����ݍ��ރ��\�b�h�����b�v���郁�\�b�h�̃��\�b�h��
     */
    protected String createWrappedMethodName(
        CtClass clazz,
        String className,
        String methodName
    ){
        final String name = className.replace('.', '$') + '$' + methodName
             + createRandomNumberString() + NOT_TRANSFORMABLE_SUFFIX1;
        CtClass tmpClazz = clazz;
        do{
            CtMethod[] methods = tmpClazz.getDeclaredMethods();
            for(int i = 0; i < methods.length; i++){
                if(methods[i].getName().equals(name)){
                    return createWrappedMethodName(clazz, className, methodName);
                }
            }
            try{
                tmpClazz = tmpClazz.getSuperclass();
            }catch(javassist.NotFoundException e){
                tmpClazz = null;
            }
        }while(tmpClazz != null);
        return name;
    }
    
    /**
     * �C���^�[�Z�v�^�����ݍ��ނ��߂�{@link InterceptorChain}���t�B�[���h�ɐ錾���邽�߂̃t�B�[���h���𐶐�����B<p>
     *
     * @param clazz Javasisst�̃N���X�I�u�W�F�N�g
     * @param className �N���X��
     * @param methodName �C���^�[�Z�v�^�����ݍ��ރ��\�b�h�̃��\�b�h��
     * @return  �C���^�[�Z�v�^�����ݍ��ނ��߂�{@link InterceptorChain}���t�B�[���h�ɐ錾���邽�߂̃t�B�[���h��
     */
    protected String createInterceptorChainFieldName(
        CtClass clazz,
        String className,
        String methodName
    ){
        final String name = className.replace('.', '$') + '$' + methodName
             + '$' +"InterceptorChain" + createRandomNumberString()
             + NOT_TRANSFORMABLE_SUFFIX1;
        try{
            clazz.getField(name);
        }catch(NotFoundException e){
            return name;
        }
        return createInterceptorChainFieldName(clazz, className, methodName);
    }
    
    private static String createRandomNumberString(){
        return String.valueOf(Math.abs(random.nextInt()));
    }
    
    /**
     * �w�肳�ꂽ���\�b�h���ΏۂƂȂ郁�\�b�h�̏ꍇ�Ɏw�肳�ꂽ{@link InterceptorChainList}���������ނ悤�ɃN���X�t�@�C����ϊ�����B<p>
     * �ΏۊO�̃��\�b�h�̏ꍇ�́Anull��Ԃ��B<br>
     *
     * @param classPool Javasisst�̃N���X�v�[��
     * @param clazz Javasisst�̃N���X�I�u�W�F�N�g
     * @param method Javasisst�̃��\�b�h�I�u�W�F�N�g
     * @return �ϊ���̃N���X��\��Javasisst�̃N���X�I�u�W�F�N�g�B�ϊ��ΏۂłȂ��ꍇ�́Anull��Ԃ��B
     */
    protected CtClass addInterceptorForMethod(
        ClassPool classPool,
        CtClass clazz,
        CtMethod method
    ) throws NotFoundException, CannotCompileException{
        final int modifiers = method.getModifiers();
        if(Modifier.isNative(modifiers) || Modifier.isAbstract(modifiers)){
            return null;
        }
        final String originalName = method.getName();
        if(originalName.endsWith(NOT_TRANSFORMABLE_SUFFIX1)
            || originalName.endsWith(NOT_TRANSFORMABLE_SUFFIX2)
            || originalName.startsWith(NOT_TRANSFORMABLE_PREFIX1)){
            return null;
        }
        if(targetMethodName != null
             && !targetMethodName.equals(originalName)
             && !Pattern.matches(targetMethodName, originalName)){
            return null;
        }
        if(targetMethodModifiers != null){
            if((isPublicMethod != null
                     && Modifier.isPublic(modifiers)
                         != isPublicMethod.booleanValue())
                || (isProtectedMethod != null
                     && Modifier.isProtected(modifiers)
                         != isProtectedMethod.booleanValue())
                || (isDefaultMethod != null
                     && (!Modifier.isPublic(modifiers)
                         && !Modifier.isProtected(modifiers)
                         && !Modifier.isPrivate(modifiers))
                         != isDefaultMethod.booleanValue())
                || (isPrivateMethod != null
                     && Modifier.isPrivate(modifiers)
                         != isPrivateMethod.booleanValue())
                || (isFinalMethod != null
                     && Modifier.isFinal(modifiers)
                         != isFinalMethod.booleanValue())
                || (isStaticMethod != null
                     && Modifier.isStatic(modifiers)
                         != isStaticMethod.booleanValue())
                || (isSynchronizedMethod != null
                     && Modifier.isAbstract(modifiers)
                         != isSynchronizedMethod.booleanValue())
            ){
                return null;
            }
        }
        if(targetParameterTypes != null){
            final CtClass[] paramTypes = method.getParameterTypes();
            if(paramTypes.length != targetParameterTypes.length){
                return null;
            }
            for(int i = 0; i < targetParameterTypes.length; i++){
                final String paramName = paramTypes[i].getName();
                if(!targetParameterTypes[i].equals(paramName)
                     && !Pattern.matches(targetParameterTypes[i], paramName)){
                    return null;
                }
            }
        }
        
        // Javassist 3.1�Ή�
        try{
            clazz.stopPruning(true);
        }catch(java.lang.NoSuchMethodError e){
            // Javassist 3.0�ł́A���̃��\�b�h�͂Ȃ��H�I
        }
        
        if(!Modifier.isPublic(clazz.getModifiers())){
            try{
                clazz.setModifiers(
                    (clazz.getModifiers() & (~(Modifier.PROTECTED | Modifier.PRIVATE)))
                         | Modifier.PUBLIC
                );
            }catch(RuntimeException e){
                // Javassist �s��i�H�j�Ή�
                // static�łȂ������N���X�̃A�N�Z�X�C���q��static�ɂȂ��Ă��܂�
                clazz.setModifiers(
                    (clazz.getModifiers() & (~(Modifier.PROTECTED | Modifier.PRIVATE | Modifier.STATIC)))
                         | Modifier.PUBLIC
                );
            }
        }
        
        final boolean isStatic = Modifier.isStatic(method.getModifiers());
        final CtClass decClass = method.getDeclaringClass();
        final boolean isDeclaredMethod
             = decClass.getName().equals(clazz.getName());
        final CtClass returnType = method.getReturnType();
        CtMethod callMethod = method;
        if(isDeclaredMethod){
            callMethod = clazz.getDeclaredMethod(
                method.getName(),
                method.getParameterTypes()
            );
            clazz.removeMethod(callMethod);
        }else{
            if(Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers)){
                return null;
            }
            
            callMethod = CtNewMethod.make(
                modifiers,
                method.getReturnType(),
                method.getName(),
                method.getParameterTypes(),
                method.getExceptionTypes(),
                null,
                clazz
            );
            callMethod.setBody(method, null);
            if(returnType.equals(CtClass.voidType)){
                callMethod.setBody("super." + method.getName() + "($$);");
            }else{
                callMethod.setBody("return super." + method.getName() + "($$);");
            }
        }
        
        final StringBuffer buf = new StringBuffer();
        buf.append("private static ");
        buf.append("final ").append(INTERCEPTOR_CHAIN_CLASS_NAME).append(' ');
        final String fieldName = createInterceptorChainFieldName(
            clazz,
            clazz.getName(),
            callMethod.getName()
        );
        buf.append(fieldName);
        buf.append(" = new ")
            .append(INTERCEPTOR_CHAIN_IMPL_CLASS_NAME);
        if(getInterceptorChainListServiceName() == null){
            buf.append("((jp.ossc.nimbus.core.ServiceName)null,");
        }else{
            buf.append("(new jp.ossc.nimbus.core.ServiceName(\"");
            buf.append(getInterceptorChainListServiceName()
                .getServiceManagerName()).append("\", \"");
            buf.append(getInterceptorChainListServiceName()
                .getServiceName()).append("\"),");
        }
        buf.append("new jp.ossc.nimbus.core.ServiceName(\"");
        if(getInvokerServiceName() == null){
            buf.append(getServiceManagerName()).append("\", \"");
            buf.append(getServiceName()).append("\"));");
        }else{
            buf.append(getInvokerServiceName()
                .getServiceManagerName()).append("\", \"");
            buf.append(getInvokerServiceName()
                .getServiceName()).append("\"));");
        }
        final CtField field = CtField.make(buf.toString(), clazz);
        clazz.addField(field);
        buf.setLength(0);
        
        final CtMethod wrappedMethod = CtNewMethod.make(
            (modifiers & (~(Modifier.PROTECTED | Modifier.PRIVATE)))
                 | Modifier.PUBLIC,
            callMethod.getReturnType(),
            callMethod.getName(),
            callMethod.getParameterTypes(),
            callMethod.getExceptionTypes(),
            null,
            clazz
        );
        callMethod.setBody(callMethod, null);
        final String wrappedName = createWrappedMethodName(
            clazz,
            clazz.getName(),
            method.getName()
        );
        callMethod.setName(wrappedName);
        clazz.addMethod(callMethod);
        if(!Modifier.isPublic(modifiers)){
            callMethod.setModifiers(
                (modifiers & (~(Modifier.PROTECTED | Modifier.PRIVATE)))
                     | Modifier.PUBLIC
            );
        }
        
        buf.append("try{");
        if(!returnType.equals(CtClass.voidType)){
            buf.append("return ($r)");
        }
        buf.append(fieldName).append(".invokeNext(new ");
        final CtClass invocationClass
             = classPool.get(INVOCATION_CONTEXT_CLASS_NAME);
        buf.append(invocationClass.getName()).append('(');
        if(isStatic){
            buf.append("null, ");
        }else{
            buf.append("$0, ");
        }
        buf.append(clazz.getName())
            .append(".class.getDeclaredMethod(\"").append(originalName)
            .append("\", ");
        addMethodSigniture(buf, callMethod).append("), ");
        buf.append(clazz.getName())
            .append(".class.getDeclaredMethod(\"").append(wrappedName)
            .append("\", ");
        addMethodSigniture(buf, callMethod).append("), $args));}");
        
        final CtClass[] exceptionTypes = callMethod.getExceptionTypes();
        for(int i = 0; i < exceptionTypes.length; i++){
            addCatch(buf, exceptionTypes[i], "throw $e;");
        }
        final CtClass runtimeExClass = classPool.get(
            java.lang.RuntimeException.class.getName()
        );
        addCatch(buf, runtimeExClass, "throw $e;");
        final CtClass errorClass = classPool.get(
            java.lang.Error.class.getName()
        );
        addCatch(buf, errorClass, "throw $e;");
        final CtClass thClass = classPool.get(
            java.lang.Throwable.class.getName()
        );
        if(returnType.equals(CtClass.voidType)){
            addCatch(buf, thClass, "$e.printStackTrace();return;");
        }else{
            if(returnType.isPrimitive()){
                if(returnType.equals(CtClass.booleanType)){
                    addCatch(
                        buf,
                        thClass,
                        "$e.printStackTrace();return false;"
                    );
                }else{
                    addCatch(
                        buf,
                        thClass,
                        "{$e.printStackTrace();return ("
                             + returnType.getName() + ")0;}"
                    );
                }
            }else{
                addCatch(
                    buf,
                    thClass,
                    "{$e.printStackTrace();return ("
                         + returnType.getName() + ")null;}"
                );
            }
        }
        final String wrappedName2 = createWrappedMethodName(
            clazz,
            clazz.getName(),
            method.getName()
        );
        wrappedMethod.setName(wrappedName2);
        wrappedMethod.setBody(buf.toString());
        clazz.addMethod(wrappedMethod);
        wrappedMethod.setName(originalName);
        
        return clazz;
    }
    
    private StringBuffer addCatch(StringBuffer buf, CtClass th, String logic){
        buf.append("catch(").append(th.getName());
        buf.append(" e").append(NOT_TRANSFORMABLE_SUFFIX1);
        String tmpLogic = logic;
        if(tmpLogic.indexOf("$e") != -1){
            final StringBuffer tmpBuf = new StringBuffer(logic);
            int index = logic.length();
            while((index = tmpBuf.lastIndexOf("$e", index - 1)) != -1){
                tmpBuf.replace(
                    index,
                    index + 2,
                    'e' + NOT_TRANSFORMABLE_SUFFIX1
                );
            }
            tmpLogic = tmpBuf.toString();
        }
        buf.append("){").append(tmpLogic).append("}");
        return buf;
    }
    
    private StringBuffer addMethodSigniture(StringBuffer buf, CtMethod method) throws NotFoundException{
        CtClass[] paramTypes = method.getParameterTypes();
        if(paramTypes == null || paramTypes.length == 0){
            buf.append("null");
        }else{
            buf.append("new Class[]{");
            for(int i = 0; i < paramTypes.length; i++){
                buf.append(paramTypes[i].getName()).append(".class");
                if(i != paramTypes.length - 1){
                    buf.append(',');
                }
            }
            buf.append('}');
        }
        return buf;
    }
    
    // Invoker��JavaDoc
    public Object invoke(InvocationContext context) throws Throwable{
        if(getInvokerService() == null){
            return null;
        }else{
            return getInvokerService().invoke(context);
        }
    }
    
    /**
     * {@link Invoker}�T�[�r�X��ݒ肷��B<p>
     *
     * @param invoker {@link Invoker}�T�[�r�X
     */
    protected void setInvokerService(Invoker invoker){
        this.invoker = invoker;
    }
    
    /**
     * {@link Invoker}�T�[�r�X���擾����B<p>
     *
     * @return {@link Invoker}�T�[�r�X
     */
    protected Invoker getInvokerService(){
        return invoker;
    }
    
    /**
     * �f�t�H���g��{@link Invoker}�T�[�r�X�ł���{@link WrappedMethodReflectionCallInvokerService}���擾����B<p>
     *
     * @return {@link WrappedMethodReflectionCallInvokerService}�I�u�W�F�N�g
     */
    protected WrappedMethodReflectionCallInvokerService getDefaultInvokerService(){
        return defaultInvoker;
    }
    
    /**
     * �f�t�H���g��{@link Invoker}�T�[�r�X�ł���{@link WrappedMethodReflectionCallInvokerService}��ݒ肷��B<p>
     *
     * @param invoker {@link WrappedMethodReflectionCallInvokerService}�I�u�W�F�N�g
     */
    protected void setDefaultInvokerService(
        WrappedMethodReflectionCallInvokerService invoker
    ){
        defaultInvoker = invoker;
    }
}