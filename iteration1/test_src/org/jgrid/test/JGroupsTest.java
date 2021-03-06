package org.jgrid.test;

import junit.framework.TestCase;
import org.jgroups.JChannel;
import org.jgroups.Message;

/**
 * <br>User: Joshua Davis
 * <br>Date: Aug 11, 2005 Time: 7:14:49 AM
 */
public class JGroupsTest extends TestCase
{

    JChannel channel;
    final int NUM_MSGS=1000;
    final long TIMEOUT=30000;

    String props1="UDP1_4(mcast_addr=228.8.8.8;mcast_port=27000;ip_ttl=1;" +
            "mcast_send_buf_size=64000;mcast_recv_buf_size=64000):" +
            //"PIGGYBACK(max_wait_time=100;max_size=32000):" +
            "PING(timeout=2000;num_initial_members=3):" +
            "MERGE2(min_interval=5000;max_interval=10000):" +
            "FD_SOCK:" +
            "VERIFY_SUSPECT(timeout=1500):" +
            "pbcast.NAKACK(max_xmit_size=8096;gc_lag=50;retransmit_timeout=300,600,1200,2400,4800):" +
            "UNICAST(timeout=600,1200,2400,4800):" +
            "pbcast.STABLE(desired_avg_gossip=20000):" +
            "FRAG(frag_size=8096;down_thread=false;up_thread=false):" +
            "pbcast.GMS(join_timeout=5000;join_retry_timeout=2000;" +
            "shun=false;print_local_addr=true)";

        String props2="UDP1_4(mcast_addr=228.8.8.8;mcast_port=27000;ip_ttl=1;" +
                "mcast_send_buf_size=64000;mcast_recv_buf_size=64000):" +
                //"PIGGYBACK(max_wait_time=100;max_size=32000):" +
                "PING(timeout=2000;num_initial_members=3):" +
                "MERGE2(min_interval=5000;max_interval=10000):" +
                "FD_SOCK:" +
                "VERIFY_SUSPECT(timeout=1500):" +
                "pbcast.NAKACK(max_xmit_size=8096;gc_lag=50;retransmit_timeout=300,600,1200,2400,4800):" +
                "UNICAST(timeout=600,1200,2400,4800):" +
                "pbcast.STABLE(desired_avg_gossip=20000):" +
                "FRAG(frag_size=8096;down_thread=false;up_thread=false):" +
                "pbcast.GMS(join_timeout=5000;join_retry_timeout=2000;" +
                "shun=false;print_local_addr=true)";

    String props3="LOOPBACK:" +
            "PING(timeout=2000;num_initial_members=3):" +
            "MERGE2(min_interval=5000;max_interval=10000):" +
            "FD_SOCK:" +
            "VERIFY_SUSPECT(timeout=1500):" +
            "pbcast.NAKACK(max_xmit_size=8096;gc_lag=50;retransmit_timeout=300,600,1200,2400,4800):" +
            "UNICAST(timeout=600,1200,2400,4800):" +
             "pbcast.STABLE(desired_avg_gossip=20000):" +
            "FRAG(frag_size=8096;down_thread=false;up_thread=false):" +
            "pbcast.GMS(join_timeout=5000;join_retry_timeout=2000;" +
            "shun=false;print_local_addr=true)";

    public void setUp(String props) {
        try {
            channel=new JChannel(props);
            channel.connect("test1");
        }
        catch(Throwable t) {
            t.printStackTrace(System.err);
            fail("channel could not be created");
        }
    }


    public void tearDown() {
        if(channel != null) {
            channel.close();
            channel=null;
        }
    }


    /**
     * Sends NUM messages and expects NUM messages to be received. If
     * NUM messages have not been received after 20 seconds, the test failed.
     */
    public void testSendAndReceiveWithDefaultUDP_Loopback() {
        setUp(props1);
        sendMessages(NUM_MSGS);
        int received_msgs=receiveMessages(NUM_MSGS, TIMEOUT);
        assertTrue(received_msgs >= NUM_MSGS);
    }

    public void testSendAndReceiveWithDefaultUDP_NoLoopback() {
        setUp(props2);
        sendMessages(NUM_MSGS);
        int received_msgs=receiveMessages(NUM_MSGS, TIMEOUT);
        assertTrue(received_msgs >= NUM_MSGS);
    }

    public void testSendAndReceiveWithLoopback() {
        setUp(props3);
        sendMessages(NUM_MSGS);
        int received_msgs=receiveMessages(NUM_MSGS, TIMEOUT);
        assertTrue(received_msgs >= NUM_MSGS);
    }

    private void sendMessages(int num) {
        Message msg;
        for(int i=0; i < num; i++) {
            try {
                msg=new Message();
                channel.send(msg);
                System.out.print(i + " ");
            }
            catch(Throwable t) {
                fail("could not send message #" + i);
            }
        }
    }


    /**
     * Receive at least <tt>num</tt> messages. Total time should not exceed <tt>timeout</tt>
     * @param num
     * @param timeout Must be > 0
     */
    private int receiveMessages(int num, long timeout) {
        int received=0;
        Object msg;

        if(timeout <= 0)
            timeout=5000;

        long start=System.currentTimeMillis(), current, wait_time;
        while(true) {
            current=System.currentTimeMillis();
            wait_time=timeout - (current - start);
            if(wait_time <= 0)
                break;
            try {
                msg=channel.receive(wait_time);
                if(msg instanceof Message) {
                    received++;
                    System.out.print("+" + received + ' ');
                }
                if(received >= num)
                    break;
            }
            catch(Throwable t) {
                fail("failed receiving message");
            }
        }
        return received;
    }
}
