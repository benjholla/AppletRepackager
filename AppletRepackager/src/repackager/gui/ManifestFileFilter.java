package repackager.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class ManifestFileFilter extends FileFilter {
	 
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        } 
        
        if(f.getName().equals("MANIFEST.MF")){
        	return true;
        }
        
        return false;
    }
    
    @Override
    public String getDescription() {
        return "Manifest Files";
    }
}