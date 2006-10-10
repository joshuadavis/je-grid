package org.jegrid;

/**
 * Represents the status of a particular node in the grid.
 * <br> User: jdavis
 * Date: Oct 4, 2006
 * Time: 7:58:55 AM
 */
public interface NodeStatus
{
    /**
     * Returns the address of the node.
     *
     * @return the address of the node.
     */
    NodeAddress getNodeAddress();

    /**
     * Returns the address of the coordinator (as far as the node is concerned).
     *
     * @return the address of the coordinator
     */
    NodeAddress getCoordinator();

    /**
     * Returns the type of node.  (See Grid.TYPE_XXX)
     *
     * @return the type of node (See Grid.TYPE_XXX)
     */
    int getType();

    int getFreeThreads()
            ;

    int getTotalThreads()
            ;

    long getFreeMemory()
            ;
}
