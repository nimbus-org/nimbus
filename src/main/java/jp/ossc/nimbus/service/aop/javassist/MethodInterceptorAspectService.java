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
 * Javassistを使って、メソッドに{@link jp.ossc.nimbus.service.aop.InterceptorChainList InterceptorChainList}をアスペクトするサービス。<p>
 * 例えば、以下のようなSampleクラスがある。<br>
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
 * このSampleクラスのgetValue(String)メソッドに、メトリクス取得インターセプタ（{@link jp.ossc.nimbus.service.aop.interceptor.MethodMetricsInterceptorService MethodMetricsInterceptorService}）と同期インターセプタ（{@link jp.ossc.nimbus.service.aop.interceptor.MethodSynchronizeInterceptorService MethodSynchronizeInterceptorService}）を挟み込むようにサービス定義を行う。<br>
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
 * このサービスが起動された後に、{@link NimbusClassLoader}を経由してSampleクラスをロードすると、以下のようにクラスファイルを編集してロードする。<br>
 * アスペクト後のSample.classのソースイメージ<br>
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
 * 従って、getValue(String)メソッドを呼び出すと、<br>
 * <ol>
 *     <li>フィールドに宣言された{@link InterceptorChain}が呼び出される。</li>
 *     <li>InterceptorChainは、サービス名"SampleInterceptorChainList"の{@link InterceptorChainList}に属性"InterceptorServiceNames"で設定された{Interceptor}サービスを順次呼び出す。</li>
 *     <li>InterceptorChainから、サービス名"MethodMetricsInterceptor"のメトリクス取得インターセプタ（{@link jp.ossc.nimbus.service.aop.interceptor.MethodMetricsInterceptorService MethodMetricsInterceptorService}）が呼び出される。
 *     <li>InterceptorChainから、サービス名"MethodSynchronizeInterceptor"の同期インターセプタ（{@link jp.ossc.nimbus.service.aop.interceptor.MethodSynchronizeInterceptorService MethodSynchronizeInterceptorService}）が呼び出される。</li>
 *     <li>InterceptorChainから、サービス名"SampleAspect"の{@link MethodInterceptorAspectService}が{@link Invoker}サービスとして呼び出される。</li>
 *     <li>MethodInterceptorAspectServiceの属性"InvokerServiceName"が設定されていないので、デフォルトの{@link WrappedMethodReflectionCallInvokerService}が自動的に生成され、{@link Invoker}サービスとして呼び出される。</li>
 *     <li>Invokerが、Sampleクラスのsample$Sample$getValue$nimbus_aop(String)メソッドを呼び出す。</li>
 * </ol>
 * のような動作をして、本来のgetValue(String)メソッドの処理の入り口、出口にInterceptorを挟み込む。<br>
 * <p>
 * このアスペクトサービスによるクラスファイルの変更を行う方法は、静的アスペクトと動的アスペクトの2通り用意されている。<br>
 * <p>
 * 静的アスペクトは、{@link Compiler}を使って、Sampleクラスのクラスファイルを事前にアスペクトコンパイルしておく。<br>
 * アスペクトコンパイルのコマンドには、以下のようにアスペクトの内容を記述した上記のサービス定義ファイルaspect-service.xmlと、アスペクトする対象となるSampleクラスを指定する。<br>
 * <pre>
 * java -classpath .;javassist-3.0.jar;nimbus.jar jp.ossc.nimbus.service.aop.Compiler -servicepath aspect-service.xml sample.Sample
 * </pre>
 * <p>
 * 動的アスペクトは、上記のサービス定義ファイルaspect-service.xmlをSampleクラスをロードする前に読み込み、このサービスを起動しておく。<br>
 * このサービスは、起動時に、{@link NimbusClassLoader}に自分自身を{@link AspectTranslator}として登録する。<br>
 * そのため、NimbusClassLoaderを経由してSampleクラスをロードすると、このサービスによってクラスファイルが動的に変更される。<br>
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
    
    // MethodInterceptorAspectServiceMBeanのJavaDoc
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
    
    // MethodInterceptorAspectServiceMBeanのJavaDoc
    public String getTargetClassModifiers(){
        return targetClassModifiers;
    }
    
    // MethodInterceptorAspectServiceMBeanのJavaDoc
    public void setTargetClassName(String name){
        targetClassName = name;
    }
    
    // MethodInterceptorAspectServiceMBeanのJavaDoc
    public String getTargetClassName(){
        return targetClassName;
    }
    
    // MethodInterceptorAspectServiceMBeanのJavaDoc
    public void setNoTargetClassName(String name){
        noTargetClassName = name;
    }
    
    // MethodInterceptorAspectServiceMBeanのJavaDoc
    public String getNoTargetClassName(){
        return noTargetClassName;
    }
    
    // MethodInterceptorAspectServiceMBeanのJavaDoc
    public void setTargetInstanceClassName(String name){
        targetInstanceClassName = name;
    }
    
    // MethodInterceptorAspectServiceMBeanのJavaDoc
    public String getTargetInstanceClassName(){
        return targetInstanceClassName;
    }
    
    // MethodInterceptorAspectServiceMBeanのJavaDoc
    public void setStaticCompile(boolean isStatic){
        isStaticCompile = isStatic;
    }
    
    // MethodInterceptorAspectServiceMBeanのJavaDoc
    public boolean isStaticCompile(){
        return isStaticCompile;
    }
    
    // MethodInterceptorAspectServiceMBeanのJavaDoc
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
    
    // MethodInterceptorAspectServiceMBeanのJavaDoc
    public String getTargetMethodModifiers(){
        return targetMethodModifiers;
    }
    
    // MethodInterceptorAspectServiceMBeanのJavaDoc
    public void setTargetMethodName(String name){
        targetMethodName = name;
    }
    
    // MethodInterceptorAspectServiceMBeanのJavaDoc
    public String getTargetMethodName(){
        return targetMethodName;
    }
    
    // MethodInterceptorAspectServiceMBeanのJavaDoc
    public void setTargetParameterTypes(String[] paramTypes){
        targetParameterTypes = paramTypes;
    }
    
    // MethodInterceptorAspectServiceMBeanのJavaDoc
    public String[] getTargetParameterTypes(){
        return targetParameterTypes;
    }
    
    // MethodInterceptorAspectServiceMBeanのJavaDoc
    public void setInterceptorChainListServiceName(ServiceName name){
        interceptorChainListServiceName = name;
    }
    
    // MethodInterceptorAspectServiceMBeanのJavaDoc
    public ServiceName getInterceptorChainListServiceName(){
        return interceptorChainListServiceName;
    }
    
    // MethodInterceptorAspectServiceMBeanのJavaDoc
    public void setInvokerServiceName(ServiceName name){
        invokerServiceName = name;
    }
    
    // MethodInterceptorAspectServiceMBeanのJavaDoc
    public ServiceName getInvokerServiceName(){
        return invokerServiceName;
    }
    
    // MethodInterceptorAspectServiceMBeanのJavaDoc
    public void setRegisterVMClassLoader(boolean isRegister){
        isRegisterVMClassLoader = isRegister;
    }
    
    // MethodInterceptorAspectServiceMBeanのJavaDoc
    public boolean isRegisterVMClassLoader(){
        return isRegisterVMClassLoader;
    }
    
    // MethodInterceptorAspectServiceMBeanのJavaDoc
    public void setAspectKey(String key){
        aspectKey = key;
    }
    
    // MethodInterceptorAspectServiceMBeanのJavaDoc
    public String getAspectKey(){
        if(aspectKey == null){
            return getServiceName();
        }else{
            return aspectKey;
        }
    }

	/**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception{
        classBytes = new WeakHashMap();
    }
    
    /**
     * サービスの開始処理を行う。<p>
     * 属性で設定されたサービスの取得、及び{@link NimbusClassLoader}への{@link AspectTranslator}としての登録を行う。<br>
     *
     * @exception Exception 属性で設定されたサービスの取得に失敗した場合
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
     * サービスの停止処理を行う。<p>
     * {@link NimbusClassLoader}からの{@link AspectTranslator}としての登録解除を行う。<br>
     *
     * @exception Exception サービスの停止処理に失敗した場合
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
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception サービスの破棄処理に失敗した場合
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
     * 対象となるメソッドに指定された{@link InterceptorChainList}を差し込むようにクラスファイルを変換する。<p>
     * 対象外のクラスの場合は、nullを返す。<br>
     *
     * @param loader クラスファイルをロードするクラスローダ
     * @param className クラス名
     * @param domain クラスのドメイン
     * @param bytecode クラスファイルのバイト配列
     * @return 変換後のクラスファイルのバイト配列。変換対象でない場合は、nullを返す。
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
/* テスト用にクラスファイルを出力する
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
     * 指定されたクラスの対象となるメソッドに指定された{@link InterceptorChainList}を差し込むようにクラスファイルを変換する。<p>
     * 対象外のクラスの場合は、nullを返す。<br>
     *
     * @param classPool Javasisstのクラスプール
     * @param clazz Javasisstのクラスオブジェクト
     * @return 変換後のクラスを表すJavasisstのクラスオブジェクト。変換対象でない場合は、nullを返す。
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
     * インターセプタを挟み込むメソッドをラップするメソッドのメソッド名を生成する。<p>
     *
     * @param clazz Javasisstのクラスオブジェクト
     * @param className クラス名
     * @param methodName インターセプタを挟み込むメソッドのメソッド名
     * @return インターセプタを挟み込むメソッドをラップするメソッドのメソッド名
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
     * インターセプタを挟み込むための{@link InterceptorChain}をフィールドに宣言するためのフィールド名を生成する。<p>
     *
     * @param clazz Javasisstのクラスオブジェクト
     * @param className クラス名
     * @param methodName インターセプタを挟み込むメソッドのメソッド名
     * @return  インターセプタを挟み込むための{@link InterceptorChain}をフィールドに宣言するためのフィールド名
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
     * 指定されたメソッドが対象となるメソッドの場合に指定された{@link InterceptorChainList}を差し込むようにクラスファイルを変換する。<p>
     * 対象外のメソッドの場合は、nullを返す。<br>
     *
     * @param classPool Javasisstのクラスプール
     * @param clazz Javasisstのクラスオブジェクト
     * @param method Javasisstのメソッドオブジェクト
     * @return 変換後のクラスを表すJavasisstのクラスオブジェクト。変換対象でない場合は、nullを返す。
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
        
        // Javassist 3.1対応
        try{
            clazz.stopPruning(true);
        }catch(java.lang.NoSuchMethodError e){
            // Javassist 3.0では、このメソッドはない？！
        }
        
        if(!Modifier.isPublic(clazz.getModifiers())){
            try{
                clazz.setModifiers(
                    (clazz.getModifiers() & (~(Modifier.PROTECTED | Modifier.PRIVATE)))
                         | Modifier.PUBLIC
                );
            }catch(RuntimeException e){
                // Javassist 不具合（？）対応
                // staticでない内部クラスのアクセス修飾子がstaticになってしまう
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
    
    // InvokerのJavaDoc
    public Object invoke(InvocationContext context) throws Throwable{
        if(getInvokerService() == null){
            return null;
        }else{
            return getInvokerService().invoke(context);
        }
    }
    
    /**
     * {@link Invoker}サービスを設定する。<p>
     *
     * @param invoker {@link Invoker}サービス
     */
    protected void setInvokerService(Invoker invoker){
        this.invoker = invoker;
    }
    
    /**
     * {@link Invoker}サービスを取得する。<p>
     *
     * @return {@link Invoker}サービス
     */
    protected Invoker getInvokerService(){
        return invoker;
    }
    
    /**
     * デフォルトの{@link Invoker}サービスである{@link WrappedMethodReflectionCallInvokerService}を取得する。<p>
     *
     * @return {@link WrappedMethodReflectionCallInvokerService}オブジェクト
     */
    protected WrappedMethodReflectionCallInvokerService getDefaultInvokerService(){
        return defaultInvoker;
    }
    
    /**
     * デフォルトの{@link Invoker}サービスである{@link WrappedMethodReflectionCallInvokerService}を設定する。<p>
     *
     * @param invoker {@link WrappedMethodReflectionCallInvokerService}オブジェクト
     */
    protected void setDefaultInvokerService(
        WrappedMethodReflectionCallInvokerService invoker
    ){
        defaultInvoker = invoker;
    }
}