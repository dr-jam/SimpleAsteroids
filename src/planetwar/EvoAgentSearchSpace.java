package planetwar;

import evodef.*;
import evogame.Mutator;
import ga.SimpleGA;
import ga.SimpleRMHC;
import ntuple.SlidingMeanEDA;

public class EvoAgentSearchSpace implements NoisySolutionEvaluator, SearchSpace {

    public static void main(String[] args) {
        EvoAgentSearchSpace searchSpace = new EvoAgentSearchSpace();

        int[] point = SearchSpaceUtil.randomPoint(searchSpace);

        System.out.println(searchSpace.report(point));

        System.out.println();
        System.out.println("Size: " + SearchSpaceUtil.size(searchSpace));
    }

    double[] pointMutationRate = {0.0, 1.0, 2.0, 3.0};
    boolean[] flipAtLeastOneBit = {false, true};
    boolean[] useShiftBuffer = {false, true};
    int[] nResamples = {1, 3};
    int[] seqLength = {5, 10, 15, 20};


    public static int tickBudget = 2000;

    int[] nValues = new int[]{pointMutationRate.length, flipAtLeastOneBit.length,
            useShiftBuffer.length, nResamples.length, seqLength.length};
    int nDims = nValues.length;

    static int pointMutationRateIndex = 0;
    static int flipAtLeastOneBitIndex = 1;
    static int useShiftBufferIndex = 2;
    static int nResamplesIndex = 3;
    static int seqLengthIndex = 4;

    // NoisySolutionEvaluator problemEvaluator;

    // log the solutions found
    EvolutionLogger logger;

    public EvoAgentSearchSpace() {
        this.logger = new EvolutionLogger();
    }

    public String report(int[] solution) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("pointMutationRate:     %.2f\n", pointMutationRate[solution[pointMutationRateIndex]]));
        sb.append(String.format("flipAtLeastOneBit:     %s\n", flipAtLeastOneBit[solution[flipAtLeastOneBitIndex]]));
        sb.append(String.format("useShiftBuffer:        %s\n", useShiftBuffer[solution[useShiftBufferIndex]]));
        sb.append(String.format("nResamples:            %d\n", nResamples[solution[nResamplesIndex]]));
        sb.append(String.format("seqLength:             %d\n", seqLength[solution[seqLengthIndex]]));
        sb.append(String.format("nEvals:                %d\n", getNEvals(solution)));
        return sb.toString();
    }

    public int getNEvals(int[] solution) {
        return tickBudget / seqLength[solution[seqLengthIndex]];
    }

//    public EvoAgentSearchSpace setEvaluator(NoisySolutionEvaluator problemEvaluator) {
//        this.problemEvaluator = problemEvaluator;
//        return this;
//    }

    @Override
    public Boolean isOptimal(int[] solution) {
        return null;
    }

    @Override
    public Double trueFitness(int[] solution) {
        return null;
    }

    @Override
    public int nDims() {
        return nDims;
    }

    @Override
    public int nValues(int i) {
        return nValues[i];
    }

    int innerEvals = 2000;

    int nEvals = 0;
    @Override
    public void reset() {
        nEvals = 0;
    }


    @Override
    public double evaluate(int[] x) {

        // create a problem to evaluate this one on ...
        // this should really be set externally, but just doing it this way for now

        GameRunner gameRunner = new GameRunner();

        // search space will need to be set before use
        Mutator mutator = new Mutator(null);
        mutator.pointProb = pointMutationRate[x[pointMutationRateIndex]];
        mutator.flipAtLeastOneValue = flipAtLeastOneBit[x[flipAtLeastOneBitIndex]];
        mutator.totalRandomChaosMutation = false;

        SimpleRMHC simpleRMHC = new SimpleRMHC();
        simpleRMHC.setSamplingRate(nResamples[x[nResamplesIndex]]);
        simpleRMHC.setMutator(mutator);

        EvoAgent evoAgent = new EvoAgent().setEvoAlg(simpleRMHC, getNEvals(x));
        evoAgent.setUseShiftBuffer(useShiftBuffer[x[useShiftBufferIndex]]);
        evoAgent.setSequenceLength(seqLength[x[seqLengthIndex]]);

        EvoAlg evoAlgOpponent = new SimpleRMHC();

        // set up some defaults for opponent
        int nOpponentEvals = 200;
        int opponentSeqLength = 10;

        EvoAgent evoOpponent =
                new EvoAgent().setEvoAlg(evoAlgOpponent, nOpponentEvals).setSequenceLength(opponentSeqLength);

        // setting to false provides a much weaker opponent
        evoOpponent.setUseShiftBuffer(false);

        // now run a game and return the result

        gameRunner.verbose = false;
        gameRunner.reset();
        gameRunner.setPlayers(evoAgent, evoOpponent);
        gameRunner.playGame();

        double fitness = gameRunner.scores.mean();
        double value = 0;

        if (fitness > 0) value = 1;
        if (fitness < 0) value = -1;
        logger.log(fitness, x, false);

        System.out.println("Fitness: " + (int) fitness + " : " + value);
        System.out.println();

        return value;
    }

    @Override
    public boolean optimalFound() {
        return false;
    }

    @Override
    public SearchSpace searchSpace() {
        return this;
    }

    @Override
    public int nEvals() {
        return logger.nEvals();
    }

    @Override
    public EvolutionLogger logger() {
        return logger;
    }

    @Override
    public Double optimalIfKnown() {
        return null;
    }
}
