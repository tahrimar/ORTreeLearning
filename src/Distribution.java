import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Distribution {

	ArrayList<Variable> scope;
	
	List<Double> parameters;
	
	Distribution(){
		
		
	}
	
	/**
	 * Normalizes the distribution
	 */
	void normalize(){
		
		double z = 0.0;
		
		for(int i=0; i<this.parameterCount(); i++){
			
			z = z + this.getParameter(i);
		}
		
		for(int i=0; i<this.parameterCount(); i++)
			this.setParameter(i, this.getParameter(i)/z);
		
	}
	
	
	void setParameter(int i, double value){
		
		
		this.parameters.set(i, value);
	}
	
	void addToScope(Variable var){
		
		if(this.scope == null)
			this.scope = new ArrayList<Variable>();
		
		if(!this.scope.contains(var))
			this.scope.add(var);		
		
	}
	
	ArrayList<Variable> getScope(){
		
		if(this.scope == null)
			return new ArrayList<Variable>();
		
		else
			return this.scope;
	}
	
	Variable getScope(int i){
		
		if(this.scope == null){
			
			System.out.println("Exception in Distribution.getScope(int) : null scope!");
			Util.halt();
			return null;
		}
			
		else
			return this.getScope().get(i);
	}
	
	void addToScope(ArrayList<Variable> variables_){
		
		if(this.scope == null)
			this.scope = new ArrayList<Variable>();
		
		if(!this.scope.containsAll(variables_)){
			variables_.removeAll(this.scope);
			this.scope.addAll(variables_);
		}	
		
	}
	
	int scopeSize(){
		
		if(this.getScope() == null)
			return 0;
		
		else 
			return this.getScope().size();
	}
	
	void initializeParameters(){
		
		if(this.scope == null){
			
			System.out.println("Exception in Distribution.initializeParameters() : null scope!");
			Util.halt();
		}
		
		int size = 1;
		
		for(int i=0; i<this.scopeSize(); i++){
			
			size = size * this.getScope(i).domainSize();
		}
		

		//this.parameters = new double[size];
		this.parameters = new ArrayList<Double>();
			
	}
	
	void initializeParameters(int paramCount){
		
		if(this.scope == null){
			
			System.out.println("Exception in Distribution.initializeParameters() : null scope!");
			Util.halt();
		}
		this.parameters = new ArrayList<Double>();
		
		for(int i=0; i<paramCount; i++)
			this.parameters.add(0.0);
			
	}
	

	
	void addParameters(double[] parameters_){
		
		if(this.getParameters() == null){
			
			System.out.println("Exception in Distribution.addParameters() : null parameters!");
			Util.halt();
		}
		
		else{
			
			//System.out.println("length : "+this.parameters.length);
			for(int i=0; i<parameters_.length; i++)
				//this.parameters[i] = parameters_[i];
				this.parameters.add(parameters_[i]);
		}
	}
	
	//double[] getParameters(){
	List<Double> getParameters(){
		
		return this.parameters;
	}
	
	void addParameter(int i, double param_){
		
		//this.parameters[i] = param_;
		//this.parameters.add(param_);
		this.parameters.set(i, param_);
	}
	
	void addParameter(double param_){
		
		//this.parameters[i] = param_;
		if(this.parameters == null)
			this.parameters = new ArrayList<Double>();
		
		this.parameters.add(param_);
	}
	
	double getParameter(int hamdist){
		
		//return this.parameters[i];
		return this.parameters.get((int) hamdist);
	}
	
	/**
	 * returns the number of parameters in the distribution
	 * @return
	 */
	int parameterCount(){
		
		if(this.parameters == null)
			return 0;
		
		else
			return this.parameters.size();
	}
	
	/**
	 * Prints out this distribution
	 */
	void print(){
		
		DecimalFormat df = new DecimalFormat("0.00E00");
		
		if(this.getScope() != null)
			System.out.println("Scope : "+Variable.getLabels(this.getScope()));
		
		else
			System.out.println("Scope : []");
		
		System.out.println("Number of parameters : "+this.parameterCount());
		System.out.println("Parameters : ");
		if(this.getParameters() != null){			
			
			//print 10 parameters per line
			for(int i=0; i<this.parameterCount(); i++){
				
				for(int j=i, k=i+10; j<k && j<this.parameterCount(); j++, i++)
					System.out.print(df.format(this.getParameter(j))+"\t");
				
				System.out.println();
				
			}		
			
		}	
		
		else
			System.out.print(" Parameters : []");
	}
	
	/**
	 * Returns true if this distribution is a uniform distribution
	 * @return
	 */
	boolean isUniform(){
		
		double param_0 = this.getParameter(0);
		
		double z = param_0;
		
		for(int i=1; i<this.parameterCount(); i++){
			
			if(this.getParameter(i) != param_0)
				return false;
			
			z = z + this.getParameter(i); 
		}
		
		if(z/this.parameterCount() != param_0)
			return false;
		
		return true;
	}
	
	boolean isUniform(double eBound){
		
			
		double MSE = 0.0;		//Mean Squared Error	
		
		double hellinger = 0.0;
		
		double uniformWeight = 1/Math.pow(2, this.scopeSize());//(double)1/this.parameterCount();// 

		//System.out.println("Uniform weight : "+uniformWeight);
		
		for(int i=0; i<this.parameterCount(); i++){
			
			
			//double distance_i = Math.pow((Math.sqrt(this.getParameter(i)) - Math.sqrt(uniformWeight)),2);
			double distance_i = Math.abs(this.getParameter(i)-uniformWeight);
			//System.out.println("Distance : "+distance_i);
			if(distance_i > eBound)
				return false;	
			
			//hellinger = hellinger + distance_i;
			
			MSE = MSE + Math.pow(distance_i,2);//Math.pow(this.getParameter(i)-uniformWeight, 2);
			
		}
		
		MSE = MSE/this.parameterCount();
		
		hellinger = Math.sqrt(hellinger)/Math.sqrt(2);
		
		//System.out.println("Mean squared error : "+MSE);
		
		//System.out.println("Hellinger distance : "+hellinger);
		
		//if(MSE <= eBound)//(hellinger <= eBound)//if(MSE > eBound)
			
			return true;
		
		//if((hellinger <= eBound))
			//return true;
		
		//return false;
	}
	
	//computes the hellinger distance between this distribution and the given distribution Q
	double distance(Distribution Q){
		
		double MSE = 0.0;
		
		double hellinger = 0.0;
		
		if(!this.getScope().containsAll(Q.getScope()))
			return 1.0;
		
		if(this.parameterCount() != Q.parameterCount())
			return 1.0;
		
		for(int i=0; i<this.parameterCount(); i++){			
			
			double distance_i = Math.pow((Math.sqrt(this.getParameter(i)) - Math.sqrt(Q.getParameter(i))),2);
			
			hellinger = hellinger + distance_i;
			
			MSE = MSE + Math.pow(this.getParameter(i)-Q.getParameter(i), 2);
			
		}
		
		MSE = MSE/this.parameterCount();
		
		hellinger = Math.sqrt(hellinger)/Math.sqrt(2);
		
		return hellinger;
	}

}
