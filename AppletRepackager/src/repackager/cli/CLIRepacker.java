package repackager.cli;

import java.io.File;
import java.util.Scanner;

import repackager.AppletRepackager;
import repackager.JarUtils;

public class CLIRepacker {

	// repackage test
	// repackaging applet at http://www.genlogic.com/java2/scada_viewer.html
	// <applet code="HelloWorld.class" archive="HelloWorld.jar" width=150 height=25></applet>
	public static void main(String[] args) throws Exception {
		
		Scanner scanner = new Scanner(System.in);
		
		System.out.print("Please enter your JDK Path:");
		String jdkPath = scanner.nextLine();

		System.out.print("Please enter your qualified target class name: ");
		String qualifiedTargetClassName = scanner.nextLine();
		
		String wrapperClassName = "Wrapper";
		String wrapperPackageName = "";
		String qualifiedWrapperName = wrapperPackageName.equals("") ? wrapperClassName : (wrapperPackageName + "." + wrapperClassName);
		
		System.out.print("Please enter the original Jar:");
		File originalJar = new File(scanner.nextLine());
		File repackedJar = new File(originalJar.getAbsolutePath().replace(".jar", "_repackaged.jar"));
		
		System.out.print("Please enter the payload:");
		File testPayloadSource = new File(scanner.nextLine());

		AppletRepackager.repackageJar(jdkPath, qualifiedTargetClassName, qualifiedWrapperName, originalJar, repackedJar, JarUtils.generateEmptyManifest(), testPayloadSource);
		
		System.out.println("The applet HTML element should be update to match:");
		System.out.println("<applet code=\"" + qualifiedWrapperName + ".class\" archive=\"" + repackedJar.getName() + "\" width=150 height=25></applet>");
	
		scanner.close();
	}

}
