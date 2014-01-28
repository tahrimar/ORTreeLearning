import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;


public class Graph {

	private Map<Integer, ArrayList<Integer>> adjacencyList;
	
	Graph(){
		
		this.adjacencyList = new LinkedHashMap<Integer, ArrayList<Integer>>();
	}
	
	Graph(Map<Integer, ArrayList<Integer>> adjacencyList_){
		
		this.adjacencyList = new LinkedHashMap<Integer, ArrayList<Integer>>(adjacencyList_);
	}
	
	
	int size(){
		
		return this.adjacencyList.size();
	}
	
	
	
	//adds a new vertex u to the graph with no neighbors
	void addVertex(int u){
		
		if(!this.adjacencyList.containsKey(u))
			this.adjacencyList.put(u, new ArrayList<Integer>());
		
		else{
		
			System.out.println("Exception in addVertex() : The new node "+u+" already exists!");
			Util.waitForInput();
			
		}
	
		
	}//end of addVertex()
	
	//adds a new vertex with neighbors
	void addVertex(int v, ArrayList<Integer> neighbors){
		
		//add the node to the adjacency list if it does not exist	
		if(!this.containsVertex(v))
			this.adjacencyList.put(v, new ArrayList<Integer>(neighbors));
		
		else{
		
			System.out.println("Exception in addVertex(neighbors) : The new vertex "+v+" already exists!");
			Util.waitForInput();
			
		}
		
	}//end of addVertex()
	
	//delete vertex v
	void deleteVertex(int v){
		
		if(!this.adjacencyList.containsKey(v)){
			
			System.out.println("Exception in deleteNode() : The node "+v+" to be deleted does not exist");
			Util.waitForInput();
		}
		
		else{
			
			ArrayList<Integer> neighbors = this.getNeighbors(v);
			
			//visit each neighbor and delete 'node' from its adjacency list
			for(int i=0; i<neighbors.size(); i++){
			
				//get the i-th neighbor
				int n_i = neighbors.get(i);
				
				if(n_i != v){
					
					//get n_i's neighbors
					ArrayList<Integer> n_i_neighbors = this.getNeighbors(n_i);  
					
					//if n_i's neighbors contains 'v', then delete it
					if(n_i_neighbors.contains(v))
						n_i_neighbors.remove(n_i_neighbors.indexOf(v));
			
				}
			
			}
			
			//delete the node from the adjacency list
			this.adjacencyList.remove(v);		
		}
		
	}//end of deleteVertex
		
	//return the neighbors of a given node v
	ArrayList<Integer> getNeighbors(int v){
		
		if(!this.adjacencyList.containsKey(v)){
		
			System.out.println("Exception in Graph.getNeighbors() : The graph does not contain node "+v);
			Util.waitForInput();
			//return new ArrayList<Integer>();
			return null;
		}	
		
		else{
			
			return this.adjacencyList.get(v);
		}
		
	}
	
	//update a node's neighbors list
	
	void updateNeighbors(int node, ArrayList<Integer> newNeighbors){
	
		System.out.println("Vertex : "+node+" list : "+newNeighbors);
		
		if(!this.adjacencyList.containsKey(node)){
			
			System.out.println("Exception in updateNeighborList() : The node "+node+" does not exist!");
			Util.waitForInput();
		}
		
		//fetch the old/existing neighbors of 'node'
		ArrayList<Integer> existingNeighbors = new ArrayList<Integer>(this.getNeighbors(node));
		//remove all the existing neighbors from the new neighbors
		newNeighbors.removeAll(existingNeighbors);
		//update the existing neighbors list by the new neighbors
		existingNeighbors.addAll(newNeighbors);
		
	
		//for each of the node's new neighbors, update their adjacency list by adding 'node' to their list
		for(int i=0; i<newNeighbors.size(); i++){
			
			int n_i = newNeighbors.get(i);
			
			if(n_i != node){
				
				//if the new neighbor already exists in the graph then update it's neighbors list
				if(this.adjacencyList.containsKey(n_i)){
					
					if(!this.getNeighbors(n_i).contains(node))
						this.getNeighbors(n_i).add(node);
				}
				
				else{
					
					//add the new neighbor to the adjacency list
					this.adjacencyList.put(n_i, new ArrayList<Integer>(node));
					
				}
					
				
			}				
			
		}		
	
	}//end of updateNeighborList

	/**
	 * forms a polytree of the family - parents followed by child
	 * @param family
	 */
	void formPolyTree(ArrayList<Integer> family){
		
		Integer child = family.get(family.size()-1);	//the last in the list is the child
		
		if(family.size() == 1)
			return;
		
		else{
						
			for(int i=0; i<family.size()-1; i++)
				this.addNeighbor(family.get(i), child);
		}
			
		
	}
	
	/**
	 * Moralize a Bayesian network
	 */
	void moralize(){
		
		
	}
	
	/**
	 * forms a clique among the vertices
	 * @param vertices
	 */
	void formClique(ArrayList<Integer> vertices){
		
		if(vertices.size() < 1)
			return;
		
		for(int i=0; i<vertices.size(); i++){
			
			for(int j=0; j<vertices.size(); j++){
				
				if(vertices.get(j) != vertices.get(i)){
					
					this.addNeighbor(vertices.get(i), vertices.get(j));
				}
			}
			
		}
		
	}//end of method formClique
	
	
	//makes v a neighbor of u 
	void addNeighbor(int u, int v){
		
		if(this.getNeighbors(u).contains(v)){
			
			return;
		}
		
		else{
			
			this.getNeighbors(u).add(v);
		}	
			
	}
	
	//prints the adjacency list
	void print(){
		
		@SuppressWarnings("rawtypes")
		Iterator itr = this.adjacencyList.entrySet().iterator();
		
		while(itr.hasNext()){
			
			@SuppressWarnings("unchecked")
			Entry<Integer, ArrayList<Integer>> entry = (Entry<Integer, ArrayList<Integer>>) itr.next();
			
			System.out.println("Node : "+entry.getKey()+" Neighbors : "+entry.getValue());
		}
	}
	
	//given a set of variables, a set of potentials and a type this method returns an adjacency list

	static Graph createGraph(ArrayList<Integer> variables, ArrayList<Potential> potentials, String type){
		
		Graph graph = new Graph();
		
		for(int i=0; i<variables.size(); i++)
			graph.addVertex(variables.get(i));
		
		for(int i=0; i<potentials.size(); i++){
			
			Potential p = potentials.get(i);
			ArrayList<Integer> scopeVariables = Variable.getLabels(p.getScope());
			
			if(type.equalsIgnoreCase("Markov"))
				graph.formClique(scopeVariables);
			
			else
				graph.formPolyTree(scopeVariables);		//for Bayesian network
		}
		
		return graph;
	}//end of createGraph()
	
	int getMaxDegreeVertex(){
						
		int max = 0;
		int chosenVar = -1;	
	
		@SuppressWarnings("rawtypes")
		Iterator itr = this.adjacencyList.entrySet().iterator();
				
		while(itr.hasNext()){
			
			@SuppressWarnings("unchecked")
			Entry<Integer, ArrayList<Integer>> entry = (Entry<Integer, ArrayList<Integer>>) itr.next();
			
			int degree = entry.getValue().size();
			
			if(degree >= max){
				
				max = degree;
				chosenVar = entry.getKey();
			}
			
		}
		
		return chosenVar;	
	}//end of getMaxDegreeVertex()
	
	
	Map getAdjacencyList(){
		
		if(this.adjacencyList == null)
			
			return new LinkedHashMap<Integer, ArrayList<Integer>>();
		
		else
			return this.adjacencyList;
	}
	
	
	
	ArrayList<Integer> getVertexList(){
		
		ArrayList<Integer> vertices = new ArrayList<Integer>();
		
		@SuppressWarnings("rawtypes")
		Iterator itr = this.adjacencyList.entrySet().iterator();
		
		while(itr.hasNext()){
			
			@SuppressWarnings("unchecked")
			Entry<Integer, ArrayList<Integer>> entry = (Entry<Integer, ArrayList<Integer>>) itr.next();
			
			vertices.add(entry.getKey());			
		}
		
		return vertices;
	}
	
	
	//decomposes a graph into component graphs
	ArrayList<Graph> decompose(){
		
		int i=0;
		int numberOfVertices = 0;
			
		ArrayList<Graph> components = new ArrayList<Graph>();
		ArrayList<Integer> vertices = this.getVertexList();
		
		numberOfVertices = vertices.size();
		
		if(numberOfVertices == 1){	//a single node graph!
			
			components.add(this);
			return components;
			
		}//end of if
		
		else{
			
					
			while(i<numberOfVertices){
				
				ArrayList<Integer> dfsVertices = this.dfs(vertices.get(0), new ArrayList<Integer>());
				
				i = i + dfsVertices.size();
				
				if(dfsVertices.size() == 1){		//single node graph
					
					Graph component = new Graph();
					
					component.addVertex(dfsVertices.get(0));
					
					components.add(component);
					vertices.remove(vertices.indexOf(dfsVertices.get(0)));
					continue;
					
				}//end of if(reachable list has a single variable)
				
				else{
					
					Graph component = new Graph();
					
					for(int j=0; j<dfsVertices.size(); j++){
							
						int v = dfsVertices.get(j);
						
						ArrayList<Integer> neighbors = this.getNeighbors(v);
						if(neighbors == null){
							
							System.out.println("Exception in Graph.decompose() : neighbors of node "+v+" is null!");
							Util.waitForInput();
						}
						
						component.addVertex(v, neighbors);				
						
					}
					
					//mark each node in the dfs list as visited	
					for(int j=0; j<dfsVertices.size(); j++)
						vertices.remove(vertices.indexOf(dfsVertices.get(j)));			
					
					components.add(component);	
					
				}//end of else		
			}//end of while		
			
			return components;
		}//end of else					
	
	}//end of decompose()
	
	
	ArrayList<Integer> dfs(int v, ArrayList<Integer> visited){
		
		if(!visited.contains(v)){
			
			visited.add(v);
			
			ArrayList<Integer> neighbors = this.getNeighbors(v);
			
			for(int i=0; i<neighbors.size(); i++){
				
				if(visited.contains(neighbors.get(i)))
					continue;
			
				else
					dfs(neighbors.get(i), visited);
			}
		}		
		return visited;		
	}//end of dfs()
	
	//confirms whether a given vertex is present in the graph
	boolean containsVertex(int v){
		
		if(this.adjacencyList.containsKey(v))
			return true;
		
		else
			return false;
		
	}//end of containsVertex()
	
	void addSubgraph(Graph subGraph){
		
		Iterator itr = subGraph.adjacencyList.entrySet().iterator();
		
		while(itr.hasNext()){
			
			Entry<Integer, ArrayList<Integer>> entry = (Entry<Integer, ArrayList<Integer>>) itr.next();
			
			this.addVertex(entry.getKey(), entry.getValue());
		}
	}
	
	//confirms whether a pair of vertices are neighbors
	
	boolean isNeighbor(int u, int v){
		
		if(this.getNeighbors(u).indexOf(v) >=0 )
			
			return true;
		
		else
			
			return false;
	}
	
	//returns the degree distribution of the graph
	HashMap<Integer, ArrayList<Integer>> getDegreeDistribution(){
		
		HashMap<Integer, ArrayList<Integer>> degreeDistribution = new HashMap<Integer, ArrayList<Integer>>();
		
		Iterator itr = this.adjacencyList.entrySet().iterator();
		
		while(itr.hasNext()){
			
			Entry<Integer, ArrayList<Integer>> entry = (Entry<Integer, ArrayList<Integer>>) itr.next();
			
			int v = entry.getKey();
			int degree = entry.getValue().size();
			
			
			if(!degreeDistribution.containsKey(degree)){
				
				degreeDistribution.put(degree, new ArrayList<Integer>());
				degreeDistribution.get(degree).add(v);
			}
			
			else{
				
				if(!degreeDistribution.get(degree).contains(v))
					degreeDistribution.get(degree).add(v);
			}
		}
		
		return degreeDistribution;
		
	}//end of getDegreeDistribution()
	
	
	//returns how far a set of nodes are from forming a clique. The distance is measured
	//by the number of edges that need to be added between the nodes.
	
	int cliqueDistance(ArrayList<Integer> vertices){
		
		int distance = 0;
		
		for(int u=0; u<vertices.size(); u++){
			
			for(int v=u+1; v<vertices.size(); v++){
				
				if(!this.isNeighbor(u,v))
					distance++;
			}
		}
		
		return distance;
	}
	
	
	Graph getCopy(){
		
		Graph copyGraph = new Graph(this.adjacencyList);
		return copyGraph;
	}
	
	//get minFillOrdering of the vertices
	ArrayList<Integer> getMinFillOrdering(){
		
		int min = this.size();
		int minFillVertex = -1;
		ArrayList<Integer> neighbors = null;
		
		Graph copy = this.getCopy();
		
		ArrayList<Integer> order = new ArrayList<Integer>();

		
		do{
			
			Iterator itr = copy.adjacencyList.entrySet().iterator();
			
			while(itr.hasNext()){
				
				Entry<Integer, ArrayList<Integer>> entry = (Entry<Integer, ArrayList<Integer>>) itr.next();
				
				int vertex = entry.getKey();
							
				if(order.contains(vertex))
					continue;
				
				else{
					
					int fillInCount = this.cliqueDistance(entry.getValue());
					
					if(fillInCount < min){
						
						min = fillInCount;
						minFillVertex = vertex;
						neighbors = entry.getValue();
						
					}
					
				}
				
			}
			
			//add the selected variable to the order
			order.add(minFillVertex);
			copy.deleteVertex(minFillVertex);
			//add the fill in edges
			copy.formClique(neighbors);		
			min = copy.size() * 10000;
			minFillVertex = -1;
			
		}while(order.size() != this.size());		
		
		return order;		
	}
	


}
