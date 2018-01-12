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
package jp.ossc.nimbus.util.converter;

/**
 * âpéöÉRÉìÉoÅ[É^ÅB<p>
 * <table border=5>
 *     <tr><th>îºäpâpéö</th><th>ëSäpâpéö</th></tr>
 *     <tr><td>a</td><td>ÇÅ</td></tr>
 *     <tr><td>b</td><td>ÇÇ</td></tr>
 *     <tr><td>c</td><td>ÇÉ</td></tr>
 *     <tr><td>d</td><td>ÇÑ</td></tr>
 *     <tr><td>e</td><td>ÇÖ</td></tr>
 *     <tr><td>f</td><td>ÇÜ</td></tr>
 *     <tr><td>g</td><td>Çá</td></tr>
 *     <tr><td>h</td><td>Çà</td></tr>
 *     <tr><td>i</td><td>Çâ</td></tr>
 *     <tr><td>j</td><td>Çä</td></tr>
 *     <tr><td>k</td><td>Çã</td></tr>
 *     <tr><td>l</td><td>Çå</td></tr>
 *     <tr><td>m</td><td>Çç</td></tr>
 *     <tr><td>n</td><td>Çé</td></tr>
 *     <tr><td>o</td><td>Çè</td></tr>
 *     <tr><td>p</td><td>Çê</td></tr>
 *     <tr><td>q</td><td>Çë</td></tr>
 *     <tr><td>r</td><td>Çí</td></tr>
 *     <tr><td>s</td><td>Çì</td></tr>
 *     <tr><td>t</td><td>Çî</td></tr>
 *     <tr><td>u</td><td>Çï</td></tr>
 *     <tr><td>v</td><td>Çñ</td></tr>
 *     <tr><td>w</td><td>Çó</td></tr>
 *     <tr><td>x</td><td>Çò</td></tr>
 *     <tr><td>y</td><td>Çô</td></tr>
 *     <tr><td>z</td><td>Çö</td></tr>
 *     <tr><td>A</td><td>Ç`</td></tr>
 *     <tr><td>B</td><td>Ça</td></tr>
 *     <tr><td>C</td><td>Çb</td></tr>
 *     <tr><td>D</td><td>Çc</td></tr>
 *     <tr><td>E</td><td>Çd</td></tr>
 *     <tr><td>F</td><td>Çe</td></tr>
 *     <tr><td>G</td><td>Çf</td></tr>
 *     <tr><td>H</td><td>Çg</td></tr>
 *     <tr><td>I</td><td>Çh</td></tr>
 *     <tr><td>J</td><td>Çi</td></tr>
 *     <tr><td>K</td><td>Çj</td></tr>
 *     <tr><td>L</td><td>Çk</td></tr>
 *     <tr><td>M</td><td>Çl</td></tr>
 *     <tr><td>N</td><td>Çm</td></tr>
 *     <tr><td>O</td><td>Çn</td></tr>
 *     <tr><td>P</td><td>Ço</td></tr>
 *     <tr><td>Q</td><td>Çp</td></tr>
 *     <tr><td>R</td><td>Çq</td></tr>
 *     <tr><td>S</td><td>Çr</td></tr>
 *     <tr><td>T</td><td>Çs</td></tr>
 *     <tr><td>U</td><td>Çt</td></tr>
 *     <tr><td>V</td><td>Çu</td></tr>
 *     <tr><td>W</td><td>Çv</td></tr>
 *     <tr><td>X</td><td>Çw</td></tr>
 *     <tr><td>Y</td><td>Çx</td></tr>
 *     <tr><td>Z</td><td>Çy</td></tr>
 * </table>
 * 
 * @author   M.Takata
 */
public class AlphabetStringConverter extends HankakuZenkakuStringConverter
 implements java.io.Serializable{
    
    private static final long serialVersionUID = 2583585212364793389L;
    
    /**
     * îºäpÅ®ëSäpïœä∑ÇÃâpéöÉRÉìÉoÅ[É^Çê∂ê¨Ç∑ÇÈÅB<p>
     */
    public AlphabetStringConverter(){
        this(HANKAKU_TO_ZENKAKU);
    }
    
    /**
     * éwíËÇ≥ÇÍÇΩïœä∑éÌï ÇÃâpéöÉRÉìÉoÅ[É^Å[Çê∂ê¨Ç∑ÇÈÅB<p>
     *
     * @param type ïœä∑éÌï 
     * @see #HANKAKU_TO_ZENKAKU
     * @see #ZENKAKU_TO_HANKAKU
     */
    public AlphabetStringConverter(int type){
        super(type);
    }
    
    /**
     * îºäpëSäpïœä∑ÉLÉÉÉâÉNÉ^îzóÒÇéÊìæÇ∑ÇÈÅB<p>
     *
     * @return {@link AlphabetCharacterConverter#CONV_CHARS}
     */
    protected char[][] getHankakuZenkakuChars(){
        return AlphabetCharacterConverter.CONV_CHARS;
    }
    
    /**
     * îºäpëSäpïœä∑ï∂éöóÒîzóÒÇéÊìæÇ∑ÇÈÅB<p>
     *
     * @return null
     */
    protected String[][] getHankakuZenkakuStrings(){
        return null;
    }
}
