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
// パッケージ
// インポート
package jp.ossc.nimbus.service.aspect.metadata;

import java.io.*;
import org.w3c.dom.*;
import jp.ossc.nimbus.core.*;

/**
 * コンポーネント定義&lt;interceptor-mapping&gt;要素メタデータ。<br>
 * コンポーネント定義ファイルの&lt;interceptor-mapping&gt;要素に記述された内容を格納するメタデータコンテナである。
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class InterceptorMappingMetaData
	extends MetaData
	implements Serializable {
	
    private static final long serialVersionUID = -3762781367465774137L;
    
    /**
	 * &lt;interceptor-mapping&gt;要素の要素名文字列。
	 */
	public static final String INTERCEPTOR_MAPPING_TAG_NAME = "interceptor-mapping";
	/**
	 * &lt;interceptor-name&gt;要素のメタデータ。
	 * @see #getInterceptorName()
	 */
	private InterceptorNameMetaData interceptorName;
	/**
	 * &lt;patterns&gt;要素のメタデータ。
	 * @see #getPatterns()
	 */
	private PatternsMetaData patterns;
	/**
	 * 親要素のメタデータを持つインスタンスを生成する。<br>
	 * InterceptorMappingMetaDataの親要素は、&lt;interceptor-mappings&gt;要素を表すInterceptorMappingsMetaDataである。
	 * @param parent 親要素のメタデータ
	 * @see InterceptorMappingsMetaData
	 */
	public InterceptorMappingMetaData(MetaData parent){
		super(parent);
	}
	/**
	 * この&lt;interceptor-mapping&gt;要素の子要素&lt;patterns&gt;要素に指定されたメタデータを取得する。<br>
	 * &lt;patterns&gt;要素が指定されていない場合は、nullを返す。<br>
	 *
	 * @return &lt;patterns&gt;要素に指定されたメタデータ
	 * @see PatternsMetaData
	 */
	public PatternsMetaData getPatterns(){
		return patterns;
	}
	/**
	 * この&lt;interceptor-mapping&gt;要素の子要素&lt;interceptor-name&gt;要素に指定されたメタデータを取得する。<br>
	 * &lt;interceptor-name&gt;要素が指定されていない場合は、nullを返す。<br>
	 *
	 * @return &lt;interceptor-name&gt;要素に指定されたメタデータ
	 * @see InterceptorNameMetaData
	 */
	public InterceptorNameMetaData getInterceptorName(){
		return interceptorName;
	}
	/**
	 * &lt;interceptor-mapping&gt;要素のElementをパースして、自分自身の初期化、及び子要素のメタデータの生成を行う。<br>
	 *
	 * @param element &lt;interceptor-mapping&gt;要素のElement
	 * @exception DeploymentException &lt;interceptor-mapping&gt;要素の解析、その結果によるメタデータの生成に失敗した場合
	 */
	public void importXML(Element element) throws DeploymentException{
		super.importXML(element);
		if(!element.getTagName().equals(INTERCEPTOR_MAPPING_TAG_NAME)){
			throw new DeploymentException(
				"Tag must be " + INTERCEPTOR_MAPPING_TAG_NAME + " : "
				 + element.getTagName()
			);
		}
		final Element interceptorNameElement =
		  getUniqueChild(element, InterceptorNameMetaData.INTERCEPTOR_NAME_TAG_NAME);
		interceptorName = new InterceptorNameMetaData(this);
		interceptorName.importXML(interceptorNameElement);
		final Element patternsElement = getUniqueChild(element, PatternsMetaData.PATTERNS_TAG_NAME);
		patterns = new PatternsMetaData(this);
		patterns.importXML(patternsElement);
	}

}
