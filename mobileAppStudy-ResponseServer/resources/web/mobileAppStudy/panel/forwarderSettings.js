(function($) {

    function hidePanel(panel) {
        panel.find('input').removeAttr('required');
        panel.hide();
    }

    function showPanel(panel) {
        panel.find('input').attr('required', 'required');
        panel.show();
    }

    function showAuthPanel() {
        const selected = $("input[name='forwardingType']:checked").val();
        const basicPanel = $('#basicAuthPanel');
        const oauthPanel = $('#oauthPanel');

        switch (selected) {
            case 'Basic':
                showPanel(basicPanel);
                hidePanel(oauthPanel);
                break;
            case 'OAuth':
                hidePanel(basicPanel);
                showPanel(oauthPanel);
                break;
            case 'Disabled':
            default:
                hidePanel(basicPanel);
                hidePanel(oauthPanel);
                break;
        }
    }

    LABKEY.MobileAppForwarderSettings = {
        hidePanel: hidePanel,
        showPanel:showPanel,
        showAuthPanel: showAuthPanel,
    };

})(jQuery);