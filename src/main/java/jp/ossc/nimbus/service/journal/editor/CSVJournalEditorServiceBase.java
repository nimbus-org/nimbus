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
import java.util.*;

import jp.ossc.nimbus.service.journal.JournalEditor;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * CSV形式のジャーナルをフォーマットするエディタサービスの基底クラス。<p>
 * 
 * @author M.Takata
 */
public abstract class CSVJournalEditorServiceBase
 extends ImmutableJournalEditorServiceBase
 implements CSVJournalEditorServiceBaseMBean, Serializable{
    
    private static final long serialVersionUID = -1461229603659333648L;
    
    protected static final String CSV_SEPARATOR = ",";
    
    private static final String LINE_FEED_STRING = "\\n";
    private static final String CARRIAGE_RETURN_STRING = "\\r";
    private static final String CSV_ENCLOSE_STRING = "\"\"";
    protected static final char CSV_ENCLOSE_CHAR = '"';
    
    protected ThreadLocal csvElements;
    private String csvSeparator = CSV_SEPARATOR;
   
    protected void preCreateService() throws Exception{
        super.preCreateService();
        csvElements = new ThreadLocal();
    }
    
    protected void postDestroyService() throws Exception{
        super.postDestroyService();
        csvElements = null;
    }
    
    public void setCSVSeparator(String separator){
        csvSeparator = separator;
    }
    
    public String getCSVSeparator(){
        return csvSeparator;
    }
    
    protected void addElements(Object[] objs){
        if(objs == null || objs.length == 0){
            return;
        }
        for(int i = 0; i < objs.length; i++){
            addElement(objs[i]);
        }
    }
    
    protected void addElement(Object obj){
        List elements = (List)csvElements.get();
        if(elements == null){
            elements = new ArrayList();
            csvElements.set(elements);
        }
        elements.add(obj);
    }
    
    protected List replaceNewCSVElements(){
        List elements = (List)csvElements.get();
        if(elements == null){
            elements = new ArrayList();
            csvElements.set(elements);
        }else{
            csvElements.set(new ArrayList());
            return elements;
        }
        return null;
    }
    
    protected void setCSVElements(List elements){
        csvElements.set(elements);
    }
    
    protected String toString(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
        final List oldElements = replaceNewCSVElements();
        processCSV(finder, key, value);
        makeCSVFormat(finder, key, value, buf);
        setCSVElements(oldElements);
        return buf.toString();
    }
    
    protected void processCSV(
        EditorFinder finder,
        Object key,
        Object value
    ){
    }
    
    protected void makeCSVFormat(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
        final List elements = (List)csvElements.get();
        if(elements != null){
            final StringBuilder tmpBuf = new StringBuilder();
            final Iterator values = elements.iterator();
            while(values.hasNext()){
                final Object val = values.next();
                final JournalEditor editor = finder.findEditor(key, val);
                tmpBuf.setLength(0);
                makeObjectFormat(finder, key, val, tmpBuf);
                escape(editor, tmpBuf);
                enclose(editor, tmpBuf);
                buf.append(tmpBuf);
                if(values.hasNext()){
                    buf.append(getCSVSeparator());
                }
            }
            elements.clear();
        }
    }
    
    protected StringBuilder enclose(JournalEditor editor, StringBuilder buf){
        if(buf == null){
            return null;
        }
        if(editor != null && editor instanceof CSVJournalEditorServiceBase){
            return buf;
        }
        buf.insert(0, CSV_ENCLOSE_CHAR);
        buf.append(CSV_ENCLOSE_CHAR);
        return buf;
    }
    
    protected String enclose(JournalEditor editor, String str){
        if(editor != null && editor instanceof CSVJournalEditorServiceBase){
            return str;
        }
        return CSV_ENCLOSE_CHAR + str + CSV_ENCLOSE_CHAR;
    }
    
    protected StringBuilder escape(JournalEditor editor, StringBuilder buf){
        if(buf == null){
            return null;
        }
        if(buf.length() == 0){
            return buf;
        }
        if(editor != null && editor instanceof CSVJournalEditorServiceBase){
            return buf;
        }
        final String result = escape(editor, buf.toString());
        buf.setLength(0);
        return buf.append(result);
    }
    
    protected String escape(JournalEditor editor, String str){
        if(str == null){
            return null;
        }
        final int length = str.length();
        if(length == 0){
            return str;
        }
        if(editor != null && editor instanceof CSVJournalEditorServiceBase){
            return str;
        }
        final StringBuilder buf = new StringBuilder();
        for(int i = 0; i < length; i++){
            final char c = str.charAt(i);
            switch(c){
            case CARRIAGE_RETURN:
                buf.append(CARRIAGE_RETURN_STRING);
                break;
            case LINE_FEED:
                buf.append(LINE_FEED_STRING);
                break;
            case CSV_ENCLOSE_CHAR:
                buf.append(CSV_ENCLOSE_STRING);
                break;
            default:
                buf.append(c);
            }
        }
        return buf.toString();
    }
}
