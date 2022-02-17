package coursework.controller;

import coursework.models.Report;
import coursework.service.Buffer;
import coursework.models.Application;

import java.util.ArrayList;


public class BufferController
{
    private final Buffer buffer;
    private final ArrayList<Integer> applicationPackage;
    private int applicationPackageSize;

    public BufferController(int bufferCapacity, Report report)
    {
        buffer = new Buffer(bufferCapacity, report);
        applicationPackage = new ArrayList<>();
        applicationPackageSize = 0;
    }

    public boolean bufferIsEmpty()
    {
        return !buffer.isEmpty();
    }

    public void placeRequestInBuffer(Application application)
    {
        if (!buffer.isAvailable()) buffer.deleteRequest();
        buffer.addRequest(application);
    }

    public Application getRequestFromBuffer()
    {

        if (applicationPackageSize > 0)
        {
            applicationPackageSize--;
            if (buffer.checkRequest(applicationPackage.get(0)) != null)
            {
                Application application = new Application(buffer.getRequest(applicationPackage.get(0)));
                applicationPackage.remove(0);
                return application;
            }
            else
                return getApplication();
        }
        else
            return getApplication();
    }

    private Application getApplication() {
        applicationPackageSize = 0;
        applicationPackage.clear();
        for (int i = 0; i < buffer.getCapacity(); i++)
            if (buffer.checkRequest(i) != null) {
                applicationPackage.add(i);
                applicationPackageSize++;
            }

        applicationPackageSize--;
        Application application = new Application(buffer.getRequest(applicationPackage.get(0)));
        applicationPackage.remove(0);
        return application;
    }
}

