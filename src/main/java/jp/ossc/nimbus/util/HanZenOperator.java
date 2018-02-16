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
import java.util.Arrays;
import java.util.Comparator;

//
/**
 * 
 * @author   NRI. Hirotaka Nakano
 * @version  1.00 作成: 2003/10/02 -　H.Nakano
 */
public class HanZenOperator {
	/** 半角カナのunicodeマップ上での最初の( 一番数値として小さい )文字です。*/
	public static final int HANKANA_FIRST = 0xff61;

	/** 半角カナのunicodeマップ上での最後の( 一番数値として大きい )文字です。*/
	public static final int HANKANA_LAST  = 0xff9f;
	/**
	 * Constructor for HanZenOperator.
	 */
	public HanZenOperator() {
		super();
	}
	public static String toHankaku(String src){
		return null;
	}
	public static String toHankaku(String src,
									 int pos,
									 int length){
		return null ;
	}
	public static String toZenkaku(String src){
		if ( src == null ){
			return null;
		}
		return toZenkaku( src, 0, src.length() );
	}
	public static String toZenkaku(String val,
									 int pos,
									 int length){
		char[][][] table = HanZenTable.HANZEN_TABLE;
		Comparator comp  = HanZenTable.HANZEN_COMPARATOR;
	
		int    retIdx = 0;
		int    maxPos = pos + length;
		char[] base   = new char[ 1 ];
		char[] daku   = new char[ 2 ];
		char[] nChars = val.toCharArray();
		char[] nRets  = new char[ length ];
	
		while ( pos < maxPos ) {
			char currChar = nChars[ pos++ ];
			if ( !isHankaku( currChar ) ) {
				nRets[ retIdx++ ] = currChar;
				continue;
			}
	
			char[] res;
			if ( pos < maxPos ) {
				char nextChar = nChars[ pos ];
				if ( nextChar == 'ﾞ' || nextChar == 'ﾟ' ) {
					pos++;
					daku[ HanZenTable.IDX_HAN_BASE ]   = currChar;
					daku[ HanZenTable.IDX_HAN_DAKUON ] = nextChar;
					res = _convertPart( daku, table, comp );
					retIdx += _putChars( nRets, retIdx, res );
					continue;
				}
			}
			base[ 0 ] = currChar;
			res = _convertPart( base, table, comp );
			retIdx += _putChars( nRets, retIdx, res );
		}
		return new String( nRets, 0, retIdx );
	}

	// protected methods -----------------------------------------------------
	/**
	 * 指定されたchrが半角カナであればtrueをかえします。ここをオーバーライド
	 * して半角カナ以外の半角にもtrueを返すようにして、
	 * HanZenTable.HANZEN_TABLEに対応表を付け加えれば、全ての半角文字を全角に
	 * 変換できます。
	 */
	public static boolean isHankaku( char chr ){
		return (  HANKANA_FIRST <= chr && chr <= HANKANA_LAST );
	}
	// protected methods -----------------------------------------------------
	/**
	 * 指定されたchrが半角カナであればtrueをかえします。ここをオーバーライド
	 * して半角カナ以外の半角にもtrueを返すようにして、
	 * HanZenTable.HANZEN_TABLEに対応表を付け加えれば、全ての半角文字を全角に
	 * 変換できます。
	 */
	public static boolean containsHankaku( String val ){
		if(val == null){
			return false ;
		}
		char[] ary = val.toCharArray();
		boolean ret = false ;
		for(int cnt = 0 ; cnt<ary.length;cnt++){
			if( HANKANA_FIRST <= ary[cnt] && ary[cnt] <= HANKANA_LAST ){
				ret = true ;
				break ;
			}
		}
		return ret ;
	}


	// private methods -------------------------------------------------------
	/**
	 * mainの配列のmainPosの位置からpartsの値を上書きします。
	 */
	protected static int _putChars( char[] main,
			   int	  mainPos,
			   char[] parts ){
		int pos = mainPos;
		for ( int i = 0; i < parts.length; i++ ) {
			if ( parts[ i ] != (char) 0 )
			 main[ pos++ ] = parts[ i ];
		}
		return pos - mainPos;
	}


	/**
	 * charsで指定された文字配列をsourceTableの表を使用して全角文字へ変換しま
	 * す。charsには1 partの文字分( 通常はlength == 1, 濁音、半濁音ならば
	 * length == 2になります。 )を指定します。もし、指定されたcharsが変換table
	 * で変換出来ないならばそのまま同じ配列を返します。
	 * <p>
	 * ( ex ) ｱﾞは変換出来ないので各文字に分けて変換します。
	 * </p>
	 * charsのlengthは 0 < length < 3です。
	 * sourceTableはHanZenTable.HANZEN_TABLEを、comparatorは
	 * HanZenTable.HANZEN_COMPARATORを指定します。
	 */
	protected static  char[] _convertPart( char[] 	chars,
				 char[][][] sourceTable,
				 Comparator comparator ){
		int index = Arrays.binarySearch((Object[]) sourceTable,
									 				chars, 
									 				comparator );

		if ( index >= 0 ){
			// succeed convert.
			return sourceTable[ index ][ HanZenTable.IDX_ZENKAKU ];
		}else if ( chars.length == 1 ){
			// can't convert more.
			return chars;
		}else{
			int len = chars.length;
			for ( int i = 0; i < len; i++ ) {
				if ( chars[ i ] == (char) 0 ){
					continue;
				}	
				char[] atom = new char[]{ chars[ i ] };
				chars[ i ] = _convertPart( atom, sourceTable, comparator )[ 0 ];
			}
			return chars;
		}
	}
}
