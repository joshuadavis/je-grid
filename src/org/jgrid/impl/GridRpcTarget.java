package org.jgrid.impl;

import org.apache.log4j.Logger;
import org.jgroups.Message;
import org.jgroups.util.Util;

/**
 * Handles messages sent from the GridRpcDispatcher.
 * <br>
 * NOTE: The methods and the class need to be public because they are called via reflection.
 */
public class GridRpcTarget implements MessageConstants
{
    private static Logger log = Logger.getLogger(GridRpcTarget.class);

    private GridBusImpl gridBus;
    private static ThreadLocal localMessage = new ThreadLocal();

    public static void setLocalMessage(Message req)
    {
        localMessage.set(req);
    }

    public static Message getLocalMessage()
    {
        return (Message) localMessage.get();
    }

    public GridRpcTarget(GridBusImpl gridBus)
    {
        this.gridBus = gridBus;
    }

    public Object _nodeUpdate(NodeStateImpl nodeState)
    {
        return gridBus.getGridListener().handleNodeUpdate(nodeState);
    }

    public Object _stop()
    {
        log.info(gridBus.getLocalAddress() + " _stop()");
        // Disconnect after returning.
        Thread disconnector = new Thread(new Runnable()
        {
            public void run()
            {
                Util.sleep(1000);
                log.info(gridBus.getLocalAddress() + " *** Disconnecting now... ***");
                gridBus.disconnect();
            }
        });
        disconnector.start();
        return MessageConstants.ACK;
    }

    public Object _accept(JobRequest req)
    {
        ServerImpl server = gridBus.getServer();
        if (server == null)
            return "NACK: This node is not a server.";
        else
            return server.handleAccept(req);
    }

    public Object _completed(JobResponse response)
    {
        ClientSessionImpl client = gridBus.getClient();
        if (client == null)
            return "NACK: This node is not a client.";
        else
            return client.completed(response);
    }
}