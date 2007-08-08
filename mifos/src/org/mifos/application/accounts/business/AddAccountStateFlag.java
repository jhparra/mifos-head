package org.mifos.application.accounts.business;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.framework.persistence.Upgrade;

public class AddAccountStateFlag extends Upgrade {

	/**
	 * TODO: What is this?  There must be a constant for it somewhere.
	 * Existing flags have a status of either 10 (loan) or 15 (savings).
	 */
	public static final int STATUS_10 = 10;

	private final int newFlagId;
	private final String description;

	private final Short locale;
	private final String message;

	public AddAccountStateFlag(int higherVersion, 
		int newFlagId, String description, Short locale, String message) {
		super(higherVersion);
		this.newFlagId = newFlagId;
		this.description = description;
		this.locale = locale;
		this.message = message;
	}

	@Override
	public void upgrade(Connection connection) throws IOException, SQLException {
		int lookupEntity = MifosLookUpEntity.ACCOUNT_STATE_FLAG;

		int lookupId = insertLookupValue(connection, lookupEntity);
		insertMessage(connection, lookupId, locale, message);
		addFlag(connection, newFlagId, description, lookupId);
		upgradeVersion(connection);
	}

	@Override
	public void downgrade(Connection connection) throws IOException,
			SQLException {
		short lookupId = findLookupId(connection);

		deleteFromFlags(connection);
		deleteFromLookupValueLocale(connection, lookupId);
		deleteFromLookupValue(connection, lookupId);

		downgradeVersion(connection);
	}

	private void addFlag(Connection connection, 
		int newFlagId, String description, int lookupId) 
	throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"INSERT INTO ACCOUNT_STATE_FLAG(" +
			"  FLAG_ID,LOOKUP_ID,STATUS_ID,FLAG_DESCRIPTION,RETAIN_FLAG)" +
			"VALUES(?,?,?,?,?)");
		statement.setInt(1, newFlagId);
		statement.setInt(2, lookupId);
		statement.setInt(3, STATUS_10);
		statement.setString(4, description);
		boolean retain = false;
		statement.setInt(5, retain ? 1 : 0);
		statement.executeUpdate();
		statement.close();
	}

	private short findLookupId(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"select LOOKUP_ID " +
			"from ACCOUNT_STATE_FLAG where FLAG_ID = ?");
		statement.setInt(1, newFlagId);
		ResultSet results = statement.executeQuery();
		if (results.next()) {
			short lookupId = results.getShort("LOOKUP_ID");
			statement.close();
			return lookupId;
		}
		else {
			statement.close();
			throw new RuntimeException(
				"unable to downgrade: no account state flag " + newFlagId);
		}
	}
	
	private void deleteFromFlags(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"delete from ACCOUNT_STATE_FLAG where FLAG_ID = ?");
		statement.setInt(1, newFlagId);
		statement.executeUpdate();
		statement.close();
	}

}
