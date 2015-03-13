var tradeSchema = Schema({
    status : Number,
    totalFee : Number,
    orders : [{
        quantity : Number,
        price : Number,
        itemSnapshot : Object,
        peopleSnapshot : Object,
        r : {
            itemSnapshot : {
                skuIndex : Number
            },
            peopleSnapshot : {
                receiverIndex : Number
            }
        }
    }],
    pay : {
        weixin : {
            prepayid : String,
            transaction_id : String,
            partner : String,
            trade_mode : String,
            total_fee : String,
            fee_type : String,
            AppId : String,
            OpenId : String,
            time_end : String,
            notifyLogs : [{
                notify_id : String,
                trade_state : String,
                date : {
                    type : Date,
                    'default' : Date.now
                }
            }]
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
            buyer_email : String,
            notifyLogs : [{
                notify_type : String,
                notify_id : String,
                trade_status : String,
                refund_status : String,
                date : {
                    type : Date,
                    'default' : Date.now
                }
            }],
        }
    },
    logistic : {
        company : String,
        trackingID : String
    },
    returnLogistic : {
        company : String,
        trackingID : String
    },
    create : {
        type : Date,
        'default' : Date.now
    },
    statusLogs : [{
        status : Number,
        update : {
            type : Date,
            'default' : Date.now
        },
        peopleRef : {
            type : Schema.Types.ObjectId,
            ref : 'peoples'
        }
    }]
});

var Trade = mongoose.model('trades', tradeSchema);
module.exports = Trade;
