import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * An AND/OR tree is special case of an OR tree where the AND nodes might have
 * multiple children.
 * @author Tahrima
 *
 */
public class ANDORTree extends ORTree{
	
		
	ANDORTree(){
		
		this.root = new ANDNode();
		root.setID(this.nodeCount++);
		root.setAsRoot();
	}		
	/**
	 * The learning part of an AND/OR tree is similar to an OR tree
	 */
	
	/**
	 * given a set of variables and a set of logical potentials (along with a graph)
	 * this method builds an and/or tree
	 */
	
	static ANDNode buildTree(ArrayList<Variable> variables, ArrayList<Potential> potentials, ArrayList<Integer> order){
		
		return root;
		
	}//end of buildTree()
	
	
	Node buildTree(GraphicalModel gm, ArrayList<Integer> order){
		
		if(gm.getGraph() == null)
			gm.constructGraph();
		
		ArrayList<Graph> decomposedGraphs = gm.getGraph().decompose();
		
		System.out.println("Number of decomposed Graphs = "+decomposedGraphs.size());
		
		for(int i=0; i<decomposedGraphs.size(); i++){
			
			Graph graph_i = decomposedGraphs.get(i);
									
			Node child = createOR(gm.getVariables(), gm.getPotentials(), graph_i, new ArrayList<Variable>(), 1, order);
						
			if(this.root == null)
				Util.halt("root is nUll...!");
			
			this.root.addChild(child);
		}
		//root = createOR(gm.getVariables(), gm.getPotentials(), gm.getGraph(), new ArrayList<Variable>(), 0, order);
		this.root.computeZ();
		return this.root;
	}
	
	
	Node createOR(ArrayList<Variable> variables, ArrayList<Potential> potentials, Graph graph, ArrayList<Variable> conditionedVars, int level, ArrayList<Integer> order){
		
		int vertex;
		
		
		double weight;
		double z = 0.0;
		
		Variable var = null;
		ORNode newOR = null;
		ChangeLog log = new ChangeLog();
		
		
		//for a user given order, select the next vertex in order
		
		if(this.is_sFlag()){		//get the next variable from the user defined static ordering
			
			vertex = this.Order.get(nextInOrder++).getLabel();
			
		}
		
		
		else 
			//select max degree vertex from the graph
			vertex = graph.getMaxDegreeVertex();
		
		//get the corresponding variable			
		var = Variable.getVariable(vertex, variables);
		
		newOR = new ORNode();
		newOR.setVariable(var);
		newOR.setID(this.nodeCount++);
		newOR.setLevel(level);
		
		log.recordVertex(vertex, graph.getNeighbors(vertex));
		//deletes vertex from the graph
		graph.deleteVertex(vertex);
		
		//record the instantiated variable
		conditionedVars.add(var);
		
		//get the potentials that mention the variable
		ArrayList<Potential> varPotentials = var.getPotentials();
		
		for(int i=0; i<var.domainSize(); i++){
			
			weight = 1.0;
			var.setValue(i);
			
			for(int j=0; j<varPotentials.size(); j++){
				
				ArrayList<Variable> scope_j = varPotentials.get(j).getScope();
				
				//if the entire scope of potential j is contained in the list of the conditioned variables then
				//instantiate the potential
				if(conditionedVars.containsAll(scope_j)){
					
					weight = weight * varPotentials.get(j).instantiate();
					
				}
			}
			
			
			newOR.setWeight(i, weight);
			//create an AND child
			ANDNode andChild = createAND(variables, potentials, graph, conditionedVars, level+1, order);			
			
			if(andChild != null){
				
				//set the label of the child and set the parent child pointers
				andChild.setLabel(i);
				
				newOR.addChild(andChild);				
				
				z = z + (andChild.getZ() * newOR.getWeight(i));
				
			}			
			
		}
		
		newOR.setZ(z);
		newOR.sortChildren();
		
		//roll back the graph
		graph.addSubgraph(log.getGraph());
		log = null;
		conditionedVars.remove(var);
		this.nextInOrder--;
		this.addToLevel(level, newOR);
		//System.out.println("OR z = "+newOR.getZ());
		return newOR;
		
	}//end of createOR()
	
	
	ANDNode createAND(ArrayList<Variable> variables, ArrayList<Potential> potentials, Graph graph, ArrayList<Variable> conditionedVars, int level, ArrayList<Integer> order){
		
		double z = 1.0;
		ANDNode newAND = new ANDNode();
		newAND.setLevel(level);
		//this.nodeCount++;
		
		if(graph.size() == 0){//create a leaf node
			
			newAND.setToLeaf();		
			newAND.setZ(1.0);
			newAND.setID(this.nodeCount++);
			//System.out.println("Size of leaf AND node : "+Main.getObjectSize(newAND));
			//this.size = this.size + Main.getObjectSize(newAND);
			this.addToLevel(level, newAND);
			return newAND;
			
		}
		
		else{
			
			ArrayList<Graph> componentGraphs = graph.decompose();
			
			for(int i=0; i<componentGraphs.size(); i++){
				
				Node orChild = createOR(variables, potentials, componentGraphs.get(i), conditionedVars, level+1, order);				
				z = z * orChild.getZ();
				newAND.addChild(orChild);																	
						
			}

		}
				
		newAND.sortChildren();
		newAND.setZ(z);
		newAND.setID(this.nodeCount++);
		if(z == 0.0)
			return null;
		
		//newAND.updateZ(z);
		//System.out.println("Size of the new AND node : "+Main.getObjectSize(newAND));
		//this.size = this.size + Main.getObjectSize(newAND);
		//Util.waitForInput();
		this.addToLevel(level, newAND);
		return newAND;
	
	}//end of createAND()
	
	
	/**
	 * Searches for subtrees of uniform probability or nodes of very
	 * low probability and prunes them.
	 * @return
	 */
	void pruneTree(double errBound){
		
		
		
	}//end of prune()
	
	
	void printOrder(){
		
		if(this.is_sFlag())
			System.out.println("Order of Tree : "+Variable.getLabels(this.getOrder()));
		
		else
			System.out.println("Order of Tree : []");
		
	}
	
	Node mergeBottomUP(){
		
		for(int i = this.levelCount(); i>=1; i--){
			
			this.mergeLevel(i);
		}
		
		return this.getRoot();
	}
	
	
	void mergeLevel(int Level){
		
		
		System.out.println("Merging level "+Level);
		
		ArrayList<Node> levelNodes = this.getLevel(Level);
		
		for(int i=0; i<levelNodes.size(); i++){
			
			Node node_i = levelNodes.get(i);
			
			for(int j=i+1; j<levelNodes.size();){				
				
				Node node_j = levelNodes.get(j);
				
				if(node_i.isUnifiable(node_j, 0.0)){
					
					System.out.println("Merging nodes...");
					
					//node_i.print();
					//node_j.print();
					
					//merge
					
					//set node_j's parent to point to node_i
					
					Node parent_j = node_j.getParent();
					
					parent_j.addChild(node_i);
					
					//delete j-th node from the parent and from the level
					parent_j.deleteChild(node_j);
					
					this.deleteFromLevel(Level, node_j);
					
				}
				
				else
					j++;
			}
		}		
	
		//Util.halt();
	}
	
	void convertToDot(GraphViz gv, Node n, ArrayList<Integer> visitedNodes){
		
		//DecimalFormat df = new DecimalFormat("0.0##E00");
		DecimalFormat df = new DecimalFormat("0.000");
		
		char[] alphabet = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
					
		if(visitedNodes.contains(n.getID()))
			return;
		
		visitedNodes.add(n.getID());
		
		if(n.isAND()){
			
			String labelAND = "";
			
			labelAND = "\""+n.getLabel()+"\"";
			
			/*if(n.isLeaf())
				labelAND = "\""+n.getLabel()+":"+n.getLevel()+":"+n.castToAND().getSampleCount()+"\"";
				
			else
				labelAND = "\""+n.getLabel()+":"+n.getLevel()+"\"";*/
			
			if(n.isRoot()){
				
				gv.addln(n.getID()+" [shape=box, label = \"root\" ]");
			}
			//gv.addln(n.getID()+" [shape=box, width=0.35, height=0.35, label="+n.getLabel()+"]");
			else{
				 
				if(n.isMerged())
					//gv.addln(n.getID()+" [style = filled, shape=box, label="+labelAND+", fillcolor = turquoise]");
					gv.addln(n.getID()+" [style = filled, shape=box, label="+labelAND+", fillcolor = turquoise]");
				
				else
					//gv.addln(n.getID()+" [shape=box, label="+labelAND+"]");
					gv.addln(n.getID()+" [shape=square, label="+labelAND+"]");
				 
			}	 
			
			if(n.isLeaf())
				return;		
		}
		
		else{
			char label = alphabet[n.getLabel()];
			
			String labelOR = "\""+label+"\"";
			
			if(n.isMerged())
			
				gv.addln(n.getID()+" [style = filled, shape=circle, label="+labelOR+", fillcolor = turquoise]");
			
			else
				
				gv.addln(n.getID()+" [shape=circle, label="+labelOR+"]");
		}	
		
		for(int i=0; i<n.childCount(); i++){
			
			if(n.isOR())
				//gv.addln(n.getID()+"--"+n.getChild(i).getID()+"[fontsize = 8, label = "+df.format(n.getWeight(i))+", labelangle=110];");
				gv.addln(n.getID()+"--"+n.getChild(i).getID()+"[label = "+(n.getWeight(i))+", labelangle=110];");
			else
				gv.addln(n.getID()+"--"+n.getChild(i).getID()+";");
			
			convertToDot(gv, n.getChild(i), visitedNodes);		
		}				
						
		
	}
	
	
	void convertToDot(GraphViz gv){
		
		//DecimalFormat df = new DecimalFormat("0.0##E00");
		System.out.println("Converting to Dot...");
		
		DecimalFormat df = new DecimalFormat("0.0");
		
		char[] alphabet = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
					
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
					
					char label = alphabet[n.getLabel()];
					
					System.out.println(label);
					
					String labelOR = "\""+label+":"+n.getLevel()+"\"";
					
					if(n.isPruned() || n.isMerged())					
						gv.addln(n.getID()+" [style = filled, shape=circle, label="+labelOR+", fillcolor = red]");
					
					else						
						gv.addln(n.getID()+" [shape=circle, label="+labelOR+"]");
				}	
				
				for(int k=0; k<n.childCount(); k++){
				
					if(n.isOR())
						//gv.addln(n.getID()+"--"+n.getChild(k).getID()+"[label = "+df.format(n.getWeight(k))+", labelangle=110];");
						gv.addln(n.getID()+"--"+n.getChild(k).getID()+"[label = "+n.getWeight(k)+", labelangle=110];");
		
					else
						gv.addln(n.getID()+"--"+n.getChild(k).getID()+";");
					
				}
				
			}
		
		
		}					
		
	}
	
	
	
	//writes the tree in dot format
		
	void draw(Node node, String fileName, String msg, int type){
		
		System.out.println("Drawing AND/OR tree...");
		GraphViz gv = new GraphViz();
	    gv.addln(gv.start_graph("graph"));	    
	    gv.addln("splines=true;");
	    //this.convertToDot(gv, this.getRoot(), new ArrayList<Integer>());
	    
	    convertToDot(gv, node, new ArrayList<Integer>());
	    
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
	
}
