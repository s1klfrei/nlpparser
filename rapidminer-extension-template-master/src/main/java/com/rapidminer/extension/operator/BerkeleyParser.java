package com.rapidminer.extension.operator;





import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.AccessControlException;
import java.security.Permissions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.nio.file.SimpleFileObject;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.text.Document;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.security.PluginSandboxPolicy;
import com.rapidminer.tools.LogService;

import edu.berkeley.nlp.PCFGLA.Binarization;
import edu.berkeley.nlp.PCFGLA.CoarseToFineMaxRuleParser;
import edu.berkeley.nlp.PCFGLA.CoarseToFineMaxRuleProductParser;
import edu.berkeley.nlp.PCFGLA.CoarseToFineNBestParser;
import edu.berkeley.nlp.PCFGLA.Corpus;
import edu.berkeley.nlp.PCFGLA.Grammar;
import edu.berkeley.nlp.PCFGLA.Lexicon;
import edu.berkeley.nlp.PCFGLA.MultiThreadedParserWrapper;
import edu.berkeley.nlp.PCFGLA.ParserData;
import edu.berkeley.nlp.PCFGLA.TreeAnnotations;
import edu.berkeley.nlp.PCFGLA.BerkeleyParser.Options;
import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.tokenizer.PTBLineLexer;
import edu.berkeley.nlp.util.Numberer;

public class BerkeleyParser extends Operator{
	
	private InputPort ioobjectInputGrammar = getInputPorts().createPort("grammar", IOObject.class);
	private InputPort ioobjectInputText = getInputPorts().createPort("text", IOObject.class);
	private OutputPort ioobjectOutput = getOutputPorts().createPort("output");
	
	public BerkeleyParser(OperatorDescription description) {
		super(description);
	}
	
	@Override
	public void doWork() throws OperatorException{
		/*
		String text = getParameterAsString(PARAMETER_TEXT);
		
		Runtime rt = Runtime.getRuntime();
		try {
			//FilePermission permission = new FilePermission("C:\\Users\\Klaus\\Documents\\Uni\\BachelorArbeit\\Parser\\berkeleyparser-master\\BerkeleyParser-1.7.jar", "execute");

			Process pr = rt.exec("java -jar C:\\Users\\Klaus\\Documents\\Uni\\BachelorArbeit\\Parser\\berkeleyparser-master\\BerkeleyParser-1.7.jar "
					+ "-gr C:\\Users\\Klaus\\Documents\\Uni\\BachelorArbeit\\Parser\\berkeleyparser-master\\eng_sm6.gr "
					+ "-inputFile C:\\Users\\Klaus\\Documents\\Uni\\BachelorArbeit\\Parser\\berkeleyparser-master\\inputFile.txt "
					+ "-outputFile C:\\Users\\Klaus\\Documents\\Uni\\BachelorArbeit\\Parser\\berkeleyparser-master\\outputFile.txt");
			
			try {
				pr.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		} */
		
		SimpleFileObject grammarObject = (SimpleFileObject) ioobjectInputGrammar.getData(IOObject.class);
		File grammarFile = grammarObject.getFile();
		String grammarPath = grammarFile.getAbsolutePath();
		
		//LogService.getRoot().log(Level.INFO, grammarPath);

		
		Document iooDoc =(Document) ioobjectInputText.getData(IOObject.class);
	
		
		String text = iooDoc.getTokenText();
		String[] sentences = text.split("\\r?\\n");
		
		String outputText = "";

		
		////////////////////////////////////////////////////////////////////////
		
		
		Options opts = new Options();
		
		//opts.grFileName = "C:\\Users\\Klaus\\Documents\\Uni\\BachelorArbeit\\Parser\\berkeleyparser-master\\eng_sm6.gr";
		opts.grFileName = grammarPath;
		opts.viterbi = false;
		opts.binarize = false;
		opts.scores = false;
		opts.keepFunctionLabels = false;
		opts.substates = false;
		opts.accurate = false;
		opts.modelScore = false;
		opts.confidence = false;
		opts.sentence_likelihood = false;
		opts.tree_likelihood = false;
		opts.variational = false;
		opts.render = false;
		opts.chinese = false;
		//opts.inputFile = "C:\\Users\\Klaus\\Documents\\Uni\\BachelorArbeit\\Parser\\berkeleyparser-master\\inputFile.txt";
		opts.maxLength = 200;
		opts.nThreads = 1;
		opts.kbest = 1;
		//opts.outputFile = "C:\\Users\\Klaus\\Documents\\Uni\\BachelorArbeit\\Parser\\berkeleyparser-master\\outputFile.txt";
		opts.goldPOS = false;
		opts.dumpPosteriors = false;
		opts.ec_format = false;
		opts.nGrammars = 1;
	
		double threshold = 1.0;
	
		if (opts.chinese)
			Corpus.myTreebank = Corpus.TreeBankType.CHINESE;
	
		CoarseToFineMaxRuleParser parser = null;
		if (opts.nGrammars != 1) {
			Grammar[] grammars = new Grammar[opts.nGrammars];
			Lexicon[] lexicons = new Lexicon[opts.nGrammars];
			Binarization bin = null;
			for (int nGr = 0; nGr < opts.nGrammars; nGr++) {
				String inFileName = opts.grFileName + "." + nGr;
				ParserData pData = ParserData.Load(inFileName);
				if (pData == null) {
					System.out.println("Failed to load grammar from file"
							+ inFileName + ".");
					System.exit(1);
				}
				grammars[nGr] = pData.getGrammar();
				lexicons[nGr] = pData.getLexicon();
				Numberer.setNumberers(pData.getNumbs());
				bin = pData.getBinarization();
			}
			parser = new CoarseToFineMaxRuleProductParser(grammars, lexicons,
					threshold, -1, opts.viterbi, opts.substates, opts.scores,
					opts.accurate, opts.variational, true, true);
			parser.binarization = bin;
		} else {
			String inFileName = opts.grFileName;
			ParserData pData = ParserData.Load(inFileName);
			if (pData == null) {
				System.out.println("Failed to load grammar from file"
						+ inFileName + ".");
				System.exit(1);
			}
			Grammar grammar = pData.getGrammar();
			Lexicon lexicon = pData.getLexicon();
			Numberer.setNumberers(pData.getNumbs());
			if (opts.kbest == 1)
				parser = new CoarseToFineMaxRuleParser(grammar, lexicon,
						threshold, -1, opts.viterbi, opts.substates,
						opts.scores, opts.accurate, opts.variational, true,
						true);
			else
				parser = new CoarseToFineNBestParser(grammar, lexicon,
						opts.kbest, threshold, -1, opts.viterbi,
						opts.substates, opts.scores, opts.accurate,
						opts.variational, false, true);
			parser.binarization = pData.getBinarization();
		}
	
		MultiThreadedParserWrapper m_parser = null;
		if (opts.nThreads > 1) {
			System.err.println("Parsing with " + opts.nThreads
					+ " threads in parallel.");
			m_parser = new MultiThreadedParserWrapper(parser, opts.nThreads);
		}
		try {
			PTBLineLexer tokenizer = null;
			if (opts.tokenize)
				tokenizer = new PTBLineLexer();
			
			String line = "";
			String sentenceID = "";
			
			for(int i = 0; i < sentences.length; i++) {
				line = sentences[i].trim();
				
				if (opts.ec_format && line.equals(""))
					continue;
				List<String> sentence = null;
				List<String> posTags = null;
				if (opts.goldPOS) {
					sentence = new ArrayList<String>();
					posTags = new ArrayList<String>();
					List<String> tmp = Arrays.asList(line.split("\t"));
					if (tmp.size() == 0)
						continue;
					// System.out.println(line+tmp);
					sentence.add(tmp.get(0));
					String[] tags = tmp.get(1).split("-");
					posTags.add(tags[0]);
					while (!(line.equals(""))) {
						tmp = Arrays.asList(line.split("\t"));
						if (tmp.size() == 0)
							break;
						// System.out.println(line+tmp);
						sentence.add(tmp.get(0));
						tags = tmp.get(1).split("-");
						posTags.add(tags[0]);
					}
				} else {
					if (opts.ec_format) {
						int breakIndex = line.indexOf(">");
						sentenceID = line.substring(3, breakIndex - 1);
						line = line
								.substring(breakIndex + 2, line.length() - 5);
					}
					if (!opts.tokenize)
						sentence = Arrays.asList(line.split("\\s+"));
					else {
						sentence = tokenizer.tokenizeLine(line);
					}
				}
	
				// if (sentence.size()==0) { outputData.write("\n"); continue;
				// }//break;
				/*if (sentence.size() > opts.maxLength) {
					outputData.write("(())\n");
					if (opts.kbest > 1) {
						outputData.write("\n");
					}
					System.err.println("Skipping sentence with "
							+ sentence.size() + " words since it is too long.");
					continue;
				}*/
	
				if (opts.nThreads > 1) {
					m_parser.parseThisSentence(sentence);
					while (m_parser.hasNext()) {
						List<Tree<String>> parsedTrees = m_parser.getNext();
						outputText = outputTrees(parsedTrees, outputText, parser, opts, "",
								sentenceID);
					}
				} else {
					List<Tree<String>> parsedTrees = null;
					if (opts.kbest > 1) {
						parsedTrees = parser.getKBestConstrainedParses(
								sentence, posTags, opts.kbest);
						if (parsedTrees.size() == 0) {
							parsedTrees.add(new Tree<String>("ROOT"));
						}
					} else {
						parsedTrees = new ArrayList<Tree<String>>();
						Tree<String> parsedTree = parser
								.getBestConstrainedParse(sentence, posTags,
										null);
						if (opts.goldPOS && parsedTree.getChildren().isEmpty()) { // parse
																					// error
																					// when
																					// using
																					// goldPOS,
																					// try
																					// without
							parsedTree = parser.getBestConstrainedParse(
									sentence, null, null);
						}
						parsedTrees.add(parsedTree);
	
					}
					outputText = outputTrees(parsedTrees, outputText, parser, opts, line,
							sentenceID);
				}
			}
			if (opts.nThreads > 1) {
				while (!m_parser.isDone()) {
					while (m_parser.hasNext()) {
						List<Tree<String>> parsedTrees = m_parser.getNext();
						outputText = outputTrees(parsedTrees, outputText, parser, opts,
								line, sentenceID);
					}
				}
			}
			if (opts.dumpPosteriors) {
				String fileName = opts.grFileName + ".posteriors";
				parser.dumpPosteriors(fileName, -1);
			}
			// outputData.flush();
			// outputData.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// System.exit(0);
		
		////////////////////////////////////////////////////////////////////////
		
		Document outputDoc = new Document(outputText);
		ioobjectOutput.deliver((IOObject)outputDoc);
	}
	
	/**
	 * @param parsedTree
	 * @param outputData
	 * @param opts
	 */
	private static String outputTrees(List<Tree<String>> parseTrees,
			String outputText, CoarseToFineMaxRuleParser parser,
			edu.berkeley.nlp.PCFGLA.BerkeleyParser.Options opts, String line,
			String sentenceID) {
		String delimiter = "\t";
		if (opts.ec_format) {
			List<Tree<String>> newList = new ArrayList<Tree<String>>(
					parseTrees.size());
			for (Tree<String> parsedTree : parseTrees) {
				if (parsedTree.getChildren().isEmpty())
					continue;
				if (parser.getLogLikelihood(parsedTree) != Double.NEGATIVE_INFINITY) {
					newList.add(parsedTree);
				}
			}
			parseTrees = newList;
		}
		if (opts.ec_format) {
			outputText += parseTrees.size() + "\t" + sentenceID + "\n";
			delimiter = ",\t";
		}
	
		for (Tree<String> parsedTree : parseTrees) {
			boolean addDelimiter = false;
			if (opts.tree_likelihood) {
				double treeLL = (parsedTree.getChildren().isEmpty()) ? Double.NEGATIVE_INFINITY
						: parser.getLogLikelihood(parsedTree);
				if (treeLL == Double.NEGATIVE_INFINITY)
					continue;
				outputText += treeLL + "";
				addDelimiter = true;
			}
			if (opts.sentence_likelihood) {
				double allLL = (parsedTree.getChildren().isEmpty()) ? Double.NEGATIVE_INFINITY
						: parser.getLogLikelihood();
				if (addDelimiter)
					outputText += delimiter;
				addDelimiter = true;
				if (opts.ec_format)
					outputText += "sentenceLikelihood ";
				outputText += allLL + "";
			}
			if (!opts.binarize)
				parsedTree = TreeAnnotations.unAnnotateTree(parsedTree,
						opts.keepFunctionLabels);
			if (opts.confidence) {
				double treeLL = (parsedTree.getChildren().isEmpty()) ? Double.NEGATIVE_INFINITY
						: parser.getConfidence(parsedTree);
				if (addDelimiter)
					outputText += delimiter;
				addDelimiter = true;
				if (opts.ec_format)
					outputText += "confidence ";
				outputText += treeLL + "";
			} else if (opts.modelScore) {
				double score = (parsedTree.getChildren().isEmpty()) ? Double.NEGATIVE_INFINITY
						: parser.getModelScore(parsedTree);
				if (addDelimiter)
					outputText += delimiter;
				addDelimiter = true;
				if (opts.ec_format)
					outputText += "maxRuleScore ";
				outputText += String.format("%.8f", score);
			}
			if (opts.ec_format)
				outputText += "\n";
			else if (addDelimiter)
				outputText += delimiter;
			if (!parsedTree.getChildren().isEmpty()) {
				String treeString = parsedTree.getChildren().get(0).toString();
				if (parsedTree.getChildren().size() != 1) {
					System.err.println("ROOT has more than one child!");
					parsedTree.setLabel("");
					treeString = parsedTree.toString();
				}
				if (opts.ec_format)
					outputText +="(S1 " + treeString + " )\n";
				else
					outputText += "( " + treeString + " )\n";
			} else {
				outputText += "(())\n";
			}
			//if (opts.render)  entfernt, da nicht gerendert wird
		}
		if (opts.dumpPosteriors) {
			int blockSize = 50;
			String fileName = opts.grFileName + ".posteriors";
			parser.dumpPosteriors(fileName, blockSize);
		}
	
		if (opts.kbest > 1)
			outputText += "\n";
		//outputData.flush();
		
		return outputText;
	}
}