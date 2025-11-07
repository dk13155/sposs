import java.util.*;

class Process {
    String pid;
    int arrivalTime;
    int burstTime;
    int remainingTime;
    int completionTime;
    int turnaroundTime;
    int waitingTime;

    Process(String pid, int arrivalTime, int burstTime) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
    }
}

public class SJF_Preemptive {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();

        List<Process> processes = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            String pid = "P" + (i + 1);
            System.out.print("Enter Arrival Time for " + pid + ": ");
            int arrival = sc.nextInt();
            System.out.print("Enter Burst Time for " + pid + ": ");
            int burst = sc.nextInt();
            processes.add(new Process(pid, arrival, burst));
        }

        // Sort by arrival time initially
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int completed = 0, currentTime = 0;
        double totalTAT = 0, totalWT = 0;
        Process current = null;

        while (completed != n) {
            // Get process with shortest remaining time that has arrived
            Process shortest = null;
            int minRemaining = Integer.MAX_VALUE;
            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && p.remainingTime > 0 && p.remainingTime < minRemaining) {
                    minRemaining = p.remainingTime;
                    shortest = p;
                }
            }

            if (shortest == null) {
                currentTime++; // No process arrived yet
                continue;
            }

            // Execute process for 1 time unit
            shortest.remainingTime--;
            currentTime++;

            // If process finishes
            if (shortest.remainingTime == 0) {
                shortest.completionTime = currentTime;
                shortest.turnaroundTime = shortest.completionTime - shortest.arrivalTime;
                shortest.waitingTime = shortest.turnaroundTime - shortest.burstTime;
                completed++;

                totalTAT += shortest.turnaroundTime;
                totalWT += shortest.waitingTime;
            }
        }

        // Display results
        System.out.println("\nProcess\tArrival\tBurst\tCompletion\tTurnaround\tWaiting");
        for (Process p : processes) {
            System.out.println(p.pid + "\t" + p.arrivalTime + "\t" + p.burstTime + "\t" + p.completionTime + "\t\t"
                    + p.turnaroundTime + "\t\t" + p.waitingTime);
        }

        System.out.printf("\nAverage Turnaround Time: %.2f\n", totalTAT / n);
        System.out.printf("Average Waiting Time: %.2f\n", totalWT / n);

        sc.close();
    }
}
