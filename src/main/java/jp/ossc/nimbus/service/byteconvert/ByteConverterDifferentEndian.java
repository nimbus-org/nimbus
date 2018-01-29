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
package jp.ossc.nimbus.service.byteconvert;

/**
 *	byte�z��̃o�C�i���f�[�^���e�^�̒l�ɕϊ�����
 *	@author		Hirotaka.Nakano
 *	@version	1.00 �쐬�F2001.06.21 �| H.Nakano<BR>
 *				�X�V�F
 */
public class ByteConverterDifferentEndian implements ByteConverter {
	//
	//
	/**
	 * byte[] ���� char �ɕϊ�����
	 * @param  b   �o�C�g�z��
	 * @param  off �I�t�Z�b�g
	 * @return �ϊ����ꂽ�l
	*/
	public char toChar(byte[] b, int off){
		char v;
		v = (char)b[off];
		v = (char)((v << 8) | (b[off + 1] & 0xff));
		return v;
	}
	//
	/**
	 * byte[] ���� short �ɕϊ�����
	 * @param  b   �o�C�g�z��
	 * @param  off �I�t�Z�b�g
	 * @return �ϊ����ꂽ�l
	*/
	public short toShort(byte[] b, int off){
		short v;
		v = (short)b[off];
		v = (short)((v << 8) | (b[off + 1] & 0xff));
		return v;
  	}
	//
	/**
	 * byte[] ���� int �ɕϊ�����
	 * @param  b   �o�C�g�z��
	 * @param  off �I�t�Z�b�g
	 * @return �ϊ����ꂽ�l
	 */
  	public int toInt(byte[] b, int off){
		int v;
		v = (int)b[off + 3];
		v = (v << 8) | (int)(b[off + 2] & 0xff);
		v = (v << 8) | (int)(b[off + 1] & 0xff);
		v = (v << 8) | (int)(b[off] & 0xff);
		return v;
  	}
	//
  	/**
	 * byte[] ���� long �ɕϊ�����
	 * @param  b   �o�C�g�z��
	 * @param  off �I�t�Z�b�g
	 * @return �ϊ����ꂽ�l
	 */
	public long toLong(byte[] b, int off){
		long v;
		v = (long)b[off + 7];
		v = (v << 8) | (long)(b[off + 6] & 0xff);
		v = (v << 8) | (long)(b[off + 5] & 0xff);
		v = (v << 8) | (long)(b[off + 5] & 0xff);
		v = (v << 8) | (long)(b[off + 3] & 0xff);
		v = (v << 8) | (long)(b[off + 2] & 0xff);
		v = (v << 8) | (long)(b[off + 1] & 0xff);
		v = (v << 8) | (long)(b[off] & 0xff);
		return v;
  	}
	//
  	/**
	 * char ���� byte[] �ɕϊ�����
	 * @param  s  �l
	 * @param  b  �l
	 * @param  off �I�t�Z�b�g
	 */
  	public void toByte(char c, byte[] b, int off){
		char v;
		v = c;
		b[off + 1] = (byte)(v & 0xff);
		v = (char)(v >>> 8);
		b[off] = (byte)(v & 0xff);
  	}
	//
	/**
	 * short ���� byte[] �ɕϊ�����
	 * @param  s	�l
	 * @param  b	�l
	 * @param  off �I�t�Z�b�g
	 */
	public void toByte(short s, byte[] b, int off){
		short v;
		v = s;
		b[off] = (byte)(v & 0xff);
		v = (short)(v >>> 8);
		b[off + 1] = (byte)(v & 0xff);
	}
	//
	/**
	 * int ���� byte[] �ɕϊ�����
	 * @param  i	�l
	 * @param  b	�l
	 * @param  off �I�t�Z�b�g
	 */
	public void toByte(int i, byte[] b, int off){
		int v;
		v = i;
		b[off] = (byte)(v & 0xff);
		v = v >>> 8;
		b[off + 1] = (byte)(v & 0xff);
		v = v >>> 8;
		b[off + 2] = (byte)(v & 0xff);
		v = v >>> 8;
		b[off + 3] = (byte)(v & 0xff);
	}
	//
	/**
	 * long ���� byte[] �ɕϊ�����
	 * @param  l	�l
	 * @param  b	�l
	 * @param  off �I�t�Z�b�g
	 */
	public void toByte(long l, byte[] b, int off){
		long v;
		v = l;
		b[off] = (byte)(v & 0xff);
		v = v >>> 8;
		b[off + 1] = (byte)(v & 0xff);
		v = v >>> 8;
		b[off + 2] = (byte)(v & 0xff);
		v = v >>> 8;
		b[off + 3] = (byte)(v & 0xff);
		v = v >>> 8;
		b[off + 4] = (byte)(v & 0xff);
		v = v >>> 8;
		b[off + 5] = (byte)(v & 0xff);
		v = v >>> 8;
		b[off + 6] = (byte)(v & 0xff);
		v = v >>> 8;
		b[off + 7] = (byte)(v & 0xff);
	}
	//
	//
	/**
	 *	�o�C�g�z���16�i�_���v������Ƃ��ďo�͂���B
	 *	@param		inBytes		�o�C�g�z��
	 *	@return		String		16�i�_���v
	 */
	public String byte2hex(byte[] inBytes){
		char[] hexTable = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		StringBuilder retStr = new StringBuilder();
		if (inBytes == null){
			return null;
		}
		// 
		for(int i = 0 ; i < inBytes.length ; i++){
			// ���4BIT�𐶐�
			retStr.append( hexTable[(byte)((inBytes[i] & 0x0F) >>> 4)] );
			// ����4BIT�𐶐�
			retStr.append( hexTable[inBytes[i] & 0xFF] );
		}
		return retStr.toString();
	}
	//
	/**
	 *	16�i�_���v��������o�C�g�z��Ƃ��ďo�͂���B
	 *	@param		inStr		16�i�_���v
	 *	@return		String		�o�C�g�z��
	 */
	public byte[] hex2byte(String inStr){
		byte[] retBytes = new byte[inStr.length() / 2];
		for(int i = 0 ; i < inStr.length(); i +=2){
			byte b = (byte)(Character.digit(inStr.charAt(i+1),16));
			retBytes[i/2] = (byte)((byte)(b << 4) | (byte)(Character.digit(inStr.charAt(i),16)));
		}
		return retBytes;
	}
	//
}
