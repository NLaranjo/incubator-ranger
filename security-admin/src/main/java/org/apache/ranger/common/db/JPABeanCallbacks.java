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

 package org.apache.ranger.common.db;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.apache.log4j.Logger;
import org.apache.ranger.common.DateUtil;
import org.apache.ranger.common.UserSessionBase;
import org.apache.ranger.entity.XXDBBase;
import org.apache.ranger.security.context.XAContextHolder;
import org.apache.ranger.security.context.XASecurityContext;

public class JPABeanCallbacks {
	static final Logger logger = Logger.getLogger(JPABeanCallbacks.class);

	@PrePersist
	void onPrePersist(Object o) {
		try {
			if (o != null && o instanceof XXDBBase) {
				XXDBBase entity = (XXDBBase) o;

				entity.setUpdateTime(DateUtil.getUTCDate());

				XASecurityContext context = XAContextHolder
						.getSecurityContext();
				if (context != null) {
					UserSessionBase userSession = context.getUserSession();
					if (userSession != null) {
						entity.setAddedByUserId(userSession.getUserId());
						entity.setUpdatedByUserId(userSession
								.getUserId());
					}
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug(
								"Security context not found for this request. obj="
										+ o, new Throwable());
					}
				}
			}
		} catch (Throwable t) {
			logger.error(t);
		}

	}

	// @PostPersist
	// void onPostPersist(Object o) {
	// if (o != null && o instanceof MBase) {
	// MBase entity = (MBase) o;
	// if (logger.isDebugEnabled()) {
	// logger.debug("DBChange.create:class=" + o.getClass().getName()
	// + entity.getId());
	// }
	//
	// }
	// }

	// @PostLoad void onPostLoad(Object o) {}

	@PreUpdate
	void onPreUpdate(Object o) {
		try {
			if (o != null && o instanceof XXDBBase) {
				XXDBBase entity = (XXDBBase) o;
				entity.setUpdateTime(DateUtil.getUTCDate());
			}
		} catch (Throwable t) {
			logger.error(t);
		}

	}

	// @PostUpdate
	// void onPostUpdate(Object o) {
	// }

	// @PreRemove void onPreRemove(Object o) {}

	// @PostRemove
	// void onPostRemove(Object o) {
	// }

}