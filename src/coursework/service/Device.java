package coursework.service;


import coursework.Main;
import coursework.models.Application;
import coursework.models.Report;

public class Device
{
    private final int number;
    private boolean isAvailable;
    private Application application;
    private final double lambda;
    private double addRequestTime;
    private double outRequestTime;
    private double totalWorkTime;
    private final Report report;
    private int position;

    public Device(int number, double lambda, Report report)
    {
        this.number = number;
        isAvailable = true;
        this.lambda = lambda;
        this.report = report;
    }

    public int getNumber()
    {
        return number;
    }
    public Application getRequest()
    {
        return application;
    }
    public double getAddRequestTime()
    {
        return addRequestTime;
    }
    public double getOutRequestTime()
    {
        return outRequestTime;
    }
    public double getTotalWorkTime()
    {
        return totalWorkTime;
    }
    public boolean isAvailable()
    {
        return isAvailable;
    }

    public void addRequest(Application application, double addRequestTime)
    {
        this.addRequestTime = addRequestTime;
        isAvailable = false;
        this.application = application;
        report.addDeviceEvent("added to device", Main.simulatingTime, application.getSourceNumber(),
                application.getRequestNumber(), getNumber(), position, addRequestTime - application.getGeneratedTime());


        outRequestTime = addRequestTime + (-1/lambda) * Math.log(Math.random());

        //TODO
    }

    public void deleteRequest()
    {
        Main.simulatingTime = getOutRequestTime();
        totalWorkTime+= getOutRequestTime() - getAddRequestTime();
        report.addDeviceEvent("finished on device", Main.simulatingTime, application.getSourceNumber(),
                application.getRequestNumber(), getNumber(), position, getOutRequestTime() - getAddRequestTime());
        isAvailable = true;
        application = null;
    }
}
