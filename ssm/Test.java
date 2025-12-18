/**
 * Test class for IStore Backup System
 * Demonstrates backup and restore functionality
 */
public class Test {
    
    public static void main(String[] args) {
        System.out.println("=== IStore Backup System Test ===\n");
        
        IStoreBackup backup = new IStoreBackup();
        
        // Test 1: Create a backup
        System.out.println("Test 1: Creating backup...");
        String backupFile = backup.createBackup();
        if (backupFile != null) {
            System.out.println("✓ Backup created successfully: " + backupFile);
        } else {
            System.out.println("✗ Failed to create backup");
        }
        System.out.println();
        
        // Test 2: List all backups
        System.out.println("Test 2: Listing all backups...");
        String[] backups = backup.listBackups();
        if (backups.length > 0) {
            System.out.println("✓ Found " + backups.length + " backup(s):");
            for (int i = 0; i < backups.length; i++) {
                System.out.println("  " + (i + 1) + ". " + backups[i]);
            }
        } else {
            System.out.println("No backups found.");
        }
        System.out.println();
        
        // Test 3: Test restore functionality (if backup exists)
        if (backupFile != null) {
            System.out.println("Test 3: Testing restore functionality...");
            boolean restored = backup.restoreBackup(backupFile);
            if (restored) {
                System.out.println("✓ Backup restored successfully");
            } else {
                System.out.println("✗ Failed to restore backup");
            }
            System.out.println();
        }
        
        // Test 4: Cleanup old backups (keep only 5 most recent)
        System.out.println("Test 4: Cleaning up old backups (keeping 5 most recent)...");
        backup.cleanupOldBackups(5);
        System.out.println();
        
        System.out.println("=== Test completed ===");
        System.out.println("\nUsage Instructions:");
        System.out.println("1. To create a backup: IStoreBackup backup = new IStoreBackup(); backup.createBackup();");
        System.out.println("2. To list backups: String[] backups = backup.listBackups();");
        System.out.println("3. To restore: backup.restoreBackup(\"backups/istore_backup_YYYY-MM-DD_HH-mm-ss.zip\");");
        System.out.println("4. To cleanup old backups: backup.cleanupOldBackups(5);");
    }
}
