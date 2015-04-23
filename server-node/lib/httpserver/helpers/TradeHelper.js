var mongoose = require('mongoose');
var async = require('async');

var People = require('../../model/peoples');
var Trade = require('../../model/trades');

var qsmail = require('../../runtime/qsmail');

var TradeHelper = module.exports;

TradeHelper.updateStatus = function(trade, updateStatus, peopleId, callback) {
    var statusLog = {
        'status' : updateStatus,
        'peopleRef' : peopleId,
        'date' : Date.now
    };
    trade.set('status', updateStatus);
    trade.statusLogs = trade.statusLogs || [];
    trade.statusLogs.push(statusLog);

    trade.save(function(err) {
        callback(err, trade);
    });
};

TradeHelper.notify = function(trade, callback) {
    // @formatter:off
    var subject = "[" + _getStatusName(trade.status) + "]" + 
        trade.orders[0].itemSnapshot.name + "*" + trade.orders[0].quantity + "=" + trade.orders[0].price;
        
    var content = 
        "交易：\n" + 
        JSON.stringify(trade, null, 4) + 
        "用户：\n" + 
        JSON.stringify(trade.orders[0].peopleSnapshot, null, 4);
    // @formatter:on

    qsmail.send(subject, content, callback);
};

_getStatusName = function(status) {
    switch(status) {
        case 0:
            return "等待买家付款";
        case 1:
            return "等待倾秀代购";
        case 2:
            return "等待卖家发货";
        case 3:
            return "卖家已发货";
        case 4:
            return "买家已签收";
        case 5:
            return "交易成功";
        case 6:
            return "申请退货中";
        case 7:
            return "退货中";
        case 8:
            return "退款中";
        case 9:
            return "退款成功";
        case 10:
            return "退款失败";
    }
};