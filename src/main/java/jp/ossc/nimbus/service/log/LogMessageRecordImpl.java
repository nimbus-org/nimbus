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
package jp.ossc.nimbus.service.log;

import java.util.List;

import jp.ossc.nimbus.service.message.MessageRecordImpl;
import jp.ossc.nimbus.service.message.MessageRecordOperator;
import jp.ossc.nimbus.service.message.MessageRecordParseException;
import jp.ossc.nimbus.util.*;

//
//
/**
 *	�e�탍�O�̏o�̓C���^�[�t�F�C�X���K�肷��B<BR>
 *	�p�t�H�[�}���X�ێ��̂��߂̃��O�̃L���[�Ǘ����s���B<BR>
 *	@author		Hirotaka.Nakano
 *	@version	1.00 �쐬�F2001.06.21 �| H.Nakano<BR>
 *				�X�V�F
 */
public class LogMessageRecordImpl extends MessageRecordImpl
								implements LogMessageRecord, MessageRecordOperator, java.io.Serializable{
	
    private static final long serialVersionUID = 6861222398118645636L;
    
    //## �����o�[�ϐ��錾 	##
	/**	���O�v���C�I���e�B			*/	
	protected int mMessagePriority = 0 ;
	/**	�J�e�S���[					*/	
	protected CsvArrayList mCategory = null ;
	protected boolean isPrintStackTrace = true;

	//## �萔��` 	##
	/**	�f�t�H���g�J�e�S���[			*/	
	private static final String C_DFAUTL_CATEGORY = "debug" ;
	/**	�f�t�H���g�J�e�S���[			*/	
	private static final String C_DFAUTL_LOCALE = "default" ;
	//
	/**
	 *	���O���R�[�h��`����������������o�[�փf�[�^�����[�h����B<br>
	 *	@param		defString	��`������<BR>
	 *	LOGCODE,LOGSTR,PRIORITY,CATEGORY0:CATEGORY1...n
	 *	APL0001,�G���[����(@1),100,root
	 */
	public void rec2Obj(String defString) throws MessageRecordParseException{
		/** �C�j�V�����ς݂����� */
		if(!mIsInitialized){
			/** �f�t�@�C���������� */
			CsvArrayList parser = new CsvArrayList();
			parser.split(defString,",");
			if(parser.size()<2){
				throw new MessageRecordParseException("Message define error message is " + defString ) ;
			}else{
				// ��{�f�[�^�i�[
				this.mMessageCode = parser.getStr(0);
				this.mMessageHash.put(C_DFAUTL_LOCALE,parser.getStr(1)) ;
			}
			if(parser.size()>2){
				this.mMessagePriority = StringOperator.convertInt((String)parser.getStr(2)) ;
			}
            if(mCategory == null){
			    this.mCategory = new CsvArrayList() ;
            }
			if(parser.size()>3){
				this.mCategory.split(parser.getStr(3),":") ;
			}else{
				this.mCategory.add(C_DFAUTL_CATEGORY) ;
			}
			if(parser.size()>4){
				isPrintStackTrace = Boolean.valueOf(parser.getStr(4)).booleanValue();
			}
		}
	}
	public boolean isPrintStackTrace(){
	    return isPrintStackTrace;
	}
	/**
	 *	�v���C�I���e�B���o�͂���B<br>
	 *	@return		int		�v���C�I���e�B�[<BR>
	 */
	public int getPriority() {
		return this.mMessagePriority ;
	}
	public void setPriority(int priority) {
		this.mMessagePriority = priority;
	}
	/**
	 *	�J�e�S���[���擾����B<br>
	 *	@return		CsvArrayList	�J�e�S���[�R�[�h<BR>
	 */
	public List getCategories(){
        if(mCategory == null){
            mCategory = new CsvArrayList();
        }
		return this.mCategory ;
	}
	public void setCategories(List categories){
        if(mCategory == null){
            mCategory = new CsvArrayList();
        }
		mCategory.clear();
		mCategory.addAll(categories);
	}
	public void addCategory(String category){
        if(mCategory == null){
            mCategory = new CsvArrayList();
        }
		if(!this.mCategory.contains(category)){
			this.mCategory.add(category);
		}
	}
	/**
	 *	�ݒ�ڍו�������擾����B<br>
	 *	@return		String		���O�R�[�h<BR>
	 *	LOGCODE;LOGSTR;COUNT;OCCUR TIME;PRIORITY;CATEGORY:CATEGORY
	 */
	public String toString(){
		StringBuffer ret = new StringBuffer() ;
		
		ret.append(super.toString());
		ret.append(";") ;
		//PRIORITY
		ret.append(this.mMessagePriority).append(";");
		//CATEGORY
        if(mCategory != null){
            ret.append(this.mCategory.join("#"));
        }
		return ret.toString();
	}
}
