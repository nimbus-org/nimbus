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
package jp.ossc.nimbus.util;

import java.util.ArrayList;
import java.util.ListIterator;
import java.io.*;
/**
*	CSV����������I�u�W�F�N�g
*	@author		Hirotaka.Nakano
*	@version	1.00 �쐬�F2001.04.04 �| H.Nakano<BR>
*				�X�V�F
*/
public class CsvArrayList extends ArrayList implements java.io.Serializable{
	
    private static final long serialVersionUID = -2858942004554521568L;
    
    //## �N���X�����o�[�ϐ��錾 ##
	/** CSV��؂蕶��			*/		private String mSeptData ;
	/** �G�X�P�[�v�����萔		*/		private String mEscapeString ;
	/** �����p�I�u�W�F�N�g		*/		private Object mObjSync ;
	/** �ŏI�f�~���^�[�t���敪	*/		private boolean mAddDemiliter ;
	static public final String ESP_STR = "\u001C"; //$NON-NLS-1$
	static protected final String C_LINESEPT = "line.separator" ; //$NON-NLS-1$
	static protected final String C_COMMMA = "," ; //$NON-NLS-1$
	static protected final String C_NONE ="" ; //$NON-NLS-1$
	static protected final String C_ESCAPE ="\\" ;//$NON-NLS-1$
	protected static final String CR = "\r";
	protected static final String LF = "\n";
	protected static final String CRLF = "\r\n";
	//
	//
	/**
	 *	�R���X�g���N�^<br>
	 *	��ʃN���X�̃R���X�g���N�^�[�R�[��
	 */
	public CsvArrayList() {
		super() ;
		mSeptData = C_COMMMA ;
		mEscapeString = C_ESCAPE ;
		mAddDemiliter = false;
		mObjSync = new String();
	}
	//
	/**
	 *	�ŏI��؂蕶���t���t���O�Z�b�^�[
	 *	@param		flgAddDelimita	�ŏI��؂蕶���t���t���O
	 */
	public void setAddDelimitaFlg(boolean flgAddDelimita )	 {
		synchronized(mObjSync){
			mAddDemiliter = flgAddDelimita ;
		}
	}
	//
	/**
	 *	ECP�����Z�b�^�[<BR>
	 *	�Z�p���[�^�������G�X�P�[�v���镶����ݒ肷��<br>
	 *	�ݒ�Ȃ��̏ꍇ��\�����ɂȂ��Ă���B
	 *	@param		strEscape	�G�X�P�[�v������
	 */
	public void setEscapeString(String strEscape )	 {
		synchronized(mObjSync){
			// �G�X�P�[�v�����ݒ�
			mEscapeString = strEscape ;
		}
	}
	//
	/**
	 *	�����v���C�x�[�g���\�b�h<br>
	 *	�V���N���͏��public���\�b�h�ł����鎖
	 *	@param		strInData	�����Ώە�����
	 *	@return		�������ڐ�
	 */
	private int _splitString(String strInData )	 {
		int lngFindNum ;
		int lngMindNum ;
		StringBuffer subStr1 ;
		StringBuffer subStr2 ;
		// ���͕�?��
		//== �o�͔z�񏉊��� ==
		super.clear() ;
		if (strInData != null && strInData.length()!=0){
			//## ��؂蕶������ ##
			lngMindNum = 0 ;
			subStr1 = new StringBuffer(C_NONE) ;
			subStr2 = new StringBuffer(StringOperator.replaceString(strInData,mEscapeString + mEscapeString,ESP_STR)) ;
			//== �ŏI�f�~���^�[�����폜 ==
			if(mAddDemiliter){
				if(subStr2.substring(subStr2.length()-mSeptData.length()).equals(mSeptData)){
					subStr2 = new StringBuffer(subStr2.substring(0,subStr2.length()-mSeptData.length()));
				}
			}
			while(true){
				//== �����񌟍� ==
				lngFindNum = subStr2.toString().indexOf(mSeptData) ;
				lngMindNum += lngFindNum ;
				//== ����������������Ȃ��ꍇ ==
				if (lngFindNum == -1) {
					if(subStr1.length()>0){ 
						super.add(subStr1.toString()) ;
					}else{
						super.add(subStr2.toString()) ;
					}
					break;
				//== �������������������ꍇ ==
				}else {
					// 2�����ڈȍ~�ɔ��������ꍇ
					if(lngFindNum >= 0){
						//## ESC�����Ή� ##
						if(lngFindNum > 0 && subStr2.substring(lngFindNum-1,lngFindNum).equals(mEscapeString)){
							subStr1 = new StringBuffer(subStr1.append(subStr2.substring(0,lngFindNum-1).toString()).toString());
							subStr1.append(mSeptData);
							final String postStr = subStr2.substring(lngFindNum+mSeptData.length());
							if(postStr.indexOf(mSeptData) != -1){
								subStr2 = new StringBuffer(postStr);
							}else{
								subStr1.append(postStr);
								subStr2 = new StringBuffer(C_NONE);
							}
							continue;
						} else{
							subStr1.append(subStr2.substring(0,lngFindNum)) ;
						}

					// 1�����ڂɔ��������ꍇ
					} else {
						subStr1 = new StringBuffer(C_NONE);
					}
					// ���̌����ʒu�܂ŃV�[�N����B
					String tmp = StringOperator.replaceString(subStr1.toString(),ESP_STR, mEscapeString) ;
					super.add(tmp) ;
					lngMindNum= 0 ;
					subStr2 = new StringBuffer(subStr2.substring(lngFindNum+mSeptData.length()));
					subStr1 = new StringBuffer(C_NONE) ;
				}
			}
		}
		//## ����������Ԃ� ##
		return super.size();
	}
	//
	/**
	 *	�������\�b�h<br>
	 *	";"�����̃Z�p���[�g���s���B
	 *	@param		strInData	�����Ώە�����
	 *	@return		�������ڐ�
	 */
	public int split(String strInData) {
		int ret = 0;
		synchronized(mObjSync){
			// �f�t�H���g�؂蕶���ŕ��� /
			mSeptData = C_COMMMA ;
			ret = this._splitString(strInData) ;
		}
		return ret ;
	}
	//
	/**
	 *	�������\�b�h<br>
	 *	���s�����̃Z�p���[�g���s���B
	 *	@param		strInData	�����Ώە�����
	 *	@return		�������ڐ�
	 */
	public int splitCL(String strInData) {
		int ret = 0;
		synchronized(mObjSync){
			// �}���c��؂蕶���ŕ��� /
			if(strInData.indexOf(CRLF) != -1){
			    mSeptData = CRLF;
			}else if(strInData.indexOf(LF) != -1){
			    mSeptData = LF;
			}else if(strInData.indexOf(CR) != -1){
			    mSeptData = CR;
			}else{
			    clear();
			    add(strInData);
			    return 1;
			}
			ret = this._splitString(strInData) ;
		}
		return ret ;
	}
	//
	/**
	 *	�������\�b�h�i�w���؂�j
	 *	@param		strInData	�����Ώە�����
	 *	@param		strSept		�Z�p���[�^������
	 *	@return		�������ڐ�
	 */
	public int split(String strInData,String strSept) {
		int ret = 0 ;
		synchronized(mObjSync){
			mSeptData = strSept ;
			ret = _splitString(strInData) ;
		}
		return ret ;
	}
	//
	/**
	 *	�������\�b�h�i�G�N�Z��CSV�`���j<br>
	 *	�G�N�Z��CSV�`���̕������s��
	 *	@param		strInData	�G�N�Z��CSV�`���̕�����
	 *	@return		�������ڐ�
	 */
	public int splitExcelFile(String strInData) throws IOException {
		clear();
		synchronized(mObjSync){
			int index = 0;
			while((index = getData(strInData, index)) != -1)
				;
		}
		return size() ;
	}
	//
	/**
	 *	������擾���\�b�h<br>
	 *	�G�N�Z��CSV�`���̕����񂩂�Z�p���[�^�ŋ�؂�ꂽ��������擾����
	 *	@param		strInData	�G�N�Z��CSV�`���̕�����
	 *	@param		index			���݂�index
	 *	@return		���݂�index
	 */
	protected int getData(String strInData, int index) {
		if (index > strInData.length()) {
			return -1;
		}

		if (index == strInData.length()) {
			add(C_NONE);
			return -1;
		}
		
		char c = strInData.charAt(index);
		if (c == ',') {
			add(C_NONE);
			return index + 1;
		}
		
		if (c == '"') {
			return _getQuotedData(strInData, index + 1);
		}
		
		int begin = index;
		index = strInData.indexOf(',', index);
		if (index == -1) {
			add(strInData.substring(begin));
			return -1;
		}
		
		add(strInData.substring(begin, index));
		return index + 1;
	}
	//
	/**
	 *	�G�X�P�[�v������̎擾���\�b�h<br>
	 *	�G�X�P�[�v�������g�p���ꂽ��������擾����
	 *	@return	int			���݂�index
	 *	@param		strInData	�G�N�Z��CSV�`���̕�����
	 *	@param		int			���݂�index
	 */
	private int _getQuotedData(String strInData, int index) {
		StringBuffer buf = new StringBuffer();
		while (true) {
			int begin = index;
			index = strInData.indexOf('\"', index);
			if (index == -1) {
				buf.append(strInData.substring(begin));
				add(buf.toString());
				return -1;
			}

			if (index == strInData.length() - 1) {
				buf.append(strInData.substring(begin, index));
				add(buf.toString());
				return -1;
			}			
			int c = strInData.charAt(index + 1);
			if (c == '\"') {
				buf.append(strInData.substring(begin, index + 1));
				index += 2;
			} else if (c == ',') {
				buf.append(strInData.substring(begin, index));
				add(buf.toString());
				return index + 2;
			} else {
				buf.append(strInData.substring(begin, index));
				index += 2;
			}
		}
	}

	//
	/**
	 *	�������\�b�h�i�v���C�x�[�g�j<br>
	 *	�V���N���͏��public���\�b�h�ł����鎖
	 *	@return		String		����������
	 */
	private String _joinString() {
		StringBuffer mngBuf = new StringBuffer() ;
		for( ListIterator iterator = super.listIterator(); iterator.hasNext();) {
			String tmpBuf = (String)iterator.next() ;
			tmpBuf = StringOperator.replaceString(tmpBuf,mEscapeString,mEscapeString+mEscapeString );
			tmpBuf = StringOperator.replaceString(tmpBuf,mSeptData,mEscapeString + mSeptData);
			mngBuf.append(tmpBuf).append(mSeptData) ;
		}
		//�ŏI�f�~���^�[�폜
		if(mAddDemiliter==false){
			if( mngBuf.length() > mSeptData.length()){
				mngBuf = new StringBuffer(mngBuf.toString().substring(0,mngBuf.length() - mSeptData.length())) ;
			}
		}
		return mngBuf.toString() ;
	}
	//
	/**
	 *	�������\�b�h<BR>
	 *	";"���f�~���^�[�����ɂ��č�����������쐬����B
	 *	@return		String		����������
	 */
	public String join() {
		synchronized(mObjSync){
			/** �}���c��؂蕶���ŕ��� **/
			mSeptData = C_COMMMA ;
			return _joinString() ;
		}
	}
	//
	/**
	 *	�������\�b�h<BR>
	 *	���s�R�[�h���f�~���^�[�����ɂ��č�����������쐬����B
	 *	@return		String		����������
	 */
	public String joinCL() {
		/** �}���c��؂蕶���ŕ��� **/
		mSeptData = System.getProperty(C_LINESEPT) ;
		return _joinString() ;
	}
	//
	/**
	 *	�������\�b�h�i�w���؂�j
	 *	�w��������f�~���^�[�����ɂ��č�����������쐬����B
	 *	@return		String		����������
	 *	@param		strSept		�f�~���^�[������
	 */
	public String join(String strSept) {
		mSeptData = strSept ;
		return _joinString() ;
	}
	//
	/**
	 *	�����z��쐬���\�b�h
	 *	@return		String[]		����������
	 */
	public String[] toStringAry() {
		String result[] = null;
		Object aryObj[] = null;
		aryObj = super.toArray() ;
		if(aryObj != null){
			result = new String[aryObj.length];
			for(int rCnt=0 ;rCnt<aryObj.length;rCnt++){
				result[rCnt] = (String)aryObj[rCnt];
			}
		}
		return result ;
	}
	//
	/**
	 *	�����z��ݒ胁�\�b�h
	 *	@param		inStrAry		���͕����z��
	 */
	public void setStringAry(String inStrAry[]) {
		if(inStrAry != null){
			for (int i = 0 ; i < inStrAry.length ; i ++ ) {
				this.add(inStrAry[i]) ;
			}
		}
	}
	//
	/**
	 *	������擾���\�b�h
	 *	@param		index		�z��ԍ�
	 *	@return		INDX�w�蕶����
	 */
	public String getStr(int index) {
		String result ;
		result = (String)super.get(index);
		return result ;
	}
	//
}
