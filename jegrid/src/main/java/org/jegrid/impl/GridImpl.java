package org.jegrid.impl;

import org.apache.log4j.Logger;
import org.jegrid.*;
import org.jegrid.util.MicroContainer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;

/**
 * Implements a connection to the grid based on the Bus abstraction, manages
 * the other two components: client and server.
 * @author jdavis
 * Date: Sep 21, 2006
 * Time: 5:34:18 PM
 */
public class GridImpl implements GridImplementor
{
    private static Logger log = Logger.getLogger(GridImpl.class);
    private final GridConfiguration config;
    private final String hostName;
    private Membership membership;
    private Bus bus;
    private Server server;
    private ClientImplementor client;
    private long startTime;
    private MicroContainer singletons;
    private boolean amCoordinator;

    private static final int SERVER_WAIT_TIMEOUT = 5000;

    /**
     * Connect to the grid using the supplied configuration.
     * @param config grid config
     */
    public GridImpl(GridConfiguration config)
    {
        this.amCoordinator=false;
        this.config = config;
        membership = new Membership(this);
        String name;
        try
        {
            name = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e)
        {
            log.warn("Unable to get host name due to: " + e);
            name = "<unknown>";
        }
        hostName = name;
    }

    /**
     * Called by grid configuration code. Do extra config that can't be done with straight IoC.
     *
     * @param mc The micro container.
     */
    public void initialize(MicroContainer mc)
    {
        // These objects need the GridImplementor in the micro-container, so
        // we ask the container to construct them now to avoid circular dependency.
        bus = (Bus) mc.getComponentInstance(Bus.class);
        switch (config.getType())
        {
            case TYPE_SERVER:
                server = (Server) mc.getComponentInstance(Server.class);
            case TYPE_CLIENT:
                client = (ClientImplementor) mc.getComponentInstance(Client.class);
            default:
                break;
        }
        // Make a child micro-container.
        singletons = new MicroContainer(mc);
    }

    public Client getClient()
    {
        return client;
    }

    public Server getServer()
    {
        return server;
    }

    /**
     * Connect to the grid messaging bus.
     */
    public void connect()
    {
        startTime = System.currentTimeMillis();
        bus.connect();
    }

    /**
     * Disconnect from the grid messaging bus and remove any grid singletons we have instantiated.
     * This will call the terminate() lifecycle callbacks on the singletons.
     * @see org.jegrid.LifecycleAware#terminate() for the callback.
     */
    public void disconnect()
    {
        destroyGridSingletons();
        bus.disconnect();
    }

    public NodeAddress getLocalAddress()
    {
        return bus.getAddress();
    }

    /**
     * Get the status of this grid node.
     * @return a bunch of useful info.
     */
    public NodeStatus getLocalStatus()
    {
        // If there is a server on this node, get it's stats.
        if (server != null)
        {
            return server.getServerStatus();
        }

        final int freeThreads = 0;
        final int totalThreads = 0;
        final int tasksAccepted = 0;
        final long lastTaskAccepted = 0;

        final Runtime rt = Runtime.getRuntime();
        final long freeMemory = rt.freeMemory();
        final long totalMemory = rt.totalMemory();

        return new NodeStatusImpl(
                bus.getAddress(),
                config.getType(),
                bus.getCoordinator(),
                freeMemory,
                totalMemory,
                freeThreads,
                totalThreads,
                startTime,
                tasksAccepted,
                lastTaskAccepted,
                hostName
        );
    }

    /**
     * Get the status of all the nodes in the grid.
     * @param cached whether or not to use a cached status.
     * @return grid status.
     */
    public GridStatus getGridStatus(boolean cached)
    {
        if (!cached || membership.needsRefresh())
        {
            NodeStatus[] ns = bus.getGridStatus();
            membership.refreshStatus(ns);
        }
        return membership;
    }

    /**
     * Run the grid server thread(s).
     */
    public void runServer()
    {
        if (server == null)
            throw new GridException("This node is not configured as a server.");
        log.info("Starting server...");
        server.run();
    }

    /**
     * Shutdown grid server thread(s).
     */
    public void shutdownServers()
    {
        bus.shutdownServers();
    }

    /**
     * Get the unique federated name of this grid.
     * @return name for federation.
     */
    public String getGridName()
    {
        return config.getGridName();
    }

    public int nextMembershipChange()
    {
        return membership.nextMembershipChange();
    }

    public void waitForMembershipChange(int mark, long timeout)
    {
        membership.waitForMembershipChange(mark, timeout);
    }

    // Callback methods for grid membership invoked by the bus listener.

    public void onMembershipChange(Set joined, Set left, NodeAddress localAddress)
    {
        membership.onMembershipChange(joined, left, localAddress);
        if (client != null)
            client.onMembershipChange(joined, left);
    }

    public void onNodeStatus(NodeStatus from)
    {
        membership.onNodeStatus(from);
    }

    public void onNewCoordinator(NodeAddress address)
    {
        // If I am the coordinator, then I should try to instantiate all the grid singletons
        if (address.equals(getLocalAddress()))
        {
            log.info("*** I am the coordinator ***");
            amCoordinator=true;
            initializeGridSingletons();
        }
        else
        {
            if(amCoordinator)   // i was the coordinator but have been voted out
            {
                log.info("*** I am no longer the coordinator ***");
                destroyGridSingletons();
            }
            amCoordinator=false;
        }
    }

    private void initializeGridSingletons()
    {
        log.info("*** INITIALIZING GRID SINGLETONS ***");
        List list = config.getGridSingletonDescriptors();
        singletons.initializeFromDescriptors(list);
    }

    private void destroyGridSingletons()
    {
        log.info("*** DESTROYING GRID SINGLETONS ***");
        List list = config.getGridSingletonDescriptors();
        singletons.destroyFromDescriptors(list);
    }

    public List getSingletonDescriptors()
    {
        return config.getGridSingletonDescriptors();
    }

    public Object getLocalSingleton(Object key)
    {
        return singletons.getComponentInstance(key);
    }

    public void waitForServers() throws InterruptedException
    {
        // Use a spin loop to wait for servers in the locally cached list.
        // If the wait times out, refresh the local cache using a synchronous
        // broadcast query.
        boolean available = membership.waitForServers(SERVER_WAIT_TIMEOUT);
        int count = 0;
        while (!available)
        {
            count++;
            log.info("waitForServers() : Refreshing status (" + count + ") ...");
            NodeStatus[] ns = bus.getGridStatus();
            membership.refreshStatus(ns);
            available = membership.waitForServers(SERVER_WAIT_TIMEOUT);
        }
    }

    public Object instantiateObject(String clazz)
    {
        Object o;
        try
        {
            // TODO: Use the micro container?
            Class aClass = Thread.currentThread().getContextClassLoader().loadClass(clazz);
            o = aClass.newInstance();
            MicroContainer.initializeComponent(o);
        }
        catch (Exception e)
        {
            throw new GridException(e);
        }
        return o;
    }

    public void doShutdownServers()
    {
        if (server != null)
            server.doShutdown();
    }

    public void onNodeStopped(NodeAddress addr)
    {
        membership.onNodeStopped(addr);
    }

    public boolean isMember(NodeAddress client)
    {
        return membership.isMember(client);
    }

    public long getStartTime()
    {
        return startTime;
    }

    public String getHostName()
    {
        return hostName;
    }
}
