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
package jp.ossc.nimbus.service.test.stub.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;
import java.beans.PropertyEditor;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceLoader;
import jp.ossc.nimbus.core.ServiceMetaData;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.Utility;
import jp.ossc.nimbus.io.RecurciveSearchFile;
import jp.ossc.nimbus.beans.NimbusPropertyEditorManager;
import jp.ossc.nimbus.service.aop.Invoker;
import jp.ossc.nimbus.service.aop.InvocationContext;
import jp.ossc.nimbus.service.aop.MethodInvocationContext;
import jp.ossc.nimbus.service.interpreter.Interpreter;
import jp.ossc.nimbus.service.test.StubResourceManager;
import jp.ossc.nimbus.service.test.TestStub;
import jp.ossc.nimbus.util.converter.Converter;
import jp.ossc.nimbus.util.converter.StreamConverter;
import jp.ossc.nimbus.util.converter.BindingStreamConverter;
import jp.ossc.nimbus.util.converter.BindingConverter;
import jp.ossc.nimbus.util.converter.StringStreamConverter;
import jp.ossc.nimbus.util.converter.ConvertException;
import jp.ossc.nimbus.util.ClassMappingTree;

/**
 * リモートサービステストスタブ。<p>
 * {@link jp.ossc.nimbus.service.proxy.RemoteClientService RemoteClientService}に、{@link jp.ossc.nimbus.service.aop.Invoker Invoker}サービスとして設定する事で使用する。<br>
 * シナリオ開始の通知を受けると、{@link StubResourceManager}から、シナリオ単位でスタブのリソースをダウンロードし、配置する。<br>
 * テストケース開始の通知を受けると、テストケースIDを保持し、メソッド呼び出しに備える。<br>
 * メソッド呼び出しを受けると、そのテストケース内のリソースファイルから、メソッド呼び出しに合致するファイルを特定して、その内容に従って戻り値を応答する。また、同時にメソッド呼び出しの内容をメソッド呼び出しファイルとして出力する。<br>
 * リソースファイルのフォーマットは、以下。<br>
 * <pre>
 * methodSignature
 * argumentsCondition
 * sleep millsec
 * returnClass
 * pre-interpreter:start
 * script
 * pre-interpreter:end
 * post-interpreter:start
 * script
 * post-interpreter:end
 * argument[index]:start
 * argument
 * argument[index]:end
 * returnType
 * return
 * </pre>
 * methodSignatureは、呼び出されたメソッドのシグニチャを指定する。<br>
 * argumentsConditionは、メソッドの引数に対する条件式を指定する。argumentsConditionは、{@link Interpreter#evaluate(String,Map)}で評価され、引数の変数マップには、"args"で引数の配列が渡され、戻り値がtrueの場合、条件に合致したとみなされる。条件式でマッチングさせる必要がない場合は、空文字を指定する。<br>
 * sleepは、応答時間を調整するために、指定されたミリ秒の間スリープする場合に、指定する。必要がない場合は、この行は必要ない。<br>
 * returnClass 戻り値のクラス名を指定する。空文字を指定した場合は、メソッドの戻り値の型が適用される。<br>
 * pre-interpreter:startとpre-interpreter:endの行で挟んで、応答する戻り値を事前編集するスクリプトをscriptに指定できる。scriptは、{@link Interpreter#evaluate(String,Map)}で評価され、引数の変数マップには、"args"で引数の配列が、"ret"で戻り値が渡される。スクリプトを指定する必要がない場合は、この行は必要ない。<br>
 * post-interpreter:startとpost-interpreter:endの行で挟んで、応答する戻り値を事後編集するスクリプトをscriptに指定できる。scriptは、{@link Interpreter#evaluate(String,Map)}で評価され、引数の変数マップには、"args"で引数の配列が、"ret"で戻り値が渡される。スクリプトを指定する必要がない場合は、この行は必要ない。<br>
 * argument[index]:startとargument:endの行で挟んで、index番目の引数を、{@link #setReturnConverterServiceName}や{@link #setReturnConverter}で指定した{@link BindingConverter BindingConverter}や{@link BindingStreamConverter BindingStreamConverter}で変換する文字列をargumentとして指定する。引数を変換する必要がない場合は、この行は必要ない。<br>
 * returnTypeは、"text"、"interpreter"のいずれかを指定する。戻り値が必要ない場合は、この行以下は必要ない。<br>
 * returnは、returnTypeによって、記述方法が異なる。<br>
 * <ul>
 * <li>returnTypeが"text"の場合<br>任意の文字列を指定する。指定した文字列は、{@link #setReturnConverterServiceName}や{@link #setReturnConverter}で指定した{@link Converter Converter}で変換されて、戻り値となる。</li>
 * <li>returnTypeが"interpreter"の場合<br>応答する戻り値を生成または編集するスクリプトを指定する。スクリプトは、{@link Interpreter#evaluate(String,Map)}で評価され、引数の変数マップには、"args"で引数の配列が、"ret"で戻り値が渡される。</li>
 * </ul>
 * returnは、任意の文字列で指定する。<br>
 * <p>
 * 呼び出されたメソッドの引数を出力するファイルは、リソースファイルのファイル名に拡張子".cll"を付加したファイル名になる。<br>
 * ファイルには、戻り値のオブジェクトの文字列表現が出力される。<br>
 *
 * @author M.Takata
 */
public class RemoteServiceTestStubService extends ServiceBase implements TestStub, Invoker, RemoteServiceTestStubServiceMBean{
    
//    private static final long serialVersionUID = 356502420591126756L;
    
    protected final CallKey NOT_FOUND = new CallKey();
    
    protected String id;
    protected ServiceName stubResourceManagerServiceName;
    protected StubResourceManager stubResourceManager;
    protected ServiceName interpreterServiceName;
    protected Interpreter interpreter;
    protected File resourceDirectory;
    protected String fileEncoding;
    protected boolean isAllowRepeatCall;
    protected boolean isSafeMultithread = true;
    protected boolean isSaveCallFile = true;
    protected boolean isCacheReturn;
    protected Map argsConverterServiceNames;
    protected Map returnConverterServiceNames;
    
    protected Map returnMap;
    protected Map evidenceMap;
    protected String userId;
    protected String scenarioGroupId;
    protected String scenarioId;
    protected String testcaseId;
    protected Object lock = new String("lock");
    protected Map returnCacheMap;
    protected ClassMappingTree argsConverters = new ClassMappingTree();
    protected ClassMappingTree returnConverters = new ClassMappingTree();
    
    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }
    
    public void setFileEncoding(String encoding) {
        fileEncoding = encoding;
    }
    public String getFileEncoding() {
        return fileEncoding;
    }
    
    public void setStubResourceManagerServiceName(ServiceName name) {
        stubResourceManagerServiceName = name;
    }
    public ServiceName getStubResourceManagerServiceName() {
        return stubResourceManagerServiceName;
    }
    
    public void setInterpreterServiceName(ServiceName name) {
        interpreterServiceName = name;
    }
    public ServiceName getInterpreterServiceName() {
        return interpreterServiceName;
    }
    
    public void setResourceDirectory(File dir) {
        resourceDirectory = dir;
    }
    public File getResourceDirectory() {
        return resourceDirectory;
    }
    
    public boolean isAllowRepeatCall(){
        return isAllowRepeatCall;
    }
    public void setAllowRepeatRequest(boolean isAllow){
        isAllowRepeatCall = isAllow;
    }
    
    public boolean isSafeMultithread(){
        return isSafeMultithread;
    }
    public void setSafeMultithread(boolean isSafe){
        isSafeMultithread = isSafe;
    }
    
    public boolean isSaveCallFile(){
        return isSaveCallFile;
    }
    public void setSaveRequestFile(boolean isSave){
        isSaveCallFile = isSave;
    }
    
    public boolean isCacheReturn(){
        return isCacheReturn;
    }
    public void setCacheReturn(boolean isCache){
        isCacheReturn = isCache;
    }
    
    public void setArgumentsConverterServiceName(String className, ServiceName name){
        argsConverterServiceNames.put(className, name);
    }
    public ServiceName getArgumentsConverterServiceName(String className){
        return (ServiceName)argsConverterServiceNames.get(className);
    }
    public Map getArgumentsConverterServiceNames(){
        return new HashMap(argsConverterServiceNames);
    }
    
    public void setReturnConverterServiceName(String className, ServiceName name){
        returnConverterServiceNames.put(className, name);
    }
    public ServiceName getReturnConverterServiceName(String className){
        return (ServiceName)returnConverterServiceNames.get(className);
    }
    public Map getReturnConverterServiceNames(){
        return new HashMap(returnConverterServiceNames);
    }
    
    public void setStubResourceManager(StubResourceManager manager) {
        stubResourceManager = manager;
    }
    
    public void setInterpreter(Interpreter interpreter) {
        this.interpreter = interpreter;
    }
    
    public void setArgumentsConverter(String className, Converter converter) throws ClassNotFoundException{
        Class clazz = Utility.convertStringToClass((String)className, false);
        argsConverters.add(clazz, converter, true);
    }
    
    public void setReturnConverter(String className, Converter converter) throws ClassNotFoundException{
        Class clazz = Utility.convertStringToClass((String)className, false);
        returnConverters.add(clazz, converter, true);
    }
    
    public void createService() throws Exception {
        returnMap = Collections.synchronizedMap(new HashMap());
        evidenceMap = Collections.synchronizedMap(new HashMap());
        returnCacheMap = Collections.synchronizedMap(new HashMap());
        argsConverterServiceNames = new HashMap();
        returnConverterServiceNames = new HashMap();
    }
    
    public void startService() throws Exception {
        if(id == null){
            throw new IllegalArgumentException("Id is null.");
        }
        
        if(stubResourceManagerServiceName != null){
            stubResourceManager = (StubResourceManager)ServiceManagerFactory.getServiceObject(stubResourceManagerServiceName);
        }
        if(stubResourceManager == null){
            throw new IllegalArgumentException("StubResourceManager is null.");
        }
        if(interpreterServiceName != null){
            interpreter = (Interpreter)ServiceManagerFactory.getServiceObject(interpreterServiceName);
        }
        if(interpreter == null){
            throw new IllegalArgumentException("Interpreter is null.");
        }
        
        File serviceDefDir = null;
        if(getServiceNameObject() != null){
            ServiceMetaData metaData = ServiceManagerFactory.getServiceMetaData(getServiceNameObject());
            if(metaData != null){
                ServiceLoader loader = metaData.getServiceLoader();
                if(loader != null){
                    String filePath = loader.getServiceURL().getFile();
                    if(filePath != null){
                        serviceDefDir = new File(filePath).getParentFile();
                    }
                }
            }
        }
        if(resourceDirectory == null){
            resourceDirectory = serviceDefDir == null ? new File(id) : new File(serviceDefDir, id);
        }else if(!resourceDirectory.isAbsolute() && !resourceDirectory.exists() && serviceDefDir != null){
            resourceDirectory = new File(serviceDefDir, resourceDirectory.getPath());
        }
        if(!resourceDirectory.exists()){
            resourceDirectory.mkdirs();
        }
        if(argsConverterServiceNames.size() != 0){
            Iterator entries = argsConverterServiceNames.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                Class clazz = Utility.convertStringToClass((String)entry.getKey(), false);
                argsConverters.add(clazz, entry.getValue(), true);
            }
        }
    }
    
    public void stopService() throws Exception {
        cancelScenario();
    }
    
    public Object invoke(InvocationContext context) throws Throwable{
        if(isSafeMultithread){
            synchronized(lock){
                return invokeInternal(context);
            }
        }else{
            return invokeInternal(context);
        }
    }
    protected Object invokeInternal(InvocationContext context) throws Throwable{
        if(scenarioGroupId == null || scenarioId == null || testcaseId == null){
            throw new IllegalStateException("Testcase not started. scenarioGroupId=" + scenarioGroupId + ", scenarioId=" + scenarioId + ", testcaseId=" + testcaseId);
        }
        MethodInvocationContext methodCtx = (MethodInvocationContext)context;
        Map evidenceMapByTestCase = (Map)evidenceMap.get(testcaseId);
        if(evidenceMapByTestCase == null){
            synchronized(evidenceMap){
                evidenceMapByTestCase = (Map)evidenceMap.get(testcaseId);
                if(evidenceMapByTestCase == null){
                    evidenceMapByTestCase = Collections.synchronizedMap(new HashMap());
                    evidenceMap.put(testcaseId, evidenceMapByTestCase);
                    evidenceMapByTestCase.put(NOT_FOUND, Collections.synchronizedList(new ArrayList()));
                }
            }
        }
        if(returnMap.size() == 0){
            returnNotFound(
                methodCtx,
                scenarioGroupId,
                scenarioId,
                testcaseId,
                evidenceMapByTestCase,
                "Scenario not found. scenarioGroupId=" + scenarioGroupId + ", scenarioId=" + scenarioId
            );
            return null;
        }
        Map map = (Map)returnMap.get(testcaseId);
        if(map == null){
            returnNotFound(
                methodCtx,
                scenarioGroupId,
                scenarioId,
                testcaseId,
                evidenceMapByTestCase,
                "Testcase not found. scenarioGroupId=" + scenarioGroupId + ", scenarioId=" + scenarioId + ", testcaseId=" + testcaseId
            );
            return null;
        }
        Iterator entries = map.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry)entries.next();
            CallKey key = (CallKey)entry.getKey();
            if(key.isMatchMethod(methodCtx)){
                List evidenceList = (List)evidenceMapByTestCase.get(key);
                if(evidenceList == null){
                    synchronized(evidenceMapByTestCase){
                        evidenceList = (List)evidenceMapByTestCase.get(key);
                        if(evidenceList == null){
                            evidenceList = Collections.synchronizedList(new ArrayList());
                            evidenceMapByTestCase.put(key, evidenceList);
                        }
                    }
                }
                ReturnList list = (ReturnList)entry.getValue();
                File file = null;
                file = list.get();
                if(file == null){
                    returnNotFound(
                        methodCtx,
                        scenarioGroupId,
                        scenarioId,
                        testcaseId,
                        evidenceMapByTestCase,
                        "The number of calls is too much. scenarioGroupId=" + scenarioGroupId + ", scenarioId=" + scenarioId + ", testcaseId=" + testcaseId + ", method=" + methodCtx.getTargetMethod()
                    );
                    return null;
                }
                
                if(isSaveCallFile){
                    File callFile = saveCallFile(methodCtx, file);
                    evidenceList.add(callFile);
                }
                evidenceList.add(file);
                ReturnData data = null;
                if(isCacheReturn && returnCacheMap.containsKey(file)){
                    data = (ReturnData)returnCacheMap.get(file);
                }else{
                    data = new ReturnData();
                    data.read(file ,methodCtx);
                    if(isCacheReturn){
                        returnCacheMap.put(file, data);
                    }
                }
                if(data.sleep > 0){
                    Thread.sleep(data.sleep);
                }
                Class returnClass = null;
                if(data.returnClass == null){
                    returnClass = methodCtx.getTargetMethod().getReturnType();
                }else{
                    returnClass = Utility.convertStringToClass(data.returnClass, false);
                }
                if(Void.TYPE.equals(returnClass)){
                    return null;
                }
                Object returnValue = null;
                if(!returnClass.isPrimitive()
                    && !returnClass.isInterface()
                    && !Modifier.isAbstract(returnClass.getModifiers())
                ){
                    try{
                        Constructor constructor = returnClass.getConstructor((Class[])null);
                        returnValue = constructor.newInstance();
                    }catch(NoSuchMethodException e){
                    }catch(InstantiationException e){
                    }catch(IllegalAccessException e){
                    }
                }
                if(data.preInterpretScript != null){
                    Map variables = new HashMap();
                    variables.put("args", methodCtx.getParameters());
                    variables.put("ret", returnValue);
                    returnValue = interpreter.evaluate(data.preInterpretScript, variables);
                }
                if(data.argumentValues != null){
                    for(int i = 0; i < data.argumentValues.length; i++){
                        String argumentValue = data.argumentValues[i];
                        if(argumentValue == null){
                            continue;
                        }
                        Object argument = methodCtx.getParameters()[i];
                        Class argClass = argument == null ? methodCtx.getTargetMethod().getParameterTypes()[i] : argument.getClass();
                        Object converterObj = returnConverters.getValue(argClass);
                        Converter converter = null;
                        if(converterObj == null){
                            throw new ConvertException("Argument value can't convert. Converter not found. argumentType=" + argClass.getName());
                        }else if(converterObj instanceof ServiceName){
                            converter = (Converter)ServiceManagerFactory.getServiceObject((ServiceName)converterObj);
                        }else{
                            converter = (Converter)converterObj;
                        }
                        
                        if(converter instanceof BindingConverter){
                            ((BindingConverter)converter).convert(argumentValue, argument);
                        }else if(converter instanceof BindingStreamConverter){
                            StringStreamConverter ssc = new StringStreamConverter();
                            ssc.setCharacterEncodingToStream(fileEncoding == null ? System.getProperty("file.encoding") : fileEncoding);
                            ((BindingStreamConverter)converter).convertToObject(ssc.convertToStream(argumentValue), argument);
                        }else{
                            throw new ConvertException("Argument value can't convert. Not supported converter. argumentType=" + argClass.getName() + ", converter=" + converter);
                        }
                    }
                }
                if("text".equals(data.returnType)){
                    String returnStr = data.returnValue;
                    returnClass = returnValue == null ? returnClass : returnValue.getClass();
                    Object converterObj = returnConverters.getValue(returnClass);
                    Converter converter = null;
                    if(converterObj instanceof ServiceName){
                        converter = (Converter)ServiceManagerFactory.getServiceObject((ServiceName)converterObj);
                    }else{
                        converter = (Converter)converterObj;
                    }
                    if(converter == null){
                        PropertyEditor editor = NimbusPropertyEditorManager.findEditor(returnClass);
                        if(editor == null){
                            returnValue = returnStr;
                        }else{
                            editor.setAsText(returnStr);
                            returnValue = editor.getValue();
                        }
                    }else{
                        if(converter instanceof StreamConverter){
                            StringStreamConverter ssc = new StringStreamConverter();
                            ssc.setCharacterEncodingToStream(fileEncoding == null ? System.getProperty("file.encoding") : fileEncoding);
                            if((converter instanceof BindingStreamConverter) && returnValue != null){
                                returnValue = ((BindingStreamConverter)converter).convertToObject(ssc.convertToStream(returnStr), returnValue);
                            }else{
                                returnValue = ((StreamConverter)converter).convertToObject(ssc.convertToStream(returnStr));
                            }
                        }else{
                            if(converter instanceof BindingConverter && returnValue != null){
                                returnValue = ((BindingConverter)converter).convert(returnStr, returnValue);
                            }else{
                                returnValue = converter.convert(returnStr);
                            }
                        }
                    }
                }else if("interpreter".equals(data.returnType)){
                    Map variables = new HashMap();
                    variables.put("args", methodCtx.getParameters());
                    variables.put("ret", returnValue);
                    returnValue = interpreter.evaluate(data.returnValue, variables);
                }
                if(data.postInterpretScript != null){
                    Map variables = new HashMap();
                    variables.put("args", methodCtx.getParameters());
                    variables.put("ret", returnValue);
                    returnValue = interpreter.evaluate(data.postInterpretScript, variables);
                }
                returnClass = returnValue == null ? returnClass : returnValue.getClass();
                if(!isAssignableFrom(methodCtx.getTargetMethod().getReturnType(), returnClass)){
                    throw new ConvertException("Return value can't convert. returnType=" + returnClass.getName());
                }
                return returnValue;
            }
        }
        returnNotFound(
            methodCtx,
            scenarioGroupId,
            scenarioId,
            testcaseId,
            evidenceMapByTestCase,
            "TestCase not found. scenarioGroupId=" + scenarioGroupId + ", scenarioId=" + scenarioId + ", testcaseId=" + testcaseId + ", method=" + methodCtx.getTargetMethod()
        );
        return null;
    }
    
    protected boolean isAssignableFrom(Class thisClass, Class thatClass){
        if(thatClass == null){
            return !thisClass.isPrimitive();
        }
        if(isNumber(thisClass) && isNumber(thatClass)){
            if(Byte.TYPE.equals(thisClass)
                || Byte.class.equals(thisClass)){
                return Byte.TYPE.equals(thatClass)
                    || Byte.class.equals(thatClass);
            }else if(Short.TYPE.equals(thisClass)
                || Short.class.equals(thisClass)){
                return Short.TYPE.equals(thatClass)
                    || Short.class.equals(thatClass);
            }else if(Integer.TYPE.equals(thisClass)
                || Integer.class.equals(thisClass)){
                return Integer.TYPE.equals(thatClass)
                    || Integer.class.equals(thatClass);
            }else if(Long.TYPE.equals(thisClass)
                || Long.class.equals(thisClass)){
                return Long.TYPE.equals(thatClass)
                    || Long.class.equals(thatClass);
            }else if(Float.TYPE.equals(thisClass)
                || Float.class.equals(thisClass)){
                return Float.TYPE.equals(thatClass)
                    || Float.class.equals(thatClass);
            }else if(Double.TYPE.equals(thisClass)
                || Double.class.equals(thisClass)){
                return Double.TYPE.equals(thatClass)
                    || Double.class.equals(thatClass);
            }else{
                return thisClass.isAssignableFrom(thatClass);
            }
        }else{
            return thisClass.isAssignableFrom(thatClass);
        }
    }
    
    protected boolean isNumber(Class clazz){
        if(clazz == null){
            return false;
        }
        if(clazz.isPrimitive()){
            if(Byte.TYPE.equals(clazz)
                || Short.TYPE.equals(clazz)
                || Integer.TYPE.equals(clazz)
                || Long.TYPE.equals(clazz)
                || Float.TYPE.equals(clazz)
                || Double.TYPE.equals(clazz)){
                return true;
            }else{
                return false;
            }
        }else if(Number.class.isAssignableFrom(clazz)){
            return true;
        }else{
            return false;
        }
    }
    
    protected File saveCallFile(MethodInvocationContext context, File file) throws IOException, ConvertException{
        final File callFile = new File(file.getParentFile(), file.getName() + ".cll");
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(callFile)));
        try{
            Class[] parameterTypes = context.getTargetMethod().getParameterTypes();
            Object[] parameters = context.getParameters();
            if(parameters == null || parameters.length == 0){
                pw.println("arguments is nothing");
            }else{
                for(int i = 0; i < parameters.length; i++){
                    Object param = parameters[i];
                    if(param == null){
                        pw.println("null");
                        continue;
                    }
                    Object converterObj = argsConverters.getValue(param.getClass());
                    Converter converter = null;
                    if(converterObj instanceof ServiceName){
                        converter = (Converter)ServiceManagerFactory.getServiceObject((ServiceName)converterObj);
                    }else{
                        converter = (Converter)converterObj;
                    }
                    if(converter == null){
                        PropertyEditor editor = NimbusPropertyEditorManager.findEditor(parameterTypes[i]);
                        if(editor == null){
                            pw.println(param.toString());
                        }else{
                            editor.setValue(param);
                            pw.println(editor.getAsText());
                        }
                    }else{
                        if(converter instanceof StreamConverter){
                            StringStreamConverter ssc = new StringStreamConverter();
                            ssc.setCharacterEncodingToObject(fileEncoding == null ? System.getProperty("file.encoding") : fileEncoding);
                            pw.println(ssc.convertToObject(((StreamConverter)converter).convertToStream(param)));
                        }else{
                            pw.println(converter.convert(param));
                        }
                    }
                }
            }
            pw.flush();
        }finally {
            pw.close();
            pw = null;
        }
        return callFile;
    }
    
    protected void returnNotFound(
        MethodInvocationContext context,
        String scenarioGroupId,
        String scenarioId,
        String testcaseId,
        Map evidenceMapByTestCase,
        String message
    ) throws IOException, ConvertException{
        List notFoundList = (List)evidenceMapByTestCase.get(NOT_FOUND);
        File file = null;
        synchronized(notFoundList){
            file = new File(new File(new File(new File(resourceDirectory, scenarioGroupId), scenarioId), testcaseId), "NOT_FOUND_" + (notFoundList.size() + 1) + ".txt");
        }
        if(isSaveCallFile){
            File callFile = saveCallFile(context, file);
            notFoundList.add(callFile);
        }
        throw new UnsupportedOperationException(message);
    }
    
    public void startScenario(String userId, String scenarioGroupId, String scenarioId) throws Exception{
        synchronized(lock){
            if(this.scenarioGroupId != null && this.scenarioId != null){
                throw new Exception("Scenario started. scenarioGroupId=" + this.scenarioGroupId + ", scenarioId=" + this.scenarioId + ", userId=" + this.userId);
            }
            if(scenarioGroupId.equals(this.scenarioGroupId) && scenarioId.equals(this.scenarioId)){
                return;
            }
            RecurciveSearchFile scenarioDir = new RecurciveSearchFile(new File(resourceDirectory, scenarioGroupId), scenarioId);
            if(!scenarioDir.deleteAllTree()){
                throw new Exception("Resource directory can not delete. path=" + scenarioDir);
            }
            if(!scenarioDir.mkdirs()) {
                throw new Exception("Resource directory can not make. path=" + scenarioDir);
            }
            stubResourceManager.downloadScenarioResource(scenarioDir, scenarioGroupId, scenarioId, id);
            returnMap.clear();
            evidenceMap.clear();
            File[] testcaseDirs = scenarioDir.listFiles();
            if(testcaseDirs == null || testcaseDirs.length == 0){
                this.userId = userId;
                this.scenarioGroupId = scenarioGroupId;
                this.scenarioId = scenarioId;
                this.testcaseId = null;
                return;
            }
            for(int i = 0; i < testcaseDirs.length; i++){
                if(!testcaseDirs[i].isDirectory()){
                    continue;
                }
                String testcaseId = testcaseDirs[i].getName();
                File[] files = testcaseDirs[i].listFiles();
                if(files == null){
                    continue;
                }
                sort(files);
                for(int j = 0; j < files.length; j++){
                    File file = files[j];
                    if(file.isDirectory()){
                        continue;
                    }
                    Map map = (Map)returnMap.get(testcaseId);
                    if(map == null){
                        map = new LinkedHashMap();
                        returnMap.put(testcaseId, map);
                    }
                    FileInputStream fis = new FileInputStream(file);
                    InputStreamReader isr = fileEncoding == null ? new InputStreamReader(fis) : new InputStreamReader(fis, fileEncoding);
                    CallKey callKey = new CallKey();
                    try{
                        BufferedReader br = new BufferedReader(isr);
                        callKey.parseMethod(br.readLine());
                        callKey.argumentCondition = br.readLine();
                        if(callKey.argumentCondition != null && callKey.argumentCondition.length() == 0){
                            callKey.argumentCondition = null;
                        }
                    }finally{
                        fis.close();
                        isr.close();
                    }
                    ReturnList list = (ReturnList)map.get(callKey);
                    if(list == null){
                        list = new ReturnList();
                        map.put(callKey, list);
                    }
                    list.add(file);
                    list.sort();
                }
            }
            this.userId = userId;
            this.scenarioGroupId = scenarioGroupId;
            this.scenarioId = scenarioId;
            this.testcaseId = null;
        }
    }
    
    public void cancelScenario() throws Exception{
        synchronized(lock){
            if(scenarioGroupId == null && scenarioId == null){
                return;
            }
            returnMap.clear();
            evidenceMap.clear();
            returnCacheMap.clear();
            userId = null;
            RecurciveSearchFile scenarioDir = new RecurciveSearchFile(new File(resourceDirectory, scenarioGroupId), scenarioId);
            scenarioDir.deleteAllTree();
            this.scenarioGroupId = null;
            this.scenarioId = null;
            this.testcaseId = null;
        }
    }
    
    public void endScenario() throws Exception{
        synchronized(lock){
            if(scenarioGroupId == null && scenarioId == null){
                return;
            }
            RecurciveSearchFile scenarioDir = new RecurciveSearchFile(new File(resourceDirectory, scenarioGroupId), scenarioId);
            returnMap.clear();
            evidenceMap.clear();
            returnCacheMap.clear();
            userId = null;
            this.scenarioGroupId = null;
            this.scenarioId = null;
            this.testcaseId = null;
            scenarioDir.deleteAllTree();
        }
    }
    
    public void startTestCase(String testcaseId) throws Exception{
        synchronized(lock){
            if(this.scenarioGroupId != null && this.scenarioId != null && this.testcaseId != null){
                throw new Exception("Testcase started. scenarioGroupId=" + this.scenarioGroupId + ", scenarioId=" + this.scenarioId + ", testcaseId=" + this.testcaseId + ", userId=" + this.userId);
            }
            if(this.scenarioGroupId == null || this.scenarioId == null){
                throw new Exception("Scenario not started. scenarioGroupId=" + this.scenarioGroupId + ", scenarioId=" + this.scenarioId + ", testcaseId=" + this.testcaseId);
            }
            if(!returnMap.containsKey(testcaseId)){
                throw new Exception("Testcase not found. scenarioGroupId=" + this.scenarioGroupId + ", scenarioId=" + this.scenarioId + ", testcaseId=" + testcaseId);
            }
            this.testcaseId = testcaseId;
        }
    }
    
    public void endTestCase() throws Exception{
        synchronized(lock){
            if(scenarioGroupId == null || scenarioId == null || testcaseId == null){
                return;
            }
            File testcaseDir = new File(new File(new File(resourceDirectory, scenarioGroupId), scenarioId), testcaseId);
            if(testcaseDir.exists()){
                stubResourceManager.uploadTestCaseResource(testcaseDir, scenarioGroupId, scenarioId, testcaseId, id);
            }
            this.testcaseId = null;
        }
    }
    
    protected class CallKey{
        protected String methodName;
        protected String[] paramTypes;
        protected boolean isParamTypesCheck = true;
        protected String argumentCondition;
        
        public void parseMethod(String method) throws IllegalArgumentException{
            String tmp = method;
            int index = tmp.indexOf('(');
            if(index == 0 || index == tmp.length() - 1){
                throw new IllegalArgumentException("Invalid method : " + method);
            }else if(index == -1){
                methodName = tmp;
                isParamTypesCheck = false;
            }else{
                methodName = tmp.substring(0, index);
                tmp = tmp.substring(index + 1);
                index = tmp.indexOf(')');
                if(index == -1 || index != tmp.length() - 1){
                    throw new IllegalArgumentException("Invalid method : " + method);
                }
                if(index == 0){
                    paramTypes = new String[0];
                }else{
                    tmp = tmp.substring(0, index);
                    if(tmp.equals("*")){
                        isParamTypesCheck = false;
                    }else{
                        final StringTokenizer tokens = new StringTokenizer(tmp, ",");
                        final List paramTypeList = new ArrayList();
                        while(tokens.hasMoreTokens()){
                            final String paramType = tokens.nextToken().trim();
                            if(paramType.length() == 0){
                                throw new IllegalArgumentException("Invalid method : " + method);
                            }
                            paramTypeList.add(paramType);
                        }
                        paramTypes = (String[])paramTypeList.toArray(new String[paramTypeList.size()]);
                    }
                }
            }
        }
        
        public boolean isMatchMethod(MethodInvocationContext ctx) throws Exception{
            Method comp = ctx.getTargetMethod();
            if(!methodName.equals(comp.getName())
                && !Pattern.matches(methodName, comp.getName())){
                return false;
            }
            if(!isParamTypesCheck){
                return true;
            }
            final Class[] compParamTypes = comp.getParameterTypes();
            if(paramTypes == null && compParamTypes == null){
                return true;
            }
            if((paramTypes == null && compParamTypes != null)
                || (paramTypes != null && compParamTypes == null)
                || (paramTypes.length != compParamTypes.length)
            ){
                return false;
            }
            for(int i = 0; i < paramTypes.length; i++){
                String typeName = toClassName(compParamTypes[i]);
                if(!paramTypes[i].equals(compParamTypes[i].getName())
                    && !paramTypes[i].equals(typeName)
                    && !Pattern.matches(paramTypes[i], typeName)){
                    return false;
                }
            }
            if(argumentCondition != null){
                Map variables = new HashMap();
                variables.put("args", ctx.getParameters());
                if(!((Boolean)interpreter.evaluate(argumentCondition, variables)).booleanValue()){
                    return false;
                }
            }
            return true;
        }
        
        private String toClassName(Class clazz){
            if(clazz.isArray()){
                return toClassName(clazz.getComponentType()) + "[]";
            }else{
                return clazz.getName();
            }
        }
        
        public boolean equals(Object obj){
            final CallKey comp = (CallKey)obj;
            if((methodName == null && comp.methodName != null)
                || (methodName != null && comp.methodName == null)
                || (methodName != null && !methodName.equals(comp.methodName))
            ){
                return false;
            }
            if(isParamTypesCheck != comp.isParamTypesCheck){
                return false;
            }
            if(paramTypes == comp.paramTypes){
                return true;
            }
            if((paramTypes == null && comp.paramTypes != null)
                || (paramTypes != null && comp.paramTypes == null)
                || (paramTypes.length != comp.paramTypes.length)
            ){
                return false;
            }
            for(int i = 0; i < paramTypes.length; i++){
                if(!paramTypes[i].equals(comp.paramTypes[i])){
                    return false;
                }
            }
            if((argumentCondition == null && comp.argumentCondition != null)
                || (argumentCondition != null && (comp.argumentCondition == null || !argumentCondition.equals(comp.argumentCondition)))){
                return false;
            }
            return true;
        }
        
        public int hashCode(){
            int hashCode = methodName == null ? 0 : methodName.hashCode();
            hashCode += isParamTypesCheck ? 1 : 0;
            if(paramTypes != null){
                for(int i = 0; i < paramTypes.length; i++){
                    hashCode += paramTypes[i].hashCode();
                }
            }
            if(argumentCondition != null){
                hashCode += argumentCondition.hashCode();
            }
            return hashCode;
        }
    }
    
    protected class ReturnList{
        private List list = new ArrayList();
        private int requestCount = 0;
        public void add(File file){
            list.add(file);
        }
        public void sort(){
            Collections.sort(list);
        }
        public synchronized File get(){
            if(list.size() > requestCount){
                return (File)list.get(requestCount++);
            }else{
                if(isAllowRepeatCall){
                    requestCount = 0;
                    return get();
                }else{
                    return null;
                }
            }
        }
    }
    
    protected class ReturnData{
        public long sleep;
        public String preInterpretScript;
        public String postInterpretScript;
        public String returnClass;
        public String[] argumentValues;
        public String returnType;
        public String returnValue;
        
        public void read(File file, MethodInvocationContext context) throws Exception{
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = fileEncoding == null ? new InputStreamReader(fis) : new InputStreamReader(fis, fileEncoding);
            try{
                BufferedReader br = new BufferedReader(isr);
                String methodSignature = br.readLine();
                String argumentsCondition = br.readLine();
                String line = br.readLine();
                if(line != null && line.startsWith("sleep ")){
                    sleep = Long.parseLong(line.split(" ")[1]);
                    line = br.readLine();
                }
                if(line != null){
                    returnClass = line;
                    if(returnClass.length() == 0){
                        returnClass = null;
                    }
                    line = br.readLine();
                }
                if(line != null && "pre-interpreter:start".equals(line)){
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    while((line = br.readLine()) != null){
                        if("pre-interpreter:end".equals(line)){
                            line = br.readLine();
                            break;
                        }
                        pw.println(line);
                    }
                    pw.flush();
                    preInterpretScript = sw.toString();
                }
                if(line != null && "post-interpreter:start".equals(line)){
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    while((line = br.readLine()) != null){
                        if("post-interpreter:end".equals(line)){
                            line = br.readLine();
                            break;
                        }
                        pw.println(line);
                    }
                    pw.flush();
                    postInterpretScript = sw.toString();
                }
                while(line != null && "argument[".startsWith(line)){
                    Class[] params = context.getTargetMethod().getParameterTypes();
                    int lastIndex = line.lastIndexOf(']');
                    if(lastIndex == -1){
                        throw new Exception("Illegal argument line : " + line);
                    }
                    int argIndex = 0;
                    try{
                        argIndex = Integer.parseInt(line.substring(9, lastIndex));
                    }catch(NumberFormatException e){
                        throw new Exception("Illegal argument line : " + line, e);
                    }
                    if(params == null || params.length <= argIndex){
                        throw new Exception("Illegal argument line : " + line);
                    }
                    if(argumentValues == null){
                        argumentValues = new String[params.length];
                    }
                    argumentValues[argIndex] = read(br, "argument:end");
                    line = br.readLine();
                }
                if(line != null){
                    returnType = line;
                    if(returnType.length() == 0){
                        returnType = null;
                    }
                }
                if(line != null){
                    StringWriter sw = new StringWriter();
                    char[] chars = new char[1024];
                    int readLen = 0;
                    while ((readLen = br.read(chars, 0, chars.length)) != -1) {
                        sw.write(chars, 0, readLen);
                    }
                    returnValue = sw.toString();
                }
            }finally{
                fis.close();
                isr.close();
            }
        }
        
        private String read(BufferedReader r, String endLine) throws IOException{
            final StringBuilder buf = new StringBuilder();
            int rightIndex = 0;
            int c = 0;
            do{
                c = r.read();
                if(c < 0){
                    break;
                }
                buf.append((char)c);
                if(c == endLine.charAt(rightIndex)){
                    rightIndex++;
                }else{
                    rightIndex = 0;
                }
            }while(rightIndex <= endLine.length());
            if(c > 0){
                r.readLine();
            }
            return buf.substring(0, buf.length() - endLine.length());
        }
    }
    
    private void sort(File[] files){
        Arrays.sort(files, new Comparator() {
            public int compare(Object f1, Object f2){
                return ((File)f1).getName().compareTo(((File)f2).getName());
            }
        });
    }
}
