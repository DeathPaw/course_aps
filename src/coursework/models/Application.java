package coursework.models;

public class Application
{
    private final double generatedTime;
    private final int sourceNumber;
    private final int requestNumber;

    public Application(double generatedTime, int sourceNumber, int requestNumber)
    {
        this.generatedTime = generatedTime;
        this.sourceNumber = sourceNumber;
        this.requestNumber = requestNumber;
    }

    public Application(Application application)
    {
        this.generatedTime = application.getGeneratedTime();
        this.sourceNumber = application.getSourceNumber();
        this.requestNumber = application.getRequestNumber();
    }

    public double getGeneratedTime()
    {
        return generatedTime;
    }
    public int getSourceNumber()
    {
        return sourceNumber;
    }
    public int getRequestNumber()
    {
        return requestNumber;
    }

    @Override
    public String toString()
    {
        return sourceNumber + " " + requestNumber;
    }
}
