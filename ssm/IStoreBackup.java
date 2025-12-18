import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.*;

/**
 * IStore Backup System
 * 备份系统以备刷坏后刷回 (Backup system to restore after bricking)
 * 
 * This class provides functionality to backup and restore the istore data
 * to prevent data loss in case of system failure or corruption.
 */
public class IStoreBackup {
    private static final String BACKUP_DIR = "backups";
    private static final String DATA_DIR = ".";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    
    /**
     * Create a backup of the current istore data
     * @return backup file path if successful, null otherwise
     */
    public String createBackup() {
        try {
            // Create backup directory if it doesn't exist
            File backupDir = new File(BACKUP_DIR);
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }
            
            // Generate backup filename with timestamp
            String timestamp = DATE_FORMAT.format(new Date());
            String backupFileName = BACKUP_DIR + "/istore_backup_" + timestamp + ".zip";
            
            // Create zip file for backup
            FileOutputStream fos = new FileOutputStream(backupFileName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            
            // Backup all data files
            File dataDir = new File(DATA_DIR);
            File[] files = dataDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    // Backup .java, .log, .dat, .db files, exclude backup directory
                    String name = file.getName();
                    return file.isFile() && !name.equals(backupFileName) &&
                           (name.endsWith(".java") || name.endsWith(".log") || 
                            name.endsWith(".dat") || name.endsWith(".db") ||
                            name.endsWith(".txt") || name.endsWith(".properties"));
                }
            });
            
            if (files != null) {
                for (File file : files) {
                    addToZip(file, zos);
                }
            }
            
            zos.close();
            fos.close();
            
            System.out.println("Backup created successfully: " + backupFileName);
            return backupFileName;
            
        } catch (IOException e) {
            System.err.println("Error creating backup: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Restore istore data from a backup file
     * @param backupFileName the backup file to restore from
     * @return true if restore successful, false otherwise
     */
    public boolean restoreBackup(String backupFileName) {
        try {
            File backupFile = new File(backupFileName);
            if (!backupFile.exists()) {
                System.err.println("Backup file not found: " + backupFileName);
                return false;
            }
            
            // Create temporary directory for restoration
            String tempDir = "temp_restore";
            File tempDirFile = new File(tempDir);
            if (tempDirFile.exists()) {
                deleteDirectory(tempDirFile);
            }
            tempDirFile.mkdirs();
            
            // Extract backup to temporary directory
            FileInputStream fis = new FileInputStream(backupFile);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry entry;
            
            while ((entry = zis.getNextEntry()) != null) {
                File outFile = new File(tempDir + "/" + entry.getName());
                
                if (entry.isDirectory()) {
                    outFile.mkdirs();
                } else {
                    // Create parent directories if needed
                    outFile.getParentFile().mkdirs();
                    
                    FileOutputStream fos = new FileOutputStream(outFile);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                    fos.close();
                }
                zis.closeEntry();
            }
            
            zis.close();
            fis.close();
            
            // Move restored files to data directory
            File[] restoredFiles = tempDirFile.listFiles();
            if (restoredFiles != null) {
                for (File file : restoredFiles) {
                    File destFile = new File(DATA_DIR + "/" + file.getName());
                    // Backup existing file before overwriting
                    if (destFile.exists()) {
                        destFile.delete();
                    }
                    file.renameTo(destFile);
                }
            }
            
            // Clean up temporary directory
            deleteDirectory(tempDirFile);
            
            System.out.println("Backup restored successfully from: " + backupFileName);
            return true;
            
        } catch (IOException e) {
            System.err.println("Error restoring backup: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * List all available backups
     * @return array of backup file paths
     */
    public String[] listBackups() {
        File backupDir = new File(BACKUP_DIR);
        if (!backupDir.exists()) {
            return new String[0];
        }
        
        File[] backupFiles = backupDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isFile() && file.getName().startsWith("istore_backup_") && 
                       file.getName().endsWith(".zip");
            }
        });
        
        if (backupFiles == null || backupFiles.length == 0) {
            return new String[0];
        }
        
        // Sort by modification time (newest first)
        Arrays.sort(backupFiles, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                return Long.compare(f2.lastModified(), f1.lastModified());
            }
        });
        
        String[] backupPaths = new String[backupFiles.length];
        for (int i = 0; i < backupFiles.length; i++) {
            backupPaths[i] = backupFiles[i].getPath();
        }
        
        return backupPaths;
    }
    
    /**
     * Delete old backups, keeping only the specified number of recent backups
     * @param keepCount number of backups to keep
     */
    public void cleanupOldBackups(int keepCount) {
        String[] backups = listBackups();
        
        if (backups.length <= keepCount) {
            System.out.println("No old backups to clean up.");
            return;
        }
        
        // Delete older backups
        for (int i = keepCount; i < backups.length; i++) {
            File backupFile = new File(backups[i]);
            if (backupFile.delete()) {
                System.out.println("Deleted old backup: " + backups[i]);
            }
        }
    }
    
    /**
     * Add a file to the zip archive
     */
    private void addToZip(File file, ZipOutputStream zos) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zos.putNextEntry(zipEntry);
        
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            zos.write(buffer, 0, length);
        }
        
        fis.close();
        zos.closeEntry();
    }
    
    /**
     * Recursively delete a directory
     */
    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }
}
