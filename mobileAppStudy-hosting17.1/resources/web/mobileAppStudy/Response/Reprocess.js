/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
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