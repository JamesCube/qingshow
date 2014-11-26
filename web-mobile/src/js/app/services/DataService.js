// @formatter:off
define([
], function() {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var DataService = {};

    DataService.request = function(type, path, requestData, callback) {
        var request = {
            'dataType' : 'json',
            'cache' : false,
            'xhrFields' : {
                'withCredentials' : true
            }
        };
        var key;
        if (andrea.env.uriQuery.appServer === 'fake') {
            var suffix = [];
            for (key in requestData) {
                if (path === '/feeding/chosen' && key === 'type') {
                    suffix.push(key + '' + requestData[key]);
                } else if (path === '/feeding/byTag' && key === 'tags') {
                    suffix.push('tag' + requestData[key][0]);
                }
            }

            suffix = suffix.length ? ('_' + suffix.join('_')) : '';
            $.extend(request, {
                'url' : appConfig.appServer + '/services' + path + suffix + '.json'
            });
        } else {
            for (key in requestData) {
                if (requestData[key] instanceof Array) {
                    requestData[key] = requestData[key].join(',');
                }
            }
            $.extend(request, {
                'url' : appConfig.appServer + '/services' + path,
                'type' : type,
                'data' : requestData
            });
        }

        $.ajax(request).done(function(responseData) {
            console.log('api: ' + path, requestData, responseData);
            if (callback) {
                callback(responseData.metadata || {}, responseData.data);
            }
        }).fail(function(target, msg, err) {
            if (callback) {
                // TODO Handle network err
                var metadata = {
                    'error' : msg
                };
                callback(metadata);
            }
        }).always(function() {
        });
    };

    DataService.upload = function(path, callback) {
        var formImage$ = $('#formImage');
        formImage$.attr('action', appConfig.appServer + '/services' + path);
        formImage$[0].submit();
    };

    DataService.injectBeforeCallback = function(callback, beforeCallback) {
        return function(metadata, data) {
            if (!metadata.error) {
                beforeCallback(metadata, data, require('app/model'));
            }
            if (callback) {
                callback.apply(null, arguments);
            }
        };
    };

    return DataService;
});
