import java.util.ArrayList;


public class ChangeLog {

	Graph subGraph;
	
	ChangeLog(){
		
		
	}
	
	void recordVertex(int v, ArrayList<Integer> neighbors){
		
		//if the graph does not contain the node in the the subgraph then add the node
		//System.out.println("Adjacency List of "+var+" "+adjacencyList.toString());
		
		if(subGraph == null)
			subGraph = new Graph();
		
		if(!this.subGraph.containsVertex(v)){
			
			this.subGraph.addVertex(v, neighbors);			
		}
		
		else{//else update the node's neighborhood list

			if(neighbors == null || neighbors.size() == 0)
				
				System.out.println("Adjacency List of node "+v+" is null");
				
				
			else
				
				this.subGraph.updateNeighbors(v, neighbors);		
		}
			
	}//end of recordVertex
	
	Graph getGraph(){
		
		return this.subGraph;
	}



}
