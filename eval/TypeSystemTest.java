package eval;

import util.StopWatch;
import junit.framework.TestCase;

public class TypeSystemTest extends TestCase {

	public void testLoadFile() {
		Xsd xsd = new Xsd();
		xsd.setXsdDateType(new XsdDateType("DatumType", "Jaar", "Maand", "Dag"));
		xsd.setXsdTimeType(new XsdTimeType("TijdType", "Uur", "Minuten", "Seconden"));
		StopWatch sw = new StopWatch();
		sw.start();
		//xsd.loadFile("bericht_AX_WF_7_0_3.xsd");
		xsd.loadFile("src/valdef/ApplicationModel_1.0.0.1.xsd");
		
		//xsd.loadFile("src/valdef/EnginesForm.xsd");
		sw.stop();
//		System.out.println(xsd.getElements().size());
//		System.out.println("------------------");
		for (ComplexType ct :  xsd.getComplexTypes().values()) {
			System.out.println(ct.getName() + " " + ct.getFields().size());
		}
		System.out.println(xsd.getXsdElementType("EngineDto.IsValid").getType());
		System.out.println(sw.getTimeInMillis());
	}
}
