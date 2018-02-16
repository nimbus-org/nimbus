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

import junit.framework.TestCase;
import java.util.regex.Pattern;

public class ConvertersTest extends TestCase{
    
    public ConvertersTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(ConvertersTest.class);
    }
    
    public void testAlphabetHankakuFromZenkakuStringConverter() throws Exception {
        Converter conv = Converters.getAlphabetHankakuFromZenkakuStringConverter();
        final String from = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String to = "ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ";
        assertEquals(to, conv.convert(from));
    }
    
    public void testAlphabetZenkakuFromHankakuStringConverter() throws Exception {
        Converter conv = Converters.getAlphabetZenkakuFromHankakuStringConverter();
        final String from = "ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ";
        final String to = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        assertEquals(to, conv.convert(from));
    }
    
    public void testKatakanaHankakuFromZenkakuStringConverter() throws Exception {
        Converter conv = Converters.getKatakanaHankakuFromZenkakuStringConverter();
        final String from = "｡｢｣､･ｦｧｨｩｪｫｬｭｮｯｰｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜﾝﾞﾟｳﾞｶﾞｷﾞｸﾞｹﾞｺﾞｻﾞｼﾞｽﾞｾﾞｿﾞﾀﾞﾁﾞﾂﾞﾃﾞﾄﾞﾊﾞﾋﾞﾌﾞﾍﾞﾎﾞﾊﾟﾋﾟﾌﾟﾍﾟﾎﾟ";
        final String to = "。「」、・ヲァィゥェォャュョッーアイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワン゛゜ヴガギグゲゴザジズゼゾダヂヅデドバビブベボパピプペポ";
        assertEquals(to, conv.convert(from));
    }
    
    public void testKatakanaZenkakuFromHankakuStringConverter() throws Exception {
        Converter conv = Converters.getKatakanaZenkakuFromHankakuStringConverter();
        final String from = "。「」、・ヲァィゥェォャュョッーアイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワン゛゜ヴガギグゲゴザジズゼゾダヂヅデドバビブベボパピプペポ";
        final String to = "｡｢｣､･ｦｧｨｩｪｫｬｭｮｯｰｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜﾝﾞﾟｳﾞｶﾞｷﾞｸﾞｹﾞｺﾞｻﾞｼﾞｽﾞｾﾞｿﾞﾀﾞﾁﾞﾂﾞﾃﾞﾄﾞﾊﾞﾋﾞﾌﾞﾍﾞﾎﾞﾊﾟﾋﾟﾌﾟﾍﾟﾎﾟ";
        assertEquals(to, conv.convert(from));
    }
    public void testNumberHankakuFromZenkakuStringConverter() throws Exception {
        Converter conv = Converters.getNumberHankakuFromZenkakuStringConverter();
        final String from = "0123456789";
        final String to = "０１２３４５６７８９";
        assertEquals(to, conv.convert(from));
    }
    public void testNumberZenkakuFromHankakuStringConverter() throws Exception {
        Converter conv = Converters.getNumberZenkakuFromHankakuStringConverter();
        final String from = "０１２３４５６７８９";
        final String to = "0123456789";
        assertEquals(to, conv.convert(from));
    }
    public void testSymbolHankakuFromZenkakuStringConverter() throws Exception {
        Converter conv = Converters.getSymbolHankakuFromZenkakuStringConverter();
        final String from = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
        final String to = "　！”＃＄％＆’（）＊＋，－．／：；＜＝＞？＠［￥］＾＿‘｛｜｝～";
        assertEquals(to, conv.convert(from));
    }
    public void testSymbolZenkakuFromHankakuStringConverter() throws Exception {
        Converter conv = Converters.getSymbolZenkakuFromHankakuStringConverter();
        final String from = "　！”＃＄％＆’（）＊＋，－．／：；＜＝＞？＠［￥］＾＿‘｛｜｝～";
        final String to = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
        
        assertEquals(to, conv.convert(from));
    }
    public void testKatakanaFromHiraganaStringConverter() throws Exception {
        Converter conv = Converters.getKatakanaFromHiraganaStringConverter();
        final String from = "アァイィウゥエェオォカガキギクグケゲコゴサザシジスズセゼソゾタダチヂッツヅテデトドナニヌネノハバパヒビピフブプヘベペホボポマミムメモヤャユュヨョラリルレロヮワヰヱヲンヽヾ";
        final String to = "あぁいぃうぅえぇおぉかがきぎくぐけげこごさざしじすずせぜそぞただちぢっつづてでとどなにぬねのはばぱひびぴふぶぷへべぺほぼぽまみむめもやゃゆゅよょらりるれろゎわゐゑをんゝゞ";
        assertEquals(to, conv.convert(from));
    }
    public void testHiraganaFromKatakanaStringConverter() throws Exception {
        Converter conv = Converters.getHiraganaFromKatakanaStringConverter();
        final String from = "あぁいぃうぅえぇおぉかがきぎくぐけげこごさざしじすずせぜそぞただちぢっつづてでとどなにぬねのはばぱひびぴふぶぷへべぺほぼぽまみむめもやゃゆゅよょらりるれろゎわゐゑをんゝゞ";
        final String to = "アァイィウゥエェオォカガキギクグケゲコゴサザシジスズセゼソゾタダチヂッツヅテデトドナニヌネノハバパヒビピフブプヘベペホボポマミムメモヤャユュヨョラリルレロヮワヰヱヲンヽヾ";
        assertEquals(to, conv.convert(from));
    }
    public void testHankakuFromZenkakuStringConverter() throws Exception {
        Converter conv = Converters.getHankakuFromZenkakuStringConverter();
        final String from = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			+ "｡｢｣､･ｦｧｨｩｪｫｬｭｮｯｰｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜﾝﾞﾟｳﾞｶﾞｷﾞｸﾞｹﾞｺﾞｻﾞｼﾞｽﾞｾﾞｿﾞﾀﾞﾁﾞﾂﾞﾃﾞﾄﾞﾊﾞﾋﾞﾌﾞﾍﾞﾎﾞﾊﾟﾋﾟﾌﾟﾍﾟﾎﾟ"
			+"0123456789"
			+" !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

        final String to = "ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ"
			+ "。「」、・ヲァィゥェォャュョッーアイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワン゛゜ヴガギグゲゴザジズゼゾダヂヅデドバビブベボパピプペポ"
			+ "０１２３４５６７８９"
			+ "　！”＃＄％＆’（）＊＋，－．／：；＜＝＞？＠［￥］＾＿‘｛｜｝～";
        assertEquals(to, conv.convert(from));
    }
    public void testZenkakuFromHankakuStringConverter() throws Exception {
        Converter conv = Converters.getZenkakuFromHankakuStringConverter();
        final String from = "ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ"
			+ "。「」、・ヲァィゥェォャュョッーアイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワン゛゜ヴガギグゲゴザジズゼゾダヂヅデドバビブベボパピプペポ"
			+ "０１２３４５６７８９"
			+ "　！”＃＄％＆’（）＊＋，－．／：；＜＝＞？＠［￥］＾＿‘｛｜｝～";
        
        final String to = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			+ "｡｢｣､･ｦｧｨｩｪｫｬｭｮｯｰｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜﾝﾞﾟｳﾞｶﾞｷﾞｸﾞｹﾞｺﾞｻﾞｼﾞｽﾞｾﾞｿﾞﾀﾞﾁﾞﾂﾞﾃﾞﾄﾞﾊﾞﾋﾞﾌﾞﾍﾞﾎﾞﾊﾟﾋﾟﾌﾟﾍﾟﾎﾟ"
			+"0123456789"
			+" !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

        assertEquals(to, conv.convert(from));
    }
    public void testNewCustomConverter1() throws Exception {

    	Converter conv = Converters.newCustomConverter();
    	
        final String from = "aAＡｱアあ1１#＃";
        final String to = from;
        
        assertEquals(to, conv.convert(from));
    }
    public void testNewCustomConverter2() throws Exception {
    	Converter[] convsHankakuFromZenkaku = {
    			Converters.getAlphabetHankakuFromZenkakuStringConverter(),
				Converters.getKatakanaHankakuFromZenkakuStringConverter(),
				Converters.getNumberHankakuFromZenkakuStringConverter(),
				Converters.getSymbolHankakuFromZenkakuStringConverter()};
    	Converter[] convsZenkakuFromHankaku = {
    			Converters.getAlphabetZenkakuFromHankakuStringConverter(),
				Converters.getKatakanaZenkakuFromHankakuStringConverter(),
				Converters.getNumberZenkakuFromHankakuStringConverter(),
				Converters.getSymbolZenkakuFromHankakuStringConverter()};

    	// 全ての文字を半角→全角
		Converter convHankakuFromZenkaku = Converters.newCustomConverter(convsHankakuFromZenkaku);
    	// 全ての文字を全角→半角
		Converter convZenkakuFromHankaku = Converters.newCustomConverter(convsZenkakuFromHankaku);
        
        final String from = "aAＡｱアあ1１#＃";
        final String toHankakuFromZenkaku = "ａＡＡアアあ１１＃＃";
        final String toZenkakuFromHankaku = "aAAｱｱあ11##";
        
        assertEquals(toHankakuFromZenkaku, convHankakuFromZenkaku.convert(from));
        assertEquals(toZenkakuFromHankaku, convZenkakuFromHankaku.convert(from));
    }

    public void testNewCustomStringConverter1() throws Exception {
    	char[] fromChar = {'a','A','Ａ','ｱ','ア','あ','1','１','#','＃'};
    	char[] toChar = {'２','イ','＠','Ｘ', 'y', '#','ｶ','Z','ざ','9'}; 

    	Converter conv = Converters.newCustomStringConverter(ReversibleConverter.POSITIVE_CONVERT,fromChar,toChar);
        
        final String from = "ｱa1#Ａア＃Aあ１";
        final String to = "Ｘ２ｶざ＠y9イ#Z";
        
        assertEquals(to, conv.convert(from));
    }
    public void testNewCustomStringConverter2() throws Exception {
    	String[] fromStr = {"<", "8９", "ﾜヲん", "oPＱｒ"};
    	String[] toStr = {"0１", "ＸＹＺ", "２３", "45６78９"};

    	Converter conv = Converters.newCustomStringConverter(ReversibleConverter.POSITIVE_CONVERT,fromStr,toStr);
        
        final String from = "=ﾜヲん=ﾜヲ=oPＱｒ=PＱｒ=8９=９=<<<";
        final String to = "=２３=ﾜヲ=45６78９=PＱｒ=ＸＹＺ=９=0１0１0１";
        
       assertEquals(to, conv.convert(from));
    }
    public void testNewCustomStringConverter3() throws Exception {
    	String[] fromStr = {"<", "8９", "ﾜヲん", "oPＱｒ"};
    	String[] toStr = {"0１", "ＸＹＺ", "２３", "45６78９"};
    	char[] fromChar = {'a','A','Ａ','ｱ','ア','あ','1','１','#','＃'};
    	char[] toChar = {'２','イ','＠','Ｘ', 'y', '#','ｶ','Z','ざ','9'}; 

    	Converter conv = Converters.newCustomStringConverter(ReversibleConverter.POSITIVE_CONVERT,fromStr,toStr,fromChar,toChar);
        
        final String from = "ｱa1#Ａア＃Aあ１=ﾜヲん=ﾜヲ=oPＱｒ=PＱｒ=8９=９=<<<";
        final String to = "Ｘ２ｶざ＠y9イ#Z=２３=ﾜヲ=45６78９=PＱｒ=ＸＹＺ=９=0Z0Z0Z";
        
        assertEquals(to, conv.convert(from));
    }
    public void testPatternConverter1() throws Exception{
    	String[] fromStr = {"a*b", "c+d"};
    	String[] toStr = {"123", "456"};
    	
    	PatternStringConverter conv = Converters.patternStringConverter();
    	conv.setConvertStrings(fromStr, toStr);
    	
        final String from = "-aaaaab-b-AAB-ccccd-d-CD-";
        final String to = "-123-123-AAB-456-d-CD-";
    	
        assertEquals(to, conv.convert(from));
    }
    public void testPatternConverter2() throws Exception{
    	String[] fromStr = {"a*b", "c+d"};
    	String[] toStr = {"123", "456"};
    	
    	PatternStringConverter conv = Converters.patternStringConverter(Pattern.CASE_INSENSITIVE);
    	conv.setConvertStrings(fromStr, toStr);
    	
        final String from = "-aaaaab-b-AAB-ccccd-d-CD-";
        final String to = "-123-123-123-456-d-456-";
    	
        assertEquals(to, conv.convert(from));
    }
    public void testAlphabetHankakuFromZenkakuCharacterConverter() throws Exception {
    	CharacterConverter conv = Converters.getAlphabetHankakuFromZenkakuCharacterConverter();
        final char[] from = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        final char[] to = "ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ".toCharArray();
        for(int i=0;i<from.length;i++){
            assertEquals(to[i], conv.convert(from[i]));
        }
    }
    public void testAlphabetZenkakuFromHankakuCharacterConverter() throws Exception {
    	CharacterConverter conv = Converters.getAlphabetZenkakuFromHankakuCharacterConverter();
    	final char[] from = "ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ".toCharArray();
    	final char[] to = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        for(int i=0;i<from.length;i++){
            assertEquals(to[i], conv.convert(from[i]));
        }
    }
    
    public void testKatakanaHankakuFromZenkakuCharacterConverter() throws Exception {
    	CharacterConverter conv = Converters.getKatakanaHankakuFromZenkakuCharacterConverter();
    	final char[] from = "｡｢｣､･ｦｧｨｩｪｫｬｭｮｯｰｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜﾝﾞﾟ".toCharArray(); 
    	final char[] to = "。「」、・ヲァィゥェォャュョッーアイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワン゛゜".toCharArray();
    	
    	for(int i=0;i<from.length;i++){
            assertEquals(to[i], conv.convert(from[i]));
        }
    }
    
    public void testKatakanaZenkakuFromHankakuCharacterConverter() throws Exception {
    	CharacterConverter conv = Converters.getKatakanaZenkakuFromHankakuCharacterConverter();
    	final char[] from = "。「」、・ヲァィゥェォャュョッーアイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワン゛゜".toCharArray();
    	final char[] to = "｡｢｣､･ｦｧｨｩｪｫｬｭｮｯｰｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜﾝﾞﾟ".toCharArray(); 
        for(int i=0;i<from.length;i++){
            assertEquals(to[i], conv.convert(from[i]));
        }
    }
    public void testNumberHankakuFromZenkakuCharacterConverter() throws Exception {
    	CharacterConverter conv = Converters.getNumberHankakuFromZenkakuCharacterConverter();
    	final char[] from = "0123456789".toCharArray();
    	final char[] to = "０１２３４５６７８９".toCharArray();
        for(int i=0;i<from.length;i++){
            assertEquals(to[i], conv.convert(from[i]));
        }
    }
    public void testNumberZenkakuFromHankakuCharacterConverter() throws Exception {
    	CharacterConverter conv = Converters.getNumberZenkakuFromHankakuCharacterConverter();
    	final char[] from = "０１２３４５６７８９".toCharArray();
    	final char[] to = "0123456789".toCharArray();
        for(int i=0;i<from.length;i++){
            assertEquals(to[i], conv.convert(from[i]));
        }
    }
    public void testSymbolHankakuFromZenkakuCharacterConverter() throws Exception {
    	CharacterConverter conv = Converters.getSymbolHankakuFromZenkakuCharacterConverter();
    	final char[] from = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~".toCharArray();
    	final char[] to = "　！”＃＄％＆’（）＊＋，－．／：；＜＝＞？＠［￥］＾＿‘｛｜｝～".toCharArray();
        for(int i=0;i<from.length;i++){
            assertEquals(to[i], conv.convert(from[i]));
        }
    }
    public void testSymbolZenkakuFromHankakuCharacterConverter() throws Exception {
    	CharacterConverter conv = Converters.getSymbolZenkakuFromHankakuCharacterConverter();
    	final char[] from = "　！”＃＄％＆’（）＊＋，－．／：；＜＝＞？＠［￥］＾＿‘｛｜｝～".toCharArray();
    	final char[] to = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~".toCharArray();
        
        for(int i=0;i<from.length;i++){
            assertEquals(to[i], conv.convert(from[i]));
        }
    }
    public void testKatakanaFromHiraganaCharacterConverter() throws Exception {
    	CharacterConverter conv = Converters.getKatakanaFromHiraganaCharacterConverter();
    	final char[] from = "アァイィウゥエェオォカガキギクグケゲコゴサザシジスズセゼソゾタダチヂッツヅテデトドナニヌネノハバパヒビピフブプヘベペホボポマミムメモヤャユュヨョラリルレロヮワヰヱヲンヽヾ".toCharArray();
    	final char[] to = "あぁいぃうぅえぇおぉかがきぎくぐけげこごさざしじすずせぜそぞただちぢっつづてでとどなにぬねのはばぱひびぴふぶぷへべぺほぼぽまみむめもやゃゆゅよょらりるれろゎわゐゑをんゝゞ".toCharArray();
        for(int i=0;i<from.length;i++){
            assertEquals(to[i], conv.convert(from[i]));
        }
    }
    public void testHiraganaFromKatakanaCharacterConverter() throws Exception {
    	CharacterConverter conv = Converters.getHiraganaFromKatakanaCharacterConverter();
    	final char[] from = "あぁいぃうぅえぇおぉかがきぎくぐけげこごさざしじすずせぜそぞただちぢっつづてでとどなにぬねのはばぱひびぴふぶぷへべぺほぼぽまみむめもやゃゆゅよょらりるれろゎわゐゑをんゝゞ".toCharArray();
    	final char[] to = "アァイィウゥエェオォカガキギクグケゲコゴサザシジスズセゼソゾタダチヂッツヅテデトドナニヌネノハバパヒビピフブプヘベペホボポマミムメモヤャユュヨョラリルレロヮワヰヱヲンヽヾ".toCharArray();
        for(int i=0;i<from.length;i++){
            assertEquals(to[i], conv.convert(from[i]));
        }
    }
    public void testHankakuFromZenkakuCharacterConverter() throws Exception {
    	CharacterConverter conv = Converters.getHankakuFromZenkakuCharacterConverter();
    	final String fromStr = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			+ "｡｢｣､･ｦｧｨｩｪｫｬｭｮｯｰｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜﾝﾞﾟ" 
			+"0123456789"
			+" !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    	final String toStr = "ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ"
			+ "。「」、・ヲァィゥェォャュョッーアイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワン゛゜" 
			+ "０１２３４５６７８９"
			+ "　！”＃＄％＆’（）＊＋，－．／：；＜＝＞？＠［￥］＾＿‘｛｜｝～";
    	final char[] from = fromStr.toCharArray();
    	final char[] to = toStr.toCharArray();

    	for(int i=0;i<from.length;i++){
            assertEquals(to[i], conv.convert(from[i]));
        }
    }
    public void testZenkakuFromHankakuCharacterConverter() throws Exception {
    	CharacterConverter conv = Converters.getZenkakuFromHankakuCharacterConverter();
    	final String fromStr = "ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ"
			+ "。「」、・ヲァィゥェォャュョッーアイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワン゛゜" 
			+ "０１２３４５６７８９"
			+ "　！”＃＄％＆’（）＊＋，－．／：；＜＝＞？＠［￥］＾＿‘｛｜｝～";
        
    	final String toStr = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			+ "｡｢｣､･ｦｧｨｩｪｫｬｭｮｯｰｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜﾝﾞﾟ" 
			+"0123456789"
			+" !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    	final char[] from = fromStr.toCharArray();
    	final char[] to = toStr.toCharArray();
        for(int i=0;i<from.length;i++){
            assertEquals(to[i], conv.convert(from[i]));
        }
    }

}
