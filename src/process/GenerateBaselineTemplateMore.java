package process;


import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class GenerateBaselineTemplateMore {
	/*
	word
	smoothedWord
	prefix1
	prefix2
	prefix3
	prefix4
	suffix1
	suffix2
	suffix3
	suffix4
	hasNumber
	hasUpper
	hasHyphen
	label;
	*/
	
	//extract features as described by Ratnaparkhi
	public static void main(String[] args) throws FileNotFoundException {
		String templateFile = "/data/onco_pos/baseline.template.more";
		StringBuffer content = new StringBuffer();
		int featureIndex = 0;
		//smoothed word upto hasHyphen
		for(int i=1; i<=12; i++) {
			//skip upper
			if(i==11) continue;
			content.append(String.format("U%d:%%x[0,%d]\n", featureIndex, i));
			featureIndex++;
		}
		//context words
		for(int i=-2; i<=2; i++) {
			if(i != 0) { //already included above
				content.append(String.format("U%d:%%x[%d,1]\n", featureIndex, i));
				featureIndex++;
			}
		}
		//bigram
		int bigramFeatureIndex = 0;
		content.append(String.format("B%d\n", bigramFeatureIndex));
		bigramFeatureIndex++;
		
		
		
		//my: bigram + prevWord + nextWord
		content.append(String.format("B%d:%%x[-1,1]\n", bigramFeatureIndex));
		bigramFeatureIndex++;
		
		//bigram+nextWord
		content.append(String.format("B%d:%%x[1,1]\n", bigramFeatureIndex));
		bigramFeatureIndex++;
		
		//bigram+twoSuffix
		content.append(String.format("B%d:%%x[0,7]\n", bigramFeatureIndex));
		bigramFeatureIndex++;
		
		
		System.out.println(content.toString());
		PrintWriter pw = new PrintWriter(templateFile);
		pw.println(content.toString());
		pw.close();
		
	}
}
