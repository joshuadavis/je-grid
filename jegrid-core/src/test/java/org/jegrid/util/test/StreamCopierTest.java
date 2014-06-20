package org.jegrid.util.test;

import org.jegrid.util.StreamCopier;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Tests classes:
 * <ul>
 * <li>StreamCopier</li>
 * </ul>
 * User: josh
 * Date: Sep 22, 2002
 * Time: 12:38:27 AM
 */
public class StreamCopierTest
{
    private static final byte[] BYTES = "12345678901234567890".getBytes();

    @Test
    public void testStreamCopy()
    {
        byte[] a = StreamCopierTest.BYTES;
        InputStream in = new ByteArrayInputStream(a);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StreamCopier copier = new StreamCopier(in,out);
        copier.run();
        Assert.assertTrue(Arrays.equals(a, out.toByteArray()));
    }

}
