package de.tsawlen.testat3.server.main;

import java.net.DatagramPacket;
import java.util.LinkedList;

public class QueueMonitor {
	
	//The Job queue
	private LinkedList<DatagramPacket> jobQueue = new LinkedList<>();

	
	/**
	 * This method is responsible for adding Jobs to the queue
	 * @param job
	 */
	public synchronized void addJob(DatagramPacket job) {
		jobQueue.add(job);
		//Wake all Workers and let them fight for the job :3
		this.notifyAll();
	}
	
	/**
	 * This method is responsible for getting a Job from the Job queue
	 * @return
	 */
	public synchronized DatagramPacket getJob() {
		//Look if the Queue is empty, so if no Job is available
		while(jobQueue.isEmpty()) {
			try {
				//No job is available, sleep until one is.
				this.wait();
			}catch(IllegalMonitorStateException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//Get the last Element of the Job Queue
		return jobQueue.pop();
	}

}
