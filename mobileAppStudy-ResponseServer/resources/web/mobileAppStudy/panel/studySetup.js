/*
 * Copyright (c) 2016-2019 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define('LABKEY.MobileAppStudy.StudySetupPanel', {

    extend: 'Ext.form.Panel',

    border: false,

    isEditable: true,

    shortName: null,

    canChangeCollection: null,

    collectionEnabled: null,

    forwardingEnabled: null,

    forwardingUrl: null,

    forwardingUsername: null,

    forwardingPassword: null,

    trackResetOnLoad: true,

    initComponent: function()
    {
        if (this.isEditable) {
            this.dockedItems = [{
                xtype: 'toolbar',
                dock: 'bottom',
                ui: 'footer',
                items: [this.getSubmitButton(), this.getSuccessMessage()]
            }];

            this.callParent();
            this.add({
                tag: 'div',
                padding: '10px 10px 0px 10px',
                itemId: 'messageEl',
                cls: 'studysetup-prompt',
                //StudyId typically refers to the Study.rowId, however in this context it is the Study.shortName   Issue #28419
                html: 'Enter the StudyId to be associated with this folder.  The StudyId should be the same as it appears in the study design interface.',
                border: false
            });
        }
        else {
            if (this.canChangeCollection) {
                this.dockedItems = [{
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: [this.getSubmitButton(), this.getSuccessMessage()]
                }];
            }

            this.callParent();
            this.add({
                tag: 'div',
                padding: '10px 10px 0px 10px',
                itemId: 'messageEl',
                cls: 'studysetup-prompt',
                //StudyId typically refers to the Study.rowId, however in this context it is the Study.shortName   Issue #28419
                html: 'The StudyId associated with this folder is ' + this.shortName + '.',
                border: false
            });
        }

        this.add(this.getFormFields());
        this.add(this.getEnableCollectionControl());
    },

    getSubmitButton: function() {
        if (!this.submitButton) {
            this.submitButton = Ext4.create('Ext.button.Button', {
                text: 'Submit',
                itemId: 'submitBtn',
                disabled: true,
                handler: function (btn) {
                    btn.up('form').enableCollectionWarning(btn);
                }
            })
        }
        return this.submitButton;
    },

    getStudyIdField: function() {
        if (!this.studyIdField) {
            this.studyIdField = Ext4.create("Ext.form.field.Text", {
                width: 200,
                name: 'studyId',
                value: this.shortName,
                padding: '10px 10px 0px 10px',
                allowBlank: false,
                //StudyId typically refers to the Study.rowId, however in this context it is the Study.shortName   Issue #28419
                emptyText: "Enter StudyId",
                submitEmptyText: false,
                hidden: !this.isEditable,
                readOnly: !this.isEditable,
                validateOnChange: true,
                allowOnlyWhitespace: false,
                listeners: {
                    change: this.validateForm,
                    scope: this
                }
            });
        }
        return this.studyIdField;
    },

    getFormFields: function() {
        return [this.getStudyIdField()];
    },

    getSuccessMessage: function(){
        if (!this.successMessage) {
            this.successMessage = Ext4.create("Ext.form.Label", {
                text: 'Configuration Saved',
                cls: 'labkey-message',
                hidden: true
            });
        }
        return this.successMessage
    },

    enableCollectionWarning: function(btn) {
        btn.setDisabled(true);
        var collectionCheckbox = this.getEnableCollectionControl();
        if (!collectionCheckbox.checked)
            Ext4.Msg.show({
                title: 'Response collection stopped',
                msg: 'Response collection is disabled for this study. No data will be collected until it is enabled.',
                buttons: Ext4.Msg.OKCANCEL,
                icon: Ext4.Msg.WARNING,
                fn: function(val) {
                    if (val == 'ok'){
                        btn.up('form').doSubmit(btn);
                    }
                    else btn.setDisabled(false);
                },
                scope: this
            });
        else //No confirmation needed to enable collection
            btn.up('form').doSubmit(btn);
    },

    doSubmit: function(btn) {

        function onSuccess(response, options) {
            var obj = Ext4.decode(response.responseText);
            if (obj.success) {
                //reload form control values for Dirty tracking
                btn.up('form').getForm().setValues(obj.data);

                //Set panel values
                this.shortName = obj.data.studyId;
                this.collectionEnabled = obj.data.collectionEnabled;
                this.getSuccessMessage().show();
                new Ext4.util.DelayedTask(hideSuccess, this).delay(5000);
                this.validateForm(btn);
            }
            else
            {
                Ext4.Msg.alert("Error", "There was a problem.  Please check the logs or contact an administrator.");
            }
        }

        function onError(response, options){
            btn.setDisabled(false);

            var obj = Ext4.decode(response.responseText);
            if (obj.errors)
            {
                //StudyId typically refers to the Study.rowId, however in this context it is the Study.shortName
                Ext4.Msg.alert("Error", "There were problems storing the configuration. " + obj.errors[0].message);
            }
        }

        function hideSuccess()
        {
            this.getSuccessMessage().hide();
        }

        Ext4.Ajax.request({
            url: LABKEY.ActionURL.buildURL('mobileappstudy', 'studyConfig.api'),
            method: 'POST',
            jsonData: this.getForm().getFieldValues(),
            success: onSuccess,
            failure: onError,
            scope: this
        });

    },
    getEnableCollectionControl: function() {
        if (!this.collectionCheckbox) {
            this.collectionCheckbox = Ext4.create("Ext.form.field.Checkbox",{
                name: 'collectionEnabled',
                boxLabel: 'Enable Response Collection',
                padding:'0 0 0 10',
                id: 'collectionEnabled',
                checked: this.collectionEnabled,
                width: 200,
                value: this.collectionEnabled,
                disabled: !this.canChangeCollection,
                listeners: {
                    change: this.validateForm,
                    scope: this
                }
            });
        }
        return this.collectionCheckbox;
    },
    validateForm: function(field){
        var form = field.up('form');
        var saveBtn = form.getSubmitButton();
        if (saveBtn.hidden)
            saveBtn.show();

        saveBtn.setDisabled(!(form.isDirty() && form.isValid()));
    }
});