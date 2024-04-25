package jee.microservice.api.demo;

public class CPULoadGenerator extends Thread implements Runnable {

	private double cpuLoad;
	private long loadDuration;
	private Object waiter;

	public CPULoadGenerator(String name, double cpuLoad, long loadDuration, Object waiter) {
		//super(name);
		this.cpuLoad = cpuLoad;
		this.loadDuration = loadDuration;
		System.out.println(name + " : " + cpuLoad + " : " + loadDuration);
	}


	public void run() {
		long startTime = System.currentTimeMillis();
		synchronized (this) {
			try {
				// Loop for the given duration
				while (System.currentTimeMillis() - startTime < loadDuration) {
					// Every 100ms, sleep for the percentage of un-laden time
					if (System.currentTimeMillis() % 100 == 0) {
						Thread.sleep((long) Math.floor((1 - cpuLoad) * 100));
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				notify();
			}
		}
	}

}
