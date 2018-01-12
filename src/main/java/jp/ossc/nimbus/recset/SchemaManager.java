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
package jp.ossc.nimbus.recset;
import java.util.*;
/**
 * スキーマ管理クラス<p>
 * スキーマの集合を管理する。
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class SchemaManager {
	
	/** スキーマ管理用Hashtable */
	static private Hashtable mSchemaHash = new Hashtable();
	
	/** 更新スキーマ管理用Hashtable */
	static private Hashtable mGoneSchemaHash = new Hashtable();
	
	/**
	 * 指定されたスキーマ文字列のRowSchemaを応答します。
	 * @param schema スキーマ文字列
	 * @return RowSchema  
	 */
	static public RowSchema findRowSchema(String schema) {
			RowSchema ret = (RowSchema) mSchemaHash.get(schema);
			if (ret == null) {
				ret = new RowSchema();
				ret.initSchema(schema);
				mSchemaHash.put(schema,ret);
				mGoneSchemaHash.put(ret,ret.makeGoneSchema());
			}
			return ret ;
	}
	
	/**
	 * 指定されたRowSchemaの更新用RowSchemaを応答します。
	 * @param schema RowSchema
	 * @return 更新用RowSchema
	 */
	static public RowSchema findGoneRowSchema(RowSchema schema) {
			RowSchema ret = (RowSchema) mGoneSchemaHash.get(schema);
			if (ret == null) {
				ret = schema.makeGoneSchema() ;
				mGoneSchemaHash.put(schema,ret);
			}
			return ret ;
	}
}
