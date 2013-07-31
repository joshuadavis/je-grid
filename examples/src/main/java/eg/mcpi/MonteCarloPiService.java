package eg.mcpi;

import org.jegrid.InputProcessor;
import org.jegrid.LifecycleAware;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Random;

/**
 * The parallelizable part of computing pi with a monte carlo simulation.
 * <br>User: Joshua Davis
 * Date: Jan 15, 2006
 * Time: 11:40:36 AM
 */
public class MonteCarloPiService implements InputProcessor, LifecycleAware
{
    private static Logger log = Logger.getLogger(MonteCarloPiService.class);

    public Serializable processInput(int inputId, Serializable input)
    {
        log.info("execute() : ENTER");
        Input in = (Input)input;
        int n = in.getIterations();
        double d = in.getDistance();
        double l = in.getLength();
        Random r = new Random(in.getSeed());
        int hits = 0;
        for (int i = 0; i < n; i++)
        {
            double theta = r.nextDouble() * Math.PI;
            double a = r.nextDouble() * d;
            double x = l * Math.sin(theta);
            if (a < x)
                hits++;
        }
        Result result = new Result(hits, n, l, d);
        log.info("execute() : LEAVE");
        return result;
    }

    public void initialize()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void terminate()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
