import java.util.*;

public class NextFit {
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

        int[] allocation = new int[n];
        Arrays.fill(allocation, -1);

        int lastPos = 0; // Start searching from partition 0

        for (int i = 0; i < n; i++) {
            boolean allocated = false;
            int count = 0; // To avoid infinite loop

            while (count < m) { // Check at most all partitions once
                if (partitions[lastPos] >= processes[i]) {
                    allocation[i] = lastPos;
                    partitions[lastPos] -= processes[i];
                    allocated = true;
                    break;
                }
                lastPos = (lastPos + 1) % m;
                count++;
            }

            // Move to next partition after successful allocation
            if (allocated) {
                lastPos = (lastPos + 1) % m;
            }
        }

        // Display result
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
