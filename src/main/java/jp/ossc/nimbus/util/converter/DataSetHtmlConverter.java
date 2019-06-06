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
package jp.ossc.nimbus.util.converter;

import java.io.IOException;
import java.io.InputStream;

import jp.ossc.nimbus.beans.dataset.DataSet;
import jp.ossc.nimbus.beans.dataset.XpathPropertySchema;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * {@link DataSet}とXPathで表現されたHTMLデータとの変換を行う{@link Converter}。
 * <p>
 *     <ul>
 *         <li>プロパティスキーマが{@link XpathPropertySchema}であるプロパティに対して変換を行う。</li>
 *         <li>XPathは、XMLノードまたはXMLノードリストを返すように設定しなければならない。</li>
 *         <li>すべてのHTMLタグは大文字で表現しなければならない。</li>
 *     </ul>
 * </p>
 * @author T.Okada
 */
public class DataSetHtmlConverter extends DataSetXpathConverter {

    protected Document parseXml(InputStream inputStream) throws ConvertException {
        DOMParser parser = new DOMParser();
        InputSource inputSource = new InputSource(inputStream);
        if(characterEncodingToObject != null) {
            inputSource.setEncoding(characterEncodingToObject);
        }
        try {
            if(isSynchronizedDomParse){
                final Object lock = parser.getClass();
                synchronized(lock){
                    parser.parse(inputSource);
                }
            }else{
                parser.parse(inputSource);
            }
        } catch (SAXException e) {
            throw new ConvertException("Failed to parse a stream.", e);
        } catch (IOException e) {
            throw new ConvertException("Failed to parse a stream.", e);
        }
        return parser.getDocument();
    }

}
