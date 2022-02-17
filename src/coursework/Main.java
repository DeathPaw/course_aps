package coursework;

import coursework.controller.BufferController;
import coursework.controller.DeviceController;
import coursework.models.Application;
import coursework.models.Report;
import coursework.service.Source;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main
{
    public static double simulatingTime = 0.0;
    public static boolean showStat = false;
    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter file, to read data entry from file: ");
        double lambda;
        int sourceNumber;
        int bufferCapacity;
        int deviceNumber;
        int requestCount;
        double alpha;
        double beta;
        if(scanner.nextLine().equals("file")) {
            Scanner scannerFromFile = new Scanner(new File("./src/coursework/files/" + "in.txt"));
            lambda = Double.parseDouble(scannerFromFile.nextLine());
            alpha = Double.parseDouble(scannerFromFile.nextLine());
            beta = Double.parseDouble(scannerFromFile.nextLine());
            sourceNumber = scannerFromFile.nextInt();
            bufferCapacity = scannerFromFile.nextInt();
            deviceNumber = scannerFromFile.nextInt();
            requestCount = scannerFromFile.nextInt();
        }
        else{
            System.out.println("Enter lambda for Poisson flow :");
            lambda = scanner.nextDouble();
            System.out.println("Enter Alpha :");
            alpha = scanner.nextDouble();
            System.out.println("Enter Beta :");
            beta = scanner.nextDouble();
            System.out.println("Enter number of sources :");
            sourceNumber = scanner.nextInt();
            System.out.println("Enter capacity of buffer :");
            bufferCapacity = scanner.nextInt();
            System.out.println("Enter number of devices :");
            deviceNumber = scanner.nextInt();
            System.out.println("Enter number of applications to generate:");
            requestCount = scanner.nextInt();
        }

        System.out.println("Enter step to enable step-by-step mode: ");
        if(scanner.nextLine().equals("step"))
            showStat = true;

        ArrayList<Source> sources = new ArrayList<>();
        for(int i = 0; i < sourceNumber; i++)
            sources.add(new Source(i, alpha, beta));
        ArrayList<Application> applications = new ArrayList<>();

        for(Source s : sources)
            applications.add(s.generate());

        Report report = new Report(sourceNumber, bufferCapacity, deviceNumber);
        BufferController bufferController = new BufferController(bufferCapacity, report);
        DeviceController deviceController = new DeviceController(deviceNumber, bufferController, lambda, report);

        while(Source.getAllRequestNumber()!=requestCount)
        {
            double minTime = Integer.MAX_VALUE;
            int index = 0;
            int minIndex = 0;
            for (Application application : applications)
            {
                if(application.getGeneratedTime()<minTime)
                {
                    minTime = application.getGeneratedTime();
                    minIndex = index;
                }
                index++;
            }
            deviceController.solveRequest(minTime);
            deviceController.dispatchRequest();
            simulatingTime = applications.get(minIndex).getGeneratedTime();
            report.addSourceEvent("created", simulatingTime, applications.get(minIndex).getSourceNumber(), applications.get(minIndex).getRequestNumber());
            int numberSource = applications.get(minIndex).getSourceNumber();
            bufferController.placeRequestInBuffer(applications.get(minIndex));
            applications.remove(minIndex);
            applications.add(sources.get(numberSource).generate());
        }

        report.endSimulation();
        double simTime = simulatingTime;

        while(bufferController.bufferIsEmpty() || !deviceController.workIsDone())
        {
            simTime+=(-1/lambda) * Math.log(Math.random());
            deviceController.solveRequest(simTime);
            deviceController.dispatchRequest();
        }
       report.printStatistics(deviceController);
    }
}
