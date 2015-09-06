var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var itemSchema;
itemSchema = Schema({
    __context : Object,
    categoryRef : {
        type : Schema.Types.ObjectId,
        ref : 'categories'
    },
    shopRef : {
        type : Schema.Types.ObjectId,
        ref : 'peoples'
    },
    thumbnail : String,
    name : String,
    price: Number,
    promoPrice : Number,
    minExpectedPrice : Number,
    expectablePrice : Number,
    skuProperties : [String],
    source : String,
    numLike : Number,
    create : {
        type : Date,
        'default' : Date.now
    },
    delist : Date,
    list : Date,
    readOnly : Boolean,
    syncEnabled : {
        type : Boolean,
        'default' : true
    },
    sync : Date
});

var Item = mongoose.model('items', itemSchema);

module.exports = Item;
