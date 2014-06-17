package org.jegrid;

import java.io.Serializable;

/**
 * The input or output of a task along with it's input ID.
 * <br> User: jdavis
 * Date: Oct 7, 2006
 * Time: 10:28:25 AM
 */
public class TaskData<R extends Serializable> implements Serializable
{
    private static final long serialVersionUID = -2681038030949504077L;

    private int inputId;
    private R data;
    public static final int END_OF_OUTPUT = -1;
    @SuppressWarnings("unchecked")
    public static final TaskData END = new TaskData(END_OF_OUTPUT, null);

    public TaskData(int inputId, R inputData)
    {
        this.inputId = inputId;
        this.data = inputData;
    }

    public int getInputId()
    {
        return inputId;
    }

    public Serializable getData()
    {
        return data;
    }
}
