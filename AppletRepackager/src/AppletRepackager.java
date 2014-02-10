import java.io.File;

public class AppletRepackager {

	public static void main(String[] args) throws Exception {
		File appletJarFile = new File("test.jar");
		File extractedJarDirectory = ZipUtils.extractJar(appletJarFile);
		File outputAppletJarFile = new File(appletJarFile.getAbsolutePath().replace(".jar", "") + "_repacked.jar");
		ZipUtils.compressJar(extractedJarDirectory, outputAppletJarFile);
		System.out.println("Finished.");
	}
	
	private static void unsign(File extractedJarDirectory){
		// delete .DSA and .SF keys
		// META-INF/MYKEY.DSA
		// META-INF/MYKEY.SF
	}
	
	private static void rebuildManifest(File extractedJarDirectory){
		// META-INF/MANIFEST.MF
	}
	
	private static void signJar(File jarFile){
		
	}

}
