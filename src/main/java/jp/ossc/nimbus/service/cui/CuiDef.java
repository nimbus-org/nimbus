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
 *	
 *	@author	y-tokuda
 *	@version	1.00 作成：2003/10/29－ y-tokuda<BR>
 *				更新：
 */
public class CuiDef {
	public static final String REDO ="Redo";
	public static final String INTERRUPT = "Interrupt";
	public static final String END = "End";
	public static final String DATAINPUT_TAG = "dataInput";
	public static final String STEP_TAG ="step";
	public static final String DISPLAY_TAG = "display";
	public static final String INPUT_TAG ="input";
	public static final String END_TAG = "end";
	public static final String GOTO_TAG = "goto";
	public static final String DATAINPUT_TAG_KEY_ATT = "key";
	public static final String DISPLAY_TAG_TYPE_ATT = "type";
	public static final String STEP_TAG_NAME_ATT = "name";
	public static final String GOTO_TAG_VALUE_ATT = "value";
	public static final String DISPLAY_TYPE_SERVICE = "service";
	public static final String SERVICE_MULTIDEF_ERR = "service multi defined (in a step ) err";
	public static final String INPUT_MULTIDEF_ERR = "input multi defined (in a step ) err";
	public static final String NOT_DEF_GOTODIST = "not defined goto distination.";
	public static final String END_MULTI_DEF_ERR = "end multi defined (in a step) err";
	public static final String END_TAG_TYPE_ATT = "type";
	public static final String END_NORMAL = "normal";
	public static final String END_Force = "force";
}
