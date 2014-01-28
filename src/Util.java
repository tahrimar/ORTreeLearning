import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;


public class Util implements Comparator<Integer>{

	static void halt(){
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void halt(String msg){
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			System.out.println(msg);
			br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void waitForInput(){
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			
			System.out.println("\nPress return to continue...\n");
			br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Given a file, this method reads data samples from the file and returns
	 * a newly created dataset
	 * @param fileName
	 * @return
	 */
	static Dataset readDataSet(String fileName){
		
		int V, N;
		String[] domains;
		String[] sample = null;		
		BufferedReader br;
		ArrayList<Variable> variables = null;
		Dataset ds = null;
		
		try {
	
			ds = new Dataset();
			
			br = new BufferedReader(new FileReader(fileName));
			
			System.out.println("\nReading data from file "+fileName+"...");
		
			V = Integer.parseInt(br.readLine());
			
			System.out.print("Number of variables : "+V);
			
			//domains = br.readLine().split("\\s+");
			
			variables = new ArrayList<Variable>();
						
			for(int i=0; i<V; i++){
				
				Variable v = new Variable();
				v.setLabel(i);
				v.setValue(-1);
				//v.setDomain(Integer.parseInt(domains[i]));
				v.setDomain(2);
				variables.add(v);
				
			}
			
			ds.setVariables(variables);
			
			N = Integer.parseInt(br.readLine());
			
			System.out.println("\tNumber of samples : "+N);
						
			for(int i=0; i<N; i++){
				
				//String[] line = br.readLine().split("\\[|,|\\]|\\s+");
				String line = br.readLine().replaceAll("\\[|\\]|\\s+", "");
				
				sample = line.split(",");				
				
				int[] sample_i = new int[ds.scopeSize()];
				
				for(int j=0; j<sample_i.length; j++){
					
					sample_i[j] = Integer.parseInt(sample[j].trim());
				}	
				
				ds.addDataInstance(sample_i);
				ds.addDataIndex(i);
				
				//System.out.println("i:"+i);
			}
			
			br.close();
			System.out.println("End reading file "+fileName);
			
		}//end of try 
		
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return ds;
	}
	
	/**
	 * Given a tree model and a dataset (preferable a test set), this method computes
	 * the log likelyhood of the dataset according to the model
	 * @param tree
	 * @param ds
	 * @return
	 */
	static double computeLL(ORTree tree, Dataset ds){
		
		double logLikelyhood = 0.0;
		double probOfevidence = 0.0;
		
		ArrayList<Variable> evidVars = ds.getVariables();
		
		for(int i=0; i<ds.size(); i++){
			
			int[] evidence = ds.getDataInstance(i);
			
			tree.setEvidence(evidVars, evidence);
			
			probOfevidence = tree.probOfEvidence(tree.getRoot());
			
			//System.out.println("Probability of evidence "+Arrays.toString(evidence)+" : "+probOfevidence);
			
			if(probOfevidence == 0){
				
				Util.halt("Probability of sample is 0.0");
				continue;
				
			}	
			
			logLikelyhood = logLikelyhood + Math.log10(probOfevidence)/Math.log10(2);
		}		
		
		return logLikelyhood;
	}
	
	
	static String getNextLine(BufferedReader in) throws IOException{

		String line = "";
	
		do{
			line = in.readLine();//read lines until the first non-empty line is read		
		}while(line.isEmpty());
		
		return line;
	}	
	
	/**
	 * given a uai file, this method reads the file and returns a graphical model
	 * @param inputFileName
	 * @return
	 */
	public static GraphicalModel readUAI(String inputFileName){
		
		BufferedReader in = null; 
		String line = null;
		String[] word = null;
		
		ArrayList<Variable> parents = null;

		GraphicalModel gm = null;
		
		try{
			
			gm = new GraphicalModel();
			in = new BufferedReader(new FileReader(inputFileName));
			line = getNextLine(in);
			
			if(line.equalsIgnoreCase("BAYES")){
				
				System.out.print("Reading a Bayesian Network");
				gm = new BayesianNetwork();
				
			}
			else if (line.equalsIgnoreCase("MARKOV")){
				System.out.print("A Markov Network");
				
			}
			else
				System.out.print("Other Network");
			
			gm.setType(line);
			gm.createEmptyGraph();
			
			line = getNextLine(in);
	    	
			//Line 2: read the number of variables in the network
	    	
			int numOfVariables = Integer.parseInt(line);
	    	System.out.println(" over "+numOfVariables+" variables");
	    	
	    	
	    	//Line 3:read the line specifying the domain sizes of the variables
	    	String[] domains = getNextLine(in).split("\\s+");
	    			
	    	
	    	for(int i=0; i<numOfVariables; i++){
	    		    				
	    		Variable v = new Variable();
	    		v.setLabel(i);
	    		v.setDomain(Integer.parseInt(domains[i]));
	    		v.initializePotentials();
	    		gm.addVariable(v);	    		
	    		gm.getGraph().addVertex(v.getLabel());
	    		gm.addParents(i, new ArrayList<Variable>());
	    		
	    	}	
	    	
	    	System.out.println("Variables : "+Variable.getLabels(gm.getVariables()));	
	    	//Line 4: read the number of potentials in the network
	    	
	    	int numberOfPotentials = Integer.parseInt(getNextLine(in));
	    	System.out.println("Number of potentials in the network "+numberOfPotentials);
	    	
	    	
	    	//Line 5: read each potential information: scope size and the variable ids
	    	for(int i=0; i<numberOfPotentials; i++){
	    			
	    		
	    		line = getNextLine(in);
	    			
	    		word = line.trim().split("\\s+");
	    		Potential p = new Potential();
	    		int scopeSize = Integer.parseInt(word[0]);
	    		ArrayList<Integer> scopeIDs = new ArrayList<Integer>();
	    			    		
	    	 		
	    		for(int j=1; j<=scopeSize; j++)
	    			scopeIDs.add(Integer.parseInt(word[j])); 			    		
	    		
	    		int child = -1;
	    		
	    		if(gm.isBayes()){
	    			
	    			child = scopeIDs.get(scopeSize-1);
	    		}
	    		
	      		for(int j=0; j<scopeSize; j++){
	    			
	      			Variable v = gm.getVariable(scopeIDs.get(j));
	    			p.addScope(v);
	    			v.addPotential(p);
	    			
	    			if(gm.isBayes() && j<scopeSize-1){
	    				
	    				gm.addParent(child, v);
	    			}
	    		}
	    		
	      		
	    		gm.addPotential(p);
	    		//if it is a markov network then construct among clique of the scope variables
	    		if(gm.isMarkov()){
	    			
	    			gm.getGraph().formClique(Variable.getLabels(p.getScope()));
	    		}
	    		
	    	}	
	    			
	    	System.out.println(numberOfPotentials);
	    	//Line 5+numOfPotentials: add parameters to the potentials
	    	for(int i=0; i<numberOfPotentials; i++){
	    				
	    		
	    		line = getNextLine(in);
	    		word = line.trim().split("\\s+");
	    		
	    		int numberOfParameters = Integer.parseInt(line);
	    		
	    		Potential p = gm.getPotential(i);
	    		p.initializeParameters(numberOfParameters);
	    		
	    			    			
	    		for(int j=0; j<numberOfParameters; ){
	    	
	    			
	    			line = getNextLine(in);
	    			//System.out.println(""+line);
	    			word = line.trim().split("\\s+");
	    				
	    			for(int k=0; k<word.length; k++, j++){
	    				
	    				double param = Double.parseDouble(word[k]);
	    				p.addParameter(param, j);
	    			}    			
	    			
	    		}
	    		
	    	}
	    	
	    	
	    	System.out.println("Finished reading input file "+inputFileName);
			
	    	in.close();
		
	    }//end of try
		catch(Exception e){
			e.printStackTrace();
		}
		return gm;
	
	}//end of reader


	static ArrayList<String[]> readUAIEvidence(String fileName, GraphicalModel gm){
		
		ArrayList<String[]> evidence = new ArrayList<String[]>();
		
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			
			String line = Util.getNextLine(br);
			
			int numberOfEvidenceSamples = Integer.parseInt(line);
			
			System.out.println("Number of evidence samples : "+numberOfEvidenceSamples);
			
			for(int i=0; i<numberOfEvidenceSamples; i++){
				
				line = Util.getNextLine(br);
				
				String[] words = line.split("\\s+");
				
				ArrayList<Variable> evidence_i = new ArrayList<Variable>();
				
				int numberOfEvidVars = Integer.parseInt(words[0]);
				
				System.out.println("Number of evidence variables : "+numberOfEvidVars);
				
				//read the evidence string
				line = Util.getNextLine(br);
				
				words = line.split("\\s+");				
				
				/*for(int j=0; j<numberOfEvidVars*2; j=j+2){
					
					Variable v = gm.getVariableById(Integer.parseInt(words[j]));
					
					v.setValue(Integer.parseInt(words[j+1]));
					
					evidence_i.add(v);
					
					System.out.println("Evidence : "+evidence_i.get(j).getID()+" value = "+evidence_i.get(j).getValue());
				}*/				
				
				//evidence.add(evidence_i);
				evidence.add(words);
				
			}
			
			br.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return evidence;
		
	}

	@Override
	public int compare(Integer arg0, Integer arg1) {
		// TODO Auto-generated method stub
		return arg0-arg1;
	}
	
	static ArrayList<Integer> sortIntegerList(ArrayList<Integer> list){
		
		Collections.sort(list);
		return list;
	}
	
	
	/**
	 * converts an array of integers to an integer value
	 * @param string
	 * @return
	 */
	static int toInteger(int[] string){
		
		int mappedValue = 0;
		
		for(int i=0; i<string.length; i++){
			
			mappedValue =  mappedValue + ((int)Math.pow(2, i) * string[string.length-1-i]);
		}
		
		return mappedValue;
		
	}
	
	
	static int[] toIntArray(String str){
		
		int[] array = new int[str.length()];
		
		for(int i=0; i<array.length; i++){
			
			if(str.charAt(i) == '1')
				array[i] = 1;
			
			else
				array[i] = 0;
		
		}
				
		return array;
	}
	
	static int[] toInteger_(String s){
		
		int[] sInt = new int[s.length()];
		
		System.out.println("String length : "+s.length());
		
		for(int i=0; i<s.length(); i++){
			
			if(s.charAt(i) == '1')
				sInt[i] = 1;
			
			else
				sInt[i] = 0;
		}
		
		System.out.println("Length : "+s.length()+" Converted Integer "+Arrays.toString(sInt));
		
		return sInt;
	}
	
	static int[] or(int[] n1, int[] n2){
		
		int[] n1Orn2 = new int[n1.length];
		
		for(int i=0; i<n1.length; i++){
			
			n1Orn2[i] = n1[i] | n2[i];
		}
	
		return n1Orn2;
	}
	
	
	
	/**
	 * converts a whole number to an array of integers
	 * @param i
	 * @return
	 */
	static int[] toArray(int N, int length){
		
		int[] a = new int[length];
		
		ArrayList<Integer> tempAssignment = new ArrayList<Integer>();
		
		for(int i = 0; i<length; i++){
			tempAssignment.add(N %  2);
			N = N / 2;
		}
		
		for(int i = tempAssignment.size()-1, j=0; i>=0; i--)
			a[j++] = tempAssignment.get(i);
		
		return a;
	}
	
	
	static long hamdist(long l, long value)
	{
	  long dist = 0, val = l ^ value; // XOR
	 
	  // Count the number of set bits
	  while(val != 0)
	  {
	    ++dist; 
	    val &= val - 1;
	  }
	 
	  return dist;
	}
	
	static int hamdist(String s1, String s2){
		
		
		int dist = 0;
		
		if(s1.length() != s2.length()){
			
			System.out.println(s1);
			System.out.println(s2);
			Util.halt();
		}
		
		for(int i=0; i<s1.length(); i++){
			
			if(s1.charAt(i) != s2.charAt(i))
				dist++;
		}
		
		return dist;
	}
	
	static double euclideanDist(String s1, String s2){
		
		
		int dist = 0;
		
		if(s1.length() != s2.length()){
			
			System.out.println(s1);
			System.out.println(s2);
			Util.halt();
		}
		
		for(int i=0; i<s1.length(); i++){
			
			if(s1.charAt(i) != s2.charAt(i))
				dist++;
		}
		
		return Math.sqrt(dist);
	}
	
	
}
