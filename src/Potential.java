import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class Potential {

	private boolean deleted;
	private ArrayList<Variable> scope;
	double[] parameters;
	
	
	Potential(){
		
		scope = new ArrayList<Variable>();
	}
	
	/**
	 * returns the status of the deleted flag
	 * @return
	 */
	boolean isDeleted(){
		
		return this.deleted;
	}
	
	/**
	 * set the delete flag
	 */
	void delete(){
		
		this.deleted = true;
	}
	
	/**
	 * reset the delete flag
	 */
	void restore(){
		
		this.deleted = false;
	}
	
	
	ArrayList<Variable> getScope(){
		
		return this.scope;
	}
	
	/**
	 * get the i-th scope variable
	 * @param i
	 * @return
	 */
	Variable getScope(int i){
		
		if(this.scope == null || this.scope.size() < i){
			
			System.out.println("Exception in Potential.getScope(int) : index out of bounds");
			Util.halt();
			return null;
			
		}		
		
		return this.scope.get(i);
	}
	
	int scopeSize(){
		
		if(this.getScope() == null)
			return 0;
		
		else
			return this.getScope().size();
	}
	
	

	/**
	 * add a variable to the scope
	 * @param v
	 */
	void addScope(Variable v){
		
		if(this.scope == null)
			this.scope = new ArrayList<Variable>();
		 
		this.scope.add(v);
	}
	
	/**
	 * add a collection of variables to the scope
	 * @param scope_
	 */
	void addScope(ArrayList<Variable> scope_){
		
		if(this.scope == null)
			this.scope = new ArrayList<Variable>();
		
		if(!this.getScope().containsAll(scope_)){
			
			scope_.removeAll(this.getScope());
			this.scope.addAll(scope_);
		}
			
	}
	
	
	/**
	 * initialize the number of parameters n
	 * @param n
	 */
	void initializeParameters(int n){
		
		if(this.parameters == null)
			this.parameters = new double[n];		
	}
	
	void initializeParameters(){
		
		int parameterCount = 1;
		
		for(int i=0; i<this.getScope().size(); i++){
			
			parameterCount = parameterCount * this.getScope(i).domainSize();
		}
		
		this.parameters = new double[parameterCount];
		
		for(int i=0; i<this.parameterCount(); i++)
			this.addParameter(-1.0, i);
	}
	
	/**
	 * add the i-th parameter to the list of parameters
	 * @param param
	 * @param i
	 */
	void addParameter(double param, int i){
		
		if(this.parameters == null)
			this.initializeParameters(this.parameterCount());
		
		this.parameters[i] = param;
	}
	
	/**
	 * get the i-th paratmeter
	 * @param i
	 * @return
	 */
	double getParameter(int i){
		
		if(this.parameters == null || this.parameterCount()<i){
			
			System.out.println("Exception in Potential.getParameter(int) : index out of bounds!");
			Util.halt();
			return -1.0;
		}
		
		return this.parameters[i];
	}
	
	/**
	 * returns all the parameters
	 * @return
	 */
	double[] getParameters(){
		
		if(this.parameters == null){
			
			System.out.println("Exception in Potential.getParameters() : uninitialized parameters!!");
			Util.halt();
			return null;
		}
		
		return this.parameters;
	}
	
	/**
	 * returns the number of parameters
	 * @return
	 */
	int parameterCount(){
		
		return this.parameters.length;
	}
	
	
	/**
	 * returns the weight of an instantiation
	 * @param scope
	 * @return
	 */
	double instantiate(ArrayList<Variable> scope_){
		
		int address = 0;
		
		if(Variable.getLabels(scope_).containsAll(this.getScope())){
		
			address = Variable.getAddress(this.getScope());
			return this.parameters[address];
		}
		
		else{
			
			System.out.println("Exception in Potential.instantiate() : inconsistent scope!");
			Util.halt();
			return 1.0;
		
		}	
		
	}
	
	/**
	 * instantiate on the potentials scope
	 * @return
	 */
	double instantiate(){
		
		int address = 0;
		
		address = Variable.getAddress(this.getScope());
		
		//System.out.println("potential size : "+this.getScope().size()+" Size : "+this.countParameters());
		
		return this.parameters[address];
		
	}
	
	static void multiply(Potential p1, Potential p2){}
	
	static void marginalize(Variable[] V){}

	void print(){
		
		DecimalFormat df = new DecimalFormat("0.000");
		
		System.out.println("\nPotential defined over variables : "+Variable.getLabels(this.getScope()));
		
		System.out.print("Parameters : ");
		
		for(int i=0; i<this.parameterCount(); i++)
			
			System.out.print(df.format(this.getParameter(i))+" ");
		
		//System.out.println("Parameters : "+Arrays.toString(this.parameters));
		
	}
	
	void generateRandParams(){
		
		for(int i=0; i<this.parameterCount()/2; i++){
			
			double param = Math.random();
			
			this.addParameter(param, 2*i);
			this.addParameter(1-param, 2*i+1);
			
		}
	}

}
