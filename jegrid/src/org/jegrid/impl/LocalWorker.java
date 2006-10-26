package org.jegrid.impl;

import org.apache.log4j.Logger;
import org.jegrid.Aggregator;
import org.jegrid.GridException;
import org.jegrid.InputProcessor;
import org.jegrid.TaskData;

/**
 * A local worker that proceses input on the client.
 * <br>User: Joshua Davis
 * Date: Oct 22, 2006
 * Time: 8:29:29 AM
 */
public class LocalWorker extends AbstractInputProcessingWorker
{
    private static Logger log = Logger.getLogger(LocalWorker.class);
    private TaskImpl taskImpl;
    private Aggregator aggregator;
    private boolean processInput;

    public LocalWorker(TaskImpl taskImpl)
    {
        super(null, taskImpl.getTaskId());
        this.taskImpl = taskImpl;
    }


    protected void processInput() throws Exception
    {
        if (processInput)   // Process and aggregate.
            super.processInput();
        else                // Just aggregate until the end of the output queue.
            taskImpl.aggregateOutput(aggregator);
    }

    protected TaskData nextInput(TaskData output) throws RpcTimeoutException, InterruptedException
    {
        // Invoke the next/output method directly on the task implementation.
        TaskData next = taskImpl.getNextInput(taskImpl.getLocalAddress(), output);
        // If there is any output, aggregate it.
        taskImpl.drainOutputQueue(aggregator);
        return next;
    }

    protected void done() throws InterruptedException
    {
        log.info("Local worker done.");
        // If there is any output left, aggregate it.
        taskImpl.aggregateOutput(aggregator);
    }

    protected void handleException(GridException ge)
    {
        throw ge;
    }

    protected InputProcessor instantiateInputProcessor() throws IllegalAccessException, InstantiationException, ClassNotFoundException
    {
        Class aClass = Thread.currentThread().getContextClassLoader().loadClass(taskImpl.getInputProcessorClassName());
        return (InputProcessor) aClass.newInstance();
    }

    public void setAggregator(Aggregator aggregator)
    {
        this.aggregator = aggregator;
    }

    /**
     * Process input with this worker if the flag is true.
     *
     * @param flag true - process input, false - aggregate only
     */
    public void setProcessInput(boolean flag)
    {
        processInput = flag;
    }
}
