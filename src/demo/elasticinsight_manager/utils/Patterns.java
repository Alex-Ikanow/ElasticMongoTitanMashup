package demo.elasticinsight_manager.utils;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Patterns {

	// CONSTRUCTORS
	
	public static <G> Matcher<Object, G> matchAndReturn(G g) {
		return new Matcher<Object, G>(g);
	}
	public static <G, R> Matcher<R, G> matchAndReturn(G g, Class<R> clazz) {
		return new Matcher<R, G>(g);
	}
	public static <G> ActionMatcher<G> matchAndAct(G g) {
		return new ActionMatcher<G>(g);
	}
	
	public static class Matcher<R, G> {
		protected R _r;
		protected boolean _r_set = false;
		protected G _g;
		protected Matcher(G g) {
			_g = g;
		}
		
		// PATTERN MATCHING
		
		public <P> Matcher<R, G> when(Class<P> clazz, Function<P, R> expression) {
			if (!_r_set && clazz.isAssignableFrom(_g.getClass())) {
				_r = expression.apply(clazz.cast(_g));
				_r_set = true;
			}
			return this;
		}
		public <P> Matcher<R, G> when(Class<P> clazz, Predicate<P> predicate, Function<P, R> expression) {
			if (!_r_set && clazz.isAssignableFrom(_g.getClass())) {
				P p = clazz.cast(_g);
				if (predicate.test(p)) {
					_r = expression.apply(p);
					_r_set = true;
				}
			}
			return this;
		}
		public Matcher<R, G> when(Predicate<G> predicate, Function<G, R> expression) {
			if (!_r_set && predicate.test(_g)) {
				_r = expression.apply(_g);
				_r_set = true;
			}
			return this;
		}
		
		// DEFAULT OPERATIONS
		
		@SuppressWarnings("unchecked")
		public <RR> RR otherwise(Function<G, RR> expression) {
			if (_r_set) {
				return (RR) _r;
			}
			else {
				return expression.apply(_g);
			}
		}
		@SuppressWarnings("unchecked")
		public <RR> RR otherwise(RR r) {
			if (_r_set) {
				return (RR) _r;
			}
			else {
				return r;
			}
		}
		@SuppressWarnings("unchecked")
		public <RR> RR otherwise(Throwable t) throws Throwable {
			if (_r_set) {
				return (RR) _r;
			}
			else {
				throw t;
			}
		}
		public void otherwise(Consumer<G> expression) {
			if (!_r_set) {
				expression.accept(_g);
			}
		}
	}

	public static class ActionMatcher<G> {
		protected boolean _r_set = false;
		protected G _g;
		boolean _allow_multiple = false;
		protected ActionMatcher(G g) {
			_g = g;
		}

		// OPTIONS
		
		public ActionMatcher<G> allowMultiple() {
			_allow_multiple = true;
			return this;
		}
		
		// PATTERN MATCHING
		
		public <P> ActionMatcher<G> when(Class<P> clazz, Consumer<P> expression) {
			if ((!_r_set || _allow_multiple) && (clazz.isAssignableFrom(_g.getClass()))) {
				expression.accept(clazz.cast(_g));
				_r_set = true;
			}
			return this;
		}		
		public <P> ActionMatcher<G> when(Class<P> clazz, Predicate<P> predicate, Consumer<P> expression) {
			if ((!_r_set || _allow_multiple) && (clazz.isAssignableFrom(_g.getClass()))) {
				P p = clazz.cast(_g);
				if (predicate.test(p)) {
					expression.accept(p);
					_r_set = true;
				}
			}
			return this;
		}
		public ActionMatcher<G> when(Predicate<G> predicate, Consumer<G> expression) {
			if (!_r_set && predicate.test(_g)) {
				expression.accept(_g);
				_r_set = true;
			}
			return this;
		}
		
		// DEFAULT OPERATIONS
		
		public void otherwise(Throwable t) throws Throwable {
			if (!_r_set) {
				throw t;
			}
		}
		public void otherwise(Consumer<G> expression) {
			if (!_r_set) {
				expression.accept(_g);
			}
		}		
	}	
}
