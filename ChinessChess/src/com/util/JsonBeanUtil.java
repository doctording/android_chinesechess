package com.util;


import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;

import com.util.MapAnnotation.Map;
import com.util.ScopeAnnotation.Scope;

/**
 * JSON序列化和反序列化
 */
public class JsonBeanUtil {
	public static final int ALL = 0;
	public static final int DEFAULT = 0;
	public static final int FRIEND_ITEM = 1;
	public final static int ARRAY = 1;
	public final static int OBJECT = 2;
	public static final int LIST = 3;

	public static <T> ArrayList<T> convertList(Class<T> clazz, JSONArray values)
			throws Exception {
		return convertList(clazz, values, ALL);
	}

	@SuppressWarnings("unchecked")
	public static <T> ArrayList<T> convertList(Class<T> clazz,
			JSONArray values, int scope) throws Exception {
		ArrayList<T> list = new ArrayList<T>();
		try {
			Field[] fields = clazz.getDeclaredFields();

			if (clazz.getSuperclass() != Object.class) {
				Field[] superfields = clazz.getSuperclass().getDeclaredFields();
				Field[] result = new Field[fields.length + superfields.length];
				System.arraycopy(fields, 0, result, 0, fields.length);
				System.arraycopy(superfields, 0, result, fields.length,
						superfields.length);
				fields = result;
			}

			if (scope != ALL) {
				ArrayList<Field> resultFields = new ArrayList<Field>();
				for (Field field : fields) {
					Scope scopeAnnotation = field.getAnnotation(Scope.class);
					if (scopeAnnotation != null) {
						int fieldScope = scopeAnnotation.value();
						if (fieldScope == scope) {
							resultFields.add(field);
						}
					}
				}
				fields = resultFields.toArray(new Field[resultFields.size()]);
			}

			for (int i = 0; i < values.length(); i++) {
				JSONObject jo = values.optJSONObject(i);
				if (jo != null) {
					list.add((T) convertObjectFromJsonObject(clazz, fields, jo,
							scope));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return list;
	}

	public static <T> T convertObjectFromJsonObject(Class<T> clazz,
			JSONObject jo) throws Exception {
		return convertObjectFromJsonObject(clazz, jo, ALL);
	}

	public static <T> T convertObjectFromJsonObject(Class<T> clazz,
			JSONObject jo, int scope) throws Exception {
		Field[] fields = clazz.getDeclaredFields();

		if (clazz.getSuperclass() != Object.class) {
			Field[] superfields = clazz.getSuperclass().getDeclaredFields();
			Field[] result = new Field[fields.length + superfields.length];
			System.arraycopy(fields, 0, result, 0, fields.length);
			System.arraycopy(superfields, 0, result, fields.length,
					superfields.length);
			fields = result;
		}

		if (scope != ALL) {
			ArrayList<Field> resultFields = new ArrayList<Field>();
			for (Field field : fields) {
				Scope scopeAnnotation = field.getAnnotation(Scope.class);
				if (scopeAnnotation != null) {
					int fieldScope = scopeAnnotation.value();
					if (fieldScope == scope || fieldScope == ALL) {
						resultFields.add(field);
					}
				}
			}
			fields = resultFields.toArray(new Field[resultFields.size()]);
		}
		return convertObjectFromJsonObject(clazz, fields, jo, scope);
	}

	private static <T> T convertObjectFromJsonObject(Class<T> clazz,
			Field[] fields, JSONObject jo, int scope) throws Exception {
		T obj = null;
		try {
			obj = clazz.newInstance();
			for (Field field : fields) {
				Map mapAnnotation = field.getAnnotation(Map.class);
				field.setAccessible(true);
				if (mapAnnotation != null) {
					String key = mapAnnotation.key();
					int type = mapAnnotation.type();

					if (type == DEFAULT) {
						String value = jo.optString(key, null);
						if (value == null || "null".equals(value)
								|| "".equals(value)) {
							continue;
						}
						setValue(obj, field, value);

					} else if (type == ARRAY) {
						JSONArray array = jo.optJSONArray(key);
						if (array == null) {
							continue;
						}
						field.set(obj,
								convertObjectFromArray(field.getType(), array));
					} else if (type == OBJECT) {
						JSONObject jobj = jo.optJSONObject(key);
						if (jobj == null) {
							continue;
						}
						field.set(
								obj,
								convertObjectFromJsonObject(field.getType(),
										jobj, scope));
					} else if (type == LIST) {
						ArrayList<T> list = new ArrayList<T>();
						JSONArray array = jo.optJSONArray(key);
						if (array != null) {
							for (int i = 0; i < array.length(); i++) {
								Object aobj = array.get(i);
								if (aobj == null) {
									continue;
								}
								if (aobj instanceof JSONObject) {
									list.add((T) convertObjectFromJsonObject(
											getEntityClass(field),
											(JSONObject) aobj, scope));
								} else if (aobj instanceof JSONArray) {
									list.add((T) convertObjectFromArray(
											getEntityClass(field),
											(JSONArray) aobj));
								} else {
									if (!aobj.getClass().getName()
											.contains("org.json.JSONObject")) {
										list.add((T) aobj);
									}
								}

							}
						}
						field.set(obj, list);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return obj;
	}

	private static Class<?> getEntityClass(Field field) {
		Type genType = field.getGenericType();
		if (genType instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) genType;
			Type type = pt.getActualTypeArguments()[0];
			return (Class<?>) type;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T convertObjectFromArray(Class<T> clazz, JSONArray array)
			throws Exception {
		T obj = clazz.newInstance();

		if (array.length() == 0) {
			return obj;
		}

		Field[] fields = clazz.getDeclaredFields();

		for (Field field : fields) {
			Map mapAnnotation = field.getAnnotation(Map.class);
			field.setAccessible(true);
			if (mapAnnotation != null) {
				int index = mapAnnotation.index();
				int type = mapAnnotation.type();

				if (type == DEFAULT) {
					String value = array.getString(index);
					if (value == null || "null".equals(value)) {
						continue;
					}
					setValue(obj, field, value);

				} else if (type == ARRAY) {
					JSONArray subArray = array.getJSONArray(index);
					if (subArray == null) {
						continue;
					}
					field.set(obj,
							convertObjectFromArray(field.getType(), subArray));

				} else if (type == OBJECT) {
					JSONObject jobj = array.getJSONObject(index);
					if (jobj == null) {
						continue;
					}
					field.set(obj,
							convertObjectFromJsonObject(field.getType(), jobj));

				} else if (type == LIST) {
					ArrayList<T> list = new ArrayList<T>();
					JSONArray subArray = array.optJSONArray(index);
					if (subArray != null) {
						for (int i = 0; i < subArray.length(); i++) {
							Object aobj = subArray.get(i);
							if (aobj instanceof JSONObject) {
								list.add((T) convertObjectFromJsonObject(
										getEntityClass(field),
										(JSONObject) aobj));
							} else if (aobj instanceof JSONArray) {
								list.add((T) convertObjectFromArray(
										getEntityClass(field), (JSONArray) aobj));
							} else {
								list.add((T) aobj);
							}

						}
					}
					field.set(obj, list);
				}

			}
		}

		return obj;
	}

	private static void setValue(Object obj, Field field, String value)
			throws Exception {
		Class<?> fieldType = field.getType();
		if (fieldType == String.class) {
			field.set(obj, value);
			return;
		} else if (fieldType == Boolean.TYPE) {
			field.set(obj, Boolean.parseBoolean(value));
			return;
		} else if (fieldType == Double.TYPE) {
			field.set(obj, Double.parseDouble(value));
			return;
		} else if (fieldType == Integer.TYPE) {
			field.set(obj, Integer.parseInt(value));
			return;
		} else if (fieldType == Date.class) {
			field.set(obj, parseISO8601Date(value));
			return;
		} else if (fieldType == JSONArray.class) {
			field.set(obj, new JSONArray(value));
			return;
		} else if (fieldType == JSONObject.class) {
			field.set(obj, new JSONObject(value));
			return;
		} else if (fieldType == Long.TYPE) {
			field.set(obj, Long.parseLong(value));
			return;
		}
		 else if(fieldType == BigDecimal.class)
		 {
		 field.set(obj, new BigDecimal(value));
		 }
	}

	private static final SimpleDateFormat mISO8601DateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

	static {
		mISO8601DateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	public static Date parseISO8601Date(String s) throws ParseException {
		synchronized (mISO8601DateFormat) {
			Date date = new Date(Long.parseLong(s));
			return date;
			// return mISO8601DateFormat.parse(s);
		}
	}

	public static String formatISO8601Date(Date d) {
		synchronized (mISO8601DateFormat) {
			return mISO8601DateFormat.format(d);
		}
	}

	public static String toJson(Object object) {
		String result = null;
		try {
			result = toJsonObject(object).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static JSONObject toJsonObject(Object object) throws Exception {
		JSONObject jo = new JSONObject();
		Class<? extends Object> clazz = object.getClass();
		Field[] fields = clazz.getDeclaredFields();

		if (clazz.getSuperclass() != Object.class) {
			Field[] superfields = clazz.getSuperclass().getDeclaredFields();
			Field[] result = new Field[fields.length + superfields.length];
			System.arraycopy(fields, 0, result, 0, fields.length);
			System.arraycopy(superfields, 0, result, fields.length,
					superfields.length);
			fields = result;
		}

		for (Field field : fields) {
			Map mapAnnotation = field.getAnnotation(Map.class);
			field.setAccessible(true);
			if (mapAnnotation != null) {
				String key = mapAnnotation.key();
				int type = mapAnnotation.type();
				Object value = field.get(object);
				if (value == null) {
					continue;
				}

				if (type == DEFAULT) {
					setJsonObject(field, jo, key, value);
				} else if (type == OBJECT) {
					jo.put(key, toJsonObject(value));
				} else if (type == LIST) {
					JSONArray ja = new JSONArray();
					for (Object subValue : (List) value) {
						if (isBaseType(subValue)) {
							ja.put(subValue);
						} else {
							ja.put(toJsonObject(subValue));
						}
					}
					jo.put(key, ja);
				} else if (type == ARRAY) {
					JSONArray ja = new JSONArray();
					for (Object subValue : (Object[]) value) {
						ja.put(subValue);
					}
					jo.put(key, ja);
				}

			}
		}
		return jo;
	}

	private static boolean isBaseType(Object obj) {
		boolean isBaseType = false;
		if (obj instanceof String) {
			isBaseType = true;
		} else if (obj instanceof Boolean) {
			isBaseType = true;
		} else if (obj instanceof Double) {
			isBaseType = true;
		} else if (obj instanceof Long) {
			isBaseType = true;
		} else if (obj instanceof Integer) {
			isBaseType = true;
		}else if (obj instanceof BigDecimal) {
			isBaseType = true;
		} else if (obj.getClass().isPrimitive()) {
			isBaseType = true;
		}
		return isBaseType;
	}

	private static JSONObject setJsonObject(Field field, JSONObject jo,
			String key, Object value) throws Exception {
		Class<?> fieldType = field.getType();
		if (value == null) {
			return jo;
		}

		if (fieldType == String.class) {
			jo.put(key, (String) value);
		} else if (fieldType == Boolean.TYPE) {
			jo.put(key, (Boolean) value);
		} else if (fieldType == Double.TYPE) {
			jo.put(key, (Double) value);
		} else if (fieldType == Integer.TYPE) {
			jo.put(key, (Integer) value);
		} else if (fieldType == Date.class) {
			Date date = ((Date) value);
			if (date != null) {
				jo.put(key, date.getTime());
			}
		} else if (fieldType == Long.TYPE) {
			jo.put(key, (Long) value);
		} else if (fieldType == BigDecimal.class) {
			jo.put(key, (BigDecimal) value);
		}
		return jo;
	}

}
