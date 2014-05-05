package repackager.test;

import java.io.File;

import repackager.AppletRepackager;
import repackager.JarUtils;

public class SCADADemoRepackageTest {

	// repackage test
	// repackaging applet at http://www.genlogic.com/java2/scada_viewer.html
	// <applet code="GlgSCADAViewer.class" archive="GlgDemo.jar,GlgCE.jar" name="process" mayscript="true" height="700" width="800">
	//     <param name="DrawingURL" value="scada_main.g">
	//     <param name="ConfigFile" value="scada_config_menu.txt">
	// </applet>
	public static void main(String[] args) throws Exception {
		// need to specify the path to your JDK install
		String jdkPath = "C:\\Program Files\\Java\\jdk1.7.0_51";
		
		// this is the "code" attribute without the ".class". The ".class" appears to be optional anyway
		String qualifiedTargetClassName = "GlgSCADAViewer";
		
		String wrapperClassName = "Wrapper";
		String wrapperPackageName = "";
		String qualifiedWrapperName = wrapperPackageName.equals("") ? wrapperClassName : (wrapperPackageName + "." + wrapperClassName);
		
		// the original jar is the "archive" attribute
		File originalJar = new File("C:\\Users\\Ben\\Desktop\\GlgDemo.jar");
		File repackedJar = new File(originalJar.getAbsolutePath().replace(".jar", "_repackaged.jar"));
		
		// one or more payloads to insert (called in the order specified)
		File testPayloadSource = new File("C:\\Users\\Ben\\Desktop\\source\\TestPayload.java");
		
		// repackage the archive
		AppletRepackager.repackageJar(jdkPath, qualifiedTargetClassName, qualifiedWrapperName, originalJar, repackedJar, JarUtils.generateEmptyManifest(), testPayloadSource);
		
		System.out.println("The applet HTML element should be update to match:");
		System.out.println("<applet code=\"" + qualifiedWrapperName + ".class\" archive=\"" + repackedJar.getName() + ",GlgCE.jar\" ...");
	}
	
}
