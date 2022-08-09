package imsam.mgen;


public class AcyclicModelGenerator extends MGen {

    
    @Override
    protected void generateModel() {
        stateSpace = new State[numberOfStates];
        boolean[][] connections = new boolean[numberOfStates][numberOfStates];

        // Initialize State objects
        logger.debug("Initializing state space");
        for (int stateId=0; stateId<numberOfStates; stateId++) {
            stateSpace[stateId] = new State(stateId);
            for(int y = 0; y < numberOfStates; y++){
                connections[stateId][y] = false;    //initialize record of connections as no connections
            }
        }

        logger.debug("Generating transitions");     //Generates Transitions that only go to larger value states
        for (int stateId=0; stateId<numberOfStates; stateId++) {
            int transitionCount = (int) transitionCountDistribution.random();
            logger.trace("Generating " + transitionCount + " transitions for state " + stateId);
            for (int transitionId = 0; transitionId < transitionCount; transitionId++) {
                int successor = (int) (Math.random() * numberOfStates);
                if (stateId < successor) {
                    TransitionPath transition = new TransitionPath(
                            stateId,
                            successor, 
                            transitionRateDistribution.random()
                    );
                    stateSpace[stateId].transitionsOut.add(transition);
                    stateSpace[successor].transitionsIn.add(transition);
                    connections[successor][stateId] = true;
                }
            }
        }
 
        
        int current;
        for(int stateId = numberOfStates; stateId > 0; stateId--){  //checks all states to see if they connect to state 0
            current = stateId;
            
            for(int ancestor = current-1; current != 0; current = ancestor){ //goes through array from state current until it reaches the state 0
                
                while((connections[current][ancestor] != true) && (ancestor != -1)){
                    ancestor--;
                }
                if((ancestor == -1) && (connections[current][ancestor] != true)){    //if failure to find ancestor, create one
                        int NewAncestor = numberOfStates + 1;
                    do{
                        NewAncestor = (int) (Math.random() * (numberOfStates - stateId));       //Find a state that's smaller than current
                    }while(current < NewAncestor);
                    TransitionPath transition = new TransitionPath(                 //Make the found lesser state the new ancestor
                        NewAncestor,
                        current, 
                        transitionRateDistribution.random()
                    );

                stateSpace[stateId].transitionsOut.add(transition);     //Record new Transition in state space and connection array
                stateSpace[NewAncestor].transitionsIn.add(transition);
                connections[NewAncestor][stateId] = true;
                }
            }
           
        }
        
    }

}
