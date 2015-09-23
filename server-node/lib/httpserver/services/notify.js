var mongoose = require('mongoose');
var async = require('async');
var _ = require('underscore');

var People = require('../../models').People;
var Trade = require('../../models').Trade;
var jPushAudiences = require('../../models').JPushAudience;

var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');
var PushNotificationHelper = require('../helpers/PushNotificationHelper');

var notify = module.exports;

notify.newRecommandations = {
    'method' : 'post',
    'func' : function(req, res) {
        var group = req.body.group;

        async.waterfall([
        function(callback) {
            People.find({}).exec(callback);
        },
        function(peoples, callback) {
            var targets = _.filter(peoples, function(people) {
                var rate = people.weight / people.height;
                var type = null;
                if (rate < 0.275) {
                    type = 'A1';
                } else if (rate < 0.315) {
                    type = 'A2';
                } else if (rate < 0.405) {
                    type = 'A3';
                } else {
                    type = 'A4';
                }

                return type == group;
            });

            var ids = [];
            targets.forEach(function(target) {
                ids.push(target._id);
            });
            callback(null, ids);
        },
        function(ids, callback) {
            jPushAudiences.find({
                peopleRef : {
                    '$in' : ids
                }
            }).exec(function(err, infos) {
                callback(err, infos);
            });
        },
        function(targets, callback) {
            var registrationIDs = [];
            targets.forEach(function(target) {
                registrationIDs.push(target.registrationId);
            });
            PushNotificationHelper.push(registrationIDs, PushNotificationHelper.MessageNewRecommandations, {
                'command' : PushNotificationHelper.CommandNewRecommandations
            }, callback);
        }], function(err) {
            ResponseHelper.response(res, err, null);
        });
    }
};

