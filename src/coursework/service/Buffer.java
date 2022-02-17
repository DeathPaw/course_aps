package coursework.service;


import coursework.Main;
import coursework.models.Application;
import coursework.models.Report;

import java.util.stream.IntStream;

public class Buffer
{
    private int size;
    private final int capacity;
    private final Application[] applicationArray;
    private int position;
    private final Report report;

    public Buffer(int capacity, Report report)
    {
        size = 0;
        this.capacity = capacity;
        applicationArray = new Application[capacity];
        IntStream.range(0, capacity).forEach(i -> applicationArray[i] = null);
        position = 0;
        this.report = report;
    }

    public boolean isEmpty()
    {
        for (int i = 0; i < capacity; i++)
        {
            if(applicationArray[i]!=null)
                return false;
        }
        return true;
    }

    public boolean isAvailable()
    {
        return size < capacity;
    }
    public int getCapacity()
    {
        return capacity;
    }


    public void addRequest(Application application)
    {

        boolean added = false;
            for (int i = position; i < capacity; i++)
            {
                if(applicationArray[i]==null)
                {
                    applicationArray[i] = application;
                    position = position == capacity - 1 ? 0 : i + 1;
                    size++;
                    added = true;
                    report.addBufferEvent("added to buffer", Main.simulatingTime, application.getSourceNumber(), application.getRequestNumber(), i, position, 0);
                    break;
                }
            }
            if(!added)
                for (int i = 0; i < position; i++)
                    if (applicationArray[i] == null) {
                        applicationArray[i] = application;
                        position = i + 1;
                        size++;
                        report.addBufferEvent("added to buffer", Main.simulatingTime, application.getSourceNumber(), application.getRequestNumber(), i, position, 0);
                        break;
                    }
    }

    public Application getRequest(int pos)
    {
            Application application = new Application(applicationArray[pos]);
            applicationArray[pos] = null;
            size--;
            return application;
    }

    public Application checkRequest(int pos)
    {
        return applicationArray[pos];
    }

    public void deleteRequest()
    {
        if (position==capacity)
            position = 0;
           report.addBufferEvent("canceled", Main.simulatingTime, applicationArray[position].getSourceNumber(),
                   applicationArray[position].getRequestNumber(), position, position, Main.simulatingTime - applicationArray[position].getGeneratedTime());
           applicationArray[position] = null;
           size--;
    }
}
