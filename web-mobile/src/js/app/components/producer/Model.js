// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/DataService',
    'app/utils/CodeUtils',
    'app/utils/RenderUtils'
], function(UIComponent, TemplateManager, DataService, CodeUtils, RenderUtils) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var Model = function(dom, data) {
        Model.superclass.constructor.apply(this, arguments);
        this._model = data;

        TemplateManager.load('producer/model.html', function(err, content$) {
            this._dom$.append(content$);
            this._render();
        }.bind(this));
    };
    andrea.oo.extend(Model, UIComponent);

    Model.prototype.getPreferredSize = function() {
        return {
            'height' : 320
        };
    };

    Model.prototype._render = function() {
        Model.superclass._render.apply(this, arguments);

        $('.qsPortrait', this._dom$).css('background-image', RenderUtils.imagePathToBackground(this._model.portrait));
        $('.qsName', this._dom$).text(this._model.name);
        $('.qsRole', this._dom$).text(RenderUtils.rolesToDisplay(this._model.roles));
        $('.qsDetail', this._dom$).text([RenderUtils.heightToDisplay(this._model.height), RenderUtils.weightToDisplay(this._model.weight)].join('／'));
        // TODO
        $('.qsNumShows', this._dom$).text('99');
        $('.qsNumFollow', this._dom$).text(this._model.$numFollowRefs);
        $('.qsNumFollowers', this._dom$).text(this._model.$numFollowerRefs);
    };

    return Model;
});
