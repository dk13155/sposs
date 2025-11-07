import java.util.*;

class Process {
    String name;
    int arrivalTime, burstTime, priority;
    int waitingTime, turnaroundTime, completionTime;
    boolean completed = false;

    Process(String name, int arrivalTime, int burstTime, int priority) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
    }
}

public class PriorityNonPreemptive {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();
        Process[] processes = new Process[n];

        for (int i = 0; i < n; i++) {
            System.out.print("Enter Process name: ");
            String name = sc.next();
            System.out.print("Enter Arrival Time: ");
            int at = sc.nextInt();
            System.out.print("Enter Burst Time: ");
            int bt = sc.nextInt();
            System.out.print("Enter Priority (lower number = higher priority): ");
            int pr = sc.nextInt();
            processes[i] = new Process(name, at, bt, pr);
        }

        int completed = 0, currentTime = 0;
        float totalWT = 0, totalTAT = 0;

        // Sort by arrival time first
        Arrays.sort(processes, Comparator.comparingInt(p -> p.arrivalTime));

        while (completed != n) {
            Process current = null;
            int highestPriority = Integer.MAX_VALUE;

            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && !p.completed && p.priority < highestPriority) {
                    highestPriority = p.priority;
                    current = p;
                }
            }

            if (current == null) {
                currentTime++;
                continue;
            }

            // Execute the selected process fully (Non-Preemptive)
            currentTime += current.burstTime;
            current.completionTime = currentTime;
            current.turnaroundTime = current.completionTime - current.arrivalTime;
            current.waitingTime = current.turnaroundTime - current.burstTime;
            current.completed = true;
            completed++;

            totalWT += current.waitingTime;
            totalTAT += current.turnaroundTime;
        }

        System.out.println("\nProcess\tArrival\tBurst\tPriority\tCompletion\tTurnaround\tWaiting");
        for (Process p : processes) {
            System.out.println(p.name + "\t" + p.arrivalTime + "\t" + p.burstTime + "\t" +
                    p.priority + "\t\t" + p.completionTime + "\t\t" +
                    p.turnaroundTime + "\t\t" + p.waitingTime);
        }

        System.out.printf("\nAverage Turnaround Time = %.2f", totalTAT / n);
        System.out.printf("\nAverage Waiting Time = %.2f\n", totalWT / n);

        sc.close();
    }
}
