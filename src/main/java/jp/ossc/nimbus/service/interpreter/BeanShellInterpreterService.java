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


import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.ServiceNameEditor;
import jp.ossc.nimbus.beans.NimbusPropertyEditorManager;

/**
 * BeanShell(http://www.beanshell.org/)を使ってJavaコードを実行するインタープリタサービス。<p>
 * 
 * @author M.Takata
 */
public class BeanShellInterpreterService extends ServiceBase
 implements Interpreter, BeanShellInterpreterServiceMBean{
    
    private static final long serialVersionUID = -3344112113293549493L;
    
    private Map variables;
    private ClassLoader classLoader;
    private String[] sourceFileNames;
    private boolean isNewInterpreterByEvaluate = true;
    private bsh.Interpreter interpreter;
    
    public void setVariableServiceName(String name, ServiceName serviceName){
        variables.put(name, new Variable(serviceName));
    }
    public ServiceName getVariableServiceName(String name){
        return variables.containsKey(name) ? (ServiceName)((Variable)variables.get(name)).value : null;
    }
    
    public void setVariableObject(String name, Object val){
        variables.put(name, new Variable(val));
    }
    public Object getVariableObject(String name){
        return variables.containsKey(name) ? ((Variable)variables.get(name)).value : null;
    }
    
    public void setVariableInt(String name, int val){
        variables.put(name, new Variable(val));
    }
    public int getVariableInt(String name){
        return ((Number)((Variable)variables.get(name)).value).intValue();
    }
    
    public void setVariableLong(String name, long val){
        variables.put(name, new Variable(val));
    }
    public long getVariableLong(String name){
        return ((Number)((Variable)variables.get(name)).value).longValue();
    }
    
    public void setVariableFloat(String name, float val){
        variables.put(name, new Variable(val));
    }
    public float getVariableFloat(String name){
        return ((Number)((Variable)variables.get(name)).value).floatValue();
    }
    
    public void setVariableDouble(String name, double val){
        variables.put(name, new Variable(val));
    }
    public double getVariableDouble(String name){
        return ((Number)((Variable)variables.get(name)).value).doubleValue();
    }
    
    public void setVariableBoolean(String name, boolean val){
        variables.put(name, new Variable(val));
    }
    public boolean getVariableBoolean(String name){
        return ((Boolean)((Variable)variables.get(name)).value).booleanValue();
    }
    
    public void setClassLoader(ClassLoader loader){
        classLoader = loader;
    }
    public ClassLoader getClassLoader(){
        return classLoader;
    }
    
    public Map getVariables(){
        return variables;
    }
    
    public void setSourceFileNames(String[] names){
        sourceFileNames = names;
    }
    public String[] getSourceFileNames(){
        return sourceFileNames;
    }
    
    public void setNewInterpreterByEvaluate(boolean isNew){
        isNewInterpreterByEvaluate = isNew;
    }
    public boolean isNewInterpreterByEvaluate(){
        return isNewInterpreterByEvaluate;
    }
    
    public void createService() throws Exception{
        variables = new HashMap();
    }
    
    public void startService() throws Exception{
        if(!isNewInterpreterByEvaluate){
            interpreter = new bsh.Interpreter();
            setUpInterpreter(interpreter, null);
        }
    }
    
    public void destroyService() throws Exception{
        variables = null;
    }
    
    public boolean isCompilable(){
        return false;
    }
    
    public CompiledInterpreter compile(String code) throws EvaluateException{
        throw new UnsupportedOperationException();
    }
    
    public Object evaluate(String code) throws EvaluateException{
        return evaluate(code, null);
    }
    
    private void setUpInterpreter(bsh.Interpreter interpreter, Map variables) throws bsh.EvalError, IOException{
        
        if(classLoader != null){
            interpreter.setClassLoader(classLoader);
        }
        Map variableMap = new HashMap();
        if(this.variables != null){
            variableMap.putAll(this.variables);
        }
        if(variables != null){
            variableMap.putAll(variables);
        }
        if(variableMap.size() != 0){
            Iterator entries = variableMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                String name = (String)entry.getKey();
                Object val = entry.getValue();
                if(val instanceof Variable){
                    Variable var = (Variable)val;
                    if(var.isPrimitive){
                        if(var.value instanceof Integer){
                            interpreter.set(name, ((Number)var.value).intValue());
                        }else if(var.value instanceof Long){
                            interpreter.set(name, ((Number)var.value).longValue());
                        }else if(var.value instanceof Float){
                            interpreter.set(name, ((Number)var.value).floatValue());
                        }else if(var.value instanceof Double){
                            interpreter.set(name, ((Number)var.value).doubleValue());
                        }else{
                            interpreter.set(name, ((Boolean)var.value).booleanValue());
                        }
                    }else if(var.isServiceName){
                        Object service = null;
                        try{
                            service = ServiceManagerFactory.getServiceObject((ServiceName)var.value);
                        }catch(ServiceNotFoundException e){
                        }
                        interpreter.set(name, service);
                    }else{
                        interpreter.set(name, var.value);
                    }
                }else{
                    interpreter.set(name, val);
                }
            }
        }
        if(sourceFileNames != null && sourceFileNames.length != 0){
            for(int i = 0; i < sourceFileNames.length; i++){
                interpreter.source(sourceFileNames[i]);
            }
        }
    }
    
    public Object evaluate(String code, Map variables) throws EvaluateException{
        if(getState() != STARTED){
            throw new EvaluateException("Service is not available.");
        }
        
        bsh.Interpreter interpreter
            = isNewInterpreterByEvaluate ? new bsh.Interpreter() : this.interpreter;
        
        try{
            setUpInterpreter(interpreter, variables);
            return interpreter.eval(code);
        }catch(bsh.ParseException e){
            throw new EvaluateException("Compile error.", e);
        }catch(bsh.TargetError e){
            throw new EvaluateException("Runtime error.", e);
        }catch(Throwable th){
            throw new EvaluateException(th);
        }
    }
    
    public static class Variable implements java.io.Serializable{
        
        private static final long serialVersionUID = -3344112113293549490L;
        
        boolean isPrimitive;
        boolean isServiceName;
        Object value;
        public Variable(ServiceName val){
            isServiceName = true;
            value = val;
        }
        public Variable(Object val){
            value = val;
        }
        public Variable(int val){
            isPrimitive = true;
            value = new Integer(val);
        }
        public Variable(long val){
            isPrimitive = true;
            value = new Long(val);
        }
        public Variable(float val){
            isPrimitive = true;
            value = new Float(val);
        }
        public Variable(double val){
            isPrimitive = true;
            value = new Double(val);
        }
        public Variable(boolean val){
            isPrimitive = true;
            value = val ? Boolean.TRUE : Boolean.FALSE;
        }
        public String toString(){
            return value == null ? "null" : value.toString();
        }
    }
    
    private static void usage(){
        System.out.println("コマンド使用方法：");
        System.out.println(" java jp.ossc.nimbus.service.interpreter.BeanShellInterpreterService [options] [source code]");
        System.out.println();
        System.out.println("[options]");
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
        System.out.println("    java -classpath classes;lib/bsh-2.0b4.jar;lib/nimbus.jar jp.ossc.nimbus.service.interpreter.BeanShellInterpreterService System.out.println(\"test\");");
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
        String key = null;
        ServiceName serviceName = null;
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
                }else if(args[i].equals("-help")){
                    usage();
                    return;
                }else{
                    codeWriter.print(" " + args[i]);
                }
            }
        }
        Interpreter interpreter = null;
        if(servicePaths == null){
            BeanShellInterpreterService service = new BeanShellInterpreterService();
            service.create();
            service.start();
            interpreter = service;
        }else{
            for(int i = 0, imax = servicePaths.size(); i < imax; i++){
                if(!ServiceManagerFactory.loadManager((String)servicePaths.get(i))){
                    System.out.println("Service load error." + servicePaths.get(i));
                    Thread.sleep(1000);
                    System.exit(-1);
                }
            }
            if(!ServiceManagerFactory.checkLoadManagerCompleted()){
                Thread.sleep(1000);
                System.exit(-1);
            }
            if(serviceName == null){
                serviceName = new ServiceName("Nimbus", "Interpreter");
            }
            interpreter = (Interpreter)ServiceManagerFactory
                .getServiceObject(serviceName);
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
        }
        System.exit(exitCode);
    }
    
    private static Throwable getCause(Throwable th){
        Throwable cause = th.getCause();
        return cause == th ? null : cause;
    }
}