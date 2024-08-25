package edu.eci.arsw.primefinder;

import java.util.LinkedList;
import java.util.List;

public class PrimeFinderThread extends Thread {
    int a, b;
    private List<Integer> primes;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();

    public PrimeFinderThread(int a, int b) {
        super();
        this.primes = new LinkedList<>();
        this.a = a;
        this.b = b;
    }

    @Override
    public void run() {
        for (int i = a; i < b; i++) {
            synchronized (pauseLock) {
                while (paused) {
                    try {
                        pauseLock.wait(); // Espera a que se reanude
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            if (isPrime(i)) {
                primes.add(i);
                System.out.println(i);
            }
        }
    }

    boolean isPrime(int n) {
        boolean ans;
        if (n > 2) {
            ans = n % 2 != 0;
            for (int i = 3; ans && i * i <= n; i += 2) {
                ans = n % i != 0;
            }
        } else {
            ans = n == 2;
        }
        return ans;
    }

    public List<Integer> getPrimes() {
        return primes;
    }

    public void pauseThread() {
        paused = true;
    }

    public void resumeThread() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notify(); // Notifica para reanudar el hilo
        }
    }
}
