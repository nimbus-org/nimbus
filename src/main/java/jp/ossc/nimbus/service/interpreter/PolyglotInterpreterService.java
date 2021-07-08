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
import java.util.function.*;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.ServiceNameEditor;
import jp.ossc.nimbus.beans.NimbusPropertyEditorManager;

/**
 * Polyglotを使ってコードを実行するインタープリタサービス。<p>
 * 
 * @author M.Takata
 */
public class PolyglotInterpreterService extends ServiceBase
 implements Interpreter, PolyglotInterpreterServiceMBean{
    
    protected String languageId = "js";
    protected Context.Builder contextBuilder;
    protected boolean isShareEngine;
    protected Engine engine;
    
    public void setLanguageId(String id){
        languageId = id;
    }
    public String getLanguageId(){
        return languageId;
    }
    
    public void setShareEngine(boolean share){
        isShareEngine = share;
    }
    public boolean isShareEngine(){
        return isShareEngine;
    }
    
    public void setContextBuilder(Context.Builder builder){
        contextBuilder = builder;
    }
    
    public void startService() throws Exception{
        if(isShareEngine){
            engine = Engine.create();
        }
    }
    
    public static Context.Builder getNashornCompatContextBuilder(){
        return Context.newBuilder(new String[]{"js"})
            .allowExperimentalOptions(true)
            .option("js.nashorn-compat", "true")
            .allowHostAccess(HostAccess.ALL)
            .allowHostClassLookup(new Predicate(){public boolean test(Object val){return true;}});
    }
    
    public static Context.Builder getAllHostClassContextBuilder(){
        return Context.newBuilder(new String[0])
            .allowHostClassLookup(new Predicate(){public boolean test(Object val){return true;}});
    }
    
    protected Context createContext(){
        if(contextBuilder != null){
            if(isShareEngine){
                return contextBuilder.engine(engine).build();
            }else{
                return contextBuilder.build();
            }
        }else{
            return Context.create(new String[]{languageId});
        }
    }
    
    public boolean isCompilable(){
        return isShareEngine;
    }
    
    public CompiledInterpreter compile(String code) throws EvaluateException{
        if(!isCompilable()){
            throw new EvaluateException("Compile is not supported.");
        }
        return new MyCompiledInterpreter(code);
    }
    
    public Object evaluate(String code) throws EvaluateException{
        return evaluate(code, null);
    }
    
    public Object evaluate(String code, Map variables) throws EvaluateException{
        Context context = null;
        try{
            context = createContext();
            if(variables != null && !variables.isEmpty()){
                Value bindings = context.getBindings(languageId);
                Iterator entries = variables.entrySet().iterator();
                while(entries.hasNext()){
                    Map.Entry entry = (Map.Entry)entries.next();
                    bindings.putMember((String)entry.getKey(), entry.getValue());
                }
            }
            Value val = context.eval(languageId, code);
            if(val.canExecute()){
                return val.execute(variables == null ? new Object[0] : variables.values().toArray()).as(Object.class);
            }else{
                return val.as(Object.class);
            }
        }catch(Exception e){
            throw new EvaluateException(e);
        }finally{
            if(context != null){
                try{
                    ((AutoCloseable)context).close();
                }catch(Exception e){}
            }
        }
    }
    
    protected class MyCompiledInterpreter implements CompiledInterpreter{
        
        protected final Source source;
        
        public MyCompiledInterpreter(String code){
            source = Source.create(languageId, code);
        }
        
        public Object evaluate() throws EvaluateException{
            return evaluate(null);
        }
        
        public Object evaluate(Map variables) throws EvaluateException{
            Context context = null;
            try{
                context = createContext();
                if(variables != null && !variables.isEmpty()){
                    Value bindings = context.getBindings(languageId);
                    Iterator entries = variables.entrySet().iterator();
                    while(entries.hasNext()){
                        Map.Entry entry = (Map.Entry)entries.next();
                        bindings.putMember((String)entry.getKey(), entry.getValue());
                    }
                }
                Value val = context.eval(source);
                if(val.canExecute()){
                    return val.execute(variables == null ? new Object[0] : variables.values().toArray()).as(Object.class);
                }else{
                    return val.as(Object.class);
                }
            }catch(Exception e){
                throw new EvaluateException(e);
            }finally{
                if(context != null){
                    try{
                        ((AutoCloseable)context).close();
                    }catch(Exception e){}
                }
            }
        }
    };
    
}