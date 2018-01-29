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
 * ���ږ����������W���[�i�����t�H�[�}�b�g����G�f�B�^�T�[�r�X�̊��N���X�B<p>
 * 
 * @author M.Takata
 */
public abstract class ItemJournalEditorServiceBase
 extends ImmutableJournalEditorServiceBase
 implements ItemJournalEditorServiceBaseMBean, Serializable{
    
    private static final long serialVersionUID = 6656657866441832807L;
    
    private String itemName = EMPTY_STRING;
    
    private boolean isOutputItemName = true;
    
    public void setItemName(String name){
        itemName = name;
    }
    
    public String getItemName(){
        return itemName;
    }
    
    public void setOutputItemName(boolean isOutput){
        isOutputItemName = isOutput;
    }
    
    public boolean isOutputItemName(){
        return isOutputItemName;
    }
    
    protected String toString(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
        if(isOutputItemName){
            buf.append(getItemName());
        }
        processItem(finder, key, value, buf);
        return buf.toString();
    }
    
    /**
     * ���ڂ̓��e��ҏW����B<p>
     * �f�t�H���g�́A������Ȃ̂ŁA�T�u�N���X�ŃI�[�o�[���C�h���ď�����ǉ����邱�ƁB<br>
     *
     * @param finder EditorFinder�T�[�r�X
     * @param key �L�[������
     * @param value �W���[�i���I�u�W�F�N�g
     * @param buf �W���[�i��������i�[�p�̕�����o�b�t�@
     */
    protected void processItem(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
    }
}
