// $Id:                                                                    $
package org.jgrid;

/**
 * Application exception. Wrapper over RuntimException
 *
 * @author josh Jan 5, 2005 7:01:51 AM
 */
public class GridException extends RuntimeException
{
    public GridException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public GridException(String message)
    {
        super(message);
    }

    public GridException(Exception e)
    {
        super(e);
    }
}
