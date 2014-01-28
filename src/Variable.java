import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Variable{

	int label;
	int value;
	int domain;
	
	ArrayList<Potential> potentials;
	
	Variable(){
		
		//by default every variable has a binary domain
		this.domain = 2;
	}
	
	void setLabel(int label_){
		
		this.label = label_;
	}
	
	void setDomain(int domain_){
		
		this.domain = domain_;
		
	}
	
	int domainSize(){
		
		return this.domain;
	}
	
	int getLabel(){
		
		return this.label;
	}
	
	void setValue(int val_){
		
		this.value = val_;
	}
	
	int getValue(){
		
		return this.value;
	}
	
	
	static int toInteger(ArrayList<Variable> variables, int[] assignments){
		
		int mappedValue = 0;
		
		for(int i=0, j=variables.size(); i<variables.size(); i++,j--){
			
			mappedValue =  mappedValue + ((int)Math.pow(variables.get(i).domainSize(), j-1) * assignments[i]);
		}
		
		return mappedValue;
	}
	
	
	static ArrayList<Integer> getLabels(ArrayList<Variable> variables){
		
		ArrayList<Integer> identifiers = new ArrayList<Integer>();
		
		for(int i=0; i<variables.size(); i++){
			
			identifiers.add(variables.get(i).getLabel());
		}
		
		return identifiers;
	}
	
	//given a set of variable assignments, return mapped index
	static int getAddress(ArrayList<Variable> variables){
		
		int address = 0;
		
		for(int i=0, j=variables.size(); i<variables.size(); i++,j--){
			
			address =  address + ((int)Math.pow(variables.get(i).domainSize(), j-1) * variables.get(i).getValue());
		}
		
		return address;
	}
	
	
	
	/**
	 * given an identifier or label, this method returns the variable object corresponding to that identifier/label
	 * @param id
	 * @param variables
	 * @return
	 */
	static Variable getVariable(int label, ArrayList<Variable> variables){
		
		for(int i=0; i<variables.size(); i++){
			
			if(variables.get(i).getLabel() == label){
				
				return variables.get(i);
			}
				
		}
		
		return null;
	}
	
	static ArrayList<Variable> getOrder(ArrayList<Variable> variables){
		
		ArrayList<Variable> randOrder = new ArrayList<Variable>();
		Random rand = new Random();
		
		
		while(randOrder.size() != variables.size()){
			
			int randIndex = rand.nextInt(variables.size());
			
			if(!randOrder.contains(variables.get(randIndex)))
					randOrder.add(variables.get(randIndex));				
		}
		
		return randOrder;
	}
	
	/**
	 * initializes the number of potential that v appears in
	 */
	void initializePotentials(){
		
		this.potentials = new ArrayList<Potential>();
	}
	
	int countPotentials(){
		
		if(this.potentials == null || this.potentials.size() == 0)
			return 0;
		
		else
			return this.potentials.size();
	}
	
	/**
	 * add a potential
	 * @param p
	 */
	void addPotential(Potential p){
		
		if(this.potentials == null)
			this.potentials = new ArrayList<Potential>();
		
		if(!this.potentials.contains(p))
			this.potentials.add(p);
	}
	
	/**
	 * get the i-th potential
	 * @param i
	 * @return
	 */
	Potential getPotential(int i){
		
		if(this.potentials == null || this.countPotentials() < i){
			
			System.out.println("Exception in Variable.getPotential(int) : index out of bounds!");
			Util.halt();
			return null;
		}
		
		return this.potentials.get(i);
	}
	
	/**
	 * get all potentials	
	 * @return
	 */
	ArrayList<Potential> getPotentials(){
		
		if(this.potentials == null){
			
			System.out.println("Exception in Variable.getPotential(int) : index out of bounds!");
			Util.halt();
			return null;
		}
		
		return this.potentials;
	}
	
	/**
	 * delete the ith potential
	 * @param i
	 */
	void deletePotential(int i){
		
		this.potentials.get(i).delete();
	}
	
	
}
