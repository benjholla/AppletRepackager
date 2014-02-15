import java.io.File;


public class Test {

	public static void main(String[] args) throws Exception {
		File originalJar = new File("C:\\Users\\Ben\\Desktop\\test.jar");
		File extractedJarDirectory = new File("C:\\Users\\Ben\\Desktop\\test");
		File repackedJar = new File("C:\\Users\\Ben\\Desktop\\test2.jar");
		JarUtils.unjar(originalJar, extractedJarDirectory);
		JarUtils.purgeMetaInf(extractedJarDirectory);
		JarUtils.jar(extractedJarDirectory, repackedJar, JarUtils.generateManifest());
	}

}
