package com.citi.vdict.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StringUtils {

	private static final String DEFAULTDELIMITER = ",";

	@SuppressWarnings("unchecked")
	public static <K, V> String mapToString(Map<K, V> map, String delimiter, boolean withSingleQuote) {
		final String lcoaldelimiter = delimiter == null ? DEFAULTDELIMITER : delimiter;
		if (map != null && map.size() > 0) {
			return map.entrySet().stream().map(entry -> {
				V v = entry.getValue();
				if (v instanceof Map) {
					return (String) mapToString((Map<Object, Object>) v, lcoaldelimiter);
				} else if (v instanceof Collection) {
					return v.toString();
				} else {
					if (withSingleQuote) {
						return entry.getKey() + "='" + entry.getValue() + "'";
					}
					return entry.getKey() + "=" + entry.getValue();
				}
			}).collect(Collectors.joining(delimiter));
		}
		return map == null ? null : "";
	}

	public static <K, V> String mapToString(Map<K, V> map, String delimiter) {
		return mapToString(map, delimiter, false);
	}

	public static <K, V> String mapToString(Map<K, V> map) {
		return mapToString(map, DEFAULTDELIMITER, false);
	}

	public static <T> String arrayToString(T[] objArr, String delimiter) {
		final String lcoaldelimiter = delimiter == null ? DEFAULTDELIMITER : delimiter;
		if (objArr != null) {
			return String.join(lcoaldelimiter, (CharSequence[]) objArr);
		} else {
			return null;
		}
	}

	public static <T> String arrayToString(T[] objArr) {
		return arrayToString(objArr, DEFAULTDELIMITER);
	}

	public static <T> String collectionToWrapperString(Collection<T> objList, String wrapper, String delimiter) {
		final String lcoaldelimiter = delimiter == null ? DEFAULTDELIMITER : delimiter;
		final String lcoalWrapper = wrapper == null ? "" : wrapper;
		StringBuilder sb = new StringBuilder();
		for (T obj : objList) {
			sb.append(lcoalWrapper + obj + lcoalWrapper).append(lcoaldelimiter);
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public static <T> String collectionToString(Collection<T> objList, String delimiter) {
		final String lcoaldelimiter = delimiter == null ? DEFAULTDELIMITER : delimiter;
		StringBuilder sb = new StringBuilder();
		for (T obj : objList) {
			sb.append(obj).append(lcoaldelimiter);
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public static <T> String collectionToWrapperString(List<T> objList, String wrapper) {
		return collectionToWrapperString(objList, wrapper, DEFAULTDELIMITER);
	}

	public static <T> String collectionToString(List<T> objList) {
		return collectionToString(objList, DEFAULTDELIMITER);
	}

	public static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(str.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

	public static boolean equals(String str1, String str2) {
		return str1 == null ? str2 == null : str1.equals(str2);
	}

	public static boolean equalsIgnoreCase(String str1, String str2) {
		return str1 == null ? str2 == null : str1.equalsIgnoreCase(str2);
	}
	
	public static String firstCharUpperCase(String str) {  
	    char[] ch = str.toCharArray();  
	    if (ch[0] >= 'a' && ch[0] <= 'z') {  
	        ch[0] = (char) (ch[0] - 32);  
	    }  
	    return new String(ch);  
	}  
}
