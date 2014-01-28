import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * An AND node is also an ANDORTree
 * @author Tahrima
 *
 */
public class ANDNode extends Node{

	boolean uniform;
	int label;
	int sampleCount;
	String constantSample; 
	ArrayList<Variable> dontCares;
	ArrayList<Distribution> distributions;
	Distribution distribution;
	double fn;	//the value of a symmetric function
	ArrayList<int[]> samples;
	
	Dataset ds;
	
	ANDNode(){
		
		
	}
	
	void addDataset(Dataset ds_){
		
		this.ds = ds_;
	}
	
	
	Dataset getDataset(){
		
		return this.ds;
	}
	
	void addSample(int[] sample){
		
		if(this.samples == null){
			
			this.samples = new ArrayList<int[]>();
		}
		
		this.samples.add(sample);
	}
	
	void addSamples(ArrayList<int[]> samples){
		
		if(this.samples == null){
			
			this.samples = new ArrayList<int[]>(samples);
			return;
		}
		
		else{
			
			ArrayList<int[]> copy = new ArrayList<int[]>(samples);
			copy.removeAll(this.getSamples());			
			this.samples.addAll(copy);
			
		}	
	}
	
	
	ArrayList<int[]> getSamples(){
		
		if(this.samples != null)
			return this.samples;
		
		else
			return new ArrayList<int[]>();
	
	}
	
	int sampleCount(){
		
		if(this.samples != null)
			return this.samples.size();
		
		else 
			return 0;
	}
	
	void setConstantSample(String sample){
		
		this.constantSample = sample;
	}
	
	String getConstantSample(){
		
		return this.constantSample;
	}
	
	void setUniform(){
		
		this.uniform = true;
	}
	
	void resetUniform(){
		
		this.uniform = false;
	}
	
	boolean isUniform(){
		
		return this.uniform;
	}
	
	void setFunctionValue(double fn_){
		
		this.fn = fn_;
	}
	
	double getFunctionValue(){
		
		return this.fn;
	}
	
	/**
	 * adds a new found don't care variable to the list of don't cares
	 * @param dontCare
	 */
	void addDontCare(Variable dontCare){
		
		if(this.dontCares == null)
			this.dontCares = new ArrayList<Variable>();
		
		if(!this.dontCares.contains(dontCare))
			this.dontCares.add(dontCare);
	}
	
	void addDontCares(ArrayList<Variable> dontCares_){
		
		if(this.dontCares == null)
			this.dontCares = new ArrayList<Variable>();
		
		if(!this.dontCares.containsAll(dontCares_)){
			
			this.dontCares.addAll(dontCares_);
		}
		
	}
	
	void setSampleCount(int sampleCount_){
		
		this.sampleCount = sampleCount_;
	}
	
	int getSampleCount(){
		
		return this.sampleCount;
	}
	
	/**
	 * Deletes the given don't care variables from the list of don't cares
	 * @param dontCares_
	 */
	void deleteDontCares(ArrayList<Variable> dontCares_){
		
		if(this.dontCares == null)
			return;
		
		else{
			
			
			this.dontCares.removeAll(dontCares_);
			
			return;			
		}
	}
	
	/**
	 * retrieve the i-th don't care
	 * @return
	 */
	Variable getDontCare(int i){
		
		if(this.dontCares == null || this.dontCares.size() < i){
			
			//Util.halt("Exception in getDontCare(int) : index out of bounds!");
			return null;
		}
		
		return this.dontCares.get(i);
	}
	
	/**
	 * returns all the dont care variables
	 * @return
	 */
	ArrayList<Variable> getDontCares(){
		
		if(this.dontCares == null){
			
			//Util.halt("Exception in getDontCares() : null list!");
			//return null;
			return new ArrayList<Variable>();
			
		}
		
		return this.dontCares;
	}
	
	/**
	 * returns the number of don't care variables at this node
	 * @return
	 */
	int dontCareCount(){
		
		if(this.dontCares == null){
			
			//Util.halt("Exception in dontCareCount() : null list!");
			return 0;
		}
		
		return this.dontCares.size();
	}
	
	/**
	 * sets the label of the node
	 * @param label_
	 */
	void setLabel(int label_){
		
		this.label = label_;
	}
	
	/**
	 * returns the label of the node
	 * @return
	 */
	int getLabel(){
		
		return this.label;
	}
	
	/**
	 * Computes the local partition function of this AND node which is the
	 * product of the local partition functions of its OR children
	 */
	double computeZ(){
		
		double z = 1.0;
		
		if(this.getChildren() == null)
			this.setZ(1);

		else{
			
			for(int i=0; i<this.childCount(); i++){
				
				z = z * this.getChild(i).getZ();
			}
			
			this.setZ(z);
		}
		
		return z;
	}
	
	
	void addDistribution(Distribution D_){
		
		/*if(this.distributions == null)
			this.distributions = new ArrayList<Distribution>();
		
		if(!this.distributions.contains(D_))
			this.distributions.add(D_);*/
		
		this.distribution = D_;
	}
	
	
	ArrayList<Distribution> getDistributions(){
		
		return this.distributions;
	}
	
	Distribution getDistribution(int i){
		
		if(this.distributions != null && i<this.childCount())
			return this.getDistributions().get(i);
		
		else{
			
			System.out.println("Exception in getDistribution(int) : null set!");
			Util.halt();
			return null;
		}
			
	}
	
	Distribution getDistribution(){
		
		if(this.distribution != null)
			return this.distribution;
		
		else
			return null;
		
	}
	
	void setDistribution(Distribution P){
		
		this.distribution = P;
	}
	
	void deleteDistribution(){
		
		this.distribution = null;
	}
	
	/**
	 * prints out the node information
	 */
	void print(){
		
		DecimalFormat df = new DecimalFormat("0.0###E00");
		
		if(this.isLeaf())
			System.out.print("Leaf");
		
		System.out.print("["+this.getID()+":"+this.getLabel()+":"+this.getLevel()+"]");
		System.out.print(" Z="+df.format(this.getZ()));
		
		if(this.getChildren() != null)
			System.out.print(" Successors"+Node.getLabels(this.getChildren())+" ");
		
		/*if(this.getDistributions() != null){
			
			//print the child distributions
			
			for(int i=0; i<this.childCount(); i++){
				
				Distribution d_i = this.getDistribution(i);
				d_i.print();
			}
		}*/
		
		if(this.getDontCares() != null)
			System.out.print(" Don't cares : "+Variable.getLabels(this.getDontCares()));//+" F(.) : "+this.getFunctionValue());
		
		else
			System.out.print(" Don't cares : []");//+" F(.) : "+this.getFunctionValue());
		
		if(this.getDataset() != null)
			this.getDataset().print("Dataset...");
		/*if(this.getDistribution() != null){
			
			System.out.print(" Distribution : ");
			this.getDistribution().print();
			
		}*/
		
		//if(this.getConstantSample() !=  null)
			//System.out.println("Sample : "+this.getConstantSample());
		
		//else
			//System.out.println(" Null Distribution...");
	}
	
	
	
	
	boolean isUnifiable(ANDNode node, double eBound){
		
		//
		
		if((this.isLeaf() && !node.isLeaf()) || (!this.isLeaf() && node.isLeaf()))
			return false;
			
		
		
		if(this.getLabel() != node.getLabel()){
			
			//System.out.println("The AND nodes don't have the same labels");
			return false;
			
		}	
		
		if(Math.abs(this.getZ() - node.getZ()) > eBound){
			
			return false;
			
		}	
		
		if(this.childCount() != node.childCount())
			return false;
		
		else
			
			for(int i=0; i<this.childCount(); i++)
				if(!this.getChild(i).equals(node.getChild(i)))
					return false;	
		
		if(this.dontCareCount() != node.dontCareCount())
			return false;
		
		else{
			
			ArrayList<Integer> list1 = Util.sortIntegerList(Variable.getLabels(this.getDontCares()));
			ArrayList<Integer> list2 = Util.sortIntegerList(Variable.getLabels(node.getDontCares()));
			
			for(int i=0; i<list1.size(); i++)
				if(list1.get(i) != list2.get(i))
					return false;
			
		}
		
		if(this.isLeaf() && node.isLeaf()){
			
			Dataset thisDS = this.getDataset();
			Dataset nodeDS = node.getDataset();
			
			//this.print();
			//node.print();
			
			if(!thisDS.getVariables().containsAll(nodeDS.getVariables())){
				
				//thisDS.addDataInstances(nodeDS.getData());
				//
				return false;
			}		
		}
		
		return true;
		
	}

	ANDNode merge(ANDNode node){
		
		ANDNode newNode = new ANDNode();
		
		newNode.setLevel(this.getLevel());
		
		newNode.setLabel(this.getLabel());
		
		for(int i=0; i<this.childCount(); i++){
			
			newNode.addChild(this.getChild(i));			
		}
		
		for(int i=0; i<this.parentCount(); i++){
			
			newNode.addParent(this.getParent(i));
		
		}
		
		for(int i=0; i<node.parentCount(); i++){
			
			newNode.addParent(node.getParent(i));
		
		}
		
		if(this.isLeaf()){
			
			newNode.setToLeaf();
		
			Dataset newDS = new Dataset();
			
			newDS.setVariables(this.getDataset().getVariables());
			
			newDS.setData(this.getDataset().getData());
			
			newDS.addDataIndices(this.getDataset().getDataIndices());	//merge the two datasets at the AND nodes
			
			newDS.addDataIndices(node.getDataset().getDataIndices());
			
			
		}	
		
		newNode.computeZ();
		
		newNode.setFunctionValue((this.getFunctionValue()+node.getFunctionValue())/2);
		
		newNode.addDontCares(this.getDontCares());
		
		return newNode;
	}
	
}
