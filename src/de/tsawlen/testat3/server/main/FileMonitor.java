package de.tsawlen.testat3.server.main;

public class FileMonitor {
	
	public int waitingWriter = 0;
	public int runningWriter = 0;
	public int runningReader = 0;
	public boolean readerActive = false;
	public boolean writerActive = false;
	
	/**
	 * Entry method for Writers
	 */
	public synchronized void enterWriter() {
		//Increment waiting writers
		waitingWriter++;
		//If a reader or a Writer is Active
		while(readerActive || writerActive) {
			try {
				//go to sleep
				this.wait();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		//decrement the waiting Writers
		writerActive = true;
		waitingWriter--;
		//Increment running writers - will only get to one, why did I do this? Well... I don't know!
		runningWriter++;
		
	}
	
	/**
	 * Entry method for Readers
	 */
	public synchronized void enterRead() {
		//sleep while writers are active or waiting
		while(writerActive || waitingWriter > 0) {
			try {
				this.wait();
			}catch(Exception e) {
				e.printStackTrace();
			}	
		}
		//look if readers are already active
		if(runningReader <= 0) {
			//set readerActive to true
			readerActive = true;
		}
		//Increment runningReaders
		runningReader++;
	}
	
	/**
	 * Exit method for writers
	 */
	public synchronized void leaveWriter() {
		writerActive = false;
		//Wake all waiting threads
		this.notifyAll();
	}
	
	/**
	 * Exit Method for readers
	 */
	public synchronized void leaveReader() {
		//Decrement runningReaders
		runningReader--;
		//look if it is the last running reader
		if(runningReader <= 0) {
			//set readerActive to false
			readerActive = false;
			//Wake all sleeping Threads
			this.notifyAll();
		}
	}
		
	

}
