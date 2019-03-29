package com.rapidminer.extension.operator;

public enum PennTag {
	S("S"),
	NP("NP"), PP("PP"), VP("VP"), ADVP("ADVP"), ADJP("ADJP"), SBAR("SBAR"), PRT("PRT"), INTJ("INTJ"), PNP("PNP"), //Chunck Tags
	CC("CC"), CD("CD"), DT("DT"), EX("EX"), FW("FW"), IN("IN"), JJ("JJ"), JJR("JJR"), JJS("JJS"), LS("LS"), 
	MD("MD"), NN("NN"), NNS("NNS"), NNP("NNP"), NNPS("NNPS"), PDT("PDT"), POS("POS"), PRP("PRP"), PRP$("PRP$"), RB("RB"), 
	RBR("RBR"), RBS("RBS"), RP("RP"), SYM("SYM"), TO("TO"), UH("UH"), VB("VB"), VBZ("VBZ"), VBP("VBP"), VBD("VBD"), 
	VBN("VBN"), VBG("VBG"), WDT("WDT"), DP("DP"), DP$("DP$"), WRB("WRB"), //POS Tags
	A1("A1"), P1("P1"), //Anchor Tags
	Punkt("."), Komma(","), Semicolon(";"), Colon(":"), Dollar("$"), OpeningMark("``"), ClosingMark("''"),  //Satzzeichen
	None("")
	;
	private final String text;
	
	PennTag(String text) {
		this.text = text;
	}

	public boolean compareText(String s) {return text.equals(s);}
	
	public static PennTag stringToPennTag(String s) {
		PennTag[] pennTags = PennTag.values();
		for( int i = 0; i < pennTags.length; i++) {
			if(pennTags[i].compareText(s)) {
				return pennTags[i];
			}
		}
		return PennTag.None;
	}
}
