package org.jgrid.impl;

import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.blocks.PullPushAdapter;
import org.jgroups.blocks.MethodCall;
import org.jgroups.MessageListener;
import org.jgroups.MembershipListener;
import org.jgroups.Address;
import org.jgroups.TimeoutException;
import org.jgroups.SuspectedException;
import org.jgroups.Message;
import org.jgroups.util.RspList;
import org.jgrid.GridException;

import java.util.Vector;

/**
 * Delegates received messages from the grid message pump to the RPC target as method calls.
 * Invokes methods on other grid nodes.
 * <br>User: Joshua Davis
 * <br>Date: Oct 24, 2005 Time: 8:02:16 AM
 */
public class GridRpcDispatcher extends RpcDispatcher
{
    private boolean ready = false;

    public GridRpcDispatcher(PullPushAdapter adapter,
                             MessageListener listener,
                             MembershipListener membership,
                             GridRpcTarget target)
    {
        super(adapter, "grid-rpc-dispatcher", listener, membership, target);
        setMarshaller(new GridMarshaller());
    }

    public synchronized boolean isReady()
    {
        return ready;
    }

    public synchronized void setReady(boolean ready)
    {
        this.ready = ready;
    }

    public Object callRemoteMethod(Address dest, String methodName, Object arg,
                                   int mode, long timeout)
            throws TimeoutException, SuspectedException
    {
        MethodCall call =new MethodCall(methodName,
                new Object[] { arg },
                new Class[] { arg.getClass() });
        return callRemoteMethod(dest,call,mode,timeout);
    }

    public Object handle(Message req)
    {
        Object rv = null;  // Invoke the method on the local target.
        if (isReady())
        {
            // Set the actual message in a thread local.
            GridRpcTarget.setLocalMessage(req);
            try
            {
                rv = super.handle(req);
            }
            catch (Exception e)
            {
                log.error(e,e);
            }
            GridRpcTarget.setLocalMessage(null);
        }
        else
        {
            // This dispatcher isn't ready yet, so... ignore the message?
            return MessageConstants.NACK;
        }
        return rv;
    }

    public RspList gridInvoke(Vector dests, String methodName, Object arg,
                              int mode, long timeout)
    {
        MethodCall call =new MethodCall(methodName,
                new Object[] { arg },
                new Class[] { arg.getClass() });
        try
        {
            return callRemoteMethods(dests, call, mode, timeout);
        }
        catch (Exception e)
        {
            throw new GridException("Unable to invoke " + call + " due to : " + e.getMessage(), e);
        }
    }
}
