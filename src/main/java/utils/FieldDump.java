package utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public class FieldDump {

	public static HashMap<String, Object> dump(Object o, Class<?> c) {
		HashMap<String, Object> res = new HashMap<String, Object>();
		Field[] fields = c.getDeclaredFields();
		for (Field f : fields) {
			if (!Modifier.isStatic(f.getModifiers())) {
				f.setAccessible(true);
				try {
					Object value = f.get(o);
					res.put(f.getName(), value);
				} catch (Exception e) {

				}

			}
		}

		return res;

	}
}
