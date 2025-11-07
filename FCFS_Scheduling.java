import java.util.*;

class Process {
    String pid;
    int arrivalTime;
    int serviceTime;
    int completionTime;
    int turnaroundTime;
    int waitingTime;

    Process(String pid, int arrivalTime, int serviceTime) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }
}

public class FCFS_Scheduling {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();

        List<Process> processes = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            String pid = "P" + (i + 1);
            System.out.print("Enter Arrival Time for " + pid + ": ");
            int arrival = sc.nextInt();
            System.out.print("Enter Service Time for " + pid + ": ");
            int service = sc.nextInt();

            processes.add(new Process(pid, arrival, service));
        }

        // Sort by arrival time (FCFS)
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int currentTime = 0;

        // Calculate completion, turnaround, waiting times
        for (Process p : processes) {
            if (currentTime < p.arrivalTime) {
                currentTime = p.arrivalTime; // CPU idle time
            }
            p.completionTime = currentTime + p.serviceTime;
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.serviceTime;
            currentTime = p.completionTime;
        }

        // Display results
        System.out.println("\nProcess\tArrival\tService\tCompletion\tTurnaround\tWaiting");
        for (Process p : processes) {
            System.out.println(p.pid + "\t" + p.arrivalTime + "\t" + p.serviceTime + "\t"
                    + p.completionTime + "\t\t" + p.turnaroundTime + "\t\t" + p.waitingTime);
        }

        // Calculate averages
        double totalTAT = 0, totalWT = 0;
        for (Process p : processes) {
            totalTAT += p.turnaroundTime;
            totalWT += p.waitingTime;
        }

        System.out.printf("\nAverage Turnaround Time: %.2f\n", totalTAT / n);
        System.out.printf("Average Waiting Time: %.2f\n", totalWT / n);

        sc.close();
    }
}
