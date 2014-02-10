package repackager;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class AppletRepackager {

	public static final String META_INF = "META-INF";
	
	// some test code
	public static void main(String[] args) throws Exception {
//		File appletJarFile = new File("test.jar");
//		File extractedJarDirectory = ZipUtils.extractJar(appletJarFile);
//		File outputAppletJarFile = new File(appletJarFile.getAbsolutePath().replace(".jar", "") + "_repacked.jar");
//		unsign(extractedJarDirectory);
//		ZipUtils.compressJar(extractedJarDirectory, outputAppletJarFile);
//		System.out.println("Finished.");
		
		ArrayList<String> payloads = new ArrayList<String>();
		payloads.add("TestPayload");
		String targetClass = "Main";
		String wrapperName = "Wrapper";
		File outputFile = new File(wrapperName + ".java");
		generateWrapper(targetClass, wrapperName, payloads, outputFile);
		System.out.println(outputFile);
	}
	
	/*
	 * From: http://docs.oracle.com/javase/7/docs/technotes/tools/windows/jarsigner.html
	 * When jarsigner is used to sign a JAR file, the output signed JAR file is exactly the 
	 * same as the input JAR file, except that it has two additional files placed in the 
	 * META-INF directory: a signature file, with a .SF extension, and a signature block 
	 * file, with a .DSA, .RSA, or .EC extension.
	 * 
	 * The method deletes the jarsigner signature file and the signature block files.
	 */
	private static void unsign(File extractedJarDirectory){
		File metaInfDirectory = new File(extractedJarDirectory.getAbsolutePath() + File.separatorChar + META_INF);
		File[] files = metaInfDirectory.listFiles();
		if(files != null){
			for(File file : files){
				if(file.getName().endsWith(".SF") 
					|| file.getName().endsWith(".DSA") 
					|| file.getName().endsWith(".RSA") 
					|| file.getName().endsWith(".EC")){
					file.delete();
				}
			}
		}
	}
	
	private static void rebuildManifest(File extractedJarDirectory){
		// META-INF/MANIFEST.MF
	}
	
	private static void signJar(File jarFile){
		
	}
	
	private static void generateWrapper(String targetClass, String wrapperName, ArrayList<String> payloads, File outputFile) throws Exception {
		FileWriter fw = new FileWriter(outputFile);
		
		fw.write("import java.applet.Applet;\n");
		fw.write("import java.awt.Graphics;\n");
		fw.write("import repackager.Payload;\n");
		fw.write("\n");
		fw.write("public class " + wrapperName + " extends Applet {\n");
		fw.write("\n");
		fw.write("	private static final long serialVersionUID = 1L;\n");
		fw.write("\n");
		fw.write("	Applet applet = null;\n");
		fw.write("\n");
		for(String payload : payloads){
			fw.write("	" + "Payload " + classToVariableName(payload) + " = null;\n");
		}
		fw.write("\n");
		fw.write("	@Override\n");
		fw.write("	public void init(){\n");
		fw.write("		try {\n");
		fw.write("			Class<?> c = Class.forName(\"" + targetClass + "\");\n");
		fw.write("				this.applet = (Applet) c.newInstance();\n");
		fw.write("		} catch (Exception e){}\n");
		for(String payload : payloads){
			fw.write("		try {\n");
			fw.write("			Class<?> c = Class.forName(\"" + payload + "\");\n");
			fw.write("				" + classToVariableName(payload) + " = (Payload) c.newInstance();\n");
			fw.write("		} catch (Exception e){}\n");
		}
		for(String payload : payloads){
			fw.write("		" + classToVariableName(payload) + ".preInitPayload();\n");
		}
		fw.write("		this.applet.init();\n");
		for(String payload : payloads){
			fw.write("		" + classToVariableName(payload) + ".postInitPayload();\n");
		}
		fw.write("	}\n");
		fw.write("\n");
		fw.write("	@Override\n");
		fw.write("	public void start(){\n");
		for(String payload : payloads){
			fw.write("		" + classToVariableName(payload) + ".preStartPayload();\n");
		}
		fw.write("		this.applet.start();\n");
		for(String payload : payloads){
			fw.write("		" + classToVariableName(payload) + ".postStartPayload();\n");
		}
		fw.write("	}\n");
		fw.write("\n");
		fw.write("	@Override\n");
		fw.write("	public void paint(Graphics g){\n");
		for(String payload : payloads){
			fw.write("		" + classToVariableName(payload) + ".prePaintPayload(g);\n");
		}
		fw.write("		this.applet.paint(g);\n");
		for(String payload : payloads){
			fw.write("		" + classToVariableName(payload) + ".postPaintPayload(g);\n");
		}
		fw.write("	}\n");
		fw.write("\n");
		fw.write("	@Override\n");
		fw.write("	public void stop(){\n");
		for(String payload : payloads){
			fw.write("		" + classToVariableName(payload) + ".preStopPayload();\n");
		}
		fw.write("		this.applet.stop();\n");
		for(String payload : payloads){
			fw.write("		" + classToVariableName(payload) + ".postStopPayload();\n");
		}
		fw.write("	}\n");
		fw.write("\n");
		fw.write("	@Override\n");
		fw.write("	public void destroy(){\n");
		for(String payload : payloads){
			fw.write("		" + classToVariableName(payload) + ".preDestroyPayload();\n");
		}
		fw.write("		this.applet.destroy();\n");
		for(String payload : payloads){
			fw.write("		" + classToVariableName(payload) + ".postDestroyPayload();\n");
		}
		fw.write("	}\n");
		fw.write("\n");
		fw.write("}\n");
		
		fw.close();
	}
	
	private static String classToVariableName(String className){
		return Character.toLowerCase(className.charAt(0)) + className.substring(1);
	}
	
	private static File compileClass(File sourceFile){
		return null;
	}
	
	private static void addClassFileToJar(File classFile, String path){
		
	}

}
