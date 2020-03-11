/*
 * Copyright (c) 2016-2019 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */


Ext4.define('LABKEY.MobileAppStudy.EnrollmentTokenBatchFormPanel', {

    extend: 'Ext.window.Window',

    title: 'Generate Tokens',

    floating: true,

    closable: true,

    gridButton : null,

    initComponent : function()
    {
        this.toggleGridButton();
        this.dockedItems = [{
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            items: [
                '->',
                {
                    text: 'Submit',
                    itemId: 'submitBtn',
                    formBind: true,
                    handler: function (btn)
                    {
                        var panel = btn.up('window');
                        panel.doSubmit(btn);
                    }
                },
                {
                    text: 'Cancel',
                    itemId: 'cancelBtn',
                    handler: function (btn)
                    {
                        btn.up('window').close();
                    }
                }
            ]
        }];

        this.callParent();
        this.add({
            tag: 'div',
            padding: '10px 10px 0px 10px',
            itemId: 'messageEl',
            html:'How many tokens would you like to generate?',
            border: false
        });

        this.add(this.createForm());
        this.on (
                {
                    afterRender: function () {
                        this.getSubmitButton().setDisabled(true);
                    },
                    close : function() {
                        this.toggleGridButton();
                    }
                }
        )


    },

    toggleGridButton: function() {
        if (this.gridButton)
        {
            if (this.gridButton.className.indexOf('labkey-disabled-button') >= 0)
                this.gridButton.className = this.gridButton.className.replace('labkey-disabled-button', '');
            else
                this.gridButton.className += ' labkey-disabled-button ';
        }
    },

    createForm: function() {
        this.form = Ext4.create('Ext.form.Panel',
                {
                    border:false
                }
        );

        this.form.add(this.getFormFields());
        return this.form;
    },

    getFormFields : function()
    {
        var items = [];
        items.push(
                {
                    xtype: 'radiogroup',
                    padding: '14px',
                    defaultType: 'radio',
                    layout: 'vbox',
                    items: [
                        {
                            boxLabel: '100',
                            name: 'count',
                            inputValue: '100',
                            id: 'radio1'
                        }, {
                            boxLabel: '1,000',
                            name: 'count',
                            inputValue: '1000',
                            id: 'radio2'
                        }, {
                            boxLabel: '10,000',
                            name: 'count',
                            inputValue: '10000',
                            id: 'radio3'
                        }, {
                            xtype: 'fieldcontainer',
                            layout: 'hbox',
                            items: [
                                {
                                    boxLabel: 'Other',
                                    xtype: 'radio',
                                    name: 'count',
                                    inputValue: 'other',
                                    id: 'radio4'
                                } ,
                                {
                                    xtype: 'numberfield',
                                    name: 'otherCount',
                                    disabled: true,
                                    minValue: 1,
                                    allowDecimals: false,
                                    allowExponential: false,
                                    padding: '0 0 0 5px',
                                    listeners: {
                                        change: function(cmp)
                                        {
                                            var window = cmp.up('window');
                                            var submitBtn = window.getSubmitButton();
                                            submitBtn.setDisabled(!cmp.getValue());
                                        }
                                    }
                                }
                            ]
                        }
                    ],
                    listeners: {
                        change: function(radioGroup, radio){
                            var isOther = radio.count == 'other';
                            var form = radioGroup.up('form');
                            var otherCount = form.getForm().findField('otherCount');
                            otherCount.setDisabled(!isOther);
                            var submitBtn = radioGroup.up('window').getSubmitButton();
                            submitBtn.setDisabled(isOther && !otherCount.getValue());
                        }
                    }
                }
        );
        return items;
    },

    getSubmitButton: function()
    {
        return this.dockedItems.items[1].items.items[1];
    },

    getCancelButton: function()
    {
        return this.dockedItems.items[1].items.items[2];
    },

    doSubmit: function(btn){
        btn.setDisabled(true);
        btn.up('window').getCancelButton().setDisabled(true);
        btn.up('window').getEl().mask("Generating tokens ...");


        function onSuccess(response, options){
            btn.up('window').close();
            var batchId = JSON.parse(response.responseText).data.batchId;
            if (batchId)
            {
                window.location = LABKEY.ActionURL.buildURL('mobileappstudy', 'tokenList.view', null, {'enrollmentTokens.BatchId/RowId~eq': batchId});
            }
        }

        function onError(response, options){
            btn.setDisabled(false);

            var obj = Ext4.decode(response.responseText);
            if (obj.errors && obj.errors[0].field == "form")
            {
                Ext4.Msg.alert("Error", "There were problems generating the tokens. " + obj.errors[0].message);
            }
        }

        Ext4.Ajax.request({
            url: LABKEY.ActionURL.buildURL('mobileappstudy', 'generateTokens.api'),
            method: 'POST',
            jsonData: this.getFieldValues(),
            success: onSuccess,
            failure: onError,
            scope: this
        });

    },

    getFieldValues: function()
    {
        var values = this.down('form').getForm().getFieldValues();
        if (values.count == 'other' && values.otherCount)
        {
            values.count = values.otherCount;
            delete values.otherCount;
        }
        return values;
    }
});