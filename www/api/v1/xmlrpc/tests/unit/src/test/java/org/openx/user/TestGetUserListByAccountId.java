/*
+---------------------------------------------------------------------------+
| OpenX v${RELEASE_MAJOR_MINOR}                                                                |
| ======${RELEASE_MAJOR_MINOR_DOUBLE_UNDERLINE}                                                                 |
|                                                                           |
| Copyright (c) 2003-2009 OpenX Limited                                     |
| For contact details, see: http://www.openx.org/                           |
|                                                                           |
| This program is free software; you can redistribute it and/or modify      |
| it under the terms of the GNU General Public License as published by      |
| the Free Software Foundation; either version 2 of the License, or         |
| (at your option) any later version.                                       |
|                                                                           |
| This program is distributed in the hope that it will be useful,           |
| but WITHOUT ANY WARRANTY; without even the implied warranty of            |
| MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the             |
| GNU General Public License for more details.                              |
|                                                                           |
| You should have received a copy of the GNU General Public License         |
| along with this program; if not, write to the Free Software               |
| Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA |
+---------------------------------------------------------------------------+
$Id: TestGetUserListByAccountId.java 16124 2008-02-11 18:16:06Z andrew.hill@openads.org $
*/

package org.openx.user;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;
import org.openx.utils.ErrorMessage;
import org.openx.utils.TextUtils;

/**
 * Verify Get User method
 *
 * @author     Pawel Dachterski <pawel.dachterski@openx.org>
 */
public class TestGetUserListByAccountId extends UserTestCase {

	/**
	 * Execute test method with error
	 *
	 * @param params -
	 *            parameters for test method
	 * @param errorMsg -
	 *            true error messages
	 * @throws MalformedURLException
	 */
	private void executeGetUserListByAccountIdWithError(Object[] params, String errorMsg)
			throws MalformedURLException {

		try {
			execute(GET_USER_LIST_BY_ACCOUNT_ID_METHOD, params);
			fail(ErrorMessage.METHOD_EXECUTED_SUCCESSFULLY_BUT_SHOULD_NOT_HAVE);
		} catch (XmlRpcException e) {
			assertEquals(ErrorMessage.WRONG_ERROR_MESSAGE, errorMsg, e
					.getMessage());
		}
	}

	/**
	 * Test method with all fields.
	 *
	 * @throws MalformedURLException
	 */
	@SuppressWarnings("unchecked")
	public void testGetUserListByAccountIdAllFields() throws XmlRpcException,
			MalformedURLException {

		final int usersCount = 3;
		
		Map<Integer, Map<String, Object>> patternUsersAttributes = new HashMap<Integer, Map<String, Object>>();
		for (int i = 0; i < usersCount; i++) {
			Map<String, Object> patternUserAttributes = getUserParams(TEST_DATA_PREFIX + i);
			patternUsersAttributes.put(createUser(patternUserAttributes), patternUserAttributes);
		}

		try {
			final Object[] users = (Object[]) execute(GET_USER_LIST_BY_ACCOUNT_ID_METHOD,
					new Object[] { sessionId, 1 });

			assertEquals("Not correct count created users", usersCount, patternUsersAttributes.size());
			//TODO: "users.length-2" because 1 default user from test setup and 1 admin user, refactor to use unique accountID
			assertEquals("Not correct count return users", usersCount, users.length-2);

			for (Object user : users) {
				Integer userId = (Integer) ((Map) user).get(USER_ID);
				Map<String, Object> patternUserAttributes = patternUsersAttributes.get(userId);
				if (patternUserAttributes != null) {
					checkParameter((Map) user, USER_ID, userId);
					checkParameter((Map) user, CONTACT_NAME, patternUserAttributes.get(CONTACT_NAME));
					checkParameter((Map) user, EMAIL_ADDRESS, patternUserAttributes.get(EMAIL_ADDRESS));
					checkParameter((Map) user, LOGIN, patternUserAttributes.get(LOGIN));
					checkParameter((Map) user, PASSWORD, "");
					checkParameter((Map) user, DEFAULT_ACCOUNT_ID, patternUserAttributes.get(DEFAULT_ACCOUNT_ID));
					checkParameter((Map) user, ACTIVE, patternUserAttributes.get(ACTIVE));					
					deleteUser(userId);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test method without some required fields(error).
	 *
	 * @throws MalformedURLException
	 */
	public void testGetUserListByAccountIdWithoutSomeRequiredFields()
			throws MalformedURLException {
		Object[] XMLMethodParameters = new Object[] { sessionId };

		executeGetUserListByAccountIdWithError(XMLMethodParameters, ErrorMessage.getMessage(ErrorMessage
				.getMessage(ErrorMessage.INCORRECT_PARAMETERS_PASSED_TO_METHOD,
						"2", "1")));
	}

	/**
	 * Try to get Users with unknown accountId
	 *
	 * @throws XmlRpcException
	 * @throws MalformedURLException
	 */
	public void testGetUserListByAccountIdUnknownIdError() throws XmlRpcException,
			MalformedURLException {
		
		final Integer accountId = new Integer(111111111);
		//TODO: Refactor to use unique not existing accountId
		Object[] XMLMethodParameters = new Object[] { sessionId, accountId };
		executeGetUserListByAccountIdWithError(XMLMethodParameters, ErrorMessage.getMessage(
				ErrorMessage.UNKNOWN_ID_ERROR, ACCOUNT_ID));
	}
	
	/**
	 * Test method with fields that has value of wrong type (error).
	 *
	 * @throws MalformedURLException
	 * @throws XmlRpcException
	 */
	public void testGetUserListByAccountIdWrongUserIdTypeError() throws MalformedURLException,
			XmlRpcException {

		final String accountId = TextUtils.NOT_INTEGER;
		Object[] XMLMethodParameters = new Object[] { sessionId, accountId};

		executeGetUserListByAccountIdWithError(XMLMethodParameters, ErrorMessage.getMessage(
				ErrorMessage.INCORRECT_PARAMETERS_WANTED_INT_GOT_STRING, "2"));
	}
}
