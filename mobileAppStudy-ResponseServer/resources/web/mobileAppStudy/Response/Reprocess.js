/*
 * Copyright (c) 2016-2019 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
if (!LABKEY.MobileAppStudy) {
    LABKEY.MobileAppStudy = {};
}

LABKEY.MobileAppStudy.reprocess = function(dataRegion) {
    LABKEY.Ajax.request({
        url: LABKEY.ActionURL.buildURL('mobileappstudy', 'reprocessResponse.api'),
        method: 'POST',
        success: function (request) {
            var response = JSON.parse(request.responseText);
            if (response.success) {
                var message = 'Reprocessed ' + response.data.countReprocessed + (response.data.countReprocessed == 1 ? ' response.' : ' responses.');
                if (response.data.notReprocessed && response.data.notReprocessed.length > 0)
                    message += '<br/><br/>' +
                        (response.data.notReprocessed.length == 1 ? 'Response [ ' : 'Responses [ ') +
                        response.data.notReprocessed.join(', ') +
                        (response.data.notReprocessed.length == 1 ? ' ] was' : ' ] were') + ' not reprocessed, as ' +
                        (response.data.notReprocessed.length == 1 ? 'it was' : 'they were') + ' successfully processed previously.';

                Ext4.Msg.show({
                    title: "Reprocess Successful",
                    msg: message,
                    buttons: Ext4.MessageBox.OK,
                    icon: Ext4.MessageBox.INFO,
                    fn: function (button) {
                        if (button == 'ok')
                            window.location.reload();
                    }
                });
            } else
               LABKEY.Utils.displayAjaxErrorResponse(response);
        },
        failure: function (response, opts) {
            LABKEY.Utils.displayAjaxErrorResponse(response, opts);
        },
        jsonData: {
           key: dataRegion.selectionKey
        },
        scope: this
    });
};