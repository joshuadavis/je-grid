package eg.mcpi;

import org.apache.log4j.Logger;
import org.jegrid.*;


/**
 * Command line client that executes monte carlo calculation of pi in parallel.
 * <br>User: Joshua Davis
 * Date: Jan 15, 2006
 * Time: 11:34:51 AM
 */
public class ParllelMonteCarloPi {
    private static Logger log = Logger.getLogger(ParllelMonteCarloPi.class);

    static class MCPAggregator implements Aggregator
    {
        private Result result = null;

        public void aggregate(TaskData output)
        {
            Result r = (Result) output.getData();
            log.info("aggregate: " + r.getApproximation());
            if (result == null)
                result = new Result(r);
            else
                result.aggregate(r);
        }

        public void done()
        {
            log.info("done!  result=" + result.getApproximation() + " iterations=" + result.getIterations());
        }
    }

    public static void main(String[] args) {
        try {
            // Serial execution first, time it.
            int iterations = 100000;
            int blocks = 1000;
            log.info("Starting serial execution...");
            MonteCarloPiService serial = new MonteCarloPiService();
            Result result = (Result) serial.processInput(0, new Input(0, iterations * blocks));
            log.info("approximate pi = " + result.getApproximation());

            // Connect to the grid as a client.
            GridConfiguration config = new GridConfiguration();
            config.setGridName("test");
            config.setType(Grid.TYPE_CLIENT);
            Grid grid = config.configure();
            grid.connect();
            Client client = grid.getClient();

            Task task = client.createTask("parallel mc pi");

            // Issue the parallel part of the job.
            long seed = 1;
            for (int i = 0; i < blocks ; i++)
                task.addInput(new Input(seed++, iterations));

            MCPAggregator aggregator = new MCPAggregator();
            task.run(MonteCarloPiService.class.getName(), aggregator, 10, false);

            log.info("exiting...");

        } catch (Throwable t) {
            log.error(t);
        }
    }
}
