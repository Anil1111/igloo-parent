package org.iglooproject.jpa.more.config.util;

import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.migration.Context;

public interface IIglooMigration {

	public void migrate(Context context) throws Exception;

	public Integer getChecksum();

	public MigrationVersion getVersion();

	public String getDescription();
}
