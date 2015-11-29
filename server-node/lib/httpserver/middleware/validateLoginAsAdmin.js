var PeopleCode = require('../../dbmodels').PeopleCode;

var errors = require('../../errors');

module.exports = function(req, res, next) {
    if (req.qsCurrentUserId) {
        if (req.injection.qsCurrentUser) {
            if (req.injection.qsCurrentUser.role === PeopleCode.ROLE_ADMIN) {
                next();
            } else {
                next(errors.ERR_PERMISSION_DENIED);
            }
        } else {
            next(errors.genUnkownError('Please injectCurrentUser before validateLoginAsAdmin.'));
        }
    } else {
        next(errors.ERR_NOT_LOGGED_IN);
    }
};
