import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;


public class GraphicalModel {


	String type;	//Bayes or Markov
	
	private ArrayList<Variable> variables;
	private Map<Integer, ArrayList<Variable>> parents;	//a subset of variables where parents_i is the set of parents of variable i
	private ArrayList<Potential> potentials;
	private Graph graph;
	
	GraphicalModel(){
		
		this.variables = new ArrayList<Variable>();
		this.potentials = new ArrayList<Potential>();
	}
	

	/**
	 * sets the type of the graphical model
	 * @param type_
	 */
	void setType(String type_){
		
		this.type = type_;
	}
	
	/**
	 * sets the type of the graphical model
	 * @return
	 */
	String getType(){
		
		/*if(this.type == null || this.type.equals("")){
			
			System.out.println("Exception in GraphicalModel.getType() : type not specified!");
			return "";
		}*/
		
		return this.type;
	}
	

	/**
	 * Confirms whether this model is a Bayesian network
	 * @return
	 */
	boolean isBayes(){
		
		if(this.type.equalsIgnoreCase("Bayes"))
			return true;
		
		else
			return false;
	}
	
	
	/**
	 * Confirms whether this model is a Bayesian network
	 * @return
	 */	
	
	boolean isMarkov(){
		
		if(this.type.equalsIgnoreCase("Markov"))
			return true;
		
		else
			return false;
	}
	
	
	/**
	 * Create the underlying graph representation from the set of potentials 
	 */
	void createEmptyGraph(){
		
		this.graph = new Graph();
		//this.graph = Graph.createGraph(Variable.getLabels(this.getVariables()), this.getPotentials());
	}
	
	void constructGraph(){
		
		if(this.graph == null)
			this.graph = new Graph();
		
		this.graph = Graph.createGraph(Variable.getLabels(this.getVariables()), this.getPotentials(), this.getType());
		
	}
	
	/**
	 * fetch the graph
	 * @return
	 */
	Graph getGraph(){
		
		return this.graph;
	}
	
	/**
	 * adds a variable to the graphical model
	 * @param v
	 */
	void addVariable(Variable v){
		
		if(this.variables == null)
			this.variables = new ArrayList<Variable>();
		
		if(!this.variables.contains(v))
			this.variables.add(v);
	}
	
	void addVariables(ArrayList<Variable> variables_){
		
		if(this.variables == null)
			this.variables = new ArrayList<Variable>();
		
		if(!this.variables.containsAll(variables_)){
			
			variables_.removeAll(this.variables);
			this.variables.addAll(variables_);
		}
			
	}
	
	/**
	 * adds parents_ of variable i - used when the model is Bayes
	 * @param i
	 * @param parents_
	 */
	void addParents(int i, ArrayList<Variable> parents_){
		
		if(this.parents == null)
			this.parents = new LinkedHashMap<Integer, ArrayList<Variable>>();
		
		if(!this.parents.containsKey(i)){
			
			this.parents.put(i, new ArrayList<Variable>());
			this.parents.get(i).addAll(parents_);			
		}		
		
		else{
			
			parents_.removeAll(this.parents.get(i));
			this.parents.get(i).addAll(parents_);
		}
	}
	
	void addParents(Map<Integer, ArrayList<Variable>> parents_){
		
		if(this.parents == null)
			this.parents = new LinkedHashMap<Integer, ArrayList<Variable>>(parents_);
		
	}
	
	Map<Integer, ArrayList<Variable>> getParents(){
		
		return this.parents;
	}
	
	/**
	 * adds a parent to variable i - used when the model is Bayes
	 * @param i
	 * @param v
	 */
	void addParent(int i, Variable v){
		
		if(this.parents == null)
			this.parents = new LinkedHashMap<Integer, ArrayList<Variable>>();
		
		if(!this.parents.containsKey(i)){
			
			this.parents.put(i, new ArrayList<Variable>());
			this.parents.get(i).add(v);
		}
		
		else if(!this.parents.get(i).contains(v))
			this.parents.get(i).add(v);
		
		else
			;
	}
	
	/**
	 * get the i-th variable
	 * @param i
	 * @return
	 */
	Variable getVariable(int i){
		
		if(this.variables == null || this.variables.size() < i){
			
			System.out.println("Exception in GraphicalModel.getVariable(int) : index out of bounds!");
			Util.halt();
			return null;
		}
		
		return this.variables.get(i);
	}
	
	
	/**
	 * return the variables of the model
	 * @param identifiers
	 * @return
	 */
	public ArrayList<Variable> getVariables(){
		
		if(this.variables == null || this.variables.size() == 0){
			
			System.out.println("Exception in GraphicalModel.getVariables() : index out of bounds!");
			Util.halt();
			return null;
		}
		
		return this.variables;
	}
	
	
	/**
	 * get all the potentials
	 * @return
	 */
	ArrayList<Potential> getPotentials(){
		
		
		if(this.potentials == null || this.potentials.size() == 0){
			
			//System.out.println("Exception in GraphicalModel.getPotentials() : index out of bounds!");
			//Util.halt();
			return new ArrayList<Potential>();
		}
		
		return this.potentials;		
	}
	
	void addEmptyPotentials(ArrayList<Potential> potentials){
		
		if(this.potentials == null)
			this.potentials = new ArrayList<Potential>();
		//add empty potentials to the new model		
		for(int i=0; i<potentials.size(); i++){
			
			Potential p = new Potential();
			p.addScope(potentials.get(i).getScope());
			p.initializeParameters(potentials.get(i).parameterCount());
			this.addPotential(p);			
		}		
		
	}	
	
	/**
	 * add a potential
	 * @param p
	 */
	void addPotential(Potential p){
		
		if(this.potentials == null)
			this.potentials = new ArrayList<Potential>();
		
		if(!this.potentials.contains(p))
			this.potentials.add(p);
	}
	
	void addPotentials(ArrayList<Potential> potentials_){
		
		if(this.potentials == null)
			this.potentials = new ArrayList<Potential>();
		
		potentials_.removeAll(this.getPotentials());
		this.potentials.addAll(potentials_);
			
	}
	
	/**
	 * return the i-th potential
	 * @param i
	 * @return
	 */
	Potential getPotential(int i){
		
		if(this.potentials == null || this.potentials.size() < i){
			
			System.out.println("Exception in GraphicalModel.getPotential(int) : index out of bounds!");
			Util.halt();
			return null;
		}
		
		return this.potentials.get(i);
	}
	
	/**
	 * return the number of variables
	 * @return
	 */
	int variableCount(){
		
		return this.variables.size();
	}
	
	/**
	 * return the number of potentials
	 */
	int potentialCount(){
		
		return this.potentials.size();
	}	
	
	/**
	 * prints the model
	 */
	void print(){
		
		System.out.println("\nPrinting Graphical Model...\n");
		
		System.out.println(this.type+" Network");
		
		System.out.print("Number of variables N : "+this.variableCount());
		
		//print parents of variables
				
		System.out.println(" "+Variable.getLabels(this.getVariables()));		
	
		
		if(this.isBayes()){
			
			
			Iterator itr = this.parents.entrySet().iterator();
			
			while(itr.hasNext()){
				
				Entry<Integer, ArrayList<Variable>> entry = (Entry<Integer, ArrayList<Variable>>) itr.next();
				
				System.out.println("Parents ("+entry.getKey()+") = "+Variable.getLabels(entry.getValue()));
			}
			
		}
		
		Util.waitForInput();
		
		int numOfPotentials = this.potentialCount();
		
		System.out.println("Number of potentials : "+numOfPotentials+"\n");
	
		for(int i=0; i<numOfPotentials; i++){
			
			Potential p = this.getPotential(i);
			
			if(p.isDeleted())
				continue;
			
			System.out.print("Potential: "+i);
			
			System.out.println("\tdefined over : "+Variable.getLabels(p.getScope())+"\n");
			
			System.out.println(Arrays.toString(p.getParameters()));
			
			System.out.println("\n");
				
		}			
		
		this.graph.print();
		
		System.out.println("-------------GRAPHICAL MODEL-------------\n");	
		
	}
	
	/**
	 * Only applicable for Bayesian networks with a topological ordering
	 * This method generates samples from a Bayesian network
	 * @param order
	 */
	
	/*Dataset generateBNSamples(int N){
		
		ArrayList<Variable> order = this.getTpoLogicalOrder();
		
		System.out.println("Topological order : "+Variable.getLabels(order));	
				
		ArrayList<int[]> samples = this.sample(order, N);
		
		Dataset S = new Dataset();
		
		S.setVariables(this.getVariables());
		
		for(int i=0; i<N; i++){
			
			S.addDataIndex(i);
			S.addDataInstance(samples.get(i));
		}
		
		
		return S;
		
	}
	
	ArrayList<Variable> getTpoLogicalOrder(){
		
		ArrayList<Variable> order = new ArrayList<Variable>();
		
		for(int i=0; i<this.variableCount(); i++){
			
			if(order.contains(this.getVariable(i)))
				continue;
			
			else
				topologicalOrder(order, this.getVariable(i));			
			
		}			
		
		return order;
	}
	
	private void topologicalOrder(ArrayList<Variable> order, Variable v){
		
		ArrayList<Variable> parents_v = this.parents.get(v.getLabel());
		
		//System.out.println("Parents of "+v.getLabel()+" : "+Variable.getLabels(parents_v));
		
		if(parents_v == null){
			
			order.add(v);
			return;
		}
		
		
		else if(order.containsAll(parents_v)){
			
			order.add(v);
			return;
		}
		
		else{
			
			parents_v.removeAll(order);
		
			for(int i=0; i<parents_v.size(); i++)
				topologicalOrder(order, parents_v.get(i));
		
			order.add(v);
			return;
			
		}	
	}
	
	*/
	/**
	 * Given a variable ordering and the number of samples N, this method generates samples
	 * according to the given order
	 * @param order
	 * @param N
	 * @return
	 */
	/*public ArrayList<int[]> sample(ArrayList<Variable> order, int N){
		

		ArrayList<int[]> samples = new ArrayList<int[]>();
		
		for(int i=0; i<N; i++){
			
			int [] sample = new int[this.variableCount()];
			
			for(int j=0; j<order.size(); j++){
				
				//get the j-th variable
				Variable var_j = order.get(j);
				
				//get the potentials of the variable
				
				Potential cpt_j = var_j.getPotential(0);			
				
				double rand = Math.random();
				
				int k=0;				
				double r = 0;
				
				do
				{
					
					var_j.setValue(k);
					
					r = r + cpt_j.instantiate();
									
					if(k == var_j.domainSize()-1)
						break;
					k++;
					
				}while(r<rand);				
						
				sample[var_j.getLabel()] = var_j.getValue();	
				
			}
			
			//System.out.println("Sample["+i+"] : "+Arrays.toString(sample));
			
			samples.add(sample);
		}
		
		for(int i=0; i<order.size(); i++){
			
			int count_i = 0;
			
			for(int j=0; j<N; j++)
				if(samples.get(j)[order.get(i).getLabel()] == 1)
					count_i++;
			
			//System.out.println(order.get(i).getLabel()+" \t"+(double)count_i/N);
			
		}
		
		return samples;
	}
	*/
	
	
	
	/**
	 * given a set of variables, this method sets the evidence variables to
	 * the given evidence values - must be called before probOfEvidence()
	 * @param evidVars
	 */
	void setEvidence(ArrayList<Variable> eVars, int[] eValues){
		
		for(int i=0; i<eVars.size(); i++){
			
			Variable scopeVar = eVars.get(i);//this.getScope(eVars.get(i));
			
			scopeVar.setValue(eValues[scopeVar.getLabel()]);
		}
		
	}		
	
	

	/**
	 * Computes the probability of evidence of a Bayesian Network
	 * @param evidence
	 * @return
	 */
	double probOfEvidence(){
		
		double pr = 1.0;
		
		//instantiate each potential
		
		for(int i=0; i<this.potentialCount(); i++){
			
			double weight = this.getPotential(i).instantiate();
			//System.out.print(" "+weight);
			pr = pr * weight;
		}	
		
		//System.out.println();
		return pr;
	}
	
	/**
	 * Given a Bayesian network with structure and data and the smoothing factor, this method learns
	 * the parameters from data
	 * @param gm
	 * @param ds
	 * @param alpha
	 * @return
	 */
	/*static GraphicalModel learnParameters(GraphicalModel gm, Dataset ds, double alpha){
		
		GraphicalModel gm_ = new GraphicalModel();
		
		//System.out.println("Variables : "+Variable.getLabels(gm.getVariables()));
		
		gm_.addVariables(gm.getVariables());
		
		gm_.setType(gm.getType());
		
		gm_.addParents(gm.getParents());
		
		
		
		//add empty potentials to the new model
		
		for(int i=0; i<gm.potentialCount(); i++){
			
			Potential p = new Potential();
			p.addScope(gm.getPotential(i).getScope());
			p.initializeParameters(gm.getPotential(i).countParameters());
			gm_.addPotential(p);
			
		}
		
		gm_.constructGraph();
		
		//learn the parameters according to topological order
		
		ArrayList<Variable> order = gm.getTpoLogicalOrder();
		
		for(int i=0; i<gm_.potentialCount(); i++){
			
			Potential p_i = gm_.getPotential(i);
			
			if(p_i.getScope().size() == 1){	//no parents
				
				Dataset ds_i = ds.project(p_i.getScope());
				
				//ds_i.print("dataset for "+Variable.getLabels(p_i.getScope()));
				
				for(int j=0; j<p_i.countParameters(); j++){
					
					//double param = (double)ds_i.count(p_i.getScope().get(0).getLabel(), j)/ds_i.size();
					double param = (double)(ds_i.count(p_i.getScope().get(0).getLabel(), j)+1)/(ds_i.size()+2);
					p_i.addParameter(param, j);
					//System.out.println(p_i.getScope(0).getLabel()+"["+j+"] : "+param);
				}			
				
			}
			
			else{//conditional
				
				Dataset ds_i = ds.project(p_i.getScope());
				
				//ds_i.print("dataset for "+Variable.getLabels(p_i.getScope()));
				
				int totalCount_xy = ds_i.size();
				
				for(int j=0; j<ds_i.size(); j++){
					
					int index_ = ds_i.getDataIndex(j);
					
					int[] instance_xy = ds_i.getDataInstance(index_);
					
					int val = Variable.toInteger(p_i.getScope(), instance_xy);
					
					p_i.parameters[val]++;
				}
				
				for(int k=0; k<p_i.parameters.length; k++){
					
					//p_i.parameters[k] = (double)p_i.parameters[k]/totalCount_xy;
					
					p_i.parameters[k] = (double)(p_i.parameters[k]+1)/(totalCount_xy+2);
					
					//System.out.println(Variable.getLabels(p_i.getScope())+"["+k+"] : "+p_i.parameters[k]);					
				}				
			}
		}
		
		
		return gm_;
	}*/
	
	
	/**
	 * computes the log-likelihood of a given set of data according to this model
	 * @return
	 */
	double computeLL(Dataset ds){
		
		
		int countZeroProbs = 0;
		double logLikelihood = 0.0;
		double probOfevidence = 0.0;
		
		ArrayList<Variable> evidVars = ds.getVariables();
		
		for(int i=0; i<ds.size(); i++){
			
			int[] evidence = ds.getDataInstance(i);
			
			this.setEvidence(evidVars, evidence);
			
			probOfevidence = this.probOfEvidence();
			
			//System.out.println("Probability of evidence "+i+" "+probOfevidence);//Arrays.toString(evidence)+" : "+probOfevidence);
			
			if(probOfevidence == 0){
				
				//Util.halt("Probability of sample is 0.0");
				countZeroProbs++;
				//continue;
				
			}	
			
			logLikelihood = logLikelihood + Math.log10(probOfevidence)/Math.log10(2);
		}		
		
		if(countZeroProbs > 0)
			System.out.println("Number of zero probability samples : "+countZeroProbs);
		return logLikelihood;
	}

	/**
	 * Converts to dot format
	 * @param gv
	 * @param n
	 * @param visitedNodes
	 */
	void convertToDot(GraphViz gv, Graph graph, String type){
			
		
		ArrayList<Integer> visitedVertices = new ArrayList<Integer>();
		
		//add all the nodes to dot first
		
		ArrayList<Integer> vertices = graph.getVertexList();
		
		for(int  i=0; i<vertices.size(); i++)
			gv.addln(vertices.get(i)+" [shape=circle, label="+vertices.get(i)+"]");
		
		
		Iterator itr = graph.getAdjacencyList().entrySet().iterator();
		
		while(itr.hasNext()){
			
			Entry<Integer, ArrayList<Integer>> entry = (Entry<Integer, ArrayList<Integer>>) itr.next();
			
			int vertex = entry.getKey();
						
			ArrayList<Integer> neighbors = entry.getValue();
			
		
			for(int i=0; i<neighbors.size(); i++){
			
				if(type.equalsIgnoreCase("Bayes"))
					gv.addln(vertex+"->"+neighbors.get(i)+";");
				
				else
					gv.addln(vertex+"--"+neighbors.get(i)+";");
				
			}
			
		}					
			
	}
		
		//writes the tree in dot format
	void draw(String fileName, String msg, String ext){
		
		System.out.println(msg);
		GraphViz gv = new GraphViz();
		
		if(this.isBayes())
			gv.addln(gv.start_graph("digraph"));
		else
			gv.addln(gv.start_graph("graph"));
		
	    gv.addln("splines=true;");
	    
	    this.convertToDot(gv, this.getGraph(), this.getType());
	    gv.addln(gv.end_graph());
	    
	    try {
	         
			 File dotFile = new File(fileName+".dot");
			 File outputFile = new File(fileName+"."+ext);
			 FileWriter fw = new FileWriter(dotFile);	         
			 
			 fw.write(gv.getDotSource());
			 fw.close();
			
			 System.out.println("Dot file : "+dotFile.getPath());
			 System.out.println("Output file : "+outputFile.getPath());
			 
			 Runtime rt = Runtime.getRuntime();
			 String[] args = {"dot", "-T"+ext, dotFile.getPath(), "-o", outputFile.getPath()};
			 //String args = "dot -Tpdf "+dotFile.getAbsolutePath()+" -o "+pdfFile.getAbsolutePath();		   
			 
			 Process p = null;
			 p = rt.exec(args);
			 
			 int code = p.waitFor();	
			 
	
	     }

	    catch (Exception e) {
	         e.printStackTrace();
	         System.exit(0);
	    }        
	    
	    
	    System.out.println("End graph drawing...");
	}//end of convertToDot()
	
	
	void writeUAI(String fileName){
		
		File uaiFile = new File(fileName);
		
		System.out.println("Writing UAI file "+uaiFile.getName());
		
		try {
			
			FileWriter fw = new FileWriter(uaiFile);
			fw.write(this.getType()+"\n");
			fw.write(this.variableCount()+"\n");
			
			for(int i=0; i<this.variableCount(); i++){
				
				fw.write(this.getVariable(i).domainSize()+" ");
			}
			
			fw.write("\n");
			
			fw.write(this.potentialCount()+"\n");
			
			for(int i=0; i<this.potentialCount(); i++){
				
				Potential pi = this.getPotential(i);
				
				//Util.halt();
				
				//fw.write(pi.scopeSize());
				
				String scope = pi.scopeSize()+" ";
				
				//System.out.println("Scope size of potential : "+pi.scopeSize());
				
				for(int j=0; j<pi.scopeSize(); j++){
					
					scope = scope+pi.getScope(j).getLabel()+" ";
				}
				
				System.out.println(scope);
				
				fw.write(scope);
				
				fw.write("\n");
			}
			
			fw.write("\n");
			
			DecimalFormat df = new DecimalFormat("0.000");
			
			for(int i=0; i<this.potentialCount(); i++){
				
				
				Potential pi = this.getPotential(i);
				
				fw.write(pi.parameterCount()+"\n");
				
				for(int j=0; j<pi.parameterCount(); j++)
					fw.write(df.format(pi.getParameter(j))+" ");
				
				fw.write("\n");
			}
			
			fw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
		
	
}
