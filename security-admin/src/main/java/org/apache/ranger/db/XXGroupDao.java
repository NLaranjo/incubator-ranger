/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

 package org.apache.ranger.db;


import java.util.ArrayList;
import java.util.List;




import org.apache.ranger.common.*;
import org.apache.ranger.common.db.*;
import org.apache.ranger.entity.*;

public class XXGroupDao extends BaseDao<XXGroup> {

	public XXGroupDao(XADaoManagerBase daoManager) {
		super(daoManager);
	}

	@SuppressWarnings("unchecked")
	public List<XXGroup> findByUserId(Long userId) {
		if (userId == null) {
			return new ArrayList<XXGroup>();
		}

		List<XXGroup> groupList = (List<XXGroup>) getEntityManager()
				.createNamedQuery("XXGroup.findByUserId")
				.setParameter("userId", userId).getResultList();

		if (groupList == null) {
			groupList = new ArrayList<XXGroup>();
		}

		return groupList;
	}

	@SuppressWarnings("unchecked")
	public XXGroup findByGroupName(String groupName) {
		if (groupName == null) {
			return null;
		}
		try {

			return (XXGroup) getEntityManager()
					.createNamedQuery("XXGroup.findByGroupName")
					.setParameter("name", groupName)
					.getSingleResult();
		} catch (Exception e) {

		}
		return null;
	}

}
