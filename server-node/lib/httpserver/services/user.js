var People = require('../../model/peoples');
var ServicesUtil = require('../../util/servicesUtil');

var _login, _update;
_login = function (req, res) {
    var param, mail, encryptedPassword;
    param = req.body;
    mail = param.mail || '';
    encryptedPassword = param.encryptedPassword || '';
    People.findOne({"userInfo.mail" : mail, "userInfo.encryptedPassword": encryptedPassword}, function (err, people) {
        if (err) {
            ServicesUtil.responseError(res, err);
        }
        if (people) {
            //login succeed
            req.session.userId = people._id;
            req.session.loginDate = new Date();
            res.send(people);
        } else {
            //login fail
            delete req.session.userId;
            delete req.session.loginData;
            err = new Error('Incorrect mail or password');
            err.code = 1001;
            ServicesUtil.responseError(res, err);
        }
    });
};

//TODO
_update = function (req, res) {
    res.send('update');
};


module.exports = {
    'login' : {method: 'post', func: _login},
    'update' : {method: 'post', func: _update, needLogin: true}
};