package coursework.models;

import coursework.Main;
import coursework.controller.DeviceController;
import coursework.service.Device;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Report
{
    private final Application[] sources;
    private final Application[] buffers;
    private final Application[] devices;
    private final int[] refusedRequests;
    private final int[] countRequests;
    private final double[] timeInDevices;
    private final double[] timeInBuffers;
    private int bufferCurrentPos;
    private int deviceCurrentPos;
    private final int sourceCount;
    private final int bufferCount;
    private final ArrayList<ArrayList<Double>> bpDisp;
    private final ArrayList<ArrayList<Double>> devDisp;

    public Report(int sourceCount, int bufferCount, int deviceCount)
    {
        this.sourceCount = sourceCount;
        this.bufferCount = bufferCount;
        sources = new Application[sourceCount];
        buffers = new Application[bufferCount];
        timeInDevices = new double[sourceCount];
        timeInBuffers = new double[sourceCount];
        bpDisp = new ArrayList<>();
        devDisp = new ArrayList<>();
        IntStream.range(0, bufferCount).forEach(i -> buffers[i] = null);
        devices = new Application[deviceCount];
        IntStream.range(0, deviceCount).forEach(i -> devices[i] = null);
        refusedRequests = new int[sourceCount];
        IntStream.range(0, sourceCount).forEach(i -> {
            refusedRequests[i] = 0;
            timeInDevices[i] = 0;
            timeInBuffers[i] = 0;
            bpDisp.add(i, new ArrayList<>());
            devDisp.add(i, new ArrayList<>());
        });
        countRequests = new int[sourceCount];
        IntStream.range(0, sourceCount).forEach(i -> countRequests[i] = 0);
        bufferCurrentPos = 0;
    }

    public void endSimulation()
    {
        IntStream.range(0, sourceCount).forEach(i -> sources[i] = null);
    }

    public void addSourceEvent(String event, double time, int requestSource, int requestNumber)
    {
        IntStream.range(0, sourceCount).forEach(i -> sources[i] = null);
        sources[requestSource] = new Application(time,requestSource, requestNumber);
        countRequests[requestSource]++;
        printApplicationWithTime(event, time, requestSource, requestNumber);
    }

    private void printApplicationWithTime(String event, double time, int requestSource, int requestNumber) {
        if(Main.showStat)
        {
            Scanner scanner = new Scanner(System.in);
            String st = scanner.nextLine();

            System.out.println("\n |Application " + requestSource + " " + requestNumber + " | " + "time: " + time + " | " + event + "|");
            System.out.println("--------------------------------------------------------------");
            printEventStatistics();
        }
    }

    public void addBufferEvent(String event, double time, int requestSource, int requestNumber, int bufferPos, int bufferCurrentPos, double timeInside)
    {
        this.bufferCurrentPos = bufferCurrentPos;
        if(event.equals("canceled"))
        {
            buffers[bufferPos] = null;
            refusedRequests[requestSource]++;
            bpDisp.get(requestSource).add(timeInside);
            timeInBuffers[requestSource]+= timeInside;
        }
        else
            buffers[bufferPos] = new Application(0.0, requestSource, requestNumber);
        printApplicationWithTime(event, time, requestSource, requestNumber);

    }

    public void addDeviceEvent(String event, double time, int requestSource, int requestNumber, int devicePos, int deviceCurrentPos, double timeInside)
    {
        this.deviceCurrentPos = deviceCurrentPos;
        if(event.equals("added to device"))
        {
            timeInBuffers[requestSource]+= timeInside;
            bpDisp.get(requestSource).add(timeInside);
            devices[devicePos] = new Application(0.0, requestSource, requestNumber);
            IntStream.range(0, bufferCount).filter(i -> buffers[i] != null)
                    .filter(i -> buffers[i].getSourceNumber() == requestSource && buffers[i].getRequestNumber() == requestNumber).forEach(i -> buffers[i] = null);
        }
        else
        {
            timeInDevices[requestSource]+= timeInside;
            devDisp.get(requestSource).add(timeInside);
            devices[devicePos] = null;
        }
        printApplicationWithTime(event, time, requestSource, requestNumber);
    }


    public void printEventStatistics()
    {
        int i = 0;
        for (Application application : sources) {
            System.out.println(application != null ? " Source " + i + " | generating " + " "
                    + application.getRequestNumber() + " application | applications rejected: " + refusedRequests[i] : " Source " + i
                    + " | awaiting | applications rejected: " + refusedRequests[i]);
            i++;
        }
        i = 0;
        for (Application application : buffers) {
            System.out.println(application != null ? i == bufferCurrentPos ? "-> Cell " + i + " of buffer | " + application.getSourceNumber() + " "
                    + application.getRequestNumber() + " application  <-" : "  Cell " + i + " of buffer | " + application.getSourceNumber() + " "
                    + application.getRequestNumber() + " application " : i == bufferCurrentPos ? "-> Cell " + i + " of buffer | is empty <-" : "  Cell " + i + " of buffer | is empty ");
            i++;
        }
        i = 0;
        for (Application application : devices) {
            if (null != application) {
                System.out.println(" Device " + i + " | working on " + application.getSourceNumber() + " "
                        + application.getRequestNumber() + " application");
            } else System.out.println(" Device " + i + " | is available");
            i++;
        }
    }

    public void printStatistics(DeviceController deviceController)
    {
        System.out.println();
        DecimalFormat f = new DecimalFormat("#0.000");
        System.out.println("O============================================================================" +
                "========================================================================================================================================================================O");

        IntStream.range(0, sourceCount).mapToObj(i -> "|| Source number " + i + " generated " + countRequests[i] + "\t" + " application(s) |" + "\t"
                + " application(s) rejected : " + refusedRequests[i] + "  |  P of rejection: "
                + f.format(refusedRequests[i] / (double) countRequests[i]) + " | time in buffer: " + f.format(timeInBuffers[i] / countRequests[i])
                + " | time of processing: " + f.format(timeInDevices[i] / countRequests[i]) + " | time in device: " + f.format((timeInBuffers[i] + timeInDevices[i]) / countRequests[i])
                + " | Disp of time in buffer: " + f.format(getDispBp(i)) + " | Disp time in device: " + f.format(getDispDev(i)) + " ||").forEach(System.out::println);
        System.out.println("O========================================================================================================================" +
                "============================================================================================================================O");


        ArrayList<Device> devices = deviceController.getDevices();

        int i = 0;
        System.out.println("O=====================================O");
        for (Device device : devices) {
            System.out.println("|| Device number " + i + " | workload: " + f.format(device.getTotalWorkTime() / Main.simulatingTime) + " ||");
            i++;
        }
        System.out.println("O=====================================O");
    }

    public double getDispBp(int sourceNumber)
    {
        double sum;
        sum = 0;
        for(int i = 0; i < bpDisp.get(sourceNumber).size(); i++)
            sum += (bpDisp.get(sourceNumber).get(i) - timeInBuffers[sourceNumber] / countRequests[sourceNumber])
                    * (bpDisp.get(sourceNumber).get(i) - timeInBuffers[sourceNumber] / countRequests[sourceNumber]);
        return sum / (countRequests[sourceNumber] - 1);
    }

    public double getDispDev(int sourceNumber)
    {
        double sum = 0;
        for(int i = 0; i < devDisp.get(sourceNumber).size(); i++)
            sum += (devDisp.get(sourceNumber).get(i) - timeInDevices[sourceNumber] / countRequests[sourceNumber])
                    * (devDisp.get(sourceNumber).get(i) - timeInDevices[sourceNumber] / countRequests[sourceNumber]);
        return sum / (countRequests[sourceNumber] - 1);
    }
}
