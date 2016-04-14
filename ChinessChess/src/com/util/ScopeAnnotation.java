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
public class ScopeAnnotation {

	public static final int ALL = 0;
	public static final int FRIEND_ITEM = 1;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@Documented
	public @interface Scope {
		int value() default ALL;
	}

}
