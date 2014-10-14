var People = require('../../model/peoples');

function validate(servicesNames) {
    var validatePaths = [];
    servicesNames.forEach(function (path) {
        var module = require('../services/' + path);
        for (var id in module) {
            var needLogin = module[id].needLogin === true;
            if (needLogin) {
                var servicePath = '/services/' + path + '/' + id;
                validatePaths.push(servicePath);
            }
        }
    });

    var handleValidate = function(req, res, next){
        if (validatePaths.indexOf(req.path) !== -1){
            var userID = req.session.userId;
            var loginDate = req.session.loginDate;
            People.findOne({"_id" : userID})
                .select('userInfo.passwordUpdatedDate')
                .exec(function (err, people){
                    if (err){
                        next(err);
                    }
                    else {
                        if (!people || !people.userInfo)
                        {
                            //user not found
                            err = new Error('session expire');
                            err.code = 1002;
                            next(err);
                            return;
                        }

                        if (!people.userInfo.passwordUpdatedDate){
                            people.userInfo.passwordUpdatedDate = loginDate;
                        }
                        if (loginDate < people.userInfo.passwordUpdatedDate) {
                            err = new Error('session expire');
                            err.code = 1002;
                            next(err);
                        }
                        else {
                            People.findOne({"_id" : userID}, function(err, people){
                                req.currentUser = people;
                                next();
                            })
                        }
                    }
                });
        }
        else {
            next();
        }
    };
    return handleValidate;
}

module.exports = validate;