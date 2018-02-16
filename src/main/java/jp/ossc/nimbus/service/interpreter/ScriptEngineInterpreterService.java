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

import java.util.*;
import javax.script.*;

import jp.ossc.nimbus.core.*;

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
}