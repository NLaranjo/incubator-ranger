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

 package org.apache.ranger.service;

/**
 * 
 */

import java.util.ArrayList;
import java.util.List;

import org.apache.ranger.common.*;
import org.apache.ranger.entity.*;
import org.apache.ranger.service.*;
import org.apache.ranger.view.*;

public abstract class XCredentialStoreServiceBase<T extends XXCredentialStore, V extends VXCredentialStore>
		extends AbstractBaseResourceService<T, V> {
	public static final String NAME = "XCredentialStore";

	public XCredentialStoreServiceBase() {

	}

	@SuppressWarnings("unchecked")
	@Override
	protected XXCredentialStore mapViewToEntityBean(VXCredentialStore vObj, XXCredentialStore mObj, int OPERATION_CONTEXT) {
		mObj.setName( vObj.getName());
		mObj.setDescription( vObj.getDescription());
		return mObj;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected VXCredentialStore mapEntityToViewBean(VXCredentialStore vObj, XXCredentialStore mObj) {
		vObj.setName( mObj.getName());
		vObj.setDescription( mObj.getDescription());
		return vObj;
	}

	/**
	 * @param searchCriteria
	 * @return
	 */
	public VXCredentialStoreList searchXCredentialStores(SearchCriteria searchCriteria) {
		VXCredentialStoreList returnList = new VXCredentialStoreList();
		List<VXCredentialStore> xCredentialStoreList = new ArrayList<VXCredentialStore>();

		@SuppressWarnings("unchecked")
		List<XXCredentialStore> resultList = (List<XXCredentialStore>)searchResources(searchCriteria,
				searchFields, sortFields, returnList);

		// Iterate over the result list and create the return list
		for (XXCredentialStore gjXCredentialStore : resultList) {
			@SuppressWarnings("unchecked")
			VXCredentialStore vXCredentialStore = populateViewBean((T)gjXCredentialStore);
			xCredentialStoreList.add(vXCredentialStore);
		}

		returnList.setVXCredentialStores(xCredentialStoreList);
		return returnList;
	}

}
