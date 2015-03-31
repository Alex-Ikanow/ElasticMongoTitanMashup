package demo.elasticinsight_manager.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestPatterns {

	@Test
	public void MatchReturnTest_ClassAndConditions() {
		Object o = new Double(5.0); 
		String s = Patterns.matchAndReturn(o)
			.when(String.class, ss -> ss + ": string")
			.when(Double.class, t -> t < 4.0, d -> "1: " + d.getClass().toString())
			.when(Double.class, d -> "2: " + d.getClass().toString())
			.when(Boolean.class, b -> b ? "true" : "false")
			.otherwise("unknown");

		assertEquals("Should fail condition and return on class match", "2: class java.lang.Double", s);
		
		o = new Double(3.0); 
		s = Patterns.matchAndReturn(o)
				.when(String.class, ss -> ss + ": string")
				.when(Double.class, t -> t < 4.0, d -> "1: " + d.getClass().toString())
				.when(Double.class, d -> "2: " + d.getClass().toString())
				.when(Boolean.class, b -> b ? "true" : "false")
				.otherwise("unknown");		
		
		assertEquals("Should pass condition and return accordingly", "1: class java.lang.Double", s);
		
	}

	private String _retVal;
	
	@Test
	public void MatchActTest_ClassAndConditions() {
		Object o = new Double(5.0); 
		_retVal = null;
		Patterns.matchAndAct(o)
			.when(String.class, ss -> _retVal = (ss + ": string"))
			.when(Double.class, t -> t < 4.0, d -> _retVal = ("1: " + d.getClass().toString()))
			.when(Double.class, d -> _retVal = ("2: " + d.getClass().toString()))
			.when(Boolean.class, b -> _retVal = (b ? "true" : "false"))
			.otherwise(oo -> _retVal = ("unknown: " + oo));
		
		assertEquals("Should fail condition and assign on class match", "2: class java.lang.Double", _retVal);
		
		o = new Double(3.0); 
		_retVal = null;
		Patterns.matchAndAct(o)
			.when(String.class, ss -> _retVal = (ss + ": string"))
			.when(Double.class, t -> t < 4.0, d -> _retVal = ("1: " + d.getClass().toString()))
			.when(Double.class, d -> _retVal = ("2: " + d.getClass().toString()))
			.when(Boolean.class, b -> _retVal = (b ? "true" : "false"))
			.otherwise(oo -> _retVal = ("unknown: " + oo));
		
		assertEquals("Should pass condition and assign accordingly", "1: class java.lang.Double", _retVal);
	}

	@Test
	public void MatchActTest_ClassAndConditions_AllowMultiple() {
		Object o = new Double(5.0); 
		_retVal = null;
		Patterns.matchAndAct(o)
			.allowMultiple()
			.when(String.class, ss -> _retVal = (ss + ": string"))
			.when(Double.class, t -> t < 4.0, d -> _retVal = ("1: " + d.getClass().toString()))
			.when(Double.class, d -> _retVal = ("2: " + d.getClass().toString()))
			.when(Boolean.class, b -> _retVal = (b ? "true" : "false"))
			.when(Object.class, ooo -> _retVal += (" (object)"))
			.otherwise(oo -> _retVal = ("unknown: " + oo));
		
		assertEquals("Should fail condition and assign on class match", "2: class java.lang.Double (object)", _retVal);
		
		o = new Double(3.0); 
		_retVal = null;
		Patterns.matchAndAct(o)
			.allowMultiple()
			.when(String.class, ss -> _retVal = (ss + ": string"))
			.when(Double.class, t -> t < 4.0, d -> _retVal = ("1: " + d.getClass().toString()))
			.when(Double.class, d -> _retVal += (" 2: " + d.getClass().toString()))
			.when(Boolean.class, b -> _retVal = (b ? "true" : "false"))
			.otherwise(oo -> _retVal = ("unknown: " + oo));
		
		assertEquals("Should pass condition and assign accordingly", "1: class java.lang.Double 2: class java.lang.Double", _retVal);
	}
	
	//TODO test otherwise
	//TODO integrate test coverage
}
