import java.util.*;

public class WorstFit {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of memory partitions: ");
        int m = sc.nextInt();
        int[] partitions = new int[m];
        System.out.println("Enter sizes of memory partitions (in K):");
        for (int i = 0; i < m; i++) {
            partitions[i] = sc.nextInt();
        }

        System.out.print("\nEnter number of processes: ");
        int n = sc.nextInt();
        int[] processes = new int[n];
        System.out.println("Enter sizes of processes (in K):");
        for (int i = 0; i < n; i++) {
            processes[i] = sc.nextInt();
        }

        int[] allocation = new int[n]; // store which partition each process is allocated to
        Arrays.fill(allocation, -1);

        // Worst Fit Allocation
        for (int i = 0; i < n; i++) {
            int worstIdx = -1;
            for (int j = 0; j < m; j++) {
                if (partitions[j] >= processes[i]) {
                    if (worstIdx == -1 || partitions[j] > partitions[worstIdx]) {
                        worstIdx = j;
                    }
                }
            }

            // If found a suitable partition
            if (worstIdx != -1) {
                allocation[i] = worstIdx;
                partitions[worstIdx] -= processes[i];
            }
        }

        // Display results
        System.out.println("\nProcess No.\tProcess Size\tPartition No.");
        for (int i = 0; i < n; i++) {
            if (allocation[i] != -1)
                System.out.println((i + 1) + "\t\t" + processes[i] + "\t\t" + (allocation[i] + 1));
            else
                System.out.println((i + 1) + "\t\t" + processes[i] + "\t\tNot Allocated");
        }

        sc.close();
    }
}
