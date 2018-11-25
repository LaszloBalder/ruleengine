package eval;

import junit.framework.TestCase;

public class XsdTest extends TestCase {

	public void testLoadFile() {
		Xsd xsd = new Xsd();
		xsd.loadFile("ApplicationModel_1.1.1.003.xsd");
		assertTrue(xsd.getComplexTypes().size()>0);
		assertTrue(xsd.getComplexType("StationFacilityRef").isReferenceType());
		assertFalse(xsd.getComplexType("StationFacilityDto").isReferenceType());
		assertTrue(xsd.getComplexType("StationFacilityDto").getIdAttributeName().equals("dtoId"));
	}
}
