import java.util.*;

public class FIFO {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of pages in reference string: ");
        int n = sc.nextInt();
        int[] pages = new int[n];
        System.out.println("Enter the reference string (page numbers):");
        for (int i = 0; i < n; i++) {
            pages[i] = sc.nextInt();
        }

        System.out.print("Enter number of frames: ");
        int framesCount = sc.nextInt();

        int[] frames = new int[framesCount];
        Arrays.fill(frames, -1); // initialize all frames as empty

        int pageFaults = 0;
        int pointer = 0; // FIFO pointer

        System.out.println("\nPage\tFrames\t\tPage Fault");
        for (int i = 0; i < n; i++) {
            int page = pages[i];
            boolean found = false;

            // Check if page already exists in frame
            for (int j = 0; j < framesCount; j++) {
                if (frames[j] == page) {
                    found = true;
                    break;
                }
            }

            // If not found → Page Fault
            if (!found) {
                frames[pointer] = page; // replace oldest page
                pointer = (pointer + 1) % framesCount; // move FIFO pointer
                pageFaults++;
                System.out.print(page + "\t");
                printFrames(frames);
                System.out.println("\tYes");
            } else {
                System.out.print(page + "\t");
                printFrames(frames);
                System.out.println("\tNo");
            }
        }

        System.out.println("\nTotal Page Faults = " + pageFaults);
        sc.close();
    }

    static void printFrames(int[] frames) {
        for (int frame : frames) {
            if (frame == -1)
                System.out.print("- ");
            else
                System.out.print(frame + " ");
        }
    }
}
