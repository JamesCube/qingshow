var mongoose = require('mongoose');
var async = require('async');
// Models
var People = require('../../model/peoples');
var ServerError = require('../server-error');
var ContextHelper = require('../helpers/ContextHelper');

var RPeopleFollowPeople = require('../../model/rPeopleFollowPeople');

module.exports.create = function(Model, initiatorRef, targetRef, callback) {
    async.waterfall([
    function(callbck) {
        // Validate existed relationship
        Model.findOne({
            'initiatorRef' : initiatorRef,
            'targetRef' : targetRef
        }, function(err, r) {
            if (err) {
                callbck(err);
            } else if (r) {
                callbck(ServerError.AlreadyRelated);
            } else {
                callbck(null);
            }
        });
    },
    function(callback) {
        // Create relationship
        new Model({
            'initiatorRef' : initiatorRef,
            'targetRef' : targetRef
        }).save(callback);
    }], callback);
};

module.exports.remove = function(Model, initiatorRef, targetRef, callback) {
    async.waterfall([
    function(callback) {
        // Get relationship
        Model.findOne({
            'initiatorRef' : initiatorRef,
            'targetRef' : targetRef
        }, callback);
    },
    function(relationship, callback) {
        // Remove relationship
        if (relationship) {
            relationship.remove(callback);
        } else {
            callback(ServerError.AlreadyUnrelated);
        }
    }], callback);
};

