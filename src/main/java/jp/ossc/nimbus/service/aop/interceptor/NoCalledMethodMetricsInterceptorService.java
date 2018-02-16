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
package jp.ossc.nimbus.service.aop.interceptor;

import java.util.*;
import java.util.jar.*;
import java.util.zip.*;
import java.util.regex.*;
import java.io.*;
import java.lang.reflect.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.io.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.util.*;

/**
 * 未呼び出しメソッド統計インターセプタ。<p>
 * 統計対象のメソッドのうち、呼び出されなかったメソッドを取得するインターセプタである。<br>
 * 以下に、未呼び出しメソッド統計インターセプタのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="NoCalledMethodMetricsInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.NoCalledMethodMetricsInterceptorService"&gt;
 *             &lt;attribute name="TargetClassName"&gt;sample\..*&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class NoCalledMethodMetricsInterceptorService extends ServiceBase
 implements Interceptor, NoCalledMethodMetricsInterceptorServiceMBean{
    
    private static final long serialVersionUID = -1589746522650375741L;
    
    private static final String MODIFIER_PUBLIC = "public";
    private static final String MODIFIER_PROTECTED = "protected";
    private static final String MODIFIER_DEFAULT = "default";
    private static final String MODIFIER_PRIVATE = "private";
    private static final String MODIFIER_FINAL = "final";
    private static final String MODIFIER_STATIC = "static";
    private static final String MODIFIER_SYNCHRONIZED = "synchronized";
    private static final String MODIFIER_ABSTRACT = "abstract";
    
    private static final String NOT_TRANSFORMABLE_SUFFIX = "$nimbus_aop";
    
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
    private String targetInstanceClassName;
    private String targetMethodName;
    private String[] targetParameterTypes;
    private boolean isDeclaringMethod;
    private String[] additionalClassPaths;
    
    private Set targetMethods;
    private Set noCalledMethods;
    
    private boolean isOutputSystemOut = true;
    
    // NoCalledMethodMetricsInterceptorServiceMBeanのJavaDoc
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
    
    // NoCalledMethodMetricsInterceptorServiceMBeanのJavaDoc
    public String getTargetClassModifiers(){
        return targetClassModifiers;
    }
    
    // NoCalledMethodMetricsInterceptorServiceMBeanのJavaDoc
    public void setTargetClassName(String name){
        targetClassName = name;
    }
    
    // NoCalledMethodMetricsInterceptorServiceMBeanのJavaDoc
    public String getTargetClassName(){
        return targetClassName;
    }
    
    // NoCalledMethodMetricsInterceptorServiceMBeanのJavaDoc
    public void setTargetInstanceClassName(String name){
        targetInstanceClassName = name;
    }
    
    // NoCalledMethodMetricsInterceptorServiceMBeanのJavaDoc
    public String getTargetInstanceClassName(){
        return targetInstanceClassName;
    }
    
    // NoCalledMethodMetricsInterceptorServiceMBeanのJavaDoc
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
                        "Invalid method modifier : " + modifier
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
    
    // NoCalledMethodMetricsInterceptorServiceMBeanのJavaDoc
    public String getTargetMethodModifiers(){
        return targetMethodModifiers;
    }
    
    // NoCalledMethodMetricsInterceptorServiceMBeanのJavaDoc
    public void setTargetMethodName(String name){
        targetMethodName = name;
    }
    
    // NoCalledMethodMetricsInterceptorServiceMBeanのJavaDoc
    public String getTargetMethodName(){
        return targetMethodName;
    }
    
    // NoCalledMethodMetricsInterceptorServiceMBeanのJavaDoc
    public void setTargetParameterTypes(String[] paramTypes){
        targetParameterTypes = paramTypes;
    }
    
    // NoCalledMethodMetricsInterceptorServiceMBeanのJavaDoc
    public String[] getTargetParameterTypes(){
        return targetParameterTypes;
    }
    
    // NoCalledMethodMetricsInterceptorServiceMBeanのJavaDoc
    public void setDeclaringMethod(boolean isDeclaring){
        isDeclaringMethod = isDeclaring;
    }
    
    // NoCalledMethodMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isDeclaringMethod(){
        return isDeclaringMethod;
    }
    
    // NoCalledMethodMetricsInterceptorServiceMBeanのJavaDoc
    public Set getTargetMethodSet(){
        return getMethodSet(targetMethods);
    }
    
    // NoCalledMethodMetricsInterceptorServiceMBeanのJavaDoc
    public String getTargetMethodString(){
        return getMethodString(getTargetMethodSet());
    }
    
    // NoCalledMethodMetricsInterceptorServiceMBeanのJavaDoc
    public Set getNoCalledMethodSet(){
        return getMethodSet(noCalledMethods);
    }
    
    // NoCalledMethodMetricsInterceptorServiceMBeanのJavaDoc
    public String getNoCalledMethodString(){
        return getMethodString(getNoCalledMethodSet());
    }
    
    // NoCalledMethodMetricsInterceptorServiceMBeanのJavaDoc
    public void setClassPaths(String[] paths){
        additionalClassPaths = paths;
    }
    
    // NoCalledMethodMetricsInterceptorServiceMBeanのJavaDoc
    public String[] getClassPaths(){
        return additionalClassPaths;
    }
    
    // NoCalledMethodMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputSystemOut(boolean isOutput){
        isOutputSystemOut = isOutput;
    }
    
    // NoCalledMethodMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isOutputSystemOut(){
        return isOutputSystemOut;
    }
    
    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception{
        targetMethods = new HashSet();
        noCalledMethods = new HashSet();
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception 属性で設定されたサービスの取得に失敗した場合
     */
    public void startService() throws Exception{
        createTargetMethods();
        synchronized(noCalledMethods){
            noCalledMethods.addAll(targetMethods);
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        if(isOutputSystemOut){
            System.out.println("************ Target method ***********");
            System.out.println(getTargetMethodString());
            System.out.println("************ No called method ***********");
            System.out.println(getNoCalledMethodString());
        }
        targetMethods.clear();
        synchronized(noCalledMethods){
            noCalledMethods.clear();
        }
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception サービスの破棄処理に失敗した場合
     */
    public void destroyService() throws Exception{
        targetMethods = null;
        noCalledMethods = null;
    }
    
    private Set getMethodSet(Set methods){
        final Set result = new HashSet();
        if(methods == null || methods.size() == 0){
            return result;
        }
        final SerializableMethod[] serializableMethods
             = (SerializableMethod[])methods.toArray(
                new SerializableMethod[methods.size()]
            );
        for(int i = 0; i < serializableMethods.length; i++){
            result.add(serializableMethods[i].getMethod());
        }
        return result;
    }
    
    private String getMethodString(Set methodSet){
        final Method[] methods
             = (Method[])methodSet.toArray(new Method[methodSet.size()]);
        final String[] methodStrs = new String[methods.length];
        final String separator = System.getProperty("line.separator");
        final StringBuilder buf = new StringBuilder();
        for(int i = 0; i < methods.length; i++){
            buf.setLength(0);
            final Class decClass = methods[i].getDeclaringClass();
            final String methodName = methods[i].getName();
            final Class[] paramTypes = methods[i].getParameterTypes();
            buf.append(decClass.getName()).append('.');
            buf.append(methodName).append('(');
            for(int j = 0; j < paramTypes.length; j++){
                buf.append(paramTypes[j].getName());
                if(j != paramTypes.length - 1){
                    buf.append(',');
                }
            }
            buf.append(')');
            methodStrs[i] = buf.toString();
        }
        Arrays.sort(methodStrs);
        buf.setLength(0);
        for(int i = 0; i < methodStrs.length; i++){
            buf.append(methodStrs[i]);
            if(i != methodStrs.length - 1){
                buf.append(separator);
            }
        }
        return buf.toString();
    }
    
    private synchronized void createTargetMethods() throws Exception{
        final String classpath = System.getProperty("java.class.path");
        if((classpath == null || classpath.length() == 0)
            && (additionalClassPaths == null
                 || additionalClassPaths.length == 0)
        ){
            return;
        }
        final String pathSeparator = System.getProperty("path.separator");
        final StringTokenizer tokens = new StringTokenizer(
            classpath,
            pathSeparator
        );
        final List classpaths = new ArrayList();
        while(tokens.hasMoreTokens()){
            final String cp = tokens.nextToken();
            if(cp.length() == 0){
                continue;
            }
            if(classpaths.contains(cp)){
                continue;
            }
            classpaths.add(cp);
        }
        if(additionalClassPaths != null){
            final ServiceMetaData metaData = ServiceManagerFactory
                .getServiceMetaData(getServiceNameObject());
            File servicePathDir = null;
            if(metaData != null){
                final File servicePath = new File(
                    metaData.getServiceLoader().getServiceURL().getFile()
                );
                final File parentDir = servicePath.getParentFile();
                if(parentDir != null && parentDir.exists()){
                    servicePathDir = parentDir;
                }
            }
            for(int i = 0; i < additionalClassPaths.length; i++){
                File path = new File(additionalClassPaths[i]);
                if(path.exists()){
                    classpaths.add(additionalClassPaths[i]);
                }else if(servicePathDir != null){
                    path = new File(servicePathDir, additionalClassPaths[i]);
                    if(path.exists()){
                        classpaths.add(path.getCanonicalPath());
                    }
                }
            }
        }
        final Iterator paths = classpaths.iterator();
        while(paths.hasNext()){
            final String cp = (String)paths.next();
            createTargetMethods(cp);
        }
    }
    
    private void createTargetMethods(String classpath) throws Exception{
        final File path = new File(classpath);
        if(!path.exists()){
            return;
        }
        if(path.isDirectory()){
            final RecurciveSearchFile dir = new RecurciveSearchFile(classpath);
            final File[] classFiles = dir.listAllTreeFiles(
                new ExtentionFileFilter(".class")
            );
            for(int i = 0; i < classFiles.length; i++){
                if(classFiles[i].isDirectory()){
                    continue;
                }
                addTargetMethods(convertClassName(dir, classFiles[i]));
            }
        }else{
            final JarFile jarFile = new JarFile(path);
            final Enumeration jarEntries = jarFile.entries();
            while(jarEntries.hasMoreElements()){
                final ZipEntry jarEntry = (ZipEntry)jarEntries.nextElement();
                if(jarEntry.isDirectory()){
                    continue;
                }
                final String entryName = jarEntry.getName();
                if(!entryName.endsWith(".class")){
                    continue;
                }
                addTargetMethods(convertClassName(entryName));
            }
        }
    }
    
    private String convertClassName(File dir, File classFile){
        final String fileSeparator = System.getProperty("file.separator");
        final String dirPath = dir.getPath();
        String path = classFile.getPath();
        if(path.startsWith(dirPath)){
            path = path.substring(dirPath.length());
        }
        if(path.startsWith(fileSeparator)){
            path = path.substring(fileSeparator.length());
        }
        String result = path.substring(0, path.length() - 6);
        return StringOperator.replaceString(result, fileSeparator, ".");
    }
    
    private String convertClassName(String jarEntry){
        String result = jarEntry.substring(0, jarEntry.length() - 6);
        return result.replace('/', '.');
    }
    
    private boolean addTargetMethods(String className) throws Exception{
        if(NimbusClassLoader.isNonTranslatableClassName(className)){
            return false;
        }
        if(targetClassName != null
             && !targetClassName.equals(className)
             && !Pattern.matches(targetClassName, className)){
            return false;
        }
        final ClassLoader loader = NimbusClassLoader.getInstance();
        Class clazz = null;
        try{
            clazz = loader.loadClass(className);
        }catch(java.lang.IncompatibleClassChangeError e){
            return false;
        }catch(NoClassDefFoundError e){
            return false;
        }catch(UnsupportedClassVersionError e){
            return false;
        }catch(VerifyError e){
            return false;
        }
        
        if(clazz.isInterface()){
            return false;
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
                return false;
            }
        }
        
        if(targetInstanceClassName != null){
            final Class targetInstanceClass
                 = loader.loadClass(targetInstanceClassName);
            if(!targetInstanceClassName.equals(className)
                && !targetInstanceClass.isAssignableFrom(clazz)
            ){
                return false;
            }
        }
        
        Class target = clazz;
        Set overwrideSignatures = new HashSet();
        boolean result = false;
        do{
            if(NimbusClassLoader.isNonTranslatableClassName(target.getName())){
                if(isDeclaringMethod){
                    break;
                }else{
                    continue;
                }
            }
            Method[] methods = null;
            try{
                methods = target.getDeclaredMethods();
            }catch(NoClassDefFoundError e){
                if(isDeclaringMethod){
                    break;
                }else{
                    continue;
                }
            }catch(VerifyError e){
                if(isDeclaringMethod){
                    break;
                }else{
                    continue;
                }
            }
            final StringBuilder signatureBuf = new StringBuilder();
            for(int i = 0; i < methods.length; i++){
                final Method method = methods[i];
                if(targetMethods.contains(method)){
                    continue;
                }
                signatureBuf.setLength(0);
                signatureBuf.append(method.getName()).append('(');
                final Class[] paramTypes = method.getParameterTypes();
                for(int j = 0; j < paramTypes.length; j++){
                    signatureBuf.append(paramTypes[j].getName()).append(',');
                }
                signatureBuf.append(')');
                final String signature = signatureBuf.toString();
                if(overwrideSignatures.contains(signature)){
                    continue;
                }
                overwrideSignatures.add(signature);
                if(!clazz.equals(target)){
                    final int mod = method.getModifiers();
                    if(Modifier.isPrivate(mod)
                        || (!Modifier.isPublic(mod)
                         && !Modifier.isProtected(mod)
                         && !clazz.getPackage().getName()
                            .equals(target.getPackage().getName()))
                    ){
                        continue;
                    }
                }
                final int modifiers = method.getModifiers();
                if(Modifier.isNative(modifiers)
                     || Modifier.isAbstract(modifiers)){
                    continue;
                }
                final String methodName = method.getName();
                if(methodName.endsWith(NOT_TRANSFORMABLE_SUFFIX)){
                    continue;
                }
                if(targetMethodName != null
                     && !targetMethodName.equals(methodName)
                     && !Pattern.matches(targetMethodName, methodName)){
                    continue;
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
                             && Modifier.isSynchronized(modifiers)
                                 != isSynchronizedMethod.booleanValue())
                    ){
                        continue;
                    }
                }
                if(targetParameterTypes != null){
                    if(paramTypes.length != targetParameterTypes.length){
                        continue;
                    }
                    boolean isMatch = true;
                    for(int j = 0; j < targetParameterTypes.length; j++){
                        final String paramName = paramTypes[j].getName();
                        if(!targetParameterTypes[j].equals(paramName)
                             && !Pattern.matches(targetParameterTypes[j], paramName)){
                            isMatch = false;
                            break;
                        }
                    }
                    if(!isMatch){
                        continue;
                    }
                }
                result = true;
                targetMethods.add(new SerializableMethod(method));
            }
            if(isDeclaringMethod){
                break;
            }
        }while((target = target.getSuperclass()) != null);
        return result;
    }
    
    /**
     * 呼び出されたメソッドが統計対象のメソッドの場合は、呼び出されなかったメソッドリストから削除して、次のインターセプタを呼び出す。<p>
     * サービスが開始されていない場合は、次のインターセプタを呼び出す。<br>
     *
     * @param context 呼び出しのコンテキスト情報
     * @param chain 次のインターセプタを呼び出すためのチェーン
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合、またはこのインターセプタで任意の例外が発生した場合。但し、本来呼び出される処理がthrowしないRuntimeException以外の例外をthrowしても、呼び出し元には伝播されない。
     */
    public Object invoke(
        InvocationContext context,
        InterceptorChain chain
    ) throws Throwable{
        if(getState() == STARTED){
            final MethodInvocationContext ctx
                 = (MethodInvocationContext)context;
            final Method targetMethod = ctx.getTargetMethod();
            if(targetMethod != null){
                synchronized(noCalledMethods){
                    final Iterator methods = noCalledMethods.iterator();
                    while(methods.hasNext()){
                        final SerializableMethod method
                             = (SerializableMethod)methods.next();
                        if(method.equalsSignature(targetMethod)){
                            methods.remove();
                            break;
                        }
                    }
                }
            }
        }
        return chain.invokeNext(context);
    }
}
