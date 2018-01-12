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
package jp.ossc.nimbus.service.journal.editor;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.service.journal.JournalEditor;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * オブジェクトをフォーマットするエディタ。<p>
 * 渡されたオブジェクトの型を見て、{@link EditorFinder}に設定された、型とエディタのマッピングを使って、処理を他のエディタに委譲する。また、渡されたオブジェクトの型が配列型の場合、各要素に対して同様の処理を行い、Object配列に変換する。<br>
 * EditorFinderでエディタを検索しても見つからない場合には、そのまま返す。<br>
 * 
 * @author M.Takata
 */
public class MutableObjectJournalEditorService extends ServiceBase
 implements MutableObjectJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = -2158326775826244589L;
    
    // JournalEditorのJavaDoc
    public Object toObject(EditorFinder finder, Object key, Object value){
        if(value == null){
            return null;
        }
        if(value.getClass().isArray()){
            return toArrayObject(finder, key, value);
        }
        final JournalEditor editor = finder.findEditor(key, value.getClass());
        if(editor != null && editor != this){
            final Object obj = editor.toObject(finder, key, value);
            return obj;
        }else{
            return value;
        }
    }
    
    protected Object toArrayObject(
        EditorFinder finder,
        Object key,
        Object values
    ){
        if(values == null){
            return null;
        }
        final int length = Array.getLength(values);
        if(length == 0){
            return values;
        }
        final Object[] objs = new Object[length];
        boolean isNullArray = true;
        for(int i = 0; i < length; i++){
            objs[i] = Array.get(values, i);
            if(objs[i] != null){
                isNullArray = false;
            }
        }
        if(isNullArray){
            return values;
        }
        for(int i = 0; i < length; i++){
            objs[i] = toObject(finder, null, objs[i]);
        }
        final Class sharedClass = getSharedSuperClass(objs);
        if(sharedClass.equals(Object.class)){
            return objs;
        }
        final Object array = Array.newInstance(sharedClass, length);
        for(int i = 0; i < length; i++){
            Array.set(array, i, objs[i]);
        }
        return array;
    }
    
    protected Class getSharedSuperClass(Object[] array){
        final int length = array.length;
        if(length == 0){
            return array.getClass().getComponentType();
        }
        boolean equalsAll = true;
        Class tmpClass = null;
        Set shared = null;
        Set impls = new HashSet();
        for(int i = 0; i < length; i++){
            if(array[i] != null){
                if(tmpClass == null){
                    tmpClass = array[i].getClass();
                }else if(equalsAll){
                    equalsAll &= tmpClass.equals(array[i].getClass());
                }
            }
            final Set set = getImplementsClassSet(array[i]);
            if(set == null){
                continue;
            }
            impls.add(set);
            if(shared == null){
                shared = set;
                continue;
            }
            shared.retainAll(set);
        }
        if(equalsAll && tmpClass != null){
            return tmpClass;
        }
        if(shared == null || shared.size() == 0){
            return array.getClass().getComponentType();
        }else if(shared.size() == 1){
            return (Class)shared.iterator().next();
        }
        return getLastChildClass(shared, impls);
    }
    
    private Set getImplementsClassSet(Object obj){
        if(obj == null){
            return null;
        }
        final Set set = new HashSet();
        final Class clazz = obj.getClass();
        set.add(clazz);
        final Class[] interfaces = clazz.getInterfaces();
        for(int i = 0; i < interfaces.length; i++){
            if(!set.contains(interfaces[i])){
                set.add(interfaces[i]);
            }
        }
        final Class superClass = clazz.getSuperclass();
        if(superClass != null){
            if(!set.contains(superClass)){
                set.add(superClass);
            }
        }
        return set;
    }
    
    private Class getLastChildClass(Set shared, Set impls){
        if(shared.size() == 0){
            return Object.class;
        }else if(shared.size() == 1){
            return (Class)shared.iterator().next();
        }
        final Iterator implsSets = impls.iterator();
        final Map counts = new HashMap();
        class Counter{
            int count = 1;
            public void increment(){
                count++;
            }
        }
        while(implsSets.hasNext()){
            final Set implsSet = (Set)implsSets.next();
            implsSet.retainAll(shared);
            final Iterator classes = implsSet.iterator();
            while(classes.hasNext()){
                final Class cls = (Class)classes.next();
                if(counts.containsKey(cls)){
                    ((Counter)counts.get(cls)).increment();
                }else{
                    counts.put(cls, new Counter());
                }
            }
        }
        Iterator classes = counts.keySet().iterator();
        final Set maxCounters = new HashSet();
        int max = 0;
        while(classes.hasNext()){
            final Class cls = (Class)classes.next();
            final Counter counter = (Counter)counts.get(cls);
            if(max == 0){
                max = counter.count;
                maxCounters.add(cls);
            }else{
                if(max < counter.count){
                    max = counter.count;
                    maxCounters.clear();
                    maxCounters.add(cls);
                }else if(max == counter.count){
                    maxCounters.add(cls);
                }
            }
        }
        if(maxCounters.size() == 1){
            return (Class)maxCounters.iterator().next();
        }
        classes = maxCounters.iterator();
        Class clazz = Object.class;
        while(classes.hasNext()){
            final Class cls = (Class)classes.next();
            if(clazz.isAssignableFrom(cls)){
                clazz = cls;
            }else{
                if(clazz.isInterface() && !cls.isInterface()){
                    clazz = cls;
                }
            }
        }
        return clazz;
    }
}