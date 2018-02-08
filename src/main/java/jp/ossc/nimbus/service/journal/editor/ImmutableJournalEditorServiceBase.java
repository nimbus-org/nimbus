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

import java.io.*;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.service.journal.*;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * �W���[�i����s�ςȕ�����Ƀt�H�[�}�b�g����G�f�B�^�T�[�r�X�̊��N���X�B<p>
 * 
 * @author M.Takata
 */
public abstract class ImmutableJournalEditorServiceBase extends ServiceBase
 implements ImmutableJournalEditorServiceBaseMBean, Serializable{
    
    private static final long serialVersionUID = 481914013077689219L;

    /**
     * �󕶎����e�����B<p>
     */
    protected static final String EMPTY_STRING = "";
    
    /**
     * "null"�������e�����B<p>
     */
    protected static final String NULL_STRING = "null";
    
    /**
     * Back Space�����B<p>
     */
    protected static final char BACK_SPACE = '\b';
    
    /**
     * Carriage Return�����B<p>
     */
    protected static final char CARRIAGE_RETURN  = '\r';
    
    /**
     * Line Feed�����B<p>
     */
    protected static final char LINE_FEED  = '\n';
    
    /**
     * Form Feed�����B<p>
     */
    protected static final char FORM_FEED  = '\f';
    
    /**
     * Horizontal Tab�����B<p>
     */
    protected static final char HORIZONTAL_TAB = '\t';
    
    private static final String LINE_SEPARATOR
         = System.getProperty("line.separator");
    private static final String INDENT_STRING = "  ";
    
    private String lineSeparator = LINE_SEPARATOR;
    private String indent = INDENT_STRING;
    
    private boolean isOutputKey = false;
    private boolean isOutputIndent = true;
    
    // ImmutableJournalEditorServiceBaseMBean��JavaDoc
    public void setOutputKey(boolean isOutput){
        isOutputKey = isOutput;
    }
    
    // ImmutableJournalEditorServiceBaseMBean��JavaDoc
    public boolean isOutputKey(){
        return isOutputKey;
    }
    
    // ImmutableJournalEditorServiceBaseMBean��JavaDoc
    public void setOutputIndent(boolean isOutput){
        isOutputIndent = isOutput;
    }
    
    // ImmutableJournalEditorServiceBaseMBean��JavaDoc
    public boolean isOutputIndent(){
        return isOutputIndent;
    }
    
    // ImmutableJournalEditorServiceBaseMBean��JavaDoc
    public void setLineSeparator(String separator){
        lineSeparator = convertEscapeLiteral(separator);
    }
    
    // ImmutableJournalEditorServiceBaseMBean��JavaDoc
    public String getLineSeparator(){
        return lineSeparator;
    }
    
    // ImmutableJournalEditorServiceBaseMBean��JavaDoc
    public void setIndent(String indent){
        this.indent = indent;
    }
    
    // ImmutableJournalEditorServiceBaseMBean��JavaDoc
    public String getIndent(){
        return indent;
    }
    
    // JournalEditor��JavaDoc
    public Object toObject(EditorFinder finder, Object key, Object value){
        return toString(finder, key, value);
    }
    
    // ImmutableJournalEditor��JavaDoc
    public String toString(EditorFinder finder, Object key, Object value){
        final StringBuffer buf = new StringBuffer();
        if(isOutputKey()){
            makeKeyFormat(finder, key, value, buf);
        }
        return toString(finder, key, value, buf);
    }
    
    /**
     * �w�肳�ꂽ�I�u�W�F�N�g���W���[�i��������ɕҏW����B<p>
     * �T�u�N���X�ŁA�I�[�o�[���C�h���Ď�������B�f�t�H���g�ł́Abuf���炻�̂܂ܕ�������擾���ĕԂ��B<br>
     *
     * @param finder EditorFinder�T�[�r�X
     * @param key �L�[������
     * @param value �W���[�i���I�u�W�F�N�g
     * @param buf �W���[�i��������i�[�p�̕�����o�b�t�@
     * @return �W���[�i��������
     */
    protected String toString(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuffer buf
    ){
        return buf.toString();
    }
    
    /**
     * �L�[��������t�H�[�}�b�g���āA������o�b�t�@�̐擪�ɕt�^����B<p>
     * �����ł́A"[key]"�̃t�H�[�}�b�g�Ƃ��Ď�������Ă���B�A���Akey��null�̏ꍇ�́A�o�͂��Ȃ��B<br>
     *
     * @param finder EditorFinder�T�[�r�X
     * @param key �L�[������
     * @param obj �W���[�i���I�u�W�F�N�g
     * @param buf �W���[�i��������i�[�p�̕�����o�b�t�@
     * @return �W���[�i��������
     */
    protected StringBuffer makeKeyFormat(
        EditorFinder finder,
        Object key,
        Object obj,
        StringBuffer buf
    ){
        if(key == null){
            return buf;
        }
        return buf.append('[').append(key).append(']');
    }
    
    /**
     * �G�f�B�^�[���s���ȃI�u�W�F�N�g��K�؂ȃG�f�B�^�ŕҏW����B<p>
     * finder�ŁAvalue�̌^�ɑΉ�����G�f�B�^���擾���ĕҏW����B�G�f�B�^��������Ȃ��ꍇ�́A{@link Object#toString()}�ŕ�����ɕϊ����ĕԂ��B<br>
     *
     * @param finder EditorFinder�T�[�r�X
     * @param key �L�[������
     * @param obj �W���[�i���I�u�W�F�N�g
     * @param buf �W���[�i��������i�[�p�̕�����o�b�t�@
     * @return �W���[�i��������
     */
    protected StringBuffer makeObjectFormat(
        EditorFinder finder,
        Object key,
        Object obj,
        StringBuffer buf
    ){
        Object value = null;
        if(obj != null){
            final JournalEditor editor = finder.findEditor(key, obj.getClass());
            if(editor != null){
                value = editor.toObject(finder, key, obj);
            }else{
                value = obj.toString();
            }
        }
        return buf.append(value);
    }
    
    /**
     * �w�肳�ꂽ������o�b�t�@�Ɋi�[����Ă��镶�����1�C���f���g����������B<p>
     *
     * @param buf ������o�b�t�@
     * @return ������o�b�t�@
     */
    protected StringBuffer addIndent(StringBuffer buf){
        if(!isOutputIndent){
            return buf;
        }
        return setIndent(buf, 1);
    }
    
    /**
     * �w�肳�ꂽ�������1�C���f���g����������B<p>
     *
     * @param str ������
     * @return ������
     */
    protected String addIndent(String str){
        if(!isOutputIndent){
            return str;
        }
        return setIndent(str, 1);
    }
    
    /**
     * �w�肳�ꂽ������o�b�t�@�Ɋi�[����Ă��镶������w��C���f���g����������B<p>
     *
     * @param buf ������o�b�t�@
     * @param indent �C���f���g��
     * @return ������o�b�t�@
     */
    protected StringBuffer setIndent(StringBuffer buf, int indent){
        if(!isOutputIndent){
            return buf;
        }
        final String str = buf.toString();
        buf.setLength(0);
        return buf.append(setIndent(str, indent));
    }
    
    /**
     * �w�肳�ꂽ��������w��C���f���g����������B<p>
     *
     * @param str ������
     * @param indent �C���f���g��
     * @return ������
     */
    protected String setIndent(String str, int indent){
        if(str == null){
            return null;
        }
        if(!isOutputIndent || indent == 0 || getIndent() == null || getIndent().length() == 0){
            return str;
        }
        final int length = str.length();
        if(length == 0){
            return str;
        }
        final StringBuffer buf = new StringBuffer();
        String indentString = null;
        if(indent <= 0){
            indentString = getIndent();
        }else{
            for(int i = 0; i < indent; i++){
                buf.append(getIndent());
            }
            indentString = buf.toString();
            buf.setLength(0);
        }
        final int indentLength = indentString.length();
        final int lsLength = getLineSeparator().length();
        buf.append(str);
        buf.insert(0, indentString);
        int index = 0;
        int offset = 0;
        while((index = buf.indexOf(getLineSeparator(), offset)) != -1
            && index + lsLength != buf.length()){
            buf.insert(index + lsLength, indentString);
            offset = index + lsLength + indentLength;
        }
        return buf.toString();
    }
    
    private String convertEscapeLiteral(String str){
        if(str == null){
            return null;
        }
        final int length = str.length();
        if(length == 0){
            return str;
        }
        final int index = str.indexOf('\\');
        if(index == -1){
            return str;
        }
        final StringBuffer buf = new StringBuffer(str.substring(index));
        boolean isInEscape = false;
        for(int i = index; i < length; i++){
            char c = str.charAt(i);
            if(!isInEscape && c == '\\'){
                isInEscape = true;
                continue;
            }
            if(isInEscape){
                switch(c){
                case 'b':
                    c = BACK_SPACE;
                    break;
                case 'f':
                    c = FORM_FEED;
                    break;
                case 'n':
                    c = LINE_FEED;
                    break;
                case 'r':
                    c = CARRIAGE_RETURN;
                    break;
                case 't':
                    c = HORIZONTAL_TAB;
                    break;
                case '\\':
                    break;
                default:
                    buf.append('\\');
                    break;
                }
            }
            buf.append(c);
            isInEscape = false;
        }
        return buf.toString();
    }
}
