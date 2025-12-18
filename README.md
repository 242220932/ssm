# IStore 备份系统 (IStore Backup System)

## 概述 (Overview)

istore备份系统以备刷坏后刷回 - 这是一个用于备份和恢复istore数据的系统，可以防止系统刷坏后的数据丢失。

This is a backup and restore system for istore data that prevents data loss in case of system failure or corruption.

## 功能特性 (Features)

- ✅ **自动备份** (Automatic Backup): 创建包含所有数据文件的ZIP压缩备份
- ✅ **快速恢复** (Quick Restore): 从备份文件快速恢复数据
- ✅ **备份管理** (Backup Management): 列出所有可用备份
- ✅ **自动清理** (Auto Cleanup): 自动清理旧备份，保留最新的N个备份
- ✅ **时间戳** (Timestamp): 备份文件带有时间戳，便于识别

## 使用方法 (Usage)

### 1. 编译代码 (Compile)

```bash
cd ssm
javac IStoreBackup.java Test.java
```

### 2. 运行测试 (Run Test)

```bash
java Test
```

### 3. 创建备份 (Create Backup)

```java
IStoreBackup backup = new IStoreBackup();
String backupFile = backup.createBackup();
System.out.println("备份已创建: " + backupFile);
```

### 4. 列出所有备份 (List All Backups)

```java
IStoreBackup backup = new IStoreBackup();
String[] backups = backup.listBackups();
for (String b : backups) {
    System.out.println(b);
}
```

### 5. 恢复备份 (Restore Backup)

```java
IStoreBackup backup = new IStoreBackup();
boolean success = backup.restoreBackup("backups/istore_backup_2025-12-18_10-30-00.zip");
if (success) {
    System.out.println("恢复成功！");
}
```

### 6. 清理旧备份 (Cleanup Old Backups)

```java
IStoreBackup backup = new IStoreBackup();
backup.cleanupOldBackups(5); // 保留最新的5个备份
```

## 备份文件格式 (Backup File Format)

备份文件以ZIP格式存储，文件名格式：
```
backups/istore_backup_YYYY-MM-DD_HH-mm-ss.zip
```

例如：`backups/istore_backup_2025-12-18_10-30-00.zip`

## 支持的文件类型 (Supported File Types)

系统会自动备份以下类型的文件：
- `.java` - Java源代码文件
- `.log` - 日志文件
- `.dat` - 数据文件
- `.db` - 数据库文件
- `.txt` - 文本文件
- `.properties` - 配置文件

## 目录结构 (Directory Structure)

```
ssm/
├── IStoreBackup.java   # 备份系统核心类
├── Test.java           # 测试和示例代码
├── data.log            # 示例数据文件
└── backups/            # 备份文件存储目录（自动创建）
    ├── istore_backup_2025-12-18_10-30-00.zip
    ├── istore_backup_2025-12-18_11-00-00.zip
    └── ...
```

## 最佳实践 (Best Practices)

1. **定期备份**: 建议在进行重要操作前创建备份
2. **保留多个备份**: 使用 `cleanupOldBackups()` 保留多个历史备份
3. **验证恢复**: 定期测试备份恢复功能确保备份有效
4. **安全存储**: 将重要备份复制到外部存储设备

## 安全说明 (Security Notes)

- 备份文件包含所有敏感数据，请妥善保管
- 定期检查备份文件的完整性
- 建议对备份文件进行额外加密保护

## 故障排除 (Troubleshooting)

### 备份创建失败
- 检查是否有足够的磁盘空间
- 确认有写入权限

### 恢复失败
- 确认备份文件存在且未损坏
- 检查文件权限

## 许可证 (License)

This project is open source and available for use and modification.
