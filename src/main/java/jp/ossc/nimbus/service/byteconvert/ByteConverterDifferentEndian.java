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
 *	byte配列のバイナリデータを各型の値に変換する
 *	@author		Hirotaka.Nakano
 *	@version	1.00 作成：2001.06.21 － H.Nakano<BR>
 *				更新：
 */
public class ByteConverterDifferentEndian implements ByteConverter {
	//
	//
	/**
	 * byte[] から char に変換する
	 * @param  b   バイト配列
	 * @param  off オフセット
	 * @return 変換された値
	*/
	public char toChar(byte[] b, int off){
		char v;
		v = (char)b[off];
		v = (char)((v << 8) | (b[off + 1] & 0xff));
		return v;
	}
	//
	/**
	 * byte[] から short に変換する
	 * @param  b   バイト配列
	 * @param  off オフセット
	 * @return 変換された値
	*/
	public short toShort(byte[] b, int off){
		short v;
		v = (short)b[off];
		v = (short)((v << 8) | (b[off + 1] & 0xff));
		return v;
  	}
	//
	/**
	 * byte[] から int に変換する
	 * @param  b   バイト配列
	 * @param  off オフセット
	 * @return 変換された値
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
	 * byte[] から long に変換する
	 * @param  b   バイト配列
	 * @param  off オフセット
	 * @return 変換された値
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
	 * char から byte[] に変換する
	 * @param  s  値
	 * @param  b  値
	 * @param  off オフセット
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
	 * short から byte[] に変換する
	 * @param  s	値
	 * @param  b	値
	 * @param  off オフセット
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
	 * int から byte[] に変換する
	 * @param  i	値
	 * @param  b	値
	 * @param  off オフセット
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
	 * long から byte[] に変換する
	 * @param  l	値
	 * @param  b	値
	 * @param  off オフセット
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
	 *	バイト配列を16進ダンプ文字列として出力する。
	 *	@param		inBytes		バイト配列
	 *	@return		String		16進ダンプ
	 */
	public String byte2hex(byte[] inBytes){
		char[] hexTable = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		StringBuilder retStr = new StringBuilder();
		if (inBytes == null){
			return null;
		}
		// 
		for(int i = 0 ; i < inBytes.length ; i++){
			// 上位4BITを生成
			retStr.append( hexTable[(byte)((inBytes[i] & 0x0F) >>> 4)] );
			// 下位4BITを生成
			retStr.append( hexTable[inBytes[i] & 0xFF] );
		}
		return retStr.toString();
	}
	//
	/**
	 *	16進ダンプ文字列をバイト配列として出力する。
	 *	@param		inStr		16進ダンプ
	 *	@return		String		バイト配列
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
