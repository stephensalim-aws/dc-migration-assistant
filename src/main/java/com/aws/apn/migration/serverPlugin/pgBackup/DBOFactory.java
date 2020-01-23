/*	Copyright (c) 2012	Tomislav Gountchev <tomi@gountchev.net>	*/

package com.aws.apn.migration.serverPlugin.pgBackup;

import java.sql.Connection;
import java.sql.SQLException;

interface DBOFactory<T extends DbBackupObject> {

	Iterable<T> getDbBackupObjects(Connection con, Schema schema) throws SQLException;

	T getDbBackupObject(Connection con, String name, Schema schema) throws SQLException;

}
