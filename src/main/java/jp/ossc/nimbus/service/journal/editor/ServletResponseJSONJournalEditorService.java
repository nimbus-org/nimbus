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

import javax.servlet.ServletResponse;

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * {@link ServletResponse}��JSON�`��������ɕҏW����W���[�i���G�f�B�^�[�B<p>
 *
 * @author M.Takata
 */
public class ServletResponseJSONJournalEditorService
 extends JSONJournalEditorService
 implements ServletResponseJSONJournalEditorServiceMBean{
    
    private static final long serialVersionUID = -1841071109971741467L;
    
    protected StringBuffer appendUnknownValue(StringBuffer buf, EditorFinder finder, Class type, Object value){
        if(!(value instanceof ServletResponse)){
            return super.appendUnknownValue(buf, finder, type, value);
        }
        final ServletResponse response = (ServletResponse)value;
        
        buf.append(OBJECT_ENCLOSURE_START);
        appendServletResponse(buf, finder, response, false);
        buf.append(OBJECT_ENCLOSURE_END);
        return buf;
    }
    
    protected boolean appendServletResponse(StringBuffer buf, EditorFinder finder, ServletResponse response, boolean isAppended){
        isAppended |= appendBufferSize(buf, finder, response, isAppended);
        isAppended |= appendCharacterEncoding(buf, finder, response, isAppended);
        isAppended |= appendContentType(buf, finder, response, isAppended);
        isAppended |= appendLocale(buf, finder, response, isAppended);
        isAppended |= appendIsCommitted(buf, finder, response, isAppended);
        return isAppended;
    }
    
    protected boolean appendBufferSize(StringBuffer buf, EditorFinder finder, ServletResponse response, boolean isAppended){
        if(isOutputProperty(PROPERTY_BUFFER_SIZE)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_BUFFER_SIZE,
                new Integer(response.getBufferSize())
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendCharacterEncoding(StringBuffer buf, EditorFinder finder, ServletResponse response, boolean isAppended){
        if(isOutputProperty(PROPERTY_CHARACTER_ENCODING)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_CHARACTER_ENCODING,
                response.getCharacterEncoding()
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendContentType(StringBuffer buf, EditorFinder finder, ServletResponse response, boolean isAppended){
        if(isOutputProperty(PROPERTY_CONTENT_TYPE)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_CONTENT_TYPE,
                response.getContentType()
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendLocale(StringBuffer buf, EditorFinder finder, ServletResponse response, boolean isAppended){
        if(isOutputProperty(PROPERTY_LOCALE)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_LOCALE,
                response.getLocale()
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendIsCommitted(StringBuffer buf, EditorFinder finder, ServletResponse response, boolean isAppended){
        if(isOutputProperty(PROPERTY_IS_COMMITTED)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_IS_COMMITTED,
                response.isCommitted() ? Boolean.TRUE : Boolean.FALSE
            );
            return true;
        }else{
            return false;
        }
    }
}