package com.rapidminer.extension.operator;


public class ParseTreeNode {
	public PennTag typ;
	public int start;
	public int ende;
		
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ParseTreeNode) {
			ParseTreeNode p2 = (ParseTreeNode) obj;
			return (this.typ == p2.typ && this.start == p2.start && this.ende == p2.ende); 
		}
		else {
			return false;
		}
		
	}
	
	@Override
	public String toString() {
		return typ + "-(" + start + ":" + ende + ")";
	}
	
}
