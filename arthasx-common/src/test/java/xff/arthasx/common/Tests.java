package xff.arthasx.common;

import org.junit.Test;

public class Tests {

	@Test
	public void testLogError() throws Exception {
		AnsiLog.error("abc,{},{}",new Exception("abc"),1,2);
	}
}
