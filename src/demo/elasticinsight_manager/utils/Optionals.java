package demo.elasticinsight_manager.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * Utility class that converts a null collection/iterable into an empty one
 */
public class Optionals {
	
	/**
	 * @param a collection of Ts
	 * @return the collection, or an empty collection if "ts" is null
	 */
	public static <T> Collection<T> ofNullable(Collection<T> ts) {
		return Optional.ofNullable(ts).orElse(Collections.emptyList());
	}
	/**
	 * @param ts
	 * @return the iterable, or an empty iterable if "ts" is null
	 */
	public static <T> Iterable<T> ofNullable(Iterable<T> ts) {
		return Optional.ofNullable(ts).orElse(Collections.emptyList());
	}
}
