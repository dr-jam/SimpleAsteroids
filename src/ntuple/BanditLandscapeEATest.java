package ntuple;

import evodef.Eval2DNonLinear;
import evodef.EvalMaxM;
import evodef.SearchSpace;
import evodef.SearchSpaceUtil;

import java.util.Arrays;

/**
 * Created by simonmarklucas on 15/06/2017.
 */
public class BanditLandscapeEATest {
    public static void main(String[] args) {
        NTupleBanditEA banditEA = new NTupleBanditEA();

        // set up a 2d grid search space

        int nDims = 2;
        int mValues = 5;
        double noiseLevel = 1.0;
        // EvalMaxM problem = new EvalMaxM(nDims, mValues, noiseLevel);
        Eval2DNonLinear problem = new Eval2DNonLinear(mValues, noiseLevel);

        int nEvals = 100;
        int[] solution = banditEA.runTrial(problem, nEvals);

        report(problem, banditEA.nTupleSystem);

        System.out.println();

        System.out.println("Solution returned: " + Arrays.toString(solution));


    }

    static void report(SearchSpace searchSpace, NTupleSystem nTupleSystem) {

        // note that there are elegant ways to iterate over any number
        // dimensions but this is only dealing with 2-dimensional case
        // and I've hard-wired it in

        for (int i=0; i<SearchSpaceUtil.size(searchSpace); i++) {
            int[] p = SearchSpaceUtil.nthPoint(searchSpace, i);
            System.out.println(Arrays.toString(p));
            nTupleSystem.report(p);
            System.out.println();
            // System.out.println(system.);
        }
    }
}
