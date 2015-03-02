package demo.elasticinsight_manager.threads;

public class EsIndexMappingMonitor implements Runnable {
	protected Thread _thread;
	public EsIndexMappingMonitor() {
		_thread = new Thread(this);
		_thread.start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		//TODO: latest incarnation of this demo app - key everything off MongoDB, so this won't do anything
		// (ditto all the events.Es* classes)
	}
}
