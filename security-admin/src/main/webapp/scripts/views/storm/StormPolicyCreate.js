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

 
define(function(require){
    'use strict';

    var App				= require('App');
	var Backbone		= require('backbone');
	var XALinks 		= require('modules/XALinks');
	
	var XAUtil			= require('utils/XAUtils');
	var XAEnums			= require('utils/XAEnums');
	var localization	= require('utils/XALangSupport');
	var VXResourceList	= require('collections/VXResourceList');
	
	var StormPolicyForm		 = require('views/storm/StormPolicyForm');
	var StormPolicyCreateTmpl = require('hbs!tmpl/storm/StormPolicyCreate_tmpl');

	var StormPolicyCreate = Backbone.Marionette.Layout.extend(
	/** @lends StormPolicyCreate */
	{
		_viewName : 'StormPolicyCreate',
		
    	template: StormPolicyCreateTmpl,
    	templateHelpers : function(){
    		return {
    			editPolicy : this.editPolicy
    		};
    	},
    	breadCrumbs :function(){
    		if(this.model.isNew())
    			return [XALinks.get('RepositoryManager'),XALinks.get('ManageStormPolicies',{model : this.assetModel}),XALinks.get('PolicyCreate')];
    		else
    			return [XALinks.get('RepositoryManager'),XALinks.get('ManageStormPolicies',{model : this.assetModel}),XALinks.get('PolicyEdit')];
    	} , 
		/** Layout sub regions */
    	regions: {
    		'rForm' :'div[data-id="r_form"]'
    	},

    	/** ui selector cache */
    	ui: {
    		'btnSave'	: '[data-id="save"]',
			'btnCancel' : '[data-id="cancel"]',
			'btnDelete' : '[data-id="delete"]',
			'policyDisabledAlert' : '[data-id="policyDisabledAlert"]'
    	},

		/** ui events hash */
		events: function() {
			var events = {};
			events['click ' + this.ui.btnSave]		= 'onSave';
			events['click ' + this.ui.btnCancel]	= 'onCancel';
			events['click ' + this.ui.btnDelete]	= 'onDelete';
			return events;
		},

    	/**
		* intialize a new StormPolicyCreate Layout 
		* @constructs
		*/
		initialize: function(options) {
			var that = this;
			console.log("initialized a StormPolicyCreate Layout");

			_.extend(this, _.pick(options,'assetModel'));
			this.bindEvents();

			that.form = new StormPolicyForm({
					template : require('hbs!tmpl/storm/StormPolicyForm_tmpl'),
					model : this.model,
					assetModel : this.assetModel
			});

			this.editPolicy = this.model.has('id') ? true : false;
		},

		/** all events binding here */
		bindEvents : function(){
			/*this.listenTo(this.model, "change:foo", this.modelChanged, this);*/
			/*this.listenTo(communicator.vent,'someView:someEvent', this.someEventHandler, this)'*/
		},

		/** on render callback */
		onRender: function() {
			XAUtil.showAlerForDisabledPolicy(this);
			this.rForm.show(this.form);
			this.initializePlugins();
			this.rForm.$el.dirtyFields();
			XAUtil.preventNavigation(localization.tt('dialogMsg.preventNavStormPolicyForm'), this.rForm.$el);
		},

		/** all post render plugin initialization */
		initializePlugins: function(){
		},
		popupCallBack : function(msg,validateObj){
			var that = this;
			XAUtil.alertPopup({
				msg :msg,
				callback : function(){
			//		if(validateObj.auditLoggin)
			//			that.savePolicy();
				}
			});
		},
		onSave : function(){
			var that =this, valid = false;
			var errors = this.form.commit({validate : false});
			if(! _.isEmpty(errors)){
				return;
			}
			var validateObj = this.form.formValidation();
			valid = (validateObj.groupSet && validateObj.permSet && validateObj.groupIPSet) || (validateObj.userSet && validateObj.userPerm && validateObj.userIPSet);
			if(!valid){
				if(this.validateGroupPermission(validateObj)) {
					if((!validateObj.auditLoggin) && !(validateObj.groupPermSet || validateObj.userSet)){
						XAUtil.alertPopup({
							msg :localization.tt('msg.yourAuditLogginIsOff'),
							callback : function(){}
						});
					}else{
						this.savePolicy();
					}
				}
			}else{
				if(this.validateGroupPermission(validateObj))
					this.savePolicy();
			}

		},
		validateGroupPermission : function(validateObj){
			if((!validateObj.groupSet) && (validateObj.permSet)) {
				this.popupCallBack(localization.tt('msg.addGroup'),validateObj);
			}else if(validateObj.groupIPSet && (!validateObj.groupSet)){
					this.popupCallBack(localization.tt('msg.addGroup'),validateObj);
			/*}else if(validateObj.groupSet && (!validateObj.groupIPSet)){
				this.popupCallBack(localization.tt('msg.enterIPForGroupPerm'),validateObj);
			}else if(!validateObj.groupIPSet && (validateObj.groupSet && validateObj.permSet)){
				this.popupCallBack(localization.tt('msg.enterIPForGroupPerm'),validateObj);*/
			}else if(validateObj.groupIPSet && (!validateObj.permSet)){
				this.popupCallBack(localization.tt('msg.addGroupPermission'),validateObj);
			}else if(validateObj.groupSet && (!validateObj.permSet)){
					this.popupCallBack(localization.tt('msg.addGroupPermission'),validateObj);
			/*}else if(validateObj.groupIPSet && !validateObj.groupSet){
				this.popupCallBack(localization.tt('msg.addGroup'),validateObj);
			}else if(validateObj.groupIPSet && !validateObj.permSet){
				this.popupCallBack(localization.tt('msg.addGroupPermission'),validateObj);*/
			
			}else if(validateObj.userIPSet && (!validateObj.userSet)){
				this.popupCallBack(localization.tt('msg.addUser'),validateObj);
			}else if(validateObj.userPerm && (!validateObj.userSet)) {
				this.popupCallBack(localization.tt('msg.addUser'),validateObj);
			/*}else if(!validateObj.userIPSet && validateObj.userSet){
				this.popupCallBack(localization.tt('msg.enterIPForUserPerm'),validateObj);
			}else if(!validateObj.userIPSet && (validateObj.userSet && validateObj.userPerm)){
				this.popupCallBack(localization.tt('msg.enterIPForUserPerm'),validateObj);*/		
			}else if(validateObj.userIPSet && (!validateObj.userPerm)){
				this.popupCallBack(localization.tt('msg.addUserPermission'),validateObj);
			}else if(validateObj.userSet && (!validateObj.userPerm)){
				this.popupCallBack(localization.tt('msg.addUserPermission'),validateObj);
			}
			else
				return true;
		},
		savePolicy : function(){
			var that = this;
			this.form.afterCommit();
			this.saveMethod();
		},
		saveMethod : function(){
			var that = this;
			XAUtil.blockUI();
			this.model.save({},{
				wait: true,
				success: function () {
					XAUtil.blockUI('unblock');
					var msg = that.editPolicy ? 'Policy updated successfully' :'Policy created successfully';
					XAUtil.notifySuccess('Success', msg);
					XAUtil.allowNavigation();
					if(that.editPolicy){
						App.appRouter.navigate("#!/storm/"+that.assetModel.id+"/policies",{trigger: true});
						return;
					}
					
					App.appRouter.navigate("#!/storm/"+that.assetModel.id+"/policies",{trigger: false});
					
					var view = require('views/storm/StormTableLayout');
					var resourceListForAsset = new VXResourceList([],{
						   queryParams : {
							   'assetId' : that.assetModel.id 
						   }
					   });
					
					resourceListForAsset.fetch({
						cache : false,
						data : {
							//	'resourceType':XAEnums.AssetType.ASSET_HIVE.value,
								'assetId' :that.assetModel.id
							}
					}).done(function(){
						var newColl = resourceListForAsset;
						resourceListForAsset.getLastPage({
							cache : false,
							data  : {
					//					'resourceType':XAEnums.AssetType.ASSET_HIVE.value,
										'assetId' :that.assetModel.id
									}
						}).done(function(){
							var model = newColl.get(that.model.id);
							if(model){
								model.trigger("model1:highlightBackgridRow");
							}
						});
						App.rContent.show(new view({
							collection : resourceListForAsset,
							assetModel : that.assetModel
						}));
						
					});
					
					console.log("success");
				},
				error: function (model, response, options) {
					XAUtil.blockUI('unblock');
					if ( response && response.responseJSON && response.responseJSON.msgDesc){
						if( response.responseJSON.messageList && response.responseJSON.messageList.length > 0 && !_.isUndefined(response.responseJSON.messageList[0].fieldName)){
							if(response.responseJSON.messageList[0].fieldName == "parentPermission"){
								XAUtil.confirmPopup({
									msg :response.responseJSON.msgDesc,
									callback : function(){
										that.model.set('checkParentPermission',XAEnums.BooleanValue.BOOL_FALSE.value);
										that.saveMethod();
									}
								});
							}else 
								XAUtil.notifyError('Error', response.responseJSON.msgDesc);
						}else
							XAUtil.notifyError('Error', response.responseJSON.msgDesc);
					}else
						XAUtil.notifyError('Error', 'Error creating Policy!');
					console.log("error");
				}
			});
		},
		onDelete :function(){
			var that = this;
			XAUtil.confirmPopup({
				//msg :localize.tt('msg.confirmDelete'),
				msg :'Are you sure want to delete ?',
				callback : function(){
					XAUtil.blockUI();
					that.model.destroy({
						success: function(model, response) {
							XAUtil.blockUI('unblock');
							XAUtil.allowNavigation();
							XAUtil.notifySuccess('Success', localization.tt('msg.policyDeleteMsg'));
							App.appRouter.navigate("#!/storm/"+that.assetModel.id+"/policies",{trigger: true});
						},
						error: function (model, response, options) {
							XAUtil.blockUI('unblock');
							if ( response && response.responseJSON && response.responseJSON.msgDesc){
								    XAUtil.notifyError('Error', response.responseJSON.msgDesc);
							    }else
							    	XAUtil.notifyError('Error', 'Error deleting Policy!');
							    console.log("error");
						}
					});
				}
			});
		},
		onCancel : function(){
			XAUtil.allowNavigation();
			App.appRouter.navigate("#!/storm/"+this.assetModel.id+"/policies",{trigger: true});
		},
		
		/** on close */
		onClose: function(){
			XAUtil.allowNavigation();
		}

	});

	return StormPolicyCreate; 
});
