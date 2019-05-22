package com.rapidminer.extension.operator;

public enum PennTag {
	// Syntactic Tags
	S("S"), SBAR("SBAR"), SBARQ("SBARQ"), SINV("SINV"), SQ("SQ"), 
	ADJP("ADJP"), ADVP("ADVP"), CONJP("CONJP"), FRAG("FRAG"), INTJ("INTJ"),
	LST("LST"), NAC("NAC"), NP("NP"), NX("NX"), PP("PP"), PRN("PRN"), PRT("PRT"), 
	QP("QP"), RRC("RRC"), UCP("UCP"), VP("VP"), WHADJP("WHADJP"), WHADVP("WHADVP"), 
	WHNP("WHNP"), WHPP("WHPP"), X("X"), 
	// POS Tags
	CC("CC"), CD("CD"), DT("DT"), EX("EX"), FW("FW"), IN("IN"), JJ("JJ"), JJR("JJR"), JJS("JJS"), LS("LS"), 
	MD("MD"), NN("NN"), NNS("NNS"), NNP("NNP"), NNPS("NNPS"), PDT("PDT"), POS("POS"), PRP("PRP"), PRP$("PRP$"), RB("RB"), 
	RBR("RBR"), RBS("RBS"), RP("RP"), SYM("SYM"), TO("TO"), UH("UH"), VB("VB"), VBZ("VBZ"), VBP("VBP"), VBD("VBD"), 
	VBN("VBN"), VBG("VBG"), WDT("WDT"), WP("WP"), WP$("WP$"), WRB("WRB"),
	// Satzzeichen
	Punkt("."), Komma(","), Semicolon(";"), Colon(":"), Dollar("$"), OpeningMark("``"), ClosingMark("''"), 
	OpeningBracket("("), ClosingBracket(")"), 
	// Default Tag
	Empty("")
	;
	// String Repräsentation des Tags
	private final String text;
	
	PennTag(String text) {
		this.text = text;
	}
	
	/**
	 * Vergleicht ob s dem eigenen Text entspricht
	 * @param s	Text der verglichen wird
	 * @return	True, falls eigenem Text entspricht, false sonst
	 */
	public boolean compareText(String s) {return text.equals(s);}
	
	/**
	 * Gibt für s das passende Tag zurück
	 * @param s	Text zu dem man Tag sucht
	 * @param remove Suffix	Entfernt Suffixe von Nichtterminalen, "NP-TMP" liefert NP zurück statt Empty
	 * @return	entsprechendes PennTag oder Default Tag 'Empty' falls keines gefunden wurde
	 */
	public static PennTag stringToPennTag(String s, boolean removeSuffix) {
		if(removeSuffix) {
			// Falls Option removeSuffix gewählt werden alle Texte bis zum ersten '-' gekürzt
			// Somit wird aus statt nach NP-TMP nach NP gesucht
			
			int index = s.indexOf('-');
			
			if(index > 0) {
				s = s.substring(0, index);
			}
		}
		
		// Hole alle Tags
		PennTag[] pennTags = PennTag.values();
		
		// Durchlaufe alle Tags und vergleiche Text, gebe passendes zurück
		for( int i = 0; i < pennTags.length; i++) {
			if(pennTags[i].compareText(s)) {
				return pennTags[i];
			}
		}
		// Gebe Default zurück falls bis hier keines dem String entsprach
		return PennTag.Empty;
	}
}
