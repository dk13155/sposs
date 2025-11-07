import java.util.*;

public class Optimal {
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
        Arrays.fill(frames, -1); // initialize with -1 (empty)
        int pageFaults = 0;

        System.out.println("\nPage\tFrames\t\tPage Fault");
        for (int i = 0; i < n; i++) {
            int page = pages[i];
            boolean found = false;

            // Check if page is already in frame
            for (int j = 0; j < framesCount; j++) {
                if (frames[j] == page) {
                    found = true;
                    break;
                }
            }

            // If not found → Page fault occurs
            if (!found) {
                // Check if empty frame available
                boolean placed = false;
                for (int j = 0; j < framesCount; j++) {
                    if (frames[j] == -1) {
                        frames[j] = page;
                        placed = true;
                        pageFaults++;
                        break;
                    }
                }

                // If no empty frame, replace using OPTIMAL logic
                if (!placed) {
                    int[] nextUse = new int[framesCount];
                    Arrays.fill(nextUse, Integer.MAX_VALUE);

                    // For each frame, find next use index
                    for (int j = 0; j < framesCount; j++) {
                        for (int k = i + 1; k < n; k++) {
                            if (frames[j] == pages[k]) {
                                nextUse[j] = k;
                                break;
                            }
                        }
                    }

                    // Replace the page that is used farthest in future
                    int maxIndex = 0;
                    for (int j = 1; j < framesCount; j++) {
                        if (nextUse[j] > nextUse[maxIndex])
                            maxIndex = j;
                    }

                    frames[maxIndex] = page;
                    pageFaults++;
                }

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
