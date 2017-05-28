package com.citi.vdict.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

@Service
public class CommonService {
	private Map<String, String> supportDbms = new HashMap<>();

	@PostConstruct
	public void loadSupportDbms() {
		String[] supportDbmsTypes = System.getProperty("all.support.dbms.types").split(",");
		for (String dbmsType : supportDbmsTypes) {
			supportDbms.put(dbmsType, System.getProperty(dbmsType + ".driver.class.name"));
		}
	}

	public Set<String> getAllDbmsType() {
		return supportDbms.keySet();
	}

	public String getDbmsDriverClass(String dbmsType) {
		return supportDbms.get(dbmsType);
	}

}
