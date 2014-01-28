import java.util.ArrayList;


/**
 * Generates a random bayesian network
 * @author Tahrima
 *
 */
public class BNGenerator {

	BayesianNetwork generate(int N, int P){
		
		//generate the number of variables for the model
		
		int variableCount = N/2 + (int)(Math.random() * ((N/2)+1));
		
		System.out.println("The number of variables generated in the model is : "+variableCount);
		
		int maxParentCount = P;//(int)(Math.random() * P);
	
		System.out.println("The number of maximum parents generated in the model is : "+maxParentCount);
		
		//create the variables
		
		ArrayList<Variable> variables = new ArrayList<Variable>();
		
		for(int i=0; i<variableCount; i++){
			
			Variable v = new Variable();
			v.setLabel(i);
			v.setDomain(2);
			v.setValue(-1);
			variables.add(v);
		}
		
		//generate a random ordering of the variables
		
		ArrayList<Integer> randOrderIndices = new ArrayList<Integer>();
		
		while(randOrderIndices.size() < variableCount){
			
			//generate a random index
			int randIndex = -1;
			
			do{
				
				randIndex = (int) (Math.random()*variableCount);
				
			}while(randOrderIndices.contains(randIndex));
			
			randOrderIndices.add(randIndex);			
		}
		
		//System.out.println("Generated Variable Order : ");
		
		for(int i=0; i<variableCount; i++){
			
			//System.out.print(" "+variables.get(randOrderIndices.get(i)).getLabel());
		}
		
		//generate potentials;
		ArrayList<Potential> potentials = new ArrayList<Potential>();
		
		for(int i=0; i<variableCount; i++){
			
			if(i == 0){
				
				Potential pi = new Potential();				
				pi.addScope(variables.get(randOrderIndices.get(0)));
				potentials.add(pi);				
			}
			
			else{//randomly generate parents for the next variable
				
				int parentCount_i = 0;//
				
				if(i<maxParentCount)
					parentCount_i = (int)(Math.random() * i);
				
				else
					parentCount_i = (int)(Math.random() * (maxParentCount));
				
				//System.out.println("\nNumber of parents generated for "+variables.get(randOrderIndices.get(i)).getLabel()+" "+parentCount_i);
				
				//select the parents
				
				ArrayList<Variable> parents_i =  new ArrayList<Variable>();
				
				ArrayList<Integer> parentIndices = new ArrayList<Integer>();
				
				while(parentIndices.size() < parentCount_i){
					
					//generate a random index
					int randIndex = -1;
					
					do{
						
						randIndex = (int) (Math.random()*parentCount_i);
						
					}while(parentIndices.contains(randIndex));
					
					parentIndices.add(randIndex);			
				}
				
				for(int j=0; j<parentCount_i; j++)
					parents_i.add(variables.get(randOrderIndices.get(parentIndices.get(j))));
				
				//System.out.println("\n generated parents for "+variables.get(randOrderIndices.get(i)).getLabel()+" \t: "+Variable.getLabels(parents_i));
				
				
				Potential pi = new Potential();				
				pi.addScope(parents_i);
				pi.addScope(variables.get(randOrderIndices.get(i)));
				potentials.add(pi);
			}
		}
		
		
		
		//generate CPT parameters.
		
		for(int i=0; i<potentials.size(); i++){
			
			potentials.get(i).initializeParameters();
			
			potentials.get(i).generateRandParams();
		}
		
		//print the potentials;
		
		/*for(int i=0; i<potentials.size(); i++){
			
			potentials.get(i).print();
			System.out.println();
		}*/
		
		
		BayesianNetwork bn = new BayesianNetwork();
		bn.setType("Bayes");
		bn.addVariables(variables);
		bn.addPotentials(potentials);		
		bn.constructGraph();			
		return bn;
	}//end of method
	

}
