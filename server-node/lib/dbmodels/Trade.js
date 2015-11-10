var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var tradeSchema = Schema({
    __context : Object,
    status : Number,
    shareToPay : Boolean,
    totalFee : Number,
    quantity : Number,
    expectedPrice : Number,
    itemSnapshot : Object,
    selectedSkuProperties : [String],
    peopleSnapshot : Object,
    selectedPeopleReceiverUuid : String,
    highlight : Date,
    note : String,
    pay : {
        create : Date,
        weixin : {
            prepayid : String,
            transaction_id : String,
            partner : String,
            trade_mode : String,
            total_fee : String,
            fee_type : String,
            AppId : String,
            OpenId : String,
            time_end : String
        },
        alipay : {
            trade_no : String,
            trade_status : String,
            total_fee : String,
            refund_status : String,
            gmt_refund : String,
            seller_id : String,
            seller_email : String,
            buyer_id : String,
            buyer_email : String
        },
        forge : {
            date : Date
        }
    },
    logistic : {
        company : String,
        trackingId : String
    },
    returnLogistic : {
        company : String,
        trackingId : String
    },
    create : {
        type : Date,
        'default' : Date.now
    },
    ownerRef : {
        type : Schema.Types.ObjectId,
        ref : 'peoples'
    },
    statusLogs : [{
        status : Number,
        comment : String,
        update : {
            type : Date,
            'default' : Date.now
        },
        peopleRef : {
            type : Schema.Types.ObjectId,
            ref : 'peoples'
        }
    }],
    update : {
        type: Date,
        'default' : Date.now
    },
    // Ref to #1629, this field does not act as an order. Play as an helper field for trade/queryByPhase
    statusOrder : String,
    itemRef : {
        type : Schema.Types.ObjectId,
        ref : 'items'
    },
    promoterRef : {
        type : Schema.Types.ObjectId,
        ref : 'peoples'
    }
});

var Trade = mongoose.model('trades', tradeSchema);
module.exports = Trade;
