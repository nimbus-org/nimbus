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
        final String to = "‚‚‚‚ƒ‚„‚…‚†‚‡‚ˆ‚‰‚Š‚‹‚Œ‚‚‚‚‚‘‚’‚“‚”‚•‚–‚—‚˜‚™‚š‚`‚a‚b‚c‚d‚e‚f‚g‚h‚i‚j‚k‚l‚m‚n‚o‚p‚q‚r‚s‚t‚u‚v‚w‚x‚y";
        assertEquals(to, conv.convert(from));
    }
    
    public void testAlphabetZenkakuFromHankakuStringConverter() throws Exception {
        Converter conv = Converters.getAlphabetZenkakuFromHankakuStringConverter();
        final String from = "‚‚‚‚ƒ‚„‚…‚†‚‡‚ˆ‚‰‚Š‚‹‚Œ‚‚‚‚‚‘‚’‚“‚”‚•‚–‚—‚˜‚™‚š‚`‚a‚b‚c‚d‚e‚f‚g‚h‚i‚j‚k‚l‚m‚n‚o‚p‚q‚r‚s‚t‚u‚v‚w‚x‚y";
        final String to = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        assertEquals(to, conv.convert(from));
    }
    
    public void testKatakanaHankakuFromZenkakuStringConverter() throws Exception {
        Converter conv = Converters.getKatakanaHankakuFromZenkakuStringConverter();
        final String from = "¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏĞÑÒÓÔÕÖ×ØÙÚÛÜİŞß³Ş¶Ş·Ş¸Ş¹ŞºŞ»Ş¼Ş½Ş¾Ş¿ŞÀŞÁŞÂŞÃŞÄŞÊŞËŞÌŞÍŞÎŞÊßËßÌßÍßÎß";
        final String to = "BuvAEƒ’ƒ@ƒBƒDƒFƒHƒƒƒ…ƒ‡ƒb[ƒAƒCƒEƒGƒIƒJƒLƒNƒPƒRƒTƒVƒXƒZƒ\ƒ^ƒ`ƒcƒeƒgƒiƒjƒkƒlƒmƒnƒqƒtƒwƒzƒ}ƒ~ƒ€ƒƒ‚ƒ„ƒ†ƒˆƒ‰ƒŠƒ‹ƒŒƒƒƒ“JKƒ”ƒKƒMƒOƒQƒSƒUƒWƒYƒ[ƒ]ƒ_ƒaƒdƒfƒhƒoƒrƒuƒxƒ{ƒpƒsƒvƒyƒ|";
        assertEquals(to, conv.convert(from));
    }
    
    public void testKatakanaZenkakuFromHankakuStringConverter() throws Exception {
        Converter conv = Converters.getKatakanaZenkakuFromHankakuStringConverter();
        final String from = "BuvAEƒ’ƒ@ƒBƒDƒFƒHƒƒƒ…ƒ‡ƒb[ƒAƒCƒEƒGƒIƒJƒLƒNƒPƒRƒTƒVƒXƒZƒ\ƒ^ƒ`ƒcƒeƒgƒiƒjƒkƒlƒmƒnƒqƒtƒwƒzƒ}ƒ~ƒ€ƒƒ‚ƒ„ƒ†ƒˆƒ‰ƒŠƒ‹ƒŒƒƒƒ“JKƒ”ƒKƒMƒOƒQƒSƒUƒWƒYƒ[ƒ]ƒ_ƒaƒdƒfƒhƒoƒrƒuƒxƒ{ƒpƒsƒvƒyƒ|";
        final String to = "¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏĞÑÒÓÔÕÖ×ØÙÚÛÜİŞß³Ş¶Ş·Ş¸Ş¹ŞºŞ»Ş¼Ş½Ş¾Ş¿ŞÀŞÁŞÂŞÃŞÄŞÊŞËŞÌŞÍŞÎŞÊßËßÌßÍßÎß";
        assertEquals(to, conv.convert(from));
    }
    public void testNumberHankakuFromZenkakuStringConverter() throws Exception {
        Converter conv = Converters.getNumberHankakuFromZenkakuStringConverter();
        final String from = "0123456789";
        final String to = "‚O‚P‚Q‚R‚S‚T‚U‚V‚W‚X";
        assertEquals(to, conv.convert(from));
    }
    public void testNumberZenkakuFromHankakuStringConverter() throws Exception {
        Converter conv = Converters.getNumberZenkakuFromHankakuStringConverter();
        final String from = "‚O‚P‚Q‚R‚S‚T‚U‚V‚W‚X";
        final String to = "0123456789";
        assertEquals(to, conv.convert(from));
    }
    public void testSymbolHankakuFromZenkakuStringConverter() throws Exception {
        Converter conv = Converters.getSymbolHankakuFromZenkakuStringConverter();
        final String from = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
        final String to = "@Ih”“•fij–{C|D^FGƒ„H—mnOQeobp`";
        assertEquals(to, conv.convert(from));
    }
    public void testSymbolZenkakuFromHankakuStringConverter() throws Exception {
        Converter conv = Converters.getSymbolZenkakuFromHankakuStringConverter();
        final String from = "@Ih”“•fij–{C|D^FGƒ„H—mnOQeobp`";
        final String to = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
        
        assertEquals(to, conv.convert(from));
    }
    public void testKatakanaFromHiraganaStringConverter() throws Exception {
        Converter conv = Converters.getKatakanaFromHiraganaStringConverter();
        final String from = "ƒAƒ@ƒCƒBƒEƒDƒGƒFƒIƒHƒJƒKƒLƒMƒNƒOƒPƒQƒRƒSƒTƒUƒVƒWƒXƒYƒZƒ[ƒ\ƒ]ƒ^ƒ_ƒ`ƒaƒbƒcƒdƒeƒfƒgƒhƒiƒjƒkƒlƒmƒnƒoƒpƒqƒrƒsƒtƒuƒvƒwƒxƒyƒzƒ{ƒ|ƒ}ƒ~ƒ€ƒƒ‚ƒ„ƒƒƒ†ƒ…ƒˆƒ‡ƒ‰ƒŠƒ‹ƒŒƒƒƒƒƒ‘ƒ’ƒ“RS";
        final String to = "‚ ‚Ÿ‚¢‚¡‚¤‚£‚¦‚¥‚¨‚§‚©‚ª‚«‚¬‚­‚®‚¯‚°‚±‚²‚³‚´‚µ‚¶‚·‚¸‚¹‚º‚»‚¼‚½‚¾‚¿‚À‚Á‚Â‚Ã‚Ä‚Å‚Æ‚Ç‚È‚É‚Ê‚Ë‚Ì‚Í‚Î‚Ï‚Ğ‚Ñ‚Ò‚Ó‚Ô‚Õ‚Ö‚×‚Ø‚Ù‚Ú‚Û‚Ü‚İ‚Ş‚ß‚à‚â‚á‚ä‚ã‚æ‚å‚ç‚è‚é‚ê‚ë‚ì‚í‚î‚ï‚ğ‚ñTU";
        assertEquals(to, conv.convert(from));
    }
    public void testHiraganaFromKatakanaStringConverter() throws Exception {
        Converter conv = Converters.getHiraganaFromKatakanaStringConverter();
        final String from = "‚ ‚Ÿ‚¢‚¡‚¤‚£‚¦‚¥‚¨‚§‚©‚ª‚«‚¬‚­‚®‚¯‚°‚±‚²‚³‚´‚µ‚¶‚·‚¸‚¹‚º‚»‚¼‚½‚¾‚¿‚À‚Á‚Â‚Ã‚Ä‚Å‚Æ‚Ç‚È‚É‚Ê‚Ë‚Ì‚Í‚Î‚Ï‚Ğ‚Ñ‚Ò‚Ó‚Ô‚Õ‚Ö‚×‚Ø‚Ù‚Ú‚Û‚Ü‚İ‚Ş‚ß‚à‚â‚á‚ä‚ã‚æ‚å‚ç‚è‚é‚ê‚ë‚ì‚í‚î‚ï‚ğ‚ñTU";
        final String to = "ƒAƒ@ƒCƒBƒEƒDƒGƒFƒIƒHƒJƒKƒLƒMƒNƒOƒPƒQƒRƒSƒTƒUƒVƒWƒXƒYƒZƒ[ƒ\ƒ]ƒ^ƒ_ƒ`ƒaƒbƒcƒdƒeƒfƒgƒhƒiƒjƒkƒlƒmƒnƒoƒpƒqƒrƒsƒtƒuƒvƒwƒxƒyƒzƒ{ƒ|ƒ}ƒ~ƒ€ƒƒ‚ƒ„ƒƒƒ†ƒ…ƒˆƒ‡ƒ‰ƒŠƒ‹ƒŒƒƒƒƒƒ‘ƒ’ƒ“RS";
        assertEquals(to, conv.convert(from));
    }
    public void testHankakuFromZenkakuStringConverter() throws Exception {
        Converter conv = Converters.getHankakuFromZenkakuStringConverter();
        final String from = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			+ "¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏĞÑÒÓÔÕÖ×ØÙÚÛÜİŞß³Ş¶Ş·Ş¸Ş¹ŞºŞ»Ş¼Ş½Ş¾Ş¿ŞÀŞÁŞÂŞÃŞÄŞÊŞËŞÌŞÍŞÎŞÊßËßÌßÍßÎß"
			+"0123456789"
			+" !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

        final String to = "‚‚‚‚ƒ‚„‚…‚†‚‡‚ˆ‚‰‚Š‚‹‚Œ‚‚‚‚‚‘‚’‚“‚”‚•‚–‚—‚˜‚™‚š‚`‚a‚b‚c‚d‚e‚f‚g‚h‚i‚j‚k‚l‚m‚n‚o‚p‚q‚r‚s‚t‚u‚v‚w‚x‚y"
			+ "BuvAEƒ’ƒ@ƒBƒDƒFƒHƒƒƒ…ƒ‡ƒb[ƒAƒCƒEƒGƒIƒJƒLƒNƒPƒRƒTƒVƒXƒZƒ\ƒ^ƒ`ƒcƒeƒgƒiƒjƒkƒlƒmƒnƒqƒtƒwƒzƒ}ƒ~ƒ€ƒƒ‚ƒ„ƒ†ƒˆƒ‰ƒŠƒ‹ƒŒƒƒƒ“JKƒ”ƒKƒMƒOƒQƒSƒUƒWƒYƒ[ƒ]ƒ_ƒaƒdƒfƒhƒoƒrƒuƒxƒ{ƒpƒsƒvƒyƒ|"
			+ "‚O‚P‚Q‚R‚S‚T‚U‚V‚W‚X"
			+ "@Ih”“•fij–{C|D^FGƒ„H—mnOQeobp`";
        assertEquals(to, conv.convert(from));
    }
    public void testZenkakuFromHankakuStringConverter() throws Exception {
        Converter conv = Converters.getZenkakuFromHankakuStringConverter();
        final String from = "‚‚‚‚ƒ‚„‚…‚†‚‡‚ˆ‚‰‚Š‚‹‚Œ‚‚‚‚‚‘‚’‚“‚”‚•‚–‚—‚˜‚™‚š‚`‚a‚b‚c‚d‚e‚f‚g‚h‚i‚j‚k‚l‚m‚n‚o‚p‚q‚r‚s‚t‚u‚v‚w‚x‚y"
			+ "BuvAEƒ’ƒ@ƒBƒDƒFƒHƒƒƒ…ƒ‡ƒb[ƒAƒCƒEƒGƒIƒJƒLƒNƒPƒRƒTƒVƒXƒZƒ\ƒ^ƒ`ƒcƒeƒgƒiƒjƒkƒlƒmƒnƒqƒtƒwƒzƒ}ƒ~ƒ€ƒƒ‚ƒ„ƒ†ƒˆƒ‰ƒŠƒ‹ƒŒƒƒƒ“JKƒ”ƒKƒMƒOƒQƒSƒUƒWƒYƒ[ƒ]ƒ_ƒaƒdƒfƒhƒoƒrƒuƒxƒ{ƒpƒsƒvƒyƒ|"
			+ "‚O‚P‚Q‚R‚S‚T‚U‚V‚W‚X"
			+ "@Ih”“•fij–{C|D^FGƒ„H—mnOQeobp`";
        
        final String to = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			+ "¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏĞÑÒÓÔÕÖ×ØÙÚÛÜİŞß³Ş¶Ş·Ş¸Ş¹ŞºŞ»Ş¼Ş½Ş¾Ş¿ŞÀŞÁŞÂŞÃŞÄŞÊŞËŞÌŞÍŞÎŞÊßËßÌßÍßÎß"
			+"0123456789"
			+" !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

        assertEquals(to, conv.convert(from));
    }
    public void testNewCustomConverter1() throws Exception {

    	Converter conv = Converters.newCustomConverter();
    	
        final String from = "aA‚`±ƒA‚ 1‚P#”";
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

    	// ‘S‚Ä‚Ì•¶š‚ğ”¼Šp¨‘SŠp
		Converter convHankakuFromZenkaku = Converters.newCustomConverter(convsHankakuFromZenkaku);
    	// ‘S‚Ä‚Ì•¶š‚ğ‘SŠp¨”¼Šp
		Converter convZenkakuFromHankaku = Converters.newCustomConverter(convsZenkakuFromHankaku);
        
        final String from = "aA‚`±ƒA‚ 1‚P#”";
        final String toHankakuFromZenkaku = "‚‚`‚`ƒAƒA‚ ‚P‚P””";
        final String toZenkakuFromHankaku = "aAA±±‚ 11##";
        
        assertEquals(toHankakuFromZenkaku, convHankakuFromZenkaku.convert(from));
        assertEquals(toZenkakuFromHankaku, convZenkakuFromHankaku.convert(from));
    }

    public void testNewCustomStringConverter1() throws Exception {
    	char[] fromChar = {'a','A','‚`','±','ƒA','‚ ','1','‚P','#','”'};
    	char[] toChar = {'‚Q','ƒC','—','‚w', 'y', '#','¶','Z','‚´','9'}; 

    	Converter conv = Converters.newCustomStringConverter(ReversibleConverter.POSITIVE_CONVERT,fromChar,toChar);
        
        final String from = "±a1#‚`ƒA”A‚ ‚P";
        final String to = "‚w‚Q¶‚´—y9ƒC#Z";
        
        assertEquals(to, conv.convert(from));
    }
    public void testNewCustomStringConverter2() throws Exception {
    	String[] fromStr = {"<", "8‚X", "Üƒ’‚ñ", "oP‚p‚’"};
    	String[] toStr = {"0‚P", "‚w‚x‚y", "‚Q‚R", "45‚U78‚X"};

    	Converter conv = Converters.newCustomStringConverter(ReversibleConverter.POSITIVE_CONVERT,fromStr,toStr);
        
        final String from = "=Üƒ’‚ñ=Üƒ’=oP‚p‚’=P‚p‚’=8‚X=‚X=<<<";
        final String to = "=‚Q‚R=Üƒ’=45‚U78‚X=P‚p‚’=‚w‚x‚y=‚X=0‚P0‚P0‚P";
        
       assertEquals(to, conv.convert(from));
    }
    public void testNewCustomStringConverter3() throws Exception {
    	String[] fromStr = {"<", "8‚X", "Üƒ’‚ñ", "oP‚p‚’"};
    	String[] toStr = {"0‚P", "‚w‚x‚y", "‚Q‚R", "45‚U78‚X"};
    	char[] fromChar = {'a','A','‚`','±','ƒA','‚ ','1','‚P','#','”'};
    	char[] toChar = {'‚Q','ƒC','—','‚w', 'y', '#','¶','Z','‚´','9'}; 

    	Converter conv = Converters.newCustomStringConverter(ReversibleConverter.POSITIVE_CONVERT,fromStr,toStr,fromChar,toChar);
        
        final String from = "±a1#‚`ƒA”A‚ ‚P=Üƒ’‚ñ=Üƒ’=oP‚p‚’=P‚p‚’=8‚X=‚X=<<<";
        final String to = "‚w‚Q¶‚´—y9ƒC#Z=‚Q‚R=Üƒ’=45‚U78‚X=P‚p‚’=‚w‚x‚y=‚X=0Z0Z0Z";
        
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
        final char[] to = "‚‚‚‚ƒ‚„‚…‚†‚‡‚ˆ‚‰‚Š‚‹‚Œ‚‚‚‚‚‘‚’‚“‚”‚•‚–‚—‚˜‚™‚š‚`‚a‚b‚c‚d‚e‚f‚g‚h‚i‚j‚k‚l‚m‚n‚o‚p‚q‚r‚s‚t‚u‚v‚w‚x‚y".toCharArray();
        for(int i=0;i<from.length;i++){
            assertEquals(to[i], conv.convert(from[i]));
        }
    }
    public void testAlphabetZenkakuFromHankakuCharacterConverter() throws Exception {
    	CharacterConverter conv = Converters.getAlphabetZenkakuFromHankakuCharacterConverter();
    	final char[] from = "‚‚‚‚ƒ‚„‚…‚†‚‡‚ˆ‚‰‚Š‚‹‚Œ‚‚‚‚‚‘‚’‚“‚”‚•‚–‚—‚˜‚™‚š‚`‚a‚b‚c‚d‚e‚f‚g‚h‚i‚j‚k‚l‚m‚n‚o‚p‚q‚r‚s‚t‚u‚v‚w‚x‚y".toCharArray();
    	final char[] to = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        for(int i=0;i<from.length;i++){
            assertEquals(to[i], conv.convert(from[i]));
        }
    }
    
    public void testKatakanaHankakuFromZenkakuCharacterConverter() throws Exception {
    	CharacterConverter conv = Converters.getKatakanaHankakuFromZenkakuCharacterConverter();
    	final char[] from = "¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏĞÑÒÓÔÕÖ×ØÙÚÛÜİŞß".toCharArray(); 
    	final char[] to = "BuvAEƒ’ƒ@ƒBƒDƒFƒHƒƒƒ…ƒ‡ƒb[ƒAƒCƒEƒGƒIƒJƒLƒNƒPƒRƒTƒVƒXƒZƒ\ƒ^ƒ`ƒcƒeƒgƒiƒjƒkƒlƒmƒnƒqƒtƒwƒzƒ}ƒ~ƒ€ƒƒ‚ƒ„ƒ†ƒˆƒ‰ƒŠƒ‹ƒŒƒƒƒ“JK".toCharArray();
    	
    	for(int i=0;i<from.length;i++){
            assertEquals(to[i], conv.convert(from[i]));
        }
    }
    
    public void testKatakanaZenkakuFromHankakuCharacterConverter() throws Exception {
    	CharacterConverter conv = Converters.getKatakanaZenkakuFromHankakuCharacterConverter();
    	final char[] from = "BuvAEƒ’ƒ@ƒBƒDƒFƒHƒƒƒ…ƒ‡ƒb[ƒAƒCƒEƒGƒIƒJƒLƒNƒPƒRƒTƒVƒXƒZƒ\ƒ^ƒ`ƒcƒeƒgƒiƒjƒkƒlƒmƒnƒqƒtƒwƒzƒ}ƒ~ƒ€ƒƒ‚ƒ„ƒ†ƒˆƒ‰ƒŠƒ‹ƒŒƒƒƒ“JK".toCharArray();
    	final char[] to = "¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏĞÑÒÓÔÕÖ×ØÙÚÛÜİŞß".toCharArray(); 
        for(int i=0;i<from.length;i++){
            assertEquals(to[i], conv.convert(from[i]));
        }
    }
    public void testNumberHankakuFromZenkakuCharacterConverter() throws Exception {
    	CharacterConverter conv = Converters.getNumberHankakuFromZenkakuCharacterConverter();
    	final char[] from = "0123456789".toCharArray();
    	final char[] to = "‚O‚P‚Q‚R‚S‚T‚U‚V‚W‚X".toCharArray();
        for(int i=0;i<from.length;i++){
            assertEquals(to[i], conv.convert(from[i]));
        }
    }
    public void testNumberZenkakuFromHankakuCharacterConverter() throws Exception {
    	CharacterConverter conv = Converters.getNumberZenkakuFromHankakuCharacterConverter();
    	final char[] from = "‚O‚P‚Q‚R‚S‚T‚U‚V‚W‚X".toCharArray();
    	final char[] to = "0123456789".toCharArray();
        for(int i=0;i<from.length;i++){
            assertEquals(to[i], conv.convert(from[i]));
        }
    }
    public void testSymbolHankakuFromZenkakuCharacterConverter() throws Exception {
    	CharacterConverter conv = Converters.getSymbolHankakuFromZenkakuCharacterConverter();
    	final char[] from = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~".toCharArray();
    	final char[] to = "@Ih”“•fij–{C|D^FGƒ„H—mnOQeobp`".toCharArray();
        for(int i=0;i<from.length;i++){
            assertEquals(to[i], conv.convert(from[i]));
        }
    }
    public void testSymbolZenkakuFromHankakuCharacterConverter() throws Exception {
    	CharacterConverter conv = Converters.getSymbolZenkakuFromHankakuCharacterConverter();
    	final char[] from = "@Ih”“•fij–{C|D^FGƒ„H—mnOQeobp`".toCharArray();
    	final char[] to = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~".toCharArray();
        
        for(int i=0;i<from.length;i++){
            assertEquals(to[i], conv.convert(from[i]));
        }
    }
    public void testKatakanaFromHiraganaCharacterConverter() throws Exception {
    	CharacterConverter conv = Converters.getKatakanaFromHiraganaCharacterConverter();
    	final char[] from = "ƒAƒ@ƒCƒBƒEƒDƒGƒFƒIƒHƒJƒKƒLƒMƒNƒOƒPƒQƒRƒSƒTƒUƒVƒWƒXƒYƒZƒ[ƒ\ƒ]ƒ^ƒ_ƒ`ƒaƒbƒcƒdƒeƒfƒgƒhƒiƒjƒkƒlƒmƒnƒoƒpƒqƒrƒsƒtƒuƒvƒwƒxƒyƒzƒ{ƒ|ƒ}ƒ~ƒ€ƒƒ‚ƒ„ƒƒƒ†ƒ…ƒˆƒ‡ƒ‰ƒŠƒ‹ƒŒƒƒƒƒƒ‘ƒ’ƒ“RS".toCharArray();
    	final char[] to = "‚ ‚Ÿ‚¢‚¡‚¤‚£‚¦‚¥‚¨‚§‚©‚ª‚«‚¬‚­‚®‚¯‚°‚±‚²‚³‚´‚µ‚¶‚·‚¸‚¹‚º‚»‚¼‚½‚¾‚¿‚À‚Á‚Â‚Ã‚Ä‚Å‚Æ‚Ç‚È‚É‚Ê‚Ë‚Ì‚Í‚Î‚Ï‚Ğ‚Ñ‚Ò‚Ó‚Ô‚Õ‚Ö‚×‚Ø‚Ù‚Ú‚Û‚Ü‚İ‚Ş‚ß‚à‚â‚á‚ä‚ã‚æ‚å‚ç‚è‚é‚ê‚ë‚ì‚í‚î‚ï‚ğ‚ñTU".toCharArray();
        for(int i=0;i<from.length;i++){
            assertEquals(to[i], conv.convert(from[i]));
        }
    }
    public void testHiraganaFromKatakanaCharacterConverter() throws Exception {
    	CharacterConverter conv = Converters.getHiraganaFromKatakanaCharacterConverter();
    	final char[] from = "‚ ‚Ÿ‚¢‚¡‚¤‚£‚¦‚¥‚¨‚§‚©‚ª‚«‚¬‚­‚®‚¯‚°‚±‚²‚³‚´‚µ‚¶‚·‚¸‚¹‚º‚»‚¼‚½‚¾‚¿‚À‚Á‚Â‚Ã‚Ä‚Å‚Æ‚Ç‚È‚É‚Ê‚Ë‚Ì‚Í‚Î‚Ï‚Ğ‚Ñ‚Ò‚Ó‚Ô‚Õ‚Ö‚×‚Ø‚Ù‚Ú‚Û‚Ü‚İ‚Ş‚ß‚à‚â‚á‚ä‚ã‚æ‚å‚ç‚è‚é‚ê‚ë‚ì‚í‚î‚ï‚ğ‚ñTU".toCharArray();
    	final char[] to = "ƒAƒ@ƒCƒBƒEƒDƒGƒFƒIƒHƒJƒKƒLƒMƒNƒOƒPƒQƒRƒSƒTƒUƒVƒWƒXƒYƒZƒ[ƒ\ƒ]ƒ^ƒ_ƒ`ƒaƒbƒcƒdƒeƒfƒgƒhƒiƒjƒkƒlƒmƒnƒoƒpƒqƒrƒsƒtƒuƒvƒwƒxƒyƒzƒ{ƒ|ƒ}ƒ~ƒ€ƒƒ‚ƒ„ƒƒƒ†ƒ…ƒˆƒ‡ƒ‰ƒŠƒ‹ƒŒƒƒƒƒƒ‘ƒ’ƒ“RS".toCharArray();
        for(int i=0;i<from.length;i++){
            assertEquals(to[i], conv.convert(from[i]));
        }
    }
    public void testHankakuFromZenkakuCharacterConverter() throws Exception {
    	CharacterConverter conv = Converters.getHankakuFromZenkakuCharacterConverter();
    	final String fromStr = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			+ "¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏĞÑÒÓÔÕÖ×ØÙÚÛÜİŞß" 
			+"0123456789"
			+" !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    	final String toStr = "‚‚‚‚ƒ‚„‚…‚†‚‡‚ˆ‚‰‚Š‚‹‚Œ‚‚‚‚‚‘‚’‚“‚”‚•‚–‚—‚˜‚™‚š‚`‚a‚b‚c‚d‚e‚f‚g‚h‚i‚j‚k‚l‚m‚n‚o‚p‚q‚r‚s‚t‚u‚v‚w‚x‚y"
			+ "BuvAEƒ’ƒ@ƒBƒDƒFƒHƒƒƒ…ƒ‡ƒb[ƒAƒCƒEƒGƒIƒJƒLƒNƒPƒRƒTƒVƒXƒZƒ\ƒ^ƒ`ƒcƒeƒgƒiƒjƒkƒlƒmƒnƒqƒtƒwƒzƒ}ƒ~ƒ€ƒƒ‚ƒ„ƒ†ƒˆƒ‰ƒŠƒ‹ƒŒƒƒƒ“JK" 
			+ "‚O‚P‚Q‚R‚S‚T‚U‚V‚W‚X"
			+ "@Ih”“•fij–{C|D^FGƒ„H—mnOQeobp`";
    	final char[] from = fromStr.toCharArray();
    	final char[] to = toStr.toCharArray();

    	for(int i=0;i<from.length;i++){
            assertEquals(to[i], conv.convert(from[i]));
        }
    }
    public void testZenkakuFromHankakuCharacterConverter() throws Exception {
    	CharacterConverter conv = Converters.getZenkakuFromHankakuCharacterConverter();
    	final String fromStr = "‚‚‚‚ƒ‚„‚…‚†‚‡‚ˆ‚‰‚Š‚‹‚Œ‚‚‚‚‚‘‚’‚“‚”‚•‚–‚—‚˜‚™‚š‚`‚a‚b‚c‚d‚e‚f‚g‚h‚i‚j‚k‚l‚m‚n‚o‚p‚q‚r‚s‚t‚u‚v‚w‚x‚y"
			+ "BuvAEƒ’ƒ@ƒBƒDƒFƒHƒƒƒ…ƒ‡ƒb[ƒAƒCƒEƒGƒIƒJƒLƒNƒPƒRƒTƒVƒXƒZƒ\ƒ^ƒ`ƒcƒeƒgƒiƒjƒkƒlƒmƒnƒqƒtƒwƒzƒ}ƒ~ƒ€ƒƒ‚ƒ„ƒ†ƒˆƒ‰ƒŠƒ‹ƒŒƒƒƒ“JK" 
			+ "‚O‚P‚Q‚R‚S‚T‚U‚V‚W‚X"
			+ "@Ih”“•fij–{C|D^FGƒ„H—mnOQeobp`";
        
    	final String toStr = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			+ "¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏĞÑÒÓÔÕÖ×ØÙÚÛÜİŞß" 
			+"0123456789"
			+" !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    	final char[] from = fromStr.toCharArray();
    	final char[] to = toStr.toCharArray();
        for(int i=0;i<from.length;i++){
            assertEquals(to[i], conv.convert(from[i]));
        }
    }

}
