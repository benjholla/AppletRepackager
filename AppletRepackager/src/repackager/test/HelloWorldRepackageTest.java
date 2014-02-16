package repackager.test;

import java.io.File;

import repackager.AppletRepackager;

public class HelloWorldRepackageTest {

	// repackage test
	// repackaging applet at http://www.genlogic.com/java2/scada_viewer.html
	// <applet code="HelloWorld.class" archive="HelloWorld.jar" width=150 height=25></applet>
	public static void main(String[] args) throws Exception {
		
		// need to specify the path to your JDK install
		String jdkPath = "C:\\Program Files\\Java\\jdk1.7.0_51";
		
		// this is the "code" attribute without the ".class". The ".class" appears to be optional anyway
		String qualifiedTargetClassName = "HelloWorld";
		
		String wrapperClassName = "Wrapper";
		String wrapperPackageName = "";
		String qualifiedWrapperName = wrapperPackageName.equals("") ? wrapperClassName : (wrapperPackageName + "." + wrapperClassName);
		
		// the original jar is the "archive" attribute
		File originalJar = new File("C:\\Users\\Ben\\Desktop\\HelloWorld.jar");
		File repackedJar = new File(originalJar.getAbsolutePath().replace(".jar", "_repackaged.jar"));
		
		// one or more payloads to insert (called in the order specified)
		File testPayloadSource = new File("C:\\Users\\Ben\\Desktop\\source\\TestPayload.java");
		
		// repackage the archive
		AppletRepackager.repackageJar(jdkPath, qualifiedTargetClassName, qualifiedWrapperName, originalJar, repackedJar, testPayloadSource);
		
		System.out.println("The applet HTML element should be update to match:");
		System.out.println("<applet code=\"" + qualifiedWrapperName + ".class\" archive=\"" + repackedJar.getName() + "\" width=150 height=25></applet>");
	}
	
}
