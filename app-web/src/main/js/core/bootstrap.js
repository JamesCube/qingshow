// @formatter:off
define([
    'qs/services/httpService',
    'qs/services/navigationService',
    'qs/services/downloadService',
    'qs/core/model',
    'require'
], function(httpService, 
    navigationService, 
    downloadService, 
    model) {
// @formatter:on

    // Initialize __runtime
    window.__runtime = {
        'model' : model
    };
    // Initialize __services
    httpService.config({
        // 'root' : 'http://127.0.0.1:30001/services',
        'root' : 'http://chingshow.com/services',
        'version' : '2.1.0'
    });
    navigationService.config({
        'root' : $('#root').get(0)
    });
    window.__services = {
        'httpService' : httpService,
        'navigationService' : navigationService,
        'downloadService' : downloadService
    };

    // Bootstrap first page
    var search = violet.url.search;

    httpService.request('/trace/openShare', 'post', {
        '_id' : search._id
    }, function(err, metadata, data) {});

    httpService.request('/share/query', 'get', {
        '_ids' : [search._id]
    }, function(err, metadata, data) {
        var shareObj = data && data.sharedObjects && data.sharedObjects[0];
        if (err || !shareObj) {
            navigationService.push('qs/views/P01NotFound');
        } else {
            if (shareObj.type === 0) {
                //show
                navigationService.push('qs/views/P02ShareShow', {
                    'entity' : shareObj.targetInfo.show
                });
            } else if (shareObj.type === 1) {
                //trade
                navigationService.push('qs/views/P03ShareTrade', {
                    'entity' : shareObj.targetInfo.trade
                });
            } else if (shareObj.type === 2) {
                //bonus
                navigationService.push('qs/views/P04ShareBonus', {
                    'entity' : shareObj.targetInfo.bonus
                });
            } else {
                navigationService.push('qs/views/P01NotFound');
            }
        }
    });
});
