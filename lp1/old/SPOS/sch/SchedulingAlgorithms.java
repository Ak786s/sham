package sch; 
import java.util.*; 
public class SchedulingAlgorithms { 
    public static void main(String[] args) { 
        Scanner sc = new Scanner(System.in); 
        while (true) { 
            System.out.println("\n=== CPU Scheduling Algorithms ==="); 
            System.out.println("1. First-Come, First-Serve (FCFS)"); 
            System.out.println("2. Preemptive Shortest Job First (SJF)"); 
            System.out.println("3. Priority Scheduling (Non-Preemptive)"); 
            System.out.println("4. Round Robin (RR)"); 
            System.out.println("5. Exit"); 
            System.out.print("Choose an option (1-5): "); 
             
            int choice; 
            try { 
                choice = sc.nextInt(); 
                if (choice == 5) break; 
                if (choice < 1 || choice > 5) { 
                    System.out.println("Invalid choice. Please select 1-5."); 
                    continue; 
                } 
            } catch (InputMismatchException e) { 
                System.out.println("Invalid input. Please enter a number."); 
                sc.nextLine(); // Clear buffer 
                continue; 
            } 
 
            System.out.print("Enter number of processes: "); 
            int n; 
            try { 
                n = sc.nextInt(); 
                if (n <= 0) { 
                    System.out.println("Number of processes must be positive."); 
                    continue; 
                } 
            } catch (InputMismatchException e) { 
                System.out.println("Invalid input. Please enter a positive number."); 
                sc.nextLine(); 
                continue; 
            } 
 
            int[] pid = new int[n]; 
            int[] at = new int[n]; // Arrival times 
            int[] bt = new int[n]; // Burst times 
            int[] pt = new int[n]; // Priority times (for Priority scheduling) 
             
            // Input process details 
            for (int i = 0; i < n; i++) { 
                pid[i] = i + 1; 
                System.out.print("Enter process " + (i + 1) + " arrival time: "); 
                try { 
                    at[i] = sc.nextInt(); 
                    if (at[i] < 0) { 
                        System.out.println("Arrival time cannot be negative."); 
                        i--; 
                        continue; 
                    } 
                } catch (InputMismatchException e) { 
                    System.out.println("Invalid input. Please enter a non-negative number."); 
                    sc.nextLine(); 
                    i--; 
                    continue; 
                } 
                System.out.print("Enter process " + (i + 1) + " burst time: "); 
                try { 
                    bt[i] = sc.nextInt(); 
                    if (bt[i] <= 0) { 
                        System.out.println("Burst time must be positive."); 
                        i--; 
                        continue; 
                    } 
                } catch (InputMismatchException e) { 
                    System.out.println("Invalid input. Please enter a positive number."); 
                    sc.nextLine(); 
                    i--; 
                    continue; 
                } 
                if (choice == 3) { // Priority input for Priority scheduling 
                    System.out.print("Enter process " + (i + 1) + " priority (lower number = higherpriority): "); 
                    try { 
                        pt[i] = sc.nextInt(); 
                        if (pt[i] < 0) { 
                            System.out.println("Priority cannot be negative."); 
                            i--; 
                            continue; 
                        } 
                    } catch (InputMismatchException e) { 
                        System.out.println("Invalid input. Please enter a non-negative number."); 
                        sc.nextLine(); 
                        i--; 
                        continue; 
                    } 
                } 
            } 
 
            int quantum = 0; 
            if (choice == 4) { // Quantum input for Round Robin 
                System.out.print("Enter time quantum: "); 
                try { 
                    quantum = sc.nextInt(); 
                    if (quantum <= 0) { 
                        System.out.println("Time quantum must be positive."); 
                        continue; 
                    } 
                } catch (InputMismatchException e) { 
                    System.out.println("Invalid input. Please enter a positive number."); 
                    sc.nextLine(); 
                    continue; 
                } 
            } 
 
            switch (choice) { 
                case 1: 
                    runFCFS(n, pid, at, bt); 
                    break; 
                case 2: 
                    runPreemptiveSJF(n, pid, at, bt); 
                    break; 
                case 3: 
                    runPriority(n, pid, at, bt, pt); 
                    break; 
                case 4: 
                    runRoundRobin(n, pid, at, bt, quantum); 
                    break; 
            } 
        } 
        sc.close(); 
    } 
 
    private static void runFCFS(int n, int[] pid, int[] at, int[] bt) { 
        int[] ct = new int[n]; // Completion times 
        int[] tat = new int[n]; // Turnaround times 
        int[] wt = new int[n]; // Waiting times 
        float avgWt = 0, avgTat = 0; 
 
        // Sort by arrival time 
        for (int i = 0; i < n; i++) { 
            for (int j = 0; j < n - (i + 1); j++) { 
                if (at[j] > at[j + 1]) { 
                    int temp = at[j]; 
                    at[j] = at[j + 1]; 
                    at[j + 1] = temp; 
                    temp = bt[j]; 
                    bt[j] = bt[j + 1]; 
                    bt[j + 1] = temp; 
                    temp = pid[j]; 
                    pid[j] = pid[j + 1]; 
                    pid[j + 1] = temp; 
                } 
            } 
        } 
 
        // Calculate completion, turnaround, and waiting times 
        for (int i = 0; i < n; i++) { 
            if (i == 0) { 
                ct[i] = at[i] + bt[i]; 
            } else { 
                ct[i] = Math.max(at[i], ct[i - 1]) + bt[i]; 
            } 
            tat[i] = ct[i] - at[i]; 
            wt[i] = tat[i] - bt[i]; 
            avgWt += wt[i]; 
            avgTat += tat[i]; 
        } 
 
        // Print results 
        printResults(n, pid, at, bt, ct, tat, wt, avgWt / n, avgTat / n); 
    } 
 
    private static void runPreemptiveSJF(int n, int[] pid, int[] at, int[] bt) { 
        int[] ct = new int[n]; 
        int[] tat = new int[n]; 
        int[] wt = new int[n]; 
        int[] originalBt = bt.clone(); // Store original burst times 
        int[] completed = new int[n]; // Flag for completed processes 
        float avgWt = 0, avgTat = 0; 
        int currentTime = 0, completedCount = 0; 
 
        while (completedCount < n) { 
            int minBt = Integer.MAX_VALUE; 
            int minIndex = n; 
 
            // Find process with minimum remaining burst time that has arrived 
            for (int i = 0; i < n; i++) { 
                if (at[i] <= currentTime && completed[i] == 0 && bt[i] < minBt) { 
                    minBt = bt[i]; 
                    minIndex = i; 
                } 
            } 
 
            if (minIndex == n) { 
                currentTime++; 
            } else { 
                bt[minIndex]--; 
                currentTime++; 
                if (bt[minIndex] == 0) { 
                    ct[minIndex] = currentTime; 
                    completed[minIndex] = 1; 
                    completedCount++; 
                } 
            } 
        } 
 
        // Calculate turnaround and waiting times 
        for (int i = 0; i < n; i++) { 
            tat[i] = ct[i] - at[i]; 
            wt[i] = tat[i] - originalBt[i]; 
            avgWt += wt[i]; 
            avgTat += tat[i]; 
        } 
 
        printResults(n, pid, at, originalBt, ct, tat, wt, avgWt / n, avgTat / n); 
    } 
 
    private static void runPriority(int n, int[] pid, int[] at, int[] bt, int[] pt) { 
        int[] ct = new int[n]; 
        int[] tat = new int[n]; 
        int[] wt = new int[n]; 
        float avgWt = 0, avgTat = 0; 
 
        // Sort by priority (lower number = higher priority) 
        for (int i = 0; i < n; i++) { 
            int pos = i; 
            for (int j = i + 1; j < n; j++) { 
                if (pt[j] < pt[pos]) { 
                    pos = j; 
                } 
            } 
            int temp = pt[pos]; 
            pt[pos] = pt[i]; 
            pt[i] = temp; 
            temp = pid[pos]; 
            pid[pos] = pid[i]; 
            pid[i] = temp; 
            temp = at[pos]; 
            at[pos] = at[i]; 
            at[i] = temp; 
            temp = bt[pos]; 
            bt[pos] = bt[i]; 
            bt[i] = temp; 
        } 
 
        // Calculate completion, waiting, and turnaround times 
        ct[0] = at[0] + bt[0]; 
        wt[0] = 0; 
        for (int i = 1; i < n; i++) { 
            ct[i] = Math.max(ct[i - 1], at[i]) + bt[i]; 
            wt[i] = ct[i] - at[i] - bt[i]; 
        } 
        for (int i = 0; i < n; i++) { 
            tat[i] = ct[i] - at[i]; 
            avgWt += wt[i]; 
            avgTat += tat[i]; 
        } 
 
        // Print results with priority 
        System.out.printf("\n%-10s %-10s %-10s %-10s %-10s %-10s %-10s\n", 
                "Process", "Arrival", "Burst", "Priority", "Complete", "Turnaround", "Waiting"); 
        for (int i = 0; i < n; i++) { 
            System.out.printf("P%-9d %-10d %-10d %-10d %-10d %-10d %-10d\n", 
                    pid[i], at[i], bt[i], pt[i], ct[i], tat[i], wt[i]); 
        } 
        System.out.printf("\nAverage Waiting Time: %.2f\n", avgWt / n); 
        System.out.printf("Average Turnaround Time: %.2f\n", avgTat / n); 
    } 
 
    private static void runRoundRobin(int n, int[] pid, int[] at, int[] bt, int quantum) { 
        int[] ct = new int[n]; 
        int[] tat = new int[n]; 
        int[] wt = new int[n]; 
        int[] remainingBt = bt.clone(); 
        float avgWt = 0, avgTat = 0; 
        int currentTime = 0; 
        Queue<Integer> queue = new LinkedList<>(); 
        int[] arrived = new int[n]; 
        int completedCount = 0; 
 
        // Initialize queue with processes that have arrived 
        int i = 0; 
        while (completedCount < n) { 
            // Add newly arrived processes to queue 
            for (i = 0; i < n; i++) { 
                if (at[i] <= currentTime && arrived[i] == 0 && remainingBt[i] > 0) { 
                    queue.add(i); 
                    arrived[i] = 1; 
                } 
            } 
 
            if (!queue.isEmpty()) { 
                int curr = queue.poll(); 
                int timeSlice = Math.min(quantum, remainingBt[curr]); 
                currentTime += timeSlice; 
                remainingBt[curr] -= timeSlice; 
 
                // Add processes that arrived during execution 
                for (i = 0; i < n; i++) { 
                    if (at[i] <= currentTime && arrived[i] == 0 && remainingBt[i] > 0) { 
                        queue.add(i); 
                        arrived[i] = 1; 
                    } 
                } 
 
                if (remainingBt[curr] > 0) { 
                    queue.add(curr); 
                } else { 
                    ct[curr] = currentTime; 
                    completedCount++; 
                } 
            } else { 
                currentTime++; 
            } 
        } 
 
        // Calculate turnaround and waiting times 
        for (i = 0; i < n; i++) { 
            tat[i] = ct[i] - at[i]; 
            wt[i] = tat[i] - bt[i]; 
            avgWt += wt[i]; 
            avgTat += tat[i]; 
        } 
 
        printResults(n, pid, at, bt, ct, tat, wt, avgWt / n, avgTat / n); 
    } 
 
    private static void printResults(int n, int[] pid, int[] at, int[] bt, int[] ct, int[] tat, int[] wt, float 
avgWt, float avgTat) { 
        System.out.printf("\n%-10s %-10s %-10s %-10s %-10s %-10s\n", 
                "Process", "Arrival", "Burst", "Complete", "Turnaround", "Waiting"); 
        for (int i = 0; i < n; i++) { 
            System.out.printf("P%-9d %-10d %-10d %-10d %-10d %-10d\n", 
                    pid[i], at[i], bt[i], ct[i], tat[i], wt[i]); 
        } 
        System.out.printf("\nAverage Waiting Time: %.2f\n", avgWt); 
        System.out.printf("Average Turnaround Time: %.2f\n", avgTat); 
    } 
} 