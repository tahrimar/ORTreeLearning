import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;


public class BayesianNetwork extends GraphicalModel{

		Map<Integer, ArrayList<Variable>> parents;	//a subset of variables where parents_i is the set of parents of variable i
		
		BayesianNetwork(){
			
			
		}
		
		
		/**
		 * adds parents_ of variable i - used when the model is Bayes
		 * @param i
		 * @param parents_
		 */
		void addParents(int i, ArrayList<Variable> parents_){
			
			if(this.parents == null)
				this.parents = new LinkedHashMap<Integer, ArrayList<Variable>>();
			
			if(!this.parents.containsKey(i)){
				
				this.parents.put(i, new ArrayList<Variable>());
				this.parents.get(i).addAll(parents_);			
			}		
			
			else{
				
				parents_.removeAll(this.parents.get(i));
				this.parents.get(i).addAll(parents_);
			}
		}
		
		void addParents(Map<Integer, ArrayList<Variable>> parents_){
			
			if(this.parents == null)
				this.parents = new LinkedHashMap<Integer, ArrayList<Variable>>(parents_);
			
		}
		
		Map<Integer, ArrayList<Variable>> getParents(){
			
			return this.parents;
		}
		
		/**
		 * adds a parent to variable i - used when the model is Bayes
		 * @param i
		 * @param v
		 */
		void addParent(int i, Variable v){
			
			if(this.parents == null)
				this.parents = new LinkedHashMap<Integer, ArrayList<Variable>>();
			
			if(!this.parents.containsKey(i)){
				
				this.parents.put(i, new ArrayList<Variable>());
				this.parents.get(i).add(v);
			}
			
			else if(!this.parents.get(i).contains(v))
				this.parents.get(i).add(v);
			
			else
				;
		}
	


		/**
		 * Only applicable for Bayesian networks with a topological ordering
		 * This method generates samples from a Bayesian network
		 * @param order
		 */
	
		Dataset generateBNSamples(int N){
		
			ArrayList<Variable> order = this.getTpoLogicalOrder();
			
			System.out.println("Topological order : "+Variable.getLabels(order));	
					
			ArrayList<int[]> samples = this.sample(order, N);
			
			Dataset S = new Dataset();
			
			S.setVariables(this.getVariables());
			
			for(int i=0; i<N; i++){
				
				S.addDataIndex(i);
				S.addDataInstance(samples.get(i));
			}
			
			
			return S;
		
		}
		
		
		/**
		 * Only applicable for Bayesian networks with a topological ordering
		 * This method generates samples from a Bayesian network which are not in X
		 * @param order
		 */
	
		Dataset generateBNSamples(int N, Dataset S_){
		
			ArrayList<Variable> order = this.getTpoLogicalOrder();
			
			System.out.println("Topological order : "+Variable.getLabels(order));	
					
			ArrayList<int[]> samples = this.sample(order, N, S_);
			
			Dataset S = new Dataset();
			
			S.setVariables(this.getVariables());
			
			for(int i=0; i<N; i++){
				
				S.addDataIndex(i);
				S.addDataInstance(samples.get(i));
			}
			
			
			return S;
		
		}


		ArrayList<Variable> getTpoLogicalOrder(){
			
			ArrayList<Variable> order = new ArrayList<Variable>();
			
			for(int i=0; i<this.variableCount(); i++){
				
				if(order.contains(this.getVariable(i)))
					continue;
				
				else
					topologicalOrder(order, this.getVariable(i));			
				
			}			
			
			return order;
		}

		private void topologicalOrder(ArrayList<Variable> order, Variable v){
			
			ArrayList<Variable> parents_v = this.parents.get(v.getLabel());
			
			//System.out.println("Parents of "+v.getLabel()+" : "+Variable.getLabels(parents_v));
			
			if(parents_v == null){
				
				order.add(v);
				return;
			}
			
			
			else if(order.containsAll(parents_v)){
				
				order.add(v);
				return;
			}
			
			else{
				
				parents_v.removeAll(order);
			
				for(int i=0; i<parents_v.size(); i++)
					topologicalOrder(order, parents_v.get(i));
			
				order.add(v);
				return;
				
			}	
		}

		/**
		 * Given a variable ordering and the number of samples N, this method generates samples
		 * according to the given order
		 * @param order
		 * @param N
		 * @return
		 */
		public ArrayList<int[]> sample(ArrayList<Variable> order, int N){
			
		
			ArrayList<int[]> samples = new ArrayList<int[]>();
			
			for(int i=0; i<N; i++){
				
				int [] sample = new int[this.variableCount()];
				
				for(int j=0; j<order.size(); j++){
					
					//get the j-th variable
					Variable var_j = order.get(j);
					
					//get the potentials of the variable
					
					Potential cpt_j = var_j.getPotential(0);			
					
					double rand = Math.random();
					
					int k=0;				
					double r = 0;
					
					do
					{
						
						var_j.setValue(k);
						
						r = r + cpt_j.instantiate();
										
						if(k == var_j.domainSize()-1)
							break;
						k++;
						
					}while(r<rand);				
							
					sample[var_j.getLabel()] = var_j.getValue();	
					
				}
				
				//System.out.println("Sample["+i+"] : "+Arrays.toString(sample));
				
				samples.add(sample);
			}
			
			/*for(int i=0; i<order.size(); i++){
				
				int count_i = 0;
				
				for(int j=0; j<N; j++)
					if(samples.get(j)[order.get(i).getLabel()] == 1)
						count_i++;
				
				//System.out.println(order.get(i).getLabel()+" \t"+(double)count_i/N);
				
			}*/
			
			return samples;
		}

		
		/**
		 * Given a variable ordering and the number of samples N, this method generates samples
		 * according to the given order which are not in S_
		 * @param order
		 * @param N
		 * @return
		 */
		public ArrayList<int[]> sample(ArrayList<Variable> order, int N, Dataset S_){
			
		
			ArrayList<int[]> samples = new ArrayList<int[]>();
			
			S_ = S_.project(this.getVariables());
			
			//S_.print("");
			
			for(int i=0; i<N; ){
				
				int [] sample = new int[this.variableCount()];
				
				for(int j=0; j<order.size(); j++){
					
					//get the j-th variable
					Variable var_j = order.get(j);
					
					//get the potentials of the variable
					
					Potential cpt_j = var_j.getPotential(0);			
					
					double rand = Math.random();
					
					int k=0;				
					double r = 0;
					
					do
					{
						
						var_j.setValue(k);
						
						r = r + cpt_j.instantiate();
										
						if(k == var_j.domainSize()-1)
							break;
						k++;
						
					}while(r<rand);				
							
					sample[var_j.getLabel()] = var_j.getValue();	
					
				}
				
				//System.out.println("Sample["+i+"] : "+Arrays.toString(sample));
				
				if(!S_.contains(sample)){
					samples.add(sample);
					i++;
				}
				
				else{
					
					//Util.halt(Arrays.toString(sample)+" already present in the given dataset...");
				}
			}
			
			return samples;
		}

		/**
		 * applicable to a Bayes net
		 */
		void Moralize(){
			
			Iterator itr = this.parents.entrySet().iterator();
			
			while(itr.hasNext()){
				
				Entry<Integer, ArrayList<Variable>> entry = (Entry<Integer, ArrayList<Variable>>) itr.next();
				
				ArrayList<Variable> parents_i = entry.getValue();
				
				this.getGraph().formClique(Variable.getLabels(parents_i));
			}
			
		}
		
		void makeUndirected(){
			
			Iterator itr = this.getGraph().getAdjacencyList().entrySet().iterator();
			
			while(itr.hasNext()){
				
				Entry<Integer, ArrayList<Integer>> entry = (Entry<Integer, ArrayList<Integer>>) itr.next();
				
				ArrayList<Integer> vertices = new ArrayList<Integer>(entry.getValue());
				
				vertices.add(entry.getKey());
				
				this.getGraph().formClique(vertices);
			}
			
		}
		
		
		/**
		 * Given a Bayesian network with structure and data and the smoothing factor, this method learns
		 * the parameters from data
		 * @param gm
		 * @param ds
		 * @param alpha
		 * @return
		 */
		void learnParameters(Dataset ds, double alpha){
			
	
			//System.out.println("Variables : "+Variable.getLabels(gm.getVariables()));
			
			/*this.addVariables(gm.getVariables());
			
			this.setType(gm.getType());
			
			this.addParents(gm.getParents());
			
			
			this.constructGraph();*/
			
			//learn the parameters according to topological order
			
			ArrayList<Variable> order = this.getTpoLogicalOrder();
			
			for(int i=0; i<this.potentialCount(); i++){
				
				Potential p_i = this.getPotential(i);
				
				if(p_i.getScope().size() == 1){	//no parents
					
					Dataset ds_i = ds.project(p_i.getScope());
					
					//ds_i.print("dataset for "+Variable.getLabels(p_i.getScope()));
					
					for(int j=0; j<p_i.parameterCount(); j++){
						
						//double param = (double)ds_i.count(p_i.getScope().get(0).getLabel(), j)/ds_i.size();
						double param = (double)(ds_i.count(p_i.getScope().get(0).getLabel(), j)+1)/(ds_i.size()+2);
						p_i.addParameter(param, j);
						//System.out.println(p_i.getScope(0).getLabel()+"["+j+"] : "+param);
					}			
					
				}
				
				else{//conditional
					
					Dataset ds_i = ds.project(p_i.getScope());
					
					//ds_i.print("dataset for "+Variable.getLabels(p_i.getScope()));
					
					int totalCount_xy = ds_i.size();
					
					for(int j=0; j<ds_i.size(); j++){
						
						int index_ = ds_i.getDataIndex(j);
						
						int[] instance_xy = ds_i.getDataInstance(index_);
						
						int val = Variable.toInteger(p_i.getScope(), instance_xy);
						
						p_i.parameters[val]++;
					}
					
					for(int k=0; k<p_i.parameters.length; k++){
						
						//p_i.parameters[k] = (double)p_i.parameters[k]/totalCount_xy;
						
						p_i.parameters[k] = (double)(p_i.parameters[k]+1)/(totalCount_xy+2);
						
						//System.out.println(Variable.getLabels(p_i.getScope())+"["+k+"] : "+p_i.parameters[k]);					
					}				
				}
			}
			
			
		}//end of learnParameters	

}//end of class
