/*
 * Copyright (c) 2016-2017 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */

(function ($)
{
    /**
     * @private
     * @namespace API used by the Notification panel to show, mark as read, etc. the new notifications for a given user.
     */
    LABKEY.Notification = new function ()
    {
        var NOTIFICATION_COUNT_EL = null, NOTIFICATION_PANEL_EL = null;

        /**
         * Set the dom element Ids for the notification count and panel
         * @param countId - the dom element id for the "inbox" header icon/count
         * @param panelId  - the dom element id for the notification panel
         * @private
         */
        var setElementIds = function (countId, panelId)
        {
            if (countId)
            {
                NOTIFICATION_COUNT_EL = $('#' + countId);
            }

            if (panelId)
            {
                NOTIFICATION_PANEL_EL = $('#' + panelId);
            }
        };

        /**
         * Update the display of the notification "inbox" count
         * @private
         */
        var updateUnreadCount = function ()
        {
            if (NOTIFICATION_COUNT_EL && LABKEY.notifications)
            {
                var count = 0;
                for (var id in LABKEY.notifications)
                {
                    if (LABKEY.notifications.hasOwnProperty(id) && LABKEY.notifications[id].RowId
                        && LABKEY.notifications[id].ReadOn == null && LABKEY.notifications[id].Deleted == undefined)
                    {
                        count++;
                    }
                }
                NOTIFICATION_COUNT_EL.html(count > 0 ? count : '');

                _updateGroupDisplay();
            }
        };

        /**
         * Show the notification panel
         * @private
         */
        var showPanel = function ()
        {
            // slide open the notification panel and bind the click listener after the slide animation has completed
            if (NOTIFICATION_PANEL_EL)
            {
                NOTIFICATION_PANEL_EL.slideDown(250, _addCheckHandlers);
            }
        };

        /**
         * Hide the notification panel
         * @private
         */
        var hidePanel = function ()
        {
            // slide out the notification panel and unbind the click listener
            if (NOTIFICATION_PANEL_EL)
            {
                NOTIFICATION_PANEL_EL.slideUp(250, _removeCheckHandlers);
            }
        };

        /**
         * Expand the content body vertically for the selected notification
         * @param domEl - the expand/collapse dom element that was clicked
         * @private
         */
        var toggleBody = function (domEl)
        {
            if (domEl)
            {
                var el = $(domEl);
                if (el.hasClass('fa-angle-down'))
                {
                    el.closest('.labkey-notification').find('.labkey-notification-body').addClass('labkey-notification-body-expand');
                    el.removeClass('fa-angle-down');
                    el.addClass('fa-angle-up');
                }
                else
                {
                    el.closest('.labkey-notification').find('.labkey-notification-body').removeClass('labkey-notification-body-expand');
                    el.removeClass('fa-angle-up');
                    el.addClass('fa-angle-down');
                }
            }
        };

        /**
         * Mark a given notification as read based on the RowId
         * @param id - notification RowId
         * @param callback - function to call on success
         * @private
         */
        var markAsRead = function (id, callback)
        {
            if (id)
            {
                LABKEY.Ajax.request({
                    url: LABKEY.ActionURL.buildURL('notification', 'markNotificationAsRead.api'),
                    params: {rowIds: [id]},
                    success: LABKEY.Utils.getCallbackWrapper(function (response)
                    {
                        if (response.success && response.numUpdated == 1)
                        {
                            if (NOTIFICATION_PANEL_EL && id && LABKEY.notifications && LABKEY.notifications[id])
                            {
                                LABKEY.notifications[id].ReadOn = new Date();
                                NOTIFICATION_PANEL_EL.find('#notification-' + id).slideUp(250, _updateGroupDisplay);
                                updateUnreadCount();
                            }

                            if (callback)
                                callback.call(this, id);
                        }
                    }),
                    failure: function(response)
                    {
                        var responseText = LABKEY.Utils.decode(response.responseText);
                        LABKEY.Utils.alert('Error', responseText.exception);
                    }
                });
            }
        };

        /**
         * Delete a given notification based on the RowId
         * @param id - notification RowId
         * @param callback - function to call on success
         * @private
         */
        var deleteNotification = function (id, callback)
        {
            if (id)
            {
                LABKEY.Ajax.request({
                    url: LABKEY.ActionURL.buildURL('notification', 'deleteNotification.api'),
                    params: {rowIds: [id]},
                    success: LABKEY.Utils.getCallbackWrapper(function (response)
                    {
                        if (response.success && response.numDeleted == 1)
                        {
                            if (NOTIFICATION_PANEL_EL && id && LABKEY.notifications && LABKEY.notifications[id])
                            {
                                LABKEY.notifications[id].Deleted = true;
                                NOTIFICATION_PANEL_EL.find('#notification-' + id).slideUp(250, _updateGroupDisplay);
                                updateUnreadCount();
                            }

                            if (callback)
                                callback.call(this, id);
                        }
                    }),
                    failure: function(response)
                    {
                        var responseText = LABKEY.Utils.decode(response.responseText);
                        LABKEY.Utils.alert('Error', responseText.exception);
                    }
                });
            }
        };

        /**
         * Mark all notifications as read and clear the notification panel
         * @private
         */
        var clearAllUnread = function ()
        {
            if (NOTIFICATION_PANEL_EL && LABKEY.notifications)
            {
                var rowIds = [];
                for (var id in LABKEY.notifications)
                {
                    if (LABKEY.notifications.hasOwnProperty(id) && LABKEY.notifications[id].RowId
                        && LABKEY.notifications[id].ReadOn == null && LABKEY.notifications[id].Deleted == undefined)
                    {
                        rowIds.push(LABKEY.notifications[id].RowId);
                    }
                }

                if (rowIds.length > 0)
                {
                    LABKEY.Ajax.request({
                        url: LABKEY.ActionURL.buildURL('notification', 'markNotificationAsRead.api'),
                        method: 'POST', // Issue 31849: action fails if there is a long rowIds list
                        params: {rowIds: rowIds},
                        success: LABKEY.Utils.getCallbackWrapper(function (response)
                        {
                            if (response.success && response.numUpdated == rowIds.length)
                            {
                                for(var i = 0; i < rowIds.length; i++)
                                {
                                    LABKEY.notifications[rowIds[i]].ReadOn = new Date();
                                }
                                updateUnreadCount();

                                NOTIFICATION_PANEL_EL.find('.labkey-notification-area').slideUp(100, _showNotificationsNone);
                            }
                        }),
                        failure: function(response)
                        {
                            var responseText = LABKEY.Utils.decode(response.responseText);
                            LABKEY.Utils.alert('Error', responseText.exception);
                        }
                    });
                }
            }
        };

        /**
         * Navigate to the given notification's ActionLinkUrl
         * @param event - browser click event to check target
         * @param id - notification RowId
         * @private
         */
        var goToActionLink = function (event, id)
        {
            if (id && LABKEY.notifications && LABKEY.notifications[id])
            {
                if (!event.target.classList.contains("labkey-notification-times")
                    && !event.target.classList.contains("labkey-notification-toggle")
                    && !event.target.classList.contains("labkey-notification-close"))
                {
                    window.location = LABKEY.ActionURL.buildURL("notification", "goto",
                        LABKEY.notifications[id].ContainerId || LABKEY.container.id,
                        {
                            rowid: LABKEY.notifications[id].RowId,
                            returnUrl: LABKEY.notifications[id].ActionLinkUrl
                        }
                    );
                }
            }
        };

        /**
         * Navigate to the action to view all notifications
         * @private
         */
        var goToViewAll = function ()
        {
            window.location = LABKEY.ActionURL.buildURL('notification', 'userNotifications');
        };

        var _addCheckHandlers = function()
        {
            $('body').on('click', _checkBodyClick);
            $(document).on('keyup', _checkKeyUp);
            LABKEY.Utils.signalWebDriverTest("notificationPanelShown");
        };

        var _removeCheckHandlers = function()
        {
            $('body').off('click', _checkBodyClick);
            $(document).off('keyup', _checkKeyUp);
        };

        var _checkBodyClick = function(event)
        {
            // close if the click happened outside of the notification panel
            if (NOTIFICATION_PANEL_EL && event.target.id != NOTIFICATION_PANEL_EL.attr('id') && !NOTIFICATION_PANEL_EL.has(event.target).length)
            {
                hidePanel();
            }
        };

        var _checkKeyUp = function(event)
        {
            // close if the ESC key is pressed
            if (NOTIFICATION_PANEL_EL && event.keyCode == 27)
            {
                hidePanel();
            }
        };

        var _updateGroupDisplay = function()
        {
            if (NOTIFICATION_PANEL_EL && LABKEY.notifications.grouping)
            {
                var hasAnyGroup = false;

                for (var group in LABKEY.notifications.grouping)
                {
                    if (LABKEY.notifications.grouping.hasOwnProperty(group))
                    {
                        var groupRowIds = LABKEY.notifications.grouping[group],
                            hasUnread = false;

                        for (var i = 0; i < groupRowIds.length; i++)
                        {
                            if (LABKEY.notifications[groupRowIds[i]].ReadOn == null && LABKEY.notifications[groupRowIds[i]].Deleted == undefined)
                            {
                                hasUnread = true;
                            }
                            else
                            {
                                NOTIFICATION_PANEL_EL.find('#notification-' + groupRowIds[i]).hide();
                            }
                        }

                        if (!hasUnread)
                        {
                            var notificationGroupDiv = NOTIFICATION_PANEL_EL.find('#notificationtype-' + group);
                            if (notificationGroupDiv)
                                notificationGroupDiv.addClass('labkey-hidden');
                        }
                        else
                        {
                            hasAnyGroup = true;
                        }
                    }
                }

                if (!hasAnyGroup)
                    _showNotificationsNone();
            }
        };

        var _showNotificationsNone = function()
        {
            if (NOTIFICATION_PANEL_EL)
            {
                var el = NOTIFICATION_PANEL_EL.find('.labkey-notification-none');
                if (el)
                    el.removeClass('labkey-hidden');

                el = NOTIFICATION_PANEL_EL.find('.labkey-notification-clear-all');
                if (el)
                    el.addClass('labkey-hidden');
            }
        };

        var _notificationsUpdatedCallbacks = [];

        var _refreshFromServer = function()
        {
            LABKEY.Ajax.request({
                url: LABKEY.ActionURL.buildURL('notification', 'getUserNotificationsForPanel.api'),
                success: LABKEY.Utils.getCallbackWrapper(function (response)
                {
                    if (response.success)
                    {
                        LABKEY.notifications = response.notifications;
                        _notificationsUpdatedCallbacks.forEach(function(cb){cb();});
                    }
                })
            });
        };

        /** Add a listener specifically for notification changes */
        var onChange = function(cb)
        {
            try
            {
                if (0 === _notificationsUpdatedCallbacks.length) {
                    addServerEventListener("org.labkey.api.admin.notification.NotificationService",
                            function () {
                                _refreshFromServer()
                            });
                }
                _notificationsUpdatedCallbacks.push(cb);
            }
            catch (ex)
            {
                console.log(ex);
            }
        };

        function showDisconnectedMessage() {
            //LABKEY.Utils.alert("Disconnected", "The server is unavailable");
            console.warn("The server is unavailable");

            // CONSIDER: Periodically attempt to reestablish connection until the server comes back up.
            // CONSIDER: Once reconnected, reload the page unless page is dirty -- LABKEY.isDirty()
        }

        var _websocket = null;
        var _callbacks = {};

        function openWebsocket() {
            _websocket = new WebSocket((window.location.protocol==="http:"?"ws:":"wss:") + "//" + window.location.host + LABKEY.contextPath + "/_websocket/notifications");
            _websocket.onmessage = websocketOnMessage;
            _websocket.onclose = websocketOnclose;
        }

        var websocketOnMessage = function (evt) {
            var json = JSON.parse(evt.data);
            var event = json.event;
            console.info("websocket.onmessage", event);

            if (event === "org.labkey.api.security.AuthNotify#LoggedIn") {
                console.log("You have logged in elsewhere");
            }
            else if (event === "org.labkey.api.security.AuthNotify#LoggedOut") {
                console.log("You have logged out elsewhere");
            }

            var list = _callbacks[event] || [];
            list.forEach(function(cb){cb(json)});
        };

        var websocketOnclose = function (evt) {
            console.info("websocket.onclose", evt);

            if (evt.wasClean)
            {
                if (evt.code === 1000 || evt.code === 1003) {
                    // normal close
                    if (evt.reason === "org.labkey.api.security.AuthNotify#LoggedOut") {
                        console.log("You have logged out");
                    }
                }
                else if (evt.code === 1001 || evt.code === 1006) {
                    // 1001 sent when server is shutdown normally (or on page reload in FireFox?)
                    // 1006 abnormal close (e.g, server process died)
                    setTimeout(showDisconnectedMessage, 1000);
                }
                else if (evt.code === 1008) {
                    // Tomcat closes the websocket with "1008 Policy Violation" code when the session has expired.
                    // evt.reason === "This connection was established under an authenticated HTTP session that has ended."
                    LABKEY.Ajax.request({
                        url: LABKEY.ActionURL.buildURL("login", "whoami.api"),
                        success: function (data) {
                            if (LABKEY.user.id !== data.id) {
                                LABKEY.Utils.alert("Session expired", "Your session has expired. Reload the page to refresh your session.");
                            }
                        },
                        failure: function (data) {
                            setTimeout(showDisconnectedMessage, 1000);
                        }
                    });
                }
            }
        };

        /** Add a general purpose listener for server events */
        var addServerEventListener = function(event, cb)
        {
            if (LABKEY.user.id && 'WebSocket' in window)
            {
                if (null === _websocket)
                {
                    openWebsocket();
                }

                var list = _callbacks[event] || [];
                list.push(cb);
                _callbacks[event] = list;
            }
        };

        return {
            setElementIds: setElementIds,
            updateUnreadCount: updateUnreadCount,
            showPanel: showPanel,
            hidePanel: hidePanel,
            toggleBody: toggleBody,
            markAsRead: markAsRead,
            deleteNotification: deleteNotification,
            clearAllUnread: clearAllUnread,
            goToActionLink: goToActionLink,
            goToViewAll: goToViewAll,
            onChange: onChange,
            addServerEventListener: addServerEventListener
        };
    };

})(jQuery);
