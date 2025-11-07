import java.util.*;

class Process {
    String name;
    int arrivalTime, burstTime, remainingTime;
    int completionTime, turnaroundTime, waitingTime;
    boolean isCompleted = false;

    Process(String name, int arrivalTime, int burstTime) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
    }
}

public class RoundRobin{
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
            processes[i] = new Process(name, at, bt);
        }

        System.out.print("Enter Time Quantum: ");
        int quantum = sc.nextInt();

        Arrays.sort(processes, Comparator.comparingInt(p -> p.arrivalTime));

        Queue<Process> queue = new LinkedList<>();
        int currentTime = 0, completed = 0;
        float totalTAT = 0, totalWT = 0;

        queue.add(processes[0]);
        boolean[] added = new boolean[n];
        added[0] = true;

        while (completed < n) {
            if (queue.isEmpty()) {
                for (int i = 0; i < n; i++) {
                    if (!processes[i].isCompleted) {
                        currentTime = processes[i].arrivalTime;
                        queue.add(processes[i]);
                        added[i] = true;
                        break;
                    }
                }
            }

            Process current = queue.poll();
            int execTime = Math.min(quantum, current.remainingTime);
            current.remainingTime -= execTime;
            currentTime += execTime;

            // Add new arrivals to queue
            for (int i = 0; i < n; i++) {
                if (!added[i] && processes[i].arrivalTime <= currentTime && !processes[i].isCompleted) {
                    queue.add(processes[i]);
                    added[i] = true;
                }
            }

            // If process still not finished, re-add it
            if (current.remainingTime > 0) {
                queue.add(current);
            } else {
                current.isCompleted = true;
                current.completionTime = currentTime;
                current.turnaroundTime = current.completionTime - current.arrivalTime;
                current.waitingTime = current.turnaroundTime - current.burstTime;

                totalTAT += current.turnaroundTime;
                totalWT += current.waitingTime;
                completed++;
            }
        }

        System.out.println("\nProcess\tArrival\tBurst\tCompletion\tTurnaround\tWaiting");
        for (Process p : processes) {
            System.out.println(p.name + "\t" + p.arrivalTime + "\t" + p.burstTime + "\t" +
                    p.completionTime + "\t\t" + p.turnaroundTime + "\t\t" + p.waitingTime);
        }

        System.out.printf("\nAverage Turnaround Time = %.2f", totalTAT / n);
        System.out.printf("\nAverage Waiting Time = %.2f\n", totalWT / n);
        sc.close();
    }
}
