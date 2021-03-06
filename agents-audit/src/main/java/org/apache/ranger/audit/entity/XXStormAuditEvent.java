/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ranger.audit.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.ranger.audit.model.StormAuditEvent;

/**
 * Entity implementation class for Entity: XXHiveAuditEvent
 *
 */
@Entity
@DiscriminatorValue("5")
public class XXStormAuditEvent extends XXBaseAuditEvent implements Serializable {
	private static final long serialVersionUID = 1L;

	private String resourcePath;
	private String resourceType;
	private String requestData;


	public XXStormAuditEvent() {
		super();
	}   

	public XXStormAuditEvent(StormAuditEvent event) {
		super(event);

		this.resourcePath = event.getResourcePath();
		this.resourceType = event.getResourceType();
		this.requestData  = event.getRequestData();
	}   

	@Column(name = "resource_path")
	public String getResourcePath() {
		return this.resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}   

	@Column(name = "resource_type")
	public String getResourceType() {
		return this.resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}   

	@Column(name = "request_data")
	public String getRequestData() {
		return this.requestData;
	}

	public void setRequestData(String requestData) {
		this.requestData = requestData;
	}
   
}
