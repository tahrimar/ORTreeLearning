import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
/**
 * 
 * @author Tahrima
 *
 */

public class ORTree extends Tree{

		boolean _sFlag;					//if this flag is set, then the tree is built according to a predefined ordering of variables
		int nextInOrder;				//used for indexing through the static order
		ArrayList<Variable>	Order;		//The order according to which the tree is built
		
		//Node root;						//OR trees have single roots
		
		ArrayList<ArrayList<Variable>> leafDontCares;
		
		int leafCount = 0;	
		
		int variableCount;
		
		void set_sFlag(){
			
			this._sFlag = true;
		}
		
		void reset_sFlag(){
			
			this._sFlag = false;
		}
		
		boolean is_sFlag(){
			
			return this._sFlag;
		}	
		
		void setOrder(ArrayList<Variable> Order_){
			
			this.Order = new ArrayList<Variable>(Order_);			
		}
		
		ArrayList<Variable> getOrder(){
			
			return this.Order;
		}
		
		ORTree(){
			
			this.root = new ANDNode();
			root.setID(this.nodeCount++);
			root.setAsRoot();
			this.nextInOrder = 0;
			leafDontCares = new ArrayList<ArrayList<Variable>>();
			
		}
		
		/**
		 * Learns a full tree from the given dataset
		 * prnBound is the lower bound for pru ning - if the fraction of data is to a subtree becomes less than prnBound, then
		 * we prune that subtree.
		 * @return
		 */
		Node learnFullTree(Dataset ds, double prnBound){
			
			//this.root = (this.createOR(ds, 0, prnBound, 0.1));
			
			System.out.println("Learning tree...");
			
			this.variableCount = ds.scopeSize();
			
			double startTime = System.nanoTime();
			
			//check to see if the dataset is pure
			
			double entropy = ds.entropy();
			
			if(entropy == 0){
				
				Util.halt("Entropy of the data is 0. Creating a root leaf node");
				
				this.root = this.createLeaf(ds, 2, 0);//(dataset, label, level) : 2 is label for root 
				
			}
			
			else{
				
				Node child = this.createAND(ds, 0, 0, 1.0);//this.createOR(ds, 1);				
				
				this.root.addChild(child);
				
				child.addParent(root);
				
				//this.root.setAsRoot();
				
				this.root.setFunctionValue(1.0);
				
				System.out.println("level count : "+this.levelCount());				
				
			}
			
			
			double endTime = System.nanoTime();
			
			System.out.println("ORTree Learning Time : "+(endTime-startTime)/1e9);
			
			
			return this.root;
			//return this.createOR(ds, 0);
		}
		
		
		ORNode createOR(Dataset ds, Variable var, int level){
			
			ANDNode child = null;
			
			ORNode newOR = new ORNode();
			
			newOR.setVariable(var);
			
			newOR.setID(this.nodeCount++);
			
			newOR.setLevel(level);			
			
			//for each value of the chosen variable create an AND node
					
			for(int i=0; i<var.domainSize(); i++){
				
				double weight = 1.0;
				
				//a combination of selection and projection on the dataset - project(V\v)from all tuples with v=i
				Dataset dataset_i = ds.condition(var, i);
				
				//System.out.println("Original size = "+ds.size()+" conditioned size = "+dataset_i.size());
				
				//dataset_i.print("");
				
				//Util.halt();
				
				weight = (double)((dataset_i.size()+1))/((ds.size()+2));	//Laplacian smoothing
				
				child = createAND(dataset_i, i, level+1, weight);	
				
				newOR.setWeight(i, weight);			
				
				newOR.addChild(child);					
									
			}
			
			newOR.sortChildren();//sort the children. Most of the time sorted...
			newOR.normalize();
			newOR.computeZ();	
			
			for(int i=0; i<newOR.childCount(); i++){
				
				if(newOR.getWeight(i) >= 1.00){
					
					Util.halt("Exception in node weight ! ...");
				}
			}
			
			//add the node to the level
			
			this.addToLevel(level, newOR);
			
			ds = null;
			
			this.nextInOrder--;			
			
			return newOR;
		}
		
		ANDNode createLeaf(ArrayList<Variable> variables, int[] uniformSample, int count, int label, int level){
			
			ANDNode leaf = new ANDNode();
			
			leaf.setLabel(label);
			leaf.setLevel(level);
			
			leaf.setToLeaf();
			
			//create the distribution
			
			Distribution Q = new Distribution();
			
			
			
			return leaf;
		}
		
		/**
		 * 
		 * @param ds		-	the dataset
		 * @param label		-	the label of the AND node : 0,1		 	
		 * @param level		-	level of this AND node
		 * @return			-	a newly created AND node
		 */
		ANDNode createAND(Dataset ds, int label, int level, double weight){
			
			
			ANDNode newAND = new ANDNode();				
			newAND.setLabel(label);				
			newAND.setID(this.nodeCount++);				
			newAND.setLevel(level);
			
			this.addToLevel(level, newAND);			
						
			if(ds.size() == 1 && ds.scopeSize() > 0){
				
				//only one uniform sample left
				///ds.print("");
				newAND.setToLeaf();				
				
				newAND.addDataset(ds);
				
				newAND.setFunctionValue(0.8+Math.random()*0.1);
		
				newAND.computeZ();
				
				this.leafCount++;
				
				return newAND;
			}
			
			else if(ds.size() == 0 && ds.scopeSize() > 0){	//the edge weight will be zero
				
				//create leaf with don't care variables
				newAND.setToLeaf();
				//newAND.addDontCares(ds.getVariables());
				newAND.computeZ();
			
				newAND.setFunctionValue(0.5);
				
				newAND.addDataset(ds);
				//this.leafDontCares.add(ds.getVariables());
				
				this.leafCount++;
				return newAND;
			}
			
			else if(ds.scopeSize() == 0){
				
				//all the variables have been conditioned. 
				newAND.setToLeaf();
				//Util.halt();
				newAND.setFunctionValue(1.0);
				newAND.computeZ();
				this.leafCount++;
				return newAND;
			}
			
			else{
				
				Variable var = ds.getMaxEntropyVar();
				
					
				double splitFraction = 0.5;
				
				//System.out.println("split fraction : "+splitFraction);
				
				int splitSize = (int) (ds.size() * splitFraction);
				
				ORNode child = createOR(ds.splitWOR(splitSize), var, level+1);			
				
				newAND.addChild(child);
			
				//add the marginal distribution of each child OR node
				//newAND.addDistribution(child.getDistribution());
				newAND.setFunctionValue(1.0);		
				
				newAND.computeZ();			
				
				return newAND;
			}
				
		}//end of learnTree()
		
		void printLeafDontCares(){
			
			for(int i=0; i<this.leafDontCares.size(); i++)
				Util.halt(Variable.getLabels(this.leafDontCares.get(i))+"");
				
			
		}
		
		ANDNode createLeaf(Dataset ds, int label, int level){
			
			ANDNode leaf = new ANDNode();
			
			Distribution Q = new Distribution();			
			Q.addToScope(ds.getVariables());			
			Q.initializeParameters(Q.getScope().size()+1);
			
			if(ds.size() >= 1){
				
				int[] sample0 = ds.getDataInstance(ds.getDataIndex(0));
				
				int[] uniformSample = new int[Q.scopeSize()];
				
				for(int i=0; i<Q.scopeSize(); i++){
					
					uniformSample[i] = sample0[Q.getScope(i).getLabel()];
				}
				
				leaf.setConstantSample(Arrays.toString(uniformSample));
				
				//System.out.println("Scope size at leaf : "+Q.scopeSize());
				
				double probOfUniformSample = 1.0;//0.2 + Math.random() * (1-0.9);											
				 
				Q.setParameter(0, probOfUniformSample);
				
				for(int i=0; i<Q.parameterCount(); i++){
					
					double param = probOfUniformSample * (Math.pow(2, -i));
					
					Q.setParameter(i, param);
				}
				
				Q.normalize();				
				leaf.setDistribution(Q);
					
			}
			
			else{
				
				Util.halt("Exception in ORTree.createLeaf() : Dataset size is 0!"); 
			}
			
			return leaf;
		}
		
		
		
		
		/**
		 * Learns a compact tree from the given dataset
		 * @param ds
		 * @return
		 */
		Node learnCompactTree(Dataset ds){
			
			return this.root;
		}
		
		/**
		 * builds the full tree from the griven graphical model
		 * @return
		 */
		Node buildTree(GraphicalModel gm){
			
			return this.root;
		}
		
		/**
		 * applies merging and pruning as the tree is built
		 * @return
		 */
		Node buildCompactTree(GraphicalModel gm){
			
			return this.root;
		}
		
		ArrayList<Node> getLeaves(){
			
			ArrayList<Node> leaves = new ArrayList<Node>();
			
			for(int i=0; i<this.levelCount(); i++){
				
				if(!this.levelExists(i))
					continue;
				
				ArrayList<Node> levelNodes = this.getLevel(i);
				
				for(int j=0; j<levelNodes.size(); j++){
					
					Node n_ij = levelNodes.get(j);
					
					if(n_ij.isLeaf())
						
						leaves.add(n_ij);
				}
			}
			
			return leaves;
		}
		
		
		/*void runValidation(Node node, Dataset validation){
			
			if(validation.size() == 0)				
				return;			
			
			if(node.isLeaf())
				return;
			
			if(node.isOR()){
				
				//System.out.println(node.getLabel());				
				for(int i=0; i<node.childCount(); i++){
					
					Dataset dataset_i = validation.condition(node.castToOR().getVariable(), node.getChild(i).getLabel());
					
					double validationWeight = (double)dataset_i.size()/validation.size();
					
					if(Double.isNaN(validationWeight)){
						
						//System.out.println("dataset_i size : "+dataset_i.size()+" validation set size : "+validation.size());
						//Util.halt();
					}
					
					int childLabel = node.getChild(i).getLabel();
					
					double oldWeight = node.getWeight(childLabel);
					
					double newWeight = (double)((validationWeight*100 + oldWeight*100)/200);
					
					//System.out.println("Old Weight :"+oldWeight+" Validation weight :"+validationWeight+" New weight : "+newWeight);
					
					if(newWeight == 0.0){
						
						System.out.println("Old Weight :"+oldWeight+" Validation weight :"+validationWeight+" New weight : "+newWeight);
						
						Util.halt("New Weight is 0.0!");
						
					}
					
					node.setWeight(childLabel, newWeight);					
					runValidation(node.getChild(i), dataset_i);					
					node.castToOR().normalize();					
					
				}//end of for			
								
				
			}//end of if	
			
			
			else{
				
				
				for(int i=0; i<node.childCount(); i++)
					runValidation(node.getChild(i), validation);
				
			}
		}*/
		
		void clearEvidence(Node node){
			
			if(node.isLeaf())
				return;
			
			if(node.isOR())
				node.castToOR().getVariable().setValue(-1);
			
			for(int i=0; i<node.childCount(); i++)
				clearEvidence(node.getChild(i));
		}
		
		void validate(Node node, Dataset validationSet){
			
			if(validationSet.size() == 0)
				return;
			
			
			if(node.isOR()){
				
				//condition the dataset
				Variable var = node.castToOR().getVariable();
				
				for(int i=0; i<var.domainSize(); i++){
					
					double weight = 1.0;
					
					//a combination of selection and projection on the dataset - project(V\v)from all tuples with v=i
					Dataset dataset_i = validationSet.condition(var, i);
						
					weight = (double)(dataset_i.size())/(validationSet.size());
					
					double oldWeight = node.getWeight(i);
					
					if(weight == 0.0)
						weight = 1e-2/(1+1e-2);
					
					else if(weight == 1.0)
						weight = 1/(1+1e-2);
					
					else
						;
					
					if(weight > oldWeight){
						
						//set the average weight or the maximum weight??
						
						//System.out.println("old weight = "+node.getWeight(i)+" new weight = "+(oldWeight+weight)/2);
						//node.setWeight(i, (oldWeight+weight)/2);
						
						if(node.getWeight(i) == 0.0)
							Util.halt("Exception in ORTree.validate() : revised weight is 0."); 
						//node.setWeight(i, weight);
					}
					
					this.validate(node.getChild(i), dataset_i);									
				}
				
				node.castToOR().normalize();
				node.computeZ();	
				
				for(int i=0; i<node.childCount(); i++){
					
					if(node.getWeight(i) >= 1.00){
						
						Util.halt("Exception in node weight ! ...");
					}
				}
				
				
			}//OR node
			
			else if (node.isAND()){
				
				if(!node.isLeaf()){
					
					//for a non-terminal AND node, recurse on its children
					for(int i=0; i<node.childCount(); i++){
						
						this.validate(node.getChild(i), validationSet);
					}
					
					node.computeZ();	
					return;
				}
				
				if(node.isLeaf()){
					
					//based on the validation set at the leaf of the current tree, 
					//we might decide to extend the tree or change the distribution at the leaf accordingly
					ANDNode leaf = node.castToAND();
					
					double entropy = validationSet.entropy();
					
					if(leaf.getDistribution() != null){
						
						Distribution Q = leaf.getDistribution();
						
						//extend the current leaf based on the current entropy of the validation set
						if(entropy == 0){
							
							int[] uniformSample_v = validationSet.getDataInstance(validationSet.getDataIndex(0));
							
							int hamdist = Util.hamdist(leaf.getConstantSample(), Arrays.toString(uniformSample_v));
							
							//if the constant samples are not equal
							
							if(leaf.getConstantSample().equalsIgnoreCase(Arrays.toString(uniformSample_v))){
								
								Util.halt("Uniform samples not equal...");
							}
							
							double prob_hamdist = Q.getParameter(hamdist);
							
							Q.setParameter(hamdist, prob_hamdist * validationSet.size());
							
							Q.normalize();
							
						}
							
						else if (entropy > 0){
							
							//extend the leaf
							
							leaf.resetLeaf();
							
							leaf.deleteDistribution();							
																				
							if(!Q.isUniform()){
								
								//Util.halt("Validation sample count = "+validationSet.size()+" Training set sample count : "+leaf.getSampleCount());//Util.halt("Not a uniform distribution...");
								if(leaf.getSampleCount() >= validationSet.size()){
									
									
									Map<int[], Double> mleValidation = validationSet.getMLEWeights();
									
									Iterator itr = mleValidation.entrySet().iterator();
									
									while(itr.hasNext()){
										
										Entry<int[], Double> entry = (Entry<int[], Double>) itr.next();
										
										int hamdist = Util.hamdist(leaf.getConstantSample(), Arrays.toString(entry.getKey()));
										
										if(Q.getParameter(hamdist) < entry.getValue())
											Q.setParameter(hamdist, entry.getValue());
									
									}								
									
									Q.normalize();
								}
								
								else{//extend the leaf
									
									
									//validationSet.print("before adding leaf sample");
									//Util.halt("Validation sample count = "+validationSet.size()+" Training set sample count : "+leaf.getSampleCount());//Util.halt("Not a uniform distribution...");
									
									
									//								
									
									int[] uniformSample = Util.toInteger_(leaf.getConstantSample());
									
									int index = Dataset.getSampleIndex(validationSet.data, uniformSample);
									
									if(index == -1){
										
										//Util.halt("Sample was not found in the dataset");
										
										int currentSampleCount = leaf.getSampleCount();
										
										for(int i=0; i<currentSampleCount; i++){
											
											//validationSet.addDataInstance(uniformSample);
											validationSet.data.add(uniformSample);
											int uniformSampleIndex = validationSet.data.size()-1;
											validationSet.addDataIndex(uniformSampleIndex);
										
											
										}
									}	
									
									else{
										validationSet.addDataIndex(index);
										Util.halt("Sample was found in the dataset");
									}	
									
									
									/**/
									
									//validationSet.print("after adding leaf sample");
									
									ORNode newChild = null;//this.createOR(validationSet, leaf.getLevel()+1);
									
									leaf.addChild(newChild);
									
									leaf.computeZ();
									
								}
								
							}	
							
							
							else{
								
								//								
								
								int[] uniformSample = Util.toInteger_(leaf.getConstantSample());
								
								int index = Dataset.getSampleIndex(validationSet.data, uniformSample);
								
								if(index == -1){
									
									//Util.halt("Sample was not found in the dataset");
									//extend the leaf
									
									int currentSampleCount = leaf.getSampleCount();
									
									for(int i=0; i<currentSampleCount; i++){
										
										validationSet.data.add(uniformSample);//addDataInstance(uniformSample);
										int uniformSampleIndex = validationSet.data.size()-1;
										validationSet.addDataIndex(uniformSampleIndex);
										
									}
									
									ORNode newChild = null;//this.createOR(validationSet, leaf.getLevel()+1);
									
									leaf.addChild(newChild);
									
									leaf.computeZ();
									
								}	
								
								else{
									
									//validationSet.addDataIndex(index);
									//Util.halt("Sample was found in the dataset");
								}	
								
								
							}		
														
						
						}
							
						else{
							
							Util.halt("Exception in ORTree.validate() : negative entropy.");
						}
						
						/*if(entropy >= 0){
															
							leaf.resetLeaf();
							
							leaf.deleteDistribution();							
							
							int currentSampleCount = leaf.getSampleCount();
							
							
							if(Q.isUniform()){
								
								
							}
							// tweak!!
							int[] uniformSample = Util.toInteger(leaf.getConstantSample());
																				
							for(int i=0; i<currentSampleCount; i++){
								
								int uniformSampleIndex = validationSet.size();
								validationSet.addDataIndex(uniformSampleIndex);
								validationSet.addDataInstance(uniformSample);
								
							}
							
							ORNode newChild = this.createOR(validationSet, leaf.getLevel()+1);
							
							leaf.addChild(newChild);
							
							leaf.computeZ();	
							
							
						}
						
						else
							Util.halt("Exception in ORTree.validate() : negative entropy.");*/
					
					}
					
					else{//if there are no distributions at the leaf, then create a new leaf with the validation set
						
						if(entropy >= 0){
							
							Util.halt("Extend the leaf node...");
							
							leaf.resetLeaf();
							
							leaf.deleteDistribution();							
							
							leaf.setSampleCount(0);
							
							ORNode newChild = null;//this.createOR(validationSet, leaf.getLevel()+1);
							
							leaf.addChild(newChild);
							
							leaf.computeZ();	
							
							
						}
						
						else
							Util.halt("Exception in ORTree.validate() : negative entropy.");
					
						
					}
					
				}	
					
				/*if(leaf.isUniform()){
						
						leaf.print();
						
						
						
						//Util.halt("Uniform leaf..."+leaf.getFunctionValue());
						
						
						
						//if the entropy is > 0
						//extend the leaf on the validationSet
						Distribution Q = new Distribution();
						
						if(entropy > 0){
							
							if(!leaf.getDontCares().containsAll(validationSet.getVariables()))
								Util.halt("Exception in ORTree.validate() : leaf and dataset don't have same variables!");
													
													
						}
						
						else if(entropy == 0){
								
													
							//get the uniform sample of the validation set
								
							Q.addToScope(validationSet.getVariables());
							
							Q.initializeParameters(validationSet.scopeSize()+1);						
							
							int[] sample_ = validationSet.project(validationSet.getDataInstance(validationSet.getDataIndex(0)));						
							
							//int intSample = Util.toInteger(sample_);						
							
							String intSample = Arrays.toString(sample_);
													
							double probSample_ = 0.8 + Math.random() * (1-0.9);						
							//System.out.println("Constant sample : "+Arrays.toString(sample_)+" Index : "+intSample+" Probability : "+probSample_);						
							
							for(int i=0; i<validationSet.scopeSize()+1; i++){
								
								double prob_i = probSample_ * Math.pow(Math.E, -1*i);
								
								Q.addParameter(i, prob_i);								
								
							}
							
							Q.normalize();					
							//Q.print();
							//nAND.setFunctionValue(probSample_);
							leaf.setDistribution(Q);
							leaf.resetUniform();
							leaf.addDontCares(validationSet.getVariables());
							leaf.setSampleCount(validationSet.size());
							leaf.setConstantSample(intSample);						
							//leaf.setFunctionValue(1.0);
												
						}			
						
					}
					
					//get the don't care assignment of the validation set
							
					
					else{
						
						//compute the entropy of the validation set
						double entropy = validationSet.entropy();
						
						//if the entropy is > 0
						//extend the leaf on the validationSet
						if(entropy > 0){
							
							if(!leaf.getDontCares().containsAll(validationSet.getVariables()))
								Util.halt("Exception in ORTree.validate() : leaf and dataset don't have same variables!");
							
							
							leaf.resetLeaf();
							leaf.getDontCares().clear();
							//leaf.setFunctionValue(1.0);
							leaf.resetUniform();
							leaf.deleteDistribution();
							
							ORNode newChild = this.createOR(validationSet, node.getLevel()+1);
							
							node.addChild(newChild);
							
							//add the marginal distribution of each child OR node
							//newAND.addDistribution(child.getDistribution());
							node.computeZ();

							
						}
						
						else if(entropy == 0){
								
							Distribution leafDist = leaf.getDistribution();
								
							//get the uniform sample of the validation set
								
							int[] sample = validationSet.project(validationSet.getDataInstance(validationSet.getDataIndex(0)));						
								
							String intSample = Arrays.toString(sample);
								
							//if the constant sample from the training set stored at the leaf  is different from that of the validation set
								
							if(!leaf.getConstantSample().equalsIgnoreCase(intSample)){
							
								int hamdist = Util.hamdist(leaf.getConstantSample(), intSample);
									
								if(leaf.getSampleCount() != validationSet.size()){
										
									//Util.halt("Change leaf distribution...");
										
									//leaf.setConstantSample(Arrays.toString(Util.or(sample_, Util.toInteger(leaf.getConstantSample()))));

									//decrease the probability of the original sample
									double param_0 = leafDist.getParameter(0);
									leafDist.setParameter(0, param_0/validationSet.size());
									//increase the probability of the validation sample
									double param = leafDist.getParameter(hamdist);										
									leafDist.setParameter(hamdist, param * validationSet.size());
									leafDist.normalize();
								}
									
								else{
										
									double param_0 = leafDist.getParameter(0);
									leafDist.setParameter(hamdist, param_0);
									leafDist.normalize();
									//leaf.setFunctionValue(1.0);
								}
							
							}//different uniform samples							
							
							else{	//same uniform samples - increase the probability of the sample
								
								
								double param_0 = leafDist.getParameter(0);
								leafDist.setParameter(0, param_0 * validationSet.size());
								leafDist.normalize();
							}
							
						}
						
						else
							Util.halt("Exception in ORTree.validate() : negative entropy.");						
							
					}//validation scope size > 0
						
				}//*/			
				
			}//leaf node
			
		}//end of method	
		
		
		void runValidation(ArrayList<Node> leaves, Dataset validation){
			
			//a binomial distribution is learned at each leaf.
			//the purpose of this validation is to learn the weights of the leaves.
			//standard gradient ascent that maximized the likelihood of the data
			
			double[] w_t, w_tplus1;
			
			double ita = 0.001;
			
			this.clearEvidence(this.getRoot());
			
			double LL_t = this.computeLL(validation)/validation.size();;
			
			int sign = 1;
			
			for(int t=0; t<1000; t++){
				
				double LL_tplus1 = 0.0;
				
				for(int i=0; i<leaves.size(); i++){
					
					ANDNode andLeaf = leaves.get(i).castToAND();
					
					double oldFn = andLeaf.getFunctionValue();
					
					double delta = (double)sign*ita*Math.random();
					
					double newFn = andLeaf.getFunctionValue() + delta;
										
					andLeaf.setFunctionValue(newFn);
					
					//System.out.println("Previous leaf function : "+oldFn+" delta : "+delta+" Current leaf function : "+newFn);
											
				}			
				
				this.clearEvidence(this.getRoot());				
				LL_tplus1 = this.computeLL(validation)/validation.size();;
				
				//System.out.println("LL_t : "+LL_t+" LL_tplus1 : "+LL_tplus1);
				
				if(LL_tplus1 > LL_t){
					
					sign = 1;//keep the weights;
					LL_t = LL_tplus1;
					
				}	
				if(LL_tplus1 - LL_t <= 1e-9){
					
					System.out.println("reached error bound!");
					return;
				}	
				
				else{
					
					sign = -1;
					LL_t = LL_tplus1;
				}
			}			
			
		}
			
		
		
		void pruneTopDown(Node node, Dataset validationDS, ArrayList<Integer> ids){
			
			
			if(node.isOR()){
				
				Distribution P = this.getDistribution(node, 1.0, new Distribution());
				
				if(P.isUniform(0.001)){
									
					double LL_before =  this.computeLL(validationDS)/validationDS.size();
					
					//System.out.println("\nLikelihood before pruning uniform distibution rooted at : "+node.getLabel()+" "+LL_before);					
					//delete the node from its parents
					
					ArrayList<Node> parents = new ArrayList<Node>(node.getParents());
					
					ArrayList<Variable> dontCares = Tree.getScope(node, new ArrayList<Variable>());
					
					
					
					for(int k=0; k<node.parentCount(); k++){
						
						ANDNode parent_ = (ANDNode) node.getParent(k);
						parent_.deleteChild(node);
						parent_.addDontCares(dontCares);
						parent_.setFunctionValue(1/Math.pow(2, parent_.dontCareCount()));
						
					}
					
					if(node.isRoot()){
						
						/**
						 * The tree becomes empty deleting the root!
						 */
						
						this.setAsEmpty();
						//create a new AND node and assign all the variables as don't cares						
						this.getRoot().castToAND().addDontCares(dontCares);
					}
					
					//now run validation on the pruned tree
					
					double LL_after = this.computeLL(validationDS)/validationDS.size();

					//System.out.println("Likelihood after pruning uniform distribution rooted at : "+node.getLabel()+" "+LL_after);
					//if the likelihood decreases then unprune
					if(LL_after >= LL_before){//keep the pruned version of the tree and recurse
						
						if(ids.contains(node.getID())){
							
							Util.halt("Node already deleted ! ...");
						}					
						
						else
							ids.add(node.getID());
						
						System.out.println("Prune : deleting subtree rooted at node  "+node.getLabel()+" Level : "+node.getLevel());
						
						this.deleteSubtree(node);
						
						node.setAsPruned();
						
						return;					
						
					}
					
					else{
						
						
						for(int k=0; k<parents.size(); k++){
							
							ANDNode parent_ = (ANDNode) parents.get(k);
							parent_.addChild(node);
							//now delete the previously added don't cares((ANDNode)parent_j).addDontCare(P.getScope());
							parent_.deleteDontCares(dontCares);																		
							parent_.setFunctionValue(1/Math.pow(2, parent_.dontCareCount()));
							
						}			
				
						
						for(int i=0; i<node.childCount(); i++){
							
							this.pruneTopDown(node.getChild(i), validationDS, ids);
						}
						
						
					}
				
				}//if uniform distribution
				
				else{
					
					
					for(int i=0; i<node.childCount(); i++){
						
						this.pruneTopDown(node.getChild(i), validationDS, ids);
					}
					
				}			
				
			}//if node is an OR node
			
			else{
				
				if(node.isLeaf())
					return;						
				
				for(int i=0; i<node.childCount(); i++){
					
					this.pruneTopDown(node.getChild(i), validationDS, ids);
				}
				
			}		
			
		}
		
		void pruneBottomUp(Dataset validationDS){
			
			int pruneCount = 0;
			
			int maxLevel = this.levelCount()-1;			
			
			System.out.println("pruneBottomUp() : ");			
			
			this.clearEvidence(this.getRoot());			
			
			double LL_before =  this.computeLL(validationDS)/validationDS.size();		
			
			System.out.println("Likelihood of validation set before pruning : "+LL_before);
			
			for(int i = maxLevel; i>=1; i--){
				
				System.out.println("Pruning Level "+i);
				
				for(int j=0; j<this.getLevel(i).size(); ){
					
					Node node = this.getLevel(i).get(j);
					
					if(node.isAND()){	//return on AND nodes
					
						j++;
						continue;
					}	
					else{//recurse on OR nodes.
						
						ORNode nOR = node.castToOR();
						
						ANDNode andChild_0 = (ANDNode) nOR.getChild(0);
						ANDNode andChild_1 = (ANDNode) nOR.getChild(1);
						
						//if the AND children are non-leaf nodes then return
						
						if(!andChild_0.isLeaf() || !andChild_1.isLeaf()){
							
							j++;
							continue;
						}	
												
						
						ANDNode andParent = (ANDNode) nOR.getParent();
						
						//if(!andParent.isUniform() || andParent.getDistribution() != null)
							//Util.halt("ODD andParent...");
						//Distribution P = this.getDistribution(node, 1.0, new Distribution());
		
						//double eBound = (1/Math.pow(2, maxLevel-i)) * 0.1;
						
						if(nOR.hasUniformWeights(0.001)){//P.isUniform(0.001)){
							
							//get the don't care variables that will be produced if the subtree rooted at this node is pruned
							//ArrayList<Variable> dontCares = Tree.getScope(node, new ArrayList<Variable>());
													
							//make a new leaf node
							
							//this.print(nOR, 0, "Uniform OR node");
							//Util.halt();
							
							ANDNode newLeaf = new ANDNode();
							
							newLeaf.setID(andParent.getID());
							
							newLeaf.setLabel(andParent.getLabel());
							
							newLeaf.setLevel(andParent.getLevel());
							
							newLeaf.setToLeaf();
							
							//newLeaf.setFunctionValue(andChild_0.getFunctionValue() * 0.5);
							
							this.addToLevel(newLeaf.getLevel(), newLeaf);
							
							newLeaf.addDontCare(nOR.getVariable());
							
							ArrayList<int[]> samples = new ArrayList<int[]>(andChild_0.getSamples());
							
							samples.addAll(andChild_1.getSamples());
							
							newLeaf.addSamples(samples);
							
							if(!andChild_0.getDontCares().containsAll(andChild_1.getDontCares()))
								;//Util.halt("The AND children don't have same dont cares...");
							
							newLeaf.addDontCares(andChild_0.getDontCares());
							
							newLeaf.addDontCares(andChild_1.getDontCares());
							
							newLeaf.computeZ();
							
							newLeaf.setFunctionValue((andChild_0.getFunctionValue()+andChild_1.getFunctionValue())/2);
							
							this.replace(andParent, newLeaf);
							
					
							//now run validation on the pruned tree							
							
							double LL_after = this.computeLL(validationDS)/validationDS.size();
													
							if(LL_after >= LL_before){//keep the pruned version of the tree
								
								if(andParent.parentCount() > 0)
									Util.halt("Non zero andParent of AND andParent!");
								
								this.deleteFromLevel(andParent.getLevel(), andParent);
								
								this.deleteFromLevel(nOR.getLevel(), nOR);
								
								node.setAsPruned();
								
								pruneCount++;						
								
								LL_before = LL_after;
								//if the i-th level gets deleted due to pruning then break from current loop and proceed to the next level
								if(!this.levelExists(i))
									break;
								
								//newLeaf.print();
							}
							
							//if the likelihood decreases then unprune
							else{	
								
								this.replace(newLeaf, andParent);
								
								if(newLeaf.parentCount() > 0)
									Util.halt("Inspect newLeaf's andParent!");
								
								this.addToLevel(andParent.getLevel(), andParent);
								
								this.addToLevel(nOR.getLevel(), nOR);
								
								this.deleteFromLevel(newLeaf.getLevel(), newLeaf);
								
								j++;
							}
						
						}//if uniform distribution
						
						else
							j++;
						
					}		
				
				}//end of for j=0 to levelNodes.size()					
								
			}//end of for i=maxLevel to 0		
			
			System.out.println("Prune count : "+pruneCount);
			this.updateLevelInfo(this.getRoot(), 0);
		}
		
		/**
		 * given a set of variables, this method sets the evidence variables to
		 * the given evidence values - must be called before probOfEvidence()
		 * @param evidVars
		 */
		void setEvidence(ArrayList<Variable> eVars, int[] eValues){
			
			for(int i=0; i<eVars.size(); i++){
				
				Variable scopeVar = eVars.get(i);//this.getScope(eVars.get(i));
				
				scopeVar.setValue(eValues[scopeVar.getLabel()]);
			}
			
		}		
		
		/**
		 * Traverses the tree and returns the probability of evidence
		 * @return
		 */
		double probOfEvidence(Node node){
			
			DecimalFormat df = new DecimalFormat("0.000");
			
			//System.out.println("Computing probability of evidence...");   	
			if(node.isOR()){
				
				ORNode nOR = node.castToOR();
				//System.out.println("OR node : "+nOR.getLabel());
				//Util.halt();
				if(nOR.isEvidence()){
					
					int eValue = nOR.getVariable().getValue();
								
					if(nOR.getEvidChild(eValue) == null){
						
						System.out.println("Returning z from OR : "+0.0);
						return 0.0;
					}
					else{
						
						//System.out.print(df.format(nOR.getWeight(eValue))+" ");
						return nOR.getWeight(eValue) * probOfEvidence(nOR.getEvidChild(eValue));
					}	
				}//end of if
				
				else{
					
					double z = 0.0;
					//System.out.println("Not an evidence...");
					for(int i=0; i<nOR.childCount(); i++){
						
						z = z + (nOR.getWeight(i) * probOfEvidence(nOR.getChild(i))); 
					} 			
					
					//System.out.print(df.format(z)+" ");
					if(z == 0.0)
						Util.halt("z of non-evidence OR node is zero!");
					
					return z;
				
				}//end of else
				
			}//end of if
			
			else if(node.isAND()){
				
				double z = 1.0;
				
				ANDNode nAND = node.castToAND();
				//System.out.println("AND node : "+nAND.getLabel());
				
				for(int i=0; i<nAND.childCount(); i++){
					
					z = z * probOfEvidence(nAND.getChild(i));
				}
				
				//Whenever a leaf is encountered, get the assignment of the don't care variables
				
				z = z * nAND.getFunctionValue();
				
				if(nAND.isLeaf()){
					
					//nAND.print();
					
					//if(nAND.isUniform()){
						
					
						
					//}
					
					if(nAND.getDistribution() != null){//else{
						
						
						Distribution Q = nAND.getDistribution();
						
											
						int[] dontCareAssignment = new int[Q.scopeSize()];
						
						
						
						for(int i=0; i<Q.scopeSize(); i++){
							
							Variable dontCare_i = Q.getScope(i);//nAND.getDontCare(i);
							
							dontCareAssignment[i] = dontCare_i.getValue();
						}
						
						//System.out.println("Dont care variables : "+Variable.getLabels(nAND.getDontCares())+" Assignment : "+Arrays.toString(dontCareAssignment));
						
						//long value = Util.toInteger(dontCareAssignment);
						
						//long hamdist = Util.hamdist(nAND.getConstantSample(), value);
						
						
						
						//System.out.println(nAND.isUniform());
						
						//z = z * nAND.getDistribution().getParameter(hamdist);
						int hamdist = Util.hamdist(nAND.getConstantSample(), Arrays.toString(dontCareAssignment));
						//System.out.println("Leaf sample : "+nAND.getConstantSample()+" Prob : "+nAND.getFunctionValue()+" Ham dist : "+hamdist);
						
						
						double prob = -1.0;//
						
						/*if(Q.isUniform()){
							
							prob = Q.getParameter(0) ;
						}
						
						else{*/
							
							
							prob = nAND.getDistribution().getParameter(hamdist);
						//}
						
						if(prob == 0.0){
							
							//Util.halt("Leaf weight is 0.0 : "+hamdist+" "+nAND.getDistribution().getParameter(hamdist));
							Util.halt("Exception in ORTree.probOfEvidence() : 0.0 probability! "); 
						}
						
						z = z * prob;//nAND.getFunctionValue() * Math.pow(Math.E, -1 * hamdist);
						
						//System.out.println("sample prob : "+nAND.getFunctionValue() * Math.pow(Math.E, -1 * hamdist));
					
					}				
					
				}
				
				
				
				//z = z * nAND.getFunctionValue();//Math.pow(nAND.getFunctionValue(), nAND.dontCareCount());
				
				//System.out.println("AND function value : "+nAND.getFunctionValue());
				
				if(z == 0.0){
					
					
					Util.halt("AND node function value :"+nAND.getFunctionValue());
					nAND.print();
					
				}	
				
				return z;
				
			}		
	

			else{
			
				System.out.println("Exception in probOfEvidence() : Generic node type encountered!");
				return 0.0;
			}
			
		}//end of probOfEvid()
		
	
		double probOfEvidence_(Node node, int pathLength){
			
			DecimalFormat df = new DecimalFormat("0.000");
			
			//System.out.println("Computing probability of evidence...");   	
			if(node.isOR()){
				
				pathLength++;
				ORNode nOR = node.castToOR();
				//System.out.println("OR node : "+nOR.getLabel());
				//Util.halt();
				if(nOR.isEvidence()){
					
					int eValue = nOR.getVariable().getValue();
								
					if(nOR.getEvidChild(eValue) == null){
						
						System.out.println("Returning z from OR : "+0.0);
						return 0.0;
					}
					else{
						
						//System.out.print(df.format(nOR.getWeight(eValue))+" ");
						return nOR.getWeight(eValue) * probOfEvidence_(nOR.getEvidChild(eValue), pathLength);
					}	
				}//end of if
				
				else{
					
					double z = 0.0;
					//System.out.println("Not an evidence...");
					for(int i=0; i<nOR.childCount(); i++){
						
						z = z + (nOR.getWeight(i) * probOfEvidence_(nOR.getChild(i), pathLength)); 
					} 			
					
					//System.out.print(df.format(z)+" ");
					if(z == 0.0)
						Util.halt("z of non-evidence OR node is zero!");
					
					return z;
				
				}//end of else
				
			}//end of if
			
			else if(node.isAND()){
				
				double z = 1.0;
				
				ANDNode nAND = node.castToAND();
				//System.out.println("AND node : "+nAND.getLabel());
				
				for(int i=0; i<nAND.childCount(); i++){
					
					z = z * probOfEvidence_(nAND.getChild(i), pathLength);
				}
				
				//Whenever a leaf is encountered, get the assignment of the don't care variables
				
				//z = z * Math.pow(nAND.getFunctionValue(), nAND.dontCareCount());
				
				if(nAND.isLeaf()){
					
									
					if(nAND.getDataset() != null){
						
						double minDistance, weight = 1.0;
						
						double euclideanDist = 0.0;//Util.euclideanDist(Arrays.toString(sample_i), Arrays.toString(dontCareAssignment));
						
						double distance  = 0.0;
						
						Dataset ds = nAND.getDataset();
						
						int[] dontCareAssignment = new int[ds.scopeSize()];			
						
						for(int i=0; i<dontCareAssignment.length; i++){
							
							Variable dontCare_i = ds.getVariable(i);//nAND.getDontCare(i);
							
							dontCareAssignment[i] = dontCare_i.getValue();
						}
											
						
						if(ds.size() == 1){
														
							int[] sample_0 = ds.getDataInstance(ds.getDataIndex(0));
							
							//Util.halt("dataset size is 1");
														
							int randFeatureCount = (int) (ds.scopeSize()/2);// + Math.random() * (ds.scopeSize()/2));
							
							ArrayList<Integer> randFeatureIndices = new ArrayList<Integer>();
							
							for(int k=0; k<randFeatureCount;){
								
								int n = 0 + (int)(Math.random()*(ds.scopeSize()));
								
								if(!randFeatureIndices.contains(n)){
									
									randFeatureIndices.add(n);
									k++;
								}		
							}	
							
							//for each sample in the dataset, compute its Euclidean distance from the evidence				
							
							//randomly select N number of features to compute the distance from and to consider as
							//don't cares
							
							for(int i=0; i<ds.size(); i++){
								
								int[] sample_i = ds.getDataInstance(ds.getDataIndex(i));
								
								euclideanDist = 0.0;//Util.euclideanDist(Arrays.toString(sample_i), Arrays.toString(dontCareAssignment));
																
								for(int n=0; n<randFeatureCount; n++){
										
									if(sample_i[ds.getVariable(randFeatureIndices.get(n)).getLabel()] != dontCareAssignment[randFeatureIndices.get(n)])
										euclideanDist++;								
								}					
								
								euclideanDist = Math.sqrt(euclideanDist);

							}
							
							if(euclideanDist == 0.0){
								
								weight = (nAND.getFunctionValue()*0.01) * Math.pow(Math.E, -1 * euclideanDist);
							}
							
							else
								weight = (nAND.getFunctionValue()) * Math.pow(Math.E, -1 * euclideanDist);
							
							//System.out.println("F(.) = "+nAND.getFunctionValue()+" Distance = "+euclideanDist+" Weight = "+weight);
						}
						
						
						/*else if(ds.size() > 1){							
						
							minDistance = 10000.0;					
								
							int randFeatureCount = (int) (ds.scopeSize()/2 + Math.random() * (ds.scopeSize()/2));
							
							ArrayList<Integer> randFeatureIndices = new ArrayList<Integer>();
							
							for(int k=0; k<randFeatureCount;){
								
								int n = 0 + (int)(Math.random()*(ds.scopeSize()));
								
								if(!randFeatureIndices.contains(n)){
									
									randFeatureIndices.add(n);
									k++;
								}		
							}	
							
							//for each sample in the dataset, compute its Euclidean distance from the evidence				
							
							//randomly select N number of features to compute the distance from and to consider as
							//don't cares
							
							for(int i=0; i<ds.size(); i++){
								
								int[] sample_i = ds.getDataInstance(ds.getDataIndex(i));
								
								euclideanDist = 0.0;//Util.euclideanDist(Arrays.toString(sample_i), Arrays.toString(dontCareAssignment));
																
								for(int n=0; n<randFeatureCount; n++){
										
									if(sample_i[ds.getVariable(randFeatureIndices.get(n)).getLabel()] != dontCareAssignment[randFeatureIndices.get(n)])
										euclideanDist++;								
								}
								
								
								euclideanDist = Math.sqrt(euclideanDist);
								
								if(euclideanDist < minDistance)
									minDistance = euclideanDist;				
							}
							
							//System.out.println("F(.) = "+nAND.getFunctionValue()+" Distance = "+euclideanDist+" Feature # = "+randFeatureCount);
													
							weight = (nAND.getFunctionValue()) * Math.pow(2, -1 * minDistance);
							//System.out.println(minDistance);
							//Util.halt();
							//Util.halt("Dontcare count : "+nAND.dontCareCount());						
						}*/
						
						else{
							
							int randDontCareCount = 0;
							
							if(ds.scopeSize()/this.variableCount > 0.5)
								
								randDontCareCount = (int) (ds.scopeSize() * 0.25);
							else
								
								randDontCareCount = (int) (ds.scopeSize()/2);// + Math.random() * (ds.scopeSize()/2));
							
							//Util.halt("empty dataset...");
							
							weight = weight * Math.pow(nAND.getFunctionValue(), randDontCareCount);
							
						}
						
						
						
						z = z * weight * Math.pow(2, nAND.dontCareCount());							
					
					}
					
					else{
						
						z = z * Math.pow(nAND.getFunctionValue(), nAND.dontCareCount());
						
					}	
				}
				
							
						
				return z;
				
			}		
	

			else{
			
				System.out.println("Exception in probOfEvidence() : Generic node type encountered!");
				return 0.0;
			}
			
		}//end of probOfEvid()

		
		
		
		double computeLL(Dataset ds){
			
			double[] LL = new double[ds.size()];
			double logLikelihood = 0.0;
			double probOfevidence = 0.0;
			
			int countZeroProbs = 0;
			
			ArrayList<Variable> evidVars = ds.getVariables();
			//System.out.println("Evidence # : "+evidVars.size()+" Variables : "+Variable.getLabels(evidVars));
			//Util.halt();
			
			
			for(int i=0; i<ds.size(); i++){
				
				int[] evidence = ds.getDataInstance(i);
				
				//System.out.println("Evidence : "+Arrays.toString(evidence));
				
				this.setEvidence(evidVars, evidence);
				
				probOfevidence = this.probOfEvidence_(this.getRoot(), 0);
				
				//System.out.println("Probability of sample "+i+" = "+probOfevidence);
				//System.out.println("Probability of evidence "+i+" "+probOfevidence);//Arrays.toString(evidence)+" : "+probOfevidence);
				//System.out.println("i:"+i);
				
				if(probOfevidence == 0.0){					
					
					//Util.halt("Probability of sample is 0.0");
					countZeroProbs++;
					//continue;					
				}
				
				else
					;//Util.halt();
				
				//Util.halt();
				LL[i] = probOfevidence; 
				logLikelihood = logLikelihood + (Math.log10(probOfevidence)/Math.log10(2));
			}		
			
			if(countZeroProbs > 0)
				System.out.println("Number of zero probability samples : "+countZeroProbs);
			
			return logLikelihood;
			
		}//end of computeLL()
			
		
		//computes the average tree of this tree and the given tree
		//assumption : both trees are built accroding to a fixed random order
		void average(ORTree tree){
			
				int levelCount = this.levelCount();
				
				for(int i=0; i<levelCount; i++){
					
					if(i%2 == 0)
						averageLevels(this.getLevel(i), tree.getLevel(i));
				}
		}
		
		void averageLevels(ArrayList<Node> l1, ArrayList<Node> l2){
			
			int min = l1.size() < l2.size() ? l1.size() : l2.size();
			
			for(int i=0; i<min; i++){
				
				Node n1 = l1.get(i);
				Node n2 = l2.get(i);
				
				if(n1.getLabel() != n2.getLabel())
					
					continue;
				
				if(n1.childCount() != n2.childCount())
					continue;
				
				else{
					
					for(int j=0; j<n1.childCount(); j++){
						
						double avg_weight = (n1.getWeight(j) + n2.getWeight(j))/2;
						n1.setWeight(j, avg_weight);
								
					}
				}
			}
		}	
		
	
		Distribution getDistribution(Node node, double weight, Distribution P){
			
			if(node.isOR()){
				
				ORNode nOR = node.castToOR();				
				//Add the OR node variable to the scope
				if(!P.getScope().contains(nOR.getVariable()))
					P.addToScope(nOR.getVariable());
								
				for(int i=0; i<nOR.childCount(); i++){
					
					double weight_i = weight * nOR.getWeight(i);//nOR.getChild(i).getLabel());
					getDistribution(nOR.getChild(i), weight_i, P);					
					//P.addParameter(weight_i);
				}
				
				return P;
			}
			
			else{
				
				ANDNode nAND = node.castToAND();
				//everytime a leaf is visited add the product of the weights in the path from the root to the leaf
				if(nAND.isLeaf()){
					
					weight = weight * 1.0;//(1/Math.pow(2, nAND.dontCareCount()));
					P.addParameter(weight);
					return P;
				}					
				
				for(int i=0; i<nAND.childCount(); i++){
					
					weight = weight * 1.0;//1/Math.pow(2, nAND.dontCareCount());
					getDistribution(nAND.getChild(i), weight, P);
					//P.addParameter(weight);
				}				
				
				return P;
			}
			
		}

		
		void mergeTopDown(Dataset validationDS){
			
			System.out.println("mergeTopDown()...");
			
			for(int i=0; i<this.levelCount(); i++){
				
				if(!this.levelExists(i))
					continue;
				
				else{
					
					ArrayList<Node> levelNodes = this.getLevel(i);
					
					for(int j=0; j<levelNodes.size(); j++){
						
						Node node_j = levelNodes.get(j);
						
						if(!node_j.isOR())
							continue;
						
						Distribution Pj = this.getDistribution(node_j, 1.0, new Distribution());
						
						for(int k=j+1; k<levelNodes.size();){
							
							Node node_k = levelNodes.get(k);
							
							if(!node_k.isOR()){
								k++;
								continue;
							}	
							
							if(node_k.getLabel() != node_j.getLabel()){
								k++;
								continue;
							}	
							
							Distribution Pk = this.getDistribution(node_k, 1.0, new Distribution());
							
							if(!Pj.getScope().containsAll(Pk.getScope())){
								k++;
								continue;
							}
							
							else if(Pj.parameterCount() != Pk.parameterCount()){
								k++;
								continue;
							}	
							
							double LL_before_merge = this.computeLL(validationDS)/validationDS.size();
							
							//System.out.println("Likelihood before merging : "+LL_before_merge);
							
							if(Pj.distance(Pk) <= Math.pow(2, -0.01*node_k.getLevel())){
								
								//then merge
								
								ArrayList<Node> parents = new ArrayList<Node>(node_k.getParents());
								
								for(int l=0; l<parents.size(); l++){
									
									Node parent_k = parents.get(l);
									parent_k.deleteChild(node_k);
									parent_k.addChild(node_j);		
									
									
								}								
								
								double LL_after_merge = this.computeLL(validationDS)/validationDS.size();
								
								//System.out.println("Likelihood after merging : "+LL_after_merge);
								
								if(LL_after_merge > LL_before_merge){//keep the pruned version of the tree and recurse
									
									//System.out.println("Merging OR nodes at level "+i);
									//Util.halt("Likelihood before merging : "+LL_before_merge+" after merge : "+LL_after_merge);
									this.deleteSubtree(node_k);
								}
								
								
								else{
									
									for(int l=0; l<parents.size(); l++){
										
										Node parent_k = parents.get(l);
										parent_k.deleteChild(node_j);
										parent_k.addChild(node_k);										
									}
									
									k++;
								}								
								
							}//end of merge
							
							else
								k++;
							
						}//for k
						
					}//for j
				
				}//else
				
			}//for i			
			
			System.out.println("Merging complete...");
			
		}//end of merge()	
		
		
		void getErrorDistribution(){
			
			
			for(int i=1; i<this.levelCount(); i++){
			
				double error = (1/Math.pow(2, this.levelCount()-i));
			
				System.out.println("Acceptable error at level "+i+" is "+error);
			
			}
			
				
		}
		
		void mergeBottomUp(Dataset validationDS){
			
			
			int maxLevel = this.levelCount()-1;
			
			ArrayList<Node> deletedNodes = new ArrayList<Node>();
			
			System.out.println("mergeBottomUp() : ");
			
			this.clearEvidence(this.getRoot());
			
			double LL_before_merge = this.computeLL(validationDS)/validationDS.size();;
			
			double LL_after_merge = this.computeLL(validationDS)/validationDS.size();
			
			for(int i = maxLevel; i>=1; i--){
							
				int mergeCount = 0;
				
				if(this.levelExists(i)){					
					
					System.out.println("Merging Level : "+i+" Node # : "+this.getLevel(i).size());
					
					//ArrayList<Node> levelNodes = this.getLevel(i);
					
					for(int j=0; j<this.getLevel(i).size(); j++){
						
						Node node_j = this.getLevel(i).get(j);
						
						
						for(int k=j+1; k<this.getLevel(i).size();)
						{				
							
							Node node_k = this.getLevel(i).get(k);
							
							//System.out.println("k = "+k);
							
							if(deletedNodes.contains(node_k)){
								
								Util.halt("Node already deleted!");
							}
							
							
							double eBound = 0.001;//(1/Math.pow(2, maxLevel-i));
							
							boolean unifiable = node_j.isUnifiable(node_k, eBound);//nOR_j.isUnifiable(nOR_k, eBound); 
							
							if(unifiable){
								
								//make a new merged node, so that it is easier to delete if the LL does not improve
								
								Node newNode = node_j.merge(node_k);
								
								ArrayList<Node> node_j_parents = new ArrayList<Node>(node_j.getParents());
								
								ArrayList<Node> node_k_parents = new ArrayList<Node>(node_k.getParents());
								
								node_j.deleteAllParents();
								
								node_k.deleteAllParents();
								
													
								/*for(int pi=0; pi<node_k_parents.size(); pi++){
									
									Node parent_i = node_k_parents.get(pi);
									
									parent_i.deleteChild(node_k);
									
									parent_i.addChild(node_j);
								}*/							
																
								LL_after_merge = this.computeLL(validationDS)/validationDS.size();
								
								if(LL_after_merge >= LL_before_merge){
									
									//keep the merging
									System.out.println("Merging nodes...");									
									
									if(LL_after_merge >  LL_before_merge){
										
										//System.out.println("Likelihood increases....");
									}							
									
									this.addToLevel(newNode.getLevel(), newNode);
									mergeCount++;
									
									this.deleteNode(node_k);
									this.deleteNode(node_j);
									
									deletedNodes.add(node_k);
									deletedNodes.add(node_j);
									//node_j.setAsMerged();									
									LL_before_merge = LL_after_merge;
									k++;
									
								}	
								
								else{
									
									/*for(int pi=0; pi<node_k_parents.size(); pi++){
										
										Node parent_i = node_k_parents.get(pi);										
										parent_i.deleteChild(node_j);										
										parent_i.addChild(node_k);
									}*/								
									node_j.addParents(node_j_parents);
									node_k.addParents(node_k_parents);
									
									//for each parent of the nodes  j and k, delete the newly added node
									newNode.deleteAllParents();									
									k++;									
								}								
								
								
							}//if nodes are unifiable							
							
							else
								k++;
							
						}//for k=j+1 to levelnodes.size()				
				
					}//for j=0 to levelnodes.size()
					
				}//if the i-th level exists	
				
				//System.out.println("# of mergings at level "+i+" "+ mergeCount);
			
			}//for i=maxLevel to 1
		}
		
		
		/**
		 * Merges a tree bottom up
		 * @param validationDS
		 */
		/*void mergeBottomUp(Dataset validationDS){
			
			int maxLevel = this.levelCount();
			
			System.out.println("mergeBottomUp() : ");
			
			for(int i = maxLevel; i>=1; i--){
				
				//System.out.println("Number of nodes before merging : "+this.getLevel(i).size());			
					
				//System.out.println("Merging Level "+i+"...");
				
				
				if(this.levelExists(i))
				{	
					
					ArrayList<Node> levelNodes = this.getLevel(i);			
					
					Distribution[] nodeDistributions = new Distribution[levelNodes.size()];
					
					for(int j=0; j<levelNodes.size(); j++){
						
						if(!levelNodes.get(j).isOR()){//set the node distributions of AND nodes to null
							nodeDistributions[j] = null;
							continue;
						}	
						else
							//nodeDistributions.add(this.getDistribution(levelNodes.get(j), 1.0, new Distribution()));
							nodeDistributions[j] =  this.getDistribution(levelNodes.get(j), 1.0, new Distribution());
					
					}					
									
					for(int j=0; j<levelNodes.size(); j++){
						
						//double[] distances = new double[nodeDistributions.length];
						
						if(nodeDistributions[j] == null)
							continue;
						
						
						//double[] distances = new double[levelNodes.size() - j + 1];
						double[] distances = new double[nodeDistributions.length];
						//initialize to highes distance - AND nodes??
						for(int m=0; m<distances.length; m++){
							
							distances[m] = 1.00;
						}					
						
						for(int k=j+1; k<levelNodes.size(); k++){
							
							if(nodeDistributions[k] == null)
								continue;
							
							//the hellinger/MSE distance between node j and node k
							double distance_jk = nodeDistributions[j].distance(nodeDistributions[k]);
							
							distances[k] = distance_jk; 
							
						}
						
						double eBound = 1/Math.pow(Math.E, i*0.1);//Math.pow(Math.E, i*0.1);
						
						this.mergeExact(i, levelNodes.get(j), distances, nodeDistributions);
						this.mergeApproximate(i, levelNodes.get(j), distances, 0.001, nodeDistributions, validationDS);
						
					
					}
				
				
				}
				
			}
			
			
		}*/
				/**
				 * for(int j=0; j<levelNodes.size(); j++){
				 
					
					Node node_j = levelNodes.get(j);
					
					if(!node_j.isOR()){
						continue;
					}
					
					//get distribution of all the OR nodes in this level.
					
									
					Distribution Pj = this.getDistribution(node_j, 1.0, new Distribution());
					
					nodeDistributions.add(Pj);
					
					//System.out.println("i-th tree...");
					//node_i.print();
					
					double[] distances = new double[this.getLevel(i).size()];
					
					for(int m=0; m<distances.length; m++){
						
						distances[m] = -1.00;
					}
					
					for(int k=j+1; k<levelNodes.size();k++){				
						
						Node node_k = levelNodes.get(k);
									
						if(!node_k.isOR()){
							//k++;
							continue;
						}	
						
						if(node_k.getLabel() != node_j.getLabel()){
							//k++;
							continue;
						}	
						
						
						//double LL_before_merge = this.computeLL(validationDS)/validationDS.size();
						
						
						Distribution Pk = this.getDistribution(node_k, 1.0, new Distribution());
						
						nodeDistributions.add(Pk);
						
						double distance_jk = Pj.distance(Pk);
						
						distances[k] = distance_jk; 
							
					
					}
									
					this.mergeExact(i, node_j, distances);
					this.mergeApproximate(i, node_j, distances, 0.001);			
				
				}//for j	
			
				//this.draw("level"+i, "Drawing merged level...");
				//Util.halt();
				
			//System.out.println("Number of nodes after merging : "+this.getLevel(i).size());
			
		}//for i		
			
		System.out.println("Merging complete...");
			
	}//end of mergeBottomUp
		
	/**
	 * 	Given the level of this tree and the hellinger distance between
	 * the OR trees at this level, this method performs an exact merging of 
	 * the trees
	 */
	void mergeExact(int level, Node node, double[] divergences, Distribution[] distributions){
		
		for(int k=1; k<this.getLevel(level).size(); ){
			
			if(divergences[k] == 0.0){
				
				//System.out.println("Merging nodes...");
				
				//deletedIndices.add(k);
				
				node.setAsMerged();
				
				Node node_k = this.getLevel(level).get(k);
				
				ArrayList<Node> parents = new ArrayList<Node>(node_k.getParents());
				
				for(int l=0; l<parents.size(); l++){
					
					Node parent_k = parents.get(l);
					parent_k.deleteChild(node_k);
					parent_k.addChild(node);								
				}
				
				this.deleteSubtree(node_k);
				distributions[k] = null;
				divergences[k] = 1.0;
				k++;
			}
			
			else
				k++;
			
		}	
		
	}//end of mergeExact
	
	
	void mergeApproximate(int level, Node node, double[] divergences, double eBound, Distribution[] distributions, Dataset validationDS){
		
		for(int k=1; k<this.getLevel(level).size(); ){
			
			if(divergences[k] <= eBound){
				
				//System.out.println("Merging nodes...divergence[k]="+divergences[k]+" \t error bound="+eBound);
				
				//deletedIndices.add(k);
				
				double LL_before_merge = this.computeLL(validationDS);
				
				node.setAsMerged();
				
				Node node_k = this.getLevel(level).get(k);
				
				ArrayList<Node> parents = new ArrayList<Node>(node_k.getParents());
				
				//this.average(node, node_k);
				
				for(int l=0; l<parents.size(); l++){
					
					Node parent_k = parents.get(l);
					parent_k.deleteChild(node_k);
					parent_k.addChild(node);								
				}
				
				double LL_after_merge = this.computeLL(validationDS);
				
				if(LL_after_merge > LL_before_merge){
					
					//keep the merging
					
					this.deleteSubtree(node_k);
					distributions[k] = null;
					k++;
				}
				
				else{
					
					for(int l=0; l<parents.size(); l++){
						
						Node parent_k = parents.get(l);
						parent_k.deleteChild(node);
						parent_k.addChild(node_k);								
					}
					
					k++;
				}
				
				
			}
			
			else
				k++;
			
		}		
		
	}
	
	void average(Node n1, Node n2){
		
		if(n1.isLeaf() && n2.isLeaf())
			return;
		
		if((n1.isLeaf() && !n2.isLeaf()) || (!n1.isLeaf() && n2.isLeaf())){
			
			//Util.halt("Exception in ORTree.average() : trees are not isomorphic!");
			return;
			
		}
		
		if(n1.isOR() && n2.isOR()){
			
			
			if(n1.getLabel() != n2.getLabel()){
				
				Util.halt("Exception in ORTree.average() : OR nodes don't have the same label!");
			}
			
			if(n1.childCount() != n2.childCount())
				Util.halt("Exception in ORTree.average() : OR nodes don't have the same child counts!");
			
			else{
				
				for(int j=0; j<n1.childCount(); j++){
					
					if(n1.getWeight(j) > n2.getWeight(j))
						continue;
					else
						n1.setWeight(j, n2.getWeight(j));
					//double avg_weight = (n1.getWeight(j) + n2.getWeight(j))/2;
					//n1.setWeight(j, avg_weight);
							
				}
				
				n1.castToOR().normalize();
			}
			
			if(n1.getChildren().containsAll(n2.getChildren()))
				return;
			
			else{
				
				for(int i=0; i<n1.childCount(); i++)
					this.average(n1.getChild(i), n2.getChild(i));
			}
			
		}		
		
	}

}

/*
boolean continueTree_ = true; 

Random rand = new Random();

//System.out.println("Entropy : "+entropy +" Dataset size : "+ds.size());

if(entropy == 0.0 && ds.size() > 10){
	
	//continueTree_ = rand.nextBoolean();
	//Util.halt(continueTree_+"");
}

else if(entropy == 0.0 && ds.size() <= 1 || ds.scopeSize() == 0)
	continueTree_ = false;

if(continueTree_){
	
	//System.out.println("continuing tree...");
	
	Variable var = null;
	
	if(this.is_sFlag()){		//get the next variable from the user defined static ordering
		
		var = this.Order.get(nextInOrder++);
		//System.out.println("Chosen variable : "+var.getLabel());
	}
	
	else{
		
		var = ds.getMaxEntropyVar();
		
		int split_0 = ds.count(var.getLabel(), 0);
		
		int split_1 = ds.count(var.getLabel(), 1);
		
		if(split_0 < 2 || split_1 < 2){
			
			newAND.setToLeaf();				
			newAND.addDataset(ds);
			
			if(ds.scopeSize() < (int)(this.variableCount * 0.25)){
				
				//Util.halt("DONT CARE COUNT = "+newAND.dontCareCount());
				newAND.setFunctionValue(0.5);
				
			}	
			
			else
				newAND.setFunctionValue(0.005);
			
						
			newAND.computeZ();
			this.leafCount++;
			return newAND;
			
		}
			
			
		
		ORNode child = createOR(ds, var, level+1);			
	
		newAND.addChild(child);
	
		//add the marginal distribution of each child OR node
		//newAND.addDistribution(child.getDistribution());
		newAND.setFunctionValue(1.0);		
		newAND.computeZ();			
		
	}	
	
	return newAND;
	
}

else{
	
	newAND.setToLeaf();				
	newAND.addDataset(ds);
	
	if(ds.scopeSize() < (int)(this.variableCount * 0.25)){
		
		//Util.halt("DONT CARE COUNT = "+newAND.dontCareCount());
		newAND.setFunctionValue(0.5);
		
	}	
	
	else
		newAND.setFunctionValue(0.1);
	
				
	newAND.computeZ();
	this.leafCount++;
	return newAND;						
}*/			

