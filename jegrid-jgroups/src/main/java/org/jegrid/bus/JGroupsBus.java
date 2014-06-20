package org.jegrid.bus;

import org.jegrid.NodeAddress;
import org.jegrid.NodeStatus;

/**
 * Implements the Bus interface using a JGroups Channel.
 * <br>
 * User: Josh
 * Date: 6/18/2014
 * Time: 1:19 PM
 */
public class JGroupsBus implements Bus
{

    @Override
    public void connect()
    {
    }

    @Override
    public void disconnect()
    {
    }

    @Override
    public NodeAddress getAddress()
    {
        return null;
    }

    @Override
    public void shutdownServers()
    {
    }

    @Override
    public NodeAddress getCoordinator()
    {
        return null;
    }

    @Override
    public Iterable<NodeStatus> getStatus()
    {
        return null;
    }
}
