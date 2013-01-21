package com.k_int.gokb.refine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPOutputStream;

import org.apache.tools.tar.TarOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.refine.ProjectManager;
import com.google.refine.io.ProjectUtilities;
import com.google.refine.model.Cell;
import com.google.refine.model.Project;
import com.google.refine.model.Row;

public class RefineUtil extends ProjectUtilities {
    
    final static Logger logger = LoggerFactory.getLogger("GOKb-refine_util");
    
    private static final String DIGEST_TYPE = "MD5";
    
    public static File projectToDataZip (Project project) throws IOException {
        long id = project.id;

        // Create a temporary file in the temporary dir.
        File tempFile = File.createTempFile(
          "gokb.data." + id + ".temp", "zip", GOKbModuleImpl.getTemporaryDirectory()
        );
        
        // Try and save the project data to our temporary file.
        try {
            saveToFile(project, tempFile);
            
            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();

            logger.warn("Failed to save project {}", id);
            return null;
        }
    }
    
    public static byte[] hashFile (File f) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(DIGEST_TYPE);
        
        FileInputStream is = null;                                
        byte[] buffer = new byte[8192];
        try {
            is = new FileInputStream(f);
            int read = 0;
            while( (read = is.read(buffer)) > 0) {
                md.update(buffer, 0, read);
            }    
        } finally {
            if (is != null) is.close();
        }
        return md.digest();
    }
    
    public static String byteArrayToHex(byte[] data) {
        String result = "";

        for (int i=0; i < data.length; i++) {
            result += Integer.toString( ( data[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }
    
    public static byte[] hashProjectData(Project project, File directory) throws IOException, NoSuchAlgorithmException {

        // Create the digest.
        MessageDigest md = MessageDigest.getInstance(DIGEST_TYPE);
        
        DigestOutputStream out = new DigestOutputStream(
          new FileOutputStream(new File(directory, project.id + ".txt")),
          md
        );
        
        // Add each Cell to the Digest.
        for ( Row row : project.rows ) {
            for (Cell cell : row.cells) {
                // Completely ignore "null" cells as these are left behind as a result of 
                // undoing the addition of an extra cell. Ignoring these ensures that the
                // data in refine is compared as best as can be.
                if (cell != null) {
                    Serializable val = null;
                    if (cell.value != null) val = cell.value.toString();
                    val = (val == null ? "|" : val + "|");
                    
                    // Write the file and simultaneously update the digest.
                    out.write(val.toString().getBytes());
                }
            }
            out.write("\n".getBytes());
        }
        
        out.close();
        
        // Digest the data.
        return md.digest();
    }
    
    public static void gzipTarProjectToOutputStream(Project project, OutputStream os) throws IOException {
    	gzipTarProjectToOutputStream(project, os);
    }
    
    public static void gzipTarProjectToOutputStream(Project project, OutputStream os, MessageDigest digest) throws IOException {
        GZIPOutputStream gos = new GZIPOutputStream(os);
        try {
        	tarProjectToOutputStream(project, gos, digest);
        } finally {
            gos.close();
        }
    }

    public static void tarProjectToOutputStream(Project project, OutputStream os) throws IOException {
    	tarProjectToOutputStream(project, os);
    }
    
    public static void tarProjectToOutputStream(Project project, OutputStream os, MessageDigest digest) throws IOException {
    	TarOutputStream tos;
    	if (digest != null) {
    		tos = new TarOutputStream(new DigestOutputStream(os, digest));
    	} else {
    		tos = new TarOutputStream(os);
    	}
        
        try {
            ProjectManager.singleton.exportProject(project.id, tos);
        } finally {
            tos.close();
        }
    }
}