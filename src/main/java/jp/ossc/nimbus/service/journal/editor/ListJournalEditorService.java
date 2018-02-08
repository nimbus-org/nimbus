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
import java.util.List;

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * Listをフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class ListJournalEditorService extends BlockJournalEditorServiceBase
 implements ListJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = -8798740768642559809L;

    protected static final String MAX_SIZE_OVER = "...";
    
    protected static final String HEADER = "[List]";
    
    protected int maxSize = -1;
    
    public ListJournalEditorService(){
        super();
        setHeader(HEADER);
    }
    
    public void setMaxSize(int max){
        maxSize = max;
    }
    
    public int getMaxSize(){
        return maxSize;
    }
    
    protected boolean processBlock(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
        final List list = (List)value;
        makeListFormat(finder, key, list, buf);
        return true;
    }
    
    protected StringBuilder makeListFormat(
        EditorFinder finder,
        Object key,
        List list,
        StringBuilder buf
    ){
        final int size = list.size();
        if(size == 0){
            buf.append("[]");
            return buf;
        }else{
        }
        buf.append('[');
        buf.append(getLineSeparator());
        final StringBuilder subBuf = new StringBuilder();
        for(int i = 0, max = (maxSize > 0 && maxSize < size) ? maxSize : size; i <= max; i++){
            if(i != max){
                Object element = list.get(i);
                makeObjectFormat(finder, null, element, subBuf);
                subBuf.append(getLineSeparator());
            }else if(list.size() > max){
                subBuf.append(MAX_SIZE_OVER);
                subBuf.append(getLineSeparator());
            }
        }
        addIndent(subBuf);
        buf.append(subBuf);
        return buf.append(']');
    }
}