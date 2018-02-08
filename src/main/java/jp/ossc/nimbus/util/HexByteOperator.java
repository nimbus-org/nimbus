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
// �p�b�P�[�W
package jp.ossc.nimbus.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//�C���|�[�g

/**
 * �o�C�g�\���̂P�U�i��������o�͂���<p>
 * �t�@�C���̃R�s�[�⃊�l�[���ƌ�����������s��
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class HexByteOperator {
	/**
	 * �R���X�g���N�^�[
	 */
	public HexByteOperator() {
		super();
	}
	/**
	 *	�o�C�g�z����w�L�T�����֕ϊ�
	 *	@param compByte		�o�C�g�z��
	 * 	@return �ϊ����ꂽ������
	 */
	public  static String bytesToHexString(byte[] compByte){
		// �o�C�g�̔z����P�U�i���̐����ŕ\����String�ɕϊ�
		StringBuffer stringBuff = new StringBuffer("");
		for (int i=0; i<compByte.length; i++) {
			int ival = (compByte[i] < 0) ? (int)compByte[i] + 256 : (int)compByte[i] ;
			String tmpStr = Integer.toHexString(ival);
			if (tmpStr.length() == 1) {
				tmpStr = "0" + tmpStr;
			}
			stringBuff.append(tmpStr);
		}
		return stringBuff.toString() ;
	}
	/**
	 * �������SHA1�Ńn�b�V�����ĂP�U�i�����Ƃ��ďo��
	 * @param orgStr
	 * @param encode
	 * @return �n�b�V��������
	 */
	public static String convertSHA1String(String orgStr,String encode){
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			return null ;
		}
		byte b1[]= null;
		try {
			b1 = orgStr.getBytes(encode);
		} catch (UnsupportedEncodingException e1) {
			b1 = orgStr.getBytes();
		}
		md.update(b1);
	    byte hash[]=md.digest();
	    String ret2 =  HexByteOperator.bytesToHexString(hash) ;
	    return ret2;
	}
}
