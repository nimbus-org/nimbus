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
public class HanZenTable {
	// statics ---------------------------------------------------------------
	/** 比較テーブルの2次元目で使用する半角文字を表す定数です。
	 * HANZEN_TABLE[ row number ][ IDX_HANKAKU ] のように使用します。*/
	public static final int IDX_HANKAKU = 0;

	/** 比較テーブルの2次元目で使用する全角文字を表す定数です。
	 * HANZEN_TABLE[ row number ][ IDX_ZENKAKU ] のように使用します。*/
	public static final int IDX_ZENKAKU = 1;

	/** 半角文字特有の定数で、濁音以外の部分を表します( つまり一文字目 )。
	HANZEN_TABLE[ row number ][ HANKAKU ][ IDX_HAN_BASE ]のように使用しま
	す。*/
	public static final int IDX_HAN_BASE = 0;

	/** 半角文字特有の定数で、濁音部分を表します( つまり二文字目 )。
	HANZEN_TABLE[ row number ][ HANKAKU ][ IDX_HAN_DAKUON ]のように使用しま
	す。存在しない場合もあるので注意が必要です。*/
	public static final int IDX_HAN_DAKUON = 1;

	/** 半角 - 全角変換に使用する、並べ替え、search用の比較classです。*/
	public static final Comparator HANZEN_COMPARATOR
	= new HankakuComparator();

	/** 全角 - 半角変換に使用する、並べ替え、search用の比較classです。*/
	public static final Comparator ZENHAN_COMPARATOR
	= new ZenkakuComparator();


	/** 半角 - 全角変換用テーブルです。*/
	protected static final char[][][] HANZEN_TABLE;

	/** 全角 - 半角変換用テーブルです。*/
	protected static final char[][][] ZENHAN_TABLE;
	static {
	char[][][] table = new char[][][] 
	{
		{{'a'}, {'。'}},
		{{'b'}, {'。'}},
		{{'c'}, {'。'}},
		{{'d'}, {'。'}},
		{{'e'}, {'。'}},
		{{'f'}, {'。'}},
		{{'g'}, {'。'}},
		{{'h'}, {'。'}},
		{{'i'}, {'。'}},
		{{'j'}, {'。'}},
		{{'k'}, {'。'}},
		{{'l'}, {'。'}},
		{{'｡'}, {'。'}},
		{{'｡'}, {'。'}},
		{{'｡'}, {'。'}},
		{{'｡'}, {'。'}},
		{{'｡'}, {'。'}},
		{{'｡'}, {'。'}},
		{{'｡'}, {'。'}},
		{{'｡'}, {'。'}},
		{{'｡'}, {'。'}},
		{{'｡'}, {'。'}},
		{{'｡'}, {'。'}},
		{{'｡'}, {'。'}},
		{{'｡'}, {'。'}},
		{{'｡'}, {'。'}},
		{{'｡'}, {'。'}},
		{{'｡'}, {'。'}},
		{{'｢'}, {'「'}},
		{{'｣'}, {'」'}},
		{{'､'}, {'、'}},
		{{'･'}, {'・'}},
		{{'ｦ'}, {'ヲ'}},
		{{'ｧ'}, {'ァ'}},
		{{'ｨ'}, {'ィ'}},
		{{'ｩ'}, {'ゥ'}},
		{{'ｪ'}, {'ェ'}},
		{{'ｫ'}, {'ォ'}},
		{{'ｬ'}, {'ャ'}},
		{{'ｭ'}, {'ュ'}},
		{{'ｮ'}, {'ョ'}},
		{{'ｯ'}, {'ッ'}},
		{{'ｰ'}, {'ー'}},
		{{'-'}, {'－'}},
		{{'ｱ'}, {'ア'}},
		{{'ｲ'}, {'イ'}},
		{{'ｳ'}, {'ウ'}}, {{'ｳ', 'ﾞ'}, {'ヴ'}},
		{{'ｴ'}, {'エ'}},
		{{'ｵ'}, {'オ'}},
		{{'ｶ'}, {'カ'}}, {{'ｶ', 'ﾞ'}, {'ガ'}},
		{{'ｷ'}, {'キ'}}, {{'ｷ', 'ﾞ'}, {'ギ'}},
		{{'ｸ'}, {'ク'}}, {{'ｸ', 'ﾞ'}, {'グ'}},
		{{'ｹ'}, {'ケ'}}, {{'ｹ', 'ﾞ'}, {'ゲ'}},
		{{'ｺ'}, {'コ'}}, {{'ｺ', 'ﾞ'}, {'ゴ'}},
		{{'ｻ'}, {'サ'}}, {{'ｻ', 'ﾞ'}, {'ザ'}},
		{{'ｼ'}, {'シ'}}, {{'ｼ', 'ﾞ'}, {'ジ'}},
		{{'ｽ'}, {'ス'}}, {{'ｽ', 'ﾞ'}, {'ズ'}},
		{{'ｾ'}, {'セ'}}, {{'ｾ', 'ﾞ'}, {'ゼ'}},
		{{'ｿ'}, {'ソ'}}, {{'ｿ', 'ﾞ'}, {'ゾ'}},
		{{'ﾀ'}, {'タ'}}, {{'ﾀ', 'ﾞ'}, {'ダ'}},
		{{'ﾁ'}, {'チ'}}, {{'ﾁ', 'ﾞ'}, {'ヂ'}},
		{{'ﾂ'}, {'ツ'}}, {{'ﾂ', 'ﾞ'}, {'ヅ'}},
		{{'ﾃ'}, {'テ'}}, {{'ﾃ', 'ﾞ'}, {'デ'}},
		{{'ﾄ'}, {'ト'}}, {{'ﾄ', 'ﾞ'}, {'ド'}},
		{{'ﾅ'}, {'ナ'}},
		{{'ﾆ'}, {'ニ'}},
		{{'ﾇ'}, {'ヌ'}},
		{{'ﾈ'}, {'ネ'}},
		{{'ﾉ'}, {'ノ'}},
		{{'ﾊ'}, {'ハ'}}, {{'ﾊ', 'ﾞ'}, {'バ'}}, {{'ﾊ', 'ﾟ'}, {'パ'}},
		{{'ﾋ'}, {'ヒ'}}, {{'ﾋ', 'ﾞ'}, {'ビ'}}, {{'ﾋ', 'ﾟ'}, {'ピ'}},
		{{'ﾌ'}, {'フ'}}, {{'ﾌ', 'ﾞ'}, {'ブ'}}, {{'ﾌ', 'ﾟ'}, {'プ'}},
		{{'ﾍ'}, {'ヘ'}}, {{'ﾍ', 'ﾞ'}, {'ベ'}}, {{'ﾍ', 'ﾟ'}, {'ペ'}},
		{{'ﾎ'}, {'ホ'}}, {{'ﾎ', 'ﾞ'}, {'ボ'}}, {{'ﾎ', 'ﾟ'}, {'ポ'}},
		{{'ﾏ'}, {'マ'}},
		{{'ﾐ'}, {'ミ'}},
		{{'ﾑ'}, {'ム'}},
		{{'ﾒ'}, {'メ'}},
		{{'ﾓ'}, {'モ'}},
		{{'ﾔ'}, {'ヤ'}},
		{{'ﾕ'}, {'ユ'}},
		{{'ﾖ'}, {'ヨ'}},
		{{'ﾗ'}, {'ラ'}},
		{{'ﾘ'}, {'リ'}},
		{{'ﾙ'}, {'ル'}},
		{{'ﾚ'}, {'レ'}},
		{{'ﾛ'}, {'ロ'}},
		{{'ﾜ'}, {'ワ'}},
		{{'ﾝ'}, {'ン'}},
		{{'ﾞ'}, {'゛'}},
		{{'ﾟ'}, {'゜'}},
		{{'1'}, {'１'}},
		{{'2'}, {'２'}},
		{{'3'}, {'３'}},
		{{'4'}, {'４'}},
		{{'5'}, {'５'}},
		{{'6'}, {'６'}},
		{{'7'}, {'７'}},
		{{'8'}, {'８'}},
		{{'9'}, {'９'}},
		{{'0'}, {'０'}}
	};
	char[][][] hanzen = (char[][][]) table.clone();
	Arrays.sort( (Object[]) hanzen, HANZEN_COMPARATOR );
	HANZEN_TABLE = hanzen;

	char[][][] zenAdd = new char[][][]
	{
		{{'ｲ'}, {'ヰ'}},
		{{'ｴ'}, {'ヱ'}},
		{{'ｶ'}, {'ヵ'}},
		{{'ｹ'}, {'ヶ'}}
	};

	char[][][] zenhan = new char[ table.length + zenAdd.length ][][];
	System.arraycopy( table, 0, zenhan, 0, table.length );
	System.arraycopy( zenAdd, 0, zenhan, table.length, zenAdd.length );
	Arrays.sort( (Object[]) zenhan, ZENHAN_COMPARATOR );
	ZENHAN_TABLE = zenhan;
	}

	
	// constructors ----------------------------------------------------------
	/**
	 * instance化は行わないのでprivate constructorです。
	 */
	private HanZenTable() {}


	// inner classes ---------------------------------------------------------
	/**
	 * HankakuComparator と ZenkakuComparatorの共通部分を定義してあります。
	 * 双方のclassの差はgetCharArrayで全角、半角のどちらの配列を返すかで決定
	 * されるのでこのabstract methodをそれぞれのclassで定義します。
	 */
	private static abstract class BaseComparator
	implements Comparator
	{
	// constructors ------------------------------------------------------
	/**
	 * 特に何もしません。
	 */
	BaseComparator() {}


	// Comparator implementation -----------------------------------------
	/**
	 * xとyを比較します。x, y ともにabstract getCharArrayの定義により
	 * charを取り出せる場合に比較が可能です。それ以外の場合には
	 * ClassCastExceptionがthrowされます。
	 */
	public int compare( Object x, 
				Object y )
	{
		return compare( getCharArray( x ), getCharArray( y ) );
	}


	// public methods ----------------------------------------------------
	/**
	 * xとyを比較します。
	 */
	public final int compare( char[] xChars,
				  char[] yChars ){
		int max;
		int xLen = xChars.length;
		int yLen = yChars.length;

		if ( xLen < yLen )
		max = xLen;
		else
		max = yLen;

		int ret = 0;
		for ( int i = 0; i < max; i++ ) {
		if ( ( ret = xChars[ i ] - yChars[ i ] ) != 0 )
			return ret;
		}
		return xLen- yLen;
	}


	// abstract methods --------------------------------------------------
	/**
	 * このメソッドで o をchar[]に解釈する部分を定義して下さい。
	 * 入ってくるobjectはchar[]かchar[][]を想定しています。
	 * ( テーブルをsearchしたりsortしたりするときにはchar[][]が来ます )
	 */
	protected abstract char[] getCharArray( Object o );
	}


	/**
	 * このclassはequalsとの一貫性を持ちません。HANZEN_TABLEの半角char[]と
	 * 全角char[]を変換するためにだけ使用します。
	 */
	private static class HankakuComparator
	extends BaseComparator	{
		// constructors ------------------------------------------------------
		/**
		 * 特に何もしません。
		 */
		HankakuComparator() {}
		// over rides --------------------------------------------------------
		/**
		 * object oからchar[]を取り出し返します。
		 * oがchar[][]の時はHANKAKU_TABLEの一行とみなして
		 * ((char[][]) o)[ IDX_HANKAKU ]を返します。char[]の時はキャストして
		 * そのまま返します( 比較側として解釈 )。
		 */
		protected char[] getCharArray( Object o )	{
			if ( o instanceof char[][] ){
				return ((char[][]) o)[ IDX_HANKAKU ];
			}else{
				return (char[]) o;
			}
		}
	}

	/**
	 * このclassはequalsとの一貫性を持ちません。ZENKAKU_TABLEの全角char[]と
	 * 半角char[]を変換するためにだけ使用します。
	 */
	private static class ZenkakuComparator
	extends BaseComparator	{
		// constructors ------------------------------------------------------
		/**
		 * 特に何もしません。
		 */
		ZenkakuComparator() {}
		// over rides --------------------------------------------------------
		/**
		 * object oからchar[]を取り出し返します。
		 * oがchar[][]の時はZENKAKU_TABLEの一行とみなして
		 * ((char[][]) o)[ IDX_ZENKAKU ]を返します。char[]の時はキャストして
		 * そのまま返します( 比較側として解釈 )。
		 */
		protected char[] getCharArray( Object o )	{
			if ( o instanceof char[][] ){
				return ((char[][]) o)[ IDX_ZENKAKU ];
			}else{
				return (char[]) o;
			}
		}
	}
}

