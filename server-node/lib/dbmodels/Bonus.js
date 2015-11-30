var mongoose = require('mongoose'),
    Schema = mongoose.Schema;
    
var Bonus = mongoose.model('bonuses', Schema({
    ownerRef : {
        type : Schema.Types.ObjectId,
        ref : 'peoples'
    },
    type : Number,
    status : Number,
    amount : Number,
    description : String,
    create : {
        type : Date,
        'default' : Date.now
    },
    participants : {
        type : [{
            type : Schema.Types.ObjectId,
            ref : 'peoples'
        }]
    },
    trigger : {
        tradeRef : {    
            type : Schema.Types.ObjectId,
            ref : 'trades'
        }
    },
    weixinRedPackId : String
}));

module.exports = Bonus;
