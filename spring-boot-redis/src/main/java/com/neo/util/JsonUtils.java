package com.neo.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public abstract class JsonUtils {
	private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

	private static final String STANDARD_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private static final String DATE_PATTERN = "yyyy-MM-dd";
	private static final String TIME_PATTERN = "HH:mm:ss";

	private static final ObjectMapper jsonMapper = new ObjectMapper() {
		private static final long serialVersionUID = 1L;
		{
			//设置java.util.Date时间类的序列化以及反序列化的格式
			setDateFormat(new SimpleDateFormat(STANDARD_PATTERN));

			// 初始化JavaTimeModule
			JavaTimeModule javaTimeModule = new JavaTimeModule();
			//处理LocalDateTime
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(STANDARD_PATTERN);
			javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
			javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));

			//处理LocalDate
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
			javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
			javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));

			//处理LocalTime
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_PATTERN);
			javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormatter));
			javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(timeFormatter));

			//注册时间模块, 支持支持jsr310, 即新的时间类(java.time包下的时间类)
			registerModule(javaTimeModule);

			// 包含所有字段
			setSerializationInclusion(JsonInclude.Include.ALWAYS);
			// 在序列化一个空对象时时不抛出异常
			disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

			// 忽略反序列化时在json字符串中存在, 但在java对象中不存在的属性
			disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

			configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
			configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
		}
	};

	/**
	 * 将Object对象转化为JSON字符串，如果转换失败则抛出异常。
	 * 
	 * @param object
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static String toJson(Object object) throws IllegalArgumentException {
		if (null == object) {
			throw new IllegalArgumentException("对象不允许为NULL");
		}
		try {
			return jsonMapper.writeValueAsString(object);
		} catch (Throwable e) {
			throw new IllegalArgumentException("无法将对象转化为JSON字符串，" + e.getMessage(), e);
		}
	}

	/**
	 * 将Object对象转化为JSON字符串，保证不会抛出异常，如果转不成功，则返回空字符串。
	 * 
	 * @param object
	 * @return 保证不为NULL
	 */
	public static String toJsonQuietly(Object object) {
		if (null == object) {
			return "";
		}
		try {
			return toJson(object);
		} catch (IllegalArgumentException e) {
			logger.error("无法将对象转化为JSON字符串，将返回空字符串作为结果", e);
		}
		return "";
	}

	/**
	 * 将JSON字符串转化为Object对象，如果转不成功，抛出异常。
	 * 
	 * @param jsonString
	 * @param targetObjectType
	 * @return
	 */
	public static <T> T parseFromJson(String jsonString, Class<T> targetObjectType) {
		if (null == jsonString || 0 == jsonString.length()) {
			return null;
		}
		try {
			return jsonMapper.readValue(jsonString, targetObjectType);
		} catch (Throwable e) {
			throw new IllegalArgumentException("无法将JSON字符串转化为Java对象，" + e.getMessage(), e);
		}
	}

	/**
	 * 将JSON字符串转化为Object对象，如果转不成功，抛出异常。
	 * 
	 * @param jsonString
	 * @param targetObjectJavaType
	 * @return
	 */
	public static <T> T parseFromJson(String jsonString, JavaType targetObjectJavaType) {
		if (null == jsonString || 0 == jsonString.length()) {
			return null;
		}
		try {
			return jsonMapper.readValue(jsonString, targetObjectJavaType);
		} catch (Throwable e) {
			throw new IllegalArgumentException("无法将JSON字符串转化为Java对象，" + e.getMessage(), e);
		}
	}

	/**
	 * 将JSON字符串转化为Object对象，如果转不成功，抛出异常。
	 * 
	 * @param jsonString
	 * @param targetObjectType
	 * @param parametricType
	 *            如果Java Bean类型依赖了另一层泛型，则通过该参数指定Java Bean上依赖的具体类型
	 * @return
	 */
	public static <T> T parseFromJsonWithParametricType(String jsonString, Class<T> targetObjectType,
			Class<?>... parametricType) {
		if (null == jsonString || 0 == jsonString.length()) {
			return null;
		}
		try {
			if (null != parametricType && parametricType.length > 0) {
				return jsonMapper.readValue(jsonString, getBeanParametericType(targetObjectType, parametricType));
			} else {
				return jsonMapper.readValue(jsonString, targetObjectType);
			}
		} catch (Throwable e) {
			throw new IllegalArgumentException("无法将JSON字符串转化为Java对象，" + e.getMessage(), e);
		}
	}

	/**
	 * 将JSON字符串转化为Object对象，如果转不成功，抛出异常。
	 * 
	 * @param jsonString
	 * @param targetObjectType
	 * @param parametricType
	 *            如果Java Bean类型依赖了另多层泛型（套了两层以上），则通过该参数指定Java Bean上依赖的具体类型
	 * @return
	 */
	public static <T> T parseFromJsonWithParametricType(String jsonString, Class<T> targetObjectType,
			JavaType... parametricType) {
		if (null == jsonString || 0 == jsonString.length()) {
			return null;
		}
		try {
			if (null != parametricType && parametricType.length > 0) {
				JavaType type = jsonMapper.getTypeFactory().constructParametricType(targetObjectType, parametricType);
				return jsonMapper.readValue(jsonString, type);
			} else {
				return jsonMapper.readValue(jsonString, targetObjectType);
			}
		} catch (Throwable e) {
			throw new IllegalArgumentException("无法将JSON字符串转化为Java对象，" + e.getMessage(), e);
		}
	}

	/**
	 * 将JSON字符串转化为Object对象，保证不会抛出异常，如果转不成功，则返回NULL。
	 * 
	 * @param jsonString
	 * @param targetObjectType
	 *            带泛型的类型
	 * @return
	 */
	public static <T> T parseFromJsonQuietly(String jsonString, Class<T> targetObjectType) {
		if (null == jsonString || 0 == jsonString.length()) {
			return null;
		}
		try {
			return jsonMapper.readValue(jsonString, targetObjectType);
		} catch (Throwable e) {
			logger.error("无法将JSON字符串转化为Java对象，{}", e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 将JSON字符串转化为Object对象，保证不会抛出异常，如果转不成功，则返回NULL。
	 * 
	 * @param jsonString
	 * @param targetObjectType
	 *            带泛型的类型
	 * @param parametricType
	 *            如果Java Bean类型依赖了另一层泛型，则通过该参数指定Java Bean上依赖的具体类型
	 * @return
	 */
	public static <T> T parseFromJsonQuietlyWithParametricType(String jsonString, Class<T> targetObjectType,
			Class<?>... parametricType) {
		if (null == jsonString || 0 == jsonString.length()) {
			return null;
		}
		try {
			if (null != parametricType && parametricType.length > 0) {
				return jsonMapper.readValue(jsonString, getBeanParametericType(targetObjectType, parametricType));
			} else {
				return jsonMapper.readValue(jsonString, targetObjectType);
			}
		} catch (Throwable e) {
			logger.error("无法将JSON字符串转化为Java对象，{}", e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 将JSON字符串转化为Object对象，保证不会抛出异常，如果转不成功，则返回NULL。
	 * 
	 * @param jsonString
	 * @param targetObjectType
	 *            带泛型的类型
	 * @param parametricType
	 *            如果Java Bean类型依赖了另多层泛型（套了两层以上），则通过该参数指定Java Bean上依赖的具体类型
	 * @return
	 */
	public static <T> T parseFromJsonQuietlyWithParametricType(String jsonString, Class<T> targetObjectType,
			JavaType... parametricType) {
		if (null == jsonString || 0 == jsonString.length()) {
			return null;
		}
		try {
			if (null != parametricType && parametricType.length > 0) {
				JavaType type = jsonMapper.getTypeFactory().constructParametricType(targetObjectType, parametricType);
				return jsonMapper.readValue(jsonString, type);
			} else {
				return jsonMapper.readValue(jsonString, targetObjectType);
			}
		} catch (Throwable e) {
			logger.error("无法将JSON字符串转化为Java对象，{}", e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 将json字符串反序列化为List&lt;T&gt;类型对象
	 * 
	 * @param json
	 *            需要序列化的目标字符串
	 * @param clazz
	 *            集合中元素的类型
	 * @param collectionClass
	 *            集合类型
	 */
	public static <T> List<T> parseParametricFromJson(String json, Class<T> clazz, Class<?> collectionClass) {
		try {
			return jsonMapper.readValue(json, getParametricType(jsonMapper, collectionClass, clazz));
		} catch (IOException e) {
			logger.warn("反序列化失败.", e);
		}
		return null;
	}

	public static JavaType getBeanParametericType(Class<?> targetObjectType, Class<?>... parametricType) {
		return jsonMapper.getTypeFactory().constructParametricType(targetObjectType, parametricType);
	}

	public static JavaType getJavaType(Class<?> clz) {
		return jsonMapper.getTypeFactory().constructType(clz);
	}

	public static JavaType[] findTypeParameters(JavaType subClassJavaType, Class<?> superClass) {
		return jsonMapper.getTypeFactory().findTypeParameters(subClassJavaType, superClass);
	}

	/**
	 * 获取泛型的Collection Type
	 * 
	 * @param collectionClass
	 *            泛型的Collection
	 * @param elementClasses
	 *            元素类
	 * @return JavaType Java类型
	 * @since 1.0
	 */
	private static JavaType getParametricType(ObjectMapper mapper, Class<?> collectionClass,
			Class<?>... elementClasses) {
		return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
	}
}
