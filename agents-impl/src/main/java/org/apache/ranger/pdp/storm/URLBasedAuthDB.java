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

 package org.apache.ranger.pdp.storm;

import java.util.ArrayList;
import java.util.List;

import org.apache.ranger.authorization.hadoop.config.RangerConfiguration;
import org.apache.ranger.authorization.storm.RangerStormAccessVerifier;
import org.apache.ranger.pdp.config.PolicyChangeListener;
import org.apache.ranger.pdp.config.PolicyRefresher;
import org.apache.ranger.pdp.constants.RangerConstants;
import org.apache.ranger.pdp.model.Policy;
import org.apache.ranger.pdp.model.PolicyContainer;
import org.apache.ranger.pdp.model.RolePermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class URLBasedAuthDB implements PolicyChangeListener, RangerStormAccessVerifier {
	
	private static final Logger LOG = LoggerFactory.getLogger(URLBasedAuthDB.class) ;

	private static URLBasedAuthDB me = null;
	
	private PolicyRefresher refresher = null ;
	
	private PolicyContainer policyContainer = null;
	
	private List<StormAuthRule> stormAuthDB = null ; 
	
	public static URLBasedAuthDB getInstance() {
		if (me == null) {
			synchronized (URLBasedAuthDB.class) {
				URLBasedAuthDB temp = me;
				if (temp == null) {
					me = new URLBasedAuthDB();
					me.init() ;
				}
			}
		}
		return me;
	}
	
	private URLBasedAuthDB() {
		
		String url 			 = RangerConfiguration.getInstance().get(RangerConstants.RANGER_STORM_POLICYMGR_URL_PROP);
		
		long  refreshInMilli = RangerConfiguration.getInstance().getLong(
				RangerConstants.RANGER_STORM_POLICYMGR_URL_RELOAD_INTERVAL_IN_MILLIS_PROP ,
				RangerConstants.RANGER_STORM_POLICYMGR_URL_RELOAD_INTERVAL_IN_MILLIS_DEFAULT);
		
		String lastStoredFileName = RangerConfiguration.getInstance().get(RangerConstants.RANGER_STORM_LAST_SAVED_POLICY_FILE_PROP) ;
		
		String sslConfigFileName = RangerConfiguration.getInstance().get(RangerConstants.RANGER_STORM_POLICYMGR_SSL_CONFIG_FILE_PROP) ;
		
		refresher = new PolicyRefresher(url, refreshInMilli,sslConfigFileName,lastStoredFileName) ;
		
		String saveAsFileName = RangerConfiguration.getInstance().get(RangerConstants.RANGER_STORM_POLICYMGR_URL_SAVE_FILE_PROP) ;
		if (saveAsFileName != null) {
			refresher.setSaveAsFileName(saveAsFileName) ;
		}
		
		if (lastStoredFileName != null) {
			refresher.setLastStoredFileName(lastStoredFileName);
		}	
	}
	
	
	private void init() {
		refresher.setPolicyChangeListener(this);
	}
	
	
	@Override
	public void OnPolicyChange(PolicyContainer aPolicyContainer) {
		setPolicyContainer(aPolicyContainer);
	}
	
	
	public PolicyContainer getPolicyContainer() {
		return policyContainer;
	}

	
	
	public synchronized void setPolicyContainer(PolicyContainer aPolicyContainer) {
		
		if (aPolicyContainer != null) {
			
			List<StormAuthRule> tempStormAuthDB = new ArrayList<StormAuthRule>() ;
			
			for(Policy p : aPolicyContainer.getAcl()) {
				
				if (! p.isEnabled()) {
					continue;
				}
				
				for (String topologyName : p.getTopologyList()) {
					
					List<RolePermission> rpList = p.getPermissions() ;
					
					for(RolePermission rp : rpList) {
						StormAuthRule rule = new StormAuthRule(topologyName, rp.getAccess() , rp.getUsers(), rp.getGroups(), (p.getAuditInd() == 1)) ;
						tempStormAuthDB.add(rule) ;
					}
				}
			}
			
			this.stormAuthDB = tempStormAuthDB ;
			
			this.policyContainer = aPolicyContainer ;
		}
	}

	@Override
	public boolean isAccessAllowed(String aUserName, String[] aGroupName, String aOperationName, String aTopologyName) {

		boolean accessAllowed = false ;

		List<StormAuthRule> tempStormAuthDB =  this.stormAuthDB ;
		
		if (tempStormAuthDB != null) {
			for(StormAuthRule rule : tempStormAuthDB) {
				if (rule.isMatchedTopology(aTopologyName)) {
					if (rule.isOperationAllowed(aOperationName)) {
						if (rule.isUserAllowed(aUserName, aGroupName)) {
							accessAllowed = true ;
							break ;
						}
					}
				}
			}
		}
		
		return accessAllowed ;
	}

	@Override
	public boolean isAudited(String aTopologyName) {
		boolean auditEnabled = false ;

		List<StormAuthRule> tempStormAuthDB =  stormAuthDB ;
		
		if (tempStormAuthDB != null) {
			for(StormAuthRule rule : tempStormAuthDB) {
				if (rule.isMatchedTopology(aTopologyName)) {
					auditEnabled = rule.getAuditEnabled() ;
					if (auditEnabled) {
						break ;
					}
				}
			}
		}
		
		return auditEnabled ;
	}
	
}