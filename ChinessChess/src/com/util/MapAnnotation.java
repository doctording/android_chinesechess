package com.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author ycchen
 * 
 */
public class MapAnnotation {

	public final static int DEFAULT = 0;
	public final static int ARRAY = 1;
	public final static int OBJECT = 2;
	public static final int LIST = 3;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@Documented
	public @interface Map {
		String key() default "";

		int index() default 0;

		int type() default DEFAULT;
	}

}
