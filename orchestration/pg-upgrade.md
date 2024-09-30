# Postgres Upgrade from 16.3 to 17.0
This document describes the steps to upgrade the Postgres database from version 16.3 to 17.0.

**1. Stop application services**  
Stop the application services that are connected to the database to make sure all connections to the database are closed.

**2. Create Backup**  
Execute the following command within the database container to create a backup of the database:
```bash
pg_dump -U ${username} ${database-name} >> upgrade-backup.sql
```
Check the created backup file `upgrade-backup.sql` to ensure that the backup is created successfully.

**3. Delete the existing database**  
Based on the platform the database is running on, delete the container with the existing data.

**4. Pull the latest Postgres image**  
Upgrade the version of the Postgres Image to 17.0 and start the database container.

**5. Restore the backup**  
Connect to the database container and restore the backup created in step 1 by running the all SQL commands int the file.
Run the following command in the new container to restore the backup:
```bash
cat upgrade-backup.sql | psql -U ${username} ${database-name}
```

**6. Verification**  
Restart the database container and check if the data is present again. After that restart the application services again.