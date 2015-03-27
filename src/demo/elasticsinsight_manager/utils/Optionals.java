package demo.elasticsinsight_manager.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class Optionals {

	public static <T> Collection<T> ofNullable(Collection<T> ts) {
		return Optional.ofNullable(ts).orElse(Collections.emptyList());
	}
	public static <T> Iterable<T> ofNullable(Iterable<T> ts) {
		return Optional.ofNullable(ts).orElse(Collections.emptyList());
	}
}
