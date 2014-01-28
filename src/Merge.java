import java.util.ArrayList;

/**
 *This class provides methods to perform merging on AND/OR trees 
 */

/**
 * @author txr110830
 *
 */
public class Merge {

	Merge(){
		
		
	}
	
	
	static Tree mergeBottomUp(Tree tree){
			
		//scan through each level to search for subtrees having similar distribution

		int maxLevel = tree.levelCount()-1;
		
		for(int i = maxLevel; i>=0; i--){
			
			System.out.println("Number of nodes before merging : "+tree.getLevel(i).size());
			mergeLevel(i, tree);
			System.out.println("Number of nodes after merging : "+tree.getLevel(i).size());
		}		
		
		return tree;

	}
	
	static void mergeLevel(int L, Tree tree){
		
		System.out.println("Merging Level : "+L);
		
		ArrayList<Node> levelNodes = tree.getLevel(L);
		
		
		for(int i=0; i<levelNodes.size(); i++){
			
			Node node_i = levelNodes.get(i);
			
			//System.out.println("i-th tree...");
			//node_i.print();
			
			for(int j=i+1; j<levelNodes.size();){				
				
				Node node_j = levelNodes.get(j);
				//System.out.println("j-th tree");
				//node_j.print();
				//Util.halt();
				
				if(node_i.compare(node_j)){
					
					//System.out.println("Merging nodes...");
					
					/*node_i.print();
					node_j.print();*/
					
					//merge
					
					//set node_j's parent to point to node_i
					
					Node parent_j = node_j.getParent();
					
					parent_j.addChild(node_i);
					tree.deleteFromLevel(L, node_j);
					//delete j-th node from the parent and from the level
					parent_j.deleteChild(node_j);
					
					//add parent_j as a new parent of node_i
					
					node_i.addParent(parent_j);				
					
				}
				
				else
					j++;
			}
		}		
	}

	
	Node mergeApproximate(ANDORTree tree, double errBound){
		
		return tree.getRoot();
		
	}
	
	static ORTree merge(ORTree tree){
		
		ArrayList<Variable> order = null;
		
		if(tree.is_sFlag()){
		
			order = tree.getOrder();
			
			for(int i=0; i<order.size(); i++)
				System.out.println("Level of "+order.get(i).getLabel()+" "+(i*2));	
			
			for(int i = order.size()-2; i>=0; i--){
				
				ArrayList<Node> L_i = tree.getLevel(i*2);
				
				if(L_i == null)
					continue;
				
				System.out.println("Level "+(i*2)+" nodes : "+Node.getLabels(L_i));
				
				int j = i+1;
				
				ArrayList<Node> L_j = tree.getLevel(j);
				
				System.out.println("Level "+(j*2)+" nodes : "+Node.getLabels(L_j));
				
				//shift level of nodes accordingly
				
				for(int k=0; k<L_i.size(); k++){
					
					Node n_i = L_i.get(k);
					
					
					for(int n=0; n<n_i.childCount(); n++){
						
						//
						
						
					}
				}
				
			}			
		}	
		
		else
			System.out.println("Order based on max entropy - No merging was performed!");//max entropy order
		
		return tree;
			
	}

}
