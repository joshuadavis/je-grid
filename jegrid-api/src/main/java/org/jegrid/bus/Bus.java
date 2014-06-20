package org.jegrid.bus;

import org.jegrid.NodeAddress;
import org.jegrid.NodeStatus;

/**
 * Internal interface to the communication bus.
 * <br>User: jdavis
 * Date: Sep 30, 2006
 * Time: 7:32:12 AM
 */
public interface Bus
{
    /**
     * Connect to the grid cluster.
     */
    void connect();

    /**
     * Disconnect from the grid cluster.
     */
    void disconnect();


    /**
     * Get the address of this grid node.
     * @return the address
     */
    NodeAddress getAddress();

    /**
     * Admin: Request shutdown of all SERVER nodes.
     */
    void shutdownServers();

    /**
     * Return the address of the coordinator node.
     * @return the address of the coordinator node.
     */
    NodeAddress getCoordinator();

    /**
     * Return the status of all nodes on the grid.
     * @return the status of all nodes on the grid.
     */
    Iterable<NodeStatus> getStatus();
}
