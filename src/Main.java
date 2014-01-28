import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


public class Main {

	
	static String getLine(BufferedReader br){
		
		String line = "";
		
		do{
			
			try {
				line = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}while(line.equals(""));
		
		return line;
		
	}
	
	public static void main(String[] args){
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			
				//generate 10 random networks
			
				/*for(int i=1; i<=10; i++){
					
					
					BNGenerator bnGen = new BNGenerator();
					
					BayesianNetwork BN = bnGen.generate(10, 10);
					
					String uaiFileName = "BN1\\BN1"+i+".uai";
					
					BN.writeUAI(uaiFileName);
					
					//String uaiFile = "BN\\Bayes"+i+".uai";
					
					GraphicalModel gm = Util.readUAI(uaiFileName);
					
					String pngFile = "BN1\\BN1"+i;
					
					BN.draw(pngFile, "Drawing network"+i, "png");
									
					generateDataSets(gm, i);
				
					
				}
				
				System.exit(0);	*/
				
				//Util.halt();				
				
				System.out.println("Enter the name of the config file : ");
				
				String configFile = br.readLine();
				
				br = new BufferedReader(new FileReader(configFile));
					
				/*{
				  
				 	
				 	String uaiFile = getLine(br);
				 	
					GraphicalModel gm = null;					
					
					gm = Util.readUAI(configFile);
					
					ANDORTree aoTree = new ANDORTree();
					
					Node aoRoot = aoTree.buildTree(gm, null);
					
					aoTree.draw(aoRoot, uaiFile, "", 0);
					
					aoTree.print(aoTree.getRoot(), 0, "");
					
					System.exit(0);
					
				}*/
				
				
				int numberOfExamples = Integer.parseInt(br.readLine());
				
				for(int i=1; i<=numberOfExamples; i++){
				
					String trainFile = getLine(br);//br.readLine();
					String validationFile = getLine(br);
					String testFile = getLine(br);
					//String uaiFile = getLine(br);
					
					Dataset trainingDS = Util.readDataSet(trainFile);
										
					Dataset validationDS = Util.readDataSet(validationFile);					
					validationDS.setVariables(trainingDS.getVariables());
										
					Dataset testDS = Util.readDataSet(testFile);
					testDS.setVariables(trainingDS.getVariables());
					
					//Object GraphicalModel;
					GraphicalModel gm = null;
					
					//gm = Util.readUAI(uaiFile);
					
					//double exactLL = gm.computeLL(testDS)/testDS.size();
					
					double averageLL = 0.0;
					
					double averagePrunedLL = 0.0;
					
					double averageMergedLL = 0.0;
					
					for(int j=0; j<15; j++){
						
						ORTree tree = new ORTree();		
						
						tree.setScope(trainingDS.getVariables());
						
						/*ArrayList<Variable> Order = Variable.getOrder(trainingDS.getVariables());
						
						System.out.println("Random order generated : "+Variable.getLabels(Order));
						//set the order 
						tree.set_sFlag();				
						tree.setOrder(Order);*/
						
						double splitFraction = 0.5;
						
						//System.out.println("split fraction : "+splitFraction);
						
						int splitSize = (int) (trainingDS.size() * splitFraction);				
						
						//ds.split(spliSize)
						
						//System.out.println("Original size : "+ds.size()+" Split size : "+splitSize);
						
						Dataset trainingSplit = trainingDS.splitWOR(splitSize);
						
						tree.learnFullTree(trainingSplit, 0.0);
						
						System.out.println("Level count before validating : "+tree.levelCount());
						
						double treeLL = 0.0;//
						
						//double LL = gm.computeLL(testDS)/testDS.size();
						
						//System.out.println("Exact LL of example "+i+" on test: "+LL);
						
						tree.clearEvidence(tree.getRoot());
						
						//tree.print(tree.getRoot(), 0, "Before testing...");
						
						treeLL = tree.computeLL(testDS)/testDS.size();
						
						//System.out.println("Learned LL of example "+i+" on test: "+treeLL);
						
						
						/*Map<int[], Double> mleWeights = validationDS.getMLEWeights();
						
						Iterator itr = mleWeights.entrySet().iterator();
						
						while(itr.hasNext()){
							
							Entry<int[], Double> entry = (Entry<int[], Double>) itr.next();
							
							System.out.println(entry.getValue());
						}
						
						Util.halt();*/
						
						
						averageLL = averageLL + treeLL;
						
						tree.clearEvidence(tree.getRoot());
						
						//tree.pruneBottomUp(validationDS);					
						
						//double prunedLL = tree.computeLL(testDS)/testDS.size();
						
						//averagePrunedLL = averagePrunedLL + prunedLL;
						
						tree.clearEvidence(tree.getRoot());
						
						//tree.mergeBottomUp(validationDS);					
						
						//double mergedLL = tree.computeLL(testDS)/testDS.size();
						
						//averageMergedLL = averageMergedLL + mergedLL;
						
						
						//System.out.println("Example 	: "+i);
						
						//System.out.println("Exact LL 	: "+exactLL);
						
						System.out.println("ORTree LL 	: "+treeLL);
						
						//System.out.println("Pruned LL 	: "+prunedLL);
						
						//System.out.println("Merged LL 	: "+mergedLL);
						
						//tree.printLeafDontCares();
						
						//
					}
					
						
					System.out.println("Average LL 	: "+averageLL/15);
					
					//System.out.println("Average Pruned LL 	: "+averagePrunedLL/10);
					
					//System.out.println("Average Merged LL 	: "+averageMergedLL/50);
					
					//Util.halt();
				}				
			
		} //try
		
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}
	
	
	public static void main2(String[] args){
		
		double startTime = 0.0, endTime = 0.0, duration = 0.0;
		
		
		ArrayList<Variable> Order = null;
		
		Dataset trainSet = null;
		
		Dataset testSet = null;
		
		BufferedReader br = null;
		
		ORTree orTree = null;
		
		System.out.println("Reading configuration file..."+args[0]);
				
		
		try {
			
			br = new BufferedReader(new InputStreamReader(System.in));
			
			System.out.println("Enter the name of the uai file : ");
			
			String uaiFile = br.readLine();
			
			BayesianNetwork bnOrig =  (BayesianNetwork) Util.readUAI(uaiFile);
			
			bnOrig.constructGraph();
			
			BayesianNetwork bnLearn = new BayesianNetwork();
			
			bnLearn.setType("Bayes");
			
			bnLearn.addVariables(bnOrig.getVariables());
			
			bnLearn.addParents(bnOrig.getParents());
			
			bnLearn.addEmptyPotentials(bnOrig.getPotentials());
			
			bnLearn.constructGraph();
			
			bnLearn.Moralize();
			
			bnLearn.makeUndirected();
				
			bnLearn.draw("gm"+uaiFile, "Drawing graphical model", "png");
			
			System.out.println("Enter the name of the config file : ");
			
			String configFile = br.readLine();
			
			br = new BufferedReader(new FileReader(configFile));
							
			String trainFile = br.readLine();
			
			System.out.println(trainFile);
		
			//create the training dataset
			
			Dataset trainingDS = Util.readDataSet(trainFile);	
					
			ORTree tree = new ORTree();		
			
			tree.setScope(trainingDS.getVariables());
			
			trainingDS.setVariables(tree.getScope());
			
			//ArrayList<Variable> Order = Variable.getOrder(trainingDS.getVariables());
			
			//System.out.println("Random order generated : "+Variable.getLabels(Order));
			//set the order 
			//tree.set_sFlag();				
			//tree.setOrder(Order);
			
			System.out.println("Learning tree...");
			
			startTime = System.nanoTime();
			
			tree.learnFullTree(trainingDS, 0.0);
			
			String validationFile = br.readLine();
			
			Dataset validationDS = Util.readDataSet(validationFile);

			validationDS.setVariables(tree.getScope());
			
			String testFile = br.readLine();
			
			Dataset testDS = Util.readDataSet(testFile);

			testDS.setVariables(tree.getScope());	
			
			double logLikelihood = 0.0;
			
			logLikelihood = bnOrig.computeLL(testDS)/testDS.size();
			
			System.out.println("Exact Likelihood : "+logLikelihood);
			
			
			startTime = System.nanoTime();
			
			bnLearn.learnParameters(trainingDS, 0.1);
			
			endTime = System.nanoTime();
					
			Util.halt("BN Learning Time : "+(endTime-startTime)/1e9);
			
			logLikelihood = bnLearn.computeLL(testDS)/testDS.size();
			
			System.out.println("LearnBN Likelihood : "+logLikelihood);			
			
			System.out.print("Generate random variable order?[y/n]:");
			
			String option = "y";
			
			switch(option){
			
			case 	"y" :
				
				Order = Variable.getOrder(bnOrig.getVariables());						
				System.out.println("Random order generated : "+Variable.getLabels(Order));
				 
				buildORTree(trainingDS, testDS, validationDS, Order);			
				
				break;
				
			case	"n":
				
				System.out.println("Using maxHeuristic ordering...");
				buildORTree(trainingDS, testDS, validationDS, null);
				break;			
				
			
			}
	
			//aoTree = buildANDORTree(bnOrig, Order, uaiFile);
			
			/*logLikelihood = aoTree.computeLL(testSet)/testSet.size();
			
			System.out.println("ANDOR Tree likelihood : "+logLikelihood);
			
			Prune.prune(aoTree);
			
			logLikelihood = aoTree.computeLL(testSet)/testSet.size();
			
			System.out.println("ANDOR Tree likelihood after Pruning : "+logLikelihood);*/
	
			
		}//end of try 
		
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();           
		}
		
		
		
	}//end of main
	
	
	static ORTree buildORTree(Dataset trainSet, Dataset testSet, Dataset validationSet, ArrayList<Variable> Order){
		
		System.out.println("Learning OR tree...");
		
		ORTree tree = new ORTree();
		
		tree.setScope(trainSet.getVariables());
		
		if(Order != null){
			
			tree.set_sFlag();
			tree.setOrder(Order);
			System.out.println("Using order : "+Variable.getLabels(Order));
		}
		
		else{
			
			System.out.println("Using Max-Entropy Heuristic Ordering to build OR tree...");
		}
		
		double startTime = System.nanoTime();
		
		System.out.println("Training set scope : "+Variable.getLabels(trainSet.getVariables()));
		
		tree.learnFullTree(trainSet,0.1);	
		
		double endTime = System.nanoTime();

		//Tree.print(tree.getRoot(), 0, "\nTree before pruning...\n");			
			
		//Util.halt("ORTree Learning Time : "+(endTime-startTime)/1e9);
		
		//tree.draw("original", "Drawing learned tree...");
		
		//Merge.mergeBottomUp(tree);
		
		
		double logLikelihood = tree.computeLL(testSet)/testSet.size();
		
		
		
		//System.out.println("Likelihood before merging : "+logLikelihood);			

		//Merge.mergeBottomUp(tree);
		
		//logLikelihood = tree.computeLL(testSet)/testSet.size();
		
		//System.out.println("Likelihood after merging : "+logLikelihood);		
		
		//tree.draw("mergedTree", "Drawing merged Tree...");
		
		System.out.println("OR learning Likelihood before pruning... : "+logLikelihood);
		
		tree.pruneTopDown(tree.getRoot(), validationSet, new ArrayList<Integer>());
		
		
		tree.draw(tree.getRoot(), "prunedTree", "Drawing pruned Tree...", 1);
		
		logLikelihood = tree.computeLL(testSet)/testSet.size();
		
		System.out.println("OR learning Likelihood after pruning and merging... : "+logLikelihood);
		
		return tree;
		
	}
	
	
	static ANDORTree buildANDORTree(GraphicalModel gm, ArrayList<Variable> Order, String uaiFile){
		
		
		Node root = null;
		
		ANDORTree aoTree = new ANDORTree();
		
		if(Order != null){
			
			aoTree.set_sFlag();		
			
			aoTree.setOrder(Order);		
			
			root = aoTree.buildTree(gm, Variable.getLabels(Order));
		}		
		
		else
			
			root = aoTree.buildTree(gm, null);
		
		System.out.println("Z = "+root.getZ());
		
		//Tree.print(aoTree.getRoot(), 0, "ANDORTree");
		
		aoTree.draw(aoTree.getRoot(), uaiFile, "aoTree", 0);
		
		//Merge.mergeBottomUp(aoTree);
		
	//	aoTree.draw(uaiFile+"Merged", "Merging aoTree");
		
		//Prune.prune(aoTree);
		
		//aoTree.draw(uaiFile+"Prune", "Pruned tree");
		
		//aoTree.print(aoTree.getRoot(), 0, "Pruned Tree...\n");		
		
		
		//aoTree.printOrder();
		
		return aoTree;
		
	}
	
	
	static void generateDataSets(GraphicalModel gm, int id){
		
		((BayesianNetwork) gm).makeUndirected();
		
		System.out.println("Generating datasets...");
		
		int trainingSampleCount = 500;			
		
		Dataset trainSet = ((BayesianNetwork) gm).generateBNSamples(trainingSampleCount);
		
		String trainFile = "BN1\\BN1"+id+".train.data";
		
		trainSet.writeToFile(trainFile);
		
		//trainSet.print("Training set...");
		
		int validationSampleCount = 100;
			
		Dataset validation = ((BayesianNetwork) gm).generateBNSamples(validationSampleCount, trainSet);
		
		String validationFile = "BN1\\BN1"+id+".valid.data";
		
		validation.writeToFile(validationFile);
		
		//validation.print("Validation set...");
		
		int testSampleCount = 200;//
		
		Dataset testSet = ((BayesianNetwork) gm).generateBNSamples(testSampleCount, trainSet);
					
		String testFile = "BN1\\BN1"+id+".test.data";
		
		testSet.writeToFile(testFile);
		
		//testSet.print("Test set...");
		
		System.out.println("Generating datasets completed...");
	}
	

}

/*{
	
	
}*/
/*
 * 		
			/*ArrayList<ORTree> trees = new ArrayList<ORTree>();
			
			for(int i=0; i<10; i++){
				
				ORTree tree_i = new ORTree();		
				
				tree_i.setScope(gm.getVariables());
				
				tree_i.set_sFlag();				
				tree_i.setOrder(Order);
				
				samples = gm.generateBNSamples(N);
				
				Dataset BNtrain_i = new Dataset();
				BNtrain_i.setVariables(gm.getVariables());
				
				for(int t=0; t<N; t++){
					
					BNtrain_i.addDataIndex(t);
					BNtrain_i.addDataInstance(samples.get(t));
				}			
				
				tree_i.learnFullTree(BNtrain_i,pruneTH);
				
				//double LL_i = tree_i.computeLL(BNtest);
				
				//logLikelihood = logLikelihood + LL_i;
				
				//System.out.println("Likelihood : "+LL_i);
				
				//System.out.println("Number of leaves : "+tree_i.leafCount);
				
				trees.add(tree_i);
				
			}*/
			
			//System.out.println("Average Likelihood : "+logLikelihood/10);
			
			/*trees.get(0).printLevels("");
			
			System.out.println();
			
			trees.get(1).printLevels("");
			
			Util.halt();
			
			System.out.println("Likelihood of tree 0 : "+trees.get(0).computeLL(BNtest));
			
			for(int i=1; i<10; i++){
				
				trees.get(0).average(trees.get(i));
			}*/
			
			//double LL_i = trees.get(0).computeLL(BNtest);
			
			//logLikelihood = logLikelihood + LL_i;
			
			//System.out.println("Average tree Likelihood : "+LL_i);*/
			
			
			
		 
