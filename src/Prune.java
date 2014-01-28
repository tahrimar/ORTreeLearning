import java.util.ArrayList;
import java.util.Map.Entry;

/**
 * This class provides methods to prune an AND/OR or an OR tree based on various pruning heuristics
 */

/**
 * @author txr110830
 *
 */
public class Prune {

	

	Prune(){
		
		
	}
	
	/**
	 * This method searches for subtrees that induce a uniform distribution and prunes it
	 * @param tree
	 */
	static ORTree prune(ORTree tree){
		
		//scan through each level to search for subtrees having uniform distribution
		
		int maxLevel = tree.levelCount()-1;
		
		/*
		 * 
		for(int i = maxLevel; i>=0; i--){
			
			pruneLevel(i, tree);
		
		}
		
		*/
		//return tree.getRoot();
		
		for(int i = 0; i<tree.levelCount(); i++){
			
			if(tree.levelExists(i))
				pruneLevel(i, tree);
					
		}
		
		return tree;
		
	}
	
	/**
	 * This method tries to find OR nodes that whose children could be pruned
	 * @param level
	 * @param errBound
	 */
	static void pruneLevel2(int L, ORTree tree){
		
		ArrayList<Node> levelNodes = tree.getLevel(L);//L.getValue();
		
		System.out.println("Number of nodes at level : "+L+" = "+levelNodes.size());
		
		for(int i=0; i<levelNodes.size();){
			
			Node node_i = levelNodes.get(i);
			
					
			if(node_i.isOR()){
				
				//if the k-th child is a leaf and the k-th weight is < error bound
				//then prune the child	
				
				ORNode nOR = node_i.castToOR();
			
				//nOR.print();
				
				//Util.halt();
				
				if(nOR.hasUniformWeights() && nOR.leafCount() == nOR.childCount()){
					
					//System.out.println("deleting node "+nOR.getLabel());
					
					for(int j=0; j<nOR.parentCount(); j++){
						
						Node parent_j = nOR.getParent(j);
						parent_j.deleteChild(nOR);
						((ANDNode)parent_j).addDontCare(nOR.getVariable());
						
					}
					
					//remove the child leaves
					for(int j=0; j<nOR.leafCount(); ){
						
						
						if(nOR.getChild(j).parentCount() == 1){
							tree.deleteFromLevel(nOR.getChild(j).getLevel(), nOR.getChild(j));
						}
						
						nOR.deleteChild(nOR.getChild(j));
					}
					
					if(nOR.isRoot()){
						
						/**
						 * The tree becomes empty deleting the root!
						 */
						
						tree.setAsEmpty();
						//create a new AND node and assign all the variables as don't cares
						
						ANDNode newRoot = new ANDNode();
						newRoot.setAsRoot();
						tree.setRoot(newRoot);
						
					}
					levelNodes.remove(nOR);
				}
				
				//if the node has uniform weights over the edges
				else if(nOR.hasUniformWeights(0.0)){
					
					
					//if all the children of the node has a common descendant
					if(nOR.hasCommonDescendants()){
						
						//then prune
						System.out.println("Pruning OR node "+nOR.getLabel());
						//nOR.print();						
						ArrayList<Node> descendants = Node.getDescendants(nOR, 2, new ArrayList<Node>());
						//remove nOR as a child from its parents and make it a don't care
						for(int j=0; j<nOR.parentCount(); j++){
							
							Node parent_j = nOR.getParent(j);
							parent_j.deleteChild(nOR);
							((ANDNode)parent_j).addDontCare(nOR.getVariable());
							//add the descendants as the new children of the parent
							parent_j.addChildren(descendants);
							
						}
						
						//shift the descendants up 2 levels 
						
						for(int j=0; j<descendants.size(); j++){
							
							descendants.get(j).shiftUp(2, tree);
						}
						//remove nOR from the level
						levelNodes.remove(nOR);								
					}
					
					else
						i++;
					
				}
				
				else
					i++;
			}
			
			else	//if node is an AND node
				i++;		
		
		}//end of for
	}
	
	
	static void pruneLevel(int L, ORTree tree){
		
		ArrayList<Node> levelNodes = tree.getLevel(L);//L.getValue();
		
		//System.out.println("Number of nodes at level : "+L+" = "+levelNodes.size());
		
		for(int i=0; i<levelNodes.size();){
			
			Node node_i = levelNodes.get(i);			
					
			if(node_i.isOR()){
				
				ORNode nOR = node_i.castToOR();
			
				//nOR.print();//
				
				Distribution P = tree.getDistribution(nOR, 1.0, new Distribution());
				
				if(P.isUniform(0.001)){
									
					//System.out.println("deleting subtree rooted at node  "+nOR.getLabel()+" and level "+L);
					
					tree.deleteSubtree(nOR);
					
					if(nOR.isRoot()){
						
						/**
						 * The tree becomes empty deleting the root!
						 */
						
						tree.setAsEmpty();
						//create a new AND node and assign all the variables as don't cares						
						//ANDNode newRoot = new ANDNode();
						//newRoot.setAsRoot();
						//tree.setRoot(newRoot);
						tree.getRoot().castToAND().addDontCares(P.getScope());
					}
										
					else{
						
						for(int j=0; j<nOR.parentCount(); j++){
							
							Node parent_j = nOR.getParent(j);
							parent_j.deleteChild(nOR);
							((ANDNode)parent_j).addDontCares(P.getScope());
							
						}
						
					}
					
					//levelNodes.remove(nOR);
					//delete nodes from level
					//Util.halt();
				}
				
				else
					
					i++;
				
			}
			
			else
				i++;
	}//end of for
	
		//tree.printLevels("Levels after deleting...");
		
}
	
	/**
	 * 
	 * @param tree
	 * @param errBound			- 	the error bound
	 * @return
	 */
	Node pruneApproximate(ANDORTree tree, double errBound){
		
		return tree.getRoot();
	}
	
	
	
	
	

}
