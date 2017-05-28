package com.citi.vdict.utils;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
	private static Logger logger = LoggerFactory.getLogger(JsonUtils.class);

	private static ObjectMapper mapper = new ObjectMapper();

	public static String toJson(Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			logger.error("cannot parse object " + obj + " to json string", e);
			return null;
		}
	}

	public static <T> T toObject(String json, Class<T> jsonType) {
		try {
			return mapper.readValue(json, jsonType);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
