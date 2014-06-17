package org.jegrid.jgroups.test;

import org.jgroups.JChannel;
import org.junit.Test;

/**
 * Test basic JGroups Channel operations.
 * <br>
 * User: Josh
 * Date: 6/16/2014
 * Time: 9:55 PM
 */
public class BasicChannelTest
{
    @Test
    public void checkChannelCreate() throws Exception
    {
        // Create a JGroups channel.
        JChannel channel = new JChannel();

        channel.connect("je-grid");

        channel.disconnect();

        channel.close();
    }
}
