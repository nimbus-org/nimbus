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

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * �u���b�N���������W���[�i�����t�H�[�}�b�g����G�f�B�^�T�[�r�X�̊��N���X�B<p>
 * 
 * @author M.Takata
 */
public abstract class BlockJournalEditorServiceBase
 extends ImmutableJournalEditorServiceBase
 implements BlockJournalEditorServiceBaseMBean, Serializable{
    
    private static final long serialVersionUID = -7909316333619901629L;
    
    protected static final String START_BLOCK_SEPARATOR = "{";
    protected static final String END_BLOCK_SEPARATOR = "}";
    
    private String header = EMPTY_STRING;
    private String startBlockSeparator = START_BLOCK_SEPARATOR;
    private String endBlockSeparator = END_BLOCK_SEPARATOR;
    
    private boolean isOutputHeader = true;
    private boolean isOutputBlockLineSeparator = true;
    
    public void setHeader(String header){
        this.header = header;
    }
    
    public String getHeader(){
        return header;
    }
    
    public void setOutputHeader(boolean isOutput){
        isOutputHeader = isOutput;
    }
    
    public boolean isOutputHeader(){
        return isOutputHeader;
    }
    
    public void setOutputBlockLineSeparator(boolean isOutput){
        isOutputBlockLineSeparator = isOutput;
    }
    
    public boolean isOutputBlockLineSeparator(){
        return isOutputBlockLineSeparator;
    }
    
    public void setStartBlockSeparator(String start){
        startBlockSeparator = start;
    }
    
    public String getStartBlockSeparator(){
        return startBlockSeparator;
    }
    
    public void setEndBlockSeparator(String end){
        endBlockSeparator = end;
    }
    
    public String getEndBlockSeparator(){
        return endBlockSeparator;
    }
    
    protected String toString(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
        startBlock(finder, key, value, buf);
        if(isOutputBlockLineSeparator()){
            buf.append(getLineSeparator());
        }
        final StringBuilder subBuf = new StringBuilder();
        if(processBlock(finder, key, value, subBuf)){
            addIndent(subBuf);
            buf.append(subBuf);
        }
        if(isOutputBlockLineSeparator()){
            buf.append(getLineSeparator());
        }
        endBlock(finder, key, value, buf);
        return buf.toString();
    }
    
    /**
     * �u���b�N�̊J�n�ɕt�^���镶�����ҏW����B<p>
     * �f�t�H���g�́A�w�b�_�ƃu���b�N�J�n�Z�p���[�^���o�͂���B<br>
     *
     * @param finder EditorFinder�T�[�r�X
     * @param key �L�[������
     * @param value �W���[�i���I�u�W�F�N�g
     * @param buf �W���[�i��������i�[�p�̕�����o�b�t�@
     */
    protected void startBlock(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
        if(isOutputHeader()){
            buf.append(getHeader());
        }
        buf.append(getStartBlockSeparator());
    }
    
    /**
     * �u���b�N���̓��e��ҏW����B<p>
     * �f�t�H���g�́A������Ȃ̂ŁA�T�u�N���X�ŃI�[�o�[���C�h���ď�����ǉ����邱�ƁB<br>
     *
     * @param finder EditorFinder�T�[�r�X
     * @param key �L�[������
     * @param value �W���[�i���I�u�W�F�N�g
     * @param buf �W���[�i��������i�[�p�̕�����o�b�t�@
     * @return �u���b�N�̏o�͂��s��ꂽ���ǂ����������t���O�B���炩�̃f�[�^���o�͂��ꂽ�ꍇtrue
     */
    protected boolean processBlock(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
        return false;
    }
    
    /**
     * �u���b�N�̏I���ɕt�^���镶�����ҏW����B<p>
     * �f�t�H���g�́A�w�b�_�ƃu���b�N�I���Z�p���[�^���o�͂���B<br>
     *
     * @param finder EditorFinder�T�[�r�X
     * @param key �L�[������
     * @param value �W���[�i���I�u�W�F�N�g
     * @param buf �W���[�i��������i�[�p�̕�����o�b�t�@
     */
    protected void endBlock(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
        buf.append(getEndBlockSeparator());
    }
}
