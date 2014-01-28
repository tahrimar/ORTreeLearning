import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 
 * @author Tahrima
 *
 */
public class ORNode extends Node{

	Variable variable;
	double[] weights;
	
	
	ORNode(){
		
	}
	
	/**
	 * sets the variable of this node
	 * @param variable_
	 */
	void setVariable(Variable variable_){
		
		this.variable = variable_;
	}
	
	/**
	 * returns the variable associated with this Or node
	 * @return
	 */
	Variable getVariable(){
		
		return this.variable;
	}
	
	/**
	 * tests whether the node is an evidence node
	 */
	boolean isEvidence(){
		
		if(this.getVariable().getValue() == -1)
			return false;
			
		else
			return true;
	}
	
	
	/**
	 * returns the weight of the i-th branch
	 * @param i
	 * @return
	 */
	double getWeight(int i){
		
		if(this.weights == null){
			
			Util.halt("Exception in getWeight() : null weights!");
			return -1;
		}
		
		else
			return this.weights[i];
	}
	
	/**
	 * sets the weight of the i-th edge
	 * @param i
	 * @param weight_
	 */
	void setWeight(int i, double weight_){
		
		if(this.weights == null)
			this.weights = new double[this.variable.domainSize()]; 
		
		this.weights[i] = weight_;
	}
	
	/**
	 * returns weights of all the edges
	 * @return
	 */
	double[] getWeights(){
		
		if(this.weights == null){
			
			Util.halt("Exception in getWeight() : null weights!");
			return null;
		}
		
		else
			return this.weights;
	}
	
	/**
	 * computes the local z of the OR node - the sum of the edge weights
	 */
	double computeZ(){
		
		double z = 0.0;
		
		if(this.getZ() != 0)
			this.setZ(0);
		
		for(int i=0; i<this.childCount(); i++){
			
			Node child_i = super.getChild(i);
			
			int child_label = child_i.castToAND().getLabel();
			
			z = z + this.getWeight(child_label) * child_i.getZ();
		}
		
		this.setZ(z);
		return z;
	}
	
	void normalize(){
		
		double z =  0.0;
		
		for(int i=0; i<this.childCount(); i++){
			
			Node child = this.getChild(i);
			int child_label = child.getLabel();
			
			z = z + this.getWeight(child_label);
		}
		
		for(int i=0; i<this.weights.length; i++)
			this.weights[i] =  this.weights[i]/z;
	}
	
	
	void print(){
		
		DecimalFormat df = new DecimalFormat("0.0###E00");
		System.out.print("("+this.getID()+":"+this.getVariable().getLabel()+":"+this.getLevel()+")");			
		System.out.print(" Z = "+df.format(this.getZ()));
		System.out.println(" Weights"+Arrays.toString(this.getWeights()));	
	}
	
	
	Node getEvidChild(int label){
	
		for(int i=0; i<this.childCount(); i++){
			
			Node child_i = this.children.get(i);
			
			int child_label = child_i.getLabel();
			
			if(child_label == label)
				return child_i;
			
		}
	
		//Util.halt("Exception in ORNode.getChild(int) : node does not have a child with label "+label);
		return null;
	}
	
	/**
	 * Compares this node to the given OR node n. They are equal if they have
	 * 1. the same number of children
	 * 2. the same weights over the same edges
	 * 3. they are defined over the same variable/feature
	 * @param n
	 * @return
	 */
	boolean compare(ORNode n){
		
				
		if(this.getVariable().getLabel() != n.getVariable().getLabel())
			return false;
		
		if(this.getZ() != n.getZ())
			return false;
		
		if(this.childCount() != n.childCount())
			return false;
		
		for(int i=0; i<this.childCount(); i++)
			if(this.getWeight(i) != n.getWeight(i))
				return false;
		
		for(int i=0; i<this.childCount(); i++)
			if(!this.getChild(i).equals(n.getChild(i)))
				return false;
		
		return true;
	}
	
	
	boolean hasUniformWeights(){
		
		if(this.childCount() != this.getVariable().domainSize())
			return false;
		else	
			return true;
		
	}
	
	boolean hasUniformWeights(double error){
		
		double uniform_weight = (double)1/this.getVariable().domainSize();
		
		//System.out.println("Uniform weight : "+uniform_weight);
		
		//compute the total mean squared error between the uniform_weight and
		//each edge weight.
		
		double mse_ = 0.0;		
		double weight_0 = this.getWeight(0);
				
		//if 
		if(this.childCount() != this.getVariable().domainSize())
			return false;
				
		for(int i=0; i<this.childCount(); i++){
			
			mse_ = mse_ + Math.pow((this.getWeight(i) - uniform_weight), 2);
			
		}
		
		mse_ = (mse_)/this.getVariable().domainSize();
		
		//System.out.println("Mean squared error : "+mse_);
		
		if(mse_ > error)
			return false;
		
		return true;
	}
	
	
	boolean hasCommonDescendants(){
		
		if(this.leafCount() == this.childCount())
			return true;
		
		else{
			
			//compare the set of children of the first AND child to all others set of children
			
			ArrayList<Node> childSet_0 = this.getChild(0).getChildren();
			
			for(int i=1; i<this.childCount(); i++){
				
				ArrayList<Node> childSet_i = this.getChild(i).getChildren();
				
				
				
				if(!childSet_i.equals(childSet_0))
					return false;
			}
			
			return true;
		}
	}//end of hasCommonDescendants()
	
	
	Distribution getDistribution(){
		
		Distribution D = new Distribution();
		
		D.addToScope(this.getVariable());
		
		D.initializeParameters();
		
		D.addParameters(this.getWeights());
		
		return D;
	}
	
	/*boolean compare(ORNode n, double eBound){
		
		//System.out.println("Error bound : "+eBound);
		
		//Two OR nodes are different if 
		
		//1. if they have different labels
		if(this.getLabel() != n.getLabel())
			return false;	
					
		//2. if they have different number of children
		if(this.childCount() != n.childCount())
			return false;
			
		else{//in case they have the same number of children, check if the children are the same
			
			//3. if their corresponding AND children are different
			for(int i=0; i<this.childCount(); i++)
				if(!this.getChild(i).equals(n.getChild(i)))
					return false;	
			
		}		
		
		//4. if their corresponding weights are different - relaxed given a error bound. 
		for(int i=0; i<this.childCount(); i++){
			
			//System.out.println("Weight difference : "+Math.abs(this.getWeight(i) - n.getWeight(i)));
			if(Math.abs(this.getWeight(i) - n.getWeight(i)) > 0.00001)
				//if(this.getWeight(i) != n.getWeight(i))
					return false;			
		}	
		
		//System.out.println("The OR nodes are the SAME!");
		return true;
	}*/
	
	void average(ORNode n){
		
		for(int i=0; i<this.childCount(); i++){
			
			//System.out.println("Weight difference : "+Math.abs(this.getWeight(i) - n.getWeight(i)));
			this.setWeight(i, (this.getWeight(i)+n.getWeight(i))/2);			
		}
		
	}
	
	/**
	 * Decides whether two OR nodes are unifiable
	 * @param node
	 * @return
	 */
	boolean isUnifiable(ORNode node, double eBound){
		
		//
		//1. if they have different labels
		if(this.getLabel() != node.getLabel())
			return false;	
							
		//2. if they have different number of children
		if(this.childCount() != node.childCount())
			return false;
					
		else{//in case they have the same number of children, check if the children are the same
			//3. if their corresponding AND children are different
			for(int i=0; i<this.childCount(); i++)
				if(!this.getChild(i).equals(node.getChild(i)))
					return false;	
					
		}		
				
		//4. if their corresponding weights are different - relaxed given a error bound. 
		for(int i=0; i<this.childCount(); i++){
					
			//System.out.println("Weight difference : "+Math.abs(this.getWeight(i) - n.getWeight(i)));
			if(Math.abs(this.getWeight(i) - node.getWeight(i)) > eBound)
				return false;			
			
		}	
		
		if(Math.abs(this.getZ() - node.getZ()) > eBound)
			return false;
				
		//System.out.println("The OR nodes are the SAME!");
		return true;
		
		
	}
	
	/**
	 * Creates a new OR node which is the result of merging two other OR nodes
	 * @param node
	 * @return
	 */
	
	ORNode merge(ORNode node){
		
		ORNode newNode = new ORNode();
		
		newNode.setLevel(this.getLevel());
		
		newNode.setVariable(this.getVariable());
		
		for(int i=0; i<this.childCount(); i++){
			
			newNode.setWeight(i, (this.getWeight(i)+node.getWeight(i))/2);
			
			newNode.addChild(this.getChild(i));			
		}
		
		for(int i=0; i<this.parentCount(); i++){
			
			newNode.addParent(this.getParent(i));
		}
		
		for(int i=0; i<node.parentCount(); i++){
			
			newNode.addParent(node.getParent(i));
		}
		
		
		newNode.computeZ();
		
		return newNode;
	}
}
