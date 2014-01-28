/**
 * 
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class Dataset {

	//keeps track of the projected variables - changes
	ArrayList<Variable> variables;
	//list of indices of the current dataset - changes
	ArrayList<Integer> indices;
	
	//main set of data - created once and never changed
	ArrayList<int[]> data;		//store it compactly!!
	
	public Dataset(){
		
		//initialize
		/*this.data = new ArrayList<int[]>();
		this.instances = new ArrayList<Integer>();
		this.variables = new ArrayList<Variable>();*/
	}
	
	
	
	/**************************************Variable/Attribute Handling*************************************/
	
	/**
	 * Sets the variables of the dataset
	 * @param variables_
	 */
	void setVariables(ArrayList<Variable> variables_){
		
		if(this.variables == null)
			this.variables = new ArrayList<Variable>(variables_);
		
		else{
			
			this.variables.clear();
			this.variables.addAll(variables_);
		}
	}
	
	/**
	 * deletes the given variable from the dataset
	 * @param var
	 */
	void removeVariable(Variable var){
		
		if(this.variables == null){
			
			Util.halt("Exception in removeVariable() : set of variables is null!");
		}
		
		else{
			 
			if(!this.variables.contains(var))
				Util.halt("Exception in removeVariable() : variable not found!");
			else
				this.variables.remove(var);
		}	 
	}
	
	/**
	 * retrieves the list of variables of the dataset
	 * @return
	 */
	ArrayList<Variable> getVariables(){
		
		if(this.variables == null){
			
			System.out.println("Exception in getVariables() : list of variables is null!");
			return null;
		}
			
		else
			return this.variables;
	}//end of getVariables()
	
	
	/**
	 * retrieves the i-th variable in the list of variables
	 * @param index
	 * @return
	 */
	Variable getVariable(int index){
		
		if(this.variables == null){
			
			System.out.println("Exception in getVariable() : list of variables is null!");
			return null;
		}
			
		else
			return this.getVariables().get(index);
	}//end of getVariable()
	
	
	/**
	 * retrieves the variable with the given label
	 * @param label
	 * @return
	 */
	Variable getVariableByLabel(int label){
		
		if(this.variables == null){
			
			System.out.println("Exception in getVariable() : list of variables is null!");
			return null;
		}	
		
		for(int i=0; i<this.getVariables().size(); i++){
			
			Variable v = this.getVariable(i);
			if(v.getLabel() == label)
				return v;
		}
		
		System.out.println("Exception in getVariableByLabel() : The queried variable was not found!");
		return null;		
	}//end of getVariableByLabel()
	
	/**
	 * Get the scope size or number of variables on which the data is defined
	 * @return
	 */
	int scopeSize(){
		
		if(this.variables == null || this.variables.size()==0)
			return 0;
		
		else
			return this.variables.size();
	
	}//end of scopeSize()
	
	/************************************Instance Index Handling***************************************/
	
	/**
	 * sets the list of indices 
	 * @param indices
	 */
	void setDataIndices(ArrayList<Integer> indices_){
		
		if(this.indices == null)
			this.indices = new ArrayList<Integer>(indices_);		
	}
	
	
	/**
	 * gets the set of data indices
	 * @return
	 */
	ArrayList<Integer> getDataIndices(){
		
		if(this.indices == null){
			
			//System.out.println("Exception in getDataIndices() : null data indices!");
			//Util.halt("Exception in getDataIndices() : null data indices!"); 
			//return null;
			return new ArrayList<Integer>();
		}
		
		else
			return this.indices;
	}
	
	/**
	 * adds a data index to the list of data indices
	 * @param index_
	 */
	void addDataIndex(int index_){
		
		if(this.indices == null){
			
			this.indices = new ArrayList<Integer>();
			this.indices.add(index_);
		}
		
		else
			if(this.indices.indexOf(index_) == -1)
				this.indices.add(index_);
	
	}//end of addDataIndex
	
	void addDataIndices(ArrayList<Integer> indices){
		
		if(this.indices == null)
			this.indices = new ArrayList<Integer>();
		
		for(int i=0; i<indices.size(); i++){
			
			this.indices.add(indices.get(i));
			
		}
		
	}
	
	/**
	 * adds a data index to the list of data indices
	 * @param index_
	 */
	void addDataIndexWithDuplications(int index_){
		
		if(this.indices == null){
			
			this.indices = new ArrayList<Integer>();
			
		}
		
		this.indices.add(index_);
	
	}//end of addDataIndex
	
	
	
	/**
	 * deletes the data index from the list of data indices
	 * @param index_
	 */
	void deleteDataIndex(int index_){
		
		if(this.indices.indexOf(index_) == -1){
			
			System.out.println("Exception in deleteDataIndex() : data index does not exist!");
			Util.halt();
		}
		
		else
			this.indices.remove(this.indices.indexOf(index_));
	}//end of deleteDataIndex
	
	/**
	 * fetches the i-th data index from the list of indices
	 * @param i
	 * @return
	 */
	int getDataIndex(int i){
		
		if(this.indices == null){
			
			Util.halt("Exception in getDataIndex() : null list encountered!");
			return -1;
		}
		
		else
			return this.indices.get(i);
	
	}//end of getDataIndex()
	
	/**
	 * Returns the number of instances in this dataset indicated by the size of the data indices
	 * @return
	 */
	int size(){
		
		
		if(this.indices == null || this.indices.size() == 0)
			return 0;
		
		else
			return this.indices.size();
	
	}//end of size()
	
	/****************************************Data Instance Handling ***********************************************/
	
	/**
	 * sets the data field to point to the dataset
	 */
	
	void setData(ArrayList<int[]> data_){
		
		if(this.data == null)
			this.data = data_;
	
	}//end of setData()
	
	/**
	 * gets the data instances
	 * @return
	 */
	ArrayList<int[]> getData(){
		
		if(this.data == null){
			
			Util.halt("Exception in getData() : uninitialized data!");
			return null;
		}
		else
			return this.data;
	}
	/**
	 * adds a vector data instance to the list of instances
	 * @param instance_
	 */
	void addDataInstance(int[] instance_){
		
		if(this.data == null)
			this.data = new ArrayList<int[]>();
		
		if(!this.data.contains(instance_))
			this.data.add(instance_);
		
		else
			System.out.println("Data instance exists...");
	}//end of addDataInstance()
	
	void addDataInstances(ArrayList<int[]> instances){
		
		if(this.data == null)
			this.data = new ArrayList<int[]>();
		
		for(int i=0; i<instances.size(); i++){
			
			if(!this.getData().contains(instances.get(i)))
				this.addDataInstance(instances.get(i));
		}
	}//end of addDataInstance()
	
	
	/**
	 * adds a vector data instance to the list of instances
	 * @param instance_
	 */
	void addDataInstanceWithDuplications(int[] instance_){
		
		if(this.data == null)
			this.data = new ArrayList<int[]>();
		
		this.data.add(instance_);
		
		
	}//end of addDataInstance()
	
	
	/**
	 * deletes an instance from the list of data instances
	 * @param instance_
	 */
	void deleteDataInstance(int[] instance_){
		
		if(this.data == null){
			
			System.out.println("Exception in deleteDataInstance() : null dataset!");
			Util.halt();
		}
		
		if(!this.data.contains(instance_)){
			
			System.out.println("Exception in deleteDataInstance() : the data instance does not exist!");
			Util.halt();
		}
		
		else
			this.data.remove(instance_);
	}//end of deleteDataInstance()
	
	/**
	 * retrieves the data instance at index i 
	 * @param index_
	 */
	int[] getDataInstance(int index){
		
		//verify if the i-th index is a valid index in the list of indices
		/*if(!this.checkConsistency(index)){
			
			System.out.println("Exception in getInstance() : the data index does not exist!");
			Util.halt();
			return null;
		}
		
		else{
			
			int[] instance = this.data.get(index);
			
			//project onto the proper variables
			/*if(this.scopeSize() < instance.length){
				
				int[] projectedInstance = new int[this.scopeSize()];
				
				for(int i=0; i<projectedInstance.length; i++){
					
					projectedInstance[i] = instance[this.getVariable(i).getLabel()];
				}
				return projectedInstance;
			}
			else
				return instance;
			
		}*/	
		//int dataIndex = this.getDataIndex(index);
		
		return this.data.get(index);
		
		
	}//end of getInstance()
	
	
	/**
	 * verifies whether the given index i is present in the list of data indices
	 * @param i
	 * @return
	 */
	boolean checkConsistency(int i){
		
		if(this.indices != null)
			if(this.indices.indexOf(i) == -1)
				return false;
			else
				return true;
		
		else{
			
			
			return false;
		}
			
			
	}//end of checkConsistency()
	
	/**
	 * verifies whether the number of indices is the same as the size of the dataset
	 * @return
	 */
	boolean checkConsistency(){
		
		if(this.indices != null)
			if(this.indices.size() != this.data.size())
				return false;
			else
				return true;
		
		else
			return false;
	
	}//end of checkConsistency
	
	int[] project(int[] Sample){
		
		//project onto the proper variables
		
		int[] projectedInstance = new int[this.scopeSize()];
			
		for(int i=0; i<projectedInstance.length; i++){
				
			projectedInstance[i] = Sample[this.getVariable(i).getLabel()];
		}
			
		return projectedInstance;
	}
	
	
	/**
	 * Given a set of attributes/variables this method randomly generates data from
	 * a random distribution 
	 */
	static Dataset generateData(ArrayList<Variable> attributes, int N){
		
		Dataset newDataset = new Dataset();
		
		newDataset.setVariables(attributes);
		
		System.out.println("Generating new dataset of size "+N);
		
		for(int i=0; i<N; i++){
			
			int[] data_i = new int[attributes.size()];
			
			for(int j=0; j<attributes.size(); j++){
				
				double random = Math.random();
				
				//System.out.println(random);
				
				int domSize = attributes.get(j).domainSize();
				
				double interval = (double)1/domSize;
				
				double k=1;
				
				//System.out.println(interval*k);
				
				while((interval*k) < random)
					k++;
				
				data_i[j] = (int)k-1;
			}
			
			newDataset.addDataInstance(data_i);
			newDataset.addDataIndex(i);
			
		}
		
		return newDataset;
	}
	
	/**
	 * prints the data set
	 */
	public void print(String str){
		
		System.out.println(str);
		
		ArrayList<Integer> variableLabels = Variable.getLabels(this.getVariables());
		
		System.out.println("Number of variables : "+variableLabels.size());
		
		System.out.println("Number of instances : "+this.size());
		
		System.out.println("Variables : "+variableLabels);
		
		int N = this.size();
		
		int[] projectedInstance = new int[this.scopeSize()];
		
		for(int i=0; i<N; i++){
			
			int instance_index = this.getDataIndex(i);
			
			int[] instance = this.getDataInstance(instance_index);		
			
			for(int j=0; j<this.scopeSize(); j++)
				projectedInstance[j] = instance[this.getVariable(j).getLabel()];
			
			//System.out.println("Instance "+instance_index+" : "+Arrays.toString(projectedInstance));
			System.out.println(Arrays.toString(projectedInstance));
			
		}		
		
	}//end of print()
	
	/**Counts the number of instances that have the given value of the variable_i
	 * @param attribute_i
	 * @param value
	 * @return
	 */
	int count(int variable_i, int value){
		
		int count_i = 0;
		
		for(int i=0; i<this.size(); i++){
			
			//get the index from the valid indices
			int index = this.getDataIndex(i);
			
			int[] instance = this.getDataInstance(index);
			
			if(instance[variable_i] == value)
				count_i++;			
		}
		
		return count_i;
		
	}//end of count_i;
	
	/**
	 * finds the entropy of the given attribute
	 * @param attribute
	 * @return
	 */
	double entropy(Variable variable){
		
		double entropy = 0.0;
		
		//for each value v of attribute, compute -pr(v)log[pr(v)]
		
		int totalCount = this.size();
		
		for(int i=0; i<variable.domainSize(); i++){
			
			int count_i = this.count(variable.getLabel(), i);
			
			double pr_i = (double)count_i/(double)totalCount;
			
			if(pr_i  != 0)
				entropy = entropy - ((pr_i) * Math.log10(pr_i)/Math.log10(2));
			
			//else
				//Util.halt("0 prob!");
			
		}
		
		return entropy;
		
	}//end of entropy()
	
	/**
	 * finds the joint entropy of the variable X and Y
	 * @param X
	 * @param Y
	 * @return
	 */
	double jointEntropy(Variable X, Variable Y){
		
		double entropy = 0.0;
		
		//for each (x,y) belonging to (X,Y) compute -p(x,y)log(pr(x,y))
		
		int[] dom_xy = new int[X.domainSize() * Y.domainSize()]; 
		
		ArrayList<Variable> xy = new ArrayList<Variable>();
		
		xy.add(X);
		xy.add(Y);
		
		Dataset project_xy = this.project(xy);

		int totalCount_xy = project_xy.size();
		
		for(int i=0; i<project_xy.size(); i++){
			
			int index_xy = project_xy.getDataIndex(i);
			
			int[] instance_xy = project_xy.getDataInstance(index_xy);
			
			int val = Variable.toInteger(xy, instance_xy);
			
			dom_xy[val]++;
		}
		
		for(int i=0; i<dom_xy.length; i++){
			
			int count_xy_i = dom_xy[i];
			
			double pr_xy_i = (double)count_xy_i/totalCount_xy;
			
			if(pr_xy_i != 0)
				entropy = entropy - ((pr_xy_i) * Math.log10(pr_xy_i)/Math.log10(2));
			
		}	
		
		return entropy;
		
	}//end of jointEntropy
	
	/**
	 * returns the conditional entropy of H(X|Y) = H(X,Y) - H(Y)
	 * @param X
	 * @param Y
	 * @return
	 */
	double conditionalEntropy(Variable X, Variable Y){
		
		double H_x_y = 0.0;	//conditional entropy of x given y
		
		//calculate entropy of Y
		
		double H_y = this.entropy(Y);
		
		//calculate joint entropy of X and Y
		
		double H_xy = this.jointEntropy(X, Y);
		
		H_x_y = H_xy - H_y;
		
		return H_x_y;
		
	}//end of conditionalEntropy()
	
	
	/**
	 * Selects a set of instances from this dataset
	 * @param selectedInstances
	 * @return
	 */
	Dataset select(ArrayList<Integer> selectedInstances){
		
		Dataset newDataset = new Dataset();
		
		newDataset.setData(this.getData());
		
		newDataset.setVariables(this.getVariables());
		
		newDataset.setDataIndices(selectedInstances);
		
		return newDataset;		
	}



	/**
	 * Projects the given variables from this dataset and returns a new dataset
	 * @param projectedVars
	 */
	Dataset project(ArrayList<Variable> projectedVars){
		
		Dataset newDataset = new Dataset();
		
		newDataset.setData(this.getData());
		
		newDataset.setVariables(projectedVars);
		
		newDataset.setDataIndices(this.getDataIndices());
		
		return newDataset;		
	}

	/**
	 * Splits the dataset 
	 * @return
	 */
	Dataset split(int N){
		
		Dataset split = new Dataset();
		
		split.setVariables(this.getVariables());
		
		split.setData(this.getData());
		
		//generate N number of random indices
		
		int[] randIndices = new int[N];
		
		for(int i=0; i<N; i++){
			
			int randIndex = 0 + (int)(Math.random()*(N+1));
			
			split.addDataIndexWithDuplications(this.getDataIndex(randIndex));
		
			//int[] instance = this.getDataInstance(this.getDataIndex(randIndex));
			
			//split.addDataInstanceWithDuplications(instance);
		}
		
		
		return split;
	
		
	}
	
	Dataset splitWOR(int N){
		
		Dataset split = new Dataset();
		
		split.setVariables(this.getVariables());
		
		split.setData(this.getData());
		
		//generate N number of random indices
		
		int[] randIndices = new int[N];
		
		for(int i=0; i<N; i++){
			
			int randIndex = 0 + (int)(Math.random()*(N+1));
			
			if(!split.getDataIndices().contains(this.getDataIndex(randIndex))){
				
				split.addDataIndex(this.getDataIndex(randIndex));
				i++;
			}	
		
			//int[] instance = this.getDataInstance(this.getDataIndex(randIndex));
			
			//split.addDataInstanceWithDuplications(instance);
		}
		
		
		return split;
	
		
	}



	
	/**
	 * partition this dataset based on the given attributes value
	 * @param varLabel
	 * @param value
	 * @return
	 */
	Dataset partition(int varLabel, int value){
		
		Dataset partition = new Dataset();
		
		partition.setData(this.getData());
		
		//the partition will not contain the variable on which the data was partitioned
		partition.setVariables(this.getVariables());
		
		//partition.removeVariable(this.getVariableByLabel(varLabel));
		
		for(int i=0; i<this.size(); i++){
			
			int index = this.getDataIndex(i);
			
			int[] instance = this.getDataInstance(index);
			
			if(instance[varLabel] == value)
				partition.addDataIndex(index);
		
		}		
		
		return partition;
		
	}//end of partition()
	
	/**
	 * conditions the dataset on the value of the variable
	 * @param var
	 * @param value
	 * @return
	 */
	Dataset condition(Variable var, int value){
		
		Dataset newDataset = this.partition(var.getLabel(), value);
		
		//if(newDataset == null || newDataset.size() == 0)
			//return newDataset;
		
		ArrayList<Variable> projectVars = new ArrayList<Variable>(this.getVariables());
		
		projectVars.remove(var);
		
		newDataset = newDataset.project(projectVars);		
		
		return newDataset;		
	
		
	}
	
	
	/**
	 * reads training data from a file
	 * @param fileName
	 */
	
	public void readData(String fileName){
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.print("Enter the number of classes:");
		
		try {
		
			int classes = Integer.parseInt(br.readLine());
			String line = "";
			int numberOfInstances = 0;
			
			System.out.print("Enter the number of attributes:");
			int attributes = Integer.parseInt(br.readLine());
			
			System.out.println("Enter the number of instances:");
			//int numberOfInstances = Integer.parseInt(br.readLine());
			
			br = new BufferedReader(new FileReader(fileName));
			
		//	this.initialize(attributes, numberOfInstances);
			
			line = br.readLine();
			while(line != null){
				
				line = line.trim();
				
				int[] newInstance = new int[attributes];
							
				String[] words = line.split("\\s+");
				
				for(int j=0; j<words.length-1; j++){
					
					//this.data[numberOfInstances++][j] = Integer.parseInt(words[j]);
					//System.out.println(data[i][j]);					
					newInstance[j] = Integer.parseInt(words[j]);
				}
				
				this.data.add(newInstance);
				
				line = br.readLine();
				
			}
			
			
			System.out.println("End reading training file "+fileName);
			br.close();
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
				
				
	}//end of readData()
	
	
	ArrayList<Variable> getMaxEntropyVars(){
		
		ArrayList<Variable> maxEntropyVars = new ArrayList<Variable>();
		
		double maxEntropy = 0.0;
		Variable V = null;
		double[] entropies = new double[this.scopeSize()];
		int [] labels = new int[this.scopeSize()];
		
		int maxEntropyCount = 0;
		
		
		for(int i=0; i<this.getVariables().size(); i++){
			
			Variable v_i = this.getVariable(i);
			
			double entropy_i = this.entropy(v_i);
			
			//System.out.println("Entropy : "+entropy_i);
			
			if(entropy_i > maxEntropy){
				
				maxEntropy = entropy_i;
				V = v_i;
			}
			
			entropies[i] = entropy_i;
			
		}		
		
		if(maxEntropy == 0.0){	//none of the variables have an entropy greater than 0.0. So select any one variable
			
			maxEntropyVars.addAll(this.getVariables());
		}
		
		
		else if(maxEntropy > 0){
			
			for(int i=0; i<entropies.length; i++){
				
				if(entropies[i] == maxEntropy){
					maxEntropyCount ++;
					maxEntropyVars.add(this.getVariable(i));
				}	
			}
			
			if(maxEntropyCount > 2){
				
				//this.print("dataset");
				//Util.halt("Number of maxEntropy var "+maxEntropyCount);
			}
			
		}
		
		else
			;//maxEntropyVars.add(V);
		
		return maxEntropyVars;
	}
	
	
	/**
	 * returns the variable that has the maximum entropy in the data
	 * @return
	 */
	Variable getMaxEntropyVar(){
		
		double maxEntropy = 0.0;
		
		Variable V = null;
		
		/*for(int i=0; i<this.getVariables().size(); i++){
			
			Variable v_i = this.getVariable(i);
			
			double entropy_i = this.entropy(v_i);
			
			//System.out.println("Entropy : "+entropy_i);
			
			if(entropy_i > maxEntropy){
				
				maxEntropy = entropy_i;
				V = v_i;
			}
			
			
		}		
		
		if(V == null){	//none of the variables have an entropy greater than 0.0. So select any one variable
			
			V = this.getVariable(0);
		}*/
		
		ArrayList<Variable> maxEntropyVars = this.getMaxEntropyVars();
		
		if(maxEntropyVars.size() > 1){
			
			Random rand = new Random();
			
			int ranVar = 0 + (int)(Math.random()*(maxEntropyVars.size() - 0));
			
			V = maxEntropyVars.get(ranVar);
		}
		
		else
			V = maxEntropyVars.get(0);
		
		return V;
	}	
	
	
	void writeToFile(String fileName){
		
		
		try {
		
			FileWriter fw = new FileWriter(fileName);
			
			fw.write(this.scopeSize()+"\n");
			fw.write(this.size()+"\n");
			
			for(int i=0; i<this.size(); i++){
				
				String sample = Arrays.toString(this.getDataInstance(i));
				
				sample = sample.substring(1, sample.length()-1);
				
				fw.write(sample+"\n");
				
			}
			
			fw.close();
		
		} 
		
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * computes the entropy of the dataset
	 * @return
	 */
	double entropy(){
		
		double total_entropy = 0.0;
		
		if(this.size() <= 1){
			
			//Util.halt("Dataset size "+this.size());
			return 0.0;
			
		}	
		
		for(int i=0; i<this.scopeSize(); i++){
			
			Variable v = this.getVariable(i);
			
			double ENT_v = this.entropy(v);
			
			total_entropy = total_entropy + ENT_v;
		}
		
		return total_entropy;
	}
	
	boolean isConstant(){
		
		int[] sample_0 = this.getDataInstance(this.getDataIndex(0));
		
		for(int i=1; i<this.size(); i++){
			
			int[] sample_i = this.getDataInstance(this.getDataIndex(i)); 
			
			//System.out.println("sample_0 : "+Arrays.toString(sample_0)+" sample_i : "+Arrays.toString(sample_i));
			
			if(!Arrays.toString(sample_i).equals(Arrays.toString(sample_0))){
				
				return false;
				
			}
				
		}
		
		return true;
	}

	/**
	 * Verifies whether this dataset contains the given sample.
	 * @param sample_
	 * @return
	 */
	boolean contains(int[] sample_){
		
		for(int i=0; i<this.size(); i++){
			
			int[] sample_i = this.getDataInstance(i);
			
			if(Arrays.toString(sample_i).equalsIgnoreCase(Arrays.toString(sample_)))
				return true;
		}
		
		return false;
	}
	
	
	Map<int[], Double> getMLEWeights(){
		
		
		Map<int[], Double> mleWeights = new LinkedHashMap<int[], Double>();
		
		Iterator itr = mleWeights.entrySet().iterator();
		
		//compute the MLE of each validation sample
		for(int i=0; i<this.size(); i++){
			
			int[] sample_i = this.getDataInstance(this.getDataIndex(i));
			
			itr = mleWeights.entrySet().iterator();
			
			while(itr.hasNext()){
				
				Entry<int[], Double> entry = (Entry<int[], Double>) itr.next();
				
				String strSample = Arrays.toString(entry.getKey());
				
				if(strSample.equalsIgnoreCase(Arrays.toString(sample_i))){
					
					entry.setValue(entry.getValue()+1);
					//continue;
				}
			}
		
			mleWeights.put(sample_i, 1.0);
			
		}	
		
		
		
		itr = mleWeights.entrySet().iterator();	
			
		while(itr.hasNext()){
			
			Entry<int[], Double> entry = (Entry<int[], Double>) itr.next();
			
			entry.setValue((double)entry.getValue()/this.size());
			
		}
	
		return mleWeights;
	}
	
	static int getSampleIndex(ArrayList<int[]> dataset, int [] sample){
		
		
		
		String strSample = Arrays.toString(sample);
		
		for(int i=0; i<dataset.size(); i++){
			
			if(strSample.equalsIgnoreCase(Arrays.toString(dataset.get(i))))
				return i;
		}
		
		return -1;
	}
	
	/**
	 * creates a new copy of the original sample
	 * @param original
	 * @return
	 */
	static int [] copySample(int[] original){
		
		int[] copy = new int[original.length];
		
		for(int i=0; i<original.length; i++)
			
			copy[i] = original[i];
		
		return copy;
	}
	
	
}
