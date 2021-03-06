// @formatter:off
define([
    'app/utils/CodeUtils'
], function(CodeUtils) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var RenderUtils = {};

    var _today;
    RenderUtils.timeToAge = function(time) {
        if (!_today) {
            _today = new Date();
        }
        var birth = new Date(time);
        var yearDiff = _today.getFullYear() - birth.getFullYear();
        var monthDiff = _today.getMonth() - birth.getMonth();
        var dateDiff = _today.getDate() - birth.getDate();
        if (monthDiff > 0 || (monthDiff === 0 && dateDiff >= 0)) {
            return yearDiff;
        } else {
            return yearDiff - 1;
        }
    };

    RenderUtils.imagePathToURL = function(path) {
        return path;
    };

    RenderUtils.imagePathToBackground = function(path) {
        return andrea.string.substitute('url("{0}")', RenderUtils.imagePathToURL(path));
    };

    RenderUtils.videoPathToURL = function(path) {
        return path;
    };

    RenderUtils.videoPathToBackground = function(path) {
        return andrea.string.substitute('url("{0}")', RenderUtils.videoPathToURL(path));
    };

    RenderUtils.heightToDisplay = function(height) {
        return height ? height + 'cm' : '';
    };
    RenderUtils.weightToDisplay = function(weight) {
        return weight ? weight + 'kg' : '';
    };

    RenderUtils.checkStringMatchPattern = function(pattern, value) {

        var regExp = new RegExp(pattern);

        if (!regExp.test(value)) {
            return false;
        }

        return true;
    };

    return RenderUtils;
});
