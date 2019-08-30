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
package jp.ossc.nimbus.service.interpreter;

import java.io.*;
import java.util.*;
import java.beans.*;
import javax.script.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.ServiceNameEditor;
import jp.ossc.nimbus.beans.NimbusPropertyEditorManager;

/**
 * ScriptEngineを使ってJavaコードを実行するインタープリタサービス。<p>
 * 
 * @author M.Takata
 */
public class ScriptEngineInterpreterService extends ServiceBase
 implements Interpreter, ScriptEngineInterpreterServiceMBean{
    
    private static final long serialVersionUID = -649936290793723124L;
    
    private String extension;
    private String mimeType;
    private String engineName;
    private Map<String, Object> globalBindings;
    private Map<String, Object> engineBindings;
    private boolean isCompilable;
    private ClassLoader classLoader;
    private boolean isNewScriptEngineByEvaluate = true;
    
    private ScriptEngineManager scriptEngineManager;
    private ScriptEngine scriptEngine;
    
    public void setExtension(String ext){
        extension = ext;
    }
    public String getExtension(){
        return extension;
    }
    
    public void setMimeType(String type){
        mimeType = type;
    }
    public String getMimeType(){
        return mimeType;
    }
    
    public void setEngineName(String name){
        engineName = name;
    }
    public String getEngineName(){
        return engineName;
    }
    
    public void setGlobalBinding(String name, Object val){
        globalBindings.put(name, val);
    }
    public Object getGlobalBinding(String name){
        return globalBindings.get(name);
    }
    public Map<String, Object> getGlobalBindings(){
        return globalBindings;
    }
    
    public void setEngineBinding(String name, Object val){
        engineBindings.put(name, val);
    }
    public Object getEngineBinding(String name){
        return engineBindings.get(name);
    }
    public Map<String, Object> getEngineBindings(){
        return engineBindings;
    }
    
    public void setNewScriptEngineByEvaluate(boolean isNew){
        isNewScriptEngineByEvaluate = isNew;
    }
    public boolean isNewScriptEngineByEvaluate(){
        return isNewScriptEngineByEvaluate;
    }
    
    public void setClassLoader(ClassLoader loader){
        classLoader = loader;
    }
    public ClassLoader getClassLoader(){
        return classLoader;
    }
    
    public void createService() throws Exception{
        globalBindings = new HashMap<String, Object>();
        engineBindings = Collections.synchronizedMap(new HashMap<String, Object>());
    }
    
    public void startService() throws Exception{
        if(classLoader == null){
            scriptEngineManager = new ScriptEngineManager();
        }else{
            scriptEngineManager = new ScriptEngineManager(classLoader);
        }
        ScriptEngine engine = createScriptEngine();
        if(engine == null){
            throw new IllegalArgumentException("ScriptEngine not found.");
        }
        scriptEngineManager.getBindings().putAll(globalBindings);
        isCompilable = engine instanceof Compilable;
    }
    
    public void stopService() throws Exception{
        scriptEngineManager = null;
    }
    
    public void destroyService() throws Exception{
        globalBindings = null;
        engineBindings = null;
    }
    
    private ScriptEngine createScriptEngine(){
        ScriptEngine engine = null;
        if(extension != null){
            engine = scriptEngineManager.getEngineByExtension(extension);
            if(engine != null){
                return engine;
            }
        }
        if(mimeType != null){
            engine = scriptEngineManager.getEngineByMimeType(mimeType);
            if(engine != null){
                return engine;
            }
        }
        if(engineName != null){
            engine = scriptEngineManager.getEngineByName(engineName);
            if(engine != null){
                return engine;
            }
        }
        final List<ScriptEngineFactory> factories
            = scriptEngineManager.getEngineFactories();
        if(factories != null && factories.size() != 0){
            return factories.get(0).getScriptEngine();
        }
        return null;
    }
    
    private synchronized ScriptEngine getScriptEngine(){
        if(scriptEngine == null){
            scriptEngine = createScriptEngine();
        }
        return scriptEngine;
    }
    
    public boolean isCompilable(){
        return isCompilable;
    }
    
    public CompiledInterpreter compile(String code) throws EvaluateException{
        if(!isCompilable){
            throw new EvaluateException("Compile is not supported.");
        }
        final ScriptEngine engine = isNewScriptEngineByEvaluate ? createScriptEngine() : getScriptEngine();
        if(engine == null || !(engine instanceof Compilable)){
            throw new EvaluateException("ScriptEngine not found.");
        }
        final Compilable compilable = (Compilable)engine;
        try{
            final CompiledScript compiled = compilable.compile(code);
            return new CompiledInterpreter(){
                
                public Object evaluate() throws EvaluateException{
                    return evaluate(null);
                }
                
                public Object evaluate(Map variables) throws EvaluateException{
                    try{
                        if(variables == null || variables.size() == 0){
                            return compiled.eval();
                        }else{
                            return compiled.eval(new SimpleBindings(variables));
                        }
                    }catch(ScriptException e){
                        throw new EvaluateException(e);
                    }
                }
            };
        }catch(ScriptException e){
            throw new EvaluateException(e);
        }
    }
    
    public Object evaluate(String code) throws EvaluateException{
        return evaluate(code, null);
    }
    
    public Object evaluate(String code, Map variables) throws EvaluateException{
        final ScriptEngine engine = isNewScriptEngineByEvaluate ? createScriptEngine() : getScriptEngine();
        if(engine == null){
            throw new EvaluateException("ScriptEngine not found.");
        }
        engine.getBindings(ScriptContext.ENGINE_SCOPE).putAll(engineBindings);
        try{
            if(variables == null || variables.size() == 0){
                return engine.eval(code);
            }else{
                return engine.eval(code, new SimpleBindings(variables));
            }
        }catch(ScriptException e){
            throw new EvaluateException(e);
        }
    }
    
    private static void usage(){
        System.out.println("コマンド使用方法：");
        System.out.println(" java jp.ossc.nimbus.service.interpreter.ScriptEngineInterpreterService [options] [source code]");
        System.out.println();
        System.out.println("[options]");
        System.out.println();
        System.out.println(" [-servicedir path filter]");
        System.out.println("  インタープリタサービスの起動に必要なサービス定義ファイルのディレクトリとサービス定義ファイルを特定するフィルタを指定します。");
        System.out.println();
        System.out.println(" [-servicepath paths]");
        System.out.println("  インタープリタサービスの起動に必要なサービス定義ファイルのパスを指定します。");
        System.out.println("  パスセパレータ区切りで複数指定可能です。");
        System.out.println();
        System.out.println(" [-servicename name]");
        System.out.println("  インタープリタサービスのサービス名を指定します。");
        System.out.println("  指定しない場合はNimbus#Interpreterとみなします。");
        System.out.println();
        System.out.println(" [-file paths]");
        System.out.println("  実行するソースコードファイルを指定します。");
        System.out.println("  パスセパレータ区切りで複数指定可能です。");
        System.out.println("  このオプションの指定がない場合は、引数source codeでソースコードを指定します。");
        System.out.println();
        System.out.println(" [-param name(type)=value]");
        System.out.println("  スクリプト変数として渡す変数名と型と値を指定します。");
        System.out.println("  型宣言である(type)は省略可能で、省略した場合は、java.lang.Stringとなります。");
        System.out.println();
        System.out.println(" [-encoding encode]");
        System.out.println("  実行するソースコードファイルの文字コードを指定します。");
        System.out.println();
        System.out.println(" [-help]");
        System.out.println("  ヘルプを表示します。");
        System.out.println();
        System.out.println("[source code]");
        System.out.println(" 実行するソースコードを指定します。");
        System.out.println();
        System.out.println(" 使用例 : ");
        System.out.println("    java -classpath classes;lib/nimbus.jar jp.ossc.nimbus.service.interpreter.ScriptEngineInterpreterService Java.type(\"java.lang.System\").out.println(\"test\");");
    }
    
    private static List parsePaths(String paths){
        String pathSeparator = System.getProperty("path.separator");
        final List result = new ArrayList();
        if(paths == null || paths.length() == 0){
            return result;
        }
        if(paths.indexOf(pathSeparator) == -1){
            result.add(paths);
            return result;
        }
        String tmpPaths = paths;
        int index = -1;
        while((index = tmpPaths.indexOf(pathSeparator)) != -1){
            result.add(tmpPaths.substring(0, index));
            if(index != tmpPaths.length() - 1){
                tmpPaths = tmpPaths.substring(index + 1);
            }else{
                tmpPaths = null;
                break;
            }
        }
        if(tmpPaths != null && tmpPaths.length() != 0){
            result.add(tmpPaths);
        }
        return result;
    }
    
    public static void main(String[] args) throws Exception{
        
        if(args.length == 0 || (args.length != 0 && args[0].equals("-help"))){
            usage();
            if(args.length == 0){
                System.exit(-1);
            }
            return;
        }
        
        boolean option = false;
        boolean isServiceDir = false;
        String key = null;
        ServiceName serviceName = null;
        List serviceDirs = null;
        String serviceDir = null;
        List servicePaths = null;
        List files = null;
        String encode = null;
        Map params = null;
        StringWriter code = new StringWriter();
        PrintWriter codeWriter = new PrintWriter(code);
        for(int i = 0; i < args.length; i++){
            if(option){
                if(key.equals("-servicename")){
                    ServiceNameEditor editor = new ServiceNameEditor();
                    editor.setAsText(args[i]);
                    serviceName = (ServiceName)editor.getValue();
                }else if(key.equals("-servicedir")){
                    if(serviceDirs == null){
                        serviceDirs = new ArrayList();
                    }
                    serviceDirs.add(new String[]{serviceDir, args[i]});
                }else if(key.equals("-servicepath")){
                    servicePaths = parsePaths(args[i]);
                }else if(key.equals("-file")){
                    files = parsePaths(args[i]);
                }else if(key.equals("-encoding")){
                    encode = args[i];
                }else if(key.equals("-param")){
                    int index = args[i].indexOf("=");
                    if(index == -1){
                        System.out.println("パラメータが不正です : " + args[i]);
                        System.exit(-1);
                    }
                    String name = args[i].substring(0, index);
                    String value = args[i].substring(index + 1);
                    Class type = String.class;
                    index = name.indexOf("(");
                    if(index != -1 && name.charAt(name.length() - 1) == ')'){
                        try{
                            type = Utility.convertStringToClass(name.substring(index + 1, name.length() - 1));
                        }catch(ClassNotFoundException e){
                            System.out.println("パラメータの型クラスが見つかりません : " + args[i]);
                            System.exit(-1);
                        }
                        name = name.substring(0, index);
                    }
                    PropertyEditor editor = NimbusPropertyEditorManager.findEditor(type);
                    if(editor == null){
                        System.out.println("パラメータの型が編集できない型です : " + args[i]);
                        System.exit(-1);
                    }
                    editor.setAsText(value);
                    if(params == null){
                        params = new HashMap();
                    }
                    params.put(name, editor.getValue());
                }
                option = false;
                key = null;
            }else{
                if(args[i].equals("-servicename")
                     || args[i].equals("-servicepath")
                     || args[i].equals("-file")
                     || args[i].equals("-encoding")
                     || args[i].equals("-param")
                ){
                    option = true;
                    key = args[i];
                }else if(args[i].equals("-servicedir")){
                    isServiceDir = true;
                    key = args[i];
                }else if(args[i].equals("-help")){
                    usage();
                    return;
                }else if(isServiceDir){
                    isServiceDir = false;
                    option = true;
                    serviceDir = args[i];
                }else{
                    codeWriter.print(" " + args[i]);
                }
            }
        }
        Interpreter interpreter = null;
        if(serviceDirs != null || servicePaths != null){
            if(serviceDirs != null){
                for(int i = 0, imax = serviceDirs.size(); i < imax; i++){
                    String[] array = (String[])serviceDirs.get(i);
                    if(!ServiceManagerFactory.loadManagers(array[0], array[1])){
                        System.out.println("Service load error. path=" + array[0] + ", filter=" + array[1]);
                        Thread.sleep(1000);
                        System.exit(-1);
                    }
                }
            }
            if(servicePaths != null){
                for(int i = 0, imax = servicePaths.size(); i < imax; i++){
                    if(!ServiceManagerFactory.loadManager((String)servicePaths.get(i))){
                        System.out.println("Service load error." + servicePaths.get(i));
                        Thread.sleep(1000);
                        System.exit(-1);
                    }
                }
            }
            if(!ServiceManagerFactory.checkLoadManagerCompleted()){
                Thread.sleep(1000);
                System.exit(-1);
            }
            if(serviceName == null){
                serviceName = new ServiceName("Nimbus", "Interpreter");
                if(ServiceManagerFactory.isRegisteredService(serviceName)){
                    interpreter = (Interpreter)ServiceManagerFactory
                        .getServiceObject(serviceName);
                }
            }else{
                interpreter = (Interpreter)ServiceManagerFactory
                    .getServiceObject(serviceName);
            }
        }
        if(interpreter == null){
            ScriptEngineInterpreterService service = new ScriptEngineInterpreterService();
            service.create();
            service.start();
            interpreter = service;
        }
        if(files != null){
            code.flush();
            for(int i = 0, imax = files.size(); i < imax; i++){
                FileInputStream fis = new FileInputStream((String)files.get(i));
                InputStreamReader isr = null;
                if(encode == null){
                    isr = new InputStreamReader(fis);
                }else{
                    isr = new InputStreamReader(fis, encode);
                }
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while((line = br.readLine()) != null){
                    codeWriter.println(line);
                }
                fis.close();
            }
        }
        codeWriter.flush();
        int exitCode = 0;
        try{
            System.out.println(params == null ? interpreter.evaluate(code.toString()) : interpreter.evaluate(code.toString(), params));
        }catch(Throwable e){
            StringBuilder buf = new StringBuilder();
            final String lineSeparator = System.getProperty("line.separator");
            buf.append("Exception occuers :").append(e.toString()).append(lineSeparator);
            final StackTraceElement[] elemss = e.getStackTrace();
            if(elemss != null){
                for(int i = 0; i < elemss.length; i++){
                    buf.append('\t');
                    if(elemss[i] != null){
                        buf.append(elemss[i].toString()).append(lineSeparator);
                    }else{
                        buf.append("null").append(lineSeparator);
                    }
                }
            }
            for(Throwable ee = getCause(e); ee != null; ee = getCause(ee)){
                buf.append("Caused by:").append(ee.toString()).append(lineSeparator);
                final StackTraceElement[] elems = ee.getStackTrace();
                if(elems != null){
                    for(int i = 0; i < elems.length; i++){
                        buf.append('\t');
                        if(elems[i] != null){
                            buf.append(elems[i].toString()).append(lineSeparator);
                        }else{
                            buf.append("null").append(lineSeparator);
                        }
                    }
                }
            }
            System.out.println(buf.toString());
            exitCode = -1;
        }finally{
            if(servicePaths != null){
                for(int i = servicePaths.size(); --i >= 0;){
                    ServiceManagerFactory.unloadManager((String)servicePaths.get(i));
                }
            }
            if(serviceDirs != null){
                for(int i = serviceDirs.size(); --i >= 0;){
                    String[] array = (String[])serviceDirs.get(i);
                    ServiceManagerFactory.unloadManagers(array[0], array[1]);
                }
            }
        }
        System.exit(exitCode);
    }
    
    private static Throwable getCause(Throwable th){
        Throwable cause = th.getCause();
        return cause == th ? null : cause;
    }
}