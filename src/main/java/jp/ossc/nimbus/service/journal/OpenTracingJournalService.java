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
package jp.ossc.nimbus.service.journal;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;
import java.util.HashSet;
import java.util.Properties;
import java.util.Iterator;

import jp.ossc.nimbus.beans.Property;
import jp.ossc.nimbus.beans.PropertyFactory;
import jp.ossc.nimbus.beans.NoSuchPropertyException;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.aop.interceptor.ThreadContextKey;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;
import jp.ossc.nimbus.service.sequence.Sequence;

import io.opentracing.Tracer;
import io.opentracing.Span;
import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.SpanContext;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tag;
import io.opentracing.util.GlobalTracer;

/**
 * <a href="https://opentracing.io/">OpenTracing</a>を使った{@link Journal}サービス。<br>
 *
 * @author M.Takata
 */
public class OpenTracingJournalService extends ServiceBase implements Journal, Tracer, OpenTracingJournalServiceMBean{
    
    protected int journalLevel;
    protected boolean isUseGlobalTracer = true;
    
    protected ServiceName editorFinderServiceName;
    protected EditorFinder editorFinder;
    
    protected ServiceName sequenceServiceName;
    protected Sequence sequence;
    
    protected String[] tagKeys;
    protected Set tagKeySet;

    protected Map tagValueMapping;
    
    protected JournalTracer tracer;
    protected ThreadLocal<Stack<Scope>> scopeStack;
    
    public void setJournalLevel(int level){
        journalLevel = level;
    }
    public int getJournalLevel(){
        return journalLevel;
    }
    
    public void setUseGlobalTracer(boolean isUse){
        isUseGlobalTracer = isUse;
    }
    public boolean isUseGlobalTracer(){
        return isUseGlobalTracer;
    }
    
    public void setTagKeys(String[] keys){
        tagKeys = keys;
    }
    public String[] getTagKeys(){
        return tagKeys;
    }
    
    public void setTagValueMapping(String key, Properties mapping) throws IllegalArgumentException{
        Map tagAndProp = new HashMap();
        Iterator entries = mapping.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry)entries.next();
            Property prop = PropertyFactory.createProperty((String)entry.getValue());
            prop.setIgnoreNullProperty(true);
            tagAndProp.put((String)entry.getKey(), prop);
        }
        tagValueMapping.put(key, tagAndProp);
    }
    
    public void setEditorFinderServiceName(ServiceName name){
        editorFinderServiceName = name;
    }
    public ServiceName getEditorFinderServiceName(){
        return editorFinderServiceName;
    }
    
    public void setSequenceServiceName(ServiceName name){
        sequenceServiceName = name;
    }
    public ServiceName getSequenceServiceName(){
        return sequenceServiceName;
    }
    
    public void setEditorFinder(EditorFinder finder){
        editorFinder = finder;
    }
    
    public void setSequence(Sequence sequence){
        this.sequence = sequence;
    }
    
    public void createService(){
        scopeStack = new ThreadLocal<Stack<Scope>>(){
            protected Stack<Scope> initialValue(){
                return new Stack<Scope>();
            }
        };
        tagValueMapping = new HashMap();
    }
    
    protected void preStartService() throws Exception{
        super.preStartService();
        if(editorFinderServiceName != null){
            editorFinder = (EditorFinder)ServiceManagerFactory.getServiceObject(
                editorFinderServiceName
            );
        }
        if(editorFinder == null){
            throw new IllegalArgumentException("EditorFinder is null.");
        }
        if(sequenceServiceName != null){
            sequence = (Sequence)ServiceManagerFactory.getServiceObject(
                sequenceServiceName
            );
        }
        tagKeySet = new HashSet();
        if(tagKeys != null){
            for(int i = 0; i < tagKeys.length; i++){
                tagKeySet.add(tagKeys[i]);
            }
        }
    }
    
    protected void postStartService() throws Exception{
        Tracer tr = createTracer();
        if(tr != null && isUseGlobalTracer){
            GlobalTracer.registerIfAbsent(tr);
        }
        tracer = new JournalTracer(isUseGlobalTracer ? GlobalTracer.get() : tr);
        super.postStartService();
    }
    
    public void stopService() throws Exception{
        if(tracer != null){
            tracer.close();
            tracer = null;
        }
    }
    
    public void destroyService(){
        scopeStack = null;
    }
    
    protected Tracer createTracer() throws Exception{
        return null;
    }
    
    public String getRequestId(){
        if(tracer == null){
            return null;
        }
        Span span = tracer.activeSpan();
        if(span == null){
            return null;
        }
        return span.getBaggageItem(ThreadContextKey.REQUEST_ID);
    }
    
    public void setRequestId(String requestID){
        Span span = tracer == null ? null : tracer.activeSpan();
        if(span == null){
            return;
        }
        span.setBaggageItem(ThreadContextKey.REQUEST_ID, requestID);
        span.setTag(ThreadContextKey.REQUEST_ID, requestID);
    }
    
    public void startJournal(String key){
        startJournal(key, (Date)null);
    }
    
    public void startJournal(String key, EditorFinder finder){
        startJournal(key, null, finder);
    }
    
    public void startJournal(String key, Date startTime){
        startJournal(key, startTime, null);
    }
    
    public void startJournal(
        String key,
        Date startTime,
        EditorFinder finder
    ){
        if(tracer == null){
            return;
        }
        Span span = tracer.activeSpan();
        if(span == null){
            Tracer.SpanBuilder builder = tracer.buildSpan(key);
            if(startTime != null){
               builder = builder.withStartTimestamp(startTime.getTime() * 1000);
            }
            span = builder.start();
            ((JournalSpan)span).setEditorFinder(finder);
            scopeStack.get().push(tracer.activateSpan(span));
        }else{
            addStartStep(key, startTime);
        }
        if(sequence != null){
            String requestId = sequence.increment();
            setRequestId(requestId);
        }
    }
    
    public void endJournal(){
        endJournal(null);
    }
    
    public void endJournal(Date endTime){
        if(tracer == null){
            return;
        }
        Span span = tracer.activeSpan();
        if(span == null){
            return;
        }
        if(endTime != null){
            span.finish(endTime.getTime() * 1000);
        }else{
            span.finish();
        }
        scopeStack.get().pop().close();
    }
    
    public void addInfo(String key, Object value){
        addInfo(key, value, null);
    }
    
    public void addInfo(
        String key,
        Object value,
        EditorFinder finder
    ){
        if(tracer == null){
            return;
        }
        Span span = tracer.activeSpan();
        if(span == null){
            return;
        }
        if(finder == null){
            finder = ((JournalSpan)span).getEditorFinder();
            if(finder == null){
                finder = editorFinder;
            }
        }
        JournalEditor editor = finder.findEditor(key, value);
        Object edited = editor.toObject(finder, key, value);
        if(tagKeySet.contains(key)){
            if(edited instanceof Boolean){
                span.setTag(key, ((Boolean)edited).booleanValue());
            }else if(edited instanceof Number){
                span.setTag(key, (Number)edited);
            }else{
                span.setTag(key, edited == null ? (String)null : edited.toString());
            }
        }else{
            if(tagValueMapping.containsKey(key)){
                Map tagAndProp = (Map)tagValueMapping.get(key);
                Iterator entries = tagAndProp.entrySet().iterator();
                while(entries.hasNext()){
                    Map.Entry entry = (Map.Entry)entries.next();
                    String tag = (String)entry.getKey();
                    Property prop = (Property)entry.getValue();
                    Object propValue = null;
                    try{
                        propValue = prop.getProperty(edited);
                    }catch(NoSuchPropertyException e){
                    }catch(InvocationTargetException e){
                    }
                    if(propValue instanceof Boolean){
                        span.setTag(tag, ((Boolean)propValue).booleanValue());
                    }else if(propValue instanceof Number){
                        span.setTag(tag, (Number)propValue);
                    }else{
                        span.setTag(tag, propValue == null ? (String)null : propValue.toString());
                    }
                }
            }
            
            if(edited != null){
                if(edited instanceof Map){
                    span.log((Map)edited);
                }else{
                    span.log(edited.toString());
                }
            }
        }
    }
    
    public void addInfo(String key, Object value, int level){
        addInfo(key, value, null, level);
    }
    
    public void addInfo(
        String key,
        Object value,
        EditorFinder finder,
        int level
    ){
        if(level < getJournalLevel()){
            return;
        }
        addInfo(key, value, finder);
    }
    
    public void removeInfo(int from){
    }
    
    public void addStartStep(String key){
        addStartStep(key, null, null);
    }
    
    public void addStartStep(String key, EditorFinder finder){
        addStartStep(key, null, finder);
    }
    
    public void addStartStep(String key, Date startTime){
        addStartStep(key, startTime, null);
    }
    
    public void addStartStep(
        String key,
        Date startTime,
        EditorFinder finder
    ){
        if(tracer == null){
            return;
        }
        Span span = tracer.activeSpan();
        if(span == null){
            startJournal(key, startTime, finder);
        }else{
            Tracer.SpanBuilder builder = tracer.buildSpan(key);
            if(startTime != null){
               builder = builder.withStartTimestamp(startTime.getTime() * 1000);
            }
            builder.asChildOf(span);
            span = builder.start();
            ((JournalSpan)span).setEditorFinder(finder);
            scopeStack.get().push(tracer.activateSpan(span));
        }
    }
    
    public void addEndStep(){
        endJournal();
    }
    public void addEndStep(Date endTime){
        endJournal(endTime);
    }
    
    public String getCurrentJournalString(EditorFinder finder){
        return null;
    }
    
    public boolean isStartJournal(){
        Span span = tracer == null ? null : tracer.activeSpan();
        return span != null;
    }
    
    public ScopeManager scopeManager(){
        return tracer.scopeManager();
    }
    
    public Span activeSpan(){
        return tracer.activeSpan();
    }
    
    public Scope activateSpan(Span span){
        return tracer.activateSpan(span);
    }
    
    public Tracer.SpanBuilder buildSpan(String operationName){
        return tracer.buildSpan(operationName);
    }
    
    public <C> void inject(SpanContext spanContext, Format<C> format, C carrier){
        tracer.inject(spanContext, format, carrier);
    }
    
    public <C> SpanContext extract(Format<C> format, C carrier){
        return tracer.extract(format, carrier);
    }
    
    public void close(){
    }
    
    protected class JournalTracer implements Tracer{
        
        protected final Tracer tracer;
        
        public JournalTracer(Tracer tracer){
            this.tracer = tracer;
        }
        
        public ScopeManager scopeManager(){
            return tracer.scopeManager();
        }
        
        public Span activeSpan(){
            return tracer.activeSpan();
        }
        
        public Scope activateSpan(Span span){
            return tracer.activateSpan(span);
        }
        
        public Tracer.SpanBuilder buildSpan(String operationName){
            return new JournalSpanBuilder(tracer.buildSpan(operationName));
        }
        
        public <C> void inject(SpanContext spanContext, Format<C> format, C carrier){
            tracer.inject(spanContext, format, carrier);
        }
        
        public <C> SpanContext extract(Format<C> format, C carrier){
            return tracer.extract(format, carrier);
        }
        
        public void close(){
            tracer.close();
        }
    }
    
    protected class JournalSpanBuilder implements Tracer.SpanBuilder{
        
        protected final Tracer.SpanBuilder builder;
        
        public JournalSpanBuilder(Tracer.SpanBuilder builder){
            this.builder = builder;
        }
        
        public Tracer.SpanBuilder asChildOf(SpanContext parent){
            builder.asChildOf(parent);
            return JournalSpanBuilder.this;
        }
        
        public Tracer.SpanBuilder asChildOf(Span parent){
            builder.asChildOf(parent);
            return JournalSpanBuilder.this;
        }
        
        public Tracer.SpanBuilder addReference(String referenceType, SpanContext referencedContext){
            builder.addReference(referenceType, referencedContext);
            return JournalSpanBuilder.this;
        }
        
        public Tracer.SpanBuilder ignoreActiveSpan(){
            builder.ignoreActiveSpan();
            return JournalSpanBuilder.this;
        }
        
        public Tracer.SpanBuilder withTag(String key, String value){
            builder.withTag(key, value);
            return JournalSpanBuilder.this;
        }
        
        public Tracer.SpanBuilder withTag(String key, boolean value){
            builder.withTag(key, value);
            return JournalSpanBuilder.this;
        }
        
        public Tracer.SpanBuilder withTag(String key, Number value){
            builder.withTag(key, value);
            return JournalSpanBuilder.this;
        }
        
        public <T> Tracer.SpanBuilder withTag(Tag<T> tag, T value){
            builder.withTag(tag, value);
            return JournalSpanBuilder.this;
        }
        
        public Tracer.SpanBuilder withStartTimestamp(long microseconds){
            builder.withStartTimestamp(microseconds);
            return JournalSpanBuilder.this;
        }
        
        public Span start(){
            return new JournalSpan(builder.start());
        }
    }
    
    protected class JournalSpan implements Span{
        protected EditorFinder finder;
        protected final Span span;
        
        public JournalSpan(Span span){
            this.span = span;
            this.finder = finder;
        }
        
        public void setEditorFinder(EditorFinder finder){
            this.finder = finder;
        }
        
        public EditorFinder getEditorFinder(){
            return finder;
        }
        
        public SpanContext context(){
            return span.context();
        }
        
        public Span setTag(String key, String value){
            return span.setTag(key, value);
        }
        
        public Span setTag(String key, boolean value){
            return span.setTag(key, value);
        }
        
        public Span setTag(String key, Number value){
            return span.setTag(key, value);
        }
        
        public <T> Span setTag(Tag<T> tag, T value){
            return span.setTag(tag, value);
        }
        
        public Span log(Map<String, ?> fields){
            return span.log(fields);
        }
        
        public Span log(long timestampMicroseconds, Map<String, ?> fields){
            return span.log(timestampMicroseconds, fields);
        }
        
        public Span log(String event){
            return span.log(event);
        }
        
        public Span log(long timestampMicroseconds, String event){
            return span.log(timestampMicroseconds, event);
        }
        
        public Span setBaggageItem(String key, String value){
            return span.setBaggageItem(key, value);
        }
        
        public String getBaggageItem(String key){
            return span.getBaggageItem(key);
        }
        
        public Span setOperationName(String operationName){
            return span.setOperationName(operationName);
        }
        
        public void finish(){
            span.finish();
        }
        
        public void finish(long finishMicros){
            span.finish(finishMicros);
        }
    }
}