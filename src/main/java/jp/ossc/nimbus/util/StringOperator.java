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
/**
*	�X�g�����O����N���X
*	@author		Hirotaka.Nakano
*	@version	1.00 �쐬�F2001.04.04 �| H.Nakano<BR>
*				�X�V�F
*/
public class StringOperator {
	public static final int C_NOAP = -10000 ;
	public static final String C_SPACE = " " ; //$NON-NLS-1$
	//
	/**
	 *	�󔒕�����ԋp���\�b�h<br>
	 *	�w�肳�ꂽ�����̃X�y�[�X������A�����ďo�͂���B
	 *	@param		spaceNum	������
	 *	@return		�󔒘A��������
	 */
	public static String makeSpace(int spaceNum)	{
		//## �Ԃ�l������ ##
		StringBuilder strRet = new StringBuilder() ;
		int rCnt;
		//## ������쐬 ##
		for(rCnt=0; rCnt<spaceNum;rCnt++){
			strRet.append(C_SPACE);
		}
		//## �Ԃ�l���^�[�� ##
		return strRet.toString()  ;
	}
	//
	/**
	 *	�w�蕶����ԋp���\�b�h
	 *	@param		strElement	�쐬�P�ʕ�����
	 *	@param		strNum		������
	 *	@return		�A��������
	 */
	public static String makeString(String strElement,int strNum){
		//## �Ԃ�l������ ##
		StringBuilder strRet = new StringBuilder() ;
		int rCnt;
		//## ������쐬 ##
		for(rCnt=0; rCnt<strNum;rCnt++){
			strRet.append(strElement) ;
		}
		//## �Ԃ�l���^�[�� ##
		return strRet.toString()  ;
	}
	
	public static String replaceString(String inDataBuff, String targetChr, Object replace){
		//## ���[�J���錾 ##
		StringBuilder strRet = new StringBuilder();
		String inStr = new String(inDataBuff);
		int lngFindNum ;
		int lngStartCnt ;
		lngStartCnt = 0 ;
		//## �u���������� ##
		while(true){
			//== �Ώە��������񌟍� ==
			lngFindNum = inStr.indexOf(targetChr) ;
			//�����Ȃ��Ȃ�u���C�N
			if (lngFindNum == -1){
				strRet.append(inStr);
				break ;
			//���������ꍇ�͒u������
			}else{
				strRet.append(inStr.substring(0, lngFindNum ));
				strRet.append(replace);
				lngStartCnt = lngFindNum + targetChr.length();
				inStr=inStr.substring(lngStartCnt);
			}
		}
		return strRet.toString();
	}
	//
	/**
	 *	�w�蕶����u���������\�b�h<br>
	 *	�ϊ����̕�����ixxxppqq)��̕���������ipp�j��<br>
	 *	�C�ӂ̕�����itest�j�ɒu��������ꍇ�ϊ��㕶����ixxxtestqq�j���o�͂���B
	 *	@param		inDataBuff	�ϊ������͕�����
	 *	@param		targetChr	�u�������Ώە�����
	 *	@param		replaceChr	�u������������
	 *	@return		�u������
	 */
	public static String replaceString(String inDataBuff, String targetChr, String replaceChr){
	    return replaceString(inDataBuff, targetChr, (Object)replaceChr);
	}
	
	public static String replaceString(String inDataBuff, String targetStr, Object[] replaces){
		/** ���͕����z��null�Ȃ烊�^�[�� */
		if(replaces==null){
			return new String(inDataBuff) ;
		}
		String retStr = new String(inDataBuff) ;
		/** �z��������̒u������������B*/
		for(int rCnt = replaces.length -1;rCnt>=0;rCnt--){
			//�^�[�Q�b�g�����{�z��INDEX�Œu���������s���B
			String cntStr = targetStr + new Integer(rCnt).toString()  ;
			retStr = replaceString(retStr,cntStr,replaces[rCnt]);
		}
		return retStr ;
	}
	//
	/**
	 *	�w�薄�ߍ��ݕ����z��u���������\�b�h<br>
	 *	@param		inDataBuff	�ϊ������͕�����
	 *	@param		targetStr	�u�������Ώە�����
	 *	@param		replaceStrs	�u�����������z��
	 *	@return		�u����������
	 */
	public static String replaceString(String inDataBuff, String targetStr, String[] replaceStrs){
	    return replaceString(inDataBuff, targetStr, (Object[])replaceStrs);
	}
	//
	/**
	 *	�w�薄�ߍ��ݕ����z��u���������\�b�h<br>
	 *	@param		inDataBuff	�ϊ������͕�����
	 *	@param		targetStr	�u�������Ώە�����
	 *	@param		replaceStrs	�u�����������z��
	 *	@return		�u����������
	 */
	public static String replaceString(String inDataBuff, String targetStr, ArrayList replaceStrs){
		/** ���͕����z��null�Ȃ烊�^�[�� */
		if(replaceStrs==null){
			return inDataBuff ;
		}
		String retStr = new String(inDataBuff) ;
		/** �z��������̒u������������B*/
		for(int rCnt = replaceStrs.size() -1;
			rCnt>=0;rCnt--){
			//�^�[�Q�b�g�����{�z��INDEX�Œu���������s���B
			String cntStr = targetStr + new Integer(rCnt).toString()  ;
			retStr = replaceString(retStr,cntStr,(String)replaceStrs.get(rCnt));
		}
		return retStr ;
	}
	//
	/**
	 *	������A���l�ϊ����\�b�h<br>
	 *	@param		inStr		���l������
	 *	@return		�����񂪕\�����l
	 */
	public static int convertInt(String inStr){
		//## ���[�J���錾 ##
		int findPriod = inStr.indexOf(".");
		String mngBuf = inStr ;
		if(findPriod > -1){
			mngBuf = inStr.substring(0,findPriod);
		}
		Integer intRet = null;
		try{
			intRet = new Integer(mngBuf);
		}catch(NumberFormatException e){
			intRet = new Integer(0) ;
		}
		return intRet.intValue();
	}
	//
	/**
	 *	������A���l�ϊ����\�b�h<br>
	 *	@param		inStr		���l������
	 *	@return		�����񂪕\�����l
	 */
	public static long convertLong(String inStr){
		//## ���[�J���錾 ##
		int findPriod = inStr.indexOf(".");
		String mngBuf = inStr ;
		if(findPriod > -1){
			mngBuf = inStr.substring(0,findPriod);
		}
		Long lngRet = null;
		try{
			lngRet = new Long(mngBuf);
		}catch(NumberFormatException e){
			lngRet = new Long(0) ;
		}
		return lngRet.longValue();
	}
	/**
	 *	���͕�����ASCII�����ł��邩���肷��B<BR>
	 * @param inStr ���͕���
	 *	@return		�`�F�b�N����
	 */
	public static boolean isAscii (String inStr) {
		for(int cnt = 0; cnt < inStr.length(); cnt++){
			char valtmp = inStr.charAt(cnt);
			if(valtmp < ' ' || valtmp > '~'){
				return false;
			}
		}
		return true ;
	}
	//
	/**
	 *	���͕����������ł��邩���肷��B<BR>
	 * @param inStr ���͕���
	 *	@return	�`�F�b�N����
	 */
	public static boolean isNumeric (String inStr) {
		for(int cnt = 0; cnt < inStr.length(); cnt++){
			char valtmp = inStr.charAt(cnt);
			if(valtmp < '0' || valtmp > '9'){
				return false;
			}
		}
		return true ;
	}
	//
	/**
	 *	�����`�F�b�N���\�b�h<BR>
	 *	�����̐������A�����_�ȉ��̌����`�F�b�N���s���B<BR>
	 * @param getData �`�F�b�N����
	 *	@return		�`�F�b�N����
	 */
	public static boolean isDecimal (String getData) {
		int checkInt = 0 ;
		int checkDec = 0 ;
		for(int cnt = 0; cnt < getData.length(); cnt++){
			char valtmp = getData.charAt(cnt);
			if(valtmp < '0' || valtmp > '9') {
				if(valtmp != '.' && valtmp != ',') {
					return false ;
				} else if(checkDec > 0) {
					return false ;
				} else if(cnt == getData.length() - 1) {
					return false ;
				} else if(valtmp == '.') {
					checkDec++ ;
				}
			} else if(checkDec > 0) {
				checkDec++ ;
			} else {
				checkInt++ ;
			}
		}
		return true ;
	}
	//
	//
	/**
	 *	�S�p�C���p�X�y�[�X�폜���\�b�h<BR>
	 *	������̗�������S�p�X�y�[�X,���p�X�y�[�X���폜����B<BR>
	 *	@param		getData		�폜�Ώە�����
	 *	@return		�폜����������
	 */
	public static String trimSpace (String getData) {
		StringBuilder retBuff = new StringBuilder() ;
		char [] cValtmp = getData.toCharArray();
		for(int k = 0 ; k < 2 ; k++) {
			for(int i=0 ; i < cValtmp.length ; i++) {
				if(cValtmp[i]!='�@' && cValtmp[i]!=' ') {
					try {
						if(cValtmp[i]==65377 && cValtmp[i+1]==65377){
							i++ ;
							continue;
						}
					} catch(ArrayIndexOutOfBoundsException e) {
						e.printStackTrace() ;
					}
					for(int j=i ; j < cValtmp.length ; j++) {
						retBuff.append(cValtmp[j]) ;
						i=j;
					}
				}
			}
			if(k==0) {
				cValtmp = new String(retBuff.reverse()).toCharArray() ;
				retBuff.append(retBuff.delete(0,retBuff.length())) ;
			} 
		}
		String retData = retBuff.reverse().toString() ;
		return retData ;
	}
	//
	/**
	 *	�S�p�C���p�X�y�[�X,���s�R�[�h�폜���\�b�h<BR>
	 *	������̗�������S�p�X�y�[�X,���p�X�y�[�X���폜���A<BR>
	 *	�����񂩂�S�Ẳ��s�R�[�h���폜����B<BR>
	 *	@param		getData		�폜�Ώە�����
	 *	@param dummy �I�[�o�[���[�h�p�_�~�[����
	 *	@return		�폜����������
	 */
	public static String trimSpace (String getData,int dummy) {
		String removeStr = removeReturn(getData) ;
		StringBuilder retBuff = new StringBuilder() ;
		char [] cValtmp = removeStr.toCharArray();
		for(int k = 0 ; k < 2 ; k++) {
			for(int i=0 ; i < cValtmp.length ; i++) {
				if(cValtmp[i]!='�@' && cValtmp[i]!=' ') {
					try {
						if(cValtmp[i]==65377 && cValtmp[i+1]==65377){
							i++ ;
							continue;
						}
					} catch(ArrayIndexOutOfBoundsException e) {
						e.printStackTrace() ;
					}
					for(int j=i ; j < cValtmp.length ; j++) {
						retBuff.append(cValtmp[j]) ;
						i=j;
					}
				}
			}
			if(k==0) {
				cValtmp = new String(retBuff.reverse()).toCharArray() ;
				retBuff.append(retBuff.delete(0,retBuff.length())) ;
			} 
		}
		String retData = retBuff.reverse().toString() ;
		return retData ;
	}
	//
	/**
	 *	�S�p�X�y�[�X,���p�X�y�[�X,��,�^�u�폜���\�b�h<BR>
	 *	������̗�������S�p�X�y�[�X,���p�X�y�[�X,��,�^�u<BR>
	 *	���폜����B<BR>
	 *	@param		getData		�폜�Ώە�����
	 *	@return		�폜����������
	 */
	public static String trimNeedlessChara (String getData) {
		StringBuilder retBuff = new StringBuilder() ;
		char [] cValtmp = getData.toCharArray();
		for(int k = 0 ; k < 2 ; k++) {
			for(int i=0 ; i < cValtmp.length ; i++) {
				if(cValtmp[i]!='�@' 
					&& cValtmp[i]!=' ' 
					&& cValtmp[i]!='��' && cValtmp[i]!='\t') {
					try {
						if(cValtmp[i]==65377 && cValtmp[i+1]==65377){
							i++ ;
							continue;
						}
						if(k == 0) {
							if(cValtmp[i]==65378 && cValtmp[i+1]==65384){
								i++ ;
								continue;
							}
						} else {
							if(cValtmp[i]==65384 && cValtmp[i+1]==65378){
								i++ ;
								continue;
							}
						}
					} catch(ArrayIndexOutOfBoundsException e) {
						e.printStackTrace() ;
					}
					for(int j=i ; j < cValtmp.length ; j++) {
						retBuff.append(cValtmp[j]) ;
						i=j;
					}
				}
			}
			if(k==0) {
				cValtmp = new String(retBuff.reverse()).toCharArray() ;
				retBuff.append(retBuff.delete(0,retBuff.length())) ;
			} 
		}
		String retData = retBuff.reverse().toString() ;
		return retData ;
	}
	//
	/**
	 *	�S�p�X�y�[�X,���p�X�y�[�X,��,�^�u,���s�R�[�h �폜���\�b�h<BR>
	 *	������̗�������S�p�X�y�[�X,���p�X�y�[�X,��,�^�u���폜���A<BR>
	 *	������̒�����S�Ẳ��s�R�[�h���폜����B<BR>
	 *	@param		getData		�폜�Ώە�����
	 *	@param		dummy			�I�[�o�[���[�h�p�_�~�[����
	 *	@return		�폜����������
	 */
	public static String trimNeedlessChara (String getData,int dummy) {
		String removeStr = removeReturn(getData) ;
		StringBuilder retBuff = new StringBuilder() ;
		char [] cValtmp = removeStr.toCharArray();
		for(int k = 0 ; k < 2 ; k++) {
			for(int i=0 ; i < cValtmp.length ; i++) {
				if(cValtmp[i]!='�@' 
					&& cValtmp[i]!=' ' 
					&& cValtmp[i]!='��' 
					&& cValtmp[i]!='\t') {
					try {
						if(cValtmp[i]==65377 && cValtmp[i+1]==65377){
							i++ ;
							continue;
						}
						if(k == 0) {
							if(cValtmp[i]==65378 && cValtmp[i+1]==65384){
								i++ ;
								continue;
							}
						} else {
							if(cValtmp[i]==65384 && cValtmp[i+1]==65378){
								i++ ;
								continue;
							}
						}
					} catch(ArrayIndexOutOfBoundsException e) {
						e.printStackTrace() ;
					}
					for(int j=i ; j < cValtmp.length ; j++) {
						retBuff.append(cValtmp[j]) ;
						i=j;
					}
				}
			}
			if(k==0) {
				cValtmp = new String(retBuff.reverse()).toCharArray() ;
				retBuff.append(retBuff.delete(0,retBuff.length())) ;
			} 
		}
		String retData = retBuff.reverse().toString() ;
		return retData ;
	}
	//
		//
	/**
	 *	�S�p�X�y�[�X,���p�X�y�[�X,�^�u,���s�R�[�h �폜���\�b�h<BR>
	 *	������̗�������S�p�X�y�[�X,���p�X�y�[�X,�^�u���폜���A<BR>
	 *	������̒�����S�Ẳ��s�R�[�h���폜����B<BR>
	 *	@param		getData		�폜�Ώە�����
	 *	@return		�폜����������
	 */
	public static String trimNeedlessChara2 (String getData) {
		String removeStr = removeReturn(getData) ;
		StringBuilder retBuff = new StringBuilder() ;
		char [] cValtmp = removeStr.toCharArray();
		for(int k = 0 ; k < 2 ; k++) {
			for(int i=0 ; i < cValtmp.length ; i++) {
				if(cValtmp[i]!='�@' 
					&& cValtmp[i]!=' ' 
					&& cValtmp[i]!='\t') {
					try {
						if(cValtmp[i]==65377 && cValtmp[i+1]==65377){
							i++ ;
							continue;
						}
						if(k == 0) {
							if(cValtmp[i]==65378 && cValtmp[i+1]==65384){
								i++ ;
								continue;
							}
						} else {
							if(cValtmp[i]==65384 && cValtmp[i+1]==65378){
								i++ ;
								continue;
							}
						}
					} catch(ArrayIndexOutOfBoundsException e) {
						e.printStackTrace() ;
					}
					for(int j=i ; j < cValtmp.length ; j++) {
						retBuff.append(cValtmp[j]) ;
						i=j;
					}
				}
			}
			if(k==0) {
				cValtmp = new String(retBuff.reverse()).toCharArray() ;
				retBuff.append(retBuff.delete(0,retBuff.length())) ;
			} 
		}
		String retData = retBuff.reverse().toString() ;
		return retData ;
	}
	//
	/**
	 *	�S�p�X�y�[�X,���p�X�y�[�X,��,�^�u,���s�R�[�h �폜���\�b�h<BR>
	 *	�S�p�X�y�[�X,���p�X�y�[�X,��,�^�u,���s�R�[�h<BR>
	 *	�𕶎���̗�������폜����B<BR>
	 *	@param		getData		�폜�Ώە�����
	 *	@return		�폜����������
	 */
	public static String trimNeedless (String getData) {
		StringBuilder retBuff = new StringBuilder() ;
		char [] cValtmp = getData.toCharArray();
		for(int k = 0 ; k < 2 ; k++) {
			for(int i=0 ; i < cValtmp.length ; i++) {
				if(cValtmp[i]!='�@' 
				&& cValtmp[i]!=' ' 
				&& cValtmp[i]!='��' 
				&& cValtmp[i]!='\t') {
					try {
						if(cValtmp[i]==65377 && cValtmp[i+1]==65377){
							i++ ;
							continue;
						}
						if(k == 0) {
							if(cValtmp[i]==65378 && cValtmp[i+1]==65384){
								i++ ;
								continue;
							}
						} else {
							if(cValtmp[i]==65384 && cValtmp[i+1]==65378){
								i++ ;
								continue;
							}
						}
					} catch(ArrayIndexOutOfBoundsException e) {
						e.printStackTrace() ;
					}
					if(cValtmp[i]!='\r' && cValtmp[i]!='\n' ){
						for(int j=i ; j < cValtmp.length ; j++) {
							retBuff.append(cValtmp[j]) ;
							i=j;
						}
					}else{
					//	i++ ;
					}
				}
			}
			if(k==0) {
				cValtmp = new String(retBuff.reverse()).toCharArray() ;
				retBuff.append(retBuff.delete(0,retBuff.length())) ;
			} 
		}
		String retData = retBuff.reverse().toString() ;
		return retData ;
	}
	//
	/**
	 *	�S�p�X�y�[�X,���p�X�y�[�X,��,�^�u,���s�R�[�h �폜���\�b�h<BR>
	 *	�����񂩂�S�Ă̑S�p�X�y�[�X,���p�X�y�[�X,��,�^�u,���s�R�[�h<BR>
	 *	���폜����B<BR>
	 *	@param		getData		�폜�Ώە�����
	 *	@return		�폜����������
	 */
	public static String removeNeedlessChara (String getData) {
		String removeStr = removeReturn(getData) ;
		StringBuilder retBuff = new StringBuilder() ;
		char [] cValtmp = removeStr.toCharArray();
		for(int i=0 ; i < cValtmp.length ; i++) {
			if(cValtmp[i]!='�@' 
			&& cValtmp[i]!=' ' 
			&& cValtmp[i]!='��' 
			&& cValtmp[i]!='\t') {
				try {
					if(cValtmp[i]==65377 && cValtmp[i+1]==65377){
						i++ ;
						continue;
					}
					if(cValtmp[i]==65378 && cValtmp[i+1]==65384){
						i++ ;
						continue;
					}
				} catch(ArrayIndexOutOfBoundsException e) {
					e.printStackTrace() ;
				}
				retBuff.append(cValtmp[i]) ;
			}
		}
//		if(k==0) {
//			cValtmp = new String(retBuff.reverse()).toCharArray() ;
//			retBuff.append(retBuff.delete(0,retBuff.length())) ;
//		} 
//		String retData = retBuff.reverse().toString() ;
		String retData = retBuff.toString() ;
		return retData ;
	}
	//2001/11/21 Add K.Nakamura �����폜���Ȃ����\�b�h��ǉ�
	/**
	 *	�S�p�X�y�[�X,���p�X�y�[�X,�^�u,���s�R�[�h �폜���\�b�h<BR>
	 *	�����񂩂�S�Ă̑S�p�X�y�[�X,���p�X�y�[�X,�^�u,���s�R�[�h<BR>
	 *	���폜����B<BR>
	 *	@param getData �폜�Ώە�����
	 *	@param dummy �I�[�o�[���[�h�p�_�~�[����
	 *	@return �폜����������
	 */
	public static String removeNeedlessChara (String getData,int dummy) {
		String removeStr = removeReturn(getData) ;
		StringBuilder retBuff = new StringBuilder() ;
		char [] cValtmp = removeStr.toCharArray();
		for(int i=0 ; i < cValtmp.length ; i++) {
			if(cValtmp[i]!='�@' 
			&& cValtmp[i]!=' ' 
			&& cValtmp[i]!='\t') {
				try {
					if(cValtmp[i]==65377 && cValtmp[i+1]==65377){
						i++ ;
						continue;
					}
					if(cValtmp[i]==65378 && cValtmp[i+1]==65384){
						i++ ;
						continue;
					}
				} catch(ArrayIndexOutOfBoundsException e) {
					e.printStackTrace() ;
				}
				retBuff.append(cValtmp[i]) ;
			}
		}
		String retData = retBuff.toString() ;
		return retData ;
	}
	//
	/**
	 *	���s�R�[�h�폜���\�b�h<BR>
	 *	�����񂩂���s�R�[�h���폜����B<BR>
	 *	@param getData �폜�Ώە�����
	 *	@return �폜����������
	 */
	public static String removeReturn (String getData) {
		String retStr = new String(getData);
		if(retStr.indexOf("\r\n") != -1){
			retStr = StringOperator.replaceString(retStr,"\r\n","");
		}
		if(retStr.indexOf("\n") != -1){
			retStr = StringOperator.replaceString(retStr,"\n","");
		}
		if(retStr.indexOf("\r") != -1){
			retStr = StringOperator.replaceString(retStr,"\r","");
		}
		return retStr ;
	}
	//
	//2002/02/15 Add K.Nakamura
	/** ��������w�肳�ꂽ�������ɑ}�����Ă����B
	 *	@param	argStr		�Ώە�����
	 *	@param	argLen		��؂蒷
	 *	@param	argLinefeed	�}��������
	 *	@param	argUnfeed	��؂�ΏۊO������̗��񕶎� ��: "�A�B�j)"�@�Ȃ�
	 *	@return ���ʕ�����
	 */
	public static String setLinefeed(String argStr, int argLen, String argLinefeed, String argUnfeed){
		// �����`�F�b�N
		if (argStr == null || argStr.equals("")){
			return argStr;
		}
		if (argLen <= 0){
			return argStr;
		}
		if (argLinefeed == null || argLinefeed.equals("")){
			return argStr;
		}
		if (argStr.length() <= argLen){
			return argStr;
		}
		// �ԋp������
		StringBuilder retStr = new StringBuilder();
		// ��Ɨp������
		String targetStr = argStr;
		int begine = 0;
		int end = argLen;
		while(true){
			// �w�蒷�������o����B
			retStr.append(targetStr.substring(begine,end));
			// ���̃|�C���g�ɐi�߂�
			begine = end;
			end = end + argLen;
			if (argStr.length() <= begine){
				// �J�n�_��END�܂ł����̂ŏI������
				break;
			}else{
				// ��Ǔ_�Ȃǂ͂��̍s�Ɋ܂߂�
				if (argUnfeed != null && !argUnfeed.equals("")){
					if(argUnfeed.indexOf(targetStr.substring(begine,begine + 1)) > -1 ){
						// ��؂�ΏۊO�Ȃ̂ł��̍s�ɒǉ�
						retStr.append(targetStr.substring(begine,begine + 1));
						begine ++;
						end ++;
						if (argStr.length() <= begine){
							// �J�n�_��END�܂ł����̂ŏI������
							break;
						}
					}
				}
				// �w�蕶����}������
				retStr.append(argLinefeed);
			}
			// �I���_���Ώە����񒷂𒴂����ꍇ�͑Ώە������END�ɂ���B
			if (argStr.length() < end){
				end = argStr.length();
			}
		}
		return retStr.toString();
	}
	//
}
