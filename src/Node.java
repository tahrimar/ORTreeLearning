import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;




public class Node extends Tree implements Comparator<Node>{

	boolean leaf;			//indicates whether the node is a terminal node
	boolean root;
	boolean pruned;
	boolean merged;
	
	int id;					//the unique identifier of a node
	int level;				//the level at which this node resides
	
	double z;	
	
	ArrayList<Node> children;
	ArrayList<Node> parents;	
	
		
	Node(){
		
		
	}
	
	void setAsMerged(){
		
		this.merged = true;
	}
	
	boolean isMerged(){
		
		return this.merged;
	}
	
	void setAsPruned(){
		
		this.pruned = true;
	}
	
	boolean isPruned(){
		
		return this.pruned;
	}
	
	
	
	void setID(int id_){
		
		this.id = id_;
	}
	
	int getID(){
		
		return this.id;
	}
	
	void setLevel(int level_){
		
		this.level = level_;
	}
	
	int getLevel(){
		
		return this.level;
	}
	
	
	/**
	 * returns the local z of this node
	 * @return
	 */
	double getZ(){
		
		return this.z;
	}
	
	/**
	 * sets the initial value of the node's z
	 * @param z
	 */
	void setZ(double z_){
		
		this.z = z_;
	}	
	
	
	void addChildren(ArrayList<Node> children_){
		
		if(this.children == null)
			this.children = new ArrayList<Node>(children_);
		
		else{
			
			children_.removeAll(this.getChildren());
			this.getChildren().addAll(children_);
		}
		
	}
	/**
	 * add an AND child
	 * @param child
	 */
	void addChild(Node child){
		
		if(this.children == null)
			this.children = new ArrayList<Node>();
		
		if(!this.children.contains(child))
			this.children.add(child);
		
		//whenever a child is added, set this node to be the parent of the child		
		
		child.addParent(this);
		
		if(this.isLeaf())
			this.resetLeaf();
		this.computeZ();
	}
	/**
	 * Delete the i-th indexed child
	 * @param i
	 */
	void deleteChild(int i){
		
		if(this.children == null || this.children.size() < i){
			
			//do nothing
		}
		
		else{
			
			this.children.remove(i);
			//if the number of children decreses to 0 then set this node to a leaf node
			if(this.childCount() == 0)
				this.setToLeaf();
		}	
	}
	
	/**
	 * Deletes the child from and recompute the partition function
	 * @param child
	 */
	void deleteChild(Node child){
		
		if(this.children == null || this.children.size() == 0){
			
			//do nothing
		}
		
		else if(!this.getChildren().contains(child))
			return;
		
		else{
		
			this.getChildren().remove(child);
			if(this.childCount() == 0)
				this.setToLeaf();			
			//recompute the local partition function of this node
			child.deleteParent(this);
			this.computeZ();
		}	
		
	}
	
	/**
	 * get the i-th child
	 * @param i
	 * @return
	 */
	Node getChild(int i){
		
		if(this.children == null){
			
			return null;
		}
		
		else if(i <= this.children.size())
			return this.children.get(i);
		
		else{
			
			Util.halt("Exception in Node.getChild(int) : array index out of bounds!");
			return null;
		}
	}
	
	/**
	 * confirms whether an i-th child exists
	 * @param i
	 * @return
	 */
	boolean childExists(int i){
		
		if(this.getChild(i) != null)
			return true;
		
		else
			return false;
	}
	
	/**
	 * returns all the AND children
	 * @return
	 */
	ArrayList<Node> getChildren(){
		
		return this.children;
	}
	
	/**
	 * counts the number of children of this node
	 * @return
	 */
	int childCount(){
		
		if(this.children == null || this.children.size() == 0){
			
			return 0;
		}
		
		else
			return this.children.size();
	}
	
	
	void sortChildren(){
		
		Collections.sort(this.getChildren(), new Node());
	}
	
	int leafCount(){
		
		int numberOfLeafChildren_ = 0;
		
		for(int i=0; i<this.childCount(); i++){
			
			if(this.getChild(i).isLeaf())
				numberOfLeafChildren_++;
		}
		
		return numberOfLeafChildren_;
	}
	
	
	/**
	 * counts the number of parents of this node
	 * @return
	 */
	
	
	int parentCount(){
		
		if(this.parents == null || this.parents.size() == 0){
			
			return 0;
		}
		
		else
			return this.parents.size();
	}
	
	/**
	 * add AND parent
	 * @param parent
	 */
	void addParent(Node parent){
		
		if(this.parents == null)
			this.parents = new ArrayList<Node>();
		
		if(!this.parents.contains(parent)){
			this.parents.add(parent);
			
			if(!parent.getChildren().contains(this))
				parent.getChildren().add(this);
		}	
	}
	
	void addParents(ArrayList<Node> parents_){
		
		if(this.parents == null){
			this.parents = new ArrayList<Node>();
		}	
		
		for(int i=0; i<parents_.size(); i++){
			
			this.addParent(parents_.get(i));
		}
		
	}
	
	void deleteParent(Node parent){
		
		if(this.parents == null || this.parents.size() == 0){
			
			//Util.halt("Exception in Node.deleteParent() : null parents!");
			return;
		}
		
		else if(!this.getParents().contains(parent))
			return;
		
		else{
			
			this.getParents().remove(parent);
			parent.deleteChild(this);
		}
			
		
	}
	
	void deleteAllParents(){
		
		for(int i=0; i<this.parentCount(); i++){
			
			this.deleteParent(this.getParent(i));
		}
	}
	
	/**
	 * returns the i-th parent
	 * @param i
	 * @return
	 */
	Node getParent(int i){
		
		if(this.parents == null || this.parents.size()<i){
			
			System.out.println("Exception in getParent() : index out of bounds!");
			Util.halt();
		}
		
		return this.parents.get(i);
	}
	
	/**
	 * return the first parent by default
	 * @return
	 */
	Node getParent(){
		
		if(this.parents != null)
			return this.parents.get(0);
		else
			return null;
	}
	
	
	ArrayList<Node> getParents(){
		
		if(this.parents == null)
			return new ArrayList<Node>();
		
		else
			return this.parents;
	}
	
	
	/**
	 * returns the status of a terminal node
	 * @return
	 */
	boolean isLeaf(){
		
		if(this.leaf)
			return true;
		
		else
			return false;
	}
	
	void setAsRoot(){
		
		this.root = true;
		
	}
	
	boolean isRoot(){
		
		return this.root;
	}
	
	/**
	 * sets the leaf status flag of this node
	 */
	void setToLeaf(){
		
		this.leaf = true;
	}
	
	void resetLeaf(){
		
		this.leaf = false;
	}
	
	
	/**
	 * sets the local data of this node
	 * @param dataset_
	 */
	void setDataset(Dataset dataset_){
		
		//this.localData = dataset_;
	}
	
	
	/**
	 * returns the local dataset of this node
	 * @return
	 */
	/*Dataset getDataset(){
		
		return this.localData;
	}*/
	
	
	/**
	 * Given a set of nodes, this method returns the labels of the nodes
	 * @param nodes
	 * @return
	 */
	static ArrayList<Integer> getLabels(ArrayList<Node> nodes){
		
		ArrayList<Integer> labels = new ArrayList<Integer>();
		
		for(int i=0; i<nodes.size(); i++){
			
			if(nodes.get(i).getClass() == ORNode.class){
				
				Variable v = nodes.get(i).castToOR().getVariable();
				labels.add(v.getLabel());				
			}	
			
			else{
				
				labels.add(nodes.get(i).castToAND().getLabel());
			}
		}
		
		return labels;
	}
	
	/**
	 * converts the generic type node to an or node
	 * @return
	 */
	ORNode castToOR(){
		
		return (ORNode)this;
	}
	
	/**
	 * converts the generic type node to an and node
	 * @return
	 */
	ANDNode castToAND(){
		
		return (ANDNode)this;
	}
	
	boolean isEvidence(){
		
		if(this.getClass() == ORNode.class){
			
			ORNode nOR = this.castToOR();
			if(nOR.getVariable().getValue() != -1)
				return true;
			
			else
				return false;
		}
		
		else
			return false;
	}
	
	/**
	 * Confirms whether this is an OR node
	 * @return
	 */
	boolean isOR(){
		
		if(this.getClass() == ORNode.class)
			return true;
		
		else
			return false;
	}
	
	/**
	 * confirms whether this is an AND node
	 * @return
	 */
	boolean isAND(){
		
		if(this.getClass() == ANDNode.class)
			return true;
		
		else
			return false;
	}
	
	double getWeight(int i){
		
		if(this.isOR())
			return this.castToOR().getWeight(i);
		
		else{
			
			Util.halt("Exception in Node.getWeight(int) : Node is not an OR node");
			return 1.0;		
		}
			
	}
	
	void setWeight(int i, double weight){
		
		if(this.isOR())
			this.castToOR().setWeight(i, weight);
		
		else{
			
			Util.halt("Exception in Node.getWeight(int) : Node is not an OR node");
					
		}
		
		
	}
	
	int getLabel(){
		
		if(this.isOR())
			return this.castToOR().getVariable().getLabel();
		
		else
			return this.castToAND().getLabel();
		
	}
	
	/**
	 * Compares this node to the given node 'n'. 
	 * @param n
	 * @return
	 */
	boolean compare(Node n){
		
		//two nodes are NOT equal if		
		//1. they are of different types 
		if(this.isOR() && n.isAND() || this.isAND() && n.isOR())
			return false;
		
		//2. they have different labels
		if(this.getLabel() != n.getLabel())
			return false;	
					
		//if they have different number of children
		if(this.childCount() != n.childCount())
			return false;
			
		else{//in case they have the same number of children, check if the children are the same
			
			//if their corresponding children are are different
			/*for(int i=0; i<this.childCount(); i++)
				if(!this.getChild(i).equals(n.getChild(i)))
					return false;*/	
			
		}		
		//if their local Z's are different - can be relaxed	
		//if(this.getZ() != n.getZ())
			//return false;	
		
		//in case of OR nodes, if all the above conditions
		//are true then the nodes are different if their
		//corresponding weights are different - can be relaxed. 
		/*if(this.isOR() && n.isOR()){
					
			for(int i=0; i<this.childCount(); i++)
				if(this.getWeight(i) != n.getWeight(i))
				//if(Math.abs(this.getWeight(i) - n.getWeight(i)) != 0)
					return false;			
		}*/		
		
		//in case of AND nodes, test if the nodes have the same set of don't care
		//variables
		
		if(this.isAND() && n.isAND()){
			
			ANDNode n_1 = this.castToAND();
			ANDNode n_2 = n.castToAND();
			
			if(!n_1.compare(n_2))
				return false;
			
		}		
		return true;
	}
	
	
	boolean isUnifiable(Node n, double eBound){
		
		if((this.isOR() && n.isAND()) || (this.isAND() && n.isOR()))
			return false;
		
		else if(this.isAND() && n.isAND()){
			
			//System.out.println("Comparing and nodes...");
			ANDNode thisAND = this.castToAND();
			ANDNode nAND = n.castToAND();
			
			return thisAND.isUnifiable(nAND, eBound);
			
		}	
		
		else if (this.isOR() && n.isOR()){
			
			//System.out.println("Comparing OR nodes :D...Error bound : "+eBound);
			//return this.castToOR().compare(n.castToOR(), eBound);
			ORNode thisOR = this.castToOR();
			ORNode nOR = n.castToOR();
			
			return thisOR.isUnifiable(nOR, eBound);
		}	
		
		
		else
			return false;	
			
	}
	
	/**
	 * Prints out the information about the node
	 */
	void print(){
		
		if(this.isOR())
			this.castToOR().print();
		
		else if(this.isAND())
			this.castToAND().print();
		
		else{	//print a generic node
			
			System.out.println("{"+this.getID()+":"+this.getLabel()+"}");
		}
	}
	
	/**
	 * Computes the local z
	 * @return
	 */
	
	double computeZ(){
		
		if(this.isOR())
			return this.castToOR().computeZ();
		
		else
			return this.castToAND().computeZ();
	}
	
	/**
	 * Returns the descendants of a node at the (current + i-th) level. For example, 
	 * if the current level of this node is 1 and i is given to be 2, then the descendants
	 * of level 1+2 = 3 will be returned.
	 * @param n				-	node whose descendants are sought
	 * @param level			-	the level offset	
	 * @param descendants	-	list in which the descendants are collected; initially empty
	 * @return
	 */
	static ArrayList<Node> getDescendants(Node n, int levelOffset, ArrayList<Node> descendants){
		
		if(levelOffset == 0){
			
			descendants.add(n);
			return descendants;
		}
		
		for(int i=0; i<n.childCount(); i++){
			
			descendants = getDescendants(n.getChild(i), levelOffset-1, descendants);
		}
		
		return descendants;
	}
	
	/**
	 * shifts up the level of this node by offset amount 
	 */
	void shiftUp(int offset, Tree tree){
		
		tree.deleteFromLevel(this.getLevel(), this);
		tree.addToLevel(this.getLevel()-offset, this);
		this.setLevel(this.getLevel()-offset);
		
		
		if(this.isLeaf())
			return;
		
		else
			for(int i=0; i<this.childCount(); i++)				
				this.getChild(i).shiftUp(offset, tree);
		
	}

	void type(){
		
		if(this.isOR())
			System.out.println("Node is an OR node");
		
		else if(this.isAND())
			System.out.println("Node is an AND node");
		
		else
			System.out.println("Undefined node type...");
	}
	
	/**
	 * Compares the labels of two same typed nodes - used for sorting 
	 * @param n1
	 * @param n2
	 * @return
	 */
	
	public int compare(Node n1, Node n2) {
		
		if(n1.isOR() && n2.isOR())			
			return n1.castToOR().getVariable().getLabel() - n2.castToOR().getVariable().getLabel();
		
		else if(n1.isAND() && n2.isAND())						
			return n1.castToAND().getLabel() - n2.castToAND().getLabel();
		
		else{
			
			System.out.println("Exception in Node.compare() : Node type(s) is undefined! ");
			Util.halt();
			return -1;
		}
	}
	
	Node merge(Node node){
		
		if(this.isOR() && node.isAND()){
			Util.halt("Exception in Node.merge() : incompatible types!");
			return null;
		}	
		
		else if(this.isAND() && node.isOR()){
			Util.halt("Exception in Node.merge() : incompatible types!");
			return null;
		}	
		
		else{
			
			ORNode mergedOR = null;
			ANDNode mergedAND;
			
			if(this.isOR()){
				
				mergedOR = this.castToOR().merge(node.castToOR());
				return mergedOR;
			}
			else{
				
				mergedAND = this.castToAND().merge(node.castToAND());
				return mergedAND;
			}		
		
		}
			
	}
	
}
