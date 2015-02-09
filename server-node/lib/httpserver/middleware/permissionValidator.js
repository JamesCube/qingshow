var async = require('async');
var _ = require('underscore');

var ServerError = require('../server-error');
var People = require('../../model/peoples');
var _validatorsMap = {};

var _init = function(services) {
    services.forEach(function(service) {
        var module = service.module, path = service.path;
        for (var id in module) {
            var validators = module[id].permissionValidators;
            if (validators) {
                _validatorsMap['/services/' + path + '/' + id] = validators;
            }
        }
    });
    return _validate;
};

var _validate = function(req, res, next) {
    var validators = _validatorsMap[req.path];
    if (validators) {
        var tasks = [];
        validators.forEach(function(validator) {
            tasks.push(function(callback) {
                if (_.isString(validator)) {
                    validator = _builtInValidators[validator];
                }
                validator(req, res, callback);
            });
        });
        async.series(tasks, function(err) {
            if (err) {
                next(new ServerError(err));
            } else {
                next();
            }
        });
    } else {
        next();
    }
};

var _builtInValidators = {
    'loginValidator' : function(req, res, callback) {
        if (req.qsCurrentUserId) {
            callback(null);
        } else {
            callback(ServerError.NeedLogin);
        }
    },
    'adminValidator': function(req, res, callback) {
      if (!req.qsCurrentUserId) {
        callback(ServerError.NeedLogin);
        return;
      } else {
        People.findOne({
          '_id' : req.qsCurrentUserId
        }, function(err, people) {
          if (err) {
            callback(ServerError.fromError(err));
          } else if (people) {
            if (!people.roles) {
              callback(ServerError.IsNotAdmin);
            } else {
              var isAdmin = false;
              people.roles.forEach(function(role) {
                if (role == 2) {
                  isAdmin = true;
                }
              });
              if (isAdmin) {
                callback(null);
              } else {
                callback(ServerError.IsNotAdmin);
              }
            }
          } else {
            callback(ServerError.NeedLogin);
          }
        });
      }
    }
};

module.exports = _init;
