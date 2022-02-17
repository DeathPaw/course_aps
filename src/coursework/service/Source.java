package coursework.service;

import coursework.models.Application;

public class Source
{
    private static int allRequestNumber;
    private final int number;

    private final double alpha;
    private final double beta;
    private double previousGenTime;
    private int requestNumber;

    public Source(int number, double alpha, double beta)
    {
        this.number = number;
        this.alpha = alpha;
        this.beta = beta;
        previousGenTime = 0.0;
        requestNumber = 0;
    }

    public Application generate()
    {
        //double genTime = previousGenTime + (-1/lambda) * Math.log(Math.random());
        double genTime = previousGenTime + (beta - alpha) * Math.random() + alpha;
        //TODO
        previousGenTime = genTime;
        requestNumber++;
        allRequestNumber++;
        return new Application(genTime, number, requestNumber);
    }

    public static int getAllRequestNumber()
    {
        return allRequestNumber;
    }
}
