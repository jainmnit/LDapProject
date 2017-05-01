package service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.buffer.BufferMetricReader;
import org.springframework.boot.actuate.metrics.repository.MetricRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
class MetricExporterService {
	Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private BufferMetricReader metricReader;
	

   

	@Scheduled(fixedRate = 5000)
    void exportMetrics() {
		System.out.println("Comes in scheudle");
		
		metricReader.findAll().forEach(this::log);
    }

    private void log(Metric<?> m) {
    	System.out.println("Comes in scheudle1");
    	logger.info("Reporting metric {}={}" +m.getName() +m.getValue());
    }
}
