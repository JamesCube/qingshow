var JPush = require('jpush-sdk');
var winston = require('winston');
var _ = require('underscore');
var async = require('async');

var People = require('../dbmodels').People;
var jPushAudiences = require('../dbmodels').JPushAudience;

// APP_KEY,Master_Key 
var JPushConfig = {
    Debug: {                // <-- HashMap的测试用 App Key
        AppKey : 'caa96ddbab44562ee6bb9d58',
        MasterKey : 'c1cd20800d855c7b751548a8'
    },
    Release: {              // APNS 生产证书
        AppKey : 'dad14285add5a5ade0fbfd81',
        MasterKey : 'd10901a237cddc3e1b0d1f63'
    }
};
var client = JPush.buildClient(JPushConfig.Release.AppKey, JPushConfig.Release.MasterKey);

var NotificationHelper = module.exports;

NotificationHelper.MessageQuestSharingObjectiveComplete = "恭喜您！完成倾秀夏日季搭配活动任务！点击此处领奖吧～";
NotificationHelper.MessageNewShowComment = "您的搭配有新评论！";
NotificationHelper.MessageNewRecommandations = "最新的搭配已经推送给您，美丽怎能忍心被忽略，去看看吧！";
NotificationHelper.MessageQuestSharingProgress = "您还需要{0}个小伙伴助力即可获取大奖，继续加油吧！";
NotificationHelper.MessageTradeInitialized = "您申请的折扣已经成功啦，别让宝贝飞了，快来付款吧！";
NotificationHelper.MessageTradeShipped = "您购买的宝贝已经向您狂奔而来，等着接收惊喜哟！";
NotificationHelper.MessageItemPriceChanged = "您申请的折扣有最新信息，不要错过哦！";
NotificationHelper.MessageNewBonus = "您有一笔佣金入账啦，立即查看！";
NotificationHelper.MessageBonusWithdrawComplete = "您的账户成功提现{0}，请注意查看账户！";

NotificationHelper.CommandQuestSharingObjectiveComplete = "questSharingObjectiveComplete";
NotificationHelper.CommandNewShowComments = "newShowComments";
NotificationHelper.CommandNewRecommandations= "newRecommandations";
NotificationHelper.CommandQuestSharingProgress = "questSharingProgress";
NotificationHelper.CommandTradeInitialized = "tradeInitialized";
NotificationHelper.CommandTradeShipped = "tradeShipped";
NotificationHelper.CommandItemExpectablePriceUpdated = "itemExpectablePriceUpdated";
NotificationHelper.CommandNewBonus = "newBonus";
NotificationHelper.CommandBonusWithdrawComplete = "bonusWithdrawComplete";

NotificationHelper.notify = function(peoplesIds, message, extras, cb) {
    async.series([function(callback){
        NotificationHelper._push(peoplesIds, message, extras, function(err, res){
            callback(err, res);
        })
    }, function(callback){
        NotificationHelper._saveAsUnread(peoplesIds, extras, function(err){
            callback(err);
        })
    }], cb);
}

NotificationHelper._push = function(peoplesIds, message, extras, cb) {
    async.waterfall([function(callback){
        jPushAudiences.find({
            peopleRef : {
                '$in' : peoplesIds
            }
        }).exec(function(err, infos) {
            callback(err, infos);
        });
    }, function(infos, callback){
        var registrationIDs = [];
        infos.forEach(function(info) {
            registrationIDs.push(info.registrationId);
        });
        var sendTargets = _.filter(registrationIDs, function(registrationId) {
            return (registrationId && (registrationId.length > 0));
        });
        if (sendTargets.length) {
            client.push().setPlatform('ios', 'android')
            .setAudience(JPush.registration_id(sendTargets))
            .setNotification(JPush.ios(message, 'default', null, false, extras), JPush.android(message, message, null, extras))
            .setOptions(null, null, null, true, null)
            .send(function(err, res) {
                if (err) {
                    winston.error('Push error: ' + err);
                } else {
                    winston.info('Push success: ' + res);
                }
                if (callback) {
                    callback(err, res);
                }
            });
        } else {
            callback();
        }
    }], cb)
}

NotificationHelper.read = function(peoplesIds, criteria, callback) {
    if (criteria.command === NotificationHelper.CommandTradeInitialized ||
        criteria.command === NotificationHelper.CommandItemExpectablePriceUpdated) {
        criteria.command = {
            '$or' : [NotificationHelper.CommandTradeInitialized,
                NotificationHelper.CommandItemExpectablePriceUpdated]
        };
    }
    People.update({
        '_id' : {
            '$in' : peoplesIds
        }
    }, {
        '$pull' : {
            'unreadNotifications' : criteria
        }
    }, {
        multi : true
    }, function(err, peoples){
        callback(err, peoples);  
    })
};

NotificationHelper._saveAsUnread = function(peoplesIds, extras, cb) {
    async.waterfall([function(callback){
        var criteria = {};
        for (var element in extras) {
            criteria['extra.' + element] = extras[element];
        }
        NotificationHelper.read(peoplesIds, criteria, function(err, peoples){
            callback(err, peoples);
        });
    }, function(peoples, callback){
        People.update({
            '_id' : {
                '$in' : peoplesIds
            }
        }, {
            $push : {
                'unreadNotifications' : {
                    'extra' : extras
                }
            }
        }, {
            multi : true
        }, function(err, peoples){
            callback(err, peoples);
        })
    }], cb)
}
