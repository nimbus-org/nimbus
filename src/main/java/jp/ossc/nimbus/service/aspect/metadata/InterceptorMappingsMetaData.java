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
package jp.ossc.nimbus.service.aspect.metadata;
//インポート
import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import jp.ossc.nimbus.core.*;
/**
 * ファイル操作クラス<p>
 * ファイルのコピーやリネームと言った操作を行う
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class InterceptorMappingsMetaData 
	extends MetaData 
	implements Serializable{
	
    private static final long serialVersionUID = 1599099889157424387L;
    
    /**
	 * &lt;interceptor-mappings&gt;要素の要素名文字列。<br>
	 */
	public static final String INTERCEPTOR_MAPPINGS_TAG_NAME = "interceptor-mappings";
	/**
	 * &lt;interceptor-mapping&gt;要素のメタデータのマップ
	 */
	private final List mInterceptorMappingList = new ArrayList();
	/**
	 * 
	 */
	public InterceptorMappingsMetaData(){
		super();
	}
	/**
	 * &lt;interceptor-mappings&gt;要素の子要素の&lt;interceptor-mapping&gt;要素のメタデータのリストを取得する。
	 *	@return List - &lt;interceptor-mapping&gt;要素のメタデータのリスト
	 */
	public List getInterceptorMappingList(){
		return mInterceptorMappingList;
	}
	/**
	 * &lt;interceptor-mappings&gt;要素の子要素として定義される&lt;interceptor-mapping&gt;要素のメタデータを登録する。
	 *	@param interceptorMapping - &lt;interceptor-mapping&gt;要素のメタデータ
	 */
	public void addInterceptorMapping(InterceptorMappingMetaData interceptorMapping){
		mInterceptorMappingList.add(interceptorMapping);
	}
	/**
	 *	<br>
	 *	@param element
	 */
	public void importXML(Element element) throws DeploymentException{
		super.importXML(element);
		if(!element.getTagName().equals(INTERCEPTOR_MAPPINGS_TAG_NAME)){
			throw new DeploymentException(
				"Tag must be " + INTERCEPTOR_MAPPINGS_TAG_NAME + " : "
				 + element.getTagName()
			);
		}
		final Iterator interceptorMappingElements = getChildrenByTagName(
			element,
			InterceptorMappingMetaData.INTERCEPTOR_MAPPING_TAG_NAME
		);
		while(interceptorMappingElements.hasNext()){
			final InterceptorMappingMetaData interceptorMappingData = new InterceptorMappingMetaData(this);
			interceptorMappingData.importXML((Element)interceptorMappingElements.next());
			addInterceptorMapping(interceptorMappingData);
		}
	}

}
