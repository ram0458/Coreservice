package com.rite.products.convertrite.configuration;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.rite.products.convertrite.model.XxrCloudDataProcess;
import com.rite.products.convertrite.model.XxrCloudDataProcessConfig;
import com.rite.products.convertrite.respository.XxrCloudDataProcessConfigRepository;
import com.rite.products.convertrite.respository.XxrCloudDataProcessRepository;

@Component
public class ScheduledTasks {

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Value("${clouddataprocess-url}")
	private String url;

	@Autowired
	RestTemplate restTemplate;
	@Autowired
	XxrCloudDataProcessRepository xxrCloudDataProcessRepository;
	@Autowired
	XxrCloudDataProcessConfigRepository xxrCloudDataProcessConfigRepository;

	//@Scheduled(fixedRate = 1500)
	@Scheduled(cron = "0 0 0 * * *", zone = "IST")
	public void reportCurrentTime() {
		log.info("The time is now {}", dateFormat.format(new Date()));
		String sqlQuery = "";
		try {

			Timestamp creationDate = xxrCloudDataProcessRepository.getCreationDate();
			log.info("CreationDate::::::" + creationDate);

			/*
			 * Map<String, Object> params = new HashMap<>(); params.put("description",
			 * "ConvertRite"); params.put("lookUpFlag", "Y"); params.put("scheduledJobCall",
			 * "Y"); if (creationDate == null) params.put("sqlQuery",
			 * "Select DISTINCT LOOKUP_TYPE,LOOKUP_CODE from fnd_lookup_values"); else
			 * params.put("sqlQuery",
			 * "Select DISTINCT LOOKUP_TYPE,LOOKUP_CODE from fnd_lookup_values where LAST_UPDATE_DATE > '"
			 * + creationDate + "' or CREATION_DATE > '" + creationDate + "'");
			 * 
			 * String url = "http://172.16.0.67" + contextPath +
			 * "/clouddataprocessingrequest?%s"; String parametrizedArgs =
			 * params.keySet().stream().map(k -> String.format("%s={%s}", k, k))
			 * .collect(Collectors.joining("&"));
			 */

			if (creationDate == null)
				sqlQuery = "Select DISTINCT LOOKUP_TYPE,LOOKUP_CODE from fnd_lookup_values";
			else
				sqlQuery = "Select DISTINCT LOOKUP_TYPE,LOOKUP_CODE from fnd_lookup_values where LAST_UPDATE_DATE > '"
						+ creationDate + "' or CREATION_DATE > '" + creationDate + "'";

			// XxrCloudDataProcessConfig
			// xxrCloudDataProcessConfig=xxrCloudDataProcessConfigRepository.findFirstByOrderByCreationDateDesc();
			List<XxrCloudDataProcessConfig> cloudDataProcessLi = xxrCloudDataProcessConfigRepository.findAll();
			for (XxrCloudDataProcessConfig xxrCloudDataProcessConfig : cloudDataProcessLi) {
				XxrCloudDataProcess xxrCloudDataProcess = new XxrCloudDataProcess();
				UUID requestId = UUID.randomUUID();
				xxrCloudDataProcess.setRequestId(requestId.toString());
				xxrCloudDataProcess.setDescription("ConvertRite");
				xxrCloudDataProcess.setSqlQuery(sqlQuery);
				xxrCloudDataProcess.setLookupFlag("Y");
				xxrCloudDataProcess.setStatus("Starting");
				// java.util.Date date = new java.util.Date();
				// System.out.println("date::::::" + date);
				xxrCloudDataProcess.setCreationDate(new java.util.Date());
				xxrCloudDataProcess.setScheduledJobCall("Y");
				xxrCloudDataProcess.setPodId(xxrCloudDataProcessConfig.getPodId());
				xxrCloudDataProcess.setProjectId(xxrCloudDataProcessConfig.getProjectId());
				xxrCloudDataProcess.setPriority(0);

				XxrCloudDataProcess xxrCloudDataProcessRes = xxrCloudDataProcessRepository.save(xxrCloudDataProcess);
				log.info("RequestId:::::" + xxrCloudDataProcessRes.getRequestId());
			}
			/*
			 * HttpHeaders headers = new HttpHeaders(); headers.set("Accept",
			 * MediaType.APPLICATION_JSON_VALUE); HttpEntity<CloudDataProcessingReqPo>
			 * requestEntity = new HttpEntity<>(cloudDataProcessingReqPo,headers);
			 * //System.out.println(String.format(url, parametrizedArgs));
			 * 
			 * ResponseEntity<XxrCloudDataProcess> cloudDataProcess =
			 * restTemplate.exchange(url, HttpMethod.POST, requestEntity,
			 * XxrCloudDataProcess.class);
			 */
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	@Scheduled(cron = "0 0 0 * * *", zone = "IST")
	public void toFetchCloudTables() {

		try {
			List<XxrCloudDataProcess> xxrCloudDataProcessList = xxrCloudDataProcessRepository.findAllByExtractionFlagAndStatus("Y", "completed");
			List<XxrCloudDataProcess> xxrCloudDataProcesses = new ArrayList<>();
			for (XxrCloudDataProcess xxrCloudDataProces : xxrCloudDataProcessList) {
				xxrCloudDataProces.setStatus("Starting");
				xxrCloudDataProces.setLastUpdatedDate(new java.util.Date());
				xxrCloudDataProcesses.add(xxrCloudDataProces);
			}
			List<XxrCloudDataProcess> xxrCloudDataProcessRes = xxrCloudDataProcessRepository.saveAll(xxrCloudDataProcesses);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}