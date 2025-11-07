import java.util.*;

public class LRU {
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

        ArrayList<Integer> frames = new ArrayList<>(framesCount);
        int pageFaults = 0;

        System.out.println("\nPage\tFrames\t\tPage Fault");

        for (int i = 0; i < n; i++) {
            int page = pages[i];

            // If page not in frames → Page Fault
            if (!frames.contains(page)) {
                if (frames.size() < framesCount) {
                    frames.add(page); // empty space available
                } else {
                    // Remove Least Recently Used (LRU)
                    int lruIndex = findLRU(frames, pages, i);
                    frames.set(lruIndex, page);
                }
                pageFaults++;
                System.out.print(page + "\t");
                printFrames(frames, framesCount);
                System.out.println("\tYes");
            } else {
                System.out.print(page + "\t");
                printFrames(frames, framesCount);
                System.out.println("\tNo");
            }
        }

        System.out.println("\nTotal Page Faults = " + pageFaults);
        sc.close();
    }

    // Find the Least Recently Used page’s index
    static int findLRU(ArrayList<Integer> frames, int[] pages, int currentIndex) {
        int minIndex = currentIndex;
        int lruPageIndex = -1;

        for (int i = 0; i < frames.size(); i++) {
            int page = frames.get(i);
            int lastUsed = -1;

            for (int j = currentIndex - 1; j >= 0; j--) {
                if (pages[j] == page) {
                    lastUsed = j;
                    break;
                }
            }

            if (lastUsed == -1) {
                return i; // page never used → replace immediately
            }

            if (lastUsed < minIndex) {
                minIndex = lastUsed;
                lruPageIndex = i;
            }
        }
        return lruPageIndex;
    }

    static void printFrames(ArrayList<Integer> frames, int totalFrames) {
        for (int i = 0; i < totalFrames; i++) {
            if (i < frames.size())
                System.out.print(frames.get(i) + " ");
            else
                System.out.print("- ");
        }
    }
}
