package process;


import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class GenerateBaselineTemplateOrthographic {
	/*
	word
	smoothedWord
	prefix
	suffix
	caps
	alphanum
	label;
	*/
	
	//extract features as described by Ratnaparkhi
	public static void main(String[] args) throws FileNotFoundException {
		String templateFile = "/data/onco_pos/new/baseline.template.orthographic";
		StringBuffer content = new StringBuffer();
		int featureIndex = 0;
		for(int i=1; i<=5; i++) {
			if(i==2) continue; //don't include prefix
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
		
		System.out.println(content.toString());
		PrintWriter pw = new PrintWriter(templateFile);
		pw.println(content.toString());
		pw.close();
		
	}
}
