package demo.elasticinsight_manager.utils;

// Immutable tuples class
// Call t = Tuples._<n>(_1, ..., _n) and then t._1(), t._2() etc 

public class Tuples {

	public static <A, B> _2T<A, B> _2(A a, B b) { return new _2T<A, B>(a, b); }
	public static <A, B, C> _3T<A, B, C> _3(A a, B b, C c) { return new _3T<A, B, C>(a, b, c); }
	public static <A, B, C, D> _4T<A, B, C, D> _4(A a, B b, C c, D d) { return new _4T<A, B, C, D>(a, b, c, d); }
	public static <A, B, C, D, E> _5T<A, B, C, D, E> _5(A a, B b, C c, D d, E e) { return new _5T<A, B, C, D, E>(a, b, c, d, e); }
	
	public static class _2T<A, B> {
		public _2T(A a, B b) { _1 = a; _2 = b; }
		private A _1;
		private B _2;
		public A _1() { return _1; }
		public B _2() { return _2; }
	}
	public static class _3T<A, B, C> {
		public _3T(A a, B b, C c) { _1 = a; _2 = b; _3 = c; }
		private A _1;
		private B _2;
		private C _3;
		public A _1() { return _1; }
		public B _2() { return _2; }
		public C _3() { return _3; }
	}
	public static class _4T<A, B, C, D> {
		public _4T(A a, B b, C c, D d) { _1 = a; _2 = b; _3 = c; _4 = d;}
		private A _1;
		private B _2;
		private C _3;
		private D _4;
		public A _1() { return _1; }
		public B _2() { return _2; }
		public C _3() { return _3; }
		public D _4() { return _4; }
	}
	public static class _5T<A, B, C, D, E> {
		public _5T(A a, B b, C c, D d, E e) { _1 = a; _2 = b; _3 = c; _4 = d; _5 = e; }
		private A _1;
		private B _2;
		private C _3;
		private D _4;
		private E _5;
		public A _1() { return _1; }
		public B _2() { return _2; }
		public C _3() { return _3; }
		public D _4() { return _4; }
		public E _5() { return _5; }
	}
	
	// Example code
	public static void main(String[] args) {
		final String s = "test";
		final Boolean b = false;
		final int n = 4;
		Tuples._2(s, b);
		Tuples._3(2, s, b);		
		Tuples._4(2, s, b, n);		
	}
}
