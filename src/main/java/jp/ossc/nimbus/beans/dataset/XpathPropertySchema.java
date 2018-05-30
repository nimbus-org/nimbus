/*
 * This software is distributed under following license based on modified BSD
 * style license.
 * ----------------------------------------------------------------------
 * 
 * Copyright 2008 The Nimbus Project. All rights reserved.
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
package jp.ossc.nimbus.beans.dataset;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * プロパティに対応するXPathを設定可能な{@link PropertySchema}。
 * <p>
 *   {@link DefaultPropertySchema}でサポートされるスキーマ定義に加えて、プロパティに対応するXPathを定義することができる。<br/>
 *   フォーマット：名前,型,入力変換種類,出力変換種類,制約,XPath,主キーフラグ
 * </p>
 * @author T.Okada
 */
public class XpathPropertySchema extends DefaultPropertySchema {
    
    private static final long serialVersionUID = -3734020985222260851L;
    
    private XPathExpression xpathExpression;
    
    /**
     * 空のプロパティスキーマを生成する。<p>
     */
    public XpathPropertySchema(){
    }
    
    /**
     * プロパティスキーマを生成する。<p>
     *
     * @param schema プロパティのスキーマ定義
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public XpathPropertySchema(String schema) throws PropertySchemaDefineException{
        super(schema);
    }
    
    /**
     * @see DefaultPropertySchema#parseSchema(String, int, String)
     */
    protected void parseSchema(String schema, int index, String value) throws PropertySchemaDefineException {
        switch(index){
        case 0:
        case 1:
        case 2:
        case 3:
        case 4:
            super.parseSchema(schema, index, value);
            break;
        case 5:
            parseXPath(schema, value);
            break;
        case 6:
            parsePrimaryKey(schema, value);
            break;
        }
    }
    
    /**
     * 文字列を解析してXPathを抽出する。
     * @param value 解析対象の文字列
     */
    protected void parseXPath(String schema, String value) {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        try {
            xpathExpression = xpath.compile(value);
        } catch (XPathExpressionException e) {
            throw new PropertySchemaDefineException(schema, e.getMessage());
        }
    }
    
    /**
     * XPathを取得する。
     */
    public XPathExpression getXpathExpression() {
        return xpathExpression;
    }
}
