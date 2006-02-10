/* Created on Feb 10, 2006 by agautam */
package org.jgrid.test;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.jgrid.ClientSession;
import org.jgrid.GridBus;
import org.jgrid.GridConfiguration;
import org.jgrid.Job;
import org.jgrid.Peers;
import org.jgrid.util.JavaProcess;

import eg.MonteCarloPi;
import eg.mcpi.Input;

import junit.framework.TestCase;


/**
 * This test attempts to run tasks in parallel.
 * start a grid, launch MonteCarloPiService tasks on them.
 *
 * Functionality copied from GridTest
 *
 * @author agautam
 */
public class ParallelTaskTest extends TestCase
{
    private static Logger log = Logger.getLogger(ParallelTaskTest.class);

    // default values.
    private int jobCount = 10;
    private int serverCount = 5;
    private int monteCarloIterations = 1000;
    private int utestTimeoutMS = 10 * 60 * 1000; // 10 mins in milliseconds
    /**
     * Constructor.
     *
     * @param arg0
     */
    public ParallelTaskTest(String arg0)
    {
        //TODO Auto-generated constructor stub
        super(arg0);
    }

    /**
     * ParllelMonteCarloPi along with start and stop of required servers.
     */
    public void testParallel() throws Exception
    {
        log.debug("Starting parallel test");
        GridConfiguration config = new GridConfiguration();
        GridBus bus = config.getGridBus();
        JavaProcess[] servers = null;
        try
        {
            servers = GridSetupHelper.startServers(getServerCount());

            // wait for all servers to start.
            Peers peers = bus.getPeers();
            peers.waitForPeers(servers.length);

            ClientSession session = config.getClientSession();
            Job job = session.createJob(MonteCarloPi.class);
            List inputList = new ArrayList();
            for (int i = 0; i < getJobCount(); i++)
            {
                inputList.add(new Input(i + 1, getMonteCarloIterations()));
            }
            job.startParallel(inputList);
            job.join(getUtestTimeoutMS());
        }
        finally
        {
            log.debug("Starting parallel cleanup");
            if (servers != null)
            {
                GridSetupHelper.stopServers(bus, servers);
            }
            log.debug("Starting parallel Done.");
        }
    }

    /** @return Returns the jobCount. */
    public int getJobCount()
    {
        return jobCount;
    }

    /** @param jobCount The jobCount to set. */
    public void setJobCount(int jobCount)
    {
        this.jobCount = jobCount;
    }


    /** @return Returns the monteCarloIterations. */
    public int getMonteCarloIterations()
    {
        return monteCarloIterations;
    }


    /** @param monteCarloIterations The monteCarloIterations to set. */
    public void setMonteCarloIterations(int monteCarloIterations)
    {
        this.monteCarloIterations = monteCarloIterations;
    }


    /** @return Returns the serverCount. */
    public int getServerCount()
    {
        return serverCount;
    }


    /** @param serverCount The serverCount to set. */
    public void setServerCount(int serverCount)
    {
        this.serverCount = serverCount;
    }


    /** @return Returns the utestTimeoutMS. */
    public int getUtestTimeoutMS()
    {
        return utestTimeoutMS;
    }


    /** @param utestTimeoutMS The utestTimeoutMS to set. */
    public void setUtestTimeoutMS(int utestTimeoutMS)
    {
        this.utestTimeoutMS = utestTimeoutMS;
    }
}
