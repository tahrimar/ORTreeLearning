/**
 * A generic tree structure
 */
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;




public class Tree {

	boolean empty;
	
	int nodeCount;							//keeps track of the number of nodes in the tree
	static ANDNode root;								//root

	ArrayList<Variable> scope;				//the scope of the tree
	Map<Integer, ArrayList<Node>> levels;	//levels of the tree
	
	Tree(){
	
	}
	
	
	/**
	 * This method learns an and/or tree from a given set of data
	 */
	
	Node getRoot(){
		
		return this.root;
	}//end of getRoot()
	
	void setRoot(ANDNode root_){
		
		this.root = root_;
	}
	
	/**
	 * indicates whether the tree is empty - no nodes exist
	 * @return
	 */
	boolean isEmpty(){
		
		return this.empty;
	}
	
	/**
	 * 
	 */
	void setAsEmpty(){
		
		this.empty = true;
	}
	
	/**
	 * Confirms whether the given level contains the given node
	 * @param level
	 * @param node
	 * @return
	 */
	boolean levelContains(int level, Node node){
		
		if(this.levels == null)
			return false;
		
		else{
			
			if(this.levels.get(level).contains(node))
				return true;
			
			else
				return false;
		}
	
	}//end of levelContains()
	
	/**
	 * returns the list of nodes at the i-th level
	 * @param i
	 * @return
	 */
	ArrayList<Node> getLevel(int i){
		
		if(this.levels == null){ 
			
			return null;
		}	
			
		else if(!this.levels.containsKey(i)){
						
			return null;			
		}	
			
		else
			return this.levels.get(i);
	}//end of getLevel()
	
	/**
	 * Confirms whether the given level exists or not
	 * @param level
	 * @return
	 */
	boolean levelExists(int level){
		
		if(this.getLevel(level) == null)
			return false;
		
		else
			return true;
		
	}
	
	/**
	 * creates the corresponding i-thlevel
	 * @param level
	 */
	void createLevel(int i){
		
		if(this.levels == null){
			
			this.levels = new LinkedHashMap<Integer, ArrayList<Node>>();
		}
		
		this.levels.put(i, new ArrayList<Node>());
	}//end of createLevel()
	
	void deleteLevel(int i){
		
		if(this.levels != null){
		
			Iterator it = this.levels.entrySet().iterator();
			
			while(it.hasNext()){
				
				Entry<Integer, ArrayList<Node>> entry = (Entry<Integer, ArrayList<Node>>) it.next();
				
				if(entry.getKey() == i){
					it.remove();
					return;
				}	
				
			}
		
		}
			
		
	}
	
	/**
	 * Adds node n to the i-th level
	 * @param level
	 * @param node
	 */
	void addToLevel(int i, Node n){
		
		if(this.getLevel(i) == null){
						
			this.createLevel(i);
			this.getLevel(i).add(n);
		}	
		
		else if(!this.levelContains(i, n)){
						
			this.getLevel(i).add(n);
			
		}	
		
		else{
			
			//System.out.println("Level "+i+" already contains node ");
			//n.print();
		}	
		
		return;
		
	}//end of addToLevel()
	
	/**
	 * Delete the node from the list of nodes at the given level
	 * @param level
	 * @param node
	 */
	void deleteFromLevel(int level, Node node){
		
		if(!this.levelExists(level))
			
			return;//Util.halt("Exception in Tree.deleteFromLevel(int, Node) : level "+level+" does not exist!");
		
		else{
			
			this.getLevel(level).remove(node);
			//if the number of nodes in the level becomes 0, then delete the level
			if(this.getLevel(level).size() == 0){
				
				System.out.println("Deleting level ... "+level);
				
				//this.levels.remove(this.getLevel(level));
				this.deleteLevel(level);
				
				System.out.println("Level count : "+this.levels.size());
			}	
		}
		
	}//end of deleteFromLevel()
	
	/**
	 * returns the number of levels in the tree
	 * @return
	 */
	int levelCount(){
		
		if(this.levels == null || this.levels.size() == 0)
			return 0;
		
		else
			return this.levels.size();
	
	}//end of levelCount()
	

	/**
	 * Iterate through the levels of the tree
	 */
	
	void printLevels(String printMsg){
		
		System.out.print(printMsg);		
		
		System.out.println("Number of levels "+this.levelCount());
		
		for(int i=0; i<this.levelCount(); i++){
			
			ArrayList<Node> levelNodes = this.getLevel(i);
			
			
			
			System.out.println("Level : "+i+" # = "+levelNodes.size()+" Nodes : "+Node.getLabels(levelNodes));
			
			
			
		}		
		
	}//end of printLevels()

	void updateLevelInfo(Node node, int level){
		
		node.setLevel(level);
		
		if(node.isLeaf())
			return;
		
		for(int i=0; i<node.childCount(); i++)
			this.updateLevelInfo(node.getChild(i), level+1);
		
	}
	/**
	 * retrieves the scope variables of this tree
	 * @return
	 */
	
	ArrayList<Variable> getScope(){
		
		return this.scope;
	}//end of getScope()
	
	void setScope(ArrayList<Variable> scope_){
		
		if(this.getScope() == null)
			this.scope = new ArrayList<Variable>();
		
		if(!this.getScope().containsAll(scope_))
			this.scope.addAll(scope_);
	
		
	}
	
	/**
	 * adds a new scope variable to the list of variables
	 * @param var_
	 */
	void addToScope(Variable var_){
		
		if(this.getScope() == null)
			this.scope = new ArrayList<Variable>();
		
		if(!this.getScope().contains(var_))
			this.scope.add(var_);
	
	} //end of addToScope()
	
	/**
	 * returns the number of scope variables
	 * @return
	 */
	int scopeCount(){
		
		if(this.getScope() == null || this.getScope().size() == 0)
			return 0;
		else
			return this.getScope().size();
	
	}//end of scopeCount()
	
	/**
	 * returns the i-th scope variable
	 * @param i
	 * @return
	 */
	Variable getScope(int i){
		
		if(this.getScope() == null || this.scopeCount() < i){
			
			Util.halt("Exception in getScopeVar(int) : index out of bounds!");
			return null;
		}
		
		else
			return this.getScope().get(i);
			
	}//end of getScope()
	
	Variable getScope(Variable v){
		
		if(this.getScope() == null || this.scopeCount() == 0){
			
			Util.halt("Exception in getScopeVar(int) : index out of bounds!");
			return null;
		}
		
		else
			return this.getScope().get(this.getScope().indexOf(v));
		
	}//end of getScope()
	
	/**
	 * Given the root node of an AND/OR tree, this method prints out
	 * the tree
	 * @param node
	 * @param level
	 */
	
	static void print(Node node, int level, String outputMsg){
				
		System.out.print(outputMsg);
		
		for(int i=0; i<level; i++)
			System.out.print("| ");
		
		node.print();
		
		if(node.isLeaf())
			return;
		
		else
			for(int i=0; i<node.childCount(); i++)				
				print(node.getChild(i), level+1, "");
			
		
	}//end of print()	
	
	static void print(Node node, int level){
		
		for(int i=0; i<level; i++)
			System.out.print("| ");
		
		//node.print();
		
		if(node.isLeaf())
			return;
		
		if(node.isOR()){
			System.out.println(node.getLabel());
			level = level+1;
		}	
		
		for(int i=0; i<node.childCount(); i++)				
			print(node.getChild(i), level);			
		
		
	}
	
	//compares two trees recursively and returns the decision
	boolean compare(Tree T){
		
		return false;
		
	}//end of compare()
	
	
	void convertToDot(GraphViz gv, Node n, ArrayList<Integer> visitedNodes){
		
		//DecimalFormat df = new DecimalFormat("0.0##E00");
		DecimalFormat df = new DecimalFormat("0.000");
					
		if(visitedNodes.contains(n.getID()))
			return;
		
		visitedNodes.add(n.getID());
		
		if(n.isAND()){
			
			String labelAND = "";
			
			if(n.isLeaf())
				labelAND = "\""+n.getLabel()+":"+n.getLevel()+":"+n.castToAND().getSampleCount()+"\"";
				
			else
				labelAND = "\""+n.getLabel()+":"+n.getLevel()+"\"";
			
			if(n.isRoot()){
				
				gv.addln(n.getID()+" [shape=box, label = \"root\" ]");
			}
			//gv.addln(n.getID()+" [shape=box, width=0.35, height=0.35, label="+n.getLabel()+"]");
			else{
				 
				if(n.isMerged())
					gv.addln(n.getID()+" [style = filled, shape=box, label="+labelAND+", fillcolor = turquoise]");
				
				else
					gv.addln(n.getID()+" [shape=box, label="+labelAND+"]");
				 
			}	 
			
			if(n.isLeaf())
				return;		
		}
		
		else{
			
			String labelOR = "\""+n.getLabel()+":"+n.getLevel()+"\"";
			
			if(n.isMerged())
			
				gv.addln(n.getID()+" [style = filled, shape=circle, label="+labelOR+", fillcolor = turquoise]");
			
			else
				
				gv.addln(n.getID()+" [shape=circle, label="+labelOR+"]");
		}	
		
		for(int i=0; i<n.childCount(); i++){
			
			if(n.isOR())
				//gv.addln(n.getID()+"--"+n.getChild(i).getID()+"[fontsize = 8, label = "+df.format(n.getWeight(i))+", labelangle=110];");
				gv.addln(n.getID()+"--"+n.getChild(i).getID()+"[label = "+df.format(n.getWeight(i))+", labelangle=110];");
			else
				gv.addln(n.getID()+"--"+n.getChild(i).getID()+";");
			
			convertToDot(gv, n.getChild(i), visitedNodes);		
		}				
						
		
	}
	
	
	void convertToDot(GraphViz gv){
		
		//DecimalFormat df = new DecimalFormat("0.0##E00");
		DecimalFormat df = new DecimalFormat("0.000");
					
		for(int j=1; j<=this.levelCount(); j++){
			
			ArrayList<Node> levelNodes = this.getLevel(j);
		
			for(int i=0; i<levelNodes.size(); i++){
				
				Node n = levelNodes.get(i);
				
				if(n.isAND()){
					
					String labelAND = "\""+n.getLabel()+":"+n.getLevel()+"\"";
					
					if(n.isRoot()){
						
						gv.addln(n.getID()+" [shape=box, label = \"root\" ]");
					}
					//gv.addln(n.getID()+" [shape=box, width=0.35, height=0.35, label="+n.getLabel()+"]");
					else{
						 
						if(n.isMerged() || n.isPruned())
							gv.addln(n.getID()+" [style = filled, shape=box, label="+labelAND+", fillcolor = red]");
						
						else
							gv.addln(n.getID()+" [shape=box, label="+labelAND+"]");
						 
					}			
							
				}
				
				else{
					
					String labelOR = "\""+n.getLabel()+":"+n.getLevel()+"\"";
					
					if(n.isPruned() || n.isMerged())					
						gv.addln(n.getID()+" [style = filled, shape=circle, label="+labelOR+", fillcolor = red]");
					
					else						
						gv.addln(n.getID()+" [shape=circle, label="+labelOR+"]");
				}	
				
				for(int k=0; k<n.childCount(); k++){
				
					if(n.isOR())
						gv.addln(n.getID()+"--"+n.getChild(k).getID()+"[label = "+df.format(n.getWeight(k))+", labelangle=110];");
		
					else
						gv.addln(n.getID()+"--"+n.getChild(k).getID()+";");
					
				}
				
			}
		
		
		}					
		
	}
	
	
	
	//writes the tree in dot format
	void draw(Node node, String fileName, String msg, int type){
		
		System.out.println(msg);
		GraphViz gv = new GraphViz();
	    gv.addln(gv.start_graph("graph"));	    
	    gv.addln("splines=true;");
	    //this.convertToDot(gv, this.getRoot(), new ArrayList<Integer>());
	    
	    this.convertToDot(gv, node, new ArrayList<Integer>());
	    
	   gv.addln(gv.end_graph());
	    
	    try {
	         
			 File dotFile = new File(fileName+".dot");
			 File pdfFile = new File(fileName+".pdf");
			 FileWriter fw = new FileWriter(dotFile);	         
			 
			 fw.write(gv.getDotSource());
			 fw.close();
			
			 System.out.println("Dot file : "+dotFile.getPath());
			 System.out.println("PDF file : "+pdfFile.getPath());
			 
			 Runtime rt = Runtime.getRuntime();
			 String[] args = {"dot", "-T"+"pdf", dotFile.getPath(), "-o", pdfFile.getPath()};
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
	
	/**
	 * Delete subtree rooted at a particular node
	 */
	
	void deleteSubtree(Node node){
		
		if(node.isLeaf()){
			
			//System.out.println("Parent Count : "+node.parentCount());
			
			//Util.halt();
			
			//if(node.parentCount() == 1){
				
				//if(node.getParent().isMerged())
					//return;
				
				
				int level = node.getLevel();
				
				//node.setAsPruned();
				
				this.deleteFromLevel(level, node);			
				
			//}	
			
			return;
		}
		
		else{
			
			for(int i=0; i<node.childCount(); i++){
				
				deleteSubtree(node.getChild(i));
				
				
			}
			
			//if(node.parentCount() == 1){
			
				//if(node.getParent().isMerged())
					//return;
				
				int level = node.getLevel();				
				
				for(int i=0; i<node.childCount(); i++){
					
					node.getChild(i).deleteParent(node);
					
				}
				
				//node.setAsPruned();
				this.deleteFromLevel(level, node);				
			//}	
		}
		
	}
	
	/**
	 * Traverses the tree rooted at the given node and returns the scope of the tree
	 * @param node
	 * @param scope_
	 * @return
	 */
	static ArrayList<Variable> getScope(Node node, ArrayList<Variable> scope_){
		
		if(node.isLeaf()){
			
			if(!scope_.containsAll(node.castToAND().getDontCares()))
				scope_.addAll(node.castToAND().getDontCares());
			
			return scope_;
			
		}	
		
		else if(node.isAND()){
			
			for(int i=0; i<node.childCount(); i++){
				
				getScope(node.getChild(i), scope_);
			}
			
			if(!scope_.containsAll(node.castToAND().getDontCares()))
				scope_.addAll(node.castToAND().getDontCares());
			
			return scope_;
		}
		
		else{
			
			if(!scope_.contains(node.castToOR().getVariable()))
				scope_.add(node.castToOR().getVariable());
			
			for(int i=0; i<node.childCount(); i++)
				getScope(node.getChild(i), scope_);
				
			return scope_;	
		}
	}
	
	void deleteNode(Node n){
		
		
		for(int i=0; i<n.parentCount(); i++){
			
			Node parent_i = n.getParent(i);
			
			parent_i.deleteChild(n);
			
			parent_i.computeZ();
			
			
		}
		
		for(int i=0; i<n.childCount(); i++){
			
			Node child_i = n.getChild(i);
			
			child_i.deleteParent(n);
		}
		
		this.deleteFromLevel(n.getLevel(), n);
	}
	
	
	/**
	 * replaces node n1 by node n2
	 * @param n1
	 * @param n2
	 */
	void replace(Node n1, Node n2){
		
		//this.print(n1, 0, "before replacement");
		
		for(int i=0; i<n1.parentCount(); i++){
			
			Node parent_i = n1.getParent(i);
			
			parent_i.addChild(n2);
			
			parent_i.deleteChild(n1);
			
			parent_i.computeZ();
		}
		
	
		//this.print(n2.getParent(), 0, "replaced");
		
		//Util.halt();
		
	}
	
}
