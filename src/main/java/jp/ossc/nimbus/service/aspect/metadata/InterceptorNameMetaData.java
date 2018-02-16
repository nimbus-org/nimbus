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
import org.w3c.dom.*;
import jp.ossc.nimbus.core.*;
/**
 * コンポーネント定義&lt;interceptor-name&gt;要素メタデータ。<br>
 * コンポーネント定義ファイルの&lt;interceptor-name&gt;要素に記述された内容を格納するメタデータコンテナである。
 * @author H.Nakano
 * @version $Name:  $
 * @since 1.0
 * @see <a href="doc-files/interceptor.dtd.txt">インターセプタ定義ファイルDTD</a>
 */
public class InterceptorNameMetaData extends MetaData implements Serializable {
	
    private static final long serialVersionUID = 3537538730775599354L;
    
    /**
	 * &lt;interceptor-name&gt;要素の要素名文字列。
	 */
	public static final String INTERCEPTOR_NAME_TAG_NAME = "interceptor-name";
	/**
	 * &lt;interceptor-name&gt;要素のメタデータ。
	 * @see #getInterceptorName()
	 */
	private String interceptorName;
	/**
	 * コンストラクタ
	 */
	public InterceptorNameMetaData(MetaData parent){
		super(parent);
	}
	/**
	 * インターセプタ名を表す要素の内容で指定されたインターセプタ名を取得する。<br>
	 * 内容が指定されていない場合は、nullを返す。
	 * @return インターセプタ名を表す要素の内容
	 */
	public String getInterceptorName(){
		return interceptorName;
	}
	/**
	 * &lt;interceptor-name&gt;要素のElementをパースして、自分自身の初期化、及び子要素のメタデータの生成を行う。<br>
	 *
	 * @param element &lt;interceptor-name&gt;要素のElement
	 * @exception DeploymentException &lt;interceptor-name&gt;要素の解析、その結果によるメタデータの生成に失敗した場合
	 */
	public void importXML(Element element) throws DeploymentException{
		super.importXML(element);
		if(!element.getTagName().equals(INTERCEPTOR_NAME_TAG_NAME)){
			throw new DeploymentException(
				"Tag must be " + INTERCEPTOR_NAME_TAG_NAME + " : "
				 + element.getTagName()
			);
		}
		final String content = getElementContent(element);
		if(content == null || content.length() == 0){
			throw new DeploymentException(
				INTERCEPTOR_NAME_TAG_NAME + " is empty "
			);
		}
		interceptorName = content;
	}

}
