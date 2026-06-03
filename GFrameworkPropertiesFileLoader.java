/*
 * Copyright 2026 Kyocera Communication Systems Co., Ltd All rights reserved.
 */
package jp.co.kccs.greenearth.commons;

import jp.co.kccs.greenearth.commons.service.GScanComponentService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;

/**
 * 元々GFrameworkPropertiesにある「ge-framework.properties」の読み込む処理は、ここに切り出します.<br>
 *
 * @create 2026.06.03
 * @author KCSS yangfeng
 * @since GEF_NEXT_VERSION
 */
public class GFrameworkPropertiesFileLoader {
	
	/** importプロパティファイルキー名 */
	private static final String IMPORT_FILE_KEY = "@import.File";
	
	public static Properties getProperties() {
		return loadProperties();
	}
	
	public static String getScanPackagesProperty() {
		return getProperties().getProperty(GScanComponentService.GEFRAME_CORE_SERVICE_SCAN_PACKAGES, "");
	}
	
	protected static URL getResource(String path) {
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (null == classLoader) {
			classLoader = GFrameworkPropertiesFileLoader.class.getClassLoader();
		}
		return classLoader.getResource(path);
	}
	
	/**
	 * プロパティファイル情報を読み込みます.
	 * 
	 * @create 2007/01/24
	 * @throws MissingResourceException プロパティファイルが見つからない場合
	 * @author kitamura
	 * @since 3.0.0
	 */
	protected static Properties loadProperties() throws MissingResourceException {
		String path = GFrameworkProperties.getPropertiesResourceName();
		URL url = getResource(path);
		if (url == null) {
			return new Properties();
		}

		Properties properties = new Properties();
		InputStream stream = null;
		try {
			stream = url.openStream();
			properties.load(stream);
		} catch (IOException e) {
			throw new MissingResourceException(e.getMessage(), path, "");
		} finally {
			if (null != stream) {
				try {
					stream.close();
				} catch (IOException e) {
					throw new MissingResourceException(e.getMessage(), path, "");
				}
			}
		}
		// 追加プロパティファイル読込
		loadImportProperty(properties);

		return properties;
	}
	
	/**
	 * 追加プロパティファイル情報を読み込みます。
	 * 
	 * @create 2013/04/17
	 * @param properties プロパティ
	 * @throws MissingResourceException プロパティファイルが見つからない場合
	 * @author Saiga
	 * @since 3.0.0
	 */
	private static void loadImportProperty(Properties properties) throws MissingResourceException {

		List<String> importKeys = new ArrayList<String>();
		for (String key : properties.stringPropertyNames()) {
			if (!key.startsWith(IMPORT_FILE_KEY)) {
				continue;
			}
			importKeys.add(key);
		}
		Collections.sort(importKeys, new Comparator<String>() {
			@Override
			public int compare(String val1, String val2) {
				int num1 = Integer.parseInt(val1.replace(IMPORT_FILE_KEY, ""));
				int num2 = Integer.parseInt(val2.replace(IMPORT_FILE_KEY, ""));
				return num1 - num2;
			}
		});

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (null == classLoader) {
			classLoader = GFrameworkPropertiesFileLoader.class.getClassLoader();
		}

		Properties branchProperties = new Properties();
		for (String importKey : importKeys) {
			String path = properties.getProperty(importKey);
			
			URL url = classLoader.getResource(path);
			if (url == null) {
				throw new MissingResourceException("property file is not found. : " + path, path, "");
			}

			Properties leafProperties = new Properties();
			InputStream stream = null;
			try {
				stream = url.openStream();
				leafProperties.load(stream);
			} catch (IOException e) {
				throw new MissingResourceException(e.getMessage(), path, "");
			} finally {
				if (null != stream) {
					try {
						stream.close();
					} catch (IOException e) {
						throw new MissingResourceException(e.getMessage(), path, "");
					}
				}
			}
			loadImportProperty(leafProperties);
			branchProperties.putAll(leafProperties);
		}
		properties.putAll(branchProperties);
	}
}
