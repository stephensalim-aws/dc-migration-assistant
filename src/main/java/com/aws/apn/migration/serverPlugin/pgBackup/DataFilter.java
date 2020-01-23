package com.aws.apn.migration.serverPlugin.pgBackup;


public interface DataFilter {
	DataFilter ALL_DATA = new DataFilter() {
		public boolean dumpData(String schema, String tableName) {
			return true;
		}
	};
	DataFilter NO_DATA = new DataFilter() {
		public boolean dumpData(String schema, String tableName) {
			return false;
		}
	};

	boolean dumpData(String schema, String tableName);
}
