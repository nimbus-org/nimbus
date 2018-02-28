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
package jp.ossc.nimbus.service.cui;

/**
 *	Cui定数定義クラス
 *	@author	y-tokuda
 *	@version	1.00 作成：2003/10/31－ y-tokuda<BR>
 *				更新：
 */
public interface CuiTagDefine {
	/** 最初からやり直しを表す予約語 （Step名には使えない）*/
	public static final String REDO ="Redo";
	/** 中断を表す予約語 (Step名には使えない）*/
	public static final String INTERRUPT = "Interrupt";
	/** 終了を表す予約語 （Step名には使えない） */
	public static final String END = "End";
	/** dataInputタグ　子要素にstepを持つ*/
	public static final String DATAINPUT_TAG = "dataInput";
	/** stepタグ　子要素に、display,input,goto,endを持つ */
	public static final String STEP_TAG ="step";
	/** displayタグ　*/
	public static final String DISPLAY_TAG = "display";
	/** inputタグ　入力チェックを定義する。*/
	public static final String INPUT_TAG ="input";
	/** inputタグのtype属性。値として、"text","service"がある */
	public static final String INPUT_TAG_TYPE_ATT = "type";
	/** inputタグ属性で、サービスを指定するときの文字列 */
	public static final String INPUT_TYPE_SERVICE = "service";
	/** 終了タグ */
	public static final String END_TAG = "end";
	/** gotoタグ */
	public static final String GOTO_TAG = "goto";
	/** dataInputタグの、キー属性 */
	public static final String DATAINPUT_TAG_KEY_ATT = "key";
	/** displayタグのタイプ属性。 "service"または"text"を指定する */
	public static final String DISPLAY_TAG_TYPE_ATT = "type";
	/** stepタグのname属性 */
	public static final String STEP_TAG_NAME_ATT = "name";
	/** gotoタグのvalue属性 */
	public static final String GOTO_TAG_VALUE_ATT = "value";
	/** displayタグの タイプ属性に、サービスを指定する場合 */
	public static final String DISPLAY_TYPE_SERVICE = "service";
	/** 同一step中に、inputが複数指定された場合のエラーメッセージ */
	public static final String INPUT_MULTIDEF_ERR = "input multi defined (in a step ) err";
	/** 定義されていないStepに遷移しようとした場合のエラーメッセージ */
	public static final String NOT_DEF_GOTODIST = "not defined goto distination.";
	/** 同一step中に終了タグが複数存在した場合のエラーメッセージ */
	public static final String END_MULTI_DEF_ERR = "end multi defined (in a step) err";
	/** 終了タグのタイプ属性 */
	public static final String END_TAG_TYPE_ATT = "type";
	/** 終了タグのタイプ属性に指定する値（強制終了）*/
	public static final String END_FORCE = "force";
}
